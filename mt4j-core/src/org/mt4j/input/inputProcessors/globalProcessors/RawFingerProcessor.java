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
package org.mt4j.input.inputProcessors.globalProcessors;


import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.input.inputData.MTInputEvent;


/**
 * The Class RawFingerProcessor. This input processor basically just forwards
 * all finger input without any input processing involved. Useful where we only
 * need the raw finger data. The corresponding MTFingerInputEvt thus has null
 * as its targetComponent!
 * 
 * @author Christopher Ruff
 */
public class RawFingerProcessor extends AbstractGlobalInputProcessor {
	
	public void processInputEvtImpl(MTInputEvent inputEvent){ 
//		MTFingerInputEvt touchEvt = (MTFingerInputEvt)inputEvent;
		
		if (inputEvent instanceof MTFingerInputEvt){
			MTFingerInputEvt te = (MTFingerInputEvt)inputEvent;
			this.fireInputEvent(te); 
			/*
			InputCursor motion = te.getCursor();
			
			switch (te.getId()) {
			case MTFingerInputEvt.INPUT_DETECTED:
				fireInputEvent(new RawFingerEvent(this, MTGestureEvent.GESTURE_DETECTED, null, motion, new Vector3D(te.getPosX(),te.getPosY(), 0), new Vector3D(te.getPosX(),te.getPosY(), 0)));
				break;
			case MTFingerInputEvt.INPUT_UPDATED:
				AbstractCursorInputEvt previousEvent = motion.getPreviousEventOf(te);
				if (previousEvent != null){
//					logger.debug("Prev " + previousEvent.getPositionX() +"," + previousEvent.getPositionY());
//					logger.debug("now " + te.getPositionX() +"," + te.getPositionY());
					fireInputEvent(new RawFingerEvent(this, MTGestureEvent.GESTURE_UPDATED, null, motion, new Vector3D(previousEvent.getPosX(), previousEvent.getPosY(), 0) , new Vector3D(te.getPosX(),te.getPosY(), 0)));
				}
				break;
			case MTFingerInputEvt.INPUT_ENDED:
				fireInputEvent(new RawFingerEvent(this, MTGestureEvent.GESTURE_ENDED, null, motion, new Vector3D(te.getPosX(),te.getPosY(), 0), new Vector3D(te.getPosX(),te.getPosY(), 0)));
				break;
			default:
				break;
			}
			*/
		}
	}


//	@Override
//	public Class<? extends MTInputEvent> getListenEventType() {
//		return MTFingerInputEvt.class;
//	}

	
}
