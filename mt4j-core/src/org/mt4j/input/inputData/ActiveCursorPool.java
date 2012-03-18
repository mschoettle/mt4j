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

import java.util.Collection;
import java.util.HashMap;



/**
 * This class is basically a centralized wrapper for a HashMap acting as a store
 * for active inputs.
 * cursors are added when a new Touch is registered. cursors should be put in the map by
 * using their cursor-ID. When the cursor has ended they have to be removed again.
 * 
 * @author Christopher Ruff
 */
public class ActiveCursorPool {
	
	/** The cursor pool. */
	private static ActiveCursorPool cursorPool;
	
	/** The cursors map. */
	private HashMap<Long, InputCursor> cursorMap;
	
	/**
	 * Instantiates a new cursor pool.
	 */
	private ActiveCursorPool(){
		cursorMap = new HashMap<Long, InputCursor>();
	}
	
	/**
	 * Gets the single instance of cursorPool.
	 * 
	 * @return single instance of cursorPool
	 */
	public static ActiveCursorPool getInstance(){
		if (cursorPool == null){
			cursorPool = new ActiveCursorPool();
			return cursorPool;
		}else{
			return cursorPool;
		}
	}

	
	/**
	 * Put cursor. The id is assumed to be the cursors ID.
	 * 
	 * @param cursorID the cursor id
	 * @param m the m
	 */ //TODO automate because id = cursorID?
	public void putActiveCursor(long cursorID, InputCursor m){
		cursorMap.put(cursorID, m);
	}
	
	
	/**
	 * Gets the cursor by id.
	 * 
	 * @param ID the iD
	 * 
	 * @return the cursor by id
	 */
	public InputCursor getActiveCursorByID(long ID){
		return cursorMap.get(ID);
	}
	
	public InputCursor[] getActiveCursors(){
		Collection<InputCursor> values = cursorMap.values();
		return values.toArray(new InputCursor[values.size()]);
		/*
		Set<Long> keys = cursorsMap.keySet();
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			Long long1 = (Long) iterator.next();
			Inputcursor cursor = cursorsMap.get(long1);
			MTCursorInputEvt lastEvt = cursor.getLastEvent();
			MTCursorInputEvt copy = (MTCursorInputEvt) lastEvt.clone();
			copy.setId(MTCursorInputEvt.INPUT_ENDED);
			
		}
		return null;
		*/
	}
	
	
	/**
	 * Removes the cursor.
	 * 
	 * @param ID the iD
	 * @return the input cursor
	 */
	public InputCursor removeCursor(long ID){
		return cursorMap.remove(ID);
	}
	
	/**
	 * Gets the active cursor count.
	 * 
	 * @return the active cursor count
	 */
	public long getActiveCursorCount(){
		return cursorMap.size();
	}
	
	
	

	
}
