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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mt4j.util.math.Vector3D;




/**
 * Acts as a visitor to the scene and collects the pick information if
 * any objects were hit (picked).
 * Later, whe can retrieve the nearest picked object and its intersection point.
 * 
 * @author Christopher Ruff
 */
public class PickResult {
	
	/** The pick list. */
//	private ArrayList<MTComponent> pickList;
	
	/** The comp to inter section point. */
//	private WeakHashMap<MTComponent, Vector3D[]> compToInterSectionPoint;
	
	private List<PickEntry> pickEntries;
	
	private boolean isAlreadySorted;
	
	/** 
	 * Sometimes the wrong obj gets picked if they are on the same plane but with different inverted rays..
	 * probably math rounding off errors with floats etc. (at inverting the ray?) 
	 * <br>This makes sure, objs which are checked later for a hit, 
	 * (and are probably drawn ontop of the previous ones because drawn later), 
	 * are picked more likely.
	 * <br>Still this is kind of a hack
	 */
	public static final float HIT_TOLERANCE = 0.1f; //0.03f; //FIXME reset to old value!?
	
	/**
	 * Instantiates a new pick result.
	 */
	public PickResult() {
		super();
//		pickList = new ArrayList<MTComponent>();
//		compToInterSectionPoint = new WeakHashMap<MTComponent, Vector3D[]>();
		
		pickEntries = new ArrayList<PickEntry>();
		isAlreadySorted = false;
	}

	/**
	 * This should only be called by the scene while this objects visits all scene nodes.
	 * 
	 * @param hitObject the hit object
	 * @param intersectionPoint the intersection point
	 * @param distance the distance
	 */
	public void addPickedObject(MTComponent hitObject, Vector3D intersectionPoint, float distance){
//		pickList.add(hitObject);
//		compToInterSectionPoint.put(hitObject, new Vector3D[]{intersectionPoint, new Vector3D(distance,distance,distance)}); //hack
		
		pickEntries.add(new PickEntry(hitObject, intersectionPoint, distance));
		int lastIndex = pickEntries.size()-1;
		pickEntries.get(lastIndex).originalOrderIndex = lastIndex;
		isAlreadySorted = false;
	}

	/**
	 * Returns the picked component.
	 * This is the last in the list of picked components, not neccessarily the one with
	 * the shortest distance from the pickray (<code>setComposite</code> can interfere with that). But usually it should be
	 * the nearest one to the origin of the pick. :)
	 * 
	 * @return the nearest pick result
	 * 
	 * the picked component or null if nothing could be picked
	 */
	public MTComponent getNearestPickResult(){
		if (this.isEmpty())
			return null;
		else{
			return this.getPickList().get(0).hitObj;
//			return this.pickEntries.get(pickEntries.size()-1).hitObj;
		}
		
		/*
		if (this.isEmpty())
			return null;
		else
			return pickList.get(pickList.size()-1);
		 */
	}
	
	
	public PickEntry getNearestPickEntry(){
		if (this.isEmpty())
			return null;
		else{
			return this.getPickList().get(0);
//			return this.pickEntries.get(pickEntries.size()-1).hitObj;
		}
	}


	/**
	 * Gets the pick list.
	 * 
	 * @return the pick list
	 */
	public List<PickEntry> getPickList() {
//		return pickList;
		this.sort();
		return pickEntries;
	}

//	public void addPickedObjects(ArrayList<MTBaseComponent> pickList) {
//		pickList.addAll(pickList);
//	}
	
/**
 * Returns the distance of the origin of the pick to the nearest picked obj.
 * 
 * @return the distance nearest pick obj
 */
	public float getDistanceNearestPickObj(){
//		if (this.isEmpty()){
//			return Float.MAX_VALUE;
//		}else{
//			return getDistanceOfPickedObj(this.getNearestPickResult());
//		}
		return getNearestPickEntry().cameraDistance;
	}
	
	/**
	 * Returns the distance of the origin of the pick to the specified picked obj.
	 * 
	 * @param pickedObj the picked obj
	 * 
	 * @return the distance of picked obj
	 */
	public float getDistanceOfPickedObj(MTComponent pickedObj){
//		return compToInterSectionPoint.get(pickedObj)[1].x;
		
		for (int i = 0; i < getPickList().size(); i++) {
			PickEntry p = pickEntries.get(i);
			if (p.hitObj.equals(pickedObj))
				return p.cameraDistance;
		}
		return Float.MAX_VALUE;
	}
	
	/**
	 * Returns the interseciton point of the specified picked obj.
	 * Returns null if the object isnt in the pick list!
	 * 
	 * @param pickedObj the picked obj
	 * 
	 * @return the inter section point of picked obj
	 */
	public Vector3D getInterSectionPointOfPickedObj(MTComponent pickedObj){
//		return compToInterSectionPoint.get(pickedObj)[0];
		
		for (int i = 0; i < getPickList().size(); i++) {
			PickEntry p = pickEntries.get(i);
			if (p.hitObj.equals(pickedObj)) 
				return p.intersectionPoint;
		}
		return null;
	}
	
	/**
	 * Gets the inter section point nearest picked obj.
	 * 
	 * @return the inter section point nearest picked obj
	 */
	public Vector3D getInterSectionPointNearestPickedObj(){
		if (this.isEmpty()){
			return null;
		}else{
			return getInterSectionPointOfPickedObj(this.getNearestPickResult());
		}
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty(){
//		return pickList.isEmpty();
		return pickEntries.isEmpty();
	}

	public void sort() {
		if (!isAlreadySorted){
			Collections.sort(pickEntries);
			isAlreadySorted = true;
//			printList();
		}
	}
	
	public void printList() {
		sort();
		System.out.println("Pick Entries:");
        for (PickEntry p : pickEntries) {
            System.out.println("Entry: " + p.hitObj + " Distance: " + p.cameraDistance + " Intersection: " + p.intersectionPoint);
        }
	}
	
	
	public class PickEntry implements Comparable<PickEntry>{
		public int originalOrderIndex;
		public Vector3D intersectionPoint;
		public float cameraDistance;
		public MTComponent hitObj;
		
		
		public PickEntry(MTComponent hitObject, Vector3D intersectionPoint2, float distance) {
			this.hitObj = hitObject;
			this.intersectionPoint = intersectionPoint2;
			this.cameraDistance = distance;
		}

		//We give the later picked objects with the same distance priority 
		//(by substracting the hit tolerance from their distance)
		//We do this because they are probably drawn ontop because they are located later in the scene graph
		//Also, we priorize objects that are drawn with depth buffer disabled because they are also in front of others,
		//even if camera distance is farther
		public int compareTo(PickEntry o2) {
			if (o2.equals(this)){
				return 0;
			}
			if (this.originalOrderIndex >= o2.originalOrderIndex){ 
				if (this.cameraDistance - HIT_TOLERANCE <= o2.cameraDistance || isDrawnWithoutDepthBuffer(this.hitObj)){
					return -1;
				}else{
					return 1;
				}
			}else{
				if (o2.cameraDistance - HIT_TOLERANCE <= this.cameraDistance || isDrawnWithoutDepthBuffer(o2.hitObj)){
					return 1;
				}else{
					return -1;
				}
			}
		}
		
//		public boolean addedAfter(PickEntry other){
//			return this.originalOrderIndex >= other.originalOrderIndex;
//		}

		/**
		 * Checks if is drawn without depth buffer.
		 * Since this is inherited to children we have to check
		 * the parents.
		 * 
		 * @param comp the comp
		 * 
		 * @return true, if is drawn without depth buffer
		 */
		public boolean isDrawnWithoutDepthBuffer(MTComponent comp){
			if (comp.isDepthBufferDisabled())
				return true;
			
			MTComponent p = comp.getParent();
			while (p != null){
				if (p.isDepthBufferDisabled())
					return true;
				p = p.getParent();
			}
			return false;
		}
	}
	
	
}
