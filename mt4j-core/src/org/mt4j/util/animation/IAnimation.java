package org.mt4j.util.animation;

public interface IAnimation {

	/**
	 * Start.
	 */
	public abstract void start();

	/**
	 * Stop.
	 */
	public abstract void stop();

	/**
	 * Adds the animation listener.
	 * 
	 * @param listener the listener
	 */
	public abstract IAnimation addAnimationListener(IAnimationListener listener);

	/**
	 * Removes the animation listener.
	 * 
	 * @param listener the listener
	 */
	public abstract void removeAnimationListener(IAnimationListener listener);

	public abstract float getDelta();

	public abstract float getValue();
	
	public Object getTarget();

	public abstract IAnimationListener[] getAnimationListeners();

}