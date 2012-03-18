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
package org.mt4j.input.inputProcessors;

import org.mt4j.input.inputData.MTInputEvent;


/**
 * The Interface IInputProcessor.
 * 
 * @author Christopher Ruff
 */
public interface IInputProcessor {
	
//	/**
//	 * Gets the motion locking priority.
//	 * 
//	 * @return the locking priority
//	 */
//	public int getLockPriority();
	
	
//	/**
//	 * Process input evt implementation.
//	 * 
//	 * @param inputEvent the input event
//	 */
//	abstract public void processInputEvtImpl(MTInputEvent inputEvent);
	
	/**
	 * Process input evt.
	 * 
	 * @param inputEvent the input event
	 */
	abstract public boolean processInputEvent(MTInputEvent inputEvent);
	
	
	/**
	 * Checks if is disabled.
	 * 
	 * @return true, if is disabled
	 */
	public boolean isDisabled();

	/**
	 * Sets the disabled.
	 * 
	 * @param disabled the new disabled
	 */
	public void setDisabled(boolean disabled);


}
