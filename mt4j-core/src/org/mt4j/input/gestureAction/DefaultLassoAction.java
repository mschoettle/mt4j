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
package org.mt4j.input.gestureAction;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.clusters.Cluster;
import org.mt4j.components.clusters.ClusterManager;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.ILassoable;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.LassoEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.MTColor;

import processing.core.PApplet;

/**
 * The Class DefaultLassoAction.
 * 
 * @author Christopher Ruff
 */
public class DefaultLassoAction implements IGestureEventListener {
	
	/** The cluster mgr. */
	private ClusterManager clusterMgr;
	
	/** The canvas. */
	private MTCanvas canvas;
	
	/** The pa. */
	private PApplet pa;

	/**
	 * Instantiates a new default clustering action.
	 * 
	 * @param pa the pa
	 * @param clustermgr the clustermgr
	 * @param canvas the canvas
	 */
	public DefaultLassoAction(PApplet pa, ClusterManager clustermgr, MTCanvas canvas){
		this.pa = pa;
		this.clusterMgr = clustermgr;
		this.canvas = canvas;
		
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.IGestureEventListener#processGestureEvent(org.mt4j.input.inputProcessors.MTGestureEvent)
	 */
	public boolean processGestureEvent(MTGestureEvent g) {
		if (g instanceof LassoEvent){
			LassoEvent dse = (LassoEvent)g;
			switch (dse.getId()) {
			case MTGestureEvent.GESTURE_RESUMED:
			case MTGestureEvent.GESTURE_STARTED:
				//System.out.println("dse detected");
				canvas.addChild(dse.getSelectionPoly());
				break;
			case MTGestureEvent.GESTURE_UPDATED:
				//System.out.println("dse updated");
				break;
			case MTGestureEvent.GESTURE_CANCELED: 
			case MTGestureEvent.GESTURE_ENDED: 
				//TODO make method addSelection and do the stuff here
				//so it can be called from the outside too /addNewSelection(comps[])
				
				//System.out.println("dse ended");
				ILassoable[] selectedComps = dse.getClusteredComponents();
				
				//Create new selection only if at least more than 1 is selected
				if (selectedComps.length > 1){
					//Create new Cluster
					Cluster cluster = new Cluster(pa, dse.getSelectionPoly());
					//Attach a cam to the cluster because it doesent have the canvas as a parent as it is now..
//					cluster.attachCamera(selectedComps[0].getViewingCamera());
					
					//Add gestures //TODO what about the isRotatable/dragable settings of the childs?
					//TODO What if we want to click a item of the cluster only? ->Maybe cluster should 
					//delegate other gestures to the components..
//					cluster.assignGestureClassAndAction(DragGestureAnalyzer.class, 	 new DefaultDragAction());
					cluster.registerInputProcessor(new DragProcessor(pa));
					cluster.addGestureListener(DragProcessor.class, new DefaultDragAction());
					
					cluster.addGestureListener(DragProcessor.class, new InertiaDragAction());
					
					cluster.registerInputProcessor(new RotateProcessor(pa));
					cluster.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
					
					cluster.registerInputProcessor(new ScaleProcessor(pa));
					cluster.addGestureListener(ScaleProcessor.class,  new DefaultScaleAction());
					
					dse.getSelectionPoly().setFillColor(new MTColor(100,150,250, 50));
					
					dse.getSelectionPoly().setGestureAllowance(DragProcessor.class, true);
					dse.getSelectionPoly().setGestureAllowance(RotateProcessor.class, true);
					dse.getSelectionPoly().setGestureAllowance(ScaleProcessor.class, true);
					
					
					System.out.println("\n" + selectedComps.length + " Selected:");
//					int n = -1;
					int n = Integer.MAX_VALUE;
					//Set all cards selected
					for (ILassoable currentComp : selectedComps){
						System.out.print((currentComp).getName() + "  "); //remove later
						
						if (currentComp instanceof MTComponent){//Add selected comps to selection - RIGHT NOW ONLY SUPPORTS INSTANCES OF MTCOMPONENT!
							MTComponent mtCurrentComp = (MTComponent)currentComp;
							
///////////////////////////////							
							// Listen to destroy events of the clustered components, to remove them from
							// the cluster and pack the polygon.
							mtCurrentComp.addStateChangeListener(StateChange.COMPONENT_DESTROYED, new StateChangeListener(){
								public void stateChanged(StateChangeEvent evt) {
									if (evt.getSource() instanceof MTComponent) {
										MTComponent sourceComp = (MTComponent) evt.getSource();
										
										//Remove component from cluster it is in
										Cluster clusterOfComponent = clusterMgr.getCluster(sourceComp);
										if (clusterOfComponent != null){
											((ILassoable)sourceComp).setSelected(false);
											//Remvove the component from its former selection
											clusterOfComponent.removeChild(sourceComp);
											
											//System.out.println("Comp destroyed and removed from cluster: " + sourceComp.getName());
											
											//remove the former selection from the selectionmanager if it consists only of 1 less components
											if (clusterOfComponent.getChildCount() <= 2){
												clusterMgr.removeCluster(clusterOfComponent);
												
											}else{
												//Tighten convex hull of reduced cluster
												clusterOfComponent.packClusterPolygon();
											}
										}
									}
								}
							});
////////////////////////////////
							
							//Remove comp from former selection if it is in a new selection
							Cluster formerSelection = clusterMgr.getCluster(currentComp);
							if (formerSelection != null){
								formerSelection.removeChild(mtCurrentComp);
								//Remove the former selection from the selectionmanager if it consists only of 1 less components
								if (formerSelection.getChildCount() <= 2){ //2 because the selection polygon is also always in the selection
//									SceneManager.getInstance().getCurrentScene().getMainCanvas().getSelectionManager().removeSelection(formerSelection);
									clusterMgr.removeCluster(formerSelection);
								}else{
									//Tighten convex hull of reduced cluster
									formerSelection.packClusterPolygon();
								}
							}
							
							//Get the last index of the selected component in the parent list to know where to add the selectionpoly
							if (mtCurrentComp.getParent() != null){
								int indexInParentList = mtCurrentComp.getParent().getChildIndexOf(mtCurrentComp);
//								if (indexInParentList > n){
//									n = indexInParentList;
//								}
								if (indexInParentList < n){
									n = indexInParentList;
								}
							}
								
							//ADD components to the selection and set it to selected
							cluster.addChild(mtCurrentComp);
							currentComp.setSelected(true);
						}//if instance mtbasecomp
					}//for
					
					//Draw a convex hull around all selected shapes
					cluster.packClusterPolygon();
					dse.getSelectionPoly().setLineStipple((short)0xDDDD);
					dse.getSelectionPoly().setStrokeColor(new MTColor(0,0,0,255));
					
					//Add the selection poly 1 index after the index of the highest index of the selected components
					if (selectedComps[0] instanceof MTComponent 
						&& ((MTComponent)selectedComps[0]).getParent() != null){
							MTComponent firstSelectedComp = (MTComponent)selectedComps[0];
							
//							System.out.println("n:" + n);
//							System.out.println("Parent childcount: " + firstSelectedComp.getParent().getChildCount());
							
							firstSelectedComp.getParent().addChild(n, dse.getSelectionPoly());
							
//							if (n < firstSelectedComp.getParent().getChildCount())
//								firstSelectedComp.getParent().addChild(n+1, dse.getSelectionPoly());
//							else //FIXME this has caused out of bounds
//								firstSelectedComp.getParent().addChild(n, dse.getSelectionPoly());
						}
					
					//Add selection to selection manager
					clusterMgr.addCluster(cluster);
				//IF exactly 1 component is selected and its already part of an selection remove it from it without making a new selection with it
				}else if (selectedComps.length == 1){ 
					for (ILassoable currentComp : selectedComps){
						if (currentComp instanceof MTComponent){//Add selected comps to selection - RIGHT NOW ONLY SUPPORTS INSTANCES OF MTCOMPONENT!
							//Remove comp from former selection if it is in a new selection
							Cluster formerSelection = clusterMgr.getCluster(currentComp);
							if (formerSelection != null){
								currentComp.setSelected(false);
								//Remvove the component from its former selection
								formerSelection.removeChild((MTComponent)currentComp);
								//remove the former selection from the selectionmanager if it consists only of 1 less components
								if (formerSelection.getChildCount() <= 2){
									clusterMgr.removeCluster(formerSelection);
								}else{
									//Tighten convex hull of reduced cluster
									formerSelection.packClusterPolygon();
								}
							}
						}
					}
					
					//Remove the Selection Polygon from the canvas when only 1 component is selected
					clusterMgr.removeClusterPolyFromCanvas(dse.getSelectionPoly());
				}
				//If no comp is selected, just remove the selection polygon from canvas
				else if (selectedComps.length < 1){ 
					//Remove the Selection Polygon from the canvas when no component is selected
					clusterMgr.removeClusterPolyFromCanvas(dse.getSelectionPoly());
				}
				break;
			}//switch
		}//instanceof clusterevt
		return false;
	}//processgesture()

}
