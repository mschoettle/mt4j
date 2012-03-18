package org.mt4j.input.inputProcessors.globalProcessors;

import java.util.HashMap;
import java.util.Map;


import org.mt4j.components.MTCanvas;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeContext;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Recognizer;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public class GlobalUnistrokeProcessor extends AbstractGlobalInputProcessor {
	private PApplet pa;
	private MTCanvas canvas;
	private Vector3D planeNormal;
	private Vector3D pointInPlane;
	private Recognizer recognizer;
	private UnistrokeUtils du;
	
	private Map<InputCursor, UnistrokeContext> cursorToContext;

	public GlobalUnistrokeProcessor(PApplet pa, MTCanvas canvas){
		this.pa = pa;
		this.canvas = canvas;
//		planeNormal = new Vector3D(0, 0, 1);
//		pointInPlane = new Vector3D(0, 0, 0);
		
		//Test - Calculate the normal of the plane we will be dragging at (useful if camera isnt default)
		this.planeNormal =  canvas.getViewingCamera().getPosition().getSubtracted(canvas.getViewingCamera().getViewCenterPos()).normalizeLocal();
		pointInPlane = canvas.getViewingCamera().getViewCenterPos();
		du = new UnistrokeUtils();
		recognizer = du.getRecognizer();
		cursorToContext = new HashMap<InputCursor, UnistrokeContext>();
	}
	
	public void addTemplate(UnistrokeGesture gesture, Direction direction){
		recognizer.addTemplate(gesture, direction);
	}

	@Override
	public void processInputEvtImpl(MTInputEvent inputEvent) {
		if (inputEvent instanceof AbstractCursorInputEvt) {
			AbstractCursorInputEvt ce = (AbstractCursorInputEvt) inputEvent;
			InputCursor inputCursor = ce.getCursor();
			
			switch (ce.getId()) {
			case AbstractCursorInputEvt.INPUT_STARTED:{
				UnistrokeContext context = new UnistrokeContext(pa, planeNormal, pointInPlane, inputCursor, recognizer, du, canvas);
				if (!context.isGestureAborted()) {
					cursorToContext.put(inputCursor, context);
					
					context.update(inputCursor);
					
					//FIXME ?? 3 times? REMOVE?
					context.update(inputCursor);
					context.update(inputCursor);
					context.update(inputCursor);
					
					this.fireInputEvent(new UnistrokeEvent(this, MTGestureEvent.GESTURE_STARTED, canvas, context.getVisualizer(), UnistrokeGesture.NOGESTURE, inputCursor));
				}
				
			}break;
			case AbstractCursorInputEvt.INPUT_UPDATED:{
				UnistrokeContext context = cursorToContext.get(inputCursor);
				if (context != null){
					context.update(inputCursor);
					this.fireInputEvent(new UnistrokeEvent(this, MTGestureEvent.GESTURE_UPDATED, canvas, context.getVisualizer(), UnistrokeGesture.NOGESTURE, inputCursor));
				}
				
			}break;
			case AbstractCursorInputEvt.INPUT_ENDED:{
				UnistrokeContext context = cursorToContext.remove(inputCursor);
				if (context != null){
					context.update(inputCursor);
					this.fireInputEvent(new UnistrokeEvent(this, MTGestureEvent.GESTURE_ENDED, canvas, context.getVisualizer(), context.recognizeGesture(), inputCursor));
					context.getVisualizer().destroy();
				}
			}break;
			default:
				break;
			}
		}
	}

}
