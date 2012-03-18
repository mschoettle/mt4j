/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.input.gestureAction;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTController;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

/**
 * The Class TapAndHoldVisualizer. Animates the drawing of a circle
 * to indicate the status of the tap&hold gesture in progress.
 * 
 * @author Christopher Ruff
 */
public class TapAndHoldVisualizer implements IGestureEventListener {
	
	/** The app. */
	private AbstractMTApplication app;
	
	/** The parent. */
	private MTComponent parent;
	
	/** The cam. */
	private MTCamera cam;

	/** The e. */
	private HoldEllipse e;
	
	
	/**
	 * Instantiates a new tap and hold visualizer.
	 * 
	 * @param app the app
	 * @param parent the parent
	 */
	public TapAndHoldVisualizer(AbstractMTApplication app, MTComponent parent) {
		super();
		this.app = app;
		this.parent = parent;
		
		cam = new MTCamera(app);
		
		e = new HoldEllipse(app, new Vector3D(0, 0), 35, 35, 50);
		e.setPickable(false);
		e.unregisterAllInputProcessors();
		e.setStrokeColor(new MTColor(240,50,50,200));
		e.setStrokeWeight(4);
		e.setNoFill(true);
		e.setDepthBufferDisabled(true);
		e.attachCamera(cam);
		e.setVisible(false);
		e.setDegrees(0);
		
		e.setController(new IMTController() {
			public void update(long timeDelta) {
				MTComponent parent = e.getParent();
				if (parent != null){
					int childCount = parent.getChildCount();
					if (childCount > 0
						&& !parent.getChildByIndex(childCount-1).equals(e))
					{
						TapAndHoldVisualizer.this.app.invokeLater(new Runnable() {
							public void run(){
								MTComponent parent = e.getParent();
								if (parent != null){
									parent.removeChild(e);
									parent.addChild(e);
								}
							}
						});
					}
				}
			}
		});
	}


	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.IGestureEventListener#processGestureEvent(org.mt4j.input.inputProcessors.MTGestureEvent)
	 */
	public boolean processGestureEvent(MTGestureEvent ge) {
		TapAndHoldEvent t = (TapAndHoldEvent)ge;

		float d = 360f * t.getElapsedTimeNormalized();
		
//		float a = 255 * t.getElapsedTimeNormalized();
		float a = 205 * t.getElapsedTimeNormalized();
		
		switch (t.getId()) {
		case TapAndHoldEvent.GESTURE_STARTED:
		case TapAndHoldEvent.GESTURE_RESUMED:
			parent.addChild(e);
			e.setDegrees(0);
			e.recreate(false);
			e.setPositionGlobal(new Vector3D(t.getLocationOnScreen().x, t.getLocationOnScreen().y));
			break;
		case TapAndHoldEvent.GESTURE_UPDATED:
			e.setVisible(true);
			
			if (d >= 350){ //FIXME HACK to display the circle really closed before the end
				d = 360;
				e.setDegrees(d);
				e.recreate(true);
				
				MTColor stroke = e.getStrokeColor();
				e.setStrokeColor(new MTColor(stroke.getR(), stroke.getG(), stroke.getB(), 255));
			}else{
			e.setDegrees(d);
			e.recreate(false);
			
			MTColor stroke = e.getStrokeColor();
			e.setStrokeColor(new MTColor(stroke.getR(), stroke.getG(), stroke.getB(), a));
			}
			break;
		case MTGestureEvent.GESTURE_CANCELED:
		case TapAndHoldEvent.GESTURE_ENDED:
			e.setVisible(false);
			parent.removeChild(e);
			break;
		default:
			break;
		}
		return false;
	}

	
	/**
	 * The Class HoldEllipse.
	 * 
	 * @author Christopher Ruff
	 */
	private class HoldEllipse extends MTEllipse{
		
		/** The segments. */
		private int segments;

		/**
		 * Instantiates a new hold ellipse.
		 * 
		 * @param applet the applet
		 * @param centerPoint the center point
		 * @param radiusX the radius x
		 * @param radiusY the radius y
		 * @param segments the segments
		 */
		public HoldEllipse(PApplet applet, Vector3D centerPoint, float radiusX,float radiusY, int segments) {
			super(applet, centerPoint, radiusX, radiusY, segments);
			this.segments = segments;
			
		}
		
		@Override
		protected void setDefaultGestureActions() {
			//no gestures
		}
		
		/**
		 * Recreate.
		 * 
		 * @param close the close
		 */
		public void recreate(boolean close){
			if (close){
				setVertices(getVertices(segments));
			}else{
				Vertex[] verts = getVertices(segments);
				Vertex[] v = new Vertex[verts.length -1];
				System.arraycopy(verts, 0, v, 0, verts.length-1);
				setVertices(v);
			}
		}
		
		
	}
}
