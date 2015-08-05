package org.mt4j.input.inputSources;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.mt4j.AbstractMTApplication;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.util.MT4jSettings;

import com.alderstone.multitouch.mac.touchpad.Finger;
import com.alderstone.multitouch.mac.touchpad.FingerState;
import com.alderstone.multitouch.mac.touchpad.TouchpadObservable;

/**
 * Input source for Mac OS X Trackpads.
 * Uses the library from http://kenai.com/projects/macmultitouch
 * 
 * @author Florian Thalmann
 */
public class MacTrackpadSource extends AbstractInputSource implements Observer {

	private TouchpadObservable tpo;
	private int windowWidth, windowHeight;
	private Map<Integer, Long> fingerIdToCursorId;
	
	public MacTrackpadSource(AbstractMTApplication mtApp) {
		super(mtApp);
		this.tpo = TouchpadObservable.getInstance();
		this.tpo.addObserver(this);
		
		this.windowWidth = MT4jSettings.getInstance().getWindowWidth();
		this.windowHeight = MT4jSettings.getInstance().getWindowHeight();
		
		this.fingerIdToCursorId = new HashMap<Integer, Long>();
	}

	public void update(Observable obj, Object arg) {
		Finger finger = (Finger) arg;
		int fingerID = finger.getID();
		
		ActiveCursorPool cursorPool = ActiveCursorPool.getInstance();
		int inputID;
		Long cursorID = fingerIdToCursorId.get(fingerID);
		InputCursor cursor = (cursorID != null)? cursorPool.getActiveCursorByID(cursorID) : null;
		
		if (finger.getState() == FingerState.PRESSED) {
			if (cursor == null) { //new finger
				cursor = new InputCursor();
				fingerIdToCursorId.put(fingerID, cursor.getId());
				cursorPool.putActiveCursor(cursor.getId(), cursor);
				inputID = MTFingerInputEvt.INPUT_STARTED;
			} else { //updated finger
				inputID = MTFingerInputEvt.INPUT_UPDATED;
			}
		} else { //removed finger
			if (cursorID != null){
				cursorPool.removeCursor(cursorID);
			}
			fingerIdToCursorId.remove(fingerID);
			inputID = MTFingerInputEvt.INPUT_ENDED;
		}
		
		int absoluteX = Math.round(finger.getX()*this.windowWidth);
		int absoluteY = Math.round((1-finger.getY())*this.windowHeight);
		this.enqueueInputEvent(new MTFingerInputEvt(this, absoluteX, absoluteY, inputID, cursor));
	}

	public void update() {}

}
