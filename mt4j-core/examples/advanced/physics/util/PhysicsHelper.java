/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package advanced.physics.util;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.glu.GLU;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeChainDef;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.jbox2d.util.nonconvex.Polygon;
import org.jbox2d.util.nonconvex.Triangle;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.MultipleDragProcessor;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLMaterial;
import org.mt4j.util.opengl.GluTrianglulator;

import processing.core.PApplet;
import advanced.physics.physicsShapes.PhysicsRectangle;

public class PhysicsHelper {
	
	
	public static MouseJoint createDragJoint(World world, Body body, float x, float y){
		MouseJointDef mjd = new MouseJointDef();
		mjd.body1 = body; // Not used, avoid a NPE
		mjd.body2 = body;
		mjd.target = new Vec2(x, y);
//		mjd.target = new Vec2(x/scale, y/scale);
//		mjd.maxForce = 8000.0f * body.m_mass;
//		mjd.maxForce = 99000.0f * body.m_mass;
//		mjd.maxForce = 99000.0f * body.m_mass;
//		mjd.maxForce = Float.MAX_VALUE; //Too big values will result in erratic behaviour with more than 1 mousejoint on a component
		mjd.maxForce = 90000.0f * body.m_mass;
		return (MouseJoint) world.createJoint(mjd);
	}
	
//	/*
	public static void removeDragJoints(Body body){
//		MouseJoint mouseJoint = (MouseJoint) comp.getUserData("mouseJoint");
//		if (mouseJoint != null){
//		world.destroyJoint(mouseJoint);	
//		}
		//FIXME this doesent remove the userData that pointed to the mousejoint!
		for (Joint joint = body.getWorld().getJointList(); joint != null; joint = joint.getNext()) {
			JointType type = joint.getType();
			switch (type) {
			case MOUSE_JOINT:
				MouseJoint mj = (MouseJoint)joint;
				if (body.equals(mj.getBody1()) || body.equals(mj.getBody2())){
					body.getWorld().destroyJoint(mj);
				}
				break;
			default:
				break;
			}
		}
	}
//	*/
	
	public static void addDragJoint(World world, MTComponent comp, boolean isDynamic, float scale){
		final float worldScale = scale;
		final World theWorld = world;
		
		if (isDynamic){
			//DYNAMIC BODIES MAKE MOUSEJOINTS
			comp.removeAllGestureEventListeners(DragProcessor.class);

			comp.registerInputProcessor(new MultipleDragProcessor(comp.getRenderer()));
			comp.addGestureListener(MultipleDragProcessor.class, new IGestureEventListener() {

//				comp.addGestureListener(DragProcessor.class, new IGestureEventListener() {
				//@Override
				public boolean processGestureEvent(MTGestureEvent ge) {
					DragEvent de = (DragEvent)ge;
					try{
						MTComponent comp = (MTComponent)de.getTarget();
						Body body = (Body)comp.getUserData("box2d");
						MouseJoint mouseJoint;
						Vector3D to = new Vector3D(de.getTo());
						//Un-scale position from mt4j to box2d
						PhysicsHelper.scaleDown(to, worldScale);
						//System.out.println("MouseJoint To: " + to);
						long cursorID =  de.getDragCursor().getId();

						switch (de.getId()) {
						case DragEvent.GESTURE_STARTED:
							comp.sendToFront();
							body.wakeUp();
							mouseJoint = createDragJoint(theWorld, body, to.x, to.y);
							comp.setUserData("mouseJoint" + cursorID, mouseJoint);
							break;
						case DragEvent.GESTURE_UPDATED:
							mouseJoint = (MouseJoint) comp.getUserData("mouseJoint" + cursorID);
							if (mouseJoint != null){
								mouseJoint.setTarget(new Vec2(to.x, to.y));
							}
							break;
						case DragEvent.GESTURE_ENDED:
							mouseJoint = (MouseJoint) comp.getUserData("mouseJoint" + cursorID);
							if (mouseJoint != null){
								comp.setUserData("mouseJoint" + cursorID, null);
//								theWorld.destroyJoint(mouseJoint);	
								//Only destroy the joint if it isnt already (go through joint list and check)
								for (Joint joint = theWorld.getJointList(); joint != null; joint = joint.getNext()) {
									JointType type = joint.getType();
									switch (type) {
									case MOUSE_JOINT:
										MouseJoint mj = (MouseJoint)joint;
										if (body.equals(mj.getBody1()) || body.equals(mj.getBody2())){
//											theWorld.destroyJoint(mj);
											if (mj.equals(mouseJoint)) {
												theWorld.destroyJoint(mj);
											}
										}
										break;
									default:
										break;
									}
								}
							}
							mouseJoint = null;
							break;
						default:
							break;
						}
					}catch (Exception e) {
						System.err.println(e.getMessage());
					}
					return true;
				}
			});
		}else{
			comp.removeAllGestureEventListeners(DragProcessor.class);
			
			boolean hasDragProcessor = false;
			AbstractComponentProcessor[] p = comp.getInputProcessors();
            for (AbstractComponentProcessor abstractComponentProcessor : p) {
                if (abstractComponentProcessor instanceof DragProcessor) {
                    hasDragProcessor = true;
                }
            }
			if (!hasDragProcessor){
				comp.registerInputProcessor(new DragProcessor(comp.getRenderer()));
			}
			
			//For static bodies just alter the transform of the body
			comp.addGestureListener(DragProcessor.class, new IGestureEventListener() {
				//@Override
				public boolean processGestureEvent(MTGestureEvent ge) {
					DragEvent de = (DragEvent)ge;
					Vector3D dir = PhysicsHelper.scaleDown(new Vector3D(de.getTranslationVect()), worldScale);
					try{
						MTComponent comp = (MTComponent)de.getTarget();
						Body body = (Body)comp.getUserData("box2d");
						body.setXForm(
								new Vec2(body.getPosition().x + dir.x, body.getPosition().y + dir.y),
								body.getAngle());
						switch (de.getId()) {
						case DragEvent.GESTURE_STARTED:
							comp.sendToFront();
							body.wakeUp();
							break;
						case DragEvent.GESTURE_UPDATED:
						case DragEvent.GESTURE_ENDED:
						default:
							break;
						}
					}catch (Exception e) {
						System.err.println(e.getMessage());
					}
					return true;
				}
			});
		}
	}

		
	
	public static List<Vertex> triangulateEarClips(List<Vertex> vertices){
		org.jbox2d.util.nonconvex.Triangle[] tri = getEarClipTriangles(vertices);
		List<Vertex> tris = new ArrayList<Vertex>();
        for (Triangle triangle : tri) {
            tris.add(new Vertex(triangle.x[0], triangle.y[0], 0));
            tris.add(new Vertex(triangle.x[1], triangle.y[1], 0));
            tris.add(new Vertex(triangle.x[2], triangle.y[2], 0));
        }
		return tris;
	}
	
	
	public static org.jbox2d.util.nonconvex.Triangle[] getEarClipTriangles(List<Vertex> vertices){
		org.jbox2d.util.nonconvex.Triangle[] tri = null;
		float[] xCoords = new float[vertices.size()];
		float[] yCoords = new float[vertices.size()];
		
		for (int i = 0; i < vertices.size(); i++) {
			xCoords[i] = vertices.get((vertices.size()-1)-i).x;
			yCoords[i] = vertices.get((vertices.size()-1)-i).y;
		}
		//tri = earClipper.triangulatePolygon(xCoords, yCoords, vertices.size());
		
		org.jbox2d.util.nonconvex.Triangle[] triangulated = new org.jbox2d.util.nonconvex.Triangle[vertices.size() - 2];
        for (int i=0; i<triangulated.length; ++i) {
        	triangulated[i] = new org.jbox2d.util.nonconvex.Triangle();
        }
        
		Polygon.triangulatePolygon(xCoords, yCoords, vertices.size(), triangulated);
		
		//Try reversed order
		if (tri == null){
			System.err.println("Null! trying reversed!");
			for (int i = 0; i < vertices.size(); i++) {
				xCoords[i] = vertices.get(i).x;
				yCoords[i] = vertices.get(i).y;
			}
//			tri = earClipper.triangulatePolygon(xCoords, yCoords, vertices.size());
			Polygon.triangulatePolygon(xCoords, yCoords, vertices.size(), triangulated);
		}
		return tri;
	}
	
	
	public static List<Vertex> triangulateGLU(AbstractMTApplication app, List<Vertex> vertices){
		System.err.println("Trying glu triangulation..");
		GluTrianglulator triangulator = new GluTrianglulator(app);
		Vertex[] vertexArray = vertices.toArray(new Vertex[vertices.size()]);
		return triangulator.tesselate(vertexArray, GLU.GLU_TESS_WINDING_NONZERO);
	}
	
	
	public static float scaleDown(float distance, float physicsScale){
		return distance /physicsScale;
	}
	
	public static float scaleUp(float distance, float physicsScale){
		return distance * physicsScale;
	}
	
	public static Vertex[] scaleDown(Vertex[] vertices, float physicsScale){
		return Vertex.scaleVectorArray(vertices, Vector3D.ZERO_VECTOR, 1f/physicsScale, 1f/physicsScale, 1);
	}
	
	public static Vertex[] scaleUp(Vertex[] vertices, float physicsScale){
		return Vertex.scaleVectorArray(vertices, Vector3D.ZERO_VECTOR, physicsScale, physicsScale, 1);
	}
	
	public static Vector3D[] scaleDown(Vector3D[] vertices, float physicsScale){
		return Vector3D.scaleVectorArray(vertices, Vector3D.ZERO_VECTOR, 1f/physicsScale, 1f/physicsScale, 1);
	}
	
	public static Vector3D[] scaleUp(Vector3D[] vertices, float physicsScale){
		return Vector3D.scaleVectorArray(vertices, Vector3D.ZERO_VECTOR, physicsScale, physicsScale, 1);
	}
	
	public static Vector3D scaleDown(Vector3D vec, float physicsScale){
		return vec.scaleLocal(1f/physicsScale);
	}
	
	public static Vector3D scaleUp(Vector3D vec, float physicsScale){
		return vec.scaleLocal(physicsScale);
	}
	
	
	public static GLMaterial createDefaultGLMaterial(PApplet app){
		//Set up a material
		GLMaterial material = new GLMaterial(PlatformUtil.getGL());
		material.setAmbient(new float[]{ .2f, .2f, .2f, 1f });
		material.setDiffuse(new float[]{ .8f, .8f, .8f, 1f } );
		material.setEmission(new float[]{ .0f, .0f, .0f, 1f });
		material.setSpecular(new float[]{ 1.0f, 1.0f, 1.0f, 1f });  // almost white: very reflective
		material.setShininess(110);// 0=no shine,  127=max shine
		return material;
	}
	
	
	
	
	/**
	 * Draw physics debug.
	 */
	public static void drawDebugPhysics(PApplet app, World world, float scale){
		app.fill(180, 190);
		app.stroke(140, 190);
		app.strokeWeight(1);
		app.pushMatrix();
//		this.getSceneCam().update();
		app.scale(scale, scale);
		for (Body body = world.getBodyList(); body != null; body = body.getNext()) {
			Shape shape;
			for (shape = body.getShapeList(); shape != null; shape = shape.getNext()) {
				switch (shape.getType()) {
				case POLYGON_SHAPE:
					app.beginShape();
					PolygonShape poly = (PolygonShape)shape;
					int count = poly.getVertexCount();
					Vec2[] verts = poly.getVertices();
					for(int i = 0; i < count; i++) {
						Vec2 vert = body.getWorldLocation(verts[i]);
						app.vertex(vert.x , vert.y );
					}
					app.endShape();
					break;
				case EDGE_SHAPE:
					EdgeShape edge = (EdgeShape)shape;
					Vec2 v1 = body.getWorldLocation(edge.getVertex1());
					Vec2 v2 = body.getWorldLocation(edge.getVertex2());
					
					app.beginShape();
					app.vertex(v1.x , v1.y );
					app.vertex(v2.x , v2.y );
					app.endShape();
//					app.line(v1.x , v1.y ,v2.x , v2.y );
					
					/*
					EdgeShape next = edge;
					while (next.getNextEdge() != null){
						next = edge.getNextEdge();
						Vec2 v11 = body.getWorldLocation(next.getVertex1());
						Vec2 v22 = body.getWorldLocation(next.getVertex2());
						
						app.beginShape();
						app.vertex(v11.x , v11.y );
						app.vertex(v22.x , v22.y );
						app.endShape();
					}
					*/
					
					break;
				case CIRCLE_SHAPE:
					CircleShape circle = (CircleShape)shape;
					float radius = circle.getRadius();
					Vec2 c = body.getWorldLocation(circle.getLocalPosition());
					app.ellipseMode(PApplet.CENTER);
					app.ellipse(c.x, c.y, radius+2, radius+2);
					break;
				default:
					break;
				}
			}
		}
		app.popMatrix();
	}
	
	
	public static void addScreenBoundaries(PApplet app, World world, MTComponent futureParent, float scale){
		// CREATE SCREEN BORDERS \\
		//Left border 
		float borderWidth = 50f;
		float borderHeight = app.height;
		Vector3D pos = new Vector3D(-(borderWidth/2f) , app.height/2f);
		PhysicsRectangle borderLeft = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderLeft.setName("borderLeft");
		futureParent.addChild(borderLeft);
		//Right border
		pos = new Vector3D(app.width + (borderWidth/2), app.height/2);
		PhysicsRectangle borderRight = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderRight.setName("borderRight");
		futureParent.addChild(borderRight);
		//Top border
		borderWidth = app.width;
		borderHeight = 50f;
		pos = new Vector3D(app.width/2, -(borderHeight/2));
		PhysicsRectangle borderTop = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderTop.setName("borderTop");
		futureParent.addChild(borderTop);
		//Bottom border
		pos = new Vector3D(app.width/2 , app.height + (borderHeight/2));
		PhysicsRectangle borderBottom = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderBottom.setName("borderBottom");
		futureParent.addChild(borderBottom);
	}
	
	
	/**
	 * Adds an edgeShape around the world with a slight offset so 
	 * that every shape hits the edge first before hitting the world boundaries.
	 * 
	 * @param app the app
	 * @param world the world
	 * @param scale the scale
	 * 
	 * @return the edge body
	 */
	public static Body addWorldEdgeBoundaries(PApplet app, World world, float scale){
		AABB wAABB = world.getWorldAABB();
		Vec2 l = new Vec2(wAABB.lowerBound);
		Vec2 u = new Vec2(wAABB.upperBound);
		
		float worldWidth = u.x - l.x;
		float worldHeight = u.y - l.y;
		
		float offset = 1f; //kleiner als world machen
		
		BodyDef dymBodyDef = new BodyDef();
		dymBodyDef.position = new Vec2(0 , 0 );
//		dymBodyDef.position = new Vec2(worldWidth*0.5f , worldHeight*0.5f);
		Body theBody = world.createBody(dymBodyDef);

		EdgeChainDef myEdges = new EdgeChainDef();
		//CCW so edge points inwards
		myEdges.addVertex(new Vec2(l.x + offset , l.y + worldHeight - offset*2));
		
		myEdges.addVertex(new Vec2(l.x + worldWidth  - offset*2, l.y + worldHeight - offset*2));
		
		myEdges.addVertex(new Vec2(l.x + worldWidth - offset*2, l.y + offset));
		
		myEdges.addVertex(new Vec2(l.x + offset , l.y + offset));

		myEdges.setIsLoop(true);
		myEdges.friction = 2.0f;
//		myEdges.density = 1.0f;
		myEdges.density = 0.0f;
		
		myEdges.isSensor = true;

		theBody.createShape(myEdges);
		theBody.setMassFromShapes();
		
		return theBody;
	}
	
}
