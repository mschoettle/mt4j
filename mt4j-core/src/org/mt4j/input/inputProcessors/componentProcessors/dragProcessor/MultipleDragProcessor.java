/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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

import java.util.HashMap;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * The Class MultipleDragProcessor. Fires drag events for every cursor on the component instead
 * of only one cursor, like the DragProcessor.
 * Fires DragEvent gesture events. 
 * <br>Note: At the moment this processor does not care about locks on cursors!
 * @author Christopher Ruff
 */
public class MultipleDragProcessor extends AbstractCursorProcessor {
	//TODO also use cursor locking mechanism?
	
	/** The app. */
	private PApplet app;
	
	/** The motion to drag context. */
	private HashMap<InputCursor, DragContext> motionToDragContext;

	/**
	 * Instantiates a new multiple drag processor.
	 * 
	 * @param app the app
	 */
	public MultipleDragProcessor(PApplet app) {
		super();
		this.app = app;
		motionToDragContext = new HashMap<InputCursor, DragContext>();
		this.setLockPriority(1);
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorStarted(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorStarted(InputCursor inputCursor,	AbstractCursorInputEvt positionEvent) {
		DragContext dc = new DragContext(inputCursor);
		IMTComponent3D comp = positionEvent.getTarget();
		if (!dc.gestureAborted){
			motionToDragContext.put(inputCursor, dc);
			this.fireGestureEvent(new DragEvent(this, DragEvent.GESTURE_STARTED, comp, inputCursor, dc.lastPosition, dc.newPosition));
		}
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUpdated(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorUpdated(InputCursor inputCursor, AbstractCursorInputEvt positionEvent) {
		IMTComponent3D comp = positionEvent.getTarget();
		DragContext dc = motionToDragContext.get(inputCursor);
		if (dc != null && dc.dragObject.getViewingCamera() != null){
			dc.updateDragPosition();
			this.fireGestureEvent(new DragEvent(this, MTGestureEvent.GESTURE_UPDATED, comp, inputCursor, dc.lastPosition, dc.newPosition));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorEnded(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorEnded(InputCursor inputCursor, AbstractCursorInputEvt positionEvent) {
		IMTComponent3D comp = positionEvent.getTarget();
		DragContext dc = motionToDragContext.get(inputCursor);
		if (dc != null){
			this.fireGestureEvent(new DragEvent(this, MTGestureEvent.GESTURE_ENDED, comp, inputCursor, dc.lastPosition, dc.newPosition));
			motionToDragContext.remove(inputCursor);
		}
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorLocked(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputProcessors.IInputProcessor)
	 */
	@Override
	public void cursorLocked(InputCursor cursor, IInputProcessor lockingprocessor) {

	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUnlocked(org.mt4j.input.inputData.InputCursor)
	 */
	@Override
	public void cursorUnlocked(InputCursor cursor) {

	}
	
	
	/**
	 * The Class DragContext.
	 */
	private class DragContext {
		
		/** The start position. */
		protected Vector3D startPosition;
		
		/** The last position. */
		protected Vector3D lastPosition;
		
		/** The new position. */
		protected Vector3D newPosition;
		
		/** The drag object. */
		private IMTComponent3D dragObject;
		
		/** The m. */
		private InputCursor m; 
		
		/** The gesture aborted. */
		protected boolean gestureAborted;
		
		/** The drag plane normal. */
		private Vector3D dragPlaneNormal;

		/**
		 * Instantiates a new drag context.
		 * 
		 * @param m the m
		 */
		public DragContext(InputCursor m){	
			this.dragObject = m.getCurrentEvent().getTarget();
			this.m = m;
			gestureAborted = false;
			//Calculate the normal of the plane we will be dragging at (useful if camera isnt default)
			this.dragPlaneNormal =  dragObject.getViewingCamera().getPosition().getSubtracted(dragObject.getViewingCamera().getViewCenterPos()).normalizeLocal();
			//Set the Drag Startposition
			Vector3D interSectP = getIntersection(app, m);
			
			if (interSectP != null)
				this.startPosition = interSectP;
			else{
				gestureAborted = true; 
				this.startPosition = new Vector3D(0,0,0); //TODO ABORT GESTURE!
			}
			this.newPosition = startPosition.getCopy();
			this.updateDragPosition();
			//Set the Drags lastPostition (the last one before the new one)
			this.lastPosition	= startPosition.getCopy();
		}

		/**
		 * Update drag position.
		 */
		public void updateDragPosition(){
			Vector3D newPos = getPlaneIntersection(app, dragPlaneNormal, startPosition, m);
			if (newPos != null){
				lastPosition = newPosition;
				newPosition = newPos;
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor#getName()
	 */
	@Override
	public String getName() {
		return "Multiple Drag Processor";
	}



}
