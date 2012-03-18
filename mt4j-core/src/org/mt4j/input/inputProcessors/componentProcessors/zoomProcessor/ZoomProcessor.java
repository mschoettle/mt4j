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
package org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor;

import java.util.List;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * The Class ZoomProcessor.
 * Multitouch background zoom gesture. This will change the scene's CAMERA if the DefaultZoomAction gesture listener is added to the canvas.
 * Fires ZoomEvent gesture events.
 * <br><strong>NOTE:</strong> Should only be used in combination with a MTCanvas component. 
 * @author Christopher Ruff
 */
public class ZoomProcessor extends AbstractCursorProcessor {
	
	/** The zoom detect radius. */
	private float zoomDetectRadius;
	
	/** The old distance. */
	private float oldDistance;
	

	/**
	 * Instantiates a new zoom processor.
	 * 
	 * @param graphicsContext the graphics context
	 */
	public ZoomProcessor(PApplet graphicsContext){
		this(graphicsContext, MT4jSettings.getInstance().getWindowWidth()/2);
	}
	
	/**
	 * Instantiates a new zoom processor.
	 * 
	 * @param graphicsContext the graphics context
	 * @param zoomDetectRadius the zoom detect radius
	 */
	public ZoomProcessor(PApplet graphicsContext, float zoomDetectRadius){
		this.zoomDetectRadius = zoomDetectRadius;
		this.setLockPriority(2);
	}

	
	@Override
	public void cursorStarted(InputCursor c, AbstractCursorInputEvt positionEvent) {
		InputCursor[] locked = getLockedCursorsArray();
		if (locked.length >= 2){ //gesture with 2 fingers already in progress
			logger.debug(this.getName() + " has already enough cursors for this gesture - adding to unused ID:" + c.getId());
		}else{//not in progress yet
			List<InputCursor> availableCursors = getFreeComponentCursors();
			logger.debug(this.getName() + " Available cursors: " + availableCursors.size());
			
			if (availableCursors.size() >= 2){
				InputCursor otherCursor = (availableCursors.get(0).equals(c))? availableCursors.get(1) : availableCursors.get(0);
				
				//See if we can obtain a lock on both cursors
				if (this.canLock(otherCursor, c)){
					float newDistance = Vector3D.distance(otherCursor.getPosition(), c.getPosition());
					if (newDistance < zoomDetectRadius) {
						this.oldDistance = newDistance;
						this.getLock(otherCursor, c);
						logger.debug(this.getName() + " we could lock both cursors! And fingers in zoom distance!");
						this.fireGestureEvent(new ZoomEvent(this, MTGestureEvent.GESTURE_STARTED, positionEvent.getCurrentTarget(), c, otherCursor, 0f, positionEvent.getCurrentTarget().getViewingCamera() ));
					}else{
						logger.debug(this.getName() + " cursors not close enough to start gesture. Distance: " + newDistance);
					}
				}else{
					logger.debug(this.getName() + " we could NOT lock both cursors!");
				}
			}
		}
	}

	@Override
	public void cursorUpdated(InputCursor c, AbstractCursorInputEvt positionEvent) {
		List<InputCursor> locked = getLockedCursors();
		if (locked.contains(c)){ //in progress with this cursors
			InputCursor firstCursor = locked.get(0);
			InputCursor secondCursor = locked.get(1);
			float fingerDistance = Vector3D.distance(firstCursor.getPosition(), secondCursor.getPosition());
			float camZoomAmount = fingerDistance - oldDistance;
			oldDistance = fingerDistance;
			if (c.equals(firstCursor)){
				this.fireGestureEvent(new ZoomEvent(this, MTGestureEvent.GESTURE_UPDATED, positionEvent.getCurrentTarget(), firstCursor, secondCursor, camZoomAmount, positionEvent.getCurrentTarget().getViewingCamera()));
			}else{
				this.fireGestureEvent(new ZoomEvent(this, MTGestureEvent.GESTURE_UPDATED, positionEvent.getCurrentTarget(), firstCursor, secondCursor, camZoomAmount, positionEvent.getCurrentTarget().getViewingCamera()));
			}
		}
	}

	@Override
	public void cursorEnded(InputCursor c,	AbstractCursorInputEvt positionEvent) {
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - cursor: " + c.getId());
		List<InputCursor> locked = getLockedCursors();
		if (locked.contains(c)){
			InputCursor leftOverCursor = (locked.get(0).equals(c))? locked.get(1) : locked.get(0);
			InputCursor futureCursor = getFarthestFreeCursorToLimited(leftOverCursor, zoomDetectRadius);
			if (futureCursor != null){
				float newDistance = Vector3D.distance(leftOverCursor.getPosition(),	futureCursor.getPosition());
				if (newDistance < zoomDetectRadius) {//Check if other cursor is in distance 
					this.oldDistance = newDistance;
					this.getLock(futureCursor);
					logger.debug(this.getName() + " we could lock another cursor! (ID:" + futureCursor.getId() +")");
					logger.debug(this.getName() + " continue with different cursors (ID: " + futureCursor.getId() + ")" + " " + "(ID: " + leftOverCursor.getId() + ")");
				}else{
					this.endGesture(c, leftOverCursor, positionEvent.getCurrentTarget());
				}
			}else{
				this.endGesture(c, leftOverCursor, positionEvent.getCurrentTarget());
			}
		}
	}
	
	
	/**
	 * End gesture.
	 * 
	 * @param inputEndedcursor the input ended cursor
	 * @param leftOvercursor the left over cursor
	 * @param comp the comp
	 */
	private void endGesture(InputCursor inputEndedCursor, InputCursor leftOverCursor, IMTComponent3D comp){
		this.unLock(leftOverCursor);
		this.fireGestureEvent(new ZoomEvent(this, MTGestureEvent.GESTURE_ENDED, comp, inputEndedCursor, leftOverCursor, 0f, comp.getViewingCamera()));
	}
	
	
	
	@Override
	public void cursorLocked(InputCursor c, IInputProcessor lockingProcessor) {
		if (lockingProcessor instanceof AbstractComponentProcessor){
			logger.debug(this.getName() + " Recieved cursor LOCKED by (" + ((AbstractComponentProcessor)lockingProcessor).getName()  + ") - cursor ID: " + c.getId());
		}else{
			logger.debug(this.getName() + " Recieved cursor LOCKED by higher priority signal - cursor ID: " + c.getId());
		}
		
		List<InputCursor> locked = getLockedCursors();
		if (locked.contains(c)){
			this.fireGestureEvent(new ZoomEvent(this, MTGestureEvent.GESTURE_CANCELED, c.getCurrentTarget(), locked.get(0), locked.get(1), 0f, c.getCurrentTarget().getViewingCamera()));			
			this.unLockAllCursors();
			logger.debug(this.getName() + " cursor:" + c.getId() + " cursor LOCKED. Was an active cursor in this gesture - we therefor have to stop this gesture!");
		}
	}

	
	
	@Override
	public void cursorUnlocked(InputCursor c) {
		logger.debug(this.getName() + " Recieved UNLOCKED signal for cursor ID: " + c.getId());
		
		if (getLockedCursors().size() >= 2){ //we dont need the unlocked cursor, gesture still in progress
			return;
		}
		
		this.unLockAllCursors();
		
		List<InputCursor> availableCursors = getFreeComponentCursors();
		if (availableCursors.size() >= 2){ //we can try to resume the gesture
			InputCursor firstCursor = availableCursors.get(0);
			InputCursor secondCursor = getFarthestFreeCursorToLimited(firstCursor, zoomDetectRadius);

			//See if we can obtain a lock on both cursors
			if (this.canLock(firstCursor, secondCursor)){
				float newDistance = Vector3D.distance(firstCursor.getPosition(), secondCursor.getPosition());
				if (newDistance < zoomDetectRadius) {//Check if other cursor is in distance 
					this.oldDistance = newDistance;
					this.getLock(firstCursor, secondCursor);
					logger.debug(this.getName() + " we could lock cursors: " + firstCursor.getId() +", " + secondCursor.getId());
					logger.debug(this.getName() + " continue with different cursors (ID: " + firstCursor.getId() + ")" + " " + "(ID: " + secondCursor.getId() + ")");
					this.fireGestureEvent(new ZoomEvent(this, MTGestureEvent.GESTURE_RESUMED, c.getCurrentTarget(), firstCursor, secondCursor, 0f, c.getCurrentTarget().getViewingCamera() ));
				}else{
					logger.debug(this.getName() + " distance was too great between cursors: " + firstCursor.getId() +", " + secondCursor.getId() + " distance: " + newDistance);
				}
			}else{
				logger.debug(this.getName() + " we could NOT lock cursors: " + firstCursor.getId() +", " + secondCursor.getId());
			}
		}
	}
	
	
	@Override
	public String getName() {
		return "Zoom Processor";
	}
	

}
