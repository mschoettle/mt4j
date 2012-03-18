/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.input.inputProcessors.globalProcessors;

import java.util.HashMap;
import java.util.Map;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.widgets.MTOverlayContainer;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * The Class CursorTracer. A global input processor tracking all AbstractCursorInputEvt events and
 * displays a circle at that position.
 * 
 * @author Christopher Ruff
 */
public class CursorTracer extends AbstractGlobalInputProcessor{
	
	/** The app. */
	private AbstractMTApplication app;
	
	/** The cursor id to display shape. */
	private Map<InputCursor, AbstractShape>cursorIDToDisplayShape;
	
	/** The scene. */
	private Iscene scene;
	
	/** The overlay group. */
	private MTComponent overlayGroup;
	
	private float ellipseRadius = 15;


	/**
	 * Instantiates a new cursor tracer.
	 * 
	 * @param mtApp the mt app
	 * @param currentScene the current scene
	 */
	public CursorTracer(AbstractMTApplication mtApp, Iscene currentScene){
		this.app = mtApp;
		this.scene = currentScene;
		this.cursorIDToDisplayShape = new HashMap<InputCursor, AbstractShape>();
		
		if (PlatformUtil.isAndroid()){
			ellipseRadius = 30;
		}
		
//		this.overlayGroup = new MTComponent(app, "Cursor Trace group", new MTCamera(app));
//		this.overlayGroup.setDepthBufferDisabled(true);
//		//Send overlay group to front again if it isnt - check each frame if its on front!
//		overlayGroup.setController(new IMTController() {
//			public void update(long timeDelta) {
//				MTComponent parent = overlayGroup.getParent();
//				if (parent != null){
//					int childCount = parent.getChildCount();
//					if (childCount > 0
//						&& !parent.getChildByIndex(childCount-1).equals(overlayGroup))
//					{
//						app.invokeLater(new Runnable() {
//							public void run(){
//								MTComponent parent = overlayGroup.getParent();
//								if (parent != null){
//									parent.removeChild(overlayGroup);
//									parent.addChild(overlayGroup);
//								}
//							}
//						});
//					}
//				}
//			}
//		});
		
//		MTOverlayContainer overlay = checkForExistingOverlay(scene.getCanvas());
//		
		this.overlayGroup = new MTOverlayContainer(app, "Cursor Trace group");
		mtApp.invokeLater(new Runnable() {
			public void run() {
				scene.getCanvas().addChild(overlayGroup);
			}
		});
		
//		//FIXME REMOVE
//		compToCreationTime = new HashMap<MTComponent, Long>();
//		mtApp.registerPre(this);
	}

//	private HashMap<MTComponent, Long> compToCreationTime; //FIXME REMOVE LATER
	
//	public void pre(){
//		Set<MTComponent> comps = compToCreationTime.keySet();
//		long currentTime = System.currentTimeMillis();
//		for (MTComponent component : comps) {
//			InputCursor c = (InputCursor) component.getUserData("Cursor");
//			Long creationTime = compToCreationTime.get(component);
//			long duration = currentTime - creationTime;
//			if (duration > 1000){
//				System.out.println("--> CURSOR: " + c.getId() + " seems to be STUCK!");
//			}
//		}
//	}
	
//	private MTOverlayContainer checkForExistingOverlay(MTCanvas canvas){
//		MTComponent[] canvasChildren = canvas.getChildren();
//		MTOverlayContainer overlay = null;
//		for (int i = 0; i < canvasChildren.length; i++) {
//			MTComponent component = canvasChildren[i];
//			if (component instanceof MTOverlayContainer) {
////				MTOverlayContainer foundOverlay = (MTOverlayContainer) component;
//				overlay = (MTOverlayContainer)component;
//			}
//		}
//		return overlay;
//	}
	
	/**
	 * Creates the display component.
	 * 
	 * @param applet the applet
	 * @param position the position
	 * 
	 * @return the abstract shape
	 */
	protected AbstractShape createDisplayComponent(PApplet applet, Vector3D position){
		MTEllipse displayShape = new CursorEllipse(applet, position, ellipseRadius, 15);
		displayShape.setPickable(false);
		displayShape.setNoFill(true);
		displayShape.setDrawSmooth(true);
		displayShape.setStrokeWeight(2);
		displayShape.setStrokeColor(new MTColor(100, 130, 220, 255));
		return displayShape;
	}
	
	private class CursorEllipse extends MTEllipse{
		public CursorEllipse(PApplet applet, Vector3D centerPoint,float radiusX, int segments) {
			super(applet, centerPoint, radiusX, radiusX, segments);
		}
		@Override
		protected IBoundingShape computeDefaultBounds() {
			return null;
		}
		@Override
		protected void setDefaultGestureActions() {
			//Dont need gestures
		}
	}
	
	

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor#processInputEvtImpl(org.mt4j.input.inputData.MTInputEvent)
	 */
	@Override
	public void processInputEvtImpl(MTInputEvent inputEvent) {
		if (inputEvent instanceof AbstractCursorInputEvt) {
			AbstractCursorInputEvt cursorEvt = (AbstractCursorInputEvt)inputEvent;
			InputCursor c = ((AbstractCursorInputEvt)inputEvent).getCursor();
			Vector3D position = new Vector3D(cursorEvt.getX(), cursorEvt.getY());

			AbstractShape displayShape = null;
			switch (cursorEvt.getId()) {
			case AbstractCursorInputEvt.INPUT_STARTED:
				displayShape = createDisplayComponent(app, position);
				cursorIDToDisplayShape.put(c, displayShape);
				overlayGroup.addChild(displayShape);
				displayShape.setPositionGlobal(position);
				
//				compToCreationTime.put(displayShape, System.currentTimeMillis()); //FIXME REMOVE
//				displayShape.setUserData("Cursor", c);//FIXME REMOVE
				break;
			case AbstractCursorInputEvt.INPUT_UPDATED:
				displayShape = cursorIDToDisplayShape.get(c);
				if (displayShape != null){
					displayShape.setPositionGlobal(position);
				}
				break;
			case AbstractCursorInputEvt.INPUT_ENDED:
				displayShape = cursorIDToDisplayShape.remove(c);
				if (displayShape != null){
					displayShape.destroy();
				}
				break;
			default:
				break;
			}
		}
	}
}
