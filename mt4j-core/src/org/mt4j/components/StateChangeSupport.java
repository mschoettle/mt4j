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
package org.mt4j.components;

import java.util.*;
import java.util.Map.Entry;

/**
 * The Class StateChangeSupport.
 * A helper class to delegate state change event handling and dispatching to.
 * 
 */
public class StateChangeSupport {
	
	/** The source. */
	private Object source;
	
	/** The states to listener. */
	private Map<StateChange, StateChangeListener[]> statesToListener;
	
	/** The Constant EMPTY. */
	private static final StateChangeListener[] EMPTY = {};

	/**
	 * Constructs a <code>PropertyChangeSupport</code> object.
	 * 
	 * @param sourceBean  The bean to be given as the source for any events.
	 */
    public StateChangeSupport(Object sourceBean) {
        if (sourceBean == null) {
            throw new NullPointerException();
        }
        source = sourceBean;
    }
    
    /**
     * Add a StateChangeListener to the listener map.
     * If <code>listener</code> is null, no exception is thrown and no action
     * is taken.
     * 
     * @param state the state
     * @param listener  The StateChangeListener to be added
     */
    public synchronized void addStateChangeListener(StateChange state, StateChangeListener listener) {
        if (listener == null) {
            return;
        }
        
        this.lazyInitMap();
        
        StateChangeListener[] array = this.statesToListener.get(state);
        
        //Add listener to array 
        int size = (array != null) ? array.length  : 0;
        StateChangeListener[] clone = newArray(size + 1);
        clone[size] = listener;
        if (array != null) {
            System.arraycopy(array, 0, clone, 0, size);
        }
        
        //Put new listener array into map
        this.statesToListener.put(state, clone);
    }
    
    
    /**
     * Creates a new array of listeners of the specified size.
     * 
     * @param length the length
     * 
     * @return the state change listener[]
     */
    protected StateChangeListener[] newArray(int length) {
        return (0 < length) ? new StateChangeListener[length] : EMPTY;
    }

    
    /**
     * Removes a StateChangeListener to the listener map.
     * Throws no error if the listener isnt found.
     * 
     * @param state the state
     * @param listener the listener
     */
    public synchronized void removeStateChangeListener(StateChange state, StateChangeListener listener) {
    	if (listener == null || state == null) {
            return;
        }
    	
    	 this.lazyInitMap();
    	 
    	  if (this.statesToListener != null) {
              StateChangeListener[] array = this.statesToListener.get(state);
              if (array != null) {
            	  
                  for (int i = 0; i < array.length; i++) {
                      if (listener.equals(array[i])) {
                          int size = array.length - 1;
                          if (size > 0) {
                        	  StateChangeListener[] clone = newArray(size);
                              System.arraycopy(array, 0, clone, 0, i);
                              System.arraycopy(array, i + 1, clone, i, size - i);
                              this.statesToListener.put(state, clone);
                          }
                          else {
                              this.statesToListener.remove(state);
                              if (this.statesToListener.isEmpty()) {
                                  this.statesToListener = null;
                              }
                          }
                          break;
                      }
                  }
              }
          }
    }
    
    
    /**
     * Fire state change.
     * 
     * @param state the state that has changed
     */
    public void fireStateChange(StateChange state) {
    	if (this.statesToListener != null 
    	&& !this.statesToListener.isEmpty()
    	){
    		this.fireStateChange(new StateChangeEvent(source, state));
    	}
    }
    
    
    /**
     * Fire an existing StateChangeEvent to any registered listeners.
     * 
     * @param evt  The StateChangeEvent object.
     */
    public void fireStateChange(StateChangeEvent evt) {
    	if (this.statesToListener != null 
        && !this.statesToListener.isEmpty()
        ){
	    	StateChange stateName = evt.getState();
	        
	        StateChangeListener[] common = this.statesToListener.get(null);
	        StateChangeListener[] named = (stateName != null)
	                    ? this.statesToListener.get(stateName)
	                    : null;
	
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
    private void fire(StateChangeListener[] listeners, StateChangeEvent event) {
        if (listeners != null) {
            for (StateChangeListener listener : listeners) {
                listener.stateChanged(event);
            }
        }
    }
    
    
    /**
     * Returns all listeners in the map.
     * 
     * @return an array of all listeners
     */
    public final synchronized StateChangeListener[] getListeners() {
        if (this.statesToListener == null) {
            return newArray(0);
        }
        List<StateChangeListener> list = new ArrayList<StateChangeListener>();

        StateChangeListener[] listeners = this.statesToListener.get(null);
        if (listeners != null) {
            list.addAll(Arrays.asList(listeners));

           //  for (StateChangeListener listener : listeners) {
           //     list.add(listener);
           // }
        }
        
        for (Entry<StateChange, StateChangeListener[]> entry : this.statesToListener.entrySet()) {
        	StateChange state = entry.getKey();
            if (state != null) {
                list.addAll(Arrays.asList(entry.getValue()));

                 // for (StateChangeListener listener : entry.getValue()) {
                 //   list.add(listener);
                //}
            }
        }
        return list.toArray(newArray(list.size()));
    }

    /**
     * Returns listeners that have been associated with the named state.
     * 
     * @param state  the name of the property
     * 
     * @return an array of listeners for the named property
     */
    public final StateChangeListener[] getListeners(StateChange state) {
        if (state != null) {
        	StateChangeListener[] listeners =  this.statesToListener.get(state);
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
     * @param state the state
     * 
     * @return      {@code true} if at least one listener exists or
     * {@code false} otherwise
     */
    public final synchronized boolean hasListeners(StateChange state) {
        if (this.statesToListener == null) {
            return false;
        }
        StateChangeListener[] array = this.statesToListener.get(null);
        return (array != null) || ((state != null) && (null != this.statesToListener.get(state)));
    }
    
    
    /**
     * Checks if the map is null and then lazily initializes it.
     */
    private void lazyInitMap(){
    	if (statesToListener == null){
    		 statesToListener = new HashMap<StateChange, StateChangeListener[]>();
    	}
    }


}
