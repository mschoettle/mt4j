/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.components.visibleComponents.widgets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * A slider widget. Allows to select a value between the specified minnimum and maximum values.
 * To recieve notice of a value change we can add a propertyChangeListener object to it.
 * <br>NOTE: The silder has to be created horizontally but can afterwards be rotated freely. 
 * To get a vertical slider, just rotate the slider locally around 90 degrees. 
 * 
 * @author Christopher Ruff
 */
public class MTSlider extends MTRectangle {
	
	/** The horizontal. */
	private boolean horizontal;
	
	/** The outer shape. */
	private AbstractShape outerShape;
	
	/** The slider. */
	private AbstractShape knob;

	/** The max value. */
	private float maxValue;
	
	/** The min value. */
	private float minValue;
	
	/** The value range. */
	private float valueRange;

	/** The x. */
	private float x;
	
	/** The y. */
	private float y;
	
	/** The inner padding. */
	private float innerPadding;
	
	/** The property change support. */
	private PropertyChangeSupport propertyChangeSupport;
	
	private PApplet app;
	
	//TODO display a MTLine for the knob to slide on?
	

	/**
	 * Instantiates a new mT slider.
	 *
	 * @param _x the _x
	 * @param _y the _y
	 * @param width the width
	 * @param height the height
	 * @param minValue the min value
	 * @param maxValue the max value
	 * @param applet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTSlider(float _x, float _y, float width, float height, float minValue, float maxValue, PApplet applet) {
		this(applet, _x, _y, width, height, minValue, maxValue);
	}
	
	
	/**
	 * Instantiates a new mT slider.
	 * @param applet the applet
	 * @param _x the _x
	 * @param _y the _y
	 * @param width the width
	 * @param height the height
	 * @param minValue the min value
	 * @param maxValue the max value
	 */
	public MTSlider(PApplet applet, float _x, float _y, float width, float height, float minValue, float maxValue) {
		super(applet, _x, _y, width, height);
		this.app = applet;
		
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		if (minValue> maxValue){
			System.err.println("Minimum value is bigger than the maximum value in " + this);
		}
		
		this.x = _x;
		this.y = _y;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.valueRange = maxValue-minValue;
		
		this.setNoFill(true);
		this.setNoStroke(true);
		this.setPickable(false);
		
		float knobWidth;
		float knobHeight;
		
		horizontal = true; //FIXME only horizontal is implemented at the moment
		if (horizontal){
			innerPadding = 2;
			knobHeight = height - 2*innerPadding;
			//Check if we can make the knob as broad as the slider height so we get a circle knob
			//But if the height is too big the knob width would exceed the sliders width
			if (height >= width){ //We check if slider height > slider width => not good!
				knobWidth = (width - 2*innerPadding)*0.4f;
//				if (knobWidth >= 2*innerPadding) //FIXME REMOVE?? why is that here..
//					knobWidth = 1.5f;
//				while (knobWidth >= 2*innerPadding){
//					knobWidth *= 0.9f;
//				}
			}else{
				knobWidth = knobHeight;
			}
		}else{
			innerPadding = 5;
			knobWidth = width - 2*innerPadding;
			knobHeight = knobWidth;
		}
		
//		outerShape = new MTRectangle(x,y, width, height, applet);
		outerShape = new MTRoundRectangle(applet,x,y, 0, width, height, knobWidth/2f + innerPadding, knobHeight/2f  + innerPadding);
		outerShape.unregisterAllInputProcessors();
		//When we click on the outershape move the knob in that direction a certain step
		outerShape.registerInputProcessor(new TapProcessor(applet, 35));
		outerShape.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				switch (te.getTapID()) {
				case TapEvent.TAPPED:
					Vector3D screenPos = te.getLocationOnScreen();
					Vector3D intersection = outerShape.getIntersectionGlobal(Tools3D.getCameraPickRay(app, outerShape, screenPos.x, screenPos.y));
					if (intersection != null){
						//Get the intersection point into knob local relative space 
						Vector3D localClickPos = knob.globalToLocal(intersection);
						Vector3D knobCenterLocal = knob.getCenterPointLocal();
						float range = getValueRange();
						float step = range/5f; //Arbitrary step value
						float oldValue = getValue();
						if (localClickPos.x < knobCenterLocal.x){
							setValue(oldValue - step); //Move knob Left
						}else if (localClickPos.x > knobCenterLocal.x){
							setValue(oldValue + step);  //Move knob Right
						}
					}
					break;
				}
				return false;
			}
		});
		
//		knob = new MTRectangle(x+innerOffset, y+innerOffset, 	knobWidth, knobHeight, applet);
		knob = new MTEllipse(applet,  new Vector3D(0,0,0), knobWidth*0.5f, knobHeight*0.5f);
		knob.setFillColor(new MTColor(140, 140, 140, 255));
		AbstractComponentProcessor[] inputPs = knob.getInputProcessors();
        for (AbstractComponentProcessor p : inputPs) {
            if (!(p instanceof DragProcessor)) {
                knob.unregisterInputProcessor(p);
            }
        }
		knob.removeAllGestureEventListeners(DragProcessor.class);
		
		outerShape.addChild(knob);
		this.addChild(outerShape);
		
		//TODO these have to be updated if knob or outershape are changed
//		final float knobWidthRelParent = knob.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
//		final float knobHeightRelParent = knob.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
//		final float outerWidthLocal = outerShape.getWidthXY(TransformSpace.LOCAL);
		
		knob.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			//@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				Vector3D dir = new Vector3D(de.getTranslationVect());
				//Transform the global direction vector into knob local coordiante space
				dir.transformDirectionVector(knob.getGlobalInverseMatrix());
				
				float oldValue = getValue();
				
				if (horizontal){
					knob.translate(new Vector3D(dir.x,0,0), TransformSpace.LOCAL);
					float knobWidthRelParent = knob.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
					float knobHeightRelParent = knob.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
					float outerWidthLocal = outerShape.getWidthXY(TransformSpace.LOCAL);
					 
					Vector3D knobCenterRelToParent = knob.getCenterPointRelativeToParent();
					//Cap the movement at both ends of the slider
					if( (knobCenterRelToParent.x + knobWidthRelParent*0.5f) > (x + outerWidthLocal - innerPadding) ){
						//FIXME we could insetead just set the new value with setValue()
//						System.out.println("OUT OF BOUNDS RIGHT ->");
						Vector3D pos = new Vector3D( x + outerWidthLocal - innerPadding -knobWidthRelParent*0.5f, y + knobHeightRelParent*0.5f + innerPadding, 0);
//						pos.transform(outerShape.getGlobalMatrix());
//						knob.setPositionGlobal(pos);
						pos.transform(outerShape.getLocalMatrix());
						knob.setPositionRelativeToParent(pos);
					}else if( (knobCenterRelToParent.x - knobWidthRelParent*0.5f ) < (x + innerPadding) ){
//						System.out.println("OUT OF BOUNDS LEFT <-");
						Vector3D pos = new Vector3D( x + knobWidthRelParent*0.5f  + innerPadding, y + knobHeightRelParent*0.5f + innerPadding, 0);
//						pos.transform(outerShape.getGlobalMatrix());
//						knob.setPositionGlobal(pos);
						pos.transform(outerShape.getLocalMatrix());
						knob.setPositionRelativeToParent(pos);
					}
					
//					System.out.println("Slider value: " + getValue());
					
					//Fire property change event
					if (propertyChangeSupport.hasListeners("value")){
						propertyChangeSupport.firePropertyChange("value", oldValue, getValue());	
					}
				}else{
					knob.translate(new Vector3D(0,dir.y,0), TransformSpace.LOCAL);
				}
				return false;
			}
		});
		
		//Default - Sets the current value to be the middle between min and max value (needed or knob may not appear correctly)
		this.setValue((minValue+maxValue)/2f);
	}
	
	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public float getValue(){
		float outerShapeWidthLocal = outerShape.getWidthXY(TransformSpace.LOCAL);
		float knobWidthRelParent = knob.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
		
		float leftMostPossibleKnobPosX = x + innerPadding + knobWidthRelParent * 0.5f;
		float rightMostPossibleKnobPosX = x + outerShapeWidthLocal - innerPadding - knobWidthRelParent * 0.5f;
		
		//in outershape local coords
		float slideableArea = rightMostPossibleKnobPosX-leftMostPossibleKnobPosX;
		float knobPosX = knob.getCenterPointRelativeToParent().x;
		float sliderAreaToValueAreaRatio = valueRange/slideableArea;
		
		float knobCurr = knobPosX-leftMostPossibleKnobPosX;
		float valueCurr = minValue +  knobCurr * sliderAreaToValueAreaRatio;
		
		valueCurr = ToolsMath.clamp(valueCurr, minValue, maxValue);
		/*
		System.out.println("sliderCurr: " + sliderCurr);
		System.out.println("ValueCurr: " + valueCurrr);
		*/
		/*//Show begin and end point of slider works only unrotated
		MTEllipse e1 = new MTEllipse(this.getRenderer(), new Vector3D(leftMostPossibleSliderPosX, y + slider.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)*0.5f),5,5);
		this.getRoot().addChild(e1);
		MTEllipse e2 = new MTEllipse(this.getRenderer(), new Vector3D(rightMostPossibleSliderPosX, y + slider.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)*0.5f),5,5);
		this.getRoot().addChild(e2);
		*/
		return valueCurr;
	}
	
	/**
	 * Sets the value.
	 * 
	 * @param value the new value
	 */
	public void setValue(float value){
		if (value > maxValue){
			value = maxValue;
		}else if (value < minValue){
			value = minValue;
		}
		
		float oldValue = this.getValue();
		
		float outerShapeWidthLocal = outerShape.getWidthXY(TransformSpace.LOCAL);
		float knobWidthRelParent = knob.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
		float leftMostPossibleknobPosX = x + innerPadding + knobWidthRelParent * 0.5f;
		float rightMostPossibleknobPosX = x + outerShapeWidthLocal - innerPadding - knobWidthRelParent * 0.5f;
		
		//in outershape local coords
		float slideableRange = rightMostPossibleknobPosX-leftMostPossibleknobPosX;
		float valueRangeToSliderValueRange = slideableRange/valueRange;
		
		float valueOffsetFromMinValue = Math.abs(value - minValue);
//		float knobValue = minValue +  value*valueAreaToSliderValueArea;
		float knobAdvanceFromLeftValue =  valueOffsetFromMinValue * valueRangeToSliderValueRange;
		
		Vector3D pos = new Vector3D( x + innerPadding + knobWidthRelParent*0.5f + knobAdvanceFromLeftValue , y + knob.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)*0.5f + innerPadding, 0);
//		pos.transform(outerShape.getGlobalMatrix());
//		knob.setPositionGlobal(pos);
		pos.transform(outerShape.getLocalMatrix());
		knob.setPositionRelativeToParent(pos);
		
//		System.out.println("slidervalue: " + sliderAdvanceFromLeftValue);
		
		//Fire property change event
		if (propertyChangeSupport.hasListeners("value")){
			this.propertyChangeSupport.firePropertyChange("value", oldValue, this.getValue());	
		}
	}
	
	/**
	 * Sets the value range.
	 * 
	 * @param min the min
	 * @param max the max
	 */
	public void setValueRange(float min, float max){
		if (minValue > maxValue){
			System.err.println("Minimum value is bigger than the maximum value in " + this);
		}
		
		float oldValue = this.getValue();
		float oldMin = this.minValue;
		float oldValueRange = valueRange;
		
		this.minValue = min;
		this.maxValue = max;
		this.valueRange = maxValue-minValue;
		
		//Keeping the relative slider andvancement
		float newValue = minValue + (valueRange * ((oldValue-oldMin)/oldValueRange));
		this.setValue(newValue);
//		this.setCurrentValue(minValue);
		
		//Fire property change event
		if (propertyChangeSupport.hasListeners("valueRange")){
			this.propertyChangeSupport.firePropertyChange("valueRange", oldValueRange, valueRange);
		}
	}

	
	/**
	 * Gets the value range.
	 * 
	 * @return the value range
	 */
	public float getValueRange(){
		return this.valueRange;
	}
	
	
	public float getMaxValue() {
		return this.maxValue;
	}

	public float getMinValue() {
		return this.minValue;
	}

	/**
	 * Gets the outer shape.
	 * 
	 * @return the outer shape
	 */
	public AbstractShape getOuterShape() {
		return outerShape;
	}

	/**
	 * Gets the knob.
	 * 
	 * @return the slider
	 */
	public AbstractShape getKnob() {
		return knob;
	}

	
	/**
	 * Adds the property change listener.
	 * The slider supports listening to the following properties changes.
	 * <li>"value"
	 * <li>"valueRange"
	 * 
	 * @param propertyName the property name
	 * @param listener the listener
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Gets the property change listeners.
	 * 
	 * @param propertyName the property name
	 * 
	 * @return the property change listeners
	 */
	public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
		return propertyChangeSupport.getPropertyChangeListeners(propertyName);
	}

	/**
	 * Removes the property change listener.
	 * 
	 * @param propertyName the property name
	 * @param listener the listener
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
	
	
	// DELEGATE APPEARANCE TO OUTERSHAPE SINCE THE SLIDER ITSELF ISNT DISPLAYED!
	@Override
	public void setFillColor(MTColor color) {
		super.setFillColor(color);
		this.getOuterShape().setFillColor(color);
	}
	@Override
	public void setStrokeColor(MTColor strokeColor) {
		super.setStrokeColor(strokeColor);
		this.getOuterShape().setStrokeColor(strokeColor);
	}
	@Override
	public void setStyleInfo(StyleInfo styleInfo) {
		super.setStyleInfo(styleInfo);
		this.getOuterShape().setStyleInfo(styleInfo);
	}
	@Override
	public void setTexture(PImage newTexImage) {
//		super.setTexture(newTexImage);
		this.getOuterShape().setTexture(newTexImage);
	}
	

}
