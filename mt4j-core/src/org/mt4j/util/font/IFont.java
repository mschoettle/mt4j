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

import org.mt4j.util.MTColor;
import org.mt4j.util.opengl.GL10;

/**
 * The Interface IFont.
 * @author Christopher Ruff
 */
public interface IFont {
	
	/**
	 * Gets the font character by name.
	 * 
	 * @param characterName the character name
	 * 
	 * @return the font character by name
	 */
	public IFontCharacter getFontCharacterByName(String characterName);
	
	/**
	 * Gets the font character by unicode.
	 * 
	 * @param unicode the unicode
	 * 
	 * @return the font character by unicode
	 */
	public IFontCharacter getFontCharacterByUnicode(String unicode);
	
	/**
	 * Gets the characters.
	 * 
	 * @return the characters
	 */
	public IFontCharacter[] getCharacters();
	
	/**
	 * Gets the font family.
	 * 
	 * @return the font family
	 */
	public String getFontFamily();
	
	/**
	 * Gets the default horizontal adv x.
	 * 
	 * @return the default horizontal adv x
	 */
	public int getDefaultHorizontalAdvX();
	
	/**
	 * Gets the font max ascent.
	 * 
	 * @return the font max ascent
	 */
	public int getFontMaxAscent();

	/**
	 * Gets the font max descent.
	 * <br>NOTE: this often is a negative value 
	 * 
	 * @return the font max descent
	 */
	public int getFontMaxDescent();
	
	/**
	 * Gets the units per em.
	 * 
	 * @return the units per em
	 */
	public int getUnitsPerEM();
	
	/**
	 * Gets the font file name.
	 * 
	 * @return the font file name
	 */
	public String getFontFileName();
	
	/**
	 * Gets the original font size.
	 * 
	 * @return the original font size
	 */
	public int getOriginalFontSize();

	/**
	 * Gets the font absolute height.
	 * 
	 * @return the font absolute height
	 */
	public int getFontAbsoluteHeight();
	
	
	/**
	 * Gets the fill color.
	 * 
	 * @return the fill color
	 */
	public MTColor getFillColor();
	
//	/**
//	 * Gets the stroke color.
//	 * 
//	 * @return the stroke color
//	 */
//	public MTColor getStrokeColor();
	
	
	public void setFillColor(MTColor color);
	
	
	/**
	 * Checks if is anti aliased.
	 *
	 * @return true, if is anti aliased
	 */
	public boolean isAntiAliased();
	
	/**
	 * Destroys the font's characters and removes it from the FontManager's cache.
	 */
	public void destroy();

	
	
	public void beginBatchRenderGL(GL10 gl, IFont font);

	public void endBatchRenderGL(GL10 gl, IFont font);

	public boolean isEqual(IFont font);
}
