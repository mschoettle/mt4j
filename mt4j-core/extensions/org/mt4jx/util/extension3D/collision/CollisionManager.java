package org.mt4jx.util.extension3D.collision;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Cluster3DExt;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.extras.gimpact.GImpactCollisionAlgorithm;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class CollisionManager {
	
	private CollisionDispatcher dispatcher;
	private BroadphaseInterface overlappingPairCache;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;
	private CollisionWorld collisionWorld;
	private HashMap<CollisionObject,MTComponent> colObjectToComponent = new HashMap<CollisionObject,MTComponent>();
	private HashMap<MTComponent,ArrayList<CollisionObject>> colObjectsForGroup = new HashMap<MTComponent,ArrayList<CollisionObject>>();
	private Iscene scene;
	private HashMap<MTComponent,ArrayList<MTTriangleMesh>> collisionGroups = new HashMap<MTComponent,ArrayList<MTTriangleMesh>>();
	private PApplet pApplet;
	private short groupId = 1;
	public CollisionManager(Iscene scene,PApplet pApplet)
	{
		this.scene = scene;	
		this.pApplet = pApplet;		
		initCollisionWorld();
	}
	
	private List<Vector3f> getConvertedVectors(Vertex[] vertices)
	{
		List<Vector3f> vectors = new ArrayList<Vector3f>();
		
		for(int i=0;i<vertices.length;i++)
		{
			vectors.add(new Vector3f(vertices[i].x,vertices[i].y,vertices[i].z));
		}
		
		return vectors;
	}
	
	private Vertex[] getConvertedVertices(List<Vector3f> vectors)
	{
		Vertex[] vertices = new Vertex[vectors.size()];
		
		for(int i=0;i<vectors.size();i++)
		{
			vertices[i] = new Vertex(vectors.get(i).x,vectors.get(i).y,vectors.get(i).z);			
		}
		
		return vertices;
	}
	/*private CollisionShape createMeshShapeFromMTMeshTriangle(ArrayList<MTTriangleMesh> mesh)
	{
		CompoundShape returnShape; 
		
		returnShape = new CompoundShape();
		
		for(int i=0;i<mesh.size();i++)
		{
			
			ConvexHullShape shape = new ConvexHullShape(getConvertedVectors(mesh.get(i).getVerticesLocal()));
			ShapeHull shaHull = new ShapeHull(shape);
			shaHull.buildHull(1.0f);
					
			ConvexHullShape shapeNew = new ConvexHullShape(shaHull.getVertexPointer());
			
			Transform startTransform = new Transform();
			startTransform.setIdentity();
			Vector3D translate = mesh.get(i).getCenterPointLocal();
			Matrix4f mat = CollisionManager.convertMT4JMatrixToMatrix4f(mesh.get(i).getLocalMatrix());
			
			Vector3f vec = new Vector3f(0.0f,0.0f,0.0f);
			vec.x = translate.x;
			vec.y = translate.y;
			vec.z = translate.z;
			mat.setTranslation(vec);
			startTransform.set(mat);
			//startTransform.origin.set(vec)
			
			returnShape.addChildShape(startTransform, shapeNew);
		}
		
		/*mesh.get(0).getV
		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(
				mesh.get(0).getTriangleCount(), mesh.get(0).getIndexBuffer(), 4 * 3,
				mesh.get(0).getVertexCount(), mesh.get(0).getVertexBuffer(mesh.get(0).getCenterPointRelativeToParent()), 4 * 3);*/
		
		//Get VertexArray for Collision 
				
		//GImpactMeshShape trimesh = new GImpactMeshShape(indexVertexArrays);
		
		//trimesh.updateBound();
		
		//returnShape = trimesh;
		
		// register algorithm
		//GImpactCollisionAlgorithm.registerAlgorithm(dispatcher);
	
	//	return returnShape;
//	}*/
	

	
	private CollisionShape createMeshShapeFromMTMeshTriangle(MTTriangleMesh mesh)
	{
			CollisionShape colShape; 
		
			GImpactMeshShape returnShape; 
									
			TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(
					mesh.getTriangleCount(), getIndexBuffer(mesh), 4 * 3,
					mesh.getVertexCount(), getVertexBuffer(mesh), 4 * 3);
			
			returnShape = new GImpactMeshShape(indexVertexArrays);
							
			returnShape.updateBound();
			colShape = returnShape;
			
			GImpactCollisionAlgorithm.registerAlgorithm(dispatcher);
			
			return colShape;
		
		/*mesh.get(0).getV
		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(
				mesh.get(0).getTriangleCount(), mesh.get(0).getIndexBuffer(), 4 * 3,
				mesh.get(0).getVertexCount(), mesh.get(0).getVertexBuffer(mesh.get(0).getCenterPointRelativeToParent()), 4 * 3);*/
		
		//Get VertexArray for Collision 
				
		//GImpactMeshShape trimesh = new GImpactMeshShape(indexVertexArrays);
		
		//trimesh.updateBound();
		
		//returnShape = trimesh;
		
		// register algorithm
			
			
		
	}
	
	//ADDTOMT4J
	/**
	 * returns the Vertex Buffer for the Collision World
	 * @return Vertex Buffer
	 */
	public ByteBuffer getVertexBuffer(MTTriangleMesh mesh) 
	{
		
		Vertex[] vertices = mesh.getGeometryInfo().getVertices();

	ByteBuffer buf = ByteBuffer.allocateDirect(vertices.length*4*3).order(ByteOrder.nativeOrder());
		for (int i=0; i<vertices.length; i++) {
			
			//vertices[i].scaleLocal(scale);
			buf.putFloat(vertices[i].x); 
			buf.putFloat(vertices[i].y);
			buf.putFloat(vertices[i].z);
		}
		buf.flip();
		return buf;
	}

	
	//ADDTOMT4J
	/**
	 * returns the Index Buffer for the Collision World
	 * @return
	 */
	public ByteBuffer getIndexBuffer(MTTriangleMesh mesh)
	{
		short[] indices = mesh.getGeometryInfo().getIndices();
		ByteBuffer buf = ByteBuffer.allocateDirect(indices.length*4).order(ByteOrder.nativeOrder());
		
		for (int i=0; i<indices.length; i++) {			
			buf.putInt(indices[i]);			
		}
		buf.flip();
		return buf;
	}
	
	
	public void initCollisionWorld()
	{
		collisionConfiguration = new DefaultCollisionConfiguration();
		
		// use the default collision dispatcher. For parallel processing you
		// can use a diffent dispatcher (see Extras/BulletMultiThreaded)
	   dispatcher = new CollisionDispatcher(
				collisionConfiguration);
	  
		// the maximum size of the collision world. Make sure objects stay
		// within these boundaries
		// Don't make the world AABB size too large, it will harm simulation
		// quality and performance
		Vector3f worldAabbMin = new Vector3f(-50000, -50000, -50000);
		Vector3f worldAabbMax = new Vector3f(50000, 50000, 50000);
		int maxProxies = 1024;
		overlappingPairCache = 	new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
		//BroadphaseInterface overlappingPairCache = new SimpleBroadphase(
		//		maxProxies);

		// the default constraint solver. For parallel processing you can use a
		// different solver (see Extras/BulletMultiThreaded)
		

		collisionWorld = new CollisionWorld(dispatcher,overlappingPairCache,collisionConfiguration);
		
	}	
	
	public void removeObjectFromCollisionDomain(MTComponent group)
	{
		if(collisionGroups.containsKey(group))
		{
			collisionGroups.remove(group);
		}
		
		ArrayList<CollisionObject> colObjs = this.getAllObjectsForCollisionGroup(group);
		Iterator<CollisionObject> iterColObjs = colObjs.iterator();
		
		while(iterColObjs.hasNext())
		{
			collisionWorld.removeCollisionObject(iterColObjs.next());
		}
		
		if(colObjectsForGroup.containsKey(group))
		{
			this.colObjectsForGroup.remove(group);
		}		
	}
	
	public void addClusterToCollisionDomain(Cluster3DExt cluster)
	{
		ArrayList<CollisionObject> colObjs = new ArrayList<CollisionObject>();
		
		for(int i=0;i<cluster.getChildren().length;i++)
		{
			colObjs.addAll(this.getAllObjectsForCollisionGroup(cluster.getChildren()[i]));//save all collision objects in on object
			removeObjectFromCollisionDomain(cluster.getChildren()[i]);//remove current object from collision world		
		}
		
		groupId = (short)(groupId<<1);//shift groupId so every group has a unique bit value
		
		for(int i=0;i<colObjs.size();i++)
		{			
			collisionWorld.addCollisionObject(colObjs.get(i),groupId,(short)~groupId);			
		}
		colObjectsForGroup.put(cluster, colObjs);
		
	}
	
	public void removeClusterFromCollisionDomain(Cluster3DExt cluster)
	{
		ArrayList<CollisionObject> colObjs = new ArrayList<CollisionObject>();
		
	}
	
	public void addMeshToCollisionGroup(MTComponent group,MTTriangleMesh mesh, Vector3D translate)
	{
		if(!collisionGroups.containsKey(group))
		{
			collisionGroups.put(group,new ArrayList<MTTriangleMesh>());			
		}
		collisionGroups.get(group).add(mesh);
	}
	
	
	public void addObjectsToCollisionDomain()
	{
		Iterator<Entry<MTComponent, ArrayList<MTTriangleMesh>>> groupIter = collisionGroups.entrySet().iterator();
		
		
		int collidesWith = 0;
		while(groupIter.hasNext())
		{
			collidesWith = ~groupId;
			
			Entry<MTComponent, ArrayList<MTTriangleMesh>> element = groupIter.next();
					
			//CollisionShape shape = createMeshShapeFromMTMeshTriangle(element.getValue());
			Iterator<MTTriangleMesh> iter = element.getValue().iterator();
			
			while(iter.hasNext())
			{
				MTComponent comp = iter.next();
				CollisionShape shape = createMeshShapeFromMTMeshTriangle((MTTriangleMesh)comp);
				
			/*	Transform startTransform = new Transform();
				startTransform.setIdentity();
							
				Vector3f vec = new Vector3f();
				Vector3D translate = currentMesh.getCenterPointRelativeToParent();
				vec.x = translate.x;
				vec.y = translate.y;
				vec.z = translate.z;
							
				startTransform.origin.set(vec);*/
			
				Transform startTransform = new Transform();
				startTransform.setIdentity();
				Matrix mat = element.getKey().getGlobalMatrix();
								
				Vector3f vec = new Vector3f(0.0f,0.0f,0.0f);
				vec.x = mat.m03;
				vec.y = mat.m13;
				vec.z = mat.m23;
			
				//startTransform.transform(vec);
				//Matrix4f mat = CollisionManager.convertMT4JMatrixToMatrix4f(mesh.get);
							
				//startTransform.set(mat);
				//startTransform.origin.set(vec);
				//mat4f.m03 = mat4f.m03 + vec.x;
				//mat4f.m13 = mat4f.m13 + vec.y;
				//mat4f.m23 = mat4f.m23 + vec.z;
			   // mat4f.setTranslation(vec);
				
				startTransform.origin.set(vec);
				
				Vector3f scale = new Vector3f(); //get scale value of global matrix
				
				Vector3D xVec = new Vector3D(mat.m00,mat.m01,mat.m02);
				Vector3D yVec = new Vector3D(mat.m10,mat.m11,mat.m12);
				Vector3D zVec = new Vector3D(mat.m20,mat.m21,mat.m22);
				
				scale.x = xVec.length();
				scale.y = yVec.length();
				scale.z = zVec.length();
				
				float[] scaleVals = new float[3];
				scale.get(scaleVals);
				
				for(int i=0;i<3;i++)//get rotation value by extracting scalation
				{
				
					try {
						float[] colvals = mat.getRow(i);
					
						for(int j=0;j<3;j++)
						{
							colvals[j] = colvals[j] / scaleVals[i];
						}
						startTransform.basis.setRow(i,colvals);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//startTransform.set(mat4f);
				
				float mass = 5f;//fake mass value only needed correctly if used with dynamic engine
				shape.setLocalScaling(scale);
				GImpactMeshShape sh = (GImpactMeshShape)shape;
				sh.updateBound();			
				
				RigidBody body = localCreateRigidBody(mass, startTransform, shape);
				
				//get Center Of Mass of Triangle Mesh and write it to MTTriangleMesh
				Vector3f vecCOM = new Vector3f();
				body.getCenterOfMassPosition(vecCOM);
				Vector3D vecComMT4J = new Vector3D(vecCOM.x,vecCOM.y,vecCOM.z);
				//mesh.setCenterOfMass(vecComMT4J);
				//mesh.setMass(100.0f);
				//add Object to Collision World
				
				collisionWorld.addCollisionObject(body,groupId,(short)collidesWith);
				
				colObjectToComponent.put(body,comp);
				
				addCollisionObjectToGroup(comp.getParent(), body);//save association between collision objects and groups
			
			}
			groupId = (short)(groupId<<1);//shift groupId so every group has a unique bit value 
		}
		SimulatePreDrawAction calcDynamics = new SimulatePreDrawAction(collisionWorld,this,scene.getCanvas());
		calcDynamics.setCurrentTimeStep(1.f/1000000000000.0f);
		scene.registerPreDrawAction(calcDynamics);
		
		
		
	}
	
	private void addCollisionObjectToGroup(MTComponent comp,CollisionObject obj)
	{
		if(colObjectsForGroup.containsKey(comp))
		{
			ArrayList<CollisionObject> colObjs = colObjectsForGroup.get(comp);
			if(!colObjs.contains(obj))
			{
				colObjs.add(obj);
			}
		}else
		{
			ArrayList<CollisionObject> colObjs = new ArrayList<CollisionObject>();
			colObjs.add(obj);
			colObjectsForGroup.put(comp, colObjs);
		}
		
	}
	
	/*public void addObjectsToCollisionDomain(MTTriangleMesh mesh,Vector3D translate)
	{
			CollisionShape shape = createMeshShapeFromMTMeshTriangle(mesh);
			
			
			// create a dynamic rigidbody
			float mass = 4f;
			
			Transform startTransform = new Transform();
			startTransform.setIdentity();
						
			Vector3f vec = new Vector3f();
			vec.x = translate.x;
			vec.y = translate.y;
			vec.z = translate.z;
						
			startTransform.origin.set(vec);
			
			CompoundShape cshape = new CompoundShape();
			cshape.addChildShape(startTransform,shape);
			
			RigidBody body = localCreateRigidBody(mass, startTransform, cshape);
			
			Transform tfOut = new Transform();
			body.getWorldTransform(tfOut);
			Matrix4f matOut = new Matrix4f();			
			tfOut.getMatrix(matOut);
		
			//get Center Of Mass of Triangle Mesh and write it to MTTriangleMesh
			Vector3f vecCOM = new Vector3f();
			body.getCenterOfMassPosition(vecCOM);
			Vector3D vecComMT4J = new Vector3D(vecCOM.x,vecCOM.y,vecCOM.z);
			mesh.setCenterOfMass(vecComMT4J);
			mesh.setMass(100.0f);
			//add Object to Collision World			
			collisionWorld.addCollisionObject(body);
			
			colObjectToComponent.put(body,mesh);
			SimulatePreDrawAction calcDynamics = new SimulatePreDrawAction(collisionWorld,this);
			calcDynamics.setCurrentTimeStep(1.f/1000000000000.0f);
			scene.registerPreDrawAction(calcDynamics);
			
		
	}*/
	
	private RigidBody localCreateRigidBody(float mass, Transform startTransform, CollisionShape shape) {
		// rigidbody is dynamic if and only if mass is non zero, otherwise static
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f(0f, 0f, 0f);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}

		// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects

		//#define USE_MOTIONSTATE 1
		//#ifdef USE_MOTIONSTATE
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		
		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);
		
		RigidBody body = new RigidBody(cInfo);
		
		return body;
	}
	
	public static Matrix convertMatrix4fToMT4JMatrix(Matrix4f matrix)
	{
		Matrix mat = new Matrix();
		for(int i=0;i<4;i++)
		{
			float[] col = new float[4];
			matrix.getColumn(i, col);
			try {
				mat.setColumn(i, col);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		return mat;
	}
	
	public static Matrix4f convertMT4JMatrixToMatrix4f(Matrix matrix)
	{
		Matrix4f mat = new Matrix4f();
		for(int i=0;i<4;i++)
		{			
			float[] col;
			try {
				col = matrix.getColumn(i);
				
				try {
					mat.setColumn(i, col);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		return mat;
	}
	
	public MTComponent getAssociatedComponent(CollisionObject obj)
	{
		if(colObjectToComponent.containsKey(obj))
		{
			return colObjectToComponent.get(obj);
		}
		return null;
	}
	
	public ArrayList<CollisionObject> getAllObjectsForCollisionGroup(MTComponent comp)
	{
		return colObjectsForGroup.get(comp);
	}
	
	public Set<MTComponent> getAllCollisionGroups()
	{
		return collisionGroups.keySet();
	}
	
}
