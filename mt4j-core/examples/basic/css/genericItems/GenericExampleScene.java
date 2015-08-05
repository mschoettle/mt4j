package basic.css.genericItems;

import java.util.Arrays;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.css.style.CSSSelector;
import org.mt4j.components.css.util.CSSKeywords.CSSSelectorType;
import org.mt4j.components.css.util.CSSTemplates;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.widgets.MTCheckbox;
import org.mt4jx.components.visibleComponents.widgets.MTOptionBox;
import org.mt4jx.components.visibleComponents.widgets.MTSuggestionTextArea;
import org.mt4jx.components.visibleComponents.widgets.OptionGroup;


public class GenericExampleScene  extends AbstractScene{
	private AbstractMTApplication app;
	
	public GenericExampleScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);

		this.app = mtApplication;
		
			app.getCssStyleManager().setGloballyEnabled(true);
			app.getCssStyleManager().loadStylesAndOverrideSelector(CSSTemplates.MATRIXSTYLE, new CSSSelector("Group A", CSSSelectorType.ID));
			app.getCssStyleManager().loadStylesAndOverrideSelector(CSSTemplates.BLUESTYLE, new CSSSelector("Group B", CSSSelectorType.ID));
			
			//Group A
			//Set CSSID of all elements
			
			//Create Checkbox
			MTCheckbox a_cb = new MTCheckbox(app, 40);
			a_cb.setCSSID("Group A");
			
			this.getCanvas().addChild(a_cb);
			
			
			//Create OptionGroup, add two OptionBoxes
			OptionGroup a_group = new OptionGroup();
			MTOptionBox a_box1 = new MTOptionBox(app,40, a_group);
			a_box1.setCSSID("Group A");
			MTOptionBox a_box2 = new MTOptionBox(app,40, a_group);
			a_box2.setCSSID("Group A");
			this.getCanvas().addChild(a_box1);
			this.getCanvas().addChild(a_box2);
			
			
			//Prepare Suggestions for MTSuggestionTextArea
			String[] su = new String[] {"Nested ", "Class", "Summary", "MTListCell", "visibleComponents" };
			List<String> suggestions = Arrays.asList(su);

			//Create MTsuggestionTextArea
			MTSuggestionTextArea a_sta = new MTSuggestionTextArea(app, 200, suggestions);
			a_sta.setCSSID("Group A");
			this.getCanvas().addChild(a_sta);

			//Position all Elements
			a_cb.setAnchor(PositionAnchor.UPPER_LEFT);
			a_cb.setPositionGlobal(new Vector3D(50,100));

			a_box1.setAnchor(PositionAnchor.UPPER_LEFT);
			a_box1.setPositionGlobal(new Vector3D(50,300));
			
			a_box2.setAnchor(PositionAnchor.UPPER_LEFT);
			a_box2.setPositionGlobal(new Vector3D(150,300));
			
			a_sta.setAnchor(PositionAnchor.UPPER_LEFT);
			a_sta.setPositionGlobal(new Vector3D(50,500));
			
			//Group B
			
			MTCheckbox b_cb = new MTCheckbox(app, 40);
			b_cb.setCSSID("Group B");
			this.getCanvas().addChild(b_cb);
			
			OptionGroup b_group = new OptionGroup();
			MTOptionBox b_box1 = new MTOptionBox(app,40, b_group);
			b_box1.setCSSID("Group B");
			MTOptionBox b_box2 = new MTOptionBox(app,40, b_group);
			b_box2.setCSSID("Group B");
			this.getCanvas().addChild(b_box1);
			this.getCanvas().addChild(b_box2);
			
		
			MTSuggestionTextArea b_sta = new MTSuggestionTextArea(app, 400);
			b_sta.setCSSID("Group B");
			this.getCanvas().addChild(b_sta);
			
			//Position all Elements
			b_cb.setAnchor(PositionAnchor.UPPER_LEFT);
			b_cb.setPositionGlobal(new Vector3D(550,100));

			b_box1.setAnchor(PositionAnchor.UPPER_LEFT);
			b_box1.setPositionGlobal(new Vector3D(550,300));
			
			b_box2.setAnchor(PositionAnchor.UPPER_LEFT);
			b_box2.setPositionGlobal(new Vector3D(650,300));
			
			b_sta.setAnchor(PositionAnchor.UPPER_LEFT);
			b_sta.setPositionGlobal(new Vector3D(550,500));
			
	}

	@Override
	public void init() {
	}

	@Override
	public void shutDown() {
	}


}
