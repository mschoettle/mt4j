package advanced.physics.util;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.math.Vector3D;

import advanced.physics.physicsShapes.IPhysicsComponent;


public class UpdatePhysicsAction implements IPreDrawAction {
	private World world;
	private float timeStep;
	private int constraintIterations;
	private float scale;
	

	public UpdatePhysicsAction(World world, float timeStep,	int constraintIterations, float scale) {
		super();
		this.world = world;
		this.timeStep = timeStep;
		this.constraintIterations = constraintIterations;
		this.scale = scale;
	}

	public void processAction() {
		try{
			//Take a timestep in the physics world
			world.step(timeStep, constraintIterations);
			for (Body body = world.getBodyList(); body != null; body = body.getNext()) {
				if (!body.isSleeping()){
					Vec2 newPos		= body.getPosition();
					body.wakeUp();
					float newAngle 	= body.getAngle();
					if (body.getUserData() != null){
						if (body.getUserData() instanceof IPhysicsComponent){
							IPhysicsComponent shape = (IPhysicsComponent)body.getUserData();
							shape.setPositionGlobal(new Vector3D(newPos.x * scale, newPos.y * scale,0));
							shape.setCenterRotation(newAngle);
						}
					}
				}
			}
		}catch (Exception e) {
			System.err.println("Physics engine error during simulation - behaviour is now undefined!");
			e.printStackTrace();
		}
	}

	public boolean isLoop() {
		return true;
	}

}
