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
package org.mt4j.input;

import java.util.*;
import java.util.Map.Entry;

import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;

public class GestureEventSupport {
//	private List<IGestureEventListener> gestureListeners;
	
	/** The gestureEvtSenders to listener. */
	private Map<Class<? extends IInputProcessor>, IGestureEventListener[]> gestureSenderToGestureListener;
	
	/** The Constant EMPTY. */
	private static final IGestureEventListener[] EMPTY = {};
	
	
	public GestureEventSupport() {
		super();
		
//		this.gestureListeners = new ArrayList<IGestureEventListener>();
	}


//	/**
//	 * Constructs a <code>PropertyChangeSupport</code> object.
//	 * 
//	 * @param sourceBean  The bean to be given as the source for any events.
//	 */
//    public GestureEventSupport(Object sourceBean) {
//        if (sourceBean == null) {
//            throw new NullPointerException();
//        }
//        source = sourceBean;
//    }
    
    /**
     * Add a gestureEvtSenderChangeListener to the listener map.
     * If <code>listener</code> is null, no exception is thrown and no action
     * is taken.
     * 
     * @param gestureEvtSender the gestureEvtSender
     * @param listener  The gestureEvtSenderChangeListener to be added
     */
    public synchronized void addGestureEvtListener(Class<? extends IInputProcessor> gestureEvtSender, IGestureEventListener listener) {
        if (listener == null) {
            return;
        }
        
        this.lazyInitializeMap();
        
        IGestureEventListener[] array = this.gestureSenderToGestureListener.get(gestureEvtSender);
        
        //Add listener to array 
        int size = (array != null) ? array.length  : 0;
        IGestureEventListener[] clone = newArray(size + 1);
        clone[size] = listener;
        if (array != null) {
            System.arraycopy(array, 0, clone, 0, size);
        }
        
        //Put new listener array into map
        this.gestureSenderToGestureListener.put(gestureEvtSender, clone);
    }
    
    
    /**
     * Creates a new array of listeners of the specified size.
     * 
     * @param length the length
     * 
     * @return the gestureEvtSender change listener[]
     */
    protected IGestureEventListener[] newArray(int length) {
        return (0 < length) ? new IGestureEventListener[length] : EMPTY;
    }

    
    /**
     * Removes a IGestureEventListener to the listener map.
     * Throws no error if the listener isnt found.
     * 
     * @param gestureEvtSender the gestureEvtSender
     * @param listener the listener
     */
    public synchronized void removeGestureEventListener(Class<? extends IInputProcessor> gestureEvtSender, IGestureEventListener listener) {
    	if (listener == null || gestureEvtSender == null) {
            return;
        }
    	
    	 this.lazyInitializeMap();
    	 
    	  if (this.gestureSenderToGestureListener != null) {
              IGestureEventListener[] array = this.gestureSenderToGestureListener.get(gestureEvtSender);
              if (array != null) {
            	  
                  for (int i = 0; i < array.length; i++) {
                      if (listener.equals(array[i])) {
                          int size = array.length - 1;
                          if (size > 0) {
                        	  IGestureEventListener[] clone = newArray(size);
                              System.arraycopy(array, 0, clone, 0, i);
                              System.arraycopy(array, i + 1, clone, i, size - i);
                              this.gestureSenderToGestureListener.put(gestureEvtSender, clone);
                          }
                          else {
                              this.gestureSenderToGestureListener.remove(gestureEvtSender);
                              if (this.gestureSenderToGestureListener.isEmpty()) {
                                  this.gestureSenderToGestureListener = null;
                              }
                          }
                          break;
                      }
                  }
              }
          }
    }
    
    
    /**
     * Clear listeners.
     */
    public synchronized void clearListeners(){
    	if (this.gestureSenderToGestureListener == null){
    		return;
    	}
    	
    	 this.lazyInitializeMap();
    	 
    	 this.gestureSenderToGestureListener.clear();
    }
    
    /**
     * Fire an existing GestureEvent to any registered listeners.
     * 
     * @param evt  The GestureEvent object.
     */
    public void fireGestureEvt(MTGestureEvent evt) {
    	if (this.gestureSenderToGestureListener != null 
        && !this.gestureSenderToGestureListener.isEmpty()
        ){
//	    	Class<? extends GestureEventFireMarker> gestureEvtSenderName = evt.getgestureEvtSender();
	    	
	    	Class<? extends IInputProcessor> marker = evt.getSource().getClass();
	        
	        IGestureEventListener[] common = this.gestureSenderToGestureListener.get(null);
	        IGestureEventListener[] named = (marker != null) ? this.gestureSenderToGestureListener.get(marker) : null;
	
	        this.fire(common, evt);
	        this.fire(named, evt);
    	}
    }

    
    
    /**
     * Fires the events to the listeners.
     * 
     * @param listeners the listeners
     * @param event the event
     */
    private void fire(IGestureEventListener[] listeners, MTGestureEvent event) {
        if (listeners != null) {
            for (IGestureEventListener listener : listeners) {
                listener.processGestureEvent(event);
            }
        }
    }
    
    
    /**
     * Returns all listeners in the map.
     * 
     * @return an array of all listeners
     */
    public final synchronized IGestureEventListener[] getListeners() {
        if (this.gestureSenderToGestureListener == null) {
            return newArray(0);
        }
        List<IGestureEventListener> list = new ArrayList<IGestureEventListener>();

        IGestureEventListener[] listeners = this.gestureSenderToGestureListener.get(null);
        if (listeners != null) {
            list.addAll(Arrays.asList(listeners));
            //for (IGestureEventListener listener : listeners) {
             //   list.add(listener);
            //}
        }
        
        for (Entry<Class<? extends IInputProcessor>, IGestureEventListener[]> entry : this.gestureSenderToGestureListener.entrySet()) {
        	Class<? extends IInputProcessor> gestureEvtSender = entry.getKey();
            if (gestureEvtSender != null) {
                list.addAll(Arrays.asList(entry.getValue()));
                //for (IGestureEventListener listener : entry.getValue()) {
                 //   list.add(listener);
                //}
            }
        }
        return list.toArray(newArray(list.size()));
    }

    
    /**
     * Returns listeners that have been associated with the named gestureEvtSender.
     * 
     * @param gestureEvtSender  the name of the property
     * 
     * @return an array of listeners for the named property
     */
    public final IGestureEventListener[] getListeners(Class<? extends IInputProcessor> gestureEvtSender) {
        if (gestureEvtSender != null) {
        	IGestureEventListener[] listeners =  this.gestureSenderToGestureListener.get(gestureEvtSender);
            if (listeners != null) {
                return listeners.clone();
            }
        }
        return newArray(0);
    }

    /**
     * Indicates whether the map contains
     * at least one listener to be notified.
     * 
     * @param gestureEvtSender the gestureEvtSender
     * 
     * @return      {@code true} if at least one listener exists or
     * {@code false} otherwise
     */
    public final synchronized boolean hasListeners(Class<? extends IInputProcessor> gestureEvtSender) {
        if (this.gestureSenderToGestureListener == null) {
            return false;
        }
        IGestureEventListener[] array = this.gestureSenderToGestureListener.get(null);
        return (array != null) || ((gestureEvtSender != null) && (null != this.gestureSenderToGestureListener.get(gestureEvtSender)));
    }
    
    
    /**
     * Checks if the map is null and then lazily initializes it.
     */
    private void lazyInitializeMap(){
    	if (gestureSenderToGestureListener == null){
    		 gestureSenderToGestureListener = new HashMap<Class<? extends IInputProcessor>, IGestureEventListener[]>();
    	}
    }



	
	
}
