package basic.scenes;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.flickProcessor.FlickEvent;
import org.mt4j.input.inputProcessors.componentProcessors.flickProcessor.FlickProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.transition.FadeTransition;
import org.mt4j.sceneManagement.transition.FlipTransition;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GLFBO;

import processing.core.PImage;

public class Scene3 extends AbstractScene {

	private AbstractMTApplication mtApp;
	
//	private String imagePath = System.getProperty("user.dir") + File.separator + "examples"+  File.separator +"basic"+  File.separator + "scenes" + File.separator + "data" + File.separator;
	private String imagePath =  "basic" +  AbstractMTApplication.separator + "scenes" + AbstractMTApplication.separator + "data" + AbstractMTApplication.separator;

	public Scene3(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.mtApp = mtApplication;
		
		//Set the background color
		this.setClearColor(new MTColor(150, 188, 146, 255));
		
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
		
		//Create a textfield
		MTTextArea textField = new MTTextArea(mtApplication, FontManager.getInstance().createFont(mtApplication, "arial.ttf", 
				50, MTColor.WHITE)); 
		textField.setNoFill(true);
		textField.setNoStroke(true);
		textField.setText("Scene 3");
		this.getCanvas().addChild(textField);
		textField.setPositionGlobal(new Vector3D(mtApplication.width/2f, mtApplication.height/2f));
		
		//Button to return to the previous scene
		PImage arrow = mtApplication.loadImage(imagePath + "arrowRight.png");
		MTImageButton previousSceneButton = new MTImageButton(mtApplication, arrow);
		previousSceneButton.setNoStroke(true);
		if (MT4jSettings.getInstance().isOpenGlMode())
			previousSceneButton.setUseDirectGL(true);
		previousSceneButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
					mtApp.popScene();
				}
				return true;
			}
		});
		getCanvas().addChild(previousSceneButton);
		previousSceneButton.scale(-1, 1, 1, previousSceneButton.getCenterPointLocal(), TransformSpace.LOCAL);
		previousSceneButton.setPositionGlobal(new Vector3D(previousSceneButton.getWidthXY(TransformSpace.GLOBAL) + 5, mtApp.height - previousSceneButton.getHeightXY(TransformSpace.GLOBAL) - 5, 0));
		
		//Set a scene transition - Flip transition only available using opengl supporting the FBO extenstion
		if (MT4jSettings.getInstance().isOpenGlMode() && GLFBO.isSupported(mtApp))
			this.setTransition(new FlipTransition(mtApp, 700));
		else{
			this.setTransition(new FadeTransition(mtApp));
		}
		
		//Register flick gesture with the canvas to change the scene
		getCanvas().registerInputProcessor(new FlickProcessor());
		getCanvas().addGestureListener(FlickProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				FlickEvent e = (FlickEvent)ge;
				if (e.getId() == MTGestureEvent.GESTURE_ENDED && e.isFlick()){
					switch (e.getDirection()) {
					case EAST:
					case NORTH_EAST:
					case SOUTH_EAST:
						mtApp.popScene();
						break;
					default:
						break;
					}
				}
				return false;
			}
		});
	}

	public void onEnter() {
		System.out.println("Entered scene: " +  this.getName());
	}
	
	public void onLeave() {	
		System.out.println("Left scene: " +  this.getName());
	}

}
