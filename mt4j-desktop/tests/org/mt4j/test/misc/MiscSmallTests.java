package org.mt4j.test.misc;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTOverlayContainer;
import org.mt4j.test.AbstractWindowTestcase;
import org.mt4j.test.testUtil.DummyScene;
import org.mt4j.test.testUtil.TestRunnable;
import org.mt4j.util.MTColor;

public class MiscSmallTests extends AbstractWindowTestcase {

	private DummyScene scene;

	@Override
	public void inStartUp(AbstractMTApplication app) {
		//Add a scene to the mt application
		this.scene = new DummyScene(app, "Dummy SceneA");
		app.addScene(scene);

	}
	
	public void testMTOverlayContainer(){
		runTest(new TestRunnable() {
			@Override
			public void runMTTestCode() {
				//MTOverlayContainer test
      MTOverlayContainer c1 = new MTOverlayContainer(getMTApplication(), "c1");
		MTRectangle r1 = new MTRectangle(getMTApplication(),0, 0,100, 100);
		c1.addChild(r1);
		MTOverlayContainer c2 = new MTOverlayContainer(getMTApplication(), "c2");
		MTRectangle r2 = new MTRectangle(getMTApplication(),20, 20,100, 100);
		r2.setFillColor(new MTColor(255,150,150));
		c2.addChild(r2);
		scene.getCanvas().addChild(c1);
		scene.getCanvas().addChild(c2);
			}
		});
		
	}

}
