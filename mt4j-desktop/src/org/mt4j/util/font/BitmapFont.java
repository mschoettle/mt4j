/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.BitmapFontCharacter;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.font.IFontCharacter;
import org.mt4j.util.font.ITextureFont;
import org.mt4j.util.font.fontFactories.BitmapFontFactory;
import org.mt4j.util.font.fontFactories.IFontFactory;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * The Class BitmapFont.
 * @author Christopher Ruff
 */
public class BitmapFont implements IFont, ITextureFont {
	/** The Constant logger. */
	private static final ILogger logger = MTLoggerFactory.getLogger(BitmapFont.class.getName());
	static{
//		logger.setLevel(ILogger.ERROR);
//		logger.setLevel(ILogger.WARN);
		logger.setLevel(ILogger.DEBUG);
	}
	
	/** The characters. */
	private BitmapFontCharacter[] characters;
	
	/** The default horizontal adv x. */
	private int defaultHorizontalAdvX;
	
	/** The font family. */
	private String fontFamily;
	
	/** The original font size. */
	private int originalFontSize;
	
	/** The font max ascent. */
	private int fontMaxAscent;
	
	/** The font max descent. */
	private int fontMaxDescent;
	
	/** The units per em. */
	private int unitsPerEM;
	
	/** The font file name. */
	private String fontFileName;
	
	/** The uni code to char. */
	private HashMap<String, BitmapFontCharacter> uniCodeToChar;
	
	/** The char name to char. */
	private HashMap<String, BitmapFontCharacter> charNameToChar;
	
	/** The fill color. */
	private MTColor fillColor;
	
//	/** The stroke color. */
//	private MTColor strokeColor;
	
	private List<String> notAvailableChars;

	private boolean antiAliased;
	
	
	/**
	 * Instantiates a new bitmap font.
	 *
	 * @param characters the characters
	 * @param defaultHorizontalAdvX the default horizontal adv x
	 * @param fontFamily the font family
	 * @param fontMaxAscent the font max ascent
	 * @param fontMaxDescent the font max descent
	 * @param unitsPerEm the units per em
	 * @param originalFontSize the original font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * @param antiAliased the anti aliased
	 * @param fontFileName2 
	 */
	public BitmapFont(BitmapFontCharacter[] characters, int defaultHorizontalAdvX, String fontFamily, int fontMaxAscent, int fontMaxDescent, int unitsPerEm, int originalFontSize,
			MTColor fillColor,
//			MTColor strokeColor,
			boolean antiAliased, 
			String fontFileName
	) {
		this.characters = characters;
		this.defaultHorizontalAdvX = defaultHorizontalAdvX;
		this.fontFamily = fontFamily;
		this.originalFontSize = originalFontSize;
		this.fillColor = fillColor;
//		this.strokeColor = strokeColor;
		this.antiAliased = antiAliased;
		
		this.fontFileName = fontFileName;
		
//		this.fontId = "";
		
		this.fontMaxAscent 	= fontMaxAscent;
		this.fontMaxDescent = fontMaxDescent;
		
		this.unitsPerEM = unitsPerEm;
		
		//Put characters in hashmaps for quick access
		uniCodeToChar 	= new HashMap<String, BitmapFontCharacter>();
		charNameToChar 	= new HashMap<String, BitmapFontCharacter>();

        for (BitmapFontCharacter currentChar : characters) {
            uniCodeToChar.put(currentChar.getUnicode(), currentChar);
            charNameToChar.put(currentChar.getName(), currentChar);
        }
		
		notAvailableChars = new ArrayList<String>();
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFontCharacterByName(java.lang.String)
	 */
	public IFontCharacter getFontCharacterByName(String characterName){
		BitmapFontCharacter returnChar = charNameToChar.get(characterName);
		if (returnChar == null)
			logger.warn("Font couldnt load charactername: " + characterName);
		return returnChar;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFontCharacterByUnicode(java.lang.String)
	 */
	public IFontCharacter getFontCharacterByUnicode(String unicode){
		BitmapFontCharacter returnChar = uniCodeToChar.get(unicode);
		if (returnChar == null){
			logger.warn("Font couldnt load characterunicode: '" + unicode + "'");
			
			//This is a kind of hacky way to try to dynamically load characters from
			//a font that were not loaded by default. 
			if (!unicode.equalsIgnoreCase("missing-glyph")
				&& !isInNotAvailableList(unicode) 
				&& fontFileName != null
				&& fontFileName.length() > 0
			){
				IFontFactory fontFactory = FontManager.getInstance().getFactoryForFileSuffix("");
				if (fontFactory != null && fontFactory instanceof BitmapFontFactory){
					BitmapFontFactory bitmapFontFactory = (BitmapFontFactory)fontFactory;
					if (this.getCharacters().length > 0 && this.getCharacters()[0] != null && this.getCharacters()[0] instanceof MTComponent){
						MTComponent comp = (MTComponent)this.getCharacters()[0];
						PApplet pa = comp.getRenderer();
						List<BitmapFontCharacter> charactersList = bitmapFontFactory.getCharacters(pa, unicode, fillColor, /*strokeColor,*/ this.fontFileName, this.originalFontSize, this.antiAliased);
						BitmapFontCharacter[] characters = charactersList.toArray(new BitmapFontCharacter[charactersList.size()]); 
						if (characters.length >= 1 && characters[0] != null){
							BitmapFontCharacter loadedCharacter = characters[0];
							BitmapFontCharacter[] newArray = new BitmapFontCharacter[this.getCharacters().length + 1];
							System.arraycopy(this.getCharacters(), 0, newArray, 0, this.getCharacters().length);
							newArray[newArray.length-1] = loadedCharacter;
							this.setCharacters(newArray);
							returnChar = loadedCharacter;
							logger.debug("Re-loaded missing character: '" + unicode + "' from the font: " + this.fontFileName);
						}
					}
				}
				
				if (returnChar == null){
					if (!isInNotAvailableList(unicode)){
						logger.debug("Couldnt re-load the character: '" + unicode + "' -> adding to ignore list.");
						notAvailableChars.add(unicode);	
					}
				}
			}
			
		}
		return returnChar;
	}
	
	
	private boolean isInNotAvailableList(String unicode){
		boolean blackListed = false;
		for (String s : notAvailableChars){
			if (s.equalsIgnoreCase(unicode)){
				blackListed = true;
			}
		}
		return blackListed;
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getCharacters()
	 */
	public IFontCharacter[] getCharacters() {
		return this.characters;
	}
	
	/**
	 * Sets the characters for the font.
	 * @param characters the new characters
	 */
	public void setCharacters(BitmapFontCharacter[] characters) {
		uniCodeToChar.clear();
		charNameToChar.clear();
        for (BitmapFontCharacter currentChar : characters) {
            uniCodeToChar.put(currentChar.getUnicode(), currentChar);
            charNameToChar.put(currentChar.getName(), currentChar);
        }
		this.characters = characters;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getDefaultHorizontalAdvX()
	 */
	public int getDefaultHorizontalAdvX() {
		return this.defaultHorizontalAdvX;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFontAbsoluteHeight()
	 */
	public int getFontAbsoluteHeight() {
		return ((Math.abs(this.getFontMaxAscent())) + (Math.abs(this.getFontMaxDescent())));
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFontFamily()
	 */
	public String getFontFamily() {
		return this.fontFamily;
	}
	
	/**
	 * Sets the font file name.
	 * 
	 * @param fileName the new font file name
	 */
	public void setFontFileName(String fileName){
		this.fontFileName = fileName;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFontFileName()
	 */
	public String getFontFileName() {
		return this.fontFileName;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFontMaxAscent()
	 */
	public int getFontMaxAscent() {
		return this.fontMaxAscent;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFontMaxDescent()
	 */
	public int getFontMaxDescent() {
		return this.fontMaxDescent;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getOriginalFontSize()
	 */
	public int getOriginalFontSize() {
		return this.originalFontSize;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getUnitsPerEM()
	 */
	public int getUnitsPerEM() {
		return this.unitsPerEM;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFillColor()
	 */
	public MTColor getFillColor() {
		return fillColor;
	}

//	/* (non-Javadoc)
//	 * @see org.mt4j.components.visibleComponents.font.IFont#getStrokeColor()
//	 */
//	public MTColor getStrokeColor() {
//		return strokeColor;
//	}

	public void setFillColor(MTColor color){
		this.fillColor = color;
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#isAntiAliased()
	 */
	public boolean isAntiAliased() {
		return this.antiAliased;
	}


	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#destroy()
	 */
	public void destroy() {
		IFontCharacter[] characters = this.getCharacters();
        for (IFontCharacter iFontCharacter : characters) {
            iFontCharacter.destroy();
        }
		FontManager.getInstance().removeFromCache(this);
	}


	@Override
	public void beginBatchRenderGL(GL10 gl, IFont font) {
		MTColor fillColor = font.getFillColor();
		gl.glColor4f(fillColor.getR()/255f, fillColor.getG()/255f, fillColor.getB()/255f, fillColor.getAlpha()/255f); 
		
		//Default texture target -> has to be POT dimensions
		int textureTarget = GL10.GL_TEXTURE_2D;
		gl.glEnable(textureTarget);
		
		//Enable Pointers, set vertex array pointer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}


	@Override
	public void endBatchRenderGL(GL10 gl, IFont font) {
		int textureTarget = GL10.GL_TEXTURE_2D;
		gl.glBindTexture(textureTarget, 0);//Unbind texture
		gl.glDisable(textureTarget); 
		
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}


	@Override
	public boolean isEqual(IFont font){
		if (font instanceof BitmapFont) {
			if (
					font.getFontFileName().equalsIgnoreCase(getFontFileName())
					&& 	
					font.getOriginalFontSize() == getOriginalFontSize()
					&&
					font.isAntiAliased() == antiAliased	
			){
				return true;		
			}
		}
		return false;
	}




	
	

}
