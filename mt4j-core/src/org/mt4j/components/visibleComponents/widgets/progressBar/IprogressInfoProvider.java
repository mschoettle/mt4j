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
package org.mt4j.components.visibleComponents.widgets.progressBar;

/**
 * The Interface IprogressInfoProvider.
 */
public interface IprogressInfoProvider {
	
	
	/**
	 * Gets the percentage finished.
	 * 
	 * @return the percentage finished
	 */
	public float getPercentageFinished();
	
	/**
	 * Gets the current.
	 * 
	 * @return the current
	 */
	public float getCurrent();
	
	/**
	 * Gets the current action.
	 * 
	 * @return the current action
	 */
	public String getCurrentAction();

	/**
	 * Checks if is finished.
	 * 
	 * @return true, if is finished
	 */
	public boolean isFinished();

	/**
	 * Gets the target.
	 * 
	 * @return the target
	 */
	public float getTarget();
	
	
	
}
