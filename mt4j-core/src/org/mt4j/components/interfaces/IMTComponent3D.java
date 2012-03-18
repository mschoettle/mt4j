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
package org.mt4j.components.interfaces;

import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Vector3D;


/**
 * The Interface IMTComponent3D.
 * @author Christopher Ruff
 */
public interface IMTComponent3D extends IMTComponent{
	
	
	/**
	 * Checks if the specified ray intersects this component <b>or one if its childcomponents</b>.
	 * The ray is assumed to be defined in global coordinates.
	 * 
	 * @param ray global ray used for intersecting. 
	 * 
	 * @return the intersection point in global coordinates
	 * 
	 * the intersection point or null when no intersection occured
	 */
	public Vector3D getIntersectionGlobal(Ray ray);

	/**
	 * Rotates the component around its y-axis and the rotation point (in global coordiantes).
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public void rotateYGlobal(Vector3D rotationPoint, float degree);
	
	/**
	 * Rotates the component around its x-axis and the rotation point (in global coordiantes).
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public void rotateXGlobal(Vector3D rotationPoint, float degree);
	
//	/**
//	 * Gets the custom viewport setting.
//	 * 
//	 * @return the custom viewport setting
//	 */
//	public ViewportSetting getCustomViewportSetting();
//	
//	/**
//	 * Gets the default viewport setting.
//	 * 
//	 * @return the default viewport setting
//	 */
//	public ViewportSetting getDefaultViewportSetting();
//	
//	/**
//	 * Checks for custom view port.
//	 * 
//	 * @return true, if successful
//	 */
//	public boolean hasCustomViewPort();
	
	/**
	 * Checks if is pickable.
	 * 
	 * @return true, if is pickable
	 */
	public boolean isPickable();

	/**
	 * If set to true, this component will be testable for intersections.
	 * 
	 * @param pickable the pickable
	 */
	public void setPickable(boolean pickable);

	/**
	 * Gets the camera through which this component is being viewed. This is the
	 * attached camera of this, or one of its parant's attached cameras.
	 * 
	 * @return the responsible camera
	 */
	public Icamera getViewingCamera();
	
//	/**
//	 * Checks if is collidable.
//	 * 
//	 * @return true, if is collidable
//	 */
//	public boolean isCollidable();
//
//	/**
//	 * Sets the collidable.
//	 * 
//	 * @param collidable the new collidable value
//	 */
//	public void setCollidable(boolean collidable);
	
}
