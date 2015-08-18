package org.mt4j.test.css;

import java.util.List;

import org.junit.Test;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.css.parser.CSSParserConnection;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.test.AbstractWindowTestcase;
import org.mt4j.test.testUtil.DummyScene;
import org.mt4j.test.testUtil.TestRunnable;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;



public class SelectorIntegrationTest extends AbstractWindowTestcase {
	private MTComponent parent;
	private AbstractMTApplication app;
	private Iscene scene;
	CSSParserConnection pc;
	List<CSSStyle> styles;
	MTColor w = new MTColor(255,255,255,255);
	
	@Override
	public void inStartUp(AbstractMTApplication app) {
		this.app = app;
		//Add a scene to the mt application
		this.scene = new DummyScene(app, "Dummy Scene");
		app.addScene(scene);
		
		//Set up components
		parent = new MTComponent(app);
		getCanvas().addChild(parent);
		
		app.getCssStyleManager().loadStyles("junit/integrationtest.css");
		app.getCssStyleManager().setGloballyEnabled(true);
	}
	
	public MTCanvas getCanvas(){
		return this.scene.getCanvas();
	}
	

	
	@Test
	public void testDirectStyleSheets() {
		MTRectangle r = new MTRectangle(app,0,0,100, 100);
		//r.enableCSS();
		getCanvas().addChild(r);
		assertTrue(r.getFillColor().equals(MTColor.GREEN));
	}
	
	@Test
	public void testClassSelector() {
		MTEllipse e = new MTEllipse(app, new Vector3D(500,500), 50, 50);
		//e.enableCSS();
		getCanvas().addChild(e);
		assertTrue(e.getFillColor().equals(MTColor.WHITE));
	}
	
	@Test
	public void testUniversalSelector() {
		MTLine l = new MTLine(app, 100,100, 200,200);
		//l.enableCSS();
		getCanvas().addChild(l);
		MTEllipse e = new MTEllipse(app, new Vector3D(500,500), 50, 50);
		//e.enableCSS();
		getCanvas().addChild(e);
		assertTrue(l.getStrokeColor().equals(MTColor.BLUE));
		assertTrue(e.getStrokeColor().equals(MTColor.BLUE));
	}
	
	
	@Test
	public void testCascadingSelectors() {
		this.runTest(new TestRunnable() {
			@Override
			public void runMTTestCode() {
				MTRectangle r1 = new MTRectangle(app,100,100,100, 100);
				MTRectangle r2 = new MTRectangle(app,100,100,100, 100);
				MTRectangle r3 = new MTRectangle(app,100,100,100, 100);
				//r1.enableCSS(); r2.enableCSS(); r3.enableCSS();
				
				MTEllipse e = new MTEllipse(app, new Vector3D(200,200), 50,50);
				//e.enableCSS();
				
				
				MTTextArea ta = new MTTextArea(app);
				MTTextArea t2 = new MTTextArea(app);
				
				//ta.enableCSS(); t2.enableCSS();
				
				getCanvas().addChild(r1);
				getCanvas().addChild(r2);
				
				r1.addChild(ta);
				r2.addChild(e);
				e.addChild(r3);
				e.addChild(t2);

				//ta.applyStyleSheet();

				//t2.applyStyleSheet();

				//r3.applyStyleSheet();
				
				assertTrue(ta.getFillColor().equals(MTColor.LIME));
				assertTrue(r3.getFillColor().equals(MTColor.GREY));
				assertTrue(t2.getFillColor().equals(MTColor.BLUE));
			}
		});
			
		
	}
	
	@Test
	public void testSizes() {
		MTRectangle r1 = new MTRectangle(app,100,100,100, 100);
		MTRectangle r2 = new MTRectangle(app,100,100,100, 100);
		MTRectangle r3 = new MTRectangle(app,100,100,100, 100);
		//r1.enableCSS(); r2.enableCSS(); r3.enableCSS();
		
		r1.setCSSID("widthtest");
		r2.setCSSID("heighttest");
		r3.setCSSID("sizetest");
		
		getCanvas().addChild(r1);
		getCanvas().addChild(r2);
		getCanvas().addChild(r3);

		assertTrue(r1.getWidthXY(TransformSpace.RELATIVE_TO_PARENT) == 90f);
		assertTrue(r2.getHeightXY(TransformSpace.RELATIVE_TO_PARENT) == 110f);
		
		
		assertTrue(r3.getWidthXY(TransformSpace.RELATIVE_TO_PARENT) == 120f);
		assertTrue(r3.getHeightXY(TransformSpace.RELATIVE_TO_PARENT) == 450f);
	}
	

}
