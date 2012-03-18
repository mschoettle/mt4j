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
package org.mt4j.components.visibleComponents.widgets;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTController;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * The Class MTList. A list component to add MTListCell objects to.
 * The layout will be done automatically. The list should be (created) vertically but
 * by rotating it, it can also be used horizontally.
 * 
 * @author Christopher Ruff
 */
public class MTList extends MTClipRectangle {
	
	private float width;
	private float height;
	
	private float preferredCellWidth;
	private float preferredCellHeight;
	
	private MTListCellContainer listCellContainer;
	
	private float cellYPadding;
	
	//TODO dont paint listcells that are clipped entirely
	//TODO horizontal/vertical list
	//TODO padding, border between list cells etc
	//TODO deal with setWIdth/Height preferredWIdth etc changes
	//FIXME this is stupid, we got tapID and getID...
	//TODO getSelected, select()
	
	//TODO scrollbar in list and/or other indicator that we can scroll in a direction
	
	/**
	 * Instantiates a new mT list.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param applet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTList(float x, float y, float width, float height,  PApplet applet) {
		this(applet, x, y, width, height);
	}
	
	/**
	 * Instantiates a new mT list.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param cellPaddingY the cell padding y
	 * @param applet the applet
	 * @deprecated constructor will be deleted! Please use the constructor with the PApplet instance as the first parameter.
	 */
	public MTList(float x, float y, float width, float height, float cellPaddingY, PApplet applet) {
		this(applet, x, y, width, height, cellPaddingY);
	}
	
	/**
	 * Instantiates a new mT list.
	 *
	 * @param applet the applet
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public MTList(PApplet applet, float x, float y, float width,  float height) {
		this(applet, x, y, width, height, 2);
	}
	
	/**
	 * Instantiates a new mT list.
	 *
	 * @param applet the applet
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param cellPaddingY the cell padding y
	 */
	public MTList(PApplet applet, float x, float y, float width, float height, float cellPaddingY) {
		super(applet, x, y, 0, width, height);
		
		this.width = width;
		this.height = height;
		
		this.preferredCellWidth = this.width;
		this.preferredCellHeight = 50;
		
		this.cellYPadding = cellPaddingY;
		
		this.listCellContainer = new MTListCellContainer(x,y,1,1, applet);
		this.addChild(listCellContainer);
		
		//So we can drag the cell container when dragging on the list 
		this.registerInputProcessor(new DragProcessor(applet));
		this.removeAllGestureEventListeners(DragProcessor.class);
		this.addGestureListener(DragProcessor.class, new ListCellDragListener(listCellContainer));
		
	}
	
	@Override
	protected void setDefaultGestureActions() {
		//No gestures
	}
	
	
	@Override
	public void addChild(MTComponent tangibleComp) {
		this.addChild(this.getChildCount(), tangibleComp);
	}

		
	@Override
	public void addChild(int i, MTComponent tangibleComp) {
		if (tangibleComp instanceof MTListCell) {
			MTListCell cell = (MTListCell) tangibleComp;
			this.addListElement(cell);
		}else{
			super.addChild(i, tangibleComp);
		}
	}
	
	
	/**
	 * Adds a list element.
	 * 
	 * @param item the item
	 */
	public void addListElement(MTListCell item){
		this.listCellContainer.addCell(listCellContainer.cells.size(), item);
	}
	
	/**
	 * Adds a list element.
	 * 
	 * @param index the index
	 * @param item the item
	 */
	public void addListElement(int index, MTListCell item){
		this.listCellContainer.addCell(index, item);
	}
	
	/**
	 * Removes a list element.
	 * 
	 * @param item the item
	 */
	public void removeListElement(MTListCell item){
		this.listCellContainer.removeCell(item);
	}
	
	/**
	 * Removes the all list elements.
	 */
	public void removeAllListElements(){
		MTComponent[] children = this.listCellContainer.getChildren();
		for (MTComponent child : children) {
			if (child instanceof MTListCell) {
				MTListCell cell = (MTListCell) child;
				this.listCellContainer.removeCell(cell);
			}
		}
	}
	
	
	private Vector3D getListUpperLeftLocal(){
		PositionAnchor savedAnchor = this.getAnchor();
		this.setAnchor(PositionAnchor.UPPER_LEFT);
		Vector3D pos = this.getPosition(TransformSpace.LOCAL);
		this.setAnchor(savedAnchor);
		return pos;
	}
	
	private Vector3D getListLowerLeftLocal(){
		PositionAnchor savedAnchor = this.getAnchor();
		this.setAnchor(PositionAnchor.LOWER_LEFT);
		Vector3D pos = this.getPosition(TransformSpace.LOCAL);
		this.setAnchor(savedAnchor);
		return pos;
	}
	
	
	private Vector3D getContainerUpperLeftRelParent(){
		PositionAnchor saved = listCellContainer.getAnchor();
		listCellContainer.setAnchor(PositionAnchor.UPPER_LEFT);
		Vector3D returnPos = listCellContainer.getPosition(TransformSpace.RELATIVE_TO_PARENT);
		listCellContainer.setAnchor(saved);
		return returnPos;
	}
	
	private Vector3D getContainerLowerLeftRelParent(){
		PositionAnchor saved = listCellContainer.getAnchor();
		listCellContainer.setAnchor(PositionAnchor.LOWER_LEFT);
		Vector3D returnPos = listCellContainer.getPosition(TransformSpace.RELATIVE_TO_PARENT);
		listCellContainer.setAnchor(saved);
		return returnPos;
	}
	
	
	private class CellDestroyedListener implements StateChangeListener{
		private MTListCell cell;
		
		public CellDestroyedListener(MTListCell cell){
			this.cell = cell;
		}
		
		@Override
		public void stateChanged(StateChangeEvent evt) {
			if (evt.getState().equals(StateChange.COMPONENT_DESTROYED)){
				removeListElement(cell);
			}
		}
	}

	/**
	 * The Class MTListCellContainer. Container for all the MTListCell's.
	 * 
	 * @author Christopher Ruff
	 */
	private class MTListCellContainer extends MTRectangle{
		private PApplet app;
		private List<MTListCell> cells;
		private List<MTListCell> selectedCells; //TODO!
		
		private boolean isDragging;
		

		public MTListCellContainer(float x, float y, float width, float height,	PApplet applet) {
			super(applet, x, y, width, height);
			this.app = applet;
			
			this.setNoFill(true); //ENABLE LATER!
			this.setNoStroke(true);
			this.setPickable(false);
			
			this.cells = new ArrayList<MTListCell>();
			this.selectedCells = new ArrayList<MTListCell>();
			
			isDragging = false;
		}
		
		
		public void addCell(int index, MTListCell item){
			if (cells.contains(item)){
				return;
			}
			
			//Add a statechangelistener (only once) to check when the cell is destroyed -> remove from list
			StateChangeListener[] stateListeners = item.getStateChangeListeners();
			boolean alreadyHasDestroyedListener = false;
			for (StateChangeListener stateChangeListener : stateListeners) {
				if (stateChangeListener instanceof CellDestroyedListener){
					alreadyHasDestroyedListener = true;
				}
			}
			if (!alreadyHasDestroyedListener){
				item.addStateChangeListener(StateChange.COMPONENT_DESTROYED, new CellDestroyedListener(item));	
			}
			
			
			this.addChild(index, item);
			this.cells.add(index, item);
			this.updateLayout();
			
			//Add drag listener which drags the cells parent (listcontainer) restriced to one axis 
			if (!hasDragProcessor(item)){
				item.registerInputProcessor(new DragProcessor(app));
			}
			
			//Remove the default drag listener from the cell for safety
			IGestureEventListener[] l = item.getGestureListeners();
            for (IGestureEventListener gestureEventListener : l) {
                if (gestureEventListener.getClass().equals(DefaultDragAction.class)) {
                    item.removeGestureEventListener(DragProcessor.class, gestureEventListener);
                }
            }
	    	
			item.addGestureListener(DragProcessor.class, new ListCellDragListener(this));
			
			//FIXME DEBUG REMOVE!
			/*
			//Add a tap processor and listener to the listcell to listen for tapps
			TapProcessor tap = new TapProcessor(app);
			tap.setMaxFingerUpDist(5);
			item.registerInputProcessor(tap);
			item.addGestureListener(TapProcessor.class, new IGestureEventListener() {
				public boolean processGestureEvent(MTGestureEvent ge) {
					TapEvent te = (TapEvent)ge;
					switch (te.getTapID()) { 
					case TapEvent.BUTTON_DOWN:
						break;
					case TapEvent.BUTTON_UP:
						break;
					case TapEvent.BUTTON_CLICKED:
						System.out.println("Clicked ListCell Item: " + te.getTargetComponent());
						break;						
					default:
						break;
					}
					return false;
				}
			});
			*/
		}
		
		private boolean hasDragProcessor(MTComponent comp){
			AbstractComponentProcessor[] ps = comp.getInputProcessors();
            for (AbstractComponentProcessor p : ps) {
                if (p instanceof DragProcessor) {
                    return true;
                }
            }
			return false;
		}
		
		public void removeCell(MTListCell item){
			if (this.containsDirectChild(item)){
				this.removeChild(item);
			}
			
			if (cells.contains(item)){
				this.cells.remove(item);	
				this.updateLayout();
			}
		}
		
		
		public void updateLayout(){
			//Extend/Shrink listCellContainer
			this.setWidthLocal(this.calcAllCellsWidth());
			this.setHeightLocal(this.calcAllCellsHeight());

			//Re-position list-container to upper left of MTList
			this.setAnchor(PositionAnchor.UPPER_LEFT);
			Vector3D listContainerUpperLeftLocal = getListUpperLeftLocal();
			this.setPositionRelativeToParent(listContainerUpperLeftLocal);
			
			//Set the Position of the list cells in listCellContainer (cells are aligned to the left side of the container)
			float offset = 0;
			if (this.cells.size() > 0 && !this.cells.get(0).isNoStroke()){//FIXME TEST so that stroke isnt cut off TOP because of clipping
				offset = this.cells.get(0).getStrokeWeight();
			}
            for (MTListCell cell : this.cells) {
                cell.setAnchor(PositionAnchor.UPPER_LEFT);
//				Vector3D pos = new Vector3D(0, offset, 0);
                //FIXME TEST so that stroke isnt cut off left because of clipping
                Vector3D pos = new Vector3D(listContainerUpperLeftLocal.x + cell.getStrokeWeight(), listContainerUpperLeftLocal.y + offset, 0);
                cell.setPositionRelativeToParent(pos);
                offset += cellYPadding + cell.getHeightXY(TransformSpace.RELATIVE_TO_PARENT); //TODO take strokeweight into account here,too
            }
		}
		
		/**
		 * Adds up the heights of all cells.
		 * Calc all cells height.
		 * 
		 * @return the float
		 */
		private float calcAllCellsHeight() {
			float neededHeight = 0;
			for (int i = 0; i < cells.size(); i++) {
				MTListCell cell = cells.get(i);
//				neededHeight += preferredCellHeight + cellYPadding; 
				if (i == cells.size()-1){ //because we dont need padding at the end of the list
					neededHeight += cell.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
				}else{
					neededHeight += cellYPadding + cell.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);	
				}
			}
//			neededHeight -= cellYPadding; //because we dont need padding at the end of the list
			return neededHeight;
		}

		
		private float calcAllCellsWidth() {
			float biggest = Float.MIN_VALUE;
            for (MTListCell cell : this.cells) {
                float cellWidth = cell.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
                if (cellWidth >= biggest) {
                    biggest = cellWidth;
                }
            }
			return biggest;
//			return preferredCellWidth;
		}

		
		public synchronized boolean isDragging() {
			return isDragging;
		}
		
		/**
		 * Restrict dragging to only 1 listcell at a time.
		 * @param isDragging
		 */
		public synchronized void setDragging(boolean isDragging) {
			this.isDragging = isDragging;
		}

	}

	/**
	 * The Class ListCellDragListener.
	 * Drag gestue listener which restricts dragging/scrolling of the cell container to its y-axis.
	 * Also stops if the list end is reached.
	 * 
	 * @author Christopher Ruff
	 */
	private class ListCellDragListener implements IGestureEventListener{
		private MTListCellContainer theListCellContainer;
		
		private boolean canDrag;
		
		public ListCellDragListener(MTListCellContainer cont){
			this.theListCellContainer = cont;
			this.canDrag = false;
		}
		
		public boolean processGestureEvent(MTGestureEvent ge) {
			DragEvent de = (DragEvent)ge;
			
			//If all cells fit into the list without scrolling dont do it.
			if (theListCellContainer.getHeightXY(TransformSpace.RELATIVE_TO_PARENT) <= getHeightXY(TransformSpace.LOCAL)){
				if (canDrag && theListCellContainer.isDragging()){
					theListCellContainer.setDragging(false);
					canDrag = false;
				}
				return false;
			}
			
			// Restrict dragging to only 1 listcell at a time
			if (!theListCellContainer.isDragging()){
//				System.out.println("Dragging list with cursor: " + de.getDragCursor().getId());
				theListCellContainer.setDragging(true);
				canDrag = true;
			}
			
			Vector3D dir = de.getTranslationVect();
			//Transform the global direction vector into listCellContainer local coordiante space
			dir.transformDirectionVector(theListCellContainer.getGlobalInverseMatrix());
			
			switch (de.getId()) {
			case MTGestureEvent.GESTURE_STARTED:
			case MTGestureEvent.GESTURE_UPDATED:
				//Constrain the movement of the listcellcontainer to the boundaries of the List
				if (canDrag){
//					if (dir.y > 0){
						theListCellContainer.translate(new Vector3D(0, dir.y), TransformSpace.LOCAL);	
						
						Vector3D listUpperLeftLocal = getListUpperLeftLocal();
						if (getContainerUpperLeftRelParent().y > listUpperLeftLocal.y){
							theListCellContainer.setAnchor(PositionAnchor.UPPER_LEFT);
							theListCellContainer.setPositionRelativeToParent(listUpperLeftLocal);
						}
//					}else if(dir.y < 0){
//						theListCellContainer.translate(new Vector3D(0, dir.y), TransformSpace.LOCAL);
						
						Vector3D listLowLeftLocal = getListLowerLeftLocal();
						if (getContainerLowerLeftRelParent().y < listLowLeftLocal.y){
							theListCellContainer.setAnchor(PositionAnchor.LOWER_LEFT);
							theListCellContainer.setPositionRelativeToParent(listLowLeftLocal);
						}
//					}
				}
				break;
			case MTGestureEvent.GESTURE_ENDED:
				if (canDrag){
					theListCellContainer.setDragging(false);
					
					Vector3D vel = de.getDragCursor().getVelocityVector(150);
					vel.scaleLocal(0.8f);
					vel = vel.getLimited(25);
					IMTController oldController = theListCellContainer.getController();
					theListCellContainer.setController(new InertiaListController(theListCellContainer, vel, oldController));
				}
				canDrag = false;
				break;
			default:
				break;
			}
			return false;
		}
		
		
		
		
		
		/**
		 * The Class InertiaListController.
		 * Controller to add an inertia scrolling after scrolling/dragging the list content.
		 * 
		 * @author Christopher Ruff
		 */
		private class InertiaListController implements IMTController{
			private MTComponent target;
			private Vector3D startVelocityVec;
			private float dampingValue = 0.95f;
//			private float dampingValue = 0.80f;
			
			private IMTController oldController;
			
			public InertiaListController(MTComponent target, Vector3D startVelocityVec, IMTController oldController) {
				super();
				this.target = target;
				this.startVelocityVec = startVelocityVec;
				this.oldController = oldController;
				
//				System.out.println(startVelocityVec);
				//Animation inertiaAnim = new Animation("Inertia anim for " + target, new MultiPurposeInterpolator(startVelocityVec.length(), 0, 100, 0.0f, 0.5f, 1), target);
			}
			
			public void update(long timeDelta) {
				if (theListCellContainer.isDragging()){
					startVelocityVec.setValues(Vector3D.ZERO_VECTOR);
					target.setController(oldController);
					return;
				}
				
				if (Math.abs(startVelocityVec.x) < 0.05f && Math.abs(startVelocityVec.y) < 0.05f){
					startVelocityVec.setValues(Vector3D.ZERO_VECTOR);
					target.setController(oldController);
					return;
				}
				startVelocityVec.scaleLocal(dampingValue);
				
				Vector3D transVect = new Vector3D(startVelocityVec);
				transVect.transformDirectionVector(listCellContainer.getGlobalInverseMatrix());
				
				theListCellContainer.translate(new Vector3D(0, transVect.y), TransformSpace.LOCAL);	
				
//				System.out.println("Vel vect: " + transVect);
				
				//Constrain the movement of the listcellcontainer to the boundaries of the List
				Vector3D listUpperLeftLocal = getListUpperLeftLocal();
				if (getContainerUpperLeftRelParent().y > listUpperLeftLocal.y){
					theListCellContainer.setAnchor(PositionAnchor.UPPER_LEFT);
					theListCellContainer.setPositionRelativeToParent(listUpperLeftLocal);
					//Bounce off list end					
					startVelocityVec.scaleLocal(-0.25f);
				}
				
				Vector3D listLowLeftLocal = getListLowerLeftLocal();
				if (getContainerLowerLeftRelParent().y < listLowLeftLocal.y){
					theListCellContainer.setAnchor(PositionAnchor.LOWER_LEFT);
					theListCellContainer.setPositionRelativeToParent(listLowLeftLocal);
					//Bounce off list end
					startVelocityVec.scaleLocal(-0.25f);
				}
				
				if (oldController != null){
					oldController.update(timeDelta);
				}
			}
		}
		
	}
	
	
	public void scrollY(float amount){
		listCellContainer.translate(new Vector3D(0, amount), TransformSpace.LOCAL);	
		Vector3D listUpperLeftLocal = getListUpperLeftLocal();
		if (getContainerUpperLeftRelParent().y > listUpperLeftLocal.y){
			listCellContainer.setAnchor(PositionAnchor.UPPER_LEFT);
			listCellContainer.setPositionRelativeToParent(listUpperLeftLocal);
		}
		Vector3D listLowLeftLocal = getListLowerLeftLocal();
		if (this.getContainerLowerLeftRelParent().y < listLowLeftLocal.y){
			listCellContainer.setAnchor(PositionAnchor.LOWER_LEFT);
			listCellContainer.setPositionRelativeToParent(listLowLeftLocal);
		}
	}
	
	
	
	
}
