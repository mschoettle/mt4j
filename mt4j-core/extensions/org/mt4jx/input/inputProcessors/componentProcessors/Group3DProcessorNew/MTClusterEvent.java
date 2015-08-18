package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew;

import org.mt4j.input.MTEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;

public class MTClusterEvent extends MTEvent {

	public static final int CLUSTER_CREATED = 1;
	
	public static final int CLUSTER_UPDATED = 2;
	
	public static final int CLUSTER_DELETED = 3;
	
	public static final int CLUSTER_SELECTED = 4;
	
	private Cluster3DExt cluster;
	
	private int id;
	
	public MTClusterEvent(Object source,int id,Cluster3DExt cluster) {
		super(source);
		this.cluster = cluster;
		this.id = id;
	}

	public void setCluster(Cluster3DExt cluster) {
		this.cluster = cluster;
	}

	public Cluster3DExt getCluster() {
		return cluster;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
