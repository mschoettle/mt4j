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


import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.mt4j.AbstractMTApplication;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.ArrayDeque;


/**
 * An abstract superclass for the abstraction of input sources.
 * Input source listener can listen to events from the inputsource.
 * 
 * @author Christopher Ruff
 */
public abstract class AbstractInputSource implements IPreDrawAction {
	
	/** The input listeners. */
	private ArrayList<IinputSourceListener> inputListeners;
	
	/** The event queue. */
	private ArrayDeque<MTInputEvent> eventQueue;
	
	private AbstractMTApplication app;
	
	private ArrayList<IinputSourceListener> inputProcessorsToFireTo;
	
	/**
	 * Instantiates a new abstract input source.
	 *
	 * @param mtApp the mt app
	 */
	public AbstractInputSource(AbstractMTApplication mtApp) {
		this.inputListeners = new ArrayList<IinputSourceListener>();
		this.eventQueue 	= new ArrayDeque<MTInputEvent>(20);
		
		this.app = mtApp;
		
		inputProcessorsToFireTo = new ArrayList<IinputSourceListener>(10);
	} 
	
	
	/**
	 * Called by the inputmanager when this inputsource is registered with the application.
	 * This method should not be invoked directly!
	 */
	public void onRegistered(){
//		app.registerPre(this); //Make processing call this class' pre() method at the beginning of each frame
		app.registerPreDrawAction(this);
	}
	
	/**
	 * Called by the inputmanager when this inputsource is unregistered from the application
	 * This method should not be invoked directly!
	 */
	public void onUnregistered(){
//		app.unregisterPre(this);
		app.unregisterPreDrawAction(this);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.IPreDrawAction#processAction()
	 */
	public void processAction() {
		pre();
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.IPreDrawAction#isLoop()
	 */
	public boolean isLoop() {
		return true;
	}
	
//	/**
//	 * Fires event type.
//	 * Determines if this particular input source fires the specified input event type.
//	 * 
//	 * @param evtClass the evt class
//	 * 
//	 * @return true, if it does.
//	 */
//	abstract public boolean firesEventType(Class<? extends MTInputEvent> evtClass);
	
	
	/**
	 * Queue input event for firing.
	 * They will be fired automatically before the next frame in snychronization with
	 * the render thread. Use this method instead of fireInputEvent()!
	 * 
	 * @param inputEvt the input evt
	 */
	protected void enqueueInputEvent(MTInputEvent inputEvt){ 
//		System.out.println("ENQUEUE EVENT: Cursor: " +  ((MTFingerInputEvt)inputEvt).getCursor().getId() + " Evt-ID: " +  ((MTFingerInputEvt)inputEvt).getId());
		this.eventQueue.addLast(inputEvt); //TODO synchronize access?
	}
	
	
	
	/**
	 * The input events have to be fired in processings (and openGL's) thread.
	 * Called by processing. This method should not be invoked directly!
	 */
	public void pre(){
		this.flushEvents();
	}
	
	
	/**
	 * Flushes the events.
	 * <p>NOTE: not threadsafe! Has to be called in the opengl thread if in opengl mode!
	 */
	public void flushEvents(){
		/*
		//FIXME DEBUG TEST REMOVE!
		int count = 0;
		for (Iterator<MTInputEvent> iterator = eventQueue.iterator(); iterator.hasNext();){
			MTInputEvent e = (MTInputEvent) iterator.next();
			if (e instanceof MTFingerInputEvt) {
				MTFingerInputEvt fe = (MTFingerInputEvt) e;
				if (fe.getId() == MTFingerInputEvt.INPUT_ENDED) {
					count++;
				}
			}
		}
		if (count >= 2){
			System.out.println("--2 INPUT ENDED QUEUED!--");
		}
		int iCount = 0;
		for (IinputSourceListener listener : inputListeners){  
			if (listener.isDisabled()){
				iCount++;
			}
		}
		if (iCount >= inputListeners.size() && !eventQueue.isEmpty()){
			System.out.println("--ALL INPUT PROCESSORS DISABLED!--");
		}
		*/
		
		if (!eventQueue.isEmpty()){
//			System.out.print("START FLUSH CURSOR: " + ((MTFingerInputEvt)eventQueue.peek()).getCursor().getId() + " Evt-ID: " + ((MTFingerInputEvt)eventQueue.peek()).getId()+ " ");
			//To ensure that all global input processors of the current scene
			//get the queued events even if through the result of one event processing the scene
			//gets changed and the currents scenes processors get disabled
			
			//Also ensure that all queued events get flushed to the scene although 
			//as a result this scenes input processors get disabled 
			//TODO do this only at real scene change? 
			//-> else if we disable input processors it may get delayed..
			
			//FIXME problem that this can get called twice - because called again in initiateScenechange
			inputProcessorsToFireTo.clear();
			for (IinputSourceListener listener : inputListeners){  
				if (!listener.isDisabled()){
					inputProcessorsToFireTo.add(listener);
				}
			}
			
			while (!eventQueue.isEmpty()){
				try {
					MTInputEvent te = eventQueue.pollFirst();
					this.fireInputEvent(te);
				} catch (NoSuchElementException e) {
					e.printStackTrace();
					//System.err.println(e.getMessage());
				} 
			}
//			System.out.println("END FLUSH");
		}
	}
	
	
	
	/**
	 * Fire input event.
	 * <br><b>Note:</b> This method should NOT be called directly.
	 * Use the <code>queueInputEvent</code> and flushEvents() methods instead!
	 * 
	 * @param inputEvt the input evt
	 */
	private void fireInputEvent(MTInputEvent inputEvt){
		//Adds the events to the cursors one by one before firing
		inputEvt.onFired();
		
		int length = inputProcessorsToFireTo.size();
		for (int i = 0; i < length; i++) {
			inputProcessorsToFireTo.get(i).processInputEvent(inputEvt);
		}

//        for (IinputSourceListener anInputProcessorsToFireTo : inputProcessorsToFireTo) {
//            anInputProcessorsToFireTo.processInputEvent(inputEvt);
//        }
		
		/*
		for (IinputSourceListener listener : inputListeners){
		listener.processInputEvent(inputEvt);
		}
		*/
	}
	
	
	/**
	 * Adds the input listener.
	 * @param listener the listener
	 */
	public synchronized void addInputListener(IinputSourceListener listener){
		if (!inputListeners.contains(listener)){
				inputListeners.add(listener);
		}
	}
	
	
	/**
	 * Removes the input listener.
	 * @param listener the listener
	 */
	public synchronized void removeInputListener(IinputSourceListener listener){
		if (inputListeners.contains(listener)){
			inputListeners.remove(listener);
		}
	}
	
	/**
	 * Gets the input listeners.
	 * @return the input listeners
	 */
	public synchronized IinputSourceListener[] getInputListeners(){
		return inputListeners.toArray(new IinputSourceListener[this.inputListeners.size()]);
	}
	
	

}
