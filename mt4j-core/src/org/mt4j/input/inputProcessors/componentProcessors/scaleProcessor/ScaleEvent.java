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
package org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.math.Vector3D;


/**
 * The Class ScaleEvent.
 * @author Christopher Ruff
 */
public class ScaleEvent extends MTGestureEvent{
	
	/** The scale factor x. */
	private float scaleFactorX;
	
	/** The scale factor y. */
	private float scaleFactorY;
	
	/** The scale factor z. */
	private float scaleFactorZ;
	
	/** The first  cursor. */
	private InputCursor firstCursor;
	
	/** The second  cursor. */
	private InputCursor secondCursor;
	
	/** The scaling point. */
	private Vector3D scalingPoint;
	
	
	/**
	 * Instantiates a new scale event.
	 * 
	 * @param source the source
	 * @param id the id
	 * @param scalingObject the scaling object
	 * @param firstCursor the first  cursor
	 * @param secondCursor the second  cursor
	 * @param scaleFactorX the scale factor x
	 * @param scaleFactorY the scale factor y
	 * @param scaleFactorZ the scale factor z
	 * @param scalingPoint the scaling point
	 */
	public ScaleEvent(IInputProcessor source, int id, IMTComponent3D scalingObject, InputCursor firstCursor, InputCursor secondCursor, float scaleFactorX, float scaleFactorY, float scaleFactorZ, Vector3D scalingPoint) {
		super(source, id, scalingObject);
		this.firstCursor = firstCursor;
		this.secondCursor = secondCursor;
		this.scaleFactorX = scaleFactorX;
		this.scaleFactorY = scaleFactorY;
		this.scaleFactorZ = scaleFactorZ;
		this.scalingPoint = scalingPoint;
	}

	/**
	 * Gets the scale factor x.
	 * 
	 * @return the scale factor x
	 */
	public float getScaleFactorX() {
		return scaleFactorX;
	}

	/**
	 * Gets the scale factor y.
	 * 
	 * @return the scale factor y
	 */
	public float getScaleFactorY() {
		return scaleFactorY;
	}

	/**
	 * Gets the scale factor z.
	 * 
	 * @return the scale factor z
	 */
	public float getScaleFactorZ() {
		return scaleFactorZ;
	}

	/**
	 * Gets the scaling point.
	 * 
	 * @return the scaling point
	 */
	public Vector3D getScalingPoint() {
		return scalingPoint;
	}


	/**
	 * Gets the first  cursor.
	 * 
	 * @return the first  cursor
	 */
	public InputCursor getFirstCursor() {
		return firstCursor;
	}

	/**
	 * Gets the second  cursor.
	 * 
	 * @return the second  cursor
	 */
	public InputCursor getSecondCursor() {
		return secondCursor;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return super.toString() + " - " + " Scaleobject:" + this.getTarget().getName() + " X-Scalefactor: " + scaleFactorX + " Y-Scalefactor:" + scaleFactorY + " Z-Scalefactor:" + scaleFactorZ + " Scalingpoint:" + scalingPoint;
	}

	/**
	 * Sets the first  cursor.
	 * 
	 * @param firstCursor the new first  cursor
	 */
	public void setFirstCursor(InputCursor firstCursor) {
		this.firstCursor = firstCursor;
	}

	/**
	 * Sets the scale factor x.
	 * 
	 * @param scaleFactorX the new scale factor x
	 */
	public void setScaleFactorX(float scaleFactorX) {
		this.scaleFactorX = scaleFactorX;
	}

	/**
	 * Sets the scale factor y.
	 * 
	 * @param scaleFactorY the new scale factor y
	 */
	public void setScaleFactorY(float scaleFactorY) {
		this.scaleFactorY = scaleFactorY;
	}

	/**
	 * Sets the scale factor z.
	 * 
	 * @param scaleFactorZ the new scale factor z
	 */
	public void setScaleFactorZ(float scaleFactorZ) {
		this.scaleFactorZ = scaleFactorZ;
	}

	/**
	 * Sets the scaling point.
	 * 
	 * @param scalingPoint the new scaling point
	 */
	public void setScalingPoint(Vector3D scalingPoint) {
		this.scalingPoint = scalingPoint;
	}

	/**
	 * Sets the second  cursor.
	 * 
	 * @param secondCursor the new second  cursor
	 */
	public void setSecondCursor(InputCursor secondCursor) {
		this.secondCursor = secondCursor;
	}
	
	
	

}
