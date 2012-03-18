/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.util.math;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;

import processing.core.PApplet;



/**
 * A collection of geometry related static helper methods.
 */
public class ToolsGeometry {
	
	
	private ToolsGeometry(){} //Dont allow instances of this class
	

	/**
	 * Calculates the intersection of a ray and a plane.
	 * 
	 * @param ray the ray
	 * @param planePoint1 the plane point1
	 * @param planePoint2 the plane point2
	 * @param planePoint3 the plane point3
	 * 
	 * @return the ray plane intersection
	 * 
	 * the intersection point or 'null' if there is not intersection
	 */
	public static Vector3D getRayPlaneIntersection(Ray ray, Vector3D planePoint1, Vector3D planePoint2, Vector3D planePoint3){
		return getRayPlaneIntersection(ray, ToolsGeometry.getNormal(planePoint1, planePoint2, planePoint3, true), planePoint1);
	}
	
	
	/**
	 * Calculates the intersection of a ray and a plane.
	 * 
	 * @param ray the ray
	 * @param planeNormal the plane normal
	 * @param pointInPlane the point in plane
	 * 
	 * @return the ray plane intersection
	 * 
	 * the intersection point or 'null' if there is not intersection
	 */
	public static Vector3D getRayPlaneIntersection(Ray ray, Vector3D planeNormal, Vector3D pointInPlane){
		Vector3D rayStartPoint 			= ray.getRayStartPoint();
		Vector3D pointInRayDirection 	= ray.getPointInRayDirection();
		
		//calculate D, with the substitution of x,y,z with the values of point a point in the polygons plane
		float d = - (planeNormal.dot(pointInPlane));
		
		//make a copy because the vector gets changed
		Vector3D rayDirectionVect = pointInRayDirection.getSubtracted(rayStartPoint);
		
		//solve for t
		float t = - ((planeNormal.dot(rayStartPoint) + d)) / 
					 (planeNormal.dot(rayDirectionVect));
		
		if (t<=0) 
			 return null;
		else {
			//calculate the intersection point
			rayDirectionVect.scaleLocal(t);
			Vector3D point = rayStartPoint.getAdded(rayDirectionVect);
			return point;
		}
	}

	
	
	/**
	 * Tests if the given ray intersects a triangle specified by the three given vector points.
	 * Returns the intersection point or null if there is none.
	 * 
	 * @param R the r
	 * @param t0 the t0
	 * @param t1 the t1
	 * @param t2 the t2
	 * 
	 * @return the ray triangle intersection
	 * 
	 * the intersection point
	 */
	public static Vector3D getRayTriangleIntersection(Ray R, Vector3D t0, Vector3D t1, Vector3D t2){
		return getRayTriangleIntersection(R, t0, t1, t2, null);
	}
	
	
	
	/**
	 * Tests if the given ray intersects a triangle specified by the three given vector points.
	 * Returns the intersection point or null if there is none.<br>
	 * This method uses the provided triangle normal vector for speeding things
	 * up so the normal doesent have to be computed again if its already cached somewhere.
	 * <p><strong>Note:</strong> The provided normal vector should NOT be normalized! (For detection of
	 * degenerate triangles)
	 * 
	 * @param R the r
	 * @param t0 the t0
	 * @param t1 the t1
	 * @param t2 the t2
	 * @param n the triangles normal vector
	 * 
	 * @return the ray triangle intersection
	 */
	public static Vector3D getRayTriangleIntersection(Ray R, Vector3D t0, Vector3D t1, Vector3D t2, Vector3D n){
		Vector3D  u, v;             // triangle vectors
		Vector3D  dir, w0, w;          // ray vectors
		float     r, a, b;             // params to calc ray-plane intersect

		// get triangle edge vectors and plane normal
		u = t1.getSubtracted(t0);
		v = t2.getSubtracted(t0);
		
		if (n == null) 
			n = u.getCross(v);
		
//		if (n.equalsVector(Vector3D.ZERO_VECTOR)){ // triangle is degenerate
//			return null;
//		}
		
//		Vector3D n2 = 
		if (n.equalsVectorWithTolerance(Vector3D.ZERO_VECTOR, ToolsMath.ZERO_TOLERANCE)){ // triangle is degenerate
			return null;
		}
			
		dir = R.getPointInRayDirection().getSubtracted(R.getRayStartPoint());
		w0 	= R.getRayStartPoint().getSubtracted(t0);
		a 	= -n.dot(w0);
		b 	= n.dot(dir);
		
		if (Math.abs(b) < ToolsMath.FLT_EPSILON) {     // ray is parallel to triangle plane
			if (a == 0){                // ray lies in triangle plane
				return null;
			}
			else {
				return null;
			}
		}

		// get intersect point of ray with triangle plane
		r = a / b;
		if (r < 0.0){                   // ray goes away from triangle
			return null;
		}
		// for a segment, also test if (r > 1.0) => no intersect
		
		dir.scaleLocal(r);
		Vector3D I = R.getRayStartPoint().getAdded(dir) ;
		
//		// is I inside T?
		float uu, uv, vv, wu, wv, D;
		uu = u.dot(u);
		uv = u.dot(v);
		vv = v.dot(v);
		w = I.getSubtracted(t0);
		wu = w.dot(u);
		wv = w.dot(v);
		D = uv * uv - uu * vv;

		// get and test parametric coords
		float s, t;
		s = (uv * wv - vv * wu) / D;
		
		if (s < 0.0 || s > 1.0){        // I is outside T
			return null;
		}
		
		t = (uv * wu - uu * wv) / D;
		if (t < 0.0 || (s + t) > 1.0){  // I is outside T
			return null;
		}
		return I;
	}
	
	
	
	
	/////////////////////////////////////////////////
	/** EPSILON represents the error buffer used to denote a hit. */
    public static final double EPSILON = 1e-12;

    /** The Constant tempVa. */
    private static final Vector3D tempVa = new Vector3D();

    /** The Constant tempVb. */
    private static final Vector3D tempVb = new Vector3D();

    /** The Constant tempVc. */
    private static final Vector3D tempVc = new Vector3D();

    /** The Constant tempVd. */
    private static final Vector3D tempVd = new Vector3D();

    /** The Constant tempVe. */
    private static final Vector3D tempVe = new Vector3D();

    /** The Constant tempFa. */
    private static final float[] tempFa = new float[2];

    /** The Constant tempFb. */
    private static final float[] tempFb = new float[2];

    /** The Constant tempV2a. */
    private static final Vector3D tempV2a = new Vector3D();

    /** The Constant tempV2b. */
    private static final Vector3D tempV2b = new Vector3D();

    /**
     * This is a <b>VERY </b> brute force method of detecting if two MTTriangleMesh
     * objects intersect.
     * 
     * @param mesh1 The first TriMesh.
     * @param mesh2 The second TriMesh.
     * 
     * @return True if they intersect, false otherwise.
     */
    public static boolean isMeshesIntersecting(MTTriangleMesh mesh1, MTTriangleMesh mesh2) {
    	Buffer indexAA = mesh1.getGeometryInfo().getIndexBuff();
    	Buffer indexBB = mesh2.getGeometryInfo().getIndexBuff();
    	
    	if (indexAA instanceof ShortBuffer && indexBB instanceof ShortBuffer) {
			ShortBuffer indexA = (ShortBuffer) indexAA;
			ShortBuffer indexB = (ShortBuffer) indexBB;
			
			//Transform the vertices of both meshes into world coordinates
	    	Matrix aTransform = mesh1.getGlobalMatrix();
	    	Matrix bTransform = mesh2.getGlobalMatrix();

	    	Vector3D[] vertA = ToolsBuffers.getVector3DArray(mesh1.getGeometryInfo().getVertBuff());
	    	Vector3D.transFormArrayLocal(aTransform, vertA);

	    	Vector3D[] vertB = ToolsBuffers.getVector3DArray(mesh2.getGeometryInfo().getVertBuff());
	    	Vector3D.transFormArrayLocal(bTransform, vertB);

	    	for (int i = 0; i < mesh1.getTriangleCount(); i++) {
	    		for (int j = 0; j < mesh2.getTriangleCount(); j++) {
	    			if (isTrianglesIntersect(vertA[indexA.get(i * 3 + 0)],
	    					vertA[indexA.get(i * 3 + 1)], vertA[indexA.get(i * 3 + 2)],
	    					vertB[indexB.get(j * 3 + 0)], vertB[indexB.get(j * 3 + 1)],
	    					vertB[indexB.get(j * 3 + 2)]))
	    				return true;
	    		}
	    	}
	    	return false;
		}else{
			IntBuffer indexA = (IntBuffer) indexAA;
			IntBuffer indexB = (IntBuffer) indexBB;
			
			//Transform the vertices of both meshes into world coordinates
	    	Matrix aTransform = mesh1.getGlobalMatrix();
	    	Matrix bTransform = mesh2.getGlobalMatrix();

	    	Vector3D[] vertA = ToolsBuffers.getVector3DArray(mesh1.getGeometryInfo().getVertBuff());
	    	Vector3D.transFormArrayLocal(aTransform, vertA);

	    	Vector3D[] vertB = ToolsBuffers.getVector3DArray(mesh2.getGeometryInfo().getVertBuff());
	    	Vector3D.transFormArrayLocal(bTransform, vertB);

	    	for (int i = 0; i < mesh1.getTriangleCount(); i++) {
	    		for (int j = 0; j < mesh2.getTriangleCount(); j++) {
	    			if (isTrianglesIntersect(vertA[indexA.get(i * 3 + 0)],
	    					vertA[indexA.get(i * 3 + 1)], vertA[indexA.get(i * 3 + 2)],
	    					vertB[indexB.get(j * 3 + 0)], vertB[indexB.get(j * 3 + 1)],
	    					vertB[indexB.get(j * 3 + 2)]))
	    				return true;
	    		}
	    	}
	    	return false;
		}
    }

    /**
     * This method tests for the intersection between two triangles defined by
     * their vertexes. Converted to java from C code found at
     * http://www.acm.org/jgt/papers/Moller97/tritri.html
     * 
     * @param v0 First triangle's first vertex.
     * @param v1 First triangle's second vertex.
     * @param v2 First triangle's third vertex.
     * @param u0 Second triangle's first vertex.
     * @param u1 Second triangle's second vertex.
     * @param u2 Second triangle's third vertex.
     * 
     * @return True if the two triangles intersect, false otherwise.
     */
    public static boolean isTrianglesIntersect(	Vector3D v0, Vector3D v1, Vector3D v2,
                    							Vector3D u0, Vector3D u1, Vector3D u2)
    {
    	Vector3D e1 = tempVa;
    	Vector3D e2 = tempVb;
    	Vector3D n1 = tempVc;
    	Vector3D n2 = tempVd;
    	float d1, d2;
    	float du0, du1, du2, dv0, dv1, dv2;
    	Vector3D d = tempVe;
    	float[] isect1 = tempFa;
    	float[] isect2 = tempFb;
    	float du0du1, du0du2, dv0dv1, dv0dv2;
    	short index;
    	float vp0, vp1, vp2;
    	float up0, up1, up2;
    	float bb, cc, max;
    	float xx, yy, xxyy, tmp;

    	/* compute plane equation of triangle(v0,v1,v2) */
    	e1 = v1.getSubtracted(v0);
    	e2 = v2.getSubtracted(v0);
    	n1 = e1.getCross(e2);

//  	v1.subtract(v0, e1);
//  	v2.subtract(v0, e2);
//  	e1.cross(e2, n1);
    	d1 = -n1.dot(v0);
    	/* plane equation 1: n1.X+d1=0 */

    	/*
    	 * put u0,u1,u2 into plane equation 1 to compute signed distances to the
    	 * plane
    	 */
    	du0 = n1.dot(u0) + d1;
    	du1 = n1.dot(u1) + d1;
    	du2 = n1.dot(u2) + d1;

    	/* coplanarity robustness check */
    	if (ToolsMath.abs(du0) < EPSILON)
    		du0 = 0.0f;
    	if (ToolsMath.abs(du1) < EPSILON)
    		du1 = 0.0f;
    	if (ToolsMath.abs(du2) < EPSILON)
    		du2 = 0.0f;
    	du0du1 = du0 * du1;
    	du0du2 = du0 * du2;

    	if (du0du1 > 0.0f && du0du2 > 0.0f) {
    		return false;
    	}

    	/* compute plane of triangle (u0,u1,u2) */
//  	u1.subtract(u0, e1);
//  	u2.subtract(u0, e2);
//  	e1.cross(e2, n2);
    	e1 = u1.getSubtracted(u0);
    	e2 = u2.getSubtracted(u0);
    	n1 = e1.getCross(e2);

    	d2 = -n2.dot(u0);
    	/* plane equation 2: n2.X+d2=0 */

    	/* put v0,v1,v2 into plane equation 2 */
    	dv0 = n2.dot(v0) + d2;
    	dv1 = n2.dot(v1) + d2;
    	dv2 = n2.dot(v2) + d2;


    	if (ToolsMath.abs(dv0) < EPSILON)
    		dv0 = 0.0f;
    	if (ToolsMath.abs(dv1) < EPSILON)
    		dv1 = 0.0f;
    	if (ToolsMath.abs(dv2) < EPSILON)
    		dv2 = 0.0f;

    	dv0dv1 = dv0 * dv1;
    	dv0dv2 = dv0 * dv2;

    	if (dv0dv1 > 0.0f && dv0dv2 > 0.0f) { /*
    	 * same sign on all of them + not
    	 * equal 0 ?
    	 */
    		return false; /* no intersection occurs */
    	}

    	/* compute direction of intersection line */
    	d = n1.getCross(n2);
//  	n1.cross(n2, d);

    	/* compute and index to the largest component of d */
    	max = ToolsMath.abs(d.x);
    	index = 0;
    	bb = ToolsMath.abs(d.y);
    	cc = ToolsMath.abs(d.z);
    	if (bb > max) {
    		max = bb;
    		index = 1;
    	}
    	if (cc > max) {
    		max = cc;
    		vp0 = v0.z;
    		vp1 = v1.z;
    		vp2 = v2.z;

    		up0 = u0.z;
    		up1 = u1.z;
    		up2 = u2.z;

    	} else if (index == 1) {
    		vp0 = v0.y;
    		vp1 = v1.y;
    		vp2 = v2.y;

    		up0 = u0.y;
    		up1 = u1.y;
    		up2 = u2.y;
    	} else {
    		vp0 = v0.x;
    		vp1 = v1.x;
    		vp2 = v2.x;

    		up0 = u0.x;
    		up1 = u1.x;
    		up2 = u2.x;
    	}

    	/* compute interval for triangle 1 */
    	Vector3D abc 	= tempVa;
    	Vector3D x0x1 	= tempV2a;
    	if (newComputeIntervals(vp0, vp1, vp2, dv0, dv1, dv2, dv0dv1, dv0dv2,
    			abc, x0x1)) {
    		return coplanarTriTri(n1, v0, v1, v2, u0, u1, u2);
    	}

    	/* compute interval for triangle 2 */
    	Vector3D def = tempVb;
    	Vector3D y0y1 = tempV2b;
    	if (newComputeIntervals(up0, up1, up2, du0, du1, du2, du0du1, du0du2,
    			def, y0y1)) {
    		return coplanarTriTri(n1, v0, v1, v2, u0, u1, u2);
    	}

    	xx = x0x1.x * x0x1.y;
    	yy = y0y1.x * y0y1.y;
    	xxyy = xx * yy;

    	tmp = abc.x * xxyy;
    	isect1[0] = tmp + abc.y * x0x1.y * yy;
    	isect1[1] = tmp + abc.z * x0x1.x * yy;

    	tmp = def.x * xxyy;
    	isect2[0] = tmp + def.y * xx * y0y1.y;
    	isect2[1] = tmp + def.z * xx * y0y1.x;

    	sort(isect1);
    	sort(isect2);

    	if (isect1[1] < isect2[0] || isect2[1] < isect1[0]) {
    		return false;
    	}

    	return true;            
    }

    /**
     * Sort.
     * 
     * @param f the f
     */
    private static void sort(float[] f) {
    	if (f[0] > f[1]) {
    		float c = f[0];
    		f[0] = f[1];
    		f[1] = c;
    	}
    }

    /**
     * New compute intervals.
     * 
     * @param vv0 the vv0
     * @param vv1 the vv1
     * @param vv2 the vv2
     * @param d0 the d0
     * @param d1 the d1
     * @param d2 the d2
     * @param d0d1 the d0d1
     * @param d0d2 the d0d2
     * @param abc the abc
     * @param x0x1 the x0x1
     * 
     * @return true, if successful
     */
    private static boolean newComputeIntervals(float vv0, float vv1, float vv2,
    								float d0, float d1, float d2, float d0d1, float d0d2, Vector3D abc,Vector3D x0x1
    ) {
    	if (d0d1 > 0.0f) {
    		/* here we know that d0d2 <=0.0 */
    		/*
    		 * that is d0, d1 are on the same side, d2 on the other or on the
    		 * plane
    		 */
    		abc.x = vv2;
    		abc.y = (vv0 - vv2) * d2;
    		abc.z = (vv1 - vv2) * d2;
    		x0x1.x = d2 - d0;
    		x0x1.y = d2 - d1;
    	} else if (d0d2 > 0.0f) {
    		/* here we know that d0d1 <=0.0 */
    		abc.x = vv1;
    		abc.y = (vv0 - vv1) * d1;
    		abc.z = (vv2 - vv1) * d1;
    		x0x1.x = d1 - d0;
    		x0x1.y = d1 - d2;
    	} else if (d1 * d2 > 0.0f || d0 != 0.0f) {
    		/* here we know that d0d1 <=0.0 or that d0!=0.0 */
    		abc.x = vv0;
    		abc.y = (vv1 - vv0) * d0;
    		abc.z = (vv2 - vv0) * d0;
    		x0x1.x = d0 - d1;
    		x0x1.y = d0 - d2;
    	} else if (d1 != 0.0f) {
    		abc.x = vv1;
    		abc.y = (vv0 - vv1) * d1;
    		abc.z = (vv2 - vv1) * d1;
    		x0x1.x = d1 - d0;
    		x0x1.y = d1 - d2;
    	} else if (d2 != 0.0f) {
    		abc.x = vv2;
    		abc.y = (vv0 - vv2) * d2;
    		abc.z = (vv1 - vv2) * d2;
    		x0x1.x = d2 - d0;
    		x0x1.y = d2 - d1;
    	} else {
    		/* triangles are coplanar */
    		return true;
    	}
    	return false;
    }

    /**
     * Coplanar tri tri.
     * 
     * @param n the n
     * @param v0 the v0
     * @param v1 the v1
     * @param v2 the v2
     * @param u0 the u0
     * @param u1 the u1
     * @param u2 the u2
     * 
     * @return true, if successful
     */
    private static boolean coplanarTriTri(Vector3D n, Vector3D v0, Vector3D v1,
    									Vector3D v2, Vector3D u0, Vector3D u1, Vector3D u2) {
    	Vector3D a = new Vector3D();
    	short i0, i1;
    	a.x = ToolsMath.abs(n.x);
    	a.y = ToolsMath.abs(n.y);
    	a.z = ToolsMath.abs(n.z);

    	if (a.x > a.y) {
    		if (a.x > a.z) {
    			i0 = 1; /* a[0] is greatest */
    			i1 = 2;
    		} else {
    			i0 = 0; /* a[2] is greatest */
    			i1 = 1;
    		}
    	} else /* a[0] <=a[1] */{
    		if (a.z > a.y) {
    			i0 = 0; /* a[2] is greatest */
    			i1 = 1;
    		} else {
    			i0 = 0; /* a[1] is greatest */
    			i1 = 2;
    		}
    	}

    	/* test all edges of triangle 1 against the edges of triangle 2 */
    	float[] v0f = new float[3];
    	v0.toArray(v0f);
    	float[] v1f = new float[3];
    	v1.toArray(v1f);
    	float[] v2f = new float[3];
    	v2.toArray(v2f);
    	float[] u0f = new float[3];
    	u0.toArray(u0f);
    	float[] u1f = new float[3];
    	u1.toArray(u1f);
    	float[] u2f = new float[3];
    	u2.toArray(u2f);
    	if (edgeAgainstTriEdges(v0f, v1f, u0f, u1f, u2f, i0, i1)) {
    		return true;
    	}

    	if (edgeAgainstTriEdges(v1f, v2f, u0f, u1f, u2f, i0, i1)) {
    		return true;
    	}

    	if (edgeAgainstTriEdges(v2f, v0f, u0f, u1f, u2f, i0, i1)) {
    		return true;
    	}


    	/* finally, test if tri1 is totally contained in tri2 or vice versa */
    	pointInTri(v0f, u0f, u1f, u2f, i0, i1);
    	pointInTri(u0f, v0f, v1f, v2f, i0, i1);

    	return false;
    }

    /**
     * Point in tri.
     * 
     * @param V0 the v0
     * @param U0 the u0
     * @param U1 the u1
     * @param U2 the u2
     * @param i0 the i0
     * @param i1 the i1
     * 
     * @return true, if successful
     */
    private static boolean pointInTri(float[] V0, float[] U0, float[] U1,
    									float[] U2, int i0, int i1) {
    	float a, b, c, d0, d1, d2;
    	/* is T1 completly inside T2? */
    	/* check if V0 is inside tri(U0,U1,U2) */
    	a = U1[i1] - U0[i1];
    	b = -(U1[i0] - U0[i0]);
    	c = -a * U0[i0] - b * U0[i1];
    	d0 = a * V0[i0] + b * V0[i1] + c;

    	a = U2[i1] - U1[i1];
    	b = -(U2[i0] - U1[i0]);
    	c = -a * U1[i0] - b * U1[i1];
    	d1 = a * V0[i0] + b * V0[i1] + c;

    	a = U0[i1] - U2[i1];
    	b = -(U0[i0] - U2[i0]);
    	c = -a * U2[i0] - b * U2[i1];
    	d2 = a * V0[i0] + b * V0[i1] + c;
    	if (d0 * d1 > 0.0 && d0 * d2 > 0.0)
    		return true;

    	return false;
    }

    /**
     * Edge against tri edges.
     * 
     * @param v0 the v0
     * @param v1 the v1
     * @param u0 the u0
     * @param u1 the u1
     * @param u2 the u2
     * @param i0 the i0
     * @param i1 the i1
     * 
     * @return true, if successful
     */
    private static boolean edgeAgainstTriEdges(float[] v0, float[] v1,
    											float[] u0, float[] u1, float[] u2, int i0, int i1) {
    	float aX, aY;
    	aX = v1[i0] - v0[i0];
    	aY = v1[i1] - v0[i1];
    	/* test edge u0,u1 against v0,v1 */
    	if (edgeEdgeTest(v0, u0, u1, i0, i1, aX, aY)) {
    		return true;
    	}
    	/* test edge u1,u2 against v0,v1 */
    	if (edgeEdgeTest(v0, u1, u2, i0, i1, aX, aY)) {
    		return true;
    	}
    	/* test edge u2,u1 against v0,v1 */
    	if (edgeEdgeTest(v0, u2, u0, i0, i1, aX, aY)) {
    		return true;
    	}
    	return false;
    }

    /**
     * Edge edge test.
     * 
     * @param v0 the v0
     * @param u0 the u0
     * @param u1 the u1
     * @param i0 the i0
     * @param i1 the i1
     * @param aX the a x
     * @param Ay the ay
     * 
     * @return true, if successful
     */
    private static boolean edgeEdgeTest(float[] v0, float[] u0, float[] u1,
    									int i0, int i1, float aX, float Ay) {
    	float Bx = u0[i0] - u1[i0];
    	float By = u0[i1] - u1[i1];
    	float Cx = v0[i0] - u0[i0];
    	float Cy = v0[i1] - u0[i1];
    	float f = Ay * Bx - aX * By;
    	float d = By * Cx - Bx * Cy;
    	if ((f > 0 && d >= 0 && d <= f) || (f < 0 && d <= 0 && d >= f)) {
    		float e = aX * Cy - Ay * Cx;
    		if (f > 0) {
    			if (e >= 0 && e <= f)
    				return true;
    		} else {
    			if (e <= 0 && e >= f)
    				return true;
    		}
    	}
    	return false;
    }

    
    
    
    
    
    /**
     * from paul bourke ( http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/ )
     *
     */
    public static final int COINCIDENT = 0;

    public static final int PARALLEL = 1;

    public static final int INTERESECTING = 2;

    public static final int NOT_INTERESECTING = 3;

    public static int lineLineIntersect2D(Vector3D aBegin, Vector3D aEnd,
                                        Vector3D bBegin, Vector3D bEnd,
                                        Vector3D theIntersection) {
        float denom = ( (bEnd.y - bBegin.y) * (aEnd.x - aBegin.x)) -
                      ( (bEnd.x - bBegin.x) * (aEnd.y - aBegin.y));

        float nume_a = ( (bEnd.x - bBegin.x) * (aBegin.y - bBegin.y)) -
                       ( (bEnd.y - bBegin.y) * (aBegin.x - bBegin.x));

        float nume_b = ( (aEnd.x - aBegin.x) * (aBegin.y - bBegin.y)) -
                       ( (aEnd.y - aBegin.y) * (aBegin.x - bBegin.x));

        if (denom == 0.0f) {
            if (nume_a == 0.0f && nume_b == 0.0f) {
                return COINCIDENT;
            }
            return PARALLEL;
        }

        float ua = nume_a / denom;
        float ub = nume_b / denom;

        if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
            if (theIntersection != null) {
                // Get the intersection point.
                theIntersection.x = aBegin.x + ua * (aEnd.x - aBegin.x);
                theIntersection.y = aBegin.y + ua * (aEnd.y - aBegin.y);
            }
            return INTERESECTING;
        }
        return NOT_INTERESECTING;
    }



  
    /**
     * Gets the shortest distance between lines.
     * 
     * from paul bourke ( http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline3d/ )
     *
     * Calculate the line segment PaPb that is the shortest route between
     * two lines P1P2 and P3P4. Calculate also the values of mua and mub where
     * Pa = P1 + mua (P2 - P1)
     * Pb = P3 + mub (P4 - P3)
     * Return FALSE if no solution exists.
     *
     * 
     * @param p1 the p1 line 1 point1
     * @param p2 the p2 line 1 point2
     * @param p3 the p3 line 2 point1
     * @param p4 the p4 line 2 point2
     * @param pa the pa a not null vector that will be filled with the first point if the shortest distance line
     * @param pb the pb a not null vector that will be filled with the 2nd point if the shortest distance line
     * @param theResult the result - a float[2] array
     * 
     * @return the shortest distance between lines
     */
    public static boolean getShortestDistanceBetweenLines(Vector3D p1, Vector3D p2, Vector3D p3, Vector3D p4,
                                            Vector3D pa, Vector3D pb,
                                            float[] theResult) {

        final Vector3D p13 = p1.getSubtracted(p3);
        final Vector3D p43 = p4.getSubtracted(p3);
        if (Math.abs(p43.x) < EPSILON && Math.abs(p43.y) < EPSILON && Math.abs(p43.z) < EPSILON) {
            return false;
        }

        final Vector3D p21 = p2.getSubtracted(p1);
        if (Math.abs(p21.x) < EPSILON && Math.abs(p21.y) < EPSILON && Math.abs(p21.z) < EPSILON) {
            return false;
        }

        final float d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
        final float d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
        final float d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
        final float d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
        final float d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;

        final float denom = d2121 * d4343 - d4321 * d4321;
        if (Math.abs(denom) < EPSILON) {
            return false;
        }
        final float numer = d1343 * d4321 - d1321 * d4343;

        final float mua = numer / denom;
        final float mub = (d1343 + d4321 * mua) / d4343;

        pa.x = p1.x + mua * p21.x;
        pa.y = p1.y + mua * p21.y;
        pa.z = p1.z + mua * p21.z;
        pb.x = p3.x + mub * p43.x;
        pb.y = p3.y + mub * p43.y;
        pb.z = p3.z + mub * p43.z;

        if (theResult != null) {
            theResult[0] = mua;
            theResult[1] = mub;
        }
        return true;
    }



    //TODO TEST THESE METHODS!
    /**
    *
    * DistancePointLine Unit Test
    * Copyright (c) 2002, All rights reserved
    *
    * Damian Coventry
    * Tuesday, 16 July 2002
    *
    * Implementation of theory by Paul Bourke
    *
    * @param thePoint Vector3D
    * @param theLineStart Vector3D
    * @param theLineEnd Vector3D
    * @return float
    */
   public static float distancePointLineSegment(final Vector3D thePoint,
                                                final Vector3D theLineStart,
                                                final Vector3D theLineEnd) {
       final float u = distancePointLineU(thePoint, theLineStart, theLineEnd);

       if (u < 0.0f || u > 1.0f) {
           return -1; // closest point does not fall within the line segment
       }

       final Vector3D myIntersection = new Vector3D();
       myIntersection.x = theLineStart.x + u * (theLineEnd.x - theLineStart.x);
       myIntersection.y = theLineStart.y + u * (theLineEnd.y - theLineStart.y);
       myIntersection.z = theLineStart.z + u * (theLineEnd.z - theLineStart.z);

//       return thePoint.distance(myIntersection);
       return Vector3D.distance(thePoint, myIntersection);
   }

   /**
    * Distance point line.
    * 
    * @param thePoint the the point
    * @param theLineStart the the line start
    * @param theLineEnd the the line end
    * @return the float
    */
   public static float distancePointLine(final Vector3D thePoint,
                                         final Vector3D theLineStart,
                                         final Vector3D theLineEnd) {
       final float u = distancePointLineU(thePoint, theLineStart, theLineEnd);
       final Vector3D myIntersection = new Vector3D();
       myIntersection.x = theLineStart.x + u * (theLineEnd.x - theLineStart.x);
       myIntersection.y = theLineStart.y + u * (theLineEnd.y - theLineStart.y);
       myIntersection.z = theLineStart.z + u * (theLineEnd.z - theLineStart.z);

//       return thePoint.distance(myIntersection);
       return Vector3D.distance(thePoint, myIntersection);
   }

   public static float distancePointLineU(final Vector3D thePoint,
                                          final Vector3D theLineStart,
                                          final Vector3D theLineEnd) {
//       final float myLineMagnitude = theLineStart.distance(theLineEnd);
       final float myLineMagnitude = Vector3D.distance(theLineStart, theLineEnd);
       final float u = (((thePoint.x - theLineStart.x) * (theLineEnd.x - theLineStart.x)) +
               ((thePoint.y - theLineStart.y) * (theLineEnd.y - theLineStart.y)) +
               ((thePoint.z - theLineStart.z) * (theLineEnd.z - theLineStart.z))) /
               (myLineMagnitude * myLineMagnitude);

       return u;
   }
   
   
   //TODO UN-TESTED!
   /**
    * http://local.wasp.uwa.edu.au/~pbourke/geometry/sphereline/raysphere.c
      Calculate the intersection of a ray and a sphere
      The line segment is defined from p1 to p2
      The sphere is of radius r and centered at sc
      There are potentially two points of intersection given by
      p = p1 + mu1 (p2 - p1)
      p = p1 + mu2 (p2 - p1)
      Return FALSE if the ray doesn't intersect the sphere.
    */
   public static boolean RaySphere(Vector3D p1,
                                   Vector3D p2,
                                   Vector3D sc,
                                   float r) {
       float a, b, c;
       float bb4ac;
       Vector3D dp = new Vector3D();

       dp.x = p2.x - p1.x;
       dp.y = p2.y - p1.y;
       dp.z = p2.z - p1.z;
       a = dp.x * dp.x + dp.y * dp.y + dp.z * dp.z;
       b = 2 * (dp.x * (p1.x - sc.x) + dp.y * (p1.y - sc.y) + dp.z * (p1.z - sc.z));
       c = sc.x * sc.x + sc.y * sc.y + sc.z * sc.z;
       c += p1.x * p1.x + p1.y * p1.y + p1.z * p1.z;
       c -= 2 * (sc.x * p1.x + sc.y * p1.y + sc.z * p1.z);
       c -= r * r;
       bb4ac = b * b - 4 * a * c;
       if (Math.abs(a) < EPSILON || bb4ac < 0) {
           return false;
       }

       return true;
   }

   
   
   /**
    * Checks if is point in triangle.
    * 
    * @param v0 the v0
    * @param v1 the v1
    * @param v2 the v2
    * @param thePoint the the point
    * @return true, if is point in triangle
    */
   public static final boolean isPointInTriangle(final Vector3D v0,  final Vector3D v1,  final Vector3D v2, final Vector3D thePoint) {
	   ////	   Compute vectors
	   //	   v0 = C - A
	   //	   v1 = B - A
	   //	   v2 = P - A
	   Vector3D v00 = new Vector3D(v2);
	   v00.subtractLocal(v0);

	   Vector3D v01 = new Vector3D(v1);
	   v01.subtractLocal(v0);

	   Vector3D v02 = new Vector3D(thePoint);
	   v02.subtractLocal(v0);

	   //Compute dot products
	   float dot00 = v00.dot(v00);
	   float dot01 = v00.dot(v01);
	   float dot02 = v00.dot(v02);
	   float dot11 = v01.dot(v01);
	   float dot12 = v01.dot(v02);

	   //Compute barycentric coordinates
	   float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
	   float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
	   float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

	   //Check if point is in triangle
	   return (u > 0) && (v > 0) && (u + v < 1);
   }


	/**
	 * The Enum PolygonTestPlane.
	 * 
	 * @author Christopher Ruff
	 */
	private enum PolygonTestPlane{
		XY,
		XZ,
		YZ
    }

   /**
    * Checks if is point2 d in polygon.
    * 
    * @param x the x
    * @param y the y
    * @param thePolygon the the polygon
    * @return true, if is point2 d in polygon
    */
   public static boolean isPoint2DInPolygon(float x, float y, Vector3D[] thePolygon) {
	   int c = 0;
	   for (int i = 0, j = thePolygon.length - 1; i < thePolygon.length; j = i++) {
		   if ((((thePolygon[i].y <= y) && (y < thePolygon[j].y)) ||
				   ((thePolygon[j].y <= y) && (y < thePolygon[i].y))) &&
				   (x < (thePolygon[j].x - thePolygon[i].x) * (y - thePolygon[i].y) /
						   (thePolygon[j].y - thePolygon[i].y) + thePolygon[i].x)) {
			   c = (c + 1) % 2;
		   }
	   }
	   return c == 1;
   }


	private static boolean isPoint2DInPolygon(Vector3D testpoint, Vector3D[] thePolygon, PolygonTestPlane whichPlane) {
	   float x;
	   float y;
	   int c = 0;
	   switch (whichPlane) {
	   case XY:	
		   //System.out.println("Projected to X-Y");
		   x = testpoint.x;
		   y = testpoint.y;
		   for (int i = 0, j = thePolygon.length - 1; i < thePolygon.length; j = i++) {
			   if ((((thePolygon[i].y <= y) && (y < thePolygon[j].y)) ||
					   ((thePolygon[j].y <= y) && (y < thePolygon[i].y))) &&
					   (x < (thePolygon[j].x - thePolygon[i].x) * (y - thePolygon[i].y) /
							   (thePolygon[j].y - thePolygon[i].y) + thePolygon[i].x)) {
				   c = (c + 1) % 2;
			   }
		   }
		   break;
	   case XZ:	
		   //System.out.println("Projected to X-Z");
		   x = testpoint.x;
		   y = testpoint.z;
		   for (int i = 0, j = thePolygon.length - 1; i < thePolygon.length; j = i++) {
			   if ((((thePolygon[i].z <= y) && (y < thePolygon[j].z)) ||
					   ((thePolygon[j].z <= y) && (y < thePolygon[i].z))) &&
					   (x < (thePolygon[j].x - thePolygon[i].x) * (y - thePolygon[i].z) /
							   (thePolygon[j].z - thePolygon[i].z) + thePolygon[i].x)) {
				   c = (c + 1) % 2;
			   }
		   }
		   break;
	   case YZ:	
		   //System.out.println("Projected to Y-Z");
		   x = testpoint.y;
		   y = testpoint.z;
		   for (int i = 0, j = thePolygon.length - 1; i < thePolygon.length; j = i++) {
			   if ((((thePolygon[i].z <= y) && (y < thePolygon[j].z)) ||
					   ((thePolygon[j].z <= y) && (y < thePolygon[i].z))) &&
					   (x < (thePolygon[j].y - thePolygon[i].y) * (y - thePolygon[i].z) /
							   (thePolygon[j].z - thePolygon[i].z) + thePolygon[i].y)) {
				   c = (c + 1) % 2;
			   }
		   }
		   break;
	   default:
		   break;
	   }
	   return c == 1;
   }


	/**
    * Projects the polygon and and the point to check into 2D and then checks if the given point
    * is inside the shape.
    * <br><strong>NOTE:</strong> The polygon to test has to be planar, meaning that all points must lie
    * int the same plane in 3d space.
    * 
    * @param testPoint the test point
    * @param polyNormal the poly normal
    * @param polygonVertices the polygon vertices
    * 
    * @return true, if checks if is point in poly
    * 
    * whether the testpoint is inside the planar polygon or not
    */
   public static boolean isPoint3DInPlanarPolygon(Vector3D[] polygonVertices, Vector3D testPoint, Vector3D polyNormal){
	   if (testPoint == null) 
		   return false;

	   //Check parts of the normal to determine in which axis the poly is most contained in
	   float absAX = PApplet.abs(polyNormal.x);
	   float absBY = PApplet.abs(polyNormal.y);
	   float absCZ = PApplet.abs(polyNormal.z);

	   if (absAX > absBY){
		   if ( absAX > absCZ){ //X biggest, project into y,z drop x
			   return (ToolsGeometry.isPoint2DInPolygon(new Vector3D(testPoint), polygonVertices, PolygonTestPlane.YZ));
		   }else{ //Z biggest //project into x,y drop z
			   return (ToolsGeometry.isPoint2DInPolygon(new Vector3D(testPoint), polygonVertices, PolygonTestPlane.XY));
		   }
	   }else if (absBY > absAX){
		   if (absBY > absCZ){ //Y biggest //project into x,z drop y
			   return (ToolsGeometry.isPoint2DInPolygon(new Vector3D(testPoint), polygonVertices, PolygonTestPlane.XZ));
		   }else{ //Z biggest //project into x,y drop Z
			   return (ToolsGeometry.isPoint2DInPolygon(new Vector3D(testPoint), polygonVertices, PolygonTestPlane.XY));
		   }
	   }else if (absCZ > absAX){
		   if (absCZ > absBY){ //Z biggest //project into x,y
			   return (ToolsGeometry.isPoint2DInPolygon(new Vector3D(testPoint), polygonVertices, PolygonTestPlane.XY));
		   }else{ //Y biggest // project into x,z
			   return (ToolsGeometry.isPoint2DInPolygon(new Vector3D(testPoint), polygonVertices, PolygonTestPlane.XZ));
		   }
	   }else{
		   return false;
	   }
   }


	/**
	 * Checks if the planar polygon vertices contain the point.
	 * 
	 * @param polygonPoints the polygon points
	 * @param testPoint the test point
	 * 
	 * @return true, if checks if is polygon contains point
	 */
	public static boolean isPolygonContainsPoint(Vector3D[] polygonPoints, Vector3D testPoint){
		Vector3D polyNormal = ToolsGeometry.getNormal(polygonPoints[0],polygonPoints[1], polygonPoints[2], true);
		//Check if point is in plane of polygon
		Vector3D tmp = testPoint.getSubtracted(polygonPoints[0]);
		float dotProdukt = tmp.dot(polyNormal);
		
		//Remove the second condition if you want exact matches, this allows a small tolerance
		if (dotProdukt == 0 || Math.abs(dotProdukt) < 0.015) {
			return isPoint3DInPlanarPolygon(polygonPoints, testPoint, polyNormal);
		}
		else{	
			return false;
		}
	}


	/**
	 * Returns the center of mass of the planar polygon vertices in the x,y plane.
	 * <br><strong>NOTE:</strong> Use this in 2D, this only uses the x, y coordinates of the vectors!.
	 * 
	 * @param vertices the vertices
	 * 
	 * @return the polygon center of mass2 d
	 */
	public static Vector3D getPolygonCenterOfMass2D(Vector3D[] vertices){
		float cx=0,cy=0;
		float area=(float)ToolsGeometry.getPolygonArea2D(vertices);
		int i,j;
		int N = vertices.length;

		float factor=0;
		for (i=0;i<N;i++) {
			j = (i + 1) % N;
			factor = (vertices[i].x * vertices[j].y - vertices[j].x * vertices[i].y);
			cx+= (vertices[i].x + vertices[j].x) * factor;
			cy+= (vertices[i].y + vertices[j].y) * factor;
		}
		area*=6.0f;
		factor=1/area;
		cx*=factor;
		cy*=factor;

		//TODO how to get this in 3D? Project all vertex to the z plane and get the centerof mass ?
		//this is a test, calculating the Z coordinate by integrating over
		//all z values
		float zValues = 0;
		for (int k = 0; k < vertices.length-1; k++) {
			Vector3D vector3D = vertices[k];
			zValues += vector3D.z;
		}

		Vector3D center = new Vector3D(cx, cy , zValues/vertices.length);
		//		System.out.println("Center: " + center);
		return center;
	}


	/**
	 * Calculates the area of a 2D polygon using its transformed world coordinates
	 * <br><strong>NOTE:</strong> Use this in 2D, this only uses the x, y coordinates of the vectors!
	 * <br>NOTE: works only if the last vertex is equal to the first (polygon is closed correctly). (not confirmed..)
	 * <br>Polygon vertices have to be declared in counter-clockwise order! (or cw..?)
	 * @param vertices the vertices
	 * 
	 * @return the area as double
	 */
	public static double getPolygonArea2D(Vector3D[] vertices){
		//		/*
		int i;
		int N = vertices.length;
		double area = 0;

		for (i=0;i<N-1;i++) {
			area = area + vertices[i].x * vertices[i+1].y - vertices[i+1].x * vertices[i].y;
		}
		area /= 2.0;
		//		System.out.println("Area: " + (area < 0 ? -area : area));
		//		*/

		//		double area = getPolygonAreaSigned2D(vertices);
		//		System.out.println("Area: " + area);

		return (area < 0 ? -area : area);
	}


	/**
	 * Calculates and returns the normal vector of the plane, the 3 given points are lying in.
	 * Also normalizes the normal if <code>normalize</code> is set to true.
	 * 
	 * @param v0 the v0
	 * @param v1 the v1
	 * @param v2 the v2
	 * @param normalize the normalize
	 * 
	 * @return the normal
	 * 
	 * the normal vector
	 */
	public static Vector3D getNormal(Vector3D v0, Vector3D v1, Vector3D v2, boolean normalize){
		float ax,ay,az, bx,by,bz;
		ax = v1.x - v0.x; //aX
		ay = v1.y - v0.y; //aY
		az = v1.z - v0.z; //aZ
		
		bx = v2.x - v0.x; //bX
		by = v2.y - v0.y; //by
		bz = v2.z - v0.z; //bz
		
		float crossX = ay * bz - by * az;
	    float crossY = az * bx - bz * ax;
	    float crossZ = ax * by - bx * ay;
	    
	    Vector3D normal = new Vector3D(crossX, crossY, crossZ);
	    
	    if (normalize)
	    	normal.normalizeLocal();
	    
		return normal;
	}
	
	
	
	final static int AXIS_NONE = -1;
	final static int AXIS_X = 0;
	final static int AXIS_Y = 1;
	final static int AXIS_Z = 2;
	
	
	/**
	 * Checks if is polygon axis aligned. Uses the polygons
	 * local vertices to check.
	 * The result is one of the values:
	 * <br>AXIS_NONE, AXIS_X, AXIS_Y, AXIS_Z
	 * 
	 * @param p the p
	 * @param epsilon the epsilon
	 * @return the int
	 */
	public static int isPolygonAxisAlignedLocal (MTPolygon p, float epsilon) {
		boolean ax = true, ay = true, az = true;
//		float where = Float.MAX_VALUE;
		
		Vector3D[] vertices = p.getVerticesLocal();
		Vector3D a = vertices[0];
		
		for (int i = 1 ; i < vertices.length ; i++){
			Vector3D b = vertices[i];
			Vector3D d = a.getSubtracted(b);
//			Vector3D d = b.getSubtracted(a);
			
			if (Math.abs(d.x) > epsilon){
				ax = false;
				if (!ay && !az) 
					return AXIS_NONE;
			}
			if (Math.abs(d.y) > epsilon){
				ay = false;
				if (!ax && !az) 
					return AXIS_NONE;
			}
			if (Math.abs(d.z) > epsilon){
				az = false;
				if (!ax && !ay) 
					return AXIS_NONE;
			}
		}
		
		if (ax) { 
//			where = a.x; 
//			System.out.println("Where: " + where);
			return AXIS_X;
		}
		if (ay) { 
//			where = a.y; 
//			System.out.println("Where: " + where);
			return AXIS_Y; 
		}
		if (az) { 
//			where = a.z; 
//			System.out.println("Where: " + where);
			return AXIS_Z; 
		}
		return AXIS_NONE;
	}
	
	
	
	/**
	 * Calculates the distance between 2 point vectors in 3D.
	 * 
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return the float
	 */
	public static float distance (Vector3D v1, Vector3D v2) {
		return v1.distance(v2);
	}

	/**
	 * Calculates the distance between 2 points in 2D (only x,y considered)
	 * 
	 * @param v1 the v1
	 * @param v2 the v2
	 * 
	 * @return the float
	 */
	public static float distance2D(Vector3D v1, Vector3D v2){
		return v1.distance2D(v2);
	}
	
	
	/**
	 * Angle between 2 directional vectors.
	 * 
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return the angle in radians
	 */
	public static float angleBetween(Vector3D v1, Vector3D v2) {
		return v1.angleBetween(v2);
	}
	

	/**
	 * Goes through a list of vector arrays and gets the minimum and maximum
	 * values of all together.
	 * 
	 * @param Vector3DLists the vector3 d lists
	 * 
	 * @return a float[4] {minX, minY, maxX, maxY};
	 */
	public static float[] getMinXYMaxXY(ArrayList<? extends Vector3D[]> Vector3DLists) {
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
        for (Vector3D[] Vector3DList : Vector3DLists) {
            Vector3D[] vertices = Vector3DList;
            for (Vector3D Vector3D : vertices) {
                if (Vector3D.getX() < minX)
                    minX = Vector3D.getX();
                if (Vector3D.getX() > maxX)
                    maxX = Vector3D.getX();
                if (Vector3D.getY() < minY)
                    minY = Vector3D.getY();
                if (Vector3D.getY() > maxY)
                    maxY = Vector3D.getY();
            }
        }
		return new float[]{minX, minY, maxX, maxY};
	}


	/**
	 * Goes through a list of vector arrays and
	 * gets the minimum and maximum values.
	 * 
	 * @param Vector3DList the vector3 d list
	 * 
	 * @return a float[4] {minX, minY, maxX, maxY};
	 */
	public static float[] getMinXYMaxXY(Vector3D[] Vector3DList) {
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
        for (Vector3D Vector3D : Vector3DList) {
            if (Vector3D.getX() < minX)
                minX = Vector3D.getX();
            if (Vector3D.getX() > maxX)
                maxX = Vector3D.getX();
            if (Vector3D.getY() < minY)
                minY = Vector3D.getY();
            if (Vector3D.getY() > maxY)
                maxY = Vector3D.getY();
        }
		return new float[]{minX, minY, maxX, maxY};
	}


	/**
	 * Checks whether the supplied vertex array contains BezierVertex instances.
	 * @param originalPointsArray the original points array
	 * 
	 * @return true, if contains bezier vertices
	 */
	public static boolean containsBezierVertices(Vertex[] originalPointsArray) {
        for (Vertex vertex : originalPointsArray) {
            if (vertex instanceof BezierVertex) {
                return true;
            }
        }
		return false;
	}


	/**
	 * Interpolates the BezierVertex' in the Vertex array into regular vertices,
	 * and approximates the bezier curve this way.
	 * 
	 * @param vertexArr the vertex arr
	 * @param resolution the resolution
	 * 
	 * @return the vertex[]
	 */
	public static Vertex[] createVertexArrFromBezierArr(Vertex[] vertexArr, int resolution){
		ArrayList<Vertex> allVerticesWithCurves = new ArrayList<Vertex>();
		//Convert BezierVertices to regular Vertices
		//RESOTULTION FACTOR! more = better quality, less performance
		int segments = resolution; 

		//Replace the beziervertices with many calculated regular vertices
		for (int i = 0; i < vertexArr.length; i++) {
			Vertex vertex = vertexArr[i];
			if (vertex instanceof BezierVertex){
				BezierVertex b = (BezierVertex)vertex;
				Vertex[] curve = getCubicBezierVertices(
						vertexArr[i-1].getX(), 
						vertexArr[i-1].getY(),
						b.getFirstCtrlPoint().getX(), 
						b.getFirstCtrlPoint().getY(), 
						b.getSecondCtrlPoint().getX(),
						b.getSecondCtrlPoint().getY(),
						b.getX(),
						b.getY(), 
						segments
				);	
				//Add all the curve vertices
                for (Vertex curveVertex : curve) {
                    //							allVerticesWithCurves.add(new Vertex(curveVertex.getX(), curveVertex.getY(), 0, vertex.getR(),vertex.getG(),vertex.getB(),vertex.getA()));
                    curveVertex.setRGBA(vertex.getR(), vertex.getG(), vertex.getB(), vertex.getA());
                    if (allVerticesWithCurves.size() > 0) {
                        //Only add if not equal to last one in list
                        if (!allVerticesWithCurves.get(allVerticesWithCurves.size() - 1).equalsVector(curveVertex)) {
                            allVerticesWithCurves.add(curveVertex);
                        }
                    } else {
                        allVerticesWithCurves.add(curveVertex);
                    }
                }
			}else{
				//Add the normal vertices
				allVerticesWithCurves.add(new Vertex(vertex));
			}//else
		}//For
		return allVerticesWithCurves.toArray(new Vertex[allVerticesWithCurves.size()]);
	}


	/**
	 * Interpolates the BezierVertex' in the Vertex array lists into regular vertices,
	 * and approximates the bezier curve this way.
	 * 
	 * @param vertexArrays the vertex arrays
	 * @param resolution the resolution
	 * 
	 * @return the list< vertex[]>
	 */
	public static List<Vertex[]> createVertexArrFromBezierVertexArrays(List<Vertex[]> vertexArrays, int resolution){
		ArrayList<Vertex[]> partialPathsListCurves = new ArrayList<Vertex[]>() ;
        for (Vertex[] partArray : vertexArrays) {
            partArray = createVertexArrFromBezierArr(partArray, resolution);
            partialPathsListCurves.add(partArray);
        }
		return partialPathsListCurves;
	}


	/**
	 * Calculates the vertices of a quadric bezier curve defined by the
	 * startpoint curveStartP, the controlpoint curveControlP and the end point curveEndP.<br>
	 * The segments parameter defines the resolution of the curve.
	 * <br>Note: This method uses only the X and Y Coordinates and generates a 2D curve!
	 * 
	 * @param curveStartP the curve start p
	 * @param curveControlP the curve control p
	 * @param curveEndP the curve end p
	 * @param segmentDetail the segment detail
	 * 
	 * @return the quad bezier vertices
	 */
	public static Vertex[] getQuadBezierVertices(Vertex curveStartP, Vertex curveControlP, Vertex curveEndP, int segmentDetail){
		//Change detail here
		double segments = (double)segmentDetail;
		double count = 0;  //used as our counter
		double detailBias; //how many points should we put on our curve.

		float x,y; //used as accumulators to make our code easier to read

		//Vertex[] vertices = new Vertex[(int)segments]; //Org
		Vertex[] vertices = new Vertex[(int)segments+1]; //FIXME TEST include start point
		vertices[vertices.length-1] = new Vertex(curveStartP);

		detailBias = 1.0 / segments; //we'll put 51 points on out curve (0.02 detail bias)

		int loopCount = 0;
		do{
			double b1 =  count*count;
			double b2 =	(2*count * (1-count)); 
			double b3 = ((1-count) * (1-count));

			x = (float)(curveStartP.getX()*b1 + curveControlP.getX()*b2 + curveEndP.getX()*b3);
			y = (float)(curveStartP.getY()*b1 + curveControlP.getY()*b2 + curveEndP.getY()*b3);

			vertices[loopCount] = new Vertex(x,y,0);

			count += detailBias;
			loopCount++;
		}while( count <= 1);
		vertices = (Vertex[]) Tools3D.reverse(vertices);
		return vertices;
	}


	/**
	 * Calculates/Interpolates the vertices of a bezier curve defined by the startpoint p,
	 * the controlpoints b,b2 and the end point p2.
	 * The segments parameter defines the resolution of the curve
	 * 
	 * @param startX the px0
	 * @param startY the py0
	 * @param controlP1X the bx1
	 * @param controlP1Y the by1
	 * @param controlP2X the b2x2
	 * @param controlP2Y the b2y2
	 * @param endX the p2x3
	 * @param endY the p2y3
	 * @param segments the segments
	 * @return the cubic bezier vertices
	 */
	public static Vertex[] getCubicBezierVertices (
			float startX, 
			float startY, 
			float controlP1X, 
			float controlP1Y,
			float controlP2X, 
			float controlP2Y, 
			float endX,
			float endY, 
			int segments
	)
	{
		//Vertex[] returnArray = new Vertex[segments*2]; //org
		//Vertex[] returnArray = new Vertex[segments]; //last org!
		Vertex[] returnArray = new Vertex[segments+1]; //FIXME test include start point in array
		returnArray[0] = new Vertex(startX,startY,0);

		float lvl = 0.0f;
		float x;
		float y;

		/*
			x = (float)((px0 * (1.0 - lvl) * (1.0 - lvl) * (1.0 - lvl)) +
					(bx1 * 3.0 * lvl * (1.0 - lvl) * (1.0 - lvl)) +
					(b2x2 * 3.0 * lvl * lvl * (1.0 - lvl)) +
					(p2x3 * lvl * lvl * lvl));

			y = (float)((py0 * (1.0 - lvl) * (1.0 - lvl) * (1.0 - lvl)) +
					(by1 * 3.0 * lvl * (1.0 - lvl) * (1.0 - lvl)) +
					(b2y2 * 3.0 * lvl * lvl * (1.0 - lvl)) +
					(p2y3 * lvl * lvl * lvl));

		 */
		for (int i = 0; i < segments; i++) {
			/*
				xx0 = Math.min(xx0, x);
				xx1 = Math.max(xx1, x);
				yy0 = Math.min(yy0, y);
				yy1 = Math.max(yy1, y);
			 */

			//			returnArray[i*2] = new Vertex(x,y,0); //org

			lvl = ((i + 1) / (float) segments);

			x = (float)((startX * (1.0 - lvl) * (1.0 - lvl) * (1.0 - lvl)) +
					(controlP1X * 3.0 * lvl * (1.0 - lvl) * (1.0 - lvl)) +
					(controlP2X * 3.0 * lvl * lvl * (1.0 - lvl)) +
					(endX * lvl * lvl * lvl));

			y = (float)((startY * (1.0 - lvl) * (1.0 - lvl) * (1.0 - lvl)) +
					(controlP1Y * 3.0 * lvl * (1.0 - lvl) * (1.0 - lvl)) +
					(controlP2Y * 3.0 * lvl * lvl * (1.0 - lvl)) +
					(endY * lvl * lvl * lvl));

			/*
				xx0 = Math.min(xx0, x);
				xx1 = Math.max(xx1, x);
				yy0 = Math.min(yy0, y);
				yy1 = Math.max(yy1, y);
			 */

			//returnArray[i] = new Vertex(x,y,0); //org
			returnArray[i+1] = new Vertex(x,y,0); //FIXME TEST

			//returnArray[i*2+1] = new Vertex(x,y,0); //org
			//System.out.println(x + " " + y);
		}
		return returnArray;
	}


	/**
	 * Converts the values of a quadric bezier curve into a cubic.
	 * <br>Returns a BezierVertex with the values of the cubic curve equivalent to
	 * the quadric curve. -> Creates 2 bezier controlpoints out of one.
	 * 
	 * @param bezierStart the bezier start
	 * @param firstQuadControlP the first quad control p
	 * @param quadEndPoint the quad end point
	 * 
	 * @return the cubic from quadratic curve
	 */
	public static BezierVertex getCubicFromQuadraticCurve(Vertex bezierStart, Vertex firstQuadControlP, Vertex quadEndPoint){
		Vertex bezStartCopy = (Vertex)bezierStart.getCopy();
		Vertex firstQuadControlPCopy = (Vertex)firstQuadControlP.getCopy();
		Vertex quadEndPCopy = (Vertex)quadEndPoint.getCopy();
		
		Vertex tmp1 = (Vertex)firstQuadControlPCopy.getSubtracted(bezStartCopy);
		tmp1.scaleLocal(2/3);
		Vertex cp1 = (Vertex)bezStartCopy.getAdded(tmp1);
		
		Vertex tmp2 = (Vertex)quadEndPCopy.getSubtracted(firstQuadControlP);
		tmp2.scaleLocal(1/3);
		Vertex cp2 = (Vertex)firstQuadControlP.getAdded(tmp1);
		return new BezierVertex(cp1.getX(), cp1.getY(),0, cp2.getX(), cp2.getY(),0, quadEndPCopy.getX(), quadEndPCopy.getY(), 0);
	}



	/**
	 * Returns an arraylist of vertices that form the arc, built from the
	 * supplied parameters.
	 * 
	 * @param fromX1 curve startpointX
	 * @param fromX2 curve startpointY
	 * @param rx the x radius
	 * @param ry the y radius
	 * @param phi the phi
	 * @param large_arc the large_arc
	 * @param sweep the sweep
	 * @param x the x
	 * @param y the y
	 * @param segments the resolution of the curve, more segments -> smoother curve -> less performance
	 * 
	 * @return the list< vertex>
	 */
	public static List<Vertex> arcTo(float fromX1, float fromX2, float rx, float ry, float phi, boolean large_arc, boolean sweep, float x , float y, int segments){
		ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
		int circle_points = segments;
		//current point
		float x1 = fromX1; 
		float y1 = fromX2; 
		float x2 = x;
		float y2 = y;
		float cp = (float) Math.cos(Math.toRadians(phi));
		float sp = (float) Math.sin(Math.toRadians(phi));
		//	    float cp = (float) Math.cos(phi);
		//	    float sp = (float) Math.sin(phi);

		float dx = .5f * (x1 - x2);
		float dy = .5f * (y1 - y2);

		float x_ = cp * dx + sp * dy;
		float y_ = -sp * dx + cp * dy;

		float zaehler = (((rx*rx) * (ry*ry)) - ((rx*rx) * (y_*y_)) - ((ry*ry) * (x_*x_)));
		if (zaehler < 0){
			zaehler *=-1;
		}
		//	    float r = (float)Math.sqrt( ( -1*(((rx*rx) * (ry*ry)) - ((rx*rx) * (y_*y_)) - ((ry*ry) * (x_*x_))) ) /
		//	                      (((rx*rx) * (y_*y_)) + ((ry*ry) * (x_*x_))));
		float r = (float)Math.sqrt( (zaehler ) /
				(((rx*rx) * (y_*y_)) + ((ry*ry) * (x_*x_))));

		/*
		    System.out.println(((rx*rx) * (ry*ry)) - ((rx*rx) * (y_*y_)) - ((ry*ry) * (x_*x_)) );
		    System.out.println(r);   
		 */

		//FIXME why does this often help? but not in all cases
		//	    if (phi>=0){
		//	    	large_arc = !large_arc;
		//	    }

		//Orgininal
		//	    if (large_arc != sweep){
		//	    	r =-r;
		//	    }   

		if (large_arc != sweep){
			//	    	r = Math.abs(r);
			r =+r;
		}else if (large_arc == sweep){
			r = -r;
		}


		float cx_ = (r * rx * y_) / ry;
		float cy_ = (-r * ry * x_) / rx;
		float cx = cp * cx_ - sp * cy_ + .5f * (x1 + x2);
		float cy = sp * cx_ + cp * cy_ + .5f * (y1 + y2);

		float psi = ToolsGeometry.angle(new float[]{1,0}, new float[]{(x_ - cx_)/rx, (y_ - cy_)/ry});

		float delta = ToolsGeometry.angle( new float[]{(x_ - cx_)/rx , (y_ - cy_)/ry} , new float[]{(-x_ - cx_)/rx, (-y_ - cy_)/ry});

		if (sweep && delta < 0){
			delta += Math.PI * 2;
		}
		if (!sweep && delta > 0){
			delta -= Math.PI * 2;
		}

		//	    float n_points = max( int( abs(circle_points * delta / (2 * Math.PI))), 1);
		float n_points = Math.max((int)( Math.abs(circle_points * delta / (2 * Math.PI))), 1);

		//Add curve startpoint
		//	    vertexList.add(new Vertex(x1,y2,0));
		//Add the rest
		for (int i = 0; i < n_points+1; i++) {
			float theta = psi + i * delta / n_points;
			float ct = (float) Math.cos(theta);
			float st = (float) Math.sin(theta);

			float newX = cp * rx * ct - sp * ry * st + cx;
			float newY = sp * rx * ct + cp * ry * st + cy;
			float newZ = 0;

			/*
	            //This prevents adding the same vertex as startpoint (fromx, fromY)
	            if ((!vertexList.isEmpty() && (vertexList.get(vertexList.size()-1).x == newX && vertexList.get(vertexList.size()-1).y == newY))
	            ){
	            	//System.out.println("Same vertex, not using it.");
	            }else if(vertexList.isEmpty() && (fromX1 == newX && fromX2 == newY)  ){
	            	//System.out.println("Same vertex as (fromX, fromY), and list empty, not using it.");
	            }else{
	            	 vertexList.add(new Vertex(newX, newY , newZ));
	            }
			 */

			//            /*
			vertexList.add(new Vertex(newX, newY , newZ));
			//            /*

			//            vertexList.add(new Vertex(cp * rx * ct - sp * ry * st + cx, sp * rx * ct + cp * ry * st + cy , 0));
			//            System.out.println(vertexList.get(vertexList.size()-1));
		}
		return vertexList;
	}


	/**
	 * Method is used by the method to draw an arc (arcTo()).
	 * 
	 * @param u the u
	 * @param v the v
	 * 
	 * @return the float
	 */
	private static float angle(float[] u, float[] v){
		float a = (float)Math.acos( (u[0]*v[0] + u[1]*v[1]) / (float)Math.sqrt((u[0]*u[0] + u[1]*u[1]) * (v[0]*v[0] + v[1]*v[1]))) ;
		float sgn = (u[0]*v[1] > u[1]*v[0])? 1 : -1;
		return sgn * a;
	}



	/**
	 * Calculates the 2D XY convex hull for this shape.
	 * 
	 * @return the convex hull xy global
	 */
	public Vector3D[] getConvexHullXYGlobal(AbstractShape shape){
		ArrayList<Vector3D> vers = new ArrayList<Vector3D>();
		Vertex[] transVerts = shape.getVerticesGlobal();
        for (Vertex vertex : transVerts) {
            vers.add(vertex);
        }
		ArrayList<Vector3D> edgeList = ConvexQuickHull2D.getConvexHull2D(vers);
		return (edgeList.toArray(new Vector3D[edgeList.size()]));
	}
	
	/**
	 * @param vects The Vector3D[] to be converted to a Vertex[]
	 * @return a Vertex[] created from the given Vector3D[]
	 */
	public static Vertex[] toVertices(Vector3D[] vects){
		Vertex[] result = new Vertex[vects.length];
		int length = result.length;
		for (int i = 0; i < length; i++) {
			result[i] = new Vertex(vects[i]);
		}
		return result;
	}

}

