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
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;



/**
 * The Class DefaultDragAction.
 * @author Christopher Ruff
 */
public class DefaultDragAction implements IGestureEventListener,ICollisionAction  {
	
	/** The drag target. */
	private IMTComponent3D dragTarget;
	
	/** The use custom target. */
	private boolean useCustomTarget;
	
	/** The gesture aborted. */
	private boolean gestureAborted = false;
	
	/** The last event. */
	private MTGestureEvent lastEvent;
	
	/**
	 * Instantiates a new default drag action.
	 */
	public DefaultDragAction(){
		this.useCustomTarget = false;
	}
	
	/**
	 * Instantiates a new default drag action.
	 *
	 * @param dragTarget the drag target
	 */
	public DefaultDragAction(IMTComponent3D dragTarget){
		this.dragTarget = dragTarget;
		this.useCustomTarget = true;
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.IGestureEventListener#processGestureEvent(org.mt4j.input.inputProcessors.MTGestureEvent)
	 */
	public boolean processGestureEvent(MTGestureEvent g) {
		if (g instanceof DragEvent){
			DragEvent dragEvent = (DragEvent)g;
			lastEvent = dragEvent;
			
			if (!useCustomTarget)
				dragTarget = dragEvent.getTarget(); 
			
			switch (dragEvent.getId()) {
			case MTGestureEvent.GESTURE_STARTED:
			case MTGestureEvent.GESTURE_RESUMED:
				//Put target on top -> draw on top of others
				if (dragTarget instanceof MTComponent){
					MTComponent baseComp = (MTComponent)dragTarget;
					
					baseComp.sendToFront();
					
					/*
					//End all animations of the target
					Animation[] animations = AnimationManager.getInstance().getAnimationsForTarget(dragTarget);
					for (int i = 0; i < animations.length; i++) {
						Animation animation = animations[i];
						animation.stop();
					}
					*/
				}
					
				translate(dragTarget, dragEvent);
				break;
			case MTGestureEvent.GESTURE_UPDATED:
				translate(dragTarget, dragEvent);
				break;
			case MTGestureEvent.GESTURE_CANCELED:
				break;
			case MTGestureEvent.GESTURE_ENDED:
				break;
			default:
				break;
			}
		}
		return false;
	}

	
	protected void translate(IMTComponent3D comp, DragEvent de){
		if(!gestureAborted)
		{ 
		comp.translateGlobal(de.getTranslationVect());
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
