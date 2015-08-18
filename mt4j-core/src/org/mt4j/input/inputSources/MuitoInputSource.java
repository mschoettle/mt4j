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


import java.util.HashMap;

import muito.motion.Motion;
import muito.motion.MotionEvent;
import muito.motion.MotionProviderListener;
import muito.motion.Settings;
import muito.motion.provider.MuitoMotionTrackerPorvider;

import org.mt4j.AbstractMTApplication;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.util.MT4jSettings;


/**
 * The Class MuitoInputSource. This is an input protocol used internally at the Fraunhofer Institute.
 * 
 * @author Christopher Ruff
 */
public class MuitoInputSource extends AbstractInputSource implements MotionProviderListener {

	/** The muito id to input motion id. */
	private HashMap<Long, Long> muitoIDToInputMotionID;

	/**
	 * Instantiates a new muito input source.
	 * 
	 * @param pa the pa
	 * @param server the server
	 * @param port the port
	 */
	public MuitoInputSource(AbstractMTApplication pa, String server, int port){
		super(pa);
	    Settings.getInstance().setScreensizeX(MT4jSettings.getInstance().getWindowWidth());
	    Settings.getInstance().setScreensizeY(MT4jSettings.getInstance().getWindowHeight());
		MuitoMotionTrackerPorvider muitoProvider = new MuitoMotionTrackerPorvider(server, port);
	    muitoProvider.addListener(this);
//	    muitoProvider.addMotionFilter(new CalibrationFilter());
	    muitoIDToInputMotionID = new HashMap<Long, Long>();
	}
	
	
	/* (non-Javadoc)
	 * @see muito.motion.MotionProviderListener#newMotionProvided(muito.motion.Motion)
	 */
	public void newMotionProvided(Motion motion) {
		MotionEvent me = motion.getLastEvent();
		InputCursor m = new InputCursor();
		MTFingerInputEvt touchEvt = new MTFingerInputEvt(this, me.getXAbs(), me.getYAbs(), MTFingerInputEvt.INPUT_STARTED, m);
		
		long motionID = motion.getId();
		ActiveCursorPool.getInstance().putActiveCursor(motionID, m);
		muitoIDToInputMotionID.put(motionID, motionID);
		
		//FIRE
		this.enqueueInputEvent(touchEvt);
	}
	

	/* (non-Javadoc)
	 * @see muito.motion.MotionProviderListener#providedMotionUpdated(muito.motion.Motion, muito.motion.MotionEvent)
	 */
	public void providedMotionUpdated(Motion m, MotionEvent me) {
		InputCursor mo = ActiveCursorPool.getInstance().getActiveCursorByID(muitoIDToInputMotionID.get(m.getId()));
		MTFingerInputEvt te = new MTFingerInputEvt(this, me.getXAbs(), me.getYAbs(), MTFingerInputEvt.INPUT_UPDATED, mo);
		this.enqueueInputEvent(te);
	}
	
	
	/* (non-Javadoc)
	 * @see muito.motion.MotionProviderListener#providedMotionCompleted(muito.motion.Motion)
	 */
	public void providedMotionCompleted(Motion m) {
		long motionID = muitoIDToInputMotionID.get(m.getId());
		InputCursor mo = ActiveCursorPool.getInstance().getActiveCursorByID(motionID);
		MTFingerInputEvt te;
		if (mo.getCurrentEvent() != null)
			te = new MTFingerInputEvt(this, mo.getCurrentEvent().getX(), mo.getCurrentEvent().getY(), MTFingerInputEvt.INPUT_ENDED, mo);
		else
			te = new MTFingerInputEvt(this, 0,0, MTFingerInputEvt.INPUT_ENDED, mo);
		
		this.enqueueInputEvent(te);
		ActiveCursorPool.getInstance().removeCursor(motionID);
		muitoIDToInputMotionID.remove(m.getId());		
	}


}
