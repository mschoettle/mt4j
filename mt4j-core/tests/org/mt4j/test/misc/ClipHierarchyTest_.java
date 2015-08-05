package org.mt4j.test.misc;
	import org.mt4j.AbstractMTApplication;
import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTClipRectangle;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;

	public class ClipHierarchyTest_ extends MTApplication {
		private static final long serialVersionUID = 1L;

		/**
		 * @param args
		 */
		public static void main(String[] args) {
			initialize();
		}
		
		
		@Override
		public void startUp() {
			addScene(new Scene(this, ""));
		}
		
		private class Scene extends AbstractScene{
			public Scene(AbstractMTApplication mtApplication, String name) {
				super(mtApplication, name);
				
				// CLIP HIERARCHY RECTANGLE TEST
		        MTRectangle drawRect7 = new MTRectangle(getMTApplication(),0, 0, 80, 90);
		        drawRect7.setFillColor(new MTColor(40, 190, 230, 255));
		        drawRect7.setStrokeColor(new MTColor(0, 0, 0, 255));
		        
		        MTClipRectangle drawRect6 = new MTClipRectangle(getMTApplication(),0,0, 0, 100, 110);
		        drawRect6.setFillColor(new MTColor(30, 110, 130, 255));
		        drawRect6.setStrokeColor(new MTColor(0, 0, 0, 255));
		        drawRect6.setStrokeWeight(1);
		        drawRect6.addChild(drawRect7);
		        
		        MTRectangle drawRect5 = new MTRectangle(getMTApplication(),8, 8, 120, 138);
		        drawRect5.setName("drawRect5");
		        drawRect5.setFillColor(new MTColor(140, 150, 30, 255));
		        drawRect5.setStrokeColor(new MTColor(0, 0, 0, 255));
//		        drawRect5.addChild(drawRect6);
		        
		        MTClipRectangle drawRect4 = new MTClipRectangle(getMTApplication(),0,0, 0, 180, 205);
		        drawRect4.setName("Clipwindow 4");
		        drawRect4.setFillColor(new MTColor(130, 250, 70, 255));
		        drawRect4.setStrokeColor(new MTColor(0, 0, 0, 255));
		        drawRect4.setStrokeWeight(5);
		        drawRect4.addChild(drawRect5);
		        
		        MTClipRectangle drawRect3 = new MTClipRectangle(getMTApplication(),0,0, 0, 120, 105);
		        drawRect3.setName("Clipwindow 3");
		        drawRect3.addChild(drawRect6);
		        drawRect3.setFillColor(new MTColor(230, 150, 70, 255));
		        drawRect3.setStrokeColor(new MTColor(0, 0, 0, 255));
		        drawRect3.setStrokeWeight(5);
		        
		        //Top window
//		        MTClipRoundRect cr = new MTClipRoundRect(0, 0, 0, 200, 300, 20, 20, getMTApplication());
		        MTClipRectangle cr = new MTClipRectangle(getMTApplication(), 0, 0, 0, 200, 300);
//		        cr.setPickable(false);
		        cr.setName("TopLevel clip window");
		        cr.setStrokeColor(new MTColor(0, 0, 0, 255));
		        cr.addChild(drawRect4);
		        cr.addChild(drawRect3);
		        cr.setStrokeWeight(2);
		        
		        this.getCanvas().addChild(cr);
			}
			

			@Override
			public void init() {}

			@Override
			public void shutDown() {}
			
		}

	}

