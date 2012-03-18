/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.components.bounds;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.util.camera.IFrustum;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;

import processing.core.PGraphics;


/**
 * The Class BoundsArbitraryPlanarPolygon.
 * @author Christopher Ruff
 */
public class BoundsArbitraryPlanarPolygon implements IBoundingShape {
	
	/** The peer component. */
	private MTComponent peerComponent;
	
	/** The bounding points local. */
	private Vector3D[] boundingPointsLocal;
	
	
	/** The xy bounds rect. */
	private BoundsZPlaneRectangle xyBoundsRect;
	
	
	private Vector3D[] worldVecs;
	private boolean worldVecsDirty;
	private Vector3D centerPointWorld;
	private boolean centerWorldDirty;
	
	
	/**
	 * A 2D bounding polygon that is defined by the specified vectors.
	 * This bounding shape is only suitable for objects that lie entirely in the z=0 plane.
	 * 
	 * @param peerComponent the peer component
	 * @param boundingPoints the bounding points
	 */
	public BoundsArbitraryPlanarPolygon(MTComponent peerComponent, Vector3D[] boundingPoints) {
		super();
		this.peerComponent 				= peerComponent;
		this.boundingPointsLocal 		= boundingPoints;
		
		if (boundingPointsLocal.length < 3){
			throw new RuntimeException("Bounds have to have at least 3 vertices!");
		}
		
		//Calc bounding rect in xy-plane for width/height
		this.xyBoundsRect = new BoundsZPlaneRectangle(peerComponent, boundingPoints);
		
		this.worldVecsDirty 	= true;
		this.centerWorldDirty 	= true;
//		this.worldVecs 			= this.getVectorsGlobal();
//		this.centerPointWorld 	= this.getCenterPointGlobal();
	}
	
	
	public void drawBounds(PGraphics g) {
		g.pushMatrix();
		g.fill(150,180);
		g.beginShape();
		Vector3D[] vectors = this.getVectorsLocal();
        for (Vector3D vector : vectors) {
            g.vertex(vector.x, vector.y, vector.z);
        }
		g.endShape();
		g.popMatrix();
	}
	
	
	public void setGlobalBoundsChanged(){
		this.worldVecsDirty = true;
		this.centerWorldDirty = true;
		xyBoundsRect.setGlobalBoundsChanged();
	}

	
	public Vector3D getCenterPointLocal() {
		//TODO check if entierely in x,y plane and use this, else create bounding box/sphere and get the center
		return  ToolsGeometry.getPolygonCenterOfMass2D(this.boundingPointsLocal);
	}
	
	
	public Vector3D getCenterPointGlobal() {
		if (centerWorldDirty){
			Vector3D tmp = this.getCenterPointLocal().getCopy();
			tmp.transform(this.peerComponent.getGlobalMatrix());
			this.centerPointWorld = tmp;
			this.centerWorldDirty = false;
			return this.centerPointWorld;
		}
		else{
			return this.centerPointWorld;
		}
	}

	
	public Vector3D getIntersectionLocal(Ray ray) {
		Vector3D[] verts 	= this.boundingPointsLocal;
		Vector3D polyNormal = this.getNormalLocal();
		Vector3D testPoint 	= ToolsGeometry.getRayPlaneIntersection(ray, polyNormal, verts[0]);
		if (testPoint == null)
			return null;
		
		return (ToolsGeometry.isPoint3DInPlanarPolygon(verts, testPoint, polyNormal)? testPoint : null);
	}

	
	/**
	 * Gets the normal local.
	 * 
	 * @return the normal local
	 */
	private Vector3D getNormalLocal() {
		return ToolsGeometry.getNormal(this.boundingPointsLocal[0], this.boundingPointsLocal[1], this.boundingPointsLocal[2], true);
	}

	
	public Vector3D[] getVectorsLocal() {
		return this.boundingPointsLocal;
	}


	public Vector3D[] getVectorsGlobal() {
		if (this.worldVecsDirty){
			Vector3D[] vecs = Vector3D.getDeepVertexArrayCopy(this.boundingPointsLocal);
			Vector3D.transFormArrayLocal(this.peerComponent.getGlobalMatrix(), vecs);
			this.worldVecs = vecs;
			this.worldVecsDirty = false;
			return this.worldVecs;
		}else{
			return this.worldVecs;
		}
	}


	public boolean containsPointLocal(Vector3D testPoint) {
		return ToolsGeometry.isPolygonContainsPoint(this.getVectorsLocal(), testPoint);
	}


	public float getHeightXY(TransformSpace transformSpace) {
		switch (transformSpace) {
		case LOCAL:
			return this.getHeightXYObjSpace();
		case RELATIVE_TO_PARENT:
			return this.getHeightXYRelativeToParent();
		case GLOBAL:
			return this.getHeightXYGlobal();
		default:
			return -1;
		}
	}


	/**
	 * Gets the height xy obj space.
	 * 
	 * @return the height xy obj space
	 */
	private float getHeightXYObjSpace() {
		return this.getHeightXYVectLocal().length();
	}

	/**
	 * Gets the "height vector" and transforms it to parent relative space, then calculates
	 * its length.
	 * 
	 * @return the height xy relative to parent
	 * 
	 * the height relative to its peer components parent frame of reference
	 */
	private float getHeightXYRelativeToParent() {
		Vector3D p = this.getHeightXYVectLocal();
		Matrix m = new Matrix(this.peerComponent.getLocalMatrix());
		m.removeTranslationFromMatrix();
		p.transform(m);
		return p.length();
		
//		Vector3D[] v = xyBoundsRect.getVectorsRelativeToParent();
//		float[] minMax = ToolsGeometry.getMinXYMaxXY(v);
//		return minMax[3] - minMax[1];
	}


	/**
	 * Gets the "height vector" and transforms it to world space, then calculates
	 * its length.
	 * 
	 * @return the height xy global
	 * 
	 * the height relative to the world space
	 */
	private float getHeightXYGlobal() {
		Vector3D p = this.getHeightXYVectLocal();
		Matrix m = new Matrix(this.peerComponent.getGlobalMatrix());
		m.removeTranslationFromMatrix();
		p.transform(m);
		return p.length();
		
//		Vector3D[] v = xyBoundsRect.getVectorsGlobal();
//		float[] minMax = ToolsGeometry.getMinXYMaxXY(v);
//		return minMax[3] - minMax[1];
	}

	/**
	 * Gets the "height vector". The vector is calculated from the bounds vectors,
	 * representing a vector with the height as its length in object space.
	 * 
	 * @return the height xy vect obj space
	 * 
	 * vector representing the height of the boundingshape of the shape
	 */
	public Vector3D getHeightXYVectLocal() {
		return this.xyBoundsRect.getHeightXYVectLocal();
	}


	public float getWidthXY(TransformSpace transformSpace) {
		switch (transformSpace) {
		case LOCAL:
			return this.getWidthXYObjSpace();
		case RELATIVE_TO_PARENT:
			return this.getWidthXYRealtiveToParent();
		case GLOBAL:
			return this.getWidthXYGlobal();
		default:
			return -1;
		}
	}


	/**
	 * Gets the width xy obj space.
	 * 
	 * @return the width xy obj space
	 */
	private float getWidthXYObjSpace() {
		return this.getWidthXYVectLocal().length();
	}


	/**
	 * Calculates the width of this shape, by using the
	 * bounding shapes vectors.
	 * Uses the objects local transform. So the width will be
	 * relative to the parent only - not the whole world
	 * 
	 * @return the width xy realtive to parent
	 * 
	 * the width
	 */
	private float getWidthXYRealtiveToParent() {
		Vector3D p = this.getWidthXYVectLocal();
		Matrix m = new Matrix(this.peerComponent.getLocalMatrix());
		m.removeTranslationFromMatrix();
		p.transform(m);
		return p.length();
		
//		Vector3D[] v = xyBoundsRect.getVectorsRelativeToParent();
//		float[] minMax = ToolsGeometry.getMinXYMaxXY(v);
//		return minMax[2] - minMax[0];
	}
	

	/**
	 * Gets the "Width vector" and transforms it to world space, then calculates
	 * its length.
	 * 
	 * @return the width xy global
	 * 
	 * the Width relative to the world space
	 */
	private float getWidthXYGlobal() {
		Vector3D p = this.getWidthXYVectLocal();
		Matrix m = new Matrix(this.peerComponent.getGlobalMatrix());
		m.removeTranslationFromMatrix();
		p.transform(m);
		return p.length();
		
//		Vector3D[] v = xyBoundsRect.getVectorsGlobal();
//		float[] minMax = ToolsGeometry.getMinXYMaxXY(v);
//		return minMax[2] - minMax[0];
	}

	/**
	 * Gets the "Width vector". The vector is calculated from the bounds vectors,
	 * representing a vector with the Width as its length in object space.
	 * 
	 * @return the width xy vect obj space
	 * 
	 * vector representing the Width of the boundingshape of the shape
	 */
	public Vector3D getWidthXYVectLocal() {
		return this.xyBoundsRect.getWidthXYVectLocal();
	}


	//@Override
	public boolean isContainedInFrustum(IFrustum frustum) {
		Vector3D[] points = this.getVectorsGlobal();
        for (Vector3D vector3D : points) {
            int test = frustum.isPointInFrustum(vector3D);
            if (test == IFrustum.INSIDE
                    || test == IFrustum.INTERSECT
                    ) {
                return true;
            }
        }
		return false;
	}
	
	

}
