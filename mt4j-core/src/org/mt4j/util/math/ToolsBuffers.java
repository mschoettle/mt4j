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

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Methods to build Buffers for use with vertex/texture/color arrays in opengl
 * Some methods are adapted from the JME.
 * 
 * @author C.Ruff
 */
public class ToolsBuffers {
	public static final int SIZEOF_BYTE = 1;
	public static final int SIZEOF_SHORT = 2;
	public static final int SIZEOF_INT = 4;
	public static final int SIZEOF_FLOAT = 4;
	public static final int SIZEOF_LONG = 8;
	public static final int SIZEOF_DOUBLE = 8;
	
	////////////////////////////////////////////////////////////
	// Methods to build Buffers for use with vertex/texture/color arrays in opengl 
	//////////////////////////////////////////////////////
	/**
	 * Generate vertex buffer.
	 * 
	 * @param vertices the vertices
	 * 
	 * @return the float buffer
	 */
	public static FloatBuffer generateVertexBuffer(Vector3D[] vertices){
		float[] xyz;
		int vertixCount = vertices.length;
		xyz = new float[vertixCount*3];
	   for (int i = 0; i < vertixCount; i++) {
			xyz[i*3]  = vertices[i].getX(); 
			xyz[i*3+1]=	vertices[i].getY();
			xyz[i*3+2]=	vertices[i].getZ();
	   }
	  FloatBuffer vertBuff = createFloatBuffer(xyz.length);
	  vertBuff.put(xyz);
	  vertBuff.rewind();
	  return vertBuff;
	}
	
    
    /**
     * Generate color buffer.
     * 
     * @param vertices the vertices
     * 
     * @return the float buffer
     */
	public static FloatBuffer generateColorBuffer(Vertex[] vertices){
		float[] rgb;
		int vertexCount = vertices.length;
		rgb = new float[vertexCount*4];
	    for (int i = 0; i < vertexCount; i++) {
	    	rgb[i*4]=	vertices[i].getR()/255f;
		    rgb[i*4+1]=	vertices[i].getG()/255f;
		    rgb[i*4+2]=	vertices[i].getB()/255f;
		    rgb[i*4+3]=	vertices[i].getA()/255f;
		}
	  FloatBuffer colorBuff = createFloatBuffer(rgb.length);
	  colorBuff.put(rgb);
	  colorBuff.rewind();
	  return colorBuff;
	}
	
	
	/**
	 * Update color buffer.
	 * 
	 * @param v the v
	 * @param buf the buf
	 */
	public static void updateColorBuffer(Vertex[] v, FloatBuffer buf){
		buf.rewind();
		for (int i = 0; i < v.length; i++) {
			buf.put(i * 4      , v[i].getR()/255f);
	    	buf.put((i * 4) + 1, v[i].getG()/255f);
	    	buf.put((i * 4) + 2, v[i].getB()/255f);
	    	buf.put((i * 4) + 3, v[i].getA()/255f);
		}
		buf.clear();
	}
	
	/**
	 * Generate stroke color buffer.
	 * 
	 * @param vertexCount the vertex count
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 * 
	 * @return the float buffer
	 */
	public static FloatBuffer generateStrokeColorBuffer(int vertexCount, float r, float g, float b, float a){
		float[] rgb;
		rgb = new float[vertexCount*4];
	    for (int i = 0; i < vertexCount; i++) {
			rgb[i*4]=	r/255;
		    rgb[i*4+1]=	g/255;
		    rgb[i*4+2]=	b/255;
		    rgb[i*4+3]=	a/255;
		}
	  FloatBuffer strokeColBuff = createFloatBuffer(rgb.length);
	  strokeColBuff.put(rgb);
	  strokeColBuff.rewind();
	  return strokeColBuff;
	}
	
	/**
	 * Update stroke color buffer.
	 * 
	 * @param buf the buf
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @param a the a
	 */
	public static void updateStrokeColorBuffer(FloatBuffer buf, float r, float g, float b, float a){
		float rr = r/255f;
		float gg = g/255f;
		float bb = b/255f;
		float aa = a/255f;
		buf.rewind();
		while(buf.hasRemaining()){
			buf.put(rr);
			buf.put(gg);
			buf.put(bb);
			buf.put(aa);
		}
		buf.clear();
	}
	
	/**
	 * Generate texture buffer.
	 * 
	 * @param vertices the vertices
	 * 
	 * @return the float buffer
	 */
	public static FloatBuffer generateTextureBuffer(Vertex[] vertices){
		float[] textureCoords;
		int vertixCount = vertices.length;
		textureCoords = new float[vertixCount*2];
	    for (int i = 0; i < vertixCount; i++) {
	    	Vertex vertex = vertices[i];
	    	textureCoords[i*2]=		vertex.getTexCoordU();
	    	textureCoords[i*2+1]=	vertex.getTexCoordV();
		}
	    FloatBuffer tBuffer = createFloatBuffer(textureCoords.length);
	    tBuffer.put(textureCoords);
	    tBuffer.rewind();
	    return tBuffer;
	}
	
	/**
	 * Generate normals buffer.
	 * 
	 * @param normals the normals
	 * 
	 * @return the float buffer
	 */
	public static FloatBuffer generateNormalsBuffer(Vector3D[] normals){
		return generateVertexBuffer(normals);
		/*
		float[] xyz;
		int normalsCount = normals.length;
		xyz = new float[normalsCount*3];
	   for (int i = 0; i < normalsCount; i++) {
			xyz[i*3]  = normals[i].getX(); 
			xyz[i*3+1]=	normals[i].getY();
			xyz[i*3+2]=	normals[i].getZ();
	   }
	  FloatBuffer normalsBuff = createFloatBuffer(xyz.length);
	  normalsBuff.put(xyz);
	  normalsBuff.rewind(); 
	  return normalsBuff;
	  */
	}
	
	
    /**
     * Generate indices buffer.
     * 
     * @param indicesArray the indices array
     * 
     * @return the int buffer
     */
	public static IntBuffer generateIndicesBuffer(int[] indicesArray){ 
		  IntBuffer indexBuff = createIntBuffer(indicesArray.length);
		  indexBuff.put(indicesArray);
		  indexBuff.rewind();
		  return indexBuff;	
	}
	
	/**
     * Generate indices buffer.
     * 
     * @param indicesArray the indices array
     * 
     * @return the int buffer
     */
	public static ShortBuffer generateIndicesBuffer(short[] indicesArray){ 
		  ShortBuffer indexBuff = createShortBuffer(indicesArray.length);
		  indexBuff.put(indicesArray);
		  indexBuff.rewind();
		  return indexBuff;	
	}
	
	
	
	/**
	 * Sets the data contained in the given Vector3D into the FloatBuffer at the
	 * specified index. Should work with vector and vertex.
	 * 
	 * @param vector the data to insert
	 * @param buf the buffer to insert into
	 * @param index the postion to place the data; in terms of vectors not floats
	 */
    public static void setInBuffer(Vector3D vector, FloatBuffer buf, int index) {
        if (buf == null) {
                return;
        }
        if (vector == null) {
            buf.put(index * 3, 0);
            buf.put((index * 3) + 1, 0);
            buf.put((index * 3) + 2, 0);
        } else {
                buf.put(index * 3, vector.x);
                buf.put((index * 3) + 1, vector.y);
                buf.put((index * 3) + 2, vector.z);
        }
    }

    public static void setInBuffer(float x, float y, float z, FloatBuffer buf, int index) {
    	if (buf == null) {
    		return;
    	}
    	buf.put(index * 3, x);
    	buf.put((index * 3) + 1, y);
    	buf.put((index * 3) + 2, z);
    }
    
	/**
	 * Sets the data contained in the given Vector into the FloatBuffer at the
	 * specified index. Should work with vector and vertex.
	 * 
	 * @param vector the data to insert
	 * @param buf the buffer to insert into
	 * @param index the postion to place the data; in terms of vectors not floats
	 */
    public static void setInBuffer(float[] vector, FloatBuffer buf, int index) {
    	if (buf == null) {
    		return;
    	}
    	buf.put(index * 3, vector[0]);
    	buf.put((index * 3) + 1, vector[1]);
    	buf.put((index * 3) + 2, vector[2]);
    }

    
    /**
     * Generates a Vector3D array from the given FloatBuffer.
     * 
     * @param buff the FloatBuffer to read from
     * 
     * @return a newly generated array of Vector3D objects
     */
    public static Vector3D[] getVector3DArray(FloatBuffer buff) {
        buff.clear(); //this doesent delete its contents!
        Vector3D[] verts = new Vector3D[buff.limit() / 3];
        for (int x = 0; x < verts.length; x++) {
            Vector3D v = new Vector3D(buff.get(), buff.get(), buff.get());
            verts[x] = v;
        }
        buff.clear(); //Reset position to 0 again
        return verts;
    }
    
    /**
     * Generates a Vertex array from the given FloatBuffers.
     * 
     * @param buff the FloatBuffer to read from
     * 
     * @return a newly generated array of Vertex objects
     */
    public static Vertex[] getVertexArray(FloatBuffer buff, FloatBuffer colBuff) {
        buff.clear(); //this doesent delete its contents!
        colBuff.clear();
//        if (colBuff.limit() != buff.limit() + buff.limit()* 1f/3f){ //Check
//        	System.err.println("Warning: Vertex Buffer limit doesent match with colorbuffer!");
//        }
        
        Vertex[] verts = new Vertex[buff.limit() / 3];
        for (int x = 0; x < verts.length; x++) {
        	Vertex v = new Vertex(buff.get(), buff.get(), buff.get(), colBuff.get(), colBuff.get(), colBuff.get(), colBuff.get());
            verts[x] = v;
        }
        buff.clear(); //Reset position to 0 again
        colBuff.clear();
        return verts;
    }
    
    /**
     * Generates a Vector3D array from the given FloatBuffer.
     * 
     * @param buff the FloatBuffer to read from
     * 
     * @return a newly generated array of Vector3D objects
     */
    public static float[][] getRGBAColorArray(FloatBuffer buff) {
    	buff.rewind(); //this doesent delete its contents!
        float[][] colorArray = new float[buff.limit() / 4][4];
        for (int x = 0; x < colorArray.length; x++) {
        	colorArray[x][0] 		= buff.get();
        	colorArray[x][1] 	= buff.get();
        	colorArray[x][2] 	= buff.get();
        	colorArray[x][3] 	= buff.get();
        }
        buff.clear(); //Reset position to 0 again
        return colorArray;
    }
    
    
    /**
     * Generates a Vector3f array from the given FloatBuffer.
     * 
     * @param buff the FloatBuffer to read from
     * 
     * @return a newly generated array of Vector3D objects
     */
	public static Vertex[] getVertexArray(FloatBuffer buff) {
	    buff.clear(); //Dosent delete its contents!
	    Vertex[] verts = new Vertex[buff.limit() / 3];
	    for (int x = 0; x < verts.length; x++) {
	    	Vertex v = new Vertex(buff.get(), buff.get(), buff.get());
	        verts[x] = v;
	    }
	    buff.clear(); //Reset position to 0 again
	    return verts;
	}

	/**
	 * Create a new int[] array and populates
	 * it with the given IntBuffer's contents.
	 * 
	 * @param buff the IntBuffer to read from
	 * 
	 * @return a new int array populated from the IntBuffer
	 */
    public static int[] getIntArray(IntBuffer buff) {
        if (buff == null) return null;
        buff.clear();
        int[] inds = new int[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        buff.clear(); //Reset position to 0 again
        return inds;
    }
    
    /**
	 * Create a new short[] array and populates
	 * it with the given ShortBuffer contents.
	 * 
	 * @param buff the ShortBuffer to read from
	 * 
	 * @return a new int array populated from the ShortBuffer
	 */
    public static short[] getShortArray(ShortBuffer buff) {
        if (buff == null) return null;
        buff.clear();
        short[] inds = new short[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        buff.clear(); //Reset position to 0 again
        return inds;
    }
    
    
    /**
     * Create a new float[] array and populate it with the given FloatBuffer's
     * contents.
     * 
     * @param buff the FloatBuffer to read from
     * 
     * @return a new float array populated from the FloatBuffer
     */
	public static float[] getFloatArray(FloatBuffer buff) {
	    if (buff == null) return null;
	    buff.clear();
	    float[] inds = new float[buff.limit()];
	    for (int x = 0; x < inds.length; x++) {
	        inds[x] = buff.get();
	    }
	    buff.clear(); //Reset position to 0 again
	    return inds;
	}


	/**
	 * Creates a new FloatBuffer with the same contents as the given
	 * FloatBuffer. The new FloatBuffer is seperate from the old one and changes
	 * are not reflected across. If you want to reflect changes, consider using
	 * Buffer.duplicate().
	 * 
	 * @param buf the FloatBuffer to copy
	 * 
	 * @return the copy
	 */
	public static FloatBuffer clone(FloatBuffer buf) {
	    if (buf == null) return null;
	    buf.rewind();
	
	    FloatBuffer copy = createFloatBuffer(buf.limit());
	    copy.put(buf);
	    return copy;
	}


	/**
	 * Creates a new IntBuffer with the same contents as the given IntBuffer.
	 * The new IntBuffer is seperate from the old one and changes are not
	 * reflected across. If you want to reflect changes, consider using
	 * Buffer.duplicate().
	 * 
	 * @param buf the IntBuffer to copy
	 * 
	 * @return the copy
	 */
    public static IntBuffer clone(IntBuffer buf) {
        if (buf == null) return null;
        buf.rewind();

        IntBuffer copy = createIntBuffer(buf.limit());
        copy.put(buf);
        return copy;
    }

    
    
    /**
     * Ensures there is at least the <code>required</code> number of entries left after the current position of the
     * buffer. If the buffer is too small a larger one is created and the old one copied to the new buffer.
     * 
     * @param buffer buffer that should be checked/copied (may be null)
     * @param required minimum number of elements that should be remaining in the returned buffer
     * 
     * @return a buffer large enough to receive at least the <code>required</code> number of entries, same position as
     * the input buffer, not null
     */
	public static FloatBuffer ensureLargeEnough( FloatBuffer buffer, int required ) {
	    if ( buffer == null || ( buffer.remaining() < required ) ) {
	        int position = ( buffer != null ? buffer.position() : 0 );
	        FloatBuffer newVerts = createFloatBuffer( position + required );
	        if ( buffer != null ) {
	            buffer.rewind();
	            newVerts.put( buffer );
	            newVerts.position( position );
	        }
	        buffer = newVerts;
	    }
	    return buffer;
	}


	/**
	 * Create a new ByteBuffer of an appropriate size to hold the specified
	 * number of ints only if the given buffer if not already the right size.
	 * 
	 * @param buf the buffer to first check and rewind
	 * @param size number of bytes that need to be held by the newly created
	 * buffer
	 * 
	 * @return the requested new IntBuffer
	 */
	public static ByteBuffer createByteBuffer(ByteBuffer buf, int size) {
	    if (buf != null && buf.limit() == size) {
	        buf.rewind();
	        return buf;
	    }
	    buf = createByteBuffer(size);
	    return buf;
	}


	/**
	 * Create a new empty FloatBuffer of the specified size.
	 * 
	 * @param size required number of floats to store.
	 * 
	 * @return the new FloatBuffer
	 */
	public  static FloatBuffer createFloatBuffer(int size) {
	    FloatBuffer buf = ByteBuffer.allocateDirect(SIZEOF_FLOAT * size).order(ByteOrder.nativeOrder()).asFloatBuffer();
	    buf.clear();
	    return buf;
	}
	
	/**
     * Create a new FloatBuffer of an appropriate size to hold the specified
     * number of Vector3D object data.
     *
     * @param vertices
     *            number of vertices that need to be held by the newly created
     *            buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector3Buffer(int vertices) {
        return createFloatBuffer(3 * vertices);
    }



	/**
	 * Create a new IntBuffer of the specified size.
	 * 
	 * @param size required number of ints to store.
	 * 
	 * @return the new IntBuffer
	 */
	public static IntBuffer createIntBuffer(int size) {
	    IntBuffer buf = ByteBuffer.allocateDirect(SIZEOF_INT * size).order(ByteOrder.nativeOrder()).asIntBuffer();
	    buf.clear();
	    return buf;
	}
	
	/**
	 * Create a new ShortBuffer of the specified size.
	 * 
	 * @param size required number of ints to store.
	 * 
	 * @return the new ShortBuffer
	 */
	public static ShortBuffer createShortBuffer(int size) {
	    ShortBuffer buf = ByteBuffer.allocateDirect(SIZEOF_SHORT * size).order(ByteOrder.nativeOrder()).asShortBuffer();
	    buf.clear();
	    return buf;
	}


	/**
	 * Create a new ByteBuffer of the specified size.
	 * 
	 * @param size required number of ints to store.
	 * 
	 * @return the new IntBuffer
	 */
	public static ByteBuffer createByteBuffer(int size) {
	    ByteBuffer buf = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	    buf.clear();
	    return buf;
	}

	
	/**
     * Updates the values of the given vector from the specified buffer at the
     * index provided.
     *
     * @param vector
     *            the vector to set data on
     * @param buf
     *            the buffer to read from
     * @param index
     *            the position (in terms of vectors, not floats) to read from
     *            the buf
     */
    public static void populateFromBuffer(Vector3D vector, FloatBuffer buf, int index) {
        vector.x = buf.get(index*3);
        vector.y = buf.get(index*3+1);
        vector.z = buf.get(index*3+2);
    }
    
    
    /**
     * Copies floats from one position in the buffer to another.
     *
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the starting point to copy from
     * @param toPos
     *            the starting point to copy to
     * @param length
     *            the number of floats to copy
     */
    public static void copyInternal(FloatBuffer buf, int fromPos, int toPos, int length) {
        float[] data = new float[length];
        buf.position(fromPos);
        buf.get(data);
        buf.position(toPos);
        buf.put(data);
    }
    
    
    // NOTE that this work must be done reflectively at the present time
    // because this code must compile and run correctly on both CDC/FP and J2SE
    private static boolean isCDCFP;
    private static Class byteOrderClass;
    private static Object nativeOrderObject;
    private static Method orderMethod;

    public static ByteBuffer nativeOrder(ByteBuffer buf) {
    	if (!isCDCFP) {
    		try {
    			if (byteOrderClass == null) {
    				byteOrderClass = Class.forName("java.nio.ByteOrder");
    				orderMethod = ByteBuffer.class.getMethod("order", new Class[] { byteOrderClass });
    				Method nativeOrderMethod = byteOrderClass.getMethod("nativeOrder", null);
    				nativeOrderObject = nativeOrderMethod.invoke(null, null);
    			}
    		} catch (Throwable t) {
    			// Must be running on CDC / FP
    			isCDCFP = true;
    		}

    		if (!isCDCFP) {
    			try {
    				orderMethod.invoke(buf, new Object[] { nativeOrderObject });
    			} catch (Throwable t) {
    			}
    		}
    	}
    	return buf;
    }

    /** Allocates a new direct ByteBuffer with the specified number of
        elements. The returned buffer will have its byte order set to
    the host platform's native byte order. */
    public static ByteBuffer newByteBuffer(int numElements) {
    	ByteBuffer bb = ByteBuffer.allocateDirect(numElements);
    	nativeOrder(bb);
    	return bb;
    }

    /** Allocates a new direct IntBuffer with the specified number of
          elements. The returned buffer will have its byte order set to
          the host platform's native byte order. */
    public static IntBuffer newIntBuffer(int numElements) {
    	ByteBuffer bb = newByteBuffer(numElements * SIZEOF_INT);
    	return bb.asIntBuffer();
    }

    public static IntBuffer newIntBuffer(int[] values, int offset, int len) {
    	IntBuffer bb = newIntBuffer(len);
    	bb.put(values, offset, len);
    	bb.rewind();
    	return bb;
    }

    public static IntBuffer newIntBuffer(int[] values, int offset) {
    	return newIntBuffer(values, 0, values.length-offset);
    }

    public static IntBuffer newIntBuffer(int[] values) {
    	return newIntBuffer(values, 0);
    }


}
