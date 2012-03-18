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
package org.mt4j.components.clipping;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GLStencilUtil;

import processing.core.PGraphics;


/**
 * The Class FillPaint. If added to a component, the specified fillpaint will be 
 * drawn where the original component would have been drawn.
 * <br><strong>NOTE:</strong> A fillpaint can only be used for a single component.
 * <br><strong>NOTE:</strong> This is only supported by the OpenGL renderer!
 * @author Christopher Ruff
 */
public class FillPaint { 
	/** The gradient shape. */
	protected MTComponent fillPaint;
	
	private GL10 gl;

	private AbstractVisibleComponent clipShape;
	
	/**
	 * Instantiates a new fill paint. The specified fillpaint will be 
	 * drawn where the corresponding component would have been drawn.
	 * 
	 * @param gl the gl
	 * @param fillPaint the fill paint
	 */
	public FillPaint(GL10 gl, AbstractVisibleComponent fillPaint) {
		//super(gl);
		this.gl = gl;
		this.fillPaint = fillPaint;
//		this.clipShape = shape;
	}

//	public FillPaint(GL gl, AbstractVisibleComponent shape, MTComponent fillPaint) {
//		//super(gl);
//		this.gl = gl;
//		this.fillPaint = fillPaint;
//		this.clipShape = shape;
//	}
	

	/**
	 * Pre.
	 * 
	 * @param g the g
	 */
	public void pre(PGraphics g) {
		GLStencilUtil.getInstance().beginDrawClipShape(gl);
	}

	
	/**
	 * Post.
	 * 
	 * @param g the g
	 */
	public void post(PGraphics g) {
		GLStencilUtil.getInstance().beginDrawClipped(gl);
		drawFillPaint(g);
		GLStencilUtil.getInstance().endClipping(g, gl, clipShape);
	}
	
	/**
	 * Draws the fill paint.
	 * 
	 * @param g the g
	 */
	protected void drawFillPaint(PGraphics g){
		//Draw the fill paint clipped to the area of the original shape
		fillPaint.drawComponent(g); 
	}
	
	/**
	 * Sets the shape to be fill painted. Usually this is called automatically
	 * when the paint is added to a shape. So this method should not be invoked directly.
	 * 
	 * @param shape the new shape
	 */
	public void setShape(AbstractVisibleComponent shape){
		this.clipShape = shape;
	}
	
	/**
	 * Gets the fill painted shape.
	 * 
	 * @return the shape
	 */
	public AbstractVisibleComponent getShape(){
		return this.clipShape;
	}
	
	
}
