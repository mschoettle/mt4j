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

import org.mt4j.AbstractMTApplication;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;
import org.mt4j.sceneManagement.AbstractScene;

/**
 * The Class AbstractTransition.
 * 
 * @author Christopher Ruff
 */
public abstract class AbstractTransition extends AbstractScene implements ITransition {
	/**
	 * Instantiates a new abstract transition.
	 * 
	 * @param mtApplication the mt application
	 * @param name the name
	 */
	public AbstractTransition(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);

		//Remove all global input processors - we dont want the transition to respond to input
		AbstractGlobalInputProcessor[] inputProcessors = this.getGlobalInputProcessors();
        for (AbstractGlobalInputProcessor abstractGlobalInputProcessor : inputProcessors) {
            this.unregisterGlobalInputProcessor(abstractGlobalInputProcessor);
        }
	}

	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.AbstractScene#registerDefaultGlobalInputProcessors()
	 */
	@Override 
	protected void registerDefaultGlobalInputProcessors() {  } //DONT REGISTER INPUT PROCESSORS!

	@Override
	public void onEnter() {
	}
	
	@Override
	public abstract void onLeave();


}
