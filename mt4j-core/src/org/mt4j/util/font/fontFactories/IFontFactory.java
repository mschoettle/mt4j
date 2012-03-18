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
package org.mt4j.util.font.fontFactories;

import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;

import processing.core.PApplet;

/**
 * A factory for creating IFont objects.
 * @author Christopher Ruff
 */
public interface IFontFactory {
	
	/**
	 * Creates a new IFont object.
	 * 
	 * @param pa the pa
	 * @param fontName the svg font file name
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * 
	 * @return the iFont
	 * 
	 * @deprecated font system only allows a single font color now
	 */
	public IFont createFont(
			PApplet pa, 
			String fontName, 
			int fontSize, 
			MTColor fillColor, 
			MTColor strokeColor);
	
	
	
	/**
	 * Creates a new IFont object.
	 *
	 * @param pa the pa
	 * @param fontName the svg font file name
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * @param antiAliased the anti aliased
	 * @return the iFont
	 * 
	 * @deprecated font system only allows a single font color now
	 */
	public IFont createFont(
			PApplet pa, 
			String fontName, 
			int fontSize, 
			MTColor fillColor, 
			MTColor strokeColor,
			boolean antiAliased
			);
	
	
	
	/**
	 * Creates a new IFont object.
	 *
	 * @param pa the pa
	 * @param fontName the font name
	 * @param fontSize the font size
	 * @param color the color
	 * @return the i font
	 */
	public IFont createFont(
			PApplet pa, 
			String fontName, 
			int fontSize, 
			MTColor color);
	
	/**
	 * Creates a new IFont object.
	 *
	 * @param pa the pa
	 * @param fontName the font name
	 * @param fontSize the font size
	 * @param color the color
	 * @param antiAliased the anti aliased
	 * @return the i font
	 */
	public IFont createFont(
			PApplet pa, 
			String fontName, 
			int fontSize, 
			MTColor color,
			boolean antiAliased);



	public IFont getCopy(IFont font);
}
