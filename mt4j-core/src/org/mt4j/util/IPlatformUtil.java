package org.mt4j.util;

import org.mt4j.util.opengl.IGLU;

import processing.core.PGraphics;
import processing.core.PMatrix3D;

public interface IPlatformUtil {
	
public PGraphics getPGraphics();
	public static final int DESKTOP = 0;
	public static final int ANDROID = 1;
	
	public PMatrix3D getModelView();
	
	public PMatrix3D getModelViewInv();
	
	public PMatrix3D getCamera();
	
	public float getCameraFOV();
	
	public float getCameraAspect();
	
	public float getCameraNear();
	
	public float getCameraFar();
	
	public int getPlatform();

	public IGLU getGLU();

	public PMatrix3D getProjection();

	public boolean isBigEndian();

	public boolean isNPOTTextureSupported();
	
	
	
	
	public void setModelView(
			float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33
    );
	
	public void setModelViewInv(
			float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33
    );
			
	
	public void setCamera(
			float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33
    );
	
	public void setCameraInv(
			float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33
    );

}
