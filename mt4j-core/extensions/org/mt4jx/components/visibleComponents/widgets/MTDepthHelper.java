package org.mt4jx.components.visibleComponents.widgets;

import java.util.ArrayList;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.input.gestureAction.DefaultDepthAction;
import org.mt4jx.input.gestureAction.HelperDragAction;
import org.mt4jx.input.inputProcessors.componentProcessors.depthProcessor.DepthProcessor;
import org.mt4jx.util.extension3D.BoundingHelper;

import processing.core.PApplet;

public class MTDepthHelper extends MTComponent {

	/** the referencing component **/
	private MTComponent targetComponent;
	
	/** the object which will be drawn**/
	private MTRectangle visualHelper;
	
	/** the camera which is attached to the scene */
	private Icamera cam;
	
	/** the canvas of the scene */
	private MTCanvas canvas;
	
	/** the depthProcessor which is responsible for moving the
	 * object when depth Helper is used
	 */
	private DepthProcessor depthProcessor;
	
	/**
	 * create new depth helper object
	 * @param pApplet
	 * @param targetComponent
	 * @param cam
	 * @param canvas
	 */
	public MTDepthHelper(PApplet pApplet,MTComponent targetComponent,Icamera cam,MTCanvas canvas) {
		super(pApplet);
		
		this.targetComponent = targetComponent;
		this.cam = cam;
		this.canvas = canvas;
		createDepthHelper();
	}

	public void setTargetComponent(MTComponent targetComponent) {
		this.targetComponent = targetComponent;
	}

	public MTComponent getTargetComponent() {
		return targetComponent;
	}
	
	/**
	 * creates the depth helper for a specific object
	 * at a point of their bounding box projected to near plane
	 */
	private void createDepthHelper() {
		
		float nearPlaneHeight = cam.getFrustum().getHeightOfNearPlane();
		float nearPlaneWidth = cam.getFrustum().getWidthOfNearPlane();

		float visualHelperHeight = nearPlaneHeight*0.2f;
		float visualHelperWidth = nearPlaneWidth*0.06f;
		
		MTRectangle visualHelperBody = new MTRectangle(this.getRenderer(), 0.0f, (visualHelperHeight/10.0f),
				visualHelperWidth, visualHelperHeight*0.9f);
		visualHelperBody.unregisterAllInputProcessors();
		visualHelperBody.setGestureAllowance(DragProcessor.class,false);
		
		visualHelperBody.setFillColor(new MTColor(255, 255, 255,
				MTColor.ALPHA_HALF_TRANSPARENCY));
			
		MTRectangle visualHelperDrag = new MTRectangle(this.getRenderer(), 0.0f, 0.0f,
				visualHelperWidth,  visualHelperHeight*0.1f);
		
		visualHelperDrag.removeAllGestureEventListeners(DragProcessor.class);
		visualHelperDrag.setGestureAllowance(DragProcessor.class,true);
		visualHelperDrag.setGestureAllowance(ScaleProcessor.class,false);
		visualHelperDrag.setGestureAllowance(RotateProcessor.class,false);
		
		visualHelperDrag.setFillColor(new MTColor(100, 100, 100,
				MTColor.ALPHA_HALF_TRANSPARENCY));
		MTRectangle visualHelperTouch = new MTRectangle(this.getRenderer(), visualHelperWidth/4f, visualHelperHeight/9.0f+1.0f,
				visualHelperWidth/2f,  visualHelperHeight/1.4f);
		
		visualHelperTouch.setGestureAllowance(DragProcessor.class, false);
		visualHelperTouch.setGestureAllowance(ScaleProcessor.class,false);
		visualHelperTouch.setGestureAllowance(RotateProcessor.class,false);
		//visualHelperTouch.setPickable(false);
		visualHelperTouch.setFillColor(new MTColor(0, 0, 0,
				MTColor.ALPHA_HALF_TRANSPARENCY));
		DepthProcessor proc = new DepthProcessor( this.getRenderer(),canvas,cam,visualHelperTouch);
		this.depthProcessor =  proc;
		visualHelperTouch.registerInputProcessor(proc);
		visualHelperTouch.addGestureListener(DepthProcessor.class,new DefaultDepthAction(targetComponent));
		visualHelperTouch.setGestureAllowance(DepthProcessor.class,true);
		visualHelperTouch.setName("visualHelperTouch"); //TODO remove 
				
		visualHelper = new MTRectangle(this.getRenderer(),0.0f,0.0f,visualHelperWidth, visualHelperHeight/11f);
		visualHelper.setFillColor(new MTColor(255,255,255,MTColor.ALPHA_FULL_TRANSPARENCY));
		
		visualHelper.addChild(visualHelperBody);
		visualHelper.addChild(visualHelperDrag);
		visualHelper.addChild(visualHelperTouch);
		visualHelper.setComposite(false);
		//visualHelper.setPickable(true);
		//visualHelper.setGestureAllowance(DepthProcessor.class, false);
		visualHelper.setGestureAllowance(DragProcessor.class, true);
		visualHelper.setGestureAllowance(ScaleProcessor.class,false);
		visualHelper.setGestureAllowance(RotateProcessor.class,false);
		
		visualHelper.setName("visualHelper");
		//visualHelper.attachCamera(new MTCamera( this.getRenderer()));
		//visualHelper.setDepthBufferDisabled(true);
		this.setComposite(false);
		this.attachCamera(new MTCamera(this.getRenderer()));
		this.setDepthBufferDisabled(true);
		
		visualHelperDrag.setName("visual Helper drag");;
		
		visualHelperDrag.addGestureListener(DragProcessor.class,new HelperDragAction(visualHelperDrag,visualHelper));
		
		//canvas.addChild(visualHelper);
		this.addChild(visualHelper);
		//visualHelperHeight = visualHelperHeight*1.2f;
		
		visualHelper.sendToFront();
		visualHelperDrag.sendToFront();
			
	    Vector3D nearTopLeft = this.getAttachedCamera().getFrustum().getNearTopLeft();
		    
		ArrayList<IBoundingShape> shapes = BoundingHelper.getBoundingShapes(targetComponent);

		Vector3D rightVector = BoundingHelper.getOuterPointsOfBounding(shapes,nearTopLeft.getZ(),BoundingHelper.RIGHT_BOUNDING_POINT, cam, this.getRenderer());
				
		Vector3D nearPoint = Tools3D.projectPointToPlaneInPerspectiveMode(rightVector,  this.getAttachedCamera().getFrustum(), nearTopLeft.getZ(),(AbstractMTApplication) this.getRenderer());
		//Vector3D nearPoint  = Tools3D.project(pApplet,cam,new Vector3D(rightVector.x,rightVector.y, rightVector.z));
			
//		PGraphics3D p3d = ((PGraphics3D)this.getRenderer().g);
//		int width = this.getRenderer().getWidth();
//		int height = this.getRenderer().getHeight();
		
		//rightVector.x = nearTopLeft.x + rightVector.x/((float)width)*nearPlaneWidth;
		//rightVector.y = nearTopLeft.y + rightVector.y/((float)height)*nearPlaneHeight;
		
		//Upper right corner is not in frustum		
		if(nearPoint.x>nearTopLeft.x)
		{
			nearPoint.x = nearTopLeft.x-visualHelperWidth;
		}
			
		if(nearPoint.y>nearTopLeft.y+nearPlaneHeight-visualHelperHeight)
		{
			nearPoint.y = nearTopLeft.y+nearPlaneHeight-visualHelperHeight;
		}
		
		if(nearPoint.y<nearTopLeft.y)
		{
			nearPoint.y = nearTopLeft.y;// + nearPlaneHeight;
		}
		
		//test if create x,y is out of the screen
		if(nearPoint.x>nearTopLeft.x+nearPlaneWidth-visualHelperWidth)
		{
			//Width of screen - width of helper
			nearPoint.x = nearTopLeft.x+cam.getFrustum().getWidthOfNearPlane() - visualHelperWidth;
		}
		
		nearPoint.z = nearPoint.z-0.001f;
		
		this.translate(nearPoint);	
						
	}

	public void setDepthProcessor(DepthProcessor depthProcessor) {
		this.depthProcessor = depthProcessor;
	}

	public DepthProcessor getDepthProcessor() {
		return depthProcessor;
	}	

}
