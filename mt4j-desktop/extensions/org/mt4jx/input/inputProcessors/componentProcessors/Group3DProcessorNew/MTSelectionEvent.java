package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew;

import java.util.ArrayList;

import org.mt4j.components.MTComponent;
import org.mt4j.input.MTEvent;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.ILassoable;

public class MTSelectionEvent extends MTEvent {

	public static final int SELECTION_STARTED = 1;
	
	public static final int SELECTION_ENDED = 2;
	
	public static final int SELECTION_UPDATED = 3;
	
	private int id;
	
	private ArrayList<MTComponent> selectedComps;
	
	public MTSelectionEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	public MTSelectionEvent(Object source,		
			int id, ArrayList<MTComponent> selectedComps) {
		super(source);	
		this.id = id;
		this.selectedComps = selectedComps;
	}

	public void setSelectedComps(ArrayList<MTComponent> selectedComps) {
		this.selectedComps = selectedComps;
	}

	public ArrayList<MTComponent> getSelectedComps() {
		return selectedComps;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
