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


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.mt4j.AbstractMTApplication;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.input.inputData.MTMouseInputEvt;

/**
 * The Class MouseInputSource.
 * @author Christopher Ruff
 */
public class MouseInputSource extends AbstractInputSource implements MouseMotionListener, MouseListener {
	
	/** The Constant OPENGL_MODE. */
	public final static int OPENGL_MODE 	= 0;
	
	/** The Constant JAVA_MODE. */
	public final static int JAVA_MODE		= 1;
//	private int mode;
	/** The last used mouse id. */
	private long lastUsedMouseID;
	
	/** The mouse busy. */
	private boolean mouseBusy; 
	
	//private Stack lastUsedMouseIDs;
	/** The mouse pressed button. */
	private int mousePressedButton;
	
	//make singleton
	/**
	 * Instantiates a new mouse input source.
	 * 
	 * @param pa the pa
	 */
	public MouseInputSource(AbstractMTApplication pa){
		super(pa);
		
		pa.registerMouseEvent(this);
		mouseBusy = false;
	}

	/**
	 * Mouse event.
	 * 
	 * @param event the event
	 */
	public void mouseEvent(MouseEvent event) {
//		/*
		switch (event.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			this.mousePressed(event);
			break;
		case MouseEvent.MOUSE_RELEASED:
			this.mouseReleased(event);
			break;
		case MouseEvent.MOUSE_CLICKED:
			this.mouseClicked(event);
			break;
		case MouseEvent.MOUSE_DRAGGED:
			this.mouseDragged(event);
			break;
		case MouseEvent.MOUSE_MOVED:
			this.mouseMoved(event);
			break;
		}
//		*/
	} 

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		if (!mouseBusy )	{
			mousePressedButton = e.getButton();
			mouseBusy = true;
			
			InputCursor m = new InputCursor();
			MTMouseInputEvt te = new MTMouseInputEvt(this, e.getModifiers(), e.getX(), e.getY(), MTFingerInputEvt.INPUT_STARTED, m);
			
			lastUsedMouseID = m.getId();
			ActiveCursorPool.getInstance().putActiveCursor(lastUsedMouseID, m);
//			System.out.println("MouseSource Finger DOWN, Motion ID: " + m.getId());
			this.enqueueInputEvent(te);
		}
	}

	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		try {
			InputCursor m = ActiveCursorPool.getInstance().getActiveCursorByID(lastUsedMouseID);
			if (m != null){
				MTMouseInputEvt te = new MTMouseInputEvt(this, e.getModifiers(), e.getX(), e.getY(), MTFingerInputEvt.INPUT_UPDATED, m);
//				System.out.println("MouseSource Finger UPDATE, Motion ID: " + m.getId());
				this.enqueueInputEvent(te);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == mousePressedButton) {
			InputCursor m = ActiveCursorPool.getInstance().getActiveCursorByID(lastUsedMouseID);
			MTMouseInputEvt te = new MTMouseInputEvt(this, e.getModifiers(), e.getX(), e.getY(), MTFingerInputEvt.INPUT_ENDED, m);
			
			//System.out.println("MouseSource Finger UP, Motion ID: " + m.getId());
			this.enqueueInputEvent(te);
			ActiveCursorPool.getInstance().removeCursor((lastUsedMouseID));
			mouseBusy = false;
		}
	}

}
