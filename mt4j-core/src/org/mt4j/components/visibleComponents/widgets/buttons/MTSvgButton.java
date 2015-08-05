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
package org.mt4j.components.visibleComponents.widgets.buttons;


import org.mt4j.components.MTComponent;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.interfaces.IclickableButton;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.input.gestureAction.DefaultSvgButtonClickAction;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;

import processing.core.PApplet;

//TODO extends from MTSvg
/**
 * The Class MTSvgButton.
 */
public class MTSvgButton extends MTSvg implements IclickableButton{
	
	/** The selected. */
	private boolean selected;
	
//	/** The registered action listeners. */
//	private ArrayList<ActionListener> registeredActionListeners;
	
	/**
	 * Instantiates a new mT svg button.
	 * @param pa the pa
	 * @param fileString the file string
	 */
	public MTSvgButton(PApplet pa, String fileString) {
		super(pa, fileString);
//		registeredActionListeners = new ArrayList<ActionListener>();
		
		this.setSvgOptions(this, AbstractShape.BOUNDS_CHECK_THEN_GEOMETRY_CHECK );
		
		this.setGestureAllowance(TapProcessor.class, true);
		this.registerInputProcessor(new TapProcessor(pa));
		this.addGestureListener(TapProcessor.class, new DefaultSvgButtonClickAction(this));
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting
		this.setDepthBufferDisabled(true);
	}
	
	
	/**
	 * Sets the bounds picking behaviour.
	 * 
	 * @param behaviour the new bounds picking behaviour
	 */
	public void setBoundsPickingBehaviour(int behaviour){
		this.setSvgOptions(this, behaviour);
	}
	
	
	/**
	 * Sets the svg options.
	 * 
	 * @param comp the comp
	 * @param boundsBehaviour the bounds behaviour
	 */
	private void setSvgOptions(MTComponent comp, int boundsBehaviour){
		comp.setGestureAllowance(DragProcessor.class, false);
		comp.setGestureAllowance(RotateProcessor.class, false);
		comp.setGestureAllowance(ScaleProcessor.class, false);
		comp.setGestureAllowance(TapProcessor.class, true);
		comp.setComposite(true);
		//Erste group nicht behandeln, aber kinder
		for(MTComponent child : comp.getChildren()){
			setSvgOptionsRecursive(child, boundsBehaviour);
		}
	}
	
	/**
	 * Sets the svg options recursive.
	 * 
	 * @param comp the comp
	 * @param boundsBehaviour the bounds behaviour
	 */
	private void setSvgOptionsRecursive(MTComponent comp, int boundsBehaviour){
		comp.setGestureAllowance(DragProcessor.class, false);
		comp.setGestureAllowance(RotateProcessor.class, false);
		comp.setGestureAllowance(ScaleProcessor.class, false);
		comp.setGestureAllowance(TapProcessor.class, false);
//		comp.unregisterAllInputProcessors();
//		/*
		if (comp instanceof AbstractShape){
			AbstractShape shape = (AbstractShape)comp;
			if (!shape.hasBounds() && !(shape.getBounds() instanceof BoundsZPlaneRectangle)){
				shape.setBounds(new BoundsZPlaneRectangle(shape));
			}
			shape.setBoundsBehaviour(boundsBehaviour);
		}
//		*/
		for(MTComponent child : comp.getChildren()){
			setSvgOptionsRecursive(child, boundsBehaviour);
		}
	}
	
	
//	//TODO listener stuff in abstract superclass!
//
//	/**
//	 * Adds the action listener.
//	 * 
//	 * @param listener the listener
//	 */
//	public synchronized void addActionListener(ActionListener listener){
//		if (!registeredActionListeners.contains(listener)){
//			registeredActionListeners.add(listener);
//		}
//	}
//	
//	/**
//	 * Removes the action listener.
//	 * 
//	 * @param listener the listener
//	 */
//	public synchronized void removeActionListener(IinputSourceListener listener){
//		if (registeredActionListeners.contains(listener)){
//			registeredActionListeners.remove(listener);
//		}
//	}
//	
//	/**
//	 * Gets the action listeners.
//	 * 
//	 * @return the action listeners
//	 */
//	public synchronized ActionListener[] getActionListeners(){
//		return registeredActionListeners.toArray(new ActionListener[this.registeredActionListeners.size()]);
//	}
//	
//	protected void fireActionPerformed() {
//		ActionListener[] listeners = this.getActionListeners();
//		synchronized(listeners) {
//			for (int i = 0; i < listeners.length; i++) {
//				ActionListener listener = (ActionListener)listeners[i];
//				
//				listener.actionPerformed(new ActionEvent(this, ClickEvent.BUTTON_CLICKED, "action performed on tangible button"));
//			}
//		}
//	}
//	
//
//	/**
//	 * fires an action event with a ClickEvent Id as its ID.
//	 * 
//	 * @param ce the ce
//	 */
//	public synchronized void fireActionPerformed(TapEvent ce) {
//		ActionListener[] listeners = this.getActionListeners();
//        for (ActionListener listener : listeners) {
//            listener.actionPerformed(new ActionEvent(this, ce.getTapID(), "action performed on tangible button"));
//        }
//	}


	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IclickableButton#setSelected(boolean)
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IclickableButton#isSelected()
	 */
	public boolean isSelected() {
		return this.selected;
	}


	/**
	 * Disable and delete children display lists.
	 */
	public void disableAndDeleteChildrenDisplayLists() {
		MTComponent[] childs = this.getChildren();
        for (MTComponent child : childs) {
            if (child instanceof MTPolygon) {
                MTPolygon poly = (MTPolygon) child;
                poly.disableAndDeleteDisplayLists();
            }
        }
	}

}
