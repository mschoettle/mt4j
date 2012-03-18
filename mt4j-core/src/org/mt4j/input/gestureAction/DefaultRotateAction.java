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
package org.mt4j.input.gestureAction;

import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateEvent;


/**
 * The Class DefaultRotateAction.
 * 
 * @author Christopher Ruff
 */
public class DefaultRotateAction implements IGestureEventListener,ICollisionAction {
	
	/** The target. */
	private IMTComponent3D target;
	
	/** The use custom target. */
	private boolean useCustomTarget;
	
	/** The last event. */
	private MTGestureEvent lastEvent;
	
	/** The gesture aborted. */
	private boolean gestureAborted = false;
	
	/**
	 * Instantiates a new default rotate action.
	 */
	public DefaultRotateAction(){
		this.useCustomTarget = false;
	}
	
	/**
	 * Instantiates a new default rotate action.
	 * 
	 * @param customTarget the custom target
	 */
	public DefaultRotateAction(IMTComponent3D customTarget){
		this.target = customTarget;
		this.useCustomTarget = true;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.IGestureEventListener#processGestureEvent(org.mt4j.input.inputProcessors.MTGestureEvent)
	 */
	public boolean processGestureEvent(MTGestureEvent g) {
		if (g instanceof RotateEvent){
			RotateEvent rotateEvent = (RotateEvent)g;
			lastEvent = rotateEvent;
			
			if (!useCustomTarget)
				target = rotateEvent.getTarget(); 
			
			switch (rotateEvent.getId()) {
			case MTGestureEvent.GESTURE_STARTED:
			case MTGestureEvent.GESTURE_RESUMED:
				if (target instanceof MTComponent){
					((MTComponent)target).sendToFront();
					/*
					Animation[] animations = AnimationManager.getInstance().getAnimationsForTarget(target);
					for (int i = 0; i < animations.length; i++) {
						Animation animation = animations[i];
						animation.stop();
					}
					*/
				}
				break;
			case MTGestureEvent.GESTURE_UPDATED:
				doAction(target, rotateEvent);
				break;
			case MTGestureEvent.GESTURE_CANCELED:
			case MTGestureEvent.GESTURE_ENDED:
				break;
			default:
				break;
			}
		}
		return false;
	}
	
	protected void doAction(IMTComponent3D comp, RotateEvent re){
		if(!gestureAborted())
		{
			comp.rotateZGlobal(re.getRotationPoint(), re.getRotationDegrees());
			if (comp.isGestureAllowed(DragProcessor.class))
				comp.translateGlobal(re.getTranslationVector());
		}
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.ICollisionAction#gestureAborted()
	 */
	public boolean gestureAborted() {
		return this.gestureAborted;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.ICollisionAction#getLastEvent()
	 */
	public MTGestureEvent getLastEvent() {
		return this.lastEvent;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.ICollisionAction#setGestureAborted(boolean)
	 */
	public void setGestureAborted(boolean aborted) {
		this.gestureAborted = aborted;
	}
}
