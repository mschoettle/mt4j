package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.GroupVisualizations;

import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.IVisualizeMethodProvider;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;

public class ActivateVisualizationAction implements IGestureEventListener {

	private Cluster3DExt cluster;
	
	private IVisualizeMethodProvider methodProvider;
	
	public ActivateVisualizationAction(Cluster3DExt cluster,IVisualizeMethodProvider methodProvider)
	{		
		this.cluster = cluster;
		this.methodProvider = methodProvider;
		this.cluster.setVisualizeProvider(null);
	}
	public boolean processGestureEvent(MTGestureEvent ge) {
		if(ge instanceof DragEvent)
		{
			DragEvent tapEv = (DragEvent)ge;
			switch(tapEv.getId())
			{
			case DragEvent.GESTURE_STARTED:
				cluster.setVisualizeProvider(methodProvider);
				break;
			case DragEvent.GESTURE_ENDED:				
				cluster.setVisualizeProvider(null);
				break;
			default: break;
			}
		}
		return false;
	}

}
