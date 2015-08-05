package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew;

import java.util.List;
import java.util.ArrayList;

import org.mt4j.input.IMTEventListener;
import org.mt4j.input.MTEvent;



public class ClusterHub implements ISelectionListener,IClusterEventListener {
	
	private List<IMTEventListener> eventListener;
	
	public ClusterHub()
	{
		this.eventListener = new ArrayList<IMTEventListener>();
	}
	
	public void addEventListener(IMTEventListener listener)
	{
		this.eventListener.add(listener);
	}
	
	public void removeEventListener(IMTEventListener listener)
	{
		this.eventListener.remove(listener);
	}
	
	public List<IMTEventListener> getListeners()
	{
		return eventListener;
	}
	
	public void processMTEvent(MTEvent event)
	{
		for(int i=0;i<eventListener.size();i++)
		{
			this.eventListener.get(i).processMTEvent(event);
		}
	}
}
