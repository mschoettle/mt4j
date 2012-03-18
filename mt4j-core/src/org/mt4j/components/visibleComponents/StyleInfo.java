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
package org.mt4j.components.visibleComponents;

import org.mt4j.util.MTColor;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GLMaterial;


/**
 * The Class StyleInfo.
 * @author Christopher Ruff
 */
public class StyleInfo {
	
	private MTColor fillColor;
	
	private MTColor strokeColor;
	
	/** The draw smooth. */
	private boolean drawSmooth;
	
	/** The no stroke. */
	private boolean noStroke;
	
	/** The no fill. */
	private boolean noFill;
	
	/** The stroke weight. */
	private float strokeWeight;
	
	/** The fill draw mode. */
	private int fillDrawMode;
	
	/** The line stipple pattern. */
	private short lineStipplePattern;
	
	/** The material. */
	private GLMaterial material;
	
	/**
	 * Instantiates a new style info.
	 */
	public StyleInfo(){
		this(
				new MTColor(255,255,255,255),
				new MTColor(255,255,255,255),
				true,
				false,
				false,
				1.0f,
				GL10.GL_TRIANGLE_FAN,
				(short)0				
			);
	}
	
	/**
	 * Instantiates a new style info.
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * @param drawSmooth the draw smooth
	 * @param noStroke the no stroke
	 * @param noFill the no fill
	 * @param strokeWeight the stroke weight
	 * @param fillDrawMode the fill draw mode
	 * @param lineStipplePattern the line stipple pattern
	 */
	public StyleInfo(
			MTColor fillColor,
			MTColor strokeColor,
			boolean drawSmooth, 
			boolean noStroke, 
			boolean noFill, 
			float strokeWeight, 
			int fillDrawMode, 
			short lineStipplePattern			
		) {
		super();
		this.fillColor = fillColor;
		this.strokeColor = strokeColor;
		this.drawSmooth = drawSmooth;
		this.noStroke = noStroke;
		this.noFill = noFill;
		this.strokeWeight = strokeWeight;
		this.fillDrawMode = fillDrawMode;
		this.lineStipplePattern = lineStipplePattern;
		
		this.material = null;
	}


	
	/**
	 * Checks if is draw smooth.
	 * 
	 * @return true, if is draw smooth
	 */
	public boolean isDrawSmooth() {
		return drawSmooth;
	}

	/**
	 * Sets the draw smooth.
	 * 
	 * @param drawSmooth the new draw smooth
	 */
	public void setDrawSmooth(boolean drawSmooth) {
		this.drawSmooth = drawSmooth;
	}


	
	/**
	 * Sets the fill color.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public void setFillColor(float r, float g, float b, float a){
		this.setFillRed(r);
		this.setFillGreen(g);
		this.setFillBlue(b);
		this.setFillAlpha(a);
	}
	
	public void setFillColor(MTColor fillColor){
		this.fillColor = fillColor;
	}
	
	public MTColor getFillColor(){
		return this.fillColor;
	}

	/**
	 * Sets the fill alpha.
	 * 
	 * @param fillAlpha the new fill alpha
	 */
	public void setFillAlpha(float fillAlpha) {
		this.fillColor.setAlpha(fillAlpha);
	}
	/**
	 * Gets the fill alpha.
	 * 
	 * @return the fill alpha
	 */
	public float getFillAlpha() {
		return this.fillColor.getAlpha();
	}
	/**
	 * Gets the fill blue.
	 * 
	 * @return the fill blue
	 */
	public float getFillBlue() {
		return this.fillColor.getB();
	}

	/**
	 * Sets the fill blue.
	 * 
	 * @param fillBlue the new fill blue
	 */
	public void setFillBlue(float fillBlue) {
		this.fillColor.setB(fillBlue);
	}

	/**
	 * Gets the fill green.
	 * 
	 * @return the fill green
	 */
	public float getFillGreen() {
		return this.fillColor.getG();
	}

	/**
	 * Sets the fill green.
	 * 
	 * @param fillGreen the new fill green
	 */
	public void setFillGreen(float fillGreen) {
		this.fillColor.setG(fillGreen);
	}

	/**
	 * Gets the fill red.
	 * 
	 * @return the fill red
	 */
	public float getFillRed() {
		return this.fillColor.getR();
	}

	/**
	 * Sets the fill red.
	 * 
	 * @param fillRed the new fill red
	 */
	public void setFillRed(float fillRed) {
		this.fillColor.setR(fillRed);
	}

	/**
	 * Checks if is no fill.
	 * 
	 * @return true, if is no fill
	 */
	public boolean isNoFill() {
		return noFill;
	}

	/**
	 * Sets the no fill.
	 * 
	 * @param noFill the new no fill
	 */
	public void setNoFill(boolean noFill) {
		this.noFill = noFill;
	}

	/**
	 * Checks if is no stroke.
	 * 
	 * @return true, if is no stroke
	 */
	public boolean isNoStroke() {
		return noStroke;
	}

	/**
	 * Sets the no stroke.
	 * 
	 * @param noStroke the new no stroke
	 */
	public void setNoStroke(boolean noStroke) {
		this.noStroke = noStroke;
	}
	
	/**
	 * Sets the stroke color.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public void setStrokeColor(float r, float g, float b, float a){
		this.setStrokeRed(r);
		this.setStrokeGreen(g);
		this.setStrokeBlue(b);
		this.setStrokeAlpha(a);
	}
	
	public void setStrokeColor(MTColor strokeColor){
		this.strokeColor = strokeColor;
	}

	public MTColor getStrokeColor() {
		return this.strokeColor;
	}
	
	/**
	 * Gets the stroke alpha.
	 * 
	 * @return the stroke alpha
	 */
	public float getStrokeAlpha() {
		return this.strokeColor.getAlpha();
	}

	/**
	 * Sets the stroke alpha.
	 * 
	 * @param strokeAlpha the new stroke alpha
	 */
	public void setStrokeAlpha(float strokeAlpha) {
		this.strokeColor.setAlpha(strokeAlpha);
	}

	/**
	 * Gets the stroke blue.
	 * 
	 * @return the stroke blue
	 */
	public float getStrokeBlue() {
		return this.strokeColor.getB();
	}

	/**
	 * Sets the stroke blue.
	 * 
	 * @param strokeBlue the new stroke blue
	 */
	public void setStrokeBlue(float strokeBlue) {
		this.strokeColor.setB(strokeBlue);
	}

	/**
	 * Gets the stroke green.
	 * 
	 * @return the stroke green
	 */
	public float getStrokeGreen() {
		return this.strokeColor.getG();
	}

	/**
	 * Sets the stroke green.
	 * 
	 * @param strokeGreen the new stroke green
	 */
	public void setStrokeGreen(float strokeGreen) {
		this.strokeColor.setG(strokeGreen);
	}

	/**
	 * Gets the stroke red.
	 * 
	 * @return the stroke red
	 */
	public float getStrokeRed() {
		return this.strokeColor.getR();
	}

	/**
	 * Sets the stroke red.
	 * 
	 * @param strokeRed the new stroke red
	 */
	public void setStrokeRed(float strokeRed) {
		this.strokeColor.setR(strokeRed);
	}

	/**
	 * Gets the stroke weight.
	 * 
	 * @return the stroke weight
	 */
	public float getStrokeWeight() {
		return strokeWeight;
	}

	/**
	 * Sets the stroke weight.
	 * 
	 * @param strokeWeight the new stroke weight
	 */
	public void setStrokeWeight(float strokeWeight) {
		this.strokeWeight = strokeWeight;
	}
	
	
	/**
	 * Gets the fill draw mode.
	 * 
	 * @return the fill draw mode
	 */
	public int getFillDrawMode() {
		return fillDrawMode;
	}
	
	/**
	 * Sets the draw mode which will be used for creating display lists.
	 * <br>Modes are the opengl draw modes, e.g. GL_POLYGON, GL_TRIANGLE_FAN, GL_LINES etc.
	 * <br>Default mode is GL_TRIANGLE_FAN
	 * 
	 * @param fillDrawMode the fill draw mode
	 */
	public void setFillDrawMode(int fillDrawMode) {
		this.fillDrawMode = fillDrawMode;
	}
	
	/**
	 * Sets a line stipple pattern for drawing outlines.
	 * <br>Only supported under OpenGL, not avaiable in OpenGL ES!
	 * <br>Example: shape.setLineStipple((short)0xDDDD);
	 * <br>Default value is '0'. No stipple should be used then.
	 * 
	 * @param stipplePattern the stipple pattern
	 */
	public void setLineStipple(short stipplePattern){
		lineStipplePattern = stipplePattern;
	}
	
	
	/**
	 * Gets the line stipple.
	 * 
	 * @return the line stipple
	 */
	public short getLineStipple() {
		return lineStipplePattern;
	}

	
	/**
	 * Gets the material.
	 * 
	 * @return the material
	 */
	public GLMaterial getMaterial() {
		return material;
	}

	/**
	 * Sets the material.
	 * 
	 * @param material the new material
	 */
	public void setMaterial(GLMaterial material) {
		this.material = material;
	}


	
	
}
