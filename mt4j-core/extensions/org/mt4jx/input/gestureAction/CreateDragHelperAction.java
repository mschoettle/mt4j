package org.mt4jx.input.gestureAction;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.util.camera.Icamera;
import org.mt4jx.components.visibleComponents.widgets.MTDepthHelper;
import processing.core.PApplet;


public class CreateDragHelperAction implements IGestureEventListener{
	
	private PApplet pApplet;
	
	private MTCanvas canvas;
	
	private Icamera cam;
	
	private MTComponent targetComponent;
	
	private MTDepthHelper depthHelper;

	/**
	 * to scale to the correct size after zooming 
	 * the distance between camera and near plane 
	 * wihtout zoom is needed
	 */
	private float zDistanceWithoutZoom;
		
	public CreateDragHelperAction(PApplet v_pApplet,MTCanvas v_canvas,Icamera v_cam,MTComponent v_targetComponent)
	{
		this.pApplet = v_pApplet;
		this.canvas = v_canvas;
		this.cam = v_cam;
		this.targetComponent = v_targetComponent;
		this.zDistanceWithoutZoom = cam.getFrustum().getZValueOfNearPlane();
	    
	}
		
	public boolean processGestureEvent(MTGestureEvent ge) {
		
		if(ge instanceof DragEvent)
		{
			DragEvent evt = (DragEvent)ge;			
			switch (evt.getId()) {
			case MTGestureEvent.GESTURE_STARTED:
				depthHelper = new MTDepthHelper(pApplet,targetComponent,cam,canvas);				
				canvas.addChild(depthHelper);				
				break;
			case MTGestureEvent.GESTURE_UPDATED:				
				break;
			case MTGestureEvent.GESTURE_ENDED:				
				deleteDepthHelper();
				break;
			}
			
		}
		return false;
	}	

	private void deleteDepthHelper()
	{
		canvas.removeChild(depthHelper);
	}
	
	
	
	

}
