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

import org.mt4j.components.MTCanvas;
import org.mt4j.sceneManagement.transition.ITransition;
import org.mt4j.util.camera.Icamera;

import processing.core.PGraphics;



/**
 * The Interface Iscene.
 * Represents a scene in a program.
 * 
 * @author Christopher Ruff
 */
public interface Iscene {
	
//	/**
//	 * Inits the scene.
//	 */
//	public void init();
//	
//	/**
//	 * Shut down scene.
//	 */
//	public void shutDown();

	/**
	 * Is invoked on a scene just before it is set to be the currently active scene.
	 */
	public void onEnter();

	/**
	 * Gets called on the current scene just before the current scene becomes inactive in favor of another scene.
	 */
	public void onLeave();
	
	/**
	 * Gets the main canvas.
	 * 
	 * @return the main canvas
	 */
	public MTCanvas getCanvas();
	
	/**
	 * Gets the scene cam.
	 * 
	 * @return the scene cam
	 */
	public Icamera getSceneCam();
	
//	public void update(long timeDelta);
	
//	public void draw();
	
	//if you use this, dont use draw(); and update(..);!
	/**
	 * Draw and update.
	 * @param graphics 
	 * 
	 * @param timeDelta the time delta
	 */
	public void drawAndUpdate(PGraphics graphics, long timeDelta);
	
	/**
	 * Shedule a action to be processed before the next drawing.
	 * 
	 * @param action the action
	 */
	public void registerPreDrawAction(IPreDrawAction action);
		
	
	/**
	 * Gets the name of the scene.
	 * 
	 * @return the name
	 */
	public String getName();
	
	
	/**
	 * Gets the transition.
	 * 
	 * @return the transition
	 */
	public ITransition getTransition();
	
	
	/**
	 * Destroy the scene.
	 */
	public boolean destroy();
	
	
}
