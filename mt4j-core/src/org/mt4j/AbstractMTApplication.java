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

package org.mt4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mt4j.components.css.util.CSSStyleManager;
import org.mt4j.input.IKeyListener;
import org.mt4j.input.InputManager;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;
import org.mt4j.input.inputSources.AbstractInputSource;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.sceneManagement.ISceneChangeListener;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.sceneManagement.SceneChangeEvent;
import org.mt4j.sceneManagement.transition.ITransition;
import org.mt4j.util.ArrayDeque;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.animation.AnimationManager;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11;
import org.mt4j.util.opengl.GL11Plus;
import org.mt4j.util.opengl.GL20;
import org.mt4j.util.opengl.GLCommon;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.EXPANSION_FILTER;
import org.mt4j.util.opengl.GLTexture.SHRINKAGE_FILTER;
import org.mt4j.util.opengl.GLTexture.WRAP_MODE;
import org.mt4j.util.opengl.GLTextureSettings;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix3D;



/**
 * Use this class to create a new multitouch application.
 * <br>The best way to create your application would be to extend this class and
 * put the <code>main</code> method into that class.
 * In the <code>main</code> method call the <code>initialize()</code> method.
 * Then override the <code>startUp()</code> method which is called
 * automatically after the initialize method. The <code>startUp()</code> method can be used to
 * create your scenes (extend the <code>AbstractScene</code> class) and add them to
 * the application by calling <code>addScene</code> method.
 * 
 * <p>Internally, the main method of processings PApplet class is called with the class name
 * of the extended PApplet class as an argument. The PApplet class then instantiates the given
 * class and calls its setup() and then repeatedly its run() method.
 * 
 * @author Christopher Ruff
 */
public abstract class AbstractMTApplication extends PApplet implements IMTApplication{
	/** The Constant logger. */
	protected static ILogger logger;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The scene change locked. */
	private boolean sceneChangeLocked;

//	private static MTApplication mtApp = null;
	
	/** The scene list. */
	private List<Iscene> sceneList;
	
	/** The current scene. */
	private Iscene currentScene;
	
	/** The animation mgr. */
	protected AnimationManager animMgr;
	
	/** The time last frame. */
	private long timeLastFrame ;
	
	/** The already run. */
	private boolean alreadyRun;
	
	/** The input manager. */
	private InputManager inputManager;
	
	/** The scene changed listeners. */
	private List<ISceneChangeListener> sceneChangedListeners;
	
	/** The invoke later actions. */
	private ArrayDeque<Runnable> invokeLaterActions;
	
	/** The scene stack. */
	private ArrayDeque<Iscene> sceneStack;
	
	protected Thread renderThread;
	
	public static String separator = "/";
	public static char separatorChar = '/';
	
//	private static boolean settingsLoadedFromFile = false; //cant initialize in constructor, need it before that!
	
	protected CSSStyleManager cssStyleManager;

	protected ArrayDeque<IPreDrawAction> preDrawActions;

	protected GLCommon glCommon;
	protected GL10 iGL10;
	protected GL11 iGL11;
	protected GL20 iGL20;
	protected GL11Plus iGL11Plus;
	
	protected boolean gl20Supported;

	protected boolean gl11Supported;

	protected boolean gl11PlusSupported;

	
//	private static boolean fullscreen;
	/*
	public static void main(String[] args){
//		MTApplication app  = new MTApplication();
		
		PApplet.main(new String[] {
//				   "--present", 
//				   "--exclusive",
				   "--bgcolor=#000000", 
				   "--hide-stop",
				   "org.mt4j.MTApplication"
				   }
				   ); 
	}
	@Override
	public void setup(){
		size(800,600, OPENGL); //TODO REMOVE
		logger.debug("Setup");
		System.out.println("Setup called");
		
		smooth();
		hint(ENABLE_OPENGL_2X_SMOOTH );
		smooth();
		noSmooth();
		
		background(0);
		
		GL gl = Tools3D.getGL(this);
//		 gl.glEnable(GL.GL_MULTISAMPLE);
//	     gl.glEnable(GL.GL_MULTISAMPLE_EXT);
	}
	@Override
	public void draw(){
//		background(255);
		
		fill(250,0,0,255);
		stroke(250,0,0,255);
		line(0,10, 280,20);
		
		GL gl = Tools3D.beginGL(this);
//		GL gl =  ((PGraphicsOpenGL)this.g).beginGL();
//		gl.glEnable(GL.GL_LINE_SMOOTH );  
		gl.glDisable(GL.GL_LINE_SMOOTH );  
//		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);  
		// Enable Blending 
		gl.glEnable(GL.GL_BLEND);  
		// Specifies pixel arithmetic  
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); 
		gl.glLineWidth(1);
		gl.glColor4d(0.0, 0.0, 0.0, 1);
		
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3d(0, 20, 0);
		gl.glVertex3d(280, 30, 0);
		gl.glEnd();
		
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3d(0, 20, 0);
		gl.glVertex3d(711, 230, 0);
		gl.glVertex3d(200, 300, 0);
		gl.glVertex3d(100, 330, 0);
		gl.glEnd();
//		((PGraphicsOpenGL)this.g).endGL();
		
		Tools3D.endGL(this);
		
		if (this.mousePressed){
			fill(150);
			rect(mouseX, mouseY, 10,10);
		}
	}
	*/
	
	/*
	//TODO test to make window undecorated - seems to mess up some textures (maybe because opengl re-initialization)
	//put frame.setLocation(-1600, 0); at the end of setup() to position the frame 
	public void init(){
		  // to make a frame not displayable, you can
		  // use frame.removeNotify()
		  frame.removeNotify();

		  frame.setUndecorated(true);

		  // addNotify, here i am not sure if you have 
		  // to add notify again.  
		  frame.addNotify();
		  super.init();
		}
	*/
	



	/**
	 * Dont instiatiate this class directly!
	 * It gets instantiated by the PApplet class via
	 * java reflection.
	 */
	public AbstractMTApplication(){
		sceneList 		= new ArrayList<Iscene>();
		currentScene 	= null;
		animMgr 		= AnimationManager.getInstance();
		alreadyRun 		= false;
		
		sceneChangedListeners = new ArrayList<ISceneChangeListener>();
		invokeLaterActions = new ArrayDeque<Runnable>();
		sceneStack = new ArrayDeque<Iscene>();
		
		sceneChangeLocked = false;
		cssStyleManager = new CSSStyleManager(this);
		
		preDrawActions = new ArrayDeque<IPreDrawAction>();
		
		keyListeners = new ArrayList<IKeyListener>();
	}
	
	
	public void setOpenGLErrorReportingEnabled(boolean reportErros){
		if (reportErros){
			hint(AbstractMTApplication.ENABLE_OPENGL_ERROR_REPORT);
		}else{
			hint(AbstractMTApplication.DISABLE_OPENGL_ERROR_REPORT);
		}
	}
	
	/**
	 * ********************************************************************************************
	 * Processings draw() gets called repeatedly by processings PApplet Class - unless noloop() is called
	 * ********************************************************************************************.
	 */
	@Override
	public void draw(){
		this.runApplication();
	}
	
	
	/**
	 * Is called at the end of the setup() method.
	 * <br>Override this method in your extended MTApplication class!
	 */
	public abstract void startUp();
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#registerPreDrawAction(org.mt4j.sceneManagement.IPreDrawAction)
	 */
	public void registerPreDrawAction(final IPreDrawAction action){
		synchronized (preDrawActions) {
//			this.preDrawActions.addLast(action);
			invokeLater(new Runnable() {
				public void run() {
					preDrawActions.addLast(action);
				}
			});
		}
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#unregisterPreDrawAction(org.mt4j.sceneManagement.IPreDrawAction)
	 */
	public void unregisterPreDrawAction(final IPreDrawAction action){
		synchronized (preDrawActions) {
			if (preDrawActions.contains(action)){
//				this.preDrawActions.remove(action);
				invokeLater(new Runnable() {
					public void run() {
						preDrawActions.remove(action);
					}
				});
			}
		}
	}
	
	
	/**
	 * Main run loop.
	 * <li>Updates the time passed since the last time drawn.
	 * <li>Updates any animations with the new time delta.
	 * <li>Updates and draws the current scene.
	 * <li>Updates and draws the current scene transitions.
	 */
	private void runApplication(){ 
		//Process preDrawActions
		synchronized (preDrawActions) {
			for (Iterator<IPreDrawAction> iter = preDrawActions.iterator(); iter.hasNext();) {
				IPreDrawAction action = iter.next();
				action.processAction();
				if (!action.isLoop()){
					iter.remove(); 
				}
			}
		}

		//Use nanoTime
		if (!alreadyRun){
			alreadyRun = true;
			timeLastFrame = System.nanoTime();
		}
		long nanos = System.nanoTime();
		long timeDelta = (nanos - timeLastFrame) / 1000000L;
		timeLastFrame = nanos;
		
		/*
		//Use currentTimeMillis
		if (!alreadyRun){
			alreadyRun = true;
			timeLastFrame = System.currentTimeMillis();
		}
		long millis = System.currentTimeMillis();
		long timeDelta = millis - timeLastFrame;
		timeLastFrame = millis;
		*/
		
//		System.out.println("TimeDelta: " + timeDelta);
		
		//Run invoke later actions
		synchronized (invokeLaterActions) {
			while (!invokeLaterActions.isEmpty()){
				invokeLaterActions.pollFirst().run();
			}
		}
		
		//Update animation manager
		animMgr.update(timeDelta);
		
//		/*
		//Handle scene transitions
		if (this.pendingTransition != null){
			//Run the transition
			this.pendingTransition.transition.drawAndUpdate(this.g, timeDelta);
			
			if (this.pendingTransition.transition.isFinished()){
				this.pendingTransition.transition.onLeave();
				this.doSceneChange(this.getCurrentScene(), this.pendingTransition.nextScene);
				this.pendingTransition = null;
			}
		}else{
			//Draw the current scene
			Iscene theCurrentScene = this.getCurrentScene();
			if (theCurrentScene != null){
				theCurrentScene.drawAndUpdate(this.g, timeDelta);	
			}
		}
//		 */
		
		/*
		//Update scene
		sceneMgr.updateCurrentScene(timeDelta);
		//Draw scene
		sceneMgr.drawCurrentScene();
		 */
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#isRenderThreadCurrent()
	 */
	public boolean isRenderThreadCurrent(){
		return Thread.currentThread().equals(renderThread);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#invokeLater(java.lang.Runnable)
	 */
	public void invokeLater(Runnable runnable){
		synchronized (invokeLaterActions) {
			invokeLaterActions.addLast(runnable);	
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#peekScene()
	 */
	public Iscene peekScene(){
		return sceneStack.peek();
	}
	
	protected int getSceneStackCount(){
		return sceneStack.size();
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#pushScene()
	 */
	public void pushScene(){
		if (getCurrentScene() == null){
			logger.debug("Scene stack is empty! No scene to put on the stack!");
		}else{
			logger.debug("Putting scene: " + getCurrentScene().getName() +  " on the stack.");
			sceneStack.offerFirst(getCurrentScene());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#popScene()
	 */
	public boolean popScene(){
//		Iscene stackScene = sceneStack.pollFirst();
		
		Iscene stackScene = sceneStack.peek();
		if (stackScene != null){
			logger.debug("Popping scene: " + stackScene.getName() +  " back from the stack.");
			boolean changed = this.changeScene(stackScene);
			if (changed){
				sceneStack.pollFirst();
				return true;
			}else{
				return false;
			}
		}else{
			logger.warn("Scene stack is empty! No scene to pop from the stack!");
			return false;
		}
	}
	
	
	
	private boolean inDoSceneChange = false;
	private TransitionInfo pendingTransition;

	
	/**
	 * The Class TransitionInfo. Holding info about a scene change transition.
	 * @author Christopher Ruff
	 */
	private class TransitionInfo{
		ITransition transition;
		Iscene lastScene;
		Iscene nextScene;
		boolean destroyLastSceneAfterTransition = false; 
		public TransitionInfo(ITransition transition, Iscene lastScene, Iscene nextScene){
			this.transition = transition;
			this.lastScene = lastScene;
			this.nextScene = nextScene;
		}
	}
	
	
	/**
	 * Initiates the scene change. Checks if the old scene has a transition
	 * and sets it to be used in the main loop.
	 * 
	 * @param oldScene the old scene
	 * @param newScene the new scene
	 */
	private boolean initiateSceneChange(Iscene oldScene, Iscene newScene){
		//FIXME TEST!
		if (oldScene.equals(newScene)){
			logger.error("Trying to change from and to the same scene.");
			return false;
		}
		
		//Lock scene changes to only 1 at a time. At sending the bridge events during the 
		//scene change, it could occur that a scene change could be triggered again which we prevent
		if (!sceneChangeLocked){
			sceneChangeLocked = true;
			
			Iscene lastScene = this.getCurrentScene();
			
			//Remove pending animations // 
			//FIXME problemes, if new animations are defined in a scenes constructor, they get removed here..
			//AnimationManager.getInstance().clear();
			
			//Flush events so that enqueued input ended get sent to the last scene
			//(Problem: they have been removed from active cursor pool already so they dont
			//appear there and no ended and started evts are sent to the scenes!
			//IF input started or updated should be flushed with this they should appear in active
			//cursor list after that and be sended the right events
			//- maybe only flush input_ended?
			for (AbstractInputSource abstractInputSource : getInputManager().getInputSources()) {
				abstractInputSource.flushEvents();
			}
			
			//Check which cursors are still active and clone their last evt as INPUT_ENDED
			//so the scene can complete its state (i.e. buttons are be released etc)
			this.sendEndedEvents(lastScene); 

			//Disable the last scene's global input processors
			this.getInputManager().disableGlobalInputProcessors(lastScene);
			
//			/*
			if (lastScene.getTransition() != null){
				ITransition t = lastScene.getTransition();
				this.pendingTransition = new TransitionInfo(t, lastScene, newScene);
				t.onEnter();
				t.setup(lastScene, newScene);
				return true;
			}else{
				return this.doSceneChange(lastScene, newScene);
			}
//			 */
			//doSceneChange(oldScene, newScene);
		}else{
			logger.debug("Couldnt change scene -> Change is locked from another scene change.");
			return false;
		}
	}
	
	
	/**
	 * Does the scene change after the transition (if existing) is completed.
	 * @param oldScene the old scene
	 * @param newScene the new scene
	 */
	private boolean doSceneChange(Iscene oldScene, Iscene newScene){
		if (sceneChangeLocked && !inDoSceneChange){
			inDoSceneChange = true;
			
			//Maybe show loading progress for newScenne.Init first?
			oldScene.onLeave();
			
			//Initialize new Scene
			newScene.onEnter();

			//Enable input Processors previously registered with that scene
			this.getInputManager().enableGlobalInputProcessors(newScene);

			//Check which cursors are active and clone their last evt as INPUT_DETECTED
			//so the scene doesent get INPUT_UPDATED without the start events
			this.sendStartedEvents(newScene); 

			//Set new current scene
			this.currentScene = newScene;
			
			//FIXME TEST -> Make it possible to destroy scenes after a transition
			//(During a transition the old scene cant be removed or destroyed because
			//its still the current scene!)
			if (pendingTransition != null){
				if (pendingTransition.destroyLastSceneAfterTransition){
					logger.debug("Destroying scene: " + pendingTransition.lastScene.getName() + " after the transition.");
					pendingTransition.lastScene.destroy();
				}
			}

			if (!this.sceneChangedListeners.isEmpty()){
				this.fireSceneChangeEvent(new SceneChangeEvent(this, oldScene, newScene));
			}
			logger.debug("Scene changed from: '" + oldScene + "' to: '" + newScene + "'");
			sceneChangeLocked = false;
			
			inDoSceneChange = false;
			return true;
		}else{
			return false;
		}
	}

	
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#changeScene(org.mt4j.sceneManagement.Iscene)
	 */
	public synchronized boolean changeScene(Iscene newScene){
		if (!this.sceneList.contains(newScene)){
			this.addScene(newScene);
		}
		return this.initiateSceneChange(this.getCurrentScene(), newScene);
	}

	
	/**
	 * Checks which cursors are active during the scene change and
	 * sends input_ended events of the active cursors to last scene's global input processors 
	 * so actions in the last scene can be completed correctly.
	 * This means that one cursor can have more than one input_ended and input_started event
	 * in its event list!
	 * 
	 * @param lastScene the last scene
	 * @param newScene the new scene
	 */
	private void sendEndedEvents(Iscene lastScene){
		logger.debug("Sending INPUT_ENDED events to the last scene, Active motions: " + ActiveCursorPool.getInstance().getActiveCursorCount());
		InputCursor[] activeCursors = ActiveCursorPool.getInstance().getActiveCursors();
        for (InputCursor inputCursor : activeCursors) {
            if (inputCursor.getCurrentEvent() != null) {
                AbstractCursorInputEvt lastEvt = inputCursor.getCurrentEvent();
                if (lastEvt.getId() != AbstractCursorInputEvt.INPUT_ENDED) {
                    try {
                        AbstractCursorInputEvt endedEvt = (AbstractCursorInputEvt) lastEvt.clone();
                        endedEvt.setId(AbstractCursorInputEvt.INPUT_ENDED);
                        endedEvt.onFired();

                        this.sendEvtToSceneProcessors(lastScene, endedEvt);
                        logger.debug("Sending INPUT_ENDED evt to scene: " + lastScene.getName() + " Cursor: " + endedEvt.getCursor());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
	}
	
	
	/**
	 * Checks which cursors are active during the scene change and
	 * sends input_started to the new scene's global input processors so actions in the
	 * last scene can be completed correctly.
	 * This means that one cursor can have more than one input_ended and input_started event
	 * in its event list!
	 * 
	 * @param lastScene the last scene
	 * @param newScene the new scene
	 */
	private void sendStartedEvents(Iscene newScene){
		logger.debug("Sending INPUT_DETECTED events to the new scene, Active motions: " + ActiveCursorPool.getInstance().getActiveCursorCount());
		InputCursor[] activeCursors = ActiveCursorPool.getInstance().getActiveCursors();
        for (InputCursor inputCursor : activeCursors) {
            if (inputCursor.getCurrentEvent() != null) {
                //PROBLEM: if in lastscene last event in cursor was input_started enqueued
                //but not added to cursor yet,
                //shall we send it again in new scene? -> will input_started be sent twice?
                //- what if input started was enqueued during transition and not sent to any scene
                AbstractCursorInputEvt lastEvt = inputCursor.getCurrentEvent();
                /*
                    if (//lastEvt.getId() != AbstractCursorInputEvt.INPUT_DETECTED
                            true
                        ){
                    */
                try {
                    AbstractCursorInputEvt startedEvt = (AbstractCursorInputEvt) lastEvt.clone();
                    startedEvt.setId(AbstractCursorInputEvt.INPUT_STARTED);
                    startedEvt.onFired();

                    this.sendEvtToSceneProcessors(newScene, startedEvt);
                    logger.debug("Sending INPUT_DETECTED evt to scene: " + newScene.getName() + " Cursor: " + startedEvt.getCursor());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
//				}
            }
        }
	}
	
	
	/**
	 * Send evt to scene processors.
	 * 
	 * @param scene the scene
	 * @param evtToFire the evt to fire
	 */
	private void sendEvtToSceneProcessors(Iscene scene, AbstractCursorInputEvt evtToFire){
		AbstractGlobalInputProcessor[] sceneInputProcessors = this.getInputManager().getGlobalInputProcessors(scene);
        for (AbstractGlobalInputProcessor a : sceneInputProcessors) {
            //Hack, because processInputEvt() is disabled at this moment! -> not anymore..
//			a.processInputEvtImpl(evtToFire);
            a.processInputEvent(evtToFire);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#getCurrentScene()
	 */
	public Iscene getCurrentScene(){
		return currentScene;
	}
	
	/*
	public void drawCurrentScene(){
		getCurrentScene().draw();
	}
	public void updateCurrentScene(long timeDelta){
		getCurrentScene().update(timeDelta);
	}
	*/

	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#addScene(org.mt4j.sceneManagement.Iscene)
	 */
	public void addScene(Iscene scene){
		if (this.getSceneCount() == 0){
			scene.onEnter();
			this.currentScene = scene;
			this.getInputManager().enableGlobalInputProcessors(scene);
			this.fireSceneChangeEvent(new SceneChangeEvent(this, this.currentScene, this.currentScene));
		}
		if (!sceneList.contains(scene))
			sceneList.add(scene);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#addAll(org.mt4j.sceneManagement.Iscene[])
	 */
	public void addAll(Iscene[] scenes){
//		if (this.getSceneCount() == 0 && scenes[0] != null){
//			this.currentScene = scenes[0];
//		}
        for (Iscene scene : scenes) {
            //			sceneList.add(scene);
            this.addScene(scene);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#removeScene(org.mt4j.sceneManagement.Iscene)
	 */
	public boolean removeScene(Iscene scene){
		if (sceneList.contains(scene)){
			if (scene.equals(this.currentScene)){
				logger.warn("Cant remove the scene if it is the currently active scene! (" + scene + ")");
				return false;
			}else{
				sceneList.remove(scene);
				return true;
			}
		}
		else{
			return false;	
		}
		
//		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#destroySceneAfterTransition(org.mt4j.sceneManagement.Iscene)
	 */
	public void destroySceneAfterTransition(Iscene scene){
		if (pendingTransition != null && pendingTransition.lastScene.equals(scene)){
			pendingTransition.destroyLastSceneAfterTransition = true;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#getScenes()
	 */
	public Iscene[] getScenes(){
		return sceneList.toArray(new Iscene[sceneList.size()]);
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#getScene(java.lang.String)
	 */
	public Iscene getScene(String name){
		Iscene returnScene = null;
		for(Iscene scene : sceneList){
			if (scene.getName().equals(name))
				returnScene = scene; 
		}
		return returnScene;
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#getSceneCount()
	 */
	public int getSceneCount(){
		return sceneList.size();
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#getInputManager()
	 */
	public InputManager getInputManager() {
		return inputManager;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#setInputManager(org.mt4j.input.InputManager)
	 */
	public void setInputManager(InputManager inputManager) {
		this.inputManager = inputManager;
	}
	
	
	public PGraphics getPGraphics(){
		return this.g;
	}
	
	/*
//	public PMatrix3D getModelView() {
////		return ((PGraphics3D)this.g).modelview;
//		return GraphicsUtil.getModelViewInv();
//	}
////	
//	public PMatrix3D getModelViewInv() {
////		return ((PGraphics3D)this.g).modelviewInv;
//		return GraphicsUtil.getModelView();
//	}
	
	
//	public GL10 beginGL() {
//		((PGraphicsOpenGL)this.g).beginGL();
//		return this.iGL10;
//	}
//    
//    public void endGL(){
//    	((PGraphicsOpenGL)this.g).endGL();
//    }
//	*/
	
//	/*
	public PMatrix3D getModelView() {
		return PlatformUtil.getModelView();
	}
	
	public PMatrix3D getModelViewInv() {
		return PlatformUtil.getModelViewInv();
	}
	
	public GL10 beginGL() {
		PlatformUtil.beginGL();
		return this.iGL10;
	}
    
    public void endGL(){
    	PlatformUtil.endGL();
    }
//    */
	
	 /**
     * Returns whether OpenGL ES 1.1 is available. If it is you can get an instance of {@link GL11} via {@link #getGL11()} to
     * access OpenGL ES 1.1 functionality. This also implies that {@link #getGL10()} will return an instance.
     * 
     * @return whether OpenGL ES 1.1 is available
     */
    public boolean isGL11Available (){
    	return this.gl11Supported;
    }
    
    public boolean isGL11PlusAvailable() {
    	return this.gl11PlusSupported;
	}

    /**
     * Returns whether OpenGL ES 2.0 is available. If it is you can get an instance of {@link GL20} via {@link #getGL20()} to
     * access OpenGL ES 2.0 functionality. Note that this functionality will only be available if you instructed the
     * {@link Application} instance to use OpenGL ES 2.0!
     * 
     * @return whether OpenGL ES 2.0 is available
     */
    public boolean isGL20Available (){
    	return this.gl20Supported;
    }
    
    /**
     * @return a {@link GLCommon} instance
     */
    public GLCommon getGLCommon (){
    	return this.glCommon;
    }

    /**
     * @return the {@link GL10} instance or null if not supported
     */
    public GL10 getGL10 (){
    	return this.iGL10;
    }

    /**
     * @return the {@link GL11} instance or null if not supported
     */
    public GL11 getGL11 (){
    	return this.iGL11;
    }
    
    /**
     * @return the {@link GL20} instance or null if not supported
     */
    public GL20 getGL20 (){
    	return this.iGL20;
    }
    
    public GL11Plus getGL11Plus (){
    	return this.iGL11Plus;
    }
	

/////////////////////////	
	/**
	 * Fire scene change event.
	 * 
	 * @param sc the sc
	 */
	protected void fireSceneChangeEvent(SceneChangeEvent sc) {
		for (ISceneChangeListener listener : sceneChangedListeners){
			listener.processSceneChangeEvent(sc);
		}
	}

	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#addSceneChangeListener(org.mt4j.sceneManagement.ISceneChangeListener)
	 */
	public synchronized void addSceneChangeListener(ISceneChangeListener listener){
		if (!this.sceneChangedListeners.contains(listener)){
			sceneChangedListeners.add(listener);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#removeSceneChangeListener(org.mt4j.sceneManagement.ISceneChangeListener)
	 */
	public synchronized void removeSceneChangeListener(ISceneChangeListener listener){
		if (sceneChangedListeners.contains(listener)){
			sceneChangedListeners.remove(listener);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#getSceneChangeListener()
	 */
	public synchronized ISceneChangeListener[] getSceneChangeListener(){
		return sceneChangedListeners.toArray(new ISceneChangeListener[this.sceneChangedListeners.size()]);
	}
/////////////////////////////////	


	/**
	 * Gets the class name.
	 * 
	 * @author C.Ruff
	 */
	public static class CurrentClassGetter extends SecurityManager {
		/**
		 * Gets the class name.
		 * 
		 * @return the class name
		 */
		public String getClassName() {
			return getClassContext()[2].getName(); //FIXME is this reliable to always work?
		}
	}


	/* (non-Javadoc)
	 * @see org.mt4j.IMTApplication#getCssStyleManager()
	 */
	public CSSStyleManager getCssStyleManager() {
		return this.cssStyleManager;
	}
	
	
	
	
	
	//////////////////////////////////// Key Listener 
	/*
	 * Key checking example:
	 * Android: if (key == CODED && keyCode == KeyEvent.KEYCODE_BACK) check if key== CODED for special keys, esc, return etc
	 * Desktop: key == KeyEvent.VK_ESCAPE
	 */
	@Override
	public void keyPressed() {
		this.fireKeyPressed(this.key, this.keyCode);
	}
	
	@Override
	public void keyReleased() {
		this.fireKeyReleased(this.key, this.keyCode);
	}
	
	
	private ArrayList<IKeyListener> keyListeners;
	protected void fireKeyPressed(char key, int keyCode) {
		for (IKeyListener listener : keyListeners){
			listener.keyPressed(key, keyCode);
		}
	}
	
	protected void fireKeyReleased(char key, int keyCode) {
		for (IKeyListener listener : keyListeners){
			listener.keyRleased(key, keyCode);
		}
	}

	public synchronized void addKeyListener(IKeyListener listener){
		if (!this.keyListeners.contains(listener)){
			keyListeners.add(listener);
		}
	}
	
	public synchronized void removeKeyListener(IKeyListener listener){
		if (keyListeners.contains(listener)){
			keyListeners.remove(listener);
		}
	}
	
	public synchronized IKeyListener[] getKeyListener(){
		return keyListeners.toArray(new IKeyListener[this.keyListeners.size()]);
	}
	//////////////////////////////// KeyListener

	//////////////////////////
	//FIXME TEST 
	//-> to create gltexture automatically if using loadImage() in OpenGL mode
	//-> prevents creating many opengl texture resources from the same PImage,
	//e.g. if an PImage is loaded and then assigned to different shapes, 
	//in which a separate GLTexture object is created each time
	@Override
	public PImage loadImage(String filename) {
		if (MT4jSettings.getInstance().isOpenGlMode()){
			GLTextureSettings ts = new GLTextureSettings();
			//Create new GLTexture from PImage
			ts.shrinkFilter 		= SHRINKAGE_FILTER.BilinearNoMipMaps;
			ts.expansionFilter 		= EXPANSION_FILTER.Bilinear;
			ts.wrappingHorizontal 	= WRAP_MODE.CLAMP_TO_EDGE;
			ts.wrappingVertical 	= WRAP_MODE.CLAMP_TO_EDGE;
			return new GLTexture(this, super.loadImage(filename), ts);
		}else{
			return super.loadImage(filename);	
		}
	}
	
	//////////////////////////////////////////////////
	

	
}
