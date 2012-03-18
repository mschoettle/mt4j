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

import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

/**
 * A triangle class used in meshes.
 * 
 * @author Christopher Ruff
 */
public class Triangle {
	
	/** The v0. */
	public Vertex v0; 
	
	/** The v1. */
	public Vertex v1; 
	
	/** The v2. */
	public Vertex v2;
	
	//Pointers into the vertex array
	/** The P0. */
	public int P0;
	
	/** The P1. */
	public int P1;
	
	/** The P2. */
	public int P2;
	
	/** The normal. */
	private Vector3D normal;
	
	/** The normal unnormalized. */
	private Vector3D normalUnnormalized;
	
	/** The center. */
	private Vector3D center;
	
	/** The normal dirty. */
	private boolean normalDirty;
	
	/** The center dirty. */
	private boolean centerDirty;

	
//	private boolean useLocalObjectSpace;
	
	
	/**
 * The Constructor.
 * 
 * @param v0 the v0
 * @param v1 the v1
 * @param v2 the v2
 * @param p0 the index p0
 * @param p1 the index p1
 * @param p2 the index p2
 */
	public Triangle(Vertex v0, Vertex v1, Vertex v2, int p0, int p1, int p2) {
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
		
		this.P0 = p0;
		this.P1 = p1;
		this.P2 = p2;
		
		this.normalDirty = true;
//		this.normal		 = this.getNormalLocal(); 
		this.normal		 = null; //lazily initialize them to save memory
		
		this.centerDirty = true;
//		this.center 	 = this.getCenterPointLocal();
		this.center 	 = null;
		
//		this.useLocalObjectSpace = true;
	}
	
	/**
	 * Sets the.
	 * 
	 * @param v0 the v0
	 * @param v1 the v1
	 * @param v2 the v2
	 */
	public void set(Vertex v0, Vertex v1, Vertex v2){
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
		this.normalDirty = true;
		this.centerDirty = true;
	}
	
	/**
	 * Gets the normal obj space.
	 * 
	 * @return the normal obj space
	 * 
	 * the normal vector of the plane of the triangle
	 */
	public Vector3D getNormalLocal(){
		this.calcNormal();
		return this.normal;
	}
	
	/**
	 * Calc normal.
	 */
	private void calcNormal(){
		if (normalDirty || this.normal == null || this.normalUnnormalized == null){
			this.normal = ToolsGeometry.getNormal(v0, v1, v2, false);
			
			//Get unnormalized normal for triangle intersection tests (So degenerate tris get detected)
			this.normalUnnormalized = normal.getCopy();
			
			this.normal.normalizeLocal();
			normalDirty = false;
		}
	}

	/**
	 * Gets the center point local.
	 * 
	 * @return the center point local
	 */
	public Vector3D getCenterPointLocal(){
		this.calcCenterLocal();
		return this.center;
	}

	/**
	 * Calc center local.
	 */
	private void calcCenterLocal(){
		if (centerDirty || center == null){
			center = this.v0.getCopy();
			center.addLocal(v1);
			center.addLocal(v2);
			center.scaleLocal(ToolsMath.ONE_THIRD);
			centerDirty = false;
		}
	}


	/**
	 * Tests if the given ray intersects this triangle.
	 * Returns the intersection point or null if there is none.
	 * 
	 * @param r the r
	 * 
	 * @return the ray triangle intersection
	 * 
	 * intersection vector
	 */
	public Vector3D getRayTriangleIntersection(Ray r){
		//Update unnormalized normal if neccessary
		this.calcNormal();
		return ToolsGeometry.getRayTriangleIntersection(r, v0, v1, v2, this.normalUnnormalized);
	}
	
	
	/**
	 * Checks if point vector is inside the triangle created by the points a, b
	 * and c. These points will create a plane and the point checked will have
	 * to be on this plane in the region between a,b,c.
	 * 
	 * Note: The triangle must be defined in clockwise order a,b,c
	 * 
	 * @param p the p
	 * 
	 * @return true, if point is in triangle.
	 */
	public boolean containsPoint(Vector3D p) {
		Vector3D a = p.getSubtracted(v0);
		a.normalizeLocal();
		Vector3D b = p.getSubtracted(v1);
		b.normalizeLocal();
		Vector3D c = p.getSubtracted(v2);
		c.normalizeLocal();

		double total_angles = Math.acos(a.dot(b));
		total_angles += Math.acos(b.dot(c));
		total_angles += Math.acos(c.dot(a));

		return (ToolsMath.abs((float) total_angles - ToolsMath.TWO_PI) <= 0.005f);
	}


	/**
	 * Finds and returns the closest point on any of the triangle edges to the
	 * point given.
	 * 
	 * @param p point to check
	 * 
	 * @return closest point
	 */
	public Vector3D getClosestVertexTo(Vector3D p) {
		Vector3D Rab = getClosestVecToVecOnSegment(p, v0, v1);
		Vector3D Rbc = getClosestVecToVecOnSegment(p, v1, v2);
		Vector3D Rca = getClosestVecToVecOnSegment(p, v2, v0);

		float dAB = p.getSubtracted(Rab).lengthSquared();
		float dBC = p.getSubtracted(Rbc).lengthSquared();
		float dCA = p.getSubtracted(Rca).lengthSquared();

		float min = dAB;
		Vector3D result = Rab;

		if (dBC < min) {
			min = dBC;
			result = Rbc;
		}

		if (dCA < min){
			result = Rca;
		}
		return result;
	}


	/**
	 * Computes the the point on the surface of
	 * triangle closest to the given vector.
	 * 
	 * From Real-Time Collision Detection by Christer Ericson, published by
	 * Morgan Kaufmann Publishers, Copyright 2005 Elsevier Inc
	 * 
	 * @param p the p
	 * 
	 * @return closest point on triangle (result may also be one of v0, v1 or v2)
	 */
	public Vector3D getClosestPointOnSurface(Vector3D p) {
		Vector3D ab = v1.getSubtracted(v0);
		Vector3D ac = v2.getSubtracted(v0);
		Vector3D bc = v2.getSubtracted(v1);

		Vector3D pa = p.getSubtracted(v0);
		Vector3D pb = p.getSubtracted(v1);
		Vector3D pc = p.getSubtracted(v2);

		Vector3D ap = v0.getSubtracted(p);
		Vector3D bp = v1.getSubtracted(p);
		Vector3D cp = v2.getSubtracted(p);

		// Compute parametric position s for projection P' of P on AB,
		// P' = A + s*AB, s = snom/(snom+sdenom)
		float snom = pa.dot(ab);
		float sdenom = pb.dot(v0.getSubtracted(v1));

		// Compute parametric position t for projection P' of P on AC,
		// P' = A + t*AC, s = tnom/(tnom+tdenom)
		float tnom = pa.dot(ac);
		float tdenom = pc.dot(v0.getSubtracted(v2));

		if (snom <= 0.0f && tnom <= 0.0f)
			return v0; // Vertex region early out

		// Compute parametric position u for projection P' of P on BC,
		// P' = B + u*BC, u = unom/(unom+udenom)
		float unom = pb.dot(bc);
		float udenom = pc.dot(v1.getSubtracted(v2));

		if (sdenom <= 0.0f && unom <= 0.0f)
			return v1; // Vertex region early out
		if (tdenom <= 0.0f && udenom <= 0.0f)
			return v2; // Vertex region early out

		// P is outside (or on) AB if the triple scalar product [N PA PB] <= 0
		Vector3D n = ab.getCross(ac);
		float vc = n.dot(ap.crossLocal(bp));

		// If P outside AB and within feature region of AB,
		// return projection of P onto AB
		if (vc <= 0.0f && snom >= 0.0f && sdenom >= 0.0f) {
			// return a + snom / (snom + sdenom) * ab;
			return v0.getAdded(ab.scaleLocal(snom / (snom + sdenom)));
		}

		// P is outside (or on) BC if the triple scalar product [N PB PC] <= 0
		float va = n.dot(bp.crossLocal(cp));
		// If P outside BC and within feature region of BC,
		// return projection of P onto BC
		if (va <= 0.0f && unom >= 0.0f && udenom >= 0.0f) {
			// return b + unom / (unom + udenom) * bc;
			return v1.getAdded(bc.scaleLocal(unom / (unom + udenom)));
		}

		// P is outside (or on) CA if the triple scalar product [N PC PA] <= 0
		float vb = n.dot(cp.crossLocal(ap));
		// If P outside CA and within feature region of CA,
		// return projection of P onto CA
		if (vb <= 0.0f && tnom >= 0.0f && tdenom >= 0.0f) {
			// return a + tnom / (tnom + tdenom) * ac;
			return v0.getAdded(ac.scaleLocal(tnom / (tnom + tdenom)));
		}

		// P must project inside face region. Compute Q using barycentric
		// coordinates
		float u = va / (va + vb + vc);
		float v = vb / (va + vb + vc);
		float w = 1.0f - u - v; // = vc / (va + vb + vc)
		// return u * a + v * b + w * c;
		return v0.getScaled(u).addLocal(v1.getScaled(v)).addLocal(v2.getScaled(w));
	}



    /**
     * Checks if is clockwise in xy.
     * 
     * @return true, if is clockwise in xy
     */
    public boolean isClockwiseInXY() {
    	return Triangle.isClockwiseInXY(v0, v1, v2);
    }

    /**
     * Checks if is clockwise in xz.
     * 
     * @return true, if is clockwise in xz
     */
    public boolean isClockwiseInXZ() {
    	return Triangle.isClockwiseInXY(v0, v1, v2);
    }

    /**
     * Checks if is clockwise in yz.
     * 
     * @return true, if is clockwise in yz
     */
    public boolean isClockwiseInYZ() {
    	return Triangle.isClockwiseInXY(v0, v1, v2);
    }

    /**
     * Checks if is clockwise in xy.
     * 
     * @param v0 the v0
     * @param v1 the v1
     * @param v2 the v2
     * 
     * @return true, if is clockwise in xy
     */
    public static boolean isClockwiseInXY(Vector3D v0, Vector3D v1, Vector3D v2) {
    	float determ = (v1.x - v0.x) * (v2.y - v0.y) - (v2.x - v0.x) * (v1.y - v0.y);
    	return (determ < 0.0);
    }

    /**
     * Checks if is clockwise in xz.
     * 
     * @param v0 the v0
     * @param v1 the v1
     * @param v2 the v2
     * 
     * @return true, if is clockwise in xz
     */
    public static boolean isClockwiseInXZ(Vector3D v0, Vector3D v1, Vector3D v2) {
    	float determ = (v1.x - v0.x) * (v2.z - v0.z) - (v2.x - v0.x) * (v1.z - v0.z);
    	return (determ < 0.0);
    }


    /**
     * Checks if is clockwise in yz.
     * 
     * @param v0 the v0
     * @param v1 the v1
     * @param v2 the v2
     * 
     * @return true, if is clockwise in yz
     */
    public static boolean isClockwiseInYZ(Vector3D v0, Vector3D v1, Vector3D v2) {
    	float determ = (v1.y - v0.y) * (v2.z - v0.z) - (v2.y - v0.y) * (v1.z - v0.z);
    	return (determ < 0.0);
    }

    
    /**
	 * Gets the closest vec to vec on segment.
	 * 
	 * @param p the point to find the closest point on the segment for
	 * @param segmentStart start point of line segment
	 * @param segmentEnd end point of line segment
	 * 
	 * @return closest point on the line segment a -> b
	 */
	private Vector3D getClosestVecToVecOnSegment(Vector3D p, Vector3D segmentStart, Vector3D segmentEnd) {
	        Vector3D c = p.getSubtracted(segmentStart);
	        Vector3D v = segmentEnd.getSubtracted(segmentStart);
	
	        float d = v.length();
	        v.normalizeLocal();
	
	        float t = v.dot(c);
	
	        // Check to see if t is beyond the extents of the line segment
	        if (t < 0.0f) {
	                return segmentStart;
	        }
	        if (t > d) {
	                return segmentEnd;
	        }
	
	        // Return the point between 'a' and 'b'
	        // set length of V to t. V is normalized so this is easy
	        v.scaleLocal(t);
	        return segmentStart.getAdded(v);
	}
	

    /* code rewritten to do tests on the sign of the determinant */
	/* the division is before the test of the sign of the det    */
//	int intersect_triangle2(
//			double orig[3], 
//			double dir[3],
//			double vert0[3], 
//			double vert1[3], 
//			double vert2[3],
//			double *t, 
//			double *u, 
//			double *v
//	){
//	   double edge1[3], edge2[3], tvec[3], pvec[3], qvec[3];
//	   double det,inv_det;
//
//	   /* find vectors for two edges sharing vert0 */
//	   SUB(edge1, vert1, vert0);
//	   SUB(edge2, vert2, vert0);
//
//	   /* begin calculating determinant - also used to calculate U parameter */
//	   CROSS(pvec, dir, edge2);
//
//	   /* if determinant is near zero, ray lies in plane of triangle */
//	   det = DOT(edge1, pvec);
//
//	   /* calculate distance from vert0 to ray origin */
//	   SUB(tvec, orig, vert0);
//	   inv_det = 1.0 / det;
//	   
//	   if (det > EPSILON)
//	   {
//	      /* calculate U parameter and test bounds */
//	      *u = DOT(tvec, pvec);
//	      if (*u < 0.0 || *u > det)
//		 return 0;
//	      
//	      /* prepare to test V parameter */
//	      CROSS(qvec, tvec, edge1);
//	      
//	      /* calculate V parameter and test bounds */
//	      *v = DOT(dir, qvec);
//	      if (*v < 0.0 || *u + *v > det)
//		 return 0;
//	      
//	   }
//	   else if(det < -EPSILON)
//	   {
//	      /* calculate U parameter and test bounds */
//	      *u = DOT(tvec, pvec);
//	      if (*u > 0.0 || *u < det)
//		 return 0;
//	      
//	      /* prepare to test V parameter */
//	      CROSS(qvec, tvec, edge1);
//	      
//	      /* calculate V parameter and test bounds */
//	      *v = DOT(dir, qvec) ;
//	      if (*v > 0.0 || *u + *v < det)
//		 return 0;
//	   }
//	   else return 0;  /* ray is parallell to the plane of the triangle */
//
//	   /* calculate t, ray intersects triangle */
//	   *t = DOT(edge2, qvec) * inv_det;
//	   (*u) *= inv_det;
//	   (*v) *= inv_det;
//
//	   return 1;
//	}


	
	
	

}
