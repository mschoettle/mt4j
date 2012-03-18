package org.mt4jx.input.inputProcessors.componentProcessors.depthProcessor;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.util.extension3D.VelocityMotionMapper;

import processing.core.PApplet;

public class DepthProcessor extends AbstractCursorProcessor {

	private PApplet applet;

	/** The dc. */
	private DepthContext dpc;

	private List<InputCursor> depthCursors = new ArrayList<InputCursor>();
	/** The un used cursorss. */
	private List<InputCursor> unUsedCursors = new ArrayList<InputCursor>();

	/** The locked cursorss. */
	private List<InputCursor> lockedCursors = new ArrayList<InputCursor>();

	private MTCanvas canvas;

	private Icamera cam;
	
	private MTRectangle visualHelper;

	private IMTComponent3D targetComponent;
	
	/** if gesture is paused by collision detection*/
	private boolean gesturePaused = false;
	
	/** if gesture has been resumed after collision*/
	private boolean resumed = false;
		
	public DepthProcessor(PApplet graphicsContext, MTCanvas canvas, Icamera cam,IMTComponent3D targetComponent) {
		this.setLockPriority(1);
		this.applet = graphicsContext;
		this.setDebug(false);
		this.canvas = canvas;
		this.setTargetComponent(targetComponent);
		this.cam = cam;
	}

	@Override
	public void cursorEnded(InputCursor inputCursor,
			AbstractCursorInputEvt positionEvent) {

		IMTComponent3D comp = positionEvent.getTarget();
				
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - MOTION: "
				+ inputCursor.getId());
		
		if (lockedCursors.contains(inputCursor)) { // cursors was a actual
													// gesture cursors
					
			if(dpc!=null) //TODO correct handling of dpc creation 
			{
				dpc.updateDepthPosition();
			}
			//targetComponent = null; //set back target Component
			lockedCursors.remove(inputCursor);
			if (unUsedCursors.size() > 0) { // check if there are other cursorss
											// on the component, we could use
											// for depth drag
				InputCursor otherMotion = unUsedCursors.get(0); // TODO cycle
																// through all
																// available
																// unUsedCursors
																// and try to
																// claim one,
																// maybe the
																// first one is
																// claimed but
																// another isnt!
				if (this.canLock(otherMotion)) { // Check if we have the
													// priority to use this
													// cursors
					dpc = new DepthContext(otherMotion, comp);
					if (!dpc.isGestureAborted()) {
						this.getLock(otherMotion);
						unUsedCursors.remove(otherMotion);
						lockedCursors.add(otherMotion);
						// TODO fire started? maybe not.. do we have to?
					} else {
						this.fireGestureEvent(new DepthGestureEvent(this,
								MTGestureEvent.GESTURE_ENDED, getTargetComponent(), inputCursor,
								dpc.getTranslationVect()));
					}
				} else {
					this.fireGestureEvent(new DepthGestureEvent(this,
							MTGestureEvent.GESTURE_ENDED, getTargetComponent(), inputCursor,
							dpc.getTranslationVect()));
				}
			} else {
				this.fireGestureEvent(new DepthGestureEvent(this,
						MTGestureEvent.GESTURE_ENDED, getTargetComponent(), inputCursor,
						dpc.getTranslationVect()));
				
			}
			this.unLock(inputCursor); // FIXME TEST
		} else { // cursors was not used for dragging
			if (unUsedCursors.contains(inputCursor)) {
				unUsedCursors.remove(inputCursor);
			}
		}

	}

	@Override
	public void cursorLocked(InputCursor cursor,
			IInputProcessor lockingprocessor) {
		if (lockingprocessor instanceof AbstractComponentProcessor) {
			logger.debug(this.getName() + " Recieved MOTION LOCKED by ("
					+ ((AbstractComponentProcessor) lockingprocessor).getName()
					+ ") - cursors ID: " + cursor.getId());
		} else {
			logger
					.debug(this.getName()
							+ " Recieved MOTION LOCKED by higher priority signal - cursors ID: "
							+ cursor.getId());
		}

		if (lockedCursors.contains(cursor)) { // cursors was a actual gesture
												// cursors
			lockedCursors.remove(cursor);
			// TODO fire ended evt?
			unUsedCursors.add(cursor);
			logger.debug(this.getName() + " cursors:" + cursor.getId()
					+ " MOTION LOCKED. Was an active cursors in this gesture!");
		} else { // TODO remove "else", it is pretty useless
			if (unUsedCursors.contains(cursor)) {
				logger
						.debug(this.getName()
								+ " MOTION LOCKED. But it was NOT an active cursors in this gesture!");
			}
		}
	}

	@Override
	public void cursorStarted(InputCursor inputCursor,
			AbstractCursorInputEvt positionEvent) {
		IMTComponent3D comp = positionEvent.getTarget();
		
		if (lockedCursors.size() == 0) { 
			dpc = new DepthContext(inputCursor, comp);
		
			if (this.canLock(inputCursor)) {
					if (!dpc.isGestureAborted()) {
						this.getLock(inputCursor);
						lockedCursors.add(inputCursor);
						InputCursor otherCursor = lockedCursors.get(0);
						//dpc = new DepthContext(inputCursor,comp);
						this.fireGestureEvent(new DepthGestureEvent(this,
								MTGestureEvent.GESTURE_STARTED, getTargetComponent(), inputCursor,
								dpc.getTranslationVect()));
					}
					depthCursors.add(inputCursor);
			}			
		} else if (lockedCursors.size() > 0) {
			unUsedCursors.add(inputCursor);
		}
	}

	public void cursorUnlocked(InputCursor cursor) {
		logger
				.debug(this.getName()
						+ " Recieved UNLOCKED signal for cursors ID: "
						+ cursor.getId());
		if (lockedCursors.size() >= 1) { // we dont need the unlocked cursors,
											// gesture still in progress
			return;
		}

		if (unUsedCursors.contains(cursor)) {
			if (this.canLock(cursor)) {
				dpc = new DepthContext(cursor, getTargetComponent());
				if (!dpc.isGestureAborted()) {
					this.getLock(cursor);
					unUsedCursors.remove(cursor);
					lockedCursors.add(cursor);
					// TODO fire started? maybe not.. do we have to?
					logger.debug(this.getName()
							+ " can resume its gesture with cursors: "
							+ cursor.getId());
				} else {
					dpc = null;
					logger
							.debug(this.getName()
									+ " we could NOT start gesture - cursors not on component: "
									+ cursor.getId());
				}
			} else {
				logger
						.debug(this.getName()
								+ " still in progress - we dont need the unlocked cursors");
			}
		}

	}

	public void cursorUpdated(InputCursor inputCursor,
			AbstractCursorInputEvt positionEvent) {
		IMTComponent3D comp = positionEvent.getTarget();
		
		
		Vector3D vec = positionEvent.getTarget().getIntersectionGlobal(
				Tools3D.getCameraPickRay(applet, comp, inputCursor.getCurrentEvent().getX(), inputCursor.getCurrentEvent().getY()));
		
		if(vec!=null)
		{
			if(lockedCursors.size()==0)
			{
				return;
			}
					
			if (lockedCursors.size() == 1) {
				if(lockedCursors.get(0) == inputCursor) {			
				
					dpc.updateDepthPosition();						
					this.fireGestureEvent(new DepthGestureEvent(this,
							MTGestureEvent.GESTURE_UPDATED, getTargetComponent(), inputCursor,
							dpc.getTranslationVect()));
				}
			} 
			
		}else
		{			
			this.fireGestureEvent(new DepthGestureEvent(this,
					MTGestureEvent.GESTURE_ENDED, getTargetComponent(), inputCursor,
					dpc.getTranslationVect()));
		}
		

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteDepthHelper(IMTComponent3D comp) {

	}

	private class DepthContext {

		private Vector3D startPosition;

		private Vector3D lastPosition;

		private Vector3D newPosition;
		
		private float lastVal;
		
		private float newVal;

		private IMTComponent3D dragDepthObject;

		private InputCursor depthCursor;

		private boolean gestureAborted;

		private MTCanvas mtCanvas;

		private MTComponent mtComp;

		private Vector3D translationVect;

		private VelocityMotionMapper velocityMotionMapper;
		
		public DepthContext(InputCursor cursor, IMTComponent3D dragObject) {
			this.dragDepthObject = dragObject;
			this.depthCursor = cursor;
			gestureAborted = false;
			
			startPosition = new Vector3D(cursor.getCurrentEvtPosX(),cursor.getCurrentEvtPosY());

			this.newPosition = startPosition.getCopy();
			
			this.velocityMotionMapper = new VelocityMotionMapper(10);

			// Set the Drags lastPostition (the last one before the new one)
			this.lastPosition = startPosition.getCopy();
			this.lastVal = 0.0f;
			
			this.updateDepthPosition();

		}

		/**
		 * Update drag position.
		 */
		public void updateDepthPosition() {

			
			if(!resumed)
			{
				Vector3D newPos = new Vector3D(depthCursor.getCurrentEvtPosX(),depthCursor.getCurrentEvent().getPosY(),0.0f);	
				
				Vector3D vec = newPos;
				
				int sign = -1;
				
				if(newPos.y<lastPosition.y)
				{
					sign = 1;
				}
				velocityMotionMapper.updateCurrentLength(sign*vec.getSubtracted(lastPosition).length());
			
				float currentVal = velocityMotionMapper.calcCurrentValue();
				
				newVal = currentVal; 
				lastVal = currentVal;
				
				translationVect = new Vector3D(0.0f, 0.0f, -newVal);
				
				lastPosition = newPosition;
				newPosition = newPos;
			}else
			{				
				translationVect = new Vector3D(0.0f,0.0f,0.0f);
				newPosition = lastPosition;
				resumed = false;
			}
			
		}

		/**
		 * Gets the last position.
		 * 
		 * @return the last position
		 */
		public Vector3D getLastPosition() {
			return lastPosition;
		}

		/**
		 * Gets the new position.
		 * 
		 * @return the new position
		 */
		public Vector3D getNewPosition() {
			return newPosition;
		}

		/**
		 * Checks if is gesture aborted.
		 * 
		 * @return true, if is gesture aborted
		 */
		public boolean isGestureAborted() {
			return gestureAborted;
		}

		public Vector3D getTranslationVect() {
			return translationVect;
		}

	}

	public void setTargetComponent(IMTComponent3D targetComponent) {
		this.targetComponent = targetComponent;
	}

	public IMTComponent3D getTargetComponent() {
		return targetComponent;
	}

	
}
