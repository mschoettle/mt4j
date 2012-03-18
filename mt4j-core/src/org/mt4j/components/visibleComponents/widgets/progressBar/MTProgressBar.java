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
package org.mt4j.components.visibleComponents.widgets.progressBar;

//import javax.media.opengl.GL;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
//import processing.opengl.PGraphicsOpenGL;

/**
 * The Class MTProgressBar.
 * 
 * @author Christopher Ruff
 */
public class MTProgressBar extends MTRoundRectangle {
	
	/** The progress info. */
	private IprogressInfoProvider progressInfo;
	
	/** The font. */
	private PFont font;
	
	/** The draw action text. */
	private boolean drawActionText;
	
	/** The pa. */
	private PApplet pa;
	
	/** The outer bar. */
	private MTRectangle outerBar;
	
	/** The inner bar. */
	private MTRectangle innerBar;
//	private PGraphics3D pgraphics3D;
	
	/** The c. */
	private Vector3D c;
	
	/** The b. */
	private Vector3D b;
	
	/** The zero. */
	private Vector3D zero = new Vector3D(0,0,0);
	
	/** The target width. */
	float targetWidth;
	
	
	/** The open gl. */
	private boolean openGl;
	
//	/** The pgl. */
//	private PGraphicsOpenGL pgl;
	
	/** The upper left. */
	private Vector3D upperLeft;
	
	/** The lower right. */
	private Vector3D lowerRight;
	
	/** The upper left projected. */
	private Vector3D upperLeftProjected;
	
	/** The lower right projected. */
	private Vector3D lowerRightProjected;
	
	/**
	 * The Constructor.
	 * 
	 * @param pApplet the applet
	 * @param font the font
	 */
	public MTProgressBar(PApplet pApplet, PFont font) {
		this(pApplet, null, font);
	}

	/**
	 * The Constructor.
	 * 
	 * @param pApplet the applet
	 * @param progressInfo the progress info
	 * @param font the font
	 */
	public MTProgressBar(PApplet pApplet, IprogressInfoProvider progressInfo, PFont font) {
		super(pApplet,0,0, 0, MT4jSettings.getInstance().getWindowWidth()/(3.5f), 100, 15, 15);
		
		this.progressInfo = progressInfo;
		this.font = font;
		this.pa = pApplet;
		this.openGl = MT4jSettings.getInstance().isOpenGlMode();
//		if (openGl){
//			this.pgl = ((PGraphicsOpenGL)pa.g);
//		}
		
		this.setStrokeColor(new MTColor(0, 0, 0, 200));
		this.setFillColor(new MTColor(200, 200, 210, 200));
		this.setNoStroke(false);
		this.setDrawSmooth(true);
		
		
		this.setGestureAllowance(DragProcessor.class, false);
		this.setGestureAllowance(RotateProcessor.class, false);
		this.setGestureAllowance(ScaleProcessor.class, false);
		this.setGestureAllowance(TapProcessor.class, false);
		
		if (font == null)
			this.setDrawActionText(false); 
		else
			this.setDrawActionText(true);
		
		b =  new Vector3D(this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)/2, this.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)/2, 0);
		
		outerBar = new MTRectangle(pa, 0, 0, 0, 200, 30);
		outerBar.setStrokeWeight(1);
		outerBar.setStrokeColor(new MTColor(0, 0, 0, 255));
		outerBar.setFillColor(new MTColor(100, 100, 100, 200));
		
		outerBar.setGestureAllowance(DragProcessor.class, false);
		outerBar.setGestureAllowance(RotateProcessor.class, false);
		outerBar.setGestureAllowance(ScaleProcessor.class, false);
		outerBar.setGestureAllowance(TapProcessor.class, false);
		
		c = new Vector3D(b.x -  outerBar.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)/2, b.y - outerBar.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)/2, 0);
		outerBar.translate(c);
		outerBar.setName("MTProgressbar outer bar");
		this.addChild(outerBar);
		
		innerBar = new MTRectangle(pa, 0, 0, 0, 199, 29);
		innerBar.setStrokeWeight(1);
		innerBar.setNoStroke(true);
		innerBar.setStrokeColor(new MTColor(255, 255, 255, 200));
		innerBar.setFillColor(new MTColor(250, 150, 150, 200));
		
		innerBar.setGestureAllowance(DragProcessor.class, false);
		innerBar.setGestureAllowance(RotateProcessor.class, false);
		innerBar.setGestureAllowance(ScaleProcessor.class, false);
		
		//Progress bar colors
		innerBar.getGeometryInfo().getVertices()[0].setRGBA(50, 50, 100, 200);
		
		innerBar.getGeometryInfo().getVertices()[1].setRGBA(50, 50, 250, 200);
		innerBar.getGeometryInfo().getVertices()[2].setRGBA(50, 50, 250, 200);
		
		innerBar.getGeometryInfo().getVertices()[3].setRGBA(50, 50, 100, 200);
		innerBar.getGeometryInfo().getVertices()[4].setRGBA(50, 50, 100, 200);
		
		innerBar.getGeometryInfo().updateVerticesColorBuffer();
		innerBar.translate(new Vector3D(0.5f, 0.5f, 0));
		innerBar.setName("MTProgressbar inner bar");
		outerBar.addChild(innerBar);
		
		targetWidth = innerBar.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
		
		this.innerBar.scale(1/targetWidth, 1, 1, zero, TransformSpace.LOCAL);
		this.innerBar.scale(1, 1, 1, zero, TransformSpace.LOCAL);
		
		this.translateGlobal(new Vector3D(
				MT4jSettings.getInstance().getWindowWidth()/2 - this.getWidthXY(TransformSpace.GLOBAL)/2, 
				MT4jSettings.getInstance().getWindowHeight()/2  - this.getHeightXY(TransformSpace.GLOBAL)/2 , 
				0) );
		
		
		this.upperLeft = new Vector3D(0,0,0);
		this.lowerRight = new Vector3D(upperLeft.x + this.getWidthXY(TransformSpace.LOCAL), upperLeft.y + this.getHeightXY(TransformSpace.LOCAL), 0);
		
		this.upperLeftProjected		= new Vector3D(0,0,0);
		this.lowerRightProjected 	= new Vector3D(0,0,0);
		
		this.setName("MTProgressbar");
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting with its children
		this.setDepthBufferDisabled(true);
	}
	


	@Override
	public void drawComponent(PGraphics g) {
		if (progressInfo == null){
			return;
		}
		
		if (progressInfo.isFinished()){
			this.setVisible(false); 
		}
		
		//gl.scissor so text gezts clipped
		GL10 gl = null;
		if (openGl){
			/*
			gl= GraphicsUtil.beginGL();
			
			gl.glEnable(GL10.GL_SCISSOR_TEST);
			
			//Project upper Left corner
			upperLeftProjected = Tools3D.projectGL(gl, pgl.glu, upperLeft, upperLeftProjected);
			int scissorStartX = (int) upperLeftProjected.x -0;
			int scissorStartY = (int) upperLeftProjected.y -1;
			
			//Project lower right corner
			lowerRightProjected =  Tools3D.projectGL(gl, pgl.glu, lowerRight, lowerRightProjected);
			int scissorWidth =  (int)(lowerRightProjected.x - scissorStartX + 1);
			int scissorHeight = (int)(lowerRightProjected.y - scissorStartY + 1);
			
			//Convert scissor start y to be upper left screen origin (0,0) relative
			scissorStartY = MT4jSettings.getInstance().getWindowHeight() - scissorStartY - scissorHeight;
			
			gl.glScissor(scissorStartX, scissorStartY, scissorWidth, scissorHeight);
			
//			pgl.endGL();
			GraphicsUtil.endGL();
			*/
		}
		
		//Draw component
		super.drawComponent(g);
		
		//Draw text
		if (this.isDrawActionText()){
			pa.textMode(PApplet.MODEL);
			pa.textAlign(PApplet.LEFT);
			pa.textFont(font, 12);
			pa.fill(20);
			pa.noStroke();
			
			pa.text(this.getProgressInfoProvider().getCurrentAction(), /*b.x*/c.x, b.y  - 20 );
		}
		
		
		float barDrawLength = (this.targetWidth/100) * this.getProgressInfoProvider().getPercentageFinished();
		float currentWidth = this.innerBar.getWidthXY(TransformSpace.GLOBAL);
		if (currentWidth != 0){
			if (barDrawLength == 0)
				barDrawLength = 1;
			
			this.innerBar.scale(1/currentWidth, 1, 1, zero, TransformSpace.LOCAL);
			this.innerBar.scale(barDrawLength, 1, 1, zero, TransformSpace.LOCAL);
		}
		
		if (this.isDrawActionText()){
			float fillText = 0;
				
			pa.fill(fillText);
			pa.textFont(font, 10);
			
			pa.text((int)this.getProgressInfoProvider().getPercentageFinished() + "%", this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)/2, this.getHeightXY(TransformSpace.RELATIVE_TO_PARENT) /2  + font.ascent()+ font.descent()  );
		}
		
		if (openGl){
			/*
			gl.glDisable(GL10.GL_SCISSOR_TEST);
			*/
		}
	}
	
	
	
	/**
	 * Sets the progress info provider.
	 * 
	 * @param provider the new progress info provider
	 */
	public void setProgressInfoProvider(IprogressInfoProvider provider){
		this.progressInfo = provider;
	}
	
	/**
	 * Gets the progress info provider.
	 * 
	 * @return the progress info provider
	 */
	public IprogressInfoProvider getProgressInfoProvider() {
		return progressInfo;
	}
	
	/**
	 * Gets the font.
	 * 
	 * @return the font
	 */
	public PFont getFont() {
		return font;
	}

	/**
	 * Sets the font.
	 * 
	 * @param font the new font
	 */
	public void setFont(PFont font) {
		this.font = font;
	}

	
	/**
	 * Checks if is draw action text.
	 * 
	 * @return true, if is draw action text
	 */
	public boolean isDrawActionText() {
		return drawActionText;
	}

	/**
	 * Sets the draw action text.
	 * 
	 * @param drawActionText the new draw action text
	 */
	public void setDrawActionText(boolean drawActionText) {
		this.drawActionText = drawActionText;
	}

}
