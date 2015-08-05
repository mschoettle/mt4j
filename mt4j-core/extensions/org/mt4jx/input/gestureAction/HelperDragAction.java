package org.mt4jx.input.gestureAction;

import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;

public class HelperDragAction implements IGestureEventListener 
{
	
	private IMTComponent3D dragObject;
	private IMTComponent3D parentObject;
	
	public HelperDragAction(IMTComponent3D dragObject,IMTComponent3D parentObject)
	{
		this.dragObject = dragObject;
		this.parentObject = parentObject;
	}
	
	public boolean processGestureEvent(MTGestureEvent ge) {
		DragEvent dragEv;
		
		if(ge instanceof DragEvent)
		{
			dragEv = (DragEvent)ge;
		}
		else
		{
			return false;
		}
		
		switch(dragEv.getId())
		{
			case MTGestureEvent.GESTURE_STARTED:
			{
				if (dragObject instanceof MTComponent){
					MTComponent baseComp = (MTComponent)dragObject;
					
					baseComp.sendToFront();
				}
				MTComponent parentComp = (MTComponent)parentObject;
				//if you click on one part of a composite the
				//whole composite object is picked
				//here the composite does not have a dragprocessor,only one children
				//so you cant drag it
				//so we have to composite the helper only
				//in this moment where it should be dragged
				//to drag all children
				parentComp.setComposite(true);
				parentObject.translateGlobal(dragEv.getTranslationVect());
				parentComp.sendToFront();
				parentComp.setComposite(false);
				
				break;
			}
			case MTGestureEvent.GESTURE_UPDATED:
			{
				MTComponent parentComp = (MTComponent)parentObject;
				parentComp.setComposite(true);				
				parentObject.translateGlobal(dragEv.getTranslationVect());
				parentComp.sendToFront();
				parentComp.setComposite(false);
				break;
			}
			case MTGestureEvent.GESTURE_ENDED:
				break;
			default:
				break;
		}
		return true;
	}

}
