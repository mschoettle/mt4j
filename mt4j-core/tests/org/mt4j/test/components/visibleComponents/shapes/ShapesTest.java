package org.mt4j.test.components.visibleComponents.shapes;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.shapes.mesh.MTCube;
import org.mt4j.components.visibleComponents.shapes.mesh.MTSphere;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.test.AbstractWindowTestcase;
import org.mt4j.test.testUtil.DummyScene;
import org.mt4j.test.testUtil.TestRunnable;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

public class ShapesTest extends AbstractWindowTestcase {

	private DummyScene scene;
	private MTComponent parent;

	@Override
	public void inStartUp(AbstractMTApplication app) {
		//Add a scene to the mt application
		this.scene = new DummyScene(app, "Dummy Scene");
		app.addScene(scene);
		
		//Set up components
		parent = new MTComponent(app);
		getCanvas().addChild(parent);
	}
	
	
	public MTCanvas getCanvas(){
		return this.scene.getCanvas();
	}
	
	
	public void testShapeCreation(){
		runTest(new TestRunnable() {
			@Override
			public void runMTTestCode() {
				
		        MTPolygon poly = new MTPolygon( 
		        		getMTApplication(),
		        		new Vertex[]{
	        				new Vertex(0,0,100),
	        				new Vertex(0,0,0),
	        				new Vertex(0,100,0),
	        				new Vertex(0,100,100),
	        				new Vertex(0,0,100)
	        		});
		        getCanvas().addChild(poly);
		        
				MTLine line = new MTLine(getMTApplication(), new Vertex(30,30), new Vertex(150,110));
				getCanvas().addChild(line);
				
				MTRectangle r1 = new MTRectangle(getMTApplication(),10, 10,200, 100);
				getCanvas().addChild(r1);
				assertEquals(getCanvas().getChildren()[3], r1);
				
				MTRoundRectangle roundRect = new MTRoundRectangle(getMTApplication(),0,0, 0, 160, 110,30, 30);
		        getCanvas().addChild(roundRect);
		        
				MTEllipse ellipse = new MTEllipse(getMTApplication(), new Vector3D(100,100,0), 160, 80);
				getCanvas().addChild(ellipse);
				
				MTCube cube = new MTCube(getMTApplication(), 150);
				getCanvas().addChild(cube);
				
				MTSphere sphere = new MTSphere(getMTApplication(), "sphere", 30,30, 130);
				getCanvas().addChild(sphere);
				
				MTTriangleMesh mesh = new MTTriangleMesh(getMTApplication(), new GeometryInfo(getMTApplication(), new Vertex[]{
					new Vertex(0,0,0),
					new Vertex(100,0,0),
					new Vertex(50,50,0),
				}));
				getCanvas().addChild(mesh);
				
				assertTrue(getCanvas().getChildren().length == 9);
			}
		});
	}
	
	
	
	public void testSetWidthHeight(){
		runTest(new TestRunnable() {
			@Override
			public void runMTTestCode() {
				MTRectangle rect1 = new MTRectangle(getMTApplication(),150, 0,150, 300);
				parent.addChild(rect1);
				
//				rect1.rotateZ(Vector3D.ZERO_VECTOR, 45, TransformSpace.LOCAL);
				
				float epsilon = 0.001f;
				
				float width1 = 384;
				rect1.setWidthXYRelativeToParent(width1);
				assertTrue(rect1.getWidthXY(TransformSpace.RELATIVE_TO_PARENT) - width1 <= epsilon);
				
				float width2 = 284;
				rect1.setWidthXYGlobal(284);
				assertTrue(rect1.getWidthXY(TransformSpace.GLOBAL) - width2 <= epsilon);
				
				float height1 = 384;
				rect1.setHeightXYRelativeToParent(height1);
				assertTrue(rect1.getHeightXY(TransformSpace.RELATIVE_TO_PARENT) - height1 <= epsilon);
				
				float height2 = 284;
				rect1.setHeightXYGlobal(284);
				assertTrue(rect1.getHeightXY(TransformSpace.GLOBAL) - height2 <= epsilon);
				
				float width3 = 512;
				float height3 = 484;
				rect1.setSizeXYRelativeToParent(width3, height3);
				assertTrue(rect1.getWidthXY(TransformSpace.RELATIVE_TO_PARENT) - width3 <= epsilon);
				assertTrue(rect1.getHeightXY(TransformSpace.RELATIVE_TO_PARENT) - height3 <= epsilon);
				
				
				float width4 = width3*2;
				rect1.scale(2, 2, 1, new Vector3D(0,0,0));
				assertTrue(rect1.getWidthXY(TransformSpace.RELATIVE_TO_PARENT) - width4 <= epsilon);
				
				float width5 = width4*2;
				parent.scale(2, 2, 1, new Vector3D(0,0,0));
				assertTrue(rect1.getWidthXY(TransformSpace.GLOBAL) - width5 <= epsilon);
				
			}
		});
	}
	
	
	
	public void testRectCenterPoint(){
		runTest(new TestRunnable() {
			@Override
			public void runMTTestCode() {
				MTRectangle rect1 = new MTRectangle(getMTApplication(), 100, 100, 100, 200, 100);
				parent.addChild(rect1);
				
				//Check untransformed and not added to a parent
				Vector3D expectedUntransformedCenter = new Vector3D(200, 150, 100);
				Vector3D centerLocalR1 = rect1.getCenterPointLocal();
				assertEquals(centerLocalR1, expectedUntransformedCenter);

				Vector3D centerRelParentR1 = rect1.getCenterPointRelativeToParent();
				assertEquals(centerRelParentR1, expectedUntransformedCenter);

				Vector3D centerGlobalR1 = rect1.getCenterPointGlobal();
				assertEquals(centerGlobalR1, expectedUntransformedCenter);


				//Check with Transformed local matrix but not added to a parent
				Vector3D transVect = new Vector3D(10,10,10);
				rect1.translate(transVect, TransformSpace.LOCAL);

				//Local center should not change
				Vector3D centerLocal2 = rect1.getCenterPointLocal();
				assertEquals(centerLocal2, expectedUntransformedCenter);

				Vector3D expCenterRelP2 = new Vector3D(expectedUntransformedCenter.getAdded(transVect));
				Vector3D centerRelParent2 = rect1.getCenterPointRelativeToParent();
				assertEquals(centerRelParent2, expCenterRelP2);

				Vector3D expCenterGlobal2 = expCenterRelP2;
				Vector3D centerGlobal2 = rect1.getCenterPointGlobal();
				assertEquals(centerGlobal2, expCenterGlobal2);
			}
		});

	}
	
	
	//TODO make subclasses returning different shapes for testing
//	abstract AbstractShape createShape();
	
	public void assertEquals(Vector3D vec1, Vector3D vec2){
		assertEquals("Vector3D check X equality", vec1.x, vec2.x);
		assertEquals("Vector3D check Y equality", vec1.y, vec2.y);
		assertEquals("Vector3D check Z equality", vec1.z, vec2.z);
		assertEquals("Vector3D check W equality", vec1.w, vec2.w);
		
		/*
		//Check with tolerance because of floating point math errors
		assertTrue("Check X equality with tolerance:",  Math.abs(vec1.x - vec2.x) <= EPSILON);
		assertTrue("Check Y equality with tolerance:",  Math.abs(vec1.y - vec2.y) <= EPSILON);
		assertTrue("Check Z equality with tolerance:",  Math.abs(vec1.z - vec2.z) <= EPSILON);
		assertTrue("Check W equality with tolerance:",  Math.abs(vec1.w - vec2.w) <= EPSILON);
		*/
	}
	
	public static float EPSILON =  1.1920928955078125E-7f;


}
