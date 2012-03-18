package org.mt4j.components.css.style;

import org.mt4j.components.css.util.CSSKeywords.CSSFontFamily;
import org.mt4j.components.css.util.CSSKeywords.CSSFontStyle;
import org.mt4j.components.css.util.CSSKeywords.CSSFontWeight;
import org.mt4j.util.MTColor;

/**
 * The Class CSSFont.
 */
public class CSSFont {
	
	/** The font family. */
	private CSSFontFamily family = CSSFontFamily.CUSTOM;
	
	/** The font style. */
	private CSSFontStyle style = CSSFontStyle.NORMAL;
	
	/** The custom font file string. */
	private String customType = "";
	
	/** The font weight. */
	private CSSFontWeight weight = CSSFontWeight.NORMAL;
	
	/** The font size. */
	private int fontsize = 16;
	
	/** The font color. */
	private MTColor color = new MTColor(255, 255, 255, 255);
	
	/** Has the font been modified. */
	private boolean modified = false;

	/**
	 * Instantiates a new CSS font using the color
	 *
	 * @param color the color
	 */
	public CSSFont(MTColor color) {
		super();
		this.color = color;
		this.modified = true;
	}
	
	/**
	 * Instantiates a new default CSS font.
	 */
	public CSSFont() {
		super();
		this.family = CSSFontFamily.DEFAULT;
	}
	
	/**
	 * Instantiates a new CSS font using the font size
	 *
	 * @param fontsize the fontsize
	 */
	public CSSFont(int fontsize) {
		super();
		this.fontsize = fontsize;
		this.modified = true;
	}

	/**
	 * Instantiates a new CSS font using a custom font file name
	 *
	 * @param customType the custom custom font file name
	 */
	public CSSFont(String customType) {
		super();
		this.customType = customType;
		this.modified = true;
	}

	/**
	 * Instantiates a new CSS font using the font style
	 *
	 * @param style the font style
	 */
	public CSSFont(CSSFontStyle style) {
		super();
		this.style = style;
		this.modified = true;
	}

	/**
	 * Instantiates a new CSS font using the font family
	 *
	 * @param family the font family
	 */
	public CSSFont(CSSFontFamily family) {
		super();
		this.family = family;
		this.modified = true;
	}

	/**
	 * Instantiates a new CSS font using the font weight
	 *
	 * @param weight the font weight
	 */
	public CSSFont(CSSFontWeight weight) {
		super();
		this.weight = weight;
		this.modified = true;
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public MTColor getColor() {
		return color;
	}

	/**
	 * Sets the color.
	 *
	 * @param color the new color
	 */
	public void setColor(MTColor color) {
		this.color = color;
		this.modified = true;
	}

	/**
	 * Gets the font size.
	 *
	 * @return the font size
	 */
	public int getFontsize() {
		return fontsize;

	}

	/**
	 * Sets the font size.
	 *
	 * @param fontsize the new font size
	 */
	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
		this.modified = true;
		debugOutput();
	}

	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	public CSSFontWeight getWeight() {
		return weight;
	}

	/**
	 * Sets the weight.
	 *
	 * @param weight the new weight
	 */
	public void setWeight(CSSFontWeight weight) {
		this.weight = weight;
		this.modified = true;
		debugOutput();
	}

	/**
	 * Gets the family.
	 *
	 * @return the family
	 */
	public CSSFontFamily getFamily() {
		return family;
	}

	/**
	 * Sets the family.
	 *
	 * @param family the new family
	 */
	public void setFamily(CSSFontFamily family) {
		this.family = family;
		this.modified = true;
		debugOutput();
	}

	/**
	 * Gets the font style.
	 *
	 * @return the font style
	 */
	public CSSFontStyle getStyle() {
		return style;
	}

	/**
	 * Sets the font style.
	 *
	 * @param style the new font style
	 */
	public void setStyle(CSSFontStyle style) {
		this.style = style;
		this.modified = true;
		debugOutput();
	}

	/**
	 * Gets the file name of custom fonts
	 *
	 * @return the file name of the custom font
	 */
	public String getCustomType() {
		return customType;
	}

	/**
	 * Sets the file name of custom fonts
	 *
	 * @param customType the file name of the custom font
	 */
	public void setCustomType(String customType) {
		this.customType = customType;
		this.modified = true;
		debugOutput();
	}

	/**
	 * Sets, if the font has been modified
	 *
	 * @param modified the new modified
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}
	
	/**
	 * Checks if the font is modified.
	 *
	 * @return true, if is modified
	 */
	public boolean isModified() {
		return modified;
	}
	
	/**
	 * Debug output.
	 */
	private void debugOutput() {
		/*Logger logger = Logger.getLogger("MT4J Extensions");
		logger.debug("Font Family: " + family + ", Font Style: " + style
				+ ", Font Weight: " + weight + ", Font Size: " + fontsize
				+ ", Custom TTF Font: " + customType);*/
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result
				+ ((customType == null) ? 0 : customType.hashCode());
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result + fontsize;
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CSSFont other = (CSSFont) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (customType == null) {
			if (other.customType != null)
				return false;
		} else if (!customType.equals(other.customType))
			return false;
		if (family == null) {
			if (other.family != null)
				return false;
		} else if (!family.equals(other.family))
			return false;
		if (fontsize != other.fontsize)
			return false;
		if (style == null) {
			if (other.style != null)
				return false;
		} else if (!style.equals(other.style))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}
	
	public CSSFont clone() {
		CSSFont newFont = new CSSFont();
		newFont.color = this.color.getCopy();
		newFont.customType = this.customType.substring(0);
		newFont.family = this.family;
		newFont.fontsize = this.fontsize;
		newFont.modified = this.modified;
		newFont.style = this.style;
		newFont.weight =this.weight;
		return newFont;
	}
	public CSSFont clone(int fontsize) {
		CSSFont newFont = this.clone();
		newFont.setFontsize(fontsize);
		newFont.setModified(true);
		return newFont;
	}
	
}
