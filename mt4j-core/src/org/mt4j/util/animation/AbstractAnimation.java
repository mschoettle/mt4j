package org.mt4j.util.animation;

import java.util.ArrayList;

public abstract class AbstractAnimation implements IAnimation {
	/** The animation listeners. */
	private ArrayList<IAnimationListener> animationListeners; 
	
	/** The target object. */
	protected Object targetObject; 
	
	public AbstractAnimation(Object targetObject){
		this.animationListeners = new ArrayList<IAnimationListener>();
		this.targetObject = targetObject;
	}
	
	
	/**
	 * Fire animation event.
	 * 
	 * @param anev the anev
	 */
	protected void fireAnimationEvent(AnimationEvent anev) {
		synchronized(animationListeners) {
			int size = animationListeners.size();
			for (int i = 0; i < size; i++) {
				IAnimationListener listener = (IAnimationListener)animationListeners.get(i);
				listener.processAnimationEvent(anev);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.util.animation.IAnimation#addAnimationListener(org.mt4j.util.animation.IAnimationListener)
	 */
	public synchronized IAnimation addAnimationListener(IAnimationListener listener){
		if (!animationListeners.contains(listener)){
			animationListeners.add(listener);
		}
		return this;
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.util.animation.IAnimation#removeAnimationListener(org.mt4j.util.animation.IAnimationListener)
	 */
	public synchronized void removeAnimationListener(IAnimationListener listener){
		if (animationListeners.contains(listener)){
			animationListeners.remove(listener);
		}
	}
	
	/**
	 * Removes the all animation listeners.
	 */
	public synchronized void removeAllAnimationListeners(){
		animationListeners.clear();
	}
	
	/**
	 * Gets the animation listeners.
	 * 
	 * @return the animation listeners
	 */
	public synchronized IAnimationListener[] getAnimationListeners(){
		return animationListeners.toArray(new IAnimationListener[this.animationListeners.size()]);
	}
	
	
	/**
	 * Gets the target object.
	 * 
	 * @return the target object
	 */
	public Object getTarget() {
		return targetObject;
	}


}
