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

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.clipping.FillPaint;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.opengl.GLMaterial;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * The Class AbstractVisibleComponent. Abstract class for creating visible
 * components. It provides methods for storing and changing style information, like
 * the fill- and stroke color.
 * @author Christopher Ruff
 */
public abstract class AbstractVisibleComponent extends MTComponent {
	
	/** The style info. */
	private StyleInfo styleInfo;
	
	/** The fill paint. */
	private FillPaint fillPaint;

	/**
	 * Instantiates a new abstract visible component.
	 * 
	 * @param pApplet the applet
	 */
	public AbstractVisibleComponent(PApplet pApplet) {
		this(pApplet,"unnamed visible component", /*null,*/ null);
	}

	/**
	 * Instantiates a new abstract visible component.
	 * 
	 * @param pApplet the applet
	 * @param globalCamera the global camera
	 * @param objectCamera the object camera
	 */
	public AbstractVisibleComponent(PApplet pApplet, Icamera globalCamera, Icamera objectCamera) {
		this(pApplet,"unnamed visible component",/* globalCamera,*/ objectCamera);
	}

	/**
	 * Instantiates a new abstract visible component.
	 * 
	 * @param pApplet the applet
	 * @param name the name
	 * @param objectCamera the object camera
	 */
	public AbstractVisibleComponent(PApplet pApplet, String name, /*Icamera globalCamera,*/ Icamera objectCamera) {
		super(pApplet, name, /*globalCamera,*/ objectCamera);
		//DEFAULTS
		styleInfo = new StyleInfo();
	}

	
	abstract public void drawComponent(PGraphics g);
	

	/**
	 * Gets the fill paint.
	 * 
	 * @return the fill paint
	 */
	public FillPaint getFillPaint() {
		return fillPaint;
	}

	/**
	 * Sets the fill paint.
	 * 
	 * @param fillPaint the new fill paint
	 */
	public void setFillPaint(FillPaint fillPaint) {
		this.fillPaint = fillPaint;
		this.fillPaint.setShape(this);
	}


	@Override
	public void preDraw(PGraphics graphics) {
		super.preDraw(graphics);
		
		//Apply material if set 
		if (this.getMaterial() != null){
			this.getMaterial().apply();
		}
		
		//Apply the stencil buffer stettings for
		//drawing the gradient shape 
		//Also DONT draw the outline here because its
		//anti aliasing will be ruined by the stencil
		//Instead we draw the outline over the gradient
		//in postDraw() method
		savedNoStrokeSetting = this.isNoStroke();
		
		if (this.getFillPaint() != null){
			FillPaint fillPaint = this.getFillPaint();
			
			//Force drawing stencil shape without stroke
			this.setNoStroke(true);
			
			fillPaint.pre(graphics);
		}
		
		//FIXME outline gets drawn twice if gradient + childClipmask
		
		//FIXME TEST //Draw without stroke because the stencil
		//will ruin the antialiased stroke line
		//so we draw here without stroke but after all the clipped
		//children are drawn we draw the stroke outline over it all
		//in postDrawChildren()
		if (this.getChildClip() != null){
			if (!this.isNoStroke()){
				this.setNoStroke(true);
			}
		}
		
	}
	
	/** The saved no stroke setting. */
	private boolean savedNoStrokeSetting;


	@Override
	public void postDraw(PGraphics graphics) {
		super.postDraw(graphics);

		//Draw gradient
		if (this.getFillPaint() != null){
			FillPaint g = this.getFillPaint();
			//Draw gradient over shape!
			g.post(graphics);

			boolean savedNoFillSetting = this.isNoFill();
			//Draw shape outlines over gradient
			if (!savedNoStrokeSetting){
				this.setNoStroke(false);
				this.setNoFill(true);
				this.drawComponent(graphics);
				this.setNoFill(savedNoFillSetting);
			}
			this.setNoStroke(savedNoStrokeSetting);
		}
		
	//FIXME how to get original state back?
//		if (this.getMaterial() != null){
//			
//		}
	}

	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.MTComponent#postDrawChildren(processing.core.PGraphics)
	 */
	@Override
	public void postDrawChildren(PGraphics g) {
		//FIXME this is a hack to draw the outline of the shape
		//over the clipped children, to not process the clipmask
		//in the superclass we temporarily set it to null
		Clip saved = this.getChildClip();
		if (saved != null){
			saved.disableClip(g);
			//Draw shape outline over everything for an antialiased outline
			if (!savedNoStrokeSetting){
				boolean noFillSetting = this.isNoFill();
				this.setNoFill(true);
				this.setNoStroke(false);
				this.drawComponent(g);
				this.setNoFill(noFillSetting);
				this.setNoStroke(savedNoStrokeSetting);
			}
			//Set mask null because the superclass 
			//implementation would also try to pop the stencil stack value
			this.setChildClip(null);
		}
		
		super.postDrawChildren(g);
		
		if (saved != null){
			this.setChildClip(saved);
		}
	}
	
	
	
	/**
	 * Gets the style info.
	 * 
	 * @return the styleInfo
	 */
	public StyleInfo getStyleInfo() {
		return styleInfo;
	}

	/**
	 * Sets the style info.
	 * 
	 * @param styleInfo the styleInfo to set
	 */
	public void setStyleInfo(StyleInfo styleInfo) {
		this.styleInfo = styleInfo;
		this.applyStyle();
	}

	
	/**
	 * Apply style.
	 */
	protected void applyStyle(){
		//TODO apply all styleInfo settings to the component
		this.setFillColor(this.getStyleInfo().getFillColor());
		this.setStrokeColor(this.getStyleInfo().getStrokeColor());
		this.setLineStipple(this.getStyleInfo().getLineStipple());
		this.setMaterial(this.getStyleInfo().getMaterial());
		this.setNoFill(this.getStyleInfo().isNoFill());
		this.setNoStroke(this.getStyleInfo().isNoStroke());
		this.setFillDrawMode(this.getStyleInfo().getFillDrawMode());
	}




	/**
	 * Sets the fill color.
	 * 
	 * @param fillColor the new fill color
	 */
	public void setFillColor(MTColor fillColor){
		//Create a copy because if we use constant/write protected colors we cant change the component's color later
		this.styleInfo.setFillColor(new MTColor(fillColor)); 
	}

	/**
	 * Gets the fill color.
	 * 
	 * @return the fill color
	 */
	public MTColor getFillColor(){
		return styleInfo.getFillColor();
	}


	/**
	 * Sets the stroke color.
	 * 
	 * @param strokeColor the new stroke color
	 */
	public void setStrokeColor(MTColor strokeColor){
		//Create a copy because if we use constant/write protected colors we cant change the component's color later
		this.styleInfo.setStrokeColor(new MTColor(strokeColor));
	}

	/**
	 * Gets the stroke color.
	 * 
	 * @return the stroke color
	 */
	public MTColor getStrokeColor(){
		return styleInfo.getStrokeColor();
	}
	

	/**
	 * Gets the stroke weight.
	 * 
	 * @return the stroke weight
	 * 
	 * @see org.mt4j.components.visibleComponents.StyleInfo#getStrokeWeight()
	 */
	public float getStrokeWeight() {
		return styleInfo.getStrokeWeight();
	}


	/**
	 * Checks if is draw smooth.
	 * 
	 * @return true, if checks if is draw smooth
	 * 
	 * @see org.mt4j.components.visibleComponents.StyleInfo#isDrawSmooth()
	 */
	public boolean isDrawSmooth() {
		return styleInfo.isDrawSmooth();
	}

	/**
	 * Checks if is no fill.
	 * 
	 * @return true, if checks if is no fill
	 * 
	 * @see org.mt4j.components.visibleComponents.StyleInfo#isNoFill()
	 */
	public boolean isNoFill() {
		return styleInfo.isNoFill();
	}

	/**
	 * Checks if is no stroke.
	 * 
	 * @return true, if checks if is no stroke
	 * 
	 * @see org.mt4j.components.visibleComponents.StyleInfo#isNoStroke()
	 */
	public boolean isNoStroke() {
		return styleInfo.isNoStroke();
	}

	/**
	 * Sets the draw smooth.
	 * 
	 * @param drawSmooth the draw smooth
	 * 
	 * @see org.mt4j.components.visibleComponents.StyleInfo#setDrawSmooth(boolean)
	 */
	public void setDrawSmooth(boolean drawSmooth) {
		styleInfo.setDrawSmooth(drawSmooth);
	}


	
	
	/**
	 * Sets the no fill.
	 * 
	 * @param noFill the no fill
	 * 
	 * @see org.mt4j.components.visibleComponents.StyleInfo#setNoFill(boolean)
	 */
	public void setNoFill(boolean noFill) {
		styleInfo.setNoFill(noFill);
	}

	/**
	 * Sets the no stroke.
	 * 
	 * @param noStroke the no stroke
	 * 
	 * @see org.mt4j.components.visibleComponents.StyleInfo#setNoStroke(boolean)
	 */
	public void setNoStroke(boolean noStroke) {
		styleInfo.setNoStroke(noStroke);
	}


	
	
	/**
	 * Sets the stroke weight.
	 * 
	 * @param strokeWeight the stroke weight
	 * 
	 * @see org.mt4j.components.visibleComponents.StyleInfo#setStrokeWeight(float)
	 */
	public void setStrokeWeight(float strokeWeight) {
		styleInfo.setStrokeWeight(strokeWeight);
	}

	
	/**
	 * Gets the fill draw mode.
	 * 
	 * @return the fill draw mode
	 */
	public int getFillDrawMode() {
		return styleInfo.getFillDrawMode();
	}
	
	/**
	 * Sets the draw mode which will be used for creating display lists.
	 * <br>Modes are the opengl draw modes, e.g. GL_POLYGON, GL_TRIANGLE_FAN, GL_LINES etc.
	 * <br>Default mode is GL_TRIANGLE_FAN
	 * 
	 * @param fillDrawMode the fill draw mode
	 */
	public void setFillDrawMode(int fillDrawMode) {
		styleInfo.setFillDrawMode(fillDrawMode);
	}
	
	/**
	 * Sets a line stipple pattern for drawing outlines.
	 * <b><br>Only supported when using the OpenGL renderer!
	 * <br>Example: shape.setLineStipple((short)0xDDDD);
	 * <br>Default value is '0'. No stipple pattern is used then.
	 * 
	 * @param stipplePattern the stipple pattern
	 */
	public void setLineStipple(short stipplePattern){
		if (MT4jSettings.getInstance().isOpenGlMode() && this.getRenderer() instanceof AbstractMTApplication && ((AbstractMTApplication) this.getRenderer()).isGL11PlusAvailable()){
			styleInfo.setLineStipple(stipplePattern);
		}else{
			System.err.println("Cant set line stipple pattern if not using the OpenGL renderer. " + (this));
		}
	}
	
	
	/**
	 * Gets the line stipple.
	 * 
	 * @return the line stipple
	 */
	public short getLineStipple() {
		return styleInfo.getLineStipple();
	}
	
	/**
	 * Sets the material used by opengl lightning.
	 * 
	 * @param material the new material
	 */
	public void setMaterial(GLMaterial material){
		this.styleInfo.setMaterial(material);
	}
	
	/**
	 * Gets the material.
	 * 
	 * @return the material
	 */
	public GLMaterial getMaterial(){
		return this.styleInfo.getMaterial();
	}
	
}
