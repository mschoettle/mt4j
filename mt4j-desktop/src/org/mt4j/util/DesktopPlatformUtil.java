package org.mt4j.util;

import javax.media.opengl.glu.GLU;

import org.mt4j.AbstractMTApplication;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11;
import org.mt4j.util.opengl.GL11Plus;
import org.mt4j.util.opengl.GL20;
import org.mt4j.util.opengl.IGLU;
import org.mt4j.util.opengl.JoglGLU;

import processing.core.PGraphics;
import processing.core.PGraphics3D;
import processing.core.PMatrix3D;
import processing.opengl.PGraphicsOpenGL;

public class DesktopPlatformUtil implements IPlatformUtil {
	private final AbstractMTApplication app;
	private final JoglGLU joglGLU;
	private PGraphics3D pg3d;

	public DesktopPlatformUtil(AbstractMTApplication app){
		this.app = app;
		this.joglGLU = new JoglGLU(new GLU());
		this.pg3d = ((PGraphics3D)app.getPGraphics());
	}

	public PGraphics getPGraphics(){
		return app.getPGraphics();
	}
	
	public PMatrix3D getModelView() {
		return pg3d.modelview;
	}
	
	public PMatrix3D getModelViewInv() {
		return pg3d.modelviewInv;
	}
	
	public PMatrix3D getCamera() {
		return pg3d.camera;
	}
	
	public float getCameraFOV() {
		return pg3d.cameraFOV;
	}

	public float getCameraAspect() {
		return pg3d.cameraAspect;
	}

	public float getCameraNear() {
		return pg3d.cameraNear;
	}

	public float getCameraFar() {
		return pg3d.cameraFar;
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
	
	public GL10 getGL() {
		return app.getGL10(); //FIXME DOES THE CAST TO kronos.GL10 work!?? -> prolly not
	}
	
	public GL11 getGL11() {
		return app.getGL11(); 
	}

	public GL20 getGL20() {
		return app.getGL20(); 
	}

	public GL10 beginGL() {
		((PGraphicsOpenGL)app.getPGraphics()).beginGL();
//		app.beginGL();
		return app.getGL10();
	}
    
    public void endGL(){
    	((PGraphicsOpenGL)app.getPGraphics()).endGL();
//    	app.endGL();
    }
    
    
    public int getPlatform() {
		return IPlatformUtil.DESKTOP;
	}

	public GL11Plus getGL11Plus() {
		return app.getGL11Plus();
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
			return pg3d.projection;
//		}
	}

	@Override
	public boolean isBigEndian() {
		return PGraphicsOpenGL.BIG_ENDIAN;
	}

	@Override
	public boolean isNPOTTextureSupported() {
		return Tools3D.supportsNonPowerOfTwoTexture(app);
	}

	@Override
	public void setModelView(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		pg3d.modelview.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}

	@Override
	public void setModelViewInv(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		pg3d.modelviewInv.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}

	@Override
	public void setCamera(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		pg3d.camera.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}

	@Override
	public void setCameraInv(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		//FIXME not visible!
	}

	

}
