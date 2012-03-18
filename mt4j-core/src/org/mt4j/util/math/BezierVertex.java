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
 * The Class BezierVertex.
 * @author Christopher Ruff
 */
public class BezierVertex extends Vertex {
	
	/** The first ctrl point. */
	private Vertex firstCtrlPoint;
	
	/** The second ctrl point. */
	private Vertex secondCtrlPoint;
	
	/**
	 * Instantiates a new bezier vertex.
	 * 
	 * @param controlP1X the control p1 x
	 * @param controlP1Y the control p1 y
	 * @param controlP1Z the control p1 z
	 * @param controlP2X the control p2 x
	 * @param controlP2Y the control p2 y
	 * @param controlP2Z the control p2 z
	 * @param anchorX the anchor x
	 * @param anchorY the anchor y
	 * @param anchorZ the anchor z
	 */
	public BezierVertex(float controlP1X, float controlP1Y, float controlP1Z,
						float controlP2X, float controlP2Y ,float  controlP2Z,
						float anchorX, float anchorY, float anchorZ) 
	{
		this(controlP1X, controlP1Y, controlP1Z, 1, controlP2X, controlP2Y, controlP2Z, 1, anchorX, anchorY, anchorZ, 1);
	}



	/**
	 * Instantiates a new bezier vertex.
	 * 
	 * @param x2 the x2
	 * @param y2 the y2
	 * @param z2 the z2
	 * @param w2 the w2
	 * @param x3 the x3
	 * @param y3 the y3
	 * @param z3 the z3
	 * @param w3 the w3
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param w the w
	 */
	public BezierVertex(float x2, float y2, float z2, float w2, float x3, float y3, float z3, float w3, float x, float y, float z, float w) {
		super(x, y, z, w);
		this.firstCtrlPoint = new Vertex(x2, y2, z2, w2);
		this.secondCtrlPoint = new Vertex(x3, y3, z3, w3);
		
		this.setType(Vector3D.BEZIERVERTEX);
	}



	/* (non-Javadoc)
	 * @see util.math.Vector3D#transform(util.math.Matrix)
	 */
	@Override
	public void transform(Matrix transformMatrix) {
		super.transform(transformMatrix);
		this.getFirstCtrlPoint().transform(transformMatrix);
		this.getSecondCtrlPoint().transform(transformMatrix);
	}

	/* (non-Javadoc)
	 * @see util.math.Vector3D#scaleLocal(float)
	 */
	@Override
	public Vector3D scaleLocal(float scalar) {
		super.scaleLocal(scalar);
		this.getFirstCtrlPoint().scaleLocal(scalar);
		this.getSecondCtrlPoint().scaleLocal(scalar);
		return this;
	}

	/* (non-Javadoc)
	 * @see util.math.Vector3D#rotateX(util.math.Vector3D, float)
	 */
	@Override
	public void rotateX(Vector3D rotationPoint, float degree) {
		super.rotateX(rotationPoint, degree);
		this.getFirstCtrlPoint().rotateX(rotationPoint, degree);
		this.getSecondCtrlPoint().rotateX(rotationPoint, degree);
	}

	/* (non-Javadoc)
	 * @see util.math.Vector3D#rotateY(util.math.Vector3D, float)
	 */
	@Override
	public void rotateY(Vector3D rotationPoint, float degree) {
		super.rotateY(rotationPoint, degree);
		this.getFirstCtrlPoint().rotateY(rotationPoint, degree);
		this.getSecondCtrlPoint().rotateY(rotationPoint, degree);
	}

	/* (non-Javadoc)
	 * @see util.math.Vector3D#rotateZ(util.math.Vector3D, float)
	 */
	@Override
	public void rotateZ(Vector3D rotationPoint, float degree) {
		super.rotateZ(rotationPoint, degree);
		this.getFirstCtrlPoint().rotateZ(rotationPoint, degree);
		this.getSecondCtrlPoint().rotateZ(rotationPoint, degree);
	}

	
	/* (non-Javadoc)
	 * @see util.math.Vertex#getCopy()
	 */
	@Override
	public Vector3D getCopy() {
		return new BezierVertex(firstCtrlPoint.getX(), firstCtrlPoint.getY(), firstCtrlPoint.getZ(), firstCtrlPoint.getW(),
						secondCtrlPoint.getX(), secondCtrlPoint.getY(), secondCtrlPoint.getZ(), secondCtrlPoint.getW(),
						this.getX(), this.getY(), this.getZ(), this.getW());
	}

	/* (non-Javadoc)
	 * @see util.math.Vertex#equalsVector(util.math.Vector3D)
	 */
	@Override
	public boolean equalsVector(Vector3D bez) {
		return (bez.getType() == this.getType() 
				&&	super.equalsVector(bez) 
				&& this.getFirstCtrlPoint() == ((BezierVertex)bez).getFirstCtrlPoint()
				&& this.getSecondCtrlPoint() == ((BezierVertex)bez).getSecondCtrlPoint()
				);
	}


	/**
	 * Gets the first ctrl point.
	 * 
	 * @return the first ctrl point
	 */
	public Vertex getFirstCtrlPoint() {
		return firstCtrlPoint;
	}


	/**
	 * Sets the first ctrl point.
	 * 
	 * @param firstCtrlPoint the new first ctrl point
	 */
	public void setFirstCtrlPoint(Vertex firstCtrlPoint) {
		this.firstCtrlPoint = firstCtrlPoint;
	}


	/**
	 * Gets the second ctrl point.
	 * 
	 * @return the second ctrl point
	 */
	public Vertex getSecondCtrlPoint() {
		return secondCtrlPoint;
	}


	/**
	 * Sets the second ctrl point.
	 * 
	 * @param secondCtrlPoint the new second ctrl point
	 */
	public void setSecondCtrlPoint(Vertex secondCtrlPoint) {
		this.secondCtrlPoint = secondCtrlPoint;
	}

}
