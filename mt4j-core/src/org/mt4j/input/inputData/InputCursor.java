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
package org.mt4j.input.inputData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Vector3D;



/**
 * This is a container for AbstractCursorInputEvt Events with a unique ID, identifying the cursor.
 * The cursor contains all cursor events of the correspinding cursor (or finger when using multi-touch).
 * Also, the cursor allows the input processors to negotiate who has priority to use (lock) this cursor.
 * @author Christopher Ruff
 */
public class InputCursor{
	private static final ILogger logger = MTLoggerFactory.getLogger(InputCursor.class.getName());

	private static final int EVENT_HISTORY_DEPTH = 99;
	static{
		logger.setLevel(ILogger.ERROR);
	}
	
	/** The events. */
	private List<AbstractCursorInputEvt> events;
	
	/** The current id. */
	private static long currentID;
	
	/** The ID. */
	private long ID;
	
	private TreeMap<AbstractCursorProcessor, Float> lockingProcessorsToPriority;
	
	private TreeMap<AbstractCursorProcessor, Float> registeredProcessorsToPriority;
	
    
	
	/**
	 * Instantiates a new input cursor.
	 */
	public InputCursor(){
		this.ID = generateNewID();
		
		events = new ArrayList<AbstractCursorInputEvt>(100);
//		events = new LinkedList<AbstractCursorInputEvt>();
		
		lockingProcessorsToPriority = new TreeMap<AbstractCursorProcessor, Float>(new Comparator<AbstractCursorProcessor>() {
			//@Override //TODO make comparater inner clas and reuse 
			public int compare(AbstractCursorProcessor o1, AbstractCursorProcessor o2) {
				if (o1.getLockPriority() < o2.getLockPriority()){
					return -1;
				}else if (o1.getLockPriority() > o2.getLockPriority()){
					return 1;
				}else{
					if (!o1.equals(o2) 
						&& o1.getLockPriority() == o2.getLockPriority()){
						return -1;
					}
					return 0;
				}
			}
		});
		
		registeredProcessorsToPriority = new TreeMap<AbstractCursorProcessor, Float>(new Comparator<AbstractCursorProcessor>() {
			//@Override
			public int compare(AbstractCursorProcessor o1, AbstractCursorProcessor o2) {
				if (o1.getLockPriority() < o2.getLockPriority()){
					return -1;
				}else if (o1.getLockPriority() > o2.getLockPriority()){
					return 1;
				}else{
					if (!o1.equals(o2) 
						&& o1.getLockPriority() == o2.getLockPriority()){
						return -1;
					}
					return 0;
				}
			}
		});
	}
	

	
	/**
	 * Gets the priority by which this cursor is locked.
	 * 
	 * @return the current lock priority
	 */
	public float getCurrentLockPriority(){
		if (lockingProcessorsToPriority.isEmpty()){
			return 0;
		}else{
			return lockingProcessorsToPriority.lastKey().getLockPriority();
		}
	}
	

	
	public boolean canLock(AbstractCursorProcessor ia){
		float currentLockPriority = this.getCurrentLockPriority();
		if (currentLockPriority == ia.getLockPriority()){
			return true;
		}else if (currentLockPriority < ia.getLockPriority()){
			return true;
		}else{ //cursor claimed by higher priority already
			return false;
		}
	}
	
	
	/**
	 * Locks this cursor with the specified processor if the processors lock priority
	 * is higher or equal than the current lock priority of this cursor.
	 * 
	 * @param ia the AbstractCursorProcessor
	 * 
	 * @return true if sucessfully locked
	 */
	public boolean getLock(AbstractCursorProcessor ia){
//		if (ia instanceof AbstractCursorProcessor){
//			AbstractCursorProcessor a = (AbstractCursorProcessor)ia;
//			System.out.println(a.getName() + " trying to LOCK cursor: " + this.getId());
			logger.debug(ia.getName() + " trying to LOCK cursor: " + this.getId());
//		}
		
			float currentLockPriority = this.getCurrentLockPriority();
		
		if (currentLockPriority == ia.getLockPriority()){
			lockingProcessorsToPriority.put(ia, ia.getLockPriority());
			logger.debug("Cursor: " + this.getId() + " LOCKED sucessfully, dont send lock signal because cursor was already locked by same priority (" + currentLockPriority +   ")");
			return true;
		}else if (currentLockPriority < ia.getLockPriority()){
			lockingProcessorsToPriority.put(ia, ia.getLockPriority());
			
			
			logger.debug("Cursor: " + this.getId() + " LOCKED sucessfully, send lock signal - Cursor priority was lower " + "(" + currentLockPriority +   ")" +  " than the gesture priority (" + ia.getLockPriority() + ")");
			// Send only to ones lower than this priority
			cursorLockedByHigherPriorityGesture(ia, ia.getLockPriority());
			
			//FIXME TEST - remove the lower priority gestures from locking this cursor AFTER having sent the lockLost signal to these lower
			//priority processors! -> (in cursorLocked() , processor.getLockedCursors() will still return this cursor! - check processor behaviour!
			//FIXME MTInputPositionEvtEST - ONLY KEEPING MTInputPositionEvtHE HIGHEST PRIORITY ANALYZERS - just keep an array with the current highest priority analyzers?
			//Just keep the head of the map
			//sprich bei drag : 1 entry mit drag
			//bei rotate/scale : 2 entries aber kein drag entry mehr gebraucht
			SortedMap<AbstractCursorProcessor, Float> m = lockingProcessorsToPriority.headMap(ia);
			Set<AbstractCursorProcessor> k = m.keySet();
			for (Iterator<AbstractCursorProcessor> iterator = k.iterator(); iterator.hasNext();) {
				AbstractCursorProcessor processor = iterator.next();
				logger.debug("itereating and removing old, lower priority processor: "  + processor);
				iterator.remove();
			}
			
			
			
			return true;
		}else{ //cursor locked by higher priority already
//			lockSeekingAnalyzersToPriority.put(ia, ia.getLockPriority()); //TODO REMOVE?
			logger.debug("Cursor: " + this.getId() + " LOCKED UN-sucessfully, send no lock signal - Cursor priority " + "(" + currentLockPriority +   ")" +  " higher than the gesture priority (" + ia.getLockPriority() + ")");
			return false;
		}
	}
	
	public boolean isLockedBy(AbstractCursorProcessor cp){
//		return lockSeekingProcessorsToPriority.containsKey(cp);
		for (AbstractCursorProcessor abstractCursorProcessor : lockingProcessorsToPriority.keySet()) {
			if (abstractCursorProcessor.equals(cp) 
					|| abstractCursorProcessor.getLockPriority() == -1 //FIXME TEST!
			){
				return true;
			}
		}
		return false;
	}
	
	//only do this when previous highest priority strictly < the new priority!
	private void cursorLockedByHigherPriorityGesture(AbstractCursorProcessor ia, float gesturePriority){
		if (!registeredProcessorsToPriority.isEmpty()){
			
			//FIXME use headMap -> inclusive = false? so it doesent send cursorLocked to some with same priority
			SortedMap<AbstractCursorProcessor, Float> lesserPriorityGestureMap = registeredProcessorsToPriority.headMap(ia); //get analyzers with strictly lower priority than the locking one 
			Set<AbstractCursorProcessor> lesserPriorityGestureKeys = lesserPriorityGestureMap.keySet();
            
			for (AbstractCursorProcessor processor : lesserPriorityGestureKeys) {
                //Only send lock signal to the processors whos priority is lower than the current locking cursor priority
                AbstractCursorProcessor a = processor;
                logger.debug("Cursor: " + this.getId() + " Sending cursor LOCKED signal to: " + a.getName());
                
                //FIXME the lock is already lost here? -it was like this, yes -> change so we can use
                //if (this.getLockedCursors.contains(inputCursor)) ... in the processors' cursorLocked() method to check if 
                //the cursor was used in the processor
                //TODO check if we get concurrent modification exception if the processors would call unlock() while we iterate here!!!
                if (this.isLockedBy(processor) && processor.getLockPriority() != -1){//FIXME test - only send lockLost to processors who actually had the cursor locked
                	//FIXME how to treat processor priority -1 ? we want to return true at isLockedBy but we don't want to send cursorLocked to them! 
                	
//                	processor.cursorLocked(this, ia);
                	processor.cursorLostLock(this, ia);
                	
//                	System.out.println("cursor " + this.getId() + " now locked by " + ia.getName() + " was Locked By( " + processor.getName() + " target: " + getTarget() + " currenttarget: " + this.getCurrentEvent().getCurrentTarget());
                	
                	if (this.getTarget() != null){
                		//TODO send lockLostEvent extends MTInputEvent so that we can use lockLostEvt.getCurrentTarget
                		//in the cursorLocked method to fire to the correct comp
                		
                		//PRoblem - im InputCursor muss tatsächlich als current event einer sein mit currentTarget = component of the processor

                		//le = new MTLockLostEvent(this, this.getTarget ) ;
                		//le.setEventPhase(MTI
                		//this.getTarget().processInputEvent();
                	}else{
                		//TODO invoke processor.cursorLocked(this, ia); directly
                	}
                }else{
//                	System.out.println("cursor " + this.getId() + " now locked by " + ia.getName() + " was NOT Locked By( " + processor.getName() + " target: " + getTarget() + " currenttarget: " + this.getCurrentEvent().getCurrentTarget());
                }
//                processor.cursorLocked(this, ia);
            }
		}
	}
	
	
	/**
	 * Input processors should call this when new input has started to be
	 * able to use the cursor locking mechanisms.
	 * 
	 * @param ia the ia
	 */
	public void registerForLocking(AbstractCursorProcessor ia) {
		registeredProcessorsToPriority.put(ia, ia.getLockPriority());
	}
	
	/**
	 * Input processors should call this when input has ended.
	 * 
	 * @param ia the ia
	 */
	public void unregisterForLocking(AbstractCursorProcessor ia){
		Set<AbstractCursorProcessor> keys = registeredProcessorsToPriority.keySet();
		for (Iterator<AbstractCursorProcessor> iterator = keys.iterator(); iterator.hasNext();) {
			AbstractCursorProcessor inputAnalyzer = iterator.next();
			if (inputAnalyzer.equals(ia)){
				iterator.remove();
			}
		}
//		if (interestedAnalyzersToPriority.containsKey(ia)){ //FIXME REMOVE, NOT RELIABLE - BUG?
//			interestedAnalyzersToPriority.remove(ia);
//		}
	}
	
	
	
	/**
	 * Unlocks this cursor from the specified processor.
	 * If the priority by which this cursor is locked changes by that, 
	 * the <code>cursorUnlocked</code> method is invoked on processors 
	 * with a lower priority who by that get a chance to lock this cursor again.
	 * 
	 * @param ia the AbstractCursorProcessor
	 */
	public void unlock(AbstractCursorProcessor ia){
		logger.debug(ia.getName() + " UNLOCKING cursor: " + this.getId());
		
		float beforeLockPriority = this.getCurrentLockPriority();
		float unlockingGesturePriority = ia.getLockPriority();
		
//		if (lockSeekingAnalyzersToPriority.containsKey(ia)){ //FIXME WARUM MANCHE NICHT IN LISTE DIE SEIN SOLLTEN??
//			//remove the analyzer from the priority map in any case
//			lockSeekingAnalyzersToPriority.remove(ia); 
//		}
			Set<AbstractCursorProcessor> keys = lockingProcessorsToPriority.keySet();
			for (Iterator<AbstractCursorProcessor> iterator = keys.iterator(); iterator.hasNext();) {
				AbstractCursorProcessor inputProcessor = iterator.next();
				if (inputProcessor.equals(ia)){
					iterator.remove();
					logger.debug("Removed " + ia + " from lockSeekingAnalyzersToPriority list.");
				}
			}
		
			//dont send released signal if cursor was consumed by higher priority anyway
			//should actually not occur because we should only call release when we have a lock on the cursor
			if (beforeLockPriority > unlockingGesturePriority){ 
				logger.debug("Trying to unlock cursor, but cursor was already locked by higher priority.");
				return;
			}
			
			float afterRemoveLockPriority = this.getCurrentLockPriority();
			//Only send released signal if the priority really was lowered by releasing (there can be more than 1 lock with the same lock priority)
			if (beforeLockPriority > afterRemoveLockPriority){ 
				if (!registeredProcessorsToPriority.isEmpty()){
					//Get strictly smaller priority gestures than the one releasing, so that the ones with same priority dont get a signal
					SortedMap<AbstractCursorProcessor, Float> lesserPriorityGestureMap = registeredProcessorsToPriority.headMap(registeredProcessorsToPriority.lastKey());
//					SortedMap<IInputAnalyzer, Integer> lesserPriorityGestureMap = watchingAnalyzersToPriority.headMap(ia);
					Set<AbstractCursorProcessor> lesserPriorityGestureKeys = lesserPriorityGestureMap.keySet();
                    for (AbstractCursorProcessor processor : lesserPriorityGestureKeys) {
                        //Only send released signal to the analyzers whos priority is higher than the current cursor priority
                        //the current highest priority of the cursor can change when released is called on a gesture that successfully
                        //locks this cursor, so check each loop iteration
                        if (processor.getLockPriority() < unlockingGesturePriority //Only call on gestures with a lower priority than the one releasing the lock
                                && this.getCurrentLockPriority() <= processor.getLockPriority() //only call unLocked on processors with a lower or equal lockpriority
//                                && this.isLockedBy(processor) //FIXME TEST
                                ) {
//                            processor.cursorUnlocked(this);
                        	processor.cursorFreed(this);
                            //FIXME funktioniert das, wenn bei getLock in anderer geste wieder was in die liste geadded wird etc?
                        }
                    }
				}
			}
		
	}

	

	/**
	 * Adds the event.
	 * 
	 * @param te the te
	 */
	protected void addEvent(AbstractCursorInputEvt te){
		this.events.add(te);
//		if (events.size() > EVENT_HISTORY_DEPTH && events.size() > 30){
//            events.subList(0, 30).clear();
//        }
		if (events.size() > EVENT_HISTORY_DEPTH ){
          events.remove(0);
          //logger.debug(this.getId() + " - First event removed!");
//          System.out.println("First event removed!");
      }
	}

	
	/**
	 * Contains event.
	 * 
	 * @param te the te
	 * 
	 * @return true, if successful
	 */
	public boolean containsEvent(AbstractCursorInputEvt te){
		return this.events.contains(te);
	}
	
	
	
	/**
	 * Generate new id.
	 * 
	 * @return the long
	 */
	synchronized private long generateNewID(){
		return currentID++;
	}
	
	
//	/**
//	 * Gets the events.
//	 * 
//	 * @return the events
//	 */
//	public MTConcretePositionEvt[] getEvents(){
//		return this.events.toArray(new MTConcretePositionEvt[this.events.size()]);
//	}
	
	/**
	 * Gets the events.
	 * 
	 * @return the events
	 */
	public List<AbstractCursorInputEvt> getEvents(){
		return this.events;
	}
	
	
	/**
	 * Gets the events.
	 * 
	 * @param millisAgo the millis ago
	 * 
	 * @return the events
	 */
	public List<AbstractCursorInputEvt> getEvents(int millisAgo){
		ArrayList<AbstractCursorInputEvt> result = new ArrayList<AbstractCursorInputEvt>();
		List<AbstractCursorInputEvt> allEvents = this.getEvents();
		long now = System.currentTimeMillis();
//		for (int i = 0; i < allEvents.size(); i++) {
//			if(now-allEvents.get(i).getWhen()<millisAgo){
//				result.add(allEvents.get(i));
//			}
//		}
		for (int i = allEvents.size()-1; i > 0; i--) {
			if((now - allEvents.get(i).getTimeStamp()) < millisAgo){
				result.add(allEvents.get(i));
			}  
			else{// schleife abbrechen wenn falsch damit rest nicht durchsucht werden muss
				break;
			}
		}
		return result;
	}
	
	
	/**
	 * Gets the last event.
	 * 
	 * @return the last event
	 */
	public AbstractCursorInputEvt getCurrentEvent(){
		if(this.events.size()==0){ 
			return null;
		}else{
			return this.events.get(this.getEventCount()-1);
		}
	}
	
	/**
	 * Gets the evt before last event.
	 * 
	 * @return the evt before last event
	 */
	public AbstractCursorInputEvt getPreviousEvent(){
		if(this.events.size()<2){
			return null;
		}else{
			return this.events.get(this.getEventCount()-2);
		}
	}
	
	/**
	 * Gets the current events position x.
	 * 
	 * @return the current events position x
	 */
	public float getCurrentEvtPosX(){
		return this.getCurrentEvent().getX();
	}
	
	/**
	 * Gets the current events position y.
	 * 
	 * @return the current events position y
	 */
	public float getCurrentEvtPosY(){
		return this.getCurrentEvent().getY();
	}
	
	
	/**
	 * Gets the position.
	 * @return the position
	 */
	public Vector3D getPosition(){
		return new Vector3D(getCurrentEvtPosX(), getCurrentEvtPosY()); 
	}
	
	
	/**
	 * Gets the target component of this cursor. (The component the cursor started on) 
	 * or null if it has no target or the cursor has no input events yet.
	 *
	 * @return the target
	 */
	public IMTComponent3D getTarget(){
		if (this.getCurrentEvent() != null){
			return this.getCurrentEvent().getTarget();
		}else{
			return null;
		}
	}
	
	public IMTComponent3D getCurrentTarget(){
		if (this.getCurrentEvent() != null){
			return this.getCurrentEvent().getCurrentTarget();
		}else{
			return null;
		}
	}
	
	/**
	 * Gets the start position x.
	 * 
	 * @return the start position x
	 */
	public float getStartPosX(){
		return this.getFirstEvent().getX();
	}	
	
	/**
	 * Gets the start position y.
	 * 
	 * @return the start position y
	 */
	public float getStartPosY(){
		return this.getFirstEvent().getY();
	}	
	
	
	/**
	 * Gets the start position.
	 * @return the start position
	 */
	public Vector3D getStartPosition(){
		return new Vector3D(getStartPosX(), getStartPosY()); 
	}
	
	/**
	 * Gets the previous event of.
	 * 
	 * @param te the te
	 * 
	 * @return the previous event of
	 */
	public AbstractCursorInputEvt getPreviousEventOf(AbstractCursorInputEvt te){ 
		List<AbstractCursorInputEvt> allEvents = this.getEvents();
		AbstractCursorInputEvt returnEvent = null;
		
//		for (int i = 0; i < allEvents.length; i++) {
//			T event = allEvents[i];
//			
//			if (event.equals(te) && allEvents[i-1] != null) {
//				returnEvent = allEvents[i-1] ;
//			}
//		}
//		return returnEvent;
		
		for (int i = 0; i < allEvents.size(); i++) {
			AbstractCursorInputEvt event = allEvents.get(i);
			
			if (event.equals(te) 
				&& (allEvents.size() >= 2) 
				&& i-1 > 0
				&& allEvents.get(i-1) != null) 
			{
				returnEvent = allEvents.get(i-1);
			}
		}
		return returnEvent;
	}
	
	
	/**
	 * Gets the first event.
	 * 
	 * @return the first event
	 */
	public AbstractCursorInputEvt getFirstEvent(){
		if(this.events.size()==0){
			return null;
		}else{
			return this.events.get(0);
		}
	}
	
	/**
	 * Gets the event count.
	 * 
	 * @return the event count
	 */
	public int getEventCount(){
		return this.events.size();
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public long getId() {
		return this.ID;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj instanceof InputCursor){
			InputCursor compare = (InputCursor)obj;
			return this.getId() == compare.getId(); 
		}else{
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (""+this.ID).hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s=("Cursor id=" +this.ID) + "\n";
		for (int i = 0; i < this.events.size(); i++) {
			s += "\t" + i + ": " + this.events.get(i)+ "\n";
		}
		return s;
	}


	public void printLockSeekingAnalyzerList() {
		Set<AbstractCursorProcessor> claimed = lockingProcessorsToPriority.keySet();
		logger.debug("Lock seeking processors list of cursor: " + this.getId());
        for (AbstractCursorProcessor inputAnalyzer : claimed) {
            logger.debug(inputAnalyzer.getClass() + " " + " Priority: " + inputAnalyzer.getLockPriority());
        }
	}



	public void printInterestedAnalyzersList() {
		Set<AbstractCursorProcessor> watching = registeredProcessorsToPriority.keySet();
		logger.debug("Interested processors list of cursor: " + this.getId());
        for (AbstractCursorProcessor inputAnalyzer : watching) {
            logger.debug(inputAnalyzer.getClass() + " " + " Priority: " + inputAnalyzer.getLockPriority());
        }
	}


	/*
	//TODO make velocity time based?
	//FIXME EXPERIMENTAL!
	public float getVelocityX(){
		if (this.events.isEmpty() || this.events.size() < 2)
			return 0;
		
		
		AbstractCursorInputEvt posEvt 	= events.get(events.size()-1);
		AbstractCursorInputEvt prev 	= events.get(events.size()-2);
		
		if (prev == null)
			prev = posEvt;
		
		Vector3D pos 		= new Vector3D(posEvt.getPosX(), 	posEvt.getPosY(), 	0);
		Vector3D prevPos 	= new Vector3D(prev.getPosX(), 	prev.getPosY(), 	0);
		
        float invWidth = 1.0f/MT4jSettings.getInstance().getScreenWidth();
        
//		System.out.println("Pos: " + pos);
		float mouseNormX = pos.x * invWidth;
		float mouseVelX = (pos.x - prevPos.x) * invWidth;
		System.out.println("Mouse vel X: " + mouseVelX + " mouseNormX:" + mouseNormX);
		return mouseVelX;
	}
	
	//FIXME EXPERIMENTAL!
	public float getVelocityY(){
		if (this.events.isEmpty() || this.events.size() < 2)
			return 0;
		
		AbstractCursorInputEvt posEvt 	= events.get(events.size()-1);
		AbstractCursorInputEvt prev 	= events.get(events.size()-2);
		
		if (prev == null)
			prev = posEvt;
		
		Vector3D pos 		= new Vector3D(posEvt.getPosX(), posEvt.getPosY(), 	0);
		Vector3D prevPos 	= new Vector3D(prev.getPosX(), 	prev.getPosY(), 	0);
		
        float invHeight = 1.0f/MT4jSettings.getInstance().getScreenHeight();
        
		float mouseNormY = pos.y * invHeight;
		float mouseVelY = (pos.y - prevPos.y) * invHeight;
		System.out.println("Mouse vel Y: " + mouseVelY + " mouseNormY:" + mouseNormY);
		return mouseVelY;
	}
	*/
	
	
	public Vector3D getDirection(){
		if (this.events.isEmpty() || this.events.size() < 2)
			return Vector3D.ZERO_VECTOR;
		
		AbstractCursorInputEvt posEvt 	= events.get(events.size()-1);
		AbstractCursorInputEvt prev 	= events.get(events.size()-2);
		if (prev == null)
			prev = posEvt;
		//TODO normalize direction or not?
		return new Vector3D(posEvt.getX() - prev.getX(), posEvt.getY() - prev.getY(), 0);
	}
	
	
	/**
	 * Calculates and returns the velocity vector. 
	 * The calculation takes the events of the last milliseconds into account.
	 * The calculation is not physically correct but provides a good vector to use as
	 * inertia.
	 * 
	 * @return the velocity vector
	 */
	public Vector3D getVelocityVector(){
		return getVelocityVector(120);
	}
	
	/**
	 * Calculates and returns the velocity vector.
	 * The calculation takes the events of the last milliseconds into account.
	 * The calculation is not physically correct but provides a good vector to use as
	 * inertia.
	 * 
	 * @param millisAgo the all events from millis ago are taken into calculation
	 * 
	 * @return the velocity vector
	 */
	public Vector3D getVelocityVector(int millisAgo){
		List<AbstractCursorInputEvt> lastEvents = getEvents(millisAgo);
		//System.out.println("Events " + millisAgo + "ms ago: " + lastEvents.size());
		
		float lastX = 0;
		float lastY = 0;
		
		float totalX = 0;
		float totalY = 0;
		int size = lastEvents.size();
		for (int i = 0; i < size; i++) {
			 AbstractCursorInputEvt ce = lastEvents.get(i);
			 float x = ce.getX();
			 float y = ce.getY();
			 
			 if (i == 0){
				 lastX = x;
				 lastY = y;
			 }

			 totalX += x - lastX;
			 totalY += y - lastY;
			 
			 lastX = x;
			 lastY = y;
		}
		
//		totalX /= 20f;
//		totalY /= 20f;
		
		totalX *= -0.2f;
		totalY *= -0.2f;
		
		//works ok with damping float dampingValue = 0.85f; later
		
		//System.out.println("X total: " + totalX);
		//System.out.println("Y total: " + totalY);
		return new Vector3D(totalX, totalY);
	}
	
//    public double getAngleFromStartPoint() {
//        if(this.getEventCount()<=1){
//        	return 0.0;
//        }
//        else{
//        	int lastElemIndex = getEventCount()-1;
//        	CursorEvent[] events = this.getEvents();
//        	double x1 = events[0].getXRel();
//        	double x2 = events[lastElemIndex].getXRel();
//        	double y1 = events[0].getYRel();
//        	double y2 = events[lastElemIndex].getYRel();
//	
//        	Position p1 = events[0].getPosition();
//        	Position p2 = events[lastElemIndex].getPosition();
//        	return AngleUtil.calcAngle(p1,p2);
//        }
//    }
//    
    
    
}

