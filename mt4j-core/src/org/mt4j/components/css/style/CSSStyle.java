package org.mt4j.components.css.style;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.css.util.CSSFontManager;
import org.mt4j.components.css.util.CSSKeywords.CSSBorderStyle;
import org.mt4j.components.css.util.CSSKeywords.CSSSelectorType;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;

import processing.core.PImage;

/**
 * The Class CSSStyle.
 */
public class CSSStyle {
	
	/** The MTApplication. */
	AbstractMTApplication app;
	
	/** The uri of the style file. */
	String uri = "";
	
	/** Has the uri been modified. */
	boolean modifiedUri = false;
	

	/** The selector. */
	CSSSelector selector = null;
	
	/** Has the selector been modified. */
	boolean modifiedSelector = false;
	//Colours
	
	/** The background color. */
	MTColor backgroundColor = new MTColor(0,0,0,0);
	
	
	/** The border color. */
	MTColor borderColor = new MTColor(255,255,255,255);
	
	/** Have the colors been modified. */
	boolean modifiedBackgroundColor= false, modifiedBorderColor = false;
	
	//Background Image
	
	/** The background image. */
	PImage backgroundImage = null;
	
	/** The position of the background image. */
	CSSBackgroundPosition backgroundPosition = new CSSBackgroundPosition();
	
	/** The background repeat options. */
	BackgroundRepeat backgroundRepeat = BackgroundRepeat.REPEAT;
	
	/**
	 * The Enum BackgroundRepeat.
	 */
	public enum BackgroundRepeat {
		
		/** The XREPEAT. */
		XREPEAT, 
	 /** The YREPEAT. */
	 YREPEAT, 
	 /** The REPEAT. */
	 REPEAT, 
	 /** The NONE. */
	 NONE;
		}
	
	
	/** Have the background image options been modified. */
	boolean modifiedBackgroundImage = false, modifiedBackgroundPosition = false, modifiedBackgroundRepeat = false; 
	
	//Border
	/** The border style. */
	CSSBorderStyle borderStyle = CSSBorderStyle.NONE;
	
	/** The font. */
	IFont font = null;
	
	/** The cssfont. */
	CSSFont cssfont = new CSSFont();
	
	/** Have the font options been modified. */
	boolean modifiedBorderStyle = false, modifiedFont = false, modifiedCssfont = false;
	
	//Sizes
	/** The width. */
	float width = 0;
	
	/** Is the width relative. */
	boolean widthPercentage = false;
	
	/** The height. */
	float height = 0;
	
	/** Is the height relative. */
	boolean heightPercentage = false;
	
	/** The depth. */
	float depth = 0;
	
	/** Have the width/height been modified. */
	boolean modifiedWidth = false, modifiedHeight = false, modifiedDepth = false;
	
	/** Have the relative options been modified. */
	boolean modifiedWidthPercentage =false, modifiedHeightPercentage = false;
	
	
	/** The border width. */
	float borderWidth = 0;
	
	/** The padding width. */
	float paddingWidth = 0; // Graphics and Text only
	
	/** Have the border/padding width been modified. */
	boolean modifiedBorderWidth = false, modifiedPaddingWidth = false;
	

	//General Properties
	/** The visibility. */
	boolean visibility = true;
	

	/** Has the visibility/zIndex been modified.*/
	boolean modifiedVisibility = false, modifiedZIndex = false;
	
	/** The opacity. */
	float opacity = 255;
	
	/** The modified opacity. */
	boolean modifiedOpacity = false;
	
	
	
	/**
	 * Gets the opacity.
	 *
	 * @return the opacity
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Sets the opacity.
	 *
	 * @param opacity the new opacity
	 */
	public void setOpacity(float opacity) {
		this.modifiedOpacity = true;
		if (opacity <= 1) {
			this.opacity = opacity * 255;
		} else {
			this.opacity = opacity;
		}
	}

	/**
	 * Checks if is modified opacity.
	 *
	 * @return true, if is modified opacity
	 */
	public boolean isModifiedOpacity() {
		return modifiedOpacity;
	}

	/**
	 * Sets the modified opacity.
	 *
	 * @param modifiedOpacity the new modified opacity
	 */
	public void setModifiedOpacity(boolean modifiedOpacity) {
		this.modifiedOpacity = modifiedOpacity;
	}

	/**
	 * Instantiates a new CSS style.
	 *
	 * @param app the MTApplication
	 */
	public CSSStyle(AbstractMTApplication app) {
		this.selector = new CSSSelector("Universal", CSSSelectorType.UNIVERSAL);
		this.app = app;
	}
	
	/**
	 * Instantiates a new CSS style using a selector.
	 *
	 * @param selector the selector
	 * @param app the MTApplication
	 */
	public CSSStyle(CSSSelector selector,AbstractMTApplication app) {
		super();
		this.selector = selector;
		this.modifiedSelector = true;
		this.app = app;
	}
	
	/**
	 * Instantiates a new CSS style using a font.
	 *
	 * @param font the font (as CSSFont)
	 * @param app the MTApplication
	 */
	public CSSStyle(CSSFont font,AbstractMTApplication app) {
		super();
		this.selector = new CSSSelector("Universal", CSSSelectorType.UNIVERSAL);
		this.cssfont = font;
		this.modifiedFont = true;
		this.app = app;
	}

	
	
	
	/**
	 * Gets the selector.
	 *
	 * @return the selector
	 */
	public CSSSelector getSelector() {
		return selector;
	}

	/**
	 * Sets the selector.
	 *
	 * @param selector the new selector
	 */
	public void setSelector(CSSSelector selector) {
		this.selector = selector;
		this.modifiedSelector = true;
	}

	/**
	 * Gets the cssfont.
	 *
	 * @return the cssfont
	 */
	public CSSFont getCssfont() {
		return cssfont;
	}

	/**
	 * Sets the cssfont.
	 *
	 * @param cssfont the new cssfont
	 */
	public void setCssfont(CSSFont cssfont) {
		this.cssfont = cssfont;
		this.modifiedCssfont = true;
	}


	/**
	 * Gets the background color.
	 *
	 * @return the background color
	 */
	public MTColor getBackgroundColor() {
		MTColor newColor = new MTColor(this.backgroundColor.getR(), this.backgroundColor.getG(), this.backgroundColor.getB(), this.backgroundColor.getAlpha() * this.getOpacity() / 255f);
		return newColor;
	}
	
	/**
	 * Sets the background color.
	 *
	 * @param backgroundColor the new background color
	 */
	public void setBackgroundColor(MTColor backgroundColor) {
		this.backgroundColor = backgroundColor;
		this.modifiedBackgroundColor = true;
	}
	
	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
//	public MTColor getColor() {
//		return color;
//		
//	}
	
	/**
	 * Sets the color.
	 *
	 * @param color the new color
	 */
//	public void setColor(MTColor color) {
//		this.color = color;
//		this.getCssfont().setColor(color);
//		this.modifiedColor = true;
//	}
	
	/**
	 * Gets the border color.
	 *
	 * @return the border color
	 */
	public MTColor getBorderColor() {
		return borderColor;
	}
	
	/**
	 * Sets the border color.
	 *
	 * @param borderColor the new border color
	 */
	public void setBorderColor(MTColor borderColor) {
		this.borderColor = borderColor;
		this.modifiedBorderColor = true;
	}
	
	/**
	 * Gets the background image.
	 *
	 * @return the background image
	 */
	public PImage getBackgroundImage() {
		return backgroundImage;
	}
	
	/**
	 * Sets the background image.
	 *
	 * @param backgroundImage the new background image
	 */
	public void setBackgroundImage(PImage backgroundImage) {
		this.backgroundImage = backgroundImage;
		this.modifiedBackgroundImage = true;
	}
	
	/**
	 * Gets the background position.
	 *
	 * @return the background position
	 */
	public CSSBackgroundPosition getBackgroundPosition() {
		return backgroundPosition;
	}
	
	/**
	 * Sets the background position.
	 *
	 * @param backgroundPosition the new background position
	 */
	public void setBackgroundPosition(CSSBackgroundPosition backgroundPosition) {
		this.backgroundPosition = backgroundPosition;
		this.modifiedBackgroundPosition = true;
	}
	
	/**
	 * Gets the background repeat.
	 *
	 * @return the background repeat
	 */
	public BackgroundRepeat getBackgroundRepeat() {
		return backgroundRepeat;
	}
	
	/**
	 * Sets the background repeat.
	 *
	 * @param backgroundRepeat the new background repeat
	 */
	public void setBackgroundRepeat(BackgroundRepeat backgroundRepeat) {
		this.backgroundRepeat = backgroundRepeat;
		this.modifiedBackgroundRepeat = true;
	}
	
	/**
	 * Gets the border style.
	 *
	 * @return the border style
	 */
	public CSSBorderStyle getBorderStyle() {
		return borderStyle;
	}
	
	/**
	 * Gets the border style pattern.
	 *
	 * @return the border style pattern (as short, for OpenGL)
	 */
	public short getBorderStylePattern() {
		switch (borderStyle) {
		case SOLID:
		case NONE:
			return (short) 0;
		case DOTTED:
			return (short) 0x0F0F;
		case DASHED:
			return (short) 0x00FF;
		case HIDDEN:
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * Sets the border style.
	 *
	 * @param borderStyle the new border style
	 */
	public void setBorderStyle(CSSBorderStyle borderStyle) {
		this.borderStyle = borderStyle;
		this.modifiedBorderStyle = true;
	}
	
	/**
	 * Gets the font.
	 *
	 * @return the font
	 */
	public IFont getFont() {
		CSSFontManager fm = new CSSFontManager(app);
		if (font == null) {
			font = fm.selectFont(getCssfont());
		} else {
			if (cssfont.isModified()) {
				font = fm.selectFont(getCssfont());
				cssfont.setModified(false);
			} else {
				//Do Nothing
			}
		}
	
		return font;
	}
	
	/**
	 * Sets the font.
	 *
	 * @param font the new font
	 */
	public void setFont(IFont font) {
		this.font = font;
		this.modifiedFont = true;
	}
	
	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}
	
	/**
	 * Sets the width.
	 *
	 * @param width the new width
	 */
	public void setWidth(float width) {
		this.width = width;
		this.modifiedWidth = true;
	}
	
	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}
	
	/**
	 * Sets the height.
	 *
	 * @param height the new height
	 */
	public void setHeight(float height) {
		this.height = height;
		this.modifiedHeight = true;
	}
	
	/**
	 * Gets the depth.
	 *
	 * @return the depth
	 */
	public float getDepth() {
		return depth;
	}
	
	/**
	 * Sets the depth.
	 *
	 * @param depth the new depth
	 */
	public void setDepth(float depth) {
		this.depth = depth;
		this.modifiedDepth = true;
	}
	
	
	/**
	 * Gets the border width.
	 *
	 * @return the border width
	 */
	public float getBorderWidth() {
		return borderWidth;
	}
	
	/**
	 * Sets the border width.
	 *
	 * @param borderWidth the new border width
	 */
	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		this.modifiedBorderWidth = true;
	}
	
	/**
	 * Gets the padding width.
	 *
	 * @return the padding width
	 */
	public float getPaddingWidth() {
		return paddingWidth;
	}
	
	/**
	 * Sets the padding width.
	 *
	 * @param paddingWidth the new padding width
	 */
	public void setPaddingWidth(float paddingWidth) {
		this.paddingWidth = paddingWidth;
		this.modifiedPaddingWidth = true;
	}
	

	
	/**
	 * Sets the font size.
	 *
	 * @return true, if is visibility
	 */
	//public void setFontSize(int fontSize) {
	//	this.fontSize = fontSize;
	//	this.modifiedFontSize = true;
	//}
	
	/**
	 * Checks if is visible.
	 *
	 * @return true, if is visibility
	 */
	public boolean isVisibility() {
		return visibility;
	}
	
	/**
	 * Sets the visibility.
	 *
	 * @param visibility the new visibility
	 */
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
		this.modifiedVisibility = true;
	}
	

	/**
	 * Checks if the width is a percentage.
	 *
	 * @return true, if is width percentage
	 */
	public boolean isWidthPercentage() {
		return widthPercentage;
	}

	/**
	 * Sets if the width is a percentage.
	 *
	 * @param widthPercentage the new width percentage
	 */
	public void setWidthPercentage(boolean widthPercentage) {
		this.widthPercentage = widthPercentage;
		this.modifiedWidthPercentage = true;
	}

	/**
	 * Checks if the height is a percentage.
	 *
	 * @return true, if is height percentage
	 */
	public boolean isHeightPercentage() {
		return heightPercentage;
	}

	/**
	 * Sets if the height is a percentage.
	 *
	 * @param heightPercentage the new height percentage
	 */
	public void setHeightPercentage(boolean heightPercentage) {
		this.heightPercentage = heightPercentage;
		this.modifiedHeightPercentage = true;
	}



	

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the uri.
	 *
	 * @param uri the new uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Checks if is modified uri.
	 *
	 * @return true, if is modified uri
	 */
	public boolean isModifiedUri() {
		return modifiedUri;
	}

	/**
	 * Sets, if the field has been modified: uri.
	 *
	 * @param modifiedUri the new modified uri
	 */
	public void setModifiedUri(boolean modifiedUri) {
		this.modifiedUri = modifiedUri;
	}

	/**
	 * Checks if is modified selector.
	 *
	 * @return true, if is modified selector
	 */
	public boolean isModifiedSelector() {
		return modifiedSelector;
	}

	/**
	 * Sets, if the field has been modified: selector.
	 *
	 * @param modifiedSelector the new modified selector
	 */
	public void setModifiedSelector(boolean modifiedSelector) {
		this.modifiedSelector = modifiedSelector;
	}

	/**
	 * Checks if is modified background color.
	 *
	 * @return true, if is modified background color
	 */
	public boolean isModifiedBackgroundColor() {
		return modifiedBackgroundColor;
	}

	/**
	 * Sets, if the field has been modified: background color.
	 *
	 * @param modifiedBackgroundColor the new modified background color
	 */
	public void setModifiedBackgroundColor(boolean modifiedBackgroundColor) {
		this.modifiedBackgroundColor = modifiedBackgroundColor;
	}



	/**
	 * Checks if is modified border color.
	 *
	 * @return true, if is modified border color
	 */
	public boolean isModifiedBorderColor() {
		return modifiedBorderColor;
	}

	/**
	 * Sets, if the field has been modified: border color.
	 *
	 * @param modifiedBorderColor the new modified border color
	 */
	public void setModifiedBorderColor(boolean modifiedBorderColor) {
		this.modifiedBorderColor = modifiedBorderColor;
	}

	/**
	 * Checks if is modified background image.
	 *
	 * @return true, if is modified background image
	 */
	public boolean isModifiedBackgroundImage() {
		return modifiedBackgroundImage;
	}

	/**
	 * Sets, if the field has been modified: background image.
	 *
	 * @param modifiedBackgroundImage the new modified background image
	 */
	public void setModifiedBackgroundImage(boolean modifiedBackgroundImage) {
		this.modifiedBackgroundImage = modifiedBackgroundImage;
	}

	/**
	 * Checks if is modified background position.
	 *
	 * @return true, if is modified background position
	 */
	public boolean isModifiedBackgroundPosition() {
		return modifiedBackgroundPosition;
	}

	/**
	 * Sets, if the field has been modified: background position.
	 *
	 * @param modifiedBackgroundPosition the new modified background position
	 */
	public void setModifiedBackgroundPosition(boolean modifiedBackgroundPosition) {
		this.modifiedBackgroundPosition = modifiedBackgroundPosition;
	}

	/**
	 * Checks if is modified background repeat.
	 *
	 * @return true, if is modified background repeat
	 */
	public boolean isModifiedBackgroundRepeat() {
		return modifiedBackgroundRepeat;
	}

	/**
	 * Sets, if the field has been modified: background repeat.
	 *
	 * @param modifiedBackgroundRepeat the new modified background repeat
	 */
	public void setModifiedBackgroundRepeat(boolean modifiedBackgroundRepeat) {
		this.modifiedBackgroundRepeat = modifiedBackgroundRepeat;
	}

	/**
	 * Checks if is modified border style.
	 *
	 * @return true, if is modified border style
	 */
	public boolean isModifiedBorderStyle() {
		return modifiedBorderStyle;
	}

	/**
	 * Sets, if the field has been modified: border style.
	 *
	 * @param modifiedBorderStyle the new modified border style
	 */
	public void setModifiedBorderStyle(boolean modifiedBorderStyle) {
		this.modifiedBorderStyle = modifiedBorderStyle;
	}

	/**
	 * Checks if is modified font.
	 *
	 * @return true, if is modified font
	 */
	public boolean isModifiedFont() {
		return modifiedFont;
	}

	/**
	 * Sets, if the field has been modified: font.
	 *
	 * @param modifiedFont the new modified font
	 */
	public void setModifiedFont(boolean modifiedFont) {
		this.modifiedFont = modifiedFont;
	}

	/**
	 * Checks if is modified cssfont.
	 *
	 * @return true, if is modified cssfont
	 */
	public boolean isModifiedCssfont() {
		return modifiedCssfont || cssfont.isModified();
	}

	/**
	 * Sets, if the field has been modified: cssfont.
	 *
	 * @param modifiedCssfont the new modified cssfont
	 */
	public void setModifiedCssfont(boolean modifiedCssfont) {
		this.modifiedCssfont = modifiedCssfont;
	}

	/**
	 * Checks if is modified width.
	 *
	 * @return true, if is modified width
	 */
	public boolean isModifiedWidth() {
		return modifiedWidth;
	}

	/**
	 * Sets, if the field has been modified: width.
	 *
	 * @param modifiedWidth the new modified width
	 */
	public void setModifiedWidth(boolean modifiedWidth) {
		this.modifiedWidth = modifiedWidth;
	}

	/**
	 * Checks if is modified height.
	 *
	 * @return true, if is modified height
	 */
	public boolean isModifiedHeight() {
		return modifiedHeight;
	}

	/**
	 * Sets, if the field has been modified: height.
	 *
	 * @param modifiedHeight the new modified height
	 */
	public void setModifiedHeight(boolean modifiedHeight) {
		this.modifiedHeight = modifiedHeight;
	}

	/**
	 * Checks if is modified depth.
	 *
	 * @return true, if is modified depth
	 */
	public boolean isModifiedDepth() {
		return modifiedDepth;
	}

	/**
	 * Sets, if the field has been modified: depth.
	 *
	 * @param modifiedDepth the new modified depth
	 */
	public void setModifiedDepth(boolean modifiedDepth) {
		this.modifiedDepth = modifiedDepth;
	}

	/**
	 * Checks if is modified width percentage.
	 *
	 * @return true, if is modified width percentage
	 */
	public boolean isModifiedWidthPercentage() {
		return modifiedWidthPercentage;
	}

	/**
	 * Sets, if the field has been modified: width percentage.
	 *
	 * @param modifiedWidthPercentage the new modified width percentage
	 */
	public void setModifiedWidthPercentage(boolean modifiedWidthPercentage) {
		this.modifiedWidthPercentage = modifiedWidthPercentage;
	}

	/**
	 * Checks if is modified height percentage.
	 *
	 * @return true, if is modified height percentage
	 */
	public boolean isModifiedHeightPercentage() {
		return modifiedHeightPercentage;
	}

	/**
	 * Sets, if the field has been modified: height percentage.
	 *
	 * @param modifiedHeightPercentage the new modified height percentage
	 */
	public void setModifiedHeightPercentage(boolean modifiedHeightPercentage) {
		this.modifiedHeightPercentage = modifiedHeightPercentage;
	}


	/**
	 * Checks if is modified border width.
	 *
	 * @return true, if is modified border width
	 */
	public boolean isModifiedBorderWidth() {
		return modifiedBorderWidth;
	}

	/**
	 * Sets, if the field has been modified: border width.
	 *
	 * @param modifiedBorderWidth the new modified border width
	 */
	public void setModifiedBorderWidth(boolean modifiedBorderWidth) {
		this.modifiedBorderWidth = modifiedBorderWidth;
	}

	/**
	 * Checks if is modified padding width.
	 *
	 * @return true, if is modified padding width
	 */
	public boolean isModifiedPaddingWidth() {
		return modifiedPaddingWidth;
	}

	/**
	 * Sets, if the field has been modified: padding width.
	 *
	 * @param modifiedPaddingWidth the new modified padding width
	 */
	public void setModifiedPaddingWidth(boolean modifiedPaddingWidth) {
		this.modifiedPaddingWidth = modifiedPaddingWidth;
	}


	/**
	 * Checks if is modified visibility.
	 *
	 * @return true, if is modified visibility
	 */
	public boolean isModifiedVisibility() {
		return modifiedVisibility;
	}

	/**
	 * Sets, if the field has been modified: visibility.
	 *
	 * @param modifiedVisibility the new modified visibility
	 */
	public void setModifiedVisibility(boolean modifiedVisibility) {
		this.modifiedVisibility = modifiedVisibility;
	}

	/**
	 * Checks if is modified z index.
	 *
	 * @return true, if is modified z index
	 */
	public boolean isModifiedZIndex() {
		return modifiedZIndex;
	}

	/**
	 * Sets, if the field has been modified: z index.
	 *
	 * @param modifiedZIndex the new modified z index
	 */
	public void setModifiedZIndex(boolean modifiedZIndex) {
		this.modifiedZIndex = modifiedZIndex;
	}
	
	/**
	 * Merges two style sheets.
	 *
	 * @param s the style sheet to be added
	 */
	public void addStyleSheet(CSSStyle s) {
		CSSStyle v = this;
		if (s.isModifiedBackgroundColor()) {
			v.setBackgroundColor(s.getBackgroundColor());
		}
		if (s.isModifiedBackgroundImage()) {
			v.setBackgroundImage(s.getBackgroundImage());
			v.setBackgroundPosition(s.getBackgroundPosition());
			v.setBackgroundRepeat(s.getBackgroundRepeat());
		}
		if (s.isModifiedBackgroundPosition()) {
			v.setBackgroundPosition(s.getBackgroundPosition());
		}
		if (s.isModifiedBackgroundRepeat()) {
			v.setBackgroundRepeat(s.getBackgroundRepeat());
		}
		if (s.isModifiedBorderColor()) {
			v.setBorderColor(s.getBorderColor());
		}
		if (s.isModifiedBorderStyle()) {
			v.setBorderStyle(s.getBorderStyle());
		}
		if (s.isModifiedBorderWidth()) {
			v.setBorderWidth(s.getBorderWidth());
		}

		if (s.isModifiedCssfont()) {
			v.setCssfont(s.getCssfont().clone());
		}
		if (s.isModifiedDepth()) {
			v.setDepth(s.getDepth());
		}

		if (s.isModifiedHeight()) {
			v.setHeight(s.getHeight());
			v.setHeightPercentage(s.isHeightPercentage());
		}
		if (s.isModifiedPaddingWidth()) {
			v.setPaddingWidth(s.getPaddingWidth());
		}
		if (s.isModifiedVisibility()) {
			v.setVisibility(s.isVisibility());
		}
		if (s.isModifiedWidth()) {
			v.setWidth(s.getWidth());
			v.setWidthPercentage(s.isWidthPercentage());
		}

		if (s.isModifiedOpacity()) {
			v.setOpacity(s.getOpacity());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((app == null) ? 0 : app.hashCode());
		result = prime * result
				+ ((backgroundColor == null) ? 0 : backgroundColor.hashCode());
		result = prime * result
				+ ((backgroundImage == null) ? 0 : backgroundImage.hashCode());
		result = prime
				* result
				+ ((backgroundPosition == null) ? 0 : backgroundPosition
						.hashCode());
		result = prime
				* result
				+ ((backgroundRepeat == null) ? 0 : backgroundRepeat.hashCode());
		result = prime * result
				+ ((borderColor == null) ? 0 : borderColor.hashCode());
		result = prime * result
				+ ((borderStyle == null) ? 0 : borderStyle.hashCode());
		result = prime * result + Float.floatToIntBits(borderWidth);
		result = prime * result + ((cssfont == null) ? 0 : cssfont.hashCode());
		result = prime * result + Float.floatToIntBits(depth);
		result = prime * result + Float.floatToIntBits(height);
		result = prime * result + (heightPercentage ? 1231 : 1237);
		result = prime * result + (modifiedBackgroundColor ? 1231 : 1237);
		result = prime * result + (modifiedBackgroundImage ? 1231 : 1237);
		result = prime * result + (modifiedBackgroundPosition ? 1231 : 1237);
		result = prime * result + (modifiedBackgroundRepeat ? 1231 : 1237);
		result = prime * result + (modifiedBorderColor ? 1231 : 1237);
		result = prime * result + (modifiedBorderStyle ? 1231 : 1237);
		result = prime * result + (modifiedBorderWidth ? 1231 : 1237);
		result = prime * result + (modifiedCssfont ? 1231 : 1237);
		result = prime * result + (modifiedDepth ? 1231 : 1237);
		result = prime * result + (modifiedFont ? 1231 : 1237);
		result = prime * result + (modifiedHeight ? 1231 : 1237);
		result = prime * result + (modifiedHeightPercentage ? 1231 : 1237);
		result = prime * result + (modifiedOpacity ? 1231 : 1237);
		result = prime * result + (modifiedPaddingWidth ? 1231 : 1237);
		result = prime * result + (modifiedSelector ? 1231 : 1237);
		result = prime * result + (modifiedUri ? 1231 : 1237);
		result = prime * result + (modifiedVisibility ? 1231 : 1237);
		result = prime * result + (modifiedWidth ? 1231 : 1237);
		result = prime * result + (modifiedWidthPercentage ? 1231 : 1237);
		result = prime * result + (modifiedZIndex ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(opacity);
		result = prime * result + Float.floatToIntBits(paddingWidth);
		result = prime * result
				+ ((selector == null) ? 0 : selector.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + (visibility ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(width);
		result = prime * result + (widthPercentage ? 1231 : 1237);
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
		CSSStyle other = (CSSStyle) obj;
		if (app == null) {
			if (other.app != null)
				return false;
		} else if (!app.equals(other.app))
			return false;
		if (backgroundColor == null) {
			if (other.backgroundColor != null)
				return false;
		} else if (!backgroundColor.equals(other.backgroundColor))
			return false;
		if (backgroundImage == null) {
			if (other.backgroundImage != null)
				return false;
		} else if (!backgroundImage.equals(other.backgroundImage))
			return false;
		if (backgroundPosition == null) {
			if (other.backgroundPosition != null)
				return false;
		} else if (!backgroundPosition.equals(other.backgroundPosition))
			return false;
		if (backgroundRepeat != other.backgroundRepeat)
			return false;
		if (borderColor == null) {
			if (other.borderColor != null)
				return false;
		} else if (!borderColor.equals(other.borderColor))
			return false;
		if (borderStyle != other.borderStyle)
			return false;
		if (Float.floatToIntBits(borderWidth) != Float
				.floatToIntBits(other.borderWidth))
			return false;
		if (cssfont == null) {
			if (other.cssfont != null)
				return false;
		} else if (!cssfont.equals(other.cssfont))
			return false;
		if (Float.floatToIntBits(depth) != Float.floatToIntBits(other.depth))
			return false;
		if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height))
			return false;
		if (heightPercentage != other.heightPercentage)
			return false;
		if (modifiedBackgroundColor != other.modifiedBackgroundColor)
			return false;
		if (modifiedBackgroundImage != other.modifiedBackgroundImage)
			return false;
		if (modifiedBackgroundPosition != other.modifiedBackgroundPosition)
			return false;
		if (modifiedBackgroundRepeat != other.modifiedBackgroundRepeat)
			return false;
		if (modifiedBorderColor != other.modifiedBorderColor)
			return false;
		if (modifiedBorderStyle != other.modifiedBorderStyle)
			return false;
		if (modifiedBorderWidth != other.modifiedBorderWidth)
			return false;
		if (modifiedCssfont != other.modifiedCssfont)
			return false;
		if (modifiedDepth != other.modifiedDepth)
			return false;
		if (modifiedFont != other.modifiedFont)
			return false;
		if (modifiedHeight != other.modifiedHeight)
			return false;
		if (modifiedHeightPercentage != other.modifiedHeightPercentage)
			return false;
		if (modifiedOpacity != other.modifiedOpacity)
			return false;
		if (modifiedPaddingWidth != other.modifiedPaddingWidth)
			return false;
		if (modifiedSelector != other.modifiedSelector)
			return false;
		if (modifiedUri != other.modifiedUri)
			return false;
		if (modifiedVisibility != other.modifiedVisibility)
			return false;
		if (modifiedWidth != other.modifiedWidth)
			return false;
		if (modifiedWidthPercentage != other.modifiedWidthPercentage)
			return false;
		if (modifiedZIndex != other.modifiedZIndex)
			return false;
		if (Float.floatToIntBits(opacity) != Float
				.floatToIntBits(other.opacity))
			return false;
		if (Float.floatToIntBits(paddingWidth) != Float
				.floatToIntBits(other.paddingWidth))
			return false;
		if (selector == null) {
			if (other.selector != null)
				return false;
		} else if (!selector.equals(other.selector))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		if (visibility != other.visibility)
			return false;
		if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
			return false;
		if (widthPercentage != other.widthPercentage)
			return false;
		return true;
	}


	
	
}
