package advanced.drawing;

import java.util.HashMap;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;


public class DrawSurfaceScene extends AbstractScene {

	private AbstractMTApplication mtApp;

	private AbstractShape drawShape;

	private float stepDistance;

	private Vector3D localBrushCenter;

	private float brushWidthHalf;

	private HashMap<InputCursor, Vector3D> cursorToLastDrawnPoint;

	private float brushHeightHalf;

	private float brushScale;
	
	private MTColor brushColor;
	
	private boolean dynamicBrush;
	
	//TODO only works as lightweight scene atm because the framebuffer isnt cleared each frame
	//TODO make it work as a heavywight scene
	//TODO scale smaller at higher speeds?
	//TODO eraser?
	//TODO get blobwidth from win7 touch events and adjust the brush scale
	
	public DrawSurfaceScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.mtApp = mtApplication;
		
		this.getCanvas().setDepthBufferDisabled(true);
		
		/*
		this.drawShape = getDefaultBrush();
		this.localBrushCenter = drawShape.getCenterPointLocal();
		this.brushWidthHalf = drawShape.getWidthXY(TransformSpace.LOCAL)/2f;
		this.brushHeightHalf = drawShape.getHeightXY(TransformSpace.LOCAL)/2f;
		this.stepDistance = brushWidthHalf/2.5f;
		*/
		
		this.brushColor = new MTColor(0,0,0);
		this.brushScale = 1.0f;
		this.dynamicBrush = true;
//		this.stepDistance = 5.5f;
		
		this.cursorToLastDrawnPoint = new HashMap<InputCursor, Vector3D>();
		
		this.getCanvas().addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt){
				if(inEvt instanceof AbstractCursorInputEvt){
					final AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt)inEvt;
					final InputCursor m = posEvt.getCursor();
//					System.out.println("PrevPos: " + prevPos);
//					System.out.println("Pos: " + pos);

					if (posEvt.getId() != AbstractCursorInputEvt.INPUT_ENDED){
						registerPreDrawAction(new IPreDrawAction() {
							public void processAction() {
								boolean firstPoint = false;
								Vector3D lastDrawnPoint = cursorToLastDrawnPoint.get(m);
								Vector3D pos = new Vector3D(posEvt.getX(), posEvt.getY(), 0);

								if (lastDrawnPoint == null){
									lastDrawnPoint = new Vector3D(pos);
									cursorToLastDrawnPoint.put(m, lastDrawnPoint);
									firstPoint = true;
								}else{
									if (lastDrawnPoint.equalsVector(pos))
										return;	
								}
								
								float scaledStepDistance = stepDistance*brushScale;

								Vector3D direction = pos.getSubtracted(lastDrawnPoint);
								float distance = direction.length();
								direction.normalizeLocal();
								direction.scaleLocal(scaledStepDistance);

								float howManySteps = distance/scaledStepDistance;
								int stepsToTake = Math.round(howManySteps);

								//Force draw at 1st point
								if (firstPoint && stepsToTake == 0){
									stepsToTake = 1;
								}
//								System.out.println("Steps: " + stepsToTake);

//								GL gl = Tools3D.getGL(mtApp);
//								gl.glBlendFuncSeparate(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

								mtApp.pushMatrix();
								//We would have to set up a default view here for stability? (default cam etc?)
								getSceneCam().update(); 

								Vector3D currentPos = new Vector3D(lastDrawnPoint);
								for (int i = 0; i < stepsToTake; i++) { //start i at 1? no, we add first step at 0 already
									currentPos.addLocal(direction);
									//Draw new brush into FBO at correct position
									Vector3D diff = currentPos.getSubtracted(localBrushCenter);

									mtApp.pushMatrix();
									mtApp.translate(diff.x, diff.y);

									//NOTE: works only if brush upper left at 0,0
									mtApp.translate(brushWidthHalf, brushHeightHalf);
									mtApp.scale(brushScale);
									
									if (dynamicBrush){
									//Rotate brush randomly
//									mtApp.rotateZ(PApplet.radians(Tools3D.getRandom(0, 179)));
//									mtApp.rotateZ(PApplet.radians(Tools3D.getRandom(-85, 85)));
									mtApp.rotateZ(PApplet.radians(ToolsMath.getRandom(-25, 25)));
//									mtApp.rotateZ(PApplet.radians(Tools3D.getRandom(-9, 9)));
									mtApp.translate(-brushWidthHalf, -brushHeightHalf);
									}

									/*
		        					//Use random brush from brushes
		        					int brushIndex = Math.round(Tools3D.getRandom(0, brushes.length-1));
		        					AbstractShape brushToDraw = brushes[brushIndex];
									 */
									AbstractShape brushToDraw = drawShape;

									//Draw brush
									brushToDraw.drawComponent(mtApp.g);

									mtApp.popMatrix();
								}
								mtApp.popMatrix();

								cursorToLastDrawnPoint.put(m, currentPos);
							}

							public boolean isLoop() {
								return false;
							}
						});
					}else{
						cursorToLastDrawnPoint.remove(m);
					}
				}
				return false;
			}
		});

	}
	
	
	public void setBrush(AbstractShape brush){
		this.drawShape = brush;
		this.localBrushCenter = drawShape.getCenterPointLocal();
		this.brushWidthHalf = drawShape.getWidthXY(TransformSpace.LOCAL)/2f;
		this.brushHeightHalf = drawShape.getHeightXY(TransformSpace.LOCAL)/2f;
		this.stepDistance = brushWidthHalf/2.8f;
		this.drawShape.setFillColor(this.brushColor);
		this.drawShape.setStrokeColor(this.brushColor);
	}
	
	public void setBrushColor(MTColor color){
		this.brushColor = color;
		if (this.drawShape != null){
			drawShape.setFillColor(color);
			drawShape.setStrokeColor(color);
		}
	}
	
	public void setBrushScale(float scale){
		this.brushScale = scale;
	}
	
	
	public void onEnter() {
	}
	
	public void onLeave() {
	}
}
