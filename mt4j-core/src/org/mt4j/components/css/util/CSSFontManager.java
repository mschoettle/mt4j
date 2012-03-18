package org.mt4j.components.css.util;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.css.style.CSSFont;
import org.mt4j.components.css.util.CSSKeywords.CSSFontWeight;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;


/**
 * The Class CSSFontManager.
 */
public class CSSFontManager {
	
	/** The app. */
	AbstractMTApplication app;
	
	/**
	 * Instantiates a new CSS font manager.
	 *
	 * @param app the MTApplication
	 */
	public CSSFontManager(AbstractMTApplication app) {
		this.app = app;
	}
	
	
	/**
	 * Select font.
	 *
	 * @param currentFont2 the CSSFont to be returned as IFont
	 * @return the font as IFont
	 */
	public IFont selectFont(CSSFont currentFont2) {

		if (currentFont2 != null) {
		try {
		switch (currentFont2.getFamily()) {
		
		case SERIF:
			switch (currentFont2.getStyle()) {
			case ITALIC:
			case OBLIQUE:
				if (currentFont2.getWeight() == CSSFontWeight.BOLD) {
					return getFont("Serif.bolditalic", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.LIGHT) {
					return getFont("Serif.italic", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.NORMAL) {
					return getFont("Serif.italic", currentFont2.getFontsize(), currentFont2.getColor());
				}
			
				break;
			case NORMAL:
			default:
				if (currentFont2.getWeight() == CSSFontWeight.BOLD) {
					return getFont("Serif.bold", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.LIGHT) {
					return getFont("Serif", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.NORMAL) {
					return getFont("Serif", currentFont2.getFontsize(), currentFont2.getColor());
				}
				break;
			}
			break;
		case MONO:
			switch (currentFont2.getStyle()) {
			case ITALIC:
			case OBLIQUE:
				if (currentFont2.getWeight() == CSSFontWeight.BOLD) {
					return getFont("Monospaced.bolditalic", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.LIGHT) {
					return getFont("Monospaced.italic", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.NORMAL) {
					return getFont("Monospaced.italic", currentFont2.getFontsize(), currentFont2.getColor());
				}
			
				break;
			case NORMAL:
			default:
				if (currentFont2.getWeight() == CSSFontWeight.BOLD) {
					return getFont("Monospaced.bold", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.LIGHT) {
					return getFont("Monospaced", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.NORMAL) {
					return getFont("Monospaced", currentFont2.getFontsize(), currentFont2.getColor());
				}
				break;
			}
			break;
		case CUSTOM:
			return getFont(currentFont2.getCustomType(), currentFont2.getFontsize(), currentFont2.getColor());
			
		case DEFAULT:
		case SANS:
		default:
			switch (currentFont2.getStyle()) {
			case ITALIC:
			case OBLIQUE:
				if (currentFont2.getWeight() == CSSFontWeight.BOLD) {
					return getFont("SansSerif.bolditalic", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.LIGHT) {
					return getFont("SansSerif.italic", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.NORMAL) {
					return getFont("SansSerif.italic", currentFont2.getFontsize(), currentFont2.getColor());
				}
			
				break;
			case NORMAL:
			default:
				if (currentFont2.getWeight() == CSSFontWeight.BOLD) {
					return getFont("SansSerif.bold", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.LIGHT) {
					return getFont("SansSerif", currentFont2.getFontsize(), currentFont2.getColor());
				}
				if (currentFont2.getWeight() == CSSFontWeight.NORMAL) {
					return getFont("SansSerif", currentFont2.getFontsize(), currentFont2.getColor());
				}
				break;
			}
			break;
		
		}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		}
		

		if (currentFont2 == null) {
			currentFont2 = new CSSFont(16);
		}
		
		 return FontManager.getInstance().createFont(app,"SansSerif", currentFont2.getFontsize(), currentFont2.getColor()); 
	}

	/**
	 * Gets an IFont by its filename, size and color
	 *
	 * @param font the font file name
	 * @param size the size
	 * @param color the color
	 * @return the font
	 */
	private IFont getFont(String font, int size, MTColor color) {
		try {
			IFont returnFont = FontManager.getInstance().createFont(app,
				font, size, color);
			if (returnFont == null) throw new Exception();
			return returnFont;	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FontManager.getInstance().createFont(app,
				"SansSerif", size, color); 
	}
}
