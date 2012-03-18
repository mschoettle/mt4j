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

import java.nio.FloatBuffer;

import org.mt4j.util.PlatformUtil;
import org.mt4j.util.opengl.GL11;
import org.mt4j.util.opengl.GL11Plus;

import processing.core.PApplet;

/**
 * Methods to build VBOs and get their binding names	(Ids).
 * 
 * @author Christopher Ruff
 */
public class ToolsVBO {
	
	//TODO some methods are redundant
	//for floatbuffer vbo updates with vectors (3lements) can be updated the same way i.e.
	
	//////////////////////////////////////////////////////////////
	// 	Methods to build VBOs and get their binding names		//
	//////////////////////////////////////////////////////////////
	/**
	 * Generate vertex vbo.
	 * 
	 * @param pa the pa
	 * @param vertexBuffer the vertex buffer
	 * @param vertexCount the vertex count
	 * 
	 * @return the int
	 */
	public static int generateVertexVBO(PApplet pa, FloatBuffer vertexBuffer, int vertexCount){
		int[] vboVertices = new int[1]; 
//		GL gl = Tools3D.getGL(pa);
			GL11 gl = PlatformUtil.getGL11();
			gl.glGenBuffers(1, vboVertices, 0);  // Get A Valid Name
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboVertices[0]);  // Bind The Buffer
			// Load The Data
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertexCount * 3 * (int)GL11Plus.SIZEOF_FLOAT, vertexBuffer, GL11.GL_STATIC_DRAW);
			//Unbind VBOs 
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		return vboVertices[0];
	}
	
	/**
	 * Update vertex vbo.
	 * 
	 * @param pa the pa
	 * @param vertexBuffer the vertex buffer
	 * @param vertexCount the vertex count
	 * @param vboName the vbo name
	 */
	public static void updateVertexVBO(PApplet pa, FloatBuffer vertexBuffer, int vertexCount, int vboName){
//		GL gl = Tools3D.getGL(pa);
		GL11 gl = PlatformUtil.getGL11();
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboName);  // Bind The Buffer
		// Load The Data
		gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertexCount * 3 * (int)GL11Plus.SIZEOF_FLOAT, vertexBuffer, GL11.GL_STATIC_DRAW);
		//Unbind VBOs 
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	/**
	 * Generate texture vbo.
	 * 
	 * @param pa the pa
	 * @param textureBuffer the texture buffer
	 * @param vertexCount the vertex count
	 * 
	 * @return the int
	 */
	public static int generateTextureVBO(PApplet pa, FloatBuffer textureBuffer, int vertexCount){
		int[] vboTexCoords = new int[1];// Texture Coordinate VBO Name
//		GL gl = Tools3D.getGL(pa);
		GL11 gl = PlatformUtil.getGL11();
			gl.glGenBuffers(1, vboTexCoords, 0);  // Get A Valid Name
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboTexCoords[0]); // Bind The Buffer
			// Load The Data
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertexCount * 2 * (int)GL11Plus.SIZEOF_FLOAT, textureBuffer, GL11.GL_STATIC_DRAW);
			//Unbind VBOs 
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		return vboTexCoords[0];
	}
	
	/**
	 * Update texture vbo.
	 * 
	 * @param pa the pa
	 * @param textureBuffer the texture buffer
	 * @param vertexCount the vertex count
	 * @param vboName the vbo name
	 */
	public static void updateTextureVBO(PApplet pa, FloatBuffer textureBuffer, int vertexCount, int vboName){
//		GL gl = Tools3D.getGL(pa);
		GL11 gl = PlatformUtil.getGL11();
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboName); // Bind The Buffer
			// Load The Data
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertexCount * 2 * (int)GL11Plus.SIZEOF_FLOAT, textureBuffer, GL11.GL_STATIC_DRAW);
			//Unbind VBOs 
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	/**
	 * Generate color vbo.
	 * 
	 * @param pa the pa
	 * @param colorBuffer the color buffer
	 * @param vertexCount the vertex count
	 * 
	 * @return the int
	 */
	public static int generateColorVBO(PApplet pa, FloatBuffer colorBuffer, int vertexCount){
		int[] vboColor = new int[1];// vertexcolor Coordinate VBO Name
//		GL gl = Tools3D.getGL(pa);
		GL11 gl = PlatformUtil.getGL11();
			gl.glGenBuffers(1, vboColor, 0);  // Get A Valid Name
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboColor[0]); // Bind The Buffer
			// Load The Data
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertexCount * 4 * (int)GL11Plus.SIZEOF_FLOAT, colorBuffer, GL11.GL_STATIC_DRAW);
			//Unbind VBOs 
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		return vboColor[0];
	}
	
	/**
	 * Update color vbo.
	 * 
	 * @param pa the pa
	 * @param colorBuffer the color buffer
	 * @param vertexCount the vertex count
	 * @param vboName the vbo name
	 */
	public static void updateColorVBO(PApplet pa, FloatBuffer colorBuffer, int vertexCount, int vboName){
//		GL gl = Tools3D.getGL(pa);
		GL11 gl = PlatformUtil.getGL11();
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboName); // Bind The Buffer
			// Load The Data
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertexCount * 4 * (int)GL11Plus.SIZEOF_FLOAT, colorBuffer, GL11.GL_STATIC_DRAW);
			//Unbind VBOs 
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	/**
	 * Generate stroke color vbo.
	 * 
	 * @param pa the pa
	 * @param strokeColBuffer the stroke col buffer
	 * @param vertexCount the vertex count
	 * 
	 * @return the int
	 */
	public static int generateStrokeColorVBO(PApplet pa, FloatBuffer strokeColBuffer, int vertexCount){
		int[] vboStrokeColor = new int[1];// stroke Coordinate VBO Name
//		GL gl = Tools3D.getGL(pa);
		GL11 gl = PlatformUtil.getGL11();
			gl.glGenBuffers(1, vboStrokeColor, 0);  // Get A Valid Name
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboStrokeColor[0]); // Bind The Buffer
			// Load The Data
			gl.glBufferData(GL11.GL_ARRAY_BUFFER,vertexCount * 4 * (int)GL11Plus.SIZEOF_FLOAT, strokeColBuffer, GL11.GL_STATIC_DRAW);
			//Unbind VBOs 
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		return vboStrokeColor[0];
	}
	
	/**
	 * Update stroke color vbo.
	 * 
	 * @param pa the pa
	 * @param strokeColBuffer the stroke col buffer
	 * @param vertexCount the vertex count
	 * @param vboName the vbo name
	 */
	public static void updateStrokeColorVBO(PApplet pa, FloatBuffer strokeColBuffer, int vertexCount, int vboName){
//		GL gl = Tools3D.getGL(pa);
		GL11 gl = PlatformUtil.getGL11();
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboName); // Bind The Buffer
			// Load The Data
			gl.glBufferData(GL11.GL_ARRAY_BUFFER,vertexCount * 4 * (int)GL11Plus.SIZEOF_FLOAT, strokeColBuffer, GL11.GL_STATIC_DRAW);
			//Unbind VBOs 
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	
	/**
	 * Generate normals vbo.
	 * 
	 * @param pa the pa
	 * @param normalsBuffer the normals buffer
	 * @param normalsCount the normals count
	 * 
	 * @return the int
	 */
	public static int generateNormalsVBO(PApplet pa, FloatBuffer normalsBuffer, int normalsCount){
		int[] vboNormals = new int[1]; 
//		GL gl = Tools3D.getGL(pa);
		GL11 gl = PlatformUtil.getGL11();
			gl.glGenBuffers(1, vboNormals, 0);  // Get A Valid Name
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboNormals[0]);  // Bind The Buffer
			// Load The Data
			gl.glBufferData(GL11.GL_ARRAY_BUFFER, normalsCount * 3 * (int)GL11Plus.SIZEOF_FLOAT, normalsBuffer, GL11.GL_STATIC_DRAW);
			//Unbind VBOs 
			gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		return vboNormals[0];
	}
	
	
	/**
	 * Update normals vbo.
	 * 
	 * @param pa the pa
	 * @param normalsBuffer the normals buffer
	 * @param normalsCount the normals count
	 * @param vboName the vbo name
	 */
	public static void updateNormalsVBO(PApplet pa, FloatBuffer normalsBuffer, int normalsCount, int vboName){
//		GL gl = Tools3D.getGL(pa);
		GL11 gl = PlatformUtil.getGL11();
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboName);  // Bind The Buffer
		// Load The Data
		gl.glBufferData(GL11.GL_ARRAY_BUFFER, normalsCount * 3 * (int)GL11Plus.SIZEOF_FLOAT, normalsBuffer, GL11.GL_STATIC_DRAW);
		//Unbind VBOs 
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

}
