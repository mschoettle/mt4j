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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * Abstracts usage of OpenGL materials.
 * 
 * Usage:
 * instantiate a new material,
 * set the material properties (setDiffuse(), setAmbient(), setSpecular(), setShininess()),
 * call material.apply() in your render function to set the OpenGL current material properties.
 */
public class GLMaterial {
    // A sampling of color and shininess values
    /** The Constant colorNone. */
    public static final float colorNone[]   = { 0f,  0f,  0f,  1f};      // no color = black
    
    /** The Constant colorYellow. */
    public static final float colorYellow[] = { 1f,  1f,  0f,  1f};
    
    /** The Constant colorRed. */
    public static final float colorRed[]    = { .4f, 0f,  0f,  1f};
    
    /** The Constant colorGreen. */
    public static final float colorGreen[]  = { .1f, .8f, .2f, 1f};
    
    /** The Constant colorBlue. */
    public static final float colorBlue[]   = { 0f,  0f,  1f,  1f};
    
    /** The Constant colorGray. */
    public static final float colorGray[]   = { .5f, .5f, .5f, 1f};
    
    /** The Constant colorWhite. */
    public static final float colorWhite[]  = { 1f,  1f,  1f,  1f};
    
    /** The Constant colorBlack. */
    public static final float colorBlack[]  = { 0f,  0f,  0f,  1f};
    
    /** The Constant colorBeige. */
    public static final float colorBeige[]  = { .7f, .7f, .4f, 1f};
    
    /** The Constant colorCyan. */
    public static final float colorCyan[]   = { .1f, .1f, .9f, 1f};
    
    /** The Constant colorDefaultDiffuse. */
    public static final float colorDefaultDiffuse[] = { .8f, .8f, .8f, 1f}; // OpenGL default diffuse color
    
    /** The Constant colorDefaultAmbient. */
    public static final float colorDefaultAmbient[] = { .2f, .2f, .2f, 1f}; // OpenGL default ambient color
    
    /** The Constant colorDefaultSpecular. */
    public static final float colorDefaultSpecular[] = { 1.0f, 1.0f, 1.0f, 1f};
    
    /** The Constant minShine. */
    public static final float minShine   = 0.0f;
    
    /** The Constant maxShine. */
    public static final float maxShine   = 127.0f;
    //
    /** The default diffuse. */
    private static FloatBuffer defaultDiffuse;
    
    /** The default ambient. */
    private static FloatBuffer defaultAmbient;
    
    /** The default specular. */
    private static FloatBuffer defaultSpecular;
    
    /** The default emission. */
    private static FloatBuffer defaultEmission;
    
    /** The default shine. */
    private static FloatBuffer defaultShine;

    // The color values for this material
    /** The diffuse. */
    private FloatBuffer diffuse;      // color of the lit surface
    
    /** The ambient. */
    private FloatBuffer ambient;      // color of the shadowed surface
    
    /** The specular. */
    private FloatBuffer specular;     // reflection color (typically this is a shade of gray)
    
    /** The emission. */
    private FloatBuffer emission;     // glow color
    
    /** The shininess. */
    private FloatBuffer shininess;    // size of the reflection highlight

    /** The gl. */
    private GL10 gl;
    
	/**
	 *  set up some default material color values
	 *  this code is run only once, when the class is first used
	 */
    static {
    	defaultAmbient  = allocFloats(colorDefaultAmbient);
    	defaultDiffuse  = allocFloats(colorDefaultDiffuse);
    	defaultSpecular = allocFloats(colorDefaultSpecular);
    	
        float[] shine = {50, 0, 0, 0};  // LWJGL requires four values, so include three extra zeroes
        defaultShine    = allocFloats(shine);
        
        defaultEmission = allocFloats(colorNone);
    }

    /**
     * Instantiates a new gL material.
     * 
     * @param gl the gl
     */
    public GLMaterial(GL10 gl) {
    	this.gl = gl;
        this.setDefaults();
    }

    /**
     * Instantiates a new gL material.
     * 
     * @param gl the gl
     * @param color the color
     */
    public GLMaterial(GL10 gl, float[] color) {
    	this.gl = gl;
        this.setDefaults();
        this.setDiffuseAndAmbientColor(color);
    }

	/**
	 * Set the material to OpenGL's default values (gray, with no reflection and no glow).
	 */
    public void setDefaults() {
        this.setDiffuse(colorDefaultDiffuse);
        this.setAmbient(colorDefaultAmbient);
        this.setSpecular(colorNone);
        this.setEmission(colorNone);
        this.setShininess(minShine);
    }

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *
	 * Functions to set the material properties
	 *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	/**
	 * Set the diffuse material color.  ([R][G][B][A]) This is the color of the material
	 * where it is directly lit.
	 * 
	 * @param color the color
	 */
    public void setDiffuse(float[] color) {
        this.diffuse = allocFloats(color);
    }

	/**
	 * Set the ambient material color.  ([R][G][B][A]) This is the color of the material
	 * where it is lit by indirect light (light scattered off the environment).
	 * Ie. the shadowed side of an object.
	 * 
	 * @param color the color
	 */
    public void setAmbient(float[] color) {
        this.ambient = allocFloats(color);
    }

	/**
	 * Set the specular material color. ([R][G][B][A]) This controls how much light
	 * is reflected off a glossy surface.  This color value describes
	 * the brightness of the reflection and is typically a shade of gray.
	 * Pure black means that no light is reflected (ie. a very rough matte
	 * surface).  Pure white means that the surface is highly reflective,
	 * 
	 * see also:  setShininess()
	 * 
	 * @param color the color
	 */
    public void setSpecular(float[] color) {
        this.specular = allocFloats(color);
    }

	/**
	 * Set the emission material color.   ([R][G][B][A]) This controls the "glow" of the material,
	 * and can be used to make a material that seems to be lit from inside.
	 * 
	 * @param color the color
	 */
    public void setEmission(float[] color) {
        this.emission = allocFloats(color);
    }

    /**
     * Set size of the reflection highlight.  Must also set the specular color for
     * shininess to have any effect:
     * setSpecular(GLMaterial.colorWhite);
     * 
     * @param howShiny  How sharp reflection is: 0 - 127 (127=very sharp pinpoint)
     */
    public void setShininess(float howShiny) {
        if (howShiny >= minShine && howShiny <= maxShine) {
            float[] tmp = {howShiny,0,0,0};
            this.shininess = allocFloats(tmp);
        }
    }

    /**
     * Call glMaterial() to activate these material properties in the OpenGL environment.
     * These properties will stay in effect until you change them or disable lighting.
     */
    public void apply() {
    	/*
    	// GL_FRONT: affect only front facing triangles
    	gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_AMBIENT, this.ambient);
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_DIFFUSE, this.diffuse);
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, this.specular);
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_EMISSION, this.emission);
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SHININESS, this.shininess);
        */
//    	/*
    	//FIXME welche einstellung nehmen?
    	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, this.ambient);
    	gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, this.diffuse);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, this.specular);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, this.emission);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, this.shininess);
//    	 */
    }

    /**
     * Reset all material settings to the default values.
     * 
     * @param gl the gl
     */
    public static void clear(GL10 gl) {
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_DIFFUSE, defaultDiffuse);
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_AMBIENT, defaultAmbient);
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, defaultSpecular);
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_EMISSION, defaultEmission);
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SHININESS, defaultShine);
        /*
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, defaultDiffuse);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, defaultAmbient);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, defaultSpecular);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, defaultEmission);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, defaultShine);
        */
    }

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *
	 * The following functions provide a simpler way to use materials
	 * that hides some of the complexity of the OpenGL functions.
	 *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	/**
	 * Sets the material color to approximate a "real" surface color.
	 * 
	 * Use the same color for diffuse and ambient.  To create a
	 * shadowed effect you should lower the ambient value for the
	 * light sources and lower the overall ambient light.
	 * 
	 * @param color the color
	 */
    public void setDiffuseAndAmbientColor(float[] color) {
        this.setDiffuse(color);   // surface directly lit
        this.setAmbient(color);   // surface in shadow
    }

    /**
     * Set the reflection properties.  Typically the reflection (specular color)
     * describes the brightness of the reflection, and is a shade of gray.
     * This function takes two params that describe the intensity
     * of the reflection, and the size of the highlight.
     * 
     * intensity - a float from 0-1 (0=no reflectivity, 1=maximum reflectivity)
     * highlight - a float from 0-1 (0=soft highlight, 1=sharpest highlight)
     * 
     * example: setReflection(1,1)  creates a bright, sharp reflection
     * setReflection(.5f,.5f)  creates a softer, wider reflection
     * 
     * @param intensity the intensity
     * @param highlight the highlight
     */
    public void setSpecular(float intensity, float highlight) {
		float[] color = {intensity,intensity,intensity,1}; // create a shade of gray
        this.setSpecular(color);
        this.setShininess((int)(highlight*127f)); // convert 0-1 to 0-127
    }

    /**
     * Make material appear to emit light.
     * 
     * @param color the color
     */
    public void setEmissionColor(float[] color) {
        this.emission = allocFloats(color);
    }

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *
	 * Native IO buffer functions
	 *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /** The Constant SIZE_FLOAT. */
	public static final int SIZE_FLOAT = 4;  // four bytes in a float

    /**
     * Alloc floats.
     * 
     * @param howmany the howmany
     * 
     * @return the float buffer
     */
    public static FloatBuffer allocFloats(int howmany) {
        return ByteBuffer.allocateDirect(howmany * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    /**
     * Alloc floats.
     * 
     * @param floatarray the floatarray
     * 
     * @return the float buffer
     */
    public static FloatBuffer allocFloats(float[] floatarray) {
        FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(floatarray).flip();
        return fb;
    }

}