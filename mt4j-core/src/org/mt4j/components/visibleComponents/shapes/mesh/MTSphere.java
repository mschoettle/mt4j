/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mt4j.components.visibleComponents.shapes.mesh;


import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.mt4j.components.bounds.BoundingSphere;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.ToolsBuffers;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLTexture;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * <code>Sphere</code> represents a 3D object with all points equidistance
 * from a center point.
 * 
 * @author Joshua Slack, Christopher Ruff
 * @version $Revision$, $Date$
 */
public class MTSphere extends MTTriangleMesh {

    private static final long serialVersionUID = 1L;

    @Deprecated
    public static final int TEX_ORIGINAL = 0;

    // Spherical projection mode, donated by Ogli from the jME forums.
    @Deprecated
    public static final int TEX_PROJECTED = 1;
        
    public enum TextureMode {
        /** Wrap texture radially and along z-axis */
        Original,
        /** Wrap texure radially, but spherically project along z-axis */
        Projected,
        /** Apply texture to each pole.  Eliminates polar distortion,
         * but mirrors the texture across the equator 
         */
        Polar
    }

    protected int zSamples;

    protected int radialSamples;

    protected boolean useEvenSlices;

    /** the distance from the center point each point falls on */
    public float radius;
    /** the center of the sphere */
    public Vector3D center;

    private static Vector3D tempVa = new Vector3D();

    private static Vector3D tempVb = new Vector3D();

    private static Vector3D tempVc = new Vector3D();

    protected TextureMode textureMode = TextureMode.Original;

    /**
     * Constructs a sphere with center at the origin. For details, see the other
     * constructor.
     * 
     * @param name
     *            Name of sphere.
     * @param zSamples
     *            The samples along the Z.
     * @param radialSamples
     *            The samples along the radial.
     * @param radius
     *            Radius of the sphere.
     */
    public MTSphere(PApplet pa, String name, int zSamples, int radialSamples, float radius,  TextureMode texMode) {
        this(pa, name, new Vector3D(0, 0, 0), zSamples, radialSamples, radius);
    }
    

    /**
     * Constructs a sphere with center at the origin. For details, see the other
     * constructor.
     * 
     * @param name
     *            Name of sphere.
     * @param zSamples
     *            The samples along the Z.
     * @param radialSamples
     *            The samples along the radial.
     * @param radius
     *            Radius of the sphere.
     */
    public MTSphere(PApplet pa, String name, int zSamples, int radialSamples, float radius) {
        this(pa, name, new Vector3D(0, 0, 0), zSamples, radialSamples, radius);
    }

    /**
     * Constructs a sphere. All geometry data buffers are updated automatically.
     * Both zSamples and radialSamples increase the quality of the generated
     * sphere.
     * 
     * @param name
     *            Name of the sphere.
     * @param center
     *            Center of the sphere.
     * @param zSamples
     *            The number of samples along the Z.
     * @param radialSamples
     *            The number of samples along the radial.
     * @param radius
     *            The radius of the sphere.
     */
    public MTSphere(PApplet pa, String name, Vector3D center, int zSamples,
            int radialSamples, float radius) {
        this(pa, name, center, zSamples, radialSamples, radius, false, TextureMode.Original);
    }

    /**
     * Constructs a sphere. Additional arg to evenly space latitudinal slices
     * 
     * @param name
     *            Name of the sphere.
     * @param center
     *            Center of the sphere.
     * @param zSamples
     *            The number of samples along the Z.
     * @param radialSamples
     *            The number of samples along the radial.
     * @param radius
     *            The radius of the sphere.
     * @param useEvenSlices
     *            Slice sphere evenly along the Z axis
     */
    public MTSphere(PApplet pa, String name, Vector3D center, int zSamples,
            int radialSamples, float radius, boolean useEvenSlices, TextureMode texMode) {
        super(pa, new GeometryInfo(pa, new Vertex[]{}));
        this.textureMode = texMode;
        this.updateGeometry(pa, center, zSamples, radialSamples, radius, useEvenSlices);
        
        this.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
        
        this.setName(name);
    }

    
    
    @Override
	protected IBoundingShape computeDefaultBounds() {
    	return new BoundingSphere(this);
	}

	/**
     * Returns the center of this sphere.
     * 
     * @return The sphere's center.
     */
    public Vector3D getCenter() {
        return center;
    }

    public int getRadialSamples() {
        return radialSamples;
    }

    public float getRadius() {
        return radius;
    }

    /**
     * @return Returns the textureMode.
     */
    public TextureMode getTextureMapMode() {
        return textureMode;
    }

    public int getZSamples() {
        return zSamples;
    }


    
//    /**
//     * builds the vertices based on the radius, center and radial and zSamples.
//     */
//    private void setGeometryData(PApplet pa) {
//    	int vertexCount = (zSamples - 2) * (radialSamples + 1) + 2;
//    	
//        // allocate vertices
////        setVertexCount((zSamples - 2) * (radialSamples + 1) + 2);
////        setVertexBuffer(ToolsBuffers.createVector3Buffer(getVertexBuffer(),
////                getVertexCount()));
//        Vertex[] verts = new Vertex[vertexCount];
//        
//        List<Vertex> vertList = new ArrayList<Vertex>();
//        
//        // allocate normals if requested
////        setNormalBuffer(ToolsBuffers.createVector3Buffer(getNormalBuffer(),
////                getVertexCount()));
//        Vector3D[] normals = new Vector3D[vertexCount];
//        
//        List<Vector3D> normList = new ArrayList<Vector3D>();
//        
//        List<float[]> texCoordList = new ArrayList<float[]>();
//
//        // allocate texture coordinates
////        setTextureCoords(new TexCoords(ToolsBuffers.createVector2Buffer(getVertexCount())));
//        
//
//        // generate geometry
//        float fInvRS = 1.0f / radialSamples;
//        float fZFactor = 2.0f / (zSamples - 1);
//
//        // Generate points on the unit circle to be used in computing the mesh
//        // points on a sphere slice.
//        float[] afSin = new float[(radialSamples + 1)];
//        float[] afCos = new float[(radialSamples + 1)];
//        for (int iR = 0; iR < radialSamples; iR++) {
//            float fAngle = FastMath.TWO_PI * fInvRS * iR;
//            afCos[iR] = FastMath.cos(fAngle);
//            afSin[iR] = FastMath.sin(fAngle);
//        }
//        afSin[radialSamples] = afSin[0];
//        afCos[radialSamples] = afCos[0];
//
//        // generate the sphere itself
//        int i = 0;
//        for (int iZ = 1; iZ < (zSamples - 1); iZ++) {
//            float fAFraction = FastMath.HALF_PI * (-1.0f + fZFactor * iZ); // in (-pi/2, pi/2)
//            float fZFraction;
//            if (useEvenSlices)
//                fZFraction = -1.0f + fZFactor * iZ; // in (-1, 1)
//            else
//                fZFraction = FastMath.sin(fAFraction); // in (-1,1)
//
//            float fZ = radius * fZFraction;
//
//            // compute center of slice
//            Vector3D kSliceCenter = tempVb.setValues(center);
//            kSliceCenter.z += fZ;
//
//            // compute radius of slice
//            float fSliceRadius = FastMath.sqrt(FastMath.abs(radius * radius
//                    - fZ * fZ));
//
//            // compute slice vertices with duplication at end point
//            Vector3D kNormal;
//            int iSave = i;
//            for (int iR = 0; iR < radialSamples; iR++) {
//                float fRadialFraction = iR * fInvRS; // in [0,1)
//                tempVc.setXYZ(afCos[iR], afSin[iR], 0);
//                Vector3D kRadial = tempVc;
//                
////                kRadial.mult(fSliceRadius, tempVa);
//                tempVa.setValues(kRadial.getScaled(fSliceRadius));
//               
////                FloatBuffer f;
////                f.put(src, offset, length)
////                verts[]
//                
////                getVertexBuffer().put(kSliceCenter.x + tempVa.x).put(
////                        kSliceCenter.y + tempVa.y).put(
////                        kSliceCenter.z + tempVa.z);
//                vertList.add(new Vertex(kSliceCenter.x + tempVa.x, kSliceCenter.y + tempVa.y, kSliceCenter.z + tempVa.z));
//
////                ToolsBuffers.populateFromBuffer(tempVa, getVertexBuffer(), i);
////                tempVa.setValues(vertList.get(i)); //FIXME warum gehts nicht?
//                tempVa.setValues(vertList.get(vertList.size()-1));
//                
//                kNormal = tempVa.subtractLocal(center);
//                kNormal.normalizeLocal();
//                if (true){ // later we may allow interior texture vs. exterior
////                    getNormalBuffer().put(kNormal.x).put(kNormal.y).put(
////                            kNormal.z);
//                	normList.add(new Vector3D(kNormal.x, kNormal.y, kNormal.z));
//                }else{
////                    getNormalBuffer().put(-kNormal.x).put(-kNormal.y).put(
////                            -kNormal.z);
//                	normList.add(new Vector3D(-kNormal.x, -kNormal.y, -kNormal.z));
//                }
//                
//                if (textureMode == TextureMode.Original){
////                    getTextureCoords().get(0).coords.put(fRadialFraction).put(
////                            0.5f * (fZFraction + 1.0f));
//                	texCoordList.add(new float[]{fRadialFraction, 0.5f * (fZFraction + 1.0f)});
//                }else if (textureMode == TextureMode.Projected){
////                    getTextureCoords().get(0).coords.put(fRadialFraction).put(
////                            FastMath.INV_PI
////                                    * (FastMath.HALF_PI + FastMath
////                                            .asin(fZFraction)));
//                	texCoordList.add(new float[]{fRadialFraction, FastMath.INV_PI
//                            * (FastMath.HALF_PI + FastMath
//                                    .asin(fZFraction))});
//                }else if (textureMode == TextureMode.Polar) {
//                    float r = (FastMath.HALF_PI - FastMath.abs(fAFraction)) / FastMath.PI;
//                    float u = r * afCos[iR] + 0.5f;
//                    float v = r * afSin[iR] + 0.5f;
//                    texCoordList.add(new float[]{u, v});
////                    getTextureCoords().get(0).coords.put(u).put(v);
//                }
//
//                i++;
//            }
//            
////          ToolsBuffers.copyInternalVector3(getVertexBuffer(), iSave, i);
////          ToolsBuffers.copyInternalVector3(getNormalBuffer(), iSave, i);
//            
//            if (vertList.get(iSave) == null){
//            	vertList.add(iSave, new Vertex(-1,-1,-1));
//            }
////            vertList.set(i-1, 
////            		new Vertex(vertList.get(iSave)));
//            vertList.set(vertList.size()-1, 
//            		new Vertex(vertList.get(iSave)));
//            
//            if (normList.get(iSave) == null){
//            	normList.add(iSave, new Vector3D(-1,-1,-1));
//            }
////            normList.set(i-1, 
////            		new Vector3D(normList.get(iSave)));
//            normList.set(vertList.size()-1, 
//            		new Vector3D(normList.get(iSave)));
//
//
//            if (textureMode == TextureMode.Original){
////                getTextureCoords().get(0).coords.put(1.0f).put(
////                        0.5f * (fZFraction + 1.0f));
//                texCoordList.add(new float[]{1.0f, 0.5f * (fZFraction + 1.0f)});
//            }else if (textureMode == TextureMode.Projected){
////                getTextureCoords().get(0).coords.put(1.0f)
////                        .put(
////                                FastMath.INV_PI
////                                        * (FastMath.HALF_PI + FastMath
////                                                .asin(fZFraction)));
//                texCoordList.add(new float[]{1.0f, FastMath.INV_PI
//                        * (FastMath.HALF_PI + FastMath
//                                .asin(fZFraction))});
//            }else if (textureMode == TextureMode.Polar) {
//                float r = (FastMath.HALF_PI - FastMath.abs(fAFraction)) / FastMath.PI;
////                getTextureCoords().get(0).coords.put(r+0.5f).put(0.5f);
//                texCoordList.add(new float[]{r+0.5f, 0.5f});
//            }
//
//            i++;
//        }
//
//        // south pole
////        getVertexBuffer().position(i * 3);
////        getVertexBuffer().put(center.x).put(center.y).put(center.z - radius);
////        vertList.add(i, new Vertex(center.x, center.y, center.z)); //TODO ??
//        vertList.add(new Vertex(center.x, center.y, center.z));
//
////        getNormalBuffer().position(i * 3);
//        if (true){
////            getNormalBuffer().put(0).put(0).put(-1); // allow for inner
////                                                        // texture orientation
////                                                        // later.
////        	normList.add(i, new Vector3D(0, 0, -1)); //TODO?
//        	normList.add(new Vector3D(0, 0, -1));
//        }else{
////            getNormalBuffer().put(0).put(0).put(1);
//            normList.add(i, new Vector3D(0, 0, 1));
//        }
//        
////        getTextureCoords().get(0).coords.position(i * 2);
//        if (textureMode == TextureMode.Polar) {
////            getTextureCoords().get(0).coords.put(0.5f).put(0.5f);
//        	texCoordList.add(new float[]{0.5f, 0.5f});
//        }
//        else {
////            getTextureCoords().get(0).coords.put(0.5f).put(0.0f);
//        	texCoordList.add(new float[]{0.5f, 0.0f});
//        }
//
//        i++;
//
//        // north pole
////        getVertexBuffer().put(center.x).put(center.y).put(center.z + radius);
//        vertList.add(new Vertex(center.x, center.y, center.z + radius));
//
//        if (true){
////            getNormalBuffer().put(0).put(0).put(1);
//        	normList.add(new Vector3D(0, 0, 1));
//        }else{
////            getNormalBuffer().put(0).put(0).put(-1);
//        	normList.add(new Vector3D(0, 0, -1));
//        }
//        
//        if (textureMode == TextureMode.Polar) {
////            getTextureCoords().get(0).coords.put(0.5f).put(0.5f);
//            texCoordList.add(new float[]{0.5f, 0.5f});
//        }
//        else {
////            getTextureCoords().get(0).coords.put(0.5f).put(1.0f);
//        	texCoordList.add(new float[]{0.5f, 1.0f});
//        }
//        
////        for (int j = 0; j < texCoordList.size(); j++) { //TODO ?
////			float[] f = texCoordList.get(j);
////			vertList.get(j).setTexCoordU(f[0]);
////			vertList.get(j).setTexCoordV(f[1]);
////		}
//        
//        for (int j = 0; j < vertList.size(); j++) {
//        	Vertex v = vertList.get(j);
//        	float[] f = texCoordList.get(j);
//        	v.setTexCoordU(f[0]);
//			v.setTexCoordV(f[1]);
//		}
//       
//        verts = vertList.toArray(new Vertex[vertList.size()]);
//        normals = normList.toArray(new Vector3D[normList.size()]);
//        
//        int[] indices = getIndexData();
//        
////        GeometryInfo geomInfo = new GeometryInfo(pa, verts, normals, indices); //TODO?
//        GeometryInfo geomInfo = new GeometryInfo(pa, verts, normals);
//        
//        this.setGeometryInfo(geomInfo);
//    }

    
    /**
     * builds the vertices based on the radius, center and radial and zSamples.
     */
    private void setGeometryData(PApplet pa) {
        // allocate vertices
//        setVertexCount((zSamples - 2) * (radialSamples + 1) + 2);
//        setVertexBuffer(ToolsBuffers.createVector3Buffer(vertBuff,
//                getVertexCount()));
        
        int vertexCount = (zSamples - 2) * (radialSamples + 1) + 2;
        FloatBuffer vertexBuff = ToolsBuffers.createFloatBuffer(3 * vertexCount);
        
        // allocate normals if requested
//        setNormalBuffer(ToolsBuffers.createVector3Buffer(normBuff,
//                getVertexCount()));
        FloatBuffer normBuff = ToolsBuffers.createFloatBuffer(3 * vertexCount);

        // allocate texture coordinates
//        setTextureCoords(new TexCoords(ToolsBuffers.createVector2Buffer(getVertexCount())));
        FloatBuffer texBuff = ToolsBuffers.createFloatBuffer(2 * vertexCount);

        // generate geometry
        float fInvRS = 1.0f / radialSamples;
        float fZFactor = 2.0f / (zSamples - 1);

        // Generate points on the unit circle to be used in computing the mesh
        // points on a sphere slice.
        float[] afSin = new float[(radialSamples + 1)];
        float[] afCos = new float[(radialSamples + 1)];
        for (int iR = 0; iR < radialSamples; iR++) {
            float fAngle = ToolsMath.TWO_PI * fInvRS * iR;
            afCos[iR] = ToolsMath.cos(fAngle);
            afSin[iR] = ToolsMath.sin(fAngle);
        }
        afSin[radialSamples] = afSin[0];
        afCos[radialSamples] = afCos[0];

        // generate the sphere itself
        int i = 0;
        for (int iZ = 1; iZ < (zSamples - 1); iZ++) {
            float fAFraction = ToolsMath.HALF_PI * (-1.0f + fZFactor * iZ); // in (-pi/2, pi/2)
            float fZFraction;
            if (useEvenSlices)
                fZFraction = -1.0f + fZFactor * iZ; // in (-1, 1)
            else
                fZFraction = ToolsMath.sin(fAFraction); // in (-1,1)

            float fZ = radius * fZFraction;

            // compute center of slice
            Vector3D kSliceCenter = tempVb.setValues(center);
            kSliceCenter.z += fZ;

            // compute radius of slice
            float fSliceRadius = ToolsMath.sqrt(ToolsMath.abs(radius * radius
                    - fZ * fZ));

            // compute slice vertices with duplication at end point
            Vector3D kNormal;
            int iSave = i;
            for (int iR = 0; iR < radialSamples; iR++) {
                float fRadialFraction = iR * fInvRS; // in [0,1)
                tempVc.setXYZ(afCos[iR], afSin[iR], 0);
                Vector3D kRadial = tempVc;
                tempVa = kRadial.getScaled(fSliceRadius);
                
                vertexBuff.put(kSliceCenter.x + tempVa.x).put(
                        kSliceCenter.y + tempVa.y).put(
                        kSliceCenter.z + tempVa.z);

                ToolsBuffers.populateFromBuffer(tempVa, vertexBuff, i);
                kNormal = tempVa.subtractLocal(center);
                kNormal.normalizeLocal();
                if (true) // later we may allow interior texture vs. exterior
                    normBuff.put(kNormal.x).put(kNormal.y).put(
                            kNormal.z);
                else
                    normBuff.put(-kNormal.x).put(-kNormal.y).put(
                            -kNormal.z);

                if (textureMode == TextureMode.Original)
                    texBuff.put(fRadialFraction).put(
                            0.5f * (fZFraction + 1.0f));
                else if (textureMode == TextureMode.Projected)
                    texBuff.put(fRadialFraction).put(
                            ToolsMath.INV_PI
                                    * (ToolsMath.HALF_PI + ToolsMath
                                            .asin(fZFraction)));
                else if (textureMode == TextureMode.Polar) {
                    float r = (ToolsMath.HALF_PI - ToolsMath.abs(fAFraction)) / ToolsMath.PI;
                    float u = r * afCos[iR] + 0.5f;
                    float v = r * afSin[iR] + 0.5f;
                    texBuff.put(u).put(v);
                }

                i++;
            }

            copyInternalVector3(vertexBuff, iSave, i);
            copyInternalVector3(normBuff, iSave, i);

            if (textureMode == TextureMode.Original)
                texBuff.put(1.0f).put(
                        0.5f * (fZFraction + 1.0f));
            else if (textureMode == TextureMode.Projected)
                texBuff.put(1.0f)
                        .put(
                                ToolsMath.INV_PI
                                        * (ToolsMath.HALF_PI + ToolsMath
                                                .asin(fZFraction)));
            else if (textureMode == TextureMode.Polar) {
                float r = (ToolsMath.HALF_PI - ToolsMath.abs(fAFraction)) / ToolsMath.PI;
                texBuff.put(r+0.5f).put(0.5f);
            }

            i++;
        }

        // south pole
        vertexBuff.position(i * 3);
        vertexBuff.put(center.x).put(center.y).put(center.z - radius);

        normBuff.position(i * 3);
        if (true)
            normBuff.put(0).put(0).put(-1); // allow for inner
                                                        // texture orientation
                                                        // later.
        else
            normBuff.put(0).put(0).put(1);

        texBuff.position(i * 2);

        if (textureMode == TextureMode.Polar) {
            texBuff.put(0.5f).put(0.5f);
        }
        else {
            texBuff.put(0.5f).put(0.0f);
        }

        i++;

        // north pole
        vertexBuff.put(center.x).put(center.y).put(center.z + radius);

        if (true)
            normBuff.put(0).put(0).put(1);
        else
            normBuff.put(0).put(0).put(-1);

        if (textureMode == TextureMode.Polar) {
            texBuff.put(0.5f).put(0.5f);
        }
        else {
            texBuff.put(0.5f).put(1.0f);
        }
        
        
        Vertex[] verts = ToolsBuffers.getVertexArray(vertexBuff);
        Vector3D[] norms = ToolsBuffers.getVector3DArray(normBuff);
        
        //Set texcoords to vertices
        float[] tex = ToolsBuffers.getFloatArray(texBuff);
        for (int j = 0; j < tex.length/2; j++) {
			float u = tex[j*2];
			float v = tex[j*2+1];
			verts[j].setTexCoordU(u);
			verts[j].setTexCoordV(v);
		}
       
        //get indices
        short[] indices = this.getIndexData();

	    GeometryInfo geomInfo = new GeometryInfo(pa, verts, norms, indices);
	    this.setGeometryInfo(geomInfo);
    }
    
    
    
    /**
     * 
     * @param buf
     * @param fromPos
     * @param toPos
     */
    public static void copyInternalVector3(FloatBuffer buf, int fromPos, int toPos) {
        ToolsBuffers.copyInternal(buf, fromPos*3, toPos*3, 3);
    }

    
    
    /**
     * Gets the indices data for rendering the sphere.
     * @return the index data
     */
    private short[] getIndexData() {
    	int triCount = (2 * (zSamples - 2) * radialSamples);
    	
//    	IntBuffer indexBuff = ToolsBuffers.createIntBuffer(3 * triCount);
    	ShortBuffer indexBuff = ToolsBuffers.createShortBuffer(3 * triCount);
    	
        // allocate connectivity
//        setTriangleQuantity(2 * (zSamples - 2) * radialSamples);
//        setIndexBuffer(BufferUtils.createIntBuffer(3 * getTriangleCount()));

        // generate connectivity
        int index = 0;
        for (short iZ = 0, iZStart = 0; iZ < (zSamples - 3); iZ++) {
            short i0 = iZStart;
            short i1 = (short) (i0 + 1);
            iZStart += (radialSamples + 1);
            short i2 = iZStart;
            short i3 = (short) (i2 + 1);
            for (short i = 0; i < radialSamples; i++, index += 6) {
                if (true) {
                    indexBuff.put(i0++);
                    indexBuff.put(i1);
                    indexBuff.put(i2);
                    indexBuff.put(i1++);
                    indexBuff.put(i3++);
                    indexBuff.put(i2++);
                } else // inside view
                {
                    indexBuff.put(i0++);
                    indexBuff.put(i2);
                    indexBuff.put(i1);
                    indexBuff.put(i1++);
                    indexBuff.put(i2++);
                    indexBuff.put(i3++);
                }
            }
        }
        
        int vertexCount = (zSamples - 2) * (radialSamples + 1) + 2; 

        // south pole triangles
        for (short i = 0; i < radialSamples; i++, index += 3) {
            if (true) {
                indexBuff.put(i);
                indexBuff.put((short) (vertexCount - 2));
                indexBuff.put((short) (i + 1));
            } else { // inside view
                indexBuff.put(i);
                indexBuff.put((short) (i + 1));
                indexBuff.put((short) (vertexCount - 2));
            }
        }

        // north pole triangles
        int iOffset = (zSamples - 3) * (radialSamples + 1);
        for (short i = 0; i < radialSamples; i++, index += 3) {
            if (true) {
                indexBuff.put((short) (i + iOffset));
                indexBuff.put((short) (i + 1 + iOffset));
                indexBuff.put((short) (vertexCount - 1));
            } else { // inside view
                indexBuff.put((short) (i + iOffset));
                indexBuff.put((short) (vertexCount - 1));
                indexBuff.put((short) (i + 1 + iOffset));
            }
        }
        
//        return ToolsBuffers.getIntArray(indexBuff);
        return ToolsBuffers.getShortArray(indexBuff);
    }

    
    
//    /**
//     * sets the indices for rendering the sphere.
//     * @return 
//     */
//    private int[] getIndexData() {
//    	int triCount = 2 * (zSamples - 2) * radialSamples;
//    	
//        // allocate connectivity
////        setTriangleQuantity(2 * (zSamples - 2) * radialSamples);
////        setIndexBuffer(ToolsBuffers.createIntBuffer(3 * getTriangleCount()));
//    	List<Integer> indList = new ArrayList<Integer>();
//
//        // generate connectivity
//        int index = 0;
//        for (int iZ = 0, iZStart = 0; iZ < (zSamples - 3); iZ++) {
//            int i0 = iZStart;
//            int i1 = i0 + 1;
//            iZStart += (radialSamples + 1);
//            int i2 = iZStart;
//            int i3 = i2 + 1;
//            for (int i = 0; i < radialSamples; i++, index += 6) {
//                if (true) {
////                	getIndexBuffer().put(i0++);
////                    getIndexBuffer().put(i1);
////                    getIndexBuffer().put(i2);
////                    getIndexBuffer().put(i1++);
////                    getIndexBuffer().put(i3++);
////                    getIndexBuffer().put(i2++);
//                    
//                	indList.add(i0++);
//                	indList.add(i1);
//                	indList.add(i2);
//                	indList.add(i1++);
//                	indList.add(i3++);
//                	indList.add(i2++);
//                } else // inside view
//                {
//                	indList.add(i0++);
//                	indList.add(i2);
//                	indList.add(i1);
//                	indList.add(i1++);
//                	indList.add(i2++);
//                	indList.add(i3++);
//                }
//            }
//        }
//
//        // south pole triangles
//        for (int i = 0; i < radialSamples; i++, index += 3) {
//            if (true) {
////                getIndexBuffer().put(i);
////                getIndexBuffer().put(getVertexCount() - 2);
////                getIndexBuffer().put(i + 1);
//            	indList.add(i);
//            	indList.add(getVertexCount() - 2);
//            	indList.add(i + 1);
//            } else { // inside view
////                getIndexBuffer().put(i);
////                getIndexBuffer().put(i + 1);
////                getIndexBuffer().put(getVertexCount() - 2);
//                
//            	indList.add(i);
//            	indList.add(i + 1);
//            	indList.add(getVertexCount() - 2);
//            }
//        }
//
//        // north pole triangles
//        int iOffset = (zSamples - 3) * (radialSamples + 1);
//        for (int i = 0; i < radialSamples; i++, index += 3) {
//            if (true) {
////                getIndexBuffer().put(i + iOffset);
////                getIndexBuffer().put(i + 1 + iOffset);
////                getIndexBuffer().put(getVertexCount() - 1);
//                
//            	indList.add(i + iOffset);
//            	indList.add(i + 1 + iOffset);
//            	indList.add(getVertexCount() - 1);
//            } else { // inside view
////                getIndexBuffer().put(i + iOffset);
////                getIndexBuffer().put(getVertexCount() - 1);
////                getIndexBuffer().put(i + 1 + iOffset);
//            	indList.add(i + iOffset);
//            	indList.add(getVertexCount() - 1);
//            	indList.add(i + 1 + iOffset);
//            }
//        }
//        
//        int[] indices = new int[indList.size()];
//        for (int i = 0; i < indices.length; i++) {
//			indices[i] = indList.get(i);
//		}
//        return indices;
//    }

    /**
//     * @param textureMode
//     *            The textureMode to set.
//     * @deprecated Use enum version of setTextureMode
//     */
//    @Deprecated
//    public void setTextureMode(int textureMode) {
//        if (textureMode == TEX_ORIGINAL)
//            this.textureMode = TextureMode.Original;
//        else if (textureMode == TEX_PROJECTED)
//            this.textureMode = TextureMode.Projected;
//        setGeometryData(this.getRenderer());
//    }

//    /**
//     * @param textureMode
//     *            The textureMode to set.
//     */
//    public void setTextureMode(TextureMode textureMode) {
//        this.textureMode = textureMode;
//        setGeometryData(this.getRenderer());
//    }

    /**
     * Changes the information of the sphere into the given values.
     * 
     * @param center the center of the sphere.
     * @param zSamples the number of zSamples of the sphere.
     * @param radialSamples the number of radial samples of the sphere.
     * @param radius the radius of the sphere.
     */
    public void updateGeometry(PApplet pa,Vector3D center, int zSamples, int radialSamples, float radius) {
        updateGeometry(pa, center, zSamples, radialSamples, radius, false);
    }

    public void updateGeometry(PApplet pa, Vector3D center, int zSamples, int radialSamples, float radius, boolean useEvenSlices) {
        this.center = center != null ? center : new Vector3D();
        this.zSamples = zSamples;
        this.radialSamples = radialSamples;
        this.radius = radius;
        this.useEvenSlices = useEvenSlices;
       
        this.setGeometryData(pa);
//        getIndexData();
    }

    
	//FIXME TEST -> adapt tex coords for non fitting, NPOT gl texture
	private void adaptTexCoordsForNPOTUse(){
		PImage tex = this.getTexture();
		if (tex instanceof GLTexture){
			Tools3D.adaptTextureCoordsNPOT(this, (GLTexture)tex);
		}
	}
	
	@Override
	public void setUseDirectGL(boolean drawPureGL) {
		super.setUseDirectGL(drawPureGL);
		adaptTexCoordsForNPOTUse();
	}
	
	@Override
	public void setTexture(PImage newTexImage) {
		super.setTexture(newTexImage);
		adaptTexCoordsForNPOTUse();
	}

}

