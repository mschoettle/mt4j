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
package org.mt4j.components.interfaces;

import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * The Interface IMTComponent.
 * @author Christopher Ruff
 */
public interface IMTComponent {

	/**
	 * Rotates the component around its z-axis and the rotation point (in world coordiantes).
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public void rotateZGlobal(Vector3D rotationPoint, float degree);
	
	/**
	 * Translates the object in the given direction relative to the
	 * global world coordinate frame.
	 * 
	 * @param directionVect the direction vect
	 */
	public void translateGlobal(Vector3D directionVect);
	
//	public void scale(float factor, Vector3D scaleReferencePoint);
	
	/**
 * Scales the component in world from the scalingpoint (in world coordiantes) in the 3 axis.
 * 
 * @param factorX the factor x
 * @param factorY the factor y
 * @param factorZ the factor z
 * @param scalingPoint the scaling point
 */
	public void scaleGlobal(float factorX, float factorY, float factorZ, Vector3D scalingPoint);
	
	//public Point getCenterPoint();
	
//	/**
//	 * Draw.
//	 */
//	public void draw(PGraphics g);
	
	
	/**
	 * Draws this component. (Not its children)
	 * 
	 * @param g the g
	 */
	public void drawComponent(PGraphics g);
	
	
	/**
	 * Updates the component.
	 * 
	 * @param timeDelta the time delta
	 */
	public void updateComponent(long timeDelta);
	
//	/**
//	 * Update.
//	 * 
//	 * @param timeDelta the time delta
//	 */
//	public void update(long timeDelta);
	
	/**
	 * Gets the iD.
	 * 
	 * @return the iD
	 */
	public int getID();
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 * 
	 * the name of this component
	 */
	public String getName();
	
	/**
	 * Sets the name of the component.
	 * 
	 * @param name the new name
	 */
	public void setName(String name);
	
	/**
	 * Gets the renderer.
	 * 
	 * @return the renderer
	 */
	public PApplet getRenderer();
	
	/**
	 * Checks if the component is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public boolean isEnabled();
	
	/**
	 * Sets the component enabled. If enabled,
	 * the component will process input events.
	 * 
	 * @param enabled the new enabled
	 */
	public void setEnabled(boolean enabled);
	
	/**
	 * Checks if the component is visible.
	 * 
	 * @return true, if is visible
	 */
	public boolean isVisible();
	
	/**
	 * Sets the visibility.
	 * 
	 * @param visible the new visible
	 */
	public void setVisible(boolean visible);
	
	
	/**
	 * Checks whether this, or this component's children contains the specified point.
	 * The point is assumed to be in global space coordinates.
	 * 
	 * @param testPoint the test point
	 * 
	 * @return true, if successful
	 */
	public boolean containsPointGlobal(Vector3D testPoint);

	
	/**
	 * Checks if the gesture is allowed on this component.
	 * 
	 * @param c The Class of the InputAnalyzer (Gesture Analyzer)
	 * 
	 * @return true, if this gesture is allowed
	 */
	public boolean isGestureAllowed(Class<? extends IInputProcessor> c);
	
	
	/**
	 * Processes the input event.
	 * 
	 * @param inEvt the in evt
	 * 
	 * @return true, if successful
	 */
	public boolean processInputEvent(MTInputEvent inEvt);
	
	public IMTComponent3D getRoot();
	
}
