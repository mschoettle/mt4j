package org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor;

import org.mt4j.input.gestureAction.Rotate3DAction;
import org.mt4j.sceneManagement.IPreDrawAction;

public class RotationPreDraw implements IPreDrawAction {

	private Rotate3DAction action;
	
	public RotationPreDraw(Rotate3DAction action)
	{
		this.action = action;
	}
	
	public boolean isLoop() {
		return true;
	}

	public void processAction() {
		//System.out.println("process predraw aciton");
		action.draw();
	}

}
