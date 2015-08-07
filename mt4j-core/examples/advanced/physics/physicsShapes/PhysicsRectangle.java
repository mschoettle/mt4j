package advanced.physics.physicsShapes;

import java.util.List;

import javax.media.opengl.glu.GLU;

import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.bounds.BoundsArbitraryPlanarPolygon;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GluTrianglulator;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import advanced.physics.util.PhysicsHelper;

public class PhysicsRectangle extends MTRectangle implements IPhysicsComponent{
	private float angle;
	private boolean drawBounds;
	
	private World world;
	private Body body;
	private float density;
	private float friction;
	private float restituion;
	
	/*
	PhysicsRectangle rect = new PhysicsRectangle(new Vector3D(400,200), 100,150, app, world, 0.6f, 0.4f, 0.2f, scale);
	rect.setNoStroke(true);
	PhysicsHelper.addDragJoint(world, rect, rect.getBody().isDynamic(), scale);
	physObjGroup.addChild(rect);
	*/
	
	public PhysicsRectangle(
			Vector3D centerPosition, 
			float width, float height, PApplet applet,
			World world, float density, float friction, float restitution, float scale
	) {
		super(applet, 0, 0, PhysicsHelper.scaleDown(width, scale), PhysicsHelper.scaleDown(height, scale));
		this.angle = 0;
		this.drawBounds = false;
		this.world = world;
		this.density = density;
		this.friction = friction;
		this.restituion = restitution;
		
		Vector3D scaledPos = PhysicsHelper.scaleDown(centerPosition.getCopy(), scale);
//		PhysicsHelper.scaleDown(centerPosition, scale);
//		centerPosition.scaleLocal(1f/scale);
		
		BodyDef dymBodyDef = new BodyDef();
//		dymBodyDef.position = new Vec2(pos.x, pos.y);
		dymBodyDef.position = new Vec2(scaledPos.x, scaledPos.y);//FIXME WORKS?
		this.bodyDefB4CreationCallback(dymBodyDef);
		this.body = world.createBody(dymBodyDef);
		
		PolygonDef dymShapeDef = new PolygonDef();
		dymShapeDef.setAsBox( (width/2f)/scale, (height/2f)/scale);
//		dymShapeDef.setAsBox( (width/2f), (height/2f));
		if (density != 0.0f){
			dymShapeDef.density 		= density;
			dymShapeDef.friction 		= friction;
			dymShapeDef.restitution 	= restitution;
		}
		
		this.polyDefB4CreationCallback(dymShapeDef); //FIXME TEST
		
		body.createShape(dymShapeDef);
		body.setMassFromShapes();
		
		//TEST
//		theBody.setBullet(true);
		
		this.setPositionGlobal(scaledPos); 
		body.setUserData(this);
		this.setUserData("box2d", body); 
		this.setGestureAllowance(ScaleProcessor.class, false);
		this.setGestureAllowance(RotateProcessor.class, false);
	}
	
	
	
	
	public PhysicsRectangle(PImage texture, 
			Vector3D pos, 
			PApplet applet,
			Vertex[] physicsVertices,
			World world, float density, float friction, float restitution, float scale
	) {
		super(applet, texture);
		this.angle = 0;
		this.drawBounds = false;
		this.world = world;
		this.density = density;
		this.friction = friction;
		this.restituion = restitution;
		
		this.setGestureAllowance(ScaleProcessor.class, false);
		this.setGestureAllowance(RotateProcessor.class, false);
		
		//Scale shape vertices
//		this.setGeometryInfo(new GeometryInfo(applet, Vertex.scaleVectorArray(this.getGeometryInfo().getVertices(), Vector3D.ZERO_VECTOR, 1f/scale, 1f/scale, 1)));
//		this.setGeometryInfo(new GeometryInfo(applet, PhysicsHelper.scaleDown(this.getGeometryInfo().getVertices(), scale)));
		this.setGeometryInfo(new GeometryInfo(applet, PhysicsHelper.scaleDown(Vertex.getDeepVertexArrayCopy(this.getGeometryInfo().getVertices()), scale)));
		
		//Scale physics vertics
//		Vertex.scaleVectorArray(bodyVerts, Vector3D.ZERO_VECTOR, 1f/scale, 1f/scale, 1);
		PhysicsHelper.scaleDown(physicsVertices, scale);
		
		Vector3D scaledPos = PhysicsHelper.scaleDown(pos.getCopy(), scale);
		//Scale position
//		pos.scaleLocal(1f/scale); //FIXME REALLY?
//		PhysicsHelper.scaleDown(pos, scale);
		
		this.setGestureAllowance(ScaleProcessor.class, false);
		this.setGestureAllowance(RotateProcessor.class, false);
		
		//Temporarily move the mesh so that we know where the calculated center of the body
		//would be (the body takes the body.position as the center reference instead of a calculated center) 
//		//FIXME welchen centerpoint nehmen? -> kommt auch drauf an ob shape schon auf canvas war!?
		//We have to do this because the anchor point ("position") of the pyhsics shape is the body.position
		//but the anchor point of our shapes is the point returned from getCenterpoint..()
		this.translate(scaledPos);
		Vector3D realBodyCenter = this.getCenterPointGlobal(); //FIXME geht nur if detached from world //rename futurebodycenter?
		//Reset position
		this.translate(scaledPos.getScaled(-1));
		
		//Now get the position where the global center will be after setting the shape at the desired position
		this.setPositionGlobal(scaledPos);
		Vector3D meshCenterAtPosition = this.getCenterPointGlobal();

		//Compute the distance we would have to move the vertices for the body creation
		//so that the body.position(center) is at the same position as our mesh center
		Vector3D realBodyCenterToMeshCenter = meshCenterAtPosition.getSubtracted(realBodyCenter);
		//System.out.println("Diff:" +  realBodyCenterToMeshCenter);
		
		//FIXME TEST Needed for level saving the local vertices at the right position 
		this.setUserData("realBodyCenterToMeshCenter", realBodyCenterToMeshCenter);

		//Move the vertices so the body position is at the center of the shape 
		Vertex.translateVectorArray(physicsVertices, realBodyCenterToMeshCenter);

		//Create vertex structure for creation of decomposition polygon (use the translated vertices)
		float xArr[] = new float[physicsVertices.length];
		float yArr[] = new float[physicsVertices.length];
		for (int i = 0; i < physicsVertices.length; i++) {
			Vertex v = physicsVertices[i];
			xArr[i] = v.x;
			yArr[i] = v.y;
		}

		//Create a polygon too see if its simple and eventually decompose it
		org.jbox2d.util.nonconvex.Polygon myPoly = new org.jbox2d.util.nonconvex.Polygon(xArr, yArr);

		//System.out.println("Polygon is simple! -> Using convex decomposition for physics shape and glu triangulated mesh for display!");
		PolygonDef pd = new PolygonDef();
		if (density != 0.0f){
			pd.density 		= density;
			pd.friction 	= friction;
			pd.restitution 	= restituion;
		}

		//Create polygon body
		BodyDef dymBodyDef = new BodyDef();
		dymBodyDef.position = new Vec2(scaledPos.x, scaledPos.y);
		this.bodyDefB4CreationCallback(dymBodyDef);
		this.body = world.createBody(dymBodyDef);

		this.polyDefB4CreationCallback(pd); //FIXME TEST
		
		int success = org.jbox2d.util.nonconvex.Polygon.decomposeConvexAndAddTo(myPoly, body, pd); 
		if (success != -1){
			System.out.println("-> Ear clipping SUCCESSFUL -> Using triangulated and polygonized shape for b2d.");
			body.setMassFromShapes();
			body.setUserData(this);
			this.setUserData("box2d", body); 
			//Performance hit! but prevents object from sticking to another sometimes
//			theBody.setBullet(true);
		}else{
			System.out.println("-> Ear clipping had an ERROR - trying again by triangulating shape for b2d with GLU-Triangulator");
			GluTrianglulator triangulator = new GluTrianglulator(applet);
			List<Vertex> physicsTris = triangulator.tesselate(physicsVertices, GLU.GLU_TESS_WINDING_NONZERO);
			Vertex[] triangulatedBodyVerts = physicsTris.toArray(new Vertex[physicsTris.size()]);
			//System.out.println("GLU tris created: " + triangulatedBodyVerts.length);

			//Cap the max triangles - dont use anymore triangles for the physics body..
			int cap = 400;
			if (triangulatedBodyVerts.length > cap){
				//System.err.println("OVER cap! -> capping!");
				Vertex[] tmp = new Vertex[cap];
				System.arraycopy(triangulatedBodyVerts, 0, tmp, 0, cap);
				triangulatedBodyVerts = tmp;
			}

			//Create polygon body
			world.destroyBody(body);
			dymBodyDef = new BodyDef();
			dymBodyDef.position = new Vec2(scaledPos.x, scaledPos.y);
			this.bodyDefB4CreationCallback(dymBodyDef);
			body = world.createBody(dymBodyDef);
			for (int i = 0; i < triangulatedBodyVerts.length/3; i++) {
				//Create polygon definition
				PolygonDef polyDef = new PolygonDef();
				if (density != 0.0f){
					polyDef.density 		= density;
					polyDef.friction 		= friction;
					polyDef.restitution 	= restituion;
				}
				//Add triangle vertices
				Vertex vertex1 = triangulatedBodyVerts[i*3];
				Vertex vertex2 = triangulatedBodyVerts[i*3+1];
				Vertex vertex3 = triangulatedBodyVerts[i*3+2];
				polyDef.addVertex(new Vec2(vertex1.x, vertex1.y));
				polyDef.addVertex(new Vec2(vertex2.x, vertex2.y));
				polyDef.addVertex(new Vec2(vertex3.x, vertex3.y));
				
				this.polyDefB4CreationCallback(pd); //FIXME TEST
				
				//Add poly to body
				body.createShape(polyDef);
			}
			body.setMassFromShapes();
			//performance hit!?
			//theBody.setBullet(true);
			body.setUserData(this);
			this.setUserData("box2d", body); 
			triangulator.deleteTess();
		}
		

	}

	
	
	public void setPhysicsVertices(Vertex[] bodyVerts, 
			Vector3D pos, 
			float scale
		){
		if (this.body != null && this.world != null){
			world.destroyBody(this.body);
		}
		
		//Scale physics vertics
//		Vertex.scaleVectorArray(bodyVerts, Vector3D.ZERO_VECTOR, 1f/scale, 1f/scale, 1);
		PhysicsHelper.scaleDown(bodyVerts, scale);
		
		//Scale position
//		pos.scaleLocal(1f/scale); //FIXME REALLY?
//		PhysicsHelper.scaleDown(pos, scale);
		Vector3D scaledPos = PhysicsHelper.scaleDown(pos.getCopy(), scale);
		
		MTComponent parent = this.getParent();
		this.removeFromParent();
		
		//Temporarily move the mesh so that we know where the calculated center of the body
		//would be (the body takes the body.position as the center reference instead of a calculated center) 
//		//FIXME welchen centerpoint nehmen? -> kommt auch drauf an ob shape schon auf canvas war!?
		//We have to do this because the anchor point ("position") of the pyhsics shape is the body.position
		//but the anchor point of our shapes is the point returned from getCenterpoint..()
		this.translate(scaledPos);
		Vector3D realBodyCenter = this.getCenterPointGlobal(); //FIXME geht nur if detached from world //rename futurebodycenter?
		//Reset position
		this.translate(scaledPos.getScaled(-1));
		
		//Now get the position where the global center will be after setting the shape at the desired position
		this.setPositionGlobal(scaledPos);
		Vector3D meshCenterAtPosition = this.getCenterPointGlobal();
		
		if (parent != null){
			parent.addChild(this);//TODO add at same index
		}

		//Compute the distance we would have to move the vertices for the body creation
		//so that the body.position(center) is at the same position as our mesh center
		Vector3D realBodyCenterToMeshCenter = meshCenterAtPosition.getSubtracted(realBodyCenter);
		//System.out.println("Diff:" +  realBodyCenterToMeshCenter);

		//Move the vertices so the body position is at the center of the shape 
		Vertex.translateVectorArray(bodyVerts, realBodyCenterToMeshCenter);
		
		//FIXME TEST
		this.setUserData("realBodyCenterToMeshCenter", realBodyCenterToMeshCenter);

		//Create vertex structure for creation of decomposition polygon (use the translated vertices)
		float xArr[] = new float[bodyVerts.length];
		float yArr[] = new float[bodyVerts.length];
		for (int i = 0; i < bodyVerts.length; i++) {
			Vertex v = bodyVerts[i];
			xArr[i] = v.x;
			yArr[i] = v.y;
		}

		//Create a polygon too see if its simple and eventually decompose it
		org.jbox2d.util.nonconvex.Polygon myPoly = new org.jbox2d.util.nonconvex.Polygon(xArr, yArr);

		//System.out.println("Polygon is simple! -> Using convex decomposition for physics shape and glu triangulated mesh for display!");
		PolygonDef pd = new PolygonDef();
		if (density != 0.0f){
			pd.density 		= density;
			pd.friction 	= friction;
			pd.restitution 	= restituion;
		}

		//Create polygon body
		BodyDef dymBodyDef = new BodyDef();
		dymBodyDef.position = new Vec2(scaledPos.x, scaledPos.y);
		this.bodyDefB4CreationCallback(dymBodyDef);
		this.body = world.createBody(dymBodyDef);
		
		this.polyDefB4CreationCallback(pd); //FIXME TEST
		
		int success = org.jbox2d.util.nonconvex.Polygon.decomposeConvexAndAddTo(myPoly, body, pd); 
		if (success != -1){
			System.out.println("-> Ear clipping SUCCESSFUL -> Using triangulated and polygonized shape for b2d.");
			body.setMassFromShapes();
			body.setUserData(this);
			this.setUserData("box2d", body); 
			//Performance hit! but prevents object from sticking to another sometimes
//			theBody.setBullet(true);
		}else{
			System.out.println("-> Ear clipping had an ERROR - trying again by triangulating shape for b2d with GLU-Triangulator");
			GluTrianglulator triangulator = new GluTrianglulator(this.getRenderer());
			List<Vertex> physicsTris = triangulator.tesselate(bodyVerts, GLU.GLU_TESS_WINDING_NONZERO);
			Vertex[] triangulatedBodyVerts = physicsTris.toArray(new Vertex[physicsTris.size()]);
			//System.out.println("GLU tris created: " + triangulatedBodyVerts.length);

			//Cap the max triangles - dont use anymore triangles for the physics body..
			int cap = 400;
			if (triangulatedBodyVerts.length > cap){
				//System.err.println("OVER cap! -> capping!");
				Vertex[] tmp = new Vertex[cap];
				System.arraycopy(triangulatedBodyVerts, 0, tmp, 0, cap);
				triangulatedBodyVerts = tmp;
			}

			//Create polygon body
			world.destroyBody(body);
			dymBodyDef = new BodyDef();
			dymBodyDef.position = new Vec2(scaledPos.x, scaledPos.y);
			this.bodyDefB4CreationCallback(dymBodyDef);
			body = world.createBody(dymBodyDef);
			for (int i = 0; i < triangulatedBodyVerts.length/3; i++) {
				//Create polygon definition
				PolygonDef polyDef = new PolygonDef();
				if (density != 0.0f){
					polyDef.density 		= density;
					polyDef.friction 		= friction;
					polyDef.restitution 	= restituion;
				}
				//Add triangle vertices
				Vertex vertex1 = triangulatedBodyVerts[i*3];
				Vertex vertex2 = triangulatedBodyVerts[i*3+1];
				Vertex vertex3 = triangulatedBodyVerts[i*3+2];
				polyDef.addVertex(new Vec2(vertex1.x, vertex1.y));
				polyDef.addVertex(new Vec2(vertex2.x, vertex2.y));
				polyDef.addVertex(new Vec2(vertex3.x, vertex3.y));
				
				this.polyDefB4CreationCallback(polyDef); //FIXME TEST
				//Add poly to body
				body.createShape(polyDef);
			}
			body.setMassFromShapes();
			//FIXME TEST - performance hit!?
			//theBody.setBullet(true);
			body.setUserData(this);
			this.setUserData("box2d", body); 
			triangulator.deleteTess();
		}
	}
	
	protected void polyDefB4CreationCallback(PolygonDef def){
		
	}
	
	protected void bodyDefB4CreationCallback(BodyDef def){
		
	}
	
//	private Vertex[] physicsVertices;
//	
//	private Vertex[] getPhysicsVertices(){
//		return this.physicsVertices;
//	}
	
	
	
	
	//@Override
	public void drawComponent(PGraphics g) {
		super.drawComponent(g);

		if (drawBounds){
			IBoundingShape bounds = this.getBounds();
			if (bounds instanceof BoundsArbitraryPlanarPolygon){
				BoundsArbitraryPlanarPolygon bound = (BoundsArbitraryPlanarPolygon)bounds;

				Vector3D[] boundVecs = bound.getVectorsLocal();
//				app.noFill();
				g.fill(100);
				g.stroke(50);
				g.beginShape();
                for (Vector3D v : boundVecs) {
                    //					app.vertex(v.x*scale, v.y*scale, v.z);
                    g.vertex(v.x, v.y, v.z);
                }
				g.endShape();
			}
		}
	}
	
	//@Override
	public void rotateZGlobal(Vector3D rotationPoint, float degree) {
		angle += degree;
		super.rotateZGlobal(rotationPoint, degree);
	} 	

	public float getAngle() {
		return angle;
	}
	
	public void setCenterRotation(float angle){
		float degreeAngle = AbstractMTApplication.degrees(angle);
		float oldAngle = this.getAngle();
		float diff = degreeAngle-oldAngle;
		//System.out.println("Old angle: " + oldAngle + " new angle:" + degreeAngle + " diff->" +  diff);
		this.rotateZGlobal(this.getCenterPointGlobal(), diff);
	}
	
	//@Override
	protected void destroyComponent() {
		super.destroyComponent();
		boolean exists = false;
		for (Body body = world.getBodyList(); body != null; body = body.getNext()) {
			if (body.equals(this.body))
				exists = true;//Delete later to avoid concurrent modification
		}
		if (exists)
			world.destroyBody(body);
	}




	public World getWorld() {
		return world;
	}




	public Body getBody() {
		return body;
	}




	public float getDensity() {
		return density;
	}




	public float getFriction() {
		return friction;
	}




	public float getRestituion() {
		return restituion;
	}
	
	

}
