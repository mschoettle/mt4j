package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.FingerTapGrouping;

import java.util.ArrayList;

import org.mt4j.components.MTComponent;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.MTClusterEvent;

public enum FingerTapState implements FingerTapTransitions,FingerTapStateMethods {
    		
	NOELEMENTSELECTED
	{

		public synchronized void tapPress(FingerTapSelection sel,InputCursor cursor,MTComponent comp) {
			this.stateExit(sel);
			//System.out.println("NO ELEMENT SELECTED");
			if(sel.getClusterDataManager().getClusterForComponent(comp)==null)
			{
				sel.setFirstCursor(cursor);
				sel.setFirstCursorComp(comp);
				sel.addComponentToSelection(comp);
				sel.setState(ONEELEMENTSELECTED);
			}else
			{	
				////System.out.println("NOELEMENTSELECTED to CLUSTERSELECTED CursorId " + cursor.getId() + " compname " + comp.getName());
				sel.setFirstCursor(cursor);
				sel.setFirstCursorComp(comp);
				sel.setCurrentlySelectedCluster(sel.getClusterDataManager().getClusterForComponent(comp));
				sel.setState(CLUSTERSELECTED);
			}
		} 

		public synchronized void tapRelease(FingerTapSelection sel,InputCursor cursor,MTComponent comp) {
			//do nothing not possible
			//System.out.println("NO ELEMENT SELECTED tapRelease");
		}

		public void stateEntry(FingerTapSelection sel) {
			//System.out.println("stateEntry");
			sel.setFirstCursor(null);	
			sel.setFirstCursorComp(null);
			sel.setSelectedComponents(new ArrayList<MTComponent>());
		}

		public void stateExit(FingerTapSelection sel) {
			// TODO Auto-generated method stub
			
		}
		
	},
	
	ONEELEMENTSELECTED
	{

		private boolean sameComponent = false;
		public synchronized void tapPress(FingerTapSelection sel,InputCursor cursor,MTComponent comp) {
			this.stateExit(sel);
			//System.out.println("ONELEMENTSELECTED tapPress");
			if(!sel.compIsInSelection(comp))//do not change status or add component if it is already in comp,
											//this happens if you want to do an rotate or scale action on an object
			{
				
				sel.addComponentToSelection(comp);	
				
				sameComponent = false;		
				sel.setState(MANYELEMENTSSELECTED);
				
			}else
			{
				sameComponent = true;
			}
		}

		public synchronized void tapRelease(FingerTapSelection sel,InputCursor cursor,MTComponent comp) {
			//System.out.println("ONELEMENTSELECTED tapRelease");
			if(!sameComponent)
			{
				this.stateExit(sel);			
				sel.setState(NOELEMENTSELECTED);				
			}else
			{
				sameComponent = false;
			}
		}

		public void stateEntry(FingerTapSelection sel) {
			
			
		}

		public void stateExit(FingerTapSelection sel) {
			this.sameComponent = false;//reset info about same component
			//System.out.println("ONEELEMENTSELECTED");			
			
		}
		
	},
	
	MANYELEMENTSSELECTED
	{

		
		public synchronized void tapPress(FingerTapSelection sel,InputCursor cursor,MTComponent comp) {
			//System.out.println("MANYELEMENTSSELECTED tapPress");
			sel.addComponentToSelection(comp);
			sel.setState(MANYELEMENTSSELECTED);
		}

		public synchronized void tapRelease(FingerTapSelection sel,InputCursor cursor,MTComponent comp){
		
			//System.out.println("MANYELEMENTSSELECTED taprealase");
			
			if(sel.isFirstCursor(cursor) && sel.getCurrentlyPressedCursors().size()==0)
			{
			
				sel.removeCurrentlySelectedFromCanvas();
				sel.createCluster();
				this.stateExit(sel);
				sel.setState(NOELEMENTSELECTED);
			}else if(sel.isFirstCursor(cursor)&&sel.getCurrentlyPressedCursors().size()!=0)
			{
				sel.setFirstCursor(sel.getCurrentlyPressedCursors().get(0));
			}else
			{				
				
				sel.setState(MANYELEMENTSSELECTED);
			}
			
		}

		public void stateEntry(FingerTapSelection sel) {
			// TODO Auto-generated method stub
			
		}

		public void stateExit(FingerTapSelection sel) {
			// TODO Auto-generated method stub
			//System.out.println("MANYELEMENTSSELECTED");
			
			
		}
		
	},
	
	CLUSTERSELECTED
	{

		private boolean tapPressed = false;
		
		public synchronized void tapPress(FingerTapSelection sel,InputCursor cursor,MTComponent comp){
							
			if(sel.getCurrentlyPressedCursors().size()==1)
			{
				if(sel.getFirstCursor()!=cursor)
				{
					//System.out.println("Cluster selected changing to first Cursor " + cursor.getId() + " compname " + comp.getName());					
					sel.setFirstCursor(cursor);
					sel.setFirstCursorComp(comp);
				}
			}
			//System.out.println("Cluster selected");
		}

		public synchronized void tapRelease(FingerTapSelection sel,InputCursor cursor,MTComponent comp) {
			//System.out.println("Cluster selected Tap Release");
			Cluster3DExt cluster = sel.getClusterDataManager().getClusterForComponent(comp);
			//if you have selected one cluster and tap on a second cluster
			//then all children of the second cluster will be
			//added to the first selected cluster
			
			//DEBUG
			MTComponent[] children = sel.getCurrentlySelectedCluster().getChildren();
			 for (int i = 0; i < children.length; i++) {
				MTComponent mtComponent = children[i];
				//System.out.println(mtComponent.getName());
			}
			//DEBUG
			 
			if(cluster!=null&&cluster!=sel.getCurrentlySelectedCluster())
			{		
				
				for(MTComponent mtComp : cluster.getChildren())
				{
					sel.getCurrentlySelectedCluster().addChild(mtComp);					
				}				
				
				sel.getClusterDataManager().deleteCluster(cluster);
			}else if(cluster!=null&&cluster==sel.getCurrentlySelectedCluster())//if element is already in the same cluster as first cluster
			{		
				if(!sel.isFirstCursor(cursor)&&comp!=sel.getFirstCursorComp())//if it is the first cursor which has been released do not remove component, only deselect later 
				{															  //compare if is the component of the first cursor, cause this can not be removed, due to starting of other
																			  //actions like Rotate3D and Scaling etc.
					sel.getClusterDataManager().removeComponentFromCluster(comp, cluster);
					sel.addComponentToCanvas(comp);
										
					//if cluster is still available					
					if(cluster.getChildren().length>1)
					{
						//System.out.println("Cluster selected Tap Release changing cluster state CLUSTERSELECTED " + cursor.getId() + " compname " + comp.getName() );
						MTComponent[] children1 = sel.getCurrentlySelectedCluster().getChildren();
						 for (int i = 0; i < children1.length; i++) {
							MTComponent mtComponent = children1[i];
							//System.out.println(mtComponent.getName());
						}						
						
						sel.setState(CLUSTERSELECTED);						
					}else
					{	
						//System.out.println("Cluster selected Tap Release changing cluster state ONEELEMENTSELECTED");
						this.stateExit(sel);
						sel.addComponentToSelection(cluster.getChildren()[0]);
						
						sel.setState(ONEELEMENTSELECTED);
					}
				}
			}else
			{
				sel.removeComponentFromCanvas(comp);
				sel.getClusterDataManager().addComponentToCluster(comp, sel.getCurrentlySelectedCluster());
				
			}
			
			//System.out.println("CurrentlyPressedCursors " +sel.getCurrentlyPressedCursors().size() );
			if(sel.getCurrentlyPressedCursors().size()==0)
			{					
					sel.setState(NOELEMENTSELECTED);
			}else
			{
				if(sel.isFirstCursor(cursor))
				{
					sel.setFirstCursor(sel.getCurrentlyPressedCursors().get(0));//set the first cursor to the next cursor which is pressed
					MTComponent compAtNewCursor = sel.getComponentForCursor(sel.getCurrentlyPressedCursors().get(0));//set the component for the new first cursor
					sel.setFirstCursorComp(compAtNewCursor);
				}
			}
		}

		public void stateEntry(FingerTapSelection sel) {
			//System.out.println("CLUSTERSELECTED STATEENTRY");
			sel.getSelectionManager().fireClusterSelectionEvent(new MTClusterEvent(this,MTClusterEvent.CLUSTER_SELECTED,sel.getCurrentlySelectedCluster()));
		}

		public void stateExit(FingerTapSelection sel) {
			sel.setCurrentlySelectedCluster(null);		
		}
		
	};
	
}
