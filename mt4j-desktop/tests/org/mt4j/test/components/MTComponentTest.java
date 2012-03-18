package org.mt4j.test.components;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.test.AbstractWindowTestcase;
import org.mt4j.test.testUtil.DummyScene;
import org.mt4j.test.testUtil.TestRunnable;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;


public class MTComponentTest extends AbstractWindowTestcase {
	private MTComponent parent;
	private AbstractMTApplication app;
	private Iscene scene;
	
	@Override
	public void inStartUp(AbstractMTApplication app) {
		this.app = app;
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
	
	public void testComponentAddRemove(){
		runTest(new TestRunnable() {
			@Override
			public void runMTTestCode() {
				System.out.println("\nTesting some base MTComponent functions..");
				System.out.println("Ext supported: " + Tools3D.isGLExtensionSupported(app, "test"));
				
				assertEquals(0, parent.getChildCount());
				int numChildren = Math.round(ToolsMath.getRandom(1, 10));
				assertTrue("children created and added: " + numChildren  + " -> >= 1 && <= 10", numChildren >= 1 && numChildren <= 10);

				for (int i = 0; i < numChildren; i++) {
					MTComponent newChild = new MTComponent(app);
					parent.addChild(newChild);
				}
				assertTrue("children list not emtpy", parent.getChildren().length > 0);

				assertEquals("parent Childcount == created children number", numChildren, parent.getChildCount());

				MTComponent first = parent.getChildByIndex(0);
				//parent.sendChildToFront(first);
				first.sendToFront();
				MTComponent last = parent.getChildByIndex(parent.getChildCount()-1);
				assertEquals("Sent first to front (last) - is it there now?" , first, last);

				
				int id = first.getID();
				MTComponent childByID = parent.getChildbyID(id);
				assertEquals(first, childByID);
				
				assertTrue(parent.containsChild(first));
				assertTrue(parent.containsDirectChild(first));
				
				assertTrue(first.getViewingCamera() != null);
				
				parent.removeAllChildren();
				assertEquals("All children removed?", 0	, parent.getChildCount());
			}
		});
	}
	
	
	public void testPicking(){
		runTest(new TestRunnable() {
			@Override
			public void runMTTestCode() {
				//Create 3 rectangles
				MTRectangle rect1 = new MTRectangle(getMTApplication(),100,100);
				rect1.setFillColor(new MTColor(255,0,0));
				MTRectangle rect2 = new MTRectangle(getMTApplication(),100,100);
				rect2.setFillColor(new MTColor(0,255,0));
				MTRectangle rect3 = new MTRectangle(getMTApplication(),100,100);
				rect3.setFillColor(new MTColor(0,0,255));
				
				parent.addChild(rect1);
				parent.addChild(rect2);
				parent.addChild(rect3);
				assertEquals(3, parent.getChildCount());
				
				//Test if all 3 rects in pick list
				assertEquals(getCanvas().pick(50, 50).getPickList().size(), 3);
				
				//Check order of picks, should be 3,2,1
				assertEquals(getCanvas().pick(50, 50).getPickList().get(0).hitObj, rect3);
				assertEquals(getCanvas().pick(50, 50).getPickList().get(1).hitObj, rect2);
				assertEquals(getCanvas().pick(50, 50).getPickList().get(2).hitObj, rect1);
				
				//Check if intersectionpoint is 50,50,0
				assertTrue(getCanvas().pick(50, 50).getInterSectionPointNearestPickedObj().equalsVectorWithTolerance(new Vector3D(50,50,0), 0.05f));
				
				//Should pick rect3
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), rect3);
				
				//Test sendToFront()
				rect1.sendToFront();
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), rect1);
				rect2.sendToFront();
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), rect2);
				rect3.sendToFront();
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), rect3);
				
				//Test when translated Z
				rect3.translate(new Vector3D(0,0,-0.5f));
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), rect2);
				rect3.translate(new Vector3D(0,0, 0.5f));
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), rect3);
				rect1.translate(new Vector3D(0,0, 0.5f));
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), rect1);
				rect1.translate(new Vector3D(0,0, -0.5f));
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), rect3);
				
				//Test setcomposite
				parent.setComposite(true);
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), parent);
				parent.setComposite(false);
				assertEquals(getCanvas().pick(50, 50).getNearestPickResult(), rect3);
				
				parent.removeAllChildren();
				assertEquals("All children removed?", 0	, parent.getChildCount());
			}
		});
	}
	
	
	
	
	
	
	

}
