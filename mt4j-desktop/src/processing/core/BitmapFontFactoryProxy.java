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
package processing.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.font.BitmapFontCharacter;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.BitmapFont;
import org.mt4j.util.font.IFont;
import org.mt4j.util.font.fontFactories.IFontFactory;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.ToolsMath;

import processing.core.PFont.Glyph;

/**
 * A factory for creating BitmapFont objects.
 * @author Christopher Ruff
 */
public class BitmapFontFactoryProxy implements IFontFactory {
	/** The Constant logger. */
	private static final ILogger logger;
//	= MTLoggerFactory.getLogger(BitmapFontFactoryProxy.class.getName());
	static{
		logger = MTLoggerFactory.getLogger(BitmapFontFactoryProxy.class.getName());
//		logger.setLevel(ILogger.ERROR);
//		logger.setLevel(ILogger.WARN);
		logger.setLevel(ILogger.DEBUG);
	}
	
	public static String defaultCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ¡¿…»ÕÃ”“abcdefghijklmnopqrstuvwxyz·‡ÈËÌÏÛÚ<>|,;.:-_#'+*!\"ß$%&/()=?¥{[]}\\@";
	
//	static{
//		FontManager.getInstance().registerFontFactory("", new BitmapFontFactory());
	//	}
	
	public IFont getCopy(IFont font) {
		if (font instanceof BitmapFont) {
			BitmapFont bf = (BitmapFont) font;
			BitmapFont copy = new BitmapFont((BitmapFontCharacter[]) bf.getCharacters(), bf.getDefaultHorizontalAdvX(), bf.getFontFamily(), bf.getFontMaxAscent(), bf.getFontMaxDescent(), bf.getUnitsPerEM(), bf.getOriginalFontSize(), bf.getFillColor(),  /*bf.getStrokeColor(),*/ bf.isAntiAliased(), bf.getFontFileName());
			return copy;
		}
		return null;
	}
	
	public IFont createFont(PApplet pa, String fontName, int fontSize, MTColor color) {
		return this.createFont(pa, fontName, fontSize, color, color, true);
	}

	public IFont createFont(PApplet pa, String fontName, int fontSize, MTColor color, boolean antiAliased) {
		return this.createFont(pa, fontName, fontSize, color, color, antiAliased);
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.fontFactories.IFontFactory#createFont(processing.core.PApplet, java.lang.String, int, org.mt4j.util.MTColor, org.mt4j.util.MTColor)
	 */
	public IFont createFont(
			PApplet pa, 
			String fontFileName, 
			int fontSize,
			MTColor fillColor, 
			MTColor strokeColor
	) {
		return this.createFont(pa, fontFileName, fontSize, fillColor, strokeColor, true);
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.fontFactories.IFontFactory#createFont(processing.core.PApplet, java.lang.String, int, org.mt4j.util.MTColor, org.mt4j.util.MTColor)
	 */
	public IFont createFont(
			PApplet pa, 
			String fontFileName, 
			int fontSize,
			MTColor fillColor, 
			MTColor strokeColor,
			boolean antiAliased
	) {
		PFont p5Font = null;
		try {
			p5Font = this.getProcessingFont(pa, fontFileName, fontSize, antiAliased);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		List<BitmapFontCharacter> bitMapCharacters = this.createCharacters(pa, p5Font, defaultCharacters, fillColor /*, strokeColor*/);
	
		//font is null sometimes (vlw)
		/*
		Font f = p5Font.getFont();
		FontMetrics fm = pa.getFontMetrics(f); 
		Map<TextAttribute, ?> atts = f.getAttributes();
		Set<TextAttribute> attKeys = atts.keySet();
		for (Iterator iterator = attKeys.iterator(); iterator.hasNext();) {
			TextAttribute textAttribute = (TextAttribute) iterator.next();
			Object value = atts.get(textAttribute);
			logger.debug("Key: " + textAttribute + " Value: " + value);
		}
//		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
		 */
		
		int defaultHorizontalAdvX = (!bitMapCharacters.isEmpty())? bitMapCharacters.get(0).getHorizontalDist() : Math.round(p5Font.descent() * fontSize); //FIXME HACK!
		String fontFamily = p5Font.getPostScriptName();
//		String fontFamily = f.getFamily(); 
		//FIXME ascent() and descent() return to small values! wheres the difference??
		int fontMaxAscent = Math.round(p5Font.ascent()* (fontSize));
		fontMaxAscent +=(float)fontSize/5.5f; //FIXME HACK! because the same ttf fonts seem to have bigger ascents
//		int fontMaxAscent = p5Font.lazyMetrics.getAscent();
		int fontMaxDescent = Math.round(p5Font.descent() * fontSize);
		/*
		//TODO INFO: because in vector font this is a negative value, too
		Font f = p5Font.getFont();
		if (f != null){
			FontMetrics fm = pa.getFontMetrics(f);
			fontMaxDescent = fm.getDescent();
		}
		*/
		fontMaxDescent *= -1; //We use negative descent values
		
		//logger.debug("Bitmapfont max descent: " + fontMaxDescent);
		
//		int fontMaxAscent = Math.round(p5Font.ascent()*fontSize);
//		int fontMaxDescent = Math.round(p5Font.descent()*fontSize);
//		int fontMaxAscent = fm.getMaxAscent(); 
//		int fontMaxDescent = fm.getMaxDescent(); 
		int unitsPerEm = 1000; //FIXME HACK!
		int originalFontSize = fontSize; //important for font cache
		
		PImage dummy = new PImage(1,1);
//		/*
		//Manually add a newLine character to the font
		BitmapFontCharacter newLine = new BitmapFontCharacter(pa, dummy, "\n", 0, 0, 0);
		newLine.setPickable(false);						    		
		newLine.setVisible(false);
		newLine.setNoFill(true);
		newLine.setNoStroke(true);
		newLine.setName("newline");
		bitMapCharacters.add(newLine);
		
		//Manually add a SPACE character to the font
//		int spaceAdvancex = defaultHorizontalAdvX;
//		int spaceAdvancex = fm.charWidth(' '); 
		//TODO hack, we use the dash character's width for the space width, because dont know how to get it
//		int spaceIndex = p5Font.index('-');
//		int spaceAdvancex = p5Font.width[spaceIndex];
//		int spaceAdvancex = p5Font.getGlyph('-').width;
		int spaceAdvancex = Math.round((p5Font.width('i') * (float) fontSize));
//		int spaceAdvancex = Math.round(pa.textWidth(' '));
//		int spaceAdvancex = Math.round(p5Font.width(' ') * p5Font.size);
		BitmapFontCharacter space = new BitmapFontCharacter(pa, dummy, " ", 0, 0, spaceAdvancex);
		space.setPickable(false);						    		
		space.setVisible(false);
		space.setNoFill(true);
		space.setNoStroke(true);
		space.setName("space");
		bitMapCharacters.add(space);
		
		//Manually add a TAB character to the font
		int defaultTabWidth = spaceAdvancex*4;
		BitmapFontCharacter tab = new BitmapFontCharacter(pa, dummy, "\t", 0, 0, defaultTabWidth);
		try {
			int tabWidth = 4 * space.getHorizontalDist();
			tab.setHorizontalDist(tabWidth);
		} catch (Exception e) {
			tab.setHorizontalDist(defaultTabWidth);
		}
		tab.setPickable(false);
		tab.setName("tab"); 
		tab.setVisible(false);
		tab.setNoFill(true);
		tab.setNoStroke(true);
		bitMapCharacters.add(tab);
//		*/
		
		//TODO bitmap font size seems different to same size vector font, we must have check descent -> textarea -> res*em*etc
		//TODO eureka font -  numbers baseline wrong?
		
		//Create the bitmap font
		BitmapFontCharacter[] characters = bitMapCharacters.toArray(new BitmapFontCharacter[bitMapCharacters.size()]);
		BitmapFont bitmapFont = new BitmapFont(characters, defaultHorizontalAdvX, fontFamily, fontMaxAscent, fontMaxDescent, unitsPerEm, originalFontSize, 
				fillColor,
//				strokeColor,
				antiAliased,
				fontFileName
		);
		bitmapFont.setFontFileName(fontFileName);
		return bitmapFont;
	}
	
	
//	/**
//	 * Gets the processing font.
//	 *
//	 * @param pa the pa
//	 * @param fontFileName the font file name
//	 * @param fontSize the font size
//	 * @return the processing font
//	 */
//	private PFont getProcessingFont(PApplet pa, String fontFileName, int fontSize){
//		return this.getProcessingFont(pa, fontFileName, fontSize, true);
//	}

	/**
	 * Gets the processing font.
	 *
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param antiAliased the anti aliased
	 * @return the processing font
	 * @throws FileNotFoundException 
	 */
	private PFont getProcessingFont(PApplet pa, String fontFileName, int fontSize, boolean antiAliased) throws FileNotFoundException{
		PFont p5Font;
		//When loading the vlw font the font size and anti aliasing is already determined with the file
		//and our parameter isnt honored
		if (fontFileName.endsWith(".vlw")){
			p5Font = pa.loadFont(fontFileName);
			//If not found try to load from the "/data" directory
			if (p5Font == null){
				int lastDirFileSeparator = fontFileName.lastIndexOf(java.io.File.separator);
				int lastDirSeparator = fontFileName.lastIndexOf(AbstractMTApplication.separator);
				if (lastDirFileSeparator != -1){
					p5Font = pa.loadFont(fontFileName.substring(lastDirFileSeparator+1, fontFileName.length()));
				}else if (lastDirSeparator != -1){
					p5Font = pa.loadFont(fontFileName.substring(lastDirSeparator+1, fontFileName.length()));
				}
			}
		}
		else if (fontFileName.endsWith(".ttf") || fontFileName.endsWith(".otf")){
			p5Font = pa.createFont(fontFileName, fontSize, antiAliased); 
			//If not found try to load from the "/data" directory
			if (p5Font == null){
				int lastDirFileSeparator = fontFileName.lastIndexOf(java.io.File.separator);
				int lastDirSeparator = fontFileName.lastIndexOf(AbstractMTApplication.separator);
				if (lastDirFileSeparator != -1){
					p5Font = pa.createFont(fontFileName.substring(lastDirFileSeparator+1, fontFileName.length()), fontSize, antiAliased); 
				}else if (lastDirSeparator != -1){
					p5Font = pa.createFont(fontFileName.substring(lastDirSeparator+1, fontFileName.length()), fontSize, antiAliased); 
				}else{
					p5Font = pa.loadFont(fontFileName);
				}
			}
		}
		else{
			//No file suffix -> Create font from a java/system font
			int lastDirFileSeparator = fontFileName.lastIndexOf(java.io.File.separator);
			int lastDirSeparator = fontFileName.lastIndexOf(AbstractMTApplication.separator);
			if (lastDirFileSeparator != -1){
				p5Font = pa.createFont(fontFileName.substring(lastDirFileSeparator+1, fontFileName.length()), fontSize, antiAliased); //Creats the font
			}
			else if (lastDirSeparator != -1){
				p5Font = pa.createFont(fontFileName.substring(lastDirSeparator+1, fontFileName.length()), fontSize, antiAliased); //Creats the font	
			}
			else{
				p5Font = pa.loadFont(fontFileName);
			}
		}
		
		if (p5Font == null){
			throw new FileNotFoundException("Couldn't load the font: " + fontFileName);
		}
		return p5Font;
	}
	
	
	
	public List<BitmapFontCharacter> getCharacters(PApplet pa, 
			String chars,
			MTColor fillColor, 
//			MTColor strokeColor,
			String fontFileName, 
			int fontSize
	){
		return this.getCharacters(pa, chars, fillColor, /*strokeColor,*/ fontFileName, fontSize, true);
	}
	
	/**
	 * Creates the specified characters.
	 *
	 * @param pa the pa
	 * @param chars the chars
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param antiAliased the anti aliased
	 * @return the characters
	 */
	public List<BitmapFontCharacter> getCharacters(PApplet pa, 
			String chars,
			MTColor fillColor, 
//			MTColor strokeColor,
			String fontFileName, 
			int fontSize,
			boolean antiAliased
	){
		PFont p5Font = null;
		try {
			p5Font = this.getProcessingFont(pa, fontFileName, fontSize, antiAliased);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return createCharacters(pa, p5Font, chars, fillColor /*, strokeColor*/);
	}
	
	
	private List<BitmapFontCharacter> createCharacters(PApplet pa, PFont p5Font, String chars, MTColor fillColor /*, MTColor strokeColor*/){
		List<BitmapFontCharacter> bitMapCharacters = new ArrayList<BitmapFontCharacter>();
		
		for (int i = 0; i < chars.length(); i++) {
			char c = chars.charAt(i);
//			int charIndex = p5Font.index(c);
			Glyph glyph = p5Font.getGlyph(c);
			if (glyph != null){
				PImage charImage = glyph.image;
				int charWidth = glyph.width;
				int charHeight = glyph.height;
				int topExtend = glyph.topExtent;
				int leftExtend = glyph.leftExtent;
				int widthDisplacement = glyph.setWidth;
				

				//int topOffset = p5Font.descent + (-charHeight - (topExtend-charHeight)); //ORIGINAL
				int topOffset =  (-charHeight - (topExtend-charHeight));
				
				//Copy the actual font data on the image from the upper left corner 1 pixel
				//into the middle of the image to avoid anti aliasing artefacts at the corners
//				PImage copy = new PImage(charImage.width, charImage.height, PImage.ARGB); //ORG

//				for (int j = 0; j < charImage.pixels.length; j++) { //ORG
//					int d = charImage.pixels[j];
//					/*
//						int a = d >> 24 & 0xFF;
//						int r = d >> 16 & 0xFF;
//						int g = d >> 8 & 0xFF;
//						int b = d & 0xFF;
//						logger.debug("R: " + r + " G:" + g + " B:" + " A:" + a);
//					 */
//					charImage.pixels[j] = (d << 24) | 0x00FFFFFF; //ORIGINAL! //make it white
////					charImage.pixels[j] = (d << 24) | pa.color(fillColor.getR(), fillColor.getG(), fillColor.getB(), 0);
////					charImage.pixels[j] = (charImage.pixels[j] << 24) | 0x00FFFFFF;
//					//charImage.format = PConstants.ARGB;
//					
//					//Clear the copy image in the same loop
//					copy.pixels[j] = (copy.pixels[j] << 24) | 0x00FFFFFF; //Original! //make it white
////					copy.pixels[j] = (d << 24) | 0x00FFFFFF; //Original! //make it white
//				}
				
				for (int j = 0; j < charImage.pixels.length; j++) { //ORG
					charImage.pixels[j] = (charImage.pixels[j] << 24) | 0x00FFFFFF; //ORIGINAL! //make it white
				}
				
				//Shift character image data down and right in the image because of aliasing artifacts at the border
				//we need to compensate for this when displaying the char
				//FIXME this creates far to big images..but because of artefacts needed..?
				int topShiftAmount = 1;
				int leftShiftAmount = 1;
				
//				PImage copy = new PImage(ToolsMath.nearestPowerOfTwo(charWidth + shiftAmount), ToolsMath.nearestPowerOfTwo(charHeight + shiftAmount), PImage.ARGB);
//				
				PImage copy = new PImage(nextPowerOfTwo(charImage.width + leftShiftAmount + 1), nextPowerOfTwo(charImage.height + topShiftAmount +1), PImage.ARGB);
//				PImage copy = new PImage(charImage.width + leftShiftAmount + 1, charImage.height + topShiftAmount, PImage.ARGB);
				
				copy.copy(charImage, 0, 0, charWidth, charHeight, leftShiftAmount, topShiftAmount, charWidth, charHeight);
				
//				copy.copy(charImage, 0, 0, charImage.width, charImage.height, leftShiftAmount, topShiftAmount, charImage.width, charImage.height);
				
//				copy.copy(charImage, 0, 0, charWidth, charHeight, shiftAmount, shiftAmount, charWidth, charHeight);
//				copy.copy(charImage, 0, 0, charImage.width, charImage.height, shiftAmount, shiftAmount, charImage.width, charImage.height);
//				copy.copy(charImage, 0, 0, charImage.width, charImage.height, shiftAmount, shiftAmount, charImage.width, charImage.height);
//				copy.copy(charImage, 0, 0, charWidth, charHeight, shiftAmount, shiftAmount, charWidth, charHeight);
				
				charImage = copy;
				
				//FIXME the topoffset is smaller than with the vector font! check that!
				//FIXME anti aliasing artefacts may also stem from using a perspective and not ortho camera!!
				//FIXME space character too wide..
				
				//Move the character to compensate for the shifting of the image
				topOffset -= topShiftAmount; //org shiftamount 
				leftExtend -= leftShiftAmount;
				
				//FIXME TEST
//				if (c == 'i'){
//					copy.save(MT4jSettings.DEFAULT_IMAGES_PATH + "i.png");
//				}
				
				//Create bitmap font character
				String StringChar = Character.toString(c);
				BitmapFontCharacter character = new BitmapFontCharacter(pa, charImage, StringChar, leftExtend, topOffset, widthDisplacement);
				character.setName(StringChar);
				character.setFillColor(new MTColor(fillColor));
				if (MT4jSettings.getInstance().isOpenGlMode()){
					character.generateAndUseDisplayLists();
				}
				bitMapCharacters.add(character);
				//logger.debug("Char: " + c + " charWidth: " + charWidth +  " leftExtend: " + leftExtend + " widthDisplacement: " + widthDisplacement + " imageHeight: " + charImage.height + " charHeight: " + charHeight +  " topExtent: " + topExtend);
			}else{
				logger.warn("Couldnt create bitmap character : " + c + " -> not found!");
			}
		}
		return bitMapCharacters;
	}

	private int nextPowerOfTwo(int val) {
		return ToolsMath.nextPowerOfTwo(val);
	}

//	
//	  /**
//	   * Create a .vlw font on the fly from either a font name that's
//	   * installed on the system, or from a .ttf or .otf that's inside
//	   * the data folder of this sketch.
//	   * <P/>
//	   * Many .otf fonts don't seem to be supported by Java, perhaps because 
//	   * they're CFF based?
//	   * <P/>
//	   * Font names are inconsistent across platforms and Java versions.
//	   * On Mac OS X, Java 1.3 uses the font menu name of the font,
//	   * whereas Java 1.4 uses the PostScript name of the font. Java 1.4
//	   * on OS X will also accept the font menu name as well. On Windows,
//	   * it appears that only the menu names are used, no matter what
//	   * Java version is in use. Naming system unknown/untested for 1.5.
//	   * <P/>
//	   * Use 'null' for the charset if you want to dynamically create
//	   * character bitmaps only as they're needed. (Version 1.0.9 and
//	   * earlier would interpret null as all unicode characters.)
//	   */
//	  public PFont createFont(PApplet app, String name, float size,
//	                          boolean smooth, char charset[]) {
//	    String lowerName = name.toLowerCase();
//	    Font baseFont = null;
//
//	    try {
//	      InputStream stream = null;
//	      if (lowerName.endsWith(".otf") || lowerName.endsWith(".ttf")) {
//	        stream = app.createInput(name);
//	        if (stream == null) {
//	          System.err.println("The font \"" + name + "\" " +
//	                             "is missing or inaccessible, make sure " +
//	                             "the URL is valid or that the file has been " +
//	                             "added to your sketch and is readable.");
//	          return null;
//	        }
//	        baseFont = Font.createFont(Font.TRUETYPE_FONT, app.createInput(name));
//
//	      } else {
//	        baseFont = PFont.findFont(name);
//	      }
//	      return new PFont(baseFont.deriveFont(size), smooth, charset, 
//	                       stream != null);
//
//	    } catch (Exception e) {
//	      System.err.println("Problem createFont(" + name + ")");
//	      e.printStackTrace();
//	      return null;
//	    }
//	  }
//	  
//	 private class MYPFont extends PFont{
//		 
//		 public void getGlyphImage(){
//			 getGlyph('a');
//		 }
//		 
//		 public class bla extends PFont.Glyph{
//			 
//		 }
//		 
//	 }


}
