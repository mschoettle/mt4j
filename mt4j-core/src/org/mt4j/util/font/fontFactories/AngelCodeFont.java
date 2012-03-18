package org.mt4j.util.font.fontFactories;

import java.util.HashMap;
import java.util.List;

import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.font.IFontCharacter;
import org.mt4j.util.font.ITextureFont;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GLTexture;

import processing.core.PImage;

public class AngelCodeFont implements IFont, ITextureFont {
	private static final ILogger logger = MTLoggerFactory.getLogger(AngelCodeFont.class.getName());
	static{
//		logger.setLevel(ILogger.ERROR);
//		logger.setLevel(ILogger.WARN);
		logger.setLevel(ILogger.DEBUG);
	}
	
	private AngelCodeFontCharacter[] characters;
	
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
	private HashMap<String, AngelCodeFontCharacter> uniCodeToChar;
	
	/** The char name to char. */
	private HashMap<String, AngelCodeFontCharacter> charNameToChar;
	
	/** The fill color. */
	private MTColor fillColor;
	
//	/** The stroke color. */
//	private MTColor strokeColor;
	
	private boolean antiAliased;
	
	private PImage fontImage;

	private int hieroPadding;
	
	
	//TODO make class AbstractFont with destroy method, getters, setters
	
	
	public AngelCodeFont(PImage fontImage, AngelCodeFontCharacter[] characters, int defaultHorizontalAdvX, String fontFileName, String fontFamily, int fontMaxAscent, int fontMaxDescent, int unitsPerEm, int originalFontSize,
			MTColor fillColor,
			boolean antiAliased, int hieroPadding
	) {
		this.fontImage = fontImage;
		
		this.characters = characters;
		this.defaultHorizontalAdvX = defaultHorizontalAdvX;
		this.fontFileName = fontFileName;
		this.fontFamily = fontFamily;
		this.originalFontSize = originalFontSize;
		this.fillColor = fillColor;
		this.antiAliased = antiAliased;
		
		this.fontMaxAscent 	= fontMaxAscent;
		this.fontMaxDescent = fontMaxDescent;
		
		this.unitsPerEM = unitsPerEm;
		
		this.hieroPadding = hieroPadding;
		
		//Put characters in hashmaps for quick access
		uniCodeToChar 	= new HashMap<String, AngelCodeFontCharacter>();
		charNameToChar 	= new HashMap<String, AngelCodeFontCharacter>();
        for (AngelCodeFontCharacter currentChar : characters) {
            uniCodeToChar.put(currentChar.getUnicode(), currentChar);
            charNameToChar.put(currentChar.getName(), currentChar);
        }
		
	}

	public IFontCharacter getFontCharacterByName(String characterName){
		IFontCharacter returnChar = charNameToChar.get(characterName);
		if (returnChar == null)
			logger.warn("Font couldnt load charactername: " + characterName);
		return returnChar;
	}

	@Override
	public IFontCharacter getFontCharacterByUnicode(String unicode) {
		IFontCharacter returnChar = uniCodeToChar.get(unicode);
		if (returnChar == null){
			logger.warn("Font couldnt load characterunicode: '" + unicode + "'");
		}
		return returnChar;
	}

	@Override
	public IFontCharacter[] getCharacters() {
		return this.characters;
	}
	
	@Override
	public String getFontFamily() {
		return this.fontFamily;
	}

	@Override
	public int getDefaultHorizontalAdvX() {
		return this.defaultHorizontalAdvX;
	}

	@Override
	public int getFontMaxAscent() {
		return this.fontMaxAscent;
	}

	@Override
	public int getFontMaxDescent() {
		return this.fontMaxDescent;
	}
	
	@Override
	public int getFontAbsoluteHeight() {
		return ((Math.abs(this.getFontMaxAscent())) + (Math.abs(this.getFontMaxDescent())));
	}

	@Override
	public int getUnitsPerEM() {
		return this.unitsPerEM;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFontFileName()
	 */
	public String getFontFileName() {
		return this.fontFileName;
	}


	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getOriginalFontSize()
	 */
	public int getOriginalFontSize() {
		return this.originalFontSize;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.IFont#getFillColor()
	 */
	public MTColor getFillColor() {
		return fillColor;
	}


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
		
		GLTexture tex = (GLTexture)this.fontImage;
		int textureTarget = tex.getTextureTarget();
		gl.glEnable(textureTarget);
		
		//Enable Pointers, set vertex array pointer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glBindTexture(textureTarget, tex.getTextureID()); 
	}


	@Override
	public void endBatchRenderGL(GL10 gl, IFont font) {
		GLTexture tex = (GLTexture)this.fontImage;
		int textureTarget = tex.getTextureTarget();
		gl.glBindTexture(textureTarget, 0);//Unbind texture
		gl.glDisable(textureTarget); 
		
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	public PImage getFontImage() {
		return fontImage;
	}

	
	public AngelCodeFont getCopy(){
		return new AngelCodeFont(fontImage, characters, defaultHorizontalAdvX, fontFileName, fontFamily, fontMaxAscent , fontMaxDescent , unitsPerEM, originalFontSize, fillColor,  /*bf.getStrokeColor(),*/ antiAliased, getHieroPadding());
	}
	
	public boolean isEqual(IFont font){
		if (font instanceof AngelCodeFont) {
			AngelCodeFont af = (AngelCodeFont) font;
			if (
					font.getFontFileName().equalsIgnoreCase(getFontFileName())
					&& 	
					font.getOriginalFontSize() == getOriginalFontSize()
					&&
					font.isAntiAliased() == antiAliased	
					&&
					af.getHieroPadding() == getHieroPadding()
			){
					return true;	
			}
				
		}
		return false;
	}

	public int getHieroPadding() {
		return hieroPadding;
	}
	
	

}
