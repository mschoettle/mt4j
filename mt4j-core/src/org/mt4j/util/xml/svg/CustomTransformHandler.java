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
package org.mt4j.util.xml.svg;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListHandler;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;


/**
 * The Class CustomTransformHandler.
 */
public class CustomTransformHandler implements TransformListHandler {
	
	/** The result matrix. */
	private Matrix resultMatrix;
	
	/** The verbose. */
	private boolean verbose;
	
	/**
	 * Instantiates a new custom transform handler.
	 */
	public CustomTransformHandler(){
		resultMatrix = Matrix.get4x4Identity();
		verbose = false;
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#startTransformList()
	 */
	public void startTransformList() throws ParseException {
		
	}
	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#matrix(float, float, float, float, float, float)
	 */
	public void matrix(float a, float b, float c, float d, float e, float f)  throws ParseException {
		if (verbose)
			System.out.println("Matrix: " + a + " " + b + " " + c + "\n" + d + " " + e + " " + f);
		
		resultMatrix = resultMatrix.mult(new Matrix(
				a, c, 0, e,
				b, d, 0, f,
                0, 0, 1, 0,
                0, 0, 0, 1
		), resultMatrix);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#rotate(float)
	 */
	public void rotate(float theta) throws ParseException {
		resultMatrix = resultMatrix.mult(Matrix.getZRotationMatrix(new Vector3D(0,0,0), theta), resultMatrix);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#rotate(float, float, float)
	 */
	public void rotate(float theta, float cx, float cy)  throws ParseException {
		resultMatrix = resultMatrix.mult(Matrix.getZRotationMatrix(new Vector3D(cx,cy,0), theta), resultMatrix);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#scale(float)
	 */
	public void scale(float sx) throws ParseException {
		resultMatrix = resultMatrix.mult(Matrix.getScalingMatrix(sx, 1, 1), resultMatrix);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#scale(float, float)
	 */
	public void scale(float sx, float sy) throws ParseException {
		resultMatrix = resultMatrix.mult(Matrix.getScalingMatrix(sx, sy, 1), resultMatrix);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#skewX(float)
	 */
	public void skewX(float arg0) throws ParseException {
		resultMatrix = resultMatrix.mult(new Matrix(
                1, 0, 0, 0,
                (float)Math.tan(arg0), 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
		), resultMatrix);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#skewY(float)
	 */
	public void skewY(float arg0) throws ParseException {
		resultMatrix = resultMatrix.mult(new Matrix(
				1, (float)Math.tan(arg0), 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
		), resultMatrix);
	}


	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#translate(float)
	 */
	public void translate(float tx) throws ParseException {
		resultMatrix = resultMatrix.mult(Matrix.getTranslationMatrix(tx, 0, 0), resultMatrix);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#translate(float, float)
	 */
	public void translate(float tx, float ty) throws ParseException {
		resultMatrix = resultMatrix.mult(Matrix.getTranslationMatrix(tx, ty, 0), resultMatrix);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.TransformListHandler#endTransformList()
	 */
	public void endTransformList() throws ParseException {
		
	}

	/**
	 * Gets the result matrix.
	 * 
	 * @return the result matrix
	 */
	public Matrix getResultMatrix() {
		return resultMatrix;
	}

	/**
	 * Checks if is verbose.
	 * 
	 * @return true, if is verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * Sets the verbose.
	 * 
	 * @param verbose the new verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	

}
