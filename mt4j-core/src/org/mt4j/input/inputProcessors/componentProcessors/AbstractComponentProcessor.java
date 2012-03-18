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
package org.mt4j.input.inputProcessors.componentProcessors;

import java.util.ArrayList;

import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;

/**
 * The Class AbstractComponentProcessor.
 * @author Christopher Ruff
 */
public abstract class AbstractComponentProcessor implements IMTInputEventListener, IInputProcessor,  Comparable<AbstractComponentProcessor> {
	protected static final ILogger logger = MTLoggerFactory.getLogger(AbstractComponentProcessor.class.getName());
	static{
		logger.setLevel(ILogger.ERROR);
//		logger.setLevel(ILogger.WARN);
//		logger.setLevel(ILogger.DEBUG);
	}

	
	/** if disabled. */
	private boolean disabled;

	/** The input listeners. */
	private ArrayList<IGestureEventListener> inputListeners;

	/** The debug. */
	private boolean debug;
	
	private boolean stopPropagation;
	

	

	/**
	 * Instantiates a new abstract component input processor.
	 */
	public AbstractComponentProcessor() {
		this(false);
	}

	/**
	 * Instantiates a new abstract component processor.
	 *
	 * @param stopPropagation indicates whether to stop event bubbling
	 */
	public AbstractComponentProcessor(boolean stopPropagation){
		this.inputListeners = new ArrayList<IGestureEventListener>();
		this.disabled = false;
		this.debug = false;
		this.stopPropagation = stopPropagation;
		this.bubbledEventsEnabled = false;
	}


	/* (non-Javadoc)
	 * @see org.mt4j.input.IMTInputEventListener#processInputEvent(org.mt4j.input.inputData.MTInputEvent)
	 */
	public boolean processInputEvent(MTInputEvent inEvt){
//	public void processInputEvent(MTInputEvent inEvt){
		if(!disabled && inEvt.hasTarget()){ //Allow component processors to recieve inputevts only if they have a target (Canvas is target if null is picked..)
			
			//FIXME TEST
			if (this.bubbledEventsEnabled  ||  (inEvt.getEventPhase() == MTInputEvent.AT_TARGET)){
				this.processInputEvtImpl(inEvt);
			}
			
			//FIXME TEST 
			if (this.stopPropagation){
				inEvt.stopPropagation();
			}
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * Pre process. Called before {@link #processInputEvent(MTInputEvent)} is called.
	 *
	 * @param inEvt the in evt
	 */
	public void preProcess(MTInputEvent inEvt) {
		//FIXME TEST
		if(!disabled && inEvt.hasTarget()){
			if (bubbledEventsEnabled  ||  (inEvt.getEventPhase() == MTInputEvent.AT_TARGET) ){
				preProcessImpl(inEvt);
			}
		}
	}
	
	
		//FIXME test
	private boolean bubbledEventsEnabled;
	public boolean isBubbledEventsEnabled() {
		return bubbledEventsEnabled;
	}
	public void setBubbledEventsEnabled(boolean enableForBubbledEvents) {
		this.bubbledEventsEnabled = enableForBubbledEvents;
	}
	////
	
	
	/**
	 * Process input evt implementation.
	 * 
	 * @param inputEvent the input event
	 */
	abstract protected void processInputEvtImpl(MTInputEvent inputEvent);
	
	abstract protected void preProcessImpl(MTInputEvent inputEvent);
	
	
	
	/**
	 * Checks if this input processor is interested in the specified
	 * MTInputEvent instance.
	 * If we want to create custom input processors we override this method
	 * and return true only for the kind of events we want to recieve.
	 * 
	 * @param inputEvt the input evt
	 * 
	 * @return true, if is interested in
	 */
	abstract public boolean isInterestedIn(MTInputEvent inputEvt);
	
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	abstract public String getName();
	
	
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
	public synchronized void addGestureListener(IGestureEventListener listener){
		if (!inputListeners.contains(listener)){
			inputListeners.add(listener);
		}
		
	}
	
	/**
	 * Removes the processor listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void removeGestureListener(IGestureEventListener listener){
		if (inputListeners.contains(listener)){
			inputListeners.remove(listener);
		}
	}
	
	/**
	 * Gets the processor listeners.
	 * 
	 * @return the processor listeners
	 */
	public synchronized IGestureEventListener[] getGestureListeners(){
		return inputListeners.toArray(new IGestureEventListener[this.inputListeners.size()]);
	}
	
	/**
	 * Fire gesture event.
	 * 
	 * @param ge the ge
	 */
	protected void fireGestureEvent(MTGestureEvent ge) {
//		/*
		if (debug){
			switch (ge.getId()) {
			case MTGestureEvent.GESTURE_STARTED:
				System.out.println(((AbstractComponentProcessor)ge.getSource()).getName() +  " fired GESTURE_STARTED");
				break;
			case MTGestureEvent.GESTURE_UPDATED:
				System.out.println(((AbstractComponentProcessor)ge.getSource()).getName() +  " fired GESTURE_UPDATED");
				break;
			case MTGestureEvent.GESTURE_ENDED:
				System.out.println(((AbstractComponentProcessor)ge.getSource()).getName() +  " fired GESTURE_ENDED");
				break;
			default:
				break;
			}
		}
//		 */
		
		for (IGestureEventListener listener : inputListeners){
			listener.processGestureEvent(ge);
		}
	}


	/**
	 * Sets the debug.
	 * 
	 * @param debug the new debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	
	
	public int compareTo(AbstractComponentProcessor o) {
		return -1;
	}

	
	public boolean isStopPropagation() {
		return this.stopPropagation;
	}

	public void setStopPropagation(boolean stopPropagation) {
		this.stopPropagation = stopPropagation;
	}
	
	

}
