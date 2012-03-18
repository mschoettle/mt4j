/*
 * $RCSfile: ObjectFile.java,v $
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision: 1.6 $
 * $Date: 2009/12/21 13:55:05 $
 * $State: Exp $
 */


package org.mt4j.util.modelImporter.fileObj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.TriangleNormalGenerator;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.modelImporter.ModelImporterFactory;

import processing.core.PApplet;



/**
 * The ObjectFile class implements the Loader interface for the Wavefront
 * .obj file format, a standard 3D object file format created for use with
 * Wavefront's Advanced Visualizer (tm) and available for purchase from
 * Viewpoint DataLabs, as well as other 3D model companies.  Object Files
 * are text based
 * files supporting both polygonal and free-form geometry (curves 
 * and surfaces).  The Java 3D .obj file loader supports a subset of the
 * file format, but it is enough to load almost all commonly available 
 * Object Files.  Free-form geometry is not supported.</p>
 *
 * The Object File tokens currently supported by this loader are:</p>
 * <code>v <i>float</i> <i>float</i> <i>float</i></code></p>
 *   <dl><dd>A single vertex's geometric position in space.  The first vertex
 *   listed in the file has index 1,
 *   and subsequent vertices are numbered sequentially.</dl></p>
 * <code>vn <i>float</i> <i>float</i> <i>float</i></code></p>
 *   <dl><dd>A normal.  The first normal in the file is index 1, and 
 *   subsequent normals are numbered sequentially.</dl></p>
 * <code>vt <i>float</i> <i>float</i></code></p>
 *   <dl><dd>A texture coordinate.  The first texture coordinate in the file is
 *   index 1, and subsequent normals are numbered sequentially.</dl></p>
 * <code>f <i>int</i> <i>int</i> <i>int</i> . . .</code></p>
 *   <dl><dd><i><b>or</b></i></dl></p>
 * <code>f <i>int</i>/<i>int</i> <i>int</i>/<i>int</i> <i>int</i>/<i>int</i> . . .</code></p>
 *   <dl><dd><i><b>or</i></b></dl></p>
 * <code>f <i>int</i>/<i>int</i>/<i>int</i> <i>int</i>/<i>int</i>/<i>int</i> <i>int</i>/<i>int</i>/<i>int</i> . . .</code></p>
 *   <dl><dd>A polygonal face.  The numbers are indexes into the arrays of
 *   vertex positions, texture coordinates, and normals respectively.
 *   There is no maximum number of vertices that a single polygon may
 *   contain.  The .obj file specification says that each face must
 *   be flat and convex, but if the TRIANGULATE flag is sent to the
 *   ObjectFile constructor, each face will be triangulated by the
 *   Java 3D Triangulator, and therefore may be concave.
 *   A number may be omitted if, for example, texture coordinates are
 *   not being defined in the model.  Numbers are normally positive
 *   indexes, but may also be negative.  An index of -1 means the last
 *   member added to the respective array, -2 is the one before that,
 *   and so on.</dl></p>
 * <code>g <i>name</i></code></p>
 *   <dl><dd>Faces defined after this token will be added to the named group.
 *   These geometry groups are returned as separated Shape3D objects
 *   attached to the parent SceneGroup.  Each named Shape3D will also
 *   be in the Hashtable returned by Scene.getNamedObjects().  It is
 *   legal to add faces to a group, switch to another group, and then 
 *   add more faces to the original group by reissuing the same name
 *   with the g token.  If faces are added to the model before the g
 *   token is seen, the faces are put into the default group called
 *   "default."</dl></p>
 * <code>s <i>int</i></code></p>
 *   <dl><dd><i><b>or</i></b></dl></p>
 * <code>s off</code></p>
 *   <dl><dd>If the vn token is not used in the file to specify vertex normals
 *   for the model, this token may be used to put faces into groups
 *   for normal calculation ("smoothing groups") in the same manner as
 *   the 'g' token
 *   is used to group faces geometrically.  Faces in the same smoothing
 *   group will have their normals calculated as if they are part of
 *   the same smooth surface.  To do this, we use the Java 3D NormalGenerator
 *   utility with the creaseAngle parameter set to PI (180 degrees - 
 *   smooth shading, no creases) or to whatever the user has set the 
 *   creaseAngle.  Faces in group 0 or 'off' use a 
 *   creaseAngle of zero, meaning there is no smoothing (the normal
 *   of the face is used at all vertices giving the surface a faceted
 *   look; there will be a
 *   crease, or "Hard Edge," between each face in group zero).  There is
 *   also an implied hard edge <i>between</i> each smoothing group, where they
 *   meet each other.</p>
 *   </p>
 *   If neither the vn nor the s token is used in the file, then normals
 *   are calculated using the creaseAngle set in the contructor.
 *   Normals are calculated on each geometry
 *   group separately, meaning there will be a hard edge between each
 *   geometry group.</dl></p>
 *   </p>
 * <code>usemtl <i>name</i></code></p>
 *   <dl><dd>The current (and subsequent) geometry groups (specified with
 *   the 'g' token) have applied
 *   to them the named material property.  The following set of material
 *   properties are available by default:</dl></p>
 *   <pre>
 *     amber           amber_trans       aqua            aqua_filter
 *     archwhite       archwhite2        bflesh          black
 *     blondhair       blue_pure         bluegrey        bluetint
 *     blugrn          blutan            bluteal         bone
 *     bone1           bone2             brass           brnhair
 *     bronze          brown             brownlips       brownskn
 *     brzskin         chappie           charcoal        deepgreen
 *     default         dkblue            dkblue_pure     dkbrown
 *     dkdkgrey        dkgreen           dkgrey          dkorange
 *     dkpurple        dkred             dkteal          emerald
 *     fgreen          flaqua            flblack         flblonde
 *     flblue_pure     flbrown           fldkblue_pure   fldkdkgrey
 *     fldkgreen       fldkgreen2        fldkgrey        fldkolivegreen
 *     fldkpurple      fldkred           flesh           fleshtransparent
 *     flgrey          fllime            flltbrown       flltgrey
 *     flltolivegreen  flmintgreen       flmustard       florange
 *     flpinegreen     flpurple          flred           fltan
 *     flwhite         flwhite1          flyellow        glass
 *     glassblutint    glasstransparent  gold            green
 *     greenskn        grey              hair            iris
 *     jetflame        lavendar          lcdgreen        lighttan
 *     lighttan2       lighttan3         lighttannew     lightyellow
 *     lime            lips              ltbrown         ltgrey
 *     meh             metal             mintgrn         muscle
 *     navy_blue       offwhite.cool     offwhite.warm   olivegreen
 *     orange          pale_green        pale_pink       pale_yellow
 *     peach           periwinkle        pink            pinktan
 *     plasma          purple            red             redbrick
 *     redbrown        redorange         redwood         rubber
 *     ruby            sand_stone        sapphire        shadow
 *     ship2           silver            skin            sky_blue
 *     smoked_glass    tan               taupe           teeth
 *     violet          white             yellow          yellow_green
 *     yellowbrt       yelloworng
 *   </pre>
 * <code>mtllib <i>filename</i></code></p>
 *   <dl><dd>Load material properties from the named file.  Materials
 *   with the same name as the predefined materials above will override
 *   the default value.  Any directory path information in (filename)
 *   is ignored.  The .mtl files are assumed to be in the same directory 
 *   as the .obj file.  If they are in a different directory, use
 *   Loader.setBasePath() (or Loader.setBaseUrl() ).  The format of the
 *   material properties files
 *   are as follows:</p>
 *   <code>newmtl <i>name</i></code></p>
 *     <dl><dd>Start the definition of a new named material property.</dl></p>
 *   <code>Ka <i>float</i> <i>float</i> <i>float</i></code></p>
 *     <dl><dd>Ambient color.</dl></p>
 *   <code>Kd <i>float</i> <i>float</i> <i>float</i></code></p>
 *     <dl><dd>Diffuse color.</dl></p>
 *   <code>Ks <i>float</i> <i>float</i> <i>float</i></code></p>
 *     <dl><dd>Specular color.</dl></p>
 *   <code>illum <i>(0, 1, or 2)</i></code></p>
 *     <dl><dd>0 to disable lighting, 1 for ambient & diffuse only (specular
 *     color set to black), 2 for full lighting.</dl></p>
 *   <code>Ns <i>float</i></code></p>
 *     <dl><dd>Shininess (clamped to 1.0 - 128.0).</dl></p>
 *   <code>map_Kd <i>filename</i></code></p>
 *     <dl><dd>Texture map.  Supports .rgb, .rgba, .int, .inta, .sgi, and
 *     .bw files in addition to those supported by
 *     <a href="../../utils/image/TextureLoader.html">TextureLoader</a>.
 *     </dl></dl></p>
 */

public class ModelObjFileFactory  extends ModelImporterFactory {
	// 0=Input file assumed good
	// 1=Input file checked for inconsistencies
	// 2=path names
	// 4=flags
	// 8=Timing Info
	// 16=Tokens
	// 32=Token details (use with 16)
	// 64=limits of model coordinates
	private static final int DEBUG = 0;

    // These are the values to be used in constructing the
    // load flags for the loader.  Users should OR the selected
    // values together to construct an aggregate flag integer
    // (see the setFlags() method).  Users wishing to load all
    // data in a file should use the LOAD_ALL specifier.
    
    /** This flag enables the loading of light objects into the scene.*/
    public static final int LOAD_LIGHT_NODES		= 1;

    /** This flag enables the loading of fog objects into the scene.*/
    public static final int LOAD_FOG_NODES			= 2;

    /** This flag enables the loading of background objects into the scene.*/
    public static final int LOAD_BACKGROUND_NODES	= 4;

    /** This flag enables the loading of behaviors into the scene.*/
    public static final int LOAD_BEHAVIOR_NODES		= 8;

    /** This flag enables the loading of view (camera) objects into
     * the scene.*/
    public static final int LOAD_VIEW_GROUPS		= 16;

    /** This flag enables the loading of sound objects into the scene.*/
    public static final int LOAD_SOUND_NODES		= 32;

    /** This flag enables the loading of all objects into the scene.*/
    public static final int LOAD_ALL			= 0xffffffff;

    
	/**
	 * Flag sent to constructor.  The object's vertices will be changed
	 * so that the object is centered at (0,0,0) and the coordinate
	 * positions are all in the range of (-1,-1,-1) to (1,1,1).
	 */
	public static final int RESIZE = LOAD_SOUND_NODES << 1;

	/**
	 * Flag sent to constructor.  The Shape3D object will be created
	 * by using the GeometryInfo POLYGON_ARRAY primitive, causing
	 * them to be Triangulated by GeometryInfo.  Use
	 * this if you suspect concave or other non-behaving polygons
	 * in your model.
	 */
	public static final int TRIANGULATE = RESIZE << 1;

	/**
	 * Flag sent to constructor.  Use if the vertices in your .obj
	 * file were specified with clockwise winding (Java 3D wants
	 * counter-clockwise) so you see the back of the polygons and
	 * not the front.  Calls GeometryInfo.reverse().
	 */
	public static final int REVERSE = TRIANGULATE << 1;

	/**
	 * Flag sent to contructor.  After normals are generated the data
	 * will be analyzed to find triangle strips.  Use this if your
	 * hardware supports accelerated rendering of strips.
	 */
	public static final int STRIPIFY = REVERSE << 1;
	

	private static final char BACKSLASH = '\\';

	private int flags;
	private String basePath = null;
	private URL baseUrl = null;
	private boolean fromUrl = false;
	private float radians;

	// First, lists of points are read from the .obj file into these arrays. . .
	private ArrayList<Vertex> coordList;	// Holds Point3f
	private ArrayList<float[]> texList;		// Holds TexCoord2f
	private ArrayList<Vector3D> normList;		// Holds Vector3f

	// . . . and index lists are read into these arrays.
	private ArrayList<Integer> coordIdxList;	// Holds Integer index into coordList
	private ArrayList<Integer> texIdxList;	// Holds Integer index into texList
	private ArrayList<Integer> normIdxList;	// Holds Integer index into normList

	// The length of each face is stored in this array.
	private ArrayList<Integer> stripCounts;	// Holds Integer

	// Each face's Geometry Group membership is kept here. . .
	private HashMap<Integer, String> groups;		// key=Integer index into stripCounts
	// value=String name of group
	private String curGroup;

	// . . . and Smoothing Group membership is kept here
	private HashMap<Integer, String> sGroups;		// key=Integer index into stripCounts
	// value=String name of group
	private String curSgroup;

	// The name of each group's "usemtl" material property is kept here
	private HashMap<String, String> groupMaterials;	// key=String name of Group
	// value=String name of material


	// After reading the entire file, the faces are converted into triangles.
	// The Geometry Group information is converted into these structures. . .
	private HashMap triGroups;		// key=String name of group
	// value=ArrayList of Integer
	//       indices into coordIdxList
	private ArrayList curTriGroup;

	// . . . and Smoothing Group info is converted into these.
	private HashMap triSgroups;		// key=String name of group
	// value=ArrayList of Integer
	// indices into coordIdxList
	private ArrayList curTriSgroup;


	// Finally, coordList, texList, and normList are converted to arrays for
	// use with GeometryInfo
	private Vertex coordArray[] = null;
	private Vector3D normArray[] = null;
	
//	private TexCoord2f texArray[] = null;
	
	private float[][] texArray = null;

	// Used for debugging
	private long time;

	private ObjectFileMaterials materials = null;

	private PApplet pa;

	private HashMap<String, Group> groupNameToGroupObj;

//	private boolean flipTextureY;
	
	private boolean debugNormalGenerator;


	/**
	 * Default constructor.  Crease Angle set to default of
	 * 44 degrees (see NormalGenerator utility for details).  Flags
	 * set to zero (0).
	 */
	public ModelObjFileFactory() {
		this(0, -1.0f);
	} // End of ObjectFile()

	
	/**
	 * Constructor.  Crease Angle set to default of
	 * 44 degrees (see NormalGenerator utility for details).
	 * @param flags The constants from above or from
	 * com.sun.j3d.IModelLoaders.IModelLoader, possibly "or'ed" (|) together.
	 */
	private ModelObjFileFactory(int flags) {
		this(flags, -1.0f);
	} // End of ObjectFile(int)

	
	/**
	 * Constructor.
	 *
	 * @param flags The constants from above or from
	 * com.sun.j3d.IModelLoaders.IModelLoader, possibly "or'ed" (|) together.
	 * @param radians Ignored if the vn token is present in the model (user
	 * normals supplied).  Otherwise, crease angle to use within smoothing
	 * groups, or within geometry groups if the s token isn't present either.
	 */
	private ModelObjFileFactory(int flags, float radians) {
		this.setFlags(flags);
		this.radians = radians;
		
		groupNameToGroupObj = new HashMap<String, Group>();
		
		debugNormalGenerator = false;
	} // End of ObjectFile(int, float)



	/**
	 * Load model.
	 * 
	 * @param pa the pa
	 * @param filename the filename
	 * @param creaseAngle the crease angle
	 * @param flipTextureY the flip texture y
	 * @param flipTextureX the flip texture x
	 * 
	 * @return the mT triangle mesh[]
	 * 
	 * @throws FileNotFoundException the file not found exception
	 */
	public MTTriangleMesh[] loadModelImpl(PApplet pa, String filename, float creaseAngle, boolean flipTextureY, boolean flipTextureX) throws FileNotFoundException {
		this.pa = pa;
		this.setBasePathFromFilename(filename);
		Reader reader = null;
		
		File file = new File(filename);
		if (file.exists()){
			reader = new BufferedReader(new FileReader(filename));
		}else{
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);  
			if (stream == null){
				stream = pa.getClass().getResourceAsStream(filename);
			}
			if (stream == null){
				throw new FileNotFoundException("File not found: " + filename);
			}
			reader = new BufferedReader(new InputStreamReader(stream)); 
		}
//			throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
		
		return load(reader,creaseAngle, flipTextureY, flipTextureX);
	} // End of load(String)



	/**
	 * The Object File is loaded from the already opened file.
	 * To attach the model to your scene, call getSceneGroup() on
	 * the Scene object passed back, and attach the returned
	 * BranchGroup to your scene graph.  For an example, see 
	 * j3d-examples/ObjLoad/ObjLoad.java.
	 * @param flipTextureX 
	 */
	private MTTriangleMesh[] load(Reader reader, float creaseAngle, boolean flipTextureY, boolean flipTextureX) throws ParsingErrorException {
		// ObjectFileParser does lexical analysis
		ObjectFileParser st = new ObjectFileParser(reader);

		coordList 		= new ArrayList<Vertex>();
		texList 		= new ArrayList<float[]>();
		normList 		= new ArrayList<Vector3D>();
		coordIdxList 	= new ArrayList<Integer>();
		texIdxList 		= new ArrayList<Integer>();
		normIdxList 	= new ArrayList<Integer>();
		groups 			= new HashMap<Integer, String>(50);
		curGroup 		= "default";
		sGroups 		= new HashMap<Integer, String>(50);
		curSgroup 		= null;
		stripCounts 	= new ArrayList<Integer>();
		groupMaterials 	= new HashMap<String, String>(50);
		groupMaterials.put(curGroup, "default");
		materials 		= new ObjectFileMaterials();
		materials.pa 	= this.pa;

		//FIXME ADDED
		Group group = new Group(curGroup);
		groupNameToGroupObj.put(curGroup, group);

		time = 0L;
		if ((DEBUG & 8) != 0) {
			time = System.currentTimeMillis();
		}

		//GO THROUGH THE FILE
		this.readFile(st);

		if ((DEBUG & 8) != 0) {
			time = System.currentTimeMillis() - time;
			System.out.println("Read file: " + time + " ms");
			time = System.currentTimeMillis();
		}


//		if ((flags & RESIZE) != 0) 
		resize(); //FIXME resize default ja / nein?
		return createMeshesFromGroups(creaseAngle, flipTextureY, flipTextureX);
	} // End of load(Reader)


	/**
	 * 
	 * @param creaseAngle 
	 * @param flipTextureY
	 * @param flipTextureX 
	 * @return
	 */
	private MTTriangleMesh[] createMeshesFromGroups(float creaseAngle, boolean flipTextureY, boolean flipTextureX){
		ArrayList<MTTriangleMesh> meshList = new ArrayList<MTTriangleMesh>();

		TriangleNormalGenerator normalGenerator = new TriangleNormalGenerator();
		normalGenerator.setDebug(this.debugNormalGenerator);

		//Go through all groups and create the meshes
		int totalNumVerts = 0;
        for (String s : groupNameToGroupObj.keySet()) {
            String currentGroupName = s;
            Group currentGroup = groupNameToGroupObj.get(currentGroupName);

            //Compile vertices/indices/texture arrays just for this group as a teilmenge from all with recalculated indices
            currentGroup.compileItsOwnLists(coordList, texList);

            //Get the new arrays
            Vertex[] vertices = currentGroup.getGroupVertices(); //currentGroup.verticesForGroup.toArray(new Vertex[currentGroup.verticesForGroup.size()]);
            short[] indices = currentGroup.getIndexArray(); //currentGroup.indexArray;
            float[][] textureCoords = currentGroup.getGroupTexCoords(); //currentGroup.texCoordsForGroup.toArray(new float[currentGroup.texCoordsForGroup.size()][]);
            int[] texIndices = currentGroup.getTexCoordIndices(); //currentGroup.texCoordIndexArray;

            System.out.println("\nGroup: \"" + currentGroup.name + "\" ->Vertices: " + currentGroup.verticesForGroup.size() + " ->TextureCoords: " + currentGroup.texCoordsForGroup.size() + " ->Indices: " + currentGroup.indexArray.length + " ->Texcoord Indices: " + currentGroup.texCoordIndexArray.length);
            System.out.println();

            if (vertices.length > 2) {
                GeometryInfo geometry = null;

                //Load as all vertex normals smoothed if creaseAngle == 180;
                if (creaseAngle == 180) {
                    geometry = normalGenerator.generateSmoothNormals(pa, vertices, indices, textureCoords, texIndices, creaseAngle, flipTextureY, flipTextureX);
                } else {
                    geometry = normalGenerator.generateCreaseAngleNormals(pa, vertices, indices, textureCoords, texIndices, creaseAngle, flipTextureY, flipTextureX);
                }

                MTTriangleMesh mesh = new MTTriangleMesh(pa, geometry);

                if (mesh != null) {
                    mesh.setName(currentGroupName);
                    //Assign texture and material
                    String matName = groupMaterials.get(currentGroupName);
//                    materials.assignMaterial(((PGraphicsOpenGL) pa.g).gl, matName, mesh);
                    materials.assignMaterial(PlatformUtil.getGL(), matName, mesh);

                    if (mesh.getTexture() != null) {
                        mesh.setTextureEnabled(true);
                    } else {
                        System.out.println("No texture could be assigned to mesh.");
                    }
                    meshList.add(mesh);
                } else {
                    System.err.println("Mesh not created, returned null from meshDenormalization.");
                }
            } else {
                System.out.println("Group not created, < 2 vertices..");
            }
            totalNumVerts += currentGroup.verticesForGroup.size();
        }
		System.out.println("All groups on .obj file have total number of vertices: " + totalNumVerts);

		//Cleanup
		stripCounts = null;
		groups 		= null;
		sGroups 	= null;
		groupNameToGroupObj.clear();

		return meshList.toArray(new MTTriangleMesh[meshList.size()]);
	}





	/**
	 * readFile
	 *
	 *    Read the model data from the file.
	 */
	void readFile(ObjectFileParser st) throws ParsingErrorException {
		int t;
	
		st.getToken();
		while (st.ttype != ObjectFileParser.TT_EOF) {
	
			// Print out one token for each line
			if ((DEBUG & 16) != 0) {
				System.out.print("Token ");
				if (st.ttype == ObjectFileParser.TT_EOL) System.out.println("EOL");
				else if (st.ttype == ObjectFileParser.TT_WORD)
					System.out.println(st.sval);
				else System.out.println((char)st.ttype);
			}
	
			if (st.ttype == ObjectFileParser.TT_WORD) {
				if (st.sval.equals("v")) {
					readVertex(st);
				} else if (st.sval.equals("vn")) {
					readNormal(st);
				} else if (st.sval.equals("vt")) {
					readTexture(st);
				} else if (st.sval.equals("f")) {
					readFace(st);
				} else if (st.sval.equals("fo")) {  // Not sure what the dif is
					readFace(st);
				} else if (st.sval.equals("g")) {
					readPartName(st);
				} else if (st.sval.equals("s")) {
					readSmoothingGroup(st);
				} else if (st.sval.equals("p")) {
					st.skipToNextLine();
				} else if (st.sval.equals("l")) {
					st.skipToNextLine();
				} else if (st.sval.equals("mtllib")) {
					loadMaterialFile(st);
				} else if (st.sval.equals("usemtl")) {
					readMaterialName(st);
				} else if (st.sval.equals("maplib")) {
					st.skipToNextLine();
				} else if (st.sval.equals("usemap")) {
					st.skipToNextLine();
				} else {
					/*
					throw new ParsingErrorException(
							"Unrecognized token, line " + st.lineno());
							*/
					System.err.println("Unrecognized token, line " + st.lineno());
//					st.getToken();
//					st.skipToNextLine();
				}
			}
			
			if (st.ttype == StreamTokenizer.TT_EOF) {
				break;
			}
	
			st.skipToNextLine();
	
			// Get next token
			st.getToken();
		}
	} // End of readFile


	void readVertex(ObjectFileParser st) throws ParsingErrorException {
		Vertex p = new Vertex();

		st.getNumber();
		p.x = (float)st.nval;
		st.getNumber();
		p.y = (float)st.nval;
		st.getNumber();
		p.z = (float)st.nval;

		if ((DEBUG & 32) != 0)
			System.out.println("  (" + p.x + "," + p.y + "," + p.z + ")");

		st.skipToNextLine();

		// Add this vertex to the array
		coordList.add(p);
	} // End of readVertex


	/**
	 * readNormal
	 */
	void readNormal(ObjectFileParser st) throws ParsingErrorException {
		Vector3D p = new Vector3D();

		st.getNumber();
		p.x = (float)st.nval;
		st.getNumber();
		p.y = (float)st.nval;
		st.getNumber();
		p.z = (float)st.nval;

		if ((DEBUG & 32) != 0)
			System.out.println("  (" + p.x + "," + p.y + "," + p.z + ")");

		st.skipToNextLine();

		// Add this vertex to the array
		normList.add(p);
	} // End of readNormal


	/**
	 * readTexture
	 */
	void readTexture(ObjectFileParser st) throws ParsingErrorException {
//		TexCoord2f p = new TexCoord2f();
		float[] p = new float[2];

		st.getNumber();
//		p.x = (float)st.nval;
		p[0] = (float)st.nval;
		st.getNumber();
//		p.y = (float)st.nval;
		p[1] = (float)st.nval;

		if ((DEBUG & 32) != 0)
			System.out.println("  (" + p[0] + "," + p[1] + ")");

		st.skipToNextLine();

		// Add this vertex to the array
		texList.add(p);
	} // End of readTexture


	/**
	 * readFace
	 *
	 *    Adds the indices of the current face to the arrays.
	 *
	 *    ViewPoint files can have up to three arrays:  Vertex Positions,
	 *    Texture Coordinates, and Vertex Normals.  Each vertex can
	 *    contain indices into all three arrays.
	 */
	void readFace(ObjectFileParser st) throws ParsingErrorException {
		int vertIndex, 
			texIndex = 0, 
			normIndex = 0;
		
		int count = 0;

		//   There are n vertices on each line.  Each vertex is comprised
		//   of 1-3 numbers separated by slashes ('/').  The slashes may
		//   be omitted if there's only one number.

		st.getToken();

		ArrayList<Integer> faceVertIndices = new ArrayList<Integer>();
		
		ArrayList<Integer> faceTexIndices  = new ArrayList<Integer>();
		
		
		while (st.ttype != StreamTokenizer.TT_EOF &&
				st.ttype != StreamTokenizer.TT_EOL
		) {
			// First token is always a number (or EOL)
			st.pushBack();
			st.getNumber();
			vertIndex = (int)st.nval - 1;
			
			if (vertIndex < 0) 
				vertIndex += coordList.size() + 1;
			
			coordIdxList.add(vertIndex);
			
			//	MODIFIED
			faceVertIndices.add(vertIndex);

			// Next token is a slash, a number, or EOL.  Continue on slash
			st.getToken();
			if (st.ttype == '/') {

				// If there's a number after the first slash, read it
				st.getToken();
				if (st.ttype == StreamTokenizer.TT_WORD) {
					// It's a number
					st.pushBack();
					st.getNumber();
					texIndex = (int)st.nval - 1;
					
					if (texIndex < 0) 
						texIndex += texList.size() + 1;
					
					texIdxList.add(texIndex);
					
					//MODIFIED
					faceTexIndices.add(texIndex);
					
					st.getToken();
				}

				// Next token is a slash, a number, or EOL.  Continue on slash
				if (st.ttype == '/') {
					// There has to be a number after the 2nd slash
					st.getNumber();
					normIndex = (int)st.nval - 1;
					if (normIndex < 0) 
						normIndex += normList.size() + 1;
					
					normIdxList.add(normIndex);
					st.getToken();
				}
			}
			if ((DEBUG & 32) != 0) {
				System.out.println("  " + vertIndex + '/' + texIndex + '/' + normIndex);
			}
			count++;
		}

		Integer faceNum = stripCounts.size();
		stripCounts.add(count);

		// Add face to current groups
		groups.put(faceNum, curGroup);
		if (curSgroup != null) sGroups.put(faceNum, curSgroup);
		

		//TODO really Triangulate any faces with more than 4 vertices
		
		//Add Face to current group
		//"Triangulate" the Quad if the objecfile defines quad faces
		//instead of triangle faces, just make 2 triangles out of the quad
		Group currGroup = groupNameToGroupObj.get(curGroup);
		if (currGroup != null){
			if (faceVertIndices.size() == 3){
				//Create my own face
				AFace face = new AFace();
				face.p0 = faceVertIndices.get(0);
				face.p1 = faceVertIndices.get(1);
				face.p2 = faceVertIndices.get(2);
				
				if (faceTexIndices.size() >= 3){
					face.t0 = faceTexIndices.get(0);
					face.t1 = faceTexIndices.get(1);
					face.t2 = faceTexIndices.get(2);
				}
				currGroup.addFace(face);
			}else if (faceVertIndices.size() == 4){
				AFace face1 = new AFace();
				face1.p0 = faceVertIndices.get(0);
				face1.p1 = faceVertIndices.get(1);
				face1.p2 = faceVertIndices.get(2);
				
				AFace face2 = new AFace();
				face2.p0 = faceVertIndices.get(0);
				face2.p1 = faceVertIndices.get(2);
				face2.p2 = faceVertIndices.get(3);
				
				if (faceTexIndices.size() == 4){
					face1.t0 = faceTexIndices.get(0);
					face1.t1 = faceTexIndices.get(1);
					face1.t2 = faceTexIndices.get(2);
					
					face2.t0 = faceTexIndices.get(0);
					face2.t1 = faceTexIndices.get(2);
					face2.t2 = faceTexIndices.get(3);
				}
				currGroup.addFace(face1);
				currGroup.addFace(face2);
			}else{
				System.err.println("Obj-Loader only supports faces with 3 or 4 vertices per face!");
			}
		}else{
			System.err.println("CURRENT GROUP IS NULL! SOMETHINGS WRONG!");
		}
		
		if (st.ttype == StreamTokenizer.TT_EOF) {
			return;
		}
		
		// In case we exited early
		st.skipToNextLine();
	} // End of readFace


	/**
	 * readPartName
	 */
	void readPartName(ObjectFileParser st) {
		st.getToken();

		// Find the Material Property of the current group
		String curMat = groupMaterials.get(curGroup);

		// New faces will be added to the curGroup
		if (st.ttype != ObjectFileParser.TT_WORD) 
			curGroup = "default";
		else 
			curGroup = st.sval;
		
		if ((DEBUG & 32) != 0) 
			System.out.println("  Changed to group " + curGroup);

		// See if this group has Material Properties yet
		if (groupMaterials.get(curGroup) == null) {
			// It doesn't - carry over from last group
			groupMaterials.put(curGroup, curMat);
		}

		//FIXME ADDED!
		if (groupNameToGroupObj.get(curGroup) == null){
			Group group = new Group(curGroup);
			groupNameToGroupObj.put(curGroup, group);
			
		}
		
		st.skipToNextLine();
	} // End of readPartName


	/**
	 * readMaterialName
	 */
	void readMaterialName(ObjectFileParser st) throws ParsingErrorException {
		st.getToken();
		if (st.ttype == ObjectFileParser.TT_WORD) {
			String useMaterialName = st.sval;
			
			//////FIXME ADDED! added, to group by material if no groups are in obj file!
//			if (curGroup.equalsIgnoreCase("default")){
				curGroup = useMaterialName;
				
				if ((DEBUG & 32) != 0) 
					System.out.println("  Changed to group " + curGroup);
				
				if (groupNameToGroupObj.get(curGroup) == null){
					Group group = new Group(curGroup);
					groupNameToGroupObj.put(curGroup, group);
				}
				
//				// See if this group has Material Properties yet
//				if (groupMaterials.get(curGroup) == null) {
//					// It doesn't - carry over from last group
//					groupMaterials.put(curGroup, curMat);
//				}
//			}
			//////// added to group by material if no groups are in obj file!
			
			groupMaterials.put(curGroup, useMaterialName);
			if ((DEBUG & 32) != 0) {
				System.out.println("  Material Property " + st.sval +
						" assigned to group " + curGroup);
			}
		}
		st.skipToNextLine();
	} // End of readMaterialName


	/**
	 * loadMaterialFile
	 *
	 *	Both types of slashes are returned as tokens from our parser,
	 *	so we go through the line token by token and keep just the
	 *	last token on the line.  This should be the filename without
	 *	any directory info.
	 */
	void loadMaterialFile(ObjectFileParser st) throws ParsingErrorException {
		String s = null;

		// Filenames are case sensitive
		st.lowerCaseMode(false);

		// Get name of material file (skip path)
		do {
			st.getToken();
			if (st.ttype == ObjectFileParser.TT_WORD) s = st.sval;
		} while (st.ttype != ObjectFileParser.TT_EOL);

		materials.readMaterialFile(basePath, s);

		st.lowerCaseMode(true);
		st.skipToNextLine();
	} // End of loadMaterialFile


	/**
	 * readSmoothingGroup
	 */
	void readSmoothingGroup(ObjectFileParser st) throws ParsingErrorException {
		st.getToken();
		if (st.ttype != ObjectFileParser.TT_WORD) {
			st.skipToNextLine();
			return;
		}
		if (st.sval.equals("off")) curSgroup = "0";
		else curSgroup = st.sval;
		if ((DEBUG & 32) != 0) System.out.println("  Smoothing group " + curSgroup);
		st.skipToNextLine();
	} // End of readSmoothingGroup


	/**
	 * Takes a file name and sets the base path to the directory
	 * containing that file.
	 */
	private void setBasePathFromFilename(String fileName) {
		if (fileName.lastIndexOf(AbstractMTApplication.separator) == -1 && fileName.lastIndexOf(java.io.File.separator) == -1) {
			// No path given - current directory
			setBasePath("");
		} else{
			if (fileName.lastIndexOf(AbstractMTApplication.separator) != -1 ) {
				setBasePath(
						fileName.substring(0, fileName.lastIndexOf(AbstractMTApplication.separator)));
			}else if (fileName.lastIndexOf(java.io.File.separator) != -1){
				setBasePath(
						fileName.substring(0, fileName.lastIndexOf(java.io.File.separator)));
			}
		}
			
		
		/*
		if (fileName.lastIndexOf(java.io.File.separator) == -1) {
			// No path given - current directory
			setBasePath("." + java.io.File.separator);
		} else {
			setBasePath(
					fileName.substring(0, fileName.lastIndexOf(java.io.File.separator)));
		}
		*/
	} // End of setBasePathFromFilename


	/**
	 * getLimits
	 *
	 * Returns an array of Vertex which form a bounding box around the
	 * object.  Element 0 is the low value, element 1 is the high value.
	 * See normalize() below for an example of how to use this method.
	 */
	private Vertex[] getLimits() {
		Vertex cur_vtx = new Vertex();

		// Find the limits of the model
		Vertex[] limit = new Vertex[2];
		limit[0] = new Vertex(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		limit[1] = new Vertex(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        for (Vertex aCoordList : coordList) {

            cur_vtx = aCoordList;

            // Keep track of limits for normalization
            if (cur_vtx.x < limit[0].x) limit[0].x = cur_vtx.x;
            if (cur_vtx.x > limit[1].x) limit[1].x = cur_vtx.x;
            if (cur_vtx.y < limit[0].y) limit[0].y = cur_vtx.y;
            if (cur_vtx.y > limit[1].y) limit[1].y = cur_vtx.y;
            if (cur_vtx.z < limit[0].z) limit[0].z = cur_vtx.z;
            if (cur_vtx.z > limit[1].z) limit[1].z = cur_vtx.z;
        }

		if ((DEBUG & 64) != 0) {
			System.out.println("Model range: (" +
					limit[0].x + "," + limit[0].y + "," + limit[0].z + ") to (" +
					limit[1].x + "," + limit[1].y + "," + limit[1].z + ")");
		}

		return limit;
	} // End of getLimits



	/**
	 * Center the object and make it (-1,-1,-1) to (1,1,1).
	 */
	private void resize() {
		int i, j;
		Vertex cur_vtx = new Vertex();
		float biggest_dif;

		Vertex[] limit = getLimits();

		// Move object so it's centered on (0,0,0)
		Vector3D offset = new Vector3D(-0.5f * (limit[0].x + limit[1].x),
				-0.5f * (limit[0].y + limit[1].y),
				-0.5f * (limit[0].z + limit[1].z));

		if ((DEBUG & 64) != 0) {
			System.out.println("Offset amount: (" +
					offset.x + "," + offset.y + "," + offset.z + ")");
		}

		// Find the divide-by value for the normalization
		biggest_dif = limit[1].x - limit[0].x;
		if (biggest_dif < limit[1].y - limit[0].y)
			biggest_dif = limit[1].y - limit[0].y;
		if (biggest_dif < limit[1].z - limit[0].z)
			biggest_dif = limit[1].z - limit[0].z;
		biggest_dif /= 2.0f;

		for (i = 0 ; i < coordList.size() ; i++) {

			cur_vtx = coordList.get(i);

//			cur_vtx.add(cur_vtx, offset);
			Vector3D tmp = cur_vtx.getAdded(offset);
			cur_vtx = new Vertex(tmp.x,tmp.y,tmp.z);
			
//			Vector3D tmp = new Vector3D(offset);
//			cur_vtx.addLocal(tmp);
//			cur_vtx.addLocal(cur_vtx);

			cur_vtx.x /= biggest_dif;
			cur_vtx.y /= biggest_dif;
			cur_vtx.z /= biggest_dif;

			// coordList.setElementAt(cur_vtx, i);
		}
	} // End of resize


//	private int[] objectToIntArray(ArrayList inList) {
//		int outList[] = new int[inList.size()];
//		for (int i = 0 ; i < inList.size() ; i++) {
//			outList[i] = ((Integer)inList.get(i)).intValue();
//		}
//		return outList;
//	} // End of objectToIntArray
//
//
//	private Vertex[] objectToPoint3Array(ArrayList inList) {
//		Vertex outList[] = new Vertex[inList.size()];
//		for (int i = 0 ; i < inList.size() ; i++) {
//			outList[i] = (Vertex)inList.get(i);
//		}
//		return outList;
//	} // End of objectToPoint3Array
//
//
//
////	private TexCoord2f[] objectToTexCoord2Array(ArrayList inList) {
////		TexCoord2f outList[] = new TexCoord2f[inList.size()];
////		for (int i = 0 ; i < inList.size() ; i++) {
////			outList[i] = (TexCoord2f)inList.get(i);
////		}
////		return outList;
////	} // End of objectToTexCoord2Array
//	
//	private float[][] objectToTexCoord2Array(ArrayList inList) {
//		float outList[][] = new float[inList.size()][2]; 
//		for (int i = 0 ; i < inList.size() ; i++) {
////			outList[i] = (TexCoord2f)inList.get(i);
//			outList[i][0] = ((float[]) inList.get(i))[0];
//			outList[i][1] = ((float[]) inList.get(i))[1];
//		}
//		return outList;
//	} //
//
//
//	private Vector3D[] objectToVectorArray(ArrayList inList) {
//		Vector3D outList[] = new Vector3D[inList.size()];
//		for (int i = 0 ; i < inList.size() ; i++) {
//			outList[i] = (Vector3D)inList.get(i);
//		}
//		return outList;
//	} // End of objectToVectorArray
//
//
//	/**
//	 * Each group is a list of indices into the model's index lists,
//	 * indicating the starting index of each triangle in the group.
//	 * This method converts those data structures
//	 * into an integer array to use with GeometryInfo.
//	 */
//	private int[] groupIndices(ArrayList sourceList, ArrayList group) {
//		int indices[] = new int[group.size() * 3];
//		for (int i = 0 ; i < group.size() ; i++) {
//			int j = ((Integer)group.get(i)).intValue();
//			indices[i * 3 + 0] = ((Integer)sourceList.get(j + 0)).intValue();
//			indices[i * 3 + 1] = ((Integer)sourceList.get(j + 1)).intValue();
//			indices[i * 3 + 2] = ((Integer)sourceList.get(j + 2)).intValue();
//		}
//		return indices;
//	} // end of groupIndices
//



	/**
	 * For an .obj file loaded from a URL, set the URL where associated files
	 * (like material properties files) will be found.
	 * Only needs to be called to set it to a different URL
	 * from that containing the .obj file.
	 */
	public void setBaseUrl(URL url) {
		baseUrl = url;
	} // End of setBaseUrl


	/**
	 * Return the URL where files associated with this .obj file (like
	 * material properties files) will be found.
	 */
	public URL getBaseUrl() {
		return baseUrl;
	} // End of getBaseUrl


	/**
	 * Set the path where files associated with this .obj file are
	 * located.
	 * Only needs to be called to set it to a different directory
	 * from that containing the .obj file.
	 */
	private void setBasePath(String pathName) {
		basePath = pathName;
		
//		if (basePath == null || basePath == "")
//			basePath = "." + java.io.File.separator;
//		basePath = basePath.replace('/', java.io.File.separatorChar);
//		basePath = basePath.replace('\\', java.io.File.separatorChar);
//		if (!basePath.endsWith(java.io.File.separator))
//			basePath = basePath + java.io.File.separator;
		
//		if (basePath == null || basePath == "")
//			basePath = MTApplication.separator;
		
//		basePath = basePath.replace('/', MTApplication.separatorChar);
		basePath = basePath.replace('\\', AbstractMTApplication.separatorChar);
		
		if (!basePath.endsWith(AbstractMTApplication.separator))
			basePath = basePath + AbstractMTApplication.separator;
		
	} // End of setBasePath


	/**
	 * Return the path where files associated with this .obj file (like material
	 * files) are located.
	 */
	public String getBasePath() {
		return basePath;
	} // End of getBasePath


	/**
	 * Set parameters for loading the model.
	 * Flags defined in IModelLoader.java are ignored by the ObjectFile IModelLoader
	 * because the .obj file format doesn't include lights, fog, background,
	 * behaviors, views, or sounds.  However, several flags are defined
	 * specifically for use with the ObjectFile IModelLoader (see above).
	 */
	public void setFlags(int flags) {
		this.flags = flags;
		if ((DEBUG & 4) != 0) System.out.println("Flags = " + flags);
	} // End of setFlags


	
	public void setDebug(boolean debugNormalGenerator) {
		this.debugNormalGenerator = debugNormalGenerator;
	}


	/**
	 * Get the parameters currently defined for loading the model.
	 * Flags defined in IModelLoader.java are ignored by the ObjectFile IModelLoader
	 * because the .obj file format doesn't include lights, fog, background,
	 * behaviors, views, or sounds.  However, several flags are defined
	 * specifically for use with the ObjectFile IModelLoader (see above).
	 */
	public int getFlags() {
		return flags;
	} // End of getFlags
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * A class representing one group in a .obj file
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
			faces = new ArrayList<AFace>();
			
			verticesForGroup = new ArrayList<Vertex>();
			oldIndexToNewIndex = new HashMap<Integer, Integer>();
			
			texCoordsForGroup = new ArrayList<float[]>();
			oldTexIndexToNewTexIndex = new HashMap<Integer, Integer>();
			
			indexArray = new short[0];
			texCoordIndexArray = new int[0];
			
			name = "default";
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
		public void compileItsOwnLists(ArrayList<Vertex> allFileVerts, ArrayList<float[]> allTexCoords){
			indexArray = new short[faces.size()*3];
			
			if (allTexCoords.size() > 0){
				texCoordIndexArray = new int[faces.size()*3];
			}
			
			for (int i = 0; i < faces.size(); i++) {
				AFace currentFace = faces.get(i);
				
				Vertex v0 = allFileVerts.get(currentFace.p0);
				Vertex v1 = allFileVerts.get(currentFace.p1);
				Vertex v2 = allFileVerts.get(currentFace.p2);
				
				if (	allTexCoords.size() > currentFace.t0
					&& 	allTexCoords.size() > currentFace.t1
					&& 	allTexCoords.size() > currentFace.t2
				){
					float[] texV0 = allTexCoords.get(currentFace.t0);
					float[] texV1 = allTexCoords.get(currentFace.t1);
					float[] texV2 = allTexCoords.get(currentFace.t2);
					
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
				
				if (allTexCoords.size() > 0){
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
	
	
	
	
	
	
	
	/**
	 * Each face is converted to triangles.  As each face is converted,
	 * we look up which geometry group and smoothing group the face
	 * belongs to.  The generated triangles are added to each of these
	 * groups, which are also being converted to a new triangle based format.
	 *
	 * We need to convert to triangles before normals are generated
	 * because of smoothing groups.  The faces in a smoothing group 
	 * are copied into a GeometryInfo to have their normals calculated,
	 * and then the normals are copied out of the GeometryInfo using
	 * GeometryInfo.getNormalIndices.  As part of Normal generation,
	 * the geometry gets converted to Triangles.  So we need to convert
	 * to triangles *before* Normal generation so that the normals we
	 * read out of the GeometryInfo match up with the vertex data
	 * that we sent in.  If we sent in TRIANGLE_FAN data, the normal
	 * generator would convert it to triangles and we'd read out 
	 * normals formatted for Triangle data.  This would not match up
	 * with our original Fan data, so we couldn't tell which normals
	 * go with which vertices.
	 */
/*	private void convertToTriangles() {
		boolean triangulate = (flags & TRIANGULATE) != 0;
		boolean textures = !texList.isEmpty() && !texIdxList.isEmpty() &&
		(texIdxList.size() == coordIdxList.size());
		boolean normals = !normList.isEmpty() && !normIdxList.isEmpty() &&
		(normIdxList.size() == coordIdxList.size());
		int numFaces = stripCounts.size();
		boolean haveSgroups = curSgroup != null;

		triGroups = new HashMap(50);
		if (haveSgroups) triSgroups = new HashMap(50);

		ArrayList newCoordIdxList = null;
		ArrayList newTexIdxList = null;
		ArrayList newNormIdxList = null;

		if (triangulate) {
			GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
			gi.setStripCounts(objectToIntArray(stripCounts));
			gi.setCoordinates(coordArray);
			gi.setCoordinateIndices(objectToIntArray(coordIdxList));
			if (textures) {
				gi.setTextureCoordinateParams(1, 2);
				gi.setTextureCoordinates(0, texArray);
				gi.setTextureCoordinateIndices(0, objectToIntArray(texIdxList));
			}
			if (normals) {
				gi.setNormals(normArray);
				gi.setNormalIndices(objectToIntArray(normIdxList));
			}
			gi.convertToIndexedTriangles();

//			Data is now indexed triangles.  Next step is to take the data
//			out of the GeometryInfo and put into internal data structures

			int coordIndicesArray[] = gi.getCoordinateIndices();

			// Fix for #4366060
			// Make sure triangulated geometry has the correct number of triangles
			int tris = 0;
			for (int i = 0 ; i < numFaces ; i++)
				tris += ((Integer)stripCounts.get(i)).intValue() - 2;

			if (coordIndicesArray.length != (tris * 3)) {
				// Model contains bad polygons that didn't triangulate into the
				// correct number of triangles.  Fall back to "simple" triangulation
				triangulate = false;
			} else {

				int texIndicesArray[] = gi.getTextureCoordinateIndices();
				int normIndicesArray[] = gi.getNormalIndices();

				// Convert index arrays to internal ArrayList format
				coordIdxList.clear();
				texIdxList.clear();
				normIdxList.clear();
				for (int i = 0 ; i < coordIndicesArray.length ; i++) {
					coordIdxList.add(new Integer(coordIndicesArray[i]));
					if (textures) texIdxList.add(new Integer(texIndicesArray[i]));
					if (normals) normIdxList.add(new Integer(normIndicesArray[i]));
				}
			}
		}

		if (!triangulate) {
			newCoordIdxList = new ArrayList();
			if (textures) newTexIdxList = new ArrayList();
			if (normals) newNormIdxList = new ArrayList();
		}

		// Repeat for each face in the model - add the triangles from each 
		// face to the Geometry and Smoothing Groups
		int baseVertex = 0;
		for (int f = 0 ; f < numFaces ; f++) {
			int faceSize = ((Integer)stripCounts.get(f)).intValue();

			// Find out the name of the group to which this face belongs
			Integer curFace = new Integer(f);
			curGroup = (String)groups.get(curFace);

			// Change to a new geometry group, create if it doesn't exist
			curTriGroup = (ArrayList)triGroups.get(curGroup);
			if (curTriGroup == null) {
				curTriGroup = new ArrayList();
				triGroups.put(curGroup, curTriGroup);
			}

			// Change to a new smoothing group, create if it doesn't exist
			if (haveSgroups) {
				curSgroup = (String)sGroups.get(curFace);
				if (curSgroup == null) {
					// Weird case - this face has no smoothing group.  Happens if the
					// first 's' token comes after some faces have already been defined.
					// Assume they wanted no smoothing for these faces
					curSgroup = "0";
				}
				curTriSgroup = (ArrayList)triSgroups.get(curSgroup);
				if (curTriSgroup == null) {
					curTriSgroup = new ArrayList();
					triSgroups.put(curSgroup, curTriSgroup);
				}
			}

			if (triangulate) {

				// Each polygon of n vertices is now n-2 triangles
				for (int t = 0 ; t < faceSize - 2 ; t++) {

					// The groups just remember the first vertex of each triangle
					Integer triBaseVertex = new Integer(baseVertex);
					curTriGroup.add(triBaseVertex);
					if (haveSgroups) curTriSgroup.add(triBaseVertex);

					baseVertex += 3;
				}
			} else {
				// Triangulate simply
				for (int v = 0 ; v < faceSize - 2 ; v++) {
					// Add this triangle to the geometry group and the smoothing group
					Integer triBaseVertex = new Integer(newCoordIdxList.size());
					curTriGroup.add(triBaseVertex);
					if (haveSgroups) curTriSgroup.add(triBaseVertex);

					newCoordIdxList.add(coordIdxList.get(baseVertex));
					newCoordIdxList.add(coordIdxList.get(baseVertex + v + 1));
					newCoordIdxList.add(coordIdxList.get(baseVertex + v + 2));

					if (textures) {
						newTexIdxList.add(texIdxList.get(baseVertex));
						newTexIdxList.add(texIdxList.get(baseVertex + v + 1));
						newTexIdxList.add(texIdxList.get(baseVertex + v + 2));
					}

					if (normals) {
						newNormIdxList.add(normIdxList.get(baseVertex));
						newNormIdxList.add(normIdxList.get(baseVertex + v + 1));
						newNormIdxList.add(normIdxList.get(baseVertex + v + 2));
					}
				}
				baseVertex += faceSize;
			}
		}

		// No need to keep these around
		stripCounts = null;
		groups = null;
		sGroups = null;

		if (!triangulate) {
			coordIdxList = newCoordIdxList;
			texIdxList = newTexIdxList;
			normIdxList = newNormIdxList;
		}
	} // End of convertToTriangles
	*/
	
		//	/**
	//	* smoothingGroupNormals
	//	*
	//	* Smoothing groups are groups of faces who should be grouped
	//	* together for normal calculation purposes.  The faces are
	//	* put into a GeometryInfo object and normals are calculated
	//	* with a 180 degree creaseAngle (no creases) or whatever the
	//	* user has specified.  The normals
	//	* are then copied out of the GeometryInfo and back into
	//	* ObjectFile data structures.
	//	*/
	//	private void smoothingGroupNormals() {
	//	NormalGenerator ng =
	//	new NormalGenerator(radians == -1.0f ? Math.PI : radians);
	//	NormalGenerator ng0 = new NormalGenerator(0.0);
	//	normList.clear();
	//	normIdxList = null;
	//	int newNormIdxArray[] = new int[coordIdxList.size()];
	
	//	Iterator e = triSgroups.keySet().iterator();
	//	while (e.hasNext()) {
	//	String curname = (String)e.next();
	//	ArrayList triList = (ArrayList)triSgroups.get(curname);
	
	//	// Check for group with no faces
	//	if (triList.size() > 0) {
	
	//	GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
	
	//	gi.setCoordinateIndices(groupIndices(coordIdxList, triList));
	//	gi.setCoordinates(coordArray);
	
	//	if (curname.equals("0")) ng0.generateNormals(gi);
	//	else ng.generateNormals(gi);
	
	//	// Get the generated normals and indices
	//	Vector3D genNorms[] = gi.getNormals();
	//	int genNormIndices[] = gi.getNormalIndices();
	
	//	// Now we need to copy the generated normals into ObjectFile
	//	// data structures (normList and normIdxList).  The variable
	//	// normIdx is the index of the index of the normal currently
	//	// being put into the list.  It takes some calculation to
	//	// figure out the new index and where to put it.
	//	int normIdx = 0;
	//	// Repeat for each triangle in the smoothing group
	//	for (int i = 0 ; i < triList.size() ; i++) {
	
	//	// Get the coordIdxList index of the first index in this face
	//	int idx = ((Integer)triList.get(i)).intValue();
	
	//	// Repeat for each vertex in the triangle
	//	for (int j = 0 ; j < 3 ; j++) {
	
	//	// Put the new normal's index into the index list
	//	newNormIdxArray[idx + j] = normList.size();
	
	//	// Add the vertex's normal to the normal list
	//	normList.add(genNorms[genNormIndices[normIdx++]]);
	//	}
	//	}
	//	}
	//	}
	//	normIdxList = new ArrayList(coordIdxList.size());
	//	for (int i = 0 ; i < coordIdxList.size() ; i++) {
	//	normIdxList.add(new Integer(newNormIdxArray[i]));
	//	}
	//	normArray = objectToVectorArray(normList);
	//	} // end of smoothingGroupNormals

} // End of class ObjectFile

 

//End of file ObjectFile.java

