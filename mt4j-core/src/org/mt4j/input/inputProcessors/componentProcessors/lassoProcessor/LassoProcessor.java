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
package org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTStencilPolygon;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

/**
 * The Class LassoProcessor. This gesture processor should only be
 * registered with a MTCanvas component.
 * Fires LassoEvent gesture events.
 * 
 * @author Christopher Ruff
 */
public class LassoProcessor extends AbstractCursorProcessor {

	/** The pa. */
	private PApplet pa;
	
	/** The canvas. */
	private MTCanvas canvas;
	
	/** The cursor to context. */
	private Hashtable<InputCursor, ClusteringContext> cursorToContext;
	
	/** The drag selectables. */
	private List<ILassoable> dragSelectables;
	
	/** The camera. */
	private Icamera camera;
	
	/** The plane normal. */
	private Vector3D planeNormal;
	
	/** The point in plane. */
	private Vector3D pointInPlane;
	
	private int verticesLimit;
	
	private int minDistance;

	/**
	 * Instantiates a new lasso processor.
	 * 
	 * @param pa the pa
	 * @param canvas the canvas
	 * @param camera the camera
	 */
	public LassoProcessor(PApplet pa, MTCanvas canvas, Icamera camera) {
		super();
		this.pa = pa;
		this.canvas = canvas;
		this.camera = camera;
		this.dragSelectables = new ArrayList<ILassoable>();
		cursorToContext = new Hashtable<InputCursor, ClusteringContext>();
		planeNormal = new Vector3D(0,0,1);
		pointInPlane = new Vector3D(0,0,0);
		this.setLockPriority(1);
		
		if (PlatformUtil.isAndroid()){
			this.verticesLimit = 170;
			this.minDistance = 7;	
		}else{
			this.verticesLimit = 270;
			this.minDistance = 3;
		}
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorStarted(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.AbstractCursorInputEvt)
	 */
	@Override
	public void cursorStarted(InputCursor c, AbstractCursorInputEvt positionEvent) {
		if (this.getLock(c)){
			ClusteringContext context = new ClusteringContext(c);
			if (!context.gestureAborted){
				cursorToContext.put(c, context);
				//To speed things up, selection is only checked at the end of the gesture
				ILassoable[] selectedComps = new ILassoable[0]; //no things selected anyway yet
				this.fireGestureEvent(new LassoEvent(this, MTGestureEvent.GESTURE_STARTED, canvas, c, context.getPolygon(), selectedComps));
			}
		}
		
	}



	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorUpdated(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.AbstractCursorInputEvt)
	 */
	@Override
	public void cursorUpdated(InputCursor c, AbstractCursorInputEvt positionEvent) {
		ClusteringContext context = cursorToContext.get(c);
		if (context != null){ //cursor was used here
			if (!context.gestureAborted){
				context.update(c);
				//TODO visually mark selected cards and give back real selected cards again..
				ILassoable[] selectedComps = new ILassoable[0];
				this.fireGestureEvent(new LassoEvent(this, MTGestureEvent.GESTURE_UPDATED, canvas, c, context.getPolygon(), selectedComps));
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor#cursorEnded(org.mt4j.input.inputData.InputCursor, org.mt4j.input.inputData.AbstractCursorInputEvt)
	 */
	@Override
	public void cursorEnded(InputCursor c, AbstractCursorInputEvt positionEvent) {
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - cursor: " + c.getId());
		ClusteringContext context = cursorToContext.get(c);
		if (context != null){ //cursor was used here
			cursorToContext.remove(c); 
			ILassoable[] selectedComps = context.getselectedComps();
			this.fireGestureEvent(new LassoEvent(this, MTGestureEvent.GESTURE_ENDED, canvas, c, context.getPolygon(), selectedComps));
			this.unLock(c);
		}
	}



	@Override
	public void cursorLocked(InputCursor c, IInputProcessor lockingAnalyzer) {
		if (lockingAnalyzer instanceof AbstractComponentProcessor){
			logger.debug(this.getName() + " Recieved cursor LOCKED by (" + ((AbstractComponentProcessor)lockingAnalyzer).getName()  + ") - cursor ID: " + c.getId());
		}else{
			logger.debug(this.getName() + " Recieved cursor LOCKED by higher priority signal - cursor ID: " + c.getId());
		}
		this.abortGesture(c);
	}



	@Override
	public void cursorUnlocked(InputCursor c) {
		logger.debug(this.getName() + " Recieved UNLOCKED signal for cursor ID: " + c.getId());
		//Do nothing here, we dont want this gesture to be resumable
	}

	
	/**
	 * Abort gesture.
	 * 
	 * @param c the involved cursor
	 */
	public void abortGesture(InputCursor c){
		ClusteringContext context = cursorToContext.get(c);
		if (context != null){ //cursor was used here
			cursorToContext.remove(c); 
			context.update(c);
			//because of aborting we send an empty selection array 
			ILassoable[] selectedComps = new ILassoable[0];
			this.fireGestureEvent(new LassoEvent(this, MTGestureEvent.GESTURE_ENDED, canvas, c, context.getPolygon(), selectedComps));
			logger.debug(this.getName() + " cursor:" + c.getId() + " cursor LOCKED. Was an active cursor in this gesture!");
		}else{
			logger.debug(this.getName() + " cursor LOCKED. But it was NOT an active cursor in this gesture!");
		}
	}
	
	/**
	 * Adds the lassoable.
	 *
	 * @param selectable the selectable
	 */
	public synchronized void addLassoable(ILassoable selectable){
		if (!dragSelectables.contains(selectable)){
			dragSelectables.add(selectable);
			if (selectable instanceof MTComponent) {
				MTComponent baseComp = (MTComponent) selectable;
				baseComp.addStateChangeListener(StateChange.COMPONENT_DESTROYED, new StateChangeListener(){
					public void stateChanged(StateChangeEvent evt) {
						if (evt.getSource() instanceof ILassoable) {
							ILassoable clusterAble = (ILassoable) evt.getSource();
							removeClusterable(clusterAble);
							//logger.debug("Removed comp from cluster gesture analyzers tracking");
						}
					}
				});

			}
		}
	}

	/**
	 * Adds the clusterable.
	 * 
	 * @param selectable the selectable
	 * @deprecated renamed, use addLassoable() instead.
	 */
	public synchronized void addClusterable(ILassoable selectable){
		addLassoable(selectable);
	}
	
	
	/**
	 * Removes the clusterable.
	 * 
	 * @param selectable the selectable
	 */
	public synchronized  void removeClusterable(ILassoable selectable){
		dragSelectables.remove(selectable);
	}
	
	/**
	 * Gets the tracked selectables.
	 * 
	 * @return the tracked selectables
	 */
	public ILassoable[] getTrackedSelectables(){
		return dragSelectables.toArray(new ILassoable[this.dragSelectables.size()]);
	}
	
	
	/**
	 * The Class ClusteringContext.
	 * 
	 * @author Besitzer
	 */
	private class ClusteringContext{
		
		/** The polygon. */
		private MTStencilPolygon polygon;
		
		/** The last position. */
		private Vector3D lastPosition;
		
		/** The new position. */
		private Vector3D newPosition;
		
		/** The cursor. */
		private InputCursor cursor;
		
		/** The selected comps. */
		private ArrayList<ILassoable> selectedComps;
		
		/** The gesture aborted. */
		protected boolean gestureAborted;

		
		/**
		 * Instantiates a new clustering context.
		 * 
		 * @param cursor the cursor
		 */
		public ClusteringContext(InputCursor cursor) {
			gestureAborted = false;
			this.cursor = cursor;
			
			Vector3D newPos = ToolsGeometry.getRayPlaneIntersection(
					Tools3D.getCameraPickRay(pa, camera, cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY()), 
					planeNormal, 
					pointInPlane);
			
			if (newPos == null){
				logger.error(getName() + " intersection with plane was null in class: " + this.getClass().getName());
				gestureAborted = true;
				abortGesture(cursor);
				return;
			}
			
			this.newPosition = newPos;
			this.lastPosition = newPos;
			
//			polygon = new MTPolygon(
//					new Vertex[]{
//							new Vertex(newPos.getX(), newPos.getY(), newPos.getZ()),
//							new Vertex(newPos.getX()+0.1f, newPos.getY(), newPos.getZ()),
//							new Vertex(newPos.getX(), newPos.getY()+0.1f, newPos.getZ()),
//							new Vertex(newPos.getX(), newPos.getY(), newPos.getZ())},
//					pa);
			polygon = new MTStencilPolygon(
					pa,
					new Vertex[]{
							new Vertex(newPos.getX(), newPos.getY(), newPos.getZ()),
							new Vertex(newPos.getX()+0.1f, newPos.getY(), newPos.getZ()),
							new Vertex(newPos.getX(), newPos.getY()+0.1f, newPos.getZ()),
							new Vertex(newPos.getX(), newPos.getY(), newPos.getZ())});
			polygon.setPickable(true);
			polygon.setNoStroke(false);
			polygon.setNoFill(false);
			polygon.setFillColor(new MTColor(100, 150, 250, 55));
//			polygon.setStrokeColor(150,150,250,255);
			polygon.setStrokeColor(new MTColor(0,0,0,255));
			polygon.setStrokeWeight(1.5f);
			polygon.setDrawSmooth(true);
			polygon.setUseDirectGL(true);
			polygon.setLineStipple((short)0xBBBB);
			polygon.setName("SelectPoly");
			
			polygon.setGestureAllowance(RotateProcessor.class, false);
			polygon.setGestureAllowance(ScaleProcessor.class, false);
			polygon.setGestureAllowance(TapProcessor.class, false);
			
			polygon.setGestureAllowance(DragProcessor.class, false);
			
			polygon.setBoundsAutoCompute(false);
			polygon.setBoundsBehaviour(AbstractShape.BOUNDS_DONT_USE);
			
//			polygon.setComposite(true);
			selectedComps = new ArrayList<ILassoable>();
		}
		
		/**
		 * Gets the selected comps.
		 * 
		 * @return the selected comps
		 */
		public ILassoable[] getselectedComps() {
			selectedComps.clear();
            for (ILassoable currentCard : dragSelectables) {
                Vector3D globCenter = new Vector3D(currentCard.getCenterPointGlobal());
                globCenter.setZ(0);
//				if (this.getPolygon().containsPointGlobal(currentCard.getCenterPointGlobal())){
                if (this.getPolygon().containsPointGlobal(globCenter)) {
                    selectedComps.add(currentCard);
                }
            }
			return selectedComps.toArray(new ILassoable[this.selectedComps.size()]);
		}

		
		/**
		 * Update.
		 * 
		 * @param cursor the cursor
		 */
		public void update(InputCursor cursor){
			if (!gestureAborted){
				lastPosition = newPosition;

//				pa.pushMatrix();
//				camera.update();
//				//Unproject the coords again taking the changed camera into account
//				this.newPosition = Tools3D.unprojectScreenCoords(pa, cursor.getLastEvent().getPositionX(), cursor.getLastEvent().getPositionY());			
//				pa.popMatrix();

//				this.newPosition = Tools3D.unprojectScreenCoords(pa, camera, cursor.getCurrentEvent().getX(), cursor.getCurrentEvent().getY());
//				Vector3D rayStartPoint = camera.getPosition(); //default cam
//				Vector3D newPos = ToolsGeometry.getRayPlaneIntersection(new Ray(rayStartPoint, newPosition), planeNormal, pointInPlane);
//				newPosition = newPos;
				
				newPosition = ToolsGeometry.getRayPlaneIntersection(
						Tools3D.getCameraPickRay(pa, camera, cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY()), 
						planeNormal, 
						pointInPlane);
				

				if (newPosition != null && !lastPosition.equalsVector(newPosition) && this.getPolygon().getVertexCount() < verticesLimit){
					if (minDistance == 0 ){
						addNewPoint(newPosition);
					}else{
						Vertex[] verts = this.getPolygon().getVerticesLocal();
						if (verts.length > 1){
							Vertex lastVert = new Vertex(verts[verts.length-2]);
							lastVert.transform(getPolygon().getGlobalMatrix());
							float distance = lastVert.distance2D(newPosition);
							if (distance > minDistance){
								addNewPoint(newPosition);	
							}
						}else{
							addNewPoint(newPosition);
						}
					}
				}
			}
		}
	
		private void addNewPoint(Vector3D newPosition){
			Vertex[] newArr = new Vertex[this.getPolygon().getVertexCount()+1];

			Vertex[] polyVertices = this.getPolygon().getVerticesGlobal();

			//set the old last point to the next index
			System.arraycopy(polyVertices, 0, newArr, 0, this.getPolygon().getVertexCount());
			newArr[newArr.length-1] = polyVertices[0]; //close poly correctly

			//Create the new vertex
			Vertex newVert = new Vertex(newPosition.getX(), newPosition.getY(), newPosition.getZ(), 100,150,250,255);
			newVert.setA(120);
			newArr[newArr.length-2] = newVert; //set the new value to be the length-2 one

			polygon.setVertices(newArr);
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
		 * Gets the cursor.
		 * 
		 * @return the cursor
		 */
		public InputCursor getCursor() {
			return cursor;
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
		 * Gets the polygon.
		 * 
		 * @return the polygon
		 */
		public MTPolygon getPolygon() {
			return polygon;
		}
		
	}
	
	

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor#getName()
	 */
	@Override
	public String getName() {
		return "Lasso";
	}



	
}
