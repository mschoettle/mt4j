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
package org.mt4j.sceneManagement;

import org.mt4j.components.MTComponent;

/**
 * This class can be used to add a component to another one from a 
 * different thread than the main drawing thread.
 * The action has to be registered with a scene to take effect the next time
 * the draw method is called on that scene.
 * 
 * @author Christopher Ruff
 */
public class AddNodeActionThreadSafe implements IPreDrawAction {
	
	/** The future parent. */
	private MTComponent futureParent;
	
	/** The object to add. */
	private MTComponent objectToAdd;
	
	/**
	 * Instantiates a new adds the node action thread safe.
	 * 
	 * @param objectToAdd the object to add
	 * @param futureParent the future parent
	 */
	public AddNodeActionThreadSafe(MTComponent objectToAdd, MTComponent futureParent){
		this.futureParent = futureParent;
		this.objectToAdd = objectToAdd;
	}
	
	/* (non-Javadoc)
	 * @see com.jMT.sceneManagement.IPreDrawAction#processAction()
	 */
	public void processAction() {
		futureParent.addChild(objectToAdd);
	}

	/* (non-Javadoc)
	 * @see com.jMT.sceneManagement.IPreDrawAction#isLoop()
	 */
	public boolean isLoop() {
		return false;
	}
	
}
