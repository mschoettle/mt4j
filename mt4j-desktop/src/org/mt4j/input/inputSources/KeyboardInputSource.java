/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.input.inputSources;


import java.awt.event.KeyEvent;

import org.mt4j.AbstractMTApplication;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFingerInputEvt;

import processing.core.PApplet;

/**
 * The Class KeyboardInputSource.
 * @author Christopher Ruff
 */
public class KeyboardInputSource extends AbstractInputSource {
	
	/** The last used keyb id. */
	private long lastUsedKeybID;
	
	/** The location x. */
	private int locationX;
	
	/** The location y. */
	private int locationY;
	
	/** The space has been pressed. */
	private boolean spaceHasBeenPressed = false;
	
	/** The applet. */
	private PApplet applet;

	private int newFingerLocationKCode;

	private int moveUpKeyCode;

	private int moveLeftKeyCode;

	private int moveDownKeyCode;

	private int moveRightKeyCode;

	private int fingerDownKeyCode;
	
	/**
	 * Instantiates a new keyboard input source.
	 * 
	 * @param pa the pa
	 */
	public KeyboardInputSource(AbstractMTApplication pa){
		super(pa);
		this.applet = pa;
		applet.registerKeyEvent(this);
		
		
//		applet.registerDraw(this);
		
//		//TODO
//		pa.addKeyListener(new IKeyListener() {
//			
//			@Override
//			public void keyRleased(char key, int keyCode) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void keyPressed(char key, int keyCode) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		this.locationX = 0;
		this.locationY = 0;
		
		
		this.moveUpKeyCode 		= KeyEvent.VK_W;
		this.moveLeftKeyCode 	= KeyEvent.VK_A;
		this.moveDownKeyCode 	= KeyEvent.VK_S;
		this.moveRightKeyCode 	= KeyEvent.VK_D;
		
		this.newFingerLocationKCode = KeyEvent.VK_N;
		
		this.fingerDownKeyCode 	= KeyEvent.VK_SHIFT;
	}

	
	//401 = pressed //402 = released
	/**
	 * Key event.
	 * 
	 * @param e the e
	 */
	public void keyEvent(KeyEvent e){
//		System.out.println(e.getID());
//		System.out.println(e.getKeyCode());
		
		int evtID = e.getID();
		
		if (evtID == KeyEvent.KEY_PRESSED ){
			if (e.isControlDown() && e.getKeyCode() == this.newFingerLocationKCode){
				locationX = applet.mouseX;
				locationY = applet.mouseY;
			}else if (e.getKeyCode() == this.moveUpKeyCode){
				locationY-=5;
				if (e.isShiftDown()){
					fingerDown(e);
				}
			}else if (e.getKeyCode() == this.moveLeftKeyCode){
				locationX-=5;
				if (e.isShiftDown()){
					fingerDown(e);
				}
			}else if (e.getKeyCode() == this.moveDownKeyCode){
				locationY+=5;
				if (e.isShiftDown()){
					fingerDown(e);
				}
			}else if (e.getKeyCode() == this.moveRightKeyCode){
				locationX+=5;
				if (e.isShiftDown()){
					fingerDown(e);
				}
			}else if (e.getKeyCode() == this.fingerDownKeyCode){
				fingerDown(e);
			}
		}else if (evtID == KeyEvent.KEY_RELEASED){
			if (e.getKeyCode() == this.fingerDownKeyCode){
				fingerUp(e);
			}
		}
			
		
		/*
		switch (e.getKeyCode()){
		case KeyEvent.VK_W:
			if (evtID == KeyEvent.KEY_PRESSED ){
				locationY-=5;
				if (e.isShiftDown()){
					shiftPressed(e);
				}
			}
			break;
		case KeyEvent.VK_A:
			if (evtID == KeyEvent.KEY_PRESSED){
				locationX-=5;
				if (e.isShiftDown()){
					shiftPressed(e);
				}
			}
			break;
		case KeyEvent.VK_S:
			if (evtID == KeyEvent.KEY_PRESSED){
				locationY+=5;
				if (e.isShiftDown()){
					shiftPressed(e);
				}
			}
			break;
		case KeyEvent.VK_D:
			if (evtID == KeyEvent.KEY_PRESSED){
				locationX+=5;
				if (e.isShiftDown()){
					shiftPressed(e);
				}
			}
			break;
		case KeyEvent.VK_SHIFT:
			if (evtID == KeyEvent.KEY_PRESSED){
				shiftPressed(e);
			}
			else if (evtID == KeyEvent.KEY_RELEASED){
				shiftReleased(e);
			}
			break;
		case KeyEvent.VK_N: //set the location to the mouseposition
			if (evtID == KeyEvent.KEY_PRESSED){
				locationX = applet.mouseX;
				locationY = applet.mouseY;
			}
			break;
		default:
			break;
		}
		*/
	}
	

	public void setNewFingerLocationKeyCode(int keyCode){
		this.newFingerLocationKCode = keyCode;
	}
	
	public void setFingerDownKeyCode(int keyCode){
		this.fingerDownKeyCode = keyCode;
	}
	
	public void setMoveUpKeyCode(int keyCode){
		this.moveUpKeyCode = keyCode;
	}
	
	public void setMoveLeftKeyCode(int keyCode){
		this.moveLeftKeyCode = keyCode;
	}
	
	public void setmoveDownKeyCode(int keyCode){
		this.moveDownKeyCode = keyCode;
	}
	
	public void setMoveRightKeyCode(int keyCode){
		this.moveRightKeyCode = keyCode;
	}
	
	
	/**
	 * 
	 * @param e the e
	 */
	private void fingerDown(KeyEvent e){
		if (!spaceHasBeenPressed){
			InputCursor m = new InputCursor();
			MTFingerInputEvt touchEvt = new MTFingerInputEvt(this, locationX, locationY, MTFingerInputEvt.INPUT_STARTED, m);
//			m.addEvent(touchEvt);
			
			lastUsedKeybID = m.getId();
			ActiveCursorPool.getInstance().putActiveCursor(lastUsedKeybID, m);
			
			//FIRE
			this.enqueueInputEvent(touchEvt);
			
		spaceHasBeenPressed = true;
		}else{
			InputCursor m = ActiveCursorPool.getInstance().getActiveCursorByID(lastUsedKeybID);
			
//			if (m.getLastEvent().getPositionX() != locationX || m.getLastEvent().getPositionY() != locationY){
			MTFingerInputEvt te = new MTFingerInputEvt(this, locationX, locationY, MTFingerInputEvt.INPUT_UPDATED, m);
//			m.addEvent(new MTFingerInputEvt2(this, e.getX(), e.getY(), MTFingerInputEvt.FINGER_UPDATE, m));
			
			//FIRE
			this.enqueueInputEvent(te);
//			}
		}
	}
	
	
	/**
	 * 
	 * @param e the e
	 */
	private void fingerUp(KeyEvent e) {
		InputCursor m = ActiveCursorPool.getInstance().getActiveCursorByID(lastUsedKeybID);
		MTFingerInputEvt te = new MTFingerInputEvt(this, locationX, locationY, MTFingerInputEvt.INPUT_ENDED, m);
//		m.addEvent(te);
		
		this.enqueueInputEvent(te);
		
		ActiveCursorPool.getInstance().removeCursor((lastUsedKeybID));
		spaceHasBeenPressed = false;
		
//		MTFingerInputEvt te = MTFingerInputEvtPool.getInstance().getEventByID(lastUsedKeybID);
//		//FIXME warum gibts manchmal NUllpointer weil kein TE vorhanden? auch bei mouse..
	}

//	@Override
//	public boolean firesEventType(Class<? extends MTInputEvent> evtClass){
//		return (evtClass == MTFingerInputEvt.class);
//	}
	
	
}
