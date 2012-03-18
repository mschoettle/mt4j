package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.GroupVisualizations;

import java.util.ArrayList;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundingSphere;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.input.IMTEventListener;
import org.mt4j.input.MTEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.ConvexQuickHull2D;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.MTClusterEvent;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.MTLassoSelectionEvent;

import processing.core.PApplet;

public class LassoVisualizationAction implements IMTEventListener {

	//THIS CLASS IS NOT MAINTAINED ANYMORE
	private PApplet pApplet;
	
	public LassoVisualizationAction(PApplet pApplet)
	{
		this.pApplet = pApplet;
	}
	public void processMTEvent(MTEvent mtEvent) {
		
		if(mtEvent instanceof MTLassoSelectionEvent)
		{
			MTLassoSelectionEvent lassoEvent = (MTLassoSelectionEvent)mtEvent;
			switch(lassoEvent.getId())
			{
				case MTLassoSelectionEvent.SELECTION_ENDED:
					if(lassoEvent.getCluster()!=null&&lassoEvent.getSelectedComps().size()>1)
					{
						lassoEvent.getSelectionPoly().setFillColor(new MTColor(100,150,250,50));
					
						lassoEvent.getSelectionPoly().setGestureAllowance(DragProcessor.class, true);
						lassoEvent.getSelectionPoly().setGestureAllowance(RotateProcessor.class, true);
						lassoEvent.getSelectionPoly().setGestureAllowance(ScaleProcessor.class, true);					
						lassoEvent.getSelectionPoly().setPickable(false);
						
						lassoEvent.getSelectionPoly().setBoundsAutoCompute(true);
						lassoEvent.getSelectionPoly().setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
						
						packClusterPolygon(lassoEvent.getSelectionPoly(),lassoEvent.getCluster());
						
						lassoEvent.getSelectionPoly().setLineStipple((short)0xDDDD);
						lassoEvent.getSelectionPoly().setStrokeColor(new MTColor(0,0,0,255));
						
						MTComponent visualComponentGroup = new MTComponent(pApplet, lassoEvent.getCluster().getAttachedCamera());
						visualComponentGroup.addChild(lassoEvent.getSelectionPoly());
						//System.out.println("lassoEvent " + lassoEvent.getSelectionPoly().toString());
						lassoEvent.getCluster().setVisualComponentGroup(visualComponentGroup);
						((AbstractMTApplication)pApplet).getCurrentScene().getCanvas().addChild(lassoEvent.getSelectionPoly());
						
					}
					break;
				case MTLassoSelectionEvent.SELECTION_UPDATED:					
					break;
			}
		}
		else if(mtEvent instanceof MTClusterEvent)
		{
			MTClusterEvent clEvent = (MTClusterEvent)mtEvent;
			switch(clEvent.getId())
			{
				case MTClusterEvent.CLUSTER_UPDATED:					
					MTPolygon polygon = createNewPolygon(clEvent.getCluster());
					MTComponent visualComponentGroup = new MTComponent(pApplet, clEvent.getCluster().getAttachedCamera());
					visualComponentGroup.addChild(polygon);					
					clEvent.getCluster().setVisualComponentGroup(visualComponentGroup);					
					break;
				case MTClusterEvent.CLUSTER_DELETED:
					for(MTComponent comp : clEvent.getCluster().getChildren())
					{
						if(comp instanceof MTPolygon)
						{
							clEvent.getCluster().removeChild(comp);
						}
					}
			}
		}
		
		
	}
	
	private MTPolygon createNewPolygon(Cluster3DExt cluster)
	{
		MTPolygon polygon = new MTPolygon(pApplet, new Vertex[0]);
		polygon.setFillColor(new MTColor(100,150,250,50));
		
		polygon.setGestureAllowance(DragProcessor.class, true);
		polygon.setGestureAllowance(RotateProcessor.class, true);
		polygon.setGestureAllowance(ScaleProcessor.class, true);					
		
		polygon.setBoundsAutoCompute(true);
		polygon.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
		
		packClusterPolygon(polygon,cluster);
		
		polygon.setLineStipple((short)0xDDDD);
		polygon.setStrokeColor(new MTColor(0,0,0,255));
		polygon.attachCamera(cluster.getAttachedCamera());
		return polygon;
	}
	
	/**
	 * Calculates the convex hull of all its children.
	 * Then changes the cluster-polygon to represent that convex hull
	 * and adds it as a child.
	 */
	private void packClusterPolygon(MTPolygon polygon,Cluster3DExt cluster){
		ArrayList<Vector3D> allClusteredVerts = new ArrayList<Vector3D>();
				
		MTComponent[] children = cluster.getChildren();
		for (int i = 0; i < children.length; i++) {
			allClusteredVerts.addAll(getAllClusteredVerts(children[i],polygon,cluster));
		}
		
		//if (shapes != 0){// && shapes == children.length){ //If all children are of type abstractShape
			
			ArrayList<Vector3D> hull = ConvexQuickHull2D.getConvexHull2D(allClusteredVerts);
			if (hull.size() > 0){
				//Correctly close polygon with 1.st vertex again
				hull.add(hull.get(0).getCopy());
				
				Vertex[] newVerts = new Vertex[hull.size()];
				for (int i = 0; i < hull.size(); i++) {
					Vector3D vec = hull.get(i);
					newVerts[i] = new Vertex(vec);
				}
				
//				Vertex[] newVerts = (Vertex[])hull.toArray(new Vertex[hull.size()]);
//				System.out.println("Hull vertices: ");
				for (Vertex v : newVerts){
					v.setRGBA(100,150,250, 50);
				}
				
				polygon.setVertices(newVerts);
				
				polygon.setBoundsBehaviour(AbstractShape.BOUNDS_DONT_USE);
//				clusterPoly.setBoundingShape(new BoundsArbitraryPlanarPolygon(clusterPoly, clusterPoly.getVerticesLocal()));
				
				//Reset matrix of the clusterpoly because the new vertices are set at the global location
				polygon.setLocalMatrix(new Matrix()); 
				
				//FIXME center are is negative if verts are in counterclockwise order?
//				Vector3D clusterCenter = clusterPoly.getCenterPointGlobal();
//				clusterPoly.scaleGlobal(1.1f, 1.1f, 1, new Vector3D(-1* clusterCenter.x, -1 * clusterCenter.y, clusterCenter.z));
				polygon.scale(1.1f, 1.1f, 1, polygon.getCenterPointLocal(), TransformSpace.LOCAL);
				
			}else{
				System.err.println("Couldnt pack polygon.");
			}
			//shapes = new Integer(0); //reset for next call to zero
		
	}
	
	private ArrayList<Vector3D> getAllClusteredVerts(MTComponent comp,MTPolygon polygon,Cluster3DExt cluster)
	{		
		ArrayList<Vector3D> allClusteredVerts = new ArrayList<Vector3D>();
				
		if(comp.getChildren().length==0)
		{
			//Get vertices for convex hull of all selected components
			if (comp instanceof AbstractShape){			
				//shapes++;				
				AbstractShape shape = (AbstractShape)comp;
//				Vertex[] verts = shape.getVerticesPickingWorld();
				Vector3D[] verts = null;
				if (shape.hasBounds()){
					 verts = shape.getBounds().getVectorsGlobal();
					 //FIXME add for all not only for boundingsphere
					 if(shape.getBounds() instanceof BoundingSphere)
					 {
						 BoundingSphere bSphere = (BoundingSphere)shape.getBounds();
						 verts = bSphere.getVectorsOnBoundingSphereGlobal(4);						 
					 }
					 //check if points should be projected in case of 3D grouping
					// Vector3D[] newVerts = new Vector3D[verts.length];
					// int i = 0;
					/*for(Vector3D vert : verts)
					{
						if(polygon.getVerticesGlobal()[0].z!=vert.z)
						{
							//Tools3D.projectPointToPlane(vert, this.getAttachedCamera().getFrustum(),selectionPolygon.getVerticesGlobal()[0].z,(MTApplication)this.getRenderer());														
						}						
						
					}*/		
					
				}else{
					 verts = shape.getVerticesGlobal();
				}
								
				for (Vector3D v : verts){
					if(cluster.getAttachedCamera().getFrustum().getZValueOfNearPlane()!=v.z)
					{
						v = Tools3D.projectPointToPlaneInPerspectiveMode(v, cluster.getAttachedCamera().getFrustum(), cluster.getAttachedCamera().getFrustum().getZValueOfNearPlane(),((AbstractMTApplication)pApplet));
					}					
					allClusteredVerts.add(v);
				}
				
			}
			return allClusteredVerts;
		}
	
		for(int i=0;i<comp.getChildren().length;i++)
		{		
			allClusteredVerts.addAll(this.getAllClusteredVerts(comp.getChildren()[i],polygon,cluster));
		}
		
		return allClusteredVerts;
	}

}
