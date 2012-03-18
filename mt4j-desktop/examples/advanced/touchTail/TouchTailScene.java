package advanced.touchTail;

import java.awt.event.KeyEvent;

import org.mt4j.AbstractMTApplication;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;

public class TouchTailScene extends AbstractScene {
	private AbstractMTApplication mtApp;
	private TouchTailComponent tails;
	
	public TouchTailScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.mtApp = mtApplication;
		this.setClearColor(new MTColor(140, 140, 110, 255));
		
		//Create tail component
		tails = new TouchTailComponent(mtApp);
		this.getCanvas().addChild(tails);
		
		//Add tap&hold gesture to clear all tails
		TapAndHoldProcessor tapAndHold = new TapAndHoldProcessor(mtApplication);
		tapAndHold.setMaxFingerUpDist(10);
		tapAndHold.setHoldTime(3000);
		tails.registerInputProcessor(tapAndHold);
		tails.addGestureListener(TapAndHoldProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapAndHoldEvent t = (TapAndHoldEvent)ge;
				if (t.getId() == TapAndHoldEvent.GESTURE_ENDED && t.isHoldComplete()){
					tails.clearTails();	
				}
				return false;
			}
		});
		tails.addGestureListener(TapAndHoldProcessor.class, new TapAndHoldVisualizer(mtApp, getCanvas()));
		
		//Add touch feedback
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
	}

	public void keyEvent(KeyEvent e){
		if (e.getID() != KeyEvent.KEY_PRESSED)
			return;
		switch (e.getKeyCode()){
		case KeyEvent.VK_F:
			System.out.println("FPS: " + mtApp.frameRate);
			break;
		case KeyEvent.VK_SPACE:
			tails.clearTails();
		case KeyEvent.VK_PLUS:
			getSceneCam().zoomAmount(5);
			getSceneCam().update();
			break;
		default:
			break;
		}
	}
	
	public void onEnter() {
		getMTApplication().registerKeyEvent(this);
	}
	
	public void onLeave() {	
		getMTApplication().unregisterKeyEvent(this);
	}

}
