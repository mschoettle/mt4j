package basic.css.menus;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.css.style.CSSSelector;
import org.mt4j.components.css.util.CSSKeywords.CSSSelectorType;
import org.mt4j.components.css.util.CSSTemplates;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.widgets.menus.MTHUD;
import org.mt4jx.components.visibleComponents.widgets.menus.MTHexagonMenu;
import org.mt4jx.components.visibleComponents.widgets.menus.MTSquareMenu;
import org.mt4jx.components.visibleComponents.widgets.menus.MenuItem;

import processing.core.PImage;


public class MenuExampleScene  extends AbstractScene{
	private AbstractMTApplication app;
	
	public MenuExampleScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);

		this.app = mtApplication;
			
			//Set CSS Enabled for all components
			app.getCssStyleManager().setGloballyEnabled(true);

			//Load a different CSS Style for each component
			app.getCssStyleManager().loadStylesAndOverrideSelector(CSSTemplates.MATRIXSTYLE, new CSSSelector("MTHUD", CSSSelectorType.CLASS));
			app.getCssStyleManager().loadStylesAndOverrideSelector(CSSTemplates.BLUESTYLE, new CSSSelector("MTHexagonMenu", CSSSelectorType.CLASS));
			app.getCssStyleManager().loadStylesAndOverrideSelector(CSSTemplates.REDSTYLE, new CSSSelector("MTSquareMenu", CSSSelectorType.CLASS));
			
			PImage p1 = app.loadImage("basic/css/data/p1.jpg");
			PImage p2 = app.loadImage("basic/css/data/p2.jpg");
			PImage p3 = app.loadImage("basic/css/data/p3.jpg");
			
			//Create Menu Items
			List<MenuItem> menus = new ArrayList<MenuItem>();
			menus.add(new MenuItem("Start", new gestureListener("Start")));
			menus.add(new MenuItem("Open", new gestureListener("Open")));
			menus.add(new MenuItem("Close", new gestureListener("Close")));
			menus.add(new MenuItem("Exit", new gestureListener("Exit")));
			menus.add(new MenuItem("Save", new gestureListener("Save")));
			menus.add(new MenuItem("Load", new gestureListener("Load")));
			menus.add(new MenuItem("Cancel", new gestureListener("Cancel")));
			menus.add(new MenuItem("Undo", new gestureListener("Undo")));
			menus.add(new MenuItem(p1, new gestureListener("Picture Item 1")));
			menus.add(new MenuItem(p2, new gestureListener("Picture Item 2")));
			menus.add(new MenuItem(p3, new gestureListener("Picture Item 3")));
			
			//Create Square Menu
			MTSquareMenu sm = new MTSquareMenu(app, new Vector3D(25,200),  menus, 75);
			this.getCanvas().addChild(sm);
			
			//Create Hexagon Menu
			MTHexagonMenu hm = new MTHexagonMenu(app, new Vector3D(500,200),  menus, 100);
			this.getCanvas().addChild(hm);
			
			//Create Heads up display (on bottom of the screen)
			MTHUD hud = new MTHUD(app,menus, 64, MTHUD.BOTTOM );
			this.getCanvas().addChild(hud);
			
	}

	public void onEnter() {}
	
	public void onLeave() {}

	public class gestureListener implements IGestureEventListener {
		String string;
		public gestureListener(String string) {
			super();
			this.string = string;
		}
		
		
		public boolean processGestureEvent(MTGestureEvent ge) {

			if (ge instanceof TapEvent) {
				TapEvent te = (TapEvent) ge;
				if (te.getTapID() == TapEvent.TAPPED) {
					System.out.println(string);
				}
			}
			return true;
		}
		
	}
}
