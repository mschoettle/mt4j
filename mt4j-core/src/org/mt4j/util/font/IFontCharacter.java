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
package org.mt4j.util.font;

import org.mt4j.util.opengl.GL10;

import processing.core.PGraphics;

/**
 * The Interface IFontCharacter.
 * @author Christopher Ruff
 */
public interface IFontCharacter {
	
	/**
	 * Draw component.
	 * 
	 * @param g the graphics
	 */
	public void drawComponent(PGraphics g);

	/**
	 * Draw component.
	 * 
	 * @param gl the gl
	 */
	public void drawComponent(GL10 gl);
	
	/**
	 * Gets the unicode.
	 * 
	 * @return the unicode
	 */
	public String getUnicode();
	
	
	public int getKerning(String character);
	
	
	/**
	 * The horizontal advancement distance specifies, how many units
	 * to the right we have to move before drawing the NEXT character.
	 * 
	 * @return the horizontal dist
	 * 
	 * the distance advancement
	 */
	public int getHorizontalDist();
	
	
	/**
	 * Destroys the character and its used resources.
	 */
	public void destroy();
}
