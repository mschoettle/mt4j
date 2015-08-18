package basic.css;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.css.style.CSSSelector;
import org.mt4j.components.css.util.CSSKeywords.CSSSelectorType;
import org.mt4j.components.css.util.CSSTemplates;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.widgets.MTSuggestionTextArea;


public class CssExampleScene  extends AbstractScene{
	private AbstractMTApplication app;
	
	public CssExampleScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);

		this.app = mtApplication;
			
			//Enable CSS for all components. If you do not use this switch, for every component you have to apply component.enableCSS()
			app.getCssStyleManager().setGloballyEnabled(true);

			//Load style sheet with selector defined in file (this case: Universal)
			app.getCssStyleManager().loadStyles("templates/bluestyle.css");
			
			//Load style sheet only for MTTextAreas and derived classes (because of CSSSelectorType.CLASS. To only address MTTextAreas (wihtout subclosses), user CSSSelectorType.TYPE
			app.getCssStyleManager().loadStylesAndOverrideSelector(CSSTemplates.MATRIXSTYLE, new CSSSelector("MTTextArea", CSSSelectorType.CLASS));
			
			//Load style sheet only for certain IDs
			app.getCssStyleManager().loadStylesAndOverrideSelector(CSSTemplates.WHITESTYLE, new CSSSelector("Special Component", CSSSelectorType.ID));

			//Create MTRectangle: Only the universal selector applies
			MTRectangle rect = new MTRectangle(mtApplication, 100, 100);
			this.getCanvas().addChild(rect);
			
			rect.setAnchor(PositionAnchor.UPPER_LEFT);
			rect.setPositionGlobal(new Vector3D(50,50));
			
			//Create MTSuggestionTextArea: The first two stylesheets apply
			MTSuggestionTextArea sta = new MTSuggestionTextArea(mtApplication, 400);
			this.getCanvas().addChild(sta);
			sta.setAnchor(PositionAnchor.UPPER_LEFT);
			sta.setPositionGlobal(new Vector3D(50,200));
			
			//Create MTTextArea with certain ID
			//The TextArea is created with a default font, which is overwritten using CSS
			MTTextArea ta = new MTTextArea(mtApplication);
			ta.setCSSID("Special Component");
			this.getCanvas().addChild(ta);
			
			ta.setAnchor(PositionAnchor.UPPER_LEFT);
			ta.setPositionGlobal(new Vector3D(50,600));
			
	}

	public void onEnter() { }
	
	public void onLeave() {	}

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
