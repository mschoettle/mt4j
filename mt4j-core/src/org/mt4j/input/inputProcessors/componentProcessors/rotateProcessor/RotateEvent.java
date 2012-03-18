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
package org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.math.Vector3D;


/**
 * The Class RotateEvent.
 * @author Christopher Ruff
 */
public class RotateEvent extends MTGestureEvent {
	
	/** The first finger motion. */
	private InputCursor firstFingerMotion;
	
	/** The second finger motion. */
	private InputCursor secondFingerMotion;
	
	/** The rotation point. */
	private Vector3D rotationPoint;
	
	/** The translation vector. */
	private Vector3D translationVector;
	
	/** The rotation degrees. */
	private float rotationDegrees;
	

	/**
	 * Instantiates a new rotate event.
	 * 
	 * @param source the source
	 * @param id the id
	 * @param targetComponent the target component
	 * @param firstFingerMotion the first finger motion
	 * @param secondFingerMotion the second finger motion
	 * @param translationVector the translation vector
	 * @param rotationPoint the rotation point
	 * @param rotationDegrees the rotation degrees
	 */
	public RotateEvent(IInputProcessor source, int id, IMTComponent3D targetComponent, InputCursor firstFingerMotion, InputCursor secondFingerMotion, Vector3D translationVector, Vector3D rotationPoint, float rotationDegrees) {
		super(source, id, targetComponent);
		this.firstFingerMotion = firstFingerMotion;
		this.secondFingerMotion = secondFingerMotion;
		this.translationVector = translationVector;
		this.rotationPoint = rotationPoint;
		this.rotationDegrees = rotationDegrees;
	}

	/**
	 * Gets the first finger motion.
	 * 
	 * @return the first finger motion
	 */
	public InputCursor getFirstCursor() {
		return firstFingerMotion;
	}

	/**
	 * Gets the rotation point.
	 * 
	 * @return the rotation point
	 */
	public Vector3D getRotationPoint() {
		return rotationPoint;
	}

	/**
	 * Gets the second finger motion.
	 * 
	 * @return the second finger motion
	 */
	public InputCursor getSecondCursor() {
		return secondFingerMotion;
	}

	/**
	 * Gets the translation vector.
	 * 
	 * @return the translation vector
	 */
	public Vector3D getTranslationVector() {
		return translationVector;
	}

	/**
	 * Gets the rotation degrees.
	 * 
	 * @return the rotation degrees
	 */
	public float getRotationDegrees() {
		return rotationDegrees;
	}

	/**
	 * Sets the first finger motion.
	 * 
	 * @param firstFingerMotion the new first finger motion
	 */
	public void setFirstCursor(InputCursor firstFingerMotion) {
		this.firstFingerMotion = firstFingerMotion;
	}

	/**
	 * Sets the rotation degrees.
	 * 
	 * @param rotationDegrees the new rotation degrees
	 */
	public void setRotationDegrees(float rotationDegrees) {
		this.rotationDegrees = rotationDegrees;
	}

	/**
	 * Sets the rotation point.
	 * 
	 * @param rotationPoint the new rotation point
	 */
	public void setRotationPoint(Vector3D rotationPoint) {
		this.rotationPoint = rotationPoint;
	}

	/**
	 * Sets the second finger motion.
	 * 
	 * @param secondFingerMotion the new second finger motion
	 */
	public void setSecondCursor(InputCursor secondFingerMotion) {
		this.secondFingerMotion = secondFingerMotion;
	}

	/**
	 * Sets the translation vector.
	 * 
	 * @param translationVector the new translation vector
	 */
	public void setTranslationVector(Vector3D translationVector) {
		this.translationVector = translationVector;
	}
	
	
	

}
