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
package org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.math.Matrix;


/**
 * The Class ArcBallGestureEvent.
 * @author Christopher Ruff
 */
public class ArcBallGestureEvent extends MTGestureEvent {
	
	/** The transformation matrix. */
	private Matrix transformationMatrix;

	/**
	 * Instantiates a new arc ball gesture event.
	 * 
	 * @param source the source
	 * @param id the id
	 * @param targetComponent the target component
	 * @param transformationMatrix the transformation matrix
	 */
	public ArcBallGestureEvent(IInputProcessor source, int id, IMTComponent3D targetComponent, Matrix transformationMatrix) {
		super(source, id, targetComponent);
		this.transformationMatrix = transformationMatrix;
	}

	/**
	 * Gets the transformation matrix.
	 * 
	 * @return the transformation matrix
	 */
	public Matrix getTransformationMatrix() {
		return transformationMatrix;
	}
}
