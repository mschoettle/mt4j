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
package org.mt4j.input.inputProcessors.componentProcessors.dragProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.math.Vector3D;


/**
 * The Class DragEvent.
 * @author Christopher Ruff
 */
public class DragEvent extends MTGestureEvent {
	
	/** The drag cursor. */
	private InputCursor dragCursor;
	
	/** The from. */
	private Vector3D from;
	
	/** The to. */
	private Vector3D to;
	
	/** The translation vect. */
	private Vector3D translationVect;
	
	
	/**
	 * Instantiates a new drag event.
	 * 
	 * @param source the source
	 * @param id the id
	 * @param targetComponent the target component
	 * @param dragCursor the drag cursor
	 * @param from the from
	 * @param to the to
	 */
	public DragEvent(IInputProcessor source, int id, IMTComponent3D targetComponent, InputCursor dragCursor, Vector3D from, Vector3D to) {
		super(source, id, targetComponent);
		this.dragCursor = dragCursor;
		this.from = from;
		this.to = to;
		this.translationVect = to.getSubtracted(from);
	}

	/**
	 * Gets the drag cursor.
	 * 
	 * @return the drag cursor
	 */
	public InputCursor getDragCursor() {
		return dragCursor;
	}

	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	public Vector3D getFrom() {
		return from;
	}

	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	public Vector3D getTo() {
		return to;
	}

	/**
	 * Gets the translation vect.
	 * 
	 * @return the translation vect
	 */
	public Vector3D getTranslationVect() {
		return translationVect;
	}

	


}
