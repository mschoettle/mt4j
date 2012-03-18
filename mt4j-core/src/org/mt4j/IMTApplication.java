package org.mt4j;

import org.mt4j.components.css.util.CSSStyleManager;
import org.mt4j.input.IKeyListener;
import org.mt4j.input.InputManager;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.sceneManagement.ISceneChangeListener;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11;
import org.mt4j.util.opengl.GL11Plus;
import org.mt4j.util.opengl.GL20;
import org.mt4j.util.opengl.GLCommon;

public interface IMTApplication extends IPAppletBoth{
	


	/**
	 * Registers an action to be processed before the next frame
	 * in the main drawing thread.
	 * 
	 * @param action the action
	 */
	public void registerPreDrawAction(final IPreDrawAction action);

	/**
	 * Unregisters an PreDrawAction.
	 * 
	 * @param action the action
	 */
	public void unregisterPreDrawAction(final IPreDrawAction action);

	/**
	 * Checks if is render thread is current.
	 *
	 * @return true, if is render thread current
	 */
	public boolean isRenderThreadCurrent();

	/**
	 * Invokes the specified runnable at the beginning the next rendering loop in the rendering thread.
	 * This is especially useful for executing opengl commands from another thread - which would lead to errors
	 * if not synchronized with the rendering thread.
	 * 
	 * @param runnable the runnable
	 */
	public void invokeLater(Runnable runnable);

	/**
	 * Checks which scene is on top of the scene stack at the moment.
	 * If no scene has been pushed on the stack, null is returned.
	 * 
	 * @return the iscene
	 */
	public Iscene peekScene();

	/**
	 * Pushes the current scene on the scene stack.
	 */
	public void pushScene();

	/**
	 * Pops the scene thats currently ontop of the scene stack and changes back to it. 
	 * If the stack is empty no error is thrown and no scene change will happen.
	 */
	public boolean popScene();

	/**
	 * Changes the scene to the specified scene.
	 * <p>NOTE: This is not threadsafe while using OpenGL mode. If in openGL mode make,
	 * sure to call this only from the same thread. If running in a different thread,
	 * execute the scene change using the <code>invokeLater(Runnable runnable)</code> method 
	 * of the MTApplication instance!
	 * <p>NOTE: If the scene is not already added to the application by invoking <code>addScene()</code>, the scene
	 * is automatically added to the mtapplication.
	 * 
	 * @param newScene the new scene
	 */
	public boolean changeScene(Iscene newScene);

	/**
	 * Gets the currently active scene.
	 * 
	 * @return the current scene
	 */
	public Iscene getCurrentScene();

	/**
	 * Adds the scene to the list of scenes. 
	 * Also changes to that scene if it is the first one to be added.
	 * 
	 * @param scene the scene
	 */
	public void addScene(Iscene scene);

	/**
	 * Adds all scenes.
	 * 
	 * @param scenes the scenes
	 */
	public void addAll(Iscene[] scenes);

	/**
	 * Removes the scene from the list of scenes. Fails if the scene is the currently active scene.
	 * If the scene isnt going to be used anymore, calling the scene's destroy() method is the better choice
	 * than the removeScene method alone.
	 * 
	 * @param scene the scene
	 */
	public boolean removeScene(Iscene scene);
	
	/**
	 * Destroy scene after transition. Workaround so that if a scene's destroy() method is called
	 * but the scene is in a transition (cant be removed then) we call destroy on the scene after
	 * the transition.
	 * Only has an impact if there is a pending transition with the specified scene as the last scene.
	 * 
	 * @param scene the scene
	 */
	public void destroySceneAfterTransition(Iscene scene);

	/**
	 * Gets the registered scenes.
	 * 
	 * @return the scenes
	 */
	public Iscene[] getScenes();

	/**
	 * Gets the scene by name.
	 * 
	 * @param name the name
	 * 
	 * @return the scene
	 */
	public Iscene getScene(String name);

	/**
	 * Gets the scene count.
	 * 
	 * @return the scene count
	 */
	public int getSceneCount();

	/**
	 * Gets the input manager.
	 * 
	 * @return the input manager
	 */
	public InputManager getInputManager();

	/**
	 * Sets the input manager.
	 * 
	 * @param inputManager the new input manager
	 */
	public void setInputManager(InputManager inputManager);


	/**
	 * Adds a scene change listener.
	 * 
	 * @param listener the listener
	 */
	public void addSceneChangeListener(ISceneChangeListener listener);

	/**
	 * Removes the scene change listener.
	 * 
	 * @param listener the listener
	 */
	public void removeSceneChangeListener(ISceneChangeListener listener);

	/**
	 * Gets the scene change listeners.
	 * 
	 * @return the scene change listeners
	 */
	public ISceneChangeListener[] getSceneChangeListener();

	/////////////////////////////////	

	public CSSStyleManager getCssStyleManager();
	
	
//	public PGraphics getPGraphics();
//	
//	public PMatrix3D getModelView();
//	
//	public PMatrix3D getModelViewInv();
	
	 /**
     * Returns whether OpenGL ES 1.1 is available. If it is you can get an instance of {@link GL11} via {@link #getGL11()} to
     * access OpenGL ES 1.1 functionality. This also implies that {@link #getGL10()} will return an instance.
     * 
     * @return whether OpenGL ES 1.1 is available
     */
    public boolean isGL11Available ();

    /**
     * Returns whether OpenGL ES 2.0 is available. If it is you can get an instance of {@link GL20} via {@link #getGL20()} to
     * access OpenGL ES 2.0 functionality. Note that this functionality will only be available if you instructed the
     * {@link Application} instance to use OpenGL ES 2.0!
     * 
     * @return whether OpenGL ES 2.0 is available
     */
    public boolean isGL20Available ();
    
    /**
     * @return a {@link GLCommon} instance
     */
    public GLCommon getGLCommon ();

    /**
     * @return the {@link GL10} instance or null if not supported
     */
    public GL10 getGL10 ();

    /**
     * @return the {@link GL11} instance or null if not supported
     */
    public GL11 getGL11 ();
    
    /**
     * @return the {@link GL20} instance or null if not supported
     */
    public GL20 getGL20 ();
    
    public GLCommon beginGL() ;
    
    public void endGL();
    
    
    public void addKeyListener(IKeyListener listener);
	
	public void removeKeyListener(IKeyListener listener);
	
	public IKeyListener[] getKeyListener();

	public GL11Plus getGL11Plus();

}