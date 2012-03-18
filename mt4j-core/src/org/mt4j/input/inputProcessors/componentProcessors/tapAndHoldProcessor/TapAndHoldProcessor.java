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
package org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor;

import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.math.Vector3D;

/**
 * The Class TapAndHoldProcessor. Multi-Touch gesture which is triggered
 * after touching and resting the finger on the same spot for some time.
 * Only works with the first cursor entering the component. Cancels if the finger is moved beyond
 * a specified threshold distance.
 * Fires TapAndHoldEvent gesture events.
 * 
 * @author Christopher Ruff
 */
public class TapAndHoldProcessor extends AbstractCursorProcessor implements IPreDrawAction{
	
	/** The applet. */
	private AbstractMTApplication app;
	
	/** The max finger up dist. */
	private float maxFingerUpDist;
	
	/** The button down screen pos. */
	private Vector3D buttonDownScreenPos;

	/** The tap start time. */
	private long tapStartTime;
	
	/** The tap time. */
	private int holdTime;
	
	private IMTComponent3D lastCurrentTarget;
	
	//TODO atm this only allows 1 tap on 1 component
	//if we want more we have to do different (save each cursor to each start time etc, dont relock other cursors)
	
	
	/**
	 * Instantiates a new tap processor.
	 * @param pa the pa
	 */
	public TapAndHoldProcessor(AbstractMTApplication pa) {
		this(pa, 1800, false);
	}
	
	public TapAndHoldProcessor(AbstractMTApplication pa, int duration){
		this(pa, duration, false);
	}
	
	/**
	 * Instantiates a new tap and hold processor.
	 * @param pa the pa
	 * @param duration the duration
	 * @param stopPropatation 
	 */
	public TapAndHoldProcessor(AbstractMTApplication pa, int duration, boolean stopPropatation) {
		super(stopPropatation);
		this.app = pa;
		
		this.maxFingerUpDist = 17.0f;
		this.holdTime = duration;
		
		this.setLockPriority(1);
		this.setDebug(false);
//		logger.setLevel(Level.DEBUG);
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorStarted(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.AbstractCursorInputEvt)
	 */
	@Override
	public void cursorStarted(InputCursor c, AbstractCursorInputEvt positionEvent) {
		List<InputCursor> locked = getLockedCursors();
		
		if (locked.size() >=1){
			//already in progress
		}else{
			if (getFreeComponentCursors().size() == 1){ //Only start if this is the first cursor on the component
				if (this.getLock(c)){//See if we can obtain a lock on this cursor (depends on the priority)
					logger.debug(this.getName() + " successfully locked cursor (id:" + c.getId() + ")");
					buttonDownScreenPos = c.getPosition();
					tapStartTime = System.currentTimeMillis();
					//Save the last used currenttarget so we can use that during the updates in pre() 
					this.lastCurrentTarget = positionEvent.getCurrentTarget();
					this.fireGestureEvent(new TapAndHoldEvent(this, MTGestureEvent.GESTURE_STARTED, positionEvent.getCurrentTarget(), c, false, c.getPosition(), this.holdTime, 0, 0));
					try {
//						applet.registerPre(this);
						app.registerPreDrawAction(this);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			}
		}
	}

	//problem: mouse does only send update evt if mouse is actually dragged
	//idea: registerPre and check if we have  alocked cursor and the times is up
	//then do the checking
	
	/**
	 * Pre.
	 */
	public void pre(){
		List<InputCursor> locked = getLockedCursors();
		if (locked.size() == 1){
			InputCursor c = locked.get(0);
			IMTComponent3D comp = c.getTarget();
//			IMTComponent3D currentTarget = c.getCurrentEvent().getCurrentTarget(); //FIXME this will often return the wrong target since we are not in a processInputEvent() method!
			IMTComponent3D currentTarget = lastCurrentTarget;
			
			long nowTime = System.currentTimeMillis();
			long elapsedTime = nowTime - this.tapStartTime;
			Vector3D screenPos = c.getPosition();
			float normalized = (float)elapsedTime / (float)this.holdTime;
			
			if (elapsedTime >= holdTime){
				normalized = 1;
				logger.debug("TIME PASSED!");
				Vector3D intersection = getIntersection(app, comp, c);
				//logger.debug("Distance between buttondownScreenPos: " + buttonDownScreenPos + " and upScrPos: " + buttonUpScreenPos +  " is: " + Vector3D.distance(buttonDownScreenPos, buttonUpScreenPos));
				if ( (intersection != null || comp instanceof MTCanvas) //hack - at canvas no intersection..
						&& 
					Vector3D.distance2D(buttonDownScreenPos, screenPos) <= this.maxFingerUpDist
				){
					this.fireGestureEvent(new TapAndHoldEvent(this, MTGestureEvent.GESTURE_ENDED, currentTarget, c, true, screenPos, this.holdTime, elapsedTime, normalized));
				}else{
					logger.debug("DISTANCE TOO FAR OR NO INTERSECTION");
					this.fireGestureEvent(new TapAndHoldEvent(this, MTGestureEvent.GESTURE_ENDED, currentTarget, c, false, screenPos, this.holdTime, elapsedTime, normalized));
				}
				this.unLock(c); 
				try {
//					app.unregisterPre(this);
					app.unregisterPreDrawAction(this);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}else{
				this.fireGestureEvent(new TapAndHoldEvent(this, MTGestureEvent.GESTURE_UPDATED, currentTarget, c, false, screenPos, this.holdTime, elapsedTime, normalized));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUpdated(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.AbstractCursorInputEvt)
	 */
	@Override
	public void cursorUpdated(InputCursor c, AbstractCursorInputEvt positionEvent) {
		List<InputCursor> locked = getLockedCursors();
		if (locked.contains(c)){
			long nowTime = System.currentTimeMillis();
			long elapsedTime = nowTime - this.tapStartTime;
			Vector3D screenPos = c.getPosition();
			float normalized = (float)elapsedTime / (float)this.holdTime;

			//logger.debug("Distance between buttondownScreenPos: " + buttonDownScreenPos + " and upScrPos: " + buttonUpScreenPos +  " is: " + Vector3D.distance(buttonDownScreenPos, buttonUpScreenPos));
			if (Vector3D.distance2D(buttonDownScreenPos, screenPos) > this.maxFingerUpDist){
				logger.debug("DISTANCE TOO FAR OR NO INTERSECTION");
				this.unLock(c); 
				try {
//					app.unregisterPre(this);
					app.unregisterPreDrawAction(this);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				this.fireGestureEvent(new TapAndHoldEvent(this, MTGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), c, false, screenPos, this.holdTime, elapsedTime, normalized));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorEnded(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.AbstractCursorInputEvt)
	 */
	@Override
	public void cursorEnded(InputCursor c, AbstractCursorInputEvt positionEvent) {
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - MOTION: " + c.getId());
		
		List<InputCursor> locked = getLockedCursors();
		if (locked.contains(c)){
			long nowTime = System.currentTimeMillis();
			long elapsedTime = nowTime - this.tapStartTime;
			float normalized = (float)elapsedTime / (float)this.holdTime;
			
			List<InputCursor> free = getFreeComponentCursors();
			if (free.size() > 0){ 			//check if there are other cursors on the component, we could use 
				InputCursor otherCursor = free.get(0); 
				if (this.canLock(otherCursor) 
						&& 
					Vector3D.distance2D(buttonDownScreenPos, otherCursor.getPosition()) <= this.maxFingerUpDist)
				{ 	//Check if we have the priority to use this other cursor and if cursor is in range
					this.getLock(otherCursor);
					buttonDownScreenPos = otherCursor.getPosition();
				}else{
					//Other cursor has higher prior -> end this gesture
					this.fireGestureEvent(new TapAndHoldEvent(this, MTGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), c, false,  c.getPosition(), this.holdTime, elapsedTime, normalized));
					try {
//						app.unregisterPre(this);
						app.unregisterPreDrawAction(this);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			}else{
				//We have no other cursor to continue gesture -> end
				this.fireGestureEvent(new TapAndHoldEvent(this, MTGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), c, false,  c.getPosition(), this.holdTime, elapsedTime, normalized));
				try {
//					app.unregisterPre(this);
					app.unregisterPreDrawAction(this);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
			this.unLock(c); 
		}	
	}



	@Override
	public void cursorLocked(InputCursor c, IInputProcessor lockingAnalyzer) {
		if (lockingAnalyzer instanceof AbstractComponentProcessor){
			logger.debug(this.getName() + " Recieved MOTION LOCKED by (" + ((AbstractComponentProcessor)lockingAnalyzer).getName()  + ") - cursor ID: " + c.getId());
		}else{
			logger.debug(this.getName() + " Recieved MOTION LOCKED by higher priority signal - cursor ID: " + c.getId());
		}

		long nowTime = System.currentTimeMillis();
		long elapsedTime = nowTime - this.tapStartTime;
		float normalized = (float)elapsedTime / (float)this.holdTime;

		this.fireGestureEvent(new TapAndHoldEvent(this, MTGestureEvent.GESTURE_ENDED, c.getCurrentEvent().getCurrentTarget(), c, false, c.getPosition(), this.holdTime, elapsedTime, normalized));

		try {
//			app.unregisterPre(this);
			app.unregisterPreDrawAction(this);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		logger.debug(this.getName() + " cursor:" + c.getId() + " MOTION LOCKED. Was an active cursor in this gesture!");
	}



	@Override
	public void cursorUnlocked(InputCursor c) {
		//TAP AND HOLD IS NOT RESUMABLE 
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
	 * that fires a click event
	 * <br>This ensures that a click event is only raised
	 * if the finger didnt move that far during the click.
	 * 
	 * @param maxFingerUpDist the max finger up dist
	 */
	public void setMaxFingerUpDist(float maxFingerUpDist) {
		this.maxFingerUpDist = maxFingerUpDist;
	}
	
	
	
	
	/**
	 * Gets the time (in ms.) needed to hold to successfully tap&hold.
	 * 
	 * @return the Hold time
	 */
	public long getHoldTime() {
		return this.holdTime;
	}



	/**
	 * Sets the holding time for the gesture.
	 *
	 * @param tapTime the new hold time
	 */
	public void setHoldTime(int tapTime) {
		this.holdTime = tapTime;
	}



	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor#getName()
	 */
	@Override
	public String getName() {
		return "tap and hold processor";
	}

	public boolean isLoop() {
		return true;
	}

	public void processAction() {
		pre();
	}

}
