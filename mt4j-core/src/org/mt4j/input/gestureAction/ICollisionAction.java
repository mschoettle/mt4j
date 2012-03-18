package org.mt4j.input.gestureAction;

import org.mt4j.input.inputProcessors.MTGestureEvent;

/**
 * The Interface ICollisionAction.
 */
public interface ICollisionAction {
	
	/**
	 * Gesture aborted.
	 *
	 * @return true, if successful
	 */
	public boolean gestureAborted();
	
	/**
	 * Sets the gesture aborted.
	 *
	 * @param aborted the new gesture aborted
	 */
	public void setGestureAborted(boolean aborted);
	
	/**
	 * Gets the last event.
	 *
	 * @return the last event
	 */
	public MTGestureEvent getLastEvent();
}
