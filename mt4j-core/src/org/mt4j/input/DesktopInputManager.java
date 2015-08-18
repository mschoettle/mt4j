package org.mt4j.input;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.mt4j.AbstractMTApplication;
import org.mt4j.MTApplication;
import org.mt4j.input.inputSources.KeyboardInputSource;
import org.mt4j.input.inputSources.MouseInputSource;
import org.mt4j.input.inputSources.MultipleMiceInputSource;
import org.mt4j.input.inputSources.Tuio2DCursorInputSource;
import org.mt4j.input.inputSources.Tuio2dObjectInputSource;
import org.mt4j.input.inputSources.Win7NativeTouchSource;
import org.mt4j.util.MT4jSettings;

/**
 * The Class DesktopInputManager.
 */
public class DesktopInputManager extends InputManager{
	
	/**
	 * Instantiates a new desktop input manager.
	 *
	 * @param app the app
	 */
	public DesktopInputManager(AbstractMTApplication app) {
		this(app, true);
	}
	
	/**
	 * Instantiates a new desktop input manager.
	 *
	 * @param app the app
	 * @param registerDefaultSources the register default sources
	 */
	public DesktopInputManager(AbstractMTApplication app, boolean registerDefaultSources) {
		super(app, registerDefaultSources);
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.InputManager#registerDefaultInputSources()
	 */
	@Override
	/**
	 * Initialize default input sources.
	 */
	protected void registerDefaultInputSources(){
		super.registerDefaultInputSources();
		
		boolean enableMultiMouse = false;
		Properties properties = new Properties();

		try {
			FileInputStream fi = new FileInputStream(MT4jSettings.getInstance().getDefaultSettingsPath() + "Settings.txt");
			properties.load(fi); 
			enableMultiMouse = Boolean.parseBoolean(properties.getProperty("MultiMiceEnabled", "false").trim());
		}catch (Exception e) {
			logger.debug("Failed to load Settings.txt from the File system. Trying to load it from classpath..");
			try {
				InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("Settings.txt");
				if (in != null){
					properties.load(in);
					enableMultiMouse = Boolean.parseBoolean(properties.getProperty("MultiMiceEnabled", "false").trim());
				}else{
					logger.debug("Couldnt load Settings.txt as a resource. Using defaults.");
				}
			} catch (IOException e1) {
				logger.error("Couldnt load Settings.txt. Using defaults.");
				e1.printStackTrace();
			}
		}

		if (enableMultiMouse){
			try {
				//Register single or multiple mice input source
				int connectedMice = MultipleMiceInputSource.getConnectedMouseCount();
				//	    		/*
				logger.info("Found mice: " + connectedMice);
				if (connectedMice >= 2){ //FIXME should be > 1, but manymouse often detects more that arent there!?
					logger.info("-> Multiple Mice detected!");
					MultipleMiceInputSource multipleMice = new MultipleMiceInputSource(app);
	    			multipleMice.setMTApp(app);
	    			this.registerInputSource(multipleMice);
	    			this.hideCursorInFrame();
	    		}else{
//	    			*/
	    			MouseInputSource mouseInput = new MouseInputSource(app);
	    			this.registerInputSource(mouseInput);
	    		}
//	    		*/
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		//Use default mouse input source
	    		MouseInputSource mouseInput = new MouseInputSource(app);
	    		this.registerInputSource(mouseInput);
	    	}
	    }
	    else{
//	    	*/
	    	MouseInputSource mouseInput = new MouseInputSource(app);
	    	this.registerInputSource(mouseInput);
	    }
//	    */

	    //Check if we run windows 7
	    if (System.getProperty("os.name").toLowerCase().contains("windows 7")){
	    	Win7NativeTouchSource win7NativeInput = new Win7NativeTouchSource(app);
	    	if (win7NativeInput.isSuccessfullySetup()){
	    		this.registerInputSource(win7NativeInput);
	    	}
	    }
	    
	    //check which versions it supports and only start there!
	    /*
	    if (System.getProperty("os.name").toLowerCase().contains("mac os x")){
	    	this.registerInputSource(new MacTrackpadSource(app));
	    }
	    */

	    //Register keyboard multitouch-emulation input source
	    KeyboardInputSource keyInput = new KeyboardInputSource(app);
		this.registerInputSource(keyInput);
		
//		MuitoInputSource muitoInput = new MuitoInputSource(pa, "localhost", 6666);
		
		//Register TUIO protocol input sources
		if (app instanceof MTApplication) {
			MTApplication desktopApp = (MTApplication) app;
			this.registerInputSource(new Tuio2DCursorInputSource(desktopApp));
			this.registerInputSource(new Tuio2dObjectInputSource(desktopApp));
		}
	}



	/**
	 * Hides the mousecursor in multiple mice mode.
	 */
	protected void hideCursorInFrame(){
		int[] pixels = new int[16 * 16];
		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor =
			Toolkit.getDefaultToolkit().createCustomCursor
			(image, new Point(0, 0), "invisibleCursor");
		app.frame.setCursor(transparentCursor);
	}

}
