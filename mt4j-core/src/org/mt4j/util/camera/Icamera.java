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
 * The Interface Icamera.
 * @author Christopher Ruff
 */
public interface Icamera {
	//TODO add more methods?
	
	/**
	 * Update.
	 */
	public void update();
	
	/**
	 * Gets the position.
	 * 
	 * @return the position
	 */
	public Vector3D getPosition();

	/**
	 * Sets the position.
	 * 
	 * @param camPos the new position
	 */
	public void setPosition(Vector3D camPos);

	/**
	 * Gets the point, the camera looks at.
	 * 
	 * @return the view center pos
	 */
	public Vector3D getViewCenterPos();
	
	/**
	 * Sets the point, the camera should look at.
	 * 
	 * @param viewCenter the new view center pos
	 */
	public void setViewCenterPos(Vector3D viewCenter);
	
	/**
	 * Move cam and view center.
	 * 
	 * @param directionX the direction x
	 * @param directionY the direction y
	 * @param directionZ the direction z
	 */
	public void moveCamAndViewCenter(float directionX, float directionY, float directionZ);

	/**
	 * Sets the zoom min distance.
	 * 
	 * @param minDistanceToViewCenter the new zoom min distance
	 */
	public void setZoomMinDistance(float minDistanceToViewCenter);
	
	/**
	 * Zoom amount.
	 * 
	 * @param zoomAmount the zoom amount
	 */
	public void zoomAmount(float zoomAmount);
	
	/**
	 * Gets the frustum.
	 * 
	 * @return the frustum
	 */
	public IFrustum getFrustum();
}

