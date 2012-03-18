package org.mt4j.input.inputProcessors.componentProcessors.flickProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.math.Vector3D;

/**
 * The Class FlickEvent.
 */
public class FlickEvent extends MTGestureEvent {
	
	/** The direction. */
	private FlickDirection direction;
	
	/** The is flick. */
	private boolean isFlick;
	
	/**
	 * The Enum FlickDirection.
	 */
	public enum FlickDirection{
		 
 		/** The WEST. */
 		WEST,
		 
 		/** The NORT h_ west. */
 		NORTH_WEST,
		 
 		/** The NORTH. */
 		NORTH,
		 
 		/** The NORT h_ east. */
 		NORTH_EAST,
		 
 		/** The EAST. */
 		EAST,
		 
 		/** The SOUT h_ east. */
 		SOUTH_EAST,
		 
 		/** The SOUTH. */
 		SOUTH,
		 
 		/** The SOUT h_ west. */
 		SOUTH_WEST, 
		 
 		/** The UNDETERMINED. */
 		UNDETERMINED,
	}
	

	/**
	 * Instantiates a new flick event.
	 *
	 * @param source the source
	 * @param id the id
	 * @param targetComponent the target component
	 * @param direction the direction
	 * @param isFlickComplete the is flick complete
	 */
	public FlickEvent(IInputProcessor source, int id, IMTComponent3D targetComponent, FlickDirection direction, boolean isFlickComplete) {
		super(source, id, targetComponent);
		this.direction = direction;
		this.isFlick = isFlickComplete;
	}

	/**
	 * Gets the direction.
	 *
	 * @return the direction
	 */
	public FlickDirection getDirection() {
		return direction;
	}

	/**
	 * Checks if is flick.
	 *
	 * @return true, if is flick
	 */
	public boolean isFlick() {
		return isFlick;
	}
	

}
