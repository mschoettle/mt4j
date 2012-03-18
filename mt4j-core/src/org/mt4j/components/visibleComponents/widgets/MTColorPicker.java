/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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

import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * The Class MTColorPicker.
 * A widget which displays a calculated or specified image that can be used to pick a color on.
 * The picked color can than be retrieved by calling <code>getSelectedColor()</code>.
 * 
 * @author Christopher Ruff
 */
public class MTColorPicker extends MTRectangle {
	  
  	/** The h. */
  	private int x, y, w, h/*, c*/;
	  
  	/** The cp image. */
  	private PImage cpImage;
	  
  	/** The app. */
  	private PApplet app;
	  
	  /** The current color. */
  	private MTColor currentColor;
	  
	  /** The selection rect. */
  	private MTRectangle selectionRect;
	  
	  /**
  	 * Instantiates a new mT color picker.
	 * @param applet the applet
	 * @param x the x
	 * @param y the y
	 * @param texture the texture
  	 */
  	public MTColorPicker(PApplet applet, int x, int y, PImage texture) {
		  super(applet, x, y, texture.width, texture.height);
		  
		  this.app = applet;
		  this.x = x;
		  this.y = y;
		  this.w = texture.width;
		  this.h = texture.height;
		  
		  this.cpImage = texture;
		  this.setTexture(texture);
		  
		  this.init();
	  }

	/**
	 * Instantiates a new mT color picker.
	 * @param applet the applet
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public MTColorPicker(PApplet applet, int x, int y, int width, int height) {
		super(applet, x, y, width, height);
		
		this.app = applet;
		this.x = x;
	    this.y = y;
	    this.w = width;
	    this.h = height;
//	    this.c = c;
			
	    this.cpImage = new PImage( w, h );
	    this.calcColors();
	    
	    this.setTexture(cpImage);
	    
	    this.init();
	}
	
	
	/**
	 * Inits the.
	 */
	private void init(){
	    this.currentColor = new MTColor(255,255,255,255);
//	    this.setNoFill(true);
	    
	    this.selectionRect = new MTRectangle(app,x, y,3, 3);
	    this.selectionRect.setStrokeColor(new MTColor(150,150,150,255));
	    this.selectionRect.setNoFill(true);
	    this.selectionRect.setPickable(false);
	    this.selectionRect.unregisterAllInputProcessors();
	    this.addChild(selectionRect);
	    
//	    this.registerInputProcessor(new DragProcessor(app));
	    this.removeAllGestureEventListeners(DragProcessor.class);
	    this.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				
				Vector3D hitPointLocal = getIntersectionLocal(globalToLocal(Tools3D.getCameraPickRay(app, MTColorPicker.this, de.getDragCursor().getCurrentEvtPosX(), de.getDragCursor().getCurrentEvtPosY())));
				if (hitPointLocal != null){
//					 int col = cpImage.get( (int)hitPointLocal.x, (int)hitPointLocal.y );
					int col = cpImage.get( (int)hitPointLocal.x - x, (int)hitPointLocal.y -y);
					 float r = app.red(col);
					 float g = app.green(col);
					 float b = app.blue(col);
					 currentColor.setR(r);
					 currentColor.setG(g);
					 currentColor.setB(b);
					 //System.out.println("New Color: " + currentColor);
					 selectionRect.setPositionRelativeToParent(new Vector3D(hitPointLocal));
				}
				return false;
			}
	    });
	    
	    /*
	    //FIXME REMOVE? now done with drag processor..
	    this.registerInputProcessor(new TapProcessor(app));
	    this.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				switch (te.getTapID()) {
				case TapEvent.BUTTON_DOWN:
					break;
				case TapEvent.BUTTON_UP:
					break;
				case TapEvent.BUTTON_CLICKED:
					Vector3D hitPointLocal = getIntersectionLocal(globalToLocal(Tools3D.getCameraPickRay(app, MTColorPicker.this, te.getLocationOnScreen().x, te.getLocationOnScreen().y)));
					if (hitPointLocal != null){
//						 int col = cpImage.get( (int)hitPointLocal.x, (int)hitPointLocal.y );
						int col = cpImage.get( (int)hitPointLocal.x - x, (int)hitPointLocal.y -y);
						 float r = app.red(col);
						 float g = app.green(col);
						 float b = app.blue(col);
						 currentColor.setR(r);
						 currentColor.setG(g);
						 currentColor.setB(b);
						 //System.out.println("New Color: " + currentColor);
						 selectionRect.setPositionRelativeToParent(new Vector3D(hitPointLocal));
						 
						 ////Check hitpoint by setting pixel
						 //int tmpCol = app.color(0, 0, 0, 255);
						 //cpImage.set((int)hitPointLocal.x, (int)hitPointLocal.y, tmpCol);
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		*/
	}
	
	
	 /**
 	 * Calc colors.
 	 */
 	private void calcColors (){
	    // draw color.
//	    int cw = w - 60;
		 int cw = w;
		 
	    for( int i=0; i<cw; i++ ) 
	    {
	      float nColorPercent = i / (float)cw;
	      float rad = (-360 * nColorPercent) * (PConstants.PI / 180);
	      int nR = (int)(PApplet.cos(rad) * 127 + 128) << 16;
	      int nG = (int)(PApplet.cos(rad + 2 * PApplet.PI / 3) * 127 + 128) << 8;
	      int nB = (int)(Math.cos(rad + 4 * PApplet.PI / 3) * 127 + 128);
	      int nColor = nR | nG | nB;
				
	      setGradient( i, 0, 1, h/2, 0xFFFFFF, nColor );
	      setGradient( i, (h/2), 1, h/2, nColor, 0x000000 );
	    }
			
	    /*
	    // draw black/white.
	    drawRect( cw, 0,   30, h/2, 0xFFFFFF );
	    drawRect( cw, h/2, 30, h/2, 0 );
			
	    // draw grey scale.
	    for( int j=0; j<h; j++ )
	    {
	      int g = 255 - (int)(j/(float)(h-1) * 255 );
	      drawRect( w-30, j, 30, 1, app.color( g, g, g ) );
	    }
	    */
	  }

	  /**
  	 * Sets the gradient.
  	 * 
  	 * @param x the x
  	 * @param y the y
  	 * @param w the w
  	 * @param h the h
  	 * @param c1 the c1
  	 * @param c2 the c2
  	 */
  	private void setGradient(int x, int y, float w, float h, int c1, int c2 )
	  {
	    float deltaR = app.red(c2) - app.red(c1);
	    float deltaG = app.green(c2) - app.green(c1);
	    float deltaB = app.blue(c2) - app.blue(c1);

	    for (int j = y; j<(y+h); j++)
	    {
	      int c = app.color( app.red(c1)+(j-y)*(deltaR/h), app.green(c1)+(j-y)*(deltaG/h), app.blue(c1)+(j-y)*(deltaB/h) );
	      cpImage.set( x, j, c );
	    }
	  }
		
	  /*
	  private void drawRect( int rx, int ry, int rw, int rh, int rc )
	  {
	    for(int i=rx; i<rx+rw; i++) 
	    {
	      for(int j=ry; j<ry+rh; j++) 
	      {
	        cpImage.set( i, j, rc );
	      }
	    }
	  }
	  */
	  
	  
//	  @Override
//	public void drawComponent(PGraphics g) {
//		
//		app.image( cpImage, x, y );
//		
//		super.drawComponent(g);
//		
//	}
	
	  
	  /**
  	 * Gets the selected color.
  	 * 
  	 * @return the selected color
  	 */
  	public MTColor getSelectedColor(){
		  return this.currentColor;
	  }

}
