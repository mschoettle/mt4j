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
package org.mt4j.components.visibleComponents.shapes;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.math.ToolsBuffers;
import org.mt4j.util.math.ToolsVBO;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GL11;
import org.mt4j.util.opengl.GL11Plus;

import processing.core.PApplet;

/**
 * A class which holds the vertices and eventually also
 * the normals, colors, indices, displaylist ids,and vbo ids of the
 * geometry.
 * 
 * @author C.Ruff
 */
public class GeometryInfo {
	
	/** The r. */
	private PApplet r;
	
//	Vertices Stuff	\\
	
	/** The vertices local. */
	private Vertex[] vertices;
	
	/** The normals. */
	private Vector3D[] normals;
	
	/** The indices. */
	private short[] indices; 
	
	/** The vert buff. */
	private FloatBuffer vertBuff;
	
	/** The color buff. */
	private FloatBuffer colorBuff;
	
	/** The stroke col buff. */
	private FloatBuffer strokeColBuff;
	
	/** The tex buff. */
	private FloatBuffer texBuff;
	
	/** The normals buff. */
	private FloatBuffer normalsBuff; 
	
	/** The indices buff. */
	private Buffer 	indicesBuff;
	
	//	 Pure GL VBO indices names \\	
	/** The vbo vertices id. */
	private int vboVerticesID;
	
	/** The vbo color id. */
	private int vboColorID;
	
	/** The vbo texture id. */
	private int vboTextureID;
	
	/** The vbo stroke col id. */
	private int vboStrokeColID;
	
	/** The vbo normals id. */
	private int vboNormalsID; 	
	
	// Display list ids
	/** The display list i ds. */
	private int[] displayListIDs;

	/** The indexed. */
	private boolean indexed;
	
	/** The contains normals. */
	private boolean containsNormals;
	
	private boolean textureCoordsNormalized = true;
	
	private boolean adaptedCoordsNPOT = false;
	
	
	/**
	 * Creates a new GeometryInfo.
	 * <br>As only vertices are supplied,
	 * the normals (+ normal buffers, normal vbos) will be null.
	 * <br>The indices array and indices buffer will also be null.
	 * 
	 * @param pApplet the applet
	 * @param vertices the vertices
	 */
	public GeometryInfo(PApplet pApplet, Vertex[] vertices){
		this(pApplet, vertices, null, null);
	}
	
	/**
	 * Instantiates a new geometry info.
	 * 
	 * @param pApplet the applet
	 * @param vertices the vertices
	 * @param normals the normals
	 */
	public GeometryInfo(PApplet pApplet, Vertex[] vertices, Vector3D[] normals){
		this(pApplet, vertices, normals, null);
	}
	
	/**
	 * Instantiates a new geometry info.
	 * 
	 * @param pApplet the applet
	 * @param vertices the vertices
	 * @param indices the indices
	 */
	public GeometryInfo(PApplet pApplet, Vertex[] vertices, short[] indices){
		this(pApplet, vertices, null, indices);
	}

	/**
	 * Creates a new GeometryInfo with vertices, normals and indices.
	 * <br>The number of normals should match the number of indices or vertices.
	 * 
	 * @param pApplet the applet
	 * @param vertices the vertices
	 * @param normals the normals
	 * @param indices the indices
	 */
	public GeometryInfo(PApplet pApplet, Vertex[] vertices, Vector3D[] normals, short[] indices){
		this.r = pApplet;
		//VBO Ids
		this.vboVerticesID 	= -1;
		this.vboColorID 	= -1;
		this.vboTextureID 	= -1;
		this.vboStrokeColID = -1;
		this.vboNormalsID 	= -1;
		//Displaylist Ids
		this.displayListIDs = new int[]{-1, -1};
		if (!(vertices.length > 0)){
//			System.err.println("Warning in " + this + " : trying to create GeometryInfo with no vertices supplied!");
		}
		this.reconstruct(vertices, normals, indices, false, false, null);
	}
	
	
	/**
	 * Reconstructs the geometry with the given parameters.
	 * Normals, indices and styleinfo may be null.
	 * 
	 * @param vertices the vertices
	 * @param normals the normals
	 * @param indices the indices
	 * @param createOrUpdateOGLBuffers the create or update ogl buffers
	 * @param createOrUpdateVBO the create or update vbo
	 * @param styleInfo the style info
	 */
	public void reconstruct(
			Vertex[] 	vertices, 
			Vector3D[] 	normals, 
			short[] 		indices, 
			boolean 	createOrUpdateOGLBuffers, 
			boolean 	createOrUpdateVBO, 
			StyleInfo 	styleInfo
		){
		this.vertices = vertices;
		
		//Set the indices and normals, 
		//also creates buffers and vbos if createOrUpdateOGLBuffers, createOrUpdateVBO are set
		this.setIndices(indices, createOrUpdateOGLBuffers);
		this.setNormals(normals, createOrUpdateOGLBuffers, createOrUpdateVBO);
		
		if (createOrUpdateOGLBuffers){
			if (styleInfo == null){
				styleInfo = new StyleInfo();
			}
			
			//Create new Buffers for verts, color, stroke color, and texture buffers
			this.generateNewVertsColStrokeColTexBuffers(styleInfo);
			
			if (createOrUpdateVBO){ 
				//Generate or update the VBOs
				this.generateOrUpdateVertColStrokeColTexVBOs();
			}
		}
	}
	
	
	
	
	//////// INDICES STUFF //////////////////
	/**
	 * Adds indices to the geometry. Marks the geometry to be indexed. (isIndexed() returns true)
	 * <br>If useopenGL is true, a IntBuffer is also created for use with OpenGl.
	 * If the indices array is != null, the geometry will return true at isIndexed() afterwards
	 * 
	 * @param indices the indices
	 * @param createOrUpdateOGLBuffers the create or update ogl buffers
	 */
	public void setIndices(short[] indices, boolean createOrUpdateOGLBuffers/*, boolean createOrUpdateVBO*/) {
		if (indices != null && indices.length > 0){
			this.setIndexed(true);
			this.indices = indices;
			
			if (MT4jSettings.getInstance().isOpenGlMode() && createOrUpdateOGLBuffers){
				//Set Buffer and maybe EBO //TODO create EBO Element Buffer Object?
//				this.setIndicesBuffer(ToolsBuffers.generateIndicesBuffer(indices));
				
				this.setIndicesBuffer(ToolsBuffers.generateIndicesBuffer(indices));
			}
		}else{
			this.setIndexed(false);
		}
	}
	
	
	/**
	 * Gets the indices.
	 * 
	 * @return the indices
	 * 
	 * the array if indices
	 */
	public short[] getIndices(){
		return this.indices;
	}

	/**
	 * Sets the indexed.
	 * 
	 * @param b the new indexed
	 */
	private void setIndexed(boolean b) {
		this.indexed = b;
	}
	
	/**
	 * Returns true, if an indices array for the geometry has been set.
	 * 
	 * @return true, if checks if is indexed
	 * 
	 * true, if indexed
	 */
	public boolean isIndexed(){
		return this.indexed;
	}
	//////// INDICES STUFF //////////////////
	
	
	
	
	//////// NORMALS STUFF //////////////////
	/**
	 * Adds normals to the geometry info.
	 * <br>Also creates/updates the buffers and vbos of the normals if the booleans are set.
	 * <br>If the normal vector is != null, the geometry will return true at isContainsNormals() afterwards
	 * 
	 * @param normals the normals
	 * @param createOrUpdateOGLBuffers the create or update ogl buffers
	 * @param createOrUpdateVBO the create or update vbo
	 */
	public void setNormals(Vector3D[] normals, boolean createOrUpdateOGLBuffers, boolean createOrUpdateVBO) {
		if (normals != null && normals.length > 0){
			this.setContainsNormals(true);
			//Set the normal array and say that the geometry contains normals
			this.normals = normals;
			//Set Buffer and maybe VBO
			if (MT4jSettings.getInstance().isOpenGlMode() 
				&& createOrUpdateOGLBuffers
				){
				this.setNormalsBuffer(ToolsBuffers.generateNormalsBuffer(normals));
				
				if (createOrUpdateVBO){
					if (this.getVBONormalsName() == -1){ 
						//Create new normal vbo
						this.vboNormalsID = ToolsVBO.generateNormalsVBO(this.r, this.getNormalsBuff(), this.getNormals().length);
					}else{
						//Update normals vbo
						this.updateNormalsVBO(this.getNormalsBuff(), false, false);
					}
				}
				
			}
			
			//If the geometry isnt indexed, the number of normals should match the number of vertices!		
			if (!this.isIndexed() && normals.length != this.getVertexCount()){
				System.err.println("WARNING: The number of normal vectors supplied (to " + this + ")  isnt equal to the number of vertices!" +
						"\n Normals: " + normals.length + " Vertices: " + this.getVertexCount());
			}
		}else{
			this.setContainsNormals(false);
		}
	}
	
	/**
	 * Gets the normals.
	 * 
	 * @return the normals
	 * 
	 * The array of normal vectors
	 */
	public Vector3D[] getNormals() {
		return this.normals;
	}

	/**
	 * Sets the contains normals.
	 * 
	 * @param b the new contains normals
	 */
	private void setContainsNormals(boolean b){
		this.containsNormals = b;
	}
	
	/**
	 * Returns true, if a normals array for the geometry has been set.
	 * 
	 * @return true, if checks if is contains normals
	 * 
	 * true, if the geometry contains normals
	 */
	public boolean isContainsNormals(){
		return this.containsNormals;
	}
	//////// NORMALS STUFF //////////////////
	
	
	/**
	 * (Re-)Generates buffers for use with gl.drawElements or gl.drawArrays in opengl mode.
	 * <p>Generates:
	 * <li>Vertex-
	 * <li>Color-
	 * <li>StrokeColor-
	 * <li>Texture-
	 * Buffers.
	 * <br><strong>NOTE:</strong>DOESENT CREATE A NORMAL OR INDEX BUFFER! THIS IS DONE WITH THE setNormals()/setIndices() METHODs!
	 * 
	 * @param styleInfo the style info
	 */
	private void generateNewVertsColStrokeColTexBuffers(StyleInfo styleInfo){
			this.generateDefaultVertexBuffer();
			this.generateDefaultColorBuffer();
			this.generateDefaultStrokeColorBuffer(styleInfo);
			this.generateDefaultTextureBuffer();
	}
	
	/**
	 * Updates all draw buffers with the current settings
	 * 
	 * @param styleInfo the style info
	 */
	public void generateOrUpdateBuffersLocal(StyleInfo styleInfo){
////		if (this.getVertBuff() == null){
//			this.generateDefaultVertexBuffer();
////		}
////		if (this.getColorBuff() == null){
//			this.generateDefaultColorBuffer();
////		}
////		if (this.getStrokeColBuff() == null){
//			this.generateDefaultStrokeColorBuffer(styleInfo);
////		}
////		if (this.getTexBuff() == null){
//			this.generateDefaultTextureBuffer();
////		}
		this.generateNewVertsColStrokeColTexBuffers(styleInfo);
		
		if (this.isContainsNormals()){
//			if (this.getNormalsBuff() == null){
				this.setNormals(this.getNormals(), true, false);
//			}
		}
		if (this.isIndexed()){
//			if (this.getIndexBuff() == null){
				this.setIndices(this.getIndices(), true);
//			}
		}
	}
	
	/**
	 * Generate default vertex buffer.
	 */
	private void generateDefaultVertexBuffer(){
		this.setVertexBuffer(ToolsBuffers.generateVertexBuffer(this.getVertices()));
	}
	
	/**
	 * Generate default color buffer.
	 */
	private void generateDefaultColorBuffer(){
		this.setColorBuffer(ToolsBuffers.generateColorBuffer(this.getVertices()));
	}
	
	/**
	 * Generate default stroke color buffer.
	 * 
	 * @param styleInfo the style info
	 */
	private void generateDefaultStrokeColorBuffer(StyleInfo styleInfo){
		this.setStrokeColorBuffer(ToolsBuffers.generateStrokeColorBuffer(this.getVertices().length, styleInfo.getStrokeRed(), styleInfo.getStrokeGreen(), styleInfo.getStrokeBlue(), styleInfo.getStrokeAlpha()));
	}
	
	/**
	 * Generate default stroke color buffer.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	private void generateDefaultStrokeColorBuffer(float r, float g, float b, float a){
		this.setStrokeColorBuffer(ToolsBuffers.generateStrokeColorBuffer(this.getVertices().length, r,g,b,a));
	}
	
	/**
	 * Generate default texture buffer.
	 */
	private void generateDefaultTextureBuffer(){
		this.setTextureBuffer(ToolsBuffers.generateTextureBuffer(this.getVertices()));
	}
	
	/**
	 * Sets the vertex buffer.
	 * 
	 * @param vertBuff the new vertex buffer
	 */
	private void setVertexBuffer(FloatBuffer vertBuff){
		this.vertBuff = vertBuff;
	}
	
	/**
	 * Sets the color buffer.
	 * 
	 * @param colorBuff the new color buffer
	 */
	private void setColorBuffer(FloatBuffer colorBuff){
		this.colorBuff = colorBuff;
	}
	
	/**
	 * Sets the stroke color buffer.
	 * 
	 * @param strokeColBuff the new stroke color buffer
	 */
	private void setStrokeColorBuffer(FloatBuffer strokeColBuff){
		this.strokeColBuff = strokeColBuff;
	}
	
	/**
	 * Sets the texture buffer.
	 * 
	 * @param texBuff the new texture buffer
	 */
	private void setTextureBuffer(FloatBuffer texBuff){
		this.texBuff = texBuff;
	}
	
	/**
	 * Sets the normals buffer.
	 * 
	 * @param normBuff the new normals buffer
	 */
	private void setNormalsBuffer(FloatBuffer normBuff){
		this.normalsBuff = normBuff;
	}
	
	/**
	 * Sets the indices buffer.
	 * 
	 * @param indicesBuff the new indices buffer
	 */
	private void setIndicesBuffer(ShortBuffer indicesBuff){
		this.indicesBuff = indicesBuff;
	}
	
	/////////////// BUFFERS GETTER //////////////////////
	/**
	 * Gets the color buff.
	 * 
	 * @return the color buff
	 */
	public FloatBuffer getColorBuff() {
		return this.colorBuff;
	}
	
	/**
	 * Gets the index buff.
	 * 
	 * @return the index buff
	 */
	public Buffer getIndexBuff() {
		return this.indicesBuff;
	}
	
	/**
	 * Gets the stroke col buff.
	 * 
	 * @return the stroke col buff
	 */
	public FloatBuffer getStrokeColBuff() {
		return this.strokeColBuff;
	}
	
	/**
	 * Gets the tex buff.
	 * 
	 * @return the tex buff
	 */
	public FloatBuffer getTexBuff() {
		return this.texBuff;
	}
	
	/**
	 * Gets the vert buff.
	 * 
	 * @return the vert buff
	 */
	public FloatBuffer getVertBuff() {
		return this.vertBuff;
	}
	
	/**
	 * Gets the normals buff.
	 * 
	 * @return the normals buff
	 */
	public FloatBuffer getNormalsBuff() {
		return this.normalsBuff;
	}
	/////////////// BUFFERS GETTER //////////////////////
	
	
	/////////////// VBO GENERATING //////////////////////
	/**
	 * Generates Vertex Buffer Objects (VBO)
	 * from the local buffers
	 * for Vertex, Texture, Color and StrokeColor.
	 * <b>CREATES THEM ONLY IF THEY DONT EXIST YET!
	 * IF THEY EXIST; THEY ARE UPDATED FROM THE BUFFERS!
	 * <p>
	 * If the geometry had vbos already, we should delete them first
	 * usually.
	 */
	private void generateOrUpdateVertColStrokeColTexVBOs(){
		PApplet pa 		= this.getRenderer();
		int vertexCount = this.getVertexCount();
		//If no vbos exist yet, create them now
		if (this.getVBOVerticesName() 	== -1){
			this.vboVerticesID 	= ToolsVBO.generateVertexVBO(pa, this.getVertBuff(), vertexCount);
		}else{
			this.updateVertexVBO(this.getVertBuff(), false, false);
		}
		if (this.getVBOColorName() 		== -1){
			this.vboColorID 	= ToolsVBO.generateColorVBO(pa, this.getColorBuff(), vertexCount);
		}else{
			this.updateColorVBO(this.getColorBuff());
		}
		if (this.getVBOStrokeColorName()== -1){
			this.vboStrokeColID = ToolsVBO.generateStrokeColorVBO(pa, this.getStrokeColBuff(), vertexCount);
		}else{
			this.updateStrokeColorVBO(this.getStrokeColBuff());
		}
		if(this.getVBOTextureName()	== -1){
			this.vboTextureID 	= ToolsVBO.generateTextureVBO(pa, this.getTexBuff(), vertexCount);
		}else{
			this.updateTextureVBO(this.getTexBuff());
		}
	}
	
	
	/**
	 * Generates Vertex Buffer Objects (VBO)
	 * from the local buffers
	 * for Vertex, Texture, Color, StrokeColor and Normals.
	 * <b>CREATES THEM ONLY IF THEY DONT EXIST YET!
	 * <p>
	 * If the geometry had vbos already, we should delete them first
	 * usually.
	 */
	public void generateOrUpdateAllVBOs(){
		//If no normals vbo exists, create it now
		if (this.isContainsNormals()){
			PApplet pa = this.getRenderer();
			if (this.getVBONormalsName() == -1){
				this.vboNormalsID 	= ToolsVBO.generateNormalsVBO(pa, this.getNormalsBuff(), this.getNormals().length);
			}else{
				this.updateNormalsVBO(this.getNormalsBuff(), false, false);
			}
		}
		//Generate/Update other VBOs
		this.generateOrUpdateVertColStrokeColTexVBOs();
	}
	/////////////// VBO GENERATING //////////////////////
	
	
	/////////////// VBO UPDATING //////////////////////
//	/**
//	 * Updates all vbos with the current buffers.
//	 * <br>Will crash if the vbos havent been creates yet.
//	 * <br>To create VBOs, use <code>generateAllVbos()</code>
//	 */
//	public void updateAllVbosLocal() {
//		try {
//			PApplet pa 		= this.getRenderer();
//			int vertexCount = this.getVertexCount();
//			ToolsVBO.updateVertexVBO(pa, this.getVertBuff(), vertexCount, this.vboVerticesID);
//			ToolsVBO.updateTextureVBO(pa, this.getTexBuff(), vertexCount, this.vboTextureID);
//			ToolsVBO.updateColorVBO(pa, this.getColorBuff(), vertexCount, this.vboColorID);
//			ToolsVBO.updateStrokeColorVBO(pa, this.getStrokeColBuff(), vertexCount, this.vboStrokeColID);
//			
//			if (this.isContainsNormals()){
//				ToolsVBO.updateNormalsVBO(pa, this.getNormalsBuff(), this.normals.length, this.vboNormalsID);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * Updates the vertex buffer objects and sets the specified vertexbuffer to the geometryinfo.
	 * If setAsNewVertexBuffer is set to true, this also updates the vertex array. (not cheap but the geometry
	 * may act inconsistent if the arrays are not also updated - depends on usage).
	 * <br>If we only want the vbo to be updated we set both booleans to false
	 * 
	 * @param vertexBuffer the vertex buffer
	 * @param setAsNewVertexBuffer the set as new vertex buffer
	 * @param setAsNewVertexArray the set as new vertex array
	 */
	public void updateVertexVBO(FloatBuffer vertexBuffer, boolean setAsNewVertexBuffer , boolean setAsNewVertexArray){
		if (setAsNewVertexArray)
			this.vertices = ToolsBuffers.getVertexArray(vertexBuffer);
		//FIXME size correctly calculated?
		if (setAsNewVertexBuffer)
			this.setVertexBuffer(vertexBuffer);
		ToolsVBO.updateVertexVBO(this.getRenderer(), vertexBuffer, vertexBuffer.capacity()/(3 /*3 v array data per vertex */ * 4 /*when buffer created v array * 4*/), this.getVBOVerticesName()); 
	}
	
	/**
	 * Update texture vbo.
	 * 
	 * @param textureBuffer the texture buffer
	 */
	public void updateTextureVBO(FloatBuffer textureBuffer){
		this.setTextureBuffer(textureBuffer);
		//FIXME size correctly calculated?
		ToolsVBO.updateTextureVBO(this.getRenderer(), textureBuffer, textureBuffer.capacity()/(2 /*2 col data per vertex */ * 4 /*when buffer created v array * 4*/), this.getVBOTextureName()); 
	}
	
	/**
	 * Update color vbo.
	 * 
	 * @param colorBuffer the color buffer
	 */
	public void updateColorVBO(FloatBuffer colorBuffer){
		this.setColorBuffer(colorBuffer);
		//FIXME size correctly calculated?
		ToolsVBO.updateColorVBO(this.getRenderer(), colorBuffer, colorBuffer.capacity()/(4 /*4 col data per vertex */ * 4 /*when buffer created v array * 4*/), this.getVBOColorName());
	}
	
	/**
	 * Update stroke color vbo.
	 * 
	 * @param strokeColorBuffer the stroke color buffer
	 */
	public void updateStrokeColorVBO(FloatBuffer strokeColorBuffer){
		this.setStrokeColorBuffer(strokeColorBuffer);
		//FIXME size correctly calculated?
		ToolsVBO.updateStrokeColorVBO(this.getRenderer(), strokeColorBuffer, strokeColorBuffer.capacity()/(4 /*4 col data per vertex */ * 4 /*when buffer created v array * 4*/), this.getVBOStrokeColorName());
	}
	
	/**
	 * Update normals vbo.
	 * 
	 * @param normalsBuffer the normals buffer
	 * @param setAsNewNormalBuffer the set as new normal buffer
	 * @param setAsNewNormalArray the set as new normal array
	 */
	public void updateNormalsVBO(FloatBuffer normalsBuffer, boolean setAsNewNormalBuffer, boolean setAsNewNormalArray){
		if (setAsNewNormalArray)
			this.setNormals(ToolsBuffers.getVector3DArray(normalsBuffer), false, false);
		if (setAsNewNormalBuffer)
			this.setNormalsBuffer(normalsBuffer);
		//FIXME size correctly calculated?
		ToolsVBO.updateNormalsVBO(this.getRenderer(), normalsBuffer, normalsBuffer.capacity()/(3 /*3 v array data per vertex */ * 4 /*when buffer created v array * 4*/), this.getVBONormalsName()); 
	}
	/////////////// VBO UPDATING //////////////////////
	
	
	/**
	 * Deletes all VBOs of the geometry.
	 */
	public void deleteAllVBOs(){
		if (MT4jSettings.getInstance().isOpenGlMode()){
			//			GL gl = Tools3D.getGL(r);
			GL11 gl = PlatformUtil.getGL11();
			if (gl != null){
				if (this.getVBOVerticesName() != -1){
					gl.glDeleteBuffers(1, new int[]{this.getVBOVerticesName()},0);
					this.vboVerticesID = -1;
				}
				if (this.getVBOColorName() != -1){
					gl.glDeleteBuffers(1, new int[]{this.getVBOColorName()},0);
					this.vboColorID = -1;
				}
				if (this.getVBOStrokeColorName() != -1){
					gl.glDeleteBuffers(1, new int[]{this.getVBOStrokeColorName()},0);
					this.vboStrokeColID = -1;
				}
				if (this.getVBOTextureName() != -1){
					gl.glDeleteBuffers(1, new int[]{this.getVBOTextureName()},0);
					this.vboTextureID = -1;
				}
				if (this.getVBONormalsName() != -1){
					gl.glDeleteBuffers(1, new int[]{this.getVBONormalsName()},0);
					this.vboNormalsID = -1;
				}
			}
		}
	}

	
	//////////////// VBO GETTERS //////////////////////
	/**
	 * Gets the vBO vertices name.
	 * 
	 * @return the vBO vertices name
	 */
	public int getVBOVerticesName(){
		return this.vboVerticesID;
	}
	
	/**
	 * Gets the vBO color name.
	 * 
	 * @return the vBO color name
	 */
	public int getVBOColorName(){
		return this.vboColorID;
	}
	
	/**
	 * Gets the vBO texture name.
	 * 
	 * @return the vBO texture name
	 */
	public int getVBOTextureName(){
		return this.vboTextureID;
	}
	
	/**
	 * Gets the vBO stroke color name.
	 * 
	 * @return the vBO stroke color name
	 */
	public int getVBOStrokeColorName(){
		return this.vboStrokeColID;
	}
	
	/**
	 * Gets the vBO normals name.
	 * 
	 * @return the vBO normals name
	 */
	public int getVBONormalsName(){
		return this.vboNormalsID;
	}
	////////////////VBO GETTERS //////////////////////
	

	
	
	
	//////////////// DISPLAY LISTS //////////////////////
	/**
	 * Generates 2 openGL display lists for drawing this shape.
	 * <br>One for the interior (with textures etc.) and
	 * one for drawing the outline.
	 * <br><code>setUseDirectGL</code> has to be set to true first!
	 * <br>To use the display lists for drawing, call <code>setUseDisplayList()</code>
	 * <br>NOTE: if a display list already existed, we should delete that first!
	 * 
	 * @param useTexture the use texture
	 * @param texture the texture
	 * @param fillDrawMode the fill draw mode
	 * @param drawSmooth the draw smooth
	 * @param strokeWeight the stroke weight
	 */
//	public void generateDisplayLists(boolean useTexture, PImage texture, int fillDrawMode, boolean drawSmooth, float strokeWeight){
	public boolean generateDisplayLists(AbstractShape shape, boolean genFillList, boolean genStrokeList){
//		this.setDisplayListIDs(Tools3D.generateDisplayLists(
//				this.getRenderer(), fillDrawMode, this.getVertBuff(), this.getTexBuff(), 
//				this.getColorBuff(), this.getStrokeColBuff(), this.getIndexBuff(),
//				useTexture, texture, drawSmooth, strokeWeight));
		//TODO test - automaticall delete old display list before
		this.deleteDisplayLists();
		
//		this.setDisplayListIDs(Tools3D.generateDisplayLists(this.getRenderer(), shape.getFillDrawMode(), this, shape.isTextureEnabled(),  shape.getTexture(), shape.isDrawSmooth(), shape.getStrokeWeight()));
		
//		/*
		int[] displayListIDs = new int[]{-1,-1};

		//Create a new empty displaylist
//		GL gl = Tools3D.getGL(getRenderer());
		GL11Plus gl = PlatformUtil.getGL11Plus();
		int listIDFill = gl.glGenLists(1);
		if (listIDFill == 0){
			System.err.println("Failed to create fill display list");
			return false;
		}
		int listIDOutline = gl.glGenLists(1);
		if (listIDOutline == 0){
			System.err.println("Failed to create stroke display list");
			return false;
		}
		
		boolean noFillb4 = shape.isNoFill();
		boolean noStrokeb4 = shape.isNoStroke();
		boolean displayListUsageb4 = shape.isUseDisplayList();
		//TODO also vbo?
		shape.setUseDisplayList(false);
		
		if (genFillList){
			//Start recording display list
			gl.glNewList(listIDFill, GL11Plus.GL_COMPILE);
			shape.setNoFill(false);
			shape.setNoStroke(true);
			shape.drawPureGl(gl);
			shape.setNoFill(noFillb4);
			shape.setNoStroke(noStrokeb4);
			//End recording
			gl.glEndList();
			displayListIDs[0] = listIDFill;
		}
		
		if (genStrokeList){
		//Start recording display list
		gl.glNewList(listIDOutline, GL11Plus.GL_COMPILE);
		shape.setNoFill(true);
		shape.setNoStroke(false);
		shape.drawPureGl(gl);
		shape.setNoFill(noFillb4);
		shape.setNoStroke(noStrokeb4);
		//End recording
		gl.glEndList();
		displayListIDs[1] = listIDOutline;
		}

		//Set the new display list IDs
		setDisplayListIDs(displayListIDs);
		
		shape.setUseDisplayList(displayListUsageb4);
//		 */
		return true;
	}
	
	/**
	 * Delete the the displaylists of that geometry.
	 */
	public void deleteDisplayLists(){
		if (MT4jSettings.getInstance().isOpenGlMode()){
//			GL gl = Tools3D.getGL(this.r);
			GL11Plus gl11Plus = PlatformUtil.getGL11Plus();
			if (gl11Plus != null) {
				for (int id : this.displayListIDs){
					if (id != -1){
						gl11Plus.glDeleteLists(id, 1);
					}
				}
			}
			this.displayListIDs[0] = -1;
			this.displayListIDs[1] = -1;
		}
	}
	
	/**
	 * Returns the IDs (names) of the display lists if they have
	 * been generated! setUseDisplayList has to be called first!.
	 * 
	 * @return int[2] array where [0] is the list of the fill
	 * and [1] the list of the outline
	 */
	public int[] getDisplayListIDs() {
		return this.displayListIDs;
	}
	
	/**
	 * Sets the display lists for this shape.
	 * <br><strong>The int array has to be of length=2 and
	 * contain 2 display list ids, generated with <code>glGenlists</code></strong>
	 * 
	 * @param ids the ids
	 */
	public void setDisplayListIDs(int[] ids){
		this.displayListIDs = ids;
	}
	////////////////DISPLAY LISTS //////////////////////
	
	
	

	
	/**
	 * Returns the vertices of this shape without any transformations applied
	 * <br> <b>Caution:</b> If you alter them in anyway, changes will only
	 * be consistent by calling the setNewVertices() method!.
	 * 
	 * @return the untransformed vertices
	 */
	public Vertex[] getVertices(){
		return this.vertices;
	}
	
	
	
	
	//////////////// VERTEX COLORS  //////////////////////
	//Methods that set the vertex colors for all vertices
	
	//TODO change the vertex color AND the Color Buffers (+vbo) in the
	//same loop for speed!
	
	/**
	 * Sets the vertices color all.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public void setVerticesColorAll(float r, float g, float b, float a){
		for (Vertex vertex : this.getVertices()){
			vertex.setR(r);
			vertex.setG(g);
			vertex.setB(b);
			vertex.setA(a);
		}
		
		//Dont always create a new buffer -> update old one if possible
		if (this.getColorBuff() != null && this.getVertices().length == (this.getColorBuff().limit()/4)){
			ToolsBuffers.updateColorBuffer(this.getVertices(), this.getColorBuff());
//			System.out.println("UPDATE color buffer");
		}else{
			this.generateDefaultColorBuffer();
//			System.out.println("GENERATE color buffer");
		}
		
		if (this.getVBOColorName() != -1){
			this.updateColorVBO(this.getColorBuff());
		}
	}
	
	
	
	///// STROKE COLORS ////////////////////////
	/**
	 * Sets the stroke color all.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public void setStrokeColorAll(float r, float g, float b, float a){
		//Dont always create a new buffer -> update old one if possible
		if (this.getStrokeColBuff() != null && this.getVertices().length == (this.getStrokeColBuff().limit()/4)){
			ToolsBuffers.updateStrokeColorBuffer(this.getStrokeColBuff(), r, g, b, a);
//			System.out.println("UPDATE stroke color buffer");
		}else{
			this.generateDefaultStrokeColorBuffer(r,g,b,a);
//			System.out.println("GENERATE stroke color buffer");
		}
		
//		this.generateDefaultStrokeColorBuffer(r,g,b,a);
		
		if (this.getVBOStrokeColorName() != -1){
			this.updateStrokeColorVBO(this.getStrokeColBuff());
		}
	}
	
	/**
	 * Generates new color buffers for openGL use.
	 * <br>This has to be called after
	 * manually changing a vertex color without using a method like
	 * setFillColor(..) to take effect.
	 * <br>Only makes sense when using OPENGL!
	 * <br>Doesent update strokecolors!
	 */
	public void updateVerticesColorBuffer(){
		if (MT4jSettings.getInstance().isOpenGlMode()){
			//Dont always create a new buffer -> update old one if possible
			if (this.getColorBuff() != null && this.getVertices().length == (this.getColorBuff().limit()/4)){
				ToolsBuffers.updateColorBuffer(this.getVertices(), this.getColorBuff());
//				System.out.println("UPDATE color buffer");
			}else{
				this.generateDefaultColorBuffer();
//				System.out.println("GENERATE color buffer");
			}
			if (this.getVBOColorName() != -1){
				this.updateColorVBO(this.getColorBuff());
			}
		}
	}
	
	/**
	 * Generates new texture buffer for openGL use.
	 * <br>This has to be called after
	 * manually changing a vertex u,v texture coordinates in the vertex array.
	 * <br>Only makes sense when using OPENGL!
	 * 
	 * @param updateVBO the update vbo
	 */
	public void updateTextureBuffer(boolean updateVBO){
		if (MT4jSettings.getInstance().isOpenGlMode()){
			this.generateDefaultTextureBuffer();
			if (updateVBO && this.getVBOTextureName() != -1){
				this.updateTextureVBO(this.getTexBuff());
			}
		}
	}
	//////////////// VERTEX COLORS  //////////////////////

	
	/**
	 * Gets the vertex count.
	 * 
	 * @return the vertex count
	 */
	public int getVertexCount(){
		return this.vertices.length;
	}
	
	/**
	 * Gets the renderer.
	 * 
	 * @return the renderer
	 */
	public PApplet getRenderer(){
		return this.r;
	}

	/**
	 * Checks if is texture coords are normalized.
	 *
	 * @return true, if is texture coords normalized
	 */
	public boolean isTextureCoordsNormalized() {
		return this.textureCoordsNormalized;
	}
	
	/**
	 * Informs the geometryinfo that the texture coordinates are 
	 * supplied normalized (0..1). This method doesent normalize the
	 * tex coords itself.
	 *
	 * @param normalized the new texture coords normalized
	 */
	public void setTextureCoordsNormalized(boolean normalized){
		this.textureCoordsNormalized = normalized;
	}
	
	/**
	 * Checks if is texture coords are adapted to npot.
	 * (image data is actually smaller than the OpenGL texture object)
	 * @return true, if is texture coords are adapted npot
	 */
	public boolean isTextureCoordsAdaptedNPOT() {
		return this.adaptedCoordsNPOT;
	}
	
	/**
	 * Informs the geometryinfo that the texture coordinates are 
	 * adapted to NPOT texture dimensions (image data is actually smaller than the OpenGL texture object). 
	 * This method doesent adapt the tex coords itself!
	 *
	 * @param adaptedCoords adaptedCoords
	 */
	public void setTextureCoordsAdaptedNPOT(boolean adaptedCoords){
		this.adaptedCoordsNPOT = adaptedCoords;
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		//System.out.println("Finalizing GLTEXTURE - " + this);
		if (this.r instanceof AbstractMTApplication) {
			AbstractMTApplication mtApp = (AbstractMTApplication) this.r;
			mtApp.invokeLater(new Runnable() {
				public void run() {
					deleteDisplayLists();
					deleteAllVBOs();
				}
			});
		}else{
			//TODO use registerPre()?
			//is the object even valid after finalize() is called??
		}
		super.finalize();
	}
}
