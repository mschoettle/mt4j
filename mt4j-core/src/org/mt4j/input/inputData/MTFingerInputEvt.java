/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.input.inputData;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputSources.AbstractInputSource;

/**
 * The Class MTFingerInputEvt.
 * 
 * @author Christopher Ruff
 */
public class MTFingerInputEvt extends AbstractCursorInputEvt{

	
	/**
	 * Instantiates a new mT finger input evt.
	 * 
	 * @param source the source
	 * @param positionX the position x
	 * @param positionY the position y
	 * @param id the id
	 * @param m the m
	 */
	public MTFingerInputEvt(
			AbstractInputSource source, 
			float positionX,
			float positionY, 
			int id,
			InputCursor m
	) {
		super(source, positionX, positionY, id, m);
	}
	
	/**
	 * Instantiates a new mT finger input evt.
	 * 
	 * @param source the source
	 * @param target the target
	 * @param positionX the position x
	 * @param positionY the position y
	 * @param id the id
	 * @param m the m
	 */
	public MTFingerInputEvt(
			AbstractInputSource source, 
			IMTComponent3D target,
			float positionX,
			float positionY, 
			int id,
			InputCursor m
	) {
		super(source, target, positionX, positionY, id, m);
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputData.AbstractCursorInputEvt#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new MTFingerInputEvt((AbstractInputSource) this.getSource(), this.getX(), this.getY(), this.getId(), this.getCursor());
	}

	
//	/** The Constant FINGER_DOWN. */
//	public static final int FINGER_DOWN = 0;
//	
//	/** The Constant FINGER_UPDATE. */
//	public static final int FINGER_UPDATE = 1;
//	
//	/** The Constant FINGER_UP. */
//	public static final int FINGER_UP = 2;
	
//	
//	
//	/** The has been fired. */
//	private boolean hasBeenFired; //TODO remove?
//	
//	/** The position x. */
//	private float positionX;
//	
//	/** The position y. */
//	private float positionY;
//	
//	/** The id. */
//	private int id;
//	
//	
//	/** The added to motion. */
//	private boolean addedToMotion;
//	
//	/** The associated motion. */
//	private InputMotion<? extends MTConcretePositionEvt> associatedMotion;
//	
//	/**
//	 * Instantiates a new touch event.
//	 * 
//	 * @param source the source
//	 * @param positionX the position x
//	 * @param positionY the position y
//	 * @param id the id
//	 * @param m the m
//	 */
//	public MTFingerInputEvt(AbstractInputSource source, float positionX, float positionY, int id, InputMotion<? extends MTConcretePositionEvt> m) {
//		super(source); 
//		hasBeenFired = false;
//		this.id = id;
//		
//		this.positionX = positionX;
//		this.positionY = positionY;
//		
//		this.associatedMotion = m;
//		
//		this.addedToMotion = false;
//	}
//	
//	
//	
//	/**
//	 * Checks if is added to motion.
//	 * 
//	 * @return true, if is added to motion
//	 */
//	public boolean isAddedToMotion() {
//		return addedToMotion;
//	}
//
//
//
//	/**
//	 * Sets the added to motion.
//	 * 
//	 * @param addedToMotion the new added to motion
//	 */
//	public void setAddedToMotion(boolean addedToMotion) {
//		this.addedToMotion = addedToMotion;
//	}
//
//
//
//	/**
//	 * Checks if is checks for been fired.
//	 * 
//	 * @return true, if is checks for been fired
//	 */
//	public boolean isHasBeenFired() {
//		return hasBeenFired;
//	}
//
//	
//	/**
//	 * Gets the motion.
//	 * 
//	 * @return the motion
//	 */
//	public InputMotion<? extends MTConcretePositionEvt> getMotion() {
//		return associatedMotion;
//	}
//
//
//	/**
//	 * Sets the checks for been fired.
//	 * 
//	 * @param hasBeenFired the new checks for been fired
//	 */
//	public void setHasBeenFired(boolean hasBeenFired) {
//		this.hasBeenFired = hasBeenFired;
//	}
//	
//	
//	/**
//	 * Gets the position x.
//	 * 
//	 * @return the position x
//	 */
//	public float getPositionX() {
//		return positionX;
//	}
//
//	
//	/**
//	 * Gets the position y.
//	 * 
//	 * @return the position y
//	 */
//	public float getPositionY() {
//		return positionY;
//	}
//
//	
//	/**
//	 * Gets the id.
//	 * 
//	 * @return the id
//	 */
//	public int getId() {
//		return id;
//	}
//
//
//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	public String toString(){
//		return new String(super.toString() + "; " + " PosX: " + positionX + " PosY: " + positionY + " InputSource: " + this.getSource()); 
//	}

}
