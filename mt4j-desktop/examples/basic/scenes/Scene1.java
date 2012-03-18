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
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.sceneManagement.transition.BlendTransition;
import org.mt4j.sceneManagement.transition.FadeTransition;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GLFBO;

import processing.core.PImage;

public class Scene1 extends AbstractScene {
	private AbstractMTApplication mtApp;
	private Iscene scene2;
	
//	private String imagePath = System.getProperty("user.dir") + File.separator + "examples"+  File.separator +"basic"+  File.separator + "scenes" + File.separator + "data" + File.separator;
	private String imagePath =  "basic"+  AbstractMTApplication.separator + "scenes" + AbstractMTApplication.separator + "data" + AbstractMTApplication.separator;
	
	public Scene1(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.mtApp = mtApplication;
		
		//Set the background color
		this.setClearColor(new MTColor(146, 150, 188, 255));
		
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
		
		//Create a textfield
		MTTextArea textField = new MTTextArea(mtApplication, FontManager.getInstance().createFont(mtApp, "arial.ttf", 
				50, MTColor.WHITE)); 
		textField.setNoFill(true);
		textField.setNoStroke(true);
		textField.setText("Scene 1");
		this.getCanvas().addChild(textField);
		textField.setPositionGlobal(new Vector3D(mtApp.width/2f, mtApp.height/2f));
		
		//Button to get to the next scene
		PImage arrow = mtApplication.loadImage(imagePath +	"arrowRight.png");
		MTImageButton nextSceneButton = new MTImageButton(mtApplication, arrow);
		nextSceneButton.setNoStroke(true);
		if (MT4jSettings.getInstance().isOpenGlMode())
			nextSceneButton.setUseDirectGL(true);
		nextSceneButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
					//Save the current scene on the scene stack before changing
					mtApp.pushScene();
					if (scene2 == null){
						scene2 = new Scene2(mtApp, "Scene 2");
						//Add the scene to the mt application
						mtApp.addScene(scene2);
					}
					//Do the scene change
					mtApp.changeScene(scene2);
				}
				return true;
			}
		});
		getCanvas().addChild(nextSceneButton);
		nextSceneButton.setPositionGlobal(new Vector3D(mtApp.width - nextSceneButton.getWidthXY(TransformSpace.GLOBAL) - 5, mtApp.height - nextSceneButton.getHeightXY(TransformSpace.GLOBAL) - 5, 0));
		
		//Set a scene transition - Flip transition only available using opengl supporting the FBO extenstion
		if (MT4jSettings.getInstance().isOpenGlMode() && GLFBO.isSupported(mtApp))
			this.setTransition(new BlendTransition(mtApp, 700));
		else{
			this.setTransition(new FadeTransition(mtApplication, 1700));
		}
		
		//Register flick gesture with the canvas to change the scene
		getCanvas().registerInputProcessor(new FlickProcessor());
		getCanvas().addGestureListener(FlickProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				FlickEvent e = (FlickEvent)ge;
				if (e.getId() == MTGestureEvent.GESTURE_ENDED && e.isFlick()){
					switch (e.getDirection()) {
					case WEST:
					case NORTH_WEST:
					case SOUTH_WEST:
						//Save the current scene on the scene stack before changing
						mtApp.pushScene();
						if (scene2 == null){
							scene2 = new Scene2(mtApp, "Scene 2");
							//Add the scene to the mt application
							mtApp.addScene(scene2);
						}
						//Do the scene change
						mtApp.changeScene(scene2);
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
