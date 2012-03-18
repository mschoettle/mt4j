package org.mt4j.test.testUtil;

import org.mt4j.AbstractMTApplication;
import org.mt4j.sceneManagement.AbstractScene;

public class DummyScene extends AbstractScene {

	public DummyScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
	}

	public void onEnter() {}
	
	public void onLeave() {}

}
