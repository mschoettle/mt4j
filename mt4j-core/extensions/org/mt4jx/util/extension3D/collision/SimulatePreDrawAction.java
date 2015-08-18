package org.mt4jx.util.extension3D.collision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.components.visibleComponents.widgets.MTOverlayContainer;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.gestureAction.ICollisionAction;
import org.mt4j.input.gestureAction.Rotate3DAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Rotate3DEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.widgets.MTDepthHelper;
import org.mt4jx.input.gestureAction.DefaultDepthAction;
import org.mt4jx.input.inputProcessors.componentProcessors.depthProcessor.DepthGestureEvent;
import org.mt4jx.util.extension3D.ComponentHelper;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;
import com.bulletphysics.linearmath.Transform;



public class SimulatePreDrawAction implements IPreDrawAction {

	private CollisionWorld collisionWorld;
	private float currentTimeStep = 0.f;
	private Iscene sceneRef;
	private boolean debugPoint = false;
	private CollisionManager collisionManager;
	private AbstractMTApplication mtApp;
	private HashMap<MTComponent,Transform> oldComponentMatrices = new HashMap<MTComponent,Transform>();
	private HashMap<MTComponent,Vector3f> oldComponentScaling = new HashMap<MTComponent,Vector3f>();
	private ArrayList<MTComponent> contactMap = new ArrayList<MTComponent>();
	private MTCanvas canvas;
	//TODO this class should be optimized. performance and structure
	
	public SimulatePreDrawAction(CollisionWorld v_collisionWorld,CollisionManager v_collisionManager,MTCanvas v_canvas)
	{
		this.setCollisionWorld(v_collisionWorld);
		this.collisionManager = v_collisionManager;
		this.canvas = v_canvas;
	}
	
	public boolean isLoop() {
		return true;
	}
		
	public void processAction() {
		
		if(getCollisionWorld()!=null&&getCurrentTimeStep()!=0.f)
		{
		
			
		final Transform m = new Transform();
			m.setIdentity();
					
    		if(getCollisionWorld()!=null)
    		{
    			for(int i=0;i<getCollisionWorld().getNumCollisionObjects();i++)
    			{
    				CollisionObject obj = getCollisionWorld().getCollisionObjectArray().get(i);
    				CollisionShape shape = obj.getCollisionShape();
    							
    				MTComponent mesh = (MTComponent)collisionManager.getAssociatedComponent(obj);
    				    				
    				if(mesh!=null)
    				{ 
	    				Matrix	mat = mesh.getGlobalMatrix();
    					   							
	    				Transform tf = new Transform();
	    				Matrix4f mat4f = CollisionManager.convertMT4JMatrixToMatrix4f(mat);
	    				tf.set(mat4f);
	    				
	    				Transform oldTransform = new Transform();//save old values
	    				obj.getWorldTransform(oldTransform);
	    				
	    				//temp
	    				Matrix4f mat1 = new Matrix4f();
	    				oldTransform.getMatrix(mat1);
	    			        				
	    				oldComponentMatrices.put(getFirstNonMTTriangleMeshParent(mesh), oldTransform);
	    				//save current scaling for later undo
	    				Vector3f oldScaleVec = new Vector3f();
	    				obj.getCollisionShape().getLocalScaling(oldScaleVec);
	    				
	    				oldComponentScaling.put(getFirstNonMTTriangleMeshParent(mesh),oldScaleVec);
	    				
	    				//tf.basis.setScale(1.0f);
	    				
	    				//tf.origin.set(vec);
	    				
	    				Vector3f scale = new Vector3f(); //get scale value of global matrix
	    					    					    					    				
	    				Vector3D xVec = new Vector3D(mat.m00,mat.m01,mat.m02);
	    				Vector3D yVec = new Vector3D(mat.m10,mat.m11,mat.m12);
	    				Vector3D zVec = new Vector3D(mat.m20,mat.m21,mat.m22);
	    				
	    				scale.x = xVec.length();
	    				scale.y = yVec.length();
	    				scale.z = zVec.length();
	    					    				
	    				float[] scaleVals = new float[3];
	    				scale.get(scaleVals);
	    				
	    				for(int a=0;a<3;a++)//get rotation value by extracting scalation
	    				{
	    				
	    					try {
	    						float[] colvals = mat.getRow(a);
	    					
	    						for(int j=0;j<3;j++)
	    						{
	    							colvals[j] = colvals[j] / scaleVals[a];
	    						}
	    						tf.basis.setRow(a,colvals);
	    					} catch (Exception e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					}
	    				}
	    				   				
	    				obj.getCollisionShape().setLocalScaling(scale);//apply scaling of world matrix to the collision shape, remember no scaling in rigid body
	    				((GImpactMeshShape)obj.getCollisionShape()).updateBound();//has to be done that the collision shape bound is updated after scaling
	    				obj.setWorldTransform(tf);	 //set world transform of object, only rotation and translation of MT4j world matrix  				
	    				   			
	    				
    				}
    			
    				
    			}    			
    			
    			getCollisionWorld().performDiscreteCollisionDetection();//do bullet engine collision detection
    			        			
    			//do ray intersection testing for objects which
    			//are translated in depth
    			//because it can be that if the velocity is higher than
    			//the size of the object that their is no collision.
    			//This is due to in the moment before the collision the
    			//object is completely in front of the other object and the moment after the
    			//object is completely after the other object. So the collision 
    			//is missed. Thus we do a ray intersection test 
    			//between the two transformation points
    			//TODO this works only for a bottom plane in z direction
    			//it should be extended to all other directions
    			for(int i=0;i<canvas.getChildren().length;i++)
				{
					if(canvas.getChildren()[i] instanceof MTDepthHelper)
					{
						MTDepthHelper helper = (MTDepthHelper) canvas.getChildren()[i];
						MTComponent targetComp = (MTComponent) helper.getTargetComponent();
												
						if(targetComp instanceof Cluster3DExt)
						{
							Cluster3DExt cl = (Cluster3DExt)targetComp;
							MTComponent[] clChildren = cl.getChildren();
							
							for(MTComponent children : clChildren)
							{
								Transform trans = oldComponentMatrices.get(children);	
								
								Vector3f vecOld = trans.origin;
								Vector3D vecOldMt4j = new Vector3D(vecOld.x,vecOld.y,vecOld.z);
								
								ArrayList<CollisionObject> objs = collisionManager.getAllObjectsForCollisionGroup(children);
								if(objs.size()>0)
								{
									Transform out = new Transform();
									objs.get(0).getWorldTransform(out);
									Vector3f vecNew = out.origin;
									Vector3D vecNewMt4j = new Vector3D(vecNew.x,vecNew.y,vecNew.z);
									Ray ray = new Ray(vecOldMt4j,vecNewMt4j);
									boolean clusterCollision = false;//determines if there is a cluster collision
									CollisionObject collisionObjectOnCanvas = null; //object with which the cluster collides
									
									for(int a=0;a<canvas.getChildren().length;a++)
									{
										if(canvas.getChildren()[a]!=children&&!(canvas.getChildren()[a] instanceof Cluster3DExt)&&!(canvas.getChildren()[a] instanceof MTOverlayContainer)&&(ComponentHelper.getCenterPointGlobal(canvas.getChildren()[a])!=null))
										{																
											
											if(ComponentHelper.getCenterPointGlobal(canvas.getChildren()[a]).z<ray.getRayStartPoint().z&&ComponentHelper.getCenterPointGlobal(canvas.getChildren()[a]).z>ray.getPointInRayDirection().z)
												{
													Vector3D interSectionPos = ComponentHelper.getIntersectionGlobal(canvas.getChildren()[a],ray);
													
													if(interSectionPos!=null)
													{
														clusterCollision = true;
																									
														collisionObjectOnCanvas = collisionManager.getAllObjectsForCollisionGroup((canvas.getChildren()[a])).get(0);
														break;
													}
												}
											}
										}
									//}
									
									//in case of any of the children of the clusters has a collision
									//prepare collision for all children
									if(clusterCollision)
									{
										objectCollision(collisionObjectOnCanvas);
										for(MTComponent comp : clChildren)
										{
											ArrayList<CollisionObject> compColObjs = collisionManager.getAllObjectsForCollisionGroup(comp);
											objectCollision(compColObjs.get(0));//get only first collision object, collision of other objects will be done in objectCollision method											
										}
									}
								}
							}
						}else
						{
							Transform trans = oldComponentMatrices.get(targetComp);	
													
							Vector3f vecOld = trans.origin;
							Vector3D vecOldMt4j = new Vector3D(vecOld.x,vecOld.y,vecOld.z);
														
							ArrayList<CollisionObject> objs = collisionManager.getAllObjectsForCollisionGroup(targetComp);
							if(objs.size()>0)
							{
								Transform out = new Transform();
								objs.get(0).getWorldTransform(out);
								Vector3f vecNew = out.origin;
								Vector3D vecNewMt4j = new Vector3D(vecNew.x,vecNew.y,vecNew.z);
						
								Ray ray = new Ray(vecOldMt4j,vecNewMt4j);
								
								for(int a=0;a<canvas.getChildren().length;a++)
								{
									
									if(canvas.getChildren()[a]!=targetComp&&!(canvas.getChildren()[a] instanceof MTOverlayContainer)&&(ComponentHelper.getCenterPointGlobal(canvas.getChildren()[a])!=null))
									{											
										if(ComponentHelper.getCenterPointGlobal(canvas.getChildren()[a]).z<ray.getRayStartPoint().z
												&&ComponentHelper.getCenterPointGlobal(canvas.getChildren()[a]).z>ray.getPointInRayDirection().z)
										{
										
											Vector3D interSectionPos = ComponentHelper.getIntersectionGlobal(canvas.getChildren()[a],ray);
											if(interSectionPos!=null)
											{
												objectCollision(objs.get(0));//get only first collision object, collision of other objects will be done in objectCollision method													
												objectCollision(collisionManager.getAllObjectsForCollisionGroup((canvas.getChildren()[a])).get(0));//the object with which it is colliding													
											}
										}
										
									}
								}
								
							}
						}
						
					}
				}
    			
    			//test if engine detected collisions and
    			//execute collision behaviour for colliding objects
    			int numManifolds = collisionWorld.getDispatcher().getNumManifolds();
    			boolean contact = false;		
    			 
    			for(int a=0;a<numManifolds;a++)
    			{    				
    				PersistentManifold contactManifold = collisionWorld.getDispatcher().getManifoldByIndexInternal(a);
    			   	  
    				Cluster3DExt cl = isChildrenOfACluster(collisionManager.getAssociatedComponent((CollisionObject)contactManifold.getBody0()));
    				Cluster3DExt cl2 = isChildrenOfACluster(collisionManager.getAssociatedComponent((CollisionObject)contactManifold.getBody1()));
    								
    				if(cl!=null)
    				{    					
    					performCollisionForClusterChildren(cl);
    				}else
    				{
    					objectCollision(contactManifold.getBody0());//set back collision object 1
    				}
    				
    				if(cl2!=null)
    				{    					
    					performCollisionForClusterChildren(cl2);
    				}else
    				{
    					objectCollision(contactManifold.getBody1());//set back collision object 2
    				}
    			}
    			
    				
    			}
    			
    		
    		
    		}

			Iterator<MTComponent> iterGroups = collisionManager.getAllCollisionGroups().iterator();
			/*if(contactMap.size()==0)
			{
				System.out.println("contact map empty");
			}
			else
			{
				System.out.println("contact map full");
			}*/
			
			while(iterGroups.hasNext())
			{
				MTComponent comp = iterGroups.next();
				if(!contactMap.contains(comp))
				{
					//get MTCanvas to look for a Drag Helper Object 
					//if one is on the canvas and the target comp is our collision body pause the depth gesture
					/*for(int i=0;i<canvas.getChildren().length;i++)
					{
						if(canvas.getChildren()[i] instanceof MTDepthHelper)
						{
							MTDepthHelper helper = (MTDepthHelper) canvas.getChildren()[i];
							MTComponent targetComp = (MTComponent) helper.getTargetComponent();
							
							if(targetComp==comp)
							{
								if(helper.getDepthProcessor().isGesturePaused())
								{
									System.out.println("resume");
									helper.getDepthProcessor().resumeGesture();
								}
							}
						}
					}*/
					
					for(int counterListener=0;counterListener<comp.getGestureListeners().length;counterListener++)
					{
						IGestureEventListener listener = comp.getGestureListeners()[counterListener];
						if(listener instanceof ICollisionAction)
						{
							boolean gestureAbort = proofGestureAbort(listener,comp);
							
							((ICollisionAction)listener).setGestureAborted(gestureAbort);							
						}
					}					
					
				}
			}
			contactMap.clear();
		
		
	}
	
	private void performCollisionForClusterChildren(Cluster3DExt cl)
	{
		if(cl!=null)
		{
			for(MTComponent clusterChildren : cl.getChildren())
			{
				objectCollision(collisionManager.getAllObjectsForCollisionGroup(clusterChildren).get(0));
			}
		}
	}
	
	/**
	 * perform collision behaviour for a specific object
	 * 1. put object into a map
	 * 2. stop gesture actions
	 * 3. set back to old transform and scale
	 * @param body
	 */
	private void objectCollision(Object body)
	{
		
		if(body instanceof CollisionObject)
		{
				CollisionObject obj = (CollisionObject)body;
				MTComponent comp = collisionManager.getAssociatedComponent(obj);
				
				//add collision object to contact map, so gestures 
				//will not be resumed for this component
				if(!contactMap.contains(getFirstNonMTTriangleMeshParent(comp)))
				{
					contactMap.add(getFirstNonMTTriangleMeshParent(comp));
				}else
				{
					return;//everything has already been done for this object
				}
				
				
				
			
			for(int b=0;b<getFirstNonMTTriangleMeshParent(comp).getGestureListeners().length;b++)
			{
				if(comp.getParent().getGestureListeners()[b] instanceof org.mt4j.input.gestureAction.ICollisionAction)
				{
					org.mt4j.input.gestureAction.ICollisionAction dragAction = (org.mt4j.input.gestureAction.ICollisionAction)getFirstNonMTTriangleMeshParent(comp).getGestureListeners()[b];
					dragAction.setGestureAborted(true);              					
				}			        					    					
			}
			        				
			//set back to old transform and scale
			Transform oldTransform = oldComponentMatrices.get(getFirstNonMTTriangleMeshParent(comp));
			Vector3f oldScale = oldComponentScaling.get(getFirstNonMTTriangleMeshParent(comp));
			
			Iterator<CollisionObject> iter = collisionManager.getAllObjectsForCollisionGroup(getFirstNonMTTriangleMeshParent(comp)).iterator();
			while(iter.hasNext())
			{
				CollisionObject colObj = iter.next();
				colObj.setWorldTransform(oldTransform);
				colObj.getCollisionShape().setLocalScaling(oldScale);	
				((GImpactMeshShape)colObj.getCollisionShape()).updateBound();//has to be done that the collision shape bound is updated after scaling
			}
			
			//oldTransform.basis.
			Matrix oldMatrix = new Matrix();
	
			try
			{
				for(int counter=0;counter<3;counter++)
				{
					float[] colVals = new float[4];            						
					oldTransform.basis.getRow(counter, colVals);
					oldMatrix.setRow(counter, colVals);           						
				}
				float[] translateVals = new float[4];
				oldTransform.origin.get(translateVals);
				oldMatrix.setColumn(3,translateVals);
				
				Vector3f vec = new Vector3f();
				//get the scaling for our matrix from the collision shape, because the 
				//rigid body must not have any scaling in his matrix
				obj.getCollisionShape().getLocalScaling(vec);
					
				Vector3D vecMT4J = new Vector3D(vec.x,vec.y,vec.z);
				
				float scaleVals[] = new float[3];
				vecMT4J.toArray(scaleVals);
				
				//oldMatrix.scale(vecMT4J);
				for(int i=0;i<3;i++)
				{
					float[] row = oldMatrix.getRow(i);
					for(int a=0;a<3;a++)
					{
						row[a] = row[a]*scaleVals[i];
						oldMatrix.setRow(i, row);
					}
				}
				
				oldMatrix.m33 = 1f;
				
				resetComponentMatrix(oldMatrix,getFirstNonMTTriangleMeshParent(comp));
						
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		//end - set back to old transform
	}

	private void resetComponentMatrix(Matrix mat,MTComponent comp)
	{
		//comp.getGlobalMatrix().set(mat);
		//comp.getLocalMatrix().set(mat);
		comp.setLocalMatrix(mat);
		
		/*for(MTComponent childComp : comp.getChildren())
		{
			resetComponentMatrix(mat,childComp);
		}*/
	}
	
	public void setCurrentTimeStep(float currentTimeStep) {
		this.currentTimeStep = currentTimeStep;
	}

	public float getCurrentTimeStep() {
		return currentTimeStep;
	}



	public void setCollisionWorld(CollisionWorld collisionWorld) {
		this.collisionWorld = collisionWorld;
	}

	public CollisionWorld getCollisionWorld() {
		return collisionWorld;
	}

	public void setDebugPoint(boolean debugPoint) {
		this.debugPoint = debugPoint;
	}

	public boolean isDebugPoint() {
		return debugPoint;
	}

	private MTComponent getFirstNonMTTriangleMeshParent(MTComponent comp)
	{
		if(!(comp instanceof MTTriangleMesh))
		{
			return comp;
		}
		
		return getFirstNonMTTriangleMeshParent(comp.getParent());
	}
	
	private Cluster3DExt isChildrenOfACluster(MTComponent comp)
	{
		if(comp instanceof Cluster3DExt)
		{
			return (Cluster3DExt)comp;
		}
		
		if(comp.getParent()==null)
		{
			return null;
		}
		
		return isChildrenOfACluster(comp.getParent());
	}
	
	/**
	 * proofs for specific actions if the gesture should be aborted
	 * @param gestureListener the Action which should be tested
	 * @return false if it should be aborted
	 */
	private boolean proofGestureAbort(IGestureEventListener gestureListener,MTComponent comp)
	{
		if(gestureListener instanceof DefaultDragAction)
		{
			DefaultDragAction act = (DefaultDragAction)gestureListener;
			DragEvent lastEvent = (DragEvent) act.getLastEvent();
					
			if(lastEvent!=null)
			{
				if (comp.getIntersectionGlobal(Tools3D.getCameraPickRay(comp.getRenderer(), comp, lastEvent.getDragCursor().getCurrentEvtPosX(), lastEvent.getDragCursor().getCurrentEvtPosY())) != null)
				{
					return false;
				}
			}
		}
		else if(gestureListener instanceof DefaultScaleAction)
		{
			DefaultScaleAction act = (DefaultScaleAction)gestureListener;
			ScaleEvent lastEvent = (ScaleEvent)act.getLastEvent();
			
			if(lastEvent!=null)
			{
				if (comp.getIntersectionGlobal(Tools3D.getCameraPickRay(comp.getRenderer(), comp,
						lastEvent.getFirstCursor().getCurrentEvtPosX(), lastEvent.getFirstCursor().getCurrentEvtPosY())) != null ||
						comp.getIntersectionGlobal(Tools3D.getCameraPickRay(comp.getRenderer(), comp,
								lastEvent.getSecondCursor().getCurrentEvtPosX(), lastEvent.getSecondCursor().getCurrentEvtPosY())) != null)
				{
					return false;
				}
			}
		}
		else if(gestureListener instanceof DefaultRotateAction)
		{
			DefaultRotateAction act = (DefaultRotateAction)gestureListener;
			RotateEvent lastEvent = (RotateEvent)act.getLastEvent();
			
			if(lastEvent!=null)
			{
				if (comp.getIntersectionGlobal(Tools3D.getCameraPickRay(comp.getRenderer(), comp,
						lastEvent.getFirstCursor().getCurrentEvtPosX(), lastEvent.getFirstCursor().getCurrentEvtPosY())) != null ||
						comp.getIntersectionGlobal(Tools3D.getCameraPickRay(comp.getRenderer(), comp,
								lastEvent.getSecondCursor().getCurrentEvtPosX(), lastEvent.getSecondCursor().getCurrentEvtPosY())) != null)
				{
					return false;
				}
			}
		}
		else if(gestureListener instanceof Rotate3DAction)
		{
			Rotate3DAction act = (Rotate3DAction)gestureListener;
			Rotate3DEvent lastEvent = (Rotate3DEvent)act.getLastEvent();
			
			if(lastEvent!=null)
			{
				if (comp.getIntersectionGlobal(Tools3D.getCameraPickRay(comp.getRenderer(), comp,
						lastEvent.getFirstCursor().getCurrentEvtPosX(), lastEvent.getFirstCursor().getCurrentEvtPosY())) != null ||
						comp.getIntersectionGlobal(Tools3D.getCameraPickRay(comp.getRenderer(), comp,
								lastEvent.getSecondCursor().getCurrentEvtPosX(), lastEvent.getSecondCursor().getCurrentEvtPosY())) != null)
				{
					return false;
				}
			}
		}else if(gestureListener instanceof DefaultDepthAction)
		{
			DefaultDepthAction act = (DefaultDepthAction)gestureListener;
			DepthGestureEvent lastEvent = (DepthGestureEvent)act.getLastEvent();
			
			if(lastEvent!=null)
			{
				 return false;
			}
		}
		return true;
	}
}
