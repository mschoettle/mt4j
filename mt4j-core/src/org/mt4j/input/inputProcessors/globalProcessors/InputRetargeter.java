/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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

import java.util.HashMap;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.IHitTestInfoProvider;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;

/**
 * The Class InputRetargeter. This global input analyzer is automatically created with each new scene and listens
 * to all InputSources for AbstractCursorInputEvt events (Input events with a discrete position attached).
 * <br>This global input processor uses AbstractCursorInputEvt's to check which object in the current scene was hit.
 * Then the target is set at the event and the event is delivered to the scenes canvas where the 
 * targeted event is delivered to the target. No new event is created for performance reasons.
 * So the event is merely re-targeted and redirected.
 * 
 * @author Christopher Ruff
 */
public class InputRetargeter extends AbstractGlobalInputProcessor {
	private HashMap<InputCursor, IMTComponent3D> cursorToObjectMap;
	
	/** The app info provider. */
	private IHitTestInfoProvider appInfoProvider;

	public InputRetargeter(IHitTestInfoProvider appInfoProvider) {
		this.appInfoProvider = appInfoProvider;
		this.cursorToObjectMap = new HashMap<InputCursor, IMTComponent3D>();
	}


	public void processInputEvtImpl(MTInputEvent inputEvent) {
		if (inputEvent instanceof AbstractCursorInputEvt) {
			AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt) inputEvent;
			InputCursor c = posEvt.getCursor();
			
			switch (posEvt.getId()) {
			case AbstractCursorInputEvt.INPUT_STARTED:{
//				logger.debug("Finger DOWN-> " + " ID:" + posEvt.getId() + "; X:" + posEvt.getPosX() + " Y:" + posEvt.getPosY() + "; Source: " + posEvt.getSource());
				//Check if there is an object under the cursor and save it to a hashtable with the event if so
				IMTComponent3D obj = appInfoProvider.getComponentAt(posEvt.getX(), posEvt.getY());
				if (obj != null){
					cursorToObjectMap.put(c, obj);
					posEvt.setTarget(obj);
//					posEvt.setCurrentTarget(obj.getRoot()); //Enable this if using event CAPTURING PHASE
					posEvt.setCurrentTarget(obj);
					posEvt.setEventPhase(MTInputEvent.CAPTURING_PHASE);
					this.fireInputEvent(posEvt);
				}
			}
			break;
			case AbstractCursorInputEvt.INPUT_UPDATED:{
//				logger.debug("Finger UPDATE-> " + " ID:" + posEvt.getId() + "; X:" + posEvt.getPositionX() + " Y:" + posEvt.getPositionY() + "; Source: " + posEvt.getSource());
				IMTComponent3D associatedObj = cursorToObjectMap.get(c);
				if (associatedObj != null){
					posEvt.setTarget(associatedObj);
//					posEvt.setCurrentTarget(associatedObj.getRoot());//Enable this if using event CAPTURING PHASE
					posEvt.setCurrentTarget(associatedObj);
					posEvt.setEventPhase(MTInputEvent.CAPTURING_PHASE);
					this.fireInputEvent(posEvt);
				}
			}
			break;
			case AbstractCursorInputEvt.INPUT_ENDED:{
//				logger.debug("Finger UP-> " + " ID:" + posEvt.getId() + "; X:" + posEvt.getPositionX() + " Y:" + posEvt.getPositionY() + "; Source: " + posEvt.getSource());
//				IMTComponent3D associatedObj = motionToObjectMap.get(m);
				IMTComponent3D associatedObj = cursorToObjectMap.remove(c);
				if (associatedObj != null){
					posEvt.setTarget(associatedObj);
//					posEvt.setCurrentTarget(associatedObj.getRoot());//Enable this if using event CAPTURING PHASE
					posEvt.setCurrentTarget(associatedObj);
					posEvt.setEventPhase(MTInputEvent.CAPTURING_PHASE);
					this.fireInputEvent(posEvt);
//					motionToObjectMap.remove(m);
				}
			}
			break;
			default:
				break;
			}
		}else{
			//Other event type, evtl ohne absolute x,y coordianten (z.b. joystick)
			//einfach an mtcanvas weiterleiten?
//			logger.error("Warning in " + this  + " Dont know how to handle evt: " + inputEvent );
			
			//Just fire other input events to the current canvas by default
			this.fireInputEvent(inputEvent);
		}
	}



}
