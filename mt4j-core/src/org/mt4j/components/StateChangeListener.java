/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.components;

/**
 * The listener interface for receiving stateChange events.
 * The class that is interested in processing a stateChange
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addStateChangeListener<code> method. When
 * the stateChange event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see StateChangeEvent
 * 
 * @author Christopher Ruff
 */
public interface StateChangeListener {
	
	/**
	 * State changed.
	 * 
	 * @param evt the evt
	 */
	public void stateChanged(StateChangeEvent evt);

}
