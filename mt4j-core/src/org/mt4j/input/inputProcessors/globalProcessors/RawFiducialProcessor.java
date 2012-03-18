/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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

import org.mt4j.input.inputData.MTFiducialInputEvt;
import org.mt4j.input.inputData.MTInputEvent;


/**
 * The Class RawFiducialProcessor. Tracks all fiducial input events (MTFiducialInputEvt).
 * 
 * @author Christopher Ruff
 */
public class RawFiducialProcessor extends AbstractGlobalInputProcessor {

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor#processInputEvtImpl(org.mt4j.input.inputData.MTInputEvent)
	 */
	@Override
	public void processInputEvtImpl(MTInputEvent inputEvent) {
		if (inputEvent instanceof MTFiducialInputEvt){
			MTFiducialInputEvt fEvt = (MTFiducialInputEvt)inputEvent;
//			InputCursor motion = fEvt.getCursor();
			this.fireInputEvent(fEvt);
			/*
			switch (fEvt.getId()) {
			case MTFiducialInputEvt.INPUT_DETECTED:{
				this.fireInputEvent(new RawFiducialEvt(this, MTGestureEvent.GESTURE_DETECTED, null, motion, new Vector3D(fEvt.getPosX(), fEvt.getPosY(), 0), new Vector3D(fEvt.getPosX(), fEvt.getPosY(),0), fEvt.getFiducialId(), fEvt.getAngle(), fEvt.getX_speed(), fEvt.getY_speed(), fEvt.getR_speed(), fEvt.getM_accel(), fEvt.getR_accel()));
				break;
			}
			case MTFiducialInputEvt.INPUT_UPDATED:{
				AbstractCursorInputEvt previousEvent = motion.getPreviousEventOf(fEvt);
//				logger.debug("Prev " + previousEvent.getPositionX() +"," + previousEvent.getPositionY());
//				logger.debug("now " + te.getPositionX() +"," + te.getPositionY());
				this.fireInputEvent(new RawFiducialEvt(this, MTGestureEvent.GESTURE_UPDATED, null, motion, new Vector3D(previousEvent.getPosX(), previousEvent.getPosY(), 0) , new Vector3D(fEvt.getPosX(), fEvt.getPosY(), 0), fEvt.getFiducialId(),  fEvt.getAngle(), fEvt.getX_speed(), fEvt.getY_speed(), fEvt.getR_speed(), fEvt.getM_accel(), fEvt.getR_accel()));
				break;
			}
			case MTFiducialInputEvt.INPUT_ENDED:{
				this.fireInputEvent(new RawFiducialEvt(this, MTGestureEvent.GESTURE_ENDED, null, motion, new Vector3D(fEvt.getPosX(), fEvt.getPosY(), 0), new Vector3D(fEvt.getPosX(), fEvt.getPosY(), 0), fEvt.getFiducialId(), fEvt.getAngle(), fEvt.getX_speed(), fEvt.getY_speed(), fEvt.getR_speed(), fEvt.getM_accel(), fEvt.getR_accel()));
				break;
			}
			default:
				break;
			}
			*/
		}
	}

	
	
//	@Override
//	public Class<? extends MTInputEvent> getListenEventType() {
//		return MTFiducialInputEvt.class;
//	}

}
