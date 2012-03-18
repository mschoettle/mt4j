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
package org.mt4j.components;

import org.mt4j.util.math.Ray;


/**
 * The Class PickInfo. Used for picking a component in a canvas.
 * Contains the picking ray and the screen coordiantes of the picking location.
 * @author Christopher Ruff
 */
public class PickInfo {  
	
	/** The screen x coordinate. */
	private float screenXCoordinate;
	
	/** The screen y coordinate. */
	private float screenYCoordinate;
	
	/** The original pick ray. */
	private Ray originalPickRay;

	/**
	 * Instantiates a new pick info. Used for the pick() method of
	 * the MTComponent class.
	 * This class is used to check what
	 * 
	 * @param screenXCoordinate the screen x coordinate
	 * @param screenYCoordinate the screen y coordinate
	 * @param originalPickRay the original pick ray
	 */
	public PickInfo(float screenXCoordinate, float screenYCoordinate, Ray originalPickRay) {
		super();
		this.screenXCoordinate = screenXCoordinate;
		this.screenYCoordinate = screenYCoordinate;
		this.originalPickRay = originalPickRay;
	}

	/**
	 * Gets the pick ray.
	 * 
	 * @return the pick ray
	 */
	public Ray getPickRay() {
		return originalPickRay;
	}

	/**
	 * Gets the screen x coordinate.
	 * 
	 * @return the screen x coordinate
	 */
	public float getScreenXCoordinate() {
		return screenXCoordinate;
	}

	/**
	 * Gets the screen y coordinate.
	 * 
	 * @return the screen y coordinate
	 */
	public float getScreenYCoordinate() {
		return screenYCoordinate;
	}
	
	

}
