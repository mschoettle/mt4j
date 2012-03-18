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
package org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;



/**
 * The Class ClusteringEvent.
 * @author Christopher Ruff
 */
public class LassoEvent extends MTGestureEvent {
	
	/** The selection poly. */
	private MTPolygon selectionPoly;
	
	/** The motion. */
	private InputCursor motion;
	
	/** The selected comps. */
	private ILassoable[] selectedComps;
	

	/**
	 * Instantiates a new clustering event.
	 * 
	 * @param source the source
	 * @param id the id
	 * @param targetComponent the target component
	 * @param motion the motion
	 * @param selectionPoly the selection poly
	 * @param selectedComponents the selected components
	 */
	public LassoEvent(IInputProcessor source, int id, IMTComponent3D targetComponent, InputCursor motion, MTPolygon selectionPoly, ILassoable[] selectedComponents) {
		super(source, id, targetComponent);
		this.motion = motion;
		this.selectionPoly = selectionPoly;
		this.selectedComps = selectedComponents;
	}


	/**
	 * Gets the motion.
	 * 
	 * @return the motion
	 */
	public InputCursor getCursor() {
		return motion;
	}


	/**
	 * Gets the clustered components.
	 * 
	 * @return the clustered components
	 */
	public ILassoable[] getClusteredComponents() { 
		return selectedComps;
	}


	/**
	 * Gets the selection poly.
	 * 
	 * @return the selection poly
	 */
	public MTPolygon getSelectionPoly() {
		return selectionPoly;
	}
	
	

}
