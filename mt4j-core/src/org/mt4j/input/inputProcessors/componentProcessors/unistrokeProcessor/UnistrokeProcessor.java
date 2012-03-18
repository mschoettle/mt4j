package org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor;


import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Recognizer;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * The Class UnistrokeProcessor. A component input processor to recognize pre-recorded gestures.
 */
public class UnistrokeProcessor extends AbstractCursorProcessor {
	
	/** The pa. */
	private PApplet pa;
	/** The plane normal. */
	private Vector3D planeNormal;
	/** The point in plane. */
	private Vector3D pointInPlane;
	
	
	/** The context. */
	private UnistrokeContext context;
	
	/** The recognizer. */
	private Recognizer recognizer;
	
	/** The du. */
	private UnistrokeUtils du;


	/**
	 * Instantiates a new unistroke processor.
	 *
	 * @param pa the pa
	 */
	public UnistrokeProcessor(PApplet pa) { 
		super();
		this.pa = pa;
		planeNormal = new Vector3D(0, 0, 1);
		pointInPlane = new Vector3D(0, 0, 0);
		
		this.setLockPriority(1);
		
		du = new UnistrokeUtils();
		recognizer = du.getRecognizer();
	}
	
	/**
	 * Adds the template.
	 *
	 * @param gesture the gesture
	 * @param direction the direction
	 */
	public void addTemplate(UnistrokeGesture gesture, Direction direction){
		recognizer.addTemplate(gesture, direction);
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorStarted(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorStarted(InputCursor inputCursor, AbstractCursorInputEvt currentEvent) {
		if (this.canLock(inputCursor)) {
			context = new UnistrokeContext(pa, planeNormal, pointInPlane, inputCursor, recognizer, du, inputCursor.getTarget());
			if (!context.gestureAborted) {
				this.getLock(inputCursor);
				context.update(inputCursor);
				
				//FIXME ?? 3 times? REMOVE?
				context.update(inputCursor);
				context.update(inputCursor);
				context.update(inputCursor);
				
				this.fireGestureEvent(new UnistrokeEvent(this, MTGestureEvent.GESTURE_STARTED, inputCursor.getCurrentTarget(), context.getVisualizer(), UnistrokeGesture.NOGESTURE, inputCursor));
			}
		}

	}

	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUpdated(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorUpdated(InputCursor inputCursor, AbstractCursorInputEvt currentEvent) {
		if (getLockedCursors().contains(inputCursor) && context != null) {
			if (!context.gestureAborted) {
				context.update(inputCursor);
				this.fireGestureEvent(new UnistrokeEvent(this, MTGestureEvent.GESTURE_UPDATED, inputCursor.getCurrentTarget(), context.getVisualizer(), UnistrokeGesture.NOGESTURE, inputCursor));
			}
		}

	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorEnded(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.MTFingerInputEvt)
	 */
	@Override
	public void cursorEnded(InputCursor inputCursor, AbstractCursorInputEvt currentEvent) {
		if (getLockedCursors().contains(inputCursor) && context != null) {
			this.fireGestureEvent(new UnistrokeEvent(this, MTGestureEvent.GESTURE_ENDED, inputCursor.getCurrentTarget(), context.getVisualizer(), context.recognizeGesture(), inputCursor));
			
			context.getVisualizer().destroy();
			this.unLock(inputCursor);
			context = null;
		}
	}

	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorLocked(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputProcessors.IInputProcessor)
	 */
	@Override
	public void cursorLocked(InputCursor inputCursor, IInputProcessor lockingprocessor) {
		if (getLockedCursors().contains(inputCursor) && context != null) {
			this.fireGestureEvent(new UnistrokeEvent(this, MTGestureEvent.GESTURE_ENDED, inputCursor.getCurrentTarget(), context.getVisualizer(), UnistrokeGesture.NOGESTURE, inputCursor));
		}
		//If not resumable we can fire GESTURE_ENDED, if resumable its better to fire GESTURE_CANCELED
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUnlocked(org.mt4j.input.inputData.InputCursor)
	 */
	@Override
	public void cursorUnlocked(InputCursor inputCursor) {

	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor#getName()
	 */
	@Override
	public String getName() {
		return "MTDollarGesture Processor";
	}

	
}
