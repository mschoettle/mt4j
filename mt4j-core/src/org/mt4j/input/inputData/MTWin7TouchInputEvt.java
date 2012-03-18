/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
 * The Class MTWin7TouchInputEvt.
 *
 * @author Christopher Ruff
 */
public class MTWin7TouchInputEvt extends MTFingerInputEvt {
	
	/** The contact size x. */
	private int contactSizeX;
	
	/** The contact size y. */
	private int contactSizeY;

	/**
	 * Instantiates a new mT win7 touch input evt.
	 *
	 * @param source the source
	 * @param target the target
	 * @param positionX the position x
	 * @param positionY the position y
	 * @param contactSizeX the contact size x
	 * @param contactSizeY the contact size y
	 * @param id the id
	 * @param c the c
	 */
	public MTWin7TouchInputEvt(
			AbstractInputSource source, 
			IMTComponent3D target, 
			float positionX, 
			float positionY, 
			int contactSizeX,
			int contactSizeY,
			int id,
			InputCursor c
	) {
		super(source, target, positionX, positionY, id, c);
		this.contactSizeX = contactSizeX;
		this.contactSizeY = contactSizeY;
	}

	/**
	 * Instantiates a new mT win7 touch input evt.
	 *
	 * @param source the source
	 * @param positionX the position x
	 * @param positionY the position y
	 * @param contactSizeX the contact size x
	 * @param contactSizeY the contact size y
	 * @param id the id
	 * @param c the c
	 */
	public MTWin7TouchInputEvt(
			AbstractInputSource source, 
			float positionX,
			float positionY, 
			int contactSizeX,
			int contactSizeY,
			int id, 
			InputCursor c
	) {
		super(source, positionX, positionY, id, c);
		this.contactSizeX = contactSizeX;
		this.contactSizeY = contactSizeY;
	}

	/**
	 * Gets the contact size x.
	 *
	 * @return the contact size x
	 */
	public int getContactSizeX() {
		return this.contactSizeX;
	}

	/**
	 * Gets the contact size y.
	 *
	 * @return the contact size y
	 */
	public int getContactSizeY() {
		return this.contactSizeY;
	}
	
	

}
