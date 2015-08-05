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
package org.mt4j.input.inputSources;

import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.mt4j.AbstractMTApplication;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.input.inputData.MTWin7TouchInputEvt;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;

/**
 * Input source for native Windows 7 WM_TOUCH messages for single/multi-touch.
 * <br>Be careful to instantiate this class only ONCE!
 * 
 * @author C.Ruff
 *
 */
public class Win7NativeTouchSource extends AbstractInputSource {
	/** The Constant logger. */
	private static final ILogger logger = MTLoggerFactory.getLogger(Win7NativeTouchSource.class.getName());
	static{
//		logger.setLevel(ILogger.ERROR);
//		logger.setLevel(ILogger.DEBUG);
		logger.setLevel(ILogger.INFO);
	}
	
	static boolean loaded = false;
	
	private AbstractMTApplication app;

	private int sunAwtCanvasHandle;

	private int awtFrameHandle;
	
	private Native_WM_TOUCH_Event wmTouchEvent;

	private boolean initialized;
	
	private boolean success;
	
	private HashMap<Integer, Long> touchToCursorID;
	
	private static final String dllName32 = "Win7Touch";
	
	private static final String dllName64 = "Win7Touch64";
	
	private static final String canvasClassName = "SunAwtCanvas";
	
	
	// NATIVE METHODS //
	private native int findWindow(String tmpTitle, String subWindowTitle);
	
	private native boolean init(long HWND); 
	
	private native boolean getSystemMetrics(); 
	
	private native boolean quit(); 
	
	private native boolean pollEvent(Native_WM_TOUCH_Event myEvent);
	// NATIVE METHODS //
	
	
	//TODO remove points[] array? -> if digitizer has more than 255 touchpoints we could get out of bounds in points[]
	//TODO did we "delete [] ti;" in wndProc?
	//TODO- check dpi, if higher than 96 - if the screen is set to High DPI (more than 96 DPI),
	// you may also need to divide the values by 96 and multiply by the current DPI. (or already handled by ScreenToClient()?)
	//TODO try again getWindow() in windows -> no success in all thread (we probably need to do it in the awt-windows thread?)
	
	//TODO make singleton to avoid multiple instances
	
	/**
	 * Instantiates a new win7 native touch source.
	 *
	 * @param mtApp the mt app
	 */
	public Win7NativeTouchSource(AbstractMTApplication mtApp) {
		super(mtApp);
		this.app = mtApp;
		this.success = false;
		
		String platform = System.getProperty("os.name").toLowerCase();
		logger.debug("Platform: \"" + platform + "\"");
		
//		/*
		if (!platform.contains("windows 7")) {
			logger.error("Win7NativeTouchSource input source can only be used on platforms running windows 7!");
			return;
		}
		
		if (!loaded){
			loaded = true;
			String dllName = (MT4jSettings.getInstance().getArchitecture() == MT4jSettings.ARCHITECTURE_32_BIT)? dllName32 : dllName64;
			System.loadLibrary(dllName);
//			System.load(System.getProperty("user.dir") + File.separator + dllName + ".dll");
		}else{
			logger.error("Win7NativeTouchSource may only be instantiated once.");
			return;
		}
		
		boolean touchAvailable = this.getSystemMetrics();
		if (!touchAvailable){
			logger.error("Windows 7 Touch Input currently not available!");
			return;
		}else{
			logger.info("Windows 7 Touch Input available.");
		}
//		*/
		
		wmTouchEvent = new Native_WM_TOUCH_Event();
		wmTouchEvent.id = -1;
		wmTouchEvent.type = -1;
		wmTouchEvent.x = -1;
		wmTouchEvent.y = -1;
		
		initialized = false;
		
		touchToCursorID = new HashMap<Integer, Long>();
		
		this.getNativeWindowHandles();
		success = true;
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (isSuccessfullySetup()){
					logger.debug("Cleaning up Win7 touch source..");
					quit();
				}
			}
		}));
	}

	
//	private boolean addedArtificalTouchDown = false; //FIXME REMOVE
	
	public boolean isSuccessfullySetup() {
		return success;
	}


	@Override
	public void pre(){ //we dont have to call registerPre() again (already in superclass and called there)
		if (initialized){ //Only poll events if native c++ core was initialized successfully
			while (pollEvent(wmTouchEvent)) {
				/*
				 //FIXME TEST, make a artifical TOUCH_DOWN event REMOVE LATER!
				if (!addedArtificalTouchDown){
					addedArtificalTouchDown = true;
					wmTouchEvent.type = Native_WM_TOUCH_Event.TOUCH_DOWN;
				}
				 */
				
				switch (wmTouchEvent.type) {
				case Native_WM_TOUCH_Event.TOUCH_DOWN:{
//					logger.debug("TOUCH_DOWN ==> ID:" + wmTouchEvent.id + " x:" +  wmTouchEvent.x + " y:" +  wmTouchEvent.y);
					
					InputCursor c = new InputCursor();
					long cursorID = c.getId();
					MTWin7TouchInputEvt touchEvt = new MTWin7TouchInputEvt(this, wmTouchEvent.x, wmTouchEvent.y, wmTouchEvent.contactSizeX, wmTouchEvent.contactSizeY, MTFingerInputEvt.INPUT_STARTED, c);
					int touchID = wmTouchEvent.id;
					ActiveCursorPool.getInstance().putActiveCursor(cursorID, c);
					touchToCursorID.put(touchID, cursorID);
					this.enqueueInputEvent(touchEvt);
					
					break;
				}case Native_WM_TOUCH_Event.TOUCH_MOVE:{
//					logger.debug("TOUCH_MOVE ==> ID:" + wmTouchEvent.id + " x:" +  wmTouchEvent.x + " y:" +  wmTouchEvent.y);
//					System.out.println("Contact area X:" + wmTouchEvent.contactSizeX + " Y:" + wmTouchEvent.contactSizeY);
					
					Long cursorID = touchToCursorID.get(wmTouchEvent.id);
					if (cursorID != null){
						InputCursor c = ActiveCursorPool.getInstance().getActiveCursorByID(cursorID);
						if (c != null){
							MTWin7TouchInputEvt te = new MTWin7TouchInputEvt(this, wmTouchEvent.x, wmTouchEvent.y, wmTouchEvent.contactSizeX, wmTouchEvent.contactSizeY, MTFingerInputEvt.INPUT_UPDATED, c);
							this.enqueueInputEvent(te);	
						}
					}
					
					break;
				}case Native_WM_TOUCH_Event.TOUCH_UP:{
//					logger.debug("TOUCH_UP ==> ID:" + wmTouchEvent.id + " x:" +  wmTouchEvent.x + " y:" +  wmTouchEvent.y);

					Long cursorID = touchToCursorID.get(wmTouchEvent.id);
					if (cursorID != null){
						InputCursor c = ActiveCursorPool.getInstance().getActiveCursorByID(cursorID);
						if (c != null){
							MTWin7TouchInputEvt te = new MTWin7TouchInputEvt(this, wmTouchEvent.x, wmTouchEvent.y, wmTouchEvent.contactSizeX, wmTouchEvent.contactSizeY, MTFingerInputEvt.INPUT_ENDED, c);
							this.enqueueInputEvent(te);
						}
						ActiveCursorPool.getInstance().removeCursor(cursorID);
						touchToCursorID.remove(wmTouchEvent.id);
					}
					
					break;
				}default:
					break;
				}
			}
		}

		super.pre();
	}
	
	
	
	private void getNativeWindowHandles(){
		if (app.frame == null){
			logger.error("applet.frame == null! -> cant set up windows 7 input!");
			return;
		}
		
		//TODO kind of hacky way of getting the HWND..but there seems to be no real alternative(?)
		final String oldTitle = app.frame.getTitle();
		final String tmpTitle = "Initializing Native Windows 7 Touch Input " + Math.random();
		app.frame.setTitle(tmpTitle);
		logger.debug("Temp title: " + tmpTitle);
		
		//FIXME TEST REMOVE
		//Window window = SwingUtilities.getWindowAncestor(app);
//		AWTUtilities.setWindowOpacity(window, 0.5f); //works!
				
		//Invokelater because of some crash issue 
		//-> maybe we need to wait a frame until windows is informed of the window name change
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				int awtCanvasHandle = 0;
				try {
//					//TODO also search for window class?
					awtCanvasHandle = (int)findWindow(tmpTitle, canvasClassName);
					setSunAwtCanvasHandle(awtCanvasHandle);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				app.frame.setTitle(oldTitle); //Reset title text
			}
		});
	}

	
	private void setTopWindowHandle(int HWND){
		if (HWND > 0){
			this.awtFrameHandle = HWND;
			logger.debug("-> Found AWT HWND: " + this.awtFrameHandle);
		}else{
			logger.error("-> Couldnt retrieve the top window handle!");
		}
	}
	
	
	private void setSunAwtCanvasHandle(int HWND){
		if (HWND > 0){
			this.sunAwtCanvasHandle = HWND;
			logger.debug("-> Found SunAwtCanvas HWND: " + this.sunAwtCanvasHandle);
			//Initialize c++ core (subclass etc)
			this.init(this.sunAwtCanvasHandle);
			this.initialized = true;
		}else{
			logger.error("-> Couldnt retrieve the SunAwtCanvas handle!");
		}
	}
	
	private class Native_WM_TOUCH_Event{
		//can be real enums in Java 5.0.
	    /** The Constant TOUCH_DOWN. */
	    public static final int TOUCH_DOWN = 0;
	    
	    /** The Constant TOUCH_MOVE. */
	    public static final int TOUCH_MOVE = 1;
	    
	    /** The Constant TOUCH_UP. */
	    public static final int TOUCH_UP = 2;
	    
	    /** The type. */
	    public int type;
	    
	    /** The id. */
	    public int id;
	    
	    /** The x value. */
	    public int x;
	    
	    /** The y value. */
	    public int y;
	    
	    /** The contact size area X dimension */
	    public int contactSizeX;
	    
	    /** The contact size area Y dimension */
	    public int contactSizeY;
	}

}
