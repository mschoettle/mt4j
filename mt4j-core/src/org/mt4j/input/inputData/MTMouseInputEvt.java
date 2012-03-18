package org.mt4j.input.inputData;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputSources.AbstractInputSource;

/**
 * The Class MTMouseInputEvt.
 */
public class MTMouseInputEvt extends MTFingerInputEvt {

	/** The mouse button. */
	private int mouseModifiers;

	/**
	 * Instantiates a new mT mouse input evt.
	 *
	 * @param source the source
	 * @param target the target
	 * @param mouseModifiers the mouse modifiers
	 * @param positionX the position x
	 * @param positionY the position y
	 * @param id the id
	 * @param m the m
	 */
	public MTMouseInputEvt(AbstractInputSource source, IMTComponent3D target, int mouseModifiers, float positionX, float positionY, int id, InputCursor m) {
		super(source, target, positionX, positionY, id, m);
		this.mouseModifiers = mouseModifiers;
	}

	/**
	 * Instantiates a new mT mouse input evt.
	 *
	 * @param source the source
	 * @param mouseModifiers the mouse modifiers
	 * @param positionX the position x
	 * @param positionY the position y
	 * @param id the id
	 * @param m the m
	 */
	public MTMouseInputEvt(AbstractInputSource source, int mouseModifiers, float positionX, float positionY, int id, InputCursor m) {
		super(source, positionX, positionY, id, m);
		this.mouseModifiers = mouseModifiers;
	}
	
	/**
	 * Gets the modifiers.
	 *
	 * @return the modifiers
	 */
	public int getModifiers(){
		return this.mouseModifiers;
	}

}
