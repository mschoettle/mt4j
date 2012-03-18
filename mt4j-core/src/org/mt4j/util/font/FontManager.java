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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.fontFactories.IFontFactory;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;

import processing.core.PApplet;

/**
 * Manager for creating fonts. Manages a set of IFontFactory classes.
 * 
 * @author Christopher Ruff
 */
public class FontManager {
	private static final ILogger logger = MTLoggerFactory.getLogger(FontManager.class.getName());
	static{
		logger.setLevel(ILogger.INFO);
	}
	
	/** The font manager. */
	private static FontManager fontManager;
	
	/** The fonts. */
	private ArrayList<IFont> fonts;
	
	/** The suffix to factory. */
	private HashMap<String, IFontFactory> suffixToFactory;
	
	private static final int CACHE_MAX_SIZE = 10;
	
	
	public static String DEFAULT_FONT = "SansSerif";
	public static int DEFAULT_FONT_SIZE = 16;
	public static MTColor DEFAULT_FONT_FILL_COLOR = MTColor.BLACK;
	public static MTColor DEFAULT_FONT_STROKE_COLOR = MTColor.BLACK;
	public static boolean DEFAULT_FONT_ANTIALIASING = true;
	
	//TODO fonts seem to be one of the main memory eaters in MT4j!
	//somehow the fontmanager seems to grow bigger and bigger (at least it seems at profiling
	//although the same cached(!) fonts are used
	
	
	/**
	 * Instantiates a new font manager.
	 */
	private FontManager(){
		fonts = new ArrayList<IFont>();
		suffixToFactory = new HashMap<String, IFontFactory>();
	}
	
	/**
	 * Gets the instance.
	 * 
	 * @return the instance
	 * 
	 * this VectorFontManager, use <code>createFont</code> to create a font with it
	 */
	public static FontManager getInstance(){ 
		if (fontManager == null){
			fontManager = new FontManager();
			return fontManager;
		}else{
			return fontManager;
		}
	}
	
	/**
	 * Gets the default font.
	 *
	 * @param app the app
	 * @return the default font
	 */
	public IFont getDefaultFont(PApplet app){
		return createFont(app, DEFAULT_FONT, DEFAULT_FONT_SIZE, new MTColor(DEFAULT_FONT_STROKE_COLOR), DEFAULT_FONT_ANTIALIASING);
	}
	
	
	/**
	 * Creates the font.
	 *
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param antiAliased the anti aliased
	 * @return the i font
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, boolean antiAliased){
		return createFont(pa, fontFileName, fontSize, new MTColor(DEFAULT_FONT_FILL_COLOR), antiAliased);
	}
	
	
	/**
	 * Loads and returns a font from a file.
	 * <br>The file has to be located in the ./data/ directory of the program.
	 * <br>Example: "IFont font = FontManager.createFont(papplet, "Pakenham.svg", 100);"
	 * 
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * 
	 * @return the i font
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize){
		return createFont(pa, fontFileName, fontSize, new MTColor(DEFAULT_FONT_FILL_COLOR));
	}
	
	/**
	 * Loads and returns a vector font from a file.
	 * <br>The file has to be located in the ./data/ directory of the program.
	 * 
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * 
	 * @return the i font
	 * @deprecated from now on, only a single font color is supported for conformity across factories
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor fillColor, MTColor strokeColor) {
		return this.createFont(pa, fontFileName, fontSize, fillColor, strokeColor, true);
	}
	
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor color) {
		return this.createFont(pa, fontFileName, fontSize, color, true);
	}
	

	/**
	 * Creates the font.
	 *
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * @param antiAliased the anti aliased
	 * @return the i font
	 * @deprecated from now on, only a single font color is supported for conformity across factories 
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor fillColor, MTColor strokeColor, boolean antiAliased) {
		return this.createFont(pa, fontFileName, fontSize, fillColor, antiAliased);
	}
	
	/**
	 * Loads and returns a vector font from a file.
	 * <br>The file has to be located in the ./data/ directory of the program.
	 *
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * @param antiAliased the anti aliased
	 * @return the i font
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor fillColor, boolean antiAliased) {
//		String fontAbsoultePath = System.getProperty("user.dir") + File.separator + "data" + File.separator + "fonts"+  File.separator + fontFileName;
		String fontAbsoultePath =  MT4jSettings.getInstance().getDefaultFontPath() + fontFileName;
		
		//Return cached font if there
//		IFont font = this.getCachedFont(fontAbsoultePath, fontSize,	fillColor, strokeColor, antiAliased);
		
		IFont font = this.getCachedFont(fontAbsoultePath, fontSize,	fillColor, antiAliased);
		if (font != null){
			System.out.println("Using cached font (" + fontFileName + ")");
			return font;
		}
		
		IFont loadedFont = null;
//		if (new File(fontAbsoultePath).exists()){
		try {
			String suffix = getFontSuffix(fontFileName);

			//Check which factory to use for this file type
			IFontFactory factoryToUse = this.getFactoryForFileSuffix(suffix);

			//Create the font if we have a factory
			if (factoryToUse != null){
				logger.info("Loading new font \"" + fontFileName + "\" with factory: " + factoryToUse.getClass().getName());
//				loadedFont = factoryToUse.createFont(pa, fontAbsoultePath, fontSize, fillColor, strokeColor);
//				loadedFont = factoryToUse.createFont(pa, fontAbsoultePath, fontSize, fillColor, strokeColor, antiAliased);
				loadedFont = factoryToUse.createFont(pa, fontAbsoultePath, fontSize, fillColor, antiAliased);
				fonts.add(loadedFont);
				if (fonts.size() > CACHE_MAX_SIZE && !fonts.isEmpty()){
					IFont removedFont = fonts.remove(0); 
					if (removedFont != null){
						removedFont.destroy();
					}
				}
			}else{
				logger.error("Couldnt find a appropriate font factory for: " + fontFileName + " Suffix: " + suffix);
//				loadedFont = new VectorFont(new VectorFontCharacter[0]);
			}
		}catch (Exception e) {
			logger.error("Error while trying to create the font: " + fontFileName);
			e.printStackTrace();
		}
		return (loadedFont);
	}
	
	private String getFontSuffix(String fontFileName){
		int indexOfPoint = fontFileName.lastIndexOf(".");
		String suffix;
		if (indexOfPoint != -1){
			suffix = fontFileName.substring(indexOfPoint, fontFileName.length());
			suffix = suffix.toLowerCase();
		}else{
			suffix = "";
		}
		return suffix;
	}
	
	/**
	 * Register a new fontfactory for a file type.
	 * 
	 * @param factory the factory
	 * @param fileSuffix the file suffix to use with that factory. ".ttf" for example.
	 */
	public void registerFontFactory(String fileSuffix, IFontFactory factory){
		fileSuffix = fileSuffix.toLowerCase();
		this.suffixToFactory.put(fileSuffix, factory);
	}
	
	/**
	 * Unregister a fontfactory for a file type.
	 * 
	 * @param factory the factory
	 */
	public void unregisterFontFactory(IFontFactory factory){
		Set<String> suffixesInHashMap = this.suffixToFactory.keySet();
		for (Iterator<String> iter = suffixesInHashMap.iterator(); iter.hasNext();) {
			String suffix = (String) iter.next();
			if (this.getFactoryForFileSuffix(suffix).equals(factory)){
				this.suffixToFactory.remove(suffix);
			}
		}
	}
	
	
	/**
	 * Gets the registered factories.
	 * @return the registered factories
	 */
	public IFontFactory[] getRegisteredFactories(){
		Collection<IFontFactory> factoryCollection = this.suffixToFactory.values();
		return factoryCollection.toArray(new IFontFactory[factoryCollection.size()]);
	}
	
	
	/**
	 * Gets the factory for file suffix.
	 * @param suffix the suffix
	 * @return the factory for file suffix
	 */
	public IFontFactory getFactoryForFileSuffix(String suffix){
		return this.suffixToFactory.get(suffix);
	}
	
	
	/**
	 * Gets the cached font.
	 * 
	 * @param fontAbsoultePath the font absoulte path
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * 
	 * @return the cached font
	 */
//	public IFont getCachedFont(String fontAbsoultePath, int fontSize, MTColor fillColor, MTColor strokeColor, boolean antiAliased){
	public IFont getCachedFont(String fontAbsoultePath, int fontSize, MTColor fillColor, boolean antiAliased){
		for (IFont font : fonts){
//			if (fontsAreEqual(font, fontAbsoultePath, fontSize,	fillColor, antiAliased)){
//				System.out.println("Using cached font: " + fontAbsoultePath + " Fontsize: " + Math.round(fontSize) +
//						" FillColor: " + fillColor 
////						+" StrokeColor: " + strokeColor
//						);
//				return font; // also return a COPY here, because a font color change would change font color everywhere..?
			//}else 
			if (fontsAreSimilar(font, fontAbsoultePath, fontSize, fillColor, antiAliased)){
				IFontFactory factoryToUse = this.getFactoryForFileSuffix(getFontSuffix(fontAbsoultePath));
				if (factoryToUse != null){
					IFont copy = factoryToUse.getCopy(font);
					if (copy != null && copy.isEqual(font)){
						copy.setFillColor(new MTColor(fillColor));
						return copy;	
					}else{
						logger.error("Couldnt copy font: " + font);
					}
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Fonts are equal.
	 * 
	 * @param font the font
	 * @param IVectorFontFileName the i vector font file name
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * 
	 * @return true, if successful
	 */
	public static boolean fontsAreEqual(IFont font, String fontFileName, int fontSize, MTColor fillColor /*, MTColor strokeColor */, boolean antiAliased){
		return (	
				font.getFontFileName().equalsIgnoreCase(fontFileName)
				&& 	
				font.getOriginalFontSize() == fontSize
				&&
				font.getFillColor().equals(fillColor)
								&&
				(font.isAntiAliased() == antiAliased)
		);
	}
	
	public static boolean fontsAreSimilar(IFont font, String fontFileName, int fontSize, MTColor fillColor, boolean antiAliased){
		return (	
				font.getFontFileName().equalsIgnoreCase(fontFileName)
				&& 	
				font.getOriginalFontSize() == fontSize
				&&
				(font.isAntiAliased() == antiAliased)
		);
	}
	

	
	/**
	 * Checks if is fonts are equal.
	 * 
	 * @param font1 the font1
	 * @param font2 the font2
	 * 
	 * @return true, if is fonts are equal
	 */
	public static boolean isFontsAreEqual(IFont font1, IFont font2){
		return (	
				//font1.getFontFileName().equalsIgnoreCase(font2.getFontFileName())
				//&& 	
				font1.getOriginalFontSize() == font2.getOriginalFontSize()
								&&
				font1.getFontFamily().equalsIgnoreCase(font2.getFontFamily())
								&&
				font1.getFillColor().equals(font2.getFillColor())
		);
	}

	/**
	 * Removes the font from the cache.
	 * <br><b>NOTE:</b> doesent destroy the font! To cleanly destroy a font AND remove it from
	 * the fontmanager cache call <code>font.destroy()</code>.
	 *
	 * @param font the font
	 * @return true, if successful
	 */
	public boolean removeFromCache(IFont font) {
		int in = this.fonts.indexOf(font);
		if (in != -1){
//			IFont removedFont = 
				fonts.remove(in); 
				
//			if (removedFont != null){ //DOESENT DESTROY THE FONT, font would get destroyed 2 times if font.destroy is called, which calls removeFromCache
//				removedFont.destroy();
//			}
			return true;
		}else{
			return false;
		}
	}

	public void clearCache() {
		IFont[] theFonts = fonts.toArray(new IFont[fonts.size()]);
		
		for (IFont font : theFonts) {
			font.destroy();
		}
		fonts.clear();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		clearCache();
	}
	

	
}
