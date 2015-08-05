package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew;

import org.mt4j.input.MTEvent;

public interface ISelectionManager {
	
	public void addSelectionListener(ISelectionListener listener);
	
	public void removeSelectionListener(ISelectionListener listener);
	
	public void fireEvent(MTEvent event);
	
}
