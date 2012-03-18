package org.mt4j.input.gestureAction;

import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Rotate3DEvent;

import processing.core.PApplet;

public class Rotate3DAction implements IGestureEventListener,ICollisionAction {

	private IMTComponent3D target;
	
	private boolean gestureAborted = false;
	
	private PApplet pApplet;
	
	private Rotate3DEvent lastRotateEvent;
	
	private boolean registered = false;
	
	private boolean drawAble = false;
	
	public Rotate3DAction(PApplet pApplet,IMTComponent3D target)
	{
		this.target = target;
		this.pApplet = pApplet;	
		
	}
	
	public boolean processGestureEvent(MTGestureEvent ge) {
		
		if(ge instanceof Rotate3DEvent)
		{
			
			Rotate3DEvent rotateEvent = (Rotate3DEvent)ge;
			this.drawAble = true;
			lastRotateEvent = rotateEvent;
			switch (rotateEvent.getId()) {
			case MTGestureEvent.GESTURE_STARTED:
				if (target instanceof MTComponent){
					((MTComponent)target).sendToFront();
					if(!(target instanceof Cluster3DExt))
					{
						target.rotateZGlobal(rotateEvent.getRotationPoint(), rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesZ());
						target.rotateXGlobal(rotateEvent.getRotationPoint(),rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesX());
						target.rotateYGlobal(rotateEvent.getRotationPoint(),rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesY());					
					}else
					{
						Cluster3DExt clu = (Cluster3DExt)target;
						
						for(MTComponent comp : clu.getChildren())
						{
							//only move children, not cluster itself
							//cause it should stay on the floor
							if(!(comp instanceof MTPolygon))
							{
								comp.rotateZGlobal(rotateEvent.getRotationPoint(), rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesZ());
								comp.rotateXGlobal(rotateEvent.getRotationPoint(),rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesX());
								comp.rotateYGlobal(rotateEvent.getRotationPoint(),rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesY());
							}
									
						}
					}
				}
				break;
			case MTGestureEvent.GESTURE_UPDATED:
				if(!(target instanceof Cluster3DExt))
				{					
//					System.out.println("Rotating: " + target + "\n RotationPoint: " + rotateEvent.getRotationPoint() + " ZrotDeg: " + rotateEvent.getRotationDegreesZ() + " XrotDeg: " + rotateEvent.getRotationDegreesX() + " YrotDeg: " + rotateEvent.getRotationDegreesY());
					target.rotateZGlobal(rotateEvent.getRotationPoint(), rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesZ());
					target.rotateXGlobal(rotateEvent.getRotationPoint(),rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesX());
					target.rotateYGlobal(rotateEvent.getRotationPoint(),rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesY());
					
				}else
				{
					Cluster3DExt clu = (Cluster3DExt)target;
					for(MTComponent comp : clu.getChildren())
					{
						//only move children, not cluster itself
						//cause it should stay on the floor
						if(!(comp instanceof MTPolygon))
						{
							comp.rotateZGlobal(rotateEvent.getRotationPoint(), rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesZ());
							comp.rotateXGlobal(rotateEvent.getRotationPoint(),rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesX());
							comp.rotateYGlobal(rotateEvent.getRotationPoint(),rotateEvent.getRotationDirection()*rotateEvent.getRotationDegreesY());
						}								
					}
				}
				break;
			case MTGestureEvent.GESTURE_ENDED:
				break;
			case MTGestureEvent.GESTURE_CANCELED:
				break;
			default:
				break;
			}
			
			return true;
		}
		return false;
	}
	
	public void draw()
	{
		/*
		if(lastRotateEvent!=null)
		{
			
			//Tools3D.beginGL(pApplet);
			GL gl = Tools3D.getGL(pApplet);
			gl.glPointSize(5.0f);
			gl.glLineWidth(100.0f);
			gl.glColor3f(255.0f,0.0f,0.0f);
			gl.glBegin(GL.GL_POINTS);
			System.out.println(lastRotateEvent.getRotationPoint());
			gl.glVertex3f(lastRotateEvent.getRotationPoint().x,lastRotateEvent.getRotationPoint().y,lastRotateEvent.getRotationPoint().z);
			gl.glEnd();
			gl.glBegin(GL.GL_LINES);
			Vector3D rotPoint = Tools3D.projectPointToPlane(lastRotateEvent.getRotationPoint(), ((MTApplication)pApplet).getScenes()[0].getSceneCam().getFrustum(), ((MTApplication)pApplet).getScenes()[0].getSceneCam().getFrustum().getZValueOfNearPlane()-0.001f, (MTApplication)pApplet);
			gl.glVertex3f(rotPoint.x,rotPoint.y,rotPoint.z);
			Vector3D axisAdded = rotPoint.getAdded(lastRotateEvent.getRotationAxis());
			//axisAdded.scaleLocal(20.0f);
			gl.glVertex3f(axisAdded.x, axisAdded.y, axisAdded.z);
			gl.glVertex3f(300.0f,400.0f,((MTApplication)pApplet).getScenes()[0].getSceneCam().getFrustum().getZValueOfNearPlane()-1.0f);
			gl.glVertex3f(400.0f,300.0f,((MTApplication)pApplet).getScenes()[0].getSceneCam().getFrustum().getZValueOfNearPlane()-1.0f);
			
			gl.glEnd();
			//Tools3D.endGL(pApplet);*/
		//}
	}
	
	public boolean gestureAborted() {
		return this.gestureAborted;		
	}

	public void setGestureAborted(boolean aborted) {
		this.gestureAborted = aborted;
	}

	public void setDrawAble(boolean drawAble) {
		this.drawAble = drawAble;
	}

	public boolean isDrawAble() {
		return drawAble;
	}

	public MTGestureEvent getLastEvent() {
		return this.lastRotateEvent;
	}


}
