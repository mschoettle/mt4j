package advanced.physics.physicsShapes;

import java.util.List;

import javax.media.opengl.glu.GLU;

import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GluTrianglulator;

import processing.core.PApplet;

public class PhysicsPolygon extends MTTriangleMesh implements IPhysicsComponent {
	private float angle;
	private World world;
	private Body body;
	private float density;
	private float friction;
	private float restituion;
	
	public PhysicsPolygon(Vertex[] vertices, Vector3D position, PApplet applet,
			World world, float density, float friction, float restitution, float worldScale
	) {
		super(applet, new GeometryInfo(applet, new Vertex[]{}), false);
		this.angle = 0;
		this.world = world;
		this.density = density;
		this.friction = friction;
		this.restituion = restitution;
		
		this.setGestureAllowance(ScaleProcessor.class, false);
		this.setGestureAllowance(RotateProcessor.class, false);
		
		Vertex.scaleVectorArray(vertices, Vector3D.ZERO_VECTOR, 1f/worldScale, 1f/worldScale, 1);
		
		position.scaleLocal(1f/worldScale); //FIXME REALLY?
		
    	GluTrianglulator triangulator = new GluTrianglulator(applet);
		List<Vertex> physicsTris = triangulator.tesselate(vertices, GLU.GLU_TESS_WINDING_NONZERO);
		Vertex[] triangulatedBodyVerts = physicsTris.toArray(new Vertex[physicsTris.size()]);
		triangulator.deleteTess();
		//Set the triangulated vertices as the polygons (mesh's) vertices
		this.setGeometryInfo(new GeometryInfo(applet, triangulatedBodyVerts));
		
		this.translate(position);
		Vector3D realBodyCenter = this.getCenterPointGlobal(); //FIXME geht nur if detached from world //rename futurebodycenter?
		//Reset position
		this.translate(position.getScaled(-1));
		
		//Now get the position where the global center will be after setting the shape at the desired position
		this.setPositionGlobal(position);
		Vector3D meshCenterAtPosition = this.getCenterPointGlobal();
		
		//Compute the distance we would have to move the vertices for the body creation
		//so that the body.position(center) is at the same position as our mesh center
		Vector3D realBodyCenterToMeshCenter = meshCenterAtPosition.getSubtracted(realBodyCenter);
		//System.out.println("Diff:" +  realBodyCenterToMeshCenter);
		
		//Move the vertices so the body position is at the center of the shape 
		Vertex.translateVectorArray(triangulatedBodyVerts, realBodyCenterToMeshCenter);
		
		this.setGeometryInfo(new GeometryInfo(applet, triangulatedBodyVerts));
		
//		MTPolygon p = new MTPolygon(vertices, applet);
//		p.translate(position);
////		p.setPositionGlobal(position);
//		Vector3D realBodyCenter = p.getCenterPointGlobal(); //FIXME geht nur if detached from world //rename futurebodycenter?
//		//Reset position
//		p.translate(position.getScaled(-1));
//		
//		//Now get the position where the global center will be after setting the shape at the desired position
//		p.setPositionGlobal(position);
//		Vector3D meshCenterAtPosition = p.getCenterPointGlobal();
//		
//		//Compute the distance we would have to move the vertices for the body creation
//		//so that the body.position(center) is at the same position as our mesh center
//		Vector3D realBodyCenterToMeshCenter = meshCenterAtPosition.getSubtracted(realBodyCenter);
//		//System.out.println("Diff:" +  realBodyCenterToMeshCenter);
//		
//		//Move the vertices so the body position is at the center of the shape 
//		Vertex.translateVectorArray(vertices, realBodyCenterToMeshCenter);
		
		Vertex.translateVectorArray(vertices, realBodyCenterToMeshCenter);
		
		//Create vertex structure for creation of decomposition polygon (use the translated vertices)
		float xArr[] = new float[vertices.length];
		float yArr[] = new float[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			Vertex v = vertices[i];
			xArr[i] = v.x;
			yArr[i] = v.y;
		}
		//Create a polygon too see if its simple and eventually decompose it
		org.jbox2d.util.nonconvex.Polygon myPoly = new org.jbox2d.util.nonconvex.Polygon(xArr, yArr);
		
		PolygonDef pd = new PolygonDef();
    	if (density != 0.0f){
    		pd.density 		= density;
    		pd.friction 	= friction;
    		pd.restitution 	= restituion;
		}
    	
		//Create polygon body
		BodyDef dymBodyDef = new BodyDef();
//		dymBodyDef.position = new Vec2(position.x /worldScale, position.y /worldScale);
		dymBodyDef.position = new Vec2(position.x , position.y );
		this.bodyDefB4CreationCallback(dymBodyDef);
		this.body = world.createBody(dymBodyDef);
		
//    	GluTrianglulator triangulator = new GluTrianglulator(applet);
//		List<Vertex> physicsTris = triangulator.tesselate(vertices, GLU.GLU_TESS_WINDING_NONZERO);
//		Vertex[] triangulatedBodyVerts = physicsTris.toArray(new Vertex[physicsTris.size()]);
//		triangulator.deleteTess();
		
//		//Set the triangulated vertices as the polygons (mesh's) vertices
//		this.setGeometryInfo(new GeometryInfo(applet, triangulatedBodyVerts));
		
		//Decompose poly and add verts to phys body if possible
    	int success = org.jbox2d.util.nonconvex.Polygon.decomposeConvexAndAddTo(myPoly, this.body, pd); 
    	
    	if (success != -1){
    		System.out.println("-> Ear clipping SUCCESSFUL -> Using triangulated and polygonized shape for b2d.");
    		this.body.setMassFromShapes();
    		this.body.setUserData(this);
	    	this.setUserData("box2d", this.body); //TODO rename userData
			//Performance hit! but prevents object from sticking to another sometimes
//			theBody.setBullet(true);
    	}else{
    		System.out.println("-> Ear clipping had an ERROR - trying again by triangulating shape for b2d with GLU-Triangulator");
//    		GluTrianglulator triangulator = new GluTrianglulator(app);
//    		List<Vertex> physicsTris = triangulator.tesselate(bodyVerts, GLU.GLU_TESS_WINDING_NONZERO);
//    		Vertex[] triangulatedBodyVerts = physicsTris.toArray(new Vertex[physicsTris.size()]);
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
    		world.destroyBody(this.body);
    		dymBodyDef = new BodyDef();
    		dymBodyDef.position = new Vec2(position.x, position.y);
    		this.bodyDefB4CreationCallback(dymBodyDef);
    		this.body = world.createBody(dymBodyDef);
    		for (int i = 0; i < triangulatedBodyVerts.length/3; i++) {
    			//Create polygon definition
    			PolygonDef polyDef = new PolygonDef();
    			if (density != 0.0f){
    				polyDef.density 		= density;
    				polyDef.friction 		= friction;
    				polyDef.restitution 	= restituion;
    			}
    			this.polyDefB4CreationCallback(polyDef); //FIXME TEST
    			
    			//Add triangle vertices
    			Vertex vertex1 = triangulatedBodyVerts[i*3];
    			Vertex vertex2 = triangulatedBodyVerts[i*3+1];
    			Vertex vertex3 = triangulatedBodyVerts[i*3+2];
    			polyDef.addVertex(new Vec2(vertex1.x, vertex1.y));
    			polyDef.addVertex(new Vec2(vertex2.x, vertex2.y));
    			polyDef.addVertex(new Vec2(vertex3.x, vertex3.y));
    			//Add poly to body
    			this.body.createShape(polyDef);
    		}
    		this.body.setMassFromShapes();
    		//FIXME TEST - performance hit!?
    		//theBody.setBullet(true);
    		this.body.setUserData(this);
    		this.setUserData("box2d", this.body); //TODO rename userData
    	}
		
//    	p.destroy();
	}
	
	
	protected void polyDefB4CreationCallback(PolygonDef def){
		
	}
	
	protected void bodyDefB4CreationCallback(BodyDef def){
		
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
		Object o = this.getUserData("box2d");
		if (o != null && o instanceof Body){ 
			Body box2dBody = (Body)o;
			boolean exists = false;
			for (Body body = world.getBodyList(); body != null; body = body.getNext()) {
				if (body.equals(this.body))
					exists = true;//Delete later to avoid concurrent modification
			}
			if (exists)
				box2dBody.getWorld().destroyBody(box2dBody);
		}
		super.destroyComponent();
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
