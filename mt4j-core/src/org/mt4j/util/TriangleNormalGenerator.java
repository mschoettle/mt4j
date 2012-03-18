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

import java.util.ArrayList;

import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

/**
 * This class can be used to generate normals for indexed triangles geometry.
 * <br><li>It duplicates a vertex that has more than 1 texture coordinate.
 * So texture with indexed geometry can still be used.
 * <br><li>It also duplicates equal vertices that get assigned different normals in the calculation process according
 * to the face they belong to.
 * <p>
 * 
 * @author C.Ruff
 */
public class TriangleNormalGenerator {
	
	/** The Constant logger. */
	private static final ILogger logger = MTLoggerFactory.getLogger(TriangleNormalGenerator.class.getName());
	static{
		logger.setLevel(ILogger.ERROR);
	}
	
	/** The null vect. */
	private Vertex nullVect;
	
	/** Use these for console debug info. */
//	private boolean debug;

	/** The use normals equal to face. */
	private boolean useNormalsEqualToFace;

	/** The use equal neighbor normals again. */
	private boolean useEqualNeighborNormalsAgain;
	
 
	/**
	 * Constructor.
	 */
	public TriangleNormalGenerator(){
		nullVect = new Vertex(0,0,0, -1, -1);
		useNormalsEqualToFace 			= true;
		useEqualNeighborNormalsAgain 	= true;
	}
	
	
	/**
	 * Generates smooth normals for a triangle geometry array.
	 * 
	 * @param pa the pa
	 * @param geometryInfo the geometry info
	 * 
	 * @return the geometry info
	 * 
	 * the geometry array with normals
	 */
	public GeometryInfo generateTriangleNormals(PApplet pa, GeometryInfo geometryInfo){
		return this.generateTriangleNormals(pa, geometryInfo, 180);
	}
	
	
	/**
	 * Generates normals for the provided geometry info according to the crease angle.
	 * <br>A crease angle of 180 will result in a all smoothed model, smoothing each vertex normal
	 * across all its neighbor faces' face normals.
	 * <br>A crease angle of zero (0) will result in a flat shaded geometry. Only face normals will
	 * be used then.
	 * <br>A crease angle of 89 will create hard edges at 90 degree angle faces and smooth faces with
	 * less then 90 degree normals. This would be ideal to generate normals for a cube.
	 * <br>The best crease angle for a model has to be found by testing different angles.
	 * 
	 * <br><strong>Note:</strong>The geometry has to represent a TRIANGLES array!
	 * <br><strong>Note:</strong>The stroke color information gets lost during the process and is reset
	 * to the default color. Set it again if needed with <code>setStrokeColorAll</code>.
	 * 
	 * @param pa the pa
	 * @param geometryInfo the geometry info
	 * @param creaseAngle the crease angle
	 * 
	 * @return the geometry info
	 * 
	 * the geometry array with normals
	 */
	public GeometryInfo generateTriangleNormals(PApplet pa, GeometryInfo geometryInfo, float creaseAngle ){
		Vertex[] vertices = geometryInfo.getVertices();
		
		//Gen texcoord array
		float[][] texCoords = new float[vertices.length][2];
		for (int i = 0; i < vertices.length; i++) {
			Vertex v = vertices[i];
			texCoords[i][0] = v.getTexCoordU();
			texCoords[i][1] = v.getTexCoordV();
		}
		
		//Gen or get indices array
		short[] indices = null;
		if (!geometryInfo.isIndexed()){
			indices = new short[vertices.length];
			for (short i = 0; i < vertices.length; i++) {
				indices[i] = i;
			}
		}else{
			indices = geometryInfo.getIndices();
		}
		
		//Gen texcoord array as same as indices array
		int[] texIndices = new int[indices.length];
        System.arraycopy(indices, 0, texIndices, 0, indices.length);
        //		for (int i = 0; i < indices.length; i++) {
		//	texIndices[i] = indices[i];
		//}

		//Generate normals
		GeometryInfo geom = null;
		if (creaseAngle == 180){
			geom = this.generateSmoothNormals(pa, vertices, indices, texCoords, texIndices, creaseAngle, false, false);
		}else{
			geom = this.generateCreaseAngleNormals(pa, vertices, indices, texCoords, texIndices, creaseAngle, false, false);
		}
		
		//Reset indices if they werent set before,too
		//Reconstruct the passed in geometryinfo
		if (!geometryInfo.isIndexed()){
			geometryInfo.reconstruct(geom.getVertices(), geom.getNormals(), null, true, false, null);
		}else{
			geometryInfo.reconstruct(geom.getVertices(), geom.getNormals(), geom.getIndices(), true, false, null);
		}
		geom = null;
		return geometryInfo;
	}

	
	
	
	/**
	 * Generates triangle normals, smoothed acroos all neighbor faces.
	 * <br>Also dissolves multiple texturecoordinates belonging to the same vertex by
	 * duplicating them.
	 * 
	 * @param pa the pa
	 * @param originalVertices the original vertices
	 * @param originalIndices the original indices
	 * @param originalTexCoords the original tex coords
	 * @param originalTexIndices the original tex indices
	 * @param creaseAngle the crease angle
	 * @param flipTextureY the flip texture y
	 * @param flipTextureX the flip texture x
	 * 
	 * @return the geometry info
	 */
	public GeometryInfo generateSmoothNormals(PApplet pa, Vertex[] originalVertices, short[] originalIndices, float[][] originalTexCoords, int[] originalTexIndices, float creaseAngle, boolean flipTextureY, boolean flipTextureX){
			int newDuplicatesWithDiffTexCoordsCreated 	= 0;
			int newDuplicatesWithDiffNormalCreated 		= 0;
			
			logger.debug("-> Loading all smoothed model.");
			
			ArrayList<VertexData> 	vertexDatas = new ArrayList<VertexData>(originalVertices.length);
			ArrayList<MyFace> 		faces 		= new ArrayList<MyFace>(Math.round(originalIndices.length/3));
			
			//Init and fill vertexdata list with as many as vertices
			for (int i = 0; i < originalVertices.length; i++) {
				//Vertex v = vertices[i];
				VertexData vd = new VertexData();
				vd.setArrayIndex(i);
				vertexDatas.add(vd);
			}
			
			
			int pp0 = 0;
			int pp1 = 0;
			int pp2 = 0;
			
			int tIndexPP0 = 0;
			int tIndexPP1 = 0;
			int tIndexPP2 = 0;
			//GO through indices array and create a face for every three indices (must be triangle array!) 
			for (int i = 0; i < originalIndices.length/3; i++) {
				//int currentIndex = indices[i];
				
				//Next 3 vertex indices as the new faces pointers
				pp0 = originalIndices[i*3];
				pp1 = originalIndices[i*3+1];
				pp2 = originalIndices[i*3+2];
				
				if (originalTexCoords.length > 0){
					//Next 3 texture texture indices //(vertex and texture indices indexed in the same order)
					tIndexPP0 = originalTexIndices[i*3];
					tIndexPP1 = originalTexIndices[i*3+1];
					tIndexPP2 = originalTexIndices[i*3+2];
				}
				
				
				MyFace myFace = new MyFace();
				myFace.p0 = pp0;
				myFace.p1 = pp1;
				myFace.p2 = pp2;
				
				int vertexPointer = pp0;
				int texturePointer = tIndexPP0;
				for (int j = 0; j < 3; j++) {
					if 	   (j == 0)	{
						vertexPointer 	= pp0;
						texturePointer  = tIndexPP0;
					}
					else if(j == 1){
						vertexPointer 	= pp1;
						texturePointer  = tIndexPP1;
					}
					else if(j == 2){
						vertexPointer 	= pp2;
						texturePointer  = tIndexPP2;
					}
					
					//Get the vertexdata at the index, the face points to
					VertexData myVertexData 	= vertexDatas.get(vertexPointer);
					
					Vertex newVertex = new Vertex(
							originalVertices[vertexPointer].x, 
							originalVertices[vertexPointer].y, 
							originalVertices[vertexPointer].z,
							originalVertices[vertexPointer].getR(),
							originalVertices[vertexPointer].getG(),
							originalVertices[vertexPointer].getB(),
							originalVertices[vertexPointer].getA()
					);
					
	
//					Create texcoords and add to vertex
					float[] tex = new float[2];
					if (originalTexCoords.length > 0){
						tex[0] = originalTexCoords[texturePointer][0];
						tex[1] = originalTexCoords[texturePointer][1];
						
						if (flipTextureY){
							tex[1] = 1.0f-tex[1];
						}
						
						if (flipTextureX){
							tex[0] = 1.0f-tex[0];
						}
						
						newVertex.setTexCoordU(tex[0]);
						newVertex.setTexCoordV(tex[1]);
					}

					//Check if the vertex data at the pointer is empty thats the case before first time a vertex is added to it here
					if (myVertexData.getVertex() == null){
						myVertexData.setVertex(newVertex);
						myVertexData.addNeighborFace(myFace);
						logger.debug("vdP" + j + " vertex in vertexData not initialzied -> set it");
//						logger.debug("vdP0 is empty -> adding first vertex, texture tuple");
					}else{//Vertex data at index already contains one or more vertices 
						
						//Check if the vertex data at the index contains a vertex that is equal to the new vertex and its texture information 
						//-> if true, just add the vertex to that vertexdata and the face it also is in
						//-> if false, check if a duplicate vertexdata is registered in the vertexdata the face points to
						//		-> if true, add the vertex to the duplicate, change face index to the duplicates index
						//		-> if false, create new vertexdata at end of list, add the vertex and texcoords of the vertex, and register 
						//			it as a duplicate in the original vertexdata, change the face index to the new duplicates index
						if (myVertexData.equalsVertex(newVertex)){
							//Register das face mit dem vertex data
							myVertexData.addNeighborFace(myFace);
							logger.debug("vdP" + j + "already CONTAINS a vertex with same coords and texture information -> do nothing, just a the current face to its neighborlist");
						}else{
							
							//Check if already duplicates were created, maybe with the same vertex and texture information,
							//then we have to add the vertex to that vertexdata rather than creating a new one
							int duplicateIndex = myVertexData.getVertDuplicateSameVertDiffTextureCoordListIndex(newVertex);
							
							if (duplicateIndex != -1){ //Es gibt schon ein duplicate mit gleichen tex coords wie dieses hier, adde bei duplicateIndex
								//Change face pointer of p0 to the duplicates index
								if 	   (j == 0)	myFace.p0 = duplicateIndex;
								else if(j == 1) myFace.p1 = duplicateIndex;
								else if(j == 2)	myFace.p2 = duplicateIndex;
								
								//wenn wir orginal hinzuf�gen wird auch allen duplicates neighbor face zugef�gt.
								myVertexData.addNeighborFace(myFace);
								
								logger.debug("vdP" + j + "has different texture coordiantes but a already created duplicate has the same -> change this face pointer to the duplicate one");
							}else{//there was no duplicate created with the same texture coords yet -> create one!
								
								//Neuen Vertex machen, an original vertices list anh�ngen, neuen index in face auf diesen setzen, face adden
								VertexData newVertexData = new VertexData();
								
								//Add the vertex information to the newly created vertexdata
								newVertexData.setVertex(newVertex);
								
								//Get new index at end of vertex list
								int newIndexOfNewVertexData = vertexDatas.size();
								
								//Change the index of the face to the index the new Vdata is created at
								if 	   (j == 0)	myFace.p0 = newIndexOfNewVertexData;
								else if(j == 1) myFace.p1 = newIndexOfNewVertexData;
								else if(j == 2)	myFace.p2 = newIndexOfNewVertexData;
								
								//Tell the vertexdata the index it is in the overall data
								newVertexData.setArrayIndex(newIndexOfNewVertexData);
								
								//Add new vertex data at the end of the list of all vertexdatas
								vertexDatas.add(newVertexData);
								
								//Inform the original vertexdata, that a duplicate with diff tex coords was created and register it
								myVertexData.registerCreatedDuplicateDiffTexCoords(newVertexData);
								
								//Adde face zu orginal face -> damit wird auch duplicates und dem neuen hinzugef�gt,
								//wenn wir es vorher mit registerDuplicate am orginal registiert haben!
								myVertexData.addNeighborFace(myFace);
								
								//copy the face list the vertex is contained in of the original 
								//to the duplicate (for normal generation later, so they are still neighbors)! 
								newVertexData.addNeighborFaces(myVertexData.getFacesContainedIn());
								logger.debug("vdP" + j + "isnt empty but DOESENT CONTAIN (also no duplicate contains) a vertex with same coords and texture information -> creating new V.D. at: " + newIndexOfNewVertexData);
								newDuplicatesWithDiffTexCoordsCreated++;
							}//end if duplicate exists
						}//end if vertexdata already contains Vnew
					}//end if vertexdata is empty
				}//end for p0,p1,p2
				
				//Calculate the face's normal vector
				myFace.calcFaceNormal(vertexDatas);
				
				myFace.index = faces.size(); 
				
				//Add face to facelist
				faces.add(myFace);
			}
			
			logger.debug("-> Processed duplicate vertex/texture points.");
			
			
			//Create arrays
			Vertex[] newVertices 	= new Vertex[vertexDatas.size()];
			Vector3D[] normals  	= new Vector3D[vertexDatas.size()];
			short[] newIndices		= new short[faces.size()*3];
			
			
			/*
			 * Go through the final faces list and fill vertex/newIndices/normal arrays
			 */
			for (int j = 0; j < faces.size(); j++) {
				MyFace myFace = faces.get(j);
				//Get vertex pointers of face to newVertices in newVertices list
				int indexP0 = myFace.p0;
				int indexP1 = myFace.p1;
				int indexP2 = myFace.p2;
				
				//Use pointers as newIndices and fill newIndices array
				newIndices[j*3]		= (short) indexP0;
				newIndices[j*3+1]	= (short) indexP1;
				newIndices[j*3+2]	= (short) indexP2;
				
				//Get the vertexdatas out of the list with the pointers
				VertexData vdP0 = vertexDatas.get(indexP0);
				VertexData vdP1 = vertexDatas.get(indexP1);
				VertexData vdP2 = vertexDatas.get(indexP2);
				
				//Get the vertex out of the vdata
				Vertex v0 = vdP0.getVertex();
				Vertex v1 = vdP1.getVertex();
				Vertex v2 = vdP2.getVertex();
				
				//Put newVertices from vertexdata list in vertex array for the geometry array
				newVertices[indexP0] = v0;
				newVertices[indexP1] = v1;
				newVertices[indexP2] = v2;
				
				//Get the faces normal
//				Vector3D faceNormal = Tools3D.getNormal(v0, v1, v2, true); //myFace.normal;//
//				Vector3D faceNormal = myFace.normal.getCopy();
//				faceNormal.normalizeLocal();
				
				Vector3D faceNormal = myFace.normalNormalized;
				
				Vector3D normalP0;
				if (vdP0.getNeighborFaces().size() > 1){
//					logger.debug("Calcing v normal of face: " + myFace.index + " P0");
					normalP0 = vdP0.calcVertexNormalAllNeighbors();
				}else{
					normalP0 = faceNormal;
				}
				
				Vector3D normalP1;
				if (vdP1.getNeighborFaces().size() > 1){
//					logger.debug("Calcing v normal of face: " + myFace.index + " P1");
					normalP1 = vdP1.calcVertexNormalAllNeighbors();
				}else{
					normalP1 = faceNormal;
				}
				
				Vector3D normalP2;
				if (vdP2.getNeighborFaces().size() > 1){
//					logger.debug("Calcing v normal of face: " + myFace.index + " P2");
					normalP2 = vdP2.calcVertexNormalAllNeighbors();
				}else{
					normalP2 = faceNormal;
				}
				
				normals[indexP0] = normalP0;
				normals[indexP1] = normalP1;
				normals[indexP2] = normalP2;
				
				////Normalen debug linien sehen mit normals invertiert richtig
				//aus, aber model wird falsch geshaded!
				/*
				normals[indexP0].scale(-1);
				normals[indexP1].scale(-1);
				normals[indexP2].scale(-1);
				*/
			} 
			
			//Leider n�tig? da manche models newVertices enthalten, die in den faces garnicht referenziert werden, quasi tote verts
			for (int j = 0; j < newVertices.length; j++) {
				if (newVertices[j] == null){
					newVertices[j] = nullVect;
				}
				//Normals array should be same length as verts, check for nulls here
				if (normals[j] == null){
					normals[j] = nullVect;
				}
				//System.out.print(normals[j] + " - ");
			}
			
			logger.debug("----------------------------------------------------------------------------------");
			logger.debug("New duplicates of same vertices with different texture coordinates created: " + newDuplicatesWithDiffTexCoordsCreated);
			logger.debug("New duplicates of same vertices with different normal created: " + newDuplicatesWithDiffNormalCreated);
			logger.debug("Original number of vertices: " + originalVertices.length);
			logger.debug("Final number of vertices: " + vertexDatas.size());
			logger.debug("Original number of faces: " + originalIndices.length/3);
			logger.debug("Final number of faces: " + faces.size());
			logger.debug("Original number of indices: " + originalIndices.length);
			logger.debug("Final number of indices: " + newIndices.length);
			logger.debug("Final number of normals: " + normals.length);
			logger.debug("----------------------------------------------------------------------------------");
			
			if (newVertices.length > 2 && faces.size() > 0){
				// Create a geometryInfo with all vertices of the mesh
				GeometryInfo geometryInfo = new GeometryInfo(pa, newVertices, newIndices);
	
				//Set the normals for the geometry
				geometryInfo.setNormals(normals, true, false);
	
				//Clean up a bit
				vertexDatas = null;
				faces		= null;
				
				return geometryInfo;
			}
			return null;
		}



	
	
	/**
	 * Generates normals for the provided geometry data according to the crease angle.
	 * <br>A crease angle of 180 will result in a all smoothed model, smoothing each vertex normal
	 * across all its neighbor faces' face normals.
	 * <br>A crease angle of zero (0) will result in a flat shaded geometry. Only face normals will
	 * be used then.
	 * <br>A crease angle of 89 will create hard edges at 90 degree angle faces and smooth faces with
	 * less then 90 degree normals. This would be ideal to generate normals for a cube.
	 * <br>The best crease angle for a model has to be found by testing.
	 * 
	 * @param pa the pa
	 * @param originalVertices the original vertices
	 * @param originalIndices the original indices
	 * @param originalTexCoords the original tex coords
	 * @param originalTexIndices the original tex indices
	 * @param creaseAngle the crease angle
	 * @param flipTextureY the flip texture y
	 * @param flipTextureX the flip texture x
	 * 
	 * @return the geometry info
	 * 
	 * indexed, geometry info with normals
	 */
	public GeometryInfo generateCreaseAngleNormals(PApplet pa, Vertex[] originalVertices, short[] originalIndices, float[][] originalTexCoords, int[] originalTexIndices, float creaseAngle, boolean flipTextureY, boolean flipTextureX){
			int newDuplicatesWithDiffTexCoordsCreated 	= 0;
			int newDuplicatesWithDiffNormalCreated 		= 0;
			
			boolean useNormailizedNormalsForAdding = true;

			logger.debug("-> Loading  model with a crease angle: " + creaseAngle);
			
			float creaseAngleRad = (float)Math.toRadians(creaseAngle);
//			float creaseAngleRad = 
			float creaseCosinus = (float)Math.cos(creaseAngle); 
			
			nullVect 	= new Vertex(0,0,0, -1, -1);
			ArrayList<VertexData> 	vertexDatas = new ArrayList<VertexData>();
			ArrayList<MyFace> 		faces 		= new ArrayList<MyFace>();
			
			//Init and fill vertexdata list with as many as vertices
			for (int i = 0; i < originalVertices.length; i++) {
				//Vertex v = vertices[i];
				VertexData vd = new VertexData();
				vd.setArrayIndex(i);
				vertexDatas.add(vd);
			}
			
	/////////////////////////////////////
			
			//GO through indices array and create a face for every three indices (must be triangle array!) 
			int pp0 = 0;
			int pp1 = 0;
			int pp2 = 0;
			
			int tIndexPP0 = 0;
			int tIndexPP1 = 0;
			int tIndexPP2 = 0;
			
			for (int i = 0; i < originalIndices.length/3; i++) {
				//int currentIndex = indices[i];
				
				//Next 3 vertex indices as the new faces pointers for our face
				pp0 = originalIndices[i*3];
				pp1 = originalIndices[i*3+1];
				pp2 = originalIndices[i*3+2];
				
				if (originalTexCoords.length > 0){
					//Next 3 texture texture indices //(vertex and texture indices indexed in the same order)
					tIndexPP0 = originalTexIndices[i*3];
					tIndexPP1 = originalTexIndices[i*3+1];
					tIndexPP2 = originalTexIndices[i*3+2];
				}
				
				
				MyFace myFace = new MyFace();
				myFace.p0 = pp0;
				myFace.p1 = pp1;
				myFace.p2 = pp2;
				
				int vertexPointer = pp0;
				int texturePointer = tIndexPP0;
				for (int j = 0; j < 3; j++) {
					if 	   (j == 0)	{
						vertexPointer 	= pp0;
						texturePointer  = tIndexPP0;
					}
					else if(j == 1){
						vertexPointer 	= pp1;
						texturePointer  = tIndexPP1;
					}
					else if(j == 2){
						vertexPointer 	= pp2;
						texturePointer  = tIndexPP2;
					}
					
					//Get the vertexdata at the index, the face points to
					VertexData myVertexData 	= vertexDatas.get(vertexPointer);
					
					Vertex newVertex = new Vertex(
							originalVertices[vertexPointer].x, 
							originalVertices[vertexPointer].y, 
							originalVertices[vertexPointer].z,
							originalVertices[vertexPointer].getR(),
							originalVertices[vertexPointer].getG(),
							originalVertices[vertexPointer].getB(),
							originalVertices[vertexPointer].getA()
					);
	
					//Create texcoords and add to vertex
					float[] tex = new float[2];
					if (originalTexCoords.length > 0){
						tex[0] = originalTexCoords[texturePointer][0];
						tex[1] = originalTexCoords[texturePointer][1];
						
						if (flipTextureY){
							tex[1] = 1.0f-tex[1];
						}
						
						if (flipTextureX){
							tex[0] = 1.0f-tex[0];
						}
						
						newVertex.setTexCoordU(tex[0]);
						newVertex.setTexCoordV(tex[1]);
					}

					//Check if the vertex data at the pointer is empty thats the case before first time a vertex is added to it here
					if (myVertexData.getVertex() == null){
						myVertexData.setVertex(newVertex);
						myVertexData.addNeighborFace(myFace);
						logger.debug("vdP" + j + " vertex in vertexData not initialized -> set it: " + newVertex);
					}else{//Vertex data at index already contains one or more vertices 
						
						//Check if the vertex data at the index contains a vertex that is equal to the new vertex and its texture information 
						//-> if true, just add the vertex to that vertexdata and the face it also is in
						//-> if false, check if a duplicate vertexdata is registered in the vertexdata the face points to
						//		-> if true, add the vertex to the duplicate, change face index to the duplicates index
						//		-> if false, create new vertexdata at end of list, add the vertex and texcoords of the vertex, and register 
						//			it as a duplicate in the original vertexdata, change the face index to the new duplicates index
						if (myVertexData.equalsVertex(newVertex)){
							//Register das face mit dem vertex data
							myVertexData.addNeighborFace(myFace);
							logger.debug("vdP" + j + "already CONTAINS a vertex with same coords and texture information -> do nothing, just add the current face to its neighborlist");
						}else{
							
							//Check if already duplicates were created, maybe with the same vertex and texture information,
							//then we have to add the vertex to that vertexdata rather than creating a new one
							int duplicateIndex = myVertexData.getVertDuplicateSameVertDiffTextureCoordListIndex(newVertex);
							
							if (duplicateIndex != -1){ //Es gibt schon ein duplicate mit gleichen tex coords wie dieses hier, adde bei duplicateIndex
								//Change face pointer of p0 to the duplicates index
								if 	   (j == 0)	myFace.p0 = duplicateIndex;
								else if(j == 1) myFace.p1 = duplicateIndex;
								else if(j == 2)	myFace.p2 = duplicateIndex;
								
								//wenn wir orginal hinzuf�gen wird auch allen duplicates neighbor face zugef�gt.
								myVertexData.addNeighborFace(myFace);
								
								logger.debug("vdP" + j + "has different texture coordiantes but a already created duplicate has the same -> change this face pointer to the duplicate one");
							}else{//there was no duplicate created with the same texture coords yet -> create one!
								
								//Neuen Vertex machen, an original vertices list anh�ngen, neuen index in face auf diesen setzen, face adden
								VertexData newVertexData = new VertexData();
								
								//Add the vertex information to the newly created vertexdata
								newVertexData.setVertex(newVertex);
								
								//Get new index at end of vertex list
								int newIndexOfNewVertexData = vertexDatas.size();
								
								//Change the index of the face to the index the new Vdata is created at
								if 	   (j == 0)	myFace.p0 = newIndexOfNewVertexData;
								else if(j == 1) myFace.p1 = newIndexOfNewVertexData;
								else if(j == 2)	myFace.p2 = newIndexOfNewVertexData;
								
								//Tell the vertexdata the index it is in the overall data
								newVertexData.setArrayIndex(newIndexOfNewVertexData);
								
								//Add new vertex data at the end of the list of all vertexdatas
								vertexDatas.add(newVertexData);
								
								//Inform the original vertexdata, that a duplicate with diff tex coords was created and register it
								myVertexData.registerCreatedDuplicateDiffTexCoords(newVertexData);
								
								//Adde face zu orginal face -> damit wird auch duplicates und dem neuen hinzugef�gt,
								//wenn wir es vorher mit registerDuplicate am orginal registiert haben!
								myVertexData.addNeighborFace(myFace);
								
								//copy the face list the vertex is contained in of the original 
								//to the duplicate (for normal generation later, so they are still neighbors)! 
								newVertexData.addNeighborFaces(myVertexData.getFacesContainedIn());
								logger.debug("vdP" + j + "isnt empty but DOESENT CONTAIN (also no duplicate contains) a vertex with same coords and texture information -> creating new V.D. at: " + newIndexOfNewVertexData);
								newDuplicatesWithDiffTexCoordsCreated++;
							}//end if duplicate exists
						}//end if vertexdata already contains Vnew
					}//end if vertexdata is empty
				}//end for p0,p1,p2
				
				//Calculate the face's normal vector
				myFace.calcFaceNormal(vertexDatas);
				
				myFace.index = faces.size(); 
				
				//Add face to facelist
				faces.add(myFace);
			}
			
			logger.debug("-> Processed duplicate vertex/texture points.");
	/////////////////////////////////////////
			
			
	//////////////////////////////////////		
	
			if (creaseAngleRad != 0.0) {
				//Durch alle selbst kreierten faces gehen, und schauen ob ein vertex des faces mit einem seiner
				//nachbar faces einen "sharp edge" hat oder smooth ist.
				//wenn smooth -> als smooth neighbor des face pointers hinzuf�gen
                for (MyFace currentFace : faces) {
                    //Get vertex pointers of face to vertices in vertices list
                    int indexP0 = currentFace.p0;
                    int indexP1 = currentFace.p1;
                    int indexP2 = currentFace.p2;

                    //Get the vertexdatas out of the list with the pointers
                    VertexData vdP0 = vertexDatas.get(indexP0);
                    VertexData vdP1 = vertexDatas.get(indexP1);
                    VertexData vdP2 = vertexDatas.get(indexP2);


                    int[] currentFaceVertIndices = currentFace.getVertexIndices();
                    //Go through all 3 vertexdata entries in the current face and check for its smooth neighbors
                    for (int faceVD = 0; faceVD < currentFaceVertIndices.length /*currentFace.getVertexIndices().length*/; faceVD++) {

                        VertexData currentFaceVertexData = null;
                        if (faceVD == 0) {
                            currentFaceVertexData = vdP0; /*logger.debug("Face: " + j + " - P0");*/
                        } else if (faceVD == 1) {
                            currentFaceVertexData = vdP1; /*logger.debug("Face: " + j + " - P1");*/
                        } else if (faceVD == 2) {
                            currentFaceVertexData = vdP2; /*logger.debug("Face: " + j + " - P2");*/
                        }

                        ArrayList<MyFace> facesVDIsIn = currentFaceVertexData.getFacesContainedIn();

                        //Go through all neighbor faces that the vertex(data) is a part of
                        for (MyFace anotherFaceVDisIn : facesVDIsIn) {
                            //Check that we are not considering the same face as the currentFace
                            if (!anotherFaceVDisIn.equals(currentFace)) {

                                boolean onSameSurface = isOnSameSurfaceRadians(currentFace, anotherFaceVDisIn, creaseAngleRad);
//								boolean onSameSurface = isOnSameSurfaceCosAngle(currentFace, anotherFaceVDisIn, creaseCosinus);

                                //Check if the current face and the other face VD are connected
                                //by an angle < cos_angle degrees,
                                //if so, add the faces to the vds smooth neighbor list (for later normal generation)
                                //if NOT so, create new VertexData, as a copy of the current one at the end of
                                //the vertexdata list, adjust the face pointers of the current face to the new one
                                //(we need another vertex so we have two different normals for them if the two faces arent smoothly connected)
                                if (onSameSurface) {
                                    if (faceVD == 0) {
                                        logger.debug("Face: " + (currentFace.index) + " (P0:" + currentFace.p0 + " P1:" + currentFace.p1 + " P2:" + currentFace.p2 + ")" + " is smooth with face: " + (anotherFaceVDisIn.index) + " (P0:" + anotherFaceVDisIn.p0 + " P1:" + anotherFaceVDisIn.p1 + " P2:" + anotherFaceVDisIn.p2 + ") at currentFaces' pointer: " + currentFace.p0 + " (" + vdP0.getVertex() + " )");
                                    } else if (faceVD == 1) {
                                        logger.debug("Face: " + (currentFace.index) + " (P0:" + currentFace.p0 + " P1:" + currentFace.p1 + " P2:" + currentFace.p2 + ")" + " is smooth with face: " + (anotherFaceVDisIn.index) + " (P0:" + anotherFaceVDisIn.p0 + " P1:" + anotherFaceVDisIn.p1 + " P2:" + anotherFaceVDisIn.p2 + ") at currentFaces' pointer: " + currentFace.p1 + " (" + vdP1.getVertex() + " )");
                                    } else if (faceVD == 2) {
                                        logger.debug("Face: " + (currentFace.index) + " (P0:" + currentFace.p0 + " P1:" + currentFace.p1 + " P2:" + currentFace.p2 + ")" + " is smooth with face: " + (anotherFaceVDisIn.index) + " (P0:" + anotherFaceVDisIn.p0 + " P1:" + anotherFaceVDisIn.p1 + " P2:" + anotherFaceVDisIn.p2 + ") at currentFaces' pointer: " + currentFace.p2 + " (" + vdP2.getVertex() + " )");
                                    }

                                    if (faceVD == 0) {
                                        currentFace.addSmoothNeighborP0(anotherFaceVDisIn);
                                    } else if (faceVD == 1) {
                                        currentFace.addSmoothNeighborP1(anotherFaceVDisIn);
                                    } else if (faceVD == 2) {
                                        currentFace.addSmoothNeighborP2(anotherFaceVDisIn);
                                    }
                                }//if smooth
                            }//if not checking against this same face
                        }
                    }//for loop through all 3 vertexdatas of the current face
                }
	
			}//end if creaseAngle != 0.0
	///////////////////////////////////////////////////////////////////////////		

			
			
	/////////////////	//moved to the next loop
//			//Vertex normalen berechnen
//			for (int j = 0; j < faces.size(); j++) {
//				MyFace currentFace = faces.get(j);
//				currentFace.calcVertexNormals(useNormailizedNormalsForAdding);
//			}
	//////////////////
			
			// /|\
			// combine?
			// \|/
			
	//////////////////////////////////////////////////////////////////////		
			
	
			//Die vertex normalen wurden pro face und pro pointer auf ein vertex in den faces berechnet
			//Jetzt f�gen wir den VertexDatas, die die vertices representieren diese vertex normalen hinzu.
			//Wenn in mehreren faces auf den gleichen vertex gezeigt wird, aber in dem face f�r diesen vertex eine
			//andere normale berechnet wurde (weil face mit anderen smooth ist) m�ssen wir evtl das Vertex(data) duplizieren
			//und diesem die andere normale hinzuf�gen
			for (int j = 0; j < faces.size(); j++) {
				MyFace currentFace = faces.get(j);
				
				//GENERATE THE FACES VERTEX NORMAL BASED ON ITS SMOOTH NEIGHBORS
				currentFace.calcVertexNormals(useNormailizedNormalsForAdding);
				
				int[] faceVertexPointer = currentFace.getVertexIndices();
				
				//Go trough all (3) vertexpointer p0..2 of the current face
				for (int i = 0; i < faceVertexPointer.length; i++) {
					int currentVertexPointer = faceVertexPointer[i];
					
					logger.debug("-> Processing face[" + j + "].P" + i + " Vertex: " + vertexDatas.get(currentVertexPointer).getVertex());
					
					//Hole vertexdata auf das das currentFace an dem aktuellen zeiger (p0,p1,p2) zeigt
					VertexData currentVertexDataP0OrP1OrP2 = vertexDatas.get(currentVertexPointer);
					
					//Get normal saved in the vertexdata at position Face.Px
					Vector3D currentFacesCurrentVDNormal = currentVertexDataP0OrP1OrP2.getUniqueVertexNormal();
					
					//Get vertex normal array calculated and saved in the face for each point Px
					Vector3D[] vertexNormalsCurrentFace = currentFace.getVertexNormals();
					
					//Check if the vertexnormal data at the pointer is null -> thats the case before first time a vertexnormal is set here
					if (currentFacesCurrentVDNormal == null){
						currentVertexDataP0OrP1OrP2.setUniqueVertexNormal(vertexNormalsCurrentFace[i]);
						logger.debug("Face " + j + ", vdP" + i + " (Vertex: " + vertexDatas.get(currentVertexPointer).getVertex() + ")" + " normal not yet set -> set it: " + vertexNormalsCurrentFace[i]);
					}else{//Vertexdata at index already contains a vertexnormal -> check if its the equal to this faces currentVD's
						
						if (currentFacesCurrentVDNormal.equalsVectorWithTolerance(vertexNormalsCurrentFace[i], ToolsMath.ZERO_TOLERANCE)){
							logger.debug("Face " + j + ", vdP" + i +  " (Vertex: " + vertexDatas.get(currentVertexPointer).getVertex() + ")" + " already CONTAINS a normal with the same values as the normal of this faces point ->  we can leave the index and normal at the same place: N:" + vertexNormalsCurrentFace[i]);
						}else{
							int duplicateIndexOfSameVertDiffNormal = currentVertexDataP0OrP1OrP2.getVertDuplicateSameVertDiffNormalListIndex(vertexNormalsCurrentFace[i]); 
							
							if (duplicateIndexOfSameVertDiffNormal != -1){ //Es gibt schon ein duplicate mit gleichen tex coords wie dieses hier, adde bei duplicateIndex
								//Change face pointer of p0 to the duplicates index
								if 	   (i == 0)	currentFace.p0 = duplicateIndexOfSameVertDiffNormal;
								else if(i == 1) currentFace.p1 = duplicateIndexOfSameVertDiffNormal;
								else if(i == 2)	currentFace.p2 = duplicateIndexOfSameVertDiffNormal;
								logger.debug("Face " + j + " vdP" + i  + " (Vertex: " + vertexDatas.get(currentVertexPointer).getVertex() + ")" +  " vertexnormal is conform with a duplicate of the original vertex -> point to that duplicate: N:"  + vertexNormalsCurrentFace[i]);
							}else{//duplicate index == -1 -> neither the orignal faces point has the same vertexnormal nor a duplicate with different normal has that normal -> create new VD with the different normal and same vertex
								
								//Neuen Vertex machen, an original vertices list anh�ngen, neuen index in face auf diesen setzen
								VertexData newVertexData = new VertexData();
								
								//Add the vertex information to the newly created vertexdata
								newVertexData.setVertex(currentVertexDataP0OrP1OrP2.getVertex());
								
								//Set the vertex normal for the new vertexdata  as the vertex normal of the current face' calced normal for that vertex
								newVertexData.setUniqueVertexNormal(vertexNormalsCurrentFace[i]);
								
								//Get new index at end of vertex list
								int newIndexOfNewVertexData = vertexDatas.size();
								
								//Change the index of the face to the index the new Vdata is created at
								if 	   (i == 0)	currentFace.p0 = newIndexOfNewVertexData;
								else if(i == 1) currentFace.p1 = newIndexOfNewVertexData;
								else if(i == 2)	currentFace.p2 = newIndexOfNewVertexData;
								
								//Tell the vertexdata the index it is in the overall data
								newVertexData.setArrayIndex(newIndexOfNewVertexData);
								
								//Add new vertex data at the end of the list of all vertexdatas
								vertexDatas.add(newVertexData);
								
								//Inform the original vertexdata, that a duplicate with diff tex coords was created and register it
								currentVertexDataP0OrP1OrP2.registerCreatedDuplicateDiffNormal(newVertexData);
								logger.debug("Face " + j + ", vdP" + i  + " (Vertex: " + vertexDatas.get(currentVertexPointer).getVertex() + ")" +  " has a different vertexnormal and DOESENT CONTAIN a link to a duplicate vertex with same normal information -> creating new VD at: " + newIndexOfNewVertexData + " N:" + vertexNormalsCurrentFace[i]);
								newDuplicatesWithDiffNormalCreated++;
							}//end if duplicate exists
						}//end if vertexdata already contains Vnew
					}//end if vertexdata is empty
				}	
			}
	////////////////////////////////////		
			
			
			
			
	//////////////////////////////////////////		
			//Create arrays
			Vertex[] newVertices 	= new Vertex[vertexDatas.size()];
			Vector3D[] normals  	= new Vector3D[vertexDatas.size()];
			short[] newIndices		= new short[faces.size()*3];
			
			/*
			 * Go through the final faces list and fill vertex/newIndices/normal arrays
			 */
			for (int j = 0; j < faces.size(); j++) {
				MyFace myFace = faces.get(j);
				//Get vertex pointers of face to newVertices in newVertices list
				int indexP0 = myFace.p0;
				int indexP1 = myFace.p1;
				int indexP2 = myFace.p2;
				
				//Use pointers as newIndices and fill newIndices array
				newIndices[j*3]		= (short) indexP0;
				newIndices[j*3+1]	= (short) indexP1;
				newIndices[j*3+2]	= (short) indexP2;
				
				//Get the vertexdatas out of the list with the pointers
				VertexData vdP0 = vertexDatas.get(indexP0);
				VertexData vdP1 = vertexDatas.get(indexP1);
				VertexData vdP2 = vertexDatas.get(indexP2);
				
				//Get the vertex out of the vdata
				Vertex v0 = vdP0.getVertex();
				Vertex v1 = vdP1.getVertex();
				Vertex v2 = vdP2.getVertex();
				
				//Put newVertices from vertexdata list in vertex array for the geometry array
				newVertices[indexP0] = v0;
				newVertices[indexP1] = v1;
				newVertices[indexP2] = v2;
				
				//Get the faces normal
				normals[indexP0] = vdP0.uniqueVertexNormal;
				normals[indexP1] = vdP1.uniqueVertexNormal;
				normals[indexP2] = vdP2.uniqueVertexNormal;
				
				////Normalen debug linien sehen mit normals invertiert richtig
				//aus, aber model wird falsch geshaded!
				/*
				normals[indexP0].scale(-1);
				normals[indexP1].scale(-1);
				normals[indexP2].scale(-1);
				*/
			} 
			
			//Leider n�tig? da manche models newVertices enthalten, die in den faces garnicht referenziert werden, quasi tote verts
			for (int j = 0; j < newVertices.length; j++) {
				if (newVertices[j] == null){
					newVertices[j] = nullVect;
				}
				//Normals array should be same length as verts, check for nulls here
				if (normals[j] == null){
					normals[j] = nullVect;
				}
	//			System.out.print(normals[j] + " - ");
			}
	//		logger.debug();
	///////////////////////////////////////////////////////		
			
			logger.debug("----------------------------------------------------------------------------------");
			logger.debug("New duplicates of vertices with same vertex but different texture coordinates created: " + newDuplicatesWithDiffTexCoordsCreated);
			logger.debug("New duplicates of vertices with same vertex but different normal created: " + newDuplicatesWithDiffNormalCreated);
			logger.debug("Original number of vertices: " + originalVertices.length);
			logger.debug("Final number of vertices: " + vertexDatas.size());
			logger.debug("Final number of indices: " + newIndices.length);
			logger.debug("Final number of faces: " + faces.size());
			logger.debug("Final number of normals: " + normals.length);
			logger.debug("----------------------------------------------------------------------------------");
			
			if (newVertices.length > 2 && faces.size() > 0){
				// Create a geometryInfo with all vertices of the mesh
				GeometryInfo geometryInfo = new GeometryInfo(pa, newVertices, newIndices);
	
				//Set the normals for the geometry
				geometryInfo.setNormals(normals, true, false);
	
				//Clean up a bit
				vertexDatas = null;
				faces		= null;
				
				return geometryInfo;
			}
			return null;
		}


	
	/**
	 * Sets the debug mode.
	 * 
	 * @param debug the debug
	 */
	public void setDebug(boolean debug) {
		if (debug)
			logger.setLevel(ILogger.DEBUG);
		else
			logger.setLevel(ILogger.ERROR);
	}
	
	/**
	 * This influences the normal generation with crease angles.
	 * If <code>useNormalsEqualToFace</code> is set to true, normals
	 * of neighbor faces, that have the same normal as the face and vertex were checking
	 * against will be used in the calculation.
	 * </br>If <code>useNormalsEqualToFace</code> is set to false, these normals equal to
	 * the test face normal will be discarded to avoid adding up redundant normals.
	 * </br>The default is FALSE.
	 * 
	 * @param useNormalsEqualToFace use other normals equal to face or
	 */
	public void setUseNormalsEqualToFace(boolean useNormalsEqualToFace) {
		this.useNormalsEqualToFace = useNormalsEqualToFace;
	}

	/**
	 * This influences the normal generation with crease angles.
	 * <br<>If <code>useEqualNeighborNormalsAgain</code> is set to true, normals
	 * of neighbor faces, that have the same normal as a neighbor face previously used in
	 * the calculation for one same vertex normal will again be used and added in.
	 * <br>If <code>useEqualNeighborNormalsAgain</code> is set to false, these normals will
	 * not be added in the calculation again.
	 * <br>The default is FALSE.
	 * 
	 * @param useEqualNeighborNormalsAgain use equal neighbor normals again
	 */
	public void setUseEqualNeighborNormalsAgain(boolean useEqualNeighborNormalsAgain) {
		this.useEqualNeighborNormalsAgain = useEqualNeighborNormalsAgain;
	}
	
	
	/**
	 * Calculates ..
	 * 
	 * @param face1 the face1
	 * @param face2 the face2
	 * @param cosAngle the cos angle
	 * 
	 * @return true, if checks if is on same surface radians
	 */
		private boolean isOnSameSurfaceRadians(MyFace face1, MyFace face2, float cosAngle) { 
			
	//		float cosineBetweenNormals 	= face1.normal.dot(face2.normal);
	//		logger.debug(Math.acos(cosineBetweenNormals));
	//		boolean smooth 	= cosineBetweenNormals > cosAngle;
	//		//boolean smoothTriangles = (cosineBetweenNormals > cosAngle);
	//        //logger.debug("surface: compare dot=" +dot + " cos-angle=" + cos_angle + " return " + (dot > cos_angle));
	//		logger.debug(Vector3D.angleBetween(face1.normal, face2.normal));
	//		logger.debug();
	//		
	//        return smooth;
	        
			boolean debugSmoothChecking = false;
	        float angleBetweenNorms = Vector3D.angleBetween(face1.normal, face2.normal);
	        
	        if (debugSmoothChecking){
	        	 float angleBetweenNormsDegrees = (float)Math.toDegrees(angleBetweenNorms);
		        logger.debug("Angle between normals :" + angleBetweenNormsDegrees);
		        logger.debug("Crease angle: " + Math.toDegrees(cosAngle));
	        }
	        
	        boolean smooth = angleBetweenNorms < cosAngle; 
	        
	        if (debugSmoothChecking)
	        	logger.debug("-> Smooth: " + smooth);
	        
	        if (Float.isNaN(angleBetweenNorms)){
	        	smooth = true;
	        	if (debugSmoothChecking)
	        		logger.debug("NAN!");
	        }
	        
	        return smooth;
	//		float threshold = (float)Math.cos(creaseAngle);
	//		float cosine 	= face1.normal.dot(face2.normal);
	//		boolean smooth 	= cosine > threshold;
	//
	//		//boolean smoothTriangles = (cosine > threshold);
	//        //logger.debug("surface: compare dot=" +dot + " cos-angle=" + cos_angle + " return " + (dot > cos_angle));
	//        return smooth;
	//        return false;
	////		return true; 
	    }



	/**
	 * Calculates ..
	 * 
	 * @param face1 the face1
	 * @param face2 the face2
	 * @param cosAngle the cos angle
	 * 
	 * @return true, if checks if is on same surface cos angle
	 */
	private boolean isOnSameSurfaceCosAngle(MyFace face1, MyFace face2, float cosAngle) { //FIXME really correct?
		float cosineBetweenNormals 	= face1.normal.dot(face2.normal);
		boolean smooth 	= cosineBetweenNormals > Math.abs(cosAngle);
		
		if (Float.isNaN(cosineBetweenNormals))
			smooth = true;
		
	    return smooth;
	}



	/**
	 * Class representing a triangle face of a mesh.
	 * 
	 * @author C.Ruff
	 */
		private class MyFace{
			
			/** The p0. */
			int p0;
			
			/** The p1. */
			int p1;
			
			/** The p2. */
			int p2;
			
			/** The smooth neighbors p0. */
			private ArrayList<MyFace> smoothNeighborsP0;
			
			/** The smooth neighbors p1. */
			private ArrayList<MyFace> smoothNeighborsP1;
			
			/** The smooth neighbors p2. */
			private ArrayList<MyFace> smoothNeighborsP2;
			
			/** The normal. */
			Vector3D normal;
			
			/** The normal normalized. */
			Vector3D normalNormalized;
			
			/** The center. */
			private Vector3D center;
			
			/** The index. */
			int index;
			
			/** The vertex normal p0. */
			private Vector3D vertexNormalP0;
			
			/** The vertex normal p1. */
			private Vector3D vertexNormalP1;
			
			/** The vertex normal p2. */
			private Vector3D vertexNormalP2;
			
			/** The vertex normals. */
			private Vector3D[] vertexNormals;
			
			/**
			 * Instantiates a new my face.
			 */
			public MyFace(){
				p0 = -1;
				p1 = -1;
				p2 = -1;
				//normal = nullVect;
				
				smoothNeighborsP0 = new ArrayList<MyFace>();
				smoothNeighborsP1 = new ArrayList<MyFace>();
				smoothNeighborsP2 = new ArrayList<MyFace>();
			}
			
			/**
			 * Gets the vertex indices.
			 * 
			 * @return the vertex indices
			 * 
			 * the pointers (indices) into the vertex array, this face holds (length=3 here)
			 */
			public int[] getVertexIndices() {
				return new int[]{p0,p1,p2};
			}
			
			/**
			 * Registers the face as a smooth neighbor of this faces vertex at
			 * the index P0. This is only relevant if a crease angle is used.
			 * 
			 * @param neighborFace the neighbor face
			 */
			public void addSmoothNeighborP0(MyFace neighborFace){
				if (!smoothNeighborsP0.contains(neighborFace)){
					smoothNeighborsP0.add(neighborFace);
				}
			}
			
			/**
			 * Registers the face as a smooth neighbor of this faces vertex at
			 * the index P1. This is only relevant if a crease angle is used.
			 * 
			 * @param neighborFace the neighbor face
			 */
			public void addSmoothNeighborP1(MyFace neighborFace){
				if (!smoothNeighborsP1.contains(neighborFace)){
					smoothNeighborsP1.add(neighborFace);
				}
			}
			
			/**
			 * Registers the face as a smooth neighbor of this faces vertex at
			 * the index P2. This is only relevant if a crease angle is used.
			 * 
			 * @param neighborFace the neighbor face
			 */
			public void addSmoothNeighborP2(MyFace neighborFace){
				if (!smoothNeighborsP2.contains(neighborFace)){
					smoothNeighborsP2.add(neighborFace);
				}
			}
			
	
			/**
			 * Calculates this face's face normal.
			 * 
			 * @param vertexList the vertex list
			 */
			public void calcFaceNormal(ArrayList<VertexData> vertexList){
				//We DONT NORMALIZE YET! 
				this.normal = ToolsGeometry.getNormal(vertexList.get(p0).getVertex(), vertexList.get(p1).getVertex(), vertexList.get(p2).getVertex(), false);
				this.normalNormalized = normal.getCopy();
				this.normalNormalized.normalizeLocal();
			}
			
			/**
			 * Gets the center point local.
			 * 
			 * @param vertexDataList the vertex data list
			 * 
			 * @return the center point local
			 * 
			 * the center point of this face
			 */
			public Vector3D getCenterPointLocal(ArrayList<VertexData> vertexDataList){
				center = vertexDataList.get(p0).getVertex().getCopy();
				center.addLocal(vertexDataList.get(p1).getVertex());
				center.addLocal(vertexDataList.get(p2).getVertex());
				center.scaleLocal(ToolsMath.ONE_THIRD);
				return this.center;
			}
	
			
			/**
			 * Cals the vertex normals for the three points this face has.
			 * 2 different ways are implemented from which can be chosen by
			 * the boolean variable.
			 * if "useNormailizedNormalsForAdding" is false, all unnormalized normals of all
			 * smooth neighbor faces are added up and at the end normalized.
			 * If "useNormailizedNormalsForAdding" is false, all neighbors face normals are normalized before adding up.
			 * Also, normals that are equal to this faces normal arent added again.
			 * Furthermore, only normals that arent equal to one already added from another
			 * neighbor are added in. This is slower but should produce better results.
			 * 
			 * @param useNormailizedNormalsForAdding the use normailized normals for adding
			 */
			public void calcVertexNormals(boolean useNormailizedNormalsForAdding) {
				if (normal.equals(nullVect)){
					throw new RuntimeException("We have to calculate the face normal before calling calcVertexNormals!");
				}
				

				if (useNormailizedNormalsForAdding){
					//Use normalized normals for all calculations
					//Also checks if normals are attempted to be added that are equal to this faces normal
					//or normals that have already been added -> those are not added
					
//					/*
					Vector3D normalizedFaceNormal = this.normal.getCopy();
					normalizedFaceNormal.normalizeLocal();

					logger.debug("");
					logger.debug("Face " + index + " normal: " + this.normal + " Normalized: " + normalizedFaceNormal);
					logger.debug("P0:");

					//For each face point, calc vertex normal by adding up all 
					//smooth connected neighbors + this faces' normal and normalize in the end
					ArrayList<Vector3D> alreadyAddedInP0 = new ArrayList<Vector3D>();
//					vertexNormalP0 = this.normal.getCopy(); //Init with own faces face normal
					vertexNormalP0 = normalizedFaceNormal.getCopy();
                    for (MyFace neighborFaceP0 : smoothNeighborsP0) {
                        Vector3D nextSmoothNeighborNormal = neighborFaceP0.normal;
                        Vector3D nextSmoothNeighborNormalNormalized = nextSmoothNeighborNormal.getCopy();
                        //TODO doch nochmal probieren mit vorher ausgerechneter normal? sonst performance loss!
                        nextSmoothNeighborNormalNormalized.normalizeLocal();//neighborFaceP0.normalNormalized;
                        boolean alreadyAddedSameNormalIn = false;


                        //Dont add faces normals that are equal to this faces normals
                        if (!useNormalsEqualToFace && nextSmoothNeighborNormalNormalized.equalsVectorWithTolerance(normalizedFaceNormal, ToolsMath.ZERO_TOLERANCE)) {
                            alreadyAddedSameNormalIn = true;
                            logger.debug("Not using normal: " + nextSmoothNeighborNormalNormalized + " of face " + neighborFaceP0.index + " in vertex norm calc because its equal to this faces normal.");
                        }
//						else //Dont add face normals that are equal to one already added 
//						{
                        if (!useEqualNeighborNormalsAgain) {
                            for (Vector3D neighBorNorm : alreadyAddedInP0) {
                                if (neighBorNorm.equalsVectorWithTolerance(nextSmoothNeighborNormalNormalized, ToolsMath.ZERO_TOLERANCE)) {
                                    alreadyAddedSameNormalIn = true;
                                    logger.debug("Already added same normal -> dont add again N: " + neighBorNorm);
                                }
                            }
                        }
//						}

                        if (!alreadyAddedSameNormalIn) {
                            vertexNormalP0.addLocal(nextSmoothNeighborNormalNormalized);
                            alreadyAddedInP0.add(nextSmoothNeighborNormalNormalized);
                            logger.debug("Added normal: " + nextSmoothNeighborNormalNormalized + " of face: " + neighborFaceP0.index);
                        }
                    }
					vertexNormalP0.normalizeLocal();

					logger.debug("P1");
					//For each face point, calc vertex normalby adding up all 
					//smooth connected neighbors + this faces' normal and normalize in the end
					ArrayList<Vector3D> alreadyAddedInP1 = new ArrayList<Vector3D>();
//					vertexNormalP1 = this.normal.getCopy(); //Init with own faces face normal
					vertexNormalP1 = normalizedFaceNormal.getCopy();
                    for (MyFace neighborFaceP1 : smoothNeighborsP1) {
                        Vector3D nextSmoothNeighborNormal = neighborFaceP1.normal;
                        Vector3D nextSmoothNeighborNormalNormalized = nextSmoothNeighborNormal.getCopy();
                        //TODO doch nochmal probieren mit vorher ausgerechneter normal? sonst performance loss!
                        nextSmoothNeighborNormalNormalized.normalizeLocal();//neighborFaceP1.normalNormalized;
                        boolean alreadyAddedSameNormalIn = false;

                        //Dont add faces normals that are equal to this faces normals
                        if (!useNormalsEqualToFace && nextSmoothNeighborNormalNormalized.equalsVectorWithTolerance(normalizedFaceNormal, ToolsMath.ZERO_TOLERANCE)) {
                            alreadyAddedSameNormalIn = true;
                            logger.debug("Not using normal: " + nextSmoothNeighborNormalNormalized + " of face " + neighborFaceP1.index + " in vertex norm calc because its equal to this faces normal.");
                        }
//						else //Dont add face normals that are equal to one already added 
//						{
                        if (!useEqualNeighborNormalsAgain) {
                            for (Vector3D neighBorNorm : alreadyAddedInP1) {
                                if (neighBorNorm.equalsVectorWithTolerance(nextSmoothNeighborNormalNormalized, ToolsMath.ZERO_TOLERANCE)) {
                                    alreadyAddedSameNormalIn = true;
                                    logger.debug("Already added same normal -> dont add again N: " + neighBorNorm);
                                }
                            }
                        }
//						}

                        if (!alreadyAddedSameNormalIn) {
                            vertexNormalP1.addLocal(nextSmoothNeighborNormalNormalized);

                            alreadyAddedInP1.add(nextSmoothNeighborNormalNormalized);
                            logger.debug("Added normal: " + nextSmoothNeighborNormalNormalized + " of face: " + neighborFaceP1.index);
                        }
                    }
					vertexNormalP1.normalizeLocal();

					
					logger.debug("P2");
					//For each face point, calc vertex normalby adding up all 
					//smooth connected neighbors + this faces' normal and normalize in the end
					ArrayList<Vector3D> alreadyAddedInP2 = new ArrayList<Vector3D>();
//					vertexNormalP2 = this.normal.getCopy(); //Init with own faces face normal
					vertexNormalP2 = normalizedFaceNormal.getCopy();
                    for (MyFace neighborFaceP2 : smoothNeighborsP2) {
                        Vector3D nextSmoothNeighborNormal = neighborFaceP2.normal;
                        Vector3D nextSmoothNeighborNormalNormalized = nextSmoothNeighborNormal.getCopy();
                        //TODO doch nochmal probieren mit vorher ausgerechneter normal? sonst performance loss!
                        nextSmoothNeighborNormalNormalized.normalizeLocal();//neighborFaceP2.normalNormalized;
                        boolean alreadyAddedSameNormalIn = false;


                        //Dont add faces normals that are equal to this faces normals
                        if (!useNormalsEqualToFace && nextSmoothNeighborNormalNormalized.equalsVectorWithTolerance(normalizedFaceNormal, ToolsMath.ZERO_TOLERANCE)) {
                            alreadyAddedSameNormalIn = true;
                            logger.debug("Not using normal: " + nextSmoothNeighborNormalNormalized + " of face " + neighborFaceP2.index + " in vertex norm calc because its equal to this faces normal.");
                        }
//						else //Dont add face normals that are equal to one already added 
//						{
                        if (!useEqualNeighborNormalsAgain) {
                            for (Vector3D neighBorNorm : alreadyAddedInP2) {
                                if (neighBorNorm.equalsVectorWithTolerance(nextSmoothNeighborNormalNormalized, ToolsMath.ZERO_TOLERANCE)) {
                                    alreadyAddedSameNormalIn = true;
                                    logger.debug("Already added same normal -> dont add again N: " + neighBorNorm);
                                }
                            }
                        }
//						}

                        if (!alreadyAddedSameNormalIn) {
                            vertexNormalP2.addLocal(nextSmoothNeighborNormalNormalized);
                            alreadyAddedInP2.add(nextSmoothNeighborNormalNormalized);
                            logger.debug("Added normal: " + nextSmoothNeighborNormalNormalized + " of face: " + neighborFaceP2.index);
                        }
                    }
					vertexNormalP2.normalizeLocal();
				}else{
					//Just add up all smooth neighbors and normalize after
					
					//P0 Normal
					vertexNormalP0 = this.normal.getCopy();
                    for (MyFace neighborFaceP0 : smoothNeighborsP0) {
                        vertexNormalP0.addLocal(neighborFaceP0.normal);
                    }
					vertexNormalP0.normalizeLocal();
					
					//P1 Normal
					vertexNormalP1 = this.normal.getCopy();
                    for (MyFace neighborFaceP1 : smoothNeighborsP1) {
                        vertexNormalP1.addLocal(neighborFaceP1.normal);
                    }
					vertexNormalP1.normalizeLocal();
					
					
					vertexNormalP2 = this.normal.getCopy();
                    for (MyFace neighborFaceP2 : smoothNeighborsP2) {
                        vertexNormalP2.addLocal(neighborFaceP2.normal);
                    }
					vertexNormalP2.normalizeLocal();
				}

				
					logger.debug("Face: " + index + " -> P0 VertexNormal:-> " + vertexNormalP0);
					logger.debug("Face: " + index + " -> P1 VertexNormal:-> " + vertexNormalP1);
					logger.debug("Face: " + index + " -> P2 VertexNormal:-> " + vertexNormalP2);
					logger.debug("");
				
				vertexNormals = new Vector3D[]{vertexNormalP0,vertexNormalP1,vertexNormalP2};
			}
	
			
			/**
			 * Gets the vertex normals.
			 * 
			 * @return the vertex normals
			 */
			public Vector3D[] getVertexNormals() {
				if (vertexNormals == null){
					throw new RuntimeException("We have to calculate the vertex normals first!");
				}
				return vertexNormals;
			}
		}//End MyFace class
		
		
		/**
		 * Class to hold information about a mesh vertex.
		 * 
		 * @author C.Ruff
		 */
		private class VertexData{
			
			/** The array index. */
			private int arrayIndex;
			
			/** The vertex. */
			private Vertex vertex;
			
			/** The faces. */
			private ArrayList<MyFace> faces;
			
			/** The duplications with diff tex coords. */
			private ArrayList<VertexData> duplicationsWithDiffTexCoords;
			
//			private ArrayList<MyFace> smoothNeighbors;
//			private Vector3D vertexNormal;
//			private boolean vertexNormalDirty;
			
			/** The unique vertex normal. */
			private Vector3D uniqueVertexNormal;
			
			/** The duplications with diff normal. */
			private ArrayList<VertexData> duplicationsWithDiffNormal;
			
			//Normal calculated in the getSmoothnormals method
			/** The all neighbors normal. */
			private Vector3D allNeighborsNormal;
	
			/**
			 * Constructor.
			 */
			public VertexData() {
				super();
				faces 							= new ArrayList<MyFace>();
				duplicationsWithDiffTexCoords 	= new ArrayList<VertexData>();
				
				
				duplicationsWithDiffNormal 		= new ArrayList<VertexData>();
				
//				smoothNeighbors 				= new ArrayList<MyFace>();
				
				arrayIndex = -1;
				
				allNeighborsNormal = null;
//				vertexNormalDirty 	= true;
//				vertexNormal 		= new Vector3D(0,0,0);
			}
			
			
			/**
			 * Adds and registers the vertex with this vertexdata. The Vertex
			 * datas vertex is null at the start and has to be initilazied once here.
			 * 
			 * @param vertex the vertex
			 */
			public void setVertex(Vertex vertex){
				this.vertex = vertex;
			}
			
			
			/**
			 * Gets the vertex.
			 * 
			 * @return the vertex
			 * 
			 * Returns the vertex this vertex data wraps around.
			 */
			public Vertex getVertex() {
				return vertex;
			}
			
			
			//this is wrong here, the vertex normal when considering a crease angle
			//has to be calculated per face and per face vertex instead of per vertex here
			//alone. Because for one face the same vertex has to be smoothed with different
			//faces than for another face.
//			/**
//			 * Tells the VertexData, that the specified face is a neighbor which should
//			 * be used to calculate a smooth vertex normal later.
//			 * 
//			 * @param myFace
//			 */
//			public void addNewSmoothNeighbor(MyFace myFace){
//				if (! this.smoothNeighbors.contains(myFace)){
//					this.smoothNeighbors.add(myFace);
//					this.vertexNormalDirty = true;
//				}
//			}

//			/**
//			 * Calculates and returns the vertexnormal for this vertexdata,
//			 * using all the vertexes smooth neighbors and adding them up, then normalizing.
//			 * This should be called only after all the smooth neighbors were added.
//			 * 
//			 * @return
//			 * 	the normal 
//			 */
//			public Vector3D calcVertexNormalSmoothNeighbors() {
//				if (vertexNormalDirty){
//					vertexNormal = new Vector3D(0,0,0);
//					
//					//Add up face normals of smooth neighbors
//					for (int i = 0; i < smoothNeighbors.size(); i++) {
//						MyFace smoothNeighbor = smoothNeighbors.get(i);
//						vertexNormal.addLocal(smoothNeighbor.normal);
//					}
//					//Normalize in the end
//					vertexNormal.normalizeLocal();
//					vertexNormalDirty = false;
//					return vertexNormal;
//				}else{
//					return vertexNormal;
//				}
//			}
			
//			/**
//			 * @return
//			 *  whether the vertexdata has smooth neighbors 
//			 */
//			public boolean hasSmoothNeighborFaces(){
//				return !this.smoothNeighbors.isEmpty();
//			}
			
			
	
			/**
			 * Add and register the specified neighbor for this vertexdata.
			 * This also adds the face to the duplicates registered (added) with this vertexdata
			 * with different tex coords but same vertex pointer.
			 * 
			 * @param face the face
			 */
			public void addNeighborFace(MyFace face){
				if (!faces.contains(face))
					faces.add(face);
				//Auch duplicates face hinzuf�gen, da sie ja den gleichen vertex sharen
				//und f�r die normalenberechnung alle neighborfaces brauchen
				for (VertexData vdDuplicate : this.getDuplicateVertexWithDiffTexCoordsList()){
					vdDuplicate.addNeighborFace(face);
				}
			}
			
			/**
			 * Add and register the specified neighbors for this vertexdata and ints duplicates
			 * with different tex coords but same vertex pointer.
			 * 
			 * @param addFaces the add faces
			 */
			public void addNeighborFaces(ArrayList<MyFace> addFaces){
				for (MyFace currFace: addFaces){
					if (!faces.contains(currFace)){
						faces.add(currFace);
					}
				}
				//Auch duplicates faces hinzuf�gen, da sie ja den gleichen vertex sharen
				//und f�r die normalenberechnung alle neighborfaces brauchen
				for (VertexData vdDuplicate : this.getDuplicateVertexWithDiffTexCoordsList()){
					vdDuplicate.addNeighborFaces(addFaces);
				}
			}
			
			/**
			 * Gets the neighbor faces.
			 * 
			 * @return the neighbor faces
			 * 
			 * All neighbors of this vertex, so all faces this vertex is a part of
			 */
			public ArrayList<MyFace> getNeighborFaces(){
				return this.faces;
			}
			
			/**
			 * Calculates and returns the vertex normal considering
			 * _all_ the vertexes neighbors (smooth or not!). Results
			 * in an all smoothed model.
			 * 
			 * @return the vector3 d
			 * 
			 * the resulting vertex normal
			 */
			public Vector3D calcVertexNormalAllNeighbors(){
				if (allNeighborsNormal == null ){
					//Add up face normals of smooth neighbors
					Vector3D allNeighborNormal = new Vector3D(0,0,0);
                    for (MyFace neighbor : faces) {
                        allNeighborNormal.addLocal(neighbor.normal);

                        logger.debug("Vertex index:" + this.getArrayIndex() + " calcing in neighbor normal of face: " + neighbor.index);
                    }
					//Normalize in the end
					allNeighborNormal.normalizeLocal();
					allNeighborsNormal = allNeighborNormal;
					return allNeighborsNormal;
				}else{
					return allNeighborsNormal;
				}
			}
			
			
			
			
	///////////// Handle vertices with same vertex but different texture coordinates
	/**
	 * Returns the index of the already created duplicate with the same vertex and texture
	 * information we are checking with, or "-1" if no duplicate with that information exists.
	 * Then we have to create a new duplicate and register it to this Vertex Data's duplicate list.
	 * 
	 * @param vertex the vertex
	 * 
	 * @return the vert duplicate same vert diff texture coord list index
	 * 
	 * the index of the dupicate with the same tex coords or -1 if there is none
	 */
			public int getVertDuplicateSameVertDiffTextureCoordListIndex(Vertex vertex){
				//Go through list of all duplicates
        for (VertexData possibleDuplicate : duplicationsWithDiffTexCoords) {
            if (possibleDuplicate.equalsVertex(vertex)) {
                return possibleDuplicate.getArrayIndex();
            }
        }
				return -1;
			}
			
			/**
			 * Checks if the vertex with the same coordinates and texture coordinates was already added
			 * to this vertexData.
			 * 
			 * @param vertex the vertex
			 * 
			 * @return true, if equals vertex
			 */
			private boolean equalsVertex(Vertex vertex){
				return this.vertex.equalsVector(vertex);
			}//contains()
			
			/**
			 * Tell the vertexdata obj, that we have created a duplicate and saved it.
			 * 
			 * @param vd the vd
			 */
			public void registerCreatedDuplicateDiffTexCoords(VertexData vd){
				this.duplicationsWithDiffTexCoords.add(vd);
			}
			
			/**
			 * Gets the duplicate vertex with diff tex coords list.
			 * 
			 * @return the duplicate vertex with diff tex coords list
			 */
			private ArrayList<VertexData> getDuplicateVertexWithDiffTexCoordsList(){
				return this.duplicationsWithDiffTexCoords;
			}
	///////////// Handle vertices with same vertex but different texture coordinates
			
	
	///////////// Handle vertices with same vertex but different Vertex normal // This is only relevant if a crease angle is used!
			/**
	 * Returns the vertex normal that has been calculated and set
	 * with a crease angle for a group of faces for which this normal
	 * is the same.
	 * 
	 * @return the unique vertex normal
	 */
			public Vector3D getUniqueVertexNormal(){
				return uniqueVertexNormal;
			}
			
			
			/**
			 * Sets the vertex normal for this vertex(data). In the crease angle
			 * calculation process. May only set it once. Different normals belonging
			 * to the same vertex have to be put in another vertex data object!
			 * The vertex normal is calculated in the faces and then set here.
			 * 
			 * @param newVertexNormal the new vertex normal
			 */
			public void setUniqueVertexNormal(Vector3D newVertexNormal) {
				this.uniqueVertexNormal = newVertexNormal;
			}
			
			/**
			 * The crease angle cal process tell this vertex data that it created another vertex data
			 * because in this one, the vertex is the same but the normal differs -> duplicate the vertex!.
			 * 
			 * @param vdWithDiffNormal the vd with diff normal
			 */
			public void registerCreatedDuplicateDiffNormal(VertexData vdWithDiffNormal) {
				this.duplicationsWithDiffNormal.add(vdWithDiffNormal);
			}
			
			/**
			 * Asks the vertex data if a duplicate of itself with a different normal was created and registered as a duplicate.
			 * <br>If there is a duplicate already registered with the same normal to be checked with, we get the index of that duplicate
			 * in the overall vertex data list and can point the face to the duplicate so we dont have to create another vertexdata.
			 * <br>It is assumed that the normal we are checking against belongs to a vertex with the same values as this Vertex datas vertex!
			 * 
			 * @param normalToCheckWith the normal to check with
			 * 
			 * @return the vert duplicate same vert diff normal list index
			 * 
			 * the index of the duplicate or -1 if there is none
			 */
			public int getVertDuplicateSameVertDiffNormalListIndex(Vector3D normalToCheckWith) {
	//			Go through list of all duplicates
                for (VertexData possibleDuplicate : duplicationsWithDiffNormal) {
                    if (possibleDuplicate.getUniqueVertexNormal().equalsVectorWithTolerance(normalToCheckWith, ToolsMath.ZERO_TOLERANCE)) {
                        return possibleDuplicate.getArrayIndex();
                    }
                }
				return -1;
			}
	///////////// Handle vertices with same vertex but different Vertex normal
			
			
			
			/**
	 * Tell the vertexdata which index its at in a list of vertexdatas for example.
	 * 
	 * @param i the i
	 */
			public void setArrayIndex(int i){
				this.arrayIndex = i;
			}
			
			/**
			 * Gets the array index.
			 * 
			 * @return the array index
			 * 
			 * returns the index, the vertexData is at in the overall vertex data list.
			 * ->This has to be set manually by calling <code>setArrayIndex</code> first
			 */
			public int getArrayIndex(){
				return this.arrayIndex;
			}
			
			/**
			 * Gets the faces contained in.
			 * 
			 * @return the faces contained in
			 * 
			 * a list of faces, this vertex(-data) is a part of (all its neighbors)
			 * NOTE: not at all stages of the algorithm returns all real neighbors. Some may  be deleted already.
			 */
			public ArrayList<MyFace> getFacesContainedIn(){
				return this.faces;
			}
		}//Vertexdata class

		
		

		/*----------------------------------------------------------------------------*/
		
		
	
}
