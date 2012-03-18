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

package org.mt4j.util.math;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.logging.Logger;



/**
 * <code>Matrix</code> defines and maintains a 4x4 matrix in row major order.
 * This matrix is intended for use in a translation and rotational capacity.
 * It provides convenience methods for creating the matrix from a multitude
 * of sources.
 * 
 * Matrices are stored assuming column vectors on the right, with the translation
 * in the rightmost column. Element numbering is row,column, so m03 is the zeroth
 * row, third column, which is the "x" translation part. This means that the implicit
 * storage order is column major. However, the get() and set() functions on float
 * arrays default to row major order!
 * Copyright (c) JMonkeyEngine
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @author C.Ruff
 */
public class Matrix  implements Serializable, Cloneable {
    
    /** The Constant logger. */
    private static final Logger logger = Logger.getLogger(Matrix.class.getName());

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The m03. */
    public float m00, m01, m02, m03;

    /** The m13. */
    public float m10, m11, m12, m13;

    /** The m23. */
    public float m20, m21, m22, m23;

    /** The m33. */
    public float m30, m31, m32, m33;

    /**
     * Constructor instantiates a new <code>Matrix</code> that is set to the
     * identity matrix.
     */
    public Matrix() {
        loadIdentity();
    }

    /**
     * constructs a matrix with the given values.
     * 
     * @param m00 the m00
     * @param m01 the m01
     * @param m02 the m02
     * @param m03 the m03
     * @param m10 the m10
     * @param m11 the m11
     * @param m12 the m12
     * @param m13 the m13
     * @param m20 the m20
     * @param m21 the m21
     * @param m22 the m22
     * @param m23 the m23
     * @param m30 the m30
     * @param m31 the m31
     * @param m32 the m32
     * @param m33 the m33
     */
    public Matrix(float m00, float m01, float m02, float m03, 
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33) {

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    /**
     * Create a new Matrix4f, given data in column-major format.
     * 
     * @param array An array of 16 floats in column-major format (translation in elements 12, 13 and 14).
     */
    public Matrix(float[] array) {
    	try {
			set(array, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Constructor instantiates a new <code>Matrix</code> that is set to the
     * provided matrix. This constructor copies a given Matrix. If the provided
     * matrix is null, the constructor sets the matrix to the identity.
     * 
     * @param mat the matrix to copy.
     */
    public Matrix(Matrix mat) {
        copy(mat);
    }

    /**
     * <code>copy</code> transfers the contents of a given matrix to this
     * matrix. If a null matrix is supplied, this matrix is set to the identity
     * matrix.
     * 
     * @param matrix the matrix to copy.
     */
    public void copy(Matrix matrix) {
        if (null == matrix) {
            loadIdentity();
        } else {
            m00 = matrix.m00;
            m01 = matrix.m01;
            m02 = matrix.m02;
            m03 = matrix.m03;
            m10 = matrix.m10;
            m11 = matrix.m11;
            m12 = matrix.m12;
            m13 = matrix.m13;
            m20 = matrix.m20;
            m21 = matrix.m21;
            m22 = matrix.m22;
            m23 = matrix.m23;
            m30 = matrix.m30;
            m31 = matrix.m31;
            m32 = matrix.m32;
            m33 = matrix.m33;
        }
    }

    /**
     * <code>get</code> retrieves the values of this object into
     * a float array in row-major order.
     * 
     * @param matrix the matrix to set the values into.
     */
    public void get(float[] matrix)  {
        try {
			get(matrix, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * <code>set</code> retrieves the values of this object into
     * a float array.
     * 
     * @param matrix the matrix to set the values into.
     * @param rowMajor whether the outgoing data is in row or column major order.
     * 
     * @throws Exception the exception
     */
    public void get(float[] matrix, boolean rowMajor) throws Exception {
        if (matrix.length != 16) throw new Exception(
                "Array must be of size 16.");

        if (rowMajor) {
            matrix[0] = m00;
            matrix[1] = m01;
            matrix[2] = m02;
            matrix[3] = m03;
            matrix[4] = m10;
            matrix[5] = m11;
            matrix[6] = m12;
            matrix[7] = m13;
            matrix[8] = m20;
            matrix[9] = m21;
            matrix[10] = m22;
            matrix[11] = m23;
            matrix[12] = m30;
            matrix[13] = m31;
            matrix[14] = m32;
            matrix[15] = m33;
        } else {
            matrix[0] = m00;
            matrix[4] = m01;
            matrix[8] = m02;
            matrix[12] = m03;
            matrix[1] = m10;
            matrix[5] = m11;
            matrix[9] = m12;
            matrix[13] = m13;
            matrix[2] = m20;
            matrix[6] = m21;
            matrix[10] = m22;
            matrix[14] = m23;
            matrix[3] = m30;
            matrix[7] = m31;
            matrix[11] = m32;
            matrix[15] = m33;
        }
    }

    /**
     * <code>get</code> retrieves a value from the matrix at the given
     * position. If the position is invalid a <code>JmeException</code> is
     * thrown.
     * 
     * @param i the row index.
     * @param j the colum index.
     * 
     * @return the value at (i, j).
     * 
     * @throws Exception the exception
     */
    public float get(int i, int j) throws Exception {
        switch (i) {
        case 0:
            switch (j) {
            case 0: return m00;
            case 1: return m01;
            case 2: return m02;
            case 3: return m03;
            }
        case 1:
            switch (j) {
            case 0: return m10;
            case 1: return m11;
            case 2: return m12;
            case 3: return m13;
            }
        case 2:
            switch (j) {
            case 0: return m20;
            case 1: return m21;
            case 2: return m22;
            case 3: return m23;
            }
        case 3:
            switch (j) {
            case 0: return m30;
            case 1: return m31;
            case 2: return m32;
            case 3: return m33;
            }
        }

        logger.warning("Invalid matrix index.");
        throw new Exception("Invalid indices into matrix.");
    }

    /**
     * <code>getColumn</code> returns one of three columns specified by the
     * parameter. This column is returned as a float array of length 4.
     * 
     * @param i the column to retrieve. Must be between 0 and 3.
     * 
     * @return the column specified by the index.
     * 
     * @throws Exception the exception
     */
    public float[] getColumn(int i) throws Exception  {
        return getColumn(i, null);
    }

    /**
     * <code>getColumn</code> returns one of three columns specified by the
     * parameter. This column is returned as a float[4].
     * 
     * @param i the column to retrieve. Must be between 0 and 3.
     * @param store the float array to store the result in. if null, a new one
     * is created.
     * 
     * @return the column specified by the index.
     * 
     * @throws Exception the exception
     */
    public float[] getColumn(int i, float[] store) throws Exception {
        if (store == null) store = new float[4];
        switch (i) {
        case 0:
            store[0] = m00;
            store[1] = m10;
            store[2] = m20;
            store[3] = m30;
            break;
        case 1:
            store[0] = m01;
            store[1] = m11;
            store[2] = m21;
            store[3] = m31;
            break;
        case 2:
            store[0] = m02;
            store[1] = m12;
            store[2] = m22;
            store[3] = m32;
            break;
        case 3:
            store[0] = m03;
            store[1] = m13;
            store[2] = m23;
            store[3] = m33;
            break;
        default:
            logger.warning("Invalid column index.");
            throw new Exception("Invalid column index. " + i);
        }
        return store;
    }
    
    /**
     * <code>getRow</code> returns one of three Rows specified by the
     * parameter. This row is returned as a float array of length 4.
     * 
     * @param i the row to retrieve. Must be between 0 and 3.
     * 
     * @return the row specified by the index.
     * 
     * @throws Exception the exception
     */
    public float[] getRow(int i) throws Exception {
    	return getRow(i,null);
    }
    
    /**
     * <code>getRow</code> returns one of three rows specified by the
     * parameter. This row is returned as a float[4].
     * 
     * @param i the row to retrieve. Must be between 0 and 3.
     * @param store the float array to store the result in. if null, a new one
     * is created.
     * 
     * @return the column specified by the index.
     * 
     * @throws Exception the exception
     */
    public float[] getRow(int i, float[] store) throws Exception {
        if (store == null) store = new float[4];
        switch (i) {
        case 0:
            store[0] = m00;
            store[1] = m01;
            store[2] = m02;
            store[3] = m03;
            break;
        case 1:
            store[0] = m10;
            store[1] = m11;
            store[2] = m12;
            store[3] = m13;
            break;
        case 2:
            store[0] = m20;
            store[1] = m21;
            store[2] = m22;
            store[3] = m23;
            break;
        case 3:
            store[0] = m30;
            store[1] = m31;
            store[2] = m32;
            store[3] = m33;
            break;
        default:
            logger.warning("Invalid row index.");
            throw new Exception("Invalid row index. " + i);
        }
        return store;
    }
    
    
//    /**
//    * <code>getColumn</code> returns one of three columns specified by the
//    * parameter. This column is returned as a <code>Vector3f</code> object.
//    * 
//    * @param i
//    *            the column to retrieve. Must be between 0 and 2.
//    * @param store
//    *            the vector object to store the result in. if null, a new one
//    *            is created.
//    * @return the column specified by the index.
//     * @throws Exception 
//    */
//   public Vector3D getColumn(int i, Vector3D store) throws Exception {
//       if (store == null) store = new Vector3D();
//       switch (i) {
//       case 0:
//           store.x = m00;
//           store.y = m10;
//           store.z = m20;
//           break;
//       case 1:
//           store.x = m01;
//           store.y = m11;
//           store.z = m21;
//           break;
//       case 2:
//           store.x = m02;
//           store.y = m12;
//           store.z = m22;
//           break;
//       default:
//           logger.warning("Invalid column index.");
//           throw new Exception("Invalid column index. " + i);
//       }
//       return store;
//   }


    /**
 * <code>setColumn</code> sets a particular column of this matrix to that
 * represented by the provided vector.
 * 
 * @param i the column to set.
 * @param column the data to set.
 * 
 * @throws Exception the exception
 */
    public void setColumn(int i, float[] column) throws Exception {

        if (column == null) {
            logger.warning("Column is null. Ignoring.");
            return;
        }
        switch (i) {
        case 0:
            m00 = column[0];
            m10 = column[1];
            m20 = column[2];
            m30 = column[3];
            break;
        case 1:
            m01 = column[0];
            m11 = column[1];
            m21 = column[2];
            m31 = column[3];
            break;
        case 2:
            m02 = column[0];
            m12 = column[1];
            m22 = column[2];
            m32 = column[3];
            break;
        case 3:
            m03 = column[0];
            m13 = column[1];
            m23 = column[2];
            m33 = column[3];
            break;
        default:
            logger.warning("Invalid column index.");
            throw new Exception("Invalid column index. " + i);
        }    }

    /**
     * <code>setRow</code> sets a particular column of this matrix to that
     * represented by the provided vector.
     * 
     * @param i the row to set.
     * @param row the data to set.
     * 
     * @throws Exception the exception
     */
        public void setRow(int i, float[] row) throws Exception {

            if (row == null) {
                logger.warning("row is null. Ignoring.");
                return;
            }
            switch (i) {
            case 0:
                m00 = row[0];
                m01 = row[1];
                m02 = row[2];
                m03 = row[3];
                break;
            case 1:
                m10 = row[0];
                m11 = row[1];
                m12 = row[2];
                m13 = row[3];
                break;
            case 2:
                m20 = row[0];
                m21 = row[1];
                m22 = row[2];
                m23 = row[3];
                break;
            case 3:
                m30 = row[0];
                m31 = row[1];
                m32 = row[2];
                m33 = row[3];
                break;
            default:
                logger.warning("Invalid row index.");
                throw new Exception("Invalid row index. " + i);
            }    }
        
    
    /**
     * <code>set</code> places a given value into the matrix at the given
     * position. If the position is invalid a <code>Exception</code> is
     * thrown.
     * 
     * @param i the row index.
     * @param j the colum index.
     * @param value the value for (i, j).
     * 
     * @throws Exception the exception
     */
    public void set(int i, int j, float value) throws Exception {
        switch (i) {
        case 0:
            switch (j) {
            case 0: m00 = value; return;
            case 1: m01 = value; return;
            case 2: m02 = value; return;
            case 3: m03 = value; return;
            }
        case 1:
            switch (j) {
            case 0: m10 = value; return;
            case 1: m11 = value; return;
            case 2: m12 = value; return;
            case 3: m13 = value; return;
            }
        case 2:
            switch (j) {
            case 0: m20 = value; return;
            case 1: m21 = value; return;
            case 2: m22 = value; return;
            case 3: m23 = value; return;
            }
        case 3:
            switch (j) {
            case 0: m30 = value; return;
            case 1: m31 = value; return;
            case 2: m32 = value; return;
            case 3: m33 = value; return;
            }
        }

        logger.warning("Invalid matrix index.");
        throw new Exception("Invalid indices into matrix.");
    }

    /**
     * <code>set</code> sets the values of this matrix from an array of
     * values.
     * 
     * @param matrix the matrix to set the value to.
     * 
     * @throws Exception if the array is not of size 16.
     */
    public void set(float[][] matrix) throws Exception {
        if (matrix.length != 4 || matrix[0].length != 4) { throw new Exception(
                "Array must be of size 16."); }

        m00 = matrix[0][0];
        m01 = matrix[0][1];
        m02 = matrix[0][2];
        m03 = matrix[0][3];
        m10 = matrix[1][0];
        m11 = matrix[1][1];
        m12 = matrix[1][2];
        m13 = matrix[1][3];
        m20 = matrix[2][0];
        m21 = matrix[2][1];
        m22 = matrix[2][2];
        m23 = matrix[2][3];
        m30 = matrix[3][0];
        m31 = matrix[3][1];
        m32 = matrix[3][2];
        m33 = matrix[3][3];
    }

    /**
     * <code>set</code> sets the values of this matrix from another matrix.
     * 
     * @param matrix the matrix to read the value from.
     * 
     * @return the matrix
     */
    public Matrix set(Matrix matrix) {
        m00 = matrix.m00; m01 = matrix.m01; m02 = matrix.m02; m03 = matrix.m03;
        m10 = matrix.m10; m11 = matrix.m11; m12 = matrix.m12; m13 = matrix.m13;
        m20 = matrix.m20; m21 = matrix.m21; m22 = matrix.m22; m23 = matrix.m23;
        m30 = matrix.m30; m31 = matrix.m31; m32 = matrix.m32; m33 = matrix.m33;
        return this;
    }

    /**
     * <code>set</code> sets the values of this matrix from an array of
     * values assuming that the data is rowMajor order;.
     * 
     * @param matrix the matrix to set the value to.
     * 
     * @throws Exception the exception
     */
    public void set(float[] matrix) throws Exception {
        set(matrix, true);
    }

    /**
     * <code>set</code> sets the values of this matrix from an array of
     * values;.
     * 
     * @param matrix the matrix to set the value to.
     * @param rowMajor whether the incoming data is in row or column major order.
     * 
     * @throws Exception the exception
     */
    public void set(float[] matrix, boolean rowMajor) throws Exception {
        if (matrix.length != 16) throw new Exception(
                "Array must be of size 16.");

        if (rowMajor) {
            m00 = matrix[0];
            m01 = matrix[1];
            m02 = matrix[2];
            m03 = matrix[3];
            m10 = matrix[4];
            m11 = matrix[5];
            m12 = matrix[6];
            m13 = matrix[7];
            m20 = matrix[8];
            m21 = matrix[9];
            m22 = matrix[10];
            m23 = matrix[11];
            m30 = matrix[12];
            m31 = matrix[13];
            m32 = matrix[14];
            m33 = matrix[15];
        } else {
            m00 = matrix[0];
            m01 = matrix[4];
            m02 = matrix[8];
            m03 = matrix[12];
            m10 = matrix[1];
            m11 = matrix[5];
            m12 = matrix[9];
            m13 = matrix[13];
            m20 = matrix[2];
            m21 = matrix[6];
            m22 = matrix[10];
            m23 = matrix[14];
            m30 = matrix[3];
            m31 = matrix[7];
            m32 = matrix[11];
            m33 = matrix[15];
        }
    }

    /**
     * Transpose.
     * 
     * @return the matrix
     * 
     * @throws Exception the exception
     */
    public Matrix transpose() throws Exception {
        float[] tmp = new float[16];
        get(tmp, true);
        Matrix mat = new Matrix(tmp);
    	return mat;
    }

    /**
     * <code>transpose</code> locally transposes this Matrix.
     * 
     * @return this object for chaining.
     */
    public Matrix transposeLocal() {
        float[] tmp = new float[16];
        try {
			get(tmp, true);
			 set(tmp, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return this;
    }
    
    
    /**
     * <code>toFloatBuffer</code> returns a FloatBuffer object that contains
     * the matrix data.
     * 
     * @return matrix data as a FloatBuffer.
     */
    public FloatBuffer toFloatBuffer() {
    	return toFloatBuffer(false);
    }

    /**
     * <code>toFloatBuffer</code> returns a FloatBuffer object that contains the
     * matrix data.
     * 
     * @param columnMajor
     *            if true, this buffer should be filled with column major data,
     *            otherwise it will be filled row major.
     * @return matrix data as a FloatBuffer. The position is set to 0 for
     *         convenience.
     */
    public FloatBuffer toFloatBuffer(boolean columnMajor) {
    	FloatBuffer fb = ToolsBuffers.createFloatBuffer(16);
    	fillFloatBuffer(fb, columnMajor);
    	fb.rewind();
    	return fb;
    }
    
    /**
     * <code>fillFloatBuffer</code> fills a FloatBuffer object with
     * the matrix data.
     * 
     * @param fb the buffer to fill, must be correct size
     * 
     * @return matrix data as a FloatBuffer.
     */
    public FloatBuffer fillFloatBuffer(FloatBuffer fb) {
    	return fillFloatBuffer(fb, false);
    }

    /**
     * <code>fillFloatBuffer</code> fills a FloatBuffer object with the matrix
     * data.
     * 
     * @param fb the buffer to fill, starting at current position. Must have
     * room for 16 more floats.
     * @param columnMajor if true, this buffer should be filled with column major data,
     * otherwise it will be filled row major.
     * 
     * @return matrix data as a FloatBuffer. (position is advanced by 16 and any
     * limit set is not changed).
     */
    public FloatBuffer fillFloatBuffer(FloatBuffer fb, boolean columnMajor) {
        if(columnMajor) {
    	    fb.put(m00).put(m10).put(m20).put(m30);
	        fb.put(m01).put(m11).put(m21).put(m31);
	        fb.put(m02).put(m12).put(m22).put(m32);
	        fb.put(m03).put(m13).put(m23).put(m33);
	    } else {
	        fb.put(m00).put(m01).put(m02).put(m03);
	        fb.put(m10).put(m11).put(m12).put(m13);
	        fb.put(m20).put(m21).put(m22).put(m23);
	        fb.put(m30).put(m31).put(m32).put(m33);
	    }
        return fb;
    }
    
    /**
     * <code>readFloatBuffer</code> reads value for this matrix from a FloatBuffer.
     * 
     * @param fb the buffer to read from, must be correct size
     * 
     * @return this data as a FloatBuffer.
     */
    public Matrix readFloatBuffer(FloatBuffer fb) {
    	return readFloatBuffer(fb, false);
    }

    /**
     * <code>readFloatBuffer</code> reads value for this matrix from a FloatBuffer.
     * 
     * @param fb the buffer to read from, must be correct size
     * @param columnMajor if true, this buffer should be filled with column
     * major data, otherwise it will be filled row major.
     * 
     * @return this data as a FloatBuffer.
     */
    public Matrix readFloatBuffer(FloatBuffer fb, boolean columnMajor) {
    	
    	if(columnMajor) {
    		m00 = fb.get(); m10 = fb.get(); m20 = fb.get(); m30 = fb.get();
    		m01 = fb.get(); m11 = fb.get(); m21 = fb.get(); m31 = fb.get();
    		m02 = fb.get(); m12 = fb.get(); m22 = fb.get(); m32 = fb.get();
    		m03 = fb.get(); m13 = fb.get(); m23 = fb.get(); m33 = fb.get();
    	} else {
    		m00 = fb.get(); m01 = fb.get(); m02 = fb.get(); m03 = fb.get();
    		m10 = fb.get(); m11 = fb.get(); m12 = fb.get(); m13 = fb.get();
    		m20 = fb.get(); m21 = fb.get(); m22 = fb.get(); m23 = fb.get();
    		m30 = fb.get(); m31 = fb.get(); m32 = fb.get(); m33 = fb.get();
    	}
        return this;
    }

    /**
     * <code>loadIdentity</code> sets this matrix to the identity matrix,
     * namely all zeros with ones along the diagonal.
     */
    public void loadIdentity() {
        m01 = m02 = m03 = 0.0f;
        m10 = m12 = m13 = 0.0f;
        m20 = m21 = m23 = 0.0f;
        m30 = m31 = m32 = 0.0f;
        m00 = m11 = m22 = m33 = 1.0f;
    }

    /**
     * <code>fromAngleAxis</code> sets this matrix4f to the values specified
     * by an angle and an axis of rotation.  This method creates an object, so
     * use fromAngleNormalAxis if your axis is already normalized.
     * 
     * @param angle the angle to rotate (in radians).
     * @param axis the axis of rotation.
     */
    public void fromAngleAxis(float angle, Vector3D axis) {
        axis.normalizeLocal();
        fromAngleNormalAxis(angle, axis);
    }

    /**
     * <code>fromAngleNormalAxis</code> sets this matrix4f to the values
     * specified by an angle and a normalized axis of rotation.
     * 
     * @param angle the angle to rotate (in radians).
     * @param axis the axis of rotation (already normalized).
     */
    public void fromAngleNormalAxis(float angle, Vector3D axis) {
        zero();
        m33 = 1;

        float fCos = (float) ToolsMath.cos(angle);
        float fSin = (float) ToolsMath.sin(angle);
        float fOneMinusCos = ((float)1.0)-fCos;
        float fX2 = axis.x*axis.x;
        float fY2 = axis.y*axis.y;
        float fZ2 = axis.z*axis.z;
        float fXYM = axis.x*axis.y*fOneMinusCos;
        float fXZM = axis.x*axis.z*fOneMinusCos;
        float fYZM = axis.y*axis.z*fOneMinusCos;
        float fXSin = axis.x*fSin;
        float fYSin = axis.y*fSin;
        float fZSin = axis.z*fSin;
        
        m00 = fX2*fOneMinusCos+fCos;
        m01 = fXYM-fZSin;
        m02 = fXZM+fYSin;
        m10 = fXYM+fZSin;
        m11 = fY2*fOneMinusCos+fCos;
        m12 = fYZM-fXSin;
        m20 = fXZM-fYSin;
        m21 = fYZM+fXSin;
        m22 = fZ2*fOneMinusCos+fCos;
    }

    /**
     * <code>mult</code> multiplies this matrix by a scalar.
     * 
     * @param scalar the scalar to multiply this matrix by.
     */
    public void multLocal(float scalar) {
        m00 *= scalar;
        m01 *= scalar;
        m02 *= scalar;
        m03 *= scalar;
        m10 *= scalar;
        m11 *= scalar;
        m12 *= scalar;
        m13 *= scalar;
        m20 *= scalar;
        m21 *= scalar;
        m22 *= scalar;
        m23 *= scalar;
        m30 *= scalar;
        m31 *= scalar;
        m32 *= scalar;
        m33 *= scalar;
    }
    
    /**
     * Mult.
     * 
     * @param scalar the scalar
     * 
     * @return the matrix
     */
    public Matrix mult(float scalar) {
    	Matrix out = new Matrix();
    	out.set(this);
    	out.multLocal(scalar);
    	return out;
    }
    
    /**
     * Mult.
     * 
     * @param scalar the scalar
     * @param store the store
     * 
     * @return the matrix
     */
    public Matrix mult(float scalar, Matrix store) {
    	store.set(this);
    	store.multLocal(scalar);
    	return store;
    }

    /**
     * <code>mult</code> multiplies this matrix with another matrix. The
     * result matrix will then be returned. This matrix will be on the left hand
     * side, while the parameter matrix will be on the right.
     * 
     * @param in2 the matrix to multiply this matrix by.
     * 
     * @return the resultant matrix
     */
    public Matrix mult(Matrix in2) {
        return mult(in2, null);
    }

    /**
     * <code>mult</code> multiplies this matrix with another matrix. The
     * result matrix will then be returned. This matrix will be on the left hand
     * side, while the parameter matrix will be on the right.
     * 
     * @param in2 the matrix to multiply this matrix by.
     * @param store where to store the result. It is safe for in2 and store to be
     * the same object.
     * 
     * @return the resultant matrix
     */
    public Matrix mult(Matrix in2, Matrix store) {
        if (store == null) store = new Matrix();

        float temp00, temp01, temp02, temp03;
        float temp10, temp11, temp12, temp13;
        float temp20, temp21, temp22, temp23;
        float temp30, temp31, temp32, temp33;

        temp00 = m00 * in2.m00 + 
                m01 * in2.m10 + 
                m02 * in2.m20 + 
                m03 * in2.m30;
        temp01 = m00 * in2.m01 + 
                m01 * in2.m11 + 
                m02 * in2.m21 +
                m03 * in2.m31;
        temp02 = m00 * in2.m02 + 
                m01 * in2.m12 + 
                m02 * in2.m22 +
                m03 * in2.m32;
        temp03 = m00 * in2.m03 + 
                m01 * in2.m13 + 
                m02 * in2.m23 + 
                m03 * in2.m33;
        
        temp10 = m10 * in2.m00 + 
                m11 * in2.m10 + 
                m12 * in2.m20 +
                m13 * in2.m30;
        temp11 = m10 * in2.m01 +
                m11 * in2.m11 +
                m12 * in2.m21 +
                m13 * in2.m31;
        temp12 = m10 * in2.m02 +
                m11 * in2.m12 + 
                m12 * in2.m22 +
                m13 * in2.m32;
        temp13 = m10 * in2.m03 +
                m11 * in2.m13 +
                m12 * in2.m23 + 
                m13 * in2.m33;

        temp20 = m20 * in2.m00 + 
                m21 * in2.m10 + 
                m22 * in2.m20 +
                m23 * in2.m30;
        temp21 = m20 * in2.m01 + 
                m21 * in2.m11 + 
                m22 * in2.m21 +
                m23 * in2.m31;
        temp22 = m20 * in2.m02 + 
                m21 * in2.m12 + 
                m22 * in2.m22 +
                m23 * in2.m32;
        temp23 = m20 * in2.m03 + 
                m21 * in2.m13 + 
                m22 * in2.m23 +
                m23 * in2.m33;

        temp30 = m30 * in2.m00 + 
                m31 * in2.m10 + 
                m32 * in2.m20 +
                m33 * in2.m30;
        temp31 = m30 * in2.m01 + 
                m31 * in2.m11 + 
                m32 * in2.m21 +
                m33 * in2.m31;
        temp32 = m30 * in2.m02 + 
                m31 * in2.m12 + 
                m32 * in2.m22 +
                m33 * in2.m32;
        temp33 = m30 * in2.m03 + 
                m31 * in2.m13 + 
                m32 * in2.m23 +
                m33 * in2.m33;
        
        store.m00 = temp00;  store.m01 = temp01;  store.m02 = temp02;  store.m03 = temp03;
        store.m10 = temp10;  store.m11 = temp11;  store.m12 = temp12;  store.m13 = temp13;
        store.m20 = temp20;  store.m21 = temp21;  store.m22 = temp22;  store.m23 = temp23;
        store.m30 = temp30;  store.m31 = temp31;  store.m32 = temp32;  store.m33 = temp33;
        
        return store;
    }

    /**
     * <code>mult</code> multiplies this matrix with another matrix. The
     * results are stored internally and a handle to this matrix will
     * then be returned. This matrix will be on the left hand
     * side, while the parameter matrix will be on the right.
     * 
     * @param in2 the matrix to multiply this matrix by.
     * 
     * @return the resultant matrix
     */
    public Matrix multLocal(Matrix in2) {
        return mult(in2, this);
    }

    /**
     * <code>mult</code> multiplies a vector about a rotation matrix. The
     * resulting vector is returned as a new Vector3f.
     * 
     * @param vec vec to multiply against.
     * 
     * @return the rotated vector.
     */
    public Vector3D mult(Vector3D vec) {
        return mult(vec, vec);
    }

    /**
     * <code>mult</code> multiplies a vector about a rotation matrix and adds
     * translation. The resulting vector is returned.
     * 
     * @param vec vec to multiply against.
     * @param store a vector to store the result in. Created if null is passed.
     * 
     * @return the rotated vector.
     */
    public Vector3D mult(Vector3D vec, Vector3D store) {
        if (store == null) store = new Vector3D(0,0);
        
        float
        vx = vec.x,
        vy = vec.y, 
        vz = vec.z;
        store.x = m00 * vx + m01 * vy + m02 * vz + m03;
        store.y = m10 * vx + m11 * vy + m12 * vz + m13;
        store.z = m20 * vx + m21 * vy + m22 * vz + m23;

//        store.w = m30 * vx + m31 * vy + m32 * vz + m33; //FIXME NEEDED?
        return store;
    }

    /**
     * <code>mult</code> multiplies a vector about a rotation matrix. The
     * resulting vector is returned.
     * 
     * @param vec vec to multiply against.
     * @param store a vector to store the result in.  created if null is passed.
     * 
     * @return the rotated vector.
     */
    public Vector3D multAcross(Vector3D vec, Vector3D store) {
        if (null == vec) {
            logger.info("Source vector is null, null result returned.");
            return null;
        }
        if (store == null) store = new Vector3D(0,0,0);
        
        float vx = vec.x, vy = vec.y, vz = vec.z;
        store.x = m00 * vx + m10 * vy + m20 * vz + m30 * 1;
        store.y = m01 * vx + m11 * vy + m21 * vz + m31 * 1;
        store.z = m02 * vx + m12 * vy + m22 * vz + m32 * 1;

        return store;
    }

    /**
     * <code>mult</code> multiplies a quaternion about a matrix. The
     * resulting vector is returned.
     * 
     * @param vec vec to multiply against.
     * @param store a quaternion to store the result in.  created if null is passed.
     * 
     * @return store = this * vec
     */
    public Quaternion mult(Quaternion vec, Quaternion store) {

        if (null == vec) {
            logger.warning("Source vector is null, null result returned.");
            return null;
        }
        if (store == null) store = new Quaternion();

        float x = m00 * vec.x + m10 * vec.y + m20 * vec.z + m30 * vec.w;
        float y = m01 * vec.x + m11 * vec.y + m21 * vec.z + m31 * vec.w;
        float z = m02 * vec.x + m12 * vec.y + m22 * vec.z + m32 * vec.w;
        float w = m03 * vec.x + m13 * vec.y + m23 * vec.z + m33 * vec.w;
        store.x = x;
        store.y = y;
        store.z = z;
        store.w = w;

        return store;
    }
    
    /**
     * <code>mult</code> multiplies an array of 4 floats against this rotation
     * matrix. The results are stored directly in the array. (vec4f x mat4f)
     * 
     * @param vec4f float array (size 4) to multiply against the matrix.
     * 
     * @return the vec4f for chaining.
     */
    public float[] mult(float[] vec4f) {
        if (null == vec4f || vec4f.length != 4) {
            logger.warning("invalid array given, must be nonnull and length 4");
            return null;
        }

        float x = vec4f[0], y = vec4f[1], z = vec4f[2], w = vec4f[3];
        
        vec4f[0] = m00 * x + m01 * y + m02 * z + m03 * w;
        vec4f[1] = m10 * x + m11 * y + m12 * z + m13 * w;
        vec4f[2] = m20 * x + m21 * y + m22 * z + m23 * w;
        vec4f[3] = m30 * x + m31 * y + m32 * z + m33 * w;

        return vec4f;
    }

    /**
     * <code>mult</code> multiplies an array of 4 floats against this rotation
     * matrix. The results are stored directly in the array. (vec4f x mat4f)
     * 
     * @param vec4f float array (size 4) to multiply against the matrix.
     * 
     * @return the vec4f for chaining.
     */
    public float[] multAcross(float[] vec4f) {
        if (null == vec4f || vec4f.length != 4) {
            logger.warning("invalid array given, must be nonnull and length 4");
            return null;
        }

        float x = vec4f[0], y = vec4f[1], z = vec4f[2], w = vec4f[3];
        
        vec4f[0] = m00 * x + m10 * y + m20 * z + m30 * w;
        vec4f[1] = m01 * x + m11 * y + m21 * z + m31 * w;
        vec4f[2] = m02 * x + m12 * y + m22 * z + m32 * w;
        vec4f[3] = m03 * x + m13 * y + m23 * z + m33 * w;

        return vec4f;
    }

    /**
     * Inverts this matrix as a new Matrix4f.
     * 
     * @return The new inverse matrix
     */
    public Matrix invert() {
        return invert(null);
    }

    /**
     * Inverts this matrix and stores it in the given store.
     * 
     * @param store the store
     * 
     * @return The store
     */
    public Matrix invert(Matrix store) {
        if (store == null) store = new Matrix();

        float fA0 = m00*m11 - m01*m10;
        float fA1 = m00*m12 - m02*m10;
        float fA2 = m00*m13 - m03*m10;
        float fA3 = m01*m12 - m02*m11;
        float fA4 = m01*m13 - m03*m11;
        float fA5 = m02*m13 - m03*m12;
        float fB0 = m20*m31 - m21*m30;
        float fB1 = m20*m32 - m22*m30;
        float fB2 = m20*m33 - m23*m30;
        float fB3 = m21*m32 - m22*m31;
        float fB4 = m21*m33 - m23*m31;
        float fB5 = m22*m33 - m23*m32;
        float fDet = fA0*fB5-fA1*fB4+fA2*fB3+fA3*fB2-fA4*fB1+fA5*fB0;

//        if ( FastMath.abs(fDet) <= FastMath.FLT_EPSILON ) //TODO ENABLE ORIGINAL
//            throw new ArithmeticException("This matrix cannot be inverted");
        
        if ( ToolsMath.abs(fDet) <= ToolsMath.DBL_EPSILON )
            throw new ArithmeticException("This matrix cannot be inverted");

        store.m00 = + m11*fB5 - m12*fB4 + m13*fB3;
        store.m10 = - m10*fB5 + m12*fB2 - m13*fB1;
        store.m20 = + m10*fB4 - m11*fB2 + m13*fB0;
        store.m30 = - m10*fB3 + m11*fB1 - m12*fB0;
        store.m01 = - m01*fB5 + m02*fB4 - m03*fB3;
        store.m11 = + m00*fB5 - m02*fB2 + m03*fB1;
        store.m21 = - m00*fB4 + m01*fB2 - m03*fB0;
        store.m31 = + m00*fB3 - m01*fB1 + m02*fB0;
        store.m02 = + m31*fA5 - m32*fA4 + m33*fA3;
        store.m12 = - m30*fA5 + m32*fA2 - m33*fA1;
        store.m22 = + m30*fA4 - m31*fA2 + m33*fA0;
        store.m32 = - m30*fA3 + m31*fA1 - m32*fA0;
        store.m03 = - m21*fA5 + m22*fA4 - m23*fA3;
        store.m13 = + m20*fA5 - m22*fA2 + m23*fA1;
        store.m23 = - m20*fA4 + m21*fA2 - m23*fA0;
        store.m33 = + m20*fA3 - m21*fA1 + m22*fA0;

        float fInvDet = 1.0f/fDet;
        store.multLocal(fInvDet);

        return store;
    }

    /**
     * Inverts this matrix locally.
     * 
     * @return this
     */
    public Matrix invertLocal() {

        float fA0 = m00*m11 - m01*m10;
        float fA1 = m00*m12 - m02*m10;
        float fA2 = m00*m13 - m03*m10;
        float fA3 = m01*m12 - m02*m11;
        float fA4 = m01*m13 - m03*m11;
        float fA5 = m02*m13 - m03*m12;
        float fB0 = m20*m31 - m21*m30;
        float fB1 = m20*m32 - m22*m30;
        float fB2 = m20*m33 - m23*m30;
        float fB3 = m21*m32 - m22*m31;
        float fB4 = m21*m33 - m23*m31;
        float fB5 = m22*m33 - m23*m32;
        float fDet = fA0*fB5-fA1*fB4+fA2*fB3+fA3*fB2-fA4*fB1+fA5*fB0;

        if ( ToolsMath.abs(fDet) <= ToolsMath.FLT_EPSILON )
            return zero();

        float f00 = + m11*fB5 - m12*fB4 + m13*fB3;
        float f10 = - m10*fB5 + m12*fB2 - m13*fB1;
        float f20 = + m10*fB4 - m11*fB2 + m13*fB0;
        float f30 = - m10*fB3 + m11*fB1 - m12*fB0;
        float f01 = - m01*fB5 + m02*fB4 - m03*fB3;
        float f11 = + m00*fB5 - m02*fB2 + m03*fB1;
        float f21 = - m00*fB4 + m01*fB2 - m03*fB0;
        float f31 = + m00*fB3 - m01*fB1 + m02*fB0;
        float f02 = + m31*fA5 - m32*fA4 + m33*fA3;
        float f12 = - m30*fA5 + m32*fA2 - m33*fA1;
        float f22 = + m30*fA4 - m31*fA2 + m33*fA0;
        float f32 = - m30*fA3 + m31*fA1 - m32*fA0;
        float f03 = - m21*fA5 + m22*fA4 - m23*fA3;
        float f13 = + m20*fA5 - m22*fA2 + m23*fA1;
        float f23 = - m20*fA4 + m21*fA2 - m23*fA0;
        float f33 = + m20*fA3 - m21*fA1 + m22*fA0;
        
        m00 = f00;
        m01 = f01;
        m02 = f02;
        m03 = f03;
        m10 = f10;
        m11 = f11;
        m12 = f12;
        m13 = f13;
        m20 = f20;
        m21 = f21;
        m22 = f22;
        m23 = f23;
        m30 = f30;
        m31 = f31;
        m32 = f32;
        m33 = f33;

        float fInvDet = 1.0f/fDet;
        multLocal(fInvDet);

        return this;
    }
    
    /**
     * Returns a new matrix representing the adjoint of this matrix.
     * 
     * @return The adjoint matrix
     */
    public Matrix adjoint() {
        return adjoint(null);
    }
     
    
    /**
     * Places the adjoint of this matrix in store (creates store if null.)
     * 
     * @param store The matrix to store the result in.  If null, a new matrix is created.
     * 
     * @return store
     */
    public Matrix adjoint(Matrix store) {
        if (store == null) store = new Matrix();

        float fA0 = m00*m11 - m01*m10;
        float fA1 = m00*m12 - m02*m10;
        float fA2 = m00*m13 - m03*m10;
        float fA3 = m01*m12 - m02*m11;
        float fA4 = m01*m13 - m03*m11;
        float fA5 = m02*m13 - m03*m12;
        float fB0 = m20*m31 - m21*m30;
        float fB1 = m20*m32 - m22*m30;
        float fB2 = m20*m33 - m23*m30;
        float fB3 = m21*m32 - m22*m31;
        float fB4 = m21*m33 - m23*m31;
        float fB5 = m22*m33 - m23*m32;

        store.m00 = + m11*fB5 - m12*fB4 + m13*fB3;
        store.m10 = - m10*fB5 + m12*fB2 - m13*fB1;
        store.m20 = + m10*fB4 - m11*fB2 + m13*fB0;
        store.m30 = - m10*fB3 + m11*fB1 - m12*fB0;
        store.m01 = - m01*fB5 + m02*fB4 - m03*fB3;
        store.m11 = + m00*fB5 - m02*fB2 + m03*fB1;
        store.m21 = - m00*fB4 + m01*fB2 - m03*fB0;
        store.m31 = + m00*fB3 - m01*fB1 + m02*fB0;
        store.m02 = + m31*fA5 - m32*fA4 + m33*fA3;
        store.m12 = - m30*fA5 + m32*fA2 - m33*fA1;
        store.m22 = + m30*fA4 - m31*fA2 + m33*fA0;
        store.m32 = - m30*fA3 + m31*fA1 - m32*fA0;
        store.m03 = - m21*fA5 + m22*fA4 - m23*fA3;
        store.m13 = + m20*fA5 - m22*fA2 + m23*fA1;
        store.m23 = - m20*fA4 + m21*fA2 - m23*fA0;
        store.m33 = + m20*fA3 - m21*fA1 + m22*fA0;

        return store;
    }

    /**
     * <code>determinant</code> generates the determinate of this matrix.
     * 
     * @return the determinate
     */
    public float determinant() {
        float fA0 = m00*m11 - m01*m10;
        float fA1 = m00*m12 - m02*m10;
        float fA2 = m00*m13 - m03*m10;
        float fA3 = m01*m12 - m02*m11;
        float fA4 = m01*m13 - m03*m11;
        float fA5 = m02*m13 - m03*m12;
        float fB0 = m20*m31 - m21*m30;
        float fB1 = m20*m32 - m22*m30;
        float fB2 = m20*m33 - m23*m30;
        float fB3 = m21*m32 - m22*m31;
        float fB4 = m21*m33 - m23*m31;
        float fB5 = m22*m33 - m23*m32;
        float fDet = fA0*fB5-fA1*fB4+fA2*fB3+fA3*fB2-fA4*fB1+fA5*fB0;
        return fDet;
    }

    /**
     * Sets all of the values in this matrix to zero.
     * 
     * @return this matrix
     */
    public Matrix zero() {
        m00 = m01 = m02 = m03 = 0.0f;
        m10 = m11 = m12 = m13 = 0.0f;
        m20 = m21 = m22 = m23 = 0.0f;
        m30 = m31 = m32 = m33 = 0.0f;
        return this;
    }
    
    /**
     * Adds the.
     * 
     * @param mat the mat
     * 
     * @return the matrix
     */
    public Matrix add(Matrix mat) {
    	Matrix result = new Matrix();
    	result.m00 = this.m00 + mat.m00;
    	result.m01 = this.m01 + mat.m01;
    	result.m02 = this.m02 + mat.m02;
    	result.m03 = this.m03 + mat.m03;
    	result.m10 = this.m10 + mat.m10;
    	result.m11 = this.m11 + mat.m11;
    	result.m12 = this.m12 + mat.m12;
    	result.m13 = this.m13 + mat.m13;
    	result.m20 = this.m20 + mat.m20;
    	result.m21 = this.m21 + mat.m21;
    	result.m22 = this.m22 + mat.m22;
    	result.m23 = this.m23 + mat.m23;
    	result.m30 = this.m30 + mat.m30;
    	result.m31 = this.m31 + mat.m31;
    	result.m32 = this.m32 + mat.m32;
    	result.m33 = this.m33 + mat.m33;
    	return result;
    }

    /**
     * <code>add</code> adds the values of a parameter matrix to this matrix.
     * 
     * @param mat the matrix to add to this.
     */
    public void addLocal(Matrix mat) {
        m00 += mat.m00;
        m01 += mat.m01;
        m02 += mat.m02;
        m03 += mat.m03;
        m10 += mat.m10;
        m11 += mat.m11;
        m12 += mat.m12;
        m13 += mat.m13;
        m20 += mat.m20;
        m21 += mat.m21;
        m22 += mat.m22;
        m23 += mat.m23;
        m30 += mat.m30;
        m31 += mat.m31;
        m32 += mat.m32;
        m33 += mat.m33;
    }
    
    /**
     * To translation vector.
     * 
     * @return the vector3 d
     */
    public Vector3D toTranslationVector() {
        return new Vector3D(m03, m13, m23);
    }
    
    /**
     * To translation vector.
     * 
     * @param vector the vector
     */
    public void toTranslationVector(Vector3D vector) {
        vector.setXYZ(m03, m13, m23);
    }
    
    /**
     * To rotation quat.
     * 
     * @return the quaternion
     */
    public Quaternion toRotationQuat() {
        Quaternion quat = new Quaternion();
        quat.fromRotationMatrix(toRotationMatrix());
        return quat;
    }
    
    /**
     * To rotation quat.
     * 
     * @param q the q
     */
    public void toRotationQuat(Quaternion q) {
        q.fromRotationMatrix(toRotationMatrix());
    }
    
    /**
     * To rotation matrix.
     * 
     * @return the matrix
     */
    public Matrix toRotationMatrix() {
        return new Matrix(
        		m00, m01, m02, 0,  
        		m10, m11, m12, 0, 
        		m20, m21, m22, 0, 
        		0,	0,	0,	1);
        
    }
    
//    public void toRotationMatrix(Matrix3f mat) {
//        mat.m00 = m00;
//        mat.m01 = m01;
//        mat.m02 = m02;
//        mat.m10 = m10;
//        mat.m11 = m11;
//        mat.m12 = m12;
//        mat.m20 = m20;
//        mat.m21 = m21;
//        mat.m22 = m22;
//        
//    }

    /**
 * <code>setTranslation</code> will set the matrix's translation values.
 * 
 * @param translation the new values for the translation.
 * 
 * @throws Exception if translation is not size 3.
 */
    public void setTranslation(float[] translation) throws Exception {
        if (translation.length != 3) { throw new Exception(
                "Translation size must be 3."); }
        m03 = translation[0];
        m13 = translation[1];
        m23 = translation[2];
    }

    /**
     * <code>setTranslation</code> will set the matrix's translation values.
     * 
     * @param x value of the translation on the x axis
     * @param y value of the translation on the y axis
     * @param z value of the translation on the z axis
     */
    public void setTranslation(float x, float y, float z) {
        m03 = x;
        m13 = y;
        m23 = z;
    }

    /**
     * <code>setTranslation</code> will set the matrix's translation values.
     * 
     * @param translation the new values for the translation.
     */
    public void setTranslation(Vector3D translation) {
        m03 = translation.x;
        m13 = translation.y;
        m23 = translation.z;
    }

    /**
     * <code>setInverseTranslation</code> will set the matrix's inverse
     * translation values.
     * 
     * @param translation the new values for the inverse translation.
     * 
     * @throws Exception if translation is not size 3.
     */
    public void setInverseTranslation(float[] translation) throws Exception {
        if (translation.length != 3) { throw new Exception(
                "Translation size must be 3."); }
        m03 = -translation[0];
        m13 = -translation[1];
        m23 = -translation[2];
    }

    /**
     * <code>angleRotation</code> sets this matrix to that of a rotation about
     * three axes (x, y, z). Where each axis has a specified rotation in
     * degrees. These rotations are expressed in a single <code>Vector3D</code>
     * object.
     * 
     * @param angles the angles to rotate (in degrees).
     */
    public void angleRotation(Vector3D angles) {
        float angle;
        float sr, sp, sy, cr, cp, cy;

        angle = (angles.z * ToolsMath.DEG_TO_RAD);
        sy = ToolsMath.sin(angle);
        cy = ToolsMath.cos(angle);
        angle = (angles.y * ToolsMath.DEG_TO_RAD);
        sp = ToolsMath.sin(angle);
        cp = ToolsMath.cos(angle);
        angle = (angles.x * ToolsMath.DEG_TO_RAD);
        sr = ToolsMath.sin(angle);
        cr = ToolsMath.cos(angle);

        // matrix = (Z * Y) * X
        m00 = cp * cy;
        m10 = cp * sy;
        m20 = -sp;
        m01 = sr * sp * cy + cr * -sy;
        m11 = sr * sp * sy + cr * cy;
        m21 = sr * cp;
        m02 = (cr * sp * cy + -sr * -sy);
        m12 = (cr * sp * sy + -sr * cy);
        m22 = cr * cp;
        m03 = 0.0f;
        m13 = 0.0f;
        m23 = 0.0f;
    }

    /**
     * <code>setRotationQuaternion</code> builds a rotation from a
     * <code>Quaternion</code>.
     * 
     * @param quat the quaternion to build the rotation from.
     * 
     * @throws NullPointerException if quat is null.
     */
    public void setRotationQuaternion(Quaternion quat) {
        quat.toRotationMatrix(this);
    }

    /**
     * <code>setInverseRotationRadians</code> builds an inverted rotation from
     * Euler angles that are in radians.
     * 
     * @param angles the Euler angles in radians.
     * 
     * @throws Exception if angles is not size 3.
     */
    public void setInverseRotationRadians(float[] angles) throws Exception {
        if (angles.length != 3) { throw new Exception(
                "Angles must be of size 3."); }
        double cr = ToolsMath.cos(angles[0]);
        double sr = ToolsMath.sin(angles[0]);
        double cp = ToolsMath.cos(angles[1]);
        double sp = ToolsMath.sin(angles[1]);
        double cy = ToolsMath.cos(angles[2]);
        double sy = ToolsMath.sin(angles[2]);

        m00 = (float) (cp * cy);
        m10 = (float) (cp * sy);
        m20 = (float) (-sp);

        double srsp = sr * sp;
        double crsp = cr * sp;

        m01 = (float) (srsp * cy - cr * sy);
        m11 = (float) (srsp * sy + cr * cy);
        m21 = (float) (sr * cp);

        m02 = (float) (crsp * cy + sr * sy);
        m12 = (float) (crsp * sy - sr * cy);
        m22 = (float) (cr * cp);
    }

    /**
     * <code>setInverseRotationDegrees</code> builds an inverted rotation from
     * Euler angles that are in degrees.
     * 
     * @param angles the Euler angles (in degrees).
     * 
     * @throws Exception if angles is not size 3.
     */
    public void setInverseRotationDegrees(float[] angles) throws Exception {
        if (angles.length != 3) { throw new Exception(
                "Angles must be of size 3."); }
        float vec[] = new float[3];
        vec[0] = (angles[0] * ToolsMath.RAD_TO_DEG);
        vec[1] = (angles[1] * ToolsMath.RAD_TO_DEG);
        vec[2] = (angles[2] * ToolsMath.RAD_TO_DEG);
        setInverseRotationRadians(vec);
    }

    /**
     * <code>inverseTranslateVect</code> translates a given Vector3D by the
     * translation part of this matrix.
     * 
     * @param vec the Vector3D data to be translated.
     * 
     * @throws Exception if the size of the Vector3D is not 3.
     */
    public void inverseTranslateVect(float[] vec) throws Exception {
        if (vec.length != 3) { throw new Exception(
                "vec must be of size 3."); }

        vec[0] = vec[0] - m03;
        vec[1] = vec[1] - m13;
        vec[2] = vec[2] - m23;
    }

    /**
     * <code>inverseTranslateVect</code> translates a given Vector3D by the
     * translation part of this matrix.
     * 
     * @param data the Vector3D to be translated.
     * 
     * @throws Exception if the size of the Vector3D is not 3.
     */
    public void inverseTranslateVect(Vector3D data) {
        data.x -= m03;
        data.y -= m13;
        data.z -= m23;
    }

    /**
     * <code>inverseTranslateVect</code> translates a given Vector3D by the
     * translation part of this matrix.
     * 
     * @param data the Vector3D to be translated.
     * 
     * @throws Exception if the size of the Vector3D is not 3.
     */
    public void translateVect(Vector3D data) {
        data.x += m03;
        data.y += m13;
        data.z += m23;
    }

    /**
     * <code>inverseRotateVect</code> rotates a given Vector3D by the rotation
     * part of this matrix.
     * 
     * @param vec the Vector3D to be rotated.
     */
    public void inverseRotateVect(Vector3D vec) {
        float vx = vec.x, vy = vec.y, vz = vec.z;

        vec.x = vx * m00 + vy * m10 + vz * m20;
        vec.y = vx * m01 + vy * m11 + vz * m21;
        vec.z = vx * m02 + vy * m12 + vz * m22;
    }
    
    /**
     * Rotate vect.
     * 
     * @param vec the vec
     */
    public void rotateVect(Vector3D vec) {
        float vx = vec.x, vy = vec.y, vz = vec.z;

        vec.x = vx * m00 + vy * m01 + vz * m02;
        vec.y = vx * m10 + vy * m11 + vz * m12;
        vec.z = vx * m20 + vy * m21 + vz * m22;
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * It is in a format of a 4x4 matrix. For example, an identity matrix would
     * be represented by the following string. com.jme.math.Matrix3f <br>[<br>
     * 1.0  0.0  0.0  0.0 <br>
     * 0.0  1.0  0.0  0.0 <br>
     * 0.0  0.0  1.0  0.0 <br>
     * 0.0  0.0  0.0  1.0 <br>]<br>
     * 
     * @return the string representation of this object.
     */
    public String toString() {
        StringBuffer result = new StringBuffer("com.jme.math.Matrix4f\n[\n");
        result.append(" ");
        result.append(m00);
        result.append("  ");
        result.append(m01);
        result.append("  ");
        result.append(m02);
        result.append("  ");
        result.append(m03);
        result.append(" \n");
        result.append(" ");
        result.append(m10);
        result.append("  ");
        result.append(m11);
        result.append("  ");
        result.append(m12);
        result.append("  ");
        result.append(m13);
        result.append(" \n");
        result.append(" ");
        result.append(m20);
        result.append("  ");
        result.append(m21);
        result.append("  ");
        result.append(m22);
        result.append("  ");
        result.append(m23);
        result.append(" \n");
        result.append(" ");
        result.append(m30);
        result.append("  ");
        result.append(m31);
        result.append("  ");
        result.append(m32);
        result.append("  ");
        result.append(m33);
        result.append(" \n]");
        return result.toString();
    }

    /**
     * <code>hashCode</code> returns the hash code value as an integer and is
     * supported for the benefit of hashing based collection classes such as
     * Hashtable, HashMap, HashSet etc.
     * 
     * @return the hashcode for this instance of Matrix4f.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 37;
        hash = 37 * hash + Float.floatToIntBits(m00);
        hash = 37 * hash + Float.floatToIntBits(m01);
        hash = 37 * hash + Float.floatToIntBits(m02);
        hash = 37 * hash + Float.floatToIntBits(m03);

        hash = 37 * hash + Float.floatToIntBits(m10);
        hash = 37 * hash + Float.floatToIntBits(m11);
        hash = 37 * hash + Float.floatToIntBits(m12);
        hash = 37 * hash + Float.floatToIntBits(m13);

        hash = 37 * hash + Float.floatToIntBits(m20);
        hash = 37 * hash + Float.floatToIntBits(m21);
        hash = 37 * hash + Float.floatToIntBits(m22);
        hash = 37 * hash + Float.floatToIntBits(m23);

        hash = 37 * hash + Float.floatToIntBits(m30);
        hash = 37 * hash + Float.floatToIntBits(m31);
        hash = 37 * hash + Float.floatToIntBits(m32);
        hash = 37 * hash + Float.floatToIntBits(m33);

        return hash;
    }
    
    /**
     * are these two matrices the same? they are is they both have the same mXX values.
     * 
     * @param o the object to compare for equality
     * 
     * @return true if they are equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof Matrix) || o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        Matrix comp = (Matrix) o;
        if (Float.compare(m00,comp.m00) != 0) return false;
        if (Float.compare(m01,comp.m01) != 0) return false;
        if (Float.compare(m02,comp.m02) != 0) return false;
        if (Float.compare(m03,comp.m03) != 0) return false;

        if (Float.compare(m10,comp.m10) != 0) return false;
        if (Float.compare(m11,comp.m11) != 0) return false;
        if (Float.compare(m12,comp.m12) != 0) return false;
        if (Float.compare(m13,comp.m13) != 0) return false;

        if (Float.compare(m20,comp.m20) != 0) return false;
        if (Float.compare(m21,comp.m21) != 0) return false;
        if (Float.compare(m22,comp.m22) != 0) return false;
        if (Float.compare(m23,comp.m23) != 0) return false;

        if (Float.compare(m30,comp.m30) != 0) return false;
        if (Float.compare(m31,comp.m31) != 0) return false;
        if (Float.compare(m32,comp.m32) != 0) return false;
        if (Float.compare(m33,comp.m33) != 0) return false;

        return true;
    }


    /**
     * Gets the class tag.
     * 
     * @return the class tag
     */
    public Class<? extends Matrix> getClassTag() {
    	return this.getClass();
    }

    /**
     * Checks if is identity.
     * 
     * @return true if this matrix is identity
     */
    public boolean isIdentity() {
//        return 
//        (m00 == 1 && m01 == 0 && m02 == 0 && m03 == 0) &&
//        (m10 == 0 && m11 == 1 && m12 == 0 && m13 == 0) &&
//        (m20 == 0 && m21 == 0 && m22 == 1 && m23 == 0) &&
//        (m30 == 0 && m31 == 0 && m32 == 0 && m33 == 1);
        if (m00 != 1 || m01 != 0 || m02 != 0 || m03 != 0) return false;
        if (m10 != 0 || m11 != 1 || m12 != 0 || m13 != 0) return false;
        if (m20 != 0 || m21 != 0 || m22 != 1 || m23 != 0) return false;
        if (m30 != 0 || m31 != 0 || m32 != 0 || m33 != 1) return false;
        return true;
//    	return equalIdentity(this); //FIXME this call is more expensive!
    }

    /**
     * Apply a scale to this matrix.
     * 
     * @param scale the scale to apply
     */
    public void scale(Vector3D scale) {
        m00 *= scale.getX();
        m10 *= scale.getX();
        m20 *= scale.getX();
        m30 *= scale.getX();
        m01 *= scale.getY();
        m11 *= scale.getY();
        m21 *= scale.getY();
        m31 *= scale.getY();
        m02 *= scale.getZ();
        m12 *= scale.getZ();
        m22 *= scale.getZ();
        m32 *= scale.getZ();
    }
    
    
    public void rotateX(float angle) {
    	angle = angle * ToolsMath.DEG_TO_RAD;
        float c = ToolsMath.cos(angle);
        float s = ToolsMath.sin(angle);
        apply(1, 0, 0, 0,  0, c, -s, 0,  0, s, c, 0,  0, 0, 0, 1);
      }


      public void rotateY(float angle) {
    	angle = angle * ToolsMath.DEG_TO_RAD;
        float c = ToolsMath.cos(angle);
        float s = ToolsMath.sin(angle);
        apply(c, 0, s, 0,  0, 1, 0, 0,  -s, 0, c, 0,  0, 0, 0, 1);
      }


      public void rotateZ(float angle) {
    	angle = angle * ToolsMath.DEG_TO_RAD;
        float c = ToolsMath.cos(angle);
        float s = ToolsMath.sin(angle);
        apply(c, -s, 0, 0,  s, c, 0, 0,  0, 0, 1, 0,  0, 0, 0, 1);
      }

      private void apply(float n00, float n01, float n02, float n03,
    		  float n10, float n11, float n12, float n13,
    		  float n20, float n21, float n22, float n23,
    		  float n30, float n31, float n32, float n33) {

    	  float r00 = m00*n00 + m01*n10 + m02*n20 + m03*n30;
    	  float r01 = m00*n01 + m01*n11 + m02*n21 + m03*n31;
    	  float r02 = m00*n02 + m01*n12 + m02*n22 + m03*n32;
    	  float r03 = m00*n03 + m01*n13 + m02*n23 + m03*n33;

    	  float r10 = m10*n00 + m11*n10 + m12*n20 + m13*n30;
    	  float r11 = m10*n01 + m11*n11 + m12*n21 + m13*n31;
    	  float r12 = m10*n02 + m11*n12 + m12*n22 + m13*n32;
    	  float r13 = m10*n03 + m11*n13 + m12*n23 + m13*n33;

    	  float r20 = m20*n00 + m21*n10 + m22*n20 + m23*n30;
    	  float r21 = m20*n01 + m21*n11 + m22*n21 + m23*n31;
    	  float r22 = m20*n02 + m21*n12 + m22*n22 + m23*n32;
    	  float r23 = m20*n03 + m21*n13 + m22*n23 + m23*n33;

    	  float r30 = m30*n00 + m31*n10 + m32*n20 + m33*n30;
    	  float r31 = m30*n01 + m31*n11 + m32*n21 + m33*n31;
    	  float r32 = m30*n02 + m31*n12 + m32*n22 + m33*n32;
    	  float r33 = m30*n03 + m31*n13 + m32*n23 + m33*n33;

    	  m00 = r00; m01 = r01; m02 = r02; m03 = r03;
    	  m10 = r10; m11 = r11; m12 = r12; m13 = r13;
    	  m20 = r20; m21 = r21; m22 = r22; m23 = r23;
    	  m30 = r30; m31 = r31; m32 = r32; m33 = r33;
      }




    /**
     * Equal identity.
     * 
     * @param mat the mat
     * 
     * @return true, if successful
     */
    static final boolean equalIdentity(Matrix mat) {
		if (Math.abs(mat.m00 - 1) > 1e-4) return false;
		if (Math.abs(mat.m11 - 1) > 1e-4) return false;
		if (Math.abs(mat.m22 - 1) > 1e-4) return false;
		if (Math.abs(mat.m33 - 1) > 1e-4) return false;

		if (Math.abs(mat.m01) > 1e-4) return false;
		if (Math.abs(mat.m02) > 1e-4) return false;
		if (Math.abs(mat.m03) > 1e-4) return false;

		if (Math.abs(mat.m10) > 1e-4) return false;
		if (Math.abs(mat.m12) > 1e-4) return false;
		if (Math.abs(mat.m13) > 1e-4) return false;

		if (Math.abs(mat.m20) > 1e-4) return false;
		if (Math.abs(mat.m21) > 1e-4) return false;
		if (Math.abs(mat.m23) > 1e-4) return false;

		if (Math.abs(mat.m30) > 1e-4) return false;
		if (Math.abs(mat.m31) > 1e-4) return false;
		if (Math.abs(mat.m32) > 1e-4) return false;

		return true;
    }

    // XXX: This tests more solid than converting the q to a matrix and multiplying... why?
    /**
     * Mult local.
     * 
     * @param rotation the rotation
     */
    public void multLocal(Quaternion rotation) {
        Vector3D axis = new Vector3D(0,0,0);
        float angle = rotation.toAngleAxis(axis);
        Matrix matrix4f = new Matrix();
        matrix4f.fromAngleAxis(angle, axis);
        multLocal(matrix4f);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Matrix clone() {
        try {
            return (Matrix) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // can not happen
        }
    }

    
    
    
    
    
    
    
    
    
    /**
     * The following code generates a 4x4 matrix from a quaternion and a vector.
     * <br>Represents a rotation about that point vector.
     * 
     * @param q the q
     * @param centre the centre
     */
    public void setRotateLocal(Quaternion q, Vector3D centre) {
    	   float sqw = q.w*q.w;
    	   float sqx = q.x*q.x;
    	   float sqy = q.y*q.y;
    	   float sqz = q.z*q.z;
    	   m00 = sqx - sqy - sqz + sqw; // since sqw + sqx + sqy + sqz =1
    	   m11 = -sqx + sqy - sqz + sqw;
    	   m22 = -sqx - sqy + sqz + sqw;
    	   
    	   float tmp1 = q.x*q.y;
    	   float tmp2 = q.z*q.w;
    	   m01 = 2.0f * (tmp1 + tmp2);
    	   m10 = 2.0f * (tmp1 - tmp2);
    	   
    	   tmp1 = q.x*q.z;
    	   tmp2 = q.y*q.w;
    	   m02 = 2.0f * (tmp1 - tmp2);
    	   m20 = 2.0f * (tmp1 + tmp2);
    	   
    	   tmp1 = q.y*q.z;
    	   tmp2 = q.x*q.w;
    	   m12 = 2.0f * (tmp1 + tmp2);
    	   m21 = 2.0f * (tmp1 - tmp2);
    	   
    	   float a1,a2,a3;
    	  if (centre == null) {
    	    a1=a2=a3=0;
    	  } else {
    	    a1 = centre.x;
    	    a2 = centre.y;
    	    a3 = centre.z;
    	  }
    	  m03 = a1 - a1 * m00 - a2 * m01 - a3 * m02;
    	  m13 = a2 - a1 * m10 - a2 * m11 - a3 * m12;
    	  m23 = a3 - a1 * m20 - a2 * m21 - a3 * m22;
    	  m30 = m31 = m32 = 0.0f;
    	  m33 = 1.0f;
    	}
    
	/**
	 * Gets the translation matrix.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * 
	 * @return the translation matrix
	 */
	public static Matrix getTranslationMatrix(float x, float y, float z) {
		return new Matrix(1, 0, 0 , x ,
				          0, 1, 0 , y ,
				          0, 0, 1,	z ,
				          0, 0, 0,	1 );
	}
	
	/**
	 * Gets the inv translation matrix.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * 
	 * @return the inv translation matrix
	 */
	public static Matrix getInvTranslationMatrix(float x, float y, float z) {
		return new Matrix(1, 0, 0 , -x ,
				          0, 1, 0 , -y ,
				          0, 0, 1,	-z ,
				          0, 0, 0,	1 );
	}
	
	/**
	 * Gets the translation matrix and inverse.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * 
	 * @return the translation matrix and inverse
	 */
	public static Matrix[] getTranslationMatrixAndInverse(float x, float y, float z) {
		return new Matrix[]{
				new Matrix(1, 0, 0 , x ,
				          0, 1, 0 , y ,
				          0, 0, 1,	z ,
				          0, 0, 0,	1 ),
				new Matrix(1, 0, 0 ,-x ,
						  0, 1, 0 , -y ,
						  0, 0, 1,	-z ,
						  0, 0, 0,	1 )
		};
	}
	
	/**
	 * Sets the given matrices to be translation and inverse translation matrices, overwriting their previous values.
	 * 
	 * @param m the m
	 * @param mInv the m inv
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public static void toTranslationMatrixAndInverse(Matrix m, Matrix mInv, float x, float y, float z) {
				try {
					m.set(new float[]{
							1, 0, 0 , x ,
					        0, 1, 0 , y ,
					        0, 0, 1,  z ,
					        0, 0, 0,  1 });
					
					mInv.set(new float[]{
							1, 0, 0 , -x ,
							0, 1, 0 , -y ,
							0, 0, 1,  -z ,
							0, 0, 0,  1} );
				} catch (Exception e) {
					e.printStackTrace();
				}
	}

	/**
	 * Gets the x rotation matrix.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the x rotation matrix
	 */
	public static Matrix getXRotationMatrix(Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus = ToolsMath.sin(rotationAngle);
		float cosinus = ToolsMath.cos(rotationAngle);
//		float rotationAngle = PApplet.radians(degree); 
//		float sinus = (float)Math.sin(rotationAngle);
//		float cosinus = (float)Math.cos(rotationAngle);
		
		return new Matrix(
		        1	, 0, 			  0, 	   0,
                0	, cosinus, 	- sinus, rotationPoint.y - (cosinus * rotationPoint.y) - (- sinus * rotationPoint.z) ,
                0	, sinus	, 	cosinus, rotationPoint.z - (sinus * rotationPoint.y)- (cosinus * rotationPoint.z) ,
                0	, 		0, 		  0,      1);
	}
	
	/**
	 * Gets the inv x rotation matrix.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the inv x rotation matrix
	 */
	public static Matrix getInvXRotationMatrix(Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus = ToolsMath.sin(rotationAngle);
		float cosinus = ToolsMath.cos(rotationAngle);
//		float rotationAngle = PApplet.radians(degree); 
//		float sinus = (float)Math.sin(rotationAngle);
//		float cosinus = (float)Math.cos(rotationAngle);
		
		return new Matrix(
		        1	, 0, 			  0, 	   0,
                0	, cosinus, 	 sinus, 	rotationPoint.y - (cosinus * rotationPoint.y) - ( sinus * rotationPoint.z) ,
                0	, -sinus	,cosinus, 	rotationPoint.z - (-sinus * rotationPoint.y)- (cosinus * rotationPoint.z) ,
                0	, 		0, 		  0,      1);
	}
	
	/**
	 * Gets the x rotation matrix and inverse.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the x rotation matrix and inverse
	 */
	public static Matrix[] getXRotationMatrixAndInverse(Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus 	= ToolsMath.sin(rotationAngle);
		float cosinus 	= ToolsMath.cos(rotationAngle);
//		float rotationAngle = PApplet.radians(degree); 
//		float sinus = (float)Math.sin(rotationAngle);
//		float cosinus = (float)Math.cos(rotationAngle);
		
		return new Matrix[]{
				new Matrix(
				        1	, 0, 			  0, 	   0,
		                0	, cosinus, 	- sinus, rotationPoint.y - (cosinus * rotationPoint.y) - (- sinus * rotationPoint.z) ,
		                0	, sinus	, 	cosinus, rotationPoint.z - (sinus * rotationPoint.y)- (cosinus * rotationPoint.z) ,
		                0	, 		0, 		  0,      1),
		        new Matrix(
		                1	, 0, 		  0, 	   0,
		                0	, cosinus, 	 sinus,   rotationPoint.y - (cosinus * rotationPoint.y) - ( sinus * rotationPoint.z) ,
		                0	, -sinus, 	cosinus,  rotationPoint.z - (-sinus * rotationPoint.y)- (cosinus * rotationPoint.z) ,
		                0	, 	0, 	  	  0,       1)
		};
	}
	
	/**
	 * Sets the given matrices to be x-rotation and inverse rotation matrices, overwriting their previous values.
	 * 
	 * @param m the m
	 * @param mInv the m inv
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public static void toXRotationMatrixAndInverse(Matrix m, Matrix mInv, Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus 	= ToolsMath.sin(rotationAngle);
		float cosinus 	= ToolsMath.cos(rotationAngle);
				try {
					m.set(new float[]{
							 1	, 0, 			  0, 	   0,
				                0	, cosinus, 	- sinus, rotationPoint.y - (cosinus * rotationPoint.y) - (- sinus * rotationPoint.z) ,
				                0	, sinus	, 	cosinus, rotationPoint.z - (sinus * rotationPoint.y)- (cosinus * rotationPoint.z) ,
				                0	, 		0, 		  0,      1});
					
					mInv.set(new float[]{
							  1	, 0, 		  0, 	   0,
				                0	, cosinus, 	 sinus,   rotationPoint.y - (cosinus * rotationPoint.y) - ( sinus * rotationPoint.z) ,
				                0	, -sinus, 	cosinus,  rotationPoint.z - (-sinus * rotationPoint.y)- (cosinus * rotationPoint.z) ,
				                0	, 	0, 	  	  0,       1} );
				} catch (Exception e) {
					e.printStackTrace();
				}
	}

	/**
	 * Gets the y rotation matrix.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the y rotation matrix
	 */
	public static Matrix getYRotationMatrix(Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus = ToolsMath.sin(rotationAngle);
		float cosinus = ToolsMath.cos(rotationAngle);
//		float rotationAngle = PApplet.radians(degree); 
//		float sinus = (float)Math.sin(rotationAngle);
//		float cosinus = (float)Math.cos(rotationAngle);
		
		return new Matrix(
		        	cosinus,	0,	  sinus, 	rotationPoint.x - (cosinus * rotationPoint.x)- (sinus * rotationPoint.z) ,
                		0, 		1,		0, 		0 ,
                	-sinus, 	0, 	  cosinus, 	rotationPoint.z - (-sinus * rotationPoint.x) - (cosinus * rotationPoint.z) ,
                		0, 		0, 		0,   	1);
	}
	
	/**
	 * Gets the inv y rotation matrix.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the inv y rotation matrix
	 */
	public static Matrix getInvYRotationMatrix(Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus = ToolsMath.sin(rotationAngle);
		float cosinus = ToolsMath.cos(rotationAngle);
//		float rotationAngle = PApplet.radians(degree); 
//		float sinus = (float)Math.sin(rotationAngle);
//		float cosinus = (float)Math.cos(rotationAngle);
		
		return new Matrix(
		        	cosinus,	0,	  -sinus, 	rotationPoint.x - (cosinus * rotationPoint.x)- (-sinus * rotationPoint.z) ,
                		0, 		1,		0, 		0 ,
                	sinus, 	0, 	  cosinus, 		rotationPoint.z - (sinus * rotationPoint.x) - (cosinus * rotationPoint.z) ,
                		0, 		0, 		0,   	1);
	}
	
	/**
	 * Gets the y rotation matrix and inverse.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the y rotation matrix and inverse
	 */
	public static Matrix[] getYRotationMatrixAndInverse(Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus 	= ToolsMath.sin(rotationAngle);
		float cosinus 	= ToolsMath.cos(rotationAngle);
//		float rotationAngle = PApplet.radians(degree); 
//		float sinus = (float)Math.sin(rotationAngle);
//		float cosinus = (float)Math.cos(rotationAngle);
		
		return new Matrix[]{
				new Matrix(
			        	cosinus,	0,	  sinus, 	rotationPoint.x - (cosinus * rotationPoint.x)- (sinus * rotationPoint.z) ,
	                		0, 		1,		0, 		0 ,
	                	-sinus, 	0, 	  cosinus, 	rotationPoint.z - (-sinus * rotationPoint.x) - (cosinus * rotationPoint.z) ,
	                		0, 		0, 		0,   	1),
	           new	Matrix(cosinus,	0,	  -sinus, 	rotationPoint.x - (cosinus * rotationPoint.x)- (-sinus * rotationPoint.z) ,
	                      0, 		1,		0, 		0 ,
	                      sinus, 	0, 	  cosinus, 	rotationPoint.z - (sinus * rotationPoint.x) - (cosinus * rotationPoint.z) ,
	                      0, 		0, 		0,   	1)
		};
	}
	
	/**
	 * Sets the given matrices to be y-rotation and inverse rotation matrices, overwriting their previous values.
	 * 
	 * @param m the m
	 * @param mInv the m inv
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public static void toYRotationMatrixAndInverse(Matrix m, Matrix mInv, Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus 	= ToolsMath.sin(rotationAngle);
		float cosinus 	= ToolsMath.cos(rotationAngle);
				try {
					m.set(new float[]{
							cosinus,	0,	  sinus, 	rotationPoint.x - (cosinus * rotationPoint.x)- (sinus * rotationPoint.z) ,
	                		0, 		1,		0, 		0 ,
	                	-sinus, 	0, 	  cosinus, 	rotationPoint.z - (-sinus * rotationPoint.x) - (cosinus * rotationPoint.z) ,
	                		0, 		0, 		0,   	1});
					
					mInv.set(new float[]{
							cosinus,	0,	  -sinus, 	rotationPoint.x - (cosinus * rotationPoint.x)- (-sinus * rotationPoint.z) ,
		                      0, 		1,		0, 		0 ,
		                      sinus, 	0, 	  cosinus, 	rotationPoint.z - (sinus * rotationPoint.x) - (cosinus * rotationPoint.z) ,
		                      0, 		0, 		0,   	1} );
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	/**
	 * Gets the z rotation matrix.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the z rotation matrix
	 */
	public static Matrix getZRotationMatrix(Vector3D rotationPoint, float degree){
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus 	= ToolsMath.sin(rotationAngle);
		float cosinus 	= ToolsMath.cos(rotationAngle);
//		float rotationAngle = PApplet.radians(degree); 
//		float sinus = (float)Math.sin(rotationAngle);
//		float cosinus = (float)Math.cos(rotationAngle);
		
		return new Matrix(
		         cosinus ,-sinus,	0, 	rotationPoint.x - cosinus * rotationPoint.x + sinus   * rotationPoint.y ,
				 sinus , cosinus,	0, 	rotationPoint.y - sinus   * rotationPoint.x - cosinus * rotationPoint.y ,
				 0, 		  0, 	1, 	0,
				 0, 		  0, 	0,  1);
	}
	
	/**
	 * Gets the inv z rotation matrix.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the inv z rotation matrix
	 */
	public static Matrix getInvZRotationMatrix(Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus 	= ToolsMath.sin(rotationAngle);
		float cosinus 	= ToolsMath.cos(rotationAngle);
		return new Matrix(
		         cosinus , sinus,	0,  rotationPoint.x - cosinus * rotationPoint.x   - sinus   * rotationPoint.y ,
				 -sinus , cosinus,	0,  rotationPoint.y + sinus   * rotationPoint.x   - cosinus * rotationPoint.y ,
				 0, 		  0, 	1,   0 ,
				 0, 		  0, 	0,   1);
	}
	
	//TODO weiter zusammenfassen mit z.b. sinus   * rotationPoint.y in variable speichern
	/**
	 * Gets the z rotation matrix and inverse.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * 
	 * @return the z rotation matrix and inverse
	 */
	public static Matrix[] getZRotationMatrixAndInverse(Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus 	= ToolsMath.sin(rotationAngle);
		float cosinus 	= ToolsMath.cos(rotationAngle);
		return new Matrix[]{
				new Matrix(
				         cosinus ,-sinus,	0, 	rotationPoint.x - cosinus * rotationPoint.x + sinus   * rotationPoint.y ,
						 sinus , cosinus,	0, 	rotationPoint.y - sinus   * rotationPoint.x - cosinus * rotationPoint.y ,
						 0, 		  0, 	1, 	0,
						 0, 		  0, 	0,  1),
				new Matrix(
				         cosinus , sinus,	0,  rotationPoint.x - cosinus * rotationPoint.x   - sinus   * rotationPoint.y ,
						 -sinus , cosinus,	0,  rotationPoint.y + sinus   * rotationPoint.x   - cosinus * rotationPoint.y ,
						 0, 		  0, 	1,   0 ,
						 0, 		  0, 	0,   1)
		};
	}
	
	
	/**
	 * Sets the given matrices to be z-rotation and inverse rotation matrices, overwriting their previous values.
	 * 
	 * @param m the m
	 * @param mInv the m inv
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public static void toZRotationMatrixAndInverse(Matrix m, Matrix mInv, Vector3D rotationPoint, float degree) {
		float rotationAngle = degree * ToolsMath.DEG_TO_RAD;
		float sinus 	= ToolsMath.sin(rotationAngle);
		float cosinus 	= ToolsMath.cos(rotationAngle);
				try {
					m.set(new float[]{
							 cosinus ,-sinus,	0, 	rotationPoint.x - cosinus * rotationPoint.x + sinus   * rotationPoint.y ,
							 sinus , cosinus,	0, 	rotationPoint.y - sinus   * rotationPoint.x - cosinus * rotationPoint.y ,
							 0, 		  0, 	1, 	0,
							 0, 		  0, 	0,  1});
					
					mInv.set(new float[]{
							cosinus , sinus,	0,  rotationPoint.x - cosinus * rotationPoint.x   - sinus   * rotationPoint.y ,
							 -sinus , cosinus,	0,  rotationPoint.y + sinus   * rotationPoint.x   - cosinus * rotationPoint.y ,
							 0, 		  0, 	1,   0 ,
							 0, 		  0, 	0,   1} );
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	/**
	 * Modifies a rotation matrix to rotate about the specified point instead of the origin.
	 * @param m
	 * @param rotationPoint
	 */
	public static void toRotationAboutPoint(Matrix m, Vector3D rotationPoint) {
//		float rotationAngle = degree * FastMath.DEG_TO_RAD;
//		float sinus 	= FastMath.sin(rotationAngle);
//		float cosinus 	= FastMath.cos(rotationAngle);
				try {
					m.set(new float[]{
							 m.m00 ,m.m01,	m.m02, 	rotationPoint.x - m.m00 * rotationPoint.x - m.m01 * rotationPoint.y - m.m02	* rotationPoint.z ,
							 m.m10 ,m.m11,	m.m12, 	rotationPoint.y - m.m10 * rotationPoint.x - m.m11 * rotationPoint.y - m.m12 * rotationPoint.z,
							 m.m20, m.m21, 	m.m22, 	rotationPoint.z - m.m20 * rotationPoint.x - m.m21 * rotationPoint.y - m.m22 * rotationPoint.z,
							 m.m30, m.m31, 	m.m32,  m.m33});
//							 0, 0, 	0,  1});
					
//					mInv.set(new float[]{
//							cosinus , sinus,	0,  rotationPoint.x - cosinus * rotationPoint.x   - sinus   * rotationPoint.y ,
//							 -sinus , cosinus,	0,  rotationPoint.y + sinus   * rotationPoint.x   - cosinus * rotationPoint.y ,
//							 0, 		  0, 	1,   0 ,
//							 0, 		  0, 	0,   1} );
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	/**
	 * Gets the scaling matrix.
	 * 
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * 
	 * @return the scaling matrix
	 */
	public static Matrix getScalingMatrix(float X, float Y, float Z) {
		return new Matrix(
                X ,	0,	0, 0,
                0 , Y,	0, 0 ,
                0, 	0, 	Z, 0,
               	0, 	0, 	0, 1);
	}

	/**
	 * Gets the scaling matrix.
	 * 
	 * @param scalingPoint the scaling point
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * 
	 * @return the scaling matrix
	 */
	public static Matrix getScalingMatrix(Vector3D scalingPoint, float X, float Y, float Z) {
		return new Matrix(
		                X ,	0,	0, scalingPoint.x - (X * scalingPoint.x)  ,
		                0 , Y,	0, scalingPoint.y - (Y * scalingPoint.y),
		                0, 	0, 	Z, scalingPoint.z - (Z * scalingPoint.z),
		               	0, 	0, 	0, 1);
	}
	
	/**
	 * Gets the inv scaling matrix.
	 * 
	 * @param scalingPoint the scaling point
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * 
	 * @return the inv scaling matrix
	 */
	public static Matrix getInvScalingMatrix(Vector3D scalingPoint, float X, float Y, float Z) {
		float xs = 1f/X;
		float ys = 1f/Y;
		float zs = 1f/Z;
		return new Matrix(
				xs,0,	0, 		scalingPoint.x - (xs * scalingPoint.x),
			        0, 	ys,0, 	scalingPoint.y - (ys * scalingPoint.y),
			        0, 	0, 	zs,	scalingPoint.z - (zs * scalingPoint.z),
			        0, 	0, 	0, 	1);
	}
	
	/**
	 * Gets the scaling matrix and inverse.
	 * 
	 * @param scalingPoint the scaling point
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * 
	 * @return the scaling matrix and inverse
	 */
	public static Matrix[] getScalingMatrixAndInverse(Vector3D scalingPoint, float X, float Y, float Z) {
		float xs = 1f/X;
		float ys = 1f/Y;
		float zs = 1f/Z;
		return new Matrix[]{
				new Matrix(
		                X ,	0,	0, scalingPoint.x - (X * scalingPoint.x),
		                0 , Y,	0, scalingPoint.y - (Y * scalingPoint.y),
		                0, 	0, 	Z, scalingPoint.z - (Z * scalingPoint.z),
		               	0, 	0, 	0, 1),
		        new Matrix(
		        		xs,0,	0, 	scalingPoint.x - (xs * scalingPoint.x),
		        		0, 	ys,	0, 	scalingPoint.y - (ys * scalingPoint.y),
		        		0, 	0, 	zs, scalingPoint.z - (zs * scalingPoint.z),
		        		0, 	0, 	0, 	1)
		};
	}
	
	
	/**
	 * Sets the given matrices to be x-rotation and inverse rotation matrices, overwriting their previous values.
	 * 
	 * @param m the m
	 * @param mInv the m inv
	 * @param scalingPoint the scaling point
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 */
	public static void toScalingMatrixAndInverse(Matrix m, Matrix mInv, Vector3D scalingPoint, float X, float Y, float Z) {
		float xs = 1f/X;
		float ys = 1f/Y;
		float zs = 1f/Z;
				try {
					m.set(new float[]{
							X ,	0,	0, scalingPoint.x - (X * scalingPoint.x),
			                0 , Y,	0, scalingPoint.y - (Y * scalingPoint.y),
			                0, 	0, 	Z, scalingPoint.z - (Z * scalingPoint.z),
			               	0, 	0, 	0, 1});
					
					mInv.set(new float[]{
							xs,0,	0, 	scalingPoint.x - (xs * scalingPoint.x),
			        		0, 	ys,	0, 	scalingPoint.y - (ys * scalingPoint.y),
			        		0, 	0, 	zs, scalingPoint.z - (zs * scalingPoint.z),
			        		0, 	0, 	0, 	1} );
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
			
	/**
	 * This is experimental!.
	 * 
	 * @param m the m
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * 
	 * @return the non uniform scaling trial matrix
	 */
	public static Matrix getNonUniformScalingTrialMatrix(Matrix m,  float x, float y, float z){
		return new Matrix(
				 m.m00 * x,  m.m01 * y,  m.m02 * z,  m.m03,
				 m.m10 * x,  m.m11 * y,  m.m12 * z,  m.m13,
				 m.m20 * x,  m.m21 * y,  m.m22 * z,  m.m23,
				 m.m30 * x,  m.m31 * y,  m.m32 * z , m.m33);
	}
	
	

	/**
	 * Removes the translation from matrix.
	 */
	public void removeTranslationFromMatrix(){
		try {
			//Ignore translation part of matrix
			set(0, 3, 0);
			set(1, 3, 0);
			set(2, 3, 0);
			set(3, 3, 1);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the 4x4 identity.
	 * 
	 * @return the 4x4 identity
	 */
	public static Matrix get4x4Identity() {
		return new Matrix();
	}

	/**
	 * Gets the a copy.
	 * 
	 * @param matrixToCopy the matrix to copy
	 * 
	 * @return the a copy
	 */
	public static Matrix getACopy(Matrix matrixToCopy) {
		return new Matrix(matrixToCopy);
	}
/*
	Tip: You can convert a point in one object's coordinate space into 
	another object's space by applying get-matrix action to the first object, 
	transforming the point into world space using the matrix, applying 
	a get-matrix action to the other object, and then transforming the 
	world-space point by the inverse matrix of the second object. 
*/


	/**
	 * Checks if is any of the matrix values are NaN (not a number).
	 * 
	 * @return true, if is valid
	 */
	public boolean isValid() {
		return (!(
				Float.isNaN(this.m00)
				|| Float.isNaN(this.m01)
				|| Float.isNaN(this.m02)
				|| Float.isNaN(this.m03)
				|| Float.isNaN(this.m10)
				|| Float.isNaN(this.m11)
				|| Float.isNaN(this.m12)
				|| Float.isNaN(this.m13)
				|| Float.isNaN(this.m20)
				|| Float.isNaN(this.m21)
				|| Float.isNaN(this.m22)
				|| Float.isNaN(this.m23)
				|| Float.isNaN(this.m30)
				|| Float.isNaN(this.m31)
				|| Float.isNaN(this.m32)
				|| Float.isNaN(this.m33)
		));
	}
	
	/*
        	we use:
          	(m00)(m01)(m02)(m03);
	        (m10)(m11)(m12)(m13);
	        (m20)(m21)(m22)(m23);
	        (m30)(m31)(m32)(m33);
	        
	        ogl:
	        (m00)(m10)(m20)(m30);
	        (m01)(m11)(m21)(m31);
	        (m02)(m12)(m22)(m32);
	        (m03)(m13)(m23)(m33);
	 */
	
	
	/**
	 * Decomposes the matrix into its translation, rotation and scaling parts.
	 * The results are stored in the specified vectors.
	 * 
	 * @param trans the trans
	 * @param rot the rot
	 * @param scale the scale
	 */
	public void decompose( Vector3D trans, Vector3D rot, Vector3D scale ){
		// Getting translation is trivial
//		trans = Vector3D( c[3][0], c[3][1], c[3][2] );
		trans.setXYZ(m03, m13, m23);

		// Scale is length of columns
//		scale.x = sqrt( c[0][0] * c[0][0] + c[0][1] * c[0][1] + c[0][2] * c[0][2] );
//		scale.y = sqrt( c[1][0] * c[1][0] + c[1][1] * c[1][1] + c[1][2] * c[1][2] );
//		scale.z = sqrt( c[2][0] * c[2][0] + c[2][1] * c[2][1] + c[2][2] * c[2][2] );
		
		scale.x = ToolsMath.sqrt( m00 * m00 + m10 * m10 + m20 * m20 );
		scale.y = ToolsMath.sqrt( m01 * m01 + m11 * m11 + m21 * m21 );
		scale.z = ToolsMath.sqrt( m02 * m02 + m12 * m12 + m22 * m22 );

		if( scale.x == 0 || scale.y == 0 || scale.z == 0 ) return;

		// Detect negative scale with determinant and flip one arbitrary axis
		if( determinant() < 0 ) 
			scale.x = -scale.x;

		// Combined rotation matrix YXZ
		//
		// Cos[y]*Cos[z]+Sin[x]*Sin[y]*Sin[z]   Cos[z]*Sin[x]*Sin[y]-Cos[y]*Sin[z]  Cos[x]*Sin[y]	
		// Cos[x]*Sin[z]                        Cos[x]*Cos[z]                       -Sin[x]
		// -Cos[z]*Sin[y]+Cos[y]*Sin[x]*Sin[z]  Cos[y]*Cos[z]*Sin[x]+Sin[y]*Sin[z]  Cos[x]*Cos[y]

//		rot.x = asinf( -c[2][1] / scale.z );
		rot.x = ToolsMath.asin( -m12 / scale.z );
		
		// Special case: Cos[x] == 0 (when Sin[x] is +/-1)
//		float f = fabsf( c[2][1] / scale.z );
		float f = ToolsMath.abs(m12 / scale.z );
		
		if( f > 0.999f && f < 1.001f ){
			// Pin arbitrarily one of y or z to zero
			// Mathematical equivalent of gimbal lock
			rot.y = 0;
			
			// Now: Cos[x] = 0, Sin[x] = +/-1, Cos[y] = 1, Sin[y] = 0
			// => m[0][0] = Cos[z] and m[1][0] = Sin[z]
//			rot.z = atan2f( -c[1][0] / scale.y, c[0][0] / scale.x );
			rot.z = ToolsMath.atan2( -m01 / scale.y, m00 / scale.x );
		}
		// Standard case
		else
		{
//			rot.y = atan2f( c[2][0] / scale.z, c[2][2] / scale.z );
//			rot.z = atan2f( c[0][1] / scale.x, c[1][1] / scale.y );
			rot.y = ToolsMath.atan2( m02 / scale.z, m22 / scale.z );
			rot.z = ToolsMath.atan2( m10 / scale.x, m11 / scale.y );
		}
	}
	
	
	public Vector3D getScale(){
		Vector3D scale = new Vector3D();
		// Scale is length of columns
		scale.x = ToolsMath.sqrt( m00 * m00 + m10 * m10 + m20 * m20 );
		scale.y = ToolsMath.sqrt( m01 * m01 + m11 * m11 + m21 * m21 );
		scale.z = ToolsMath.sqrt( m02 * m02 + m12 * m12 + m22 * m22 );
		return scale;
	}


	
	/*
	 static void fastMult43( Matrix4f &dst, const Matrix4f &m1, const Matrix4f &m2 )
	{
		// Note: dst may not be the same as m1 or m2

		float *dstx = dst.x;
		const float *m1x = m1.x;
		const float *m2x = m2.x;
		
		dstx[0] = m1x[0] * m2x[0] + m1x[4] * m2x[1] + m1x[8] * m2x[2];
		dstx[1] = m1x[1] * m2x[0] + m1x[5] * m2x[1] + m1x[9] * m2x[2];
		dstx[2] = m1x[2] * m2x[0] + m1x[6] * m2x[1] + m1x[10] * m2x[2];
		dstx[3] = 0.0f;

		dstx[4] = m1x[0] * m2x[4] + m1x[4] * m2x[5] + m1x[8] * m2x[6];
		dstx[5] = m1x[1] * m2x[4] + m1x[5] * m2x[5] + m1x[9] * m2x[6];
		dstx[6] = m1x[2] * m2x[4] + m1x[6] * m2x[5] + m1x[10] * m2x[6];
		dstx[7] = 0.0f;

		dstx[8] = m1x[0] * m2x[8] + m1x[4] * m2x[9] + m1x[8] * m2x[10];
		dstx[9] = m1x[1] * m2x[8] + m1x[5] * m2x[9] + m1x[9] * m2x[10];
		dstx[10] = m1x[2] * m2x[8] + m1x[6] * m2x[9] + m1x[10] * m2x[10];
		dstx[11] = 0.0f;

		dstx[12] = m1x[0] * m2x[12] + m1x[4] * m2x[13] + m1x[8] * m2x[14] + m1x[12] * m2x[15];
		dstx[13] = m1x[1] * m2x[12] + m1x[5] * m2x[13] + m1x[9] * m2x[14] + m1x[13] * m2x[15];
		dstx[14] = m1x[2] * m2x[12] + m1x[6] * m2x[13] + m1x[10] * m2x[14] + m1x[14] * m2x[15];
		dstx[15] = 1.0f;
	}

	 */

	public Matrix zRotateMultLocal(Matrix in2) {
		return zRotateMult(in2, this);
	}

	public Matrix zRotateMult(Matrix in2) {
		return zRotateMult(in2, null);
	}

	public Matrix zRotateMult(Matrix in2, Matrix store) {
		if (store == null) store = new Matrix();

		 	float temp00, temp01, temp02, temp03;
	        float temp10, temp11, temp12, temp13;
	        
	        float temp20, temp21, temp22, temp23;
	        float temp30, temp31, temp32, temp33;

	        temp00 = m00 * in2.m00 + 
	                m01 * in2.m10 + 
	                m02 * in2.m20 + 
	                m03 * in2.m30;
	        temp01 = m00 * in2.m01 + 
	                m01 * in2.m11 + 
	                m02 * in2.m21 +
	                m03 * in2.m31;
	        temp02 = m00 * in2.m02 + 
	                m01 * in2.m12 + 
	                m02 * in2.m22 +
	                m03 * in2.m32;
	        temp03 = m00 * in2.m03 + 
	                m01 * in2.m13 + 
	                m02 * in2.m23 + 
	                m03 * in2.m33;
	        
	        temp10 = m10 * in2.m00 + 
	                m11 * in2.m10 + 
	                m12 * in2.m20 +
	                m13 * in2.m30;
	        temp11 = m10 * in2.m01 +
	                m11 * in2.m11 +
	                m12 * in2.m21 +
	                m13 * in2.m31;
	        temp12 = m10 * in2.m02 +
	                m11 * in2.m12 + 
	                m12 * in2.m22 +
	                m13 * in2.m32;
	        temp13 = m10 * in2.m03 +
	                m11 * in2.m13 +
	                m12 * in2.m23 + 
	                m13 * in2.m33;

	        temp20 = in2.m20;
	        temp21 = in2.m21;
	        temp22 = in2.m22;
	        temp23 = in2.m23;
	        temp30 = in2.m30;
	        temp31 = in2.m31;
	        temp32 = in2.m32;
	        temp33 = in2.m33;
	        
	        store.m00 = temp00;  store.m01 = temp01;  store.m02 = temp02;  store.m03 = temp03;
	        store.m10 = temp10;  store.m11 = temp11;  store.m12 = temp12;  store.m13 = temp13;
	        store.m20 = temp20;  store.m21 = temp21;  store.m22 = temp22;  store.m23 = temp23;
	        store.m30 = temp30;  store.m31 = temp31;  store.m32 = temp32;  store.m33 = temp33;
		return store;
	}
	
	
	public Matrix scaleMultLocal(Matrix in2) {
		return fastMult43(in2, this);
	}
	
	public Matrix scaleMult(Matrix in2) {
		return fastMult43(in2, null);
	}

	public Matrix scaleMult(Matrix in2, Matrix store) {
		return fastMult43(in2, store);
	}
	
	
	public Matrix translateMultLocal(Matrix in2) {
		return fastMult43(in2, this);
	}
	
	public Matrix translateMult(Matrix in2) {
		return fastMult43(in2, null);
	}

	public Matrix translateMult(Matrix in2, Matrix store) {
		return fastMult43(in2, store);
	}
	
	public Matrix fastMult43(Matrix in2, Matrix store) {
		 if (store == null) store = new Matrix();

	        float temp00, temp01, temp02, temp03;
	        float temp10, temp11, temp12, temp13;
	        float temp20, temp21, temp22, temp23;
	        float temp30, temp31, temp32, temp33;

	        temp00 = m00 * in2.m00 + 
	                m01 * in2.m10 + 
	                m02 * in2.m20 + 
	                m03 * in2.m30;
	        temp01 = m00 * in2.m01 + 
	                m01 * in2.m11 + 
	                m02 * in2.m21 +
	                m03 * in2.m31;
	        temp02 = m00 * in2.m02 + 
	                m01 * in2.m12 + 
	                m02 * in2.m22 +
	                m03 * in2.m32;
	        temp03 = m00 * in2.m03 + 
	                m01 * in2.m13 + 
	                m02 * in2.m23 + 
	                m03 * in2.m33;
	        
	        temp10 = m10 * in2.m00 + 
	                m11 * in2.m10 + 
	                m12 * in2.m20 +
	                m13 * in2.m30;
	        temp11 = m10 * in2.m01 +
	                m11 * in2.m11 +
	                m12 * in2.m21 +
	                m13 * in2.m31;
	        temp12 = m10 * in2.m02 +
	                m11 * in2.m12 + 
	                m12 * in2.m22 +
	                m13 * in2.m32;
	        temp13 = m10 * in2.m03 +
	                m11 * in2.m13 +
	                m12 * in2.m23 + 
	                m13 * in2.m33;

	        temp20 = m20 * in2.m00 + 
	                m21 * in2.m10 + 
	                m22 * in2.m20 +
	                m23 * in2.m30;
	        temp21 = m20 * in2.m01 + 
	                m21 * in2.m11 + 
	                m22 * in2.m21 +
	                m23 * in2.m31;
	        temp22 = m20 * in2.m02 + 
	                m21 * in2.m12 + 
	                m22 * in2.m22 +
	                m23 * in2.m32;
	        temp23 = m20 * in2.m03 + 
	                m21 * in2.m13 + 
	                m22 * in2.m23 +
	                m23 * in2.m33;

	        temp30 = in2.m30;
	        temp31 = in2.m31;
	        temp32 = in2.m32;
	        temp33 = in2.m33;
	        
	        store.m00 = temp00;  store.m01 = temp01;  store.m02 = temp02;  store.m03 = temp03;
	        store.m10 = temp10;  store.m11 = temp11;  store.m12 = temp12;  store.m13 = temp13;
	        store.m20 = temp20;  store.m21 = temp21;  store.m22 = temp22;  store.m23 = temp23;
	        store.m30 = temp30;  store.m31 = temp31;  store.m32 = temp32;  store.m33 = temp33;
	        return store;
	}

	
	/*//FIXME implement orthogonalization!
	        Matrix r1 = Matrix.getZRotationMatrix(new Vector3D(0,0,0), 25.5f);
        Matrix s1 = Matrix.getScalingMatrix(new Vector3D(0,0,0), 1.2f,1.2f,1);
        
        Matrix i1 = new Matrix();
        System.out.println("Determinant identity: " + i1.determinant());
        
        for (int i = 0; i < 100; i++) {
        	s1.mult(i1, i1);
        	r1.mult(i1, i1);
        	
		}
        
        System.out.println("Determinant after many transforms: " + i1.determinant());
        System.out.println(i1);
        
        Vector3D xAxis, yAxis, zAxis;
        xAxis = new Vector3D(i1.m00, 
			        		 i1.m10, 
			        		 i1.m20);
        yAxis = new Vector3D(i1.m01, 
        					 i1.m11, 
        					 i1.m21);
        zAxis = new Vector3D(i1.m02, 
        					 i1.m12, 
        					 i1.m22);
        
        xAxis.normalizeLocal();
        
        yAxis = yAxis.getSubtracted(xAxis.getScaled(xAxis.dot(yAxis)));
        yAxis.normalizeLocal();
        
        zAxis = xAxis.getCross(yAxis);
        
        Matrix n1 = new Matrix(	xAxis.x, yAxis.x, zAxis.x, 0,
        						xAxis.y, yAxis.y, zAxis.y, 0,
        						xAxis.z, yAxis.z, zAxis.z, 0,        
        						0, 			0, 			0, 1
        );
        System.out.println("after ortho: " + n1 +  " \n Det: " + n1.determinant());
	 */
	
	
	/**
	 * Orthonormalizes the 3x3 upper left part of this matrix.
	 */
	public void orthonormalizeUpperLeft(){
		// Algorithm uses Gram-Schmidt orthogonalization. If 'this' matrix is
		// M = [m0|m1|m2], then orthonormal output matrix is Q = [q0|q1|q2],
		//
		// q0 = m0/|m0|
		// q1 = (m1-(q0*m1)q0)/|m1-(q0*m1)q0|
		// q2 = (m2-(q0*m2)q0-(q1*m2)q1)/|m2-(q0*m2)q0-(q1*m2)q1|
		//
		// where |V| indicates length of vector V and A*B indicates dot
		// product of vectors A and B.

		// compute q0
		float fInvLength = ToolsMath.invSqrt(
		m00*m00
		+ m10*m10 +
		m20*m20);
		
		if (Float.isInfinite(fInvLength)){ //added
			fInvLength = 0.0f;
		}

		m00 *= fInvLength;
		m10 *= fInvLength;
		m20 *= fInvLength;

		// compute q1
		float fDot0 =
		m00*m01 +
		m10*m11 +
		m20*m21;

		m01 -= fDot0*m00;
		m11 -= fDot0*m10;
		m21 -= fDot0*m20;

		fInvLength = ToolsMath.invSqrt(
		m01*m01 +
		m11*m11 +
		m21*m21);
		
		if (Float.isInfinite(fInvLength)){ //added
			fInvLength = 0.0f;
		}

		m01 *= fInvLength;
		m11 *= fInvLength;
		m21 *= fInvLength;

		// compute q2
		float fDot1 =
		m01*m02 +
		m11*m12 +
		m21*m22;

		fDot0 =
		m00*m02 +
		m10*m12 +
		m20*m22;

		m02 -= fDot0*m00 + fDot1*m01;
		m12 -= fDot0*m10 + fDot1*m11;
		m22 -= fDot0*m20 + fDot1*m21;

		fInvLength = ToolsMath.invSqrt(
		m02*m02 +
		m12*m12 +
		m22*m22);
		
		if (Float.isInfinite(fInvLength)){ //added
			fInvLength = 0.0f;
		}

		m02 *= fInvLength;
		m12 *= fInvLength;
		m22 *= fInvLength;
	}

	
	
	public Matrix orthonormalizeLocal(){
		float tx = m03;
        float ty = m13;
        float tz = m23;
        float tt = m33;
        this.orthonormalizeColumns();
        m03 = tx;
        m13 = ty;
        m23 = tz;
        m33 = tt;
        return this;
	}
	
	
		/**
		 Orthonormalizes the column vectors of this matrix using Gram Schmidt 
		 orthonormalization. Note that for this to work the column vectors 
		 of this matrix must be linearly independent, i.e. the matrix must 
		 have a determinant not equal to 0.
	
		\verbatim
		Given a set of n linearly independent vectors a_i we're looking for a
		set of n vectors b_i so that these n vectors are orthonormalized, i.e. 
		they are all orthogonal to each other and have unit-length, and span the
		same space as the original n vectors.
		Gram Schmidt orthonormalization is an algorithm that does exactly
		this in an inductive manner. The algorithm is as follows:
			
			b_1 = normalize(a_1)
			for j = 2 .. n:
				b_j = a_j - sum of all i = 1 to j-1: dot(a_j, b_i) * b_i
				normalize(b_j)
	
		For orthonormalizing the columns of a 4x4 matrix
			[ a00 a01 a02 a03 ]
			[ a10 a11 a12 a13 ]
			[ a20 a21 a22 a23 ]
			[ a30 a31 a32 a33 ]
		this simplifies to
			[ b00 b10 b20 b30 ] = normalize([ a00 a10 a20 a30 ])
			[ b01 b11 b21 b31 ] = normalize([ a01 a11 a21 a31 ]
			                    - dot([ a01 a11 a21 a31 ], [ b00 b10 b20 b30 ]) * [ b00 b10 b20 b30 ])
			[ b02 b12 b22 b32 ] = normalize([ a02 a12 a22 a32 ]
			                    - dot([ a02 a12 a22 a32 ], [ b00 b10 b20 b30 ]) * [ b00 b10 b20 b30 ]
			                    - dot([ a02 a12 a22 a32 ], [ b01 b11 b21 b31 ]) * [ b01 b11 b21 b31 ])
			[ b03 b13 b23 b33 ] = normalize([ a03 a13 a23 a33 ]
			                    - dot([ a03 a13 a23 a33 ], [ b00 b10 b20 b30 ]) * [ b00 b10 b20 b30 ]
			                    - dot([ a03 a13 a23 a33 ], [ b01 b11 b21 b31 ]) * [ b01 b11 b21 b31 ]
			                    - dot([ a03 a13 a23 a33 ], [ b02 b12 b22 b32 ]) * [ b02 b12 b22 b32 ])
		\endverbatim
	**/
	private Matrix orthonormalizeColumns(){
//		assert(Math.abs(determinant()) >= ToolsMath.FLT_EPSILON);		// make sure the rows/columns are linearly independent
		
		// compute the length of the first column and set it
		float length = ToolsMath.invSqrt(m00 * m00 + m10 * m10 + m20 * m20 + m30 * m30);
		m00 *= length;
		m10 *= length;
		m20 *= length;
		m30 *= length;

		// compute the second column
		float dot = m01 * m00 + m11 * m10 + m21 * m20 + m31 * m30;
		m01 -= dot * m00;
		m11 -= dot * m10;
		m21 -= dot * m20;
		m31 -= dot * m30;
		length = ToolsMath.invSqrt(m01 * m01 + m11 * m11 + m21 * m21 + m31 * m31);
		m01 *= length;
		m11 *= length;
		m21 *= length;
		m31 *= length;

		// compute the third column
		dot = m02 * m00 + m12 * m10 + m22 * m20 + m32 * m30;
		float dot2 = m02 * m01 + m12 * m11 + m22 * m21 + m32 * m31;
		m02 -= dot * m00 + dot2 * m01;
		m12 -= dot * m10 + dot2 * m11;
		m22 -= dot * m20 + dot2 * m21;
		m32 -= dot * m30 + dot2 * m31;
		length = ToolsMath.invSqrt(m02 * m02 + m12 * m12 + m22 * m22 + m32 * m32);
		m02 *= length;
		m12 *= length;
		m22 *= length;
		m32 *= length;

		// compute the fourth column
		dot = m03 * m00 + m13 * m10 + m23 * m20 + m33 * m30;
		dot2 = m03 * m01 + m13 * m11 + m23 * m21 + m33 * m31;
		float dot3 = m03 * m02 + m13 * m12 + m23 * m22 + m33 * m32;
		m03 -= dot * m00 + dot2 * m01 + dot3 * m02;
		m13 -= dot * m10 + dot2 * m11 + dot3 * m12;
		m23 -= dot * m20 + dot2 * m21 + dot3 * m22;
		m33 -= dot * m30 + dot2 * m31 + dot3 * m32;
		length = ToolsMath.invSqrt(m03 * m03 + m13 * m13 + m23 * m23 + m33 * m33);
		m03 *= length;
		m13 *= length;
		m23 *= length;
		m33 *= length;

		//multiplications: 80
		return this;
	}
	

	
/////////////////////////////////////////////////////////////////////////////
	/**
	 * Orthonormalizes the row vectors of this matrix using Gram Schmidt 
		 orthonormalization. Note that for this to work the row vectors 
		 of this matrix must be linearly independent, i.e. the matrix must 
		 have a determinant not equal to 0.

		\verbatim
		Given a set of n linearly independent vectors a_i we're looking for a
		set of n vectors b_i so that these n vectors are orthonormalized, i.e. 
		they are all orthogonal to each other and have unit-length, and span the
		same space as the original n vectors.
		Gram Schmidt orthonormalization is an algorithm that does exactly
		this in an inductive manner. The algorithm is as follows:
			
			b_1 = normalize(a_1)
			for j = 2 .. n:
				b_j = a_j - sum of all i = 1 to j-1: dot(a_j, b_i) * b_i
				normalize(b_j)

		For orthonormalizing the rows of a 4x4 matrix
			[ a00 a01 a02 a03 ]
			[ a10 a11 a12 a13 ]
			[ a20 a21 a22 a23 ]
			[ a30 a31 a32 a33 ]
		this simplifies to
			[ b00 b01 b02 b03 ] = normalize([ a00 a01 a02 a03 ])
			[ b10 b11 b12 b13 ] = normalize([ a10 a11 a12 a13 ]
			                    - dot([ a10 a11 a12 a13 ], [ b00 b01 b02 b03 ]) * [ b00 b01 b02 b03 ])
			[ b20 b21 b22 b23 ] = normalize([ a20 a21 a22 a23 ]
			                    - dot([ a20 a21 a22 a23 ], [ b00 b01 b02 b03 ]) * [ b00 b01 b02 b03 ]
			                    - dot([ a20 a21 a22 a23 ], [ b10 b11 b12 b13 ]) * [ b10 b11 b12 b13 ])
			[ b30 b31 b32 b33 ] = normalize([ a30 a31 a32 a33 ]
			                    - dot([ a30 a31 a32 a33 ], [ b00 b01 b02 b03 ]) * [ b00 b01 b02 b03 ]
			                    - dot([ a30 a31 a32 a33 ], [ b10 b11 b12 b13 ]) * [ b10 b11 b12 b13 ]
			                    - dot([ a30 a31 a32 a33 ], [ b20 b21 b22 b23 ]) * [ b20 b21 b22 b23 ])
		\endverbatim
	**/

	private Matrix orthonormalizeRows(){
//		wxASSERT(Math::fabs(GetDeterminant()) >= Math::g_epsilon);		// make sure the rows/columns are linearly independent

		// compute the length of the first row and set it
		float length = ToolsMath.invSqrt(m00 * m00 + m01 * m01 + m02 * m02 + m03 * m03);
		m00 *= length;
		m01 *= length;
		m02 *= length;
		m03 *= length;

		// compute the second row
		float dot = m10 * m00 + m11 * m01 + m12 * m02 + m13 * m03;
		m10 -= dot * m00;
		m11 -= dot * m01;
		m12 -= dot * m02;
		m13 -= dot * m03;
		length = ToolsMath.invSqrt(m10 * m10 + m11 * m11 + m12 * m12 + m13 * m13);
		m10 *= length;
		m11 *= length;
		m12 *= length;
		m13 *= length;

		// compute the third row
		dot = m20 * m00 + m21 * m01 + m22 * m02 + m23 * m03;
		float dot2 = m20 * m10 + m21 * m11 + m22 * m12 + m23 * m13;
		m20 -= dot * m00 + dot2 * m10;
		m21 -= dot * m01 + dot2 * m11;
		m22 -= dot * m02 + dot2 * m12;
		m23 -= dot * m03 + dot2 * m13;
		length = ToolsMath.invSqrt(m20 * m20 + m21 * m21 + m22 * m22 + m23 * m23);
		m20 *= length;
		m21 *= length;
		m22 *= length;
		m23 *= length;

		// compute the fourth row
		dot = m30 * m00 + m31 * m01 + m32 * m02 + m33 * m03;
		dot2 = m30 * m10 + m31 * m11 + m32 * m12 + m33 * m13;
		float dot3 = m30 * m20 + m31 * m21 + m32 * m22 + m33 * m23;
		m30 -= dot * m00 + dot2 * m10 + dot3 * m20;
		m31 -= dot * m01 + dot2 * m11 + dot3 * m21;
		m32 -= dot * m02 + dot2 * m12 + dot3 * m22;
		m33 -= dot * m03 + dot2 * m13 + dot3 * m23;
		length = ToolsMath.invSqrt(m30 * m30 + m31 * m31 + m32 * m32 + m33 * m33);
		m30 *= length;
		m31 *= length;
		m32 *= length;
		m33 *= length;

		return this;
	}


}

