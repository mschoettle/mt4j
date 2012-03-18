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
package org.mt4j.util.opengl;

import java.util.WeakHashMap;


//import java.util.WeakHashMap;

//FIXME this is class is not used at the moment - hard to track all states if processing also messes with it

//import javax.media.opengl.GL;


public class GLStateHolder {
	
//	    /** The state. */
//    	IntHashtable state;
//    	//	    GLFunc gl;
//	    /** The version. */
//    	String version;
//	    
//    	/** The saved count. */
//    	int savedCount;
//	    
//    	/** The done count. */
//    	int doneCount;
//	    
//    	/** The tmp value. */
//    	static int[] tmpValue = { 0 };
//	    
//    	/** The gl2state. */
//    	static WeakHashMap gl2state = new WeakHashMap();
//	    
//    	/** The last a. */
//    	static byte lastR, lastG, lastB, lastA;
//	    
//    	/** The Constant DEBUG. */
//    	static final boolean DEBUG = false;
//	    
//	    /** The gl. */
//    	GL20 gl;
//	    
//	    
//	    /**
//    	 * Get/Create a <code>GLState</code> from a GL context.
//    	 * 
//    	 * @param gl the GL context
//    	 * 
//    	 * @return the associated GLState
//    	 */
//	    public static GLStateHolder getInstance(GL gl) {
//	    	GLStateHolder s = (GLStateHolder)gl2state.get(gl);
//	        if (s == null) {
//	            s = new GLStateHolder(gl);
//	        }
//	        return s;
//	    }
//
//	    /**
//    	 * GLState constructor.
//    	 * 
//    	 * @param gl the GL context.
//    	 */
//	    private GLStateHolder(GL gl) {
//	        this.gl = gl;
//	        state = new IntHashtable();
//	        state.setDefaultValue(-1);
//	        initialize();
//	        gl2state.put(gl, this);
//	        
//	        System.out.println("initialize new glstate");
//	    }
//	    
//	    /**
//    	 * Initialize.
//    	 */
//    	private void initialize() {
//	        version = gl.glGetString(GL.GL_VERSION);
//	        state.put(GL.GL_TEXTURE_1D, 0);
//	        state.put(GL.GL_TEXTURE_2D, 0);
//	        state.put(GL.GL_TEXTURE_BINDING_1D, 0);
//	        state.put(GL.GL_TEXTURE_BINDING_2D, 0);
//	        state.put(GL.GL_TEXTURE_ENV_MODE, 0);
//	    }
//	    
//	    /**
//    	 * Initialize state.
//    	 * 
//    	 * @param attrib the attrib
//    	 * 
//    	 * @return the int
//    	 */
//    	private int initializeState(int attrib) {
//	        gl.glGetIntegerv(attrib, tmpValue, 0);
//	        state.put(attrib, tmpValue[0]);
//	        return tmpValue[0];
//	    }
//	    
//	    /**
//    	 * Returns a String representing the version of the OpenGL implementation.
//    	 * 
//    	 * @return a String representing the version of the OpenGL implementation.
//    	 */
//	    public String getVersion() {
//	        return version;
//	    }
//	    
//	    /**
//    	 * Returns the GL context.
//    	 * 
//    	 * @return the GL context.
//    	 */
//	    public GL getGL() {
//	        return gl;
//	    }
//	    
//	    /**
//    	 * Returns the value currently associated with the specified GL attribute.
//    	 * If the constant has not been queried before, the GL state is queried
//    	 * first to initialize the local state to the right value.
//    	 * 
//    	 * @param attrib the GL attribute
//    	 * 
//    	 * @return the associated value.
//    	 */
//	    public int getState(int attrib) {
//	        int s = state.get(attrib);
//	        if (s == -1) {
////	        	if(DEBUG){
////	        		System.out.println("State unititialized->initialize");
////	        	}
//	            s = initializeState(attrib);
//	        }
//	        return s;
//	    }
//
//	    /**
//    	 * Sets the value currently associated with a specified GL attribute.
//    	 * Returns <code>true</code> if the value is different from the one
//    	 * in the GL state, meaning that a GL primitive should be used to set it.
//    	 * * @param attrib the attribute
//    	 * 
//    	 * @param value the value r
//    	 * @param attrib the attrib
//    	 * 
//    	 * @return <code>true</code> if the value is different from the one
//    	 * in the GL state, meaning that a GL primitive should be used to set it.
//    	 */    
//	    public boolean setState(int attrib, int value) {
//	        if (getState(attrib) == value) {
////	        	if (DEBUG){
////	        		System.out.println("State already set, returning.");
////	        	}
//	            savedCount++;
//	            return DEBUG;
//	        }
//	        
////	        if (DEBUG)
////	        	System.out.println("State different, set it.");
//	        doneCount++;
//	        state.put(attrib, value);
//	        return true;
//	    }
//	    
//	    /**
//    	 * Check error.
//    	 */
//    	protected void checkError() {
//	        if (gl.glGetError() != 0)
//	            System.err.println("Error");            
//	    }
//
//	    /**
//    	 * Equivalent to glEnable but checks the value first and skip the
//    	 * GL function is the value is already set to 1.
//    	 * 
//    	 * @param attrib the attribute to set.
//    	 */    
//	    public void glEnable(int attrib) {
//	        if (setState(attrib, 1)) {
//	            gl.glEnable(attrib);
//	            checkError();
//	        }else{
//	        }
//	    }
//
//	    /**
//    	 * Equivalent to glDisable but checks the value first and skip the
//    	 * GL function is the value is already set to 0.
//    	 * 
//    	 * @param attrib the attribute to set.
//    	 */    
//	    public void glDisable(int attrib) {
//	        if (setState(attrib, 0)) {
//	            gl.glDisable(attrib);
//	            checkError();            
//	        }
//	    }
//
//	    /**
//    	 * Bind texture1 d.
//    	 * 
//    	 * @param tex the tex
//    	 */
//    	public void bindTexture1D(int tex) {
//	        if (setState(GL.GL_TEXTURE_BINDING_1D, tex)) {
//	            gl.glBindTexture(GL.GL_TEXTURE_1D, tex);
//	            checkError();        
//	        }
//	    }
//	    
//	    /**
//    	 * Bind texture2 d.
//    	 * 
//    	 * @param tex the tex
//    	 */
//    	public void bindTexture2D(int tex) {
//	        if (setState(GL.GL_TEXTURE_BINDING_2D, tex)) {
//	            gl.glBindTexture(GL.GL_TEXTURE_2D, tex);
//	            checkError();        
//	        }
//	    }
//	    
//	    /**
//    	 * Gl set shade model.
//    	 * 
//    	 * @param model the model
//    	 */
//    	public void glSetShadeModel(int model) {
//	        if (setState(GL.GL_SHADE_MODEL, model)) {
//	            gl.glShadeModel(model);
//	            checkError();
//	        }
//	    }
//	    
//	    /**
//    	 * Gl enable client state.
//    	 * 
//    	 * @param mode the mode
//    	 */
//    	public void glEnableClientState(int mode) {
//	        if (setState(mode, 1)) {
//	            gl.glEnableClientState(mode);
//	            checkError();
//	        }
//	    }
//	    
//	    /**
//    	 * Gl disable client state.
//    	 * 
//    	 * @param mode the mode
//    	 */
//    	public void glDisableClientState(int mode) {
//	        if (setState(mode, 0)) {
//	            gl.glDisableClientState(mode);
//	            checkError();
//	        }
//	    }
//	    
//	    /**
//    	 * Gl logic op.
//    	 * 
//    	 * @param op the op
//    	 */
//    	public void glLogicOp(int op) {
//	        if (setState(GL.GL_LOGIC_OP, op)) {
//	            gl.glLogicOp(op);
//	            checkError();
//	        }
//	    }
//	    
//	    /**
//    	 * Gl color4ub.
//    	 * 
//    	 * @param r the r
//    	 * @param g the g
//    	 * @param b the b
//    	 * @param a the a
//    	 */
//    	public void glColor4ub(byte r, byte g, byte b, byte a) {
//	        if (lastR != r || lastG != g || lastB != b || lastA != a) { 
//	            lastR = r;
//	            lastG = g;
//	            lastB = b;
//	            lastA = a;
//	            gl.glColor4ub(r, g, b, a);
//	            checkError();
//	        }
//	    }
//	    
//	    /**
//    	 * Gl color4f.
//    	 * 
//    	 * @param r the r
//    	 * @param g the g
//    	 * @param b the b
//    	 * @param a the a
//    	 */
//    	public void glColor4f(float r, float g, float b, float a) {
//	        glColor4ub((byte)(r*255), (byte)(g*255), (byte)(b*255), (byte)(a*255));
//	    }
//	    
	    
}
