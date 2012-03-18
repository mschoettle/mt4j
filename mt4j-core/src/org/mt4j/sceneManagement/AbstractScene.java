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
package org.mt4j.sceneManagement;

import java.util.Iterator;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.InputRetargeter;
import org.mt4j.sceneManagement.transition.ITransition;
import org.mt4j.util.ArrayDeque;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.opengl.GL10;

import processing.core.PGraphics;

/**
 * A class representing a scene in a program or game.
 * It has its own main canvas and global input processors.
 * 
 * @author Christopher Ruff
 */
public abstract class AbstractScene implements Iscene {
	/** The Constant logger. */
	private static final ILogger logger = MTLoggerFactory.getLogger(AbstractScene.class.getName());
	static{
//		logger.setLevel(ILogger.ERROR);
//		logger.setLevel(ILogger.WARN);
//		logger.setLevel(ILogger.DEBUG);
		logger.setLevel(ILogger.INFO);
	}
	
	/** The scene cam. */
	private Icamera sceneCam;
	
	/** The main canvas. */
	private MTCanvas mainCanvas;
	
	/** The mt application. */
	private AbstractMTApplication mtApplication;
	
	/** The name. */
	private String name;

	/** The pre draw actions. */
	private final ArrayDeque<IPreDrawAction> preDrawActions;
	
	/** The clear color. */
	private MTColor clearColor;
	
	/** The gl clear color. */
	private MTColor glClearColor;
	
	/** The clear before draw. */
	private boolean clearBeforeDraw;
	
	/** The transition. */
	private ITransition transition;

	/**
	 * The Constructor.
	 * 
	 * @param mtApplication the mt application
	 * @param name the name
	 */
	public AbstractScene(AbstractMTApplication mtApplication, String name) {
		super();
		this.name = name;
		this.mtApplication = mtApplication;
		this.sceneCam = new MTCamera(mtApplication);
		this.sceneCam.update();
		this.sceneCam.setZoomMinDistance(60);
		this.mainCanvas = new MTCanvas(mtApplication, name + " - Main Canvas", sceneCam);
		
//		preDrawActions = new LinkedList<IPreDrawAction>();
		preDrawActions = new ArrayDeque<IPreDrawAction>();
		
		this.registerDefaultGlobalInputProcessors();
		
		this.clearBeforeDraw = true;
		this.setClearColor(new MTColor(0,0,0, 255));
	}
	
	
	/**
	 * Register default global input processors. Can be overridden for custom behaviour. 
	 */
	protected void registerDefaultGlobalInputProcessors(){
		InputRetargeter inputRetargeter = new InputRetargeter(this.getCanvas());
		inputRetargeter.addProcessorListener(this.getCanvas());
		this.registerGlobalInputProcessor(inputRetargeter);
		
//		this.registerGlobalInputProcessor(new InputRetargeter(this.getCanvas()));
	}

	/**
	 * Called before this scene becomes active.
	 * @deprecated renamed to onEnter(), init() is only kept for backwards compatibility
	 */
	public void init(){
	}
	
	/**
	 * Called before this scene loses its status of being the active scene.
	 * @deprecated renamed to onLeave(), shutDown() is only kept for backwards compatibility
	 */
	public void shutDown(){
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.Iscene#onEnter()
	 */
	public void onEnter(){
		init(); //for backwards compatibility to call old code, since init() was renamed onEnter();
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.Iscene#onLeave()
	 */
	public void onLeave(){
		shutDown(); //for backwards compatibility to call old code, since shutDown() was renamed onLeave();
	}


	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.Iscene#drawAndUpdate(processing.core.PGraphics, long)
	 */
	public void drawAndUpdate(PGraphics graphics, long timeDelta){
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
		
		//Clear the background
		if (this.clearBeforeDraw){ 
			this.clear(graphics);
		}
		
		//Draw and update canvas
		this.getCanvas().drawAndUpdateCanvas(graphics, timeDelta);
	}
	
	

	protected void clear(PGraphics graphics){
		if (MT4jSettings.getInstance().isOpenGlMode() && !PlatformUtil.isAndroid()){
//			GL gl = Tools3D.getGL(mtApplication);
			GL10 gl = PlatformUtil.getGL();
			gl.glClearColor(this.glClearColor.getR(), this.glClearColor.getG(), this.glClearColor.getB(), this.glClearColor.getAlpha());
			gl.glClear(
					GL10.GL_COLOR_BUFFER_BIT 
					| 
					GL10.GL_DEPTH_BUFFER_BIT
					);
//			gl.glDepthMask(false);
//			gl.glDisable(GL.GL_DEPTH_TEST);
		}else{
			//In androids PGraphicsAndroid3D the background() method sets clearColorBuffer to true
			//which prevents expensive operations each frame..we cant set the variable because its protected..
			graphics.background(this.clearColor.getR(), this.clearColor.getG(), this.clearColor.getB(), this.clearColor.getAlpha());
		}
	}
	
	/**
	 * Sets the clear color to use when the screen is cleared each frame
	 * before drawing.
	 * 
	 * @param clearColor the new clear color
	 */
	public void setClearColor(MTColor clearColor){
		this.clearColor = clearColor;
		this.glClearColor = new MTColor(this.clearColor.getR()/255f, this.clearColor.getG()/255f, this.clearColor.getB()/255f, this.clearColor.getAlpha()/255f);
	}
	
	
	/**
	 * Gets the clear color.
	 * 
	 * @return the clear color
	 */
	public MTColor getClearColor(){
		return this.clearColor;
	}
	
	
	/**
	 * Sets the scene to be cleared each frame or not.
	 * 
	 * @param clearScreen the new clear
	 */
	public void setClear(boolean clearScreen){
		this.clearBeforeDraw = clearScreen;
	}
	
	/**
	 * Checks if the scene is being cleared each frame.
	 * 
	 * @return true, if is clear
	 */
	public boolean isClear(){
		return this.clearBeforeDraw;
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.Iscene#getCanvas()
	 */
	public MTCanvas getCanvas() {
		return mainCanvas;
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.Iscene#getSceneCam()
	 */
	public Icamera getSceneCam() {
		return sceneCam;
	}

	/**
	 * Sets the scene cam. This is the camera which gets attached
	 * to the scene's canvas.
	 * 
	 * @param sceneCam the scene cam
	 */
	public void setSceneCam(Icamera sceneCam) {
		this.sceneCam = sceneCam; 
		this.getCanvas().attachCamera(sceneCam);
	}

	/**
	 * Gets the MT application instance.
	 * 
	 * @return the mT application
	 */
	public AbstractMTApplication getMTApplication(){
		return this.mtApplication;
	}

	/* (non-Javadoc)
	 * @see mTouch.sceneManagement.Iscene#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	
//	 * By default, the scene's canvas will be listening to the events of the registered
//	 * global input processor so the events will be delivered to the canvas object first.
	/**
	 * Registers a global input processor with the current scene.
	 * The global input processor will then recieve input events from all input sources 
	 * as long as this scene is active. 
	 * We can then add our own listeners to the global input processor.
	 * 
	 * @param processor the processor
	 */
	public void registerGlobalInputProcessor(AbstractGlobalInputProcessor processor){
		//Let the inputprocessor listen to the inputsources
		mtApplication.getInputManager().registerGlobalInputProcessor(this, processor);
		//Set this scenes main canvas to listen to the inputprocessor
//		processor.addProcessorListener(this.getCanvas()); //FIXME TESTWISE DISABLED
	}
	
	
	/**
	 * Unregisters a global input processor.
	 * 
	 * @param processor the processor
	 */
	public void unregisterGlobalInputProcessor(AbstractGlobalInputProcessor processor){
		mtApplication.getInputManager().unregisterGlobalInputProcessor(processor);
		processor.removeProcessorListener(this.getCanvas()); //FIXME can be removed now..?
	}
	
	/**
	 * Gets the global input processors.
	 * 
	 * @return the global input processors
	 */
	public AbstractGlobalInputProcessor[] getGlobalInputProcessors(){
		return mtApplication.getInputManager().getGlobalInputProcessors(this);
	}
	
	
	/**
	 * Registers an action to be processed before the next frame
	 * in the main drawing thread.
	 * 
	 * @param action the action
	 */
	public void registerPreDrawAction(IPreDrawAction action){
		synchronized (preDrawActions) {
			this.preDrawActions.addLast(action);
		}
	}

	
	/**
	 * Unregisters an PreDrawAction.
	 * 
	 * @param action the action
	 */
	public void unregisterPreDrawAction(IPreDrawAction action){
		synchronized (preDrawActions) {
			this.preDrawActions.remove(action);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.Iscene#getTransition()
	 */
	public ITransition getTransition(){
		return this.transition;
	}
	
	/**
	 * Sets the transition effect to use when a scene change takes
	 * place from this scene to another scene.
	 * 
	 * @param transition the new transition
	 */
	public void setTransition(ITransition transition){
		this.transition = transition;
	}
	
	
	/**
	 * Destroys the scene. Call this if the scene definitely isnt going to be used anymore.
	 * The scene can only be destroyed if it was added to the application and it isnt the currently
	 * active scene.
	 * - Destroys the scene's canvas
	 * - removes the global input listeners from the input sources
	 * 
	 * @return true, if successful
	 */
	public boolean destroy(){
		//If not already done, remove the scene from the mt application (only if not current scene)
		if (this.mtApplication.removeScene(this)){
			this.mtApplication.invokeLater(new Runnable() {
				public void run() {
					//Remove all global input processors of this scene from listening the the input sources
					AbstractGlobalInputProcessor[] inputProcessors = getGlobalInputProcessors();
                    for (AbstractGlobalInputProcessor abstractGlobalInputProcessor : inputProcessors) {
                        unregisterGlobalInputProcessor(abstractGlobalInputProcessor);
                    }
				}
			});
			
			//Destroy the scene's canvas
			this.getCanvas().destroy();	
			
			preDrawActions.clear();
			
			logger.info("Destroyed scene: " + this.getName());
			return true;
		}else{
			//Try to destroy if removal of the scene failed because of a pending transition.
			this.mtApplication.destroySceneAfterTransition(this);
			logger.warn("Cant destroy currently active scene! (" + this.getName() + ") -> If scene in transition, trying to destroy afterwards.");
			return false;
		}
	}
}
