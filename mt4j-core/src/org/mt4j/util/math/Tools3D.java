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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.StringTokenizer;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.camera.IFrustum;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11Plus;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.TEXTURE_TARGET;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix3D;


/**
 * Class containing mostly static convenience utility methods.
 * 
 * @author Christopher Ruff
 */
public class Tools3D {
	//Declared here and static so it wont have to be initialize at every call to unproject
	/** The fb. */
	private static FloatBuffer fb = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	
	/** The fb un. */
	private static FloatBuffer fbUn = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	
//	/** The model. */
//	private static DoubleBuffer model;
//	
//	/** The proj. */
//	private static DoubleBuffer proj;
	
	/** The model. */
	private static FloatBuffer model;
	
	/** The proj. */
	private static FloatBuffer proj;
	
	/** The view. */
	private static IntBuffer view;
	
	/** The win pos. */
	private static DoubleBuffer winPos;
	
	static{
//		model 	= DoubleBuffer.allocate(16);
//		proj 	= DoubleBuffer.allocate(16);
		model 	= FloatBuffer.allocate(16);
		proj 	= FloatBuffer.allocate(16);
		view 	= IntBuffer.allocate(4);
		winPos 	= DoubleBuffer.allocate(3);
	}
	
	
	/**
	 * Unprojects screen coordinates from 2D into 3D world space and returns a point that
	 * can be used to construct a ray form the camera to that point and check
	 * for intersections with objects.
	 * <p><b>NOTE</b>: if using openGL mode, the openGL context has to be valid at the time of calling this method.
	 * 
	 * @param applet the applet
	 * @param camera the camera
	 * @param screenX the screen x
	 * @param screenY the screen y
	 * 
	 * @return the vector3d
	 */
	public static Vector3D unprojectScreenCoords(PApplet applet, Icamera camera, float screenX, float screenY ){
		Vector3D ret;
		applet.pushMatrix();
		camera.update();
		ret = Tools3D.unprojectScreenCoords(applet, screenX, screenY);
		applet.popMatrix();
		return ret;
	}
	
	
	
	
	private static float[] result = new float[4];
	private static float[] factor = new float[4];
	private static PMatrix3D modelViewTmp = new PMatrix3D();
	private static PMatrix3D projectionTmp = new PMatrix3D();
	
	/**
	 * Unprojects screen coordinates from 2D into 3D world space and returns a point that
	 * can be used to construct a ray form the camera to that point and check
	 * for intersections with objects.
	 * <p><b>NOTE</b>: if using openGL mode, the openGL context has to be valid at the time of calling this method.
	 * 
	 * @param applet processings PApplet object
	 * @param screenX x coordinate on the screen
	 * @param screenY y coordinate on the screen
	 * 
	 * @return a point that lies on the line from the screen coordinates
	 * to the 3d world coordinates
	 */
	public static Vector3D unprojectScreenCoords(PApplet applet, float screenX, float screenY ){ //FIXME MAKE PRIVATE AGAIN! 
		Vector3D returnVect = new Vector3D(-999,-999,-999); //null?
		
//		MT4jSettings.getInstance().setRendererMode(MT4jSettings.P3D_MODE);
		
//		switch (MT4jSettings.getInstance().getRendererMode()) {
//		case MT4jSettings.OPENGL_MODE:
//			int viewport[] = new int[4];
//			double[] proj  = new double[16];
//			double[] model = new double[16];
//			double[] mousePosArr = new double[4];
//			
//			try{
//			PGraphicsOpenGL pgl = ((PGraphicsOpenGL)applet.g); 
//			GL gl = pgl.beginGL();  
//			GLU glu = pgl.glu;
//			
//				  gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
//				  gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, proj, 0);
//				  gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model, 0);
//				  
//				  /*
//				  System.out.println("OpenGL ProjectionMatrix: ");
//				  for (int i = 0; i < proj.length; i++) {
//						double p = proj[i];
//						System.out.print(p + ", ");
//						//if (i%4 == 0 && i==3)
//						if (i==3 || i== 7 || i== 11 || i==15) {
//							System.out.println();
//						}
//					  }
//				  */
//				  
//				  /*
//				  System.out.println("OpenGL ModelviewMatrix: ");
//				  for (int i = 0; i < model.length; i++) {
//						double p = model[i];
//						System.out.print(p + ", ");
//						//if (i%4 == 0 && i==3)
//						if (i==3 || i== 7 || i== 11 || i==15) {
//							System.out.println();
//						}
//					  }
//				  System.out.println();
//				  System.out.println("\n");
//				  */
//				  
//				  /*
//				  fbUn.clear();
//				  gl.glReadPixels((int)screenX, applet.height - (int)screenY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, fbUn);
//				  fbUn.rewind();
//				  glu.gluUnProject((double)screenX, applet.height - (double)screenY, (double)fbUn.get(0), model, 0, proj, 0, viewport, 0, mousePosArr, 0);
//				  */
//				  
//				  //FIXME test not using glReadpixel to get the depth at the location
//				  //instead we have to build a ray with the result, from the camera location going through the resulst and check for hits ourselves
//				  glu.gluUnProject((double)screenX, applet.height - (double)screenY, 0, model, 0, proj, 0, viewport, 0, mousePosArr, 0);
//			  pgl.endGL();
//			  
//			  returnVect = new Vector3D((float)mousePosArr[0], (float)mousePosArr[1], (float)mousePosArr[2]);
//			}catch(Exception e){
//				e.printStackTrace();
//				//System.out.println("Use method getWorldForScreenCoords only when drawing with openGL! And dont put negative screen values in!");
//			}
//			break;
//		case MT4jSettings.P3D_MODE:
//			/*!
			try{
				float winZ = 1; //or read from depth buffer at that pixel! (but not available in Ogl ES)
				
//				modelViewTmp.set(applet.g.getMatrix()); //FIXME creates a new PMatrix3D everytime :(
				modelViewTmp.set(PlatformUtil.getModelView());
				
//				PMatrix3D projectionM 	= new PMatrix3D(((PGraphics3D)applet.g).projection);
				projectionTmp.set(PlatformUtil.getProjection());
				
				//-> in dekstop version glScale(1,-1,1) is done every frame because in (Desktop) opengl
				// 0,0 is on the down left corner instead of upper left
				if (PlatformUtil.isAndroid()){
					screenY = MT4jSettings.getInstance().getWindowHeight() - screenY;
				}
				
				projectionTmp.apply(modelViewTmp);
				projectionTmp.invert(); //Expensive!
				
//				factor[0] = ((2 * screenX)  / applet.width)  -1;
//				factor[1] = ((2 * screenY)  / applet.height) -1;
				factor[0] = ((2 * screenX)  / MT4jSettings.getInstance().getWindowWidth())  -1;
				factor[1] = ((2 * screenY)  / MT4jSettings.getInstance().getWindowHeight()) -1;
				factor[2] = (2 * winZ) -1;
				factor[3] = 1;
				
				//Matrix mit Vector multiplizieren
				projectionTmp.mult(factor, result);
				
				//System.out.println("\nResult2: ");
				result[0] /= result[3];
				result[1] /= result[3];
				result[2] /= result[3];
				result[3] /= result[3];
				
				//aus Result Vector3D machen
				returnVect = new Vector3D(result[0],result[1],result[2]);
			}catch(Exception e){
				e.printStackTrace();
			}
//			break;
////			*/
//		default:
//			break;
//		} 
//		System.out.println("unprojected: " + returnVect);
		return returnVect;
	} 
	
	

	private static Vector3D unprojectScreenCoords(PApplet applet, float winX, float winY, float winZ){
		PMatrix3D modelView 	= new PMatrix3D(applet.g.getMatrix());
		PMatrix3D projectionM 	= new PMatrix3D(PlatformUtil.getProjection());

		if (PlatformUtil.isAndroid()){
			winY = MT4jSettings.getInstance().getWindowHeight() - winY;
		}

		projectionM.apply(modelView);
		projectionM.invert();

		float[] result = new float[4];
		float[] factor = new float[]{  ((2 * winX)  / MT4jSettings.getInstance().getWindowWidth())  -1,
				((2 * winY)  / MT4jSettings.getInstance().getWindowHeight()) -1, //screenH - y?
				(2 * winZ) -1 ,
				1,};
		projectionM.mult(factor, result);

		//System.out.println("\nResult2: ");
		result[0] /= result[3];
		result[1] /= result[3];
		result[2] /= result[3];
		result[3] /= result[3];
		return new Vector3D(result[0],result[1],result[2]);
	}


	/**
	 * Gets the ray to pick this component.
	 *
	 * @param applet the applet
	 * @param component the component
	 * @param cursor the cursor
	 * @return the camera pick ray
	 */
	public static Ray getCameraPickRay(PApplet applet, IMTComponent3D component, InputCursor cursor){
		return Tools3D.getCameraPickRay(applet, component.getViewingCamera(), cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
	}
	
	/**
	 * Constructs a picking ray from the components viewing camera position
	 * through the specified screen coordinates.
	 * The viewing camera of the object may not be null!
	 * <p><b>NOTE</b>: the openGL context has to be valid at the time of calling this method.
	 * 
	 * @param applet the applet
	 * @param component the component
	 * @param screenX the screen x
	 * @param screenY the screen y
	 * 
	 * @return the pick ray
	 */
	public static Ray getCameraPickRay(PApplet applet, IMTComponent3D component, float screenX, float screenY ){
		return Tools3D.getCameraPickRay(applet, component.getViewingCamera(), screenX, screenY);
	}
	
	/**
	 * Constructs a picking ray from the components viewing camera position
	 * through the specified screen coordinates.
	 * <p><b>NOTE</b>: the openGL context has to be valid at the time of calling this method.
	 * 
	 * @param applet the applet
	 * @param screenX the screen x
	 * @param screenY the screen y
	 * @param camera the camera
	 * 
	 * @return the pick ray
	 */
	public static Ray getCameraPickRay(PApplet applet, Icamera camera, float screenX, float screenY ){
		Vector3D rayStartPoint 		= camera.getPosition();
		Vector3D newPointInRayDir 	=  Tools3D.unprojectScreenCoords(applet, camera, screenX, screenY);
		return new Ray(rayStartPoint, newPointInRayDir);
		/*
//		Vector3D near = unprojectNew( applet,  screenX,  screenY,  0);
//		Vector3D far = unprojectNew( applet,  screenX,  screenY,  1);
//		System.out.println("Near: " + near);
//		System.out.println("Far: " + far);
//		return new Ray(near, far);
		 */
	}
	
	

	
	
//	/**
//	 * Projects the given point to screenspace.
//	 * <br>Shows where on the screen the point in 3d-Space will appear according
//	 * to the current viewport, model and projection matrices.
//	 * <p><b>NOTE</b>: the openGL context has to be valid at the time of calling this method.
//	 * 
//	 * @param gl the gl
//	 * @param glu the glu
//	 * @param point the point to project to the screen
//	 * 
//	 * @return the vector3 d
//	 */
//	public static Vector3D projectGL(GL10 gl, GLU glu, Vector3D point){
//		return projectGL(gl, glu, point, null);
//	}
//	
//	/**
//	 * Projects the given point to screenspace.
//	 * <br>Shows where on the screen the point in 3d-Space will appear according
//	 * to the current viewport, model and projection matrices.
//	 * <br><strong>Note</strong>: this method has to be called between a call to <code>processingApplet.beginGL()</code>
//	 * and <code>processingApplet.endGL()</code>
//	 * <p><b>NOTE</b>: the openGL context has to be valid at the time of calling this method.
//	 * 
//	 * @param gl the gl
//	 * @param glu the glu
//	 * @param point the point
//	 * @param store the store - vector to store the result in or null to get a new vector
//	 * 
//	 * @return the vector3 d
//	 */
//	public static Vector3D projectGL(GL11 gl, GLU glu, Vector3D point, Vector3D store){
//		if (store == null){
//			store = new Vector3D();
//		}
//		
//		model.clear();
////		gl.glGetDoublev(GL11.GL_MODELVIEW_MATRIX, model);
//		gl.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, model);
//		
//		proj.clear();
////		gl.glGetDoublev(GL11.GL_PROJECTION_MATRIX, proj);
//		gl.glGetFloatv(GL11.GL_PROJECTION_MATRIX, proj);
//		
//		view.clear();
//		gl.glGetIntegerv(GL11.GL_VIEWPORT, view);
//		float viewPortHeight = (float)view.get(3);
//		
//		winPos.clear();
//		glu.gluProject(point.x, point.y, point.z, model, proj, view, winPos);
//		
//		winPos.rewind();
//		float x = (float) winPos.get();
//		float y = (float) winPos.get();
//		y = viewPortHeight - y;			// Subtract The Current Y Coordinate From The Screen Height.
//		
//		store.setXYZ(x, y, 0);
//		return store;
////		return new Vector3D(x, y, 0);
//	}
	
	
	/**
	 * Projects the given 3D point to screenspace.
	 * <br>Shows where on the screen the point in 3d-Space will appear according
	 * to the supplied camera, viewport, and projection matrices.
	 * The modelview is temporarily changed to match the supplied camera matrix.
	 * 
	 * @param applet the applet
	 * @param cam the cam
	 * @param point the point
	 * 
	 * @return the vector3 d
	 */
	public static Vector3D project(PApplet applet, Icamera cam, Vector3D point){
		Vector3D ret;
		applet.pushMatrix();
		cam.update();
		ret = Tools3D.project(applet, point);	
		applet.popMatrix();
		return ret;
	}
	
	
	/**
	 * Projects the given point to screenspace. Uses the current modelview, and projection matrices - so update
	 * them accordingly before calling!
	 * <br>Shows where on the screen the point in 3d-Space will appear according
	 * to the current viewport, model and projection matrices.
	 * <p><b>NOTE</b>: if using openGL mode, the openGL context has to be valid at the time of calling this method.
	 * 
	 * @param applet the applet
	 * @param point the point
	 * 
	 * @return a new projected vector3d
	 */
	public static Vector3D project(PApplet applet, Vector3D point){
//		switch (MT4jSettings.getInstance().getRendererMode()) {
//		case MT4jSettings.OPENGL_MODE:
//			try{ 
//				PGraphicsOpenGL pgl = ((PGraphicsOpenGL)applet.g); 
////				GL gl 	= pgl.beginGL();
//				GL10 gl = GraphicsUtil.beginGL();
//				GLU glu = pgl.glu;
//				Vector3D returnVect = projectGL(gl, glu, point);
////				pgl.endGL();
//				GraphicsUtil.endGL();
//				return returnVect;
//			}catch(Exception e){
//				e.printStackTrace();
//				//System.out.println("Use method getWorldForScreenCoords only when drawing with openGL! And dont put negative screen values in!");
//			}
//			break;
//		case MT4jSettings.P3D_MODE:
////			/*!
//			try{
//				float x = applet.screenX(point.x, point.y, point.z);
//				float y = applet.screenY(point.x, point.y, point.z);
//				float z = applet.screenZ(point.x, point.y, point.z);
//				return new Vector3D(x,y,z);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			break;
////			*/
//		default:
//			return new Vector3D(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
//		} 
		
		try{
			float x = applet.screenX(point.x, point.y, point.z);
			
			float y = 0;
			if (PlatformUtil.isAndroid()){ //because android opengl isnt inverted..?
				y = applet.screenY(point.x, (MT4jSettings.getInstance().getWindowHeight() - point.y) * -1, point.z); //	 applet.height - screenY
			}else{
				y = applet.screenY(point.x, point.y, point.z);
			}
			
//			float y = applet.screenY(point.x, point.y, point.z);
//			float y = applet.screenY(point.x, -1 * point.y, point.z); //	y = -1 * y;
//			float y = applet.screenY(point.x, (applet.height - point.y) * -1, point.z); //	 applet.height - screenY
//			y = applet.height - y;
			
			//we have to use applet.height - point.y in android, and in the screenY method dont use *-1 
			
//			if (GraphicsUtil.isAndroid()){
//				screenY = applet.height - screenY;
//			}
			
			float z = applet.screenZ(point.x, point.y, point.z);
			
			return new Vector3D(x,y,z);
		}catch(Exception e){
			e.printStackTrace();
		}
		return new Vector3D(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
	}
	
	/**
	 * Start drawing in on TOP of everything drawn previously,
	 * also resets the camera, so that something drawn at 0,0,0
	 * will be drawn at the top left corner of the screen regardless
	 * of the camera used
	 * 
	 * You could say this allows you to draw directly on the screen, and
	 * on top of everything else. (at the near clipping plane?)
	 * 
	 * NOTE: you have to CALL endDrawOnTopStayOnScreen() if finished!
	 * 
	 * @param pa the pa
	 */
	public static void beginDrawOnTopStayOnScreen(PApplet pa){
		switch (MT4jSettings.getInstance().getRendererMode()) {
		case MT4jSettings.OPENGL_MODE:
//			GL gl = ((PGraphicsOpenGL)pa.g).gl; 
			GL10 gl = PlatformUtil.getGL(); 
			gl.glDepthFunc(GL10.GL_ALWAYS); //turn off Z buffering
			//reset to the default camera
			pa.camera(); 
			break;
		case MT4jSettings.P3D_MODE:
			//FIXME how to handle platform independent?
//			for(int i=0;i<((PGraphics3D)pa.g).zbuffer.length;i++){
//			  ((PGraphics3D)pa.g).zbuffer[i]=Float.MAX_VALUE;
//			}
			pa.camera();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Stop drawing in 2D after calling begin2D().
	 * 
	 * @param pa the pa
	 * @param camera the camera
	 */
	public static void endDrawOnTopStayOnScreen(PApplet pa, Icamera camera){
		switch (MT4jSettings.getInstance().getRendererMode()) {
		case MT4jSettings.OPENGL_MODE:
//			GL gl = ((PGraphicsOpenGL)pa.g).gl;
			GL10 gl = PlatformUtil.getGL(); 
			gl.glDepthFunc(GL10.GL_LEQUAL); //This is used by standart processing..
			//Change camera back to current 3d camera
			camera.update();
			break;
		case MT4jSettings.P3D_MODE:
			camera.update();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Allows to draw ontop of everything, regardless of the values in the z-buffer.
	 * To stop doing that, call <code>endDrawOnTop(PApplet pa)</code> .
	 * 
	 * @param g the g
	 */
	public static void disableDepthBuffer(PGraphics g){ 
		
		switch (MT4jSettings.getInstance().getRendererMode()) {
		case MT4jSettings.OPENGL_MODE:
//			GL gl = ((PGraphicsOpenGL)pa.g).gl;
//			GL gl = ((PGraphicsOpenGL)g).gl;
			GL10 gl = PlatformUtil.getGL();
			GL11Plus plus = PlatformUtil.getGL11Plus();
			if (plus != null){
				plus.glPushAttrib(GL10.GL_DEPTH_BUFFER_BIT);//FIXME TEST	
			}
//			gl.glPushAttrib(GL10.GL_DEPTH_BUFFER_BIT);//FIXME TEST
			gl.glDepthFunc(GL10.GL_ALWAYS); //turn off Z buffering
			break;
		case MT4jSettings.P3D_MODE:
			//FIXME how to handle platform independent?
//			for(int i=0;i<((PGraphics3D)g).zbuffer.length;i++){
//				  ((PGraphics3D)g).zbuffer[i]=Float.MAX_VALUE;
//				}
			break;
		default:
			break;
		}
	}
	
	/**
	 * End draw on top.
	 * 
	 * @param g the g
	 */
	public static void restoreDepthBuffer(PGraphics g){ 
		switch (MT4jSettings.getInstance().getRendererMode()) {
		case MT4jSettings.OPENGL_MODE:
//			GL gl = ((PGraphicsOpenGL)g).gl;
			GL11Plus plus = PlatformUtil.getGL11Plus();
			if (plus != null){
				plus.glPopAttrib(); 
			}
//			gl.glDepthFunc(GL.GL_LEQUAL); //This is used by standart processing..
			//FIXME TEST
//			gl.glPopAttrib(); 
			break;
		case MT4jSettings.P3D_MODE:
			break;
		default:
			break;
		}
	}
	
	


	//////////////////////////////////////////////////////////
	//  OPENGL STUFF										//
	//////////////////////////////////////////////////////////
	
	
	
	/**
	 * Prints some available openGL extensions to the console.
	 * <p><b>NOTE</b>: the openGL context has to be valid at the time of calling this method.
	 * 
	 * @param pa the pa
	 */
	public static void printGLExtensions(PApplet pa){
		if (!MT4jSettings.getInstance().isOpenGlMode())
			return;
//		GL gl =((PGraphicsOpenGL)pa.g).beginGL();
		GL10 gl = PlatformUtil.getGL();
		String ext = gl.glGetString(GL10.GL_EXTENSIONS);
		StringTokenizer tok = new StringTokenizer( ext, " " );
		while (tok.hasMoreTokens()) {
			System.out.println(tok.nextToken());
		}
		 int[] redBits 		= new int[1];
         int[] greenBits 	= new int[1];
         int[] blueBits 	= new int[1];
         int[] alphaBits 	= new int[1];
         int[] stencilBits 	= new int[1];
         int[] depthBits 	= new int[1];
         gl.glGetIntegerv(GL10.GL_RED_BITS, redBits,0);
         gl.glGetIntegerv(GL10.GL_GREEN_BITS, greenBits,0);
         gl.glGetIntegerv(GL10.GL_BLUE_BITS, blueBits,0);
         gl.glGetIntegerv(GL10.GL_ALPHA_BITS, alphaBits,0);
         gl.glGetIntegerv(GL10.GL_STENCIL_BITS, stencilBits,0);
         gl.glGetIntegerv(GL10.GL_DEPTH_BITS, depthBits,0);
		System.out.println("Red bits: " + redBits[0]);
		System.out.println("Green bits: " + greenBits[0]);
		System.out.println("Blue bits: " + blueBits[0]);
		System.out.println("Alpha bits: " + blueBits[0]);
		System.out.println("Depth Buffer bits: " + depthBits[0]);
		System.out.println("Stencil Buffer bits: " + stencilBits[0]);
//		((PGraphicsOpenGL)pa.g).endGL();
		PlatformUtil.endGL();
	}
	
		
		/**
		 * Check for gl error.
		 * 
		 * @param gl the gl
		 */
		public static int getGLError(GL10 gl){
			int error = gl.glGetError();
			if (error != GL10.GL_NO_ERROR){
				System.out.println("GL Error: " + error);
			}else{
	//			System.out.println("No gl error.");
			}
			return error;
		}


		/**
		 * Gets the openGL context.
		 * <br>NOTE: If you want to invoke any opengl drawing commands (or other commands influencing or depending on the current modelview matrix)
		 * you have to call GL <code>Tools3D.beginGL(PApplet pa)</code> instead!
		 * <br>NOTE: the openGL context is only valid and current when the rendering thread is the current thread.
		 * <br>
		 * This only gets the opengl context if started in opengl mode using the opengl renderer.
		 * 
		 * @param pa the pa
		 * 
		 * @return the gL
		 */
		public static GL10 getGL(PApplet pa){
			return PlatformUtil.getGL();
		}
		
		
		public static GL10 getGL(PGraphics g){
			return PlatformUtil.getGL();
		}

	
	/**
	 * Begin gl.
	 * 
	 * @param pa the pa
	 * @return the gL
	 */
	public static GL10 beginGL(PApplet pa){
		return PlatformUtil.beginGL();
	}
	
	/**
	 * Begin gl.
	 *
	 * @param g the g
	 * @return the gL
	 */
	public static GL10 beginGL(PGraphics g){
		return PlatformUtil.beginGL();
	}

	
	/**
	 * End gl.
	 * 
	 * @param pa the pa
	 */
	public static void endGL(PApplet pa){
		PlatformUtil.endGL();
	}
	
	/**
	 * End gl.
	 *
	 * @param g the g
	 */
	public static void endGL(PGraphics g){
		PlatformUtil.endGL();
	}



	/**
	 * Checks whether the given extension is supported by the current opengl context.
	 * <p><b>NOTE</b>: the openGL context has to be valid at the time of calling this method.
	 * 
	 * @param pa the pa
	 * @param extensionName the extension name
	 * 
	 * @return true, if checks if is gl extension supported
	 */
	public static boolean isGLExtensionSupported(PApplet pa, String extensionName){
		if (!MT4jSettings.getInstance().isOpenGlMode())
			return false;
		
//		GL gl =((PGraphicsOpenGL)pa.g).gl;
		GL11Plus gl = PlatformUtil.getGL11Plus();
		if (gl != null){
			boolean avail = gl.isExtensionAvailable(extensionName);
			/*
			String ext = gl.glGetString(GL.GL_EXTENSIONS);
			*/
			return(avail);
		}else{
			System.err.println("GL profile doesent support 'isExtensionAvailable' command.");
			return false;
		}
	}
	
	/**
	 * Checks whether non power of two texture dimensions are natively supported
	 * by the gfx hardware.
	 * 
	 * @param pa the pa
	 * 
	 * @return true, if supports non power of two texture
	 */
	public static boolean supportsNonPowerOfTwoTexture(PApplet pa){
		boolean supports = false;
		if (	Tools3D.isGLExtensionSupported(pa, "GL_TEXTURE_RECTANGLE_ARB")
			|| 	Tools3D.isGLExtensionSupported(pa, "GL_ARB_texture_non_power_of_two")
			|| 	Tools3D.isGLExtensionSupported(pa, "GL_ARB_texture_rectangle")
			|| 	Tools3D.isGLExtensionSupported(pa, "GL_NV_texture_rectangle")
			|| 	Tools3D.isGLExtensionSupported(pa, "GL_TEXTURE_RECTANGLE_EXT")
			|| 	Tools3D.isGLExtensionSupported(pa, "GL_EXT_texture_rectangle")
		){
			supports = true;
		}
			return supports;
	}


	/**
	 * Sets the opengl vertical syncing on or off.
	 * 
	 * @param pa the pa
	 * @param on the on
	 */
	public static void setVSyncing(PApplet pa, boolean on){
		if (MT4jSettings.getInstance().getRendererMode() == MT4jSettings.OPENGL_MODE){
//			GL gl = getGL(pa);
			GL11Plus gl = PlatformUtil.getGL11Plus();
			if (on){
				gl.setSwapInterval(1);
			}else{
				gl.setSwapInterval(0);
			}
		}
	}


	public static void setLineSmoothEnabled(GL10 gl, boolean enable){
	//    	/*
	    	//DO this if we use multisampling and enable line_smooth from the beginning 
	    	//and use multisampling -> we turn off multisampling then before using line_smooth for best restult
	    	if (enable){
	    		if (MT4jSettings.getInstance().isMultiSampling()){
					gl.glDisable(GL10.GL_MULTISAMPLE);
				}
	    		//TODO Eventually even dont do that since enabled form the beginning!
	    		gl.glEnable(GL10.GL_LINE_SMOOTH); 
	    	}else{
	    		if (MT4jSettings.getInstance().isMultiSampling()){
					gl.glEnable(GL10.GL_MULTISAMPLE);
				}
	//    		gl.glDisable(GL.GL_LINE_SMOOTH); //Actually never disable line smooth
	    	}
	//    	*/
	    	
	    	//DO nothing if we use Multisampling but disable line_smooth from the beginning
	    	// -> do all anti aliasing only through multisampling!
	    	//
	    	/*
	    	if (enable){
	    		if (MT4jSettings.getInstance().isMultiSampling()){
					gl.glDisable(GL.GL_MULTISAMPLE);
				}
	    		//TODO Eventually even dont do that since enabled form the beginning!
	    		gl.glEnable(GL.GL_LINE_SMOOTH); 
	    	}else{
	    		if (MT4jSettings.getInstance().isMultiSampling()){
					gl.glEnable(GL.GL_MULTISAMPLE);
				}
	//    		gl.glDisable(GL.GL_LINE_SMOOTH); //Actually never disable line smooth
	    	}
	    	*/
	    }


	
	
	
	//////////////////////////////////////////////////////
	// Generate Display Lists and get their IDs			//
	//////////////////////////////////////////////////////
	/**
	 * Creates 2 displaylists for drawing static geometry very fast.
	 * Returns the IDs (names) of the display lists generated with the given info.
	 * 
	 * @param pa the pa
	 * @param geometryInfo the geometry info
	 * @param useTexture the use texture
	 * @param texture the texture
	 * @param styleInfo the style info
	 * 
	 * @return the int[]
	 * 
	 * Returns the IDs (names) of the display lists generated with the given info.
	 */
	public static int[] generateDisplayLists(PApplet pa, GeometryInfo geometryInfo, boolean useTexture, PImage texture, StyleInfo styleInfo){
		return generateDisplayLists(pa, styleInfo.getFillDrawMode(), geometryInfo, useTexture, texture, styleInfo.isDrawSmooth(), styleInfo.getStrokeWeight());
	}
	
	
	/**
	 * Returns the IDs (names) of the display lists generated with the given info.
	 * 
	 * @param pa the pa
	 * @param fillDrawMode the fill draw mode
	 * @param geometryInfo the geometry info
	 * @param useTexture the use texture
	 * @param texture the texture
	 * @param drawSmooth the draw smooth
	 * @param strokeWeight the stroke weight
	 * 
	 * @return int[2] array where [0] is the list of the fill
	 * and [1] the list of the outline drawing list
	 */
	public static int[] generateDisplayLists(PApplet pa, int fillDrawMode, GeometryInfo geometryInfo,
									boolean useTexture, PImage texture, boolean drawSmooth, float strokeWeight
	){
		FloatBuffer tbuff 			= geometryInfo.getTexBuff();
		FloatBuffer vertBuff 		= geometryInfo.getVertBuff();
		FloatBuffer colorBuff 		= geometryInfo.getColorBuff();
		FloatBuffer strokeColBuff 	= geometryInfo.getStrokeColBuff();
		Buffer indexBuff 		    = geometryInfo.getIndexBuff(); //null if not indexed
		
		GL10 gl = PlatformUtil.beginGL();
		GL11Plus gl11Plus = PlatformUtil.getGL11Plus();
		
		//Generate new list IDs
		int[] returnVal = new int[2];
		int listIDFill = gl11Plus.glGenLists(1);
		if (listIDFill == 0){
			System.err.println("Failed to create fill display list");
			returnVal[0] = -1;
			returnVal[1] = -1;
			return returnVal;
		}
		int listIDOutline = gl11Plus.glGenLists(1);
		if (listIDOutline == 0){
			System.err.println("Failed to create stroke display list");
			returnVal[0] = -1;
			returnVal[1] = -1;
			return returnVal;
		}
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuff);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuff);
		
		//Default target
		int textureTarget = GL10.GL_TEXTURE_2D;
		
		/////// DO FILL LIST/////////////////////////////////
		
		/////////
		boolean textureDrawn = false;
		int usedTextureID = -1;
		if (useTexture
			&& texture != null 
			&& texture instanceof GLTexture) //Bad for performance?
		{
			GLTexture tex = (GLTexture)texture;
			textureTarget = tex.getTextureTarget();
			
			//tells opengl which texture to reference in following calls from now on!
			//the first parameter is eigher GL.GL_TEXTURE_2D or ..1D
			gl.glEnable(textureTarget);
			usedTextureID = tex.getTextureID();
			gl.glBindTexture(textureTarget, tex.getTextureID());
			
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tbuff);
			textureDrawn = true;
		}
		
		// Normals
		if (geometryInfo.isContainsNormals()){
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, geometryInfo.getNormalsBuff());
		}
		
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuff);
		
		// START recording display list and DRAW////////////////////
		gl11Plus.glNewList(listIDFill, GL11Plus.GL_COMPILE);
			if (textureDrawn){
				gl.glEnable(textureTarget); //muss texture in der liste gebinded werden? anscheinend JA!
				gl.glBindTexture(textureTarget, usedTextureID);
			}
			
			//DRAW with drawElements if geometry is indexed, else draw with drawArrays!
			if (geometryInfo.isIndexed()){
				gl.glDrawElements(fillDrawMode, indexBuff.capacity(), GL10.GL_UNSIGNED_SHORT, indexBuff); //limit() oder capacity()??
			}else{
				gl.glDrawArrays(fillDrawMode, 0, vertBuff.capacity()/3);
			}
			
			if (textureDrawn){
				gl.glBindTexture(textureTarget, 0);
				gl.glDisable(textureTarget); 
			}
		gl11Plus.glEndList();
		//// STOP recording display list and DRAW////////////////////
		
		if (geometryInfo.isContainsNormals()){
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}

		if (textureDrawn){
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		returnVal[0] = listIDFill;
		
		/////// DO OUTLINE LIST////////////////////////////
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, strokeColBuff);
		//Start recording display list
		gl11Plus.glNewList(listIDOutline, GL11Plus.GL_COMPILE);
		
//			if (drawSmooth)
//				gl.glEnable(GL.GL_LINE_SMOOTH);
			//FIXME TEST
			Tools3D.setLineSmoothEnabled(gl, true);
			
			if (strokeWeight > 0)
				gl.glLineWidth(strokeWeight);
			
			//DRAW
			if (geometryInfo.isIndexed()){
				gl.glDrawElements(GL10.GL_LINE_STRIP, indexBuff.capacity(), GL10.GL_UNSIGNED_SHORT, indexBuff); ////indices.limit()?
			}else{
				gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, vertBuff.capacity()/3);
			}
			
//			if (drawSmooth)
//				gl.glDisable(GL.GL_LINE_SMOOTH);
			//FIXME TEST
			Tools3D.setLineSmoothEnabled(gl, false);
			
		gl11Plus.glEndList();
		returnVal[1] = listIDOutline;
		////////////////////////////////////////////////////
		
		//Disable client states
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		return returnVal;
	}
	
	
	//TODO make only 1 function gendisplaylist mit boolean generate outline/fill
	/**
	 * Returns the ID (name) of the display list
	 * If you dont want to use a line stipple pattern, use '0' for the parameter.
	 * 
	 * @param pa the pa
	 * @param vertBuff the vert buff
	 * @param strokeColBuff the stroke col buff
	 * @param indexBuff the index buff
	 * @param drawSmooth the draw smooth
	 * @param strokeWeight the stroke weight
	 * @param lineStipple the line stipple
	 * 
	 * @return int id of outline drawing list
	 */
	public static int generateOutLineDisplayList(PApplet pa, FloatBuffer vertBuff, FloatBuffer strokeColBuff, IntBuffer indexBuff, 
												boolean drawSmooth, float strokeWeight, short lineStipple){
//		GL gl = beginGL(pa.g);
		GL10 gl = PlatformUtil.beginGL();
		GL11Plus gl11Plus = PlatformUtil.getGL11Plus();
		
		//Generate new list IDs
		int returnVal = -1;
		int listIDOutline = gl11Plus.glGenLists(1);
		if (listIDOutline == 0){
			System.err.println("Failed to create display list");
			return returnVal;
		}
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuff);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, strokeColBuff);
		
		//Start recording display list
		gl11Plus.glNewList(listIDOutline, GL11Plus.GL_COMPILE);
//			if (drawSmooth)
//				gl.glEnable(GL.GL_LINE_SMOOTH);
			//FIXME TEST for multisample
			Tools3D.setLineSmoothEnabled(gl, true);
		
			if (strokeWeight > 0)
				gl.glLineWidth(strokeWeight);
			if (lineStipple != 0){
				gl11Plus.glLineStipple(1, lineStipple);
				gl.glEnable(GL11Plus.GL_LINE_STIPPLE);
			}
			
			if (indexBuff == null){
				gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, vertBuff.capacity()/3);
			}else{
				gl.glDrawElements(GL10.GL_LINE_STRIP, indexBuff.capacity(), GL10.GL_UNSIGNED_SHORT, indexBuff); ////indices.limit()?
			}
			
			//RESET LINE STIPPLE
			if (lineStipple != 0)
				gl.glDisable(GL11Plus.GL_LINE_STIPPLE); 
			
//			if (drawSmooth)
//				gl.glDisable(GL.GL_LINE_SMOOTH);
			//FIXME TEST for multisample
			Tools3D.setLineSmoothEnabled(gl, false);
			
		gl11Plus.glEndList();
		returnVal = listIDOutline;
		
		//Disable client states
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
//		((PGraphicsOpenGL)pa.g).endGL();
		PlatformUtil.endGL();
		return returnVal;
	}
	
	
	
	/**
	 * Reverses an array.
	 * 
	 * @param b the b
	 * @return the vector3 d[]
	 */
	public static Vector3D[] reverse(Vector3D[] b) {
		   int left  = 0;          // index of leftmost element
		   int right = b.length-1; // index of rightmost element
		  
		   while (left < right) {
		      // exchange the left and right elements
			  Vector3D temp = b[left]; 
		      b[left]  = b[right]; 
		      b[right] = temp;
		     
		      // move the bounds toward the center
		      left++;
		      right--;
		   }
		   return b;
		}//endmethod reverse


	/**
	 * Checks whether the given image is of power of 2 dimensions.
	 * 
	 * @param image the image
	 * 
	 * @return true, if checks if is power of two dimension
	 */
	public static boolean isPowerOfTwoDimension(PImage image){
		return ToolsMath.isPowerOfTwo(image.width) && ToolsMath.isPowerOfTwo(image.height);
	}
	
	/**
     * For non power of two textures, the texture coordinates
     * have to be in the range from 0..texture_width instead of from 0.0 to 1.0.
     * <br>So we try to scale the texture coords to the width/height of the texture
     * 
     * @param texture the texture
     * @param verts the verts
     */
    public static void scaleTextureCoordsForRectModeFromNormalized(PImage texture, Vertex[] verts){
        for (Vertex vertex : verts) {
            if (vertex.getTexCoordU() <= 1.0f && vertex.getTexCoordU() >= 0.0f) {
                vertex.setTexCoordU(vertex.getTexCoordU() * texture.width);
            }
            if (vertex.getTexCoordV() <= 1.0f && vertex.getTexCoordV() >= 0.0f) {
                vertex.setTexCoordV(vertex.getTexCoordV() * texture.height);
            }
        }
    }
    
    /**
	 * projects a specific point on a plane with a specific depth
	 * @param gl
	 * @param point
	 * @param frustum
	 * @param z
	 * @return
	 */
	public static Vector3D projectPointToPlaneInPerspectiveMode(Vector3D point,IFrustum frustum,float z,AbstractMTApplication mtApp)
	{
		float heightOfPlaneAtZ = frustum.getHeightOfPlane(z);
		float widthOfPlaneAtZ = frustum.getWidthOfPlane(z);
		
		float heightOfPlaneAtPoint = frustum.getHeightOfPlane(point.z);
		float widthOfPlaneAtPoint = frustum.getWidthOfPlane(point.z);
		
		//float centerX = mtApp.width/2;
		//float centerY = mtApp.height/2;
		
		Vector3D ntl = frustum.getNearTopLeft();
		
		//subtract getWidthofNearPlane, because frustum is upside down
		float centerX = ntl.x - frustum.getWidthOfNearPlane() + frustum.getWidthOfNearPlane()/2f;
		float centerY = ntl.y + frustum.getHeightOfNearPlane()/2f;
		
		float percentWidth = (point.x - (centerX-(widthOfPlaneAtPoint/2.f)))/widthOfPlaneAtPoint;
		float percentHeight = (point.y - (centerY-(heightOfPlaneAtPoint/2.f)))/heightOfPlaneAtPoint;
		
		Vector3D projectedPoint = new Vector3D();
		projectedPoint.x = (centerX - (widthOfPlaneAtZ/2.f))+widthOfPlaneAtZ*percentWidth;
		projectedPoint.y = (centerY - (heightOfPlaneAtZ/2.f))+heightOfPlaneAtZ*percentHeight;
		projectedPoint.z = z;
		
		return projectedPoint;
	}
	
	
			
	
	public static boolean adaptTextureCoordsNPOT(AbstractShape shape, GLTexture tex){
		if(!PlatformUtil.isNPOTTextureSupported() 
			&& !shape.getGeometryInfo().isTextureCoordsAdaptedNPOT()
			&& !Tools3D.isPowerOfTwoDimension(tex)
			&& ((GLTexture) tex).getTextureTargetEnum() == TEXTURE_TARGET.TEXTURE_2D 
			&& shape.getGeometryInfo().isTextureCoordsNormalized()
		) {
			GLTexture glt = (GLTexture) tex;
			float maxU = (float)glt.width / (float)glt.glWidth;
			float maxV = (float)glt.height / (float)glt.glHeight;
			
			Vertex[] verts = shape.getVerticesLocal();
	        for (Vertex vertex : verts) {
//	            vertex.setTexCoordU( ( (vertex.x - upperLeftX) / width) * maxU);
//	            vertex.setTexCoordV( ( (vertex.y - upperLeftY) / height) * maxV);
	        	 vertex.setTexCoordU( vertex.getTexCoordU() * maxU);
	        	 vertex.setTexCoordV( vertex.getTexCoordV() * maxV);
//				System.out.println("TexU:" + vertex.getTexCoordU() + " TexV:" + vertex.getTexCoordV());
	        }
	        shape.getGeometryInfo().updateTextureBuffer(shape.isUseVBOs());
	        shape.getGeometryInfo().setTextureCoordsAdaptedNPOT(true);
			return true;
		}
		return false;
	}
    

}
