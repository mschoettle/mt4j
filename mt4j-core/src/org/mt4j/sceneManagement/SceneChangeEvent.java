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

import org.mt4j.input.MTEvent;

/**
 * The Class SceneChangeEvent.
 * @author Christopher Ruff
 */
public class SceneChangeEvent extends MTEvent {
	
	/** The last scene. */
	private Iscene lastScene;
	
	/** The new scene. */
	private Iscene newScene;
	

	/**
	 * Instantiates a new scene change event.
	 * 
	 * @param source the source
	 * @param lastScene the last scene
	 * @param newScene the new scene
	 */
	public SceneChangeEvent(Object source,Iscene lastScene, Iscene newScene) {
		super(source);
		this.lastScene = lastScene;
		this.newScene = newScene;
	}


	/**
	 * Gets the last scene.
	 * 
	 * @return the last scene
	 */
	public Iscene getLastScene() {
		return lastScene;
	}

	/**
	 * Gets the new scene.
	 * 
	 * @return the new scene
	 */
	public Iscene getNewScene() {
		return newScene;
	}

	
}
