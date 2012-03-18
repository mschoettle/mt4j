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
package org.mt4j.components.bounds;

import org.mt4j.components.TransformSpace;
import org.mt4j.util.camera.IFrustum;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Vector3D;


/**
 * The Interface IBoundingShape.
 * @author Christopher Ruff
 */
public interface IBoundingShape {
	
	/**
	 * Gets the intersection point.
	 * The ray is assumed to be in local space.
	 * 
	 * @param ray the ray
	 * 
	 * @return the intersection point
	 */
	public Vector3D getIntersectionLocal(Ray ray);
	
	/**
	 * Checks if the bounding shape contains the point.
	 * The point is assumed to be in local space.
	 * 
	 * @param point the point
	 * 
	 * @return true, if successful
	 */
	public boolean containsPointLocal(Vector3D point);
	
	
	/**
	 * Informs the bounding shape that the world bounds changed.
	 * (i.e. when the corresponding shape was transformed)
	 */
	public void setGlobalBoundsChanged();
	
	
	/**
	 * Gets the center point world.
	 * @return the center point world
	 */
	public Vector3D getCenterPointGlobal();
	
	/**
	 * Gets the center point obj space.
	 * 
	 * @return the center point obj space
	 */
	public Vector3D getCenterPointLocal();
	
	/**
	 * Gets the vectors obj space.
	 * 
	 * @return the vectors, the bounding shape is made of - in object space coordinates
	 */
	public Vector3D[] getVectorsLocal();

	/**
	 * Gets the vectors world.
	 * 
	 * @return the vectors, the bounding shape is made of - in world space coordinates
	 */
	public Vector3D[] getVectorsGlobal();
	
	/**
	 * Get the height of the shape in the XY-Plane. Uses the x and y coordinate
	 * values for calculation.
	 * 
	 * @param transformSpace the space the width is calculated in, can be world space, parent relative- or object space
	 * 
	 * @return the height xy
	 * 
	 * the height
	 */
	public float getHeightXY(TransformSpace transformSpace) ;
	
	
	/**
	 * Get the width of the shape in the XY-Plane. Uses the x and y coordinate
	 * values for calculation.
	 * 
	 * @param transformSpace the space the width is calculated in, can be world space, parent relative- or object space
	 * 
	 * @return the width xy
	 * 
	 * the width
	 */
	public float getWidthXY(TransformSpace transformSpace);
	
	/**
	 * Gets the width xy vect obj space.
	 * 
	 * @return the width xy vect obj space
	 * 
	 * the vector that has the length of the obj space width of the component
	 */
	public Vector3D getWidthXYVectLocal();
	
	/**
	 * Gets the height xy vect obj space.
	 * 
	 * @return the height xy vect obj space
	 * 
	 * the vector that has the length of the obj space height of the component
	 */
	public Vector3D getHeightXYVectLocal();
	
	
	//update(Matrix) //getTransformedCoords(Matrix)
	
	public boolean isContainedInFrustum(IFrustum frustum);
	

	
}
