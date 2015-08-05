package org.mt4j.test.sceneManagement;

import org.mt4j.AbstractMTApplication;
import org.mt4j.test.AbstractWindowTestcase;
import org.mt4j.test.testUtil.DummyScene;
import org.mt4j.test.testUtil.TestRunnable;

public class SceneTest extends AbstractWindowTestcase {

	private DummyScene sceneA;
	private DummyScene sceneB;
	private DummyScene sceneC;

	@Override
	public void inStartUp(AbstractMTApplication app) {
		//Add a scene to the mt application
		this.sceneA = new DummyScene(app, "Dummy SceneA");
		app.addScene(sceneA);
		
		//Add a scene to the mt application
		this.sceneB = new DummyScene(app, "Dummy SceneB");
		app.addScene(sceneB);
		
		//Add a scene to the mt application
		this.sceneC = new DummyScene(app, "Dummy SceneC");
		app.addScene(sceneC);
	}
	
	public void testSceneChange(){
		runTest(new TestRunnable() {
			@Override
			public void runMTTestCode() {
				assertTrue(getMTApplication().isRenderThreadCurrent());
				
				assertEquals(getMTApplication().getScene("Dummy SceneA"), sceneA);
				
				//Test Scene changes without transitions
				assertEquals(getMTApplication().getCurrentScene(), sceneA);
				getMTApplication().changeScene(sceneB);
				assertEquals(getMTApplication().getCurrentScene(), sceneB);
				assertEquals(getMTApplication().getScenes()[1], sceneB);
				getMTApplication().changeScene(sceneC);
				assertEquals(getMTApplication().getCurrentScene(), sceneC);
				getMTApplication().changeScene(sceneA);
				assertEquals(getMTApplication().getCurrentScene(), sceneA);
				
				//Test push/popScene()
				getMTApplication().pushScene();
				getMTApplication().changeScene(sceneB);
				assertEquals(getMTApplication().getCurrentScene(), sceneB);
				getMTApplication().pushScene();
				getMTApplication().changeScene(sceneC);
				assertEquals(getMTApplication().getCurrentScene(), sceneC);
				getMTApplication().popScene();
				assertEquals(getMTApplication().getCurrentScene(), sceneB);
				getMTApplication().popScene();
				assertEquals(getMTApplication().getCurrentScene(), sceneA);
				getMTApplication().popScene();
				assertEquals(getMTApplication().getCurrentScene(), sceneA);
				
				//Test add/removeScene()
				getMTApplication().removeScene(sceneA);
				assertTrue(getMTApplication().getSceneCount() == 3);
				getMTApplication().removeScene(sceneC);
				assertTrue(getMTApplication().getSceneCount() == 2);
				getMTApplication().addScene(sceneC);
				getMTApplication().addScene(sceneC);
				assertTrue(getMTApplication().getSceneCount() == 3);
			}
		});
	}

}
