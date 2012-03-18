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
package org.mt4j.components.clusters;


import java.util.ArrayList;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.ILassoable;

/**
 * The Class ClusterManager.
 * @author Christopher Ruff
 */
public class ClusterManager {
	
	/** The child objects. */
	private ArrayList<Cluster> childObjects;
	
	/** The canvas. */
	private MTCanvas canvas;

	/**
	 * Instantiates a new cluster manager.
	 *
	 * @param canvas the canvas
	 */
	public ClusterManager(MTCanvas canvas) {
		this.canvas = canvas;
		childObjects = new ArrayList<Cluster>();
	}
	
	/**
	 * Gets the cluster count.
	 * 
	 * @return the cluster count
	 */
	public int getClusterCount(){
		return childObjects.size();
	}

	/**
	 * Adds the cluster.
	 * 
	 * @param selection the selection
	 */
	public void addCluster(Cluster selection){
		childObjects.add(selection);
	}
	
	/**
	 * Adds the all clusters.
	 * 
	 * @param selections the selections
	 */
	public void addAllClusters(Cluster[] selections){
        for (Cluster object : selections) {
            childObjects.add(object);
        }
	}
	
	/**
	 * removes the Cluster from the Cluster manager and
	 * also tries to delete its visible polygon from the current scenes'
	 * main canvas,
	 * also calls setSelected(false) on the Selections children.
	 * 
	 * @param selection the selection
	 */
	public void removeCluster(Cluster selection){
		//Remove the Selection Polygon of the now empty former selection from the canvas
		this.removeClusterPolyFromCanvas(selection.getClusterPolygon());
		
		//mark the remaining components from the deleted slection as not selected
		for (int i = 0; i < selection.getChildCount(); i++) {
			MTComponent comp = selection.getChildByIndex(i);
			if (comp instanceof ILassoable)
				((ILassoable)comp).setSelected(false);
		}
		
		if (this.containsCluster(selection))
			childObjects.remove(selection);
	}
	
	/**
	 * removes the visible selection polygon from the canvas.
	 * 
	 * @param selectionPoly the selection poly
	 */
	public void removeClusterPolyFromCanvas(MTPolygon selectionPoly){
		if (selectionPoly != null && canvas.containsChild(selectionPoly)){
			selectionPoly.getParent().removeChild(selectionPoly);
		}
	}
	
	/**
	 * Removes the cluster.
	 * 
	 * @param i the i
	 */
	public void removeCluster(int i){
		childObjects.remove(i);
	}
	
	/**
	 * Removes the all clusters.
	 */
	public void removeAllClusters(){
		childObjects.clear();
	}
	
	
	/**
	 * Gets the clusters.
	 * 
	 * @return the clusters
	 */
	public Cluster[] getClusters(){
		return childObjects.toArray(new Cluster[childObjects.size()]);
	}
	
	
	/**
	 * Contains cluster.
	 * 
	 * @param selection the selection
	 * 
	 * @return true, if successful
	 */
	public boolean containsCluster(Cluster selection){
		return (childObjects.contains(selection));
	}
	
	//only returns the first selection that contains that component
	//only works with mtcomponents right now
	/**
	 * Gets the cluster.
	 * 
	 * @param component the component
	 * 
	 * @return the cluster
	 */
	public Cluster getCluster(IMTComponent3D component){
        for (Cluster selection : childObjects) {
            if (component instanceof MTComponent && selection.containsDirectChild((MTComponent) component)) {
                return selection;
            }
        }
		return null;
	}


}
