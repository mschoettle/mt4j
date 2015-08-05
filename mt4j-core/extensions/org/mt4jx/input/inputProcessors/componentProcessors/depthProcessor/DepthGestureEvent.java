package org.mt4jx.input.inputProcessors.componentProcessors.depthProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.math.Vector3D;

public class DepthGestureEvent extends MTGestureEvent {

	private InputCursor dragCursor;
	private InputCursor depthCursor;
	
	private Vector3D from;
	private Vector3D to;
		
	private Vector3D translationVect;
	
	public DepthGestureEvent(IInputProcessor source,int id,IMTComponent3D targetComponent,InputCursor depthCursor,Vector3D translationVect)
	{
		super(source,id,targetComponent);	
	
		this.depthCursor = depthCursor;
			
		this.depthCursor = depthCursor;
		
		this.translationVect = translationVect;
	}




	public InputCursor getDepthCursor() {
		return depthCursor;
	}
	
	public Vector3D getFrom() {
		return from;
	}
	
	public Vector3D getTo() {
		return to;
	}
	
	public Vector3D getTranslationVect() {
		return translationVect;
	}
	
	
}
