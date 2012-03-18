/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.input.inputSources;

import java.util.Collection;
import java.util.WeakHashMap;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.sceneManagement.ISceneChangeListener;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.sceneManagement.SceneChangeEvent;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.manyMouse.ManyMouse;
import org.mt4j.util.manyMouse.ManyMouseEvent;
import org.mt4j.util.math.Vector3D;


/**
 * The Class MultipleMiceInputSource.
 * @author Christopher Ruff
 */
public class MultipleMiceInputSource extends AbstractInputSource {
	
	/** The mice. */
	private int mice;
	
	/** The event. */
	private ManyMouseEvent event;
	
	/** The device to mouse info. */
	private WeakHashMap<Integer, MouseInfo> deviceToMouseInfo;
	
	/** The max screen w. */
	private int maxScreenW;
	
	/** The max screen h. */
	private int maxScreenH;

	/** The mt app. */
	private AbstractMTApplication mtApp;
	
	/** The default center cam. */
	private Icamera defaultCenterCam;
	
	/** The current scene. */
	private Iscene currentScene;
	
	/**
	 * Instantiates a new multiple mice input source.
	 * 
	 * @param applet the applet
	 */
	public MultipleMiceInputSource(AbstractMTApplication applet) {
		super(applet);
		
		this.maxScreenW = MT4jSettings.getInstance().getWindowWidth();
		this.maxScreenH = MT4jSettings.getInstance().getWindowHeight();
		
		mice = ManyMouse.Init();

		System.out.println("ManyMouse.Init() reported " + mice + ".");
		for (int i = 0; i < mice; i++){
			System.out.println("Mouse #" + i + ": " + ManyMouse.DeviceName(i));
		}
		System.out.println();

		// Allocate one that PollEvent fills in so we aren't spamming the
		//  memory manager with throwaway objects for each event.
		event = new ManyMouseEvent(); //TODO mal austesten immer neuen zu machen
		
		deviceToMouseInfo = new WeakHashMap<Integer, MouseInfo>();

		applet.registerPost(this);

		applet.registerDispose(this);
		
		defaultCenterCam = new MTCamera(applet);
		
		currentScene = null;
	}
	
	/**
	 * Gets the connected mouse count.
	 * 
	 * @return the connected mouse count
	 */
	public static int getConnectedMouseCount(){
		 int mice = ManyMouse.Init();
		 try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 ManyMouse.Quit();
		 return mice;
	}
	
	
	
	/**
	 * Post.
	 */
	public void post(){
		if (mice > 0){
			while (ManyMouse.PollEvent(event)) {
				
//				System.out.print("Mouse #");
//				System.out.print(event.device);
//				System.out.print(" ");

				switch (event.type){
				case ManyMouseEvent.ABSMOTION:
					mouseMovedAbs(event);
//					System.out.print("absolute motion ");
//					if (event.item == 0) // x axis
//					System.out.print("X axis ");
//					else if (event.item == 1) // y axis
//					System.out.print("Y axis ");
//					else
//					System.out.print("? axis ");  // error?
//					System.out.print(event.value);
					break;
				case ManyMouseEvent.RELMOTION:
					mouseMovedRel(event);
//					System.out.print("relative motion ");
//					if (event.item == 0) // x axis
//					System.out.print("X axis ");
//					else if (event.item == 1) // y axis
//					System.out.print("Y axis ");
//					else
//					System.out.print("? axis ");  // error?

//					System.out.print(event.value);
					break;
				case ManyMouseEvent.BUTTON:
//					System.out.print("mouse button ");
//					System.out.print(event.item);
					if (event.value == 0){
//						System.out.print(" up");
						buttonUp(event);
					}else{
						buttonPressed(event);
//						System.out.print(" down");
					}
					break;
				case ManyMouseEvent.SCROLL:
					/*
					System.out.print("scroll wheel ");
					if (event.item == 0)
					{
						if (event.value > 0)
							System.out.print("up");
						else
							System.out.print("down");
					}
					else
					{
						if (event.value > 0)
							System.out.print("right");
						else
							System.out.print("left");
					}
					*/
					break;
				case ManyMouseEvent.DISCONNECT:
					System.out.print("disconnect");
					System.out.println(" Device: #" + event.device);
					//Try to remove the mouse and the circle of this mouse
					MouseInfo deviceInfo = deviceToMouseInfo.get(event.device);
					if (deviceInfo != null){
						try {
							if (deviceInfo.ellipse != null){
								if (mtApp != null){
									mtApp.getCurrentScene().getCanvas().removeChild(deviceInfo.ellipse);
								}
							}
							this.deviceToMouseInfo.remove(event.device);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mice--;
					break;
				default:
					System.out.print("Unknown event: ");
					System.out.print(event.type);
				break;
				} // switch
//				System.out.println();
			}
		}
	}


	/**
	 * Dispose.
	 */
	public void dispose(){
		ManyMouse.Quit();
	}
	
	
	/**
	 * Sets the mT app.
	 * 
	 * @param mtApp the new mT app
	 */
	public void setMTApp(AbstractMTApplication mtApp){
		this.mtApp = mtApp;
//		this.currentCanvas = mtApp.getCurrentScene().getMainCanvas();

		//Add scene change listener to mt app
		this.mtApp.addSceneChangeListener(new ISceneChangeListener(){
			public void processSceneChangeEvent(SceneChangeEvent sc) {
				sceneChange(sc.getLastScene(), sc.getNewScene());
			}
		});
	}
	
	
	
	/**
	 * Scene change.
	 * 
	 * @param lastScene the last scene
	 * @param newScene the new scene
	 */
	private void sceneChange(Iscene lastScene, Iscene newScene){
		MTCanvas oldCanvas = lastScene.getCanvas();
		MTCanvas newCanvas = newScene.getCanvas();
		currentScene = newScene;
		
		System.out.println("Removing multiple mice cursors from old and add to new canvas.");
		Collection<MouseInfo> mouseInfos = deviceToMouseInfo.values();
        for (MouseInfo mouseInfo : mouseInfos) {
            if (mouseInfo.ellipse != null) {
                float currentEllipseWidth = 6;
                Vector3D v = new Vector3D(currentEllipseWidth, 0, 0);
                v.transformDirectionVector(currentScene.getCanvas().getGlobalInverseMatrix());
                float newEllipseWidth = currentEllipseWidth = v.length();
                mouseInfo.ellipse.setWidthXYGlobal(newEllipseWidth * 2);
                try {
                    oldCanvas.removeChild(mouseInfo.ellipse);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    newCanvas.addChild(mouseInfo.ellipse);
                }
//				mouseInfo.ellipse.setCustomAndGlobalCam(currentScene.getSceneCam(), defaultCenterCam);
                mouseInfo.ellipse.attachCamera(defaultCenterCam);
            }
        }
	}
	
	/**
	 * Return the mouseinfo of the corresponding mouse device or lazily create
	 * the mouse info and return it if not initialized yet.
	 * 
	 * @param event the event
	 * 
	 * @return the or init device info
	 */
	private MouseInfo getOrInitDeviceInfo(ManyMouseEvent event){
		int device = event.device;
		
		MouseInfo deviceInfo = deviceToMouseInfo.get(device);
		if (deviceInfo == null){
			MouseInfo newMouseInfo = new MouseInfo();
			newMouseInfo.device = device;
			newMouseInfo.x = 0;
			newMouseInfo.y = 0;
			newMouseInfo.lastX = 0;
			newMouseInfo.lastY = 0;
			deviceToMouseInfo.put(device, newMouseInfo);

			this.attachCircleToMouseInfos(newMouseInfo);
			
			return	newMouseInfo; 
		}else{
			return deviceInfo;
		}
	}
	
	
	
	/**
	 * Attach circle to mouse infos.
	 * 
	 * @param mouseInfo the mouse info
	 */
	private void attachCircleToMouseInfos(MouseInfo mouseInfo){
		if (this.mtApp != null){
//			MTCanvas canvas = mtApp.getCurrentScene().getMainCanvas();
			
			float currentEllipseWidth = 6;
			if (currentScene != null){
				Vector3D v = new Vector3D(currentEllipseWidth,0,0);
				v.transformDirectionVector(currentScene.getCanvas().getGlobalInverseMatrix());
				currentEllipseWidth = v.length();
			}else{
				
			}
//			mouseInfo.ellipse.setWidthXYGlobal(newEllipseWidth);
			
			MTEllipse fingerCircle = new MTEllipse(mtApp, new Vector3D(mouseInfo.x, mouseInfo.y), currentEllipseWidth, currentEllipseWidth, 10);
			fingerCircle.setPickable(false);
			
//			fingerCircle.setCustomAndGlobalCam(currentScene.getSceneCam(), defaultCenterCam);
			fingerCircle.attachCamera(defaultCenterCam);
			
			//fingerCircle.setFillColor(50, 225, 230, 200);
//			fingerCircle.setNoFill(true);
			fingerCircle.setFillColor(new MTColor(255,50,50, 190));
//			fingerCircle.setFillColor(Tools3D.getRandom(140, 255),Tools3D.getRandom(110, 255),Tools3D.getRandom(135, 255), 150);
			fingerCircle.setDrawSmooth(true);
//			fingerCircle.setStrokeWeight(2);
//			fingerCircle.setStrokeColor(100,100,100, 150);
			fingerCircle.setNoStroke(true);
			fingerCircle.setDepthBufferDisabled(true);
			
			//Add to mouseinfo
			mouseInfo.ellipse = fingerCircle;
			//Add to canvas
//			currentCanvas.addChild(fingerCircle);
//			mtApp.getCurrentScene().getMainCanvas().addChild(fingerCircle);
		}
	}
	
	
	/**
	 * Button pressed.
	 * 
	 * @param event the event
	 */
	private void buttonPressed(ManyMouseEvent event) {
		int device = event.device;
		
		MouseInfo mouseInfo = this.getOrInitDeviceInfo(event);
		mouseInfo.isButtonPressed = true;
		
		if (mouseInfo.ellipse != null){
			try {
				if (mtApp != null && currentScene != null){
					//TODO camera fehler manchmal? 
					currentScene.getCanvas().removeChild(mouseInfo.ellipse);
					currentScene.getCanvas().addChild(mouseInfo.ellipse);
//					//Draw circles ontop
//					MTCanvas canvas = mtApp.getCurrentScene().getMainCanvas();
//					canvas.removeChild(mouseInfo.ellipse);
//					canvas.addChild(mouseInfo.ellipse);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		InputCursor m = new InputCursor();
		MTFingerInputEvt touchEvt = new MTFingerInputEvt(this, mouseInfo.x, mouseInfo.y, MTFingerInputEvt.INPUT_STARTED, m);
//		m.addEvent(touchEvt);
		
//		long motionID = m.getId();
		ActiveCursorPool.getInstance().putActiveCursor(device, m);
		this.enqueueInputEvent(touchEvt);
		
//		System.out.println("Motion added on device: #" + device);
	}

	
	/**
	 * Mouse moved rel.
	 * 
	 * @param event the event
	 */
	private void mouseMovedRel(ManyMouseEvent event) {
		int device = event.device;
		MouseInfo mouseInfo = this.getOrInitDeviceInfo(event);

		//TODO warum immer halb so viel? testen ob resolution zu tun hat oder was
		int advanceValue = event.value *2;

		switch (event.item) {
		case 0:
//			System.out.print("X axis ");
			int newX = mouseInfo.x + advanceValue; 
			if (newX > this.maxScreenW){
				newX = this.maxScreenW;
				advanceValue = 0;
			}else if(newX < 0){
				newX = 0;
				advanceValue = 0;
			}
			mouseInfo.lastX = mouseInfo.x;
			mouseInfo.x 	= newX;
			
//			//Move Circle
//			if (mouseInfo.ellipse != null){
//				mouseInfo.ellipse.translate(new Vector3D(advanceValue, 0, 0)); //TODO nicht immer neuen vector machen
//			}
			break;
		case 1:
//			System.out.print("Y axis ");
			int newY = mouseInfo.y + advanceValue;
			if (newY > this.maxScreenH){
				newY = this.maxScreenH;
				advanceValue = 0;
			}else if(newY < 0){
				newY = 0;
				advanceValue = 0;
			}
			mouseInfo.lastY = mouseInfo.y;
			mouseInfo.y = newY;
			
//			//Move Circle
//			if (mouseInfo.ellipse != null){
//				mouseInfo.ellipse.translate(new Vector3D(0, advanceValue, 0)); //TODO nicht immer neuen vector machen
//			}
			break;
		default:
			System.out.print("? axis ");  // error?
		break;
		}

		//Move Circle
		if (mouseInfo.ellipse != null){
//			mouseInfo.ellipse.translate(new Vector3D(0, advanceValue, 0)); //TODO nicht immer neuen vector machen
//			mouseInfo.ellipse.setPositionParentRelative(new Vector3D(mouseInfo.x, mouseInfo.y,0));
			if (currentScene!= null){
				//If canvas is scaled
				Vector3D dir = new Vector3D(mouseInfo.x, mouseInfo.y,0);
				dir.transform(currentScene.getCanvas().getGlobalInverseMatrix());
				mouseInfo.ellipse.setPositionRelativeToParent(dir);
			}else{
				mouseInfo.ellipse.setPositionRelativeToParent(new Vector3D(mouseInfo.x, mouseInfo.y,0));
			}
		}

		if (mouseInfo.isButtonPressed){
			InputCursor m = ActiveCursorPool.getInstance().getActiveCursorByID(device);
			MTFingerInputEvt te = new MTFingerInputEvt(this, mouseInfo.x, mouseInfo.y, MTFingerInputEvt.INPUT_UPDATED, m);
			//			m.addEvent(te);
//			System.out.println("Motion update on device: #" + device+  " X:" + mouseInfo.x + " Y:" + mouseInfo.y);
			this.enqueueInputEvent(te);
		}
	}

	/**
	 * Mouse moved abs.
	 * 
	 * @param event2 the event2
	 */
	private void mouseMovedAbs(ManyMouseEvent event2) {
		int device = event.device;
		MouseInfo mouseInfo = this.getOrInitDeviceInfo(event);

//		int advanceValue = event.value *2;
		
		switch (event.item) {
		case 0:
//			System.out.print("X axis ");
			mouseInfo.lastX = mouseInfo.x;
			mouseInfo.x = event.value;
			break;
		case 1:
//			System.out.print("Y axis ");
			mouseInfo.lastY = mouseInfo.y;
			mouseInfo.y = event.value;
			break;
		default:
			System.out.print("? axis ");  // error?
		break;
		}

		//Move Circle
		if (mouseInfo.ellipse != null){
//			mouseInfo.ellipse.translate(new Vector3D(0, advanceValue, 0)); //TODO nicht immer neuen vector machen
			if (currentScene!= null){
				//If canvas is scaled
				Vector3D dir = new Vector3D(mouseInfo.x, mouseInfo.y,0);
				dir.transform(currentScene.getCanvas().getGlobalInverseMatrix());
				mouseInfo.ellipse.setPositionRelativeToParent(dir);
			}else{
				mouseInfo.ellipse.setPositionRelativeToParent(new Vector3D(mouseInfo.x, mouseInfo.y,0));
			}
		}

		if (mouseInfo.isButtonPressed){
			InputCursor m = ActiveCursorPool.getInstance().getActiveCursorByID(device);
			MTFingerInputEvt te = new MTFingerInputEvt(this, mouseInfo.x, mouseInfo.y, MTFingerInputEvt.INPUT_UPDATED, m);
//			m.addEvent(te);
//			System.out.println("Motion update on device: #" + device+  " X:" + mouseInfo.x + " Y:" + mouseInfo.y);
			this.enqueueInputEvent(te);
		}
	}


	
	/**
	 * Button up.
	 * 
	 * @param event the event
	 */
	private void buttonUp(ManyMouseEvent event) {
		int device = event.device;
		
		MouseInfo mouseInfo = this.getOrInitDeviceInfo(event);
		if (mouseInfo.isButtonPressed){
			mouseInfo.isButtonPressed = false;

			InputCursor m = ActiveCursorPool.getInstance().getActiveCursorByID(device);
//			System.out.println("Motion ended on device: #" + device);

			MTFingerInputEvt te;
			if (m.getCurrentEvent() != null)
				te = new MTFingerInputEvt(this, m.getCurrentEvent().getX(), m.getCurrentEvent().getY(), MTFingerInputEvt.INPUT_ENDED, m);
			else
				te = new MTFingerInputEvt(this, 0,0, MTFingerInputEvt.INPUT_ENDED, m);

//			m.addEvent(te); //werden nicht hier geadded sondern synchroniesert mit dem PApplet thread in den analyzern, so dass immer nur 1
			// 1 te geadded wird und dann wieder verarbeitet, dann der n�chste

			ActiveCursorPool.getInstance().removeCursor(device);
			this.enqueueInputEvent(te);
		}
	}

	

	/**
	 * The Class MouseInfo.
	 * 
	 * @author C.Ruff
	 */
	private class MouseInfo{
		
		/** The device. */
		int device;
		
		/** The x. */
		int x;
		
		/** The y. */
		int y;
		
		/** The last x. */
		int lastX;
		
		/** The last y. */
		int lastY;
		
		/** The is button pressed. */
		boolean isButtonPressed = false;
		
		/** The ellipse. */
		MTEllipse ellipse;
	}
	
//	@Override
//	public boolean firesEventType(Class<? extends MTInputEvent> evtClass){
//		return (evtClass == MTFingerInputEvt.class);
//	}

}
