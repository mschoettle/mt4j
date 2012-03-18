package advanced.physics.scenes;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import advanced.physics.physicsShapes.PhysicsCircle;
import advanced.physics.physicsShapes.PhysicsPolygon;
import advanced.physics.physicsShapes.PhysicsRectangle;
import advanced.physics.util.PhysicsHelper;
import advanced.physics.util.UpdatePhysicsAction;

public class PhysicsScene extends AbstractScene {
	private float timeStep = 1.0f / 60.0f;
	private int constraintIterations = 10;
	
	/** THE CANVAS SCALE **/
	private float scale = 20;
	private AbstractMTApplication app;
	private World world;
	
	private MTComponent physicsContainer;
	
	public PhysicsScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		
		float worldOffset = 10; //Make Physics world slightly bigger than screen borders
		//Physics world dimensions
		AABB worldAABB = new AABB(new Vec2(-worldOffset, -worldOffset), new Vec2((app.width)/scale + worldOffset, (app.height)/scale + worldOffset));
		Vec2 gravity = new Vec2(0, 10);
//		Vec2 gravity = new Vec2(0, 0);
		boolean sleep = true;
		//Create the pyhsics world
		this.world = new World(worldAABB, gravity, sleep);
		
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		
		//Update the positions of the components according the the physics simulation each frame
		this.registerPreDrawAction(new UpdatePhysicsAction(world, timeStep, constraintIterations, scale));
		
		physicsContainer = new MTComponent(app);
		//Scale the physics container. Physics calculations work best when the dimensions are small (about 0.1 - 10 units)
		//So we make the display of the container bigger and add in turn make our physics object smaller
		physicsContainer.scale(scale, scale, 1, Vector3D.ZERO_VECTOR);
		this.getCanvas().addChild(physicsContainer);
		
		//Create borders around the screen
		this.createScreenBorders(physicsContainer);
		
		//Createa circles
		for (int i = 0; i < 20; i++) {
			PhysicsCircle c = new PhysicsCircle(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, 0.4f, scale);
			MTColor col = new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
			c.setFillColor(col);
			c.setStrokeColor(col);
			PhysicsHelper.addDragJoint(world, c, c.getBody().isDynamic(), scale);
			physicsContainer.addChild(c);
		}
		
		//Create rectangle
		PhysicsRectangle physRect = new PhysicsRectangle(new Vector3D(100,300), 100, 50, app, world, 1f, 0.4f, 0.4f, scale);
		MTColor col = new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
		physRect.setFillColor(col);
		physRect.setStrokeColor(col);
		PhysicsHelper.addDragJoint(world, physRect, physRect.getBody().isDynamic(), scale);
		physicsContainer.addChild(physRect);
		
		//Create polygon
		Vertex[] polyVertices = new Vertex[]{
				new Vertex(-10,0,0),
				new Vertex(10,0,0),
				new Vertex(10,50,0),
				new Vertex(60,50,0),
				new Vertex(60,70,0),
				new Vertex(10,70,0),
				
				new Vertex(10,120,0),
				new Vertex(-10,120,0),
				new Vertex(-10,70,0),
				new Vertex(-60,70,0),
				new Vertex(-60,50,0),
				new Vertex(-10,50,0),
				new Vertex(-10,0,0),
		};
		PhysicsPolygon physPoly = new PhysicsPolygon(polyVertices, new Vector3D(250,250), app, world, 1.0f, 0.3f, 0.4f, scale);
		MTColor polyCol = new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
		physPoly.setFillColor(polyCol);
		physPoly.setStrokeColor(polyCol);
		PhysicsHelper.addDragJoint(world, physPoly, physPoly.getBody().isDynamic(), scale);
		//For an anti-aliased outline
		List<Vertex[]> contours = new ArrayList<Vertex[]>();
		contours.add(polyVertices);
		physPoly.setOutlineContours(contours);
		physPoly.setNoStroke(false);
		physicsContainer.addChild(physPoly);
	}
	
	
	
	private void createScreenBorders(MTComponent parent){
		//Left border 
		float borderWidth = 50f;
		float borderHeight = app.height;
		Vector3D pos = new Vector3D(-(borderWidth/2f) , app.height/2f);
		PhysicsRectangle borderLeft = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderLeft.setName("borderLeft");
		parent.addChild(borderLeft);
		//Right border
		pos = new Vector3D(app.width + (borderWidth/2), app.height/2);
		PhysicsRectangle borderRight = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderRight.setName("borderRight");
		parent.addChild(borderRight);
		//Top border
		borderWidth = app.width;
		borderHeight = 50f;
		pos = new Vector3D(app.width/2, -(borderHeight/2));
		PhysicsRectangle borderTop = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderTop.setName("borderTop");
		parent.addChild(borderTop);
		//Bottom border
		pos = new Vector3D(app.width/2 , app.height + (borderHeight/2));
		PhysicsRectangle borderBottom = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderBottom.setName("borderBottom");
		parent.addChild(borderBottom);
	}

	public void onEnter() {
	}
	
	public void onLeave() {	
	}

}
