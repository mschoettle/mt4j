package org.mt4j.test.css;

import org.mt4j.AbstractMTApplication;
import org.mt4j.sceneManagement.AbstractScene;


public class TestApp extends AbstractScene{

	public TestApp(AbstractMTApplication mtApplication, String name) {
		super( mtApplication,  name);
		
		/*System.out.println("Started Test Application");
		MTRectangle rect = new MTRectangle(400,400, mtApplication);
		this.setClearColor(new MTColor(0, 0, 64, 255));
		this.getCanvas().addChild(rect);
		Logger logger = Logger.getLogger("MT4J Extensions");
		SimpleLayout l = new SimpleLayout();
		ConsoleAppender ca = new ConsoleAppender(l);
				
		logger.addAppender(ca);
		//app.initApp();
		System.out.println(this.getMTApplication().g != null);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(this.getMTApplication().g != null);
		parserConnector pc = new parserConnector("selectortest.css", this.getMTApplication());
		pc.toString();*/
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutDown() {
		// TODO Auto-generated method stub
		
	}
	
}
