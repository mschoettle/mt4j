/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.components.visibleComponents.shapes.mesh;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.bounds.BoundingSphere;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.BezierVertex;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsBuffers;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11;
import org.mt4j.util.opengl.GL11Plus;
import org.mt4j.util.opengl.GLTexture;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * A mesh class for drawing triangle meshes.
 * 
 * @author Christopher Ruff
 */
public class MTTriangleMesh extends AbstractShape{
	
	/** The triangles. */
	protected Triangle[] triangles; 
	
	/** The draw normals. */
	private boolean drawNormals;
	
	//FIXME EXPERIMENTAL
	/** The outline contours. */
	protected List<Vertex[]> outlineContours;
	
	/** The outline buffers. */
	protected List<FloatBuffer> outlineBuffers;
	
	private boolean calculateDefaultNormals = true; //has to be initialized here, else not considered in first setGeomInfo()..
	
	/**
	 * Creates a new triangle mesh.
	 * 
	 * <p><strong>Important</strong>:  This mesh expects triangles geometry!
	 * <br><li>An unindexed geometry's vertex array
	 * should contain pairs of three vertices.
	 * <br><li> An indexed geometry should contain pairs of three indices.
	 * <br>
	 * 
	 * @param pApplet the applet
	 * @param geometryInfo the geometry info
	 */
	public MTTriangleMesh(PApplet pApplet, GeometryInfo geometryInfo) {
		this(pApplet, geometryInfo, true);
	}
	
	
	/**
	 * Creates a new triangle mesh.
	 * 
	 * <p><strong>Important</strong>:  This mesh expects triangles geometry!
	 * <br><li>An unindexed geometry's vertex array
	 * should contain pairs of three vertices.
	 * <br><li> An indexed geometry should contain pairs of three indices.
	 * <br>
	 * 
	 * @param pApplet the applet
	 * @param geometryInfo the geometry info
	 * @param calculateDefaultNormals sets if default normals for lightning should be 
	 * calculated if the geometry doesent containe them yet - WE ARE ONLY REQUIRED TO CALCULATE NORMALS
	 * IF THE MESH IS TO BE USED WITH LIGHTNING!
	 */
	public MTTriangleMesh(PApplet pApplet, GeometryInfo geometryInfo, boolean calculateDefaultNormals) {
		super(pApplet, geometryInfo);
		
		this.calculateDefaultNormals = calculateDefaultNormals;
		
		//EXPERIMENTAL
		this.outlineContours = new ArrayList<Vertex[]>();
		this.outlineBuffers = new ArrayList<FloatBuffer>();
		
		//Some Settings
		this.setFillDrawMode(GL10.GL_TRIANGLES);
		this.setName("unnamed triangle mesh");
		this.drawNormals = false;
		this.setNoStroke(true);
		
		this.setBoundsBehaviour(AbstractShape.BOUNDS_CHECK_THEN_GEOMETRY_CHECK);
	}

	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setGeometryInfo(org.mt4j.components.visibleComponents.GeometryInfo)
	 */
	@Override
	public void setGeometryInfo(GeometryInfo geometryInfo) {
		//KEEP IN MIND THAT THIS IS CALLED IN ABSTRACTSHAPE CONSTRUCTOR!
		//Create tris before setGeometryInfo, because in setGeometryInfo, 
		//defaultBounds are calced (may depend on triangles)
		this.createTriangles(geometryInfo);
		super.setGeometryInfo(geometryInfo);
		this.createDefaultNormals(geometryInfo);
	}



	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setVertices(org.mt4j.util.math.Vertex[])
	 */
	@Override
	public void setVertices(Vertex[] vertices) {
		//create tris before setVertices, because in setVerts, defaultBounds are calced (depend on triangles)
		this.createTriangles(new GeometryInfo(this.getRenderer(), vertices, this.getGeometryInfo().getNormals(), //TODO why not reconstruct() when geometryinfo already there?
				this.getGeometryInfo().getIndices()));
		super.setVertices(vertices);
		this.createDefaultNormals(this.getGeometryInfo());
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#computeDefaultBounds()
	 */
	@Override
	protected IBoundingShape computeDefaultBounds() {
		return new BoundingSphere(this);
//		return new OrientedBoundingBox(this));
	}

	

	/**
	 * Checks if default normals should be calculated if a
	 * new geometryInfo or new vertices are set on this mesh.
	 * 
	 * @return true, if is creates the default normals
	 */
	public boolean isCalculateDefaultNormals() {
		return calculateDefaultNormals;
	}


	/**
	 * Sets if default normals should be calculated if a
	 * new geometryInfo or new vertices are set on this mesh.
	 * 
	 * @param calculateDefaultNormals the new creates the default normals
	 */
	public void setCalculateDefaultNormals(boolean calculateDefaultNormals) {
		this.calculateDefaultNormals = calculateDefaultNormals;
	}
	
	/**
	 * Calculates default normals for this mesh if the mesh's geometryInfo doesent
	 * yet contain normals.
 	 * Creates face or vertex normals - <br>
	 * Face normals if the geometry isnt indexed and Vertex normals are created if the geometry
	 * IS indexed.
	 */
	public void calculateDefaultNormals(){
		boolean oldVal = this.isCalculateDefaultNormals();
		this.setCalculateDefaultNormals(true);
		this.createDefaultNormals(this.getGeometryInfo());
		this.setCalculateDefaultNormals(oldVal);
	}


	/**
	 * Creates face or vertex normals if the geometryinfo doesent already contain normals and 
	 * if isCalculateDefaultNormals() is true.
	 * Creates face normals if the geometry isnt indexed. Vertex normals are created if geometry
	 * IS indexed.
	 * 
	 * @param geometryInfo the geometry info
	 */
	private void createDefaultNormals(GeometryInfo geometryInfo){
		if (!this.getGeometryInfo().isContainsNormals() && this.isCalculateDefaultNormals()){
//			System.out.println("MTTriangleMesh object: \"" + this + "\" -> Create default normals.");
			//Create and set the face normals
			if (!geometryInfo.isIndexed()){
				Vector3D[] normals = this.getFaceOrVertexNormals();
				this.getGeometryInfo().setNormals(normals, true, this.isUseVBOs());
			}else{
				//System.err.println("Triangle mesh geometry contains no normals and is indexed -> To create normals use the MeshNormalGenerator!");
				Vector3D[] normals = this.getFaceOrVertexNormals();
				this.getGeometryInfo().setNormals(normals, true, this.isUseVBOs());
			}
		}
	}
	
	
	//TODO re-use old triangles array if same length => dont create new ones each time geometry changes its vertices (and cound stays same)
	
	/**
	 * Create triangles from the mesh information.
	 * <br>If the geometryInfo is indexed, one triangle is created per 3 indices.
	 * <br>If the geometry inst indexes, one triangle per 3 vertices is creates.
	 * 
	 * @param geom the geom
	 */
	private void createTriangles(GeometryInfo geom){
		Vertex[] vertices = geom.getVertices();
		ArrayList<Triangle> tris = new ArrayList<Triangle>();
		
		if (geom.isIndexed()){
			//System.out.println("MTTriangleMesh object: \"" + this.getName() + "\" Debug-> Supplied geometry is INDEXED");
			short[] indices = geom.getIndices();
			if (indices.length % 3 != 0){
				System.err.println("WARNING: the indices of the indexed mesh geometry:\"" + this.getName() + "\" arent dividable by 3 => probably no TRIANGLES indices provided!");
			}
			
			for (int i = 0; i < indices.length/3; i++) {
				int vertIndex0 = indices[i*3];
				int vertIndex1 = indices[i*3+1];
				int vertIndex2 = indices[i*3+2];
				
				Vertex v0 = vertices[vertIndex0];
				Vertex v1 = vertices[vertIndex1];
				Vertex v2 = vertices[vertIndex2];
				
				tris.add(new Triangle(v0, v1, v2, vertIndex0, vertIndex1, vertIndex2));
			}
		}else{
			//System.out.println("MTTriangleMesh object: \"" + this.getName() + "\" Debug-> Supplied geometry is NOT INDEXED");
			if (vertices.length % 3 != 0){
				System.err.println("WARNING: the vertices of the mesh geometry:\"" + this.getName() + "\" arent dividable by 3 => probably no TRIANGLES array provided!");
			}
			
			//geht nur bei vertices/3 = ganze zahl (vertices sind dreiecke!)
			for (int i = 0; i < vertices.length/3; i++) {
				int vertIndex0 = i*3;
				int vertIndex1 = i*3+1;
				int vertIndex2 = i*3+2;
				
				Vertex v0 = vertices[vertIndex0];
				Vertex v1 = vertices[vertIndex1];
				Vertex v2 = vertices[vertIndex2];
				
				tris.add(new Triangle(v0, v1, v2, vertIndex0, vertIndex1, vertIndex2));
			}
		}
		this.triangles = tris.toArray(new Triangle[tris.size()]);
//		System.out.println("MTTriangleMesh object: \"" + this + "\" Debug-> Triangles created: " + this.triangles.length);
	}
	
	


	/**
	 * Creates an array of normals, 1 for every vertex or index.
	 * , so every drawn vertex will have a normal information.
	 * <br>If the geometry is indexed, smooth interpolated normals
	 * are generated, else face normals are generated.
	 * 
	 * @return the face or vertex normals
	 * 
	 * the normals
	 */
	private Vector3D[] getFaceOrVertexNormals(){
		Vector3D[] normals = new Vector3D[this.triangles.length*3];
		
		GeometryInfo geom = this.getGeometryInfo();
		if (geom.isIndexed()){
			//Create smooth vertex normals, smoothed across all neighbors
			short[] indices = geom.getIndices();
			normals = new Vector3D[geom.getVertices().length];
			for (int i = 0; i < indices.length/3; i++) {
				if (normals[indices[i*3]] == null){
					normals[indices[i*3]] = triangles[i].getNormalLocal().getCopy();
				}else{
					normals[indices[i*3]].addLocal(triangles[i].getNormalLocal());
				}
				if (normals[indices[i*3+1]] == null){
					normals[indices[i*3+1]] = triangles[i].getNormalLocal().getCopy();
				}else{
					normals[indices[i*3+1]].addLocal(triangles[i].getNormalLocal());
				}
				if (normals[indices[i*3+2]] == null){
					normals[indices[i*3+2]] = triangles[i].getNormalLocal().getCopy();
				}else{
					normals[indices[i*3+2]].addLocal(triangles[i].getNormalLocal());
				}
			}
            for (Vector3D n : normals) {
                if (n == null) {
                    n = new Vector3D(0, 0, 1);
                } else {
                    n.normalizeLocal();
                }
            }
		}else
		{
			//Create face normals for unindexed geometry
			for (int i = 0; i < this.triangles.length; i++) {
				Triangle tri = this.triangles[i];
				normals[i*3] 	= tri.getNormalLocal();
				normals[i*3+1] 	= tri.getNormalLocal();
				normals[i*3+2] 	= tri.getNormalLocal();
			}
		}
//		System.out.println("MTTriangleMesh Debug-> Normals created: " + normals.length);
		return normals;
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#getCenterPointLocal()
	 */
	@Override
	public Vector3D getCenterPointLocal() {
		if (this.hasBounds()){
			return this.getBounds().getCenterPointLocal();
		}else{
			BoundingSphere tempBounds = new BoundingSphere(this);
			return tempBounds.getCenterPointLocal();
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#isGeometryContainsPointLocal(org.mt4j.util.math.Vector3D)
	 */
	@Override
	public boolean isGeometryContainsPointLocal(Vector3D testPoint) {
		// Point to local space - already done!
//		testPoint.transform(this.getGlobalInverseMatrix());
		
		//Send ray from the test point in x, y, and z direction 
		//and count the intersections (this is not really sufficient!)
		Ray ray0 = new Ray(new Vector3D(testPoint), new Vector3D(1,0,0));
		Ray ray1 = new Ray(new Vector3D(testPoint), new Vector3D(0,1,0));
		Ray ray2 = new Ray(new Vector3D(testPoint), new Vector3D(0,0,1));
		
		int i0 = this.getNumIntersections(ray0);
		int i1 = this.getNumIntersections(ray1);
		int i2 = this.getNumIntersections(ray2);
		
		/*
		System.out.println("I0:" + i0);
		System.out.println("I1:" + i1);
		System.out.println("I2:" + i2);
		*/
		
		//Check if intersection count is odd -> inside
		return ((i0 & 1 ) != 0) 
			&& ((i1 & 1 ) != 0)
			&& ((i2 & 1 ) != 0);
	}

	
	/**
	 * Check how often the ray intersects with the meshes triangles.
	 * 
	 * @param ray the ray
	 * 
	 * @return the number of intersecitons
	 */
	private int getNumIntersections(Ray ray){
		//Evtl auch bbox pr�fen?
		int intersectionsFound = 0;
		
		//Save intersections to check duplicates because at an edge
		//bewteeen 2 triangles both intersections are counted but
		//we still can say we�re on the inside
		boolean checkThoroughly = true;
		ArrayList<Vector3D> intersections = new ArrayList<Vector3D>();

        for (Triangle tri : triangles) {
            Vector3D intersectionPoint = tri.getRayTriangleIntersection(ray);
            boolean sameAlreadyEncountered = false;

            if (intersectionPoint != null) {
                if (checkThoroughly) {
                    for (Vector3D v : intersections) {
                        if (v.equalsVectorWithTolerance(intersectionPoint, ToolsMath.ZERO_TOLERANCE)) {
                            sameAlreadyEncountered = true;
                        }
                    }
                }
                if (!sameAlreadyEncountered) {
                    intersections.add(intersectionPoint);
                    intersectionsFound++;
                }
            }
        }
		intersections = null; //Clean
		return intersectionsFound;
	}
	

	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#getGeometryIntersectionLocal(org.mt4j.util.math.Ray)
	 */
	@Override
	public Vector3D getGeometryIntersectionLocal(Ray ray){
		float distance = Float.MAX_VALUE;
		Vector3D returnVect = null;
        for (Triangle tri : triangles) {
            Vector3D intersectionPoint = tri.getRayTriangleIntersection(ray);
            if (intersectionPoint != null) {
                float objDistance = intersectionPoint.getSubtracted(ray.getRayStartPoint()).length();

                //It is accurate to go through all triangles and use the closest intersection
                //but slower as just returning the first..
                if (objDistance <= distance) {
                    distance = objDistance;
                    returnVect = intersectionPoint;
                }
            }
        }
		/*
		if (returnVect != null){
			System.out.println("Picked mesh: " + this.getName());
		}
		*/
		return returnVect;
	}
	
	
	/**
	 * Gets the triangle count.
	 * 
	 * @return the triangle count
	 * 
	 * number of triangles in this mesh
	 */
	public int getTriangleCount(){
		return triangles.length;
	}
	
	/**
	 * The triangles of this triangle mesh.
	 * 
	 * @return the triangles
	 */
	public Triangle[] getTriangles(){
		if (this.triangles == null){
			this.createTriangles(this.getGeometryInfo());
			return this.triangles;
		}else{
			return this.triangles;
		}
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.AbstractVisibleComponent#drawComponent(processing.core.PGraphics)
	 */
	@Override
	public void drawComponent(PGraphics g) {
		PApplet pa = this.getRenderer();
		
		if (this.isUseDirectGL()){
//			GL gl = Tools3D.beginGL(g);
			GL10 gl = PlatformUtil.beginGL();
				this.drawComponent(gl);
//			Tools3D.endGL(g);
			PlatformUtil.endGL();
		}else{ //Draw with pure proccessing...
			pa.strokeWeight(this.getStrokeWeight());

			if (this.isDrawSmooth()) 
				pa.smooth();
			else 			
				pa.noSmooth();

			//NOTE: if noFill() and noStroke()->absolutely nothing will be drawn-even when texture is set
			if (this.isNoFill())	
				pa.noFill();
			else{
				MTColor fillColor = this.getFillColor();
				pa.fill(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
			}
			
			//Set the tint values 
			MTColor fillColor = this.getFillColor();
			pa.tint(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
			
			if (this.isNoStroke()) 	
				pa.noStroke();
			else{
				MTColor strokeColor = this.getStrokeColor();
				pa.stroke(strokeColor.getR(), strokeColor.getG(), strokeColor.getB(), strokeColor.getAlpha());
			}

			if (!this.isNoStroke()){
				pa.noFill(); 
				MTColor strokeColor = this.getStrokeColor();
				pa.stroke(strokeColor.getR(), strokeColor.getG(), strokeColor.getB(), strokeColor.getAlpha());
				pa.strokeWeight(2);
				
				if (this.isDrawSmooth())
					pa.smooth();
				
				for (Vertex[] outline : this.outlineContours){
					this.drawWithProcessing(g, outline, PApplet.POLYGON, false);
				}
			}

			if (!this.isNoFill()){
				pa.noStroke();
				pa.noSmooth();
				pa.fill(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
				this.drawWithProcessing(g, this.getVerticesLocal(), PApplet.TRIANGLES, true);
			}

			//ReSet the tint values to defaults 
			pa.tint(255, 255, 255, 255);

			if (this.isDrawSmooth())
				pa.noSmooth(); //because of the tesselation/antialias bug
		}

		if (drawNormals)
			this.drawNormals();
	}
	
	
	
	/**
	 * Can be used to draw if the gl context has already been set up and is ready to use.
	 * (processingApplet.beginGL() has been called etc..)
	 * 
	 * @param gl the gl
	 */
	public void drawComponent(GL10 gl) {
		if (this.isUseDisplayList()){
			int[] displayLists = this.getGeometryInfo().getDisplayListIDs();
			if (!this.isNoFill() && displayLists[0] != -1){
//				gl.glCallList(displayLists[0]);
				((GL11Plus)gl).glCallList(displayLists[0]);
			}
			if (!this.isNoStroke() && displayLists[1] != -1){
				if (this.outlineContours != null){
//					gl.glCallList(displayLists[1]);
					((GL11Plus)gl).glCallList(displayLists[1]);
				}
			}
		}else{
			if (!(this.isNoFill() && this.isNoStroke())){
				this.drawPureGl(gl);
			}
		}
	}


	/**
	 * loops through all the vertices of the polygon
	 * and uses processings "vertex()" command to set their position
	 * and texture.
	 *
	 * @param g the g
	 * @param vertices the vertices
	 * @param drawMode the draw mode
	 * @param useTexture the use texture
	 */
	protected void drawWithProcessing(PGraphics g, Vertex[] vertices, int drawMode, boolean useTexture){
		g.beginShape(drawMode); 
		if (this.getTexture() != null && this.isTextureEnabled() && useTexture){
			g.texture(this.getTexture());
			g.textureMode(this.getTextureMode());
		}
		if (this.getGeometryInfo().isIndexed()){
			short[] indices =  this.getGeometryInfo().getIndices();
            for (int index : indices) {
                drawP5Vertex(g, vertices[index], useTexture);
            }
		}
		else{
            for (Vertex vertice : vertices) {
                drawP5Vertex(g, vertice, useTexture);
            }
		}
		g.endShape();
	}
	
	/**
	 * Draw p5 vertex.
	 *
	 * @param g the g
	 * @param v the v
	 * @param useTexture the use texture
	 */
	private void drawP5Vertex(PGraphics g, Vertex v, boolean useTexture){
		if (this.isTextureEnabled() && useTexture){
			g.vertex(v.x, v.y, v.z, v.getTexCoordU(), v.getTexCoordV());
		}else{
			if (v.getType() == Vector3D.BEZIERVERTEX){
				BezierVertex b = (BezierVertex)v;
				g.bezierVertex(
						b.getFirstCtrlPoint().x,  b.getFirstCtrlPoint().y,  b.getFirstCtrlPoint().z, 
						b.getSecondCtrlPoint().x, b.getSecondCtrlPoint().y, b.getSecondCtrlPoint().z, 
						b.x, b.y, b.z  );
			}
			else{
				g.vertex(v.x, v.y, v.z);
			}
		}
	}


	/**
	 * Draws the face normals of the mesh.
	 */
	private void drawNormals(){
		PApplet r = this.getRenderer();
//		Vector3D[] normals = this.getGeometryInfo().getNormals();
		r.stroke(255, 0, 0);
		r.strokeWeight(0.5f);

        for (Triangle t : triangles) {
            r.pushMatrix();
            Vector3D centerPoint = t.getCenterPointLocal();
            r.translate(centerPoint.x, centerPoint.y, centerPoint.z);
            r.scale(-2, -2, -2);

            r.line(0, 0, 0, t.getNormalLocal().x, t.getNormalLocal().y, t.getNormalLocal().z);
            r.popMatrix();
        }
//		for (int i = 0; i < normals.length; i++) {
//			Vector3D vector3D = normals[i];
//		}
	}
	
	
	/**
	 * Draws the mesh with ogl functions.
	 * 
	 * @param gl the gl
	 */
	protected void drawPureGl(GL10 gl){
		GL11Plus gl11Plus = PlatformUtil.getGL11Plus();
		GL11 gl11 = PlatformUtil.getGL11();
			
		//Get display array/buffer pointers
		FloatBuffer tbuff 			= this.getGeometryInfo().getTexBuff();
		FloatBuffer vertBuff 		= this.getGeometryInfo().getVertBuff();
		FloatBuffer colorBuff 		= this.getGeometryInfo().getColorBuff();
		Buffer indexBuff 			= this.getGeometryInfo().getIndexBuff(); //null if not indexed
		
		//Enable Pointers, set vertex array pointer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		if (this.isUseVBOs()){//Vertices
//			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBOVerticesName());
//			gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBOVerticesName());
			gl11.glVertexPointer(3, GL10.GL_FLOAT, 0, 0);
		}else{
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuff);
		}
		
		//Default texture target
		int textureTarget = GL10.GL_TEXTURE_2D;
		
		/////// DRAW SHAPE ///////
		if (!this.isNoFill()){ 
			boolean textureDrawn = false;
			if (this.isTextureEnabled()
				&& this.getTexture() != null 
				&& this.getTexture() instanceof GLTexture) //Bad for performance?
			{
				GLTexture tex = (GLTexture)this.getTexture();
				textureTarget = tex.getTextureTarget();
				
				//tells opengl which texture to reference in following calls from now on!
				//the first parameter is eigher GL.GL_TEXTURE_2D or ..1D
				gl.glEnable(textureTarget);
				gl.glBindTexture(textureTarget, tex.getTextureID());
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				
				if (this.isUseVBOs()){//Texture
//					gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBOTextureName());
//					gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
					gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBOTextureName());
					gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0);
				}else{
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tbuff);
				}
				textureDrawn = true;
			}
			
			// Normals
			if (this.getGeometryInfo().isContainsNormals()){
				gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
				if (this.isUseVBOs()){
//					gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBONormalsName());
//					gl.glNormalPointer(GL.GL_FLOAT, 0, 0); 
					gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBONormalsName());
					gl11.glNormalPointer(GL10.GL_FLOAT, 0, 0); 
				}else{
					gl.glNormalPointer(GL10.GL_FLOAT, 0, this.getGeometryInfo().getNormalsBuff());
				}
			}
			
			if (this.isUseVBOs()){//Color
//				gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBOColorName());
//				gl.glColorPointer(4, GL.GL_FLOAT, 0, 0);
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBOColorName());
				gl11.glColorPointer(4, GL10.GL_FLOAT, 0, 0);
			}else{
				gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuff);
			}
			
			//DRAW with drawElements if geometry is indexed, else draw with drawArrays!
			if (this.getGeometryInfo().isIndexed()){
//				gl.glDrawElements(this.getFillDrawMode(), indexBuff.capacity(), GL11Plus.GL_UNSIGNED_INT, indexBuff); //limit() oder capacity()??
				gl.glDrawElements(this.getFillDrawMode(), indexBuff.limit(), GL11.GL_UNSIGNED_SHORT, indexBuff); //limit() oder capacity()??
			}else{
				gl.glDrawArrays(this.getFillDrawMode(), 0, vertBuff.capacity()/3);
			}
			
			if (this.getGeometryInfo().isContainsNormals()){
				gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
			}

			if (textureDrawn){
				gl.glBindTexture(textureTarget, 0);//Unbind texture
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glDisable(textureTarget); //weiter nach unten?
			}
		}
		
		boolean outlineDrawn = false;
		////////// DRAW OUTLINE ////////
		if (!this.isNoStroke()
				&& this.outlineBuffers != null //FIXME EXPERIMENT
				&& this.outlineContours != null
		){ 
			outlineDrawn = true;
			
//			FloatBuffer strokeColBuff = this.getStrokeColBuff();
//			if (this.isUseVBOs()){
//				gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.getVBOStrokeColorName());
//				gl.glColorPointer(4, GL.GL_FLOAT, 0, 0);
//				
//				gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);//Experimental 
//			}else{
//				gl.glColorPointer(4, GL.GL_FLOAT, 0, strokeColBuff);
//			}
			
			//Turn on smooth outlines
//			if (this.isDrawSmooth())
//				gl.glEnable(GL.GL_LINE_SMOOTH);
			//FIXME TEST
			Tools3D.setLineSmoothEnabled(gl, true);
			
//			/*
			//SET LINE STIPPLE
			short lineStipple = this.getLineStipple();
			if (lineStipple != 0){
//				gl.glLineStipple(1, lineStipple);
				gl11Plus.glLineStipple(1, lineStipple);
				gl.glEnable(GL11Plus.GL_LINE_STIPPLE);
			}
			//*/
			
			if (this.getStrokeWeight() > 0)
				gl.glLineWidth(this.getStrokeWeight());
			
			//Dont use geometryinfo strokecolor buffer because its useless in a trianglemesh 
			//instead we use a single, simple stroke color and custom outlines, if provided 
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY); //disable color buffer use
			gl.glColor4f(strokeR, strokeG, strokeB, strokeA);
			
			//Always use just buffes and drawarrays instead of vbos..too complicated for a simple outline..
			for(FloatBuffer outlineBuffer : this.outlineBuffers){ //FIXME EXPERIMENTAL
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, outlineBuffer); 
				gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, outlineBuffer.capacity()/3);
			}
			
			/*
			//DRAW mesh outline
			//Draw with drawElements if geometry is indexed, else draw with drawArrays!
			if (this.getGeometryInfo().isIndexed()){
				gl.glDrawElements(GL.GL_LINES, indexBuff.limit(), GL.GL_UNSIGNED_INT, indexBuff);
//				gl.glDrawElements(this.getFillDrawMode(), indexBuff.capacity(), GL.GL_UNSIGNED_INT, indexBuff);
			}else{
				gl.glDrawArrays(GL.GL_LINES, 0, vertBuff.capacity()/3);
			}
			*/
			//RESET LINE STIPPLE
			if (lineStipple != 0){
				gl.glDisable(GL11Plus.GL_LINE_STIPPLE);
			}
//			if (this.isDrawSmooth())
//				gl.glDisable(GL.GL_LINE_SMOOTH);
			//FIXME TEST
			Tools3D.setLineSmoothEnabled(gl, false);
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if (!outlineDrawn){ 
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY); //If outline drawn we disabled color_array earlier
		}
		
		if (this.isUseVBOs()){
//			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
//			gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
	}


	/**
	 * Checks if is draw normals.
	 * 
	 * @return true, if is draw normals
	 */
	public boolean isDrawNormals() {
		return drawNormals;
	}
	
	/**
	 * Sets the draw normals.
	 * 
	 * @param drawNormals the new draw normals
	 */
	public void setDrawNormals(boolean drawNormals) {
		this.drawNormals = drawNormals;
	}


	@Override
	protected void destroyComponent() {
		this.triangles = null;
//		if (!this.outlineBuffers.isEmpty()){
//			outline = getGeometryInfo().getDisplayListIDs()[1];
//		}
		this.outlineBuffers.clear();
	}
	
	

	/**
	 * Gets the outline contours.
	 * 
	 * @return the outline contours
	 */
	public List<Vertex[]> getOutlineContours() {
		return this.outlineContours;
	}
	
	/**
	 * Creates the outline buffers.
	 */
	private void createOutlineBuffers(){
		outlineBuffers.clear();
		for (Vertex[] outline : this.outlineContours){
			outlineBuffers.add(ToolsBuffers.generateVertexBuffer(outline));
		}
	}
	
	
	/** The stroke r. */
	private float strokeR = 0;
	
	/** The stroke g. */
	private float strokeG = 1;
	
	/** The stroke b. */
	private float strokeB = 1;
	
	/** The stroke a. */
	private float strokeA = 1;

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setStrokeColor(org.mt4j.util.MTColor)
	 */
	@Override
	public void setStrokeColor(MTColor strokeColor) {
		super.setStrokeColor(strokeColor);
		this.strokeR = strokeColor.getR()/255f;
		this.strokeG = strokeColor.getG()/255f;
		this.strokeB = strokeColor.getB()/255f;
		this.strokeA = strokeColor.getAlpha()/255f;
	}


	/**
	 * Sets the outline contours.
	 * <br><strong>NOTE: </strong> To draw the outlines,
	 * the default setting of noStroke has to be changed to false!
	 * 
	 * @param contours the new outline contours
	 */
	public void setOutlineContours(List<Vertex[]> contours) {
		this.outlineContours = contours;
		this.createOutlineBuffers();
		
		if (this.isUseDisplayList()){
			int[] ids = this.getGeometryInfo().getDisplayListIDs();
			//Delete default outline display list, not really usable in a mesh.
			if (MT4jSettings.getInstance().isOpenGlMode()){
//				GL gl =Tools3D.getGL(getRenderer());
				GL11Plus gl = PlatformUtil.getGL11Plus();
				if (ids[1] != -1){
					gl.glDeleteLists(ids[1], 1);
				}
			}
			if (this.outlineContours != null){
				ids[1] = generateContoursDisplayList(true);
				this.getGeometryInfo().setDisplayListIDs(ids);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#generateDisplayLists()
	 */
	@Override
	public void generateDisplayLists(){
		//Delete default outline display list, not really usable in a mesh. To draw all triangles outlines..
		if (MT4jSettings.getInstance().isOpenGlMode()
			&& this.isUseDirectGL()
		){
			if (this.outlineContours != null){
				//Dont create default stroke outline display list (mostly useless with triangle meshes)
				this.getGeometryInfo().generateDisplayLists(this, true, false);
				int[] ids = this.getGeometryInfo().getDisplayListIDs();
				if (ids[1] != -1){
//					GL gl = Tools3D.getGL(getRenderer());
					GL11Plus gl = PlatformUtil.getGL11Plus();
					gl.glDeleteLists(ids[1], 1);
				}
				//Create outline display list from manually set outline contours if available.
				ids[1] = this.generateContoursDisplayList(true);
				this.getGeometryInfo().setDisplayListIDs(ids);
			}else{
				super.generateDisplayLists(); //create default display lists
			}
		}
	}
	
	/**
	 * Gens a displaylists of the outline contours (for svgs important for example, not so much for 3d objects..)
	 * 
	 * @return the int
	 */
	protected int generateContoursDisplayList(boolean useColor){
//		GL gl = Tools3D.getGL(getRenderer());
//		GL10 gl = GraphicsUtil.getGL();
		GL11Plus gl = PlatformUtil.getGL11Plus();
		
		int listId = gl.glGenLists(1);
		if (listId == 0){
			System.err.println("Failed to create display list");
			return 0;
		}

		gl.glNewList(listId, GL11Plus.GL_COMPILE);
//		if (this.isDrawSmooth()){
//			gl.glEnable(GL.GL_LINE_SMOOTH); 
//		}
		//TEST
		Tools3D.setLineSmoothEnabled(gl, true);
		gl.glLineWidth(this.getStrokeWeight());
		FloatBuffer strokeColBuff = this.getGeometryInfo().getStrokeColBuff(); 
		if (useColor)
			gl.glColor4f (strokeColBuff.get(0), strokeColBuff.get(1), strokeColBuff.get(2), strokeColBuff.get(3));
		
//		/*
		//USE BUFFERS
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		for (FloatBuffer buffer : this.outlineBuffers) {
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
			gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, buffer.capacity()/3);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//		*/
		
		/*
		//Immediate mode - wont work in OpenGL ES
		for (Vertex[] varr : this.outlineContours){
			gl.glBegin(GL.GL_LINE_STRIP);
			for(Vertex v : varr){
				gl.glVertex3f(v.x, v.y, v.z);
			}
			gl.glEnd();
		}
		*/
		
		//TEST
		Tools3D.setLineSmoothEnabled(gl, false);
		gl.glEndList();
		return listId;
	}

	

	
	/*
	private Vector3D[] generateNonIndexedNormals(){
		GeometryInfo geom 			= this.getGeometryInfo();
		NonIndexedNormalGenerator n = new NonIndexedNormalGenerator();
		
		Vertex[] vertices = geom.getVerticesLocal();
		float[] verts = new float[vertices.length*3];
		
		for (int i = 0; i < verts.length/3; i++) {
			verts[i*3] 		= vertices[i].x;
			verts[i*3+1] 	= vertices[i].y;
			verts[i*3+2] 	= vertices[i].z;
		}
		
		float[] normals = n.generateNormals(verts , geom.getIndices(), 90);
		Vector3D[] normalVecs = new Vector3D[normals.length/3];
		
		for (int i = 0; i < normalVecs.length; i++) {
			normalVecs[i] = new Vector3D(normals[i*3], normals[i*3+1],normals[i*3+2]);
		}
		return normalVecs;
	}
	*/

}
