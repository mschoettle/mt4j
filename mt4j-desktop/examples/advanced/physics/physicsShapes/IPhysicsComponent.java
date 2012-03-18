package advanced.physics.physicsShapes;

import org.jbox2d.dynamics.Body;
import org.mt4j.util.math.Vector3D;

public interface IPhysicsComponent{
	public void setCenterRotation(float angle);
	public void setPositionGlobal(Vector3D centerPoint);
	public Body getBody();
}
