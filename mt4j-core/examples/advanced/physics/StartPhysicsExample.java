package advanced.physics;

import org.mt4j.MTApplication;

import advanced.physics.scenes.PhysicsScene;

public class StartPhysicsExample extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		initialize();
	}
	
	@Override
	public void startUp() {
		addScene(new PhysicsScene(this, "Physics Example Scene"));
	}

}
