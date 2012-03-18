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
package org.mt4j.util.animation;

import org.mt4j.input.MTEvent;

/**
 * The Class AnimationEvent.
 * @author Christopher Ruff
 */
public class AnimationEvent extends MTEvent {
	
	/** The id. */
	private int id;
	
	/** The animation. */
	private IAnimation animation;
	
	/** The target object. */
	private Object targetObject;
	
	/** The Constant ANIMATION_STARTED. */
	public static final int ANIMATION_STARTED = 0;
	
	/** The Constant ANIMATION_UPDATED. */
	public static final int ANIMATION_UPDATED = 1;
	
	/** The Constant ANIMATION_ENDED. */
	public static final int ANIMATION_ENDED	  = 2;
	

	/**
	 * Instantiates a new animation event.
	 * 
	 * @param source the source
	 * @param id the id
	 * @param animation the animation
	 */
	public AnimationEvent(Object source, int id, IAnimation animation) {
		this(source, id, animation, null);
	}
	
	/**
	 * Instantiates a new animation event.
	 * 
	 * @param source the source
	 * @param id the id
	 * @param animation the animation
	 * @param targetObject the target object
	 */
	public AnimationEvent(Object source, int id, IAnimation animation, Object targetObject) {
		super(source);
		
		this.id = id;
		this.animation = animation;
		this.targetObject = targetObject;
	}


	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * Gets the animation.
	 * 
	 * @return the animation
	 */
	public IAnimation getAnimation() {
		return animation;
	}
	
	/**
	 * Gets the current step delta - the difference between the last value and the current value.
	 * 
	 * @return the current step delta
	 * @deprecated use getDelta() instead
	 * @see #getDelta()
	 */
	public float getCurrentStepDelta(){
		return this.getAnimation().getDelta();
	}
	
	/**
	 * Gets the current absolute value of the interpolated value.
	 * 
	 * @return the current value
	 * @deprecated use getValue() instead
	 * @see #getValue()
	 */
	public float getCurrentValue(){
		return this.getAnimation().getValue();
	}
	
	public float getValue(){
		return this.getAnimation().getValue();
	}
	
	public float getDelta(){
		return this.getAnimation().getDelta();
	}

	/**
	 * returns the target for the animation
	 * <br>Note: can be null!.
	 * 
	 * @return the target object of this animation - if it is set
	 * @deprecated use getTaget() instead
	 * @see #getTarget()
	 */
	public Object getTargetObject() {
		return targetObject;
	}
	
	/**
	 * returns the target for the animation
	 * <br>Note: can be null!.
	 * 
	 * @return the target object of this animation - if it is set
	 */
	public Object getTarget(){
		return this.targetObject;
	}
	
	

}
