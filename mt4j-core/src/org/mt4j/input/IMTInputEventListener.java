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
package org.mt4j.input;

import org.mt4j.input.inputData.MTInputEvent;

/**
 * The listener interface for receiving IMTInputEvent events.
 * The class that is interested in processing a IMTInputEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addIMTInputEventListener<code> method. When
 * the IMTInputEvent event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see MTInputEvent
 * 
 * @author Christopher Ruff
 */
public interface IMTInputEventListener {
	
	/**
	 * Process input event.
	 * 
	 * @param inEvt the in evt
	 * 
	 * @return true, if successful
	 */
	public boolean processInputEvent(MTInputEvent inEvt);
	

//	/**
//	 * Process input event.
//	 * 
//	 * @param inEvt the in evt
//	 */
//	public void processInputEvent(MTInputEvent inEvt);

}
