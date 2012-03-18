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

import java.util.ArrayList;

/**
 * A point in space for describing geometry.
 * 
 *@author Christopher Ruff
 */
public class Vertex extends Vector3D {
	
	/** The texture u. */
	private float textureU;
	
	/** The texture v. */
	private float textureV;
	
	/** The a. */
	private float r,g,b,a;
	
	public static final int DEFAULT_RED_COLOR_COMPONENT = 255;
	public static final int DEFAULT_GREEN_COLOR_COMPONENT = 255;
	public static final int DEFAULT_BLUE_COLOR_COMPONENT = 255;
	public static final int DEFAULT_ALPHA_COLOR_COMPONENT = 255;
	
	/**
	 * Instantiates a new vertex. (0,0,0)
	 */
	public Vertex() {
		this(0,0,0,1, 0,0, DEFAULT_RED_COLOR_COMPONENT,DEFAULT_GREEN_COLOR_COMPONENT,DEFAULT_BLUE_COLOR_COMPONENT,DEFAULT_ALPHA_COLOR_COMPONENT);
	}
	
	/**
	 * Instantiates a new vertex with z=0.
	 * 
	 * @param x the x
	 * @param y the y
	 */
	public Vertex(float x, float y){
		this(x,y,0,0,0);
	}
	
	/**
	 * Instantiates a new vertex.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public Vertex(float x, float y, float z){
		this(x,y,z,0,0);
	}
	
	/**
	 * Instantiates a new vertex.
	 * 
	 * @param vector the vector
	 */
	public Vertex(Vector3D vector) {
		this(vector.x,vector.y,vector.z,vector.w, 0,0, DEFAULT_RED_COLOR_COMPONENT,DEFAULT_GREEN_COLOR_COMPONENT,DEFAULT_BLUE_COLOR_COMPONENT,DEFAULT_ALPHA_COLOR_COMPONENT);
	}
	
	/**
	 * Instantiates a new vertex.
	 *
	 * @param vertex the vertex
	 */
	public Vertex(Vertex vertex) {
		this(vertex.x,vertex.y,vertex.z,vertex.w, vertex.getTexCoordU(), vertex.getTexCoordV(), vertex.getR(), vertex.getG(), vertex.getB(), vertex.getA());
	}
	
	/**
	 * Instantiates a new vertex.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param w the w
	 */
	public Vertex(float x, float y, float z, float w){
		this(x,y,z,w, 0,0, DEFAULT_RED_COLOR_COMPONENT,DEFAULT_GREEN_COLOR_COMPONENT,DEFAULT_BLUE_COLOR_COMPONENT,DEFAULT_ALPHA_COLOR_COMPONENT);
	}
	
	/**
	 * Instantiates a new vertex.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param textureX the texture x
	 * @param textureY the texture y
	 */
	public Vertex(float x, float y, float z, float textureX, float textureY){
		this(x,y,z,textureX,textureY, DEFAULT_RED_COLOR_COMPONENT,DEFAULT_GREEN_COLOR_COMPONENT,DEFAULT_BLUE_COLOR_COMPONENT,DEFAULT_ALPHA_COLOR_COMPONENT);
	}
	
	/**
	 * Instantiates a new vertex.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public Vertex(float x, float y, float z, float r, float g, float b, float a){
		this(x,y,z,0,0, r,g,b,a);
	}
	
	/**
	 * Instantiates a new vertex.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param textureX the texture x
	 * @param textureY the texture y
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public Vertex(float x, float y, float z, float textureX, float textureY, float r, float g, float b, float a){
		this(x, y, z, 1, textureX, textureY, r, g, b, a);
	}
	
	/**
	 * Instantiates a new vertex.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param w the w
	 * @param textureU2 the texture u2
	 * @param textureV2 the texture v2
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public Vertex(float x, float y, float z, float w, float textureU2, float textureV2, float r, float g, float b, float a) {
		super(x,y,z,w);
		
		this.textureU = textureU2;
		this.textureV = textureV2;
		
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
		this.setType(Vector3D.VERTEX);
	}


	
	/**
	 * Returns an array of exact copies of the provided vertices.
	 * 
	 * @param vertices the vertices
	 * 
	 * @return the deep vertex array copy
	 */
	public static Vertex[] getDeepVertexArrayCopy(Vertex[] vertices){
		Vertex[] copy = new Vertex[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			Vertex vertex = vertices[i]; 
			copy[i] = (Vertex)vertex.getCopy();
		}
		return copy;
	}
	
	/**
	 * Returns a list of exact copies of the provided vertices.
	 * 
	 * @param vertices the vertices
	 * 
	 * @return the deep vertex array copy
	 */
	public static ArrayList<Vertex[]> getDeepVertexArrayCopy(ArrayList<Vertex[]> vertices){
		ArrayList<Vertex[]> returnList = new ArrayList<Vertex[]>();
		for (Vertex[] vs : vertices){
			Vertex[] copy = new Vertex[vs.length];
			for (int i = 0; i < vs.length; i++) {
				Vertex vertex = vs[i];
				copy[i] = (Vertex)vertex.getCopy();
			}
			returnList.add(copy);	
		}
		return returnList;
	}
	
	/**
	 * Multiplicates all Vector3D of the Vector3D array with the given
	 * transformation matrix, thus transforming them.
	 * <br>Make a deepcopy of the vectors first if you dont want the originals being altered!
	 * 
	 * @param points the points
	 * @param transformMatrix the transform matrix
	 * 
	 * @return the transformed vector array
	 */
	public static Vertex[] transFormArray(Matrix transformMatrix, Vertex[] points){
		for (Vertex v : points)
			v.transform(transformMatrix);
		return points;
	}
	
	/**
	 * translates an array of Vertex by the given amounts in the directionvector.
	 * 
	 * @param inputArray the input array
	 * @param directionVector the direction vector
	 * 
	 * @return the vertex[]
	 */
	public static Vertex[] translateArray(Vertex[] inputArray, Vector3D directionVector){
		return Vertex.transFormArray(Matrix.getTranslationMatrix(directionVector.getX(), directionVector.getY(), directionVector.getZ())
				, inputArray);
	}
	
	
	/**
	 * rotates the Vertex array around the rotationpoint by the given degree.
	 * 
	 * @param inputArray the input array
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the rotated Vertex array
	 */
	public static Vertex[] xRotateVectorArray(Vertex[] inputArray, Vector3D rotationPoint, float degree ){
		return Vertex.transFormArray(Matrix.getXRotationMatrix(rotationPoint, degree),inputArray);
	}
	
	/**
	 * rotates the Vertex array around the rotationpoint by the given degree.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * @param inputArray the input array
	 * 
	 * @return the rotated Vertex array
	 */
	public static Vertex[] yRotateVectorArray(Vertex[] inputArray, Vector3D rotationPoint, float degree ){
		return Vertex.transFormArray(Matrix.getYRotationMatrix(rotationPoint, degree), inputArray);
	}
	
	/**
	 * rotates the Vertex array around the rotationpoint by the given degree.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * @param inputArray the input array
	 * 
	 * @return the rotated vector3D array
	 */
	public static Vertex[] zRotateVectorArray(Vertex[] inputArray, Vector3D rotationPoint, float degree ){
		return Vertex.transFormArray(Matrix.getZRotationMatrix(rotationPoint, degree), inputArray);
	}
	
	/**
	 * scales the Vertex[] around the scalingpoint by the given factor evenly in the X and Y direction.
	 * 
	 * @param inputArray the input array
	 * @param scalingPoint the scaling point
	 * @param factor the factor
	 * 
	 * @return the resulting vector array
	 */
	public static Vertex[] scaleVectorArray(Vertex[] inputArray, Vector3D scalingPoint, float factor) {
		return Vertex.transFormArray(Matrix.getScalingMatrix(scalingPoint, factor,factor,factor), inputArray); 
	}
	
	/**
	 * scales the Vertex[] around the scalingpoint by the factors given for each dimension.
	 * 
	 * @param inputArray the input array
	 * @param scalingPoint the scaling point
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * 
	 * @return the resulting vector array
	 */
	public static Vertex[] scaleVectorArray(Vertex[] inputArray, Vector3D scalingPoint, float X, float Y, float Z) {
		return Vertex.transFormArray(Matrix.getScalingMatrix(scalingPoint, X, Y, Z), inputArray); 
	}
	
    /**
     * NOTE: texture coordinates and color of the calling vector are kept.
     * 
     * @param v the v
     * 
     * @return an new Vector with the result of the addition
     */
	@Override
    public Vector3D getAdded(Vector3D v){
    	return new Vertex(
    			this.getX() + v.getX(), this.getY() + v.getY(), this.getZ() + v.getZ(), 
    			this.getTexCoordU() ,this.getTexCoordV(), 
    			this.getR(), this.getG(), this.getB(), this.getA());
    }
    
    /**
     * NOTE: texture coordinates of the calling vector are kept.
     * 
     * @param v the v
     * 
     * @return an new Vector with the result of the substraction
     */
	@Override
    public Vector3D getSubtracted(Vector3D v){
    	return new Vertex(this.getX() - v.getX() , this.getY() - v.getY(), this.getZ() - v.getZ(), 
    			this.getTexCoordU() ,this.getTexCoordV(), 
    			this.getR(), this.getG(), this.getB(), this.getA());
    }
    
    /**
     * Calculate the cross product with another vector.
     * 
     * @param v the v
     * 
     * @return  the cross product
     */ 
    @Override
    public Vector3D getCross(Vector3D v) {
        float crossX = this.getY() * v.getZ() - v.getY() * this.getZ();
        float crossY = this.getZ() * v.getX() - v.getZ() * this.getX();
        float crossZ = this.getX() * v.getY() - v.getX() * this.getY();
        return new Vertex(crossX,crossY,crossZ, 
        		this.getTexCoordU(), this.getTexCoordV(), 
    			this.getR(), this.getG(), this.getB(), this.getA());
    }
	
	
	/* (non-Javadoc)
	 * @see util.math.Vector3D#equalsVector(util.math.Vector3D)
	 */
	@Override
	public boolean equalsVector(Vector3D vertex) {
		return (this.getType() == vertex.getType() 
				&&	super.equalsVector(vertex) 
				&& this.getTexCoordU() == ((Vertex)vertex).getTexCoordU()	
				&& this.getTexCoordV() == ((Vertex)vertex).getTexCoordV()	
				&& this.getR() == ((Vertex)vertex).getR()	
				&& this.getG() == ((Vertex)vertex).getG()	
				&& this.getB() == ((Vertex)vertex).getB()	
				&& this.getA() == ((Vertex)vertex).getA()
				);
	}
	
	
	/**
	 * Copy the vector.
	 * 
	 * @return      a copy of the vector
	 */
	@Override
    public Vector3D getCopy() {
    	return new Vertex(this.getX(), this.getY(), this.getZ() ,this.getW(),
    			textureU, textureV, 
    			r, g, b, a);
    }
    
    
	
	/**
	 * Gets the tex coord u.
	 * 
	 * @return the tex coord u
	 */
	public float getTexCoordU() {
		return textureU;
	}

	/**
	 * Sets the tex coord u.
	 * 
	 * @param coordinateX the new tex coord u
	 */
	public void setTexCoordU(float coordinateX) {
		textureU = coordinateX;
	}

	/**
	 * Gets the tex coord v.
	 * 
	 * @return the tex coord v
	 */
	public float getTexCoordV() {
		return textureV;
	}

	/**
	 * Sets the tex coord v.
	 * 
	 * @param coordinateY the new tex coord v
	 */
	public void setTexCoordV(float coordinateY) {
		textureV = coordinateY;
	}
	
	/**
	 * Gets the a.
	 * 
	 * @return the a
	 */
	public float getA() {
		return a;
	}
	
	/**
	 * Sets the a.
	 * 
	 * @param a the new a
	 */
	public void setA(float a) {
		this.a = a;
	}
	
	/**
	 * Gets the b.
	 * 
	 * @return the b
	 */
	public float getB() {
		return b;
	}
	
	/**
	 * Sets the b.
	 * 
	 * @param b the new b
	 */
	public void setB(float b) {
		this.b = b;
	}
	
	/**
	 * Gets the g.
	 * 
	 * @return the g
	 */
	public float getG() {
		return g;
	}
	
	/**
	 * Sets the g.
	 * 
	 * @param g the new g
	 */
	public void setG(float g) {
		this.g = g;
	}
	
	/**
	 * Gets the r.
	 * 
	 * @return the r
	 */
	public float getR() {
		return r;
	}
	
	/**
	 * Sets the r.
	 * 
	 * @param r the new r
	 */
	public void setR(float r) {
		this.r = r;
	}
	
	/**
	 * Sets the rgba.
	 * 
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public void setRGBA(float r, float g, float b, float a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	@Override
	public String toString() {
		return super.toString() + " U:" + textureU + " V:" + textureV + " Color:(" + r + "," + g + "," + b + "," + a + ")";
	}
}
