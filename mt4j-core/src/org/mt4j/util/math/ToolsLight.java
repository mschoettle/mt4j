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
package org.mt4j.util.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import org.mt4j.util.PlatformUtil;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11Plus;

import processing.core.PApplet;

/**
 * The Class ToolsLight.
 * @author Christopher Ruff
 */
public class ToolsLight {
    
    /** The Constant SIZE_FLOAT. */
    public static final int SIZE_FLOAT 	= 4;
    
    /** The Constant SIZE_INT. */
    public static final int SIZE_INT 	= 4;
    
    /**
     * Setup default lightning.
     * 
     * @param pa the pa
     * @param lightID the light id
     * @param position the position
     */
    public static void setupDefaultLightning(PApplet pa, int lightID, Vector3D position){
//    	GL gl = Tools3D.getGL(pa);
    	GL10 gl = PlatformUtil.getGL();
    	
    	//Set default ambient lightning for all objs
    	setAmbientLight(gl, new float[]{0.2f, 0.2f, 0.2f,1});
    	
    	//This means that glMaterial will control the polygon's specular and emission colours
    	//and the ambient and diffuse will both be set using glColor. 
    	if (gl instanceof GL11Plus) {
			GL11Plus gl11Plus = (GL11Plus) gl;
			gl11Plus.glColorMaterial(GL10.GL_FRONT, GL10.GL_AMBIENT_AND_DIFFUSE);
		}
//    	gl.glColorMaterial(GL10.GL_FRONT, GL10.GL_AMBIENT_AND_DIFFUSE);
//    	gl.glColorMaterial(GL10.GL_FRONT, GL10.GL_DIFFUSE);
    	
    	//Enable color material
    	gl.glEnable(GL10.GL_COLOR_MATERIAL);

    	/*
    	 * GL_RESCALE_NORMAL multiplies the transformed normal by a scale factor. 
    	 * If the original normals are unit length, and the ModelView matrix contains 
    	 * uniform scaling, this multiplication will restore the normals to unit length.
    	 * If the ModelView matrix contains nonuniform scaling, GL_NORMALIZE is the 
    	 * preferred solution.
    	*/
    	gl.glEnable(GL10.GL_RESCALE_NORMAL);
    	
    	float lightAmbient[]  = { .0f, .0f, .0f, 1f }; // scattered light
		float lightDiffuse[]  = { 1.0f, 1.0f, 1.0f, 1f }; // direct light
		float lightSpecular[] = { 1.0f, 1.0f, 1.0f, 1f }; // highlight
		float lightPosition[] = {position.x, position.y, position.z, 1.000f}; //last coord: if 1->pointlight, gegen 0:directionallight
    	
		//Set the ligth with aboves settings
    	setLight(gl, lightID, lightDiffuse, lightAmbient, lightSpecular, lightPosition);
    	
    	// Enable lightning
    	gl.glEnable(GL10.GL_LIGHTING);
    }
    
    /*
    The OpenGL light model presumes that the light that reaches your eye from the polygon surface arrives by four different mechanisms:

        * AMBIENT - light that comes from all directions equally and is scattered in all directions 
          equally by the polygons in your scene. This isn't quite true of the real world 
          - but it's a good first approximation for light that comes pretty much uniformly from the sky 
          and arrives onto a surface by bouncing off so many other surfaces that it might as well be uniform.
          
        * DIFFUSE - light that comes from a particular point source (like the Sun) and hits surfaces with an 
          intensity that depends on whether they face towards the light or away from it. 
          However, once the light radiates from the surface, it does so equally in all directions. 
          It is diffuse lighting that best defines the shape of 3D objects.
          
        * SPECULAR - as with diffuse lighting, the light comes from a point souce, but with specular lighting, 
          it is reflected more in the manner of a mirror where most of the light bounces off in a particular 
          direction defined by the surface shape. Specular lighting is what produces the shiney highlights and 
          helps us to distinguish between flat, dull surfaces such as plaster and shiney surfaces like polished plastics and metals.
         
        * EMISSION - in this case, the light is actually emitted by the polygon - equally in all directions. 

    So, there are THREE light colours for each light - Ambient, Diffuse and Specular (set with glLight) and FOUR for 
    each surface (set with glMaterial). All OpenGL implementations support at least eight light sources - and the glMaterial 
    can be changed at will for each polygon (although there are typically large time penalties for doing that 
    - so we'd like to minimise the number of changes).
    The final polygon colour is the sum of all four light components, each of which is formed by multiplying 
    the glMaterial colour by the glLight colour (modified by the directionality in the case of Diffuse and Specular).
    
    Since there is no Emission colour for the glLight, that is added to the final colour without modification.
    
    ====> 	GL_Light guideline: set the Diffuse and Specular components to the colour of the light source, and the Ambient to 
    		the same colour - but at MUCH reduced intensity, 10% to 40% seems reasonable in most cases.
    
    ====> 	Material guidline: For the glMaterial, it's usual to set the Ambient and Diffuse colours to the natural colour of the object 
    		and to put the Specular colour to white. The emission colour is generally black for objects that do not shine by their own light.
    		Before you can use an OpenGL light source, it must be positioned using the glLight command and enabled 
    		using glEnable(GL_LIGHTn) where 'n' is 0 through 7. There are additional commands to make light sources directional 
    		(like a spotlight or a flashlight) and to have it attenuate as a function of range from the light source. 
     */
    
	/**
     * Set the color of the Global Ambient Light.  Affects all objects in
     * scene regardless of their placement.
     * 
     * @param gl the gl
     * @param ambientLightColor the ambient light color
     */
	public static void setAmbientLight(GL10 gl, float[] ambientLightColor) {
		FloatBuffer ltAmbient = allocFloats(ambientLightColor);
		gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, ltAmbient);
	}
	
	
	/**
	 * Simple way to setup a light.  Uses same color for direct light (diffuse),
	 * reflected highlight (specular) and scattered light (ambient).  Ambient
	 * color is darkened to 1/4 of the light color.
	 * 
	 * @param GLLightHandle the gL light handle
	 * @param color the color
	 * @param position the position
	 * @param gl the gl
	 */
    public static void setLight(GL10 gl, int GLLightHandle, float[] color, float[] position ){
        float[] ambientLight = {color[0]/4f, color[1]/4f, color[2]/4f, color[3]/4f};
        
        FloatBuffer lightColor 		= allocFloats(color);
        FloatBuffer ambientColor 	= allocFloats(ambientLight);
        FloatBuffer ltPosition 		= allocFloats(position);
        
        gl.glLightfv(GLLightHandle, GL10.GL_DIFFUSE, lightColor);   // color of the direct illumination
        gl.glLightfv(GLLightHandle, GL10.GL_SPECULAR, lightColor);  // color of the highlight (same as direct light)
        gl.glLightfv(GLLightHandle, GL10.GL_AMBIENT, ambientColor); // color of the scattered light (darker)
        gl.glLightfv(GLLightHandle, GL10.GL_POSITION, ltPosition);
        
//        gl.glEnable(GLLightHandle);	// Enable the light (GL_LIGHT1 - 7)
    }
	
	
    /**
     * Set the color of a 'positional' light (a light that has a specific
     * position within the scene).  <BR>
     * <BR>
     * Params:<BR>
     * an OpenGL light number (GL11.GL_LIGHT1),<BR>
     * 'Diffuse': color of direct light from this source,<BR>
     * 'Ambient': color of scattered light from this source <BR>
     * 'Specular': color of this light reflected off a surface,<BR>
     * position.<BR>
     * 
     * @param gl the gl
     * @param GLLightHandle the gL light handle
     * @param diffuseLightColor the diffuse light color
     * @param ambientLightColor the ambient light color
     * @param specularLightColor the specular light color
     * @param position the position
     */
    public static void setLight(
    		GL10 gl, 
    		int GLLightHandle,    		
            float[] diffuseLightColor,
            float[] ambientLightColor,
    		float[] specularLightColor, 
    		float[] position 
    ){
            FloatBuffer ltDiffuse 	= allocFloats(diffuseLightColor);
            FloatBuffer ltAmbient 	= allocFloats(ambientLightColor);
            FloatBuffer ltSpecular 	= allocFloats(specularLightColor);
            FloatBuffer ltPosition 	= allocFloats(position);
            
            gl.glLightfv(GLLightHandle, GL10.GL_DIFFUSE, ltDiffuse);   // color of the direct illumination
            gl.glLightfv(GLLightHandle, GL10.GL_AMBIENT, ltAmbient);   // color of the reflected light
            gl.glLightfv(GLLightHandle, GL10.GL_SPECULAR, ltSpecular); // color of the highlight (same as direct light)
            gl.glLightfv(GLLightHandle, GL10.GL_POSITION, ltPosition); //FIXME ENABLE!
            
//            gl.glEnable(GLLightHandle);	// Enable the light (GL_LIGHT1 - 7)
            //GL11.glLightf(GLLightHandle, GL11.GL_QUADRATIC_ATTENUATION, .005F);    // how light beam drops off
    }
    
//    /**
//     * Set the position of a light to the given xyz. 
//     * <br>NOTE: Positional light only, not directional.
//     */
//    public static void setLightPos(GL gl, int GLLightHandle, float x, float y, float z){
//    	float[] position = new float[] {x,y,z,1};
//        gl.glLightfv(GLLightHandle, GL10.GL_POSITION, allocFloats(position));
//    }
    
    /**
 * Set the position of a light to the given xyz.
 * <br>NOTE: Positional light only, not directional.
 * 
 * @param gl the gl
 * @param GLLightHandle the gL light handle
 * @param x the x
 * @param y the y
 * @param z the z
 */
    public static void setLightPos(GL10 gl, int GLLightHandle, float x, float y, float z){
    	float[] position = new float[] {x,y,z,1};
        gl.glLightfv(GLLightHandle, GL10.GL_POSITION, position, 0);
    }
    
    /**
     * Disables the given light. (GL10.GL_LIGHT0..7)
     * 
     * @param gl the gl
     * @param GLLightHandle the gL light handle
     */
    public static void disableLight(GL10 gl, int GLLightHandle){
    	gl.glDisable(GLLightHandle);
    }
    
    
    
    /**
     * Alloc floats.
     * 
     * @param floatArray the float array
     * 
     * @return the float buffer
     */
    private static FloatBuffer allocFloats(float[] floatArray) {
        FloatBuffer fb = ByteBuffer.allocateDirect(floatArray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(floatArray).flip();
        return fb;
    }

    /**
     * Alloc floats.
     * 
     * @param howmany the howmany
     * 
     * @return the float buffer
     */
    private static FloatBuffer allocFloats(int howmany) {
        return ByteBuffer.allocateDirect(howmany * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
    
    /**
     * Alloc ints.
     * 
     * @param howmany the howmany
     * 
     * @return the int buffer
     */
    private static IntBuffer allocInts(int howmany) {
        return ByteBuffer.allocateDirect(howmany * SIZE_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
    }

}
