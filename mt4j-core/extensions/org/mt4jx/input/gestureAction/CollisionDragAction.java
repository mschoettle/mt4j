package org.mt4jx.input.gestureAction;

import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;

public class CollisionDragAction implements IGestureEventListener {

	private IMTComponent3D dragTarget;
	private boolean useCustomTarget;	
	private boolean gestureAborted = false;
	
	public CollisionDragAction(){
		this.useCustomTarget = false;
	}
	
	public CollisionDragAction(IMTComponent3D dragTarget){
		this.dragTarget = dragTarget;
		this.useCustomTarget = true;
	}	
	
	public boolean processGestureEvent(MTGestureEvent ge) {
		
		if (ge instanceof DragEvent){
				DragEvent dragEvent = (DragEvent)ge;
				
				if (!useCustomTarget)
					dragTarget = dragEvent.getTarget(); 
				
				switch (dragEvent.getId()) {
				case MTGestureEvent.GESTURE_STARTED:
					//Put target on top -> draw on top of others
					if (dragTarget instanceof MTComponent){
						MTComponent baseComp = (MTComponent)dragTarget;
						
						baseComp.sendToFront();
						
						/*
						//End all animations of the target
						Animation[] animations = AnimationManager.getInstance().getAnimationsForTarget(dragTarget);
						for (int i = 0; i < animations.length; i++) {
							Animation animation = animations[i];
							animation.stop();
						}
						*/
					}
					dragTarget.translateGlobal(dragEvent.getTranslationVect());
					break;
				case MTGestureEvent.GESTURE_UPDATED:
					if(!isGestureAborted())
					{
						dragTarget.translateGlobal(dragEvent.getTranslationVect());						
					}
					break;
				case MTGestureEvent.GESTURE_ENDED:
					break;
				default:
					break;
				}
			}
			return false;
	}

	public void setGestureAborted(boolean gestureAborted) {
		this.gestureAborted = gestureAborted;
	}

	public boolean isGestureAborted() {
		return gestureAborted;
	}
}

