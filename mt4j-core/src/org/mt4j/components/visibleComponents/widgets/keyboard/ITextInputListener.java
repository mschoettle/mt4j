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
package org.mt4j.components.visibleComponents.widgets.keyboard;

/**
 * Interface for classes that can be written text to.
 * 
 * @author Christopher Ruff
 */
public interface ITextInputListener {
	
	/**
	 * Clear.
	 */
	public void clear();
	
	/**
	 * Append text.
	 * 
	 * @param text the text
	 */
	public void appendText(String text);
	
	/**
	 * Sets the text.
	 * 
	 * @param text the new text
	 */
	public void setText(String text);
	
//	/**
//	 * Returns the text.
//	 * <br>NOTE: the text may be different from what was set using <code>setText()</code> since
//	 * the widget may add its own control characters into the text.
//	 * 
//	 * @return the text
//	 */
//	public String getText();
		
	/**
	 * Append char by unicode.
	 * 
	 * @param unicode the unicode
	 */
	public void appendCharByUnicode(String unicode);
	
	/**
	 * Removes the last character.
	 */
	public void removeLastCharacter();

}
