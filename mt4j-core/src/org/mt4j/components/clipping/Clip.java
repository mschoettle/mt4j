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


import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GLStencilUtil;

import processing.core.PApplet;
import processing.core.PGraphics;


/**
 * This can be used to restrict drawing of a component, or a components's children
 * to only the area of the specified clipping shape/area.
 * <br><strong>NOTE:</strong> Only supported by the OpenGL renderer!
 * 
 * @author Christopher Ruff
 * @since 0.81
 * @see org.mt4j.components.MTComponent#setClip
 */
public class Clip {
	
	/** The clip shape. */
	private AbstractVisibleComponent clipShape;
	
	/** The gl. */
	private GL10 gl;
	
	/**
	 * Instantiates a new clip.
	 * The specified clipping values are assumed to be in the coordinate system
	 * as the clipped component.
	 * <br>NOTE: CLIPPING IS ONLY AVAILABLE USING THE OPENGL RENDERER!
	 * 
	 * @param pApplet the applet
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public Clip(PApplet pApplet, float x, float y, float width, float height) {
		MTRectangle clipRect = new MTRectangle(pApplet, x, y, width, height);
		clipRect.setNoStroke(true);
		if (clipRect.getBounds() == null ){
			clipRect.setBounds(new BoundsZPlaneRectangle(clipRect));
		}else{
			if (!(clipRect.getBounds() instanceof BoundsZPlaneRectangle)){
				clipRect.setBounds(new BoundsZPlaneRectangle(clipRect));
			}
		}
		clipRect.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);	
		this.clipShape = clipRect;
		
		if (MT4jSettings.getInstance().isOpenGlMode()){
//			this.gl = Tools3D.getGL(pApplet);	
			this.gl = PlatformUtil.getGL();
		}
	}
	
	/**
	 * Instantiates a new clip.
	 * The specified clipping shape is assumed to be in the same 
	 * coordinate system as the clipped component.
	 * <br>NOTE: CLIPPING IS ONLY AVAILABLE USING THE OPENGL RENDERER!
	 * 
	 * @param clipShape the clip shape
	 */
	public Clip(AbstractVisibleComponent clipShape) {
//		this(Tools3D.getGL(clipShape.getRenderer()), clipShape);
		this(PlatformUtil.getGL(), clipShape);
	}
	
	
	/**
	 * Instantiates a new clip.
	 * The specified clipping shape is assumed to be in the same 
	 * coordinate system as the clipped component.
	 * <br>NOTE: CLIPPING IS ONLY AVAILABLE USING THE OPENGL RENDERER!
	 * 
	 * @param gl the gl
	 * @param clipShape the clip shape
	 */
	public Clip(GL10 gl, AbstractVisibleComponent clipShape) {
//		if (MT4jSettings.getInstance().isOpenGlMode()){
////			this.gl = Tools3D.getGL(clipShape.getRenderer());
////			this.gl = GraphicsUtil.getGL();
//		}
		this.gl = gl;
		this.clipShape = clipShape;
	}
	
	/**
	 * Enable clipping with the clipping shape.
	 * 
	 * @param g the g
	 */
	public void enableClip(PGraphics g){
		GLStencilUtil.getInstance().beginDrawClipShape(gl);
		this.clipShape.drawComponent(g);
		GLStencilUtil.getInstance().beginDrawClipped(gl);
	}
	
	/**
	 * Disable clipping with the clipping shape.
	 * 
	 * @param g the g
	 */
	public void disableClip(PGraphics g){
		GLStencilUtil.getInstance().endClipping(g, gl, clipShape);
	}
	
	/**
	 * Gets the clip shape.
	 * 
	 * @return the clip shape
	 */
	public AbstractVisibleComponent getClipShape() {
		return this.clipShape;
	}
	
	/**
	 * Gets the clip shape intersection local.
	 * 
	 * @param ray the ray
	 * 
	 * @return the clip shape intersection local
	 */
	public Vector3D getClipShapeIntersectionLocal(Ray ray){
		return clipShape.getIntersectionLocal(ray);
	}

	
}
