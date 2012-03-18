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
package org.mt4j.input.inputProcessors.componentProcessors.tapProcessor;

import java.util.List;

import org.mt4j.components.MTCanvas;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * The Class TapProcessor. Tap multitouch gesture. Triggered on a component
 * that is tapped with a finger. Also allows to recognized double-tapps if the "enableDoubleTap" is set to true.
 * Fires TapEvent gesture events.
 * @author Christopher Ruff
 */
public class TapProcessor extends AbstractCursorProcessor {
	
	/** The applet. */
	private PApplet applet;
	
	/** The max finger up dist. */
	private float maxFingerUpDist;
	
	/** The button down screen pos. */
	private Vector3D buttonDownScreenPos;
	
	/** The enable double tap. */
	private boolean enableDoubleTap;
	
	/** The time last tap. */
	private long timeLastTap;
	
	/** The double tap time. */
	private int doubleTapTime = 300;
	
	/**
	 * Instantiates a new tap processor.
	 * 
	 * @param pa the pa
	 */
	public TapProcessor(PApplet pa) {
		this(pa, 18.0f);
	}
	
	/**
	 * Instantiates a new tap processor.
	 * 
	 * @param pa the pa
	 * @param maxFingerUpDistance the max finger up distance
	 */
	public TapProcessor(PApplet pa, float maxFingerUpDistance) {
		this(pa, maxFingerUpDistance, false, 300); 
	}
	
	/**
	 * Instantiates a new tap processor.
	 * 
	 * @param pa the pa
	 * @param maxFingerUpDistance the max finger up distance
	 * @param enableDoubleTap the enable double tap
	 */
	public TapProcessor(PApplet pa, float maxFingerUpDistance, boolean enableDoubleTap){
		this(pa, maxFingerUpDistance, enableDoubleTap, 300);
	}
	
	/**
	 * Instantiates a new tap processor.
	 * 
	 * @param pa the pa
	 * @param maxFingerUpDistance the max finger up distance
	 * @param enableDoubleTap the enable double tap
	 * @param doubleTapTime the double tap time
	 */
	public TapProcessor(PApplet pa, float maxFingerUpDistance, boolean enableDoubleTap, int doubleTapTime){
		this(pa, maxFingerUpDistance, enableDoubleTap, doubleTapTime, false);
	}

	/**
	 * Instantiates a new tap processor.
	 *
	 * @param pa the pa
	 * @param maxFingerUpDistance the max finger up distance
	 * @param enableDoubleTap the enable double tap
	 * @param doubleTapTime the double tap time
	 * @param stopEventPropagation the stop event propagation
	 */
	public TapProcessor(PApplet pa, float maxFingerUpDistance, boolean enableDoubleTap, int doubleTapTime, boolean stopEventPropagation){
		super(stopEventPropagation);
		this.applet = pa;
		this.maxFingerUpDist = maxFingerUpDistance;
		this.setLockPriority(1);
		this.setDebug(false);
		
		this.enableDoubleTap = enableDoubleTap;
		this.doubleTapTime = doubleTapTime;
		this.timeLastTap = -1;
		//System.out.println("Double click default time:" + Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval"));
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorStarted(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorStarted(InputCursor m, AbstractCursorInputEvt positionEvent) {
		InputCursor[] theLockedCursors = getLockedCursorsArray();
		//if gesture isnt started and no other cursor on comp is locked by higher priority gesture -> start gesture
		if (theLockedCursors.length == 0 && this.canLock(getCurrentComponentCursorsArray())){ 
			if (this.canLock(m)){//See if we can obtain a lock on this cursor (depends on the priority)
				this.getLock(m);
				logger.debug(this.getName() + " successfully locked cursor (id:" + m.getId() + ")");
				buttonDownScreenPos = m.getPosition();
				this.fireGestureEvent(new TapEvent(this, MTGestureEvent.GESTURE_STARTED, positionEvent.getCurrentTarget(), m, buttonDownScreenPos, TapEvent.TAP_DOWN));
			}
		}
	}


	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUpdated(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorUpdated(InputCursor c, AbstractCursorInputEvt positionEvent) {
		if (getLockedCursors().contains(c)){
			Vector3D screenPos = c.getPosition();
			//logger.debug("Distance between buttondownScreenPos: " + buttonDownScreenPos + " and upScrPos: " + buttonUpScreenPos +  " is: " + Vector3D.distance(buttonDownScreenPos, buttonUpScreenPos));
			if (Vector3D.distance2D(buttonDownScreenPos, screenPos) > this.maxFingerUpDist){
				logger.debug(this.getName() + " DISTANCE TOO FAR");
				this.endGesture(c, positionEvent);
				this.unLock(c); 
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorEnded(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorEnded(InputCursor m, AbstractCursorInputEvt positionEvent) {
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - CURSOR: " + m.getId());
		List<InputCursor> locked = this.getLockedCursors();
		if (locked.contains(m)){
			InputCursor[] availableCursors = getFreeComponentCursorsArray();
			if (availableCursors.length > 0 && this.canLock(getCurrentComponentCursorsArray())){ 
				InputCursor otherCursor = availableCursors[0]; 
				this.getLock(otherCursor);
			}else{
				this.endGesture(m, positionEvent);
			}
		}
	}

	
	/**
	 * End gesture.
	 *
	 * @param m the m
	 * @param positionEvent the position event
	 */
	private void endGesture(InputCursor m, AbstractCursorInputEvt positionEvent){
		//Default where for the event if no intersections are found
		Vector3D buttonUpScreenPos = m.getPosition();
		
		//If component is detached from tree, destroyed etc
		if (positionEvent.getCurrentTarget().getViewingCamera() == null){
			this.fireGestureEvent(new TapEvent(this, MTGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), m, buttonUpScreenPos, TapEvent.TAPPED));			
			return;
		}
		
		Vector3D intersection = positionEvent.getCurrentTarget().getIntersectionGlobal(Tools3D.getCameraPickRay(applet, positionEvent.getCurrentTarget(), m.getCurrentEvent().getX(), m.getCurrentEvent().getY()));
		//logger.debug("Distance between buttondownScreenPos: " + buttonDownScreenPos + " and upScrPos: " + buttonUpScreenPos +  " is: " + Vector3D.distance(buttonDownScreenPos, buttonUpScreenPos));
		//Check if at finger_Up the cursor is still on that object or if the cursor has moved too much 
		if ((intersection != null || positionEvent.getCurrentTarget() instanceof MTCanvas)
				&& 
			Vector3D.distance2D(buttonDownScreenPos, buttonUpScreenPos) <= this.maxFingerUpDist
		){
			//We have a valid TAP!
			if (this.isEnableDoubleTap()){
				//Check if it was a double tap by comparing the now time to the time of the last valid tap
				long now = m.getCurrentEvent().getTimeStamp();
				if (this.timeLastTap != -1 && (now - this.timeLastTap) <= this.getDoubleTapTime()){
					//Its a Double tap
					this.timeLastTap = -1;
					this.fireGestureEvent(new TapEvent(this, MTGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), m, buttonUpScreenPos, TapEvent.DOUBLE_TAPPED));
				}else{
					this.timeLastTap = now;
					this.fireGestureEvent(new TapEvent(this, MTGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), m, buttonUpScreenPos, TapEvent.TAPPED));
				}
			}else{
				this.fireGestureEvent(new TapEvent(this, MTGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), m, buttonUpScreenPos, TapEvent.TAPPED));
			}
		}else{
			//logger.debug("FINGER UP NOT ON SAME OBJ!");
			this.fireGestureEvent(new TapEvent(this, MTGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), m, buttonUpScreenPos, TapEvent.TAP_UP));
		}
	}


	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorLocked(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputProcessors.IInputProcessor)
	 */
	@Override
	public void cursorLocked(InputCursor m, IInputProcessor lockingProcessor) {
		if (lockingProcessor instanceof AbstractComponentProcessor){
			logger.debug(this.getName() + " Recieved CURSOR LOCKED by (" + ((AbstractComponentProcessor)lockingProcessor).getName()  + ") - cursor ID: " + m.getId());
		}else{
			logger.debug(this.getName() + " Recieved CURSOR LOCKED by higher priority signal - cursor ID: " + m.getId());
		}

		logger.debug(this.getName() + " cursor:" + m.getId() + " CURSOR LOCKED. Was an active cursor in this gesture!");
		this.fireGestureEvent(new TapEvent(this, MTGestureEvent.GESTURE_ENDED, m.getCurrentEvent().getCurrentTarget(), m, new Vector3D(m.getCurrentEvent().getX(), m.getCurrentEvent().getY()), TapEvent.TAP_UP));
	}



	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUnlocked(org.mt4j.input.inputData.InputCursor)
	 */
	@Override
	public void cursorUnlocked(InputCursor m) {
		logger.debug(this.getName() + " Recieved UNLOCKED signal for cursor ID: " + m.getId());

		if (getLockedCursors().size() >= 1){ //we dont need the unlocked cursor, gesture still in progress
			logger.debug(this.getName() + " still in progress - we dont need the unlocked cursor" );
			return;
		}
	}

	
	
	/**
	 * Gets the max finger up dist.
	 * 
	 * @return the max finger up dist
	 */
	public float getMaxFingerUpDist() {
		return maxFingerUpDist;
	}


	/**
	 * Sets the maximum allowed distance of the position
	 * of the finger_down event and the finger_up event
	 * that fires a click event (in screen pixels).
	 * <br>This ensures that a click event is only raised
	 * if the finger didnt move too far during the click.
	 * 
	 * @param maxFingerUpDist the max finger up dist
	 */
	public void setMaxFingerUpDist(float maxFingerUpDist) {
		this.maxFingerUpDist = maxFingerUpDist;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor#getName()
	 */
	@Override
	public String getName() {
		return "Tap Processor";
	}

	/**
	 * Checks if is enable double tap.
	 * 
	 * @return true, if is enable double tap
	 */
	public boolean isEnableDoubleTap() {
		return this.enableDoubleTap;
	}

	/**
	 * Sets the enable double tap.
	 * 
	 * @param enableDoubleTap the new enable double tap
	 */
	public void setEnableDoubleTap(boolean enableDoubleTap) {
		this.enableDoubleTap = enableDoubleTap;
	}

	/**
	 * Gets the double tap time.
	 * 
	 * @return the double tap time
	 */
	public int getDoubleTapTime() {
		return this.doubleTapTime;
	}

	/**
	 * Sets the double tap time.
	 * 
	 * @param doubleTapTime the new double tap time
	 */
	public void setDoubleTapTime(int doubleTapTime) {
		this.doubleTapTime = doubleTapTime;
	}
}
