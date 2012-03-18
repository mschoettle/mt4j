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
package org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor;

import java.util.List;

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
 * The Class RotateProcessor. Rotation multitouch gesture.
 * Fires RotateEvent gesture events.
 * @author Christopher Ruff
 */
public class RotateProcessor extends AbstractCursorProcessor {

	/** The applet. */
	private PApplet applet;
	
	/** The rc. */
	private RotationContext rc;
	
	/** The drag plane normal. */
	private Vector3D dragPlaneNormal;
	
	
	public RotateProcessor(PApplet graphicsContext){
		this(graphicsContext, false);
	}
	
	/**
	 * Instantiates a new rotate processor.
	 * 
	 * @param graphicsContext the graphics context
	 */
	public RotateProcessor(PApplet graphicsContext, boolean stopEventPropagation){
		super(stopEventPropagation);
		this.applet = graphicsContext;
		this.dragPlaneNormal = new Vector3D(0,0,1);
		this.setLockPriority(2);
	}
	
	
	
	@Override
	public void cursorStarted(InputCursor newCursor, AbstractCursorInputEvt positionEvent) {
		IMTComponent3D comp = positionEvent.getTarget();
		logger.debug(this.getName() + " INPUT_STARTED, Cursor: " + newCursor.getId());
		
		List<InputCursor> alreadyLockedCursors = getLockedCursors();
		if (alreadyLockedCursors.size() >= 2){ //gesture with 2 fingers already in progress
			//TODO get the 2 cursors which are most far away from each other
			InputCursor firstCursor = alreadyLockedCursors.get(0);
			InputCursor secondCursor = alreadyLockedCursors.get(1);
			//Check if the new finger is more far away, so use that one
			if (isCursorDistanceGreater(firstCursor, secondCursor, newCursor) && canLock(firstCursor, newCursor)){
				RotationContext newContext = new RotationContext(firstCursor, newCursor, comp);
				if (!newContext.isGestureAborted()){ //Check if we could start gesture (ie. if fingers on component)
					rc = newContext;
					//Lock new Second cursor
					this.getLock(firstCursor, newCursor);
					this.unLock(secondCursor);
					logger.debug(this.getName() + " we could lock cursors: " + firstCursor.getId() +", and more far away cursor " + newCursor.getId());
				}else{
					logger.debug(this.getName() + " we could NOT exchange new second cursor - cursor not on component: " + firstCursor.getId() +", " + secondCursor.getId());
				}
			}else{
				logger.debug(this.getName() + " has already enough cursors for this gesture and the new cursors distance to the first one ist greater (or we dont have the priority to lock it) - adding to unused ID:" + newCursor.getId());
			}
		}else{ //no gesture in progress yet
			List<InputCursor> availableCursors = getFreeComponentCursors();
			logger.debug(this.getName() + " Available cursors: " + availableCursors.size());
			if (availableCursors.size() >= 2){
				InputCursor otherCursor = getFarthestFreeComponentCursorTo(newCursor);
//				logger.debug(this.getName() + " already had 1 unused cursor - we can try start gesture! used Cursor ID:" + otherCursor.getId() + " and new cursor ID:" + newCursor.getId());
				
				if (this.canLock(otherCursor, newCursor)){ //TODO remove check, since alreday checked in getAvailableComponentCursors()?
					rc = new RotationContext(otherCursor, newCursor, comp);
					if (!rc.isGestureAborted()){
						this.getLock(otherCursor, newCursor);
						logger.debug(this.getName() + " we could lock both cursors!");
						this.fireGestureEvent(new RotateEvent(this, MTGestureEvent.GESTURE_STARTED, positionEvent.getCurrentTarget(), otherCursor, newCursor, Vector3D.ZERO_VECTOR, rc.getRotationPoint(), 0f));
					}else{
						logger.debug(this.getName() + " gesture aborted, probably at least 1 finger not on component!");
						rc = null;
					}
				}else{
					logger.debug(this.getName() + " we could NOT lock both cursors!");
				}
			}else{
				logger.debug(this.getName() + " still missing a cursor to start gesture");
			}
		}
	}


	@Override
	public void cursorUpdated(InputCursor m, AbstractCursorInputEvt positionEvent) {
		List<InputCursor> alreadyLockedCursors = getLockedCursors();
		if (rc != null && alreadyLockedCursors.size() == 2 && alreadyLockedCursors.contains(m)){
			float rotationAngleDegrees = rc.updateAndGetRotationAngle(m);
			this.fireGestureEvent(new RotateEvent(this, MTGestureEvent.GESTURE_UPDATED, positionEvent.getCurrentTarget(), rc.getPinFingerCursor(), rc.getRotateFingerCursor(), Vector3D.ZERO_VECTOR, rc.getRotationPoint(), rotationAngleDegrees));
		}
	}

	
	@Override
	public void cursorEnded(InputCursor c, AbstractCursorInputEvt positionEvent) {
		IMTComponent3D comp = positionEvent.getTarget();
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - CURSOR: " + c.getId());
		logger.debug("Rotate ended -> Active cursors: " + getCurrentComponentCursors().size() + " Available cursors: " + getFreeComponentCursors().size() +  " Locked cursors: " + getLockedCursors().size());
		if (getLockedCursors().contains(c)){
			InputCursor firstCursor = rc.getFirstCursor(); 
			InputCursor secondCursor = rc.getSecondCursor();
			if (firstCursor.equals(c) || secondCursor.equals(c)){ //The leaving cursor was used by the processor
				InputCursor leftOverCursor = firstCursor.equals(c) ? secondCursor : firstCursor;
				InputCursor futureCursor = getFarthestFreeCursorTo(leftOverCursor);
				if (futureCursor != null){ //already checked in getFartherstAvailableCursor() if we can lock it
					RotationContext newContext = new RotationContext(futureCursor, leftOverCursor, comp);
					if (!newContext.isGestureAborted()){
						rc = newContext;
						this.getLock(leftOverCursor, futureCursor);
						logger.debug(this.getName() + " continue with different cursors (ID: " + futureCursor.getId() + ")" + " " + "(ID: " + leftOverCursor.getId() + ")");
					}else{ //couldnt start gesture - cursor's not on component 
						this.endGesture(leftOverCursor, positionEvent, firstCursor, secondCursor);
					}
				}else{ //we cant use another cursor  - End gesture
					this.endGesture(leftOverCursor, positionEvent, firstCursor, secondCursor);
				}
				this.unLock(c); 
			}
		}
	}
	
	private void endGesture(InputCursor leftOverCursor, AbstractCursorInputEvt positionEvent, InputCursor firstCursor, InputCursor secondCursor){
		this.unLock(leftOverCursor);
		this.fireGestureEvent(new RotateEvent(this, MTGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), firstCursor, secondCursor, Vector3D.ZERO_VECTOR, rc.getRotationPoint(), 0));
		this.rc = null;
	}
	

	@Override
	public void cursorLocked(InputCursor c, IInputProcessor lockingAnalyzer) {
		if (lockingAnalyzer instanceof AbstractComponentProcessor){
			logger.debug(this.getName() + " Recieved CURSOR LOCKED by (" + ((AbstractComponentProcessor)lockingAnalyzer).getName()  + ") - cursor ID: " + c.getId());
		}else{
			logger.debug(this.getName() + " Recieved CURSOR LOCKED by higher priority signal - cursor ID: " + c.getId());
		}
		
		if (rc != null && (rc.getFirstCursor().equals(c) || rc.getSecondCursor().equals(c))){
			//TODO do we have to unlock the 2nd cursor, besides "c" ??
			this.unLockAllCursors();
			//FIXME TEST
			this.fireGestureEvent(new RotateEvent(this, MTGestureEvent.GESTURE_CANCELED, c.getCurrentTarget(), rc.getFirstCursor(), rc.getSecondCursor(), Vector3D.ZERO_VECTOR, rc.getRotationPoint(), 0));
			rc = null;
			logger.debug(this.getName() + " cursor:" + c.getId() + " CURSOR LOCKED. Was an active cursor in this gesture!");
		}
	}

	
	@Override
	public void cursorUnlocked(InputCursor c) {
		logger.debug(this.getName() + " Recieved UNLOCKED signal for cursor ID: " + c.getId());
		
		if (getLockedCursors().size() >= 2){ //we dont need the unlocked cursor, gesture still in progress
			return;
		}
		
		//Dont we have to unlock the cursors if there could still 1 be locked?
		//-> actually we always lock 2 cursors at once so should never be only 1 locked, but just for safety..
		this.unLockAllCursors();

		List<InputCursor> availableCursors = getFreeComponentCursors();
		if (availableCursors.size() >= 2){ //we can try to resume the gesture
			InputCursor firstCursor = availableCursors.get(0);
			InputCursor secondCursor = getFarthestFreeComponentCursorTo(firstCursor);

			//See if we can obtain a lock on both cursors
			IMTComponent3D comp = firstCursor.getFirstEvent().getTarget();
			RotationContext newContext = new RotationContext(firstCursor, secondCursor, comp);
			if (!newContext.isGestureAborted()){ //Check if we could start gesture (i.e. if fingers on component)
				rc = newContext;
				this.getLock(firstCursor, secondCursor);
				//FIXME TEST
				this.fireGestureEvent(new RotateEvent(this, MTGestureEvent.GESTURE_RESUMED, c.getCurrentTarget(), firstCursor, secondCursor, Vector3D.ZERO_VECTOR, rc.getRotationPoint(), 0f));
				logger.debug(this.getName() + " we could lock cursors: " + firstCursor.getId() +", " + secondCursor.getId());
			}else{
				rc = null;
				logger.debug(this.getName() + " we could NOT resume gesture - cursors not on component: " + firstCursor.getId() +", " + secondCursor.getId());
			}
		}
	}
	
	
	
	
	
	/**
	 * The Class RotationContext.
	 */
	private class RotationContext {

		/** The pin finger start. */
		private Vector3D pinFingerStart;

		/** The pin finger last. */
		private Vector3D pinFingerLast;

		/** The pin finger new. */
		private Vector3D pinFingerNew;

		/** The rotate finger start. */
		private Vector3D rotateFingerStart;

		/** The rotate finger last. */
		private Vector3D rotateFingerLast;

		/** The rotate finger new. */
		private Vector3D rotateFingerNew;

		/** The last rotation vect. */
		private Vector3D lastRotationVect;

		/** The object. */
		private IMTComponent3D object;

		/** The rotation point. */
		private Vector3D rotationPoint;

		/** The pin finger cursor. */
		private InputCursor pinFingerCursor; 

		/** The rotate finger cursor. */
		private InputCursor rotateFingerCursor; 

		private boolean gestureAborted;

		/**
		 * Instantiates a new rotation context.
		 * 
		 * @param pinFingerCursor the pin finger cursor
		 * @param rotateFingerCursor the rotate finger cursor
		 * @param object the object
		 */
		public RotationContext(InputCursor pinFingerCursor, InputCursor rotateFingerCursor, IMTComponent3D object){
			this.pinFingerCursor = pinFingerCursor;
			this.rotateFingerCursor = rotateFingerCursor;

//			Vector3D interPoint = getIntersection(applet, object, pinFingerCursor);
			Vector3D interPoint = getIntersection(applet, pinFingerCursor.getCurrentEvent().getCurrentTarget(), pinFingerCursor);
			if (interPoint !=null)
				pinFingerNew = interPoint;
			else{
				logger.warn(getName() + " Pinfinger NEW = NULL");
				pinFingerNew = new Vector3D();
				gestureAborted = true;
			}

			//Use lastEvent when resuming with another cursor that started long ago
//			Vector3D interPointRot = getIntersection(applet, object, rotateFingerCursor);
			Vector3D interPointRot = getIntersection(applet, object, rotateFingerCursor);

			if (interPointRot !=null)
				rotateFingerStart = interPointRot;
			else{
				logger.warn(getName() + " rotateFingerStart = NULL");
				rotateFingerStart = new Vector3D();
				//TODO ABORT THE Rotation HERE!
				gestureAborted = true;
			}

			this.pinFingerStart = pinFingerNew.getCopy(); 

			this.pinFingerLast	= pinFingerStart.getCopy(); 

			this.rotateFingerLast	= rotateFingerStart.getCopy();
			this.rotateFingerNew	= rotateFingerStart.getCopy();	

			this.object = object;

			this.rotationPoint = pinFingerNew.getCopy();

			//Get the rotation vector for reference for the next rotation
			this.lastRotationVect = rotateFingerStart.getSubtracted(pinFingerNew);

			//FIXME REMOVE!
//			dragPlaneNormal = ((MTPolygon)object).getNormal();
//			logger.debug("DragNormal: " + dragPlaneNormal);
		}

		/**
		 * Update and get rotation angle.
		 * 
		 * @param moveCursor the move cursor
		 * 
		 * @return the float
		 */
		public float updateAndGetRotationAngle(InputCursor moveCursor) {
//			/*
			float newAngleRad;
			float newAngleDegrees;

			//save the current pinfinger location as the old one
			this.pinFingerLast = this.pinFingerNew;
			//save the current pinfinger location as the old one
			this.rotateFingerLast = this.rotateFingerNew;

			//Check which finger moved and has to be updated
			if (moveCursor.equals(pinFingerCursor)){
				updatePinFinger();
			}
			else if (moveCursor.equals(rotateFingerCursor)){
				updateRotateFinger();
			}

			//FIXME REMOVE!
//			dragPlaneNormal = ((MTPolygon)object).getNormal();
//			logger.debug("DragNormal: " + dragPlaneNormal);

			//TODO drop Z values after that?
			//calculate the vector between the rotation finger vectors
			Vector3D currentRotationVect = rotateFingerNew.getSubtracted(pinFingerNew).normalizeLocal(); //TEST normalize rotation vector

			//calculate the angle between the rotaion finger vectors
			newAngleRad 	= Vector3D.angleBetween(lastRotationVect, currentRotationVect);
			newAngleDegrees = (float)Math.toDegrees(newAngleRad); 

			//FIXME EXPERIMENTAL BECAUSE ANGLEBETWEEN GIVES ROTATIONS SOMETIMES WHEN BOTH VECTORS ARE EQUAL!?
			if (rotateFingerLast.equalsVector(rotateFingerNew) && pinFingerLast.equalsVector(pinFingerNew)){
				//logger.debug("Angle gleich lassen");
				newAngleDegrees = 0.0f;
			}else{
				//logger.debug("Neuer Angle: " + newAngleDegrees);
			}

//			logger.debug("lastRotVect: " + lastRotationVect + " currentROtationVect: " + currentRotationVect + " Deg: " + newAngleDegrees);

			Vector3D cross = lastRotationVect.getCross(currentRotationVect);
			//Get the direction of rotation
			if (cross.getZ() < 0){
				newAngleDegrees*=-1;
			}

			//Check if the current and last rotation vectors are equal or not 
			if (!Float.isNaN(newAngleDegrees)/*!String.valueOf(newAngleDegrees).equalsIgnoreCase("NaN")*/){
				//if (newAngleDegrees != Float.NaN){ //if the vectors are equal rotationangle is NAN?
				lastRotationVect = currentRotationVect;
				return newAngleDegrees;
			}else{
				//lastRotationVect = currentRotationVect;
				return 0;
			}
//			*/
//			return 0;
		}


		/**
		 * Update rotate finger.
		 */
		private void updateRotateFinger(){
			if (object == null || object.getViewingCamera() == null){ //IF component was destroyed while gesture still active
				this.gestureAborted = true;
				return ;
			}
			Vector3D newRotateFingerPos = getPlaneIntersection(applet, dragPlaneNormal, rotateFingerStart.getCopy(), rotateFingerCursor);
			//Update the field
			if (newRotateFingerPos != null){
				this.rotateFingerNew = newRotateFingerPos;
				this.rotationPoint = pinFingerNew; 
			}else{
				logger.error(getName() + " new newRotateFinger Pos = null at update");
			}
		}


		/**
		 * Update pin finger.
		 */
		private void updatePinFinger(){  
			if (object == null){ //IF component was destroyed while gesture still active
				this.gestureAborted = true;
				return;
			}
			Vector3D newPinFingerPos = getPlaneIntersection(applet, dragPlaneNormal, pinFingerStart.getCopy(), pinFingerCursor);
			
			if (newPinFingerPos != null){
				// Update pinfinger with new position
				this.pinFingerNew = newPinFingerPos;
				//Set the Rotation finger as the rotation point because the pinfinger was moved
				this.rotationPoint = rotateFingerNew; //REMOVE!!? made because of scale
			}else{
				logger.error(getName() + " new PinFinger Pos = null at update");
			}
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
		 * Gets the pin finger cursor.
		 * 
		 * @return the pin finger cursor
		 */
		public InputCursor getPinFingerCursor() {
			return pinFingerCursor;
		}

		/**
		 * Gets the rotate finger cursor.
		 * 
		 * @return the rotate finger cursor
		 */
		public InputCursor getRotateFingerCursor() {
			return rotateFingerCursor;
		}

		public boolean isGestureAborted() {
			return gestureAborted;
		}
		
		public InputCursor getFirstCursor(){
			return this.pinFingerCursor;
		}
		
		public InputCursor getSecondCursor(){
			return this.rotateFingerCursor;
		}

	}
	
	@Override
	public String getName() {
		return "Rotate Processor";
	}

}
