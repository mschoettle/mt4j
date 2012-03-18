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
package org.mt4j.components.visibleComponents.widgets.progressBar;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.input.IMTEventListener;
import org.mt4j.input.MTEvent;




/**
 * The Class AbstractProgressThread.
 * 
 * @author C.Ruff
 */
public abstract class AbstractProgressThread extends Thread implements IprogressInfoProvider {
	
	/** The target. */
	private float target;
	
	/** The current. */
	private float current;
	
	/** The finished. */
	private boolean finished; 
	
	/** The percentage finished. */
	private float percentageFinished;
	
	/** The sleep time. */
	private long sleepTime;
	
	/** The current action. */
	private String currentAction;
	
	/** The auto compute percentage. */
	private boolean autoComputePercentage;
	
	/** The loading finished listeners. */
	private List<IMTEventListener> loadingFinishedListeners;
	
	/**
	 * Instantiates a new abstract progress thread.
	 * 
	 * @param sleepTime the sleep time
	 */
	public AbstractProgressThread(long sleepTime){
		this.percentageFinished = 0.0f;
		this.current 	= 0;
		this.target 	= 1;
		this.sleepTime = sleepTime;
		this.finished = false;
		this.currentAction = "Loading...";
		
		this.autoComputePercentage = true;
		
		this.loadingFinishedListeners = new ArrayList<IMTEventListener>();
	}


	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	abstract public void run();
	
//	abstract public boolean runConditionSatisfied();
	

	/**
 * Sets the value the current progress is at.
 * <br>Fires a finished event if new current value is == target.
 * 
 * @param current the current
 */
	public void setCurrent(float current) {
		this.current = current;
		
		if (this.current == this.target){
			this.setFinished(true);
		}
	}

	/* (non-Javadoc)
	 * @see com.jMT.components.visibleComponents.progressBar.IprogressInfoProvider#getCurrent()
	 */
	public float getCurrent() {
		return current;
	}
	
	/**
	 * Sets this progress to finished and fires finished event.
	 * 
	 * @param finished the finished
	 */
	public void setFinished(boolean finished) {
		this.fireEvent(new MTEvent(this));
		this.finished = finished;
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.jMT.components.visibleComponents.progressBar.IprogressInfoProvider#isFinished()
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Sets the percentage finished.
	 * 
	 * @param percentageFinished the new percentage finished
	 */
	private void setPercentageFinished(float percentageFinished) {
		this.percentageFinished = percentageFinished;
	}
	
	
	/* (non-Javadoc)
	 * @see com.jMT.components.visibleComponents.progressBar.IprogressInfoProvider#getPercentageFinished()
	 */
	public float getPercentageFinished() {
		if (this.autoComputePercentage){
			this.setPercentageFinished(100f/ this.getTarget() * this.getCurrent());
			return this.percentageFinished;
		}else{
			return percentageFinished;
		}
	}
	
	/**
	 * Sets the sleep time.
	 * 
	 * @param sleepTime the new sleep time
	 */
	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	/**
	 * Sets the value, the progress has to reach.
	 * 
	 * @param target the target
	 */
	public void setTarget(float target) {
		this.target = target;
	}

	
	/* (non-Javadoc)
	 * @see com.jMT.components.visibleComponents.progressBar.IprogressInfoProvider#getTarget()
	 */
	public float getTarget() {
		return target;
	}
	
	
	/**
	 * Checks if is auto compute percentage.
	 * 
	 * @return true, if is auto compute percentage
	 */
	public boolean isAutoComputePercentage() {
		return autoComputePercentage;
	}

	/**
	 * Sets the auto compute percentage.
	 * 
	 * @param autoComputePercentage the new auto compute percentage
	 */
	public void setAutoComputePercentage(boolean autoComputePercentage) {
		this.autoComputePercentage = autoComputePercentage;
	}


	/* (non-Javadoc)
	 * @see com.jMT.components.visibleComponents.progressBar.IprogressInfoProvider#getCurrentAction()
	 */
	public String getCurrentAction() {
		return currentAction;
	}

	/**
	 * Sets a title or description of the action currently processed.
	 * 
	 * @param currentAction the current action
	 */
	public void setCurrentAction(String currentAction) {
		this.currentAction = currentAction;
	}
	



	/**
	 * Gets the sleep time.
	 * 
	 * @return the sleep time
	 */
	public long getSleepTime() {
		return sleepTime;
	}

	
/////////////////////////////////////////////////////	
	/**
 * Fire event.
 * 
 * @param e the e
 */
protected void fireEvent(MTEvent e) {
		synchronized(loadingFinishedListeners) {
			for (int i = 0; i < loadingFinishedListeners.size(); i++) {
				IMTEventListener listener = loadingFinishedListeners.get(i);
				listener.processMTEvent(e);
			}
		}
	}
	
	/**
	 * Adds the progress finished listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void addProgressFinishedListener(IMTEventListener listener){
		if (!loadingFinishedListeners.contains(listener)){
			loadingFinishedListeners.add(listener);
		}
	}
	
	/**
	 * Removes the listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void removeListener(IMTEventListener listener){
		if (loadingFinishedListeners.contains(listener)){
			loadingFinishedListeners.remove(listener);
		}
	}
	
	/**
	 * Gets the listeners.
	 * 
	 * @return the listeners
	 */
	public synchronized IMTEventListener[] getListeners(){
		return loadingFinishedListeners.toArray(new IMTEventListener[this.loadingFinishedListeners.size()]);
	}
/////////////////////////////////////////////////////	
	
}
