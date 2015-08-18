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
package org.mt4j.components.visibleComponents.widgets;

import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.buttons.MTSvgButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.Animation;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.MultiPurposeInterpolator;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Class MTImage. A widget which can be used to display an image texture surrounded
 * by a frame.
 * The image itself is actually a child of this class, which acts as the frame.
 * 
 * @author Christopher Ruff
 */
public class MTImage extends MTRectangle {
	
	private MTRectangle image;
	
	private MTComponent closeButton;
	
	/**
	 * Instantiates a new framed image.
	 *
	 * @param texture the texture
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTImage(PImage texture, PApplet pApplet) {
		this(pApplet, texture);
	}
	
	/**
	 * Instantiates a new framed image.
	 * @param pApplet the applet
	 * @param texture the texture
	 */
	public MTImage(PApplet pApplet, PImage texture) {
		super(pApplet, -7, -7, texture.width + 14, texture.height + 14);
		
		image = new MTRectangle(pApplet, texture);
		image.setStrokeColor(new MTColor(255,255,255,255));
		image.setPickable(false);
		this.addChild(image);
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting
		this.setDepthBufferDisabled(true);
	}
	
	public MTRectangle getImage(){
		return this.image;
	}
	

	/**
	 * Sets the display close button.
	 * 
	 * @param dispClose the new display close button
	 */
	public void setDisplayCloseButton(boolean dispClose){
		if (this.closeButton != null){
			if (!dispClose){
				//Remove svg button and destroy child display lists
				MTComponent[] childs = this.getChildren();
	            for (MTComponent component : childs) {
	                if (component.getName().equals("closeButton")) {
	                    MTSvgButton svgButton = (MTSvgButton) component;
	                    svgButton.destroy();
	                }
	            }
			}
		}else{
			if (dispClose){
				MTSvgButton keybCloseSvg = new MTSvgButton(this.getRenderer(), MT4jSettings.getInstance().getDefaultSVGPath()
						+ "keybClose.svg");
				keybCloseSvg.scale(0.5f, 0.5f, 1, new Vector3D(0,0,0));
				keybCloseSvg.translate(new Vector3D(this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT) - 45, 2,0));
				keybCloseSvg.setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
				keybCloseSvg.addGestureListener(TapProcessor.class, new CloseActionListener(new MTComponent[]{this, keybCloseSvg}) );
				
				keybCloseSvg.setName("closeButton");
				this.addChild(keybCloseSvg);
				this.closeButton = keybCloseSvg;
				this.closeButton.addStateChangeListener(StateChange.COMPONENT_DESTROYED, new StateChangeListener() {
					@Override
					public void stateChanged(StateChangeEvent evt) {
						closeButton = null;
					}
				});
			}
		}
		
	}
	



	/**
	 * The Class CloseActionListener. 
	 * 
	 * @author Cruff
	 */
	private class CloseActionListener implements IGestureEventListener{
			/** The comps. */
			public MTComponent[] comps;
			
			/** The reference poly for resizing the button. */
			private MTPolygon referencePoly;
			
			/**
			 * Instantiates a new close action listener.
			 * 
			 * @param comps the comps
			 */
			public CloseActionListener(MTComponent[] comps) {
				super();
				this.comps = comps;
			}

			
			/**
			 * Resize.
			 * 
			 * @param referenceComp the reference comp
			 * @param compToResize the comp to resize
			 * @param width the width
			 * @param height the height
			 */
			protected void resize(MTPolygon referenceComp, MTComponent compToResize, float width, float height){ 
				Vector3D centerPoint = getRefCompCenterRelParent(referenceComp);
				compToResize.scale(1/referenceComp.getWidthXY(TransformSpace.RELATIVE_TO_PARENT), (float)1/referenceComp.getWidthXY(TransformSpace.RELATIVE_TO_PARENT), 1, centerPoint, TransformSpace.RELATIVE_TO_PARENT);
				compToResize.scale(width, width, 1, centerPoint, TransformSpace.RELATIVE_TO_PARENT);
			}
			
			
			/**
			 * Gets the ref comp center local.
			 * 
			 * @param shape the shape
			 * 
			 * @return the ref comp center local
			 */
			protected Vector3D getRefCompCenterRelParent(AbstractShape shape){
				Vector3D centerPoint;
				if (shape.hasBounds()){
					centerPoint = shape.getBounds().getCenterPointLocal();
					centerPoint.transform(shape.getLocalMatrix()); //macht den punkt in self space
				}else{
					Vector3D localObjCenter = shape.getCenterPointGlobal();
					localObjCenter.transform(shape.getGlobalInverseMatrix()); //to localobj space
					localObjCenter.transform(shape.getLocalMatrix()); //to parent relative space
					centerPoint = localObjCenter;
				}
				return centerPoint;
			}

			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				switch (te.getTapID()) {
				case TapEvent.TAPPED:
					//Get the first polygon type out of the array
                    for (MTComponent comp : comps) { //TODO this is stupid.. redo this whole thing
                        if (comp instanceof MTPolygon) {
                            MTPolygon poly = (MTPolygon) comp;
                            if (referencePoly == null) {//nur 1. occur zuweisen
                                referencePoly = poly;
                            }
                        }
                    }
					float width = referencePoly.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);

					IAnimation closeAnim = new Animation("comp Fade", new MultiPurposeInterpolator(width, 1, 300, 0.5f, 0.8f, 1), referencePoly);
					closeAnim.addAnimationListener(new IAnimationListener(){
						public void processAnimationEvent(AnimationEvent ae) {
							switch (ae.getId()) {
							case AnimationEvent.ANIMATION_STARTED:
							case AnimationEvent.ANIMATION_UPDATED:
								float currentVal = ae.getAnimation().getValue();
								resize(referencePoly, comps[0], currentVal, currentVal);
								break;
							case AnimationEvent.ANIMATION_ENDED:
								comps[0].setVisible(false);
								for (int i = comps.length-1; i >0 ; i--) {
									MTComponent currentComp =  comps[i];
									//Call destroy which fires a destroy state change event
									currentComp.destroy();
									//System.out.println("destroyed: " + currentComp.getName());
								}
								destroy();
								//System.out.println("destroyed: " + getName());
								break;	
							default:
								destroy();
								break;
							}//switch
						}//processanimation
					});//new IAnimationListener
					closeAnim.start(); 
					break;
				default:
					break;
				}//switch aeID
				return false;
			}
	}//Class closebutton actionlistener
	
	
	
}
