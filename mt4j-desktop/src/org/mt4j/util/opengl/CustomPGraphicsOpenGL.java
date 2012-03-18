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
package org.mt4j.util.opengl;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;

import org.mt4j.util.MT4jSettings;

import processing.opengl.PGraphicsOpenGL;

//import processing.opengl.PGraphicsOpenGL;

/**
 * The Class CustomPGraphicsOpenGL. An extension to processing's opengl renderer.
 *
 * @author Christopher Ruff
 */
public class CustomPGraphicsOpenGL 
extends PGraphicsOpenGL 
{
	//Overridden to allow stencil buffer use and custom multisampling
	/* (non-Javadoc)
	 * @see processing.opengl.PGraphicsOpenGL#allocate()
	 */
	@Override
	protected void allocate() {
		if (context == null) {
//	      System.out.println("PGraphicsOpenGL.allocate() for " + width + " " + height);
//	      new Exception().printStackTrace(System.out);
	      // If OpenGL 2X or 4X smoothing is enabled, setup caps object for them
	      GLCapabilities capabilities = new GLCapabilities();
	      // Starting in release 0158, OpenGL smoothing is always enabled
	      /*//      
	      if (!hints[DISABLE_OPENGL_2X_SMOOTH]) {
	        capabilities.setSampleBuffers(true);
	        capabilities.setNumSamples(2);
	      } else if (hints[ENABLE_OPENGL_4X_SMOOTH]) {
	        capabilities.setSampleBuffers(true);
	        capabilities.setNumSamples(4);
	      }
	      */
	      
	      //FIXME ADDED
	      if (MT4jSettings.getInstance().isMultiSampling()){
	    	  capabilities.setSampleBuffers(true);
	    	  capabilities.setNumSamples(MT4jSettings.getInstance().getNumSamples());
//	  	  capabilities.setNumSamples(4);
	      }

	      //We need a stencil buffer!
	      capabilities.setStencilBits(8);
	      
//	      capabilities.setDepthBits(4);
//	      capabilities.setDepthBits(32);

	      // get a rendering surface and a context for this canvas
	      GLDrawableFactory factory = GLDrawableFactory.getFactory();

	      /*
	      if (PApplet.platform == PConstants.LINUX) {
	        GraphicsConfiguration pconfig = parent.getGraphicsConfiguration();
	        System.out.println("parent config is " + pconfig);

	        //      GraphicsDevice device = config.getDevice();
	        //AbstractGraphicsDevice agd = new AbstractGraphicsDevice(device);
	        //AbstractGraphicsConfiguration agc = factory.chooseGraphicsConfiguration(capabilities, null, null);

	        AWTGraphicsConfiguration agc = (AWTGraphicsConfiguration)
	        factory.chooseGraphicsConfiguration(capabilities, null, null);
	        GraphicsConfiguration config = agc.getGraphicsConfiguration();
	        System.out.println("agc config is " + config);
	      }
	      */

	      drawable = factory.getGLDrawable(parent, capabilities, null);
	      context = drawable.createContext(null);

	      // need to get proper opengl context since will be needed below
	      gl = context.getGL();
	      // Flag defaults to be reset on the next trip into beginDraw().
	      settingsInited = false;

	    } else {
	    	//FIXME REMOVE THIS? can cause a "cant destroy context if its not current" error if PApplet is embedded into a swing application which causes a resize of the PApplet
	    	//this code recreates the opengl context with a new size if the size of the PApplet should change
	    	//so if we comment this out and resize the PApplet the Opengl context gets distorted...
	    	
	      // The following three lines are a fix for Bug #1176
	      // http://dev.processing.org/bugs/show_bug.cgi?id=1176
	    	/*
	      context.destroy();
	      context = drawable.createContext(null);
	      gl = context.getGL();
	      reapplySettings();
	      */
	    }
	}
	
}
