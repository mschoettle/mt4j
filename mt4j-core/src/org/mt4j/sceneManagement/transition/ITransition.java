/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.sceneManagement.transition;

import org.mt4j.sceneManagement.Iscene;

/**
 * The Interface ITransition. Interface for scene transition effects
 * 
 * @author Christopher Ruff
 */
public interface ITransition extends Iscene {

	/**
	 * Checks if is finished.
	 * 
	 * @return true, if is finished
	 */
	public boolean isFinished();

	//	public void init(Iscene oldScene, Iscene newScene);

	//	public void init();
	//	
	//	public void shutDown();

	/**
	 * Setup.
	 * 
	 * @param lastScene the last scene
	 * @param nextScene the next scene
	 */
	public void setup(Iscene lastScene, Iscene nextScene);

}
