package advanced.physics.physicsShapes;

import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.mesh.MTSphere;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import advanced.physics.util.PhysicsHelper;

public class PhysicsSphere extends MTSphere implements IPhysicsComponent {
	
	private float angle;
	private World world;
	private Body body;
	private float density;
	private float friction;
	private float restituion;
	
	public PhysicsSphere(PApplet app, String name, int samples,
			int radialSamples, float radius, TextureMode texMode,
			Vector3D centerPosition,
			World world, float density, float friction, float restitution, float scale
	) {
		super(app, name, samples, radialSamples, PhysicsHelper.scaleDown(radius, scale), texMode);
		this.angle = 0;
		this.world = world;
		this.density = density;
		this.friction = friction;
		this.restituion = restitution;
		
		this.setGestureAllowance(ScaleProcessor.class, false);
		this.setGestureAllowance(RotateProcessor.class, false);
		
		Vector3D scaledPos = PhysicsHelper.scaleDown(centerPosition.getCopy(), scale);
		
		BodyDef dymBodyDef = new BodyDef();
		dymBodyDef.position = new Vec2(scaledPos.x, scaledPos.y);
		this.bodyDefB4CreationCallback(dymBodyDef);
		this.body = world.createBody(dymBodyDef);
		
		CircleDef circleDef = new CircleDef();
		circleDef.radius = radius/scale;
		if (density != 0.0f){
			circleDef.density 		= density;
			circleDef.friction 		= friction;
			circleDef.restitution 	= restituion;
		}
		this.circleDefB4CreationCallback(circleDef);
		this.body.createShape(circleDef);
		this.body.setMassFromShapes();
		//FIXME TEST
		//theBody.setBullet(true);
		
		this.setPositionGlobal(scaledPos);
		this.body.setUserData(this);
		this.setUserData("box2d", this.body); 
		this.setMaterial(PhysicsHelper.createDefaultGLMaterial(app));
		
//		this.rotateY(this.getCenterPointRelativeToParent(), 90);
//		this.rotateX(this.getCenterPointRelativeToParent(), 180);
		
	}
	
	
	protected void circleDefB4CreationCallback(CircleDef def){
		
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
