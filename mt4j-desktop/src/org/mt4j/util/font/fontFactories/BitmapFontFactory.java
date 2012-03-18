/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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

import java.util.List;

import org.mt4j.components.visibleComponents.font.BitmapFontCharacter;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.font.fontFactories.IFontFactory;

import processing.core.BitmapFontFactoryProxy;
import processing.core.PApplet;

/**
 * A factory for creating BitmapFont objects.
 */
public class BitmapFontFactory implements IFontFactory {
	
	/** The proxy. */
	private static BitmapFontFactoryProxy proxy; //Using proxy in other package because we need package visibility in org.processing.core ...
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.fontFactories.IFontFactory#getCopy(org.mt4j.components.visibleComponents.font.IFont)
	 */
	public IFont getCopy(IFont font) {
		if (proxy == null){
			proxy = new BitmapFontFactoryProxy();
		}
		return proxy.getCopy(font);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.fontFactories.IFontFactory#createFont(processing.core.PApplet, java.lang.String, int, org.mt4j.util.MTColor)
	 */
	public IFont createFont(PApplet pa, String fontName, int fontSize, MTColor color) {
		return this.createFont(pa, fontName, fontSize, color, true);
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.fontFactories.IFontFactory#createFont(processing.core.PApplet, java.lang.String, int, org.mt4j.util.MTColor, boolean)
	 */
	public IFont createFont(PApplet pa, String fontName, int fontSize, MTColor color, boolean antiAliased) {
		if (proxy == null){
			proxy = new BitmapFontFactoryProxy();
		}
		return proxy.createFont(pa, fontName, fontSize, color, antiAliased);
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.fontFactories.IFontFactory#createFont(processing.core.PApplet, java.lang.String, int, org.mt4j.util.MTColor, org.mt4j.util.MTColor)
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor fillColor, MTColor strokeColor) {
		return this.createFont(pa, fontFileName, fontSize, fillColor, strokeColor, true);
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.fontFactories.IFontFactory#createFont(processing.core.PApplet, java.lang.String, int, org.mt4j.util.MTColor, org.mt4j.util.MTColor, boolean)
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor fillColor, MTColor strokeColor, boolean antiAliased) {
		if (proxy == null){
			proxy = new BitmapFontFactoryProxy();
		}
		return proxy.createFont(pa, fontFileName, fontSize, fillColor, strokeColor, antiAliased);
	}

	
	/**
	 * Gets the characters.
	 *
	 * @param pa the pa
	 * @param chars the chars
	 * @param fillColor the fill color
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @return the characters
	 */
	public List<BitmapFontCharacter> getCharacters(PApplet pa, String chars,
			MTColor fillColor, 
//			MTColor strokeColor, 
			String fontFileName,
			int fontSize) {
		return this.getCharacters(pa, chars, fillColor, /*strokeColor,*/ fontFileName, fontSize, true);
	}
	
	/**
	 * Gets the characters.
	 *
	 * @param pa the pa
	 * @param chars the chars
	 * @param fillColor the fill color
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param antiAliased the anti aliased
	 * @return the characters
	 */
	public List<BitmapFontCharacter> getCharacters(PApplet pa, String chars,
			MTColor fillColor, 
//			MTColor strokeColor, 
			String fontFileName,
			int fontSize, boolean antiAliased
	) {
		if (proxy == null){
			proxy = new BitmapFontFactoryProxy();
		}
		return proxy.getCharacters(pa, chars, fillColor, /*strokeColor,*/ fontFileName, fontSize, antiAliased);
	}

	
	
	

}
