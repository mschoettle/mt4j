package basic.fiducials;

import java.util.HashMap;
import java.util.Map;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.MTFiducialInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.globalProcessors.RawFiducialProcessor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;


public class FiducialScene extends AbstractScene implements IMTInputEventListener {
	private AbstractMTApplication app;
	private IFont font;
	private Map<Integer, AbstractShape> fiducialIDToComp;
	
	public FiducialScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		this.setClearColor(new MTColor(220, 220, 200, 255));
		
		//Listen to _all_ fiducial events
		RawFiducialProcessor fiducialProcessor = new RawFiducialProcessor();
		fiducialProcessor.addProcessorListener(this);
		registerGlobalInputProcessor(fiducialProcessor);
		
		//Maps the fiducial IDs to the visible component so we can keep track
		fiducialIDToComp = new HashMap<Integer, AbstractShape>();
		
		font = FontManager.getInstance().createFont(app, "arial.ttf", 30, MTColor.WHITE);
	}

	//Global input processor listener implementation (IMTInputEventListener)
	public boolean processInputEvent(MTInputEvent inEvt) {
		if (inEvt instanceof MTFiducialInputEvt) {
			MTFiducialInputEvt fEvt = (MTFiducialInputEvt)inEvt;
			int fID = fEvt.getFiducialId();
			Vector3D position = fEvt.getPosition();

			AbstractShape comp;
			switch (fEvt.getId()) {
			case MTFiducialInputEvt.INPUT_STARTED:
				//Create a new component for the fiducial
				AbstractShape newComp = createComponent(fID, position);
				fiducialIDToComp.put(fID, newComp); //Map id to component
				//Move component to fiducial position
				newComp.setPositionGlobal(position);
				//Save the absolute rotation angle in the component for late
				newComp.setUserData("angle", fEvt.getAngle()); 
				//Rotate the component
				newComp.rotateZ(newComp.getCenterPointRelativeToParent(), AbstractMTApplication.degrees(fEvt.getAngle()));
				//Add the component to the canvas to draw it
				getCanvas().addChild(newComp);	
				break;
			case MTFiducialInputEvt.INPUT_UPDATED:
				//Retrieve the corresponding component for the fiducial ID from the map
				comp = fiducialIDToComp.get(fID);
				if (comp != null){
					//Set the new position 
					comp.setPositionGlobal(position);
					//Set the rotation (we have to do a little more here because
					//mt4j does incremental rotations instead of specifying an absolute angle)
					float oldAngle = (Float)comp.getUserData("angle"); //retrieve the "old" angle
					float newAngle = fEvt.getAngle();
					if (oldAngle != newAngle){
						float diff = newAngle-oldAngle;
						comp.setUserData("angle", newAngle);
						diff = AbstractMTApplication.degrees(diff); //our rotation expects degrees (not radians)
						comp.rotateZ(comp.getCenterPointRelativeToParent(), diff); 
					}
				}
				break;
			case MTFiducialInputEvt.INPUT_ENDED:
				comp = fiducialIDToComp.get(fID);
				if (comp != null){
					comp.destroy();
					fiducialIDToComp.remove(fID);
				}
				break;
			default:
				break;
			}
		}
		return false;
	}


	private AbstractShape createComponent(int id, Vector3D pos){
		MTEllipse comp = new MTEllipse(app, new Vector3D(pos), 50,50, 50);
		comp.setNoFill(false);
		float r = ToolsMath.getRandom(20, 255);
		float g = ToolsMath.getRandom(20, 255); 
		float b = ToolsMath.getRandom(20, 255);
		comp.setFillColor(new MTColor(r, g, b, 200));
		comp.setNoStroke(false);
		comp.setStrokeWeight(1);
		comp.setStrokeColor(new MTColor(r, g, b, 200));
		comp.unregisterAllInputProcessors(); //Dont process input/gestures on this component
		
		MTTextArea text = new MTTextArea(app, font);
		text.appendText(Integer.toString(id));
		text.setFillColor(new MTColor(0, 0, 0, 0));
		text.setStrokeColor(new MTColor(0, 0, 0, 0));
		text.unregisterAllInputProcessors();
		comp.addChild(text);
		text.setPositionRelativeToParent(comp.getCenterPointLocal());
		return comp;
	}
	
	public void onEnter() {}
	
	public void onLeave() {	}

}
