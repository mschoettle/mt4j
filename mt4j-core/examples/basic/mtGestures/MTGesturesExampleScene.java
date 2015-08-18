package basic.mtGestures;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.DefaultArcballAction;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultLassoAction;
import org.mt4j.input.gestureAction.DefaultPanAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor.ArcballProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.flickProcessor.FlickEvent;
import org.mt4j.input.inputProcessors.componentProcessors.flickProcessor.FlickProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.LassoProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.panProcessor.PanProcessorTwoFingers;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;

public class MTGesturesExampleScene extends AbstractScene {
	private AbstractMTApplication app;
	
	public MTGesturesExampleScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		this.setClearColor(new MTColor(126, 130, 168, 255));
		this.registerGlobalInputProcessor(new CursorTracer(app, this));

		float verticalPad = 53;
		float horizontalPad = 500;
		
		MTColor white = new MTColor(255,255,255);
		final MTColor textAreaColor = new MTColor(50,50,50,255);
		
		IFont font = FontManager.getInstance().createFont(app, "arial.ttf", 35, white);
		
		//Add canvas background gestures
		//Zoom gesture
		MTTextArea backgroundZoom = new MTTextArea(mtApplication, font);
		backgroundZoom.setFillColor(new MTColor(150,150,150));
		backgroundZoom.setNoFill(true);
		backgroundZoom.setNoStroke(true);
		backgroundZoom.setText("Zoom anywhere on the background!");
		backgroundZoom.setPickable(false);
		this.getCanvas().addChild(backgroundZoom);
//		backgroundZoom.setPositionGlobal(new Vector3D(app.width/2f, app.height/2f,0));
		backgroundZoom.setPositionGlobal(new Vector3D(app.width/2f, app.height - backgroundZoom.getHeightXY(TransformSpace.GLOBAL),0));
		getCanvas().registerInputProcessor(new ZoomProcessor(app));
		getCanvas().addGestureListener(ZoomProcessor.class, new DefaultZoomAction());
		
		//2 finger pan gesture
		MTTextArea backgroundPan = new MTTextArea(mtApplication, font);
		backgroundPan.setFillColor(new MTColor(150,150,150));
		backgroundPan.setNoFill(true);
		backgroundPan.setNoStroke(true);
		backgroundPan.setText("Pan anywhere on the background!");
		backgroundPan.setPickable(false);
		this.getCanvas().addChild(backgroundPan);
//		backgroundPan.setPositionGlobal(new Vector3D(app.width/2f, app.height/2f + 1*verticalPad,0));
		backgroundPan.setPositionGlobal(new Vector3D(app.width/2f, app.height - backgroundZoom.getHeightXY(TransformSpace.GLOBAL) - backgroundPan.getHeightXY(TransformSpace.GLOBAL),0));
		getCanvas().registerInputProcessor(new PanProcessorTwoFingers(app));
		getCanvas().addGestureListener(PanProcessorTwoFingers.class, new DefaultPanAction());
		
		//Add component multi-touch gestures
		MTTextArea dragOnly = new MTTextArea(mtApplication, font);
		dragOnly.setFillColor(textAreaColor);
		dragOnly.setStrokeColor(textAreaColor);
		dragOnly.setText("Drag me!");
		this.clearAllGestures(dragOnly);
		dragOnly.registerInputProcessor(new DragProcessor(app));
		dragOnly.addGestureListener(DragProcessor.class, new DefaultDragAction());
		dragOnly.addGestureListener(DragProcessor.class, new InertiaDragAction()); //Add inertia to dragging
		this.getCanvas().addChild(dragOnly);
		
		MTTextArea rotateOnly = new MTTextArea(mtApplication, font);
		rotateOnly.setFillColor(textAreaColor);
		rotateOnly.setStrokeColor(textAreaColor);
		rotateOnly.setText("Rotate me!");
		this.clearAllGestures(rotateOnly);
		rotateOnly.registerInputProcessor(new RotateProcessor(app));
		rotateOnly.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
		this.getCanvas().addChild(rotateOnly);
		rotateOnly.setAnchor(PositionAnchor.UPPER_LEFT);
		rotateOnly.setPositionGlobal(new Vector3D(0, verticalPad,0));
		
		MTTextArea scaleOnly = new MTTextArea(mtApplication, font);
		scaleOnly.setFillColor(textAreaColor);
		scaleOnly.setStrokeColor(textAreaColor);
		scaleOnly.setText("Scale me!");
		this.clearAllGestures(scaleOnly);
		scaleOnly.registerInputProcessor(new ScaleProcessor(app));
		scaleOnly.addGestureListener(ScaleProcessor.class, new DefaultScaleAction());
		this.getCanvas().addChild(scaleOnly);
		scaleOnly.setAnchor(PositionAnchor.UPPER_LEFT);
		scaleOnly.setPositionGlobal(new Vector3D(0, 2*verticalPad,0));
		
		MTTextArea dragAndRotate = new MTTextArea(mtApplication, font);
		dragAndRotate.setFillColor(textAreaColor);
		dragAndRotate.setStrokeColor(textAreaColor);
		dragAndRotate.setText("Drag and Rotate me!");
		this.clearAllGestures(dragAndRotate);
		dragAndRotate.registerInputProcessor(new RotateProcessor(app));
		dragAndRotate.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
		dragAndRotate.registerInputProcessor(new DragProcessor(app));
		dragAndRotate.addGestureListener(DragProcessor.class, new DefaultDragAction());
		this.getCanvas().addChild(dragAndRotate);
		dragAndRotate.setAnchor(PositionAnchor.UPPER_LEFT);
		dragAndRotate.setPositionGlobal(new Vector3D(0, 3*verticalPad,0));
		
		MTTextArea dragAndScale = new MTTextArea(mtApplication, font);
		dragAndScale.setFillColor(textAreaColor);
		dragAndScale.setStrokeColor(textAreaColor);
		dragAndScale.setText("Drag and Scale me!");
		this.clearAllGestures(dragAndScale);
		dragAndScale.registerInputProcessor(new ScaleProcessor(app));
		dragAndScale.addGestureListener(ScaleProcessor.class, new DefaultScaleAction());
		dragAndScale.registerInputProcessor(new DragProcessor(app));
		dragAndScale.addGestureListener(DragProcessor.class, new DefaultDragAction());
		this.getCanvas().addChild(dragAndScale);
		dragAndScale.setAnchor(PositionAnchor.UPPER_LEFT);
		dragAndScale.setPositionGlobal(new Vector3D(0, 4*verticalPad,0));
		
		MTTextArea rotateAndScale = new MTTextArea(mtApplication, font);
		rotateAndScale.setFillColor(textAreaColor);
		rotateAndScale.setStrokeColor(textAreaColor);
		rotateAndScale.setText("Rotate and Scale me!");
		this.clearAllGestures(rotateAndScale);
		rotateAndScale.registerInputProcessor(new ScaleProcessor(app));
		rotateAndScale.addGestureListener(ScaleProcessor.class, new DefaultScaleAction());
		rotateAndScale.registerInputProcessor(new RotateProcessor(app));
		rotateAndScale.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
		this.getCanvas().addChild(rotateAndScale);
		rotateAndScale.setAnchor(PositionAnchor.UPPER_LEFT);
		rotateAndScale.setPositionGlobal(new Vector3D(0,5*verticalPad,0));
		
		MTTextArea dragRotScale = new MTTextArea(mtApplication, font);
		dragRotScale.setFillColor(textAreaColor);
		dragRotScale.setStrokeColor(textAreaColor);
		dragRotScale.setText("Drag, Rotate and Scale me!");
		this.clearAllGestures(dragRotScale);
		dragRotScale.registerInputProcessor(new ScaleProcessor(app));
		dragRotScale.addGestureListener(ScaleProcessor.class, new DefaultScaleAction());
		dragRotScale.registerInputProcessor(new RotateProcessor(app));
		dragRotScale.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
		dragRotScale.registerInputProcessor(new DragProcessor(app));
		dragRotScale.addGestureListener(DragProcessor.class, new DefaultDragAction());
		this.getCanvas().addChild(dragRotScale);
		dragRotScale.setAnchor(PositionAnchor.UPPER_LEFT);
		dragRotScale.setPositionGlobal(new Vector3D(0,6*verticalPad,0));
		
		//Tap gesture
		final MTTextArea tapOnly = new MTTextArea(mtApplication, font);
		tapOnly.setFillColor(textAreaColor);
		tapOnly.setStrokeColor(textAreaColor);
		tapOnly.setText("Tap me! ---");
		this.clearAllGestures(tapOnly);
		tapOnly.registerInputProcessor(new TapProcessor(app));
		tapOnly.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				switch (te.getId()) {
				case MTGestureEvent.GESTURE_STARTED:
					tapOnly.setFillColor(new MTColor(220,220,220,255));
					break;
				case MTGestureEvent.GESTURE_UPDATED:
					break;
				case MTGestureEvent.GESTURE_ENDED:
					if (te.isTapped()){
						if (tapOnly.getText().endsWith("--"))
							tapOnly.setText("Tap me! -|-");
						else
							tapOnly.setText("Tap me! ---");	
					}
					tapOnly.setFillColor(textAreaColor);
					break;
				}
				return false;
			}
		});
		this.getCanvas().addChild(tapOnly);
		tapOnly.setAnchor(PositionAnchor.UPPER_LEFT);
		tapOnly.setPositionGlobal(new Vector3D(1*horizontalPad,0,0));
		
		//Double Tap gesture
		final MTTextArea doubleTap = new MTTextArea(mtApplication, font);
		doubleTap.setFillColor(textAreaColor);
		doubleTap.setStrokeColor(textAreaColor);
		doubleTap.setText("Double Tap me! ---");
		this.clearAllGestures(doubleTap);
		doubleTap.registerInputProcessor(new TapProcessor(app, 25, true, 350));
		doubleTap.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isDoubleTap()){
					if (doubleTap.getText().endsWith("--"))
						doubleTap.setText("Double Tap me! -|-");
					else
						doubleTap.setText("Double Tap me! ---");	
				}
				return false;
			}
		});
		this.getCanvas().addChild(doubleTap);
		doubleTap.setAnchor(PositionAnchor.UPPER_LEFT);
		doubleTap.setPositionGlobal(new Vector3D(1*horizontalPad,1*verticalPad,0));
		
		//Tap and Hold gesture
		final MTTextArea tapAndHoldOnly = new MTTextArea(mtApplication, font);
		tapAndHoldOnly.setFillColor(textAreaColor);
		tapAndHoldOnly.setStrokeColor(textAreaColor);
		tapAndHoldOnly.setText("Tap&Hold me!  ---");
		this.clearAllGestures(tapAndHoldOnly);
		tapAndHoldOnly.registerInputProcessor(new TapAndHoldProcessor(app, 2000));
		tapAndHoldOnly.addGestureListener(TapAndHoldProcessor.class, new TapAndHoldVisualizer(app, getCanvas()));
		tapAndHoldOnly.addGestureListener(TapAndHoldProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapAndHoldEvent th = (TapAndHoldEvent)ge;
				switch (th.getId()) {
				case TapAndHoldEvent.GESTURE_STARTED:
					break;
				case TapAndHoldEvent.GESTURE_UPDATED:
					break;
				case TapAndHoldEvent.GESTURE_ENDED:
					if (th.isHoldComplete()){
						if (tapAndHoldOnly.getText().endsWith("--"))
							tapAndHoldOnly.setText("Tap&Hold me!  -|-");
						else
							tapAndHoldOnly.setText("Tap&Hold me!  ---");	
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		this.getCanvas().addChild(tapAndHoldOnly);
		tapAndHoldOnly.setAnchor(PositionAnchor.UPPER_LEFT);
		tapAndHoldOnly.setPositionGlobal(new Vector3D(1*horizontalPad,2*verticalPad,0));
		
		//Arcball gesture
		MTTextArea arcballOnly = new MTTextArea(mtApplication, font);
		arcballOnly.setFillColor(textAreaColor);
		arcballOnly.setStrokeColor(textAreaColor);
		arcballOnly.setText("Arcball rotate me!");
		this.clearAllGestures(arcballOnly);
		arcballOnly.registerInputProcessor(new ArcballProcessor(app, arcballOnly));
		arcballOnly.addGestureListener(ArcballProcessor.class, new DefaultArcballAction());
		this.getCanvas().addChild(arcballOnly);
		arcballOnly.setAnchor(PositionAnchor.UPPER_LEFT);
		arcballOnly.setPositionGlobal(new Vector3D(1*horizontalPad,3*verticalPad,0));
		
		//Lasso gesture
		MTTextArea lassoUs1 = new MTTextArea(mtApplication, font);
		lassoUs1.setFillColor(textAreaColor);
		lassoUs1.setStrokeColor(textAreaColor);
		lassoUs1.setText("Lasso select us!");
		this.clearAllGestures(lassoUs1);
		this.getCanvas().addChild(lassoUs1);
		lassoUs1.setAnchor(PositionAnchor.UPPER_LEFT);
		lassoUs1.setPositionGlobal(new Vector3D(1f*horizontalPad,4*verticalPad,0));
		
		MTTextArea lassoUs2 = new MTTextArea(mtApplication, font);
		lassoUs2.setFillColor(textAreaColor);
		lassoUs2.setStrokeColor(textAreaColor);
		lassoUs2.setText("Lasso select us!");
		this.clearAllGestures(lassoUs2);
		this.getCanvas().addChild(lassoUs2);
		lassoUs2.setAnchor(PositionAnchor.UPPER_LEFT);
		lassoUs2.setPositionGlobal(new Vector3D(1f*horizontalPad,5*verticalPad,0));
		
		MTTextArea lassoUs3 = new MTTextArea(mtApplication, font);
		lassoUs3.setFillColor(textAreaColor);
		lassoUs3.setStrokeColor(textAreaColor);
		lassoUs3.setText("Lasso select us!");
		this.clearAllGestures(lassoUs3);
		this.getCanvas().addChild(lassoUs3);
		lassoUs3.setAnchor(PositionAnchor.UPPER_LEFT);
		lassoUs3.setPositionGlobal(new Vector3D(1f*horizontalPad,6*verticalPad,0));
		
		//Create the lasso processor and add the components which can be lassoed
		LassoProcessor lassoProcessor = new LassoProcessor(app, getCanvas(), getSceneCam());
		lassoProcessor.addClusterable(lassoUs1);
		lassoProcessor.addClusterable(lassoUs2);
		lassoProcessor.addClusterable(lassoUs3);
		getCanvas().registerInputProcessor(lassoProcessor);
		getCanvas().addGestureListener(LassoProcessor.class, new DefaultLassoAction(app, getCanvas().getClusterManager(), getCanvas()));
		
		
		//Flick gesture
		final MTTextArea flick = new MTTextArea(mtApplication, font);
		flick.setFillColor(textAreaColor);
		flick.setStrokeColor(textAreaColor);
		flick.setText("Flick:    \n");
		this.clearAllGestures(flick);
		flick.registerInputProcessor(new FlickProcessor(300, 5));
		flick.addGestureListener(FlickProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				FlickEvent e = (FlickEvent)ge;
				if (e.getId() == MTGestureEvent.GESTURE_ENDED)
					flick.setText("Flicked:    \n " + e.getDirection());
				return false;
			}
		});
		this.getCanvas().addChild(flick);
		flick.setAnchor(PositionAnchor.UPPER_LEFT);
		flick.setPositionGlobal(new Vector3D(1*horizontalPad, 7*verticalPad,0));
		
		
		//Add uni-stroke gesture example
		MTTextArea strokeText = new MTTextArea(mtApplication, font);
		strokeText.setFillColor(textAreaColor);
		strokeText.setStrokeColor(textAreaColor);
		strokeText.setPickable(false);
		strokeText.setText("Draw a stroke gesture here");
		strokeText.setAnchor(PositionAnchor.UPPER_LEFT);
		
		MTRectangle strokeGestureRect = new MTRectangle(getMTApplication(),strokeText.getWidthXY(TransformSpace.LOCAL) + 50,200);
		strokeGestureRect.setFillColor(textAreaColor);
		strokeGestureRect.setStrokeColor(textAreaColor);
		strokeGestureRect.addChild(strokeText);
		strokeGestureRect.setAnchor(PositionAnchor.UPPER_LEFT);
		strokeText.setPositionRelativeToParent(strokeGestureRect.getPosition(TransformSpace.LOCAL));
		
		final MTTextArea recognizedGestureText = new MTTextArea(mtApplication, font);
		recognizedGestureText.setFillColor(textAreaColor);
		recognizedGestureText.setStrokeColor(textAreaColor);
		recognizedGestureText.setText("Recognized: NO_GESTURE");
		recognizedGestureText.setAnchor(PositionAnchor.LOWER_LEFT);
		recognizedGestureText.setPickable(false);
		strokeGestureRect.setAnchor(PositionAnchor.LOWER_LEFT);
		strokeGestureRect.addChild(recognizedGestureText);
		recognizedGestureText.setPositionRelativeToParent(strokeGestureRect.getPosition(TransformSpace.LOCAL));
		
		getCanvas().addChild(strokeGestureRect);
		strokeGestureRect.setAnchor(PositionAnchor.CENTER);
		strokeGestureRect.setPositionGlobal(new Vector3D(strokeGestureRect.getWidthXY(TransformSpace.GLOBAL)/2f ,9*verticalPad,0));
		this.clearAllGestures(strokeGestureRect);
		
		UnistrokeProcessor up = new UnistrokeProcessor(getMTApplication());
		up.addTemplate(UnistrokeGesture.CIRCLE, Direction.CLOCKWISE);
		up.addTemplate(UnistrokeGesture.CIRCLE, Direction.COUNTERCLOCKWISE);
		up.addTemplate(UnistrokeGesture.RECTANGLE, Direction.CLOCKWISE);
		up.addTemplate(UnistrokeGesture.RECTANGLE, Direction.COUNTERCLOCKWISE);
		up.addTemplate(UnistrokeGesture.CHECK, Direction.CLOCKWISE);
		up.addTemplate(UnistrokeGesture.TRIANGLE, Direction.COUNTERCLOCKWISE);
		up.addTemplate(UnistrokeGesture.TRIANGLE, Direction.CLOCKWISE);
		up.addTemplate(UnistrokeGesture.PIGTAIL, Direction.CLOCKWISE);
		up.addTemplate(UnistrokeGesture.PIGTAIL, Direction.COUNTERCLOCKWISE);
		up.addTemplate(UnistrokeGesture.ARROW, Direction.CLOCKWISE);
		up.addTemplate(UnistrokeGesture.ARROW, Direction.COUNTERCLOCKWISE);
		up.addTemplate(UnistrokeGesture.STAR, Direction.CLOCKWISE);
		up.addTemplate(UnistrokeGesture.STAR, Direction.COUNTERCLOCKWISE);
		up.addTemplate(UnistrokeGesture.V, Direction.CLOCKWISE);
		up.addTemplate(UnistrokeGesture.QUESTION, Direction.CLOCKWISE);
		
		strokeGestureRect.registerInputProcessor(up);
		strokeGestureRect.addGestureListener(UnistrokeProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				UnistrokeEvent ue = (UnistrokeEvent)ge;
				switch (ue.getId()) {
				case UnistrokeEvent.GESTURE_STARTED:
					getCanvas().addChild(ue.getVisualization());
					break;
				case UnistrokeEvent.GESTURE_UPDATED:
					break;
				case UnistrokeEvent.GESTURE_ENDED:
					UnistrokeGesture g = ue.getGesture();
					System.out.println("Recognized gesture: " + g);
					recognizedGestureText.setText("Recognized: " + g);
					break;
				default:
					break;
				}
				return false;
			}
		});
	}
	

	private void clearAllGestures(MTComponent comp){
		comp.unregisterAllInputProcessors();
		comp.removeAllGestureEventListeners();
	}
	
	public void onEnter() {}
	
	public void onLeave() {}

}
