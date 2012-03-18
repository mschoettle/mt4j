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
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.util.math.Vector3D;



/**
 * The Class DefaultScaleAction.
 * 
 * @author Christopher Ruff
 */
public class DefaultScaleAction implements IGestureEventListener,ICollisionAction {
	
	/** The target. */
	private IMTComponent3D target;
	
	/** The has scale limit. */
	private boolean hasScaleLimit;
	
	/** The min scale. */
	private float minScale;
	
	/** The max scale. */
	private float maxScale;
	
	/** The last event. */
	private MTGestureEvent lastEvent;
	
	/** The gesture aborted. */
	private boolean gestureAborted = false;
	
	/**
	 * Instantiates a new default scale action.
	 */
	public DefaultScaleAction(){
		this(null, 0,0, false);
	}
	
	/**
	 * Instantiates a new default scale action.
	 * 
	 * @param customTarget the custom target
	 */
	public DefaultScaleAction(IMTComponent3D customTarget){
		this(customTarget, 0,0, false);
	}
	
	
	
	
	/**
	 * Instantiates a new default scale action.
	 *
	 * @param minScaleFactor the min scale factor
	 * @param maxScaleFactor the max scale factor
	 */
	public DefaultScaleAction(float minScaleFactor, float maxScaleFactor){
		this(null, minScaleFactor, maxScaleFactor, true);
	}
	
	/**
	 * Instantiates a new default scale action.
	 *
	 * @param customTarget the custom target
	 * @param minScaleFactor the min scale factor
	 * @param maxScaleFactor the max scale factor
	 */
	public DefaultScaleAction(IMTComponent3D customTarget, float minScaleFactor, float maxScaleFactor){
		this(customTarget, minScaleFactor, maxScaleFactor, true);
	}
	
	
	/**
	 * Instantiates a new default scale action.
	 *
	 * @param customTarget the custom target
	 * @param minScaleFactor the min scale factor
	 * @param maxScaleFactor the max scale factor
	 * @param useScaleLimit  use scale limit
	 */
	private DefaultScaleAction(IMTComponent3D customTarget, float minScaleFactor, float maxScaleFactor, boolean useScaleLimit){
		this.target = customTarget;
		if (minScaleFactor < 0 || maxScaleFactor < 0){
			System.err.println("minScaleFactor < 0 || maxScaleFactor < 0    invalid settings!");
			this.hasScaleLimit = false;
		}else{
			this.hasScaleLimit = useScaleLimit;
		}
		this.minScale = minScaleFactor;
		this.maxScale = maxScaleFactor;
	}
	


	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.IGestureEventListener#processGestureEvent(org.mt4j.input.inputProcessors.MTGestureEvent)
	 */
	public boolean processGestureEvent(MTGestureEvent g) {
		if (g instanceof ScaleEvent){
			ScaleEvent scaleEvent = (ScaleEvent)g;
			this.lastEvent = scaleEvent;
			
			if (target == null)
				target = scaleEvent.getTarget(); 
			
			switch (scaleEvent.getId()) {
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
				doAction(target, scaleEvent);
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


	protected void doAction(IMTComponent3D target, ScaleEvent se) {
		if(!gestureAborted)
		{
			if (this.hasScaleLimit){
				if (target instanceof MTComponent) {
					MTComponent comp = (MTComponent) target;

					//FIXME actually we should use globalmatrix but performance is better for localMatrix..
					Vector3D currentScale = comp.getLocalMatrix().getScale(); 

					//						if (currentScale.x != currentScale.y){
					//							System.out.println("non uniform scale!");
					//						}

					//We only check X because only uniform scales (x=y factor) should be used!
					if (currentScale.x * se.getScaleFactorX() > this.maxScale){
						//							System.out.println("Scale MAX Limit Hit!");
						//We should set to min scale, but we choose performance over accuracy
						//float factor = (1f/currentScale.x) * maxScale;
						//target.scaleGlobal(factor, factor, scaleEvent.getScaleFactorZ(), scaleEvent.getScalingPoint());
					}else if (currentScale.x * se.getScaleFactorX() < this.minScale){
						//							System.out.println("Scale MIN Limit Hit!");
						//We should set to min scale, but we choose performance over accuracy
						//float factor = (1f/currentScale.x) * minScale;
						//target.scaleGlobal(factor, factor, scaleEvent.getScaleFactorZ(), scaleEvent.getScalingPoint());
					}else{
						target.scaleGlobal(
								se.getScaleFactorX(), 
								se.getScaleFactorY(), 
								se.getScaleFactorZ(), 
								se.getScalingPoint());
					}
				}else{

				}
			}else{
				target.scaleGlobal(
						se.getScaleFactorX(), 
						se.getScaleFactorY(), 
						se.getScaleFactorZ(), 
						se.getScalingPoint());
			}
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
