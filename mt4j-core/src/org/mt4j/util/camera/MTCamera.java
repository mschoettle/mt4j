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
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PMatrix3D;

/**
 * The Class MTCamera.
 * @author Christopher Ruff
 */
public class MTCamera implements Icamera{
	
	/** The pa. */
	private PApplet pa;
	
	/** The view center pos. */
	private Vector3D viewCenterPos;
	
	/** The cam pos. */
	private Vector3D camPos;
	
	/** The x axis up. */
	private float xAxisUp;
	
	/** The y axis up. */
	private float yAxisUp;
	
	/** The z axis up. */
	private float zAxisUp;
	
	/** The zoom min distance. */
	private float zoomMinDistance;
	
	private Frustum frustum;
	
	private boolean dirty;
	
	private PMatrix3D cameraMat;
	private PMatrix3D cameraInvMat;
	
	private Matrix cameraMatrix;
	private Matrix cameraInvMatrix;

	private PMatrix3D modelViewP5;

	private PMatrix3D modelViewInvP5;

	private PMatrix3D camP5;
	
//	private PGraphics3D p3d;

	/**
	 * Instantiates a new mT camera.
	 * 
	 * @param processingApplet the processing applet
	 */
	public MTCamera(PApplet processingApplet){
//		this(processingApplet.width/2.0f, processingApplet.height/2.0f, (processingApplet.height/2.0f) / PApplet.tan(PApplet.PI*60.0f / 360.0f),
//                processingApplet.width/2.0f, processingApplet.height/2.0f, 0, 0, 1,0, processingApplet);
		
		this(MT4jSettings.getInstance().getWindowWidth()/2.0f, MT4jSettings.getInstance().getWindowHeight()/2.0f, (MT4jSettings.getInstance().getWindowHeight()/2.0f) / PApplet.tan(PApplet.PI*60.0f / 360.0f),
				MT4jSettings.getInstance().getWindowWidth()/2.0f, MT4jSettings.getInstance().getWindowHeight()/2.0f, 0, 0, 1,0, processingApplet);

		
//		System.out.println("processingApplet.width: " + processingApplet.width);
//		System.out.println("processingApplet.height: " + processingApplet.height);
	    
//		this(processingApplet.width/2.0f, processingApplet.height/2.0f, (processingApplet.height/2.0f) / (((float) Math.tan(60 * PApplet.DEG_TO_RAD / 2.0f))),
//              processingApplet.width/2.0f, processingApplet.height/2.0f, 0, 0, 1, 0, processingApplet);
		
//		// init perspective projection based on new dimensions
//	    cameraFOV = 60 * DEG_TO_RAD; // at least for now
//	    cameraX = width / 2.0f;
//	    cameraY = height / 2.0f;
//	    cameraZ = cameraY / ((float) Math.tan(cameraFOV / 2.0f));
//	    cameraNear = cameraZ / 10.0f;
//	    cameraFar = cameraZ * 10.0f;
//	    cameraAspect = (float) width / (float) height;
	}
	
	
	/**
	 * Instantiates a new mT camera.
	 * 
	 * @param cameraPosX the camera pos x
	 * @param cameraPosY the camera pos y
	 * @param cameraPosZ the camera pos z
	 * @param camEyePosX the cam eye pos x
	 * @param camEyePosY the cam eye pos y
	 * @param camEyePosZ the cam eye pos z
	 * @param xAxisUp the x axis up
	 * @param yAxisUp the y axis up
	 * @param zAxisUp the z axis up
	 * @param processingApplet the processing applet
	 */
	public MTCamera(float cameraPosX, float cameraPosY, float cameraPosZ, 
						 float camEyePosX, float camEyePosY, float camEyePosZ,
						 float xAxisUp, float yAxisUp, float zAxisUp, 
						 PApplet processingApplet){
		
		this.pa 			= processingApplet;
		this.camPos 		= new Vector3D(cameraPosX, cameraPosY, cameraPosZ);
		this.viewCenterPos	= new Vector3D(camEyePosX, camEyePosY, camEyePosZ);
		this.xAxisUp 		= xAxisUp;
		this.yAxisUp 		= yAxisUp;
		this.zAxisUp 		= zAxisUp;
		
		this.zoomMinDistance = 0;
		
		this.frustum = new Frustum(pa);
		this.frustum.setCamDef(this.getPosition(), this.getViewCenterPos(),  xAxisUp, -yAxisUp, zAxisUp); //new Vector3D(xAxisUp, -yAxisUp, zAxisUp));
		
//		this.p3d = ((PGraphics3D)pa.g);
		this.modelViewP5 = PlatformUtil.getModelView();
		this.modelViewInvP5 = PlatformUtil.getModelViewInv();
		this.camP5 = PlatformUtil.getCamera();
		
		this.dirty = true;
		this.cameraMat 			= new PMatrix3D();
		this.cameraInvMat 		= new PMatrix3D();
		this.cameraMatrix 		= new Matrix();
		this.cameraInvMatrix 	= new Matrix();
	}
	
	/**
	 * Sets or updates the camera with the values specified in the camera.
	 * <br> <b>Call this after changing any camera values to take effect!</b>.
	 * <br><strong>Note:</strong> The current modelview matrices (=all transformations made up to this point) will be reset and replaced by the camera values!
	 */
	public void update(){
		/*
		pa.camera(camPos.getX(), camPos.getY() , camPos.getZ(), //eyeposition
		viewCenterPos.getX(), viewCenterPos.getY(), viewCenterPos.getZ(), //view center
		xAxisUp, yAxisUp, zAxisUp); 						//which axis points up?
		this.frustum.setCamDef(this.getPosition(), this.getViewCenterPos(),  xAxisUp, -yAxisUp, zAxisUp); //new Vector3D(xAxisUp, -yAxisUp, zAxisUp));
		*/
		
//		/*
		if (this.dirty){
//			System.out.println("Calc new camera");
			this.calcCameraMatrix(camPos.getX(), 			camPos.getY() , 		camPos.getZ(), //eyeposition
							viewCenterPos.getX(), 	viewCenterPos.getY(), 	viewCenterPos.getZ(), //view center
							xAxisUp, 				yAxisUp, 				zAxisUp);//which axis points up?
			this.setCachedCamMatrices();
			this.frustum.setCamDef(this.getPosition(), this.getViewCenterPos(),  xAxisUp, -yAxisUp, zAxisUp); //new Vector3D(xAxisUp, -yAxisUp, zAxisUp));
		}else{
//			System.out.println("Use Cached");
			this.setCachedCamMatrices();
		}
//		*/
	}
	
	
	protected void setCachedCamMatrices(){
		Matrix m = this.cameraMatrix;
		Matrix mi = this.cameraInvMatrix;
		
//		cameraMat.set(
//				m.m00, m.m01, m.m02, m.m03,
//				m.m10, m.m11, m.m12, m.m13,
//				m.m20, m.m21, m.m22, m.m23,
//				m.m30, m.m31, m.m32, m.m33);
//		
//		cameraInvMat.set(
//				mi.m00, mi.m01, mi.m02, mi.m03,
//				mi.m10, mi.m11, mi.m12, mi.m13,
//				mi.m20, mi.m21, mi.m22, mi.m23,
//				mi.m30, mi.m31, mi.m32, mi.m33);
//		
//		//cant also set cameraInv..not visible
////		p3d.camera.set(
////				m.m00, m.m01, m.m02, m.m03,
////				m.m10, m.m11, m.m12, m.m13,
////				m.m20, m.m21, m.m22, m.m23,
////				m.m30, m.m31, m.m32, m.m33);
//		camP5.set(
//				m.m00, m.m01, m.m02, m.m03,
//				m.m10, m.m11, m.m12, m.m13,
//				m.m20, m.m21, m.m22, m.m23,
//				m.m30, m.m31, m.m32, m.m33);
		
		//FIXME cannot set p5 cameraInv because its not visible..problem?
		
//		p3d.modelview.set(cameraMat);
//		p3d.modelviewInv.set(cameraInvMat);
		
//		modelViewP5.set(cameraMat);
//		modelViewInvP5.set(cameraInvMat);
		
		//FIXME remove platform dependence
//		PGraphics g = this.pa.g;
//		PGraphicsAndroid3D androidGraphics = (PGraphicsAndroid3D)g;
//		androidGraphics.updateModelview();
		
		PlatformUtil.setModelView(
				m.m00, m.m01, m.m02, m.m03,
				m.m10, m.m11, m.m12, m.m13,
				m.m20, m.m21, m.m22, m.m23,
				m.m30, m.m31, m.m32, m.m33);
		
		PlatformUtil.setModelViewInv(	
				mi.m00, mi.m01, mi.m02, mi.m03,
				mi.m10, mi.m11, mi.m12, mi.m13,
				mi.m20, mi.m21, mi.m22, mi.m23,
				mi.m30, mi.m31, mi.m32, mi.m33);
		
		PlatformUtil.setCamera(	
				m.m00, m.m01, m.m02, m.m03,
				m.m10, m.m11, m.m12, m.m13,
				m.m20, m.m21, m.m22, m.m23,
				m.m30, m.m31, m.m32, m.m33);
		
		PlatformUtil.setCameraInv(
				mi.m00, mi.m01, mi.m02, mi.m03,
				mi.m10, mi.m11, mi.m12, mi.m13,
				mi.m20, mi.m21, mi.m22, mi.m23,
				mi.m30, mi.m31, mi.m32, mi.m33);
		
		//TODO!?
//		gl.glMatrixMode(GL10.GL_MODELVIEW);
//	    gl.glLoadMatrixf(glmodelview, 0);
//	    if (usingGLMatrixStack) {
//	      modelviewStack.set(glmodelview);
//	    }
		
//		androidGraphics.updateCamera();
		
		//Sets our Matrix class cached 
		//cameraMatrix -> processing's modelView (and glModelView) and -> camera matrix 
		//and cameraInvMatrix -> processings modelviewInv (and glModelviewInv)
	}
	
	
	protected void calcCameraMatrix(float eyeX, 	float eyeY, 	float eyeZ,
							float centerX, 	float centerY, 	float centerZ,
							float upX, 		float upY, 		float upZ
	) {
		/*
		float z0 = eyeX - centerX;
		float z1 = eyeY - centerY;
		float z2 = eyeZ - centerZ;
		float mag = FastMath.sqrt(z0*z0 + z1*z1 + z2*z2);

		if (mag != 0) {
			z0 /= mag;
			z1 /= mag;
			z2 /= mag;
		}

		float y0 = upX;
		float y1 = upY;
		float y2 = upZ;

		float x0 =  y1*z2 - y2*z1;
		float x1 = -y0*z2 + y2*z0;
		float x2 =  y0*z1 - y1*z0;

		y0 =  z1*x2 - z2*x1;
		y1 = -z0*x2 + z2*x0;
		y2 =  z0*x1 - z1*x0;

		mag = FastMath.sqrt(x0*x0 + x1*x1 + x2*x2);
		if (mag != 0) {
			x0 /= mag;
			x1 /= mag;
			x2 /= mag;
		}

		mag = FastMath.sqrt(y0*y0 + y1*y1 + y2*y2);
		if (mag != 0) {
			y0 /= mag;
			y1 /= mag;
			y2 /= mag;
		}

		try {
			//just does an apply to the main matrix,
			//since that'll be copied out on endCamera
//			cameraMat.set(
//					x0, x1, x2, 0,
//					y0, y1, y2, 0,
//					z0, z1, z2, 0,
//					0,  0,  0,  1);
//			cameraMat.translate(-eyeX, -eyeY, -eyeZ);
//			this.cameraMatrix.set(new float[]{
//					x0, x1, x2, -eyeX,
//					y0, y1, y2, -eyeY,
//					z0, z1, z2, -eyeZ,
//					0,  0,  0,  1	
//			});
			
			this.cameraMatrix.set(new float[]{
					x0, x1, x2, 0,
					y0, y1, y2, 0,
					z0, z1, z2, 0,
					0,  0,  0,  1	
			});
			this.cameraMatrix.mult(Matrix.getTranslationMatrix(-eyeX, -eyeY, -eyeZ), this.cameraMatrix);
			System.out.println("My cammatrix: " + this.cameraMatrix);
			
//			cameraInvMat.reset();
//			cameraInvMat.invApply(
//					x0, x1, x2, 0,
//					y0, y1, y2, 0,
//					z0, z1, z2, 0,
//					0,  0,  0,  1);
//			cameraInvMat.translate(eyeX, eyeX, eyeZ);
		this.cameraInvMatrix = this.cameraMatrix.invert(this.cameraInvMatrix);
		
//		this.cameraInvMatrix.set(new float[]{
//				x0, x1, x2, eyeX,
//				y0, y1, y2, eyeX,
//				z0, z1, z2, eyeZ,
//				0,  0,  0,  1	
//		});
		
		this.dirty = false;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
//		/*
		try {
			pa.camera(camPos.getX(), camPos.getY() , camPos.getZ(), //eyeposition
					viewCenterPos.getX(), viewCenterPos.getY(), viewCenterPos.getZ(), //view center
					xAxisUp, yAxisUp, zAxisUp); 						//which axis points up?
			
			/*
			this.cameraMatrix.set(new float[]{
					p3d.modelview.m00, p3d.modelview.m01, p3d.modelview.m02, p3d.modelview.m03,
					p3d.modelview.m10, p3d.modelview.m11, p3d.modelview.m12, p3d.modelview.m13,
					p3d.modelview.m20, p3d.modelview.m21, p3d.modelview.m22, p3d.modelview.m23,
					p3d.modelview.m30, p3d.modelview.m31, p3d.modelview.m32, p3d.modelview.m33	
			});
			
//			System.out.println("p5 camMatrix: " + this.cameraMatrix);

			this.cameraInvMatrix.set(new float[]{
					p3d.modelviewInv.m00, p3d.modelviewInv.m01, p3d.modelviewInv.m02, p3d.modelviewInv.m03,
					p3d.modelviewInv.m10, p3d.modelviewInv.m11, p3d.modelviewInv.m12, p3d.modelviewInv.m13,
					p3d.modelviewInv.m20, p3d.modelviewInv.m21, p3d.modelviewInv.m22, p3d.modelviewInv.m23,
					p3d.modelviewInv.m30, p3d.modelviewInv.m31, p3d.modelviewInv.m32, p3d.modelviewInv.m33	
			});
			 */
			
			this.cameraMatrix.set(new float[]{
					modelViewP5.m00, modelViewP5.m01, modelViewP5.m02, modelViewP5.m03,
					modelViewP5.m10, modelViewP5.m11, modelViewP5.m12, modelViewP5.m13,
					modelViewP5.m20, modelViewP5.m21, modelViewP5.m22, modelViewP5.m23,
					modelViewP5.m30, modelViewP5.m31, modelViewP5.m32, modelViewP5.m33	
			});
			
//			System.out.println("p5 camMatrix: " + this.cameraMatrix);

			this.cameraInvMatrix.set(new float[]{
					modelViewInvP5.m00, modelViewInvP5.m01, modelViewInvP5.m02, modelViewInvP5.m03,
					modelViewInvP5.m10, modelViewInvP5.m11, modelViewInvP5.m12, modelViewInvP5.m13,
					modelViewInvP5.m20, modelViewInvP5.m21, modelViewInvP5.m22, modelViewInvP5.m23,
					modelViewInvP5.m30, modelViewInvP5.m31, modelViewInvP5.m32, modelViewInvP5.m33	
			});

			this.dirty = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
//		*/
	}

	/**
	 * Gets the camera matrix.
	 * 
	 * @return the camera matrix
	 */
	public Matrix getCameraMatrix(){
		if (this.dirty){
			this.calcCameraMatrix(camPos.getX(), 			camPos.getY() , 		camPos.getZ(), //eyeposition
							viewCenterPos.getX(), 	viewCenterPos.getY(), 	viewCenterPos.getZ(), //view center
							xAxisUp, 				yAxisUp, 				zAxisUp);//which axis points up?
			this.setCachedCamMatrices();
			this.frustum.setCamDef(this.getPosition(), this.getViewCenterPos(),  xAxisUp, -yAxisUp, zAxisUp); //new Vector3D(xAxisUp, -yAxisUp, zAxisUp));
		}else{
			this.setCachedCamMatrices();
		}
		return this.cameraMatrix;
	}
	
	/**
	 * Gets the camera inv matrix.
	 * 
	 * @return the camera inv matrix
	 */
	public Matrix getCameraInvMatrix(){
		if (this.dirty){
			this.calcCameraMatrix(camPos.getX(), 			camPos.getY() , 		camPos.getZ(), //eyeposition
							viewCenterPos.getX(), 	viewCenterPos.getY(), 	viewCenterPos.getZ(), //view center
							xAxisUp, 				yAxisUp, 				zAxisUp);//which axis points up?
			this.setCachedCamMatrices();
			this.frustum.setCamDef(this.getPosition(), this.getViewCenterPos(),  xAxisUp, -yAxisUp, zAxisUp); //new Vector3D(xAxisUp, -yAxisUp, zAxisUp));
		}else{
			this.setCachedCamMatrices();
		}
		return this.cameraInvMatrix;
	}
	
	
	/**
	 * Zooms from the camera to the eye location by the given factor.
	 * 
	 * @param factor the factor
	 */
	public void zoomFactor(float factor){
		factor = 1/factor;
		Vector3D dirToCamVect = camPos.getSubtracted(viewCenterPos);
		dirToCamVect.scaleLocal(factor);
		if (dirToCamVect.length() > zoomMinDistance){
			Vector3D toCam = viewCenterPos.getAdded(dirToCamVect);
			camPos.setXYZ(toCam.getX(), toCam.getY(), toCam.getZ());
			
			this.dirty = true;
		}
	}
	
	/**
	 * changes the distance from the eye to the camera location by the given amount
	 * negative values will increase the distance, positive values will decrease it.
	 * 
	 * @param amount the amount
	 */
	public void zoomAmount(float amount){
		amount*=-1;
		//Get direction vector from eye to camera
		Vector3D dirToCamVect = camPos.getSubtracted(viewCenterPos);
		//get the length of that vector
		float mag = dirToCamVect.length();
		//normalize the vector
		dirToCamVect.normalizeLocal();
		//scale the normalized vector with the original amount + the zoom amount
		dirToCamVect.scaleLocal(mag + amount);
		
		if (dirToCamVect.length() > zoomMinDistance){
			//Get the Vector to the camera from origin
			Vector3D toCam = viewCenterPos.getAdded(dirToCamVect);
			//set the new camPos
			camPos.setXYZ(toCam.getX(), toCam.getY(), toCam.getZ());
			
			this.dirty = true;
		}
	}
	
	/**
	 * prevent zooming the cam to the center too close
	 * set the minimal distance between the cam and the center.
	 * 
	 * @param minDistance the min distance
	 */
	public void setZoomMinDistance(float minDistance){
		this.zoomMinDistance = minDistance;
	}
	
	/**
	 * Gets the zoom min distance.
	 * 
	 * @return the zoom min distance
	 */
	public float getZoomMinDistance() {
		return zoomMinDistance;
	}

//	/**
//	 * sets the position of the camera.
//	 * 
//	 * @param x the x
//	 * @param y the y
//	 * @param z the z
//	 */
//	public void setCamPosition(float x, float y, float z){
//		camPos.setXYZ(x, y, z);
//		this.dirty = true;
//	}
	
	/**
	 * Move cam.
	 * 
	 * @param directionX the direction x
	 * @param directionY the direction y
	 * @param directionZ the direction z
	 */
	public void moveCam(float directionX, float directionY, float directionZ){
		camPos.setXYZ(camPos.getX() + directionX, camPos.getY() + directionY, camPos.getZ() + directionZ);
		this.dirty = true;
	}
	
	/**
	 * sets the position of the center view point.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void setViewCenterPosition(float x, float y, float z){
		viewCenterPos.setXYZ(x, y, z);
		this.dirty = true;
	}
	
	/**
	 * moves the view center location by the given values in the given directions.
	 * 
	 * @param directionX the direction x
	 * @param directionY the direction y
	 * @param directionZ the direction z
	 */
	public void moveViewCenter(float directionX, float directionY, float directionZ){
		viewCenterPos.setXYZ(viewCenterPos.getX() + directionX, viewCenterPos.getY() + directionY, viewCenterPos.getZ() + directionZ);
		this.dirty = true;
	}
	
	
	/**
	 * moves both the view center and the camera location by the given values in the given directions.
	 * 
	 * @param directionX the direction x
	 * @param directionY the direction y
	 * @param directionZ the direction z
	 */
	public void moveCamAndViewCenter(float directionX, float directionY, float directionZ){
		moveCam(directionX, directionY, directionZ);
		moveViewCenter(directionX, directionY, directionZ);
		this.dirty = true;
	}
	
	/**
	 * Gets the cam view center distance.
	 * 
	 * @return the cam view center distance
	 */
	public float getCamViewCenterDistance(){
		return Vector3D.distance(getPosition(), getViewCenterPos());
	}
	
	/**
	 * Reset to default.
	 */
	public void resetToDefault(){
//		this.camPos = new Vector3D((float)(pa.width/2.0), (float)(pa.height/2.0), (float)(pa.height/2.0) / PApplet.tan((float)(PApplet.PI*60.0 / 360.0)));
//		this.viewCenterPos	= new Vector3D((float)(pa.width/2.0), (float)(pa.height/2.0), 0) ;
		this.camPos = new Vector3D((float)(MT4jSettings.getInstance().getWindowWidth()/2.0), (float)(MT4jSettings.getInstance().getWindowHeight()/2.0), (float)(MT4jSettings.getInstance().getWindowHeight()/2.0) / PApplet.tan((float)(PApplet.PI*60.0 / 360.0)));
		this.viewCenterPos	= new Vector3D((float)(MT4jSettings.getInstance().getWindowWidth()/2.0), (float)(MT4jSettings.getInstance().getWindowHeight()/2.0), 0) ;
		this.xAxisUp = 0;
		this.yAxisUp = 1;
		this.zAxisUp = 0;
		
		this.dirty = true;
	}
	
	/* (non-Javadoc)
	 * @see util.camera.Icamera#getPosition()
	 */
	public Vector3D getPosition() {
		return new Vector3D(camPos);
	}

	/* (non-Javadoc)
	 * @see util.camera.Icamera#setPosition(util.math.Vector3D)
	 */
	public void setPosition(Vector3D camPos) {
		this.camPos = camPos;
		this.dirty = true;
	}

	/* (non-Javadoc)
	 * @see util.camera.Icamera#getViewCenterPos()
	 */
	public Vector3D getViewCenterPos() {
		return viewCenterPos;
	}

	/* (non-Javadoc)
	 * @see util.camera.Icamera#setViewCenterPos(util.math.Vector3D)
	 */
	public void setViewCenterPos(Vector3D eyePos) {
		this.viewCenterPos = eyePos;
		this.dirty = true;
	}

	/**
	 * Gets the x axis up.
	 * 
	 * @return the x axis up
	 */
	public float getXAxisUp() {
		return xAxisUp;
	}

	/**
	 * Sets the x axis up.
	 * 
	 * @param axisUp the new x axis up
	 */
	public void setXAxisUp(float axisUp) {
		xAxisUp = axisUp;
		this.dirty = true;
	}

	/**
	 * Gets the y axis up.
	 * 
	 * @return the y axis up
	 */
	public float getYAxisUp() {
		return yAxisUp;
	}

	/**
	 * Sets the y axis up.
	 * 
	 * @param axisUp the new y axis up
	 */
	public void setYAxisUp(float axisUp) {
		yAxisUp = axisUp;
		this.dirty = true;
	}

	/**
	 * Gets the z axis up.
	 * 
	 * @return the z axis up
	 */
	public float getZAxisUp() {
		return zAxisUp;
	}

	/**
	 * Sets the z axis up.
	 * 
	 * @param axisUp the new z axis up
	 */
	public void setZAxisUp(float axisUp) {
		zAxisUp = axisUp;
		this.dirty = true;
	}


	public Frustum getFrustum() {
		return frustum;
	}
	
	
	public int isSphereInFrustum(Vector3D p, float radius){
		return this.getFrustum().isSphereInFrustum(p, radius);
	}
	
	public int isPointInFrustum(Vector3D p) {
		return this.getFrustum().isPointInFrustum(p);
	}
	
	
//	public boolean contains(IBoundingShape bounds){
//		if (bounds != null){
//			
//			
//		}else{
//			return true;
//		}
//	}

	//TODO setFrustum(float near, float far, float left, float right, float top, float bottom);
	//TODO setFrustumPerspective(float fovY, float aspect, float near, float far);
	
}
