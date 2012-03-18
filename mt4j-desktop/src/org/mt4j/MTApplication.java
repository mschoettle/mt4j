package org.mt4j;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.media.opengl.GL;
import javax.swing.ImageIcon;

import org.mt4j.input.DesktopInputManager;
import org.mt4j.util.DesktopPlatformUtil;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.SettingsMenu;
import org.mt4j.util.animation.ani.AniAnimation;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.fontFactories.BitmapFontFactory;
import org.mt4j.util.font.fontFactories.SvgFontFactory;
import org.mt4j.util.font.fontFactories.TTFontFactory;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.Log4jLogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.modelImporter.ModelImporterFactory;
import org.mt4j.util.modelImporter.file3ds.Model3dsFileFactory;
import org.mt4j.util.modelImporter.fileObj.ModelObjFileFactory;
import org.mt4j.util.opengl.GLCommon;
import org.mt4j.util.opengl.GLFBO;
import org.mt4j.util.opengl.JoglGL10;
import org.mt4j.util.opengl.JoglGL11;
import org.mt4j.util.opengl.JoglGL20Plus;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;


public abstract class MTApplication extends AbstractMTApplication {
	private static final long serialVersionUID = 1L;
	
	static{
		//Initialize Loggin facilities  - IMPORTANT TO DO THIS ASAP!//////
		MTLoggerFactory.setLoggerProvider(new Log4jLogger()); //FIXME TEST
//		MTLoggerFactory.setLoggerProvider(new JavaLogger()); //FIXME TEST
		logger = MTLoggerFactory.getLogger(AbstractMTApplication.class.getName());
		logger.setLevel(ILogger.INFO);
	}
	
	private static boolean settingsLoadedFromFile = false; //cant initialize in constructor, need it before that!
	protected ImageIcon mt4jIcon;
	public static String CUSTOM_OPENGL_GRAPHICS = "org.mt4j.util.opengl.CustomPGraphicsOpenGL"; //PApplet.OPENGL
//	public static String CUSTOM_OPENGL_GRAPHICS = OPENGL; //PApplet.OPENGL
	
	
	
	public MTApplication(){
		super();
	}
	
	
	
	/**
	 * Initializes the processings settings.
	 * Call this method in your main method prior to anything else!
	 */
	public static void initialize(){
		initialize(new CurrentClassGetter().getClassName());
	}
	
	public static void initialize(boolean showSettingsMenu){
		initialize(new CurrentClassGetter().getClassName(), showSettingsMenu);
	}
	
	
	public static void initialize(String classToInstantiate){
		initialize(classToInstantiate, false);
	}
	
	
	/**
	 * Initializes the processing's settings.
	 * Call this method in your main method prior to anything else!
	 * We have to provide the fully qualified name to the class that
	 * we are calling this from. (Should be our MTAplication extended class)
	 * This is needed because processing will use the reflection api to instantiate
	 * an instance of the MTApplication class.
	 * <br>E.g.: <code>initialize("myPackage.myMainClass");</code>
	 *
	 * @param classToInstantiate the class to instantiate
	 * @param showSettingsMenu show settings menu
	 */
	public static void initialize(String classToInstantiate, boolean showSettingsMenu){
		if (showSettingsMenu){
			settingsLoadedFromFile = true;
			SettingsMenu menu = new SettingsMenu(classToInstantiate);
			menu.setVisible(true);
		}else{
			getSettingsFromFile();

			// Launch processing PApplet main() function
			if (MT4jSettings.getInstance().isFullscreen()){
				if (MT4jSettings.getInstance().isFullscreenExclusive()){
					PApplet.main(new String[] {
							"--display=" + MT4jSettings.getInstance().getDisplay(),
							"--present", 
							"--exclusive", 
							"--bgcolor=#000000", 
							"--hide-stop",
							classToInstantiate}
					); 
				}else{
					PApplet.main(new String[] {
							"--display=" + MT4jSettings.getInstance().getDisplay(),
							"--present", 
							"--bgcolor=#000000", 
							"--hide-stop",
							classToInstantiate}
					); 
				}
			}else{
				PApplet.main(new String[] { 
						"--display=" + MT4jSettings.getInstance().getDisplay(),
						classToInstantiate }); 
			}
		}

	}
	
	
	protected static void getSettingsFromFile(){
		 //Load some properties from Settings.txt file
		 Properties properties = new Properties();
		 try {
			 try {
				 FileInputStream fi = new FileInputStream(MT4jSettings.getInstance().getDefaultSettingsPath() + "Settings.txt");
				 properties.load(fi);	
			} catch (FileNotFoundException e) {
				logger.debug("Couldnt load Settings.txt from the File system. Trying to load it as a resource..");
				InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("Settings.txt");
				 if (in != null){
					 properties.load(in);	
				 }else{
					 logger.debug("Couldnt load Settings.txt as a resource. Using defaults.");
					 throw new FileNotFoundException("Couldnt load Settings.txt as a resource");
				 }
			}
	
			 MT4jSettings.fullscreen = Boolean.parseBoolean(properties.getProperty("Fullscreen", Boolean.valueOf(MT4jSettings.getInstance().isFullscreen()).toString()).trim());
			 //Use java's fullscreen exclusive mode (real fullscreen) or just use an undecorated window at fullscreen size 
			 MT4jSettings.getInstance().fullscreenExclusive = Boolean.parseBoolean(properties.getProperty("FullscreenExclusive", Boolean.valueOf(MT4jSettings.getInstance().isFullscreenExclusive()).toString()).trim());
			 //Which display to use for fullscreen
			 MT4jSettings.getInstance().display = Integer.parseInt(properties.getProperty("Display", String.valueOf(MT4jSettings.getInstance().getDisplay())).trim());
	
			 MT4jSettings.getInstance().windowWidth = Integer.parseInt(properties.getProperty("DisplayWidth", String.valueOf(MT4jSettings.getInstance().getWindowWidth())).trim());
			 MT4jSettings.getInstance().windowHeight = Integer.parseInt(properties.getProperty("DisplayHeight", String.valueOf(MT4jSettings.getInstance().getWindowHeight())).trim());
			 
			 //FIXME at fullscreen really use the screen dimension? -> we need to set the native resoultion ourselves!
			 //so we can have a lower fullscreen resolution than the screen dimensions
			 if (MT4jSettings.getInstance().isFullscreen() && !MT4jSettings.getInstance().isFullscreenExclusive()){
				 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				 MT4jSettings.getInstance().windowWidth = screenSize.width;
				 MT4jSettings.getInstance().windowHeight = screenSize.height;
			 }
			 /*
			 //Comment this to not change the window width to the screen width in fullscreen mode
			 else{
				 
			 }
			 */
			 
			 MT4jSettings.getInstance().maxFrameRate = Integer.parseInt(properties.getProperty("MaximumFrameRate", String.valueOf(MT4jSettings.getInstance().getMaxFrameRate())).trim());
			 MT4jSettings.getInstance().renderer = Integer.parseInt(properties.getProperty("Renderer", String.valueOf(MT4jSettings.getInstance().getRendererMode())).trim());
			 MT4jSettings.getInstance().numSamples = Integer.parseInt(properties.getProperty("OpenGLAntialiasing", String.valueOf(MT4jSettings.getInstance().getNumSamples())).trim());
	
			 MT4jSettings.getInstance().vSync = Boolean.parseBoolean(properties.getProperty("Vertical_sync", Boolean.valueOf(MT4jSettings.getInstance().isVerticalSynchronization()).toString()).trim());
	
			 //Set frametitle
			 String frameTitle = properties.getProperty("Frametitle", MT4jSettings.getInstance().getFrameTitle().trim());
			 MT4jSettings.getInstance().frameTitle = frameTitle;
	
		 } catch (Exception e) {
			 logger.error("Error while loading Settings.txt. Using defaults.");
		 }
		 settingsLoadedFromFile = true;
	}



	/**
	 * ***********************************************************
	 * Processings setup. this is called once when the applet is started
	 * Used to define some initial settings
	 * **********************************************************.
	 */
	@Override
	public void setup(){
		if (!settingsLoadedFromFile){ //because initialize() method isnt called in the swing integration/example
			getSettingsFromFile();
		}
		
		// Applet size - size() must be the first command in setup() method
		if (MT4jSettings.getInstance().getRendererMode() == MT4jSettings.OPENGL_MODE)
			this.size(MT4jSettings.getInstance().getWindowWidth(), MT4jSettings.getInstance().getWindowHeight(), CUSTOM_OPENGL_GRAPHICS);
		else if (MT4jSettings.getInstance().getRendererMode() == MT4jSettings.P3D_MODE)
			this.size(MT4jSettings.getInstance().getWindowWidth(), MT4jSettings.getInstance().getWindowHeight(), PApplet.P3D);
		
		//TOGGLES ALWAYS ON TOP MODE
		//this.frame.setAlwaysOnTop(true);
		
		//Add default font factories /////////////
		//Register default font factories
		FontManager.getInstance().registerFontFactory(".ttf", new TTFontFactory());
		FontManager.getInstance().registerFontFactory(".svg", new SvgFontFactory());
	    
		BitmapFontFactory bitmapFontFactory = new BitmapFontFactory();
//		this.registerFontFactory(".ttf", bitmapFontFactory); // TEST
		FontManager.getInstance().registerFontFactory("", bitmapFontFactory);
		FontManager.getInstance().registerFontFactory(".vlw", bitmapFontFactory);
		FontManager.getInstance().registerFontFactory(".otf", bitmapFontFactory);
	    
		FontManager.getInstance().registerFontFactory(".bold", bitmapFontFactory);
		FontManager.getInstance().registerFontFactory(".bolditalic", bitmapFontFactory);
		FontManager.getInstance().registerFontFactory(".italic", bitmapFontFactory);
		FontManager.getInstance().registerFontFactory(".plain", bitmapFontFactory);
		//////////////////////
		
		/////////////////////// //FIXME TEST
		PlatformUtil.setGraphicsUtilProvider(new DesktopPlatformUtil(this));
		///////////////////////
		
		/////////////////////
		//Add default 3D model factories for .3ds and for .obj files
		ModelImporterFactory.registerModelImporterFactory(".3ds", Model3dsFileFactory.class);
		ModelImporterFactory.registerModelImporterFactory(".obj", ModelObjFileFactory.class);
		////////////////////
		
		
		//Check if OS 32/64 Bit
		String bit = System.getProperty("sun.arch.data.model");
		logger.info("Platform: \"" + System.getProperty("os.name") + "\" -> Version: \"" + System.getProperty("os.version") +  "\" -> JVM Bit: \"" + bit + "\""); 
		MT4jSettings.getInstance().architecture = bit.contains("64")? MT4jSettings.ARCHITECTURE_64_BIT : MT4jSettings.ARCHITECTURE_32_BIT;
		
		//Switch to different resolution in fullscreen exclusive mode if neccessary
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (MT4jSettings.getInstance().isFullscreen() && MT4jSettings.getInstance().isFullscreenExclusive() && MT4jSettings.getInstance().getWindowWidth() != screenSize.width && MT4jSettings.getInstance().getWindowHeight() != screenSize.height){
			switchResolution();
		}
		
	    /*
	    //Processing Bug? seems to always use 2 samples 
	    if (MT4jSettings.getInstance().getNumSamples() <= 0){
	    	hint(DISABLE_OPENGL_2X_SMOOTH);
	    }else if (MT4jSettings.getInstance().getNumSamples() == 2){
	    	//Nothing to set, Processing default anyway
	    }else if (MT4jSettings.getInstance().getNumSamples() == 4){
	    	hint(DISABLE_OPENGL_2X_SMOOTH);
	    	hint(ENABLE_OPENGL_4X_SMOOTH);
	    }
	    */
	    
		//hint(ENABLE_DEPTH_SORT); // Enable primitive z-sorting of triangles and lines in P3D and OPENGL. This can slow performance considerably, and the algorithm is not yet perfect.
		//hint(DISABLE_ERROR_REPORT); // Speeds up the OPENGL renderer setting by not checking for errors while running.
		//hint(ENABLE_ACCURATE_TEXTURES); //Enables better texture accuracy for the P3D renderer. This option will do a better job of dealing with textures in perspective.  
		
		// Save this applets rendering thread for reference
		this.renderThread = Thread.currentThread();
		//System.out.println("Current Thread: "+  Thread.currentThread());
		
		// Set frame icon image
		try {
			//Set the window frame's title
			frame.setTitle(MT4jSettings.getInstance().getFrameTitle()); 
			this.mt4jIcon = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(MT4jSettings.getInstance().getDefaultImagesPath() + 
			"MT4j.gif"));
			this.frame.setIconImage(mt4jIcon.getImage()); 
		}catch (Exception e){
			e.printStackTrace();
		}
		
		logger.info("MT4j window dimensions: \"" + MT4jSettings.getInstance().getWindowWidth() + " X " +  MT4jSettings.getInstance().getWindowHeight() + "\"");
		
//		//Set background color
//	    pContext.background(MT4jSettings.getInstance().getBackgroundClearColor());
		background(150);
		
		//Set the framerate
	    frameRate(MT4jSettings.getInstance().getMaxFrameRate());
	    logger.info("Maximum framerate: \"" + MT4jSettings.getInstance().getMaxFrameRate() + "\"");
	    
	    //FIXME TODO add in settings.txt?
	    hint(AbstractMTApplication.DISABLE_OPENGL_ERROR_REPORT);
		
		MT4jSettings.getInstance().programStartTime = System.currentTimeMillis();
		
		//Apply some opengl settings like V-Syncing or multi-Sampling
		this.applyOpenGLStartSettings();
		
		//Create a new inputsourcePool
		if (getInputManager() == null){ //only set the default inputManager if none is set yet
			this.setInputManager(new DesktopInputManager(this, true));
		}
		
		AniAnimation.init(this); //Initialize Ani animation library
		
		/*
		* Resizable Window test
		* Problems:
		* - all textures, shaders etc get destroyed because a new gl context is created
		* - cursor coordiantes are calculated wrong? we prolly have to update Papplet width/height 
		frame.setResizable(true);
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if(e.getSource() == frame) { 
					frame.setSize(frame.getWidth(), minHeight); 
				}
			}
		} );
		*/ 
		
		//Call startup at the end of setup(). Should be overridden in extending classes
		this.startUp();
	}
	
	
	
	

	protected void loadGL(){
		String version = ((PGraphicsOpenGL)g).gl.glGetString(GL.GL_VERSION);
		logger.info("OpenGL Version: " + version);
        int major = Integer.parseInt("" + version.charAt(0));
        int minor = Integer.parseInt("" + version.charAt(2));
        
        this.gl11Supported = false;
        this.gl20Supported = false;
        if (major >= 2) {
//                JoglGL20 jogl20 = new JoglGL20(((PGraphicsOpenGL)g).gl);
        		JoglGL20Plus jogl20 = new JoglGL20Plus(((PGraphicsOpenGL)g).gl);
                iGL20 = jogl20;
                //FIXME ADDED
                iGL10  = jogl20;
                iGL11 = jogl20;
                iGL11Plus = jogl20;
                glCommon = iGL20;
                this.gl20Supported = true;
                this.gl11Supported = true;
                this.gl11PlusSupported = true;
        } else {
                if (major == 1 && minor < 5) {
                        iGL10 = new JoglGL10(((PGraphicsOpenGL)g).gl);
                } else {
                        iGL11 = new JoglGL11(((PGraphicsOpenGL)g).gl);
                        iGL10 = iGL11;
                        this.gl11Supported = true;
                }
                glCommon = iGL10;
        }
	}
	
	/**
	 * Apply open gl start settings.
	 */
	private void applyOpenGLStartSettings(){
		//TODO pa.smooth() / pa.noSmooth() ver�ndert auch line_smooth!
		//f�r test ob multisampling lines ohne Line_smooth okay rendered m�ssen
		//sicherheitshalber auch die pa.smoot() etc abgefangen werden und line_smooth immer disabled sein!
		
		//TODO check line drawing and abstractvisible at stencil in this context (line_smooth)
		
	    //TODO 
		// - if multisampling enabled dont do line smoothing at all
		// - OR: disable multisampling each time before doing line_smoothing! (better but expensive?) 
		//   -> info: disabling multisampling isnt possible at runtime..

	    // - or disable mutisample before drawing with line_smooth!
		//TOOD dont use lines to smooth some objects then (fonts, etc)
	    if (MT4jSettings.getInstance().isOpenGlMode() ){
	    	
	    	//////////////////////////////
	    	this.loadGL();
	        //////////////////////////
	        
//	    	GL gl = Tools3D.getGL(this);
	        GLCommon gl = getGLCommon();
	    	
	    	logger.info("OpenGL Version: \"" + gl.glGetString(GL.GL_VERSION) + "\"" + " - Vendor: \"" + gl.glGetString(GL.GL_VENDOR) + "\"" + " - Renderer: \"" + gl.glGetString(GL.GL_RENDERER) + "\"");
//	    	logger.info("Shading language version: \"" +  gl.glGetString(GL.GL_SHADING_LANGUAGE_VERSION) + "\"");
	    	logger.info("Non power of two texture sizes allowed: \"" + Tools3D.supportsNonPowerOfTwoTexture(this) + "\"");
	    	logger.info("OpenGL Framebuffer Object Extension available: \"" + GLFBO.isSupported(this) + "\"");
	    	
			//Set VSyncing on -> to avoid tearing 
			//-> check if gfx card settings allow apps to set it!
			//-> Use with caution! only use with fps rate == monitor Hz!
			//and fps never drop below Hz! -> else choppy!
			//-> only works with opengl!
	    	Tools3D.setVSyncing(this, MT4jSettings.getInstance().isVerticalSynchronization());
			logger.info("Vertical Sync enabled: \"" + MT4jSettings.getInstance().isVerticalSynchronization() + "\"");
	    	
	    	if ( MT4jSettings.getInstance().isMultiSampling()){
	    		gl.glEnable(GL.GL_MULTISAMPLE);
//	    		gl.glDisable(GL.GL_MULTISAMPLE);
	    		logger.info("OpenGL multi-sampling enabled.");
	    	}
	    	gl.glEnable(GL.GL_LINE_SMOOTH);
//	    	gl.glDisable(GL.GL_LINE_SMOOTH);
	    }
	}
	
	
	protected void switchResolution() {
		logger.debug("Switching resolution..");
		try {
			frame.enableInputMethods(false);
			frame.setIgnoreRepaint(true);
			final GraphicsDevice myGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			
			// Get the current display mode
	        final DisplayMode previousDisplayMode= myGraphicsDevice.getDisplayMode();
			
//			final int width = 1280;
//			final int height = 768;
			final int width = MT4jSettings.getInstance().getWindowWidth();
			final int height = MT4jSettings.getInstance().getWindowHeight();
			int bitDepth = 32;
			int refreshRate = myGraphicsDevice.getDisplayMode().getRefreshRate();
			
			myGraphicsDevice.setFullScreenWindow(this.frame); 
			
            // Check if display mode changes are supported by the OS
            if (myGraphicsDevice.isDisplayChangeSupported()) {
                // Get all available display modes
                DisplayMode[] displayModes = myGraphicsDevice.getDisplayModes();
                DisplayMode multiBitsDepthSupportedDisplayMode = null;
                DisplayMode refreshRateUnknownDisplayMode = null;
                DisplayMode multiBitsDepthSupportedAndRefreshRateUnknownDisplayMode = null;
                DisplayMode matchingDisplayMode = null;
                DisplayMode currentDisplayMode;
                // Look for the display mode that matches with our parameters
                // Look for some display modes that are close to these parameters
                // and that could be used as substitutes
                // On some machines, the refresh rate is unknown and/or multi bit
                // depths are supported. If you try to force a particular refresh 
                // rate or a bit depth, you might find no available display mode
                // that matches exactly with your parameters
                for (int i = 0; i < displayModes.length && matchingDisplayMode == null; i++) {
                    currentDisplayMode = displayModes[i];
                    if (currentDisplayMode.getWidth()  == width &&
                        currentDisplayMode.getHeight() == height) {
                        if (currentDisplayMode.getBitDepth() == bitDepth) {
                            if (currentDisplayMode.getRefreshRate() == refreshRate) {
                                matchingDisplayMode = currentDisplayMode;
                            } else if (currentDisplayMode.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN) {
                                refreshRateUnknownDisplayMode = currentDisplayMode;
                            }
                        } else if (currentDisplayMode.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI) {
                            if (currentDisplayMode.getRefreshRate() == refreshRate) {
                                multiBitsDepthSupportedDisplayMode = currentDisplayMode;
                            } else if (currentDisplayMode.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN) {
                                multiBitsDepthSupportedAndRefreshRateUnknownDisplayMode = currentDisplayMode;
                            }
                        }
                    }
                }
                DisplayMode nextDisplayMode = null;
                if (matchingDisplayMode != null) {
                    nextDisplayMode = matchingDisplayMode;                    
                } else if (multiBitsDepthSupportedDisplayMode != null) {
                    nextDisplayMode = multiBitsDepthSupportedDisplayMode;
                } else if (refreshRateUnknownDisplayMode != null) {
                    nextDisplayMode = refreshRateUnknownDisplayMode;
                } else if (multiBitsDepthSupportedAndRefreshRateUnknownDisplayMode != null) {
                    nextDisplayMode = multiBitsDepthSupportedAndRefreshRateUnknownDisplayMode;
                } else {
//                    isFullScreenSupported = false;
                	logger.error("No matching fullscreen display mode found!");
                }

                if (nextDisplayMode != null){
                	/*
                		DisplayMode myDisplayMode = new DisplayMode(
                				width,
                				height,
                				myGraphicsDevice.getDisplayMode().getBitDepth(),
                				DisplayMode.REFRESH_RATE_UNKNOWN);
                				myGraphicsDevice.setDisplayMode(myDisplayMode);
                	 */

                	myGraphicsDevice.setDisplayMode(nextDisplayMode);

                	Component[] myComponents = frame.getComponents();
                	for (int i = 0; i < myComponents.length; i++) {
                		if (myComponents[i] instanceof PApplet) {
                			myComponents[i].setLocation(0, 0);
                		}
                	}
                	
                	frame.addWindowListener(new WindowAdapter() {
                		 @Override
                		public void windowClosing(java.awt.event.WindowEvent e) {
                			// If required, restore the previous display mode
                                myGraphicsDevice.setDisplayMode(previousDisplayMode);
                            // If required, get back to the windowed mode
                            if (myGraphicsDevice.getFullScreenWindow() == frame) {
                            	myGraphicsDevice.setFullScreenWindow(null);
                            }
                		}
                    });
                }
            }
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public GL getGL(){
//		return ((PGraphicsOpenGL)g).gl;
//	}
//	
//	public GL beginGL(){
//		return ((PGraphicsOpenGL)g).beginGL();
//	}
//	
//	public void endGL(){
//		((PGraphicsOpenGL)g).endGL();
//	}

}
