/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.input.inputProcessors.componentProcessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.GestureUtils;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.ArrayDeque;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public abstract class AbstractCursorProcessor extends AbstractComponentProcessor{
	private List<InputCursor> activeCursors;
	
	
	/** The lock priority. */
	private float lockPriority;


	private ArrayList<InputCursor> activeCursorsWithEndedOnes;
	
	
	public AbstractCursorProcessor(){
		 this(false);
	}
	
	
	public AbstractCursorProcessor(boolean stopPropagation){
		super(stopPropagation);
		activeCursors = new ArrayList<InputCursor>();
		activeCursorsWithEndedOnes = new ArrayList<InputCursor>();
		this.lockPriority = 1.0f;
	}
	

	@Override
	public boolean isInterestedIn(MTInputEvent inputEvt) {
		return inputEvt instanceof AbstractCursorInputEvt && inputEvt.hasTarget();
//		return inputEvt instanceof MTFingerInputEvt 
//			&& inputEvt.hasTarget();
	}


	@Override
	public void preProcessImpl(MTInputEvent inputEvent) {
		AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt)inputEvent;
		InputCursor c = posEvt.getCursor();
		switch (posEvt.getId()) {
		case AbstractCursorInputEvt.INPUT_STARTED:
			activeCursors.add(c);
			activeCursorsWithEndedOnes.add(c);
			c.registerForLocking(this);
			break;
		case AbstractCursorInputEvt.INPUT_UPDATED:
			break;
		case AbstractCursorInputEvt.INPUT_ENDED:
			activeCursors.remove(c);
			c.unregisterForLocking(this);
			break;
		default:
			break;
		}
	}
	

	@Override
	protected void processInputEvtImpl(MTInputEvent inputEvent) {
		AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt)inputEvent;
//		MTFingerInputEvt posEvt = (MTFingerInputEvt)inputEvent;
		InputCursor c = posEvt.getCursor();
		
		//FIXME TEST/////////////////// TODO CLEAN UP / DO MORE ELEGANTLY
		Set<InputCursor> cursorLockLostKeys = this.cursorToLockLostInputProcessor.keySet();
		for (Object element : cursorLockLostKeys) {
			InputCursor inputCursor = (InputCursor) element;
			AbstractCursorProcessor ip = this.cursorToLockLostInputProcessor.get(element);
			
			//so we can use getCurrentTarget() in the processor's method
			//because  the current target may have changed through bubbling
			IMTComponent3D saved = inputCursor.getCurrentTarget(); //FIXME Hack
			inputCursor.getCurrentEvent().setCurrentTarget(inputEvent.getCurrentTarget()); //FIXME Hack
			
			this.cursorLocked(inputCursor, ip);
			
			inputCursor.getCurrentEvent().setCurrentTarget(saved); //FIXME Hack
		}
		this.cursorToLockLostInputProcessor.clear();
		
		
		while (!this.cursorUnlocked.isEmpty()){
			InputCursor cursorUnlocked = this.cursorUnlocked.pollFirst();
			
			//Check if we Unlock a already ended cursor - just for debugging - shouldnt happen actually..
			if (cursorUnlocked.getCurrentEvent().getId() == AbstractCursorInputEvt.INPUT_ENDED){
				logger.warn(this + ": Unlocking already ENDED input event");
			}
			
			IMTComponent3D saved = cursorUnlocked.getCurrentTarget(); //FIXME Hack
			cursorUnlocked.getCurrentEvent().setCurrentTarget(inputEvent.getCurrentTarget()); //FIXME Hack
			
			this.cursorUnlocked(cursorUnlocked);
			
			cursorUnlocked.getCurrentEvent().setCurrentTarget(saved); //FIXME Hack
		}
		////////////////////////////
		
		switch (posEvt.getId()) {
		case AbstractCursorInputEvt.INPUT_STARTED:
//			activeCursors.add(c);
			cursorStarted(c, posEvt);
			break;
		case AbstractCursorInputEvt.INPUT_UPDATED:
			cursorUpdated(c, posEvt);
			break;
		case AbstractCursorInputEvt.INPUT_ENDED:
//			activeCursors.remove(c);
			cursorEnded(c, posEvt);
//			if (c.isLockedBy(this)){
				unLock(c);
//			}
			activeCursorsWithEndedOnes.remove(c);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void fireGestureEvent(MTGestureEvent ge) {
		switch (ge.getId()) { 
		case MTGestureEvent.GESTURE_STARTED:
			gestureInProgress = true;
			break;
		case MTGestureEvent.GESTURE_UPDATED:
			break;
		case MTGestureEvent.GESTURE_ENDED:
			gestureInProgress = false;
			break;
		default:
			break;
		}
		
		super.fireGestureEvent(ge);
	}
	
	private boolean gestureInProgress;
	public boolean isGestureInProgress() {
		return this.gestureInProgress;
	}

	/**
	 * Gets all active cursors which started on this component.
	 * It is not check whether this input processor could lock any of them.
	 * User <code>getAvailableComponentCursors()</code> instead for that.
	 * 
	 * @return the active component cursors
	 */
	public List<InputCursor> getCurrentComponentCursors(){
		return this.activeCursors;
	}
	
	public InputCursor[] getCurrentComponentCursorsArray(){
		return this.activeCursors.toArray(new InputCursor[getCurrentComponentCursors().size()]);
	}
	
	//////////////////////////////////////////////////////////
	//TODO check at getAvailable/fartherst etc cursor if cursor on component!? -> not usable if not! -> intersect cursor target? -> but what about canvas which has no intersection -> hardcode?
	//TODO allow to subscribe to other application cursors??
	//TODO method to get the 2 cursors which are farthest away from each other
	//TODO getNearestAvailableCursorTo()
	//TODO caution: if cursorLocked() is invoked we cant check if gesture used the cursor by checking 
	//if the cursor was in getLockedCurosor list because its already removed
	
	
	//exclude INPUT_ENDED?
	//FIXME this will also be cursors from scenes the component isnt even in!
	//TODO we should only listen to InputRetargeter from the component's scene, and only get the onces which arent already
	//targeted at this component..
	public InputCursor[] getAllActiveApplicationCursors(){
		return ActiveCursorPool.getInstance().getActiveCursors();
	}
	
	//TODO dont include the input_ended cursors!?
	/**
	 * Returns all component cursors that are not yet locked but could be locked 
	 * by this input processor.
	 * @return the free component cursors array
	 */
	public InputCursor[] getFreeComponentCursorsArray(){
		List<InputCursor> freeCursors = getFreeComponentCursors();
		return freeCursors.toArray(new InputCursor[freeCursors.size()]); 
	}
		
	
	//TODO should we also check if the cursors are on the component? / input_ended?
	//TODO dont include the input_ended cursors!?
	/**
	 * Returns all component cursors that are not yet locked but could be locked 
	 * by this input processor.
	 * @return the free component cursors
	 */
	public List<InputCursor> getFreeComponentCursors(){
		List<InputCursor> activeCursorsOnComp = this.getCurrentComponentCursors();
		List<InputCursor> freeCursors = new ArrayList<InputCursor>();
		for (InputCursor inputCursor : activeCursorsOnComp) {
			if (!inputCursor.isLockedBy(this) && inputCursor.canLock(this)){
				freeCursors.add(inputCursor);
			}
		}
//		return freeCursors.toArray(new InputCursor[freeCursors.size()]);
		return freeCursors;
	}
	
	/**
	 * Return all the component cursors that this component processor has successfully locked.
	 * @return the locked cursors array
	 */
	public InputCursor[] getLockedCursorsArray(){
		List<InputCursor> locked = getLockedCursors();
		return locked.toArray(new InputCursor[locked.size()]);
	}
	
	/**
	 * Return all the component cursors that this component processor has currently locked successfully.
	 * @return the locked cursors
	 */
	public List<InputCursor> getLockedCursors(){
		List<InputCursor> activeCursorsOnCompWithENDED = this.activeCursorsWithEndedOnes;
		List<InputCursor> lockedCursors = new ArrayList<InputCursor>();
		for (InputCursor inputCursor : activeCursorsOnCompWithENDED) {
			if (inputCursor.isLockedBy(this)){
				lockedCursors.add(inputCursor);
			}
		}
		return lockedCursors;
	}
	
	/**
	 * Releases all cursors that this component input processor currently holds a lock on.
	 */
	public void unLockAllCursors(){
		//we should also unlock the cursors that have input_ended, so that processors with lower priority can start the gesture and end it correctly aferwards
//		List<InputCursor> activeCursorsOnComp = acp.getActiveComponentCursors(); 
		List<InputCursor> activeCursorsOnCompWithENDED = this.activeCursorsWithEndedOnes;
		for (InputCursor inputCursor : activeCursorsOnCompWithENDED) {
			if (inputCursor.isLockedBy(this)){
				unLock(inputCursor);
			}
		}
	}
	
	/**
	 * Returns the most far away component cursor to the specified cursor.
	 * Only returns a cursor if it is free to use and could be locked by this processor.
	 *
	 * @param cursor the cursor
	 * @return the farthest free component cursor to
	 */
	public InputCursor getFarthestFreeComponentCursorTo(InputCursor cursor){
		return getFarthestFreeCursorTo(cursor);
	}
	
	/**
	 * Returns the most far away component cursor to the specified cursor.
	 * Only returns a cursor if it is free to use and could be locked by this processor.
	 * 
	 * @param cursor the cursor
	 * @param excludedFromSearch the excluded from search
	 * @return the farthest free cursor to
	 */
	public InputCursor getFarthestFreeCursorTo(InputCursor cursor, InputCursor... excludedFromSearch){
		float currDist = Float.MIN_VALUE;
		InputCursor farthestCursor = null;
		
		Vector3D cursorPos = cursor.getPosition();
		for (InputCursor currentCursor : this.getCurrentComponentCursors()) {
			if (currentCursor.equals(cursor) || !currentCursor.canLock(this) || currentCursor.isLockedBy(this))
				continue;
			
			boolean continueLoop = false;
			for (InputCursor excludedCursor : excludedFromSearch) {
				if (currentCursor.equals(excludedCursor)){
					continueLoop = true;
//					continue; // this exits only this loop, not the outer
				}
			}
			if (continueLoop)
				continue;
			
			float distanceToCurrentCursor = currentCursor.getPosition().distance2D(cursorPos);
			if (distanceToCurrentCursor >= currDist || distanceToCurrentCursor == 0.0f){
				currDist = distanceToCurrentCursor;
				farthestCursor = currentCursor;
			}
		}
		return farthestCursor;
	}
	
	/**
	 * Returns the closest cursor to the specified cursor that started on the current target component.
	 * Only returns a cursor if it is free to use and could be locked by this processor.
	 * 
	 * @param cursor the cursor
	 * @param excludedFromSearch the excluded from search
	 * @return the closest free cursor to
	 */
	public InputCursor getClosestFreeCursorTo(InputCursor cursor, InputCursor... excludedFromSearch){
		float currDist = Float.MAX_VALUE;
		InputCursor closestCursor = null;
		
		Vector3D cursorPos = cursor.getPosition();
		for (InputCursor currentCursor : this.getCurrentComponentCursors()) {
			if (currentCursor.equals(cursor) || !currentCursor.canLock(this) || currentCursor.isLockedBy(this))
				continue;
			
			boolean continueLoop = false;
			for (InputCursor excludedCursor : excludedFromSearch) {
				if (currentCursor.equals(excludedCursor)){
					continueLoop = true;
//					continue; //FIXME this exits only this loop, not the outer
				}
			}
			if (continueLoop)
				continue;
			
			float distanceToCurrentCursor = currentCursor.getPosition().distance2D(cursorPos);
			if (distanceToCurrentCursor <= currDist || distanceToCurrentCursor == 0.0f){
				currDist = distanceToCurrentCursor;
				closestCursor = currentCursor;
			}
		}
		return closestCursor;
	}
	
	
	/**
	 * Returns the most far away component cursor to the specified cursor but limited to a specified maximum radius.
	 * Only returns a cursor if it is free to use and could be locked by this processor.
	 *
	 * @param cursor the cursor
	 * @param maxRadius the max radius
	 * @param excludedFromSearch the excluded from search
	 * @return the farthest free cursor to limited
	 */
	public InputCursor getFarthestFreeCursorToLimited(InputCursor cursor, float maxRadius, InputCursor... excludedFromSearch){
		float currDist = Float.MIN_VALUE;
		InputCursor farthestCursor = null;
		
		Vector3D cursorPos = cursor.getPosition();
		for (InputCursor currentCursor : this.getCurrentComponentCursors()) {
			if (currentCursor.equals(cursor) || !currentCursor.canLock(this) || currentCursor.isLockedBy(this))
				continue;
			
			boolean continueLoop = false;
			for (InputCursor excludedCursor : excludedFromSearch) {
				if (currentCursor.equals(excludedCursor)){
					continueLoop = true;
				}
			}
			if (continueLoop)
				continue;
			
			float distanceToCurrentCursor = currentCursor.getPosition().distance2D(cursorPos);
			if ((distanceToCurrentCursor >= currDist || distanceToCurrentCursor == 0.0f) && distanceToCurrentCursor <= maxRadius){
				currDist = distanceToCurrentCursor;
				farthestCursor = currentCursor;
			}
		}
		return farthestCursor;
	}
	

	
	/**
	 * Checks if the distance between a reference cursor and a cursor is greater than the distance to another cursor.
	 *
	 * @param reference the reference
	 * @param oldCursor the old cursor
	 * @param newCursor the new cursor
	 * @return true, if is cursor distance greater
	 */
	public boolean isCursorDistanceGreater(InputCursor reference, InputCursor oldCursor, InputCursor newCursor){
		return GestureUtils.isCursorDistanceGreater(reference, oldCursor, newCursor);
	}
	
	/**
	 * Gets the distance between two cursors.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the distance
	 */
	public float getDistance(InputCursor a, InputCursor b){
		return GestureUtils.getDistance(a, b);
	}
	
	
	/**
	 * Gets the intersection point of a cursor and a specified component.
	 * Can return null if the cursor doesent intersect the component.
	 *
	 * @param app the app
	 * @param c the c
	 * @return the intersection
	 */
	public Vector3D getIntersection(PApplet app, InputCursor c){
		return GestureUtils.getIntersection(app, c.getTarget(), c);
	}
	
	/**
	 * Gets the intersection point of a cursor and a specified component.
	 * Can return null if the cursor doesent intersect the component.
	 *
	 * @param app the app
	 * @param component the component
	 * @param c the c
	 * @return the intersection
	 */
	public Vector3D getIntersection(PApplet app, IMTComponent3D component, InputCursor c){
		return GestureUtils.getIntersection(app, component, c);
	}
	
	public Vector3D getPlaneIntersection(PApplet app, Vector3D planeNormal, Vector3D pointInPlane, InputCursor c){
		return GestureUtils.getPlaneIntersection(app, planeNormal, pointInPlane, c);
	}
	///////////////////////////////////////////////////////
	
	
	
	/**
	 * Gets the input cursor locking priority.
	 * 
	 * @return the input cursor locking priority
	 */
	public float getLockPriority() {
		return lockPriority;
	}


	/**
	 * Sets the  input cursor locking priority.
	 * This should only be set once before usage of the processor.
	 * 
	 * @param gesturePriority the new input cursor locking priority
	 */
	public void setLockPriority(float gesturePriority) {
		this.lockPriority = gesturePriority;
	}
	
	
	/**
	 * Checks if this input processor would have the 
	 * sufficient priority to lock the specified input cursors.
	 * 
	 * @param cursors the cursors
	 * 
	 * @return true, if successful
	 */
	protected boolean canLock(InputCursor... cursors){
		int locked = 0;
        for (InputCursor m : cursors) {
            if (m.canLock(this)) {
                locked++;
            }
        }
		return locked == cursors.length;
	}
	
	
//	/**
//	 * Checks if this processor has a lock on the specified cursor(s).
//	 *
//	 * @param cursors the cursors
//	 * @return true, if successful
//	 */
//	protected boolean hasLock(InputCursor... cursors){
//		int locked = 0;
//        for (InputCursor c : cursors) {
//            if (c.isLockedBy(this)) {
//                locked++;
//            }
//        }
//		return locked == cursors.length;
//	}
	
	
	/**
	 * Locks the cursor with this processor if the processors lock priority
	 * is higher or equal than the current lock priority of this cursor.
	 * 
	 * @param cursors the cursors
	 * 
	 * @return true, if all specified cursors could get locked
	 */
	protected boolean getLock(InputCursor... cursors){
		int locked = 0;
        for (InputCursor m : cursors) {
            if (m.getLock(this)) {
                locked++;
            }
        }
		return locked == cursors.length;
	}
	
	
	
	/**
	 * Unlocks the specified cursors if they are not longer used by this processor.
	 * If the priority by which the cursors are locked changes by that, 
	 * the <code>cursorUnlocked</code> method is invoked on processors 
	 * with a lower priority who by that get a chance to lock this cursor again.
	 * 
	 * @param cursors the cursors
	 */
	protected void unLock(InputCursor... cursors){
        for (InputCursor inputCursor : cursors) {
            inputCursor.unlock(this);
        }
	}
	
	
	@Override
	public int compareTo(AbstractComponentProcessor o) {
		if (o instanceof AbstractCursorProcessor) {
			AbstractCursorProcessor o2 = (AbstractCursorProcessor) o;
			
			if (this.getLockPriority() < o2.getLockPriority()){
				return -1;
			}else if (this.getLockPriority() > o2.getLockPriority()){
				return 1;
			}else{
				if (!this.equals(o2)
					&& this.getLockPriority() == o2.getLockPriority()
				){
					return -1;
				}
				return 0;
			}
		}else{
			return 1;
		}
	}	


	//FIXME TEST
	private ArrayDeque<InputCursor> cursorUnlocked = new ArrayDeque<InputCursor>();
//	private Deque<InputCursor> cursorLocked = new ArrayDeque<InputCursor>();
	private HashMap<InputCursor, AbstractCursorProcessor> cursorToLockLostInputProcessor = new HashMap<InputCursor, AbstractCursorProcessor>();
	
	public void cursorFreed(InputCursor cursor){
		//we dont call cursorUnlocked() here directly but rather in the event loop at processInputEvtImpl() the next time, so that the
		//targetComponent and the cursors current event etc are correct (required since events can change their current target at bubbling)
		if (!cursorUnlocked.contains(cursor)){
			cursorUnlocked.addLast(cursor);
		}
		
//		if (cursorLocked.contains(cursor)){
//			cursorLocked.remove(cursor);
//		}
		
		if (cursorToLockLostInputProcessor.containsKey(cursor)){  //FIXME REMOVE?
			cursorToLockLostInputProcessor.remove(cursor);
		}
		
//		cursorUnlocked(cursor);
	}

	public void cursorLostLock(InputCursor cursor, AbstractCursorProcessor lockinProcessor){
//		if (!cursorLocked.contains(cursor)){
//			cursorLocked.addLast(cursor);
//		}
		
		//we dont call cursorLocked() here directly but rather in the event loop at processInputEvtImpl() the next time, so that the
		//targetComponent and the cursors current event etc are correct (required since events can change their current target at bubbling)
		
		//To know to which processor we lost the cursor
		cursorToLockLostInputProcessor.put(cursor, lockinProcessor);
		if (cursorUnlocked.contains(cursor)){ //FIXME REMOVE?
			cursorUnlocked.remove(cursor);
		}
		
//		cursorLocked(cursor, lockinProcessor);
	}

	
	/**
	 * This method is called if a input processor with a higher locking-priority than this one successfully
	 * locked the specified cursor which was previously locked by this processor. We should stop using it and end the gesture until it
	 * is unlocked by the other processor again!
	 * So when this method is invoked, usually we can follow these instructions in our processor:
	 * <br>-> if we can't continue the gesture without the cursor that we just lost the lock on (usually the case)
	 * <br>-> unlock all other cursors that we used in this processor/gesture ( unlockAllCursors())
	 * <br>-> fire a gesture event with ID = GESTURE_ENDED
	 * 
	 * @param cursor the cursor
	 * @param lockingprocessor the locking processor
	 */
	abstract public void cursorLocked(InputCursor cursor, IInputProcessor lockingprocessor);

	
	/**
	 * This method is called if an input processor with a higher locking-priority than this one releases his lock on the specified
	 * cursor (i.e. because the conditions for continuing the gesture aren't met anymore). This gives this input processor the chance to
	 * see if it can use the cursor and try to lock it again.
	 * <br>So first of all we should check if our gesture in this processor is still in progress and if it isnt we can check if the cursor is still free for us to lock. 
	 * using canLock(cursor) and then obtain the lock using getLock(cursor);
	 * So when this method is invoked, usually we can follow these instructions in our processor:
	 * <br>-> Is the processor/gesture resumable?
	 * <br>-> If not -> do nothing, we don't want any released cursor that was used by a higher priority gesture previously
	 * <br>-> If yes -> check if the gesture is still in progress (i.e. by checking if the getLockedCursors() list has the same size as the cursors used by this processor/gesture
	 * <br>-> Check if all gesture preconditions are met and we can lock the cursor(s) using canLock(..) to restart the gesture 
	 * <br>-> lock the cursor(s) using getLock(..)
	 * <br>-> fire a new gesture event with id = GESTURE_
	 * 
	 * @param cursor the cursor
	 */
	abstract public void cursorUnlocked(InputCursor cursor);
	
	/**
	 * Called when a new cursor has been detected.
	 * @param inputCursor
	 * @param currentEvent
	 */
	abstract public void cursorStarted(InputCursor inputCursor, AbstractCursorInputEvt currentEvent);
	
	/**
	 * Called when a cursor has been updated with a new input event.
	 * @param inputCursor
	 * @param currentEvent
	 */
	abstract public void cursorUpdated(InputCursor inputCursor, AbstractCursorInputEvt currentEvent);
	
	/**
	 * Called when a cursor has been removed.
	 * @param inputCursor
	 * @param currentEvent
	 */
	abstract public void cursorEnded(InputCursor inputCursor, AbstractCursorInputEvt currentEvent);
	

}
