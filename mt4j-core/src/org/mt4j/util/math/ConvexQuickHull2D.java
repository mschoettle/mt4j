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

import java.util.ArrayList;

/**
 * The Class ConvexQuickHull2D.
 */
public class ConvexQuickHull2D{
 
  /**
   * Gets the convex hull2 d.
   * 
   * @param Vector3Ds the vector3 ds
   * 
   * @return the convex hull2 d
   */
  @SuppressWarnings("unchecked")
public static ArrayList<Vector3D> getConvexHull2D(ArrayList<Vector3D> Vector3Ds) {
    ArrayList<Vector3D> convexHull = new ArrayList<Vector3D>();
    if (Vector3Ds.size() < 3) return (ArrayList<Vector3D>) Vector3Ds.clone();
    // find extremals
    int minVector3D = -1, maxVector3D = -1;
    float minX = Float.POSITIVE_INFINITY;
    float maxX = Float.NEGATIVE_INFINITY;
    for (int i = 0; i < Vector3Ds.size(); i++) {
      if (Vector3Ds.get(i).getX() < minX) {
        minX = Vector3Ds.get(i).getX();
        minVector3D = i;
      } 
      if (Vector3Ds.get(i).getX() > maxX) {
        maxX = Vector3Ds.get(i).getX();
        maxVector3D = i;       
      }
    }
    Vector3D A = Vector3Ds.get(minVector3D);
    Vector3D B = Vector3Ds.get(maxVector3D);
    convexHull.add(A);
    convexHull.add(B);
    Vector3Ds.remove(A);
    Vector3Ds.remove(B);
    
    ArrayList<Vector3D> leftSet = new ArrayList<Vector3D>();
    ArrayList<Vector3D> rightSet = new ArrayList<Vector3D>();

      for (Vector3D p : Vector3Ds) {
          if (Vector3DLocation(A, B, p) == -1)
              leftSet.add(p);
          else
              rightSet.add(p);
      }
    hullSet(A,B,rightSet,convexHull);
    hullSet(B,A,leftSet,convexHull);
    
    return convexHull;
  }
  
  /*
   * Computes the square of the distance of Vector3D C to the segment defined by Vector3Ds AB
   */
  /**
   * Distance.
   * 
   * @param A the a
   * @param B the b
   * @param C the c
   * 
   * @return the float
   */
  private static float distance(Vector3D A, Vector3D B, Vector3D C) {
    float ABx = B.getX()-A.getX();
    float ABy = B.getY()-A.getY();
    float num = ABx*(A.getY()-C.getY())-ABy*(A.getX()-C.getX());
    if (num < 0) num = -num;
    return num;
  }
  
  /**
   * Hull set.
   * 
   * @param A the a
   * @param B the b
   * @param set the set
   * @param hull the hull
   */
  private static void hullSet(Vector3D A, Vector3D B, ArrayList<Vector3D> set, ArrayList<Vector3D> hull) {
    int insertPosition = hull.indexOf(B);
    if (set.size() == 0) return;
    if (set.size() == 1) {
      Vector3D p = set.get(0);
      set.remove(p);
      hull.add(insertPosition,p);
      return;
    }
    float dist = Float.NEGATIVE_INFINITY;
    int furthestVector3D = -1;
    for (int i = 0; i < set.size(); i++) {
      Vector3D p = set.get(i);
      float distance  = distance(A,B,p);
      if (distance > dist) {
        dist = distance;
        furthestVector3D = i;
      }
    }
    Vector3D P = set.get(furthestVector3D);
    set.remove(furthestVector3D);
    hull.add(insertPosition,P);
    
    // Determine who's to the left of AP
    ArrayList<Vector3D> leftSetAP = new ArrayList<Vector3D>();
      for (Vector3D M : set) {
          if (Vector3DLocation(A, P, M) == 1) {
              leftSetAP.add(M);
          }
      }
    
    // Determine who's to the left of PB
    ArrayList<Vector3D> leftSetPB = new ArrayList<Vector3D>();
      for (Vector3D M : set) {
          if (Vector3DLocation(P, B, M) == 1) {
              leftSetPB.add(M);
          }
      }
    hullSet(A,P,leftSetAP,hull);
    hullSet(P,B,leftSetPB,hull);
    
  }

  /**
   * Vector3 d location.
   * 
   * @param A the a
   * @param B the b
   * @param P the p
   * 
   * @return the float
   */
  private static float Vector3DLocation(Vector3D A, Vector3D B, Vector3D P) {
    float cp1 = (B.getX()-A.getX())*(P.getY()-A.getY()) - (B.getY()-A.getY())*(P.getX()-A.getX());
    return (cp1>0)?1:-1;
  }
  
}
