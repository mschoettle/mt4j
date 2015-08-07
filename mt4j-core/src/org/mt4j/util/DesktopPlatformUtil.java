package org.mt4j.util;

import javax.media.opengl.glu.GLU;

import org.mt4j.AbstractMTApplication;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.opengl.IGLU;
import org.mt4j.util.opengl.JoglGLU;

import processing.core.PGraphics;
import processing.core.PMatrix3D;
import processing.opengl.PGraphicsOpenGL;

public class DesktopPlatformUtil implements IPlatformUtil {
	private final AbstractMTApplication app;
	private final JoglGLU joglGLU;
	private PGraphicsOpenGL pGraphicsOpenGL;

	public DesktopPlatformUtil(AbstractMTApplication app){
		this.app = app;
		this.joglGLU = new JoglGLU(new GLU());
		this.pGraphicsOpenGL = ((PGraphicsOpenGL)app.getPGraphics());
	}

	public PGraphics getPGraphics(){
		return app.getPGraphics();
	}
	
	public PMatrix3D getModelView() {
		return pGraphicsOpenGL.modelview;
	}
	
	public PMatrix3D getModelViewInv() {
		return pGraphicsOpenGL.modelviewInv;
	}
	
	public PMatrix3D getCamera() {
		return pGraphicsOpenGL.camera;
	}
	
	public float getCameraFOV() {
		return pGraphicsOpenGL.cameraFOV;
	}

	public float getCameraAspect() {
		return pGraphicsOpenGL.cameraAspect;
	}

	public float getCameraNear() {
		return pGraphicsOpenGL.cameraNear;
	}

	public float getCameraFar() {
		return pGraphicsOpenGL.cameraFar;
	}

//	public GL10 getGL() {
//		return ((PGraphicsOpenGL)app.getPGraphics()).gl;
//	}
//
//	public GL10 beginGL() {
//		((PGraphicsOpenGL)app.getPGraphics()).beginGL();
//		return app.getGLCommon();
//	}
//    
//    public void endGL(){
//    	((PGraphicsOpenGL)app.getPGraphics()).endGL();
//    }
	
    public int getPlatform() {
		return IPlatformUtil.DESKTOP;
	}

	@Override
	public IGLU getGLU() {
		return this.joglGLU;
	}

	@Override
	public PMatrix3D getProjection() {
//		if (MT4jSettings.getInstance().isOpenGlMode()){
//			return ((PGraphicsOpenGL)app.getPGraphics()).projection;
//		}else{
			return pGraphicsOpenGL.projection;
//		}
	}

	@Override
	public boolean isBigEndian() {
		return true;
//		return PGraphicsOpenGL.BIG_ENDIAN;
	}

	@Override
	public boolean isNPOTTextureSupported() {
		return Tools3D.supportsNonPowerOfTwoTexture(app);
	}

	@Override
	public void setModelView(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		pGraphicsOpenGL.modelview.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}

	@Override
	public void setModelViewInv(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		pGraphicsOpenGL.modelviewInv.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}

	@Override
	public void setCamera(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		pGraphicsOpenGL.camera.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}

	@Override
	public void setCameraInv(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		//FIXME not visible!
	}

	

}
