package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.FingerTapGrouping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.PickResult;
import org.mt4j.components.PickResult.PickEntry;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.MTEvent;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.ILassoable;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ClusterDataManager;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ISelectionListener;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ISelectionManager;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.MTClusterEvent;
import org.mt4jx.util.extension3D.ComponentHelper;

public class FingerTapSelectionManager extends AbstractGlobalInputProcessor implements ISelectionManager {

	protected static final ILogger logger = MTLoggerFactory.getLogger(FingerTapSelectionManager.class.getName());
	
	private FingerTapSelection selection;
	
	/** The drag selectables. */
	private List<MTComponent> dragSelectables = new ArrayList<MTComponent>();
	
	/** all objects which are registered here for listening to selection events*/
	private List<ISelectionListener> selectionListeners;
	
	/** the canvas object*/
	private MTCanvas canvas;
	
	/** if one cursor is locked all other cursors will be put in unUsedCursorPerObject**/
	private HashMap<MTComponent,ArrayList<InputCursor>> unUsedCursorsPerObject = new  HashMap<MTComponent,ArrayList<InputCursor>>();
	
	/** for every component one cursor is locked for a tap **/
	private HashMap<MTComponent,InputCursor> lockedCursorsPerObject = new  HashMap<MTComponent,InputCursor>();
	
	/**for every component a cursor state is held **/
	private HashMap<MTComponent,FingerTapCursorState> objectCursorState = new HashMap<MTComponent,FingerTapCursorState>();
	

	public FingerTapSelectionManager(ClusterDataManager clusterDataManager,MTCanvas canvas)
	{
		this.selectionListeners = new ArrayList<ISelectionListener>();
		this.selection = new FingerTapSelection(clusterDataManager,canvas,this);
		this.canvas = canvas;
		logger.setLevel(ILogger.ERROR);
	}
	
	
	public void addSelectionListener(ISelectionListener listener) {
		if(!(selectionListeners.contains(listener)))
		{
			this.selectionListeners.add(listener);
		}
	}

	public void fireEvent(MTEvent event) {
		for(int i=0;i<selectionListeners.size();i++)
		{
			selectionListeners.get(i).processMTEvent(event);
		}	
	}

	public void removeSelectionListener(ISelectionListener listener) {
		if(selectionListeners.contains(listener))
		{
			this.selectionListeners.remove(listener);
		}		
	}
	
	/** 
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
					if (evt.getSource() instanceof ILassoable) {
						ILassoable clusterAble = (ILassoable) evt.getSource();
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
	public synchronized  void removeClusterable(ILassoable selectable){
		getDragSelectables().remove(selectable);
	}

	@Override
	public void processInputEvtImpl(MTInputEvent inputEvent) {
		if (inputEvent instanceof AbstractCursorInputEvt) {
			AbstractCursorInputEvt cursorEvt = (AbstractCursorInputEvt)inputEvent;
			InputCursor c = ((AbstractCursorInputEvt)inputEvent).getCursor();
			
			IMTComponent3D comp  = inputEvent.getTarget();
						
			//if component is a cluster
			//get the children object of the cluster 
			//which has been picked, by returing the most upper object on the canvas
			if(comp instanceof Cluster3DExt)
			{
				
				//Cluster cl = (Cluster)comp;
				Vector3D currentPos = new Vector3D(cursorEvt.getX(),cursorEvt.getY(),0.0f);				
				//PickResult prCanvas = canvas.pick2(currentPos.getX(), currentPos.getY(), true);//ADDTOMT4J
				PickResult prCanvas = ComponentHelper.pick(canvas,currentPos.getX(), currentPos.getY(), true);//ADDTOMT4J
                List<PickEntry> plCanvas = prCanvas.getPickList();
                if(plCanvas.size()>0)
                {
                	comp = plCanvas.get(0).hitObj.getParent();
                }
                //PickEntry currentPickEntry = plCanvas.get(plCanvas.size()-1);
                //comp = currentPickEntry.hitObj;                
			}
			MTComponent mtComp = (MTComponent)comp;
			logger.debug("COMP IS FROM CLASS " + mtComp.getClass().getName());
			
			if(getDragSelectables().contains(comp))
			{				
				switch (cursorEvt.getId()) {
				case AbstractCursorInputEvt.INPUT_STARTED:
					//update cursor state
					logger.debug("INPUT_DETECTED FOR COMPONENT " + comp.getName()  + " cursor-id: " + c.getId());
					if(!objectCursorState.containsKey(comp))
					{
						logger.debug("While INPUT_DETECTED Component " + comp.getName() + " added to objectCursorState Map with OBJECTWITHNOTAP  cursor-id: " + c.getId());
						objectCursorState.put(mtComp, FingerTapCursorState.OBJECTWITHNOTAP);
					}
					objectCursorState.get(mtComp).tapPress(this,mtComp,c);
					
					//update selection state of complete selection
					if(!selection.getCurrentlyPressedCursors().contains(c))
					{				
						logger.debug("While INPUT_DETECTED Component " + comp.getName() + " added to currentlyPressedCursor cursor-id: " + c.getId());
						selection.getCurrentlyPressedCursors().add(c);
					
					}
					selection.getState().tapPress(selection,c,mtComp);
												
					break;
				case AbstractCursorInputEvt.INPUT_UPDATED:										
					break;
				case AbstractCursorInputEvt.INPUT_ENDED:
					logger.debug("INPUT_ENDED FOR COMPONENT " + comp.getName() + " cursor-id: " + c.getId());
					
					//update cursor state of object
					if(!objectCursorState.containsKey(comp))
					{
						logger.debug("While INPUT_ENDED Component " + comp.getName() + " added to objectCursorState Map with OBJECTWITHNOTAP  cursor-id: " + c.getId());
						objectCursorState.put(mtComp, FingerTapCursorState.OBJECTWITHNOTAP);
					}
					objectCursorState.get(mtComp).tapRelease(this,mtComp,c);
					//update selection state of complete selection
					if(selection.getCurrentlyPressedCursors().contains(c))
					{
						logger.debug("While INPUT_ENDED Component " + comp.getName() + " removed from currentlyPressedCursor cursor-id: " + c.getId());
						selection.getCurrentlyPressedCursors().remove(c);
						selection.getState().tapRelease(selection,c,mtComp);
						
					}
					
					break;
				default:
					break;
				}
					
			}else if(comp instanceof Cluster3DExt)
			{	
				//special behaviour in case of 3D Rotation
				//when fingerinput ended with fingers not intersecting 
				//with the cluster itself			
				selection.getCurrentlyPressedCursors().clear();
				selection.setState(FingerTapState.NOELEMENTSELECTED);
			}
		}
		
	}
	
	public void fireClusterSelectionEvent(MTClusterEvent event) {
		for(int i=0;i<selectionListeners.size();i++)
		{
			selectionListeners.get(i).processMTEvent(event);
		}		
	}

	public void setSelection(FingerTapSelection selection) {
		this.selection = selection;
	}

	public FingerTapSelection getSelection() {
		return selection;
	}

	public void setDragSelectables(List<MTComponent> dragSelectables) {
		this.dragSelectables = dragSelectables;
	}

	public List<MTComponent> getDragSelectables() {
		return dragSelectables;
	}
	
	public boolean lockedCursorsContainComponent(MTComponent comp)
	{
		if(lockedCursorsPerObject.containsKey(comp))
		{
			return true;
		}
		return false;
	}
		
	public InputCursor getLockedCursorForComponent(MTComponent comp)
	{
		if(lockedCursorsPerObject.containsKey(comp))
		{
			return lockedCursorsPerObject.get(comp);
		}		
		return null;
	}
	
	public ArrayList<InputCursor> getUnUsedCursorsForComponent(MTComponent comp)
	{
		if(unUsedCursorsPerObject.containsKey(comp))
		{
			return unUsedCursorsPerObject.get(comp);
		}else
		{
			unUsedCursorsPerObject.put(comp, new ArrayList<InputCursor>());
		}
		return null;
	}
	
	public boolean addUnUsedCursorsForComponent(MTComponent comp,InputCursor c)
	{
		if(unUsedCursorsPerObject.containsKey(comp))
		{
			if(unUsedCursorsPerObject.get(comp)!=null)
			{
				unUsedCursorsPerObject.get(comp).add(c);
				return true;
			}
		}else
		{
			ArrayList<InputCursor> unUsedCursors = new ArrayList<InputCursor>();
			unUsedCursors.add(c);
			unUsedCursorsPerObject.put(comp,unUsedCursors);
		}
		return false;
	}
	
	public boolean removeUnUsedCursorsForComponent(MTComponent comp,InputCursor c)
	{
		if(unUsedCursorsPerObject.containsKey(comp))
		{
			if(unUsedCursorsPerObject.get(comp)!=null)
			{
				unUsedCursorsPerObject.get(comp).remove(c);
				return true;
			}
		}
		return false;
	}
	
	public boolean unUsedCursorsContainComponent(MTComponent comp)
	{
		if(unUsedCursorsPerObject.containsKey(comp))
		{
			return true;
		}
		
		return false;
	}
	
	public void setLockedCursorForComponent(MTComponent comp,InputCursor c)
	{
		lockedCursorsPerObject.put(comp,c);		
	}
	
	public void setCursorStateForComponent(MTComponent comp,FingerTapCursorState state)
	{		
		objectCursorState.put(comp,state);		
	}
}
