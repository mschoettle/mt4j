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
package org.mt4j.input.inputProcessors.componentProcessors.panProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.Vector3D;


/**
 * The Class PanTwoFingerEvent.
 * @author Christopher Ruff
 */
public class PanTwoFingerEvent extends PanEvent {
	
	/** The second finger. */
	private InputCursor secondFinger;

	/**
	 * Instantiates a new pan two finger event.
	 * 
	 * @param source the source
	 * @param id the id
	 * @param targetComponent the target component
	 * @param firstFinger the first finger
	 * @param secondFinger the second finger
	 * @param translationVector the translation vector
	 * @param camera the camera
	 */
	public PanTwoFingerEvent(IInputProcessor source, int id, IMTComponent3D targetComponent, InputCursor firstFinger, InputCursor secondFinger, Vector3D translationVector, Icamera camera) {
		super(source, id, targetComponent, firstFinger, translationVector, camera);
		this.secondFinger = secondFinger;
	}

	/**
	 * Gets the second finger.
	 * 
	 * @return the second finger
	 */
	public InputCursor getSecondCursor() {
		return secondFinger;
	}
	
	

}
