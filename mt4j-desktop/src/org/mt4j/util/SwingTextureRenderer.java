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

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.mt4j.AbstractMTApplication;
import org.mt4j.util.opengl.GLTextureSettings;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.EXPANSION_FILTER;
import org.mt4j.util.opengl.GLTexture.SHRINKAGE_FILTER;
import org.mt4j.util.opengl.GLTexture.WRAP_MODE;

import processing.core.PImage;

/**
 * This class can be used to render java swing JComponents to an
 * OpenGL texture. So this can at the moment only be used in OpenGL.
 * <br>After instantiating this class the component will be rendered
 * by a call to <code>scheduleRefresh()</code>.
 * The OpenGL texture can then be retrieved by the
 * <code>getTextureToRenderTo()</code> method.
 * <p>Note:
 * Order of adding components is the order in which they appear in z-order
 * -> add ontop comps first
 * <p>Note:
 * The JComponents can only be displayed if setBounds or setSize is called with positive values
 * (probably only neccessary when using no layoutManager).
 * 
 * @author Christopher Ruff
 */
public class SwingTextureRenderer {
	
	/** The comp to draw. */
	private Component compToDraw;
	
	/** The mt app. */
	private AbstractMTApplication mtApp;

	/** The texture to render to. */
	private GLTexture textureToRenderTo;

	/** The temp image. */
	private PImage tempImage;
	
	/** The tmp bounds rect. */
	private Rectangle tmpBoundsRect;

	/** The t. */
	private Timer t;
	
	private List<ActionListener> paintedListeners;


	//TODO enable non OpenGL version
	/**
	 * The Constructor.
	 * 
	 * @param mtApp the mt app
	 * @param compToRender the comp to render into texture
	 */
	public SwingTextureRenderer(AbstractMTApplication mtApp, Component compToRender) {
		super();
		this.compToDraw = compToRender;
		this.mtApp = mtApp;
		
		paintedListeners = new ArrayList<ActionListener>();
		
		this.tmpBoundsRect = new Rectangle(); 
		
		//Add component to PApplet
		//make comptoDraw invisible to not actually show it on screen
		compToDraw.setVisible(false);
		mtApp.add(compToDraw);

//		MouseEvent me = new MouseEvent(
//				compToDraw, //Source == dispatch comp?
//				MouseEvent.MOUSE_PRESSED,
//				System.currentTimeMillis(), 
//				MouseEvent.BUTTON1_MASK, 
//				10, 10, 
//				1, 
//				false);
//		compToDraw.dispatchEvent(me);
		
	//Create the texture
////    notVisibleImage = new PImage(rectC.width, rectC.height);
//      GLTextureParameters tp = new GLTextureParameters();
//      tp.minFilter = GLConstants.LINEAR; //To avoid mipmapgeneration at each update
////      tp.minFilter = GLConstants.LINEAR_MIPMAP_LINEAR; //For mip maps
//      tp.magFilter = GLConstants.LINEAR;
//      
      final Rectangle rectC = SwingUtilities.getLocalBounds(compToDraw);
//      textureToRenderTo = new GLTexture(mtApp, rectC.width, rectC.height, tp, false);
//
//      //TODO check if we are in the rendering thread or not
//      if (!mtApp.isRenderThreadCurrent()){
//    	  mtApp.invokeLater(new Runnable() {
//        	  public void run() {
//        		  textureToRenderTo.initTexture(rectC.width, rectC.height);
//        	  }
//          });
//      }
      
      GLTextureSettings ts = new GLTextureSettings();
      ts.shrinkFilter = SHRINKAGE_FILTER.BilinearNoMipMaps;
//      ts.shrinkFilter = SHRINKAGE_FILTER.Trilinear; //For better quality
      ts.expansionFilter = EXPANSION_FILTER.Bilinear;
      ts.wrappingHorizontal = WRAP_MODE.CLAMP_TO_EDGE;
      ts.wrappingVertical = WRAP_MODE.CLAMP_TO_EDGE;
     
      textureToRenderTo = new GLTexture(mtApp, ts);
//      textureToRenderTo = new MTTexture(mtApp, rectC.width, rectC.height, ts); //This would also init the gl texture 
      textureToRenderTo.width = rectC.width; //So that tex coords of shape get scaled correctly 
      textureToRenderTo.height = rectC.height;
      
      if (!mtApp.isRenderThreadCurrent()){
    	  mtApp.invokeLater(new Runnable() {
        	  public void run() {
//        		  textureToRenderTo.initTexture(rectC.width, rectC.height);
        		  textureToRenderTo.setupGLTexture(rectC.width, rectC.height);
        	  }
          });
      }else{
//    	  textureToRenderTo.initTexture(rectC.width, rectC.height);
    	  textureToRenderTo.setupGLTexture(rectC.width, rectC.height);
      }
     
//    this.scheduleRefresh(); //ENABLE?
	}
	
	
	/**
	 * Gets the texture to render to.
	 * 
	 * @return the texture to render to
	 */
	public GLTexture getTextureToRenderTo(){
		return this.textureToRenderTo;
	}
	
	/**
	 * Start timed refresh.
	 * 
	 * @param time the time
	 */
	public void startTimedRefresh(int time){
		t = new Timer(time, new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent arg0) {
				scheduleRefresh();
			}
		});
		t.start();
	}
	
	/**
	 * Stop timer.
	 */
	public void stopTimedRefresh(){
		if (t!=null){
			t.stop();
		}
	}
	
	
	/**
	 * This schedules a new pass of rendering the JComponent into a texture.
	 * Use this to render the component into the texture or to update the texture if the
	 * JComponent changed.
	 */
	public void scheduleRefresh(){
//		try {
//			SwingUtilities.invokeAndWait(new Runnable(){
		SwingUtilities.invokeLater(new Runnable(){
				//@Override
				public void run() {
					Rectangle boundingRect = SwingUtilities.getLocalBounds(compToDraw);
					//System.out.println("BoundingRect w:" + boundingRect.width + " h:" + boundingRect.height);
					//FIXME as the 2nd parameter container, really use the parent?
					paintComponentOffscreen(compToDraw, compToDraw.getParent(), boundingRect);
					
					firePaintOccurred();
				}
     });
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
	}
	
	
	private BufferedImage img;
	private Graphics2D g; //TODO dispose by hand!
	CellRendererPane crp;
	
	/**
	 * Paint not visible component.
	 * 
	 * @param c the c
	 * @param con the con
	 * @param rect the rect
	 * 
	 * @return the buffered image
	 */
	private BufferedImage paintComponentOffscreen(Component c, Container con, Rectangle rect) {
		//FIXME n�tig? paintComponent added anscheinend c nochmal zu con!?
		con.remove(c);
		/*
		BufferedImage img = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);  
		Graphics2D g = img.createGraphics(); 
		*/
		//Lazily initialize
		if (img == null){
			img = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
		}
		if (g == null){
			g = img.createGraphics(); 
		}
		
		if (c instanceof JComponent) {
			JComponent jComp = (JComponent) c;
			jComp.setOpaque(false);
		}
		/*
		if (!c.isLightweight()){
			System.err.println("Component to paint (" + c.getName() + ") is not lightweight -> undeterministic behaviour!");
		}
		*/
		
//		SwingUtilities.paintComponent(g, c, con, rect);  
		//The following does what SwingUtilities.paintComponent does
		if (crp == null){
			crp = new CellRendererPane();
		}
		crp.add(c);
		con.add(crp);
		crp.paintComponent(g, c, con, rect.x, rect.y, rect.width, rect.height, false);
		//FIXME TEST, BENEFITS?
		con.remove(crp);
		crp.remove(c);
		con.add(c);
		
		//Dispose graphics context
//		g.dispose();  //reuse context!
		
		//Bug? c's bounds get changed by the paintComponent method!?
		//This is a hack that seems to fix it
		c.getBounds(tmpBoundsRect);
		c.setBounds(tmpBoundsRect.width, tmpBoundsRect.height, Math.abs(tmpBoundsRect.x), Math.abs(tmpBoundsRect.y));
		
		//TODO evtl direkt in Intbuffer schreiben ohne PImage
//		this.tempImage = new PImage(img);
		
//		/*
		//Put buffered image into PImage pixels
		 BufferedImage bi = img;
	      int width = bi.getWidth();
	      int height = bi.getHeight();
	      if (tempImage == null){
	    	  tempImage = new PImage(width, height);
	    	  tempImage.loadPixels();
	      }
	      WritableRaster raster = bi.getRaster();
	      raster.getDataElements(0, 0, width, height, tempImage.pixels);
//		*/

		//Refresh GL texture next draw
		mtApp.invokeLater(new Runnable() {
			//@Override
			public void run() {
//				textureToRenderTo.putPixelsIntoTexture(tempImage); //OpenGL
//				textureToRenderTo.putImage(tempImage); //SLOWER but also fills the PImage pixels[] with the image
				//firePaintOccurred();
				textureToRenderTo.loadGLTexture(tempImage); //OpenGL
				
			}
		});
		return img;  
	}  

	
	private void firePaintOccurred(){
		for (Iterator<ActionListener> iterator = paintedListeners.iterator(); iterator.hasNext();) {
			ActionListener a = iterator.next();
			a.actionPerformed(new ActionEvent(this, 0,""));
		}
	}
	
	public void addPaintOcurredListener(ActionListener a){
		this.paintedListeners.add(a);
	}
	
	public void removePaintOcurredListener(ActionListener a){
		if (this.paintedListeners.contains(a)){
			this.paintedListeners.remove(a);
		}
	}
	
	

	/* 
    //Swing render usage example:
    // Render swing component to texture \\
     
    JPanel panel1 = new JPanel(true);
    panel1.setVisible(false);
    panel1.setBounds(0,0,200,200);
    
	//Button
	b = new B();
	b.setBounds(0,0,100,100);
	b.setText("Hello");
    
    JTextArea t = new JTextArea("text!");
    t.setBounds(100,100,50,50);
    
    JPanel innerP = new JPanel();
    innerP.setBackground(new Color(120));
    innerP.setBounds(0,0,150,150);
    
    JButton otherButton = new JButton("lorem ipsum");
    otherButton.setBounds(0,0, 120,120);
    innerP.add(otherButton);
    
    panel1.add(b);
    panel1.add(innerP);
    panel1.add(t);
    
    //Simulate mouse down on the button
    b.dispatchEvent(new MouseEvent(b, MouseEvent.MOUSE_PRESSED, 0, MouseEvent.BUTTON1_MASK, 10,10, 40,40, 1, false, MouseEvent.BUTTON1));
    
    swingTex = new SwingTextureRenderer(pa, panel1);
    
    MTRectangle rectangle = new MTRectangle(300,300,0, 200,200, pa);
    rectangle.setTexture(swingTex.getTextureToRenderTo());
    this.getCanvas().addChild(rectangle);
    */
	
}
