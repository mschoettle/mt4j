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
 * The Class Ray.
 * 
 * @author C.Ruff
 */
public class Ray {
	
	/** The ray start point. */
	private Vector3D rayStartPoint;
	
	/** The point in ray direction. */
	private Vector3D pointInRayDirection;
	
	/** The ray direction. */
	private Vector3D rayDirection;
	
	
	/**
	 * Instantiates a new ray.
	 * 
	 * @param ray the ray
	 */
	public Ray(Ray ray){
		super();
		this.rayStartPoint 			= ray.getRayStartPoint().getCopy();
		this.pointInRayDirection 	= ray.getPointInRayDirection().getCopy();
	}
	
	/**
	 * Instantiates a new ray.
	 * 
	 * @param rayStartPoint the ray start point
	 * @param pointInRayDirection the point in ray direction
	 */
	public Ray(Vector3D rayStartPoint, Vector3D pointInRayDirection) {
		super();
		this.rayStartPoint 			= rayStartPoint;
		this.pointInRayDirection 	= pointInRayDirection;
	}
	
	
	/**
	 * Gets the ray direction.
	 * 
	 * @return the ray direction
	 */
	public Vector3D getRayDirection(){
		return pointInRayDirection.getSubtracted(rayStartPoint);
	}
	
	/**
	 * Gets the ray direction normalized.
	 * 
	 * @return the ray direction normalized
	 */
	public Vector3D getRayDirectionNormalized(){
		return getRayDirection().normalizeLocal();
	}

	/**
	 * Gets the point in ray direction.
	 * 
	 * @return the point in ray direction
	 */
	public Vector3D getPointInRayDirection() {
		return pointInRayDirection;
	}

	/**
	 * Sets the point in ray direction.
	 * 
	 * @param pointInRayDirection the new point in ray direction
	 */
	public void setPointInRayDirection(Vector3D pointInRayDirection) {
		this.pointInRayDirection = pointInRayDirection;
	}

	/**
	 * Gets the ray start point.
	 * 
	 * @return the ray start point
	 */
	public Vector3D getRayStartPoint() {
		return rayStartPoint;
	}

	/**
	 * Sets the ray start point.
	 * 
	 * @param rayStartPoint the new ray start point
	 */
	public void setRayStartPoint(Vector3D rayStartPoint) {
		this.rayStartPoint = rayStartPoint;
	}
	
	/**
	 * Transforms the ray.
	 * The direction vector is multiplied with the transpose of the matrix.
	 * 
	 * @param m the m
	 */
	public void transform(Matrix m){
		rayStartPoint.transform(m);
		
//		pointInRayDirection.transformNormal(m);
//		pointInRayDirection.normalize(); //FIXME TRIAL REMOVE? NO oder vielleicht gleich bei transformNOrmal mit rein?
//		
//		pointInRayDirection = rayStartPoint.plus(pointInRayDirection);
		
		pointInRayDirection.transform(m);
	}
	
	/**
	 * Calculates and returns the direction vector.
	 * This is calced by subtracting the rayStartpoint from the point in the rays direction.
	 * 
	 * @return the direction
	 */
	public Vector3D getDirection(){
		return pointInRayDirection.getSubtracted(rayStartPoint);
	}
	
	/**
	 * Returns a new ray, transformed by the matrix.
	 * 
	 * @param ray the ray
	 * @param m the m
	 * 
	 * @return the transformed ray
	 */
	public static Ray getTransformedRay(Ray ray, Matrix m){
//		if (!Matrix.equalIdentity(m)){
			//Get a copy of the origonal ray
			Ray transformedRay = new Ray(ray);
			transformedRay.transform(m);
			return transformedRay;
//		}else{
//			return ray;
//		}
	}
	
	@Override
	public String toString(){
		return "Ray start: " + this.rayStartPoint + " PointInRayDirection: " + this.pointInRayDirection + " " + super.toString();
	}

}
