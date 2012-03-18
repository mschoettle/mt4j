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

import org.mt4j.components.interfaces.IMTComponent3D;

// TODO: Auto-generated Javadoc
/**
 * The Interface IHitTestInfoProvider.
 */
public interface IHitTestInfoProvider {
	
	/**
	 * Gets the object at.
	 * 
	 * @param x the x
	 * @param y the y
	 * 
	 * @return the object at
	 */
	public IMTComponent3D getComponentAt(float x, float y);
	
	/**
	 * Checks if is back ground at.
	 * 
	 * @param x the x
	 * @param y the y
	 * 
	 * @return true, if is back ground at
	 */
	public boolean isBackGroundAt(float x, float y);

}
