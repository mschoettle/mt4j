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
package org.mt4j.util;


/**
 * The Class UnitTranslator.
 */
public class UnitTranslator {

	/** The Constant INCH_MM. */
	static final double INCH_MM = 25.4;

	/** The Constant POINT_POSTSCRIPT. */
	static final double POINT_POSTSCRIPT = 72.0;

	/**
	 * Pixels to inch.
	 * 
	 * @param pix the pix
	 * @param dpi the dpi
	 * 
	 * @return the double
	 */
	public static double pixelsToInch(int pix, int dpi) {
		return (double) pix / dpi;
	}

	/**
	 * Pixels to points.
	 * 
	 * @param pix the pix
	 * @param dpi the dpi
	 * 
	 * @return the double
	 */
	public static double pixelsToPoints(int pix, int dpi) {
		return pixelsToInch(pix, dpi) * POINT_POSTSCRIPT;
	}

	/**
	 * Pixels to millis.
	 * 
	 * @param pix the pix
	 * @param dpi the dpi
	 * 
	 * @return the double
	 */
	public static double pixelsToMillis(int pix, int dpi) {
		return pixelsToInch(pix, dpi) * INCH_MM;
	}

	/**
	 * Millis to points.
	 * 
	 * @param mm the mm
	 * 
	 * @return the double
	 */
	public static double millisToPoints(double mm) {
		return mm / INCH_MM * POINT_POSTSCRIPT;
	}

	/**
	 * Points to millis.
	 * 
	 * @param pt the pt
	 * 
	 * @return the double
	 */
	public static double pointsToMillis(double pt) {
		return pt / POINT_POSTSCRIPT * INCH_MM;
	}

	/**
	 * Points to pixels.
	 * 
	 * @param pt the pt
	 * @param dpi the dpi
	 * 
	 * @return the int
	 */
	public static int pointsToPixels(double pt, int dpi) {
		return millisToPixels(pointsToMillis(pt), dpi);
	}

	/**
	 * Millis to pixels.
	 * 
	 * @param mm the mm
	 * @param dpi the dpi
	 * 
	 * @return the int
	 */
	public static int millisToPixels(double mm, int dpi) {
		return (int) (mm / INCH_MM * dpi);
	}


	/*
	  if (cssValue.getCssText().contains("px")){
							  strokeWidth = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
						  }else if (cssValue.getCssText().contains("pt")){
							  strokeWidth = v.getFloatValue(CSSPrimitiveValue.CSS_PT);
							  strokeWidth *= 1.25f;
						  }else if (cssValue.getCssText().contains("mm")){
							  strokeWidth = v.getFloatValue(CSSPrimitiveValue.CSS_MM);
							  strokeWidth *= 3.54;
						  }else if (cssValue.getCssText().contains("pc")){
							  strokeWidth = v.getFloatValue(CSSPrimitiveValue.CSS_PC);
							  strokeWidth *= 15;
						  }else if (cssValue.getCssText().contains("cm")){
							  strokeWidth = v.getFloatValue(CSSPrimitiveValue.CSS_CM);
							  strokeWidth *= 35.43f;
						  }else if (cssValue.getCssText().contains("in")){
							  strokeWidth = v.getFloatValue(CSSPrimitiveValue.CSS_IN);
							  strokeWidth *= 90;
						  }else if (cssValue.getCssText().contains("ems")){
							  strokeWidth = v.getFloatValue(CSSPrimitiveValue.CSS_EMS);
						  }else{
							  strokeWidth = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
						  }
	 */
}
