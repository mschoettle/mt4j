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
package org.mt4j.util.camera;

import org.mt4j.util.math.Vector3D;

/**
 * The Interface IFrustum.
 * @author Christopher Ruff
 */
public interface IFrustum {
	
	/** The Constant OUTSIDE. */
	public static final int OUTSIDE 	= 0;
	
	/** The Constant INTERSECT. */
	public static final int INTERSECT 	= 1;
	
	/** The Constant INSIDE. */
	public static final int INSIDE 		= 2;
	
	/**
	 * Checks if sphere is in frustum.
	 * 
	 * @param p the p
	 * @param radius the radius
	 * 
	 * @return the int
	 */
	public int isSphereInFrustum(Vector3D p, float radius);
	
	/**
	 * Checks if point is in frustum.
	 * 
	 * @param p the p
	 * 
	 * @return the int
	 */
	public int isPointInFrustum(Vector3D p) ;
	
	/**
	 * Returns the height of the near plane
	 * @return the float
	 */
	public float getHeightOfNearPlane();
	
	/**
	 * Returns the width of the near plane
	 * @return the float
	 */
	public float getWidthOfNearPlane();
	
	/**
	 * Returns the top left point of the near plane
	 * @return the Vector3D
	 */
	public Vector3D getNearTopLeft();
	
	/**
	 * Returns the width of the plane at a specific z value
	 * @return the float
	 */
	public float getWidthOfPlane(float z);
	
	/**
	 * Returns the height of the plane at a specific z value
	 * @return the float
	 */
	public float getHeightOfPlane(float z);
	
	/**
	 * Returns the z value of the near plane
	 * @return the float
	 */
	public float getZValueOfNearPlane();
	

}
