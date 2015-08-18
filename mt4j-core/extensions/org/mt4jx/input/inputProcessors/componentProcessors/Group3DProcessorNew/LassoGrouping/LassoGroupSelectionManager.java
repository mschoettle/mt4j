package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.LassoGrouping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.input.MTEvent;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ClusterDataManager;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ISelection;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ISelectionListener;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ISelectionManager;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.MTLassoSelectionEvent;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.MTSelectionEvent;

public class LassoGroupSelectionManager extends AbstractCursorProcessor implements ISelectionManager {

	private List<ISelectionListener> selectionListeners;
	
	private HashMap<InputCursor,ISelection> cursorToSelection = new HashMap<InputCursor,ISelection>();
	
	/** The canvas. */
	private MTCanvas canvas;

	/** The drag selectables. */
	private List<MTComponent> dragSelectables = new ArrayList<MTComponent>();
	
	private ClusterDataManager clusterManager;
		
	/**
	 * the selection which is used for the cursors, for every cursor one selection is copied
	 */
	private LassoSelection selection;
		
	public LassoGroupSelectionManager(MTCanvas canvas,ClusterDataManager clusterManager)
	{
		this.selectionListeners = new ArrayList<ISelectionListener>();
		this.canvas = canvas;
		this.clusterManager = clusterManager;
		this.selection = new LassoSelection(canvas.getRenderer(),canvas.getAttachedCamera(),this);
	}
	
	public void addSelectionListener(ISelectionListener listener)
	{
		if(!(selectionListeners.contains(listener)))
		{
			this.selectionListeners.add(listener);
		}
	}
	
	public void removeSelectionListener(ISelectionListener listener)
	{
		if(selectionListeners.contains(listener))
		{
			this.selectionListeners.remove(listener);
		}
	}
	
	public void newInputCursor(InputCursor cursor)
	{	
		if(!cursorToSelection.containsKey(cursor))
		{
			LassoSelection sel = (LassoSelection)selection.getCopy();
			cursorToSelection.put(cursor, sel);
		}
	}
	
	public ISelection getSelection(InputCursor inputCursor)
	{
		if(cursorToSelection.containsKey(inputCursor))
		{
			return cursorToSelection.get(inputCursor);
		}
		return null;
	}
	
	public void removeInputCursor(InputCursor inputCursor)
	{
		if(cursorToSelection.containsKey(inputCursor))
		{
			cursorToSelection.remove(inputCursor);
		}
	}
	
	@Override
	public void cursorEnded(InputCursor inputCursor,
			AbstractCursorInputEvt currentEvent) {
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - MOTION: " + inputCursor.getId());
		
		LassoSelection sel = (LassoSelection)this.getSelection(inputCursor);
		this.removeInputCursor(inputCursor);
		ArrayList<MTComponent> components = new ArrayList<MTComponent>();
		if(sel.getSelectedComponents().size()>1)
		{
			for(MTComponent elem : sel.getSelectedComponents())
			{
				
					MTComponent comp = (MTComponent)elem;
					Cluster3DExt formerCluster = null;
					if((formerCluster=clusterManager.getClusterForComponent(comp))!=null)
					{
						clusterManager.removeComponentFromCluster(comp, formerCluster);
						//send the updated Selected Comps to the visualizer
						ArrayList<MTComponent> newSelectedComps = new ArrayList<MTComponent>();
						for(MTComponent formerComp : formerCluster.getChildren())
						{
							newSelectedComps.add(formerComp);							
						}						
						//this.fireSelectionEvent(new MTLassoSelectionEvent(this,MTLassoSelectionEvent.SELECTION_UPDATED,newSelectedComps,null,formerCluster));
					}
					components.add(comp);
				
			}		
			Cluster3DExt cluster = clusterManager.createCluster(components,true);
			this.canvas.removeChild(sel.getPolygon());			
			this.fireEvent(new MTLassoSelectionEvent(this,MTLassoSelectionEvent.SELECTION_ENDED,sel.getSelectedComponents(),sel.getPolygon(),cluster));			
		}else if(sel.getSelectedComponents().size()==1)
		{
			
			Cluster3DExt formerCluster = null;
			if((formerCluster=clusterManager.getClusterForComponent((MTComponent)sel.getSelectedComponents().get(0)))!=null)
			{
				clusterManager.removeComponentFromCluster((MTComponent)sel.getSelectedComponents().get(0), formerCluster);
				this.canvas.addChild(sel.getSelectedComponents().get(0));
			}
			this.canvas.removeChild(sel.getPolygon());
		}else
		{
			this.canvas.removeChild(sel.getPolygon());
		}
			
		this.unLock(inputCursor);		
	}

	@Override
	public void cursorLocked(InputCursor cursor,
			IInputProcessor lockingprocessor) {
		if (lockingprocessor instanceof AbstractComponentProcessor){
			logger.debug(this.getName() + " Recieved MOTION LOCKED by (" + ((AbstractComponentProcessor)lockingprocessor).getName()  + ") - cursor ID: " + cursor.getId());
		}else{
			logger.debug(this.getName() + " Recieved MOTION LOCKED by higher priority signal - cursor ID: " + cursor.getId());
		}
		this.abortGesture(cursor);
		
	}

	@Override
	public void cursorStarted(InputCursor inputCursor,
			AbstractCursorInputEvt currentEvent) {		
		if (this.canLock(inputCursor)){
			newInputCursor(inputCursor);
			
			LassoSelection sel = (LassoSelection)getSelection(inputCursor);
			sel.startSelection(inputCursor);
			//sel.getPolygon().attachCamera(canvas.getAttachedCamera());
			canvas.addChild(sel.getPolygon());
			this.fireEvent(new MTSelectionEvent(this,MTSelectionEvent.SELECTION_STARTED,sel.getSelectedComponents()));				
		}
		
	}

	@Override
	public void cursorUnlocked(InputCursor cursor) {
		logger.debug(this.getName() + " Recieved UNLOCKED signal for cursor ID: " + cursor.getId());
		//Do nothing here, we dont want this gesture to be resumable	
	}

	@Override
	public void cursorUpdated(InputCursor inputCursor,
			AbstractCursorInputEvt currentEvent) {
		LassoSelection sel = (LassoSelection)getSelection(inputCursor);
		sel.updateCursorInput(inputCursor);		
	}
	
	/**g 
	 * Adds the clusterable.
	 * 
	 * @param selectable the selectable
	 */
	public synchronized void addClusterable(MTComponent selectable){
		getDragSelectables().add(selectable);		
		if (selectable instanceof MTComponent) {
			MTComponent baseComp = (MTComponent) selectable;
			
			baseComp.addStateChangeListener(StateChange.COMPONENT_DESTROYED, new StateChangeListener(){
				public void stateChanged(StateChangeEvent evt) {
					if (evt.getSource() instanceof MTComponent) {
						MTComponent clusterAble = (MTComponent) evt.getSource();
						removeClusterable(clusterAble);
						//logger.debug("Removed comp from clustergesture analyzers tracking");
					}
				}
			});		
		}
	}
	
	/**
	 * Removes the clusterable.
	 * 
	 * @param selectable the selectable
	 */
	public synchronized  void removeClusterable(MTComponent selectable){
		getDragSelectables().remove(selectable);
	}
	
	/**
	 * Abort gesture.
	 * 
	 * @param m the involved cursor
	 */
	public void abortGesture(InputCursor m){
		//because of aborting we send an empty selectionarrray 
		ArrayList<MTComponent> selectedComps = new ArrayList<MTComponent>();
		LassoSelection sel = (LassoSelection)getSelection(m);
		
		this.fireEvent(new MTLassoSelectionEvent(this,MTSelectionEvent.SELECTION_ENDED,selectedComps,sel.getPolygon(),null));		
		removeInputCursor(m);
		logger.debug(this.getName() + " cursor:" + m.getId() + " MOTION LOCKED. Was an active cursor in this gesture!");
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void fireEvent(MTEvent event) {
		for(int i=0;i<selectionListeners.size();i++)
		{
			selectionListeners.get(i).processMTEvent(event);
		}		
	}

	public void setDragSelectables(List<MTComponent> dragSelectables) {
		this.dragSelectables = dragSelectables;
	}

	public List<MTComponent> getDragSelectables() {
		return dragSelectables;
	}

}
