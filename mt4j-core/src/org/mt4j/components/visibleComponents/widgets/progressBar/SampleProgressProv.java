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



/**
 * The Class SampleProgressProv.
 */
public class SampleProgressProv extends AbstractProgressThread implements Runnable{

	/**
	 * Instantiates a new sample progress prov.
	 * 
	 * @param sleepTime the sleep time
	 */
	public SampleProgressProv(int sleepTime) {
		super(sleepTime);
		
		this.setTarget(10);
	}

	/**
	 * Run thread loop.
	 * 
	 * @return true, if successful
	 */
	private boolean runThreadLoop(){
		return (this.getCurrent() < this.getTarget());
	}
	
	/* (non-Javadoc)
	 * @see com.jMT.components.visibleComponents.progressBar.AbstractProgressThread#run()
	 */
	@Override
	public void run() {
		while (runThreadLoop()){
			try {
				//SleepTime is the amount of time the thread waits 
				//_at least_ to give other threads a chance to execute
				//smaller sleepTime -> thread runs faster, but other threads slower
				Thread.sleep(this.getSleepTime()); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//DO ONE ITERATION OF THE THREADS WORK
		    /*
				synchronized(das object auf das mehrere threads zugreifen wollen){
					mache änderungen am object;
				}
				//TODO wie funktioniert das mit notifyAll() und wait?
		     */
			this.setCurrent(this.getCurrent()+1);
			
//			System.out.println(this.getName() + ": " + current);
		}
		
		this.setFinished(true); //richtig?
//		System.out.println(this.getName() + " thread exiting.");
	}

}
