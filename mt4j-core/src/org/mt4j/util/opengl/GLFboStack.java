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

import java.util.Stack;

import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;

/**
 * The FBO stack manages the current opengl drawing target. It allows to switch drawing to/from different
 * frame buffer objects.
 * Usage:<br>
 * <br>GLFboStack.getInstance(gl).pushFBO(); //saves the current render target/frame buffer 
 * <br>GLFboStack.getInstance(gl).useFBO(glFBO); //changes to another frame buffer
 * <br>GLFboStack.getInstance(gl).popFBO() //switches back to the previously saved frame buffer
 * @author Christopher Ruff
 */
public class GLFboStack{
	/** The Constant logger. */
	private static final ILogger logger = MTLoggerFactory.getLogger(GLFboStack.class.getName());
	static{
		logger.setLevel(ILogger.ERROR);
	}
	
	/** The gl. */
	public GL20 gl;
	
	/** The current fbo. */
	protected int currentFBO;
	
	/** The fbo name stack. */
	protected Stack<Integer> fboNameStack;
	
	/** The instance. */
	private static GLFboStack instance = null;

	/**
	 * Instantiates a new gL fbo stack.
	 * @param gl the gl
	 */
	private GLFboStack(GL20 gl){
		this.gl = gl;
		fboNameStack = new Stack<Integer>();
		currentFBO = 0;
	}
	
	/**
	 * Gets the single instance of GLFboStack.
	 *
	 * @return single instance of GLFboStack
	 */
	public static GLFboStack getInstance(GL20 gl){
		if (instance == null){
//			instance = new GLFboStack(GLU.getCurrentGL());
			instance = new GLFboStack(gl);
			return instance;
		}else{
			return instance;
		}
	}

	/**
	 * Pushes the currently used render target ID on the stack.
	 */
	public void pushFBO(){
		fboNameStack.push(currentFBO);
	}

	/**
	 * Binds the specified render target ID and sets it as current.
	 * 
	 * @param fbo the fbo
	 */
	public void useFBO(int fbo){
		currentFBO = fbo;
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, currentFBO);
	}

	/**
	 * Binds the specified frame buffer object and sets it as current.
	 * 
	 * @param fbo the fbo
	 */
	public void useFBO(GLFBO fbo){
		currentFBO = fbo.getName();
		fbo.bind();
	}
	
	/**
	 * Peek fbo.
	 *
	 * @return the int
	 */
	public int peekFBO(){
		if (fboNameStack.isEmpty()){
			return 0;
		}else{
//			return fboNameStack.peek();
			return currentFBO;
		}
	}

	//NOTE THIS UNBINDS A CURRENT FBO IF SET! -> no need for calling unbind()!
	/**
	 * Pops the fbo.
	 * This switches back (binds) to the formely pushed fbo. 
	 * <br>NOTE: THIS UNBINDS A CURRENT FBO IF SET! -> no need for calling unbind()!
	 */
	public void popFBO(){
		if (fboNameStack.isEmpty()){
			logger.error("Trying to pop() from an empty framebuffer stack!"); //TODO -> just bind 0 !?
		}else{
			currentFBO = fboNameStack.pop();
			gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, currentFBO);
		}
	}
	
}
