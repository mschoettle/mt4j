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


import java.util.ArrayList;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.GestureUtils;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputSources.IinputSourceListener;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;


/**
 * The Class AbstractInputprocessor.
 * 
 * @author Christopher Ruff
 */
public abstract class AbstractGlobalInputProcessor implements IinputSourceListener, IInputProcessor {
	protected static final ILogger logger = MTLoggerFactory.getLogger(AbstractGlobalInputProcessor.class.getName());
	static{
		logger.setLevel(ILogger.ERROR);
	}
	
	/** if disabled. */
	private boolean disabled;
	
	private ArrayList<IMTInputEventListener> inputListeners;
	
	/**
	 * Instantiates a new abstract input processor.
	 */
	public AbstractGlobalInputProcessor() {
		this.disabled 	= false;
		inputListeners = new ArrayList<IMTInputEventListener>();
	} 

	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputSources.IinputSourceListener#processInputEvent(org.mt4j.input.test.MTInputEvent)
	 */
	final public boolean processInputEvent(MTInputEvent inputEvent){
		this.processInputEvtImpl(inputEvent);
		return true;
	}

	
	/**
	 * Process input evt implementation.
	 * 
	 * @param inputEvent the input event
	 */
	abstract public void processInputEvtImpl(MTInputEvent inputEvent);
	
	
	/**
	 * Checks if is disabled.
	 * 
	 * @return true, if is disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Sets the disabled.
	 * 
	 * @param disabled the new disabled
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}


	/**
	 * Adds the processor listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void addProcessorListener(IMTInputEventListener listener){
		if (!inputListeners.contains(listener)){
			inputListeners.add(listener);
		}
		
	}
	
	/**
	 * Removes the processor listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void removeProcessorListener(IMTInputEventListener listener){
		if (inputListeners.contains(listener)){
			inputListeners.remove(listener);
		}
	}
	
	/**
	 * Gets the processor listeners.
	 * 
	 * @return the processor listeners
	 */
	public synchronized IMTInputEventListener[] getProcessorListeners(){
		return inputListeners.toArray(new IMTInputEventListener[this.inputListeners.size()]);
	}
	
	/**
	 * Fire gesture event.
	 *
	 * @param ie the ie
	 */
	protected void fireInputEvent(MTInputEvent ie) {
		for (IMTInputEventListener listener : inputListeners){
			listener.processInputEvent(ie);
		}
	}
	
	////
	/**
	 * Gets the intersection point of a cursor and a specified component.
	 * Can return null if the cursor doesent intersect the component.
	 *
	 * @param app the app
	 * @param c the c
	 * @return the intersection
	 */
	public Vector3D getIntersection(PApplet app, InputCursor c){
		return GestureUtils.getIntersection(app, c.getTarget(), c);
	}
	
	/**
	 * Gets the intersection point of a cursor and a specified component.
	 * Can return null if the cursor doesent intersect the component.
	 *
	 * @param app the app
	 * @param component the component
	 * @param c the c
	 * @return the intersection
	 */
	public Vector3D getIntersection(PApplet app, IMTComponent3D component, InputCursor c){
		return GestureUtils.getIntersection(app, component, c);
	}
	
	public Vector3D getPlaneIntersection(PApplet app, Vector3D planeNormal, Vector3D pointInPlane, InputCursor c){
		return GestureUtils.getPlaneIntersection(app, planeNormal, pointInPlane, c);
	}
	///////////
	

	
}
