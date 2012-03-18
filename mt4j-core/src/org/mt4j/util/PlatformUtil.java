package org.mt4j.util;

import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11;
import org.mt4j.util.opengl.GL11Plus;
import org.mt4j.util.opengl.GL20;
import org.mt4j.util.opengl.IGLU;

import processing.core.PGraphics;
import processing.core.PMatrix3D;

public class PlatformUtil {
	//TODO dont make static? maybe we have different swing windows with different gl contexts?
	
	private static IPlatformUtil graphicsUtil;

	public static PGraphics getPGraphics() {
		return graphicsUtil.getPGraphics();
	}

	public static PMatrix3D getModelView() {
		return graphicsUtil.getModelView();
	}

	public static PMatrix3D getModelViewInv() {
		return graphicsUtil.getModelViewInv();
	}
	
	public static PMatrix3D getCamera() {
		return graphicsUtil.getCamera();
	}
	
	public static float getCameraFOV() {
		return graphicsUtil.getCameraFOV();
	}

	public static float getCameraAspect() {
		return graphicsUtil.getCameraAspect();
	}

	public static float getCameraNear() {
		return graphicsUtil.getCameraNear();
	}

	public static float getCameraFar() {
		return graphicsUtil.getCameraFar();
	}
	
	public static GL10 getGL(){
		return graphicsUtil.getGL();
	}
	
	public static GL11 getGL11(){
		return graphicsUtil.getGL11();
	}
	
	public static GL20 getGL20(){
		return graphicsUtil.getGL20();
	}

	public static GL10 beginGL() {
		return graphicsUtil.beginGL();
	}

	public static void endGL() {
		graphicsUtil.endGL();
	}
	
	public static void setGraphicsUtilProvider(IPlatformUtil graphicsUtilitiy){
		graphicsUtil = graphicsUtilitiy;
	}
	
	public static boolean isDesktop(){
		return graphicsUtil.getPlatform() == IPlatformUtil.DESKTOP;
	}
	
	public static boolean isAndroid(){
		return graphicsUtil.getPlatform() == IPlatformUtil.ANDROID;
	}
	
	public static boolean isNPOTTextureSupported(){
		return graphicsUtil.isNPOTTextureSupported();
	}

		
	public static GL11Plus getGL11Plus() {
		return graphicsUtil.getGL11Plus();
	}
	
	public static IGLU getGLU(){
		return graphicsUtil.getGLU();
	}

	public static PMatrix3D getProjection() {
		return graphicsUtil.getProjection();
	}
	
	public static boolean isBigEndian(){
		return graphicsUtil.isBigEndian();
	}

	
	public static void setModelView(
			float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33
     ){
		graphicsUtil.setModelView(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}
	
	public static void setModelViewInv(
			float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33
	){
		graphicsUtil.setModelViewInv(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}
			
	
	public static void setCamera(
			float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33
	){
		graphicsUtil.setCamera(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}
	
	public static void setCameraInv(
			float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33
	){
		graphicsUtil.setCameraInv(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}



}
