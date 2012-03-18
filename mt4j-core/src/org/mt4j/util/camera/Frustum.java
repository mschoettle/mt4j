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
package org.mt4j.util.camera;

import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Plane;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * The Class Frustum. Represents a camera frustum.
 * Can be used to check whether a point or a sphere lies in the frustum (is visible).
 * This is based off code from lighthouse3d.
 * @author Christopher Ruff
 */
public class Frustum implements IFrustum{
//	private static float ANG2RAD = (3.14159265358979323846f/360.0f); //FIXME warum /360 ? m�sste doch /180 sein?
	private float ratio;
	private float angle;
	private float nearD;
	private float farD;
	
	private GL10 gl;
	
	private Vector3D _tmpVec3 = new Vector3D();
	private Vector3D _tmpVec2 = new Vector3D();
	private Vector3D _tmpVec = new Vector3D();
	
	private float sphereFactorY;
	private float sphereFactorX;
	
	private float nh;
	private float nw;
	private float fh;
	private float fw;
	
	//camera position and vectors
	private Vector3D camPos;
	private Vector3D Z;
	private Vector3D X;
	private Vector3D Y;
	
	//Frustum planes points
	public Vector3D ntl;
	public Vector3D ntr;
	public Vector3D nbl;
	public Vector3D nbr;
	public Vector3D ftl;
	public Vector3D fbr;
	public Vector3D ftr;
	public Vector3D fbl;
	
	private float tang; 
	
	public Plane[] planes;
	
	private static final int TOP 	= 0;
	private static final int BOTTOM = 1;
	private static final int LEFT 	= 2;
	private static final int RIGHT 	= 3;
	private static final int NEARP 	= 4;
	private static final int FARP 	= 5;
	
	                      
	/**
	 * Instantiates a new frustum using the current camera values
	 * from the processing context.
	 * 
	 * 
	 * @param pa the pa
	 */
	public Frustum(PApplet pa){
		if (MT4jSettings.getInstance().isOpenGlMode()){
//			this.gl = ((PGraphicsOpenGL)pa.g).gl;
			this.gl = PlatformUtil.getGL();
		}
		
		
		camPos = new Vector3D();
		Z = new Vector3D();
		X = new Vector3D();
		Y = new Vector3D();
		
		planes = new Plane[]{
				 new Plane(Vector3D.ZERO_VECTOR,Vector3D.ZERO_VECTOR)
				,new Plane(Vector3D.ZERO_VECTOR,Vector3D.ZERO_VECTOR)
				,new Plane(Vector3D.ZERO_VECTOR,Vector3D.ZERO_VECTOR)
				,new Plane(Vector3D.ZERO_VECTOR,Vector3D.ZERO_VECTOR)
				,new Plane(Vector3D.ZERO_VECTOR,Vector3D.ZERO_VECTOR)
				,new Plane(Vector3D.ZERO_VECTOR,Vector3D.ZERO_VECTOR)
		};
		
//		PGraphics3D p3d = ((PGraphics3D)pa.g);
//		float cameraFov = p3d.cameraFOV;
//		float cameraAspect = p3d.cameraAspect;
//		float cameraNear = p3d.cameraNear;
//		float cameraFar = p3d.cameraFar;

		float cameraFov = PlatformUtil.getCameraFOV();
		float cameraAspect = PlatformUtil.getCameraAspect();
		float cameraNear = PlatformUtil.getCameraNear();
		float cameraFar = PlatformUtil.getCameraFar();
		
		//This has to be called if the perspective is changed!!
        this.setCamInternals(cameraFov*0.5f, cameraAspect, cameraNear,  cameraFar);
	}
	
	
//	#define m(col,row)  m[row*4+col]

//	void setFrustum(float m) {
//		pl[NEARP].setCoefficients(m(2,0) + m(3,0),
//								  m(2,1) + m(3,1),
//								  m(2,2) + m(3,2),
//								  m(2,3) + m(3,3));
//		pl[FARP].setCoefficients( -m(2,0) + m(3,0),
//								  -m(2,1) + m(3,1),
//								  -m(2,2) + m(3,2),
//								  -m(2,3) + m(3,3));
//		pl[BOTTOM].setCoefficients(m(1,0) + m(3,0),
//								   m(1,1) + m(3,1),
//								   m(1,2) + m(3,2),
//								   m(1,3) + m(3,3));
//		pl[TOP].setCoefficients(  -m(1,0) + m(3,0),
//								  -m(1,1) + m(3,1),
//								  -m(1,2) + m(3,2),
//								  -m(1,3) + m(3,3));
//		pl[LEFT].setCoefficients(  m(0,0) + m(3,0),
//								   m(0,1) + m(3,1),
//								   m(0,2) + m(3,2),
//								   m(0,3) + m(3,3));
//		pl[RIGHT].setCoefficients(-m(0,0) + m(3,0),
//								  -m(0,1) + m(3,1),
//								  -m(0,2) + m(3,2),
//								  -m(0,3) + m(3,3));
//	}


//TODO immer wenn sich perspective �ndert

	/**
	 * Sets the cam internals. Has to be called if perspective
	 * is changed.
	 * 
	 * @param angle the angle
	 * @param ratio the ratio
	 * @param nearD the near d
	 * @param farD the far d
	 */
	public void setCamInternals(float angle, float ratio, float nearD, float farD) { //ERWARTET ANGLE WERTE IN RADIANS! (bei p5 cameraFov*0.5f)
		// store the information
		this.ratio = ratio;
		this.angle = angle;
//		this.angle = angle * ANG2RAD;
		this.nearD = nearD;
		this.farD = farD;

		// compute width and height of the near and far plane sections
		tang = ToolsMath.tan(this.angle); //(float) Math.tan(this.angle);
		sphereFactorY = (1.0f/ToolsMath.cos(this.angle)); // (float) (1.0/Math.cos(this.angle)); 

		float anglex = ToolsMath.atan(tang*ratio); //(float) Math.atan(tang*ratio);
		sphereFactorX = (1.0f/ToolsMath.cos(anglex));//(float) (1.0f/Math.cos(anglex)); 

		nh = nearD * tang;
		nw = nh * ratio; 

		fh = farD * tang;
		fw = fh * ratio;
	}



/**
 * Re/Sets the frustum to the specified camera values.
 * 
 * @param camPos the camera position
 * @param viewCenterPos the view center pos (look at)
 * @param xUp the x up vector
 * @param yUp the y up vector
 * @param zUp the z up vector
 */
public void setCamDef(Vector3D camPos, Vector3D viewCenterPos, float xUp, float yUp, float zUp){// Vector3D u) {
		this.camPos = camPos.getCopy();
		
		_tmpVec2.setValues(this.camPos);

		Z.setValues(_tmpVec2.subtractLocal(viewCenterPos));
		Z.normalizeLocal();
		
		// X axis of camera of given "up" vector and Z axis
//		X.setValues(u.getCross(Z));
		_tmpVec2.setXYZ(xUp, yUp, zUp);
		X.setValues(_tmpVec2.crossLocal(Z));
		X.normalizeLocal();

		// the real "up" vector is the cross product of Z and X
//		Y.setValues(Z.getCross(X));
		_tmpVec2.setValues(Z);
		Y.setValues(_tmpVec2.crossLocal(X));
		
//		/*
		// compute the center of the near and far planes - THE PLANES ARENT NEEDED FOR THE CALCULATIONS TO WORK!
		Vector3D nc,fc;
		nc = this.camPos.getSubtracted(Z.getScaled(nearD));
		fc = this.camPos.getSubtracted(Z.getScaled(farD));

		// compute the 8 corners of the frustum
		Vector3D yScaledNh = Y.getScaled(nh);
		Vector3D xScaledNw = X.getScaled(nw);
		Vector3D yScaledfh = Y.getScaled(fh);
		Vector3D xScaledfw = X.getScaled(fw);
		ntl = nc.getAdded(yScaledNh).subtractLocal(xScaledNw);
		ntr = nc.getAdded(yScaledNh).addLocal(xScaledNw);
		nbl = nc.getSubtracted(yScaledNh).subtractLocal(xScaledNw);
		nbr = nc.getSubtracted(yScaledNh).addLocal(xScaledNw);
		ftl = fc.getAdded(yScaledfh).subtractLocal(xScaledfw);
		fbr = fc.getSubtracted(yScaledfh).addLocal(xScaledfw);
		ftr = fc.getAdded(yScaledfh).addLocal(xScaledfw);
		fbl = fc.getSubtracted(yScaledfh).subtractLocal(xScaledfw);
//		*/
		
		/*
		System.out.println("NEAR TOP LEFT: " + ntl + " RIGHT: " + ntr);
//		System.out.println("NEAR TOP RIGHT: " + ntr);
		System.out.println("NEAR BOTTOM LEFT: " + nbl + " RIGHT: " + nbr);
//		System.out.println("NEAR BOTTOM RIGHT: " + nbr);
		System.out.println();
		System.out.println("FAR TOP LEFT: " + ftl + " RIGHT: " + ftr);
//		System.out.println("FAR BOTTOM RIGHT: " + fbr);
//		System.out.println("FAR TOP RIGHT: " + ftr);
		System.out.println("FAR BOTTOM LEFT: " + fbl + " BOTTOM RIGHT: " + fbr);
		System.out.println();
		*/
		
		// compute the six planes
		// the function set3Points asssumes that the points
		// are given in counter clockwise order
//		/*
		//This computes the 6 frustum planes
		planes[NEARP].reconstruct(nc ,Z.getScaled(-1));
		planes[FARP].reconstruct(fc, Z);
		
		Vector3D aux,normal;

		aux = (nc.getAdded(yScaledNh)).subtractLocal(camPos);
		normal = aux.crossLocal(X);
		planes[TOP].reconstruct(nc.getAdded(yScaledNh), normal);

		aux = (nc.getSubtracted(yScaledNh)).subtractLocal(camPos);
		normal = X.getCross(aux);
		planes[BOTTOM].reconstruct(nc.getSubtracted(yScaledNh), normal);
		
		aux = (nc.getSubtracted(xScaledNw)).subtractLocal(camPos);
		normal = aux.crossLocal(Y);
		planes[LEFT].reconstruct(nc.getSubtracted(xScaledNw), normal);

		aux = (nc.getAdded(xScaledNw)).subtractLocal(camPos);
		normal = Y.getCross(aux);
		planes[RIGHT].reconstruct(nc.getAdded(xScaledNw), normal);
//		*/
	}



	/* (non-Javadoc)
	 * @see util.camera.IFrustum#isPointInFrustum(util.math.Vector3D)
	 */
	public int isPointInFrustum(Vector3D p) {
		float pcz,pcx,pcy,aux;

//		// compute vector from camera position to p
////		Vector3D v = p-camPos;
//		Vector3D v = p.getSubtracted(camPos);
//
//		// compute and test the Z coordinate
////		pcz = v.innerProduct(-Z);
////		pcz = v.dot(Z.getScaled(-1)); //TODO cache?
//		_tmpVec.setValues(Z);
//		pcz = v.dot(_tmpVec.scaleLocal(-1)); 
//		
//		if (pcz > farD || pcz < nearD)
//			return(OUTSIDE);
//
//		// compute and test the Y coordinate
////		pcy = v.innerProduct(Y);
//		pcy = v.dot(Y);
//		aux = pcz * tang;
//		if (pcy > aux || pcy < -aux)
//			return(OUTSIDE);
//			
//		// compute and test the X coordinate
////		pcx = v.innerProduct(X);
//		pcx = v.dot(X);
//		aux = aux * ratio;
//		if (pcx > aux || pcx < -aux)
//			return(OUTSIDE);
//
//		return(INSIDE);
		
		// compute vector from camera position to p
		_tmpVec.setValues(p);
		_tmpVec2.setValues(_tmpVec.subtractLocal(camPos));
		
		// compute and test the Z coordinate
		_tmpVec.setValues(Z);
		pcz = _tmpVec2.dot(_tmpVec.scaleLocal(-1)); 
		
		if (pcz > farD || pcz < nearD)
			return(OUTSIDE);

		// compute and test the Y coordinate
		pcy = _tmpVec2.dot(Y);
		aux = pcz * tang;
		if (pcy > aux || pcy < -aux)
			return(OUTSIDE);
			
		// compute and test the X coordinate
		pcx = _tmpVec2.dot(X);
		aux = aux * ratio;
		if (pcx > aux || pcx < -aux)
			return(OUTSIDE);

		return(INSIDE);
	}


	/* (non-Javadoc)
	 * @see util.camera.IFrustum#isSphereInFrustum(util.math.Vector3D, float)
	 */
	public int isSphereInFrustum(Vector3D p, float radius) {
//		float d1,d2;
//		float az,ax,ay,zz1,zz2;
//		int result = INSIDE;
//
////		Vector3D v = p-camPos;
//		Vector3D v = p.getSubtracted(camPos);
//
//		//TODO erst x,y checkan statt z da wir kaum in der tiefer operieren
////		az = v.innerProduct(-Z);
////		az = v.dot(Z.getScaled(-1));
//		_tmpVec.setValues(Z);
//		az = v.dot(_tmpVec.scaleLocal(-1));
//		
//		if (az > farD + radius || az < nearD-radius)
//			return(OUTSIDE);
//
////		ax = v.innerProduct(X);
//		ax = v.dot(X);
//		zz1 = az * tang * ratio;
//		d1 = sphereFactorX * radius;
//		if (ax > zz1+d1 || ax < -zz1-d1)
//			return(OUTSIDE);
//
////		ay = v.innerProduct(Y);
//		ay = v.dot(Y);
//		zz2 = az * tang;
//		d2 = sphereFactorY * radius;
//		if (ay > zz2+d2 || ay < -zz2-d2)
//			return(OUTSIDE);
//
//		if (az > farD - radius || az < nearD+radius)
//			result = INTERSECT;
//		if (ay > zz2-d2 || ay < -zz2+d2)
//			result = INTERSECT;
//		if (ax > zz1-d1 || ax < -zz1+d1)
//			result = INTERSECT;
//
//		return(result);
		
		float d1,d2;
		float az,ax,ay,zz1,zz2;
		int result = INSIDE;

//		Vector3D v = p-camPos;
//		Vector3D v = p.getSubtracted(camPos);
		
		_tmpVec3.setValues(p);
		_tmpVec3.subtractLocal(camPos);

		//TODO erst x,y checkan statt z da wir kaum in der tiefer operieren
//		az = v.innerProduct(-Z);
//		az = v.dot(Z.getScaled(-1));
		_tmpVec.setValues(Z);
		az = _tmpVec3.dot(_tmpVec.scaleLocal(-1));
		
		if (az > farD + radius || az < nearD-radius)
			return(OUTSIDE);

//		ax = v.innerProduct(X);
		ax = _tmpVec3.dot(X);
		zz1 = az * tang * ratio;
		d1 = sphereFactorX * radius;
		if (ax > zz1+d1 || ax < -zz1-d1)
			return(OUTSIDE);

//		ay = v.innerProduct(Y);
		ay = _tmpVec3.dot(Y);
		zz2 = az * tang;
		d2 = sphereFactorY * radius;
		if (ay > zz2+d2 || ay < -zz2-d2)
			return(OUTSIDE);

		if (az > farD - radius || az < nearD+radius)
			result = INTERSECT;
		if (ay > zz2-d2 || ay < -zz2+d2)
			result = INTERSECT;
		if (ax > zz1-d1 || ax < -zz1+d1)
			result = INTERSECT;

		return(result);
	}


	/*
	public void drawPoints() {
		gl.glBegin(GL.GL_POINTS);
			gl.glVertex3f(ntl.x,ntl.y,ntl.z);
			gl.glVertex3f(ntr.x,ntr.y,ntr.z);
			gl.glVertex3f(nbl.x,nbl.y,nbl.z);
			gl.glVertex3f(nbr.x,nbr.y,nbr.z);

			gl.glVertex3f(ftl.x,ftl.y,ftl.z);
			gl.glVertex3f(ftr.x,ftr.y,ftr.z);
			gl.glVertex3f(fbl.x,fbl.y,fbl.z);
			gl.glVertex3f(fbr.x,fbr.y,fbr.z);
		gl.glEnd();
	}


	public void drawLines() {
		gl.glBegin(GL.GL_LINE_LOOP);
		//near plane
			gl.glVertex3f(ntl.x,ntl.y,ntl.z);
			gl.glVertex3f(ntr.x,ntr.y,ntr.z);
			gl.glVertex3f(nbr.x,nbr.y,nbr.z);
			gl.glVertex3f(nbl.x,nbl.y,nbl.z);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		//far plane
			gl.glVertex3f(ftr.x,ftr.y,ftr.z);
			gl.glVertex3f(ftl.x,ftl.y,ftl.z);
			gl.glVertex3f(fbl.x,fbl.y,fbl.z);
			gl.glVertex3f(fbr.x,fbr.y,fbr.z);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		//bottom plane
			gl.glVertex3f(nbl.x,nbl.y,nbl.z);
			gl.glVertex3f(nbr.x,nbr.y,nbr.z);
			gl.glVertex3f(fbr.x,fbr.y,fbr.z);
			gl.glVertex3f(fbl.x,fbl.y,fbl.z);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		//top plane
			gl.glVertex3f(ntr.x,ntr.y,ntr.z);
			gl.glVertex3f(ntl.x,ntl.y,ntl.z);
			gl.glVertex3f(ftl.x,ftl.y,ftl.z);
			gl.glVertex3f(ftr.x,ftr.y,ftr.z);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		//left plane
			gl.glVertex3f(ntl.x,ntl.y,ntl.z);
			gl.glVertex3f(nbl.x,nbl.y,nbl.z);
			gl.glVertex3f(fbl.x,fbl.y,fbl.z);
			gl.glVertex3f(ftl.x,ftl.y,ftl.z);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		// right plane
			gl.glVertex3f(nbr.x,nbr.y,nbr.z);
			gl.glVertex3f(ntr.x,ntr.y,ntr.z);
			gl.glVertex3f(ftr.x,ftr.y,ftr.z);
			gl.glVertex3f(fbr.x,fbr.y,fbr.z);

		gl.glEnd();
	}


	public void drawPlanes() {
		gl.glBegin(GL.GL_QUADS);

		//near plane
		gl.glColor4d(1.0f, 0.5f, 0.5f, 0.5f);
			gl.glVertex3f(ntl.x,ntl.y,ntl.z);
			gl.glVertex3f(ntr.x,ntr.y,ntr.z);
			gl.glVertex3f(nbr.x,nbr.y,nbr.z);
			gl.glVertex3f(nbl.x,nbl.y,nbl.z);

		//far plane
			gl.glColor4d(0.5f, 1.0f, 0.5f, 0.5f);
			gl.glVertex3f(ftr.x,ftr.y,ftr.z);
			gl.glVertex3f(ftl.x,ftl.y,ftl.z);
			gl.glVertex3f(fbl.x,fbl.y,fbl.z);
			gl.glVertex3f(fbr.x,fbr.y,fbr.z);

		//bottom plane
			gl.glColor4d(0.5f, 0.5f, 1.0f, 0.5f);
			gl.glVertex3f(nbl.x,nbl.y,nbl.z);
			gl.glVertex3f(nbr.x,nbr.y,nbr.z);
			gl.glVertex3f(fbr.x,fbr.y,fbr.z);
			gl.glVertex3f(fbl.x,fbl.y,fbl.z);

		//top plane
			gl.glColor4d(0.7f, 0.6f, 0.5f, 0.5f);
			gl.glVertex3f(ntr.x,ntr.y,ntr.z);
			gl.glVertex3f(ntl.x,ntl.y,ntl.z);
			gl.glVertex3f(ftl.x,ftl.y,ftl.z);
			gl.glVertex3f(ftr.x,ftr.y,ftr.z);

		//left plane
			gl.glColor4d(0.5f, 0.6f, 0.7f, 0.5f);
			gl.glVertex3f(ntl.x,ntl.y,ntl.z);
			gl.glVertex3f(nbl.x,nbl.y,nbl.z);
			gl.glVertex3f(fbl.x,fbl.y,fbl.z);
			gl.glVertex3f(ftl.x,ftl.y,ftl.z);

		// right plane
			gl.glColor4d(0.6f, 0.7f, 0.5f, 0.5f);
			gl.glVertex3f(nbr.x,nbr.y,nbr.z);
			gl.glVertex3f(ntr.x,ntr.y,ntr.z);
			gl.glVertex3f(ftr.x,ftr.y,ftr.z);
			gl.glVertex3f(fbr.x,fbr.y,fbr.z);

		gl.glEnd();
	}

	public void drawNormals() {
		Vector3D a,b;

		gl.glBegin(GL.GL_LINES);
			// near
			a = (ntr .getAdded( ntl).getAdded(nbr).getAdded(nbl)).scaleLocal(0.25f);
			b = a.getAdded(planes[NEARP].normal);
			gl.glVertex3f(a.x,a.y,a.z);
			gl.glVertex3f(b.x,b.y,b.z);

			// far
			a = (ftr .getAdded( ftl).getAdded(fbr).getAdded(fbl)).scaleLocal(0.25f);
			b = a.getAdded(planes[FARP].normal);
			gl.glVertex3f(a.x,a.y,a.z);
			gl.glVertex3f(b.x,b.y,b.z);

			// left
			a = (ftl .getAdded( fbl).getAdded(nbl).getAdded(ntl)).scaleLocal(0.25f);
			b = a.getAdded(planes[LEFT].normal);
			gl.glVertex3f(a.x,a.y,a.z);
			gl.glVertex3f(b.x,b.y,b.z);
			
			// right
			a = (ftr .getAdded( nbr).getAdded(fbr).getAdded(ntr)).scaleLocal(0.25f);
			b = a.getAdded(planes[RIGHT].normal);
			gl.glVertex3f(a.x,a.y,a.z);
			gl.glVertex3f(b.x,b.y,b.z);
			
			// top
			a = (ftr .getAdded( ftl).getAdded(ntr).getAdded(ntl)).scaleLocal(0.25f);
			b = a.getAdded(planes[TOP].normal);
			gl.glVertex3f(a.x,a.y,a.z);
			gl.glVertex3f(b.x,b.y,b.z);
			
			// bottom
			a = (fbr .getAdded( fbl).getAdded(nbr).getAdded(nbl)).scaleLocal(0.25f);
			b = a.getAdded(planes[BOTTOM].normal);
			gl.glVertex3f(a.x,a.y,a.z);
			gl.glVertex3f(b.x,b.y,b.z);

		gl.glEnd();
	}
	*/

	/**
	 * Returns the height of the plane at a specific z value
	 * @return the float
	 */
	public float getHeightOfPlane(float z)
	{
		
		Vector3D ntlToftl = ftl.getSubtracted(ntl);
		float lengthComplete = ntlToftl.length();
		float zLengthComplete = Math.abs(ftl.z-ntl.z);
		float zLength = Math.abs(ntl.z-z);
		float factor = zLength/zLengthComplete;
		
		Vector3D zPlaneTopLeft = ntl.getAdded(ntlToftl.getScaled(factor));
		
		Vector3D zPlaneBottomLeft = nbl.getAdded(fbl.getSubtracted(nbl).getScaled(factor));
		
		return zPlaneTopLeft.getSubtracted(zPlaneBottomLeft).length();
		
	}
	
	/**
	 * Returns the width of the plane at a specific z value
	 * @return the float
	 */
	public float getWidthOfPlane(float z)
	{
		
		Vector3D ntrToftr = ftr.getSubtracted(ntr);
		float lengthComplete = ntrToftr.length();
		float zLength = Math.abs(ntl.z-z);
		float zLengthComplete = Math.abs(ftl.z-ntl.z);
		float factor = zLength/zLengthComplete;
				
		Vector3D zPlaneTopRight = ntr.getAdded(ntrToftr.getScaled(factor));
		Vector3D zPlaneTopLeft = ntl.getAdded(ftl.getSubtracted(ntl).getScaled(factor));
				
		return zPlaneTopLeft.getSubtracted(zPlaneTopRight).length();
	}
		
	/**
	 * Returns the height of the near plane
	 * @return the float
	 */
	public float getHeightOfNearPlane() {
		return Math.abs(ntl.y - nbl.y);
	}
	
	/**
	 * Returns the width of the near plane
	 * @return the float
	 */
	public float getWidthOfNearPlane() {
		  return Math.abs(ntl.x - ntr.x);
	}
	
	/**
	 * Returns the top left point of the near plane
	 * @return the Vector3D
	 */
	public Vector3D getNearTopLeft() {
		return ntl.getCopy();
	}
		
	
	/**
	 * Returns the z value of the near plane
	 * @return the float
	 */
	public float getZValueOfNearPlane()
	{
		return ntl.getZ();
	}


	
//	public void printPlanes() {
//
//		for (int i = 0; i < 6; i++) {
//
//				pl[i].print();
//				printf("\n");
//		}
//	}
	

}
