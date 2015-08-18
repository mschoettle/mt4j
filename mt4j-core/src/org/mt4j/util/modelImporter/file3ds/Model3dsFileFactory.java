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
package org.mt4j.util.modelImporter.file3ds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import mri.v3ds.Face3ds;
import mri.v3ds.FaceMat3ds;
import mri.v3ds.Material3ds;
import mri.v3ds.Mesh3ds;
import mri.v3ds.Scene3ds;
import mri.v3ds.TexCoord3ds;
import mri.v3ds.TextDecode3ds;
import mri.v3ds.Vertex3ds;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.TriangleNormalGenerator;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.modelImporter.ModelImporterFactory;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.EXPANSION_FILTER;
import org.mt4j.util.opengl.GLTexture.SHRINKAGE_FILTER;
import org.mt4j.util.opengl.GLTexture.TEXTURE_TARGET;
import org.mt4j.util.opengl.GLTexture.WRAP_MODE;
import org.mt4j.util.opengl.GLTextureSettings;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * A factory for creating Model3dsFile objects.
 * @author Christopher Ruff
 */
public class Model3dsFileFactory extends ModelImporterFactory{
	private static final ILogger logger = MTLoggerFactory.getLogger(Model3dsFileFactory.class.getName());
	static{
		logger.setLevel(ILogger.ERROR);
	}
	
	private PApplet pa;
	private Map<String, PImage> textureCache ;
	
	/**
	 * Load model.
	 * 
	 * @param pa the pa
	 * @param pathToModel the path to model
	 * @param creaseAngle the crease angle
	 * @param flipTextureY flip texture y
	 * @param flipTextureX flip texture x
	 * 
	 * @return the MT triangle meshes[]
	 * 
	 * @throws FileNotFoundException the file not found exception
	 */
	public MTTriangleMesh[] loadModelImpl(PApplet pa, String pathToModel, float creaseAngle, boolean flipTextureY, boolean flipTextureX) throws FileNotFoundException{
		long timeA = System.currentTimeMillis();
		this.pa = pa;
		
		ArrayList<MTTriangleMesh> returnMeshList = new ArrayList<MTTriangleMesh>();
		
		TriangleNormalGenerator normalGenerator = new TriangleNormalGenerator();
//		normalGenerator.setDebug(debug); 
		
		HashMap<Integer, Group> materialIdToGroup = new HashMap<Integer, Group>();
		
		//TODO implement
		if (textureCache != null)
			textureCache.clear();
		textureCache = new WeakHashMap<String, PImage>();
		
		Scene3ds scene = null;
		
		try{
			TextDecode3ds decode = new TextDecode3ds();
//			int level = Scene3ds.DECODE_USED_PARAMS_AND_CHUNKS; //Scene3ds.DECODE_ALL; DECODE_USED_PARAMS, DECODE_USED_PARAMS_AND_CHUNKS
			int level = Scene3ds.DECODE_ALL;

			//LOAD all meshes from file into scene object
			File file = new File(pathToModel);
			if (file.exists()){
				scene = new Scene3ds(file, decode, level );
			}else{
				InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathToModel);
				if (in == null){
					in = pa.getClass().getResourceAsStream(pathToModel);
				}
				if (in != null){
					scene = new Scene3ds(in, decode, level );
				}else{
					throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
				}
			}
				
			
			if (debug)
				logger.debug("\n-> Loading model: " + file.getName() + " <-");
			

			if (debug){
				//Go through all MATERIALS
				logger.debug("\nNum Scene Materials: " + scene.materials() );
				for( int m=0; m < scene.materials(); m++ ){
					Material3ds mat = scene.material( m );
					logger.debug("  Material " + m + ": \" " + mat.name() + "\"");
				}
				logger.debug("");
			}


			///////// Go through all MESHES //////////////////////////
			for( int i=0; i<scene.meshes(); i++ ) {
				Mesh3ds m = scene.mesh( i );
				
				if (debug){
					int texMapType = m.texMapType();
					logger.debug("Texture coordinates provided: " + m.texCoords());
					logger.debug("Texture mapping type: " + texMapType);
					logger.debug("Mesh:" + m.name() + " Pivot:" + m.pivot());
				}
				
				/*
				XYZTrack3ds pos 		= m.positionTrack();
				RotationTrack3ds rot 	= m.rotationTrack();
				XYZTrack3ds sc 			= m.scaleTrack();
				//FIXME .track and key(i) werden nicht zur verfügung gestellt?!? aber in javadoc
				*/
				
				if (debug){
					logger.debug("->Processing mesh: " 	+ i + " of " + scene.meshes() + " Name: \"" + m.name() + "\"");
					logger.debug("  Num Faces: " 			+ m.faces());
					logger.debug("  Num Vertices: " 		+ m.vertices());
					logger.debug("  Num TextureCoordinates: " + m.texCoords());
					logger.debug("");
				}
				
				//Create the arrays needed for the cruncher
				Vertex[] vertices 		= new Vertex[m.vertices()];
				short[] indices 		= new short[m.faces()*3];
				
				int[] texCoordIndices 	= new int[m.faces()*3];
				float[][] textureCoords = new float[m.texCoords()][2];
				
				//Fill Vertices array 
				for (int j = 0; j < m.vertices(); j++) {
					Vertex3ds vert = m.vertex(j);
					
					if (this.flipY){
						vertices[j] = new Vertex(vert.X, -vert.Y, vert.Z, -1,-1);
					}else{
						vertices[j] = new Vertex(vert.X, vert.Y, vert.Z, -1,-1);
					}
					
					if (m.texCoords()> j)
						textureCoords[j] = new float[]{m.texCoord(j).U, m.texCoord(j).V };
				}
				
				//Fill texcoords array
				for (int j = 0; j < m.texCoords(); j++) {
					TexCoord3ds tex =  m.texCoord(j);
					
					float[] texCoord = new float[2];
					texCoord[0] = tex.U;
					texCoord[1] = tex.V;
					
					textureCoords[j] = texCoord;
				}
				
				//TODO so werden gleiche materials in verschiedenen meshes nicht zu einem mesh gemacht!
				//also müsste also ohne clear machen und dafür vertices + texcoords in einen grossen
				//array, dafür müssten indices aber per offset angepasst werden dass die wieder stimmen!
				materialIdToGroup.clear();
				
				if (m.faceMats() > 0){
					//List face Materials  //TODO USE LOGGERS!!
					logger.debug("  Num Face-Materials: " + m.faceMats() );
					for( int n = 0; n < m.faceMats(); n++ ){
						FaceMat3ds fmat = m.faceMat( n );
						logger.debug("    FaceMat ID: " 		+ fmat.matIndex() );
						logger.debug("    FaceMat indices: " 	+ fmat.faceIndexes());
						
						int[] faceIndicesForMaterial = fmat.faceIndex();
						if (faceIndicesForMaterial.length <= 0){
							continue;
						}

						//Check if there already is a group with the same material
						Group group = materialIdToGroup.get(fmat.matIndex());

						//If not, create a new group and save it in map
						if (group == null){
							group = new Group(Integer.toString(fmat.matIndex()));
							materialIdToGroup.put(fmat.matIndex(), group);
						}

						//Go through all pointers to the faces for this material 
						//and get the corresponding face
                        for (int k : faceIndicesForMaterial) {
                            Face3ds face = m.face(k);

                            AFace aFace = new AFace();
                            aFace.p0 = face.P0;
                            aFace.p1 = face.P1;
                            aFace.p2 = face.P2;

                            aFace.t0 = face.P0;
                            aFace.t1 = face.P1;
                            aFace.t2 = face.P2;

                            group.addFace(aFace);
                        }
					}

					Iterator<Integer> it = materialIdToGroup.keySet().iterator();
					logger.debug("Mesh: " + m.name() + " Anzahl Groups:" + materialIdToGroup.keySet().size());
					while (it.hasNext()) {
						int currentGroupName =  it.next();
						Group currentGroup = materialIdToGroup.get(currentGroupName);
						logger.debug("Current group: " + currentGroupName);

						currentGroup.compileItsOwnLists(vertices, textureCoords);

						//Get the new arrays 
						Vertex[] newVertices 		= currentGroup.getGroupVertices(); 
						short[] newIndices 			= currentGroup.getIndexArray(); 
						float[][] newTextureCoords  = currentGroup.getGroupTexCoords();
						int[] newTexIndices 		= currentGroup.getTexCoordIndices();

						logger.debug("\nGroup: \"" + currentGroup.name + "\" ->Vertices: " + currentGroup.verticesForGroup.size()+ " ->TextureCoords: " + currentGroup.texCoordsForGroup.size() + " ->Indices: " + currentGroup.indexArray.length + " ->Texcoord Indices: " + currentGroup.texCoordIndexArray.length );
						logger.debug("");

						if (vertices.length > 2){
							GeometryInfo geometry  = null;
							//Load as all vertex normals smoothed if creaseAngle == 180;
							if (creaseAngle == 180){
								geometry = normalGenerator.generateSmoothNormals(pa, newVertices , newIndices, newTextureCoords, newTexIndices, creaseAngle, flipTextureY, flipTextureX);
							}else{
								geometry = normalGenerator.generateCreaseAngleNormals(pa, newVertices, newIndices, newTextureCoords, newTexIndices, creaseAngle, flipTextureY, flipTextureX);
							}

							MTTriangleMesh mesh = new MTTriangleMesh(pa, geometry);

							if (mesh != null){
								mesh.setName(m.name() + " material: " + Integer.toString(currentGroupName));
								//Assign texture
								this.assignMaterial(pathToModel, file, scene, m, currentGroupName, mesh);

								if (mesh.getTexture() != null){
									mesh.setTextureEnabled(true);
								}else{
									logger.debug("No texture could be assigned to mesh.");
								}
								returnMeshList.add(mesh);
							}
						}
					}
				}else{
					//If there are no materials for this mesh dont split mesh into
					//groups by material
					//Fill indices array and texcoords array (Here: vertex index = texcoord index)
					for( int faceIndex = 0; faceIndex < m.faces(); faceIndex++ ){
						Face3ds f = m.face( faceIndex );

						indices[faceIndex*3] 	= (short) f.P0;
						indices[faceIndex*3+1] 	= (short) f.P1;
						indices[faceIndex*3+2] 	= (short) f.P2;

						texCoordIndices[faceIndex*3] 	= f.P0;
						texCoordIndices[faceIndex*3+1] 	= f.P1;
						texCoordIndices[faceIndex*3+2] 	= f.P2;
					}//for faces

					//Create the Mesh and set a texture
					if (vertices.length > 2){
						//Create normals for the mesh and duplicate vertices for faces that share the same 
						//Vertex, but with different texture coordinates or different normals
						GeometryInfo geometry = null;
						//Generate normals and denormalize vertices with more than 1 texture coordinate
						if (creaseAngle == 180){
							geometry = normalGenerator.generateSmoothNormals(pa, vertices, indices, textureCoords, texCoordIndices, creaseAngle, flipTextureY, flipTextureX);
						}else{
							geometry = normalGenerator.generateCreaseAngleNormals(pa, vertices, indices, textureCoords, texCoordIndices, creaseAngle, flipTextureY, flipTextureX);
						}
						MTTriangleMesh mesh = new MTTriangleMesh(pa, geometry);
						mesh.setName(m.name());
//						this.assignMaterial(file, scene, m, sceneMaterialID, mesh);
						returnMeshList.add(mesh);
					}//end if vertices.lentgh > 2
				}
			}//for meshes
			
//			logger.debug(decode.text()); 
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		materialIdToGroup.clear();
		
		long timeB = System.currentTimeMillis();
		long delta = timeB-timeA;
		logger.debug("Loaded model in: " + delta + " ms");
		return returnMeshList.toArray(new MTTriangleMesh[returnMeshList.size()]);
	}
			
			
	/**
	 * Assigns the texture.
	 * @param pathToModel 
	 * @param modelFile
	 * @param scene
	 * @param m
	 * @param sceneMaterialID
	 * @param mesh
	 */		
	private void assignMaterial(String pathToModel, File modelFile, Scene3ds scene, Mesh3ds m, int sceneMaterialID, MTTriangleMesh mesh){
		if (scene.materials() > 0){
			if (m.faceMats() > 0){
				//Just take the first material in the mesh, it could have more but we dont support more than 1 material for a mesh
				// materialIndexForMesh = m.faceMat(0).matIndex();

				Material3ds mat = scene.material(sceneMaterialID);
				String materialName = mat.name();
				if (debug)
					logger.debug("Material name for mesh \"" + mesh.getName() + ":-> \"" + materialName + "\"");
				materialName = materialName.trim();
				materialName = materialName.toLowerCase();

				//Try to load texture
				try {
					PImage cachedImage = textureCache.get(materialName);
					if (cachedImage != null){
						mesh.setTexture(cachedImage);
						mesh.setTextureEnabled(true);
						if (debug)
							logger.debug("->Loaded texture from CACHE : \"" + materialName + "\"");
						return;
					}
					
					if (modelFile.exists()){ //If model is loaded from local file system
						String modelFolder  = modelFile.getParent();// pathToModel.substring(), pathToModel.lastIndexOf(File.pathSeparator)
						File modelFolderFile = new File (modelFolder);
						if (modelFolderFile.exists() &&  modelFolderFile.isDirectory())
							modelFolder = modelFolderFile.getAbsolutePath();
						else{
							modelFolder = "";
						}

						String[] suffix = new String[]{"jpg", "JPG", "tga" , "TGA", "bmp", "BMP", "png", "PNG", "tiff", "TIFF"};
						for (int j = 0; j < suffix.length; j++) {
							String suffixString = suffix[j];
							//Try to load and set texture to mesh
							String texturePath 	= modelFolder + AbstractMTApplication.separator + materialName + "." +  suffixString;
							File textureFile = new File(texturePath);
							if (textureFile.exists()){
								boolean success = textureFile.renameTo(new File(texturePath));
								if (!success) {
									// File was not successfully renamed
									logger.debug("failed to RENAME file: " + textureFile.getAbsolutePath());
								}
								PImage texture = null;
								if (MT4jSettings.getInstance().isOpenGlMode()){ //TODO check if render thread
									PImage img = pa.loadImage(texturePath);
									if (Tools3D.isPowerOfTwoDimension(img)){
										texture = new GLTexture(pa, img, new GLTextureSettings(TEXTURE_TARGET.TEXTURE_2D, SHRINKAGE_FILTER.Trilinear, EXPANSION_FILTER.Bilinear, WRAP_MODE.REPEAT, WRAP_MODE.REPEAT));
									}else{
										texture = new GLTexture(pa, img, new GLTextureSettings(TEXTURE_TARGET.RECTANGULAR, SHRINKAGE_FILTER.Trilinear, EXPANSION_FILTER.Bilinear, WRAP_MODE.REPEAT, WRAP_MODE.REPEAT));
										// ((GLTexture)texture).setFilter(SHRINKAGE_FILTER.BilinearNoMipMaps, EXPANSION_FILTER.Bilinear);
									}
								}else{
									texture 		= pa.loadImage(texturePath);
								}
								mesh.setTexture(texture);
								mesh.setTextureEnabled(true);

								textureCache.put(materialName, texture);
								if (debug)
									logger.debug("->Loaded material texture: \"" + materialName + "\"");
								break;
							}
							if (j+1==suffix.length){
								logger.error("Couldnt load material texture: \"" + materialName + "\"");
							}
						}
					}else{//Probably loading from jar file
						PImage texture = null;
						String[] suffix = new String[]{"jpg", "JPG", "tga" , "TGA", "bmp", "BMP", "png", "PNG", "tiff", "TIFF"};
						for (String suffixString : suffix) {
							String modelFolder  = pathToModel.substring(0, pathToModel.lastIndexOf(AbstractMTApplication.separator));
							String texturePath 	= modelFolder + AbstractMTApplication.separator + materialName + "." +  suffixString;
							if (MT4jSettings.getInstance().isOpenGlMode()){
								PImage img = pa.loadImage(texturePath);
								if (Tools3D.isPowerOfTwoDimension(img)){
									texture = new GLTexture(pa, img, new GLTextureSettings(TEXTURE_TARGET.TEXTURE_2D, SHRINKAGE_FILTER.Trilinear, EXPANSION_FILTER.Bilinear, WRAP_MODE.REPEAT, WRAP_MODE.REPEAT));
								}else{
									texture = new GLTexture(pa, img, new GLTextureSettings(TEXTURE_TARGET.RECTANGULAR, SHRINKAGE_FILTER.Trilinear, EXPANSION_FILTER.Bilinear, WRAP_MODE.REPEAT, WRAP_MODE.REPEAT));
									// ((GLTexture)texture).setFilter(SHRINKAGE_FILTER.BilinearNoMipMaps, EXPANSION_FILTER.Bilinear);
								}
							}else{
								texture = pa.loadImage(texturePath);
							}
							mesh.setTexture(texture);
							mesh.setTextureEnabled(true);

							textureCache.put(materialName, texture);
							if (debug)
								logger.debug("->Loaded material texture: \"" + materialName + "\"");
							break;
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}//if (m.faceMats() > 0)
		}//if (scene.materials() > 0)
	}
	
	
	
	private boolean debug = true;
	private boolean flipY = true;


	public void setDebug(boolean debug) {
		this.debug = debug;
		if (debug)
			logger.setLevel(ILogger.DEBUG);
		else
			logger.setLevel(ILogger.ERROR);
	}
	
	public void setFlipY(boolean flipY){
		this.flipY  = flipY;
	}
	

	
	/**
	 * A class representing one group in a .3ds file
	 * @author C.Ruff
	 *
	 */
	private class Group{
		private String name;
		private ArrayList<AFace> faces;
		private ArrayList<Vertex> verticesForGroup;
		
		private HashMap<Integer, Integer> oldIndexToNewIndex;
		
		private ArrayList<float[]> texCoordsForGroup;
		private HashMap<Integer, Integer> oldTexIndexToNewTexIndex;
		
		private short[] indexArray;
		
		private int[] texCoordIndexArray;
		
		public Group(String name){
			this.name = name;
			
			faces 						= new ArrayList<AFace>();
			
			verticesForGroup 			= new ArrayList<Vertex>();
			oldIndexToNewIndex 			= new HashMap<Integer, Integer>();
			
			texCoordsForGroup 			= new ArrayList<float[]>();
			oldTexIndexToNewTexIndex 	= new HashMap<Integer, Integer>();
			
			indexArray 			= new short[0];
			texCoordIndexArray 	= new int[0];
			
			//name = "default";
		}
		
		public void addFace(AFace face){
			faces.add(face);
		}

		//Irgendwann am ende ausführen
		//=>für jede gruppe eigene liste von vertices on indices speichern
		//aus hauptlisten rausholen
		
		/**
		 * Uses the faces attached to this group during the parsing process and the lists with
		 * all vertex and all texture coordinates of the obj file to create arrays for this group
		 * with vertices and texture coords that only belong to this single group.
		 * 
		 * @param allFileVerts
		 * @param allTexCoords
		 */
		public void compileItsOwnLists(Vertex[] allFileVerts, float[][] allTexCoords){
			indexArray = new short[faces.size()*3];
			
			if (allTexCoords.length > 0){
				texCoordIndexArray = new int[faces.size()*3];
			}
			
			for (int i = 0; i < faces.size(); i++) {
				AFace currentFace = faces.get(i);
				
				Vertex v0 = allFileVerts[currentFace.p0];
				Vertex v1 = allFileVerts[currentFace.p1];
				Vertex v2 = allFileVerts[currentFace.p2];
				
				if (	allTexCoords.length > currentFace.t0
					&& 	allTexCoords.length > currentFace.t1
					&& 	allTexCoords.length > currentFace.t2
				){
					float[] texV0 = allTexCoords[currentFace.t0];
					float[] texV1 = allTexCoords[currentFace.t1];
					float[] texV2 = allTexCoords[currentFace.t2];
					
					//Etwas redundant immer wieder zu machen beim gleichen vertex..whatever
					v0.setTexCoordU(texV0[0]);
					v0.setTexCoordV(texV0[1]);
					
					v1.setTexCoordU(texV1[0]);
					v1.setTexCoordV(texV1[1]);
					
					v2.setTexCoordU(texV2[0]);
					v2.setTexCoordV(texV2[1]);
					
					//Check if there is a texture index in the hashtable at the faces texture pointer
					//if not, create a new index = the end of thexcoords list, and put the pointer into the hash
					//if yes, point the faces texture pointer to the pointer in the hash
					
					//This process maps the texture coords and indices of all the groups in the obj
					//file to only this groups texture coord list and texture indices, the indices 
					//are created from the index in the thex coord list when they are put in
					//Only the texture coordinates are added to the list that have not been adressed
					//in the texture indices pointers in the faces
					//Same texture pointers will point to the same texcoord in the list
					Integer oldToNewT0 = oldTexIndexToNewTexIndex.get(currentFace.t0);
					if (oldToNewT0 != null){
						currentFace.t0 = oldToNewT0;
					}else{
						int newIndex = texCoordsForGroup.size();
						texCoordsForGroup.add(texV0);
						oldTexIndexToNewTexIndex.put(currentFace.t0, newIndex);
						currentFace.t0 = newIndex;
					}
					
					Integer oldToNewT1 = oldTexIndexToNewTexIndex.get(currentFace.t1);
					if (oldToNewT1 != null){
						currentFace.t1 = oldToNewT1;
					}else{
						int newIndex = texCoordsForGroup.size();
						texCoordsForGroup.add(texV1);
						oldTexIndexToNewTexIndex.put(currentFace.t1, newIndex);
						currentFace.t1 = newIndex;
					}
					
					Integer oldToNewT2 = oldTexIndexToNewTexIndex.get(currentFace.t2);
					if (oldToNewT2 != null){
						currentFace.t2 = oldToNewT2;
					}else{
						int newIndex = texCoordsForGroup.size();
						texCoordsForGroup.add(texV2);
						oldTexIndexToNewTexIndex.put(currentFace.t2, newIndex);
						currentFace.t2 = newIndex;
					}
				}
				
				//Do the same for the vertices.
				//Create a new vertex pointer when adding the vertex to the list
				Integer oldToNewP0 = oldIndexToNewIndex.get(currentFace.p0);
				if (oldToNewP0 != null){
					//index of the old vertex list has already been mapped to a new one here -> use the new index in the face
					currentFace.p0 = oldToNewP0;
				}else{
					int newIndex = verticesForGroup.size();
					verticesForGroup.add(v0);
					//mark that the former index (for exmample 323) is now at new index (f.e. 1)
					oldIndexToNewIndex.put(currentFace.p0, newIndex); 
					currentFace.p0 = newIndex;
				}
				
				Integer oldToNewP1 = oldIndexToNewIndex.get(currentFace.p1);
				if (oldToNewP1 != null){
					currentFace.p1 = oldToNewP1;
				}else{
					int newIndex = verticesForGroup.size();
					verticesForGroup.add(v1);
					oldIndexToNewIndex.put(currentFace.p1, newIndex);
					currentFace.p1 = newIndex;
				}
				
				Integer oldToNewP2 = oldIndexToNewIndex.get(currentFace.p2);
				if (oldToNewP2 != null){
					currentFace.p2 = oldToNewP2;
				}else{
					int newIndex = verticesForGroup.size();
					verticesForGroup.add(v2);
					oldIndexToNewIndex.put(currentFace.p2, newIndex);
					currentFace.p2 = newIndex;
				}
				
				indexArray[i*3]   = (short) currentFace.p0;
				indexArray[i*3+1] = (short) currentFace.p1;
				indexArray[i*3+2] = (short) currentFace.p2;
				
				if (allTexCoords.length > 0){
					texCoordIndexArray[i*3]   = currentFace.t0;
					texCoordIndexArray[i*3+1] = currentFace.t1;
					texCoordIndexArray[i*3+2] = currentFace.t2;
				}
			}
		}

		public short[] getIndexArray() {
			return indexArray;
		}

		public String getName() {
			return name;
		}

		public int[] getTexCoordIndices() {
			return texCoordIndexArray;
		}

		public float[][] getGroupTexCoords() {
			return texCoordsForGroup.toArray(new float[this.texCoordsForGroup.size()][]);
		}

		public Vertex[] getGroupVertices() {
			return verticesForGroup.toArray(new Vertex[this.verticesForGroup.size()]);
		}
	}
	
	/**
	 * A class representing a face in the obj file.
	 * Has pointers into the vertex and the texture arrays.
	 * 
	 * @author C.Ruff
	 *
	 */
	private class AFace{
		int p0;
		int p1;
		int p2;
		
		int t0;
		int t1;
		int t2;
		
		public AFace(){
			p0 = -1;
			p1 = -1;
			p2 = -1;
			
			t0 = 0;
			t1 = 0;
			t2 = 0;
		}
	}
	
	
}
