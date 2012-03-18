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
package org.mt4j.components.visibleComponents.shapes;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import org.mt4j.components.bounds.BoundingSphere;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.bounds.OrientedBoundingBox;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.math.BezierVertex;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11;
import org.mt4j.util.opengl.GL11Plus;
import org.mt4j.util.opengl.GLTexture;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * This class represents a planar, convex polygon. The user of this class
 * is responsible for the polygon being planar and convex.
 * Methods like picking and others depend on these facts.
 * If <code>setNoFill(true)</code> is used and the polygon isnt closed, the
 * class can also be used to display a poly-line.
 * 
 * @author Christopher Ruff
 */
public class MTPolygon extends MTCSSStylableShape{
	
	/** The normal. */
	private Vector3D normal;
	
	/** The normal dirty. */
	private boolean normalDirty;
	
	/**
	 * Instantiates a new mT polygon.
	 * 
	 * @param vertices the vertices
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTPolygon(Vertex[] vertices, PApplet pApplet) {
		this(pApplet,vertices);
	}
	
	/**
	 * Instantiates a new mT polygon.
	 * 
	 * @param pApplet the applet
	 * @param vertices the vertices
	 */
	public MTPolygon(PApplet pApplet, Vertex[] vertices) { //Added for consitency
		super(pApplet, vertices);
		
		this.normalDirty = true;
//		this.hasVertexColor = false;//Dont set here, gets set to false after being true in super constructor
		
		this.setTextureEnabled(false);
		this.setTextureMode(PApplet.NORMAL);
		
		this.setEnabled(true);
		this.setVisible(true);
		
		this.setDrawSmooth(true);
		this.setNoStroke(false);
		this.setNoFill(false);
		this.setName("Polygon");
		
		this.setBoundsBehaviour(AbstractShape.BOUNDS_DONT_USE);
//		this.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#computeDefaultBounds()
	 */
	@Override
	protected IBoundingShape computeDefaultBounds(){
		return new BoundingSphere(this);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setGeometryInfo(org.mt4j.components.visibleComponents.GeometryInfo)
	 */
	@Override
	public void setGeometryInfo(GeometryInfo geometryInfo) {
		super.setGeometryInfo(geometryInfo);
		this.normalDirty = true;
		
		//FIXME TEST 
		//If we use processings drawing we have to check if the geometry has individually colored vertices
		if (!MT4jSettings.getInstance().isOpenGlMode() || (MT4jSettings.getInstance().isOpenGlMode() && this.isUseDirectGL())){
			this.hasVertexColor = this.hasVertexColors(geometryInfo);
		}
	}
	
	//FIXME TEST
	/** The has vertex color. */
	private boolean hasVertexColor;
	
	/**
	 * Checks for vertex colors.
	 * 
	 * @param geometryInfo the geometry info
	 * @return true, if successful
	 */
	private boolean hasVertexColors(GeometryInfo geometryInfo){
		Vertex[] verts = geometryInfo.getVertices();
        for (Vertex vertex : verts) {
            if (vertex.getR() != Vertex.DEFAULT_RED_COLOR_COMPONENT ||
                    vertex.getG() != Vertex.DEFAULT_GREEN_COLOR_COMPONENT ||
                    vertex.getB() != Vertex.DEFAULT_BLUE_COLOR_COMPONENT ||
                    vertex.getA() != Vertex.DEFAULT_ALPHA_COLOR_COMPONENT
                    ) {
                return true;
            }
        }
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setUseDirectGL(boolean)
	 */
	@Override
	public void setUseDirectGL(boolean drawPureGL) {
		super.setUseDirectGL(drawPureGL);
		//If we use processings drawing we have to check if the geometry has individually colored vertices
		if (!drawPureGL && !this.hasVertexColor){ 
			this.hasVertexColor = this.hasVertexColors(this.getGeometryInfo());
		}
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setVertices(org.mt4j.util.math.Vertex[])
	 */
	@Override
	public void setVertices(Vertex[] vertices) {
		super.setVertices(vertices);
		this.normalDirty = true;
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.AbstractVisibleComponent#drawComponent(processing.core.PGraphics)
	 */
	@Override
	public void drawComponent(PGraphics g) {
//		super.drawComponent(g);
		
		//Draw the shape
		if (MT4jSettings.getInstance().isOpenGlMode()   
		   && this.isUseDirectGL()){
//			GL gl = Tools3D.beginGL(renderer);
			GL10 gl = PlatformUtil.beginGL();
			
			//Draw with PURE opengl
			if (this.isUseDisplayList() /*&& this.getDisplayListIDs() != null && this.getDisplayListIDs()[0] != -1 && this.getDisplayListIDs()[1] != -1*/){
				int[] displayLists = this.getGeometryInfo().getDisplayListIDs();
				//Use Display Lists
				if (!this.isNoFill()  && displayLists[0] != -1) 
//					gl.glCallList(displayLists[0]); //Draw fill
					((GL11Plus)gl).glCallList(displayLists[0]); //Draw fill
				if (!this.isNoStroke()  && displayLists[1] != -1)
//					gl.glCallList(displayLists[1]); //Draw outline
					((GL11Plus)gl).glCallList(displayLists[1]); //Draw outline
			}else{
				//Use Vertex Arrays or VBOs
				this.drawPureGl(gl);
			}
//			Tools3D.endGL(renderer);
			PlatformUtil.endGL();
		}else{ //Draw with pure proccessing commands...
			MTColor fillColor = this.getFillColor();
			MTColor strokeColor = this.getStrokeColor();
			g.fill(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
			g.stroke(strokeColor.getR(), strokeColor.getG(), strokeColor.getB(), strokeColor.getAlpha());
			g.strokeWeight(this.getStrokeWeight());

			if (MT4jSettings.getInstance().isOpenGlMode())
				if (this.isDrawSmooth()) 
					g.smooth();
				else 			
					g.noSmooth();

			//NOTE: if noFill() and noStroke()->absolutely nothing will be drawn-even when texture is set
			if (this.isNoFill())	
				g.noFill();
			if (this.isNoStroke()) 	
				g.noStroke();

			//Set the tint values 
			g.tint(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());

			//handles the drawing of the vertices with the texture coordinates
			//try doing a smoothed poly outline with opengl
			if (
				MT4jSettings.getInstance().isOpenGlMode()  &&
				 this.isDrawSmooth() &&
				 !this.isNoStroke() &&
				 !this.isUseDirectGL()
			){
				//draw FILL of polygon, without smooth or stroke
				g.noStroke();
				g.noSmooth();
				drawWithProcessing(g); 
				
				// DRAW SMOOTHED THE STROKE outline OF THE POLYGON WIHTOUT FILL OR TEXTURE
				g.smooth();
				g.noFill(); 
				g.stroke(strokeColor.getR(), strokeColor.getG(), strokeColor.getB(), strokeColor.getAlpha());
				drawWithProcessing(g); 

				g.noSmooth();
//				//restore fill color
//				g.fill(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
			}else{
				drawWithProcessing(g);
			}//end if gl and smooth

			//reSet the tint values to defaults 
			g.tint(255, 255, 255, 255);

			if (/*MT4jSettings.getInstance().isOpenGlMode() &&*/ this.isDrawSmooth())
				g.noSmooth(); //because of tesselation bug/lines visibile in shapes
		}
		
	}
	
	
	
	/**
	 * loops through all the vertices of the polygon
	 * and uses processings "vertex()" command to set their position
	 * and texture.
	 * @param g PGraphics
	 */
	protected void drawWithProcessing(PGraphics g){
		g.beginShape(PApplet.POLYGON); //TODO make setbeginshape() behavior settable
		if (this.getTexture() != null && this.isTextureEnabled()){
			g.texture(this.getTexture());
			g.textureMode(this.getTextureMode());
		}
		Vertex[] vertices = this.getVerticesLocal();

        for (Vertex v : vertices) {
            //FIXME TEST
            if (this.hasVertexColor) {
                g.fill(v.getR(), v.getG(), v.getB(), v.getA()); //takes vertex colors into account
            }

            if (this.getTexture() != null && this.isTextureEnabled())
                g.vertex(v.x, v.y, v.z, v.getTexCoordU(), v.getTexCoordV());
            else {
                if (v.getType() == Vector3D.BEZIERVERTEX) {
                    BezierVertex b = (BezierVertex) v;
                    g.bezierVertex(
                            b.getFirstCtrlPoint().x, b.getFirstCtrlPoint().y, b.getFirstCtrlPoint().z,
                            b.getSecondCtrlPoint().x, b.getSecondCtrlPoint().y, b.getSecondCtrlPoint().z,
                            b.x, b.y, b.z);
                } else
                    g.vertex(v.x, v.y, v.z);
            }
        }
		g.endShape();
	}
	
	
	/*
	 *TODO
	 * To do multi-texture:
	 * 
	 * glClientActiveTexture(GL_TEXTURE1);
	 * glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	 * glTexCoordPointer(2, GL_FLOAT, sizeof(myVertex), &myQuad[0].s1);
	 *
	 */

	/**
	 * Draws with pure opengl commands (without using processing) using vertex arrays, or vbos for speed.
	 * It is assumed that PGraphicsOpenGL's beginGL() method has already been called
	 * before calling this method!
	 * 
	 * @param gl the gl
	 */
	protected void drawPureGl(GL10 gl){
		GL11 gl11 = PlatformUtil.getGL11();
		GL11Plus gl11Plus = PlatformUtil.getGL11Plus();
		
//		/*
		//Get display array/buffer pointers
		FloatBuffer tbuff 			= this.getGeometryInfo().getTexBuff();
		FloatBuffer vertBuff 		= this.getGeometryInfo().getVertBuff();
		FloatBuffer colorBuff 		= this.getGeometryInfo().getColorBuff();
		FloatBuffer strokeColBuff 	= this.getGeometryInfo().getStrokeColBuff();
		Buffer indexBuff 			= this.getGeometryInfo().getIndexBuff();
		
		//Enable Pointers, set vertex array pointer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		if (this.isUseVBOs()){//Vertices
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
					gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBOTextureName());
					gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0);
				}else
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tbuff);
				
				textureDrawn = true;
			}
			
			if (this.isUseVBOs()){//Color
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBOColorName());
				gl11.glColorPointer(4, GL10.GL_FLOAT, 0, 0);
			}else{
				gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuff);
			}
			
			//Normals
			if (this.getGeometryInfo().isContainsNormals()){
				gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
				if (this.isUseVBOs()){
					gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBONormalsName());
					gl11.glNormalPointer(GL10.GL_FLOAT, 0, 0); 
				}else{
					gl.glNormalPointer(GL10.GL_FLOAT, 0, this.getGeometryInfo().getNormalsBuff());
				}
			}
			
			//DRAW //Draw with drawElements if geometry is indexed, else draw with drawArrays!
			if (this.getGeometryInfo().isIndexed()){
				gl.glDrawElements(this.getFillDrawMode(), indexBuff.limit(), GL10.GL_UNSIGNED_SHORT, indexBuff);
//				gl.glDrawElements(this.getFillDrawMode(), indexBuff.capacity(), GL11Plus.GL_UNSIGNED_INT, indexBuff);
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
		
		////////// DRAW OUTLINE ////////
		if (!this.isNoStroke()){ 
			if (this.isUseVBOs()){
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.getGeometryInfo().getVBOStrokeColorName());
				gl11.glColorPointer(4, GL10.GL_FLOAT, 0, 0);
			}else{
				gl.glColorPointer(4, GL10.GL_FLOAT, 0, strokeColBuff);
			}
			
			
//			//Turn on smooth outlines
//			if (this.isDrawSmooth()){
//				gl.glEnable(GL.GL_LINE_SMOOTH);
//			}
			//FIXME TEST
			Tools3D.setLineSmoothEnabled(gl, true);
			
//			/*
			//SET LINE STIPPLE
				short lineStipple = this.getLineStipple();
				if (lineStipple != 0){
					gl11Plus.glLineStipple(1, lineStipple);
					gl.glEnable(GL11Plus.GL_LINE_STIPPLE);
				}
//			*/
			
			if (this.getStrokeWeight() > 0)
				gl.glLineWidth(this.getStrokeWeight());
			
			//DRAW Polygon outline
			//Draw with drawElements if geometry is indexed, else draw with drawArrays!
			if (this.getGeometryInfo().isIndexed()){
				gl.glDrawElements(GL10.GL_LINE_STRIP, indexBuff.limit(), GL10.GL_UNSIGNED_SHORT, indexBuff);
//				gl.glDrawElements(this.getFillDrawMode(), indexBuff.capacity(), GL.GL_UNSIGNED_INT, indexBuff);
			}else{
				gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, vertBuff.capacity()/3); 
			}
			
			//RESET LINE STIPPLE
			if (lineStipple != 0){
				gl.glDisable(GL11Plus.GL_LINE_STIPPLE);
			}
			
			//FIXME TEST 
			Tools3D.setLineSmoothEnabled(gl, false);
			/*
			if (this.isDrawSmooth())
				gl.glDisable(GL.GL_LINE_SMOOTH);
			 */
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
		//TEST
		if (this.isUseVBOs()){
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
//		*/
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#isGeometryContainsPointLocal(org.mt4j.util.math.Vector3D)
	 */
	@Override
	public boolean isGeometryContainsPointLocal(Vector3D testPoint) { 
		return ToolsGeometry.isPolygonContainsPoint(this.getVerticesLocal(), testPoint);
	}


	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#getGeometryIntersectionLocal(org.mt4j.util.math.Ray)
	 */
	@Override
	public Vector3D getGeometryIntersectionLocal(Ray ray){
		Vector3D[] vertices;
		vertices = this.getVerticesLocal();
		Vector3D polyNormal 	= this.getNormal();
		
		//Possible intersection point in plane of polygon
		Vector3D interSectPoint = ToolsGeometry.getRayPlaneIntersection(ray, polyNormal, vertices[0]);
		
		if (interSectPoint == null)
			return null;
		
		return (ToolsGeometry.isPoint3DInPlanarPolygon(vertices, interSectPoint, polyNormal) ? interSectPoint : null);
	}


	
	/**
	 * Returns a normalized vector, perpendicular to the polygon (the normal)<br>
	 * <br>The normal vector is calculated in local object space! To transform it into
	 * world space use <code>normal.transformNormal(Matrix worldMatrix);</code>
	 * <br><b>NOTE:</b> The polygon has to have at least 3 vertices, the Polygon has to be coplanar!
	 * <br><b>NOTE:</b> Uses the three first vertices for computation, so make sure there arent duplicates!
	 * 
	 * @return the normal vector
	 */
	public Vector3D getNormal(){
		try {
			if (normalDirty){
				Vertex[] vertices;
				vertices = this.getVerticesLocal();
				if (vertices[0].equalsVector(vertices[1])
					|| vertices[0].equalsVector(vertices[2])
				){
					System.err.println("Warning: in component " + this.getName() + ", 2 vectors for normal computation are equal -> bad results! -" + this);
				}
				this.normal = ToolsGeometry.getNormal(vertices[0], vertices[1], vertices[2], true);
				this.normalDirty = false;
				return this.normal.getCopy(); 
			}else{
				return this.normal.getCopy();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Vector3D(0,0,1);
		}
	}
	

	
	/**
	 * Calculates the area of a 2D polygon using its transformed world coordinates
	 * <br>NOTE: works only if the last vertex is equal to the first (polygon is closed correctly).
	 * 
	 * @return the area as double
	 */
	public double get2DPolygonArea(){
		return ToolsGeometry.getPolygonArea2D(this.getVerticesGlobal());
	}
	
	
	/**
	 * Calculates the center of mass of the polygon.
	 * NOTE: works only if the last vertex is equal to the first (polygon is closed correctly)
	 * NOTE: polygon needs to be coplanar and in the X,Y plane!
	 * 
	 * @return the center or mass as a Vector3D
	 */
	public Vector3D getCenterOfMass2DLocal(){
		return ToolsGeometry.getPolygonCenterOfMass2D(this.getVerticesLocal());
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#getCenterPointLocal()
	 */
	@Override
	public Vector3D getCenterPointLocal(){
		if (this.hasBounds()){
			return this.getBounds().getCenterPointLocal();
		}else{
			return new OrientedBoundingBox(this).getCenterPointLocal();
		}
	}
	
	
	
	/**
	 * Scales this shape to the given width and height. Relative to its parent frame of reference.
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param width the width
	 * @param height the height
	 * 
	 * @return true, if sets the size xy relative to parent
	 * 
	 * returns false if negative values are put in
	 */
	public boolean setSizeXYRelativeToParent(float width, float height){
		if (width > 0 && height > 0){
			Vector3D centerPoint = this.getCenterPointRelativeToParent();
			this.scale( (1f/this.getWidthXYRelativeToParent()) * width, (1f/this.getHeightXYRelativeToParent()) * height, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	/**
	 * Scales this shape to the given width and height in the XY-Plane. Relative to world space.
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param width the width
	 * @param height the height
	 * 
	 * @return true, if sets the size xy global
	 */
	public boolean setSizeXYGlobal(float width, float height){
		if (width > 0 && height > 0){
			Vector3D centerPoint = this.getCenterPointGlobal();
			this.scaleGlobal( (1f/this.getWidthXYGlobal())* width , (1f/this.getHeightXYGlobal()) * height, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	/**
	 * Scales the shape to the given height relative to parent space.
	 * Aspect ratio is preserved! The scaling is done Axis aligned, so
	 * shearing might occour if rotated!
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param height the height
	 * 
	 * @return true, if the height isnt negative
	 */
	public boolean setHeightXYRelativeToParent(float height){
		if (height > 0){
			Vector3D centerPoint = this.getCenterPointRelativeToParent();
			float factor = (1f/this.getHeightXYRelativeToParent()) * height;
			this.scale(factor, factor, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	/**
	 * Scales the shape to the given height relative to world space.
	 * Aspect ratio is preserved! The scaling is done Axis aligned, so
	 * shearing might occour if rotated!
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param height the height
	 * 
	 * @return true, if sets the height xy global
	 */
	public boolean setHeightXYGlobal(float height){
		if (height > 0){
			Vector3D centerPoint = this.getCenterPointGlobal();
			float factor = (1f/this.getHeightXYGlobal())* height;
			this.scaleGlobal(factor, factor, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	/**
	 * Scales the shape to the given width relative to parent space.
	 * Aspect ratio is preserved! 
	 * <br>NOTE: The scaling is done Axis aligned, so
	 * shearing might occour if rotated before!
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param width the width
	 * 
	 * @return true, if the width isnt negative
	 */
	public boolean setWidthXYRelativeToParent(float width){
		if (width > 0){
			Vector3D centerPoint = this.getCenterPointRelativeToParent(); 
			float factor = (1f/this.getWidthXYRelativeToParent()) * width;
			this.scale(factor, factor, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	/**
	 * Scales the shape to the given width relative to world space.
	 * Aspect ratio is preserved! The scaling is done Axis aligned, so
	 * shearing might occour if rotated!
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param width the width
	 * 
	 * @return true, if sets the width xy global
	 */
	public boolean setWidthXYGlobal(float width){
		if (width > 0){
			Vector3D centerPoint = this.getCenterPointGlobal();
			float factor = (1f/this.getWidthXYGlobal())* width;
			this.scaleGlobal(factor, factor, 1, centerPoint);
			return true;
		}else
			return false;
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#destroyComponent()
	 */
	@Override
	protected void destroyComponent() { 	}

	@Override
	protected void applyStyleSheetCustom(CSSStyle virtualStyleSheet) {
		if (virtualStyleSheet.isModifiedBackgroundImage()) {
			getCssHelper().setBackground(this);
		}
	}


}
