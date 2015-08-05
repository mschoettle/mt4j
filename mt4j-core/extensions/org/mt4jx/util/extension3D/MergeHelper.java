package org.mt4jx.util.extension3D;

import java.util.ArrayList;
import java.util.HashMap;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.bounds.IBoundingShapeMergable;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;

public class MergeHelper implements StateChangeListener {

	HashMap<Integer,IBoundingShape> boundingShapes = new HashMap<Integer,IBoundingShape>();

	private int mergedCounter = 0;

	private int normalCounter = 0;

	private int dirtyCounter = 0;

	private int normalInsideCounter;

	private int addedCounter;

	private int isNorMergedCounter;

	private int isMergedCounter;

	private int case1;

	private int case2;

	private int getboundsinside;
	
	private static MergeHelper helperSingleton;
	private static long counter = 0;
	
	private MergeHelper()
	{
		
	}
	
	public static MergeHelper getInstance()
	{
		if(helperSingleton==null)
		{
			helperSingleton = new MergeHelper();
			return helperSingleton;
		}else
		{
			return helperSingleton;
		}
	}
	
	/**
	 * return the merged Bounds of the component with all children
	 * @param comp, the component which should be merged
	 * @param dirty if the passed component has changed its matrix
	 *  	  true in case of calling this method after a statechange	     	 
	 * @return
	 */	
	private IBoundingShape mergeBoundsWithChildren(MTComponent comp,boolean dirty)
	{	
		
	//	System.out.println("Dirtycounter " + dirtyCounter + " normalCounter " + normalCounter + " mergedCounter " + mergedCounter);
		if (isMergedOfChildrenBounds(comp)==true&&!dirty) return getMergedBoundsForComponent(comp);
		//System.out.println("normalinside " + normalInsideCounter++);
		
		if(comp.getChildren().length==0)
		{		
			addMTComponentWithMergedBounding(comp,comp.getBounds());
			return comp.getBounds();
		}
		
		ArrayList<IBoundingShapeMergable> shapesToMerge = new ArrayList<IBoundingShapeMergable>();
		
		if(comp.hasBounds())
		{
			if (comp.getBounds() instanceof IBoundingShapeMergable) {
				IBoundingShapeMergable mergeableBounds = (IBoundingShapeMergable)comp.getBounds(); 
				addMTComponentWithMergedBounding(comp, mergeableBounds);
				shapesToMerge.add(mergeableBounds);
			}
			
		}
		
		for(int i=0;i < comp.getChildren().length;i++)
		{				
			MTComponent children = comp.getChildren()[i];
			IBoundingShape shape1 = mergeBoundsWithChildren(children,false);
			
			if (shape1 instanceof IBoundingShapeMergable) {
				IBoundingShapeMergable mergeableBounds = (IBoundingShapeMergable)shape1; 
				IBoundingShapeMergable shape = mergeableBounds.getBoundsTransformed(TransformSpace.RELATIVE_TO_PARENT);
				shapesToMerge.add(shape);
			}
		}
		
		for(int i=shapesToMerge.size()-1;i>0;i--)
		{
			IBoundingShapeMergable mergedShape = shapesToMerge.get(i).merge(shapesToMerge.get(i-1));
			shapesToMerge.set(i-1,mergedShape);			
		}
				
		if(shapesToMerge.size()>0)
		{
			addMTComponentWithMergedBounding(comp,shapesToMerge.get(0));			
			return shapesToMerge.get(0);
		}else
		{
			return null;
		}
	}
	
	private void addMTComponentWithMergedBounding(MTComponent comp,IBoundingShape shape)
	{			 
		//System.out.println("Added counter " +  comp.getID() + " " + addedCounter++ + " " + boundingShapes.size());
		boundingShapes.put(comp.getID(),shape);		
	}
	
	private void removeMTComponentWithMergedBounding(MTComponent comp)
	{
		boundingShapes.remove(comp.getID());
	}
	
	public IBoundingShape getMergedBoundsForComponent(MTComponent comp)
	{
		if(boundingShapes.containsKey(comp.getID()))
		{		
			return boundingShapes.get(comp.getID());
		}else
		{
			IBoundingShape shape =  mergeBoundsWithChildren(comp,true);			
			updateParentAfterMerge(comp);
			return shape;
		}		
	}
	
	/**
	 * 
	 * @param comp
	 */
	private void updateParentAfterMerge(MTComponent comp)
	{		
		if(comp.getParent()!=null&&boundingShapes.containsKey(comp.getParent().getID())==true)
		{
			mergeBoundsWithChildren(comp.getParent(),true);		
		}
	}
	
	public boolean isMergedOfChildrenBounds(MTComponent comp)
	{
		//System.out.println(comp.getID());
		
		return boundingShapes.containsKey(comp.getID());
	}

	public void stateChanged(StateChangeEvent evt) {
			
		//IF needed add a new state to StateChange.GLOBAL_TRANSFORM_CHANGED to StateChange
		//and in MTComponent propagteMatrixChange fire this StateChange
		/*if(evt.getSource() instanceof MTComponent&&evt.getState()==StateChange.GLOBAL_TRANSFORM_CHANGED)
		{			
			MTComponent comp = (MTComponent)evt.getSource();
			mergeBoundsWithChildren(comp,true);	
			updateParentAfterMerge(comp);	
		}*/
	}
	
}
