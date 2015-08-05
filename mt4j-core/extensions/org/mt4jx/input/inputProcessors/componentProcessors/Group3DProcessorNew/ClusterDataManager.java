package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew;

import java.util.ArrayList;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.input.MTEvent;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.gestureAction.Rotate3DAction;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Rotate3DProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4jx.input.gestureAction.CreateDragHelperAction;
import org.mt4jx.util.extension3D.collision.CollisionManager;

import processing.core.PApplet;

public class ClusterDataManager implements ISelectionListener {

	ArrayList<IClusterEventListener> listeners;
		
	CollisionManager collisionManager;
	
	/** The cluster objects. */
	private ArrayList<Cluster3DExt> clusters;
	
	private PApplet pApplet;
	
	private MTCanvas canvas;
	
	public ClusterDataManager(PApplet pApplet,MTCanvas canvas,CollisionManager collisionManager){
		this.pApplet = pApplet;
		this.canvas = canvas;
		this.listeners = new ArrayList<IClusterEventListener>();
		this.collisionManager = collisionManager;
		setClusters(new ArrayList<Cluster3DExt>());
	}
	
	public void addClusterEventListener(IClusterEventListener listener)
	{
		if(!(listeners.contains(listener)))
		{
			listeners.add(listener);
		}
	}
	
	public void removeClusterEventListener(IClusterEventListener listener)
	{
		if(listeners.contains(listener))
		{
			listeners.remove(listener);
		}
	}
	
	public Cluster3DExt createCluster(ArrayList<MTComponent> elementsToCluster,boolean fireEvent)
	{
		
		for(MTComponent comp : elementsToCluster)
		{
			comp.attachCamera(canvas.getAttachedCamera());
			canvas.removeChild(comp);			
		}	
		
		Cluster3DExt cl = new Cluster3DExt(pApplet,elementsToCluster);
		cl.registerInputProcessor(new DragProcessor(pApplet));
		cl.addGestureListener(DragProcessor.class, new DefaultDragAction());
		
		cl.registerInputProcessor(new RotateProcessor(pApplet));
		cl.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
		
		cl.registerInputProcessor(new Rotate3DProcessor(pApplet,cl));
		cl.addGestureListener(Rotate3DProcessor.class,new Rotate3DAction(pApplet,cl));
		
		cl.registerInputProcessor(new ScaleProcessor(pApplet));
		cl.addGestureListener(ScaleProcessor.class,  new DefaultScaleAction());
		
		//cl.registerInputProcessor(new TapProcessor(pApplet,999999.0f));
		cl.addGestureListener(DragProcessor.class,new CreateDragHelperAction((AbstractMTApplication)pApplet,this.canvas,this.canvas.getAttachedCamera(),cl));
				
		cl.attachCamera(canvas.getAttachedCamera());
		cl.setComposite(true);
		
		cl.setName("cluster");
		
		canvas.addChild(cl);
				
		if(fireEvent)
		{
			fireClusterEvent(new MTClusterEvent(this,MTClusterEvent.CLUSTER_CREATED,cl));
		}
		this.clusters.add(cl);
	
		return cl;		
	}
	
	public void deleteCluster(Cluster3DExt cluster)
	{
		if(this.containsCluster(cluster))
		{
			this.clusters.remove(cluster);
			fireClusterEvent(new MTClusterEvent(this,MTClusterEvent.CLUSTER_DELETED,cluster));
		}
	}
	
	public void processMTEvent(MTEvent mtEvent) {
		if(mtEvent instanceof MTSelectionEvent)
		{
			MTSelectionEvent selEvent = (MTSelectionEvent)mtEvent;
			switch(selEvent.getId())
			{
				case MTSelectionEvent.SELECTION_ENDED:
				{
					/*ArrayList<MTComponent> components = new ArrayList<MTComponent>();
					for(IdragClusterable elem : selEvent.getSelectedComps())
					{
						if(elem instanceof MTComponent)
						{
							MTComponent comp = (MTComponent)elem;
							components.add(comp);
						}
					}*/
					//createCluster(components,true);
					break;
				}
				case MTSelectionEvent.SELECTION_UPDATED:
				{
					break;
				}
			}
		}
		
	}
	
	public void removeComponentFromCluster(MTComponent component,Cluster3DExt cluster)
	{
		if(cluster.containsChild(component))
		{
			cluster.removeChild(component);
			if(cluster.getChildren().length>1)
			{
				fireClusterEvent(new MTClusterEvent(this,MTClusterEvent.CLUSTER_UPDATED,cluster));
			}else
			{
				this.deleteCluster(cluster);					
			}				
		}
		
	}
	
	public void addComponentToCluster(MTComponent component,Cluster3DExt cluster)
	{
		if(!(cluster.containsChild(component)))
		{
			canvas.removeChild(component);
			cluster.addChild(component);	
			fireClusterEvent(new MTClusterEvent(this,MTClusterEvent.CLUSTER_UPDATED,cluster));
		}
	}
	
	public Cluster3DExt getClusterForComponent(MTComponent component)
	{
		for(Cluster3DExt cluster : clusters)
		{
			if(cluster.containsChild(component))
			{
				return cluster;
			}	
		}
		return null;		
	}
	
	/**
	 * Contains cluster.
	 * 
	 * @param selection the selection
	 * 
	 * @return true, if successful
	 */
	public boolean containsCluster(Cluster3DExt selection){
		return (clusters.contains(selection));
	}
	
	public void fireClusterEvent(MTClusterEvent mtClusterEvent)
	{
		for(int i=0;i<listeners.size();i++)
		{
			listeners.get(i).processMTEvent(mtClusterEvent);
		}
	}

	public void setClusters(ArrayList<Cluster3DExt> clusters) {
		this.clusters = clusters;
	}

	public ArrayList<Cluster3DExt> getClusters() {
		return clusters;
	}
	
}
