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
package org.mt4j.components.visibleComponents.widgets.keyboard;

import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.MTTextArea.ExpandDirection;
import org.mt4j.components.visibleComponents.widgets.buttons.MTSvgButton;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.LassoProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * A multitouch keyboard with an attached textfield that can be written to.
 * 
 * @author Christopher Ruff
 */
public class MTTextKeyboard extends MTKeyboard {
	
	/** The font for text field. */
	private IFont fontForTextField;
	
	/** The parent to add new text area to. */
	private MTComponent parentToAddNewTextAreaTo;
	
	/** The clustering gesture analyzer. */
	private LassoProcessor lassoProcessor;
	
	//Gesture Actions
	/** The default drag action. */
	private DefaultDragAction defaultDragAction;
//	private DefaultRotateAction defaultRotateAction;
//	private DefaultScaleAction defaultScaleAction;

	/** The drag from keyb action. */
	private DragTextAreaFromKeyboardAction dragFromKeybAction;

	/** The pa. */
	private PApplet pa;
	
	private ITextInputListener textInputListener;
	
	
	/**
	 * Creates a new keyboard with a default font for its textarea.
	 * 
	 * @param pApplet the applet
	 */
	public MTTextKeyboard(PApplet pApplet) {
		this(pApplet, FontManager.getInstance().createFont(pApplet, 
				"arial.ttf", 35, MTColor.BLACK)); 
//		this(pApplet, FontManager.getInstance().createFont(pApplet, "Eureka90.vlw", 35, new Color(0,0,0,255), new Color(0,0,0,255))); 
//		this(pApplet, FontManager.getInstance().createFont(pApplet, "arial", 35, new Color(0,0,0,255), new Color(0,0,0,255))); 
	}
	
	
	/**
	 * Creates a new keyboard with the specified font for its textarea.
	 * 
	 * @param pApplet the applet
	 * @param fontForTextField the font for text field
	 */
	public MTTextKeyboard(PApplet pApplet, IFont fontForTextField) {
		super(pApplet);
		this.pa = pApplet;
		
		this.fontForTextField = fontForTextField;
		
		lassoProcessor 	= null;
		
		//Set up gesture actions
		defaultDragAction 	= new DefaultDragAction();
//		defaultRotateAction = new DefaultRotateAction();
//		defaultScaleAction 	= new DefaultScaleAction();
		dragFromKeybAction 	= new DragTextAreaFromKeyboardAction();
		
		
		MTSvgButton newTextFieldSvg = new MTSvgButton(pa, MT4jSettings.getInstance().getDefaultSVGPath()
						+ "keybNewTextField.svg");
		newTextFieldSvg.setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
		newTextFieldSvg.scale(0.8f, 0.8f, 1, new Vector3D(0,0,0));
		newTextFieldSvg.translate(new Vector3D(10,5,0));

		newTextFieldSvg.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				MTComponent clickedComp = MTTextKeyboard.this;
				if (te.isTapped()){
					//should always be keyboard
					MTComponent parent = clickedComp.getParent();
					if (parent instanceof MTTextKeyboard){
						MTTextKeyboard keyboard = (MTTextKeyboard)parent;

						//Remove old Textfield from keyboard if there is any
						if (textInputListener != null){
							
							if (textInputListener instanceof MTTextArea){
								MTTextArea ta = (MTTextArea)textInputListener;
								ta.setGestureAllowance(DragProcessor.class, true);
								ta.setGestureAllowance(RotateProcessor.class, true);
								ta.setGestureAllowance(ScaleProcessor.class, true);
								
								ta.setEnableCaret(false);
								
								//Add to clusteranylyzer for clustering the tarea
								if (getLassoProcessor() != null){
									getLassoProcessor().addClusterable(ta);
								}

//								keyboard.setTextInputAcceptor(null);
								removeTextInputListener(textInputListener);
								textInputListener = null;
								
								ta.removeAllGestureEventListeners(DragProcessor.class);
								ta.addGestureListener(DragProcessor.class, defaultDragAction);
//								ta.unassignGestureClassAndAction(DragGestureAnalyzer.class);
//								ta.assignGestureClassAndAction(DragGestureAnalyzer.class, defaultDragAction);

								//The direction, in which the textarea will float off 
								Vector3D v = new Vector3D(0,-100, 0);

								//Add the textarea to the set parent
								if (getParentToAddNewTextAreaTo() != null){
									//Transform the textarea so it appears at the same world coords after its added to another parent
									Matrix m = MTComponent.getTransformToDestinationParentSpace(ta, getParentToAddNewTextAreaTo());
									ta.transform(m);
									//Transform the direction vector for the translation animation 
									//to preserve the direction from the old reference frame to the new parents one
//									v.transformNormal(m);
									v.transformDirectionVector(m);

									ta.tweenTranslate(v, 500, 0.3f, 0.7f);

									getParentToAddNewTextAreaTo().addChild(ta);
								}else{
									//If that isnt set, try to add it to the keyboards parent
									if (getParent() != null){
										/////////////////////////
										// Transform the textarea so it appears at the same place after its added to another parent
										Matrix m = MTComponent.getTransformToDestinationParentSpace(ta, getParent());
										ta.transform(m);
										//Transform the direction vector to preserve the global direction
										//from the old reference frame to the new parents one
										//The translation part has to be removed from the matrix because we're transforming 
										//a translation vector not a point vector
										v.transformDirectionVector(m);

										ta.tweenTranslate(v, 500, 0.3f, 0.7f);
										/////////////////////
										getParent().addChild(ta);
									}else{
										//do nothing..?
										throw new RuntimeException("Dont know where to add text area to!");
									}
								}
							}//if (text instanceof MTTextArea){
						}//if (keyboard.getTextInputAcceptor() != null){
						//Create a new textarea
						keyboard.createNewTextArea();
					}//if (parent instanceof MTTextKeyboard){
				}
				return false;
			}
		});
		this.addChild(newTextFieldSvg);
	}
	
//	@Override
//	protected void keyboardButtonDown(MTKey clickedKey, boolean shiftPressed){
//		ITextInputAcceptor textArea = getTextInputAcceptor();
//		if (textArea != null){
//			if (clickedKey.getCharacterToWrite().equals("back")){
//				textArea.removeLastCharacter();
//			}else if (clickedKey.getCharacterToWrite().equals("shift")){
//					//no nothing
//			}else{
//				String charToAdd = shiftPressed ? clickedKey.getCharacterToWriteShifted() : clickedKey.getCharacterToWrite();
//				textArea.appendCharByUnicode(charToAdd);
//			}
//		}
//	}
	

	/**
	 * Creates a new textarea at the keyboard.
	 * Fails if there is still one attached to it.
	 */
	public void createNewTextArea(){
		if (this.textInputListener == null){
			final MTTextArea t = new MTTextArea(pa, fontForTextField);
			this.textInputListener = t;
			
			t.setExpandDirection(ExpandDirection.UP);
			t.setStrokeColor(new MTColor(0,0 , 0, 255));
			t.setFillColor(new MTColor(205,200,177, 255));
			
			t.setGestureAllowance(DragProcessor.class, true);
			t.setGestureAllowance(RotateProcessor.class, false);
			t.setGestureAllowance(ScaleProcessor.class, false);
			
			t.removeAllGestureEventListeners(DragProcessor.class);
			t.addGestureListener(DragProcessor.class, dragFromKeybAction);
			
			t.setEnableCaret(true);
//			t.snapToKeyboard(this);
			snapToKeyboard(t);
			
			this.addTextInputListener(this.textInputListener);
			//Remove textarea from listening if destroyed
			t.addStateChangeListener(StateChange.COMPONENT_DESTROYED, new StateChangeListener() {
				public void stateChanged(StateChangeEvent evt) {
					removeTextInputListener(t);
				}
			});
		}else{
			System.err.println("Cant create new textarea - Keyboard still has a textarea attached.");
		}
	}
	


	/**
	 * Gets the parent to add new text area to.
	 * 
	 * @return the parent to add new text area to
	 */
	public MTComponent getParentToAddNewTextAreaTo() {
		return parentToAddNewTextAreaTo;
	}

	/**
	 * Determines to which parent the textarea of the keyboard will be added to after
	 * decoupling it from the keyboard.
	 * 
	 * @param parentToAddNewTextAreaTo the parent to add new text area to
	 */
	public void setParentToAddNewTextAreaTo(MTComponent parentToAddNewTextAreaTo) {
		this.parentToAddNewTextAreaTo = parentToAddNewTextAreaTo;
	}

	/**
	 * Gets the clustering gesture analyzer.
	 * 
	 * @return the clustering gesture analyzer
	 */
	public LassoProcessor getLassoProcessor() {
		return lassoProcessor;
	}

	/**
	 * Sets the clustering gesture analyzer.
	 * 
	 * @param clusteringGestureAnalyzer the new clustering gesture analyzer
	 */
	public void setLassoProcessor(LassoProcessor clusteringGestureAnalyzer) {
		this.lassoProcessor = clusteringGestureAnalyzer;
	}
	
	
	
	/**
	 * Gesture action class to be used when a textarea is dragged away from the keyboard.
	 * 
	 * @author C.Ruff
	 */
	private class DragTextAreaFromKeyboardAction extends DefaultDragAction {
		
		/* (non-Javadoc)
		 * @see com.jMT.input.gestureAction.DefaultDragAction#processGesture(com.jMT.input.inputAnalyzers.GestureEvent)
		 */
		public boolean processGestureEvent(MTGestureEvent g) {
			
			super.processGestureEvent(g);
			
			if (g.getId() == MTGestureEvent.GESTURE_ENDED){
				if (g instanceof DragEvent){
					DragEvent dragEvent = (DragEvent)g;
					
					if (dragEvent.getTarget() instanceof MTTextArea){
						MTTextArea text = (MTTextArea)dragEvent.getTarget();

						//Add default gesture actions to textfield
//						text.assignGestureClassAndAction(DragGestureAnalyzer.class, defaultDragAction);
//						text.assignGestureClassAndAction(ScaleGestureAnalyzer.class, defaultScaleAction);
//						text.assignGestureClassAndAction(RotateGestureAnalyzer.class, defaultRotateAction);
						
						text.setGestureAllowance(DragProcessor.class, true);
						text.setGestureAllowance(RotateProcessor.class, true);
						text.setGestureAllowance(ScaleProcessor.class, true);
						
						//Disable caret showing
						text.setEnableCaret(false);
						
						//Add to clusteranylyzer for clustering the tarea
						if (getLassoProcessor() != null){
							getLassoProcessor().addClusterable(text);
						}
						
						removeTextInputListener(textInputListener);
						textInputListener = null;
						
						text.removeAllGestureEventListeners(DragProcessor.class);
						text.addGestureListener(DragProcessor.class, defaultDragAction);
						
//						text.unassignGestureClassAndAction(DragGestureAnalyzer.class);
//						text.assignGestureClassAndAction(DragGestureAnalyzer.class, defaultDragAction);
						
//						/*
						//Add the textare to the set parent
						if (getParentToAddNewTextAreaTo() != null){
							text.transform(MTComponent.getTransformToDestinationParentSpace(text, getParentToAddNewTextAreaTo()));
							getParentToAddNewTextAreaTo().addChild(text);
						}else{
							//If that isnt set, try to add it to the keyboards parent
							if (getParent() != null){
								text.transform(MTComponent.getTransformToDestinationParentSpace(text, getParent()));
								getParent().addChild(text);
							}else{
								//do nothing..
//								throw new RuntimeException("Dont know where to add text area to!");
								System.err.println("Dont know where to add text area to!");
							}
						}
//						*/
					}
				}
			}
			return false;
		}
	}
	

}
