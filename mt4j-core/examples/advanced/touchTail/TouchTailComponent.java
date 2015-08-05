package advanced.touchTail;

import java.awt.Polygon;
import java.io.File;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.MultipleDragProcessor;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Plane;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * The Class TouchTailComponent.
 * 
 * Yellowtail by Golan Levin (www.flong.com)
 * Yellowtail (1998-2000) is an interactive software system for the gestural creation 
 * and performance of real-time abstract animation. Yellowtail repeats a user's strokes end-over-end, 
 * enabling simultaneous specification of a line's shape and quality of movement. 
 * Each line repeats according to its own period, 
 * producing an ever-changing and responsive display of lively, worm-like textures.
 */
public class TouchTailComponent extends AbstractVisibleComponent {
	private TailGesture[] gestureArray;
	private final int nGestures = 30;  // Number of gestures
	private final int minMove = 3;     // Minimum travel for a new point
	private int currentGestureID;
	private PApplet app;
	private HashMap<Long, TailGesture> idToGesture; 
	private Plane plane;
	private PImage a;
	
	private boolean useTexture = false;
	
	public TouchTailComponent(PApplet applet) {
		super(applet);
		idToGesture = new HashMap<Long, TailGesture>();
		
		this.app = applet;
		currentGestureID = -1;
		gestureArray = new TailGesture[nGestures];
		for (int i = 0; i < nGestures; i++) {
			gestureArray[i] = new TailGesture(app.width, app.height);
		}
		clearTails();
		
		this.registerInputProcessor(new MultipleDragProcessor(app));
		this.addGestureListener(MultipleDragProcessor.class, new DragListener());
		
		Vector3D norm = new Vector3D(0,0,1);
		Vector3D pointInPlane = new Vector3D(0,0,0);
		plane = new Plane(pointInPlane, norm);
		
//		/*
		if (useTexture){
			this.setNoStroke(true);
			a = applet.loadImage(System.getProperty("user.dir") + File.separator + "examples" +  File.separator +"advanced"+ File.separator+ File.separator + "touchTail"  +  File.separator + "data" + File.separator +
				"brush_cr3.png");
		}else{
			this.setNoStroke(false);
			this.setStrokeWeight(0.8f);
		}
//		*/
	}
	
	
	private class DragListener implements IGestureEventListener{
		public boolean processGestureEvent(MTGestureEvent ge) {
			DragEvent de = (DragEvent)ge;
			Vector3D to = de.getTo();
			switch (de.getId()) {
			case DragEvent.GESTURE_STARTED:{
				currentGestureID = (currentGestureID+1) % nGestures;
				//System.out.println("New current gesture ID => " + currentGestureID);
				TailGesture G = gestureArray[currentGestureID];
				idToGesture.put(de.getDragCursor().getId(), G); 
				G.clear();
				G.clearPolys();
				G.addPoint(to.x, to.y);
			}break;
			case DragEvent.GESTURE_UPDATED:{
				TailGesture G = idToGesture.get(de.getDragCursor().getId());
				if (G.distToLast(to.x, to.y) > minMove) {
					G.addPoint(to.x, to.y);
					G.smooth();
					G.compile();
				}
			}break;
			case DragEvent.GESTURE_ENDED:{
				idToGesture.remove(de.getDragCursor().getId());
			}break;
			default:
				break;
			}
			return true;
		}
	}

	@Override
	public void drawComponent(PGraphics g) {
		//FIXME TEST
		if (MT4jSettings.getInstance().isOpenGlMode()){
//			GL gl = ((PGraphicsOpenGL)g).gl;
			GL10 gl = PlatformUtil.getGL();
			if (useTexture){
//				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//				gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
			}
//			gl.glDisable(GL.GL_DEPTH_TEST);
//			gl.glColorMask(true, true, false, false);
		}
		
		if (useTexture){
			g.textureMode(PConstants.NORMAL);
		}
		
		/*
		g.textureMode(g.NORMALIZED);
		g.beginShape(PApplet.QUADS);
		g.texture(a); //FIXME TEST
		g.vertex(0, 0, 0,0);
		g.vertex(100, 0, 1,0);
		g.vertex(100, 100, 1,1);
		g.vertex(0, 100, 0,1);
		g.endShape();
		*/
		
		updateGeometry();
		for (int i = 0; i < nGestures; i++) {
			renderGesture(g, gestureArray[i], g.width, g.height);
		}
	}
	

	private void renderGesture(PGraphics g, TailGesture gesture, int w, int h) {
		if (gesture.exists) {
			if (gesture.nPolys > 0) {
				Polygon polygons[] = gesture.polygons;
				int crosses[] = gesture.crosses;
				
				if (this.isNoStroke())
					g.noStroke();
				else{
					MTColor strokeCol = gesture.getColor();
					g.strokeWeight(this.getStrokeWeight());
					g.stroke(strokeCol.getR(), strokeCol.getG(), strokeCol.getB(), strokeCol.getAlpha());
				}
				
				if (this.isNoFill())
					g.noFill();
				else{
					MTColor fillCol = gesture.getColor();
					g.fill(fillCol.getR(), fillCol.getG(), fillCol.getB(), fillCol.getAlpha());
				}

				int xpts[];
				int ypts[];
				Polygon p;
				int cr;
				
				//FIXME TEST
				if (useTexture){
					MTColor c = gesture.getColor();
					g.tint(c.getR(), c.getG(), c.getB(), c.getAlpha());
				}

				g.beginShape(PApplet.QUADS);
				
				if (useTexture){
					g.texture(a); //FIXME TEST
				}
				
				int gnp = gesture.nPolys;
				for (int i=0; i < gnp; i++) {

					p = polygons[i];
					xpts = p.xpoints;
					ypts = p.ypoints;

					if (useTexture){
						//FIXME TEST
						g.vertex(xpts[0], ypts[0], 0,0);
						g.vertex(xpts[1], ypts[1], 1,0);
						g.vertex(xpts[2], ypts[2], 1,1);
						g.vertex(xpts[3], ypts[3], 0,1);
					}else{
						g.vertex(xpts[0], ypts[0]);
						g.vertex(xpts[1], ypts[1]);
						g.vertex(xpts[2], ypts[2]);
						g.vertex(xpts[3], ypts[3]);
					}
					
//					/*
					if ((cr = crosses[i]) > 0) {
						if ((cr & 3)>0) {
							g.vertex(xpts[0]+w, ypts[0]);
							g.vertex(xpts[1]+w, ypts[1]);
							g.vertex(xpts[2]+w, ypts[2]);
							g.vertex(xpts[3]+w, ypts[3]);

							g.vertex(xpts[0]-w, ypts[0]);
							g.vertex(xpts[1]-w, ypts[1]);
							g.vertex(xpts[2]-w, ypts[2]);
							g.vertex(xpts[3]-w, ypts[3]);
						}
						if ((cr & 12)>0) {
							g.vertex(xpts[0], ypts[0]+h);
							g.vertex(xpts[1], ypts[1]+h);
							g.vertex(xpts[2], ypts[2]+h);
							g.vertex(xpts[3], ypts[3]+h);

							g.vertex(xpts[0], ypts[0]-h);
							g.vertex(xpts[1], ypts[1]-h);
							g.vertex(xpts[2], ypts[2]-h);
							g.vertex(xpts[3], ypts[3]-h);
						}

						// I have knowingly retained the small flaw of not
						// completely dealing with the corner conditions
						// (the case in which both of the above are true).
					}
//					*/
				}
				g.endShape();
			}
		}
	}


	private void updateGeometry() {
		TailGesture J;
		for (int g = 0; g < nGestures; g++) {
			if ((J = gestureArray[g]).exists) {
				if (!idToGesture.containsValue(J)){
					advanceGesture(J); //FIXME ENABLE
				}
			}
		}
	}


	private void advanceGesture(TailGesture gesture) {
		// Move a Gesture one step
		if (gesture.exists) { // check
			int nPts = gesture.nPoints;
			int nPts1 = nPts-1;
			Vector3D path[];
			float jx = gesture.jumpDx;
			float jy = gesture.jumpDy;

			if (nPts > 0) {
				path = gesture.path;
				for (int i = nPts1; i > 0; i--) {
					path[i].x = path[i-1].x;
					path[i].y = path[i-1].y;
				}
				path[0].x = path[nPts1].x - jx;
				path[0].y = path[nPts1].y - jy;
				gesture.compile();
			}
		}
	}

	public void clearTails() {
		for (int i = 0; i < nGestures; i++) {
			gestureArray[i].clear();
		}
	}



	@Override
	protected boolean componentContainsPointLocal(Vector3D testPoint) {
		return plane.componentContainsPointLocal(testPoint);
	}


	@Override
	public Vector3D getIntersectionLocal(Ray ray) {
		return plane.getIntersectionLocal(ray);
	}

	
	/*
	void keyPressed() {
		if (key == '+' || key == '=') {
			if (currentGestureID >= 0) {
				float th = gestureArray[currentGestureID].thickness;
				gestureArray[currentGestureID].thickness = min(96, th+1);
				gestureArray[currentGestureID].compile();
			}
		} else if (key == '-') {
			if (currentGestureID >= 0) {
				float th = gestureArray[currentGestureID].thickness;
				gestureArray[currentGestureID].thickness = max(2, th-1);
				gestureArray[currentGestureID].compile();
			}
		} else if (key == ' ') {
			clearGestures();
		}
	}
*/
	
}
