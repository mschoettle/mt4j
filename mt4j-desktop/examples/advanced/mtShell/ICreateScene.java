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
package advanced.mtShell;

import org.mt4j.sceneManagement.Iscene;

/**
 * The Interface ICreateScene.
 * 
 * @author Christopher Ruff
 */
public interface ICreateScene {
	
	/**
	 * Gets the new scene.
	 * 
	 * @return the new scene
	 */
	public Iscene getNewScene();
	
	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle();
}
