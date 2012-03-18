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



/**
 * ************************************************
 * Homogenous 3D vector class with 4 components, X, Y, Z and W.
 * 
 * @author Christopher Ruff
 * ************************************************
 */
public class Vector3D {
	
	/** The w. */
	public float x, y, z, w;
	
	/** The type. */
	private transient int type;
	
	/** The Constant VECTOR. */
	public static final int VECTOR 			= 0;
	
	/** The Constant VERTEX. */
	public static final int VERTEX 			= 1;
	
	/** The Constant BEZIERVERTEX. */
	public static final int BEZIERVERTEX 	= 2;
	
	/** Zero vector (0,0,0). */
	public static final Vector3D ZERO_VECTOR = new Vector3D(0,0,0);

    /** Defines positive X axis. */
    public static final Vector3D X_AXIS = new Vector3D(1, 0, 0);

    /** Defines positive Y axis. */
    public static final Vector3D Y_AXIS = new Vector3D(0, 1, 0);

    /** Defines positive Z axis. */
    public static final Vector3D Z_AXIS = new Vector3D(0, 0, 1);


	
	/**
	 * Instantiates a new vector3 d.
	 */
	public Vector3D() {
		this(0,0,0,1);
	}
	
	/**
	 * Instantiates a new vector3 d.
	 * 
	 * @param x the x
	 * @param y the y
	 */
	public Vector3D(float x, float y){
		this(x,y,0);
	}
	
	/**
	 * Instantiates a new vector3 d.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public Vector3D(float x, float y, float z){
		this(x,y,z,1);
	}
	
	/**
	 * Instantiates a new vector3 d.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param w the w
	 */
	public Vector3D(float x, float y, float z, float w){
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.w = w;
		
		this.setType(Vector3D.VECTOR);
	}
    

	/**
	 * Instantiates a new vector3 d.
	 * 
	 * @param v the v
	 */
	public Vector3D(Vector3D v) {
		this(v.getX(),v.getY(),v.getZ(),v.getW());
	}

	
	/**
	 * Gets the deep vertex array copy.
	 * Uses the getCopy() method on each vector.
	 * 
	 * @param vertices the vertices
	 * 
	 * @return the deep vertex array copy
	 */
	public static Vector3D[] getDeepVertexArrayCopy(Vector3D[] vertices){
		Vector3D[] copy = new Vector3D[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			Vector3D vertex = vertices[i]; 
			copy[i] = vertex.getCopy();
		}
		return copy;
	}
    
    /**
     * Applies a transformation on the vector
     * defined by the given tranformation matrix.
     * 
     * @param transformMatrix the transform matrix
     */
	public void transform(Matrix transformMatrix){
		transformMatrix.mult(this);
	}
	
	
	//TODO reicht es den W coord des Vectors auf 0 zu setzen?
	//FIXME eigentlich sollte man direction vectoren wie normale
	//transformieren also mit transformNormal, aber so gehts auch meistens..why?
	/**
	 * Transforms a direction vector, not a point.
	 * Ignores the translation part of the matrix
	 * 
	 * @param transformMatrix the transform matrix
	 */
	public void transformDirectionVector(Matrix transformMatrix){
		Matrix m = new Matrix(transformMatrix);
		m.removeTranslationFromMatrix();
		this.transform(m);
	}
	
	/**
	 * Transforms a normal or direction vector.
	 * This is done by multiplying the vector with the
	 * inverse transpose of the matrix.
	 * <p>
	 * <strong>NOTE</strong>: this is not cheap because a new matrix is created (copied)
	 * inverted and then transposed.<p>
	 * (If you can supply a precomputed inverted matrix yourself, write
	 * yourself a method that doesent do the invert() call :))
	 * 
	 * @param transformMatrix the transform matrix
	 */
	public void transformNormal(Matrix transformMatrix){
		Matrix inverse = transformMatrix.invert();
//		this.setW(0);
		try {
			Matrix transpose = inverse.transpose();
			transpose.mult(this,this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Multiplicates all Vector3D of the Vector3D array with the given
	 * transformation matrix, thus transforming them.
	 * Make a deepcopy of the vectors first if you dont want the originals being altered!
	 * 
	 * @param transformMatrix the transform matrix
	 * @param points the points
	 * 
	 * @return the transformed vector array
	 */
	public static Vector3D[] transFormArrayLocal(Matrix transformMatrix, Vector3D[] points){
		for (Vector3D v : points)
			v.transform(transformMatrix);
		return points;
	}
	
	
	/**
	 * Translate.
	 * 
	 * @param directionVector the direction vector
	 */
	public void translate(Vector3D directionVector){
		this.transform(Matrix.getTranslationMatrix(directionVector.getX(), directionVector.getY(), directionVector.getZ()));
	}
	
	/**
	 * translates an array of Vector3D by the given amounts in the directionvector.
	 * 
	 * @param inputArray the input array
	 * @param directionVector the direction vector
	 * 
	 * @return the vector3 d[]
	 */
	public static Vector3D[] translateVectorArray(Vector3D[] inputArray, Vector3D directionVector){
		return Vector3D.transFormArrayLocal(Matrix.getTranslationMatrix(directionVector.getX(), directionVector.getY(), directionVector.getZ())
				, inputArray);
	}
	
	
	/**
	 * Rotate x.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public void rotateX(Vector3D rotationPoint, float degree ){
		this.transform(Matrix.getXRotationMatrix(rotationPoint, degree));
	}
	
	/**
	 * rotates the Vector3D array around the rotationpoint by the given degree.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * @param inputArray the input array
	 * 
	 * @return the rotated vector3D array
	 */
	public static Vector3D[] rotateXVectorArray(Vector3D[] inputArray, Vector3D rotationPoint, float degree ){
		return Vector3D.transFormArrayLocal(Matrix.getXRotationMatrix(rotationPoint, degree),inputArray);
	}
	
	
	
	/**
	 * Rotate y.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public void rotateY(Vector3D rotationPoint, float degree ){
		this.transform(Matrix.getYRotationMatrix(rotationPoint, degree));
	}
	
	
	/**
	 * rotates the Vector3D array around the rotationpoint by the given degree.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * @param inputArray the input array
	 * 
	 * @return the rotated vector3D array
	 */
	public static Vector3D[] rotateYVectorArray(Vector3D[] inputArray, Vector3D rotationPoint, float degree ){
		return Vector3D.transFormArrayLocal(Matrix.getYRotationMatrix(rotationPoint, degree), inputArray);
	}
	
	
	/**
	 * Rotate z.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public void rotateZ(Vector3D rotationPoint, float degree ){
		this.transform(Matrix.getZRotationMatrix(rotationPoint, degree));
	}
	
	
	/**
	 * Rotates the vector by the given angle around the X axis.
	 * 
	 * @param theta the theta
	 * 
	 * @return itself
	 */
	public final Vector3D rotateX(float theta) {
		float co = (float) Math.cos(theta);
		float si = (float) Math.sin(theta);
		float zz = co * z - si * y;
		y = si * z + co * y;
		z = zz;
		return this;
	}

	/**
	 * Rotates the vector by the given angle around the Y axis.
	 * 
	 * @param theta the theta
	 * 
	 * @return itself
	 */
	public final Vector3D rotateY(float theta) {
		float co = (float) Math.cos(theta);
		float si = (float) Math.sin(theta);
		float xx = co * x - si * z;
		z = si * x + co * z;
		x = xx;
		return this;
	}

	/**
	 * Rotates the vector by the given angle around the Z axis.
	 * RADIANS EXPECTED!
	 * @param theta the theta
	 * 
	 * @return itself
	 */
	public final Vector3D rotateZ(float theta) {
//		/*
		float co = (float) Math.cos(theta);
		float si = (float) Math.sin(theta);
		float xx = co * x - si * y;
		y = si * x + co * y;
		x = xx;
		return this;
//		*/
	}

	/**
	 * rotates the Vector3D array around the rotationpoint by the given degree.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * @param inputArray the input array
	 * 
	 * @return the rotated vector3D array
	 */
	public static Vector3D[] rotateZVectorArray(Vector3D[] inputArray, Vector3D rotationPoint, float degree ){
		return Vector3D.transFormArrayLocal(Matrix.getZRotationMatrix(rotationPoint, degree), inputArray);
	}

	
	/**
	 * Scale the vector by factor.
	 * 
	 * @param scalar the scalar
	 * 
	 * @return the vector after scaling
	 */   
    public Vector3D scaleLocal(float scalar) {
    	this.setXYZ(this.x * scalar, this.y * scalar, this.z * scalar ) ;
    	return this;
    }
    
    public Vector3D divideLocal(float scalar) {
        scalar = 1f/scalar;
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    
    
    /**
     * Gets the scaled.
     * 
     * @param scalar the scalar
     * 
     * @return the scaled
     * 
     * a new scaled vector
     */
    public Vector3D getScaled(float scalar) {
    	return new Vector3D(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    
	/**
	 * scales the Vector3D[] around the scalingpoint by the given factor evenly in the X and Y direction.
	 * 
	 * @param inputArray the input array
	 * @param scalingPoint the scaling point
	 * @param factor the factor
	 * 
	 * @return the resulting vector array
	 */
	public static Vector3D[] scaleVectorArray(Vector3D[] inputArray, Vector3D scalingPoint, float factor) {
		return Vector3D.transFormArrayLocal(Matrix.getScalingMatrix(scalingPoint, factor,factor,factor), inputArray); 
	}
	
	/**
	 * scales the Vector3D[] around the scalingpoint by the factors given for each dimension.
	 * 
	 * @param inputArray the input array
	 * @param scalingPoint the scaling point
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * 
	 * @return the resulting vector array
	 */
	public static Vector3D[] scaleVectorArray(Vector3D[] inputArray, Vector3D scalingPoint, float X, float Y, float Z) {
		return Vector3D.transFormArrayLocal(Matrix.getScalingMatrix(scalingPoint, X, Y, Z), inputArray); 
	}
	
	
	/**
	 * Add a vector to this vector.
	 * 
	 * @param v the v
	 * 
	 * @return the vector after the addition
	 */   
    public Vector3D addLocal(Vector3D v) {
    	this.setX(x + v.getX());
    	this.setY(y + v.getY());
    	this.setZ(z + v.getZ());
    	return this;
    }
    
    /**
     * Gets the added.
     * 
     * @param v the v
     * 
     * @return an new Vector with the result of the addition
     */
    public Vector3D getAdded(Vector3D v){
    	return new Vector3D(x + v.getX() , y + v.getY(), z + v.getZ());
    }
    
    /**
     * NOTE: texture coordinates of the calling vector are kept.
     * 
     * @param v the v
     * 
     * @return an new Vector with the result of the subtraction
     */
    public Vector3D getSubtracted(Vector3D v){
    	return new Vector3D(x - v.getX() , y - v.getY(), z - v.getZ());
    }
    
    
    /**
     * Subtract a vector from this vector.
     * 
     * @param v the v
     * 
     * @return TODO
     */   
    public Vector3D subtractLocal(Vector3D v) {
    	this.setX(x - v.getX());
    	this.setY(y - v.getY());
    	this.setZ(z - v.getZ());
    	return this;
    }
	
    /**
     * Scales vector uniformly by factor -1 ( v = -v ), overrides coordinates
     * with result.
     * 
     * @return itself
     */
    public Vector3D invertLocal() {
            x *= -1;
            y *= -1;
            z *= -1;
            return this;
    }

    /**
     * Scales vector uniformly by factor -1 ( v = -v ), overrides coordinates
     * with result.
     * 
     * @return itself
     */
    public Vector3D getInverted() {
    	return new Vector3D(x*-1, y*-1, z*-1);
    }
    
    
    
    /**
     * Interpolates the vector towards the given target vector, using linear
     * interpolation.
     * 
     * @param v target vector
     * @param f interpolation factor (should be in the range 0..1)
     * 
     * @return result as new vector
     */
    public final Vector3D getInterpolatedTo(Vector3D v, float f) {
            return new Vector3D(x + (v.x - x) * f, y + (v.y - y) * f, z + (v.z - z)
                            * f);
    }
    
    
	/**
	 * Copy the vector.
	 * 
	 * @return      a copy of the vector
	 */
    public Vector3D getCopy() {
    	return new Vector3D(x, y, z, w);
    }
    
    
    /**
     * Calculate the magnitude (length) of the vector.
     * 
     * @return      the magnitude of the vector
     */
    public float length() {
        return (float) Math.sqrt(x*x + y*y + z*z);
    }
    
    /**
     * Calculates only the squared magnitude/length of the vector. Useful for
     * inverse square law applications and/or for speed reasons or if the real
     * eucledian distance is not required (e.g. sorting).
     * 
     * @return squared magnitude (x^2 + y^2 + z^2)
     */
    public float lengthSquared() {
            return x * x + y * y + z * z;
    }

    
    /**
     * Calculate the cross product with another vector. And returns
     * a new vector as the result.
     * 
     * @param v the v
     * 
     * @return  the cross product, a new vector
     */     
    public Vector3D getCross(Vector3D v) {
        float crossX = y * v.getZ() - v.getY() * z;
        float crossY = z * v.getX() - v.getZ() * x;
        float crossZ = x * v.getY() - v.getX() * y;
        return new Vector3D(crossX,crossY,crossZ);
    }
    
    /**
     * Calcs the cross and sets the new values to this vector.
     * 
     * @param v the v
     * 
     * @return the Vector after the cross operation
     */
    public Vector3D crossLocal(Vector3D v) {
        float crossX = y * v.getZ() - v.getY() * z;
        float crossY = z * v.getX() - v.getZ() * x;
        float crossZ = x * v.getY() - v.getX() * y;
        this.setXYZ(crossX, crossY, crossZ);
        return this;
    }
    
    /**
     * Calculate the dot product with another vector.
     * 
     * @param v the v
     * 
     * @return  the dot product
     */     
    public float dot(Vector3D v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    } //(x * v.x + y * v.y + z * v.z);
    
    /**
     * Normalize the vector to length 1 (make it a unit vector).
     * 
     * @return the same vector after normalization
     */     
    public Vector3D normalizeLocal() {
//        float m = length();
//        if (m > 0) {
//        	this.setX(x / m);
//        	this.setY(y / m);
//        	this.setZ(z / m);
//        }
        float length = length();
        if (length != 0) {
        	float scalar = length;
        	 scalar = 1f/scalar;
             x *= scalar;
             y *= scalar;
             z *= scalar;
        }
        return this;
    }
    
    /**
     * Normalize the vector to length 1 (make it a unit vector).
     * 
     * @return a NEW vector after normalization of this vector
     */     
    public Vector3D getNormalized() {
    	Vector3D n = this;
        float length = length();
        if (length != 0) {
        	float scalar = length;
        	 scalar = 1f/scalar;
             n = new Vector3D(this.x*scalar, this.y*scalar, this.z*scalar);
        }
        return n;
    }
    
    
    /**
     * Limits the vector to the given length .
     * 
     * @param lim new maximum magnitude
     * @return the limited vector
     */
    public final Vector3D limitLocal(float lim) {
    	if (this.lengthSquared() > lim * lim) {
    		normalizeLocal();
    		scaleLocal(lim);
    	}
    	return this;
    }
    
    
    /**
     * Creates a copy of the vector with its magnitude limited to the length
     * given.
     * 
     * @param lim new maximum magnitude
     * 
     * @return result as new vector
     */
    public final Vector3D getLimited(float lim) {
    	if (this.lengthSquared() > lim * lim) {
    		Vector3D norm = this.getCopy();
    		norm.normalizeLocal();
    		norm.scaleLocal(lim);
    		return norm;
    	}
    	return new Vector3D(this);
    }
    

    /**
     * Rotates the vector around the giving axis.
     * 
     * @param axis rotation axis vector
     * @param theta rotation angle (in radians)
     * 
     * @return itself
     */
    public final Vector3D rotateAroundAxisLocal(Vector3D axis, float theta) {
    	float ux = axis.x * x;
    	float uy = axis.x * y;
    	float uz = axis.x * z;
    	float vx = axis.y * x;
    	float vy = axis.y * y;
    	float vz = axis.y * z;
    	float wx = axis.z * x;
    	float wy = axis.z * y;
    	float wz = axis.z * z;
    	double si = Math.sin(theta);
    	double co = Math.cos(theta);
    	float xx = (float) (axis.x
    			* (ux + vy + wz)
    			+ (x * (axis.y * axis.y + axis.z * axis.z) - axis.x * (vy + wz))
    			* co + (-wy + vz) * si);
    	float yy = (float) (axis.y
    			* (ux + vy + wz)
    			+ (y * (axis.x * axis.x + axis.z * axis.z) - axis.y * (ux + wz))
    			* co + (wx - uz) * si);
    	float zz = (float) (axis.z
    			* (ux + vy + wz)
    			+ (z * (axis.x * axis.x + axis.y * axis.y) - axis.z * (ux + vy))
    			* co + (-vx + uy) * si);
    	x = xx;
    	y = yy;
    	z = zz;
    	return this;
    }


    /**
     * Calculate the Euclidean distance between two points (considering a point as a vector object).
     * 
     * @param v2 another vector
     * @param v1 the v1
     * 
     * @return the Euclidean distance between v1 and v2
     */ 
    public static float distance (Vector3D v1, Vector3D v2) {
    	float dx = v1.getX() - v2.getX();
    	float dy = v1.getY() - v2.getY();
    	float dz = v1.getZ() - v2.getZ();
    	return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    
    /**
     *  Calculate the Euclidean distance between two points (considering a point as a vector object).
     * 
     * @param v2 the v2
     * 
     * @return the float
     */
    public float distance(Vector3D v2){
    	float dx = this.getX() - v2.getX();
    	float dy = this.getY() - v2.getY();
    	float dz = this.getZ() - v2.getZ();
    	return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    
    /**
     * Calculate the Euclidean distance between two points (considering a point as a vector object).
     * 
     * @param v2 another vector
     * @param v1 the v1
     * 
     * @return the Euclidean distance between v1 and v2
     */ 
    public static float distance2D (Vector3D v1, Vector3D v2) {
    	float dx = v1.getX() - v2.getX();
    	float dy = v1.getY() - v2.getY();
    	return (float) Math.sqrt(dx*dx + dy*dy );
    }
    
    /**
     * Calculate the Euclidean distance between two points (considering a point as a vector object).
     * Disregards the Z component of the vectors and is thus a little faster.
     * 
     * @param v2 another vector
     * 
     * @return the Euclidean distance between this and v2
     */ 
    public float distance2D (Vector3D v2) {
    	float dx = this.getX() - v2.getX();
    	float dy = this.getY() - v2.getY();
    	return (float) Math.sqrt(dx*dx + dy*dy );
    }

    /**
     * Calculate the Euclidean distance between two points (considering a point as a vector object).
     * 
     * @param v2 another vector
     * @param v1 the v1
     * 
     * @return the Euclidean distance between v1 and v2 squared
     */ 
    public static float distanceSquared (Vector3D v1, Vector3D v2) {
    	if (v2 != null) {
    		float dx = v1.x - v2.x;
    		float dy = v1.y - v2.y;
    		float dz = v1.z - v2.z;
    		return dx * dx + dy * dy + dz * dz;
    	} else {
    		return Float.NaN;
    	}
    }

    /**
     * Calculate the angle between two vectors, using the dot product.
     * 
     * @param v2 another vector
     * @param v1 the v1
     * 
     * @return the angle between the vectors in radians
     */ 
//  FIXME this produces an not 0.0 angle for equal vectors sometimes..why?
    public static float angleBetween(Vector3D v1, Vector3D v2) {
//    	Vector3D v1Copy = v1.getCopy();
//    	Vector3D v2Copy = v2.getCopy();
//    	
//    	v1Copy.normalize();
//    	v2Copy.normalize();
//    	
//    	float dotP = v1Copy.dot(v2Copy);
//    	System.out.println("Dot:" + dotP);
//    	float theta = (float)Math.acos(dotP);
    	
//        float dot = v1.dot(v2);
//        float theta = FastMath.acos(dot / (v1.length() * v2.length()));
//        return theta;
    	
    	return v1.angleBetween(v2);
    }
    
    /**
     * Calculate the angle between two vectors, using the dot product.
     * 
     * @param v2 another vector
     * 
     * @return the angle between the vectors in radians
     */ 
//  FIXME this produces an not 0.0 angle for equal vectors sometimes..why?
    public float angleBetween(Vector3D v2) {
        float dot = this.dot(v2);
        float theta = ToolsMath.acos(dot / (this.length() * v2.length()));
        return theta;
    }
	
	/**
	 * sets a new X coordinate value for the vector.
	 * 
	 * @param x the x
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * sets a new Y coordinate value for the vector.
	 * 
	 * @param y the y
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * sets a new Z coordinate value for the vector.
	 * 
	 * @param z the z
	 */
	public void setZ(float z) {
		this.z = z;
	}
	
	
	/**
	 * sets new values for the vector.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void setXYZ(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * sets new values for the vector.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param w the w
	 */
	public void setXYZW(float x, float y, float z, float w){
		this.setXYZ(x, y, z);
		this.w = w;
	}
		
//	/**
//	 * Converts the 3D homogenous vector to a 2D jawa.awt.point, throwing away the Z-Value
//	 * 
//	 * @return the point
//	 */
//    public Point getJava2DPoint(){ 
//			return new Point(Math.round(x),Math.round(y));
//	}
    
	/**
	 * Gets the y.
	 * 
	 * @return the Y value of the 3D Vector
	 */
    public float getY(){ 
		return y;
    }
    
    /**
     * Gets the z.
     * 
     * @return the Z value of the 3D Vector
     */
    public float getZ(){ 
		return z;
    }
    
    /**
     * Gets the x.
     * 
     * @return the X value of the 3D Vector
     */
    public float getX(){ 
		return x;
    }

	/**
	 * Gets the w.
	 * 
	 * @return the W
	 * 
	 * the W value of the 3D Vector
	 */
	public float getW() {
		return w;
	}

	/**
	 * Sets the w.
	 * 
	 * @param w the new w
	 */
	public void setW(float w) {
		this.w = w;
	}
	
	/**
	 * Sets the.
	 * 
	 * @param i the i
	 * @param value the value
	 */
	public void set(int i, float value){
		if (i == 0)
			x = value;
		else if(i == 1)
			y = value;
		else if(i == 2)
			z = value;
		else if(i == 3)
			w = value;
		else{ 
			System.err.println("illegal vector dimension");
		}
	}
	///
	
	/**
	 * Returns an integer in case of:
	 * <br>VECTOR = 0;
	 * <br>VERTEX = 1;
	 * <br>BEZIERVERTEX = 2;.
	 * 
	 * @return the type
	 * 
	 * the integer identifying this object
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the type. This should only be called
	 * by extending classes to determine their type.
	 * 
	 * @param type the type
	 */
	protected void setType(int type) {
		this.type = type;
	}

	/**
	 * Checks if the two vectors have the same components (XYZW).
	 * <br>Does NOT check for object identity equality!
	 * 
	 * @param vector3D the vector3 d
	 * 
	 * @return true, if equals vector
	 */
	public boolean equalsVector(Vector3D vector3D){
		return (   this.getX() == vector3D.getX()		
				&& this.getY() == vector3D.getY()	
				&& this.getZ() == vector3D.getZ()
				&& this.getW() == vector3D.getW()
		);
	}
	
	/**
	 * Checks if the two vectors have the same components (XYZW) in the range of a
	 * specified tolerance.
	 * <br>Does NOT check for object identity equality!
	 * <br>NOTE: checks each component of the vector individually, so the overall difference
	 * might be greater than the given tolerance!
	 * 
	 * @param vec the vec
	 * @param tolerance the tolerance
	 * 
	 * @return true, if equals vector with tolerance
	 */
	public boolean equalsVectorWithTolerance(Vector3D vec, float tolerance){
		return (   Math.abs(this.getX() - vec.getX()) <= tolerance		
				&& Math.abs(this.getY() - vec.getY()) <= tolerance
				&& Math.abs(this.getZ() - vec.getZ()) <= tolerance
				&& Math.abs(this.getW() - vec.getW()) <= tolerance
		);
	}
	
	
	 /**
 	 * Saves this Vector3f into the given float[] object.
 	 * 
 	 * @param floats The float[] to take this Vector3f. If null, a new float[3] is
 	 * created.
 	 * 
 	 * @return The array, with X, Y, Z float values in that order
 	 */
    public float[] toArray(float[] floats) {
        if (floats == null) {
            floats = new float[3];
        }
        floats[0] = x;
        floats[1] = y;
        floats[2] = z;
        return floats;
    }

	/**
	 * Sets the values of another vector.
	 * 
	 * @param otherVector the new values
	 * 
	 * @return the vector3 d
	 */
	public Vector3D setValues(Vector3D otherVector){
		this.x = otherVector.x;
		this.y = otherVector.y;
		this.z = otherVector.z;
		this.w = otherVector.w;
		return this;
	}
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
    	return "(" + x + "," + y + "," + z + "," + w + ")";
    }
    
//    //TODO CHECK IF THIS WORKS; TOO
//    /*
//      /**
//     * Transform the provided vector by the matrix and place it back in
//     * the vector.
//     *
//     * @param vec The vector to be transformed
//     * @param mat The matrix to do the transforming with
//     * @param out The vector to be put the result in
//     */
//    private void transform(Tuple3f vec, Matrix4f mat, Tuple3d out)
//    {
//        float a = vec.x;
//        float b = vec.y;
//        float c = vec.z;
//
//        out.x = mat.m00 * a + mat.m01 * b + mat.m02 * c + mat.m03;
//        out.y = mat.m10 * a + mat.m11 * b + mat.m12 * c + mat.m13;
//        out.z = mat.m20 * a + mat.m21 * b + mat.m22 * c + mat.m23;
//    }
//
//    /**
//     * Transform the provided vector by the matrix and place it back in
//     * the vector. The fourth element is assumed to be zero for normal
//     * transformations.
//     *
//     * @param vec The vector to be transformed
//     * @param mat The matrix to do the transforming with
//     * @param out The vector to be put the result in
//     */
//    private void transformNormal(Tuple3d vec, Matrix4f mat, Tuple3d out)
//    {
//        float a = (float)vec.x;
//        float b = (float)vec.y;
//        float c = (float)vec.z;
//
//        out.x = mat.m00 * a + mat.m01 * b + mat.m02 * c;
//        out.y = mat.m10 * a + mat.m11 * b + mat.m12 * c;
//        out.z = mat.m20 * a + mat.m21 * b + mat.m22 * c;
//    }
//
//    /**
//     * Transform the provided vector by the matrix and place it back in
//     * the vector. The fourth element is assumed to be zero for normal
//     * transformations.
//     *
//     * @param vec The vector to be transformed
//     * @param mat The matrix to do the transforming with
//     * @param out The vector to be put the result in
//     */
//    private void transformNormal(Tuple3f vec, Matrix4f mat, Tuple3d out)
//    {
//        float a = vec.x;
//        float b = vec.y;
//        float c = vec.z;
//
//        out.x = mat.m00 * a + mat.m01 * b + mat.m02 * c;
//        out.y = mat.m10 * a + mat.m11 * b + mat.m12 * c;
//        out.z = mat.m20 * a + mat.m21 * b + mat.m22 * c;
//    }
//}
//     */
    
}
