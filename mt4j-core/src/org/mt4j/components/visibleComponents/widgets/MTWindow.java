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
package org.mt4j.components.visibleComponents.widgets;

import org.mt4j.components.clipping.Clip;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * The Class MTWindow. A round rectangle class that clips its 
 * children to the bounds of this window. If it is resized with the scaling
 * gesture, the window content isnt scaled.
 * 
 * @author Christopher Ruff
 */
public class MTWindow extends MTRoundRectangle {
	
	/** The clip. */
	private Clip clip;
	
	/** The draw inner border. */
	private boolean drawInnerBorder;
	
	/** The saved no stroke setting. */
	private boolean savedNoStrokeSetting;
	
	
	//TODO in abstractviscomp code von hier nehmen, clipshape bounds drüber zeichnen?
	//TODO add titlebar, maximize, close buttons
	//TODO scale border so its width doesent change..
	
	
	/**
	 * Instantiates a new mT window.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 * @param arcWidth the arc width
	 * @param arcHeight the arc height
	 * @param applet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTWindow(float x, float y, float z, float width, float height,
			float arcWidth, float arcHeight, PApplet applet) {
		this(applet, x, y, z, width, height, arcWidth, arcHeight);
	}

	/**
	 * Instantiates a new mT window.
	 * @param applet the applet
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 * @param arcWidth the arc width
	 * @param arcHeight the arc height
	 */
	public MTWindow(PApplet applet, float x, float y, float z, float width,
			float height, float arcWidth, float arcHeight) {
		super(applet, x, y, z, width, height, arcWidth, arcHeight);

		this.setName("unnamed MTWindow");
		
		if (!MT4jSettings.getInstance().isOpenGlMode()){
			System.err.println("MTWindow isnt fully supported if not using OpenGL renderer!");
			return;
		}
		
		//Create inner children clip shape
		float border = 10;
//		GL gl = ((PGraphicsOpenGL)applet.g).gl;
		GL10 gl = PlatformUtil.getGL();
//		MTRoundRectangle clipRect =  new MTRoundRectangle(x+border, y+border, z, width-(2*border), height-(2*border), arcWidth, arcHeight, applet);
		MTRectangle clipRect =  new MTRectangle(applet, x+border, y+border, z, width-(2*border), height-(2*border));
		clipRect.setDrawSmooth(true);
		clipRect.setNoStroke(true);
		clipRect.setBoundsBehaviour(MTRectangle.BOUNDS_ONLY_CHECK);
		this.clip = new Clip(gl, clipRect);
		this.setChildClip(this.clip);
		this.drawInnerBorder = true;
		
		//Add window background
		final MTRectangle windowBackGround = new MTRectangle(applet, x, y, z, 100, 200);
		windowBackGround.setFillColor(new MTColor(200,200,200,255));
		windowBackGround.setNoStroke(true);
		windowBackGround.setPickable(false);
		this.addChild(windowBackGround);
		
		this.removeAllGestureEventListeners(ScaleProcessor.class);
//		cr.removeAllGestureEventListeners(RotationDetector.class);
		this.addGestureListener(ScaleProcessor.class, new IGestureEventListener(){
			//@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				ScaleEvent se = (ScaleEvent)ge;
				
				//Scale window background normally
				windowBackGround.scaleGlobal(se.getScaleFactorX(), se.getScaleFactorY(), se.getScaleFactorZ(), se.getScalingPoint());
				
				//Scale vertices of the window
				AbstractShape target = (AbstractShape)ge.getTarget();
				Vertex[] verts = target.getGeometryInfo().getVertices();
				Vector3D newScalingPoint = target.globalToLocal(se.getScalingPoint());
				Matrix m = Matrix.getScalingMatrix(newScalingPoint, se.getScaleFactorX(), se.getScaleFactorY(), se.getScaleFactorZ());
				Vertex.transFormArray(m, verts);
				target.setVertices(verts);

				//Scale vertices of the clip shape
				AbstractShape clip = (AbstractShape)target.getChildClip().getClipShape();
				Vertex[] clipVerts = clip.getGeometryInfo().getVertices();
				Vertex.transFormArray(m, clipVerts);
				clip.setVertices(clipVerts);
				return false;
			}
		});
//		*/
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting //FIXME but we cant use 3D stuff in there then..
		this.setDepthBufferDisabled(true);
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.AbstractVisibleComponent#preDraw(processing.core.PGraphics)
	 */
	@Override
	public void preDraw(PGraphics graphics) {
		this.savedNoStrokeSetting = this.isNoStroke();
		super.preDraw(graphics);
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.AbstractVisibleComponent#postDrawChildren(processing.core.PGraphics)
	 */
	@Override
	public void postDrawChildren(PGraphics g) {
		this.clip.disableClip(g);
		
		//Draw clipshape outline over all children to get an
		//antialiased border
		AbstractVisibleComponent clipShape = this.getChildClip().getClipShape();
//		if (!clipShape.isNoStroke()){
		if (this.drawInnerBorder){
			clipShape.setNoFill(true);
			clipShape.setNoStroke(false);
				clipShape.drawComponent(g);
			clipShape.setNoStroke(true);
			clipShape.setNoFill(false);
		}
		
		if (!savedNoStrokeSetting){
			boolean noFillSetting = this.isNoFill();
			this.setNoFill(true);
			this.setNoStroke(false);
			this.drawComponent(g);
			this.setNoFill(noFillSetting);
			this.setNoStroke(savedNoStrokeSetting);
		}
		
		this.setChildClip(null);
		super.postDrawChildren(g);
		this.setChildClip(clip);
	}



//	@Override
//	public void setStrokeColor(float r, float g, float b, float a) {
//		super.setStrokeColor(r, g, b, a);
//		this.clip.getClipShape().setStrokeColor(r, g, b, a);
//	}
	
	/* (non-Javadoc)
 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setStrokeColor(org.mt4j.util.MTColor)
 */
@Override
	public void setStrokeColor(MTColor strokeColor) {
		super.setStrokeColor(strokeColor);
		this.clip.getClipShape().setStrokeColor(strokeColor); //FIXME wtf? not needed!?
	}

	

}
