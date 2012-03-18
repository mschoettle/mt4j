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
package org.mt4j.input;

/**
 * The Class MTEvent. The base class for most events used in mt4j framework.
 * @author Christopher Ruff
 */
public class MTEvent {
	
	/** The source. */
	private Object source;
	
	/** The creation time stamp. */
	private long timeStamp;
	
//	/** The consumed. */
//	private boolean consumed;
	
	/**
	 * Instantiates a new mT event.
	 * 
	 * @param source the source
	 */
	public MTEvent(Object source) {
		super();
//		consumed = false;
		this.source = source;
		this.timeStamp = System.currentTimeMillis();
	}

	/**
	 * Gets the source.
	 * 
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}
	
	/**
	 * Gets the when.
	 * 
	 * @return the when
	 */
	public long getTimeStamp(){
		return timeStamp;
	}

//	/**
//	 * Checks if is consumed.
//	 * 
//	 * @return true, if is consumed
//	 */
//	public boolean isConsumed() {
//		return consumed;
//	}
//
//	/**
//	 * Sets the consumed.
//	 * 
//	 * @param consumed the new consumed
//	 */
//	public void setConsumed(boolean consumed) {
//		this.consumed = consumed;
//	}

	
}
