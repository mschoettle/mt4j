/*
	 * Copyright (c) 2003-2009 jMonkeyEngine
	 * All rights reserved.
	 *
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions are
	 * met:
	 *
	 * * Redistributions of source code must retain the above copyright
	 *   notice, this list of conditions and the following disclaimer.
	 *
	 * * Redistributions in binary form must reproduce the above copyright
	 *   notice, this list of conditions and the following disclaimer in the
	 *   documentation and/or other materials provided with the distribution.
	 *
	 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
	 *   may be used to endorse or promote products derived from this software 
	 *   without specific prior written permission.
	 *
	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
	 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
	 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
	 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
	 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
	 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
	 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
	 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
	 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
	 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 */
package org.mt4j.components.bounds;

import java.nio.FloatBuffer;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.components.visibleComponents.shapes.mesh.Triangle;
import org.mt4j.util.camera.IFrustum;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Plane;
import org.mt4j.util.math.Quaternion;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.ToolsBuffers;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;

import processing.core.PGraphics;



	/**
	 * <code>BoundingSphere</code> defines a sphere that defines a container for a
	 * group of vertices of a particular piece of geometry. This sphere defines a
	 * radius and a center. <br>
	 * <br>
	 * A typical usage is to allow the class define the center and radius by calling
	 * either <code>containAABB</code> or <code>averagePoints</code>. A call to
	 * <code>computeFramePoint</code> in turn calls <code>containAABB</code>.
	 *
	 * @author Mark Powell, Christopher Ruff
	 */
	public class BoundingSphere implements IBoundingShapeMergable{
//	    private static final Logger logger = Logger.getLogger(BoundingSphere.class.getName());
	    
	    public float radius;
	    
	    private static final long serialVersionUID = 2L;

		static final private float radiusEpsilon = 1f + 0.00001f;

		static final private FloatBuffer _mergeBuf = ToolsBuffers.createVector3Buffer(8);

	    static private final Vector3D[] verts = new Vector3D[3];
	    
	    protected Vector3D center = new Vector3D();

		private MTComponent peerComponent;
	    
	    /** The Constant _compVect1. */
		protected static final transient Vector3D _compVect1 = new Vector3D();

//	    private Vector3D centerPointObjSpace;
		
		private Vector3D[] worldVecs;
		private boolean worldVecsDirty;
		private Vector3D centerPointWorld;
		private boolean centerWorldDirty;
		
		private float radiusWorld;
		private boolean radiusWorldDirty;

//	    /**
//	     * Default contstructor instantiates a new <code>BoundingSphere</code>
//	     * object.
//	     */
//	    public BoundingSphere() {
//	    }
//
//	    /**
//	     * Constructor instantiates a new <code>BoundingSphere</code> object.
//	     *
//	     * @param r
//	     *            the radius of the sphere.
//	     * @param c
//	     *            the center of the sphere.
//	     */
//	    public BoundingSphere(float r, Vector3D c) {
//	        this.center.setValues(c);
//	        this.radius = r;
//	    }
	    
	    /**
	     * The Constructor.
	     * 
	     * @param peerComponent the peer component
	     */
	    public BoundingSphere(AbstractShape peerComponent){
	    	this.peerComponent = peerComponent;
//	    	this.computeFromVertices(peerComponent.getGeometryInfo().getVertices());
//	    	this.computeFromPoints(peerComponent.getGeometryInfo().getVertBuff());
//	    	this.computeFromPoints(Vector3D.getDeepVertexArrayCopy(peerComponent.getGeometryInfo().getVertices()));
	    	
	    	if (peerComponent instanceof MTTriangleMesh){
	    		MTTriangleMesh mesh = (MTTriangleMesh)peerComponent;
	    		Triangle[] tris = mesh.getTriangles();
	    		this.computeFromTris(tris, 0, tris.length);
	    	}else{
	    		this.computeFromPoints(Vector3D.getDeepVertexArrayCopy(peerComponent.getGeometryInfo().getVertices())); //FIXME can we avoid the copy??
//	    		this.computeFromPoints(peerComponent.getGeometryInfo().getVertBuff()); //FIXME can we avoid the copy??
	    	}
	    	
	    	this.worldVecsDirty 	= true;
			this.centerWorldDirty 	= true;
//			this.worldVecs 			= this.getVectorsGlobal();
//			this.centerPointWorld 	= this.getCenterPointGlobal();
			this.radiusWorldDirty = true;
//			this.radiusWorld = this.getRadiusWorld();
	    }
	    
	    /**
    	 * The Constructor.
    	 * 
    	 * @param peerComponent the peer component
    	 * @param vectors the vectors
    	 */
	    public BoundingSphere(MTComponent peerComponent, Vector3D[] vectors){
	    	this.peerComponent = peerComponent;
	    	this.computeFromPoints(vectors);
	    	
	    	this.worldVecsDirty 	= true;
			this.centerWorldDirty 	= true;
//			this.worldVecs 			= this.getVectorsGlobal(); //Do it only if requested to save memory (no cached values)
//			this.centerPointWorld 	= this.getCenterPointGlobal();
			this.radiusWorldDirty = true;
//			this.radiusWorld = this.getRadiusWorld();
	    }
	    
	    /**
    	 * The Constructor.
    	 * 
    	 * @param sphere the boundingSphere 
    	 * @param vectors the vectors
    	 */
	    public BoundingSphere(BoundingSphere sphere)
	    {
	    	this.peerComponent = sphere.peerComponent;
	    	this.worldVecsDirty 	= true;
			this.centerWorldDirty 	= true;
//			this.worldVecs 			= this.getVectorsGlobal();
//			this.centerPointWorld 	= this.getCenterPointGlobal();
			this.radiusWorldDirty = true;
	    	
	    }
	    
	    /**
    	 * The Constructor.
    	 *
    	 * @param peerComponent the peer component
    	 * @param center the center
    	 * @param radius the radius
    	 */
	    public BoundingSphere(MTComponent peerComponent, Vector3D center, float radius){
	    	this.peerComponent = peerComponent;
	    	this.radius = radius;
	    	this.center = new Vector3D(center);
	    	
	    	this.worldVecsDirty 	= true;
			this.centerWorldDirty 	= true;
//			this.worldVecs 			= this.getVectorsGlobal();
//			this.centerPointWorld 	= this.getCenterPointGlobal();
			this.radiusWorldDirty = true;
//			this.radiusWorld = this.getRadiusWorld();
	    }

	    
	    /* (non-Javadoc)
    	 * @see org.mt4j.components.bounds.IBoundingShape#setGlobalBoundsChanged()
    	 */
    	public void setGlobalBoundsChanged(){
	    	this.worldVecsDirty = true;
	    	this.centerWorldDirty = true;
	    	this.radiusWorldDirty = true;
	    }
	    
	    
		/**
		 * Draw bounds.
		 *
		 * @param g the g
		 */
		public void drawBounds(PGraphics g){
			g.pushMatrix();
			g.pushStyle();
			g.fill(150,180);
			
			Vector3D l = this.getCenterPointLocal();
			g.translate(l.x, l.y, l.z);
			g.sphere(this.getRadius());
			
			g.popStyle();
			g.popMatrix();
		}
	    

	    /**
	     * <code>getRadius</code> returns the radius of the bounding sphere. (in object space)
	     *
	     * @return the radius of the bounding sphere.
	     */
	    public float getRadius() {
	        return radius;
	    }

	    /**
	     * <code>setRadius</code> sets the radius of this bounding sphere.
	     *
	     * @param radius
	     *            the new radius of the bounding sphere.
	     */
	    public void setRadius(float radius) {
	        this.radius = radius;
	    }

	    
	    /**
	     * <code>computeFromPoints</code> creates a new Bounding Sphere from a
	     * given set of points. It uses the <code>calcWelzl</code> method as
	     * default.
	     *
	     * @param points
	     *            the points to contain.
	     */
	    public void computeFromPoints(Vector3D[] points) {
	        calcWelzl(points);
	    }
	    
	    
	    /**
	     * <code>computeFromPoints</code> creates a new Bounding Sphere from a
	     * given set of points. It uses the <code>calcWelzl</code> method as
	     * default.
	     *
	     * @param points
	     *            the points to contain.
	     */
	    public void computeFromPoints(FloatBuffer points) {
	        calcWelzl(points);
	    }

	    /**
	     * <code>computeFromTris</code> creates a new Bounding Box from a given
	     * set of triangles. It is used in OBBTree calculations.
	     * 
	     * @param tris
	     * @param start
	     * @param end
	     */
	    public void computeFromTris(Triangle[] tris, int start, int end) {
	        if (end - start <= 0) {
	            return;
	        }

	        Vector3D[] vertList = new Vector3D[(end - start) * 3];
	        
	        int count = 0;
	        for (int i = start; i < end; i++) {
//	        	vertList[count++] = tris[i].get(0);
//	        	vertList[count++] = tris[i].get(1);
//	        	vertList[count++] = tris[i].get(2);
	        	vertList[count++] = tris[i].v0;
	        	vertList[count++] = tris[i].v1;
	        	vertList[count++] = tris[i].v2;
	        }
	        averagePoints(vertList);
	    }
	    
//	    /**
//	     * <code>computeFromTris</code> creates a new Bounding Box from a given
//	     * set of triangles. It is used in OBBTree calculations.
//	     * 
//		 * @param indices
//		 * @param mesh
//	     * @param start
//	     * @param end
//	     */
//	    public void computeFromTris(int[] indices, MTTriangleMesh mesh, int start, int end) {
//	    	if (end - start <= 0) {
//	            return;
//	        }
//	    	
//	    	Vector3D[] vertList = new Vector3D[(end - start) * 3];
//	        
//	    	Triangle[] tris = mesh.getTriangles();
//	    	
//	        int count = 0;
//	        for (int i = start; i < end; i++) {
//	        	mesh.getTriangle(indices[i], verts);
//	        	vertList[count++] = new Vector3D(verts[0]);
//	        	vertList[count++] = new Vector3D(verts[1]);
//	        	vertList[count++] = new Vector3D(verts[2]);
//	        }
//	        
//	        averagePoints(vertList);
//	    }

	    /**
	     * Calculates a minimum bounding sphere for the set of points. The algorithm
	     * was originally found at
	     * http://www.flipcode.com/cgi-bin/msg.cgi?showThread=COTD-SmallestEnclosingSpheres&forum=cotd&id=-1
	     * in C++ and translated to java by Cep21
	     *
	     * @param points
	     *            The points to calculate the minimum bounds from.
	     */
	    public void calcWelzl(FloatBuffer points) {
	    	 if (center == null){
		            center = new Vector3D();
		        }
	    	 
		        FloatBuffer buf = ToolsBuffers.createFloatBuffer(points.limit());
		        points.rewind();
		        
		        float[] pointsArr = ToolsBuffers.getFloatArray(points);
		        buf.put(pointsArr);
		        buf.flip();
		        this.recurseMini(buf, buf.limit() / 3, 0, 0);
	    }
	    
	    /**
	     * Calculates a minimum bounding sphere for the set of points. The algorithm
	     * was originally found at
	     * http://www.flipcode.com/cgi-bin/msg.cgi?showThread=COTD-SmallestEnclosingSpheres&forum=cotd&id=-1
	     * in C++ and translated to java by Cep21
	     *
	     * @param points
	     *            The points to calculate the minimum bounds from.
	     */
	    public void calcWelzl(Vector3D[] points) {
	    	if (center == null){
	    		center = new Vector3D();
	    	}
	    	this.recurseMini(points, points.length, 0, 0);
	    }

	    private static Vector3D tempA = new Vector3D(), tempB = new Vector3D(), tempC = new Vector3D(), tempD = new Vector3D();
	    /**
	     * Used from calcWelzl. This function recurses to calculate a minimum
	     * bounding sphere a few points at a time.
	     *
	     * @param points
	     *            The array of points to look through.
	     * @param p
	     *            The size of the list to be used.
	     * @param b
	     *            The number of points currently considering to include with the
	     *            sphere.
	     * @param ap
	     *            A variable simulating pointer arithmatic from C++, and offset
	     *            in <code>points</code>.
	     */
	    private void recurseMini(FloatBuffer points, int p, int b, int ap) {
	        switch (b) {
	        case 0:
	            this.radius = 0;
	            this.center.setXYZ(0, 0, 0);
	            break;
	        case 1:
	            this.radius = 1f - radiusEpsilon;
	            ToolsBuffers.populateFromBuffer(center, points, ap-1);
	            break;
	        case 2:
	            ToolsBuffers.populateFromBuffer(tempA, points, ap-1);
	            ToolsBuffers.populateFromBuffer(tempB, points, ap-2);
	            setSphere(tempA, tempB);
	            break;
	        case 3:
	            ToolsBuffers.populateFromBuffer(tempA, points, ap-1);
	            ToolsBuffers.populateFromBuffer(tempB, points, ap-2);
	            ToolsBuffers.populateFromBuffer(tempC, points, ap-3);
	            setSphere(tempA, tempB, tempC);
	            break;
	        case 4:
	            ToolsBuffers.populateFromBuffer(tempA, points, ap-1);
	            ToolsBuffers.populateFromBuffer(tempB, points, ap-2);
	            ToolsBuffers.populateFromBuffer(tempC, points, ap-3);
	            ToolsBuffers.populateFromBuffer(tempD, points, ap-4);
	            setSphere(tempA, tempB, tempC, tempD);
	            return;
	        }
	        for (int i = 0; i < p; i++) {
	            ToolsBuffers.populateFromBuffer(tempA, points, i+ap);
	            if (Vector3D.distanceSquared(tempA, center) - (radius * radius) > radiusEpsilon - 1f) {
	                for (int j = i; j > 0; j--) {
	                    ToolsBuffers.populateFromBuffer(tempB, points, j + ap);
	                    ToolsBuffers.populateFromBuffer(tempC, points, j - 1 + ap);
	                    ToolsBuffers.setInBuffer(tempC, points, j + ap);
	                    ToolsBuffers.setInBuffer(tempB, points, j - 1 + ap);
	                }
	                recurseMini(points, i, b + 1, ap + 1);
	            }
	        }
	    }
	    
	    
	    /**
	     * Used from calcWelzl. This function recurses to calculate a minimum
	     * bounding sphere a few points at a time.
	     *
	     * @param points
	     *            The array of points to look through.
	     * @param p
	     *            The size of the list to be used.
	     * @param b
	     *            The number of points currently considering to include with the
	     *            sphere.
	     * @param ap
	     *            A variable simulating pointer arithmatic from C++, and offset
	     *            in <code>points</code>.
	     */
	    private void recurseMini(Vector3D[] points, int p, int b, int ap) {
	        switch (b) {
	        case 0:
	            this.radius = 0;
	            this.center.setXYZ(0, 0, 0);
	            break;
	        case 1:
	            this.radius = 1f - radiusEpsilon;
//	            ToolsBuffers.populateFromBuffer(center, points, ap-1);
	            this.center.setXYZ(points[ap-1].x, points[ap-1].y, points[ap-1].z);
	            break;
	        case 2:
	        	tempA.setXYZ(points[ap-1].x, points[ap-1].y, points[ap-1].z);
	        	tempB.setXYZ(points[ap-2].x, points[ap-2].y, points[ap-2].z);
//	            ToolsBuffers.populateFromBuffer(tempA, points, ap-1);
//	            ToolsBuffers.populateFromBuffer(tempB, points, ap-2);
	            setSphere(tempA, tempB);
	            break;
	        case 3:
//	            ToolsBuffers.populateFromBuffer(tempA, points, ap-1);
//	            ToolsBuffers.populateFromBuffer(tempB, points, ap-2);
//	            ToolsBuffers.populateFromBuffer(tempC, points, ap-3);
	        	tempA.setXYZ(points[ap-1].x, points[ap-1].y, points[ap-1].z);
	        	tempB.setXYZ(points[ap-2].x, points[ap-2].y, points[ap-2].z);
	        	tempC.setXYZ(points[ap-3].x, points[ap-3].y, points[ap-3].z);
	            setSphere(tempA, tempB, tempC);
	            break;
	        case 4:
//	            ToolsBuffers.populateFromBuffer(tempA, points, ap-1);
//	            ToolsBuffers.populateFromBuffer(tempB, points, ap-2);
//	            ToolsBuffers.populateFromBuffer(tempC, points, ap-3);
//	            ToolsBuffers.populateFromBuffer(tempD, points, ap-4);
	            tempA.setXYZ(points[ap-1].x, points[ap-1].y, points[ap-1].z);
	        	tempB.setXYZ(points[ap-2].x, points[ap-2].y, points[ap-2].z);
	        	tempC.setXYZ(points[ap-3].x, points[ap-3].y, points[ap-3].z);
	        	tempD.setXYZ(points[ap-4].x, points[ap-4].y, points[ap-4].z);
	            setSphere(tempA, tempB, tempC, tempD);
	            return;
	        }
	        for (int i = 0; i < p; i++) {
//	            ToolsBuffers.populateFromBuffer(tempA, points, i+ap);
	        	tempA.setXYZ(points[i+ap].x, points[i+ap].y, points[i+ap].z);
	            if (Vector3D.distanceSquared(tempA, center) - (radius * radius) > radiusEpsilon - 1f) {
	                for (int j = i; j > 0; j--) {
//	                    ToolsBuffers.populateFromBuffer(tempB, points, j + ap);
//	                    ToolsBuffers.populateFromBuffer(tempC, points, j - 1 + ap);
	                	tempB.setXYZ(points[j + ap].x, points[j + ap].y, points[j + ap].z);
	                	tempC.setXYZ(points[j - 1 + ap].x, points[j - 1 + ap].y, points[j - 1 + ap].z);
	                	
//	                	ToolsBuffers.setInBuffer(tempC, points, j + ap);
//	                	ToolsBuffers.setInBuffer(tempB, points, j - 1 + ap);
	                	points[j+ap].setXYZ(tempC.x, tempC.y, tempC.z);
	                    points[j - 1 + ap].setXYZ(tempB.x, tempB.y, tempB.z);
	                }
	                recurseMini(points, i, b + 1, ap + 1);
	            }
	        }
	    }


	    /**
	     * Calculates the minimum bounding sphere of 4 points. Used in welzl's
	     * algorithm.
	     *
	     * @param O
	     *            The 1st point inside the sphere.
	     * @param A
	     *            The 2nd point inside the sphere.
	     * @param B
	     *            The 3rd point inside the sphere.
	     * @param C
	     *            The 4th point inside the sphere.
	     * @see #calcWelzl(java.nio.FloatBuffer)
	     */
	    private void setSphere(Vector3D O, Vector3D A, Vector3D B, Vector3D C) {
	        Vector3D a = A.getSubtracted(O);
	        Vector3D b = B.getSubtracted(O);
	        Vector3D c = C.getSubtracted(O);

	        float Denominator = 2.0f * (a.x * (b.y * c.z - c.y * b.z) - b.x
	                * (a.y * c.z - c.y * a.z) + c.x * (a.y * b.z - b.y * a.z));
	        if (Denominator == 0) {
	            center.setXYZ(0, 0, 0);
	            radius = 0;
	        } else {
//	            Vector3D o = a.cross(b).multLocal(c.lengthSquared()).addLocal(
//	                    c.cross(a).multLocal(b.lengthSquared())).addLocal(
//	                    b.cross(c).multLocal(a.lengthSquared())).divideLocal(
//	                    Denominator);
	        	Vector3D o = a.getCross(b).scaleLocal(c.lengthSquared()).addLocal(
	                    c.getCross(a).scaleLocal(b.lengthSquared())).addLocal(
	                    b.getCross(c).scaleLocal(a.lengthSquared())).scaleLocal(
	                    1f/Denominator);

	            radius = o.length() * radiusEpsilon;
	            
//	            O.add(o, center);
	            
	            center.setValues(O.getAdded(o));
	        }
	    }

	    /**
	     * Calculates the minimum bounding sphere of 3 points. Used in welzl's
	     * algorithm.
	     *
	     * @param O
	     *            The 1st point inside the sphere.
	     * @param A
	     *            The 2nd point inside the sphere.
	     * @param B
	     *            The 3rd point inside the sphere.
	     * @see #calcWelzl(java.nio.FloatBuffer)
	     */
	    private void setSphere(Vector3D O, Vector3D A, Vector3D B) {
	        Vector3D a = A.getSubtracted(O);
	        Vector3D b = B.getSubtracted(O);
	        Vector3D acrossB = a.getCross(b);

	        float Denominator = 2.0f * acrossB.dot(acrossB);

	        if (Denominator == 0) {
	            center.setXYZ(0, 0, 0);
	            radius = 0;
	        } else {
//	            Vector3D o = acrossB.cross(a).multLocal(b.lengthSquared())
//	                    .addLocal(b.cross(acrossB).multLocal(a.lengthSquared()))
//	                    .divideLocal(Denominator);
	        	Vector3D o = acrossB.getCross(a).scaleLocal(b.lengthSquared())
                .addLocal(b.getCross(acrossB).scaleLocal(a.lengthSquared()))
                .divideLocal(Denominator);
//                .scaleLocal(1f/Denominator);
	        	
	            radius = o.length() * radiusEpsilon;
	            
//	            O.add(o, center);
	            center.setValues(O.getAdded(o));
	        }
	    }

	    /**
	     * Calculates the minimum bounding sphere of 2 points. Used in welzl's
	     * algorithm.
	     *
	     * @param O
	     *            The 1st point inside the sphere.
	     * @param A
	     *            The 2nd point inside the sphere.
	     * @see #calcWelzl(java.nio.FloatBuffer)
	     */
	    private void setSphere(Vector3D O, Vector3D A) {
	        radius = ToolsMath.sqrt(((A.x - O.x) * (A.x - O.x) + (A.y - O.y)
	                * (A.y - O.y) + (A.z - O.z) * (A.z - O.z)) / 4f) + radiusEpsilon - 1f;
	        
//	        center.interpolate(O, A, .5f);
	        center.setValues(this.interpolate(O, A, .5f));
	    }
	    
	    
	    /**
	     * Sets this vector to the interpolation by changeAmnt from beginVec to finalVec
	     * this=(1-changeAmnt)*beginVec + changeAmnt * finalVec
	     * @param beginVec the beging vector (changeAmnt=0)
	     * @param finalVec The final vector to interpolate towards
	     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
	     *  change from beginVec towards finalVec
	     */
	    public Vector3D interpolate(Vector3D beginVec,Vector3D finalVec, float changeAmnt) {
	    	float x =(1-changeAmnt)*beginVec.x + changeAmnt*finalVec.x;
	    	float y =(1-changeAmnt)*beginVec.y + changeAmnt*finalVec.y;
	    	float z =(1-changeAmnt)*beginVec.z + changeAmnt*finalVec.z;
	    	return new Vector3D(x,y,z);
	    }


	    /**
	     * <code>averagePoints</code> selects the sphere center to be the average
	     * of the points and the sphere radius to be the smallest value to enclose
	     * all points.
	     *
	     * @param points
	     *            the list of points to contain.
	     */
	    public void averagePoints(Vector3D[] points) {
	        //logger.info("Bounding Sphere calculated using average points.");
	        center = points[0].getCopy();

	        for (int i = 1; i < points.length; i++) {
	            center.addLocal(points[i]);
	        }
	        
	        float quantity = 1.0f / points.length;
//	        center.multLocal(quantity);
	        center.scaleLocal(quantity);

	        float maxRadiusSqr = 0;
            for (Vector3D point : points) {
                Vector3D diff = point.getSubtracted(center);
                float radiusSqr = diff.lengthSquared();
                if (radiusSqr > maxRadiusSqr) {
                    maxRadiusSqr = radiusSqr;
                }
            }

	        radius = (float) Math.sqrt(maxRadiusSqr) + radiusEpsilon - 1f;

	    }


	    private float getMaxAxis(Vector3D scale) {
	        float x = ToolsMath.abs(scale.x);
	        float y = ToolsMath.abs(scale.y);
	        float z = ToolsMath.abs(scale.z);
	        
	        if (x >= y) {
	            if (x >= z)
	                return x;
	            return z;
	        }
	        
	        if (y >= z)
	            return y;
	        
	        return z;
	    }

//	    /**
//	     * <code>whichSide</code> takes a plane (typically provided by a view
//	     * frustum) to determine which side this bound is on.
//	     *
//	     * @param plane
//	     *            the plane to check against.
//	     * @return side
//	     */
//	    public Side whichSide(Plane plane) {
//	        float distance = plane.pseudoDistance(center);
//	        if (distance <= -radius) { return Side.NEGATIVE; }
//	        if (distance >=  radius) { return Side.POSITIVE; }
//	        return Side.NONE;
//	    }
	    
	    /**
	     * <code>whichSide</code> takes a plane (typically provided by a view
	     * frustum) to determine which side this bound is on.
	     * 
	     * @param plane the plane to check against.
	     * 
	     * @return int
	     */
	    public int whichSide(Plane plane) {
//	    	float distance = plane.pseudoDistance(center);
//	    	if (distance <= -radius) { return Side.NEGATIVE; }
//	    	if (distance >=  radius) { return Side.POSITIVE; }
//	    	return Side.NONE;
	    	int side = plane.classifyPoint(center);
	    	return side;
	    }

	    
//	    /**
//	     * <code>merge</code> combines this sphere with a second bounding sphere.
//	     * This new sphere contains both bounding spheres and is returned.
//	     *
//	     * @param volume
//	     *            the sphere to combine with this sphere.
//	     * @return a new sphere
//	     */
//	    public BoundingVolume merge(BoundingVolume volume) {
//	        if (volume == null) {
//	            return this;
//	        }
//
//	        switch(volume.getType()) {
//
//	        case Sphere: {
//	        	BoundingSphere sphere = (BoundingSphere) volume;
//	            float temp_radius = sphere.getRadius();
//	            Vector3D temp_center = sphere.getCenter();
//	            BoundingSphere rVal = new BoundingSphere();
//	            return merge(temp_radius, temp_center, rVal);
//	        }
//	        
//	        case Capsule: {
//	        	BoundingCapsule capsule = (BoundingCapsule) volume;
//	            float temp_radius = capsule.getRadius() 
//	            	+ capsule.getLineSegment().getExtent();
//	            Vector3D temp_center = capsule.getCenter();
//	            BoundingSphere rVal = new BoundingSphere();
//	            return merge(temp_radius, temp_center, rVal);
//	        }
//
//	        case AABB: {
//	        	BoundingBox box = (BoundingBox) volume;
//	            Vector3D radVect = new Vector3D(box.xExtent, box.yExtent,
//	                    box.zExtent);
//	            Vector3D temp_center = box.center;
//	            BoundingSphere rVal = new BoundingSphere();
//	            return merge(radVect.length(), temp_center, rVal);
//	        }
//
//	        case OBB: {
//	        	OrientedBoundingBox box = (OrientedBoundingBox) volume;
//	            BoundingSphere rVal = (BoundingSphere) this.clone(null);
//	            return rVal.mergeOBB(box);
//	        }
//
//	        default:
//	        	return null;
//
//	        }
//	    }

	    private Vector3D tmpRadVect = new Vector3D();

//	    /**
//	     * <code>mergeLocal</code> combines this sphere with a second bounding
//	     * sphere locally. Altering this sphere to contain both the original and the
//	     * additional sphere volumes;
//	     *
//	     * @param volume
//	     *            the sphere to combine with this sphere.
//	     * @return this
//	     */
//	    public BoundingVolume mergeLocal(BoundingVolume volume) {
//	        if (volume == null) {
//	            return this;
//	        }
//
//	        switch (volume.getType()) {
//
//	        case Sphere: {
//	        	BoundingSphere sphere = (BoundingSphere) volume;
//	            float temp_radius = sphere.getRadius();
//	            Vector3D temp_center = sphere.getCenter();
//	            return merge(temp_radius, temp_center, this);
//	        }
//
//	        case AABB: {
//	        	BoundingBox box = (BoundingBox) volume;
//	            Vector3D radVect = tmpRadVect;
//	            radVect.set(box.xExtent, box.yExtent, box.zExtent);
//	            Vector3D temp_center = box.center;
//	            return merge(radVect.length(), temp_center, this);
//	        }
//
//	        case OBB: {
//	        	return mergeOBB((OrientedBoundingBox) volume);
//	        }
//	        
//	        case Capsule: {
//	        	BoundingCapsule capsule = (BoundingCapsule) volume;
//	        	return merge(capsule.getRadius() + capsule.getLineSegment().getExtent(), 
//	        			capsule.getCenter(), this);
//	        }
//
//	        default:
//	        	return null;
//	        }
//	    }

//	    /**
//	     * Merges this sphere with the given OBB.
//	     *
//	     * @param volume
//	     *            The OBB to merge.
//	     * @return This sphere, after merging.
//	     */
//	    private BoundingSphere mergeOBB(OrientedBoundingBox volume) {
//	        // compute edge points from the obb
//	        if (!volume.correctCorners)
//	            volume.computeCorners();
//	        _mergeBuf.rewind();
//	        for (int i = 0; i < 8; i++) {
//	            _mergeBuf.put(volume.vectorStore[i].x);
//	            _mergeBuf.put(volume.vectorStore[i].y);
//	            _mergeBuf.put(volume.vectorStore[i].z);
//	        }
//
//	        // remember old radius and center
//	        float oldRadius = radius;
//	        Vector3D oldCenter = _compVect2.set( center );
//
//	        // compute new radius and center from obb points
//	        computeFromPoints(_mergeBuf);
//	        Vector3D newCenter = _compVect3.set( center );
//	        float newRadius = radius;
//
//	        // restore old center and radius
//	        center.set( oldCenter );
//	        radius = oldRadius;
//
//	        //merge obb points result
//	        merge( newRadius, newCenter, this );
//
//	        return this;
//	    }
//
//	    private BoundingVolume merge(float temp_radius, Vector3D temp_center,
//	            BoundingSphere rVal) {
//	        Vector3D diff = temp_center.subtract(center, _compVect1);
//	        float lengthSquared = diff.lengthSquared();
//	        float radiusDiff = temp_radius - radius;
//
//	        float fRDiffSqr = radiusDiff * radiusDiff;
//
//	        if (fRDiffSqr >= lengthSquared) {
//	            if (radiusDiff <= 0.0f) {
//	                return this;
//	            } 
//	                
//	            Vector3D rCenter = rVal.getCenter();
//	            if ( rCenter == null ) {
//	                rVal.setCenter( rCenter = new Vector3D() );
//	            }
//	            rCenter.set(temp_center);
//	            rVal.setRadius(temp_radius);
//	            return rVal;
//	        }
//
//	        float length = (float) Math.sqrt(lengthSquared);
//
//	        Vector3D rCenter = rVal.getCenter();
//	        if ( rCenter == null ) {
//	            rVal.setCenter( rCenter = new Vector3D() );
//	        }
//	        if (length > radiusEpsilon) {
//	            float coeff = (length + radiusDiff) / (2.0f * length);
//	            rCenter.set(center.addLocal(diff.multLocal(coeff)));
//	        } else {
//	            rCenter.set(center);
//	        }
//
//	        rVal.setRadius(0.5f * (length + radius + temp_radius));
//	        return rVal;
//	    }


	    public Vector3D getCenter() {
	        return center;
	    }

	    public void setCenter(Vector3D center) {
	    	this.center = center;
	    }
	    
//	    /*
//	     * (non-Javadoc)
//	     *
//	     * @see com.jme.bounding.BoundingVolume#intersectsSphere(com.jme.bounding.BoundingSphere)
//	     */
//	    public boolean intersectsSphere(BoundingSphere bs) {
//	        if (!Vector3D.isValidVector(center) || !Vector3D.isValidVector(bs.center)) return false;
//
//	        Vector3D diff = getCenter().subtract(bs.getCenter(), _compVect1);
//	        float rsum = getRadius() + bs.getRadius();
//	        return (diff.dot(diff) <= rsum * rsum);
//	    }
//
//	    /*
//	     * (non-Javadoc)
//	     *
//	     * @see com.jme.bounding.BoundingVolume#intersectsBoundingBox(com.jme.bounding.BoundingBox)
//	     */
//	    public boolean intersectsBoundingBox(BoundingBox bb) {
//	        if (!Vector3D.isValidVector(center) || !Vector3D.isValidVector(bb.center)) return false;
//
//	        if (FastMath.abs(bb.center.x - getCenter().x) < getRadius()
//	                + bb.xExtent
//	                && FastMath.abs(bb.center.y - getCenter().y) < getRadius()
//	                        + bb.yExtent
//	                && FastMath.abs(bb.center.z - getCenter().z) < getRadius()
//	                        + bb.zExtent)
//	            return true;
//
//	        return false;
//	    }
//
//	    /*
//	     * (non-Javadoc)
//	     *
//	     * @see com.jme.bounding.BoundingVolume#intersectsOrientedBoundingBox(com.jme.bounding.OrientedBoundingBox)
//	     */
//	    public boolean intersectsOrientedBoundingBox(OrientedBoundingBox obb) {
//	        return obb.intersectsSphere(this);
//	    }
//	    
//	    public boolean intersectsCapsule(BoundingCapsule bc) {
//	    	return bc.intersectsSphere(this);
//	    }

	    /**
	     * Check a vector... if it is null or its floats are NaN or infinite,
	     * return false.  Else return true.
	     * @param vector the vector to check
	     * @return true or false as stated above.
	     */
	    public static boolean isValidVector(Vector3D vector) {
	      if (vector == null) return false;
	      if (Float.isNaN(vector.x) ||
	          Float.isNaN(vector.y) ||
	          Float.isNaN(vector.z)) return false;
	      if (Float.isInfinite(vector.x) ||
	          Float.isInfinite(vector.y) ||
	          Float.isInfinite(vector.z)) return false;
	      return true;
	    }

	    
	    /*
	     * (non-Javadoc)
	     *
	     * @see com.jme.bounding.BoundingVolume#intersects(com.jme.math.Ray)
	     */
	    public boolean intersects(Ray ray) {
	        if (!isValidVector(center)) 
	        	return false;

//	        Vector3D diff = _compVect1.set(ray.getOrigin()).subtractLocal(getCenter());
	        
	        Vector3D diff = _compVect1.setValues(ray.getRayStartPoint()).subtractLocal(getCenter());
	        
	        float radiusSquared = getRadius() * getRadius();
	        float a = diff.dot(diff) - radiusSquared;
	        if (a <= 0.0) {
	            // in sphere
	            return true;
	        }

	        // outside sphere
	        float b = ray.getDirection().dot(diff);
	        if (b >= 0.0) {
	            return false;
	        }
	        return b*b >= a;
	    }

	    
	   
	    public Vector3D getIntersectionLocal(Ray ray){
	    	Vector3D rayDir = ray.getRayDirectionNormalized();
	    	
	        Vector3D diff = _compVect1.setValues(ray.getRayStartPoint()).subtractLocal(this.getCenter());
	        float a = diff.dot(diff) - (getRadius()*getRadius());
	        float a1, discr, root;
	        
	        if (a <= 0.0) {
	            // inside sphere
	        	a1 = rayDir.dot(diff);
	        	
	            discr = (a1 * a1) - a;
	            root = ToolsMath.sqrt(discr);
	            float[] distances = new float[] { root - a1 };
	            
	            Vector3D hitVect = new Vector3D(rayDir).scaleLocal(distances[0]).addLocal(ray.getRayStartPoint()) ;
	            
//	            Vector3D[] points = new Vector3D[] { 
//	            	hitVect
//	            };
	            
//	            IntersectionRecord record = new IntersectionRecord(distances, points);
//	            return record;
	            return hitVect;
	        }
	        
	        
	        a1 = rayDir.dot(diff);
	        if (a1 >= 0.0) {
//	            return new IntersectionRecord();
	        	return null;
	        }
	        
	        
	        discr = a1*a1 - a;
	        if (discr < 0.0){
//	            return new IntersectionRecord();
	        	return null;
	        }else if (discr >= ToolsMath.ZERO_TOLERANCE) {
	            root = ToolsMath.sqrt(discr);
	            float[] distances = new float[] { -a1 - root, -a1 + root };
	            
	            Vector3D[] points = new Vector3D[] { 
	                    new Vector3D(rayDir).scaleLocal(distances[0]).addLocal(ray.getRayStartPoint()),
	                    new Vector3D(rayDir).scaleLocal(distances[1]).addLocal(ray.getRayStartPoint())
	                    };
//	            IntersectionRecord record = new IntersectionRecord(distances, points);
//	            return record;
	            //FIXME WELCHEN PUNKT NEHMEN?
	            return points[0];
	        } else {
	            float[] distances = new float[] { -a1 };
	            Vector3D[] points = new Vector3D[] { 
	                    new Vector3D(rayDir).scaleLocal(distances[0]).addLocal(ray.getRayStartPoint())};
//	            IntersectionRecord record = new IntersectionRecord(distances, points);
//	            return record;
	            return points[0];
	        }
	    }
	    
	    
	    
//	    /*
//	     * (non-Javadoc)
//	     *
//	     * @see com.jme.bounding.BoundingVolume#intersectsWhere(com.jme.math.Ray)
//	     */
//	    public IntersectionRecord intersectsWhere(Ray ray) {
//	    	Vector3D rayDir = ray.getDirection();
//	    	
//	        Vector3D diff = _compVect1.setValues(ray.getRayStartPoint()).subtractLocal(this.getCenter());
//	        float a = diff.dot(diff) - (getRadius()*getRadius());
//	        float a1, discr, root;
//	        
//	        if (a <= 0.0) {
//	            // inside sphere
//	        	a1 = rayDir.dot(diff);
//	        	
//	            discr = (a1 * a1) - a;
//	            root = FastMath.sqrt(discr);
//	            float[] distances = new float[] { root - a1 };
//	            
//	            Vector3D[] points = new Vector3D[] { 
//	            		new Vector3D(rayDir).scaleLocal(distances[0]).addLocal(ray.getRayStartPoint()) 
//	            };
//	            
//	            IntersectionRecord record = new IntersectionRecord(distances, points);
//	            return record;
//	        }
//	        
//	        
//	        a1 = rayDir.dot(diff);
//	        if (a1 >= 0.0) {
//	            return new IntersectionRecord();
//	        }
//	        
//	        
//	        discr = a1*a1 - a;
//	        if (discr < 0.0){
//	            return new IntersectionRecord();
//	        }else if (discr >= FastMath.ZERO_TOLERANCE) {
//	            root = FastMath.sqrt(discr);
//	            float[] distances = new float[] { -a1 - root, -a1 + root };
//	            Vector3D[] points = new Vector3D[] { 
//	                    new Vector3D(rayDir).scaleLocal(distances[0]).addLocal(ray.getRayStartPoint()),
//	                    new Vector3D(rayDir).scaleLocal(distances[1]).addLocal(ray.getRayStartPoint())
//	                    };
//	            IntersectionRecord record = new IntersectionRecord(distances, points);
//	            return record;
//	        } else {
//	            float[] distances = new float[] { -a1 };
//	            Vector3D[] points = new Vector3D[] { 
//	                    new Vector3D(rayDir).scaleLocal(distances[0]).addLocal(ray.getRayStartPoint())};
//	            IntersectionRecord record = new IntersectionRecord(distances, points);
//	            return record;
//	        }
//	    }

	    //@Override
	    public boolean containsPointLocal(Vector3D point) {
	    	return 	Vector3D.distanceSquared(getCenter(), point) 
	    			< 
	    			(getRadius() * getRadius());
//	        return getCenter().distanceSquared(point) < (getRadius() * getRadius());
	    }

	    public float distanceToEdge(Vector3D point) {
	        return Vector3D.distance(center, point) - radius;
	    }
	    

	    public float getVolume() {
	        return 4 * ToolsMath.ONE_THIRD * ToolsMath.PI * radius * radius * radius;
	    }




		//@Override
		public Vector3D getCenterPointLocal() {
			return center.getCopy();
		}



		//@Override
		public Vector3D getCenterPointGlobal() {
			if (centerWorldDirty){
				Vector3D tmp = this.getCenterPointLocal();
				tmp.transform(this.peerComponent.getGlobalMatrix());
				this.centerPointWorld = tmp;
				this.centerWorldDirty = false;
				return this.centerPointWorld;
			}else{
				return this.centerPointWorld;
			}
//			Vector3D tmp = center.getCopy();
//			tmp.transform(this.peerComponent.getAbsoluteLocalToWorldMatrix());
//			return tmp;
		}




		//@Override
		public float getHeightXY(TransformSpace transformSpace) {
			switch (transformSpace) {
			case LOCAL:
				return this.getHeightXYVectLocal().length();
			case RELATIVE_TO_PARENT:{
				Vector3D p = this.getHeightXYVectLocal();
				Matrix m = new Matrix(this.peerComponent.getLocalMatrix());
				m.removeTranslationFromMatrix();
				p.transform(m);
				return p.length();
			}
			case GLOBAL:{
				Vector3D p = this.getHeightXYVectLocal();
				Matrix m = new Matrix(this.peerComponent.getGlobalMatrix());
				m.removeTranslationFromMatrix();
				p.transform(m);
				return p.length();
			}
			default:
				return -1;
			}
		}




		//@Override
		public Vector3D getHeightXYVectLocal() {
			return new Vector3D(0, this.getRadius()*2,0);
		}




		//@Override
		public Vector3D[] getVectorsLocal() {
			return new Vector3D[]{this.center.getCopy()};
		}




		//@Override
		public Vector3D[] getVectorsGlobal() {
			if (this.worldVecsDirty){
				Vector3D[] vecs = Vector3D.getDeepVertexArrayCopy(this.getVectorsLocal());
				Vector3D.transFormArrayLocal(this.peerComponent.getGlobalMatrix(), vecs);
				this.worldVecs = vecs;
				this.worldVecsDirty = false;
				return this.worldVecs;
			}else{
				return this.worldVecs;
			}
			
//			Vector3D tmp = center.getCopy();
//			tmp.transform(this.peerComponent.getAbsoluteLocalToWorldMatrix());
//			return new Vector3D[]{tmp};
		}




		//@Override
		public float getWidthXY(TransformSpace transformSpace) {
			switch (transformSpace) {
			case LOCAL:
				return this.getWidthXYVectLocal().length();
			case RELATIVE_TO_PARENT:{
				Vector3D p = this.getWidthXYVectLocal();
				Matrix m = new Matrix(this.peerComponent.getLocalMatrix());
				m.removeTranslationFromMatrix();
				p.transform(m);
				return p.length();
			}
			case GLOBAL:{
				Vector3D p = this.getWidthXYVectLocal();
				Matrix m = new Matrix(this.peerComponent.getGlobalMatrix());
				m.removeTranslationFromMatrix();
				p.transform(m);
				return p.length();
			}
			default:
				return -1;
			}
		}



		//@Override
		public Vector3D getWidthXYVectLocal() {
			return new Vector3D(this.getRadius()*2, 0,0);
		}

		//@Override
		public boolean isContainedInFrustum(IFrustum frustum) {
			int test = frustum.isSphereInFrustum(this.getCenterPointGlobal(), this.getRadiusWorld()); 
			if (test == IFrustum.OUTSIDE
			){
				return false;
			}else{
				return true;
			}
		}
		
		
		//TODO cache centerPointWorld und radiusWorld!
		
		//To avoid obj creation for each frustum test
		private Vector3D tmpVec = new Vector3D(); 
		private Matrix tmpMatrix = new Matrix();
		
		private float getRadiusWorld(){
			if (this.radiusWorldDirty){
				tmpVec.setXYZ(this.getRadius(), 0,0);
				tmpMatrix.set(this.peerComponent.getGlobalMatrix());
				tmpMatrix.removeTranslationFromMatrix();
				tmpVec.transform(tmpMatrix);
				this.radiusWorld = tmpVec.length();
				this.radiusWorldDirty = false;
				return this.radiusWorld;
			}else{
				return this.radiusWorld;
			}
//			tmpVec.setXYZ(this.getRadius(), 0,0);
////			Matrix m = new Matrix(this.peerComponent.getAbsoluteLocalToWorldMatrix());
//			tmpMatrix.set(this.peerComponent.getAbsoluteLocalToWorldMatrix());
//			tmpMatrix.removeTranslationFromMatrix();
//			tmpVec.transform(tmpMatrix);
//			return tmpVec.length();
		}
		
		/**
	     * <code>transform</code> modifies the center of the sphere to reflect the
	     * change made via a rotation, translation and scale.
	     *
	     * @param rotate
	     *            the rotation change.
	     * @param translate
	     *            the translation change.
	     * @param scale
	     *            the size change.
	     * @param store
	     *            sphere to store result in
	     * @return BoundingVolume
	     * @return ref
	     */
	    public IBoundingShapeMergable transform(Matrix transformMatrix) {
	        BoundingSphere sphere = this;
	        
	       // sphere = new BoundingSphere((AbstractShape)this.peerComponent);
	        
	        sphere.center = this.center;
	        sphere.radius = this.radius;
	        
	        Quaternion rotate = new Quaternion();
	        //transformMatrix.addLocal(this.peerComponent.getLocalMatrix());
	        rotate.fromRotationMatrix(transformMatrix);
	      
	        Vector3D translate = new Vector3D(transformMatrix.m03,transformMatrix.m13,transformMatrix.m23);
	        Vector3D scale = new Vector3D(transformMatrix.getScale());
	        
	        Matrix mat = new Matrix();
	        mat.loadIdentity();
	        mat.m00 = scale.x;
	        mat.m11 = scale.y;
	        mat.m12 = scale.z;
	        sphere.center.transform(mat);
	        //center.mult(scale, sphere.center);
	        rotate.mult(sphere.center, sphere.center);
	        sphere.center.addLocal(translate);
	        sphere.radius = Math.abs(getMaxAxis(scale) * radius) + radiusEpsilon - 1f;
	        
	        return sphere;
	    }
		
		//ADDTOMT4J
		/**
		 *  calculates the boundingsphere points on the sphere
		 *  used to get a specific location on the sphere, for example point most right/left
		 *  for example used to position the ZTechnique Control on the right outer point of the bounding sphere   
		 *  **/		
		public Vector3D[] getVectorsOnBoundingSphereGlobal(int resolution)
		{
			Vector3D[] vecs = Vector3D.getDeepVertexArrayCopy(this.getVectorsOnBoundingSphereLocal(resolution));
			Vector3D.transFormArrayLocal(this.peerComponent.getGlobalMatrix(), vecs);
			return vecs;
		}
		
		//ADDTOMT4J
		/**
		 *  calculates the boundingsphere points on the sphere
		 *  used to get a specific location on the sphere, for example point most right/left
		 *  for example used to position the ZTechnique Control on the right outer point of the bounding sphere   
		 *  **/		
		public Vector3D[] getVectorsOnBoundingSphereLocal(int resolution) {
			
			if(resolution==0)
			{
				return null; //has to be at least one 
			}
						
			int perCircleNumber = (int)Math.pow(2,resolution);
	
			Vector3D[] vecs = new Vector3D[2+(int)(Math.pow(2,resolution)*(Math.pow(2,resolution+1)-2))];
						
			//vecs up/down
			vecs[0] = calcSphereCoordinates(0,0);
			vecs[1] = calcSphereCoordinates(0,Math.PI);
			
 			int i=2;
			
			//azimuth rotation
			for(double a=0;i<vecs.length;a=a+((2*Math.PI)/(perCircleNumber*2)))
			{ 				
				//polar rotation
				for(double p=Math.PI/(perCircleNumber);p<Math.PI;p=p+Math.PI/(perCircleNumber))
				{					
					vecs[i] = calcSphereCoordinates(a,p);
					i++;					
				}
			}		
			
			return vecs;		
		}
		
		//ADDTOMT4J
		private Vector3D calcSphereCoordinates(double azimuthwinkel,double polarwinkel)
		{
		
			Vector3D vec = new Vector3D();
			
			vec.x = this.radius * (float)Math.sin(polarwinkel)*(float)Math.sin(azimuthwinkel);
			vec.y = this.radius * (float)Math.cos(polarwinkel);
			vec.z = this.radius * (float)Math.sin(polarwinkel) * (float)Math.cos(azimuthwinkel);
			
			return vec;
			
		}
		
		/**
	     * <code>merge</code> combines this sphere with a second bounding sphere.
	     * This new sphere contains both bounding spheres and is returned.
	     *
	     * @param volume
	     *            the sphere to combine with this sphere.
	     * @return a new sphere
	     */
	    public IBoundingShapeMergable merge(IBoundingShape shape) {
	        if (shape == null) {
	            return this;
	        }
	     
	        if(shape instanceof BoundingSphere)
	        {
	        	BoundingSphere sphere = (BoundingSphere) shape;
	            float temp_radius = sphere.getRadius();
	            Vector3D temp_center = sphere.getCenter().getCopy();
	            
	            BoundingSphere rVal = new BoundingSphere((AbstractShape)sphere.getPeerComponent());
	            IBoundingShapeMergable rVal2 = merge(temp_radius, temp_center, rVal,rVal);
	            
	            return rVal2;
	        }else if(shape instanceof OrientedBoundingBox)
	        {
	        	OrientedBoundingBox box = (OrientedBoundingBox) shape;
	            BoundingSphere rVal = new BoundingSphere((AbstractShape)box.getPeerComponent());
	            BoundingSphere rVal2 =(BoundingSphere) ((BoundingSphere)this.clone()).mergeOBB(box);
	            
	            return rVal2;
	        }else if(shape instanceof BoundsArbitraryPlanarPolygon)
	        {
	        	BoundsArbitraryPlanarPolygon polygon = (BoundsArbitraryPlanarPolygon) shape;
	        	BoundingSphere rVal = (BoundingSphere)this.clone();//TODO
	        	return rVal;
	        }else if(shape instanceof BoundsZPlaneRectangle)
	        {
	        	BoundsZPlaneRectangle rectangle = (BoundsZPlaneRectangle) shape;
	        	BoundingSphere rVal = (BoundingSphere)this.clone();//TODO
	        	return rVal;
	        }        
	        else
	        {
	        	return null;
	        }
	    }
	    
	    private IBoundingShapeMergable merge(float temp_radius, Vector3D temp_center, BoundingSphere rVal,BoundingSphere result) {
	    	
	    	//result.setPeerComponent(this.peerComponent);
	        Vector3D diff = temp_center.getSubtracted(center);
	      
	        float lengthSquared = diff.lengthSquared();
	        float radiusDiff = temp_radius - radius;

	        float fRDiffSqr = radiusDiff * radiusDiff;
	       
	        
	        if (fRDiffSqr >= lengthSquared) {
	            if (radiusDiff <= 0.0f) {
	                return this;
	            } 
	                
	            Vector3D rCenter = rVal.getCenter();
	            
	            rCenter = temp_center;	            
	            result.setRadius(temp_radius);
	            return rVal;
	        }

	        float length = (float) Math.sqrt(lengthSquared);

	        Vector3D rCenter = rVal.getCenter();
	       
	        if (length > radiusEpsilon) {
	        
	            float coeff = (length + radiusDiff) / (2.0f * length);
	            rCenter = (center.addLocal(diff.getScaled(coeff))).getCopy();
	        } else {
	            rCenter = center.getCopy();
	        }

	        result.setRadius(0.5f * (length + radius + temp_radius));
	        result.setCenter (rCenter);
	        
	        return result;
	    }

	    /**
	     * Merges this sphere with the given OBB.
	     *
	     * @param volume
	     *            The OBB to merge.
	     * @return This sphere, after merging.
	     */
	    private IBoundingShape mergeOBB(OrientedBoundingBox volume) {
	    	this.peerComponent = volume.getPeerComponent();

	    	// compute edge points from the obb
	    	
	    	
	        if (!volume.correctCorners)
	            volume.computeCorners();
	        _mergeBuf.rewind();
	        for (int i = 0; i < 8; i++) {	        	
	            _mergeBuf.put(volume.vectorStore[i].x);
	            _mergeBuf.put(volume.vectorStore[i].y);
	            _mergeBuf.put(volume.vectorStore[i].z);
	        }
	        
	        // remember old radius and center
	        float oldRadius = radius;
	        Vector3D oldCenter =  center.getCopy();
	      
	        // compute new radius and center from obb points
	        System.out.println("center" + center);
	        computeFromPoints(_mergeBuf);	        
	        Vector3D newCenter = center.getCopy();
	       
	        float newRadius = radius;
	       
	        // restore old center and radius
	        center = oldCenter;
	        System.out.println("newCenter: " + newCenter + " center " + center);
	        radius = oldRadius;
	       
	        //merge obb points result
	        BoundingSphere sphere = new BoundingSphere((AbstractShape)volume.getPeerComponent());
	        BoundingSphere shape =  (BoundingSphere)merge( newRadius, newCenter, this,sphere);
	        System.out.println("Shape " + shape.getCenterPointGlobal() + " " + shape.getRadius());
	        return shape;
	        
	    }
	    
	    /**
	     * <code>clone</code> creates a new BoundingSphere object containing the
	     * same data as this one.
	     *
	     * @param store
	     *            where to store the cloned information. if null or wrong class,
	     *            a new store is created.
	     * @return the new BoundingSphere
	     */
	    public IBoundingShape clone() {
	    	/*if(store !=null && store instanceof BoundingSphere) {
	            BoundingSphere rVal = (BoundingSphere) store;
	            if (null == rVal.center) {
	                rVal.center = new Vector3D();
	            }
	            rVal.center = center;
	            rVal.radius = radius;
	            
	            return rVal;
	        } */
	    	
	    	BoundingSphere sphere = new BoundingSphere(this);
	    	sphere.center = this.center;
	    	sphere.radius = this.radius;
		
	        return sphere;
	    }

		public MTComponent getPeerComponent() {
			return this.peerComponent;
		}
		
		public void setPeerComponent(MTComponent peerComponent) {
			this.peerComponent = peerComponent;
		}	
		
		public IBoundingShapeMergable getBoundsTransformed(TransformSpace transformSpace){
			BoundingSphere sphere = (BoundingSphere)this.clone();
			sphere.setRadius(this.getRadius());
			sphere.setCenter(this.getCenter().getCopy());
			
			switch(transformSpace)
			{
				case LOCAL:
		    		return sphere;        		
		    	case RELATIVE_TO_PARENT:
		    		if(this.peerComponent.getParent()!=null)
		    		{		    			
		    			sphere.transform(this.peerComponent.getLocalMatrix());		    			
		    			return sphere;
		    		}else
		    		{
		    			return sphere;
		    		}
		    	case GLOBAL:
		    		return sphere.transform(this.peerComponent.getGlobalMatrix());
		    	default:
		    		return sphere;
			}
			
		}
		
		
		
	}
