package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.FingerTapGrouping;

import java.util.ArrayList;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.ILassoable;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ClusterDataManager;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ISelection;

public class FingerTapSelection implements ISelection {

	private ClusterDataManager clusterDataManager;
	
	private FingerTapSelectionManager selectionManager;
	
	private FingerTapState state = FingerTapState.NOELEMENTSELECTED;
	
	private InputCursor firstCursor;
	
	private MTComponent firstCursorComp;
	
	private ArrayList<InputCursor> currentlyPressedCursors = new ArrayList<InputCursor>();
	
	private ArrayList<MTComponent> selectedComps = new ArrayList<MTComponent>();
	
	private MTCanvas canvas;
	
	private Cluster3DExt currentlySelectedCluster;
	
	public FingerTapSelection(ClusterDataManager clusterDataManager,MTCanvas canvas,FingerTapSelectionManager selectionManager)
	{
		this.clusterDataManager = clusterDataManager;
		this.canvas = canvas;
		this.selectionManager = selectionManager;
	}
	public synchronized void addComponentToSelection(MTComponent comp)
	{
		if(!(selectedComps.contains(comp)))
		{
			selectedComps.add(comp);
		}
	}
	
	public boolean compIsInSelection(MTComponent comp)
	{
		return  selectedComps.contains(comp);
	}
	
	public synchronized void  removeCurrentlySelectedFromCanvas()
	{
		for(MTComponent comp : selectedComps)
		{
			this.canvas.removeChild(comp);
		}
	}
	
	public synchronized void removeComponentFromSelection(MTComponent comp)
	{
		if(selectedComps.contains(comp))
		{			
			selectedComps.remove(comp);			
		}		
	}
	
	public void addComponentToCanvas(MTComponent comp)
	{
		canvas.addChild(comp);
	}
	
	public void removeComponentFromCanvas(MTComponent comp)
	{
		canvas.removeChild(comp);
	}
	
	public ArrayList<MTComponent> getSelectedComponents() {
		return selectedComps;
	}
	
	public synchronized void setSelectedComponents(ArrayList<MTComponent> selectedComps)
	{
		this.selectedComps = selectedComps;
	}

	public synchronized void setState(FingerTapState state) {
		this.state = state;		
		this.state.stateEntry(this);
	}

	public FingerTapState getState() {
		return state;
	}

	public void setClusterDataManager(ClusterDataManager clusterDataManager) {
		this.clusterDataManager = clusterDataManager;
	}

	public ClusterDataManager getClusterDataManager() {
		return clusterDataManager;
	}

	public synchronized void setFirstCursor(InputCursor firstCursor) {
		this.firstCursor = firstCursor;		
	}

	public InputCursor getFirstCursor() {
		return firstCursor;
	}
	
	public boolean isFirstCursor(InputCursor cursor)
	{
		if(firstCursor==cursor)
		{
			return true;
		}
		return false;
	}

	public synchronized void setCurrentlyPressedCursors(ArrayList<InputCursor> currentlyPressedCursors) {
		this.currentlyPressedCursors = currentlyPressedCursors;
	}

	public  ArrayList<InputCursor> getCurrentlyPressedCursors() {
		return currentlyPressedCursors;
	}
	
	
	public void createCluster()
	{
		ArrayList<MTComponent> components = new ArrayList<MTComponent>();
		for(MTComponent comp : selectedComps)
		{
			
				MTComponent component = (MTComponent)comp;
				components.add(component);						
		}
		clusterDataManager.createCluster(components, true);
	}
	
	public void setCurrentlySelectedCluster(Cluster3DExt currentlySelectedCluster) {
		this.currentlySelectedCluster = currentlySelectedCluster;
	}
	public Cluster3DExt getCurrentlySelectedCluster() {
		return currentlySelectedCluster;
	}
	public void setSelectionManager(FingerTapSelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}
	public FingerTapSelectionManager getSelectionManager() {
		return selectionManager;
	}
	public void setFirstCursorComp(MTComponent firstCursorComp) {
		this.firstCursorComp = firstCursorComp;
	}
	public MTComponent getFirstCursorComp() {
		return firstCursorComp;
	}
	
	public MTComponent getComponentForCursor(InputCursor cursor)
	{
		return (MTComponent)canvas.getComponentAt(cursor.getStartPosX(), cursor.getStartPosY());
	}
	
	

}
