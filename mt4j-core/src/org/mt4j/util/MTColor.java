/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
 * The Class MTColor.
 * 
 */
public class MTColor {
	
	/** The name. */
	private String name;
	
//	/** The Constant RED. */
//	public static transient final String RED =  "RED";
//	
//	/** The Constant GREEN. */
//	public static transient final String GREEN =  "GREEN";
//	
//	/** The Constant BLUE. */
//	public static transient final String BLUE =  "BLUE";
//	
//	/** The Constant YELLOW. */
//	public static transient final String YELLOW = "YELLOW";
//	
//	/** The Constant BLACK. */
//	public static transient final String BLACK =  "BLACK";
//	
//	/** The Constant WHITE. */
//	public static transient final String WHITE =  "WHITE";
	
	/** The Constant ALPHA_NO_TRANSPARENCY. */
	public static transient final float ALPHA_NO_TRANSPARENCY = 255f;
	
	/** The Constant ALPHA_LIGHT_TRANSPARENCY. */
	public static transient final float ALPHA_LIGHT_TRANSPARENCY = 255f/1.5f;
	
	/** The Constant ALPHA_HALF_TRANSPARENCY. */
	public static transient final float ALPHA_HALF_TRANSPARENCY = 255f/2f;
	
	/** The Constant ALPHA_HIGH_TRANSPARENCY. */
	public static transient final float ALPHA_HIGH_TRANSPARENCY = 255f/4f;
	
	/** The Constant ALPHA_FULL_TRANSPARENCY. */
	public static transient final float ALPHA_FULL_TRANSPARENCY = 0f;
	
	/** The r. */
	private float r;
	
	/** The g. */
	private float g;
	
	/** The b. */
	private float b;
	
	/** The alpha. */
	private float alpha;
	
	private boolean writeProtected;
	
	
	public static final MTColor RED = new MTColor(255,0,0,255,true);
	
	public static final MTColor GREEN = new MTColor(0,128,0,255,true);
	
	public static final MTColor BLUE = new MTColor(0,0,255,255,true);
	
	public static final MTColor BLACK = new MTColor(0,0,0,255,true);
	
	public static final MTColor WHITE = new MTColor(255,255,255,255,true);
	
	public static final MTColor GREY = new MTColor(128,128,128,255,true);
	
	public static final MTColor GRAY = new MTColor(128,128,128,255,true);
	
	public static final MTColor SILVER = new MTColor(192,192,192,255,true);
	
	public static final MTColor MAROON = new MTColor(128,0,0,255,true);
	
	public static final MTColor PURPLE = new MTColor(128,0,128,255,true);
	
	public static final MTColor FUCHSIA = new MTColor(255,0,255,255,true);
	
	public static final MTColor LIME = new MTColor(0,255,0,255,true);
	
	public static final MTColor OLIVE = new MTColor(128,128,0,255,true);
	
	public static final MTColor YELLOW = new MTColor(255,255,0,255,true);
	
	public static final MTColor NAVY = new MTColor(0,0,128,255,true);
	
	public static final MTColor TEAL = new MTColor(0,128,128,255,true);
	
	public static final MTColor AQUA = new MTColor(0,255,255,255,true);
	
	public static MTColor randomColor(){
		return new MTColor((float)(Math.random() * 255), (float)(Math.random() * 255), (float)(Math.random() * 255), 255);
	}
	
	/**
	 * Instantiates a new mT color.
	 * 
	 * @param color the color
	 */
	public MTColor(MTColor color){
		this(color.getName(), color.getR(), color.getG(), color.getB(), color.getAlpha());
	}
	
	/**
	 * Instantiates a new mT color.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 */
	public MTColor(float r, float g, float b) {
		this(r, g, b, 255);
	}
	
	/**
	 * Instantiates a new mT color.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param alpha the alpha
	 */
	public MTColor(float r, float g, float b, float alpha) {
		this("undefined", r, g, b, alpha);
	}
	
	
	/**
	 * Instantiates a new mT color.
	 * 
	 * @param name the name
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 */
	public MTColor(String name, float r, float g, float b) {
		this(name, r, g, b, 255);
	}
	
	
	/**
	 * Instantiates a new mT color.
	 * 
	 * @param name the name
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param alpha the alpha
	 */
	public MTColor(String name, int r, int g, int b, int alpha) {
		this(name, (float)r, (float)g, (float)b, (float)alpha);
	}
	
	/**
	 * Instantiates a new mT color.
	 *
	 * @param name the name
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param alpha the alpha
	 * @param writeProtected the write protected
	 */
	public MTColor(int r, int g, int b, int alpha, boolean writeProtected) {
		this("undefined", (float)r, (float)g, (float)b, (float)alpha, writeProtected);
	}
	
	/**
	 * Instantiates a new mT color.
	 * 
	 * @param name the name
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 */
	public MTColor(String name, int r, int g, int b) {
		this(name, (float)r, (float)g, (float)b, 255f);
	}
	
	
	/**
	 * Instantiates a new mT color.
	 * 
	 * @param name the name
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param alpha the alpha
	 */
	public MTColor(String name, float r, float g, float b, float alpha) {
		this(name, r, g, b, alpha, false);
	}
	
	
	public MTColor(String name, float r, float g, float b, float alpha, boolean writeProtected) {
		this.setColor(r, g, b, alpha);
		this.name = name;
		this.writeProtected = writeProtected;
	}
	
	
	public boolean isWriteProtected() {
		return writeProtected;
	}


	/**
	 * Gets the r.
	 * 
	 * @return the r
	 */
	public float getR() {
		return r;
	}
	
	/**
	 * Sets the r.
	 * 
	 * @param r the new r
	 */
	public void setR(float r) {
		if (!this.isWriteProtected())
			this.r = r;
	}
	
	/**
	 * Gets the g.
	 * 
	 * @return the g
	 */
	public float getG() {
		return g;
	}
	
	/**
	 * Sets the g.
	 * 
	 * @param g the new g
	 */
	public void setG(float g) {
		if (!this.isWriteProtected())
			this.g = g;
	}
	
	/**
	 * Gets the b.
	 * 
	 * @return the b
	 */
	public float getB() {
		return b;
	}
	
	/**
	 * Sets the b.
	 * 
	 * @param b the new b
	 */
	public void setB(float b) {
		if (!this.isWriteProtected())
			this.b = b;
	}
	
	/**
	 * Gets the alpha.
	 * 
	 * @return the alpha
	 */
	public float getAlpha() {
		return alpha;
	}
	
	/**
	 * Sets the alpha.
	 * 
	 * @param alpha the new alpha
	 */
	public void setAlpha(float alpha) {
		if (!this.isWriteProtected())
			this.alpha = alpha;
	}
	
	/**
	 * Sets the color.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param alpha the alpha
	 */
	public void setColor(float r, float g, float b, float alpha){
		if (!this.isWriteProtected()){
			this.r = r;
			this.g = g;
			this.b = b;
			this.alpha = alpha;
		}
	}

	/**
	 * Sets the color.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 */
	public void setColor(float r, float g, float b){
		if (!this.isWriteProtected()){
			this.r = r;
			this.g = g;
			this.b = b;
			this.alpha = 255;
		}
	}
	
	/**
	 * Sets the color.
	 * 
	 * @param f the new color
	 */
	public void setColor(float f){
		if (!this.isWriteProtected()){
			this.r = f;
			this.g = f;
			this.b = f;
			this.alpha = 255;
		}
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the copy.
	 * 
	 * @return the copy
	 */
	public MTColor getCopy(){
		return new MTColor(this.getName(), this.getR(),this.getG(),this.getB(), this.getAlpha());
	}
	
	/*
	public static void fill(AbstractShape as, String colorName){
		ColorManager.getInstance().fill(as, colorName);
	}
	public static void stroke(AbstractShape as, String colorName){
		ColorManager.getInstance().stroke(as, colorName);
	}
	public static void fill(AbstractShape as, String colorName, float alpha){
		ColorManager.getInstance().fill(as, colorName, alpha);
	}
	public static void stroke(AbstractShape as, String colorName, float alpha){
		ColorManager.getInstance().stroke(as, colorName, alpha);
	}
	public static Color get(String colorName){
		return ColorManager.getInstance().getColor(colorName);
	}
	*/
	
	
	/**
	 * To color string.
	 * 
	 * @return the string
	 */
	public String toColorString(){
			return "Color{" + r + "," + g + "," + b + "_" + alpha + "}";
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.toColorString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof MTColor && this.toColorString().equals(((MTColor)obj).toColorString()));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toColorString().hashCode();
	}
}

