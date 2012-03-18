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

/**
 * The Class Plane.
 */
public class Plane {

	 /** The Constant XY. */
 	public static final Plane XY = new Plane(new Vector3D(), Vector3D.Z_AXIS);
     
     /** The Constant XZ. */
     public static final Plane XZ = new Plane(new Vector3D(), Vector3D.Y_AXIS);
     
     /** The Constant YZ. */
     public static final Plane YZ = new Plane(new Vector3D(), Vector3D.X_AXIS);

     public static final int PLANE_FRONT = -1;

     public static final int PLANE_BACK = 1;

     public static final int ON_PLANE = 0;

     /** The Normal of the plane. */
     public Vector3D normal;
     
     /** The origin of the plane (a point in the plane). */
     public Vector3D origin;

     /**
      * The Constructor.
      * 
      * @param origin the origin
      * @param norm the norm
      */
     public Plane(Vector3D origin, Vector3D norm) {
    	 this.origin = origin;
         this.normal = norm.getNormalized();
     }
     
     /**
      * The Constructor.
      * 
      * @param v0 the v0
      * @param v1 the v1
      * @param v2 the v2
      */
     public Plane(Vector3D v0, Vector3D v1, Vector3D v2) {
    	 this.normal = ToolsGeometry.getNormal(v0, v1, v2, true);
    	 this.origin = v0;
     }

     public void reconstruct(Vector3D v0, Vector3D v1, Vector3D v2) {
    	 this.normal = ToolsGeometry.getNormal(v0, v1, v2, true);
    	 this.origin = v0;
     }
     
     public void reconstruct(Vector3D origin, Vector3D norm) {
    	 this.origin = origin;
         this.normal = norm.getNormalized();
     }
     
//TODO transform methods?
     
     /**
      * Calculates distance from the plane to point P.
      * 
      * @param p the p
      * 
      * @return distance
      */
     public float getDistanceToPoint(Vector3D p) {
    	 float sn = - normal.dot(p.getSubtracted(this.origin));
    	 float sd = normal.lengthSquared();
    	 Vector3D isec = p.getAdded(normal.getScaled(sn / sd));
    	 return Vector3D.distance(isec, p);
     }

     /**
      * Calculates the intersection point between plane and ray (line).
      * 
      * @param r the r
      * 
      * @return intersection point or null if ray doesn't intersect plane
      */
     public Vector3D getIntersectionLocal(Ray r) {
//             float denom = normal.dot(r.getRayDirectionNormalized());
//             if (denom > FastMath.FLT_EPSILON) {
//                     float u = normal.dot(this.origin.getSubtracted(r.getRayStartPoint())) / denom;
//                     Vector3D p =  r.getRayStartPoint().getAdded(r.getRayDirectionNormalized().getScaled(u));  
//                     return p;
//             } else
//                     return null;
             return ToolsGeometry.getRayPlaneIntersection(r, this.normal, this.origin);
     }

     /**
      * Classifies the relative position of the given point to the plane.
      * 
      * @param p the p
      * 
      * @return One of the 3 integer classification codes: PLANE_FRONT, PLANE_BACK, ON_PLANE
      */
     public int classifyPoint(Vector3D p) {
             float d = this.origin.getSubtracted(p).dot(normal);
             if (d < - ToolsMath.FLT_EPSILON)
                     return PLANE_FRONT;
             else if (d > ToolsMath.FLT_EPSILON)
                     return PLANE_BACK;
             return ON_PLANE;
     }
     
     /**
      * Component contains point local.
      * 
      * @param testPoint the test point
      * 
      * @return true, if successful
      */
     public boolean componentContainsPointLocal(Vector3D testPoint) {
    	 return this.classifyPoint(testPoint) == ON_PLANE;
     }

     
     /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
             StringBuffer sb = new StringBuffer();
             sb.append("origin: ").append(super.toString()).append(" norm: ").append(normal.toString());
             return sb.toString();
     }

	
}
