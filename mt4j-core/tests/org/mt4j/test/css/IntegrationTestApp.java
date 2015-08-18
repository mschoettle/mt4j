package org.mt4j.test.css;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Vertex;

public class IntegrationTestApp extends AbstractScene{
	private MTComponent parent;
	private AbstractMTApplication app;
	
	public IntegrationTestApp(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);

		this.app = mtApplication;
			//this.getCanvas().addChild(new MTBackgroundImage(app, app.loadImage("256x256.jpg"), true));
			
			//Set up components
			parent = new MTComponent(app);
			this.getCanvas().addChild(parent);
		
			MTRectangle r = new MTRectangle(app, 500, 500, 500, 500);
			r.enableCSS();
			this.getCanvas().addChild(r);
			
			Vertex[] vtcs = {new Vertex(100,100), new Vertex(200, 20), new Vertex(300, 200) ,new Vertex(100,100)};
			MTPolygon p = new MTPolygon(app, vtcs);
			this.getCanvas().addChild(p);
			p.enableCSS();
	}


	@Override
	public void onEnter() {
	}

	@Override
	public void onLeave() {
		
	}

}
