package org.mt4j.components.css.util;

import java.util.Arrays;

import org.mt4j.util.MTColor;
import org.w3c.css.sac.LexicalUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class CSSKeywords (contains various enums, lists of constants etc.)
 */
public class CSSKeywords {

	/** The colour names. */
	public static String[] colourNames = new String[] { "BLACK", "WHITE",
			"SILVER", "GREY", "GRAY", "AQUA", "BLUE", "NAVY", "FUCHSIA",
			"MAROON", "RED", "PURPLE", "GREEN", "LIME", "TEAL", "YELLOW",
			"OLIVE" };
	
	
	/** The background position names. */
	public static String[] backgroundPositionNames = new String[] { "LEFT",
			"RIGHT", "BOTTOM", "TOP", "CENTER" };

	/** The background repeat names. */
	public static String[] backgroundRepeatNames = new String[] { "REPEAT-X",
			"REPEAT-Y", "REPEAT", "NO-REPEAT" };

	/** The background attachment names. */
	public static String[] backgroundAttachmentNames = new String[] { "FIXED",
			"SCROLL" };

	/** The border style names. */
	public static String[] borderStyleNames = new String[] { "SOLID", "NONE",
			"DASHED", "DOTTED" };

	/** The font family names. */
	public static String[] fontFamilyNames = new String[] { "SERIF",
			"SANS-SERIF", "MONO", "MONOSPACE" };

	/** The font style names. */
	public static String[] fontStyleNames = new String[] { "ITALIC", "OBLIQUE",
			"NORMAL" };

	/** The font weight names. */
	public static String[] fontWeightNames = new String[] { "BOLD", "BOLDER",
			"LIGHTER", "LIGHT" };

	/** The measuring units. */
	public static short[] measuringUnits = new short[] {
			LexicalUnit.SAC_CENTIMETER, LexicalUnit.SAC_INCH,
			LexicalUnit.SAC_MILLIMETER, LexicalUnit.SAC_PIXEL,
			LexicalUnit.SAC_POINT, LexicalUnit.SAC_PICA, LexicalUnit.SAC_EM,
			LexicalUnit.SAC_INTEGER, LexicalUnit.SAC_REAL,
			LexicalUnit.SAC_PERCENTAGE };

	/** The border width names. */
	public static String[] borderWidthNames = new String[] { "THIN", "MEDIUM",
			"THICK" };

	/** The string values. */
	public static short[] stringValues = new short[] { LexicalUnit.SAC_IDENT,
			LexicalUnit.SAC_STRING_VALUE };

	/**
	 * Checks if LexicalUnit is a measuring unit.
	 * 
	 * @param lu the LexicalUnit
	 * @return true, if is measuring unit
	 */
	public static boolean isMeasuringUnit(LexicalUnit lu) {

		if (contains(measuringUnits, lu))
			return true;
		return false;
	}

	/**
	 * Checks if an Array of short contains a specific LexicalUnit-Type.
	 *
	 * @param keywords the keywords (short)
	 * @param lu the LexicalUnit
	 * @return true, if successful
	 */
	private static boolean contains(short[] keywords, LexicalUnit lu) {
		for (short s : keywords) {
			if (s == lu.getLexicalUnitType())
				return true;
		}
		return false;
	}

	/**
	 * Checks if LexicalUnit contains a string.
	 * 
	 * @param lu the LexicalUnit
	 * @return true, if is string
	 */
	public static boolean isString(LexicalUnit lu) {
		if (contains(stringValues, lu))
			return true;
		return false;
	}

	/**
	 * Checks, if an Array of String contains a certain string (Ignore Case).
	 *
	 * @param keywords the keywords
	 * @param lu the LexicalUnit
	 * @return true, if successful
	 */
	private static boolean contains(String[] keywords, LexicalUnit lu) {
		if (isString(lu)) {
			if (Arrays.asList(keywords).contains(
					lu.getStringValue().toUpperCase().replaceAll(" ", "")))
				return true;
		}
		return false;
	}

	/**
	 * Checks if LexicalUnit is color.
	 * 
	 * @param lu
	 *            the lu
	 * @return true, if is color
	 */
	public static boolean isColor(LexicalUnit lu) {
		if (lu.getLexicalUnitType() == LexicalUnit.SAC_RGBCOLOR)
			return true;

		return contains(colourNames, lu);
	}

	/**
	 * Checks if LexicalUnit is background position.
	 * 
	 * @param lu
	 *            the LexicalUnit 
	 * @return true, if is background position
	 */
	public static boolean isBackgroundPosition(LexicalUnit lu) {
		return contains(backgroundPositionNames, lu);
	}

	/**
	 * Checks if LexicalUnit is background repeat.
	 * 
	 * @param lu
	 *            the LexicalUnit 
	 * @return true, if is background repeat
	 */
	public static boolean isBackgroundRepeat(LexicalUnit lu) {
		return contains(backgroundRepeatNames, lu);
	}

	/**
	 * Checks if LexicalUnit is background attachment.
	 * 
	 * @param lu
	 *            the LexicalUnit 
	 * @return true, if is background attachment
	 */
	public static boolean isBackgroundAttachment(LexicalUnit lu) {
		return contains(backgroundAttachmentNames, lu);
	}

	/**
	 * Checks if LexicalUnit is border style.
	 * 
	 * @param lu
	 *            the LexicalUnit 
	 * @return true, if is border style
	 */
	public static boolean isBorderStyle(LexicalUnit lu) {
		return contains(borderStyleNames, lu);
	}

	/**
	 * Checks if LexicalUnit is border width.
	 * 
	 * @param lu
	 *            the LexicalUnit 
	 * @return true, if is border width
	 */
	public static boolean isBorderWidth(LexicalUnit lu) {
		return contains(borderWidthNames, lu);
	}

	/**
	 * Checks if LexicalUnit is font style.
	 * 
	 * @param lu
	 *            the LexicalUnit 
	 * @return true, if is font style
	 */
	public static boolean isFontStyle(LexicalUnit lu) {
		return contains(fontStyleNames, lu);
	}

	/**
	 * Checks if LexicalUnit is font family.
	 * 
	 * @param lu
	 *            the LexicalUnit 
	 * @return true, if is font family
	 */
	public static boolean isFontFamily(LexicalUnit lu) {
		return contains(fontFamilyNames, lu);
	}

	/**
	 * Checks if LexicalUnit is font weight.
	 * 
	 * @param lu
	 *            the lu
	 * @return true, if LexicalUnit is font weight
	 */
	public static boolean isFontWeight(LexicalUnit lu) {
		return contains(fontWeightNames, lu);
	}

	/**
	 * The Enum CSSFontWeight.
	 */
	public enum CSSFontWeight {

		/** The BOLD. */
		BOLD,
		/** The LIGHT. */
		LIGHT,
		/** The NORMAL. */
		NORMAL;
	}

	/**
	 * The Enum CSSSelectorType.
	 */
	public enum CSSSelectorType {

		/** The UNIVERSAL. */
		UNIVERSAL,
		/** The TYPE. */
		TYPE,
		/** The ID. */
		ID,
		/** The CLASS. */
		CLASS,
		/** The CUSTOM. */
		CUSTOM;
	}

	/**
	 * The Enum CSSFontFamily.
	 */
	public enum CSSFontFamily {

		/** The SANS. */
		SANS,
		/** The SERIF. */
		SERIF,
		/** The MONO. */
		MONO,
		/** The CUSTOM. */
		CUSTOM,
		/** The DEFAULT. */
		DEFAULT;
	}

	/**
	 * The Enum CSSFontStyle.
	 */
	public enum CSSFontStyle {

		/** The ITALIC. */
		ITALIC,
		/** The OBLIQUE. */
		OBLIQUE,
		/** The NORMAL. */
		NORMAL;
	}

	/**
	 * The Enum CSSBorderStyle.
	 */
	public enum CSSBorderStyle {

		/** The SOLID. */
		SOLID,
		/** The DASHED. */
		DASHED,
		/** The DOTTED. */
		DOTTED,
		/** The NONE. */
		NONE,
		/** The HIDDEN. */
		HIDDEN;
	}

	/**
	 * The Enum cssproperties.
	 */
	public enum cssproperties {

		/** The COLOR. */
		COLOR,
		/** The BACKGROUNDCOLOR. */
		BACKGROUNDCOLOR,
		/** The BORDERCOLOR. */
		BORDERCOLOR,
		/** The BACKGROUNDIMAGE. */
		BACKGROUNDIMAGE,
		/** The BACKGROUNDPOSITION. */
		BACKGROUNDPOSITION,
		/** The BACKGROUNDREPEAT. */
		BACKGROUNDREPEAT,
		/** The BORDERSTYLE. */
		BORDERSTYLE,
		/** The FONTFAMILY. */
		FONTFAMILY,
		/** The FONT. */
		FONT,
		/** The FONTSIZE. */
		FONTSIZE,
		/** The FONTSTYLE. */
		FONTSTYLE,
		/** The FONTWEIGHT. */
		FONTWEIGHT,
		/** The WIDTH. */
		WIDTH,
		/** The HEIGHT. */
		HEIGHT,
		/** The BORDERWIDTH. */
		BORDERWIDTH,
		/** The PADDING. */
		PADDING,
		/** The VISIBILITY. */
		VISIBILITY,
		/** The ZINDEX. */
		ZINDEX,
		/** The BORDER. */
		BORDER,
		/** The UNKNOWN. */
		UNKNOWN,
		/** The BACKGROUND. */
		BACKGROUND,
		
		/** The OPACITY. */
		OPACITY
	}
	
	/**
	 * The Enum Position.
	 */
	public enum Position {
		
		/** The LEFT. */
		LEFT, 
 /** The RIGHT. */
 RIGHT, 
 /** The TOP. */
 TOP, 
 /** The BOTTOM. */
 BOTTOM, 
 /** The CENTER. */
 CENTER;
	}
	
	
	/**
	 * The Enum PositionType.
	 */
	public enum PositionType {
		
		/** The ABSOLUTE. */
		ABSOLUTE, 
 /** The RELATIVE. */
 RELATIVE, 
 /** The KEYWORD. */
 KEYWORD;
	}
	
	/**
	 * Create a color from the hex value String
	 *
	 * @param string the string
	 * @return the mT color
	 */
	public final MTColor colorFromHex(String string) {
		float red = 0;
		float green = 0;
		float blue = 0;
		if (string.length() == 3) {
			red = Integer.valueOf(string.substring(0, 0) + string.substring(0, 0)).intValue();
			green = Integer.valueOf(string.substring(1, 1)+ string.substring(1, 1)).intValue();
			blue = Integer.valueOf(string.substring(2, 2)+ string.substring(2, 2)).intValue();
		} else if (string.length() == 6) {
			red = Integer.valueOf(string.substring(0, 1)).intValue();
			green = Integer.valueOf(string.substring(2, 3)).intValue();
			blue = Integer.valueOf(string.substring(4, 5)).intValue();
		} else return MTColor.WHITE;

		return new MTColor(red,green,blue);
	}
	
}
