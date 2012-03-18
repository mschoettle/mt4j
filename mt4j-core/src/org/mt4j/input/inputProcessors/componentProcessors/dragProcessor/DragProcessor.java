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
package org.mt4j.input.inputProcessors.componentProcessors.dragProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * The Class DragProcessor. For multi touch drag behaviour on components.
 * Fires DragEvent gesture events.
 * @author Christopher Ruff
 */
public class DragProcessor extends AbstractCursorProcessor {
	
	/** The applet. */
	private PApplet applet;
	
	/** The dc. */
	private DragContext dc;
	
	public DragProcessor(PApplet graphicsContext){
		this(graphicsContext, false);
	}

	/**
	 * Instantiates a new drag processor.
	 * 
	 * @param graphicsContext the graphics context
	 */
	public DragProcessor(PApplet graphicsContext, boolean stopEventPropagation){
		super(stopEventPropagation);
		this.applet = graphicsContext;
		this.setLockPriority(1);
		this.setDebug(false);
//		logger.setLevel(Level.DEBUG);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorStarted(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorStarted(InputCursor cursor, AbstractCursorInputEvt fe) {
		IMTComponent3D comp = fe.getTarget();
		InputCursor[] theLockedCursors = getLockedCursorsArray();
		//if gesture isnt started and no other cursor on comp is locked by higher priority gesture -> start gesture
		if (theLockedCursors.length == 0 && this.canLock(getCurrentComponentCursorsArray())){ 
			dc = new DragContext(cursor, comp); 
			if (!dc.isGestureAborted()){ //See if the drag couldnt start (i.e. cursor doesent hit component anymore etc)
				//Lock this cursor with our priority
				this.getLock(cursor);
				logger.debug(this.getName() + " successfully locked cursor (id:" + cursor.getId() + ")");
				this.fireGestureEvent(new DragEvent(this,MTGestureEvent.GESTURE_STARTED, fe.getCurrentTarget(), cursor, dc.getLastPosition(), dc.getNewPosition()));
			}else{
				logger.debug(this.getName() + " gesture aborted, probably finger not on component!");
				dc = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUpdated(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorUpdated(InputCursor c, AbstractCursorInputEvt fe) {
		if (getLockedCursors().contains(c) && dc.getCursor().equals(c)){
			dc.updateDragPosition();
			this.fireGestureEvent(new DragEvent(this, MTGestureEvent.GESTURE_UPDATED, fe.getCurrentTarget(), dc.getCursor(), dc.getLastPosition(), dc.getNewPosition()));
		}
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorEnded(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorEnded(InputCursor c, AbstractCursorInputEvt fe) {
		IMTComponent3D comp = fe.getTarget();
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - CURSOR: " + c.getId());
		if (getLockedCursors().contains(c)){ //cursors was a actual gesture cursors
			dc.updateDragPosition();
			//Check if we can resume the gesture with another cursor
			InputCursor[] availableCursors = getFreeComponentCursorsArray();
			if (availableCursors.length > 0 && this.canLock(getCurrentComponentCursorsArray())){ 
				InputCursor otherCursor = availableCursors[0]; 
				DragContext newContext = new DragContext(otherCursor, comp); 
				if (!newContext.isGestureAborted()){
					dc = newContext;
					this.getLock(otherCursor);
				}else{
					this.fireGestureEvent(new DragEvent(this, MTGestureEvent.GESTURE_ENDED, fe.getCurrentTarget(), c, dc.getLastPosition(), dc.getNewPosition()));
				}
			}else{
				this.fireGestureEvent(new DragEvent(this, MTGestureEvent.GESTURE_ENDED, fe.getCurrentTarget(), c, dc.getLastPosition(), dc.getNewPosition()));
			}
		}
	}



	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorLocked(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputProcessors.IInputProcessor)
	 */
	@Override
	public void cursorLocked(InputCursor c, IInputProcessor p) {
		if (p instanceof AbstractComponentProcessor){
			logger.debug(this.getName() + " Recieved cursor LOCKED by (" + ((AbstractComponentProcessor)p).getName()  + ") - cursors ID: " + c.getId());
		}else{
			logger.debug(this.getName() + " Recieved cursor LOCKED by higher priority signal - cursors ID: " + c.getId());
		}
		
		if (dc != null && dc.getCursor().equals(c)){ 
			this.fireGestureEvent(new DragEvent(this, MTGestureEvent.GESTURE_CANCELED, dc.getCursor().getCurrentTarget(), dc.getCursor(), dc.getLastPosition(), dc.getNewPosition()));
			dc = null;
			logger.debug(this.getName() + " cursors:" + c.getId() + " CURSOR LOCKED. Was an locked cursor in this gesture!");
		}
	}



	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUnlocked(org.mt4j.input.inputData.InputCursor)
	 */
	@Override
	public void cursorUnlocked(InputCursor c) {
		logger.debug(this.getName() + " Recieved UNLOCKED signal for cursors ID: " + c.getId());
		if (getLockedCursors().size() >= 1){ //we dont need the unlocked cursors, gesture still in progress
			return;
		}

		if (getFreeComponentCursors().size() > 0 && this.canLock(getCurrentComponentCursorsArray())){ 
			DragContext newContext = new DragContext(c, c.getTarget());
			if (!newContext.isGestureAborted()){
				dc = newContext;
				this.getLock(c);
				this.fireGestureEvent(new DragEvent(this, MTGestureEvent.GESTURE_RESUMED, c.getCurrentTarget(), c, dc.getLastPosition(), dc.getNewPosition()));
				logger.debug(this.getName() + " can resume its gesture with cursors: " + c.getId());
			}else{
				dc = null;
				logger.debug(this.getName() + " we could NOT start gesture - cursors not on component: " + c.getId());
			}
		}else{
			logger.debug(this.getName() + " still in progress - we dont need the unlocked cursors" );
		}
	}

	/**
	 * The Class DragContext.
	 * @author Christopher Ruff
	 */
	private class DragContext {
				/** The start position. */
				private Vector3D startPosition;
				
				/** The last position. */
				private Vector3D lastPosition;
				
				/** The new position. */
				private Vector3D newPosition;
				
				/** The drag object. */
				private IMTComponent3D dragObject;
				
				/** The m. */
				private InputCursor m; 
				
				/** The gesture aborted. */
				private boolean gestureAborted;
				
				/** The drag plane normal. */
				private Vector3D dragPlaneNormal;

				
				/**
				 * Instantiates a new drag context.
				 * 
				 * @param c the cursor
				 * @param dragObject the drag object
				 */
				public DragContext(InputCursor c, IMTComponent3D dragObject_){	
					this.dragObject = dragObject_;
					this.m = c;
					gestureAborted = false;
					
					this.dragObject = c.getCurrentEvent().getCurrentTarget();
					
					//Calculate the normal of the plane we will be dragging at (useful if camera isnt default)
					this.dragPlaneNormal =  dragObject.getViewingCamera().getPosition().getSubtracted(dragObject.getViewingCamera().getViewCenterPos()).normalizeLocal();
					
					//Set the Drag Startposition
					Vector3D interSectP = getIntersection(applet, dragObject, c);
					
					if (interSectP != null)
						this.startPosition = interSectP;
					else{
						logger.warn(getName() + " Drag StartPoint Null -> aborting drag");
						gestureAborted = true; 
						this.startPosition = new Vector3D(0,0,0); 
					}
					
					this.newPosition = startPosition.getCopy();
					this.updateDragPosition();
					
					//Set the Drags lastPostition (the last one before the new one)
					this.lastPosition	= startPosition.getCopy();
				}
				
				public void updateDragPosition(){
					if (dragObject == null || dragObject.getViewingCamera() == null){ //IF component was destroyed while gesture still active
						this.gestureAborted = true;
						return ;
					}
					Vector3D newPos = getPlaneIntersection(applet, dragPlaneNormal, startPosition, m);
					if (newPos != null){
						lastPosition = newPosition;
						newPosition = newPos;
					}
				}

				public Vector3D getLastPosition() {
					return lastPosition;
				}

				public Vector3D getNewPosition() {
					return newPosition;
				}

				public boolean isGestureAborted() {
					return gestureAborted;
				}
				
				public InputCursor getCursor(){
					return this.m;
				}
			}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor#getName()
	 */
	@Override
	public String getName() {
		return "Drag Processor";
	}

}
