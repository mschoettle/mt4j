/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mt4j.components.PickResult.PickEntry;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.clusters.Cluster;
import org.mt4j.components.css.util.CSSStylableComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.interfaces.IMTController;
import org.mt4j.input.ComponentInputProcessorSupport;
import org.mt4j.input.GestureEventSupport;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.camera.IFrustum;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * This is the base class for all MT4j scene graph nodes. It provides basic methods
 * for adding and managing child nodes/components. It also allows for changing the components position and
 * orientation in space. Picking those components with a picking ray is also supported, if intersection
 * testing is properly implemented by extending subclasses.
 * <p>
 * This base class has no visible representation an thus can be used
 * as a group container node for other scene graph components.
 * 
 * @author Christopher Ruff
 */
public class MTComponent implements IMTComponent3D, IMTInputEventListener, IGestureEventListener{ 
	/** The Constant logger. */
	private static final ILogger logger = MTLoggerFactory.getLogger(MTComponent.class.getName());
	static{
//		logger.setLevel(ILogger.ERROR);
		logger.setLevel(ILogger.WARN);
//		logger.setLevel(ILogger.DEBUG);
	}
	
	//CSS Enhancements
	private String CSSID = "";
	
	public String getCSSID() {
		return CSSID;
	}

	public void setCSSID(String cSSID) {
		CSSID = cSSID;
		if (this instanceof CSSStylableComponent) {
			CSSStylableComponent csc = (CSSStylableComponent)this;
			csc.applyStyleSheet();
		}
	}
	//CSS Enhancements
	
	/** The ID. */
	private int ID;
	
	/** The current id. */
	private static int currentID;
	
	/** The renderer. */
	private PApplet renderer;
	
	/** The name. */
	private String name;
	
	/** The visible. */
	private boolean visible;
	
	/** The enabled. */
	private boolean enabled;
	
	/** The pickable. */
	private boolean pickable;
	
	/** The drawn on top. */
	private boolean drawnOnTop;
	
	/** The parent. */
	private MTComponent parent;
	
	/** The child components. */
	private ArrayList<MTComponent> childComponents;
	
//	/** The custom view port. */
//	private ViewportSetting customViewPort;
//	
//	/** The default view port setting. */
//	private ViewportSetting defaultViewPortSetting;
	
	/** The composite. */
	private boolean composite;
	
	//	Matrix Stuff
	/** The local matrix. */
	private Matrix localMatrix;
	
	/** The local inverse matrix. */
	private Matrix localInverseMatrix;
	
	/** The local to Global matrix. */
	private Matrix globalMatrix;
	
	/** The Global to local matrix. */
	private Matrix globalToLocalMatrix;
	
	/** The local to Global matrix dirty. */
	private boolean globalMatrixDirty;
	
	/** The Global to local matrix dirty. */
	private boolean globalInverseMatrixDirty;
	
//	/** The pgraphics3 d. */
//	private PGraphics3D pgraphics3D;
	
	/** The controller. */
	private IMTController controller;
	
	//FIXME EXPERIMENTAL
	/** The light. */
	private MTLight light;
	
//	private PropertyChangeSupport propertyChangeSupport;
	/** The state change support. */
	private StateChangeSupport stateChangeSupport;
	
	/** The _translation computation. */
	private Matrix[] _translationComputation; 
	
	/** The _x rotation computation. */
	private Matrix[] _xRotationComputation; 
	
	/** The _y rotation computation. */
	private Matrix[] _yRotationComputation; 
	
	/** The _z rotation computation. */
	private Matrix[] _zRotationComputation; 
	
	/** The _scaling computation. */
	private Matrix[] _scalingComputation; 
	
	/** The input processors support. */
	private ComponentInputProcessorSupport inputProcessorsSupport;
	
	/** The gesture evt support. */
	private GestureEventSupport gestureEvtSupport;
	
	/** The allowed gestures. */
	private ArrayList<Class<? extends IInputProcessor>> allowedGestures;
	
	/** The attached camera. */
	private Icamera attachedCamera;
	
	/** The viewing camera. */
	private Icamera viewingCamera;
	
	/** The input listeners. */
	private ArrayList<IMTInputEventListener> inputListeners;
	
	/** The user data. */
	private Map<Object, Object> userData;
	
	/** The clip. */
	private Clip clip;
	
	/** The child clip. */
	private Clip childClip;
	
	private int inversePrecisionErrors;
	private int orthogonalityErrors;
	protected static final int invPrecisionThreshold = 1000;
	protected static final int reOrthogonalizeThreshold = 1500;
	
	private boolean isAndroid;
	
	
	/**
	 * Creates a new component. The component has no initial visual representation.
	 * 
	 * @param pApplet the applet
	 */
	public MTComponent(PApplet pApplet){
		this(pApplet , "unnamed component", null);
	}
	
	/**
	 * Creates a new component. The component has no visual representation.
	 * 
	 * @param pApplet the applet
	 * @param name the name
	 */
	public MTComponent(PApplet pApplet, String name){ 
		this(pApplet , name, null);
	}
	
	
	/**
	 * Creates a new component. The component has no visual representation.
	 * 
	 * @param pApplet the applet
	 * @param attachedCamera the camera to view this and this components children with
	 */
	public MTComponent(PApplet pApplet, Icamera attachedCamera){ 
		this(pApplet , "unnamed component", attachedCamera);
	}
	
	/**
	 * Creates a new component. The component has no visual representation.
	 * 
	 * @param pApplet the applet
	 * @param name the name of the component
	 * @param attachedCamera a camera to view this and this components children with
	 */
	public MTComponent(PApplet pApplet, String name, Icamera attachedCamera){
		synchronized (this) { 
			this.ID = currentID++;
		}
		//Defaults
		this.renderer = pApplet;
		this.visible = true;
		this.enabled = true;
		this.pickable = true;
		this.drawnOnTop = false;
		this.name = name;
		this.composite = false;
		
		this.childComponents = new ArrayList<MTComponent>();

		//			//Default viewport, can be changed in subclass //FIXME REMOVE?
		//			this.defaultViewPortSetting 	= new ViewportSetting(0, 0, this.getRenderer().width ,this.getRenderer().height);
		//			this.customViewPort 			= null;

		//(Cached) Matrices of this component
		this.localMatrix 		= new Matrix();
		this.localInverseMatrix	= new Matrix(); 
		this.globalMatrix		= new Matrix();
		this.globalToLocalMatrix= new Matrix();

		this.globalMatrixDirty = true;
		this.globalInverseMatrixDirty = true;

//		//This class should only be used with a renderer derived from pgraphics3D!
//		this.pgraphics3D = (PGraphics3D)pApplet.g;

		//FIXME EXPERIMENTAL
		light = null;

		//			propertyChangeSupport 	= new PropertyChangeSupport(this);

		//			stateChangeSupport 		= new StateChangeSupport(this);

		_translationComputation 	= new Matrix[]{new Matrix(), new Matrix()}; 
		_xRotationComputation 		= new Matrix[]{new Matrix(), new Matrix()}; 
		_yRotationComputation 		= new Matrix[]{new Matrix(), new Matrix()}; 
		_zRotationComputation 		= new Matrix[]{new Matrix(), new Matrix()}; 
		_scalingComputation 		= new Matrix[]{new Matrix(), new Matrix()}; 

		allowedGestures = new ArrayList<Class<? extends IInputProcessor>>(5);

		//TODO lazily instantiate gesturehandler/arraylist so that graphicobjects arent expensive at creation?

		this.inputListeners = new ArrayList<IMTInputEventListener>(3);

		//Delegate input processing/gesture detection to a special handler
		this.inputProcessorsSupport = new ComponentInputProcessorSupport(pApplet, this);
		//Let the input processor support class listen to the component's input events
		this.addInputListener(inputProcessorsSupport);

		this.gestureEvtSupport = new GestureEventSupport();

		this.attachedCamera = attachedCamera;
		this.viewingCamera = attachedCamera;

		this.inversePrecisionErrors = 0;
		this.orthogonalityErrors  = 0;
		
		this.isAndroid = PlatformUtil.isAndroid();
	}

	
	// BOUNDS STUFF ///////////////////////////////////
	/** The bounds */
	private IBoundingShape bounds;
	
	/**
	 * Sets the bounding shape.
	 * 
	 * @param boundingShape the new bounding shape
	 * @deprecated renamed to <code>setBounds</code>
	 */
	public void setBoundingShape(IBoundingShape boundingShape){
		this.bounds = boundingShape;
		this.setBoundsGlobalDirty(true);
	}	
	/**
	 * Gets the bounding shape.
	 * 
	 * @return the bounding shape
	 * @deprecated renamed to <code>getBounds</code>
	 */
	public IBoundingShape getBoundingShape(){
		return this.bounds;
	}
	/**
	 * Checks if is bounding shape set.
	 * @return true, if is bounding shape set
	 * @deprecated renamed to <code>hasBounds</code>
	 */
	public boolean isBoundingShapeSet(){
		return this.bounds != null;
	}
	
	
	/**
	 * Sets the bounding shape.
	 * @param boundingShape the new bounding shape
	 */
	public void setBounds(IBoundingShape boundingShape){
		this.bounds = boundingShape;
		this.setBoundsGlobalDirty(true);
	}	
	
	/**
	 * Gets the bounding shape.
	 * @return the bounding shape
	 */
	public IBoundingShape getBounds(){
		return this.bounds;
	}
	
	/**
	 * Checks if is bounding shape set.
	 * @return true, if is bounding shape set
	 */
	public boolean hasBounds(){
		return this.bounds != null;
	}
	
	//TODO REMOVE?
	/**
	 * Sets the bounds global vertices dirty.
	 * 
	 * @param boundsWorldVerticesDirty the new bounds world vertices dirty
	 */
	private void setBoundsGlobalDirty(boolean boundsWorldVerticesDirty) {
//		this.boundsGlobalVerticesDirty = boundsWorldVerticesDirty;
		if (this.hasBounds()){
			this.getBounds().setGlobalBoundsChanged();
		}
	}
	// BOUNDS STUFF ////////////////////////////////
	
	
	
/// CAMERA SETTINGS /////////////////////////////////////	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent3D#getViewingCamera()
	 */
	public Icamera getViewingCamera(){
		if (this.viewingCamera != null){
			return this.viewingCamera;
		}else{
			//Search up the component tree for a attached camera
			//automatically sets this viewcamera to the attached camera if found
			this.viewingCamera = this.searchViewingCamera();
			return this.viewingCamera;
		}
	}
	
	/**
	 * Search viewing camera.
	 */
	protected Icamera searchViewingCamera(){
		 this.viewingCamera = this.searchViewingCamRecur(this);
		 return this.viewingCamera;
	}
	
	/**
	 * Search viewing cam recur.
	 * 
	 * @param current the current
	 * @return the icamera
	 */
	private Icamera searchViewingCamRecur(MTComponent current){
		if (current.attachedCamera != null){
			return current.attachedCamera;
		}else{
			if (current.getParent() != null){
				return searchViewingCamRecur(current.getParent());
			}else{
				return null;
			}
		}
	}
	
	/**
	 * Propagate cam change.
	 * 
	 * @param cam the cam
	 */
	private void propagateCamChange(Icamera cam){
		this.propagateCamChangeRecur(this, cam);
	}
	
	/**
	 * Propagate cam change recur.
	 * 
	 * @param current the current
	 * @param cam the cam
	 */
	private void propagateCamChangeRecur(MTComponent current, Icamera cam){
		//Only propagate further if current has no attached cam of its own
		//or it is the same as the propagated one
		//-> dont overwrite other attached cams down the tree!
		if (current.getAttachedCamera() == null
						||
			(current.getAttachedCamera() != null 
						&& 
			(current.getAttachedCamera().equals(cam)))
		){
			current.viewingCamera = cam;
			for (MTComponent child: current.getChildList()){
				child.propagateCamChange(cam);
			}
		}
	}
	
	/**
	 * Gets the camera attached to this component or null if it doesent
	 * have one.
	 * 
	 * @return the attached camera
	 */
	public Icamera getAttachedCamera() {
		return attachedCamera;
	}
	
	/**
	 * Attaches a camera to this component.
	 * This component and all its children will be viewed 
	 * through the specified camera.
	 * 
	 * @param attachedCamera the attached camera
	 */
	public void attachCamera(Icamera attachedCamera) {
		this.attachedCamera = attachedCamera;
		this.viewingCamera = attachedCamera;
		this.propagateCamChange(attachedCamera); 
	}
	/// CAMERA SETTINGS /////////////////////////////////////
	
	
	
	
//Property Change Support ////////////////////
	/*
	//TODO use? remove?
	public void addPropertyChangeListener(PropertyChangeListener listener){
		this.propertyChangeSupport.addPropertyChangeListener( listener );
	}

	public void removePropertyChangeListener(PropertyChangeListener listener){
		this.propertyChangeSupport.removePropertyChangeListener( listener );
	}

	
	protected void firePropertyChange(PropertyChangeEvent arg0) {
		propertyChangeSupport.firePropertyChange(arg0);
	}

	protected void firePropertyChange(String arg0, boolean arg1, boolean arg2) {
		propertyChangeSupport.firePropertyChange(arg0, arg1, arg2);
	}
	protected void firePropertyChange(String arg0, int arg1, int arg2) {
		propertyChangeSupport.firePropertyChange(arg0, arg1, arg2);
	}
	protected void firePropertyChange(String arg0, Object arg1, Object arg2) {
		propertyChangeSupport.firePropertyChange(arg0, arg1, arg2);
	}
	
	static final String PROPERTY_NAME_STRING = "name";
	*/
//	Propery Change Support ////////////////////
	


	////STATE CHANGE SUPPORT /////
	/**
     * Checks if the map is null and then lazily initializes it.
     */
    private void lazyInitStateChangeSupport(){
    	if (stateChangeSupport == null){
    		stateChangeSupport = new StateChangeSupport(this);
    	}
    }
    
	/**
	 * Adds the state change listener.
	 * 
	 * @param state the state
	 * @param listener the listener
	 */
	public void addStateChangeListener(StateChange state, StateChangeListener listener) {
		this.lazyInitStateChangeSupport();
		stateChangeSupport.addStateChangeListener(state, listener);
	}
	
	
	/**
	 * Gets the state change listeners.
	 * @return the state change listeners
	 */
	public StateChangeListener[] getStateChangeListeners(){
		return stateChangeSupport.getListeners();
	}

	/**
	 * Removes the state change listener.
	 * 
	 * @param state the state
	 * @param listener the listener
	 */
	public void removeStateChangeListener(StateChange state, StateChangeListener listener) {
		if (stateChangeSupport != null){
			stateChangeSupport.removeStateChangeListener(state, listener);			
		}
	}
	
	/**
	 * Fire state change.
	 * 
	 * @param evt the evt
	 */
	protected void fireStateChange(StateChangeEvent evt) {
		this.lazyInitStateChangeSupport();
		stateChangeSupport.fireStateChange(evt);
	}

	/**
	 * Fire state change.
	 * 
	 * @param state the state
	 */
	protected void fireStateChange(StateChange state) { 
		this.lazyInitStateChangeSupport();
		stateChangeSupport.fireStateChange(state);
	}
/////STATE CHANGE SUPPORT /////

	/**
	 * <li>Removes this component from its parent.
	 * <li>Calls <code>destroyComponent</code> on this component which
	 * can be used to free resources that the component used.
	 * <li>Recursively calls destroy on alls its child components
	 */
	public void destroy(){
//		System.out.println(this + " -> DESTROY() -> (MTComponent)");
		
//		List<MTComponent> children = this.getChildList();
		//We save the children in an array because the childList might get modified
		//during destruction and we wont call destroy on all children then!
		MTComponent[] childArr = this.getChildren(); 
		
		if (this.getParent() != null){
			this.removeFromParent(); //really do this?
			this.fireStateChange(StateChange.REMOVED_FROM_PARENT);
		}
		this.destroyComponent();
		this.fireStateChange(StateChange.COMPONENT_DESTROYED);
		
//		/*
		if (userData != null){
			this.userData.clear();	
		}
		this.unregisterAllInputProcessors();
		
//		if (this.stateChangeSupport != null){
//			this.stateChangeSupport = null;
//		}
//		if (this.gestureEvtSupport != null){
//			this.gestureEvtSupport = null;
//		}
//		*/
		
		for (int i = 0; i < childArr.length; i++) {
			childArr[i].destroy();
		}
	}
	
	/**
	 * <br>Override this to clean up resources when destroying a component.
	 * This method gets called by the <code>destroy</code> method. So you shouldnt
	 * invoke this method directly.
	 */
	protected void destroyComponent(){ 	}

	
	/**
	 * Applies (multiplies) this component's local matrix to processings current matrix.
	 */
	protected void applyLocalMatrix(){
		this.applyMatrixToProcessingModelView(localMatrix);
	}
	
	/**
	 * Applies (multiplies) the matrix to processings current matrix.
	 * Developer note: Processings <code>applyMatrix</code> is saver than <code>g.modelview.apply</code>(?)
	 * because it also calculates a new inverse. (important for lightning calculations?)
	 * 
	 * @param m the m
	 */
	private void applyMatrixToProcessingModelView(Matrix m){
//This is slower because it also calcs the inverse..
//		pgraphics3D.applyMatrix(m.m00, m.m01, m.m02,  m.m03,
//				m.m10, m.m11, m.m12,  m.m13,
//				m.m20, m.m21, m.m22,  m.m23,
//				m.m30, m.m31, m.m32,  m.m33);
		
//		pgraphics3D.modelview.apply(
//				m.m00, m.m01, m.m02,  m.m03,
//				m.m10, m.m11, m.m12,  m.m13,
//				m.m20, m.m21, m.m22,  m.m23,
//				m.m30, m.m31, m.m32,  m.m33
//		);
		

		if (isAndroid){
			getRenderer().g.applyMatrix(
					m.m00, m.m01, m.m02,  m.m03,
					m.m10, m.m11, m.m12,  m.m13,
					m.m20, m.m21, m.m22,  m.m23,
					m.m30, m.m31, m.m32,  m.m33);
		}else{
			PlatformUtil.getModelView().apply(
					m.m00, m.m01, m.m02,  m.m03,
					m.m10, m.m11, m.m12,  m.m13,
					m.m20, m.m21, m.m22,  m.m23,
					m.m30, m.m31, m.m32,  m.m33
			);
		}

		
		/*
		Matrix mInv = localInverseMatrix;
		pgraphics3D.modelviewInv.preApply(
				mInv.m00, mInv.m01, mInv.m02,  mInv.m03,
				mInv.m10, mInv.m11, mInv.m12,  mInv.m13,
				mInv.m20, mInv.m21, mInv.m22,  mInv.m23,
				mInv.m30, mInv.m31, mInv.m32,  mInv.m33
		);
		*/
	}
	
	//TODO REMOVE?
//	/**
//	 * Checks if is matrices dirty.
//	 * 
//	 * @return true, if is matrices dirty
//	 */
//	public boolean isMatricesDirty(){
//		return (this.globalMatrixDirty || this.globalInverseMatrixDirty);
//	}
	
	/**
	 * Informs the object (and its children), that its matrix - OR ONE OF ITS PARENT'S MATRIX - has been altered.
	 * <br>Usually this shouldnt be called by the user himself.
	 * 
	 * @param matricesDirty the matrices dirty
	 */
	public void setMatricesDirty(boolean matricesDirty) {
//		System.out.println("Setting matrices dirty->" + matricesDirty + " on: "  + this.getName());
		if (matricesDirty){
			//FIXME BOUNDS TEST
			this.setBoundsGlobalDirty(true);
			
			//absolute matrix �ndert sich damit auch auch wenn dr�ber parents geadded werden!
			this.setGlobalMatrixDirty(true);
			
			this.setGlobalInverseMatrixDirty(true);
			
			//Also inform the children, so they know that parent changed
			this.propagateMatrixChange(true);
		}else{//baseMatrixDiry == false
			this.globalMatrixDirty = matricesDirty;
			this.globalInverseMatrixDirty = matricesDirty;
		}
	}
	
	/**
	 * Inform the children of the matrix change.
	 * 
	 * @param matrixDirty the matrix dirty
	 */
	private void propagateMatrixChange(boolean matrixDirty){
//		System.out.println("Setting basematrix dirty on obj: " + this.getName());
        for (MTComponent object : childComponents) {
            //TEST - only propagate unitil we get to a already dirty component
            //this should work because the dirty component should also have dirty children already
            //CAUTION: object can have for example a dirty global matrix and a clean global inverse matrix
            //so we check if both are dirty and only then dont propagate the dirty state
            //FIXME NOT WORKING WITH SVG EXAMPLEaaaaaaaaa - cause of composite?
//			if ((!object.isGlobalInverseMatrixDirty() || !object.isGlobalMatrixDirty())){ 
            object.setMatricesDirty(matrixDirty);
//			}
//			else{
//				System.out.println("Stopping matrix changed propagation at: " + object.getName() +  " because both its matrices are already dirty.");
//			}
        }
	}
	
	/**
	 * Checks if Global matrix is dirty.
	 * 
	 * @return true, if checks if is abs local to Global matrix dirty
	 * 
	 * whether the cached  Global matrix is still valid
	 */
	private boolean isGlobalMatrixDirty() {
		return globalMatrixDirty;
	}

	/**
	 * Sets the Global matrix dirty.
	 * 
	 * @param globalMatrixDirty the local to Global matrix dirty
	 */
	private void setGlobalMatrixDirty(boolean globalMatrixDirty) {
		/*
		if (localToGlobalMatrixDirty){
			System.out.println(this.getName() + ": Setting global Matrix DIRTY!");
		}else{
			System.out.println(this.getName() + ": Setting global Matrix NOT dirty!");
		}
		*/
		this.globalMatrixDirty = globalMatrixDirty;
	}
	
	/**
	 * Checks if is global to local matrix dirty.
	 * 
	 * @return true, if checks if is global to local matrix dirty
	 * 
	 * whether the cached absolute inverse global matrix is still valid
	 */
	private boolean isGlobalInverseMatrixDirty() {
		return globalInverseMatrixDirty;
	}
	
	/**
	 * Sets the Global Inverse matrix dirty.
	 * 
	 * @param dirty the dirty
	 */
	private void setGlobalInverseMatrixDirty(boolean dirty) {
		/*
		if (dirty){
			System.out.println(this.getName() + ": Setting global inverse Matrix DIRTY!");
		}else{
			System.out.println(this.getName() + ": Setting global inverse Matrix NOT dirty!");
		}
		*/
		this.globalInverseMatrixDirty = dirty;
	}
	
	/**
	 * Gets the local basis matrix.
	 * This is the matrix responsible for transforming this component relative to its parent.
	 * 
	 * @return the local basis matrix
	 * 
	 * The matrix describing the local coordinate space of this object.
	 */
	public Matrix getLocalMatrix() {
		return localMatrix;
	}
	
	/**
	 * Sets a matrix by which this component and its children will be transformed.
	 * <br>Also calculates and sets the corresponding local inverse matrix. (expensive call!)
	 * 
	 * @param localBasisMatrix the local basis matrix
	 */
	public void setLocalMatrix(Matrix localBasisMatrix) {
		this.setLocalMatrixInternal(localBasisMatrix);
		try {
			//THIS OPERATION IS NOT CHEAP!
			this.setLocalInverseMatrixInternal(this.getLocalMatrix().invert());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets a matrix by which this component and its children will be transformed.
	 * <br>This should only be called internally. The corresponding inverse matrix
	 * has to be calculated and set also for the object to be consistent!
	 * <br>This method doesent calculate the inverse matrix!
	 * 
	 * @param localMatrix the local matrix
	 */
	private void setLocalMatrixInternal(Matrix localMatrix) {
		this.localMatrix = localMatrix;
		this.setMatricesDirty(true);
	}
	
	/**
	 * Gets the local inverse matrix.
	 * 
	 * @return the local inverse matrix
	 * 
	 * the local inverse transform matrix
	 */	
	public Matrix getLocalInverseMatrix() {
		return this.localInverseMatrix;
	}
	
	/**
	 * Sets the local inverse matrix.
	 * 
	 * @param localInverseMatrix the local inverse matrix
	 */
	private void setLocalInverseMatrixInternal(Matrix localInverseMatrix) {
		this.localInverseMatrix = localInverseMatrix;
	}
	
	
	/**
	 * Multiplies all transformation matrices of the
	 * objects parents up the this object and returns it.
	 * <br>This is so to speak the <b>"global matrix"</b> of the object.
	 * This matrix can be used to transform points in the components local space
	 * to the global space, the space where they actually appear in 3D space.
	 * 
	 * @return the local to global matrix
	 * 
	 * the absolute transformation (global) matrix of the object
	 */
	public Matrix getGlobalMatrix(){
		Matrix resMatrix = this.globalMatrix;
		//Calculate the absolute local to global matrix only if necessary
		if (this.isGlobalMatrixDirty()){
			//System.out.println(this.getName() + "'s global matrix is dirty! calculate it:");
			resMatrix = new Matrix();
			this.getGlobalMatrixRecursive(this, resMatrix);
			//System.out.println("Applying Matrix of: '" + this.getName() + "' Matrix: " + this.getLocalBasisMatrix().toString());
			resMatrix.multLocal(this.getLocalMatrix());
			
			this.globalMatrix = resMatrix;
			
			this.setGlobalMatrixDirty(false);
		}
		//System.out.println(this.getName() + "'s global matrix is not dirty!");
		return resMatrix;
	}
	
	
	/**
	 * Gets the abs matrix recursive.
	 * 
	 * @param current the current
	 * @param currentMatrix the current matrix
	 * 
	 * @return the abs matrix recursive
	 */
	private MTComponent getGlobalMatrixRecursive(MTComponent current, Matrix currentMatrix){
		//System.out.println("Processing: " + current.getName());
		if (current.getParent() != null){
			if (current.getParent().isGlobalMatrixDirty()){
				//System.out.println(">Parent not null: " + current.getParent().getName());
//				System.out.println(" Recursive loop - " + current.getParent().getName() + "'s global matrix IS dirty");
				MTComponent res = this.getGlobalMatrixRecursive(current.getParent(), currentMatrix);
				if (!res.getLocalMatrix().isIdentity()){
					currentMatrix.multLocal(res.getLocalMatrix());
					//We can set 
//					System.out.println(" Recursive loop - setting " + res.getName() + " global matrix");
					res.globalMatrix = new Matrix(currentMatrix);
					res.setGlobalMatrixDirty(false);
				}
				//System.out.println("Applying Matrix of: '" + res.getName() + "' Matrix: " + res.getLocalBasisMatrix().toString());
			}else{
				//Currents global matrix isnt dirty -> apply the global matrix and stop recursion upwards
//				System.out.println(" Recursive loop - " + current.getParent().getName() + "'s global matrix is NOT dirty - stop recursion");
				Matrix parentLocalToGlobal = current.getParent().getGlobalMatrix();
				currentMatrix.multLocal(parentLocalToGlobal);
			}
		}else{
//			System.out.println(" Recursive loop - " + current.getName() + " has no parent - stop recursion and use its local as its global matrix");
			if (current.isGlobalMatrixDirty()){
				current.globalMatrix = new Matrix(current.getLocalMatrix());
				current.setGlobalMatrixDirty(false);
			}
		}
		return current;
	}
	
	
	
	/**
	 * Returns the absolute inverse matrix (inverse of the global) which inverts all transforms made
	 * from the parents down to this child.
	 * This matrix can be used to transform a point in global space to the component's local object space (untransformed space).
	 * 
	 * @return the absolute global to local matrix
	 * , the absolute inverse transformation matrix of the object
	 */
	public Matrix getGlobalInverseMatrix() {
		Matrix resMatrix = this.globalToLocalMatrix;
		//Calculate the absolute local to global matrix only if necessary
		if (this.isGlobalInverseMatrixDirty()){
//			System.out.println("Getting global inverse of: " + this.getName() + " -its dirty!");
			if (this.getParent()!= null){
				resMatrix = new Matrix(this.getLocalInverseMatrix());
				this.getGlobalInvMatrixRecursive(this.getParent(), resMatrix);
				this.globalToLocalMatrix = resMatrix;
			}else{
				//no parent -> Global inverse is local inverse
				this.globalToLocalMatrix = this.getLocalInverseMatrix();
				resMatrix = this.globalToLocalMatrix;
			}
			this.setGlobalInverseMatrixDirty(false);
		}
		return resMatrix;
	}
	

	//TODO maybe cheaper to call globalMatrix.invert() than get the matrix recursively?
	/**
	 * Gets the global inverse matrix recursive.
	 * 
	 * @param current the current
	 * @param currentMatrix the current matrix
	 * 
	 * @return the abs inv matrix recursive
	 */
	private void getGlobalInvMatrixRecursive(MTComponent current, Matrix currentMatrix){
//		System.out.println("processing: " + current.getName() + " Inverse Matrix: " + current.getLocalInverseMatrix()) ;
		
		if (current.isGlobalInverseMatrixDirty()){
//			System.out.println(" Recursive Loop -> " +  current.getName() + "'s global inverse is dirty - applying it");
			if (!current.getLocalInverseMatrix().isIdentity()){
				currentMatrix.multLocal(current.getLocalInverseMatrix());
			}
			if (current.getParent() != null)
				this.getGlobalInvMatrixRecursive(current.getParent(), currentMatrix);
		}else{
			//current isnt dirty, -> get the current absolute global inverse and apply it and stop recursion
//			System.out.println(" Recursive Loop -> " +  current.getName() + "'s global inverse is not dirty - get "  + current.getName() + "'s currents global inverse matrix and stop recursion");
			if (!current.getGlobalInverseMatrix().isIdentity()){
				currentMatrix.multLocal(current.getGlobalInverseMatrix());
			}
		}
	}
	
	
	/**
	 * Transforms the point - defined in the objects coordinate space - into parent relative space.
	 * This is done by multiplying the point with the objects local basis matrix.
	 * 
	 * @param referenceComp the reference comp
	 * @param point the point
	 * 
	 * @return the obj space vec to parent relative space
	 */
	public static Vector3D getLocalVecToParentRelativeSpace(MTComponent referenceComp, Vector3D point){
		Vector3D ret = point.getCopy();
		ret.transform(referenceComp.getLocalMatrix());
		return ret;
	}

	/**
	 * Transforms the given vector in global space coordinates
	 * to be relative to the given reference objects parent space.
	 * <br>Applies the inverse transforms associated with this parents actor
	 * and its ancestors to the vector.
	 * <br>NOTE: This transforms the global vector into the reference objects partent
	 * space! Not the reference obj's local space!
	 * 
	 * <br>Example:<br>
	 * If we would want to rotate an object that is in an arbitrary
	 * transformation hierarchy in the scene graph, around a point that is defined in Global coordinates,
	 * we have to transform the point to be relative to the object's parent transformation space.
	 * 
	 * <br>This is done by transforming the point by the inverse Global matrix of the objects parent.
	 * 
	 * @param referenceComp the vector will be relative to this components parent space
	 * @param point the point
	 * 
	 * @return the global vec to parent relative space
	 * 
	 * the transformed Vector
	 */
	public static Vector3D getGlobalVecToParentRelativeSpace(MTComponent referenceComp, Vector3D point){
		//Returns point relative to the references parent!
		if (referenceComp.getParent() == null){ 
			return point.getCopy();
		}else{
			Vector3D ret = point.getCopy();
//			System.out.println("parent abs world to local matrix: " + referenceComp.getParent().getAbsoluteWorldToLocalMatrix());
			ret.transform(referenceComp.getParent().getGlobalInverseMatrix());
			return ret;
		}
//		Vector3D ret = point.getCopy(); //OLD WAY
//		ret.transform(referenceComp.getAbsoluteWorldToLocalMatrix());
//		return ret;
	}
	
	
	/**
	 * Calculates the transformation necessary to transform a component to be relative
	 * to the destination component.
	 * <p>
	 * So for example, if you want to add a component from a random position in the scence graph
	 * to a different component somewhere else in the scene graph, with the component remaining at the same global position,
	 * you would transform the first component with the Matrix from the call of
	 * <code>getTransformToDestinationParentSpace(originComponent,destinationComponent)</code>.
	 * Then you would add the component to the destination component as its child.
	 * The component will have the same global coordinates as before, but will now
	 * be under the influence of the new parents transforms etc.
	 * 
	 * @param originComponent the origin component
	 * @param destinationComponent the destination component
	 * 
	 * @return the transform to destination parent space
	 * 
	 * the matrix
	 */
	public static Matrix getTransformToDestinationParentSpace(MTComponent originComponent, MTComponent destinationComponent){
//		/*
		if (originComponent.getParent() != null){
			//Transform to world space, keeping only the objects internal transform
			Matrix compParentWorld = new Matrix(originComponent.getParent().getGlobalMatrix());
			//Transform to destination space, so that the destination 
			//will not change the actual objects transform, shape and position
			//by negating the destination obj's absolute transform
			destinationComponent.getGlobalInverseMatrix().mult(compParentWorld, compParentWorld);
			return compParentWorld;
		}else{
//			componentToTransform.transform(destinationComponent.getAbsoluteWorldToLocalMatrix());
			return destinationComponent.getGlobalInverseMatrix();
		}
//		*/
		
		/*
			//Transform to world space, keeping only the objects internal transform
//			componentToTransfrom.transform(componentToTransform.getParent().getAbsoluteLocalToWorldMatrix());
			Matrix compParentWorld = new Matrix(originComponent.getAbsoluteLocalToWorldMatrix());
			
			//Transform to destination space, so that the destination 
			//will not change the actual objects transform, shape and position
			//by negating the destination obj's absolute transform
//			componentToTransform.transform(destinationComponent.getAbsoluteWorldToLocalMatrix());
			if (destinationComponent.getParent() != null){
				destinationComponent.getParent().getAbsoluteWorldToLocalMatrix().mult(compParentWorld, compParentWorld);
				return compParentWorld;
			}else{
				return compParentWorld;
			}
			*/
	}
	
	/**
	 * Gets the transform to destination local space.
	 * 
	 * @param originComponent the origin component
	 * @param destinationComponent the destination component
	 * 
	 * @return the transform to destination local space
	 */
	public static Matrix getTransformToDestinationLocalSpace(MTComponent originComponent, MTComponent destinationComponent){
//		/*	//Worked with centerpoint
			//Transform to global space, keeping only the objects internal transform
//			componentToTransfrom.transform(componentToTransform.getParent().getAbsoluteLocalToWorldMatrix());
			Matrix compParentWorld = new Matrix(originComponent.getGlobalMatrix());
			
			//Transform to destination space, so that the destination 
			//will not change the actual objects transform, shape and position
			//by negating the destination obj's absolute transform
//			componentToTransform.transform(destinationComponent.getAbsoluteWorldToLocalMatrix());
			if (destinationComponent.getParent() != null){
				destinationComponent.getParent().getGlobalInverseMatrix().mult(compParentWorld, compParentWorld);
				return compParentWorld;
			}else{
				return compParentWorld;
			}
//			*/
			/*
			//Transform to world space, keeping only the objects internal transform
			//Works with textarea dragaway
			Matrix compParentWorld;
			if (originComponent.getParent() != null){
//				compParentWorld = new Matrix(originComponent.getAbsoluteLocalToWorldMatrix());
				compParentWorld = new Matrix(originComponent.getParent().getAbsoluteLocalToWorldMatrix());
			}else{
				compParentWorld = new Matrix();
			}
			//Transform to destination space, so that the destination 
			//will not change the actual objects transform, shape and position
			//by negating the destination obj's absolute transform
			destinationComponent.getAbsoluteWorldToLocalMatrix().mult(compParentWorld, compParentWorld);
			return compParentWorld;
			*/
	}
	

	/**
	 *  Converts the Vector3D object from the component's (local) coordinates to the world/canvas (global) coordinates.
	 *	<br><br>
	 *	This method allows you to convert any given x, y and z coordinates from values that are relative to 
	 *	the origin (0,0) of a specific component (local coordinates) to values that are relative to 
	 *	the origin of the canvas (global coordinates).
	 *	<br><br>
	 *	To use this method, first create an instance of the Vector3D class. 
	 *	The x, y and z values that you assign represent local coordinates because they relate to the origin of the component.
	 *	<br><br>
	 *	You then pass the Vector3D instance that you created as the parameter to the localToGlobal() method. 
	 *<br>
	 *	The method returns a new Vector3D object with x, y and z values that relate to the origin of the global/canvas instead of 
	 *  the origin of the component.
	 * 
	 * @param point the point 
	 * 
	 * @return A new vector3D object with coordinates relative to the global/canvas. 
	 */
	public Vector3D localToGlobal(Vector3D point){
		Vector3D ret = point.getCopy();
		ret.transform(this.getGlobalMatrix());
		return ret;
	}
	
	
	/**
	 *  Converts the Vector3D object from the component's (local) coordinates to the parent component's coordinates.
	 *	<br><br>
	 *	This method allows you to convert any given x, y and z coordinates from values that are relative to 
	 *	the origin (0,0) of a specific component (local coordinates) to values that are relative to 
	 *	the origin of the parent component.
	 *	<br><br>
	 *	To use this method, first create an instance of the Vector3D class. 
	 *	The x, y and z values that you assign represent local coordinates because they relate to the origin of the component.
	 *	<br><br>
	 *	You then pass the Vector3D instance that you created as the parameter to the localToParent() method. 
	 *<br>
	 *	The method returns a new Vector3D object with x, y and z values that relate to the origin of the parent instead of 
	 *  the origin of the component.
	 * 
	 * @param point the point 
	 * 
	 * @return A new vector3D object with coordinates relative to the parent component. 
	 */
	public Vector3D localToParent(Vector3D point){
		Vector3D ret = point.getCopy();
		ret.transform(this.getLocalMatrix());
		return ret;
	}
	
	
	/**
	 *  Converts the Vector3D object from the parent component's coordinates to this component's (local) coordinates.
	 *	<br><br>
	 *	This method allows you to convert any given x, y and z coordinates from values that are relative to 
	 *	the origin (0,0) of a parent to values that are relative to 
	 *	the origin of this component.
	 *	<br><br>
	 *	To use this method, first create an instance of the Vector3D class. 
	 *	The x, y and z values that you assign represent parent relative coordinates because they relate to the origin of 
	 *  the component's parent.
	 *	<br><br>
	 *	You then pass the Vector3D instance that you created as the parameter to the parentToLocal() method. 
	 *  <br>
	 *	The method returns a new Vector3D object with x, y and z values that relates to the origin of this component
	 *  instead of the origin of the parent component.
	 * 
	 * @param point the point 
	 * 
	 * @return A new vector3D object with coordinates relative to the components local space. 
	 */
	public Vector3D parentToLocal(Vector3D point){
		Vector3D ret = point.getCopy();
		ret.transform(this.getLocalInverseMatrix());
		return ret;
	}
	
	
	/**
	 * Converts the Vector3D object from the world (global) coordinates to the component's (local) coordinates.
	 *<br><br>
	 * To use this method, first create an instance of the Vector3D class.
	 * <br> 
	 * The x, y and z values that you assign represent global coordinates because they relate to the origin (0,0) of the main display area. 
	 * Then pass the Vector3D instance as the parameter to the globalToLocal() method. 
	 * <br>
	 * The method returns a new Vector3D object with x and y values that relate to the origin of the component  
	 * instead of the origin of the world.
	 * 
	 * @param point the point
	 * 
	 * @return a new vector3D object with coordinates relative to the component. 
	 */
	public Vector3D globalToLocal(Vector3D point){
		Vector3D ret = point.getCopy();
		ret.transform(this.getGlobalInverseMatrix());
		return ret;
	}
	
	
	/**
	 * Transforms the global ray into local coordinate space and returns the new ray.
	 * 
	 * @param globalRay the global ray
	 * 
	 * @return the ray
	 */
	public Ray globalToLocal(Ray globalRay){
//		return Ray.getTransformedRay(globalRay, this.getGlobalMatrix().invert());
		return Ray.getTransformedRay(globalRay, this.getGlobalInverseMatrix());
	}
	
//////////////////////////////////////////////////////////////
//					COMPONENT TRANSFORMATIONS				//
//////////////////////////////////////////////////////////////
	/*
	 * NOTE: the matrix and the inverse may suffer from rounding erros and not be exact inverse to each other!
	 * we calc the inverse incrementally -> rounding off errors accumulate
	 * -> after a certain number of rotations we use the invert() method on the localMatrices instead
	 * Also from incremental rotations the matrix may loose its orthogonality
	 * -> we re-orthogonalize after a certain number of rotations
	*/
	
	/**
	 * Transforms the shapes local coordinate space by the specified matrix. This operation 
	 * can be quite costly since it involves a matrix multiplication and a calculation of its inverse.
	 * 
	 * @param transformMatrix the transform matrix
	 */
	public void transform(Matrix transformMatrix) {
		this.setLocalMatrixInternal(transformMatrix.mult(this.getLocalMatrix(), this.getLocalMatrix()));
		try {
			//THIS OPERATION IS NOT CHEAP!
			//TODO maybe also only calculate this on demand? (at getLocalInverse() or getGlobalInverse())
			this.setLocalInverseMatrixInternal(this.getLocalMatrix().invert());
			this.inversePrecisionErrors = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	/**
	 * Translate. Move the component in the direction of the specified vector.
	 * The transformspace specifies the space, which the translation should be relative
	 * to.
	 * 
	 * @param dirVect the dir vect
	 * @param transformSpace the transform space
	 */
	public void translate(Vector3D dirVect, TransformSpace transformSpace) {
		switch (transformSpace) {
		case LOCAL:
			dirVect.transformDirectionVector(this.getLocalMatrix()); 
			break;
		case RELATIVE_TO_PARENT:
			//default
			break;
		case GLOBAL:
			if (this.getParent()!= null){
				//Transform direction vector from world space to this objs parent space
				dirVect.transformDirectionVector(this.getParent().getGlobalInverseMatrix());
			} 
			break;
		default:
			break;
		}
		this.translate(dirVect);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#translateGlobal(org.mt4j.util.math.Vector3D)
	 */
	public void translateGlobal(Vector3D dirVect) {
		this.translate(dirVect, TransformSpace.GLOBAL);
	}
	
	
	/**
	 * Translates this component in the give direction, relative to its parent component.
	 * 
	 * @param dirVect the dir vect
	 */
	public void translate(Vector3D dirVect) {
//		Matrix[] ms = Matrix.getTranslationMatrixAndInverse(dirVect.getX(), dirVect.getY(), dirVect.getZ());
		Matrix[] ms = _translationComputation; //use existing object to avoid object creation
		Matrix.toTranslationMatrixAndInverse(ms[0], ms[1], dirVect.x, dirVect.y, dirVect.z);
		
//		this.setLocalBasisMatrixInternal(ms[0].mult(this.getLocalBasisMatrix(), this.getLocalBasisMatrix()));
		//Using special multiplication with fewer operations - seems to work ;)
		this.setLocalMatrixInternal(ms[0].translateMult(this.getLocalMatrix(), this.getLocalMatrix()));
		try {
//			this.setLocalInverseMatrixInternal(this.getLocalInverseMatrix().multLocal(ms[1]));
			this.setLocalInverseMatrixInternal(this.getLocalInverseMatrix().translateMultLocal(ms[1])); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * X rotate.
	 * The transformspace parameter indicates in which
	 * coordinate space the point is specified in.
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * @param transformSpace the transform space
	 */
	public void rotateX(Vector3D rotationPoint, float degree, TransformSpace transformSpace) {
		switch (transformSpace) {
		case LOCAL: 
			rotationPoint = MTComponent.getLocalVecToParentRelativeSpace(this, rotationPoint);
			break;
		case RELATIVE_TO_PARENT:
			//default
			break;
		case GLOBAL:
			rotationPoint = MTComponent.getGlobalVecToParentRelativeSpace(this, rotationPoint); 
			break;
		default:
			break;
		}
		this.rotateX(rotationPoint, degree);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent3D#rotateXGlobal(org.mt4j.util.math.Vector3D, float)
	 */
	public void rotateXGlobal(Vector3D rotationPoint, float degree) {
		this.rotateX(rotationPoint, degree, TransformSpace.GLOBAL);
	}
	
	/**
	 * X rotate. 
	 * Rotates relative to the parent coordinate space.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public void rotateX(Vector3D rotationPoint, float degree) {
//		Matrix[] ms = Matrix.getXRotationMatrixAndInverse(rotationPoint, degree);
		Matrix[] ms = _xRotationComputation;
		Matrix.toXRotationMatrixAndInverse(ms[0], ms[1], rotationPoint, degree);
		
		this.setLocalMatrixInternal(ms[0].mult(this.getLocalMatrix(), this.getLocalMatrix()));
		this.inversePrecisionErrors ++;
		this.orthogonalityErrors ++;
		
		if (this.orthogonalityErrors >= reOrthogonalizeThreshold){
//			System.out.println("Matrix re-orthogonalized and inverted at: " + this);
			this.reOrthogonalize(); //This also calculates the inverse in call of setLocalMatrix(..)
			this.orthogonalityErrors = 0;
			this.inversePrecisionErrors = 0;
		}else{
			try {
				if (this.inversePrecisionErrors >= invPrecisionThreshold){
					this.inversePrecisionErrors = 0;
//					System.out.println("Matrix inverted at: " + this);
					this.setLocalInverseMatrixInternal(new Matrix(this.getLocalMatrix()).invertLocal());
				}else{
					this.setLocalInverseMatrixInternal(this.getLocalInverseMatrix().multLocal(ms[1]));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Y rotate.
	 * The transformspace parameter indicates in which
	 * coordinate space the point is specified in.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * @param transformSpace the transform space
	 */
	public void rotateY(Vector3D rotationPoint, float degree, TransformSpace transformSpace) {
		switch (transformSpace) {
		case LOCAL:
			rotationPoint = MTComponent.getLocalVecToParentRelativeSpace(this, rotationPoint);
			break;
		case RELATIVE_TO_PARENT:
			//default
			break;
		case GLOBAL:
			rotationPoint = MTComponent.getGlobalVecToParentRelativeSpace(this, rotationPoint); 
			break;
		default:
			break;
		}
		this.rotateY(rotationPoint, degree);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent3D#rotateYGlobal(org.mt4j.util.math.Vector3D, float)
	 */
	public void rotateYGlobal(Vector3D rotationPoint, float degree) {
		this.rotateY(rotationPoint, degree, TransformSpace.GLOBAL);
	}
	
	/**
	 * Y rotate.
	 * Rotates relative to the parent coordinate space.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public void rotateY(Vector3D rotationPoint, float degree) {
//		Matrix[] ms = Matrix.getYRotationMatrixAndInverse(rotationPoint, degree);
		Matrix[] ms = _yRotationComputation;
		Matrix.toYRotationMatrixAndInverse(ms[0], ms[1], rotationPoint, degree);
		
		this.setLocalMatrixInternal(ms[0].mult(this.getLocalMatrix(), this.getLocalMatrix()));
		this.inversePrecisionErrors ++;
		this.orthogonalityErrors ++;
		
		if (this.orthogonalityErrors >= reOrthogonalizeThreshold){
//			System.out.println("Matrix re-orthogonalized and inverted at: " + this);
			this.reOrthogonalize(); //This also calculates the inverse in call of setLocalMatrix(..)
			this.orthogonalityErrors = 0;
			this.inversePrecisionErrors = 0;
		}else{
			try {
				if (this.inversePrecisionErrors >= invPrecisionThreshold){
					this.inversePrecisionErrors = 0;
//					System.out.println("Matrix inverted at: " + this);
					this.setLocalInverseMatrixInternal(new Matrix(this.getLocalMatrix()).invertLocal());
				}else{
					this.setLocalInverseMatrixInternal(this.getLocalInverseMatrix().multLocal(ms[1]));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Rotates the component around the specified point on the Z axis.
	 * The transformspace parameter indicates in which
	 * coordinate space the point is specified in.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 * @param transformSpace the transform space
	 */
	public void rotateZ(Vector3D rotationPoint, float degree, TransformSpace transformSpace) {
		switch (transformSpace) {
		case LOCAL:
			rotationPoint = MTComponent.getLocalVecToParentRelativeSpace(this, rotationPoint);
			break;
		case RELATIVE_TO_PARENT:
			//default
			break;
		case GLOBAL:
			rotationPoint = MTComponent.getGlobalVecToParentRelativeSpace(this, rotationPoint); 
			break;
		default:
			break;
		}
		this.rotateZ(rotationPoint, degree);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#rotateZGlobal(org.mt4j.util.math.Vector3D, float)
	 */
	public void rotateZGlobal(Vector3D rotationPoint, float degree) {
		this.rotateZ(rotationPoint, degree, TransformSpace.GLOBAL);
	}
	
	
	/**
	 * Rotates the obj around the z-axis around the rotationpoint.
	 * The rotation point is parent-transformation space-relative.
	 * 
	 * @param rotationPoint the rotation point
	 * @param degree the degree
	 */
	public void rotateZ(Vector3D rotationPoint, float degree) {
//		Matrix[] ms = Matrix.getZRotationMatrixAndInverse(rotationPoint, degree);
		Matrix[] ms = _zRotationComputation;
		Matrix.toZRotationMatrixAndInverse(ms[0], ms[1], rotationPoint, degree);
		
		//Using special multiplication with fewer operations - seems to work ;)
		this.setLocalMatrixInternal(ms[0].zRotateMult(this.getLocalMatrix(), this.getLocalMatrix()));
		this.inversePrecisionErrors ++;
		this.orthogonalityErrors ++;
		
		if (this.orthogonalityErrors >= reOrthogonalizeThreshold){
//			System.out.println("Matrix re-orthogonalized and inverted at: " + this);
			this.reOrthogonalize(); //This also calculates the inverse in call of setLocalMatrix(..)
			this.orthogonalityErrors = 0;
			this.inversePrecisionErrors = 0;
		}else{
			try {
				if (this.inversePrecisionErrors >= invPrecisionThreshold){
					this.inversePrecisionErrors = 0;
//					System.out.println("Matrix inverted at: " + this);
					this.setLocalInverseMatrixInternal(new Matrix(this.getLocalMatrix()).invertLocal());
				}else{
					this.setLocalInverseMatrixInternal(this.getLocalInverseMatrix().fastMult43(ms[1],this.getLocalInverseMatrix()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Scales the obj around the scalingPoint. The transformspace parameter indicates in which
	 * coordinate space the point is specified in.
	 * <br><strong>Note:</strong> Non-uniform scaling may lead to bad results!
	 * 
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * @param scalingPoint the scaling point
	 * @param transformSpace the transform space
	 */
	public void scale(float X, float Y, float Z, Vector3D scalingPoint, TransformSpace transformSpace) {
		switch (transformSpace) {
		case LOCAL:
			scalingPoint = MTComponent.getLocalVecToParentRelativeSpace(this, scalingPoint);
			break;
		case RELATIVE_TO_PARENT:
			//default
			break;
		case GLOBAL:
			scalingPoint = MTComponent.getGlobalVecToParentRelativeSpace(this, scalingPoint); 
			break;
		default:
			break;
		}
		this.scale(X, Y, Z, scalingPoint);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#scaleGlobal(float, float, float, org.mt4j.util.math.Vector3D)
	 */
	public void scaleGlobal(float X, float Y, float Z, Vector3D scalingPoint) {
		this.scale(X, Y, Z, scalingPoint, TransformSpace.GLOBAL);
	}
	
	
	//FIXME scale non uniform um parent space punkt realisieren,
	//so gibts bug z.b. beim picking, und bei tastatur
	/**
	 * <b>CURRENTLY DOES NOT REALLY SUPPORT NON-UNIFORM SCALING!</b>
	 * <p>Scales the polygon around the scalingPoint.
	 * 
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * @param scalingPoint the scaling point
	 */
	public void scale(float X, float Y, float Z, Vector3D scalingPoint) {
		/*//TODO !!//TODO mit scaling point umgehen, im moment ignoriert! also immer um 0,0,0/z in der diagonalen
		if (!isScaleUniformXY(X,Y,Z)){
			//TODO mit scaling point umgehen, im moment ignoriert! also immer um 0,0,0/z in der diagonalen
			//SetSize scaled jetzt um object frame origin! :(
			Matrix m = Matrix.getNonUniformScalingTrialMatrix(this.getLocalBasisMatrix(), X, Y, Z); 
//			this.setLocalBasisMatrix(m);
			this.setLocalBasisMatrixInternal(m); 
			
			//TODO Funktionier auch? unterschied zu unten?? dann k�nnte man auch einfach setLocalBasisMatrix(m) aufrufen..
			this.setLocalInverseMatrixInternal(this.getLocalBasisMatrix().invert()); 
			
			//Inverse wird von hand unten berechnet und ist nicht die directe inverse
//			Matrix mInv = Matrix.getInvScalingMatrix(scalingPoint, X, Y, Z);
//			this.setLocalInverseMatrixInternal(this.getLocalInverseMatrix().mult(mInv));
		}else
		*/
		{
//			/*//For uniform scalings or non uniform scalings before any other transform has happened
//			Matrix[] ms = Matrix.getScalingMatrixAndInverse(scalingPoint, X, Y, Z);
			Matrix[] ms = _scalingComputation;
			Matrix.toScalingMatrixAndInverse(ms[0], ms[1], scalingPoint, X, Y, Z);
			
//			this.setLocalBasisMatrixInternal(ms[0].mult(this.getLocalBasisMatrix(), this.getLocalBasisMatrix())); //working original
//			this.setLocalBasisMatrixInternal(this.getLocalBasisMatrix().mult(ms[0])); //FIXME TRIAL! PROBLEM WITH NON-UNIFORM SCALING!!
			this.setLocalMatrixInternal(ms[0].scaleMult(this.getLocalMatrix(), this.getLocalMatrix()));
			try {
//				this.setLocalInverseMatrixInternal(this.getLocalInverseMatrix().multLocal(ms[1])); //working original!
//				this.setLocalInverseMatrixInternal(ms[1].mult(this.getLocalInverseMatrix()));
				this.setLocalInverseMatrixInternal(this.getLocalInverseMatrix().scaleMultLocal(ms[1]));
			} catch (Exception e) {
				e.printStackTrace();
			}
//			*/
		}
		
		//TODO this is a hack to allow non-uniform scaling for 
		//Abstract shapes. This scales the shapes geometry, not their transformation.
		//Thus, children arent scaled.
		//=>Problem with complexpolys for ex. since they have more geometry than in the geometryInfo..
		//=>setSize() wont work then
		//non uniform scaling makes problems when comp has been rotated and isnt axis aligned
		/*
		if (!isScaleUniformXY(X,Y,Z) && this instanceof AbstractShape) {
			AbstractShape shape = (AbstractShape) this;
			
			Vertex[] localVecs = shape.getVerticesLocal();
			Vector3D scalingInv = scalingPoint.getCopy();
			
			//Transform local scalingpoint into object space
//			scalingInv.transform(this.getLocalInverseMatrix());
//			scalingInv.transform(this.getAbsoluteWorldToLocalMatrix());
			
			scalingInv.transformDirectionVector(this.getLocalBasisMatrix());
			
			//Transform object vertices
			Vector3D.transFormArray(Matrix.getScalingMatrix(scalingInv, X, Y, Z), localVecs);
			
			shape.setVerticesLocal(localVecs);
			//TODO bei kindern das gleich probieren?
			//TODO auch bei global machen
		}else
		*/
	}
	
	/*
	private boolean isScaleUniformXY(float x, float y, float z){
		return x==y;
	}
	*/
	

	 
	/**
	 * This method is called just before the components drawComponent method is invoked.
	 * It sets up the components matrix, clipping and other stuff.
	 * @param g the graphics context
	 */
	public void preDraw(PGraphics g) {
		if (this.isDepthBufferDisabled()){
			Tools3D.disableDepthBuffer(g);
		}
		
		g.pushMatrix();

		if (light != null){
			GL10 gl = PlatformUtil.getGL();
			gl.glEnable(GL10.GL_LIGHTING); //this is expensive
			light.enable();
		}

		if (!localMatrix.isIdentity())
			this.applyLocalMatrix();

		if (clip != null){
			clip.enableClip(g);
		}
	}

	
	/**
	 * Executes this component's drawing commands (Not its children!).
	 * The component's matrix has to be made current before drawing.
	 * <br>This method can be overridden in subclasses
	 * and filled with drawing commands.
	 * <br>NOTE: This method is called by the application. Usually you should
	 * not invoke this method directly!
	 * @param g the graphics context
	 */
	public void drawComponent(PGraphics g){ 	}
	
	
	/**
	 * Post draw.
	 * Called immediatly after drawing this component.
	 * 
	 * @param g the g
	 */
	public void postDraw(PGraphics g) {
		if (clip != null){
			clip.disableClip(g);
		}
		
		if (childClip != null){
			childClip.enableClip(g);
		}
	}
	
	
	/**
	 * Post draw Children.
	 * Called after drawing this component and its children.
	 * @param g the graphics context
	 */
	public void postDrawChildren(PGraphics g) {
		if (this.isDepthBufferDisabled()){
			Tools3D.restoreDepthBuffer(g);
		}
		
		if (childClip != null){
			childClip.disableClip(g);
		}
			
		g.popMatrix();

		//FIXME TRIAL
		if (light != null){
			light.disable();
			GL10 gl = PlatformUtil.getGL();
			gl.glDisable(GL10.GL_LIGHTING);
		}
	}
	
	// CLIP ////////////////
	/**
	 * Gets the clip.
	 * @return the clip
	 */
	public Clip getClip() {
		return clip;
	}
	
	/**
	 * Sets the clip mask for this component. This restricts the drawing
	 * of this component to the specified clip area.
	 * <br>NOTE: Only supported when using OpenGL as the renderer!
	 * 
	 * @param clip the new clip
	 */
	public void setClip(Clip clip) {
		if (MT4jSettings.getInstance().isOpenGlMode()){
			this.clip = clip;	
		}
	}
	// CLIP ////////////////

	
	// CHILDREN CLIP /////////////////////
	/**
	 * Gets the child clip.
	 * @return the child clip
	 */
	public Clip getChildClip() {
		return childClip;
	}

	/**
	 * Sets the clip mask for this components children.
	 * Only children contained in the specified clipping shape will be visible.
	 * <br>NOTE: Only supported when using OpenGL as the renderer!
	 * 
	 * @param childClip the child clip mask
	 */
	public void setChildClip(Clip childClip) {
		if (MT4jSettings.getInstance().isOpenGlMode()){
			this.childClip = childClip;	
		}
	}
	// CHILD CLIP MASK /////////////////////
	
	
	//FIXME TRIAL OPENGL LIGHTS ////////////
	/**
	 * Sets the light.
	 * <br>NOTE: Only supported when using OpenGL as the renderer!
	 * 
	 * @param light the new light
	 */
	public void setLight(MTLight light){
		this.light = light;
	}

	/**
	 * Gets the light.
	 * 
	 * @return the light
	 */
	public MTLight getLight() {
		return light;
	}
	//TRIAL OPENGL LIGHTS ////////////
	
	
	//FIXME REMOVE THIS? THIS IS OBSOLETE BECAUSE OF UPDATECOMPONENT AND drawAndUpdateRectursive!
//	/**
//	 * Calls the updateComponent() method on this component
//	 * and the update() method on its children.
//	 * This is handled automatically by the MTCanvas! Dont invoke this!
//	 * 
//	 * @param timeDelta the time delta since the last frame
//	 */
//	public void update(long timeDelta){
//		this.updateComponent(timeDelta);
//		
//		for (MTComponent child : childComponents)
//			child.update(timeDelta);
//	}
	
	/**
	 * Tells the component to update its state if neccessary. This is called
	 * shortly before the component's <code>drawComponent()</code> method is invoked at every frame.
	 *  The <code>timeDelta</code>
	 * parameter indicates the time passed since the last frame was drawn and can be used
	 * for animations for example.<br>
	 * Also, this updates the associated <code>IMTController</code> object if existing.
	 * If overriden, the superclass implementation should always be called!
	 * <br>NOTE: Be aware that this method is called every frame, so doing expensive calculations in it may slow down the
	 * application.
	 * 
	 * @param timeDelta the time delta
	 */
	public void updateComponent(long timeDelta) {
		if (controller != null){
			controller.update(timeDelta);
		}
	}

	
	/**
	 * Adds a component as a child to this component.
	 * By doing this, it will be under the influence of the parents
	 * transformations.
	 * <br>NOTE: adding children during traversal of the component hierarchy 
	 * will result in a concurrent modification error (e.g. in methods like
	 * drawComponent or updateComponent).
	 * To resolve this, we can use the AddNodeActionThreadSafe class to add
	 * as an IPreDrawAction to our current scene 
	 * (looking something like this: yourScene.addPreDrawAction(new AddNodeActionThreadSafe(..));)
	 * The component will then be added before the next rendering loop.
	 * 
	 * @param tangibleComp the tangible comp
	 */
	public void addChild(MTComponent tangibleComp){
		this.addChild(this.childComponents.size(), tangibleComp);
	}
	
	/**
	 * Adds the child at the specified position in the list of children.
	 * 
	 * @param i the i
	 * @param tangibleComp the tangible comp
	 * 
	 * @see MTComponent#addChild
	 */
	public void addChild(int i, MTComponent tangibleComp){
		MTComponent oldParent = tangibleComp.getParent();
		boolean sameParent = false;
		if (oldParent != null){
			oldParent.removeChild(tangibleComp);
			if (oldParent.equals(this)){
				i--;//If we removed the comp from this (same parent) we have to decrease the index
				sameParent = true;
			}
//			i = (i<0)? 0 : i; //ensure i > 0
			i = Math.max(0, Math.min(childComponents.size(), i));
		}
		tangibleComp.setParent(this);
		childComponents.add(i, tangibleComp);
		
		if (!sameParent){ //TEST - only mark dirty if comp was added to different parent
			//To inform its children, that they have to update their 
			//global matrices, because this new parent could
			//change it with its own 
			tangibleComp.setMatricesDirty(true);
			//search up the tree and update the camera responsible for drawing the component
			tangibleComp.searchViewingCamera();
		}
		//Fire state change event
		this.fireStateChange(StateChange.CHILD_ADDED);
		tangibleComp.fireStateChange(StateChange.ADDED_TO_PARENT);
	}
	
	
	
	/**
	 * Adds an array of components to this component as children.
	 * 
	 * @param tangibleComps the tangible comps
	 */
	public void addChildren(MTComponent[] tangibleComps){
        for (MTComponent object : tangibleComps) {
            this.addChild(object);
        }
	}
	
	
	/**
	 * Gets the child list which is also used internally in <code>MTComponent</code>.
	 * Therefor, this should be used for read operations only! 
	 * <p>This method is provided for performance reasons, because <code>getChildren()</code> 
	 * contains overhead because it creates a new array for each call.
	 * 
	 * @return the child list
	 */
	protected List<MTComponent> getChildList(){
		return childComponents;
	}
	
	/**
	 * Gets the children.
	 * @return the children
	 */
	public MTComponent[] getChildren(){
		return childComponents.toArray(new MTComponent[childComponents.size()]);
	}
	
	/**
	 * Gets the child by its unique ID. 
	 * <br>NOTE: the specified number is the unique component ID, not the index in the children array!
	 * 
	 * @param ID the iD
	 * 
	 * @return the child
	 */
	public MTComponent getChildbyID(int ID){
		MTComponent returnObject = null;
        for (MTComponent object : childComponents) {
            if (object.getID() == ID)
                returnObject = object;
        }
		return returnObject;
	}
	
	/**
	 * Gets the child by index.
	 * 
	 * @param index the index
	 * 
	 * @return the child by index
	 */
	public MTComponent getChildByIndex(int index){
		return childComponents.get(index);
	}
	
	/**
	 * Gets the child by name.
	 * 
	 * @param name the name
	 * 
	 * @return the child by name
	 */
	public MTComponent getChildByName(String name){
		MTComponent returnObject = null;
        for (MTComponent object : childComponents) {
            if (object.getName().equals(name))
                returnObject = object;
        }
		return returnObject;
	}
	
	
	/**
	 * Goes through all children and their children
	 * to check if this component tree contains the given component.
	 * 
	 * @param tangibleComp the tangible comp
	 * 
	 * @return true, if contains child
	 */
	public boolean containsChild(MTComponent tangibleComp){
		if (tangibleComp==null)
			return false;

        for (MTComponent currentChildComponent : childComponents) {
            if (currentChildComponent.equals(tangibleComp))
                return true;
            else if (currentChildComponent.containsChild(tangibleComp))
                return true;
        }
		return false;
	}
	
	/**
	 * Checks if the given component is a direct child of this component.
	 * 
	 * @param tangibleComp the tangible comp
	 * 
	 * @return true, if contains direct child
	 */
	public boolean containsDirectChild(MTComponent tangibleComp){
		return (childComponents.contains(tangibleComp));
	}
	
/*
//	///////Mal ausproibern und evtl removen
	public boolean containsRecursive(TComponent tangibleComp){
		return (containsRecursiveRec(this.getChildren(), tangibleComp));
	}
	
	private boolean containsRecursiveRec(TComponent[] tangibleComps, TComponent tangibleComp){
		boolean returnBool = false;
		for (int i = 0; i < tangibleComps.length; i++) {
			TComponent currentComponent = tangibleComps[i];
			System.out.println(currentComponent.getName());
			if (currentComponent.equals(tangibleComp)){
				//return true;
				return true;
			}else{
				TComponent[] childComps = currentComponent.getChildren();
				//return containsRecursiveRec(childComps, tangibleComp);
				returnBool = containsRecursiveRec(childComps, tangibleComp);
			}
		}
		return returnBool;
	}
//	///////Mal ausproibern und evtl removen
 * 
 */

	/**
	 * Gets the ancestor.
	 * 
	 * @return the ancestor
	 * 
	 * the ancestor - the upper most parent in the hierarchy of this component
	 */
	public MTComponent getRoot(){
		MTComponent root = this.getParent();
		if (root == null)
			return root;
		
		while (root.getParent() != null){
			root = root.getParent();
		}
		return root;
//		return getRootRecursive(this);
	}
	
//	/* careful not to have cycles in the hierarchy! */
//	/**
//	 * Gets the ancestor recursive.
//	 * 
//	 * @param current the current
//	 * 
//	 * @return the ancestor recursive
//	 */
//	private MTComponent getRootRecursive(MTComponent current){
//		if (current.getParent() == null){
//			return current;
//		}else{
//			return getRootRecursive(current.getParent());
//		}
//	}
	
	/**
	 * Gets the child count.
	 * 
	 * @return the child count
	 * 
	 * the number of childs this component has
	 */
	public int getChildCount(){
		return childComponents.size();
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 * 
	 * the parent of this child or null if it has none.
	 */
	public MTComponent getParent(){
		return this.parent;
	}
	
	/**
	 * Used internally when adding a component to another.
	 * 
	 * @param parent the parent
	 */
	private void setParent(MTComponent parent){
		//remove this from old parent //THIS IS DONE AT ADDCHILD()!
//		if (this.getParent() != null){
//			this.getParent().removeChild(this);
//		}
		this.parent = parent;
	}
	
	
	
	/**
	 * Tries to remove the specified child from this component.
	 * 
	 * @param i the i
	 */
	public void removeChild(int i){
		try {
			MTComponent comp = childComponents.get(i);
			this.removeChild(comp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Removes this component from its parent.
	 */
	public void removeFromParent(){
		if (this.getParent() != null){
			this.getParent().removeChild(this);
		}
	}
	
	
	/**
	 * Tries to remove the specified child from this component.
	 * 
	 * @param comp the comp
	 */
	public void removeChild(MTComponent comp){
		try {
			comp.setParent(null);
			childComponents.remove(comp);
			
			//search up the tree and update the camera responsible for drawing the component
			//will probably be null here
			comp.searchViewingCamera();
			
			comp.fireStateChange(StateChange.REMOVED_FROM_PARENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes all direct children of this component.
	 */
	public void removeAllChildren(){
		for (int i = childComponents.size()-1; i >= 0; i--) {
			MTComponent child = childComponents.get(i);
			child.removeFromParent();
		}
		childComponents.clear();
	}
	
	
	/**
	 * Gets the child index of a child.
	 * @param comp the comp
	 * 
	 * @return the child index of
	 */
	public int getChildIndexOf(MTComponent comp){
		return childComponents.indexOf(comp);
	}
	
	
	/**
	 * <br>If the depth buffer is disabled, the order in which the components are drawn alone decides which objects will appear
	 * ontop of others, instead of their distance to the camera. This is useful to avoid "z-fighting" when drawing
	 * co-planar objects (ie. 2D windows with ui objects on them).
	 * If set to true, this component <b>and all its children</b> will always be drawn above all previously drawn objects,
	 * even if the other objects are "in front" of this component. 
	 * 
	 * @param drawOnTop the draw on top option
	 */
	public void setDepthBufferDisabled(boolean drawOnTop){
		this.sendToFront();
		this.drawnOnTop = drawOnTop;
	}

	/**
	 * Checks if is always drawn on top.
	 * 
	 * @return true, if is always drawn on top
	 */
	public boolean isDepthBufferDisabled() {
		return drawnOnTop;
	}
	
	/**
	 * Puts this components to the end of the children list of
	 * its parent.
	 * This will result in this component being drawn last and on top
	 * of others at the same z-position.
	 */
	public void sendToFront(){
		if (this.getParent()!= null){
			this.getParent().sendChildToFront(this);
		}
	}
	
	/**
	 * Puts this child to the end of the children list of this component.
	 * This will result in this child being drawn last and on top
	 * of others at the same Z-position.
	 * @param child the child
	 */
	protected void sendChildToFront(MTComponent child){
		if (this.containsDirectChild(child)
			&& !getChildByIndex(getChildCount()-1).equals(child)
		){
			//System.out.println("Drawlast: " + tangibleComp.getName());
			childComponents.add(getChildCount(),child);
			childComponents.remove(child);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#setVisible(boolean)
	 */
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#getID()
	 */
	public int getID(){
		return this.ID;
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#setName(java.lang.String)
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#getName()
	 */
	public String getName(){
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#getRenderer()
	 */
	public PApplet getRenderer(){
		return this.renderer;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#isVisible()
	 */
	public boolean isVisible() {
		return visible;
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent3D#isPickable()
	 */
	public boolean isPickable() {
		return pickable;
	}


	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent3D#setPickable(boolean)
	 */
	public void setPickable(boolean pickable) {
		this.pickable = pickable;
	}


	/*
	public Point getCenterPoint() { //TODO get centerPoint ffrom all childs and integrate?
		float x=0,y=0,z=0;
		x+=this.getCenterPoint().getX();
		y+=this.getCenterPoint().getY();
		z+=this.getCenterPoint().getZ();
		
		TComponent[] childs = this.getChildren();
		for (int i = 0; i < childs.length; i++) {
			TComponent child = childs[i];
			
			x+=child.getCenterPoint().getX();
			y+=child.getCenterPoint().getY();
			z+=child.getCenterPoint().getZ();
		}
		
		//GO TRHOUGH ALL CHILDS
		return null;
	}
	
	protected Point getComponentCenterPoint() { //get centerPoint ffrom all childs and integrate?
		// Auto-generated method stub
		return null;
	}
	*/
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#containsPointGlobal(org.mt4j.util.math.Vector3D)
	 */
	public boolean containsPointGlobal(Vector3D testPoint) {
//		if (this.componentContainsPointLocal(testPoint))
//			return true;
//		for (int i = childComponents.size()-1; i >= 0; i--) {
//			MTComponent component = childComponents.get(i); 
//			if (component.containsPointLocal(testPoint)) 
//				return true;
//		}
//		return false;
		if (this.componentContainsPointLocal(this.globalToLocal(testPoint)))
			return true;
		for (int i = childComponents.size()-1; i >= 0; i--) {
			if (childComponents.get(i).containsPointGlobal(testPoint)) 
				return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the specified point is contained in this component.
	 * This method gets called from the componentContainsPointGlobal method.
	 * So in a extending class we would override the componentContainsPointLocal
	 * with our intersection code only!
	 * 
	 * @param testPoint the test point
	 * 
	 * @return true, if successful
	 */
	protected boolean componentContainsPointLocal(Vector3D testPoint) { //TODO rename containPointLocal
//		return this.containsPointBoundsLocal(testPoint);
		if (this.hasBounds()){
//			System.out.println("\"" + this.getName() + "\": -> BOUNDS only check");
			return this.getBounds().containsPointLocal(testPoint);
		}else{
			return false;
		}
	}
	
//	/**
//	 * Contains point bounds local.
//	 *
//	 * @param testPoint the test point in local coordiantes
//	 * @return true, if successful
//	 */
//	protected boolean containsPointBoundsLocal(Vector3D testPoint){
//		if (this.isBoundingShapeSet()){
////			System.out.println("\"" + this.getName() + "\": -> BOUNDS only check");
//			return this.getBoundingShape().containsPointLocal(testPoint);
//		}else{
//			return false;
//		}
//	}
	
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent3D#getIntersectionGlobal(org.mt4j.util.math.Ray)
	 */
	public Vector3D getIntersectionGlobal(Ray ray) {
		float currentDistance = Float.MAX_VALUE; //high value so that the first time a object is found this distance is exchanged with his
		float objDistance 		= 0;
		Vector3D returnPoint 	= null;
		Vector3D interSP 		= null;
		
		if (this.isVisible() && this.isPickable()) { 
			//Get the real ray for this obj, takes the custom camera and viewport of this obj into account
			//-> changes rayStartPoint and point in ray direction
			if (this.getAttachedCamera() != null){
				ray	= getChangedCameraPickRay(this.getRenderer(), this, ray);
			}
			
			//Transforms the ray into local object space 
			Ray invertedRay = this.globalToLocal(ray);
			
			//Check if component is clipped and only proceed if the ray intersects the clip shape
			Clip clip = this.getClip();
			if (clip == null || (clip != null && clip.getClipShapeIntersectionLocal(invertedRay) != null)){
				interSP = this.getIntersectionLocal(invertedRay);
				if (interSP != null){
					//FIXME TRIAL - muss f�r die distance messung der world ray genommen
					//werden oder geht der invertierte ray?
					interSP.transform(this.getGlobalMatrix());
					//Get distance from raystart to the intersecting point
					objDistance = interSP.getSubtracted(ray.getRayStartPoint()).length();

					//If the distance is the smalles yet = closest to the raystart replace the returnObject and current distanceFrom
					if ((objDistance - PickResult.HIT_TOLERANCE) < currentDistance ){
						returnPoint = interSP;
						currentDistance = objDistance;
					}
				}
			}

			//Check for child clip intersection, if not intersecting, dont try to pick children
			Clip childClip = this.getChildClip();
			if (childClip != null && childClip.getClipShapeIntersectionLocal(invertedRay) == null){
				return returnPoint;
			}
			
		}
		
		/* Go through all Children */
//		for (int i = childComponents.size()-1; i >= 0; i--) {
        for (MTComponent child : childComponents) {
            //Get the intersectionpoint ray/object if there is one
            interSP = child.getIntersectionGlobal(ray);

            if (interSP != null) { //if ray intersects object at a point
                //System.out.println("Intersection at: " + interSP);
                //Get distance from raystart to the intersecting point
                objDistance = interSP.getSubtracted(ray.getRayStartPoint()).length();
                //If the distance is the smalles yet = closest to the raystart replace the returnObject and current distanceFrom
                if (objDistance < currentDistance) {
                    returnPoint = interSP;
                    currentDistance = objDistance;
                }
            }//if intersection!=null
        }
		return returnPoint;
	}
	
	
	
	
	/**
	 * Returns the intersection point of the ray and this component (children are not checked for
	 * intersections). Usually, if the component has a bounding shape assigned, the bounds are checked
	 * for an intersection. Shapes may also check the shape itself for intersection.
	 * <br>The ray is assumed to already be in local component space (not in global space).
	 * <br>If the component is not intersected, null is returned.
	 * 
	 * @param localRay the rays, in local space
	 * @return the component local intersection point
	 * @see #globalToLocal
	 */
	public Vector3D getIntersectionLocal(Ray localRay) {
//		return this.getBoundsIntersectionLocal(localRay);
		if (this.hasBounds()){
			return this.getBounds().getIntersectionLocal(localRay);
		}else{
			return null;
		}
	}
	
//	/**
//	 * Gets the bounds intersection local. Test if the ray in local coordinates
//	 * intersection this component's bounding shape.
//	 * Return the local intersection point or null if there is no intersection or no bounding shape
//	 * is set.
//	 *
//	 * @param localRay the local ray
//	 * @return the local intersection point
//	 */
//	protected Vector3D getBoundsIntersectionLocal(Ray localRay){//FIXME TEST
//		if (this.isBoundingShapeSet()){
//			return this.getBoundingShape().getIntersectionLocal(localRay);
//		}else{
//			return null;
//		}
//	}


//	/** 
//	 * Sometimes the wrong obj gets picked if they are on the same plane but with different inverted rays..
//	 * probably math rounding off errors with floats etc. (at inverting the ray?) 
//	 * <br>This makes sure, objs which are checked later for a hit, 
//	 * (and are probably drawn ontop of the previous ones because drawn later), 
//	 * are picked more likely.
//	 * <br>Still this is kind of a hack
//	 */
//	private static final float HIT_TOLERANCE = 0.3f; //0.03f; //FIXME reset to old value!?
	
//	/**
//	 * This method allows to pick (Select) an object in the scene.
//	 * 
//	 * 
//	 * @param pickInfo the pick info
//	 * 
//	 * @return the pick result
//	 */
//	public PickResult pick(PickInfo pickInfo){ 
//		PickResult pickResult = new PickResult();
//		this.pickRecursive(pickInfo, pickResult, Float.MAX_VALUE, pickInfo.getPickRay());
//		return pickResult;
//	}
	
	/**
	 * Checks which object lies under the specified screen coordinates.
	 * The the results are stored in the returned PickResult object. This component and
	 * its children will be checked. 
	 * 
	 * @param x the x
	 * @param y the y
	 * 
	 * @return the pick result
	 */
	public PickResult pick(float x, float y){ 
//		System.out.println("MTComponent pick at: " + x + "," + y + " - this:" + this); 
		PickResult pickResult = new PickResult();
		PickInfo pickInfo = new PickInfo(x,y, Tools3D.getCameraPickRay(this.getRenderer(), this, x, y));
		this.pickRecursive(pickInfo, pickResult, Float.MAX_VALUE, pickInfo.getPickRay(), true);
//		pickResult.printList();
		return pickResult;
	}
	
	
	/**
	 * Gets the intersection of this component with the input cursor.
	 *
	 * @param cursor the cursor
	 * @return the intersection global or null if no intersection
	 */
	public Vector3D getIntersectionGlobal(InputCursor cursor){
		return this.getIntersectionGlobal(Tools3D.getCameraPickRay(getRenderer(), this, cursor));
	}
	
	
	/**
	 * Checks which object lies under the specified screen coordinates.
	 * The the results are stored in the returned PickResult object. This component and
	 * its children will be checked. 
	 * 
	 * @param x the x
	 * @param y the y
	 * @param onlyPickables check the only pickable components
	 * 
	 * @return the pick result
	 */
	public PickResult pick(float x, float y, boolean onlyPickables){ 
		PickResult pickResult = new PickResult();
		PickInfo pickInfo = new PickInfo(x,y, Tools3D.getCameraPickRay(this.getRenderer(), this, x, y));
		this.pickRecursive(pickInfo, pickResult, Float.MAX_VALUE, pickInfo.getPickRay(), onlyPickables);
//		pickResult.printList();
		return pickResult;
	}
	
	
	//FIXME currObjDist now in pickresult vistor, so parameter is obsolete
	//could make currObjDistance  return parameter ..
	/**
	 * Pick closest comp with ray recursive.
	 * 
	 * @param pickInfo the pick info
	 * @param pickResult the pick result
	 * @param currObjDist the curr obj dist
	 * @param currentRay the current ray
	 * @param onlyPickables the only pickables
	 * @return the float
	 */
	private float pickRecursive(PickInfo pickInfo, PickResult pickResult, float currObjDist, Ray currentRay, boolean onlyPickables){
		Vector3D interSP	= null;
		float objDistance 	= 0;
		//TEST, Wenns probleme gibt das wieder aktivieren
//		currObjDist = pickResult.getDistanceNearestPickObj();
		
//		System.out.println("At: " + this.getName() + " Current Distance: " + currObjDist);
		
		if (visible && 
			( (onlyPickables && pickable) || !onlyPickables) 
		){
			//Get the real ray for this obj, takes the viewing camera and viewport of this obj into account
			//-> changes rayStartPoint and point in ray direction
			if (this.getAttachedCamera() != null){
				currentRay	= getChangedCameraPickRay(this.getRenderer(), this, pickInfo);
			}
			
			Ray invertedRay = this.getGlobalInverseMatrix().isIdentity()? currentRay : this.globalToLocal(currentRay);
			
			
			/*
			//DEBUG HELP!!!!! 
			//This adds lines indicating the world ray and the local object ray used for ray-test
//			System.out.println("Ray start: " +  pickInfo.getPickRay().getRayStartPoint() + " ray end: " + pickInfo.getPickRay().getPointInRayDirection());
			final MTLine l1 = new MTLine(this.getRenderer(), new Vertex(currentRay.getRayStartPoint()), new Vertex(currentRay.getPointInRayDirection()));
			final MTLine l2 = new MTLine(this.getRenderer(), new Vertex(invertedRay.getRayStartPoint()), new Vertex(invertedRay.getPointInRayDirection()));
			l2.setStrokeColor(new MTColor(255, 10, 10, 255));
			((MTApplication)this.getRenderer()).invokeLater(new Runnable() {
				@Override
				public void run() {
					getRoot().addChild(l1);
					getRoot().addChild(l2);
				}
			});
			*/
			
			//Check if component is clipped and only proceed if the ray intersects the clip shape
			if (clip == null || (clip != null && clip.getClipShapeIntersectionLocal(invertedRay) != null)){
				interSP = this.getIntersectionLocal(invertedRay);
				if (interSP != null){
					//i guess we have to use the ray in global coords to measure the distance
					interSP.transform(this.getGlobalMatrix());
					// Get distance from raystart to the intersecting point
					objDistance = interSP.getSubtracted(currentRay.getRayStartPoint()).length();
//					System.out.println("Pick found: " + this.getName() + " InterSP: " + interSP +  " ObjDist: " + objDistance +  " Mouse Pos: " + pickInfo.getScreenXCoordinate() + "," + pickInfo.getScreenYCoordinate() + " InvRay RS:" + invertedRay.getRayStartPoint() + ",RE: " + invertedRay.getPointInRayDirection());

//					//If the distance is the smallest yet = closest to the raystart: replace the returnObject and current distanceFrom
//					if ( (objDistance - HIT_TOLERANCE) <= currObjDist /*|| this.isAlwaysDrawnOnTop()*/){//take isDrawnOnTop into account here?? -> OBJDistance auf 0 setzen?
//					currObjDist = objDistance;
//					pickResult.addPickedObject(this, interSP, objDistance);
////					System.out.println("-> Now nearest: " + this.getName());
//					}

					//TEST - ADD ALL PICKED OBJECTS - SORT LATER
					pickResult.addPickedObject(this, interSP, objDistance);
				}
			}
			
			//Check for child clipping shape intersection, if not intersecting -> dont try to pick children
			if (childClip != null && childClip.getClipShapeIntersectionLocal(invertedRay) == null){
				return currObjDist;
			}
		}else if (visible && childClip != null){
			//Check for child clipping shape intersection, if not intersecting -> dont try to pick children
			Ray invertedRay = this.getGlobalInverseMatrix().isIdentity()? currentRay : this.globalToLocal(currentRay);
			if (childClip.getClipShapeIntersectionLocal(invertedRay) == null){
				return currObjDist;
			}
		}
		
		/* recursively check all children now */
        for (MTComponent child : childComponents) {
            if (child.isVisible()) {
                if (composite) {
                    //Start a new picking with a new Pickresult obj from here
                    PickResult compositePickRes = new PickResult();
//                    float compDistance = 
                    	child.pickRecursive(pickInfo, compositePickRes, Float.MAX_VALUE, currentRay, onlyPickables);

                    //Add the composites picks to the overall picks
                    if (compositePickRes.getNearestPickResult() != null) {
//						System.out.println("In: " + this.getName() + " Composites child picked, pick resultDistance: " + compDistance);
                        /*//TODO m�sste diese hier nach distanz geordnet in insgesamt pickresult einf�gen..
                              ArrayList<MTBaseComponent> pickList = compositePickRes.getPickList();
                              for(MTBaseComponent comp : pickList){
                                  pickResult.addPickedObject(comp, compositePickRes.getInterSectionPointOfPickedObj(comp), compositePickRes.getDistanceOfPickedObj(comp));
                              }
                              */
                        //Add this composite as the last one picked with the distance of the last one picked in the composite pick
//						pickResult.addPickedObjects(compositePickRes.getPickList());
//						pickResult.addPickedObject(this, compositePickRes.getInterSectionPointNearestPickedObj(), compositePickRes.getDistanceNearestPickObj());

//						if (//compDistance <= currObjDist 
//							(compDistance - HIT_TOLERANCE) <= currObjDist
//						){
////							System.out.println("Composites child picked and now nearest: " + this.getName()+ " dist: " + compDistance);
//							pickResult.addPickedObject(this, compositePickRes.getInterSectionPointNearestPickedObj(), compositePickRes.getDistanceNearestPickObj());
//							currObjDist = compDistance;
//						}

                        //FIXME TEST - ADD ALL PICKED OBJECTS - SORT LATER
                        PickEntry nearestPickEntry = compositePickRes.getNearestPickEntry();
                        pickResult.addPickedObject(this, nearestPickEntry.intersectionPoint, nearestPickEntry.cameraDistance);
                    }
                } else {
                    currObjDist = child.pickRecursive(pickInfo, pickResult, currObjDist, currentRay, onlyPickables);
                }
            }
        }
		return currObjDist;
	}
	
	
	/**
	 * Calculates the "real" picking ray for the object.
	 * <br>If the obj has a custom camera attached to it, this camera's position is the new ray origin and
	 * the point in the ray direction is the unprojected x,y, coordinates while this camera is active.
	 * 
	 * @param pa the papplet
	 * @param obj the obj
	 * @param ray the ray
	 * 
	 * @return the real pick ray
	 * 
	 * the new calculated ray, or the original ray, if the obj has no custom camera attached to it.
	 */
	private static Ray getChangedCameraPickRay(PApplet pa, IMTComponent3D obj, Ray ray){
		Vector3D pointInRayDirection = ray.getPointInRayDirection();
		Vector3D projected = Tools3D.project(pa, obj.getViewingCamera(), pointInRayDirection);
//		Vector3D projected = Tools3D.project(pa, pointInRayDirection);
		return getChangedCameraPickRay(pa, obj, new PickInfo(
				projected.x,
				projected.y,
				ray));
	}
	
	
	/**
	 * Calculates the "real" pickray for the object.
	 * <br>If the obj has a custom camera attached to it, this cameras position is the new ray origin and
	 * the point in the ray direction is the unprojected x,y, coordinates while this camera is active.
	 * 
	 * @param pa the pa
	 * @param obj the obj
	 * @param pickInfo the pick info
	 * @return the real pick ray
	 * 
	 * the new calculated ray, or the original ray, if the obj has no custom camera attached to it.
	 */
	private static Ray getChangedCameraPickRay(PApplet pa, IMTComponent3D obj, PickInfo pickInfo){
		if (obj.getViewingCamera() != null){ //FIXME TEST
			//Re-Project unprojected world coords to projected viewport screen coords (Tuio INput)
			float x = pickInfo.getScreenXCoordinate(); 
			float y = pickInfo.getScreenYCoordinate(); 
			
//			System.out.println("MTComponent getChangedCameraPickRay at: " + x + "," + y + " obj: " + obj); 
			
			return Tools3D.getCameraPickRay(pa, obj, x, y);
		}else{
			return pickInfo.getPickRay();
		}
		/*//FIXME disabled for performance for now!
		if (obj.hasCustomViewPort()){
			//Take VIEWPORT changes into account, too
			ViewportSetting customViewPort 		   = obj.getCustomViewportSetting();
			ViewportSetting defaultViewPortSetting = obj.getDefaultViewportSetting();
			rayStartPoint.setX(customViewPort.getStartX() + (rayStartPoint.getX() * (customViewPort.getWidth()/defaultViewPortSetting.getWidth())));
			rayStartPoint.setY(customViewPort.getStartY() + (rayStartPoint.getY() * (customViewPort.getHeight()/defaultViewPortSetting.getHeight())));
			pointInRayDirection.setX(customViewPort.getStartX() + (pointInRayDirection.getX() * (customViewPort.getWidth()/defaultViewPortSetting.getWidth())));
			pointInRayDirection.setY(customViewPort.getStartY() + (pointInRayDirection.getY() * (customViewPort.getHeight()/defaultViewPortSetting.getHeight())));
			/////
		}
		 */
//		return new Ray(rayStartPoint, pointInRayDirection);
	}
	
	
//	/* (non-Javadoc)
//	 * @see org.mt4j.components.interfaces.IMTComponent3D#getDefaultViewportSetting()
//	 */
//	public ViewportSetting getDefaultViewportSetting(){
//		return this.defaultViewPortSetting;
//	}
//	
//	//TODO make function 2Dshift3DObj? das dann viewport �ndert?
//	/* (non-Javadoc)
//	 * @see org.mt4j.components.interfaces.IMTComponent3D#getCustomViewportSetting()
//	 */
//	public ViewportSetting getCustomViewportSetting() {
//		return customViewPort;
//	}
//
//	//FIXME funktioniert das so, dass man in subclass das erzeugen und setzen kann?
//	/**
//	 * Sets the view port settings.
//	 * 
//	 * @param viewPortSettings the new view port settings
//	 */
//	public void setViewPortSettings(ViewportSetting viewPortSettings) {
//		this.customViewPort = viewPortSettings;
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.mt4j.components.interfaces.IMTComponent3D#hasCustomViewPort()
//	 */
//	public boolean hasCustomViewPort(){
//		return this.customViewPort != null;
//	}


	
	
	
	
	// INPUT LISTENER STUF ////
	/**
	 * Adds an input listener to this component. The listener will be informed if
	 * this component recieves an input event.
	 * 
	 * @param inputListener the input listener
	 */
	public synchronized void addInputListener(IMTInputEventListener inputListener){
		if (inputListener instanceof AbstractComponentProcessor) {
			logger.warn("An abstract component processor (" + inputListener + ") was added to component '" + this + "' using addInputListener(). You probably need to use the registerInputProcessor() method instead!");
		}
		this.inputListeners.add(inputListener);
	}
	
	/**
	 * Removes the input listener.
	 * @param inputListener the input listener
	 */
	public synchronized void removeInputListener(IMTInputEventListener inputListener){
		this.inputListeners.remove(inputListener);
	}
	
	/**
	 * Gets the input listeners.
	 * @return the input listeners
	 */
	public IMTInputEventListener[] getInputListeners(){
		return this.inputListeners.toArray(new IMTInputEventListener[this.inputListeners.size()]);
	}
	
	/**
	 * Fire input event.
	 * 
	 * @param iEvt the i evt
	 */
	protected boolean dispatchInputEvent(MTInputEvent iEvt){
		boolean handled = false; //TODO REALLY IMPLEMENT, CHECK LISTENERS WHAT THEY RETURN, PROPAGET ETC!
		for (IMTInputEventListener listener : inputListeners){
			boolean handledListener = listener.processInputEvent(iEvt);
			if (!handled && handledListener){
				handled = true;
			}
		}
		return handled;
	}
	// INPUT LISTENER STUF ////
	
	
	// INPUT HANDLER ////////////////////////////////////////
	/**
	 * Registers an component input processor with this component. Input processors are used to process
	 * the input events a component recieves by checking them for special patterns and conditions and
	 * firing gesture events back to the component.
	 * To recognize a multi-touch drag gesture on a component for example, we would register a
	 * <code>DragProcessor</code> object with this component.
	 * 
	 * @param inputProcessor the input processor
	 */
	public void registerInputProcessor(AbstractComponentProcessor inputProcessor) {
		AbstractComponentProcessor[] processors = inputProcessorsSupport.getInputProcessors();
        for (AbstractComponentProcessor abstractComponentProcessor : processors) {
            if (inputProcessor.getClass() == abstractComponentProcessor.getClass()) {
                logger.warn("Warning: The same type of input processor (" + inputProcessor.getName() + ") is already registered at component: " + this);
            }
        }
		inputProcessorsSupport.registerInputProcessor(inputProcessor);
		this.setGestureAllowance(inputProcessor.getClass(), true); //Enable by default
	}
	
	/**
	 * Unregister a component input processor.
	 * @param inputProcessor the input processor
	 */
	public void unregisterInputProcessor(AbstractComponentProcessor inputProcessor) {
		inputProcessorsSupport.unregisterInputProcessor(inputProcessor);
	}
	
	/**
	 * Unregister all previously registered component input processors.
	 */
	public void unregisterAllInputProcessors() {
		AbstractComponentProcessor[] ps = inputProcessorsSupport.getInputProcessors();
        for (AbstractComponentProcessor p : ps) {
            inputProcessorsSupport.unregisterInputProcessor(p);
        }
	}
	
	/**
	 * Gets the component input processors.
	 * @return the input processors
	 */
	public AbstractComponentProcessor[] getInputProcessors() {
		return inputProcessorsSupport.getInputProcessors();
	}
	// INPUT HANDLER ////////////////////////////////////////

	
	// GESTURE LISTENER EVENT SUPPORT ///////////////////////////////////////
	/**
	 * Adds a gesture listener to this component. The specified gesture listener's 
	 * <code>processGestureEvent(..)</code> method will be called when a gesture event 
	 * is processed by this component. The <code>IInputProcessor</code> paramter type specifies the source of
	 * the gesture event we are interested in. So to listen to drag events only for example, we would specify
	 * the <code>DragProcessor.class</code> as the first parameter.
	 * 
	 * @param gestureEvtSender the gesture evt sender
	 * @param listener the listener
	 */
	public void addGestureListener(Class<? extends IInputProcessor> gestureEvtSender, IGestureEventListener listener){
		this.gestureEvtSupport.addGestureEvtListener(gestureEvtSender, listener);
	}
	
	/**
	 * Removes the gesture event listener.
	 * @param gestureEvtSender the gesture evt sender
	 * @param listener the listener
	 */
	public void removeGestureEventListener(Class<? extends IInputProcessor> gestureEvtSender, IGestureEventListener listener) {
		gestureEvtSupport.removeGestureEventListener(gestureEvtSender, listener);
	}
	
	/**
	 * Removes the all gesture event listeners.
	 */
	public void removeAllGestureEventListeners() {
		this.gestureEvtSupport.clearListeners();
	}
	
	/**
	 * Removes the all gesture event listeners who listen to the specified input processor.
	 * @param gestureEvtSender the gesture evt sender
	 */
	public void removeAllGestureEventListeners(Class<? extends IInputProcessor> gestureEvtSender) {
		IGestureEventListener[] l = this.getGestureListeners();
        for (IGestureEventListener gestureEventListener : l) {
            this.removeGestureEventListener(gestureEvtSender, gestureEventListener);
        }
	}
	
	/**
	 * Returns the gesture listeners.
	 * @return the gesture listeners
	 */
	public final IGestureEventListener[] getGestureListeners() {
		return gestureEvtSupport.getListeners();
	}
	// GESTURE LISTENER EVENT SUPPORT ///////////////////////////////////////


	
	
	//@Override
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#processInputEvent(org.mt4j.input.inputData.MTInputEvent)
	 */
	public boolean processInputEvent(MTInputEvent inEvt) {
		if (inEvt.getEventPhase() != MTInputEvent.BUBBLING_PHASE && inEvt.getTarget().equals(this) /*&& inEvt.bubbles()*/){
			inEvt.setEventPhase(MTInputEvent.AT_TARGET);
		}
		
		if (this.isEnabled()){
//			System.out.println("Comp: " + this.getName() + " Evt: " + inEvt);
			//TODO do only if not handled maybe?
			//THIS IS A HACK TO ALLOW Global GESTURE PROCESSORS to send MTGEstureevents TO WORK
			if (inEvt instanceof MTGestureEvent){  
				this.processGestureEvent((MTGestureEvent)inEvt);
			}else{
				//Fire the same input event to all of this components' input listeners
				this.dispatchInputEvent(inEvt);
			}
		}
		
		
		if (inEvt.getBubbles() && !inEvt.isPropagationStopped() && inEvt.getEventPhase() == MTInputEvent.AT_TARGET){
			inEvt.setEventPhase(MTInputEvent.BUBBLING_PHASE);	
		}

		if (inEvt.getBubbles() && !inEvt.isPropagationStopped() && inEvt.getEventPhase() == MTInputEvent.BUBBLING_PHASE){
			MTComponent theParent = this.getParent();
			if (theParent != null){
				inEvt.setCurrentTarget(theParent);
				theParent.processInputEvent(inEvt);
				//TODO (register interest in cursors -> done automatically?)
				//TODO use getCurrentTarget in all inputProcessors, at least at sending the event - also at canvas processors?
				//TODO (check if currentTarget = this component in processorsupport? -> not really needed, make optional?)
				//TODO remove default input processors form AbstractShape! -> make helper instead -> also remove from SVGs
				//TODO in inputprocessors always intersect current target -> is the actual target also chcked then? -> to get composite effect..
				//TODO in inputprocessors get intersection points using the cursors getTarget() -> because one cursors target may be different than the other cursor now 
				//TODO if no input processor is registered all events are bubbled up - prevent that by default? - how?
				//TODO also allow bubbling of MTGestureEvents?
				//TODO in inputprocessors intersect with target and if no hit- with currenttarget?
				
				//FIXME if cursor is unlocked in dispatchInputEvent(), unlocked() may be called in an I.P. up the tree, but currentTarget is still the same as target!
				
				//TODO send locked() signal only to those who currently lock the cursor (not all lower priority ones)
				
				//FIXME use headMap -> inclusive = false? so it doesent send cursorLocked to some with same priority in cursorLockedByHigherPriorityGesture
				
				//TODO why only rotate processor registered at Keyboard when switching to rot/scale at parent?
				
				//TODO priorities-> float
				//TODO tap prior > drag prior -> really default that?
				//TODO (abort tap if moved too much -> start drag/scale)
			}
		}
		
		return false;
	}

	
	/**
	 * Processes gesture events.<br>
	 * Fires the specified gesture event to the attached IGestureEventListers of this component.
	 * 
	 * @param gestureEvent the gesture event
	 * @return true, if successful
	 */
	public boolean processGestureEvent(MTGestureEvent gestureEvent){
		this.gestureEvtSupport.fireGestureEvt(gestureEvent);
//		System.out.println("processGestureEvent on obj: " + this.getName() + " gestureEvent source: " + gestureEvent.getSource() +" ID: " +  gestureEvent.getId());
		return false;
	}
	

	/**
	 * Checks if is composite.
	 * @return true, if is composite
	 */
	public boolean isComposite() {
		return composite;
	}

	/**
	 * Setting a components <code>setComposite</code> to 'true' will result in 
	 * THIS component getting picked and returned
	 * when a child of this component is picked. So this component sort of consumes all picking
	 * of its children.
	 * This behaviour is desireable if we have a component with children that should be treated 
	 * as one component as a whole by gestures etc.
	 * 
	 * @param composite the composite
	 */
	public void setComposite(boolean composite) {
		this.composite = composite;
	}


	/**
	 * Gets the controller.
	 * @return the controller
	 */
	public IMTController getController() {
		return controller;
	}


	/**
	 * This attaches a controller object to the component.
	 * The controller's update method is called everytime the component's
	 * updateComponent method is called. This allows to update the component from the outside,
	 * without extending the component and overriding its update method.
	 * 
	 * @param controller the controller
	 * 
	 * @return the old IMT controller (may be null)
	 */
	public IMTController setController(IMTController controller) {
		IMTController oldController = this.controller;
		this.controller = controller;
		return oldController;
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.interfaces.IMTComponent#isGestureAllowed(java.lang.Class)
	 */
	public boolean isGestureAllowed(Class<? extends IInputProcessor> c){
		return this.allowedGestures.contains(c);
	}
	
	/**
	 * Sets the gesture allowance. 
	 * @param c the gesture processors class
	 * @param allowed allowance
	 */
	public void setGestureAllowance(Class<? extends IInputProcessor> c, boolean allowed){
		if (allowed){
			if (!this.allowedGestures.contains(c)){
				this.allowedGestures.add(c);
			}
		}else{
			if (this.allowedGestures.contains(c)){
				this.allowedGestures.remove(c);
			}
		}
	}

	//TODO put in Interface?
	/**
	 * Checks if this component is contained in the specified viewing frustum (is currently visible).
	 * @param frustum the frustum
	 * 
	 * @return true, if is contained in
	 */
	public boolean isContainedIn(IFrustum frustum){
		//Check if bounds are contained in the frustum
		//if shape has no boundingshape return true by default
		if (this.hasBounds()){
			return this.getBounds().isContainedInFrustum(frustum);
		}else{
			return true;
		}
	}
	
	
	/**
	 * Re orthogonalizes the components local matrix. As we use incremental matrix operations, floating
	 * point errors can add up and lead to loss of precision and matrix orthogonality. This method
	 * re-orthogonalizes the matrix.
	 * <br>- EXPERIMENTAL! - I think that this will destroy any shearing of the matrix
	 * <br>- EXPENSIVE OPERATION! 
	 */
	public void reOrthogonalize(){
//		Matrix local = this.getLocalMatrix();
//		Vector3D trans = new Vector3D();
//		Vector3D rot = new Vector3D();
//		Vector3D scale = new Vector3D();
//		local.decompose(trans, rot, scale);
		Vector3D scale = this.getLocalMatrix().getScale();
		
//		System.out.println("Det b4: " + getLocalMatrix().determinant());
//		Vector3D v1 = new Vector3D(2,3,4);
//		Vector3D v2 = new Vector3D(v1);
//		v1.transform(this.getLocalMatrix());
		
		Matrix m = new Matrix(this.getLocalMatrix());
//        m.orthonormalizeLocal();
		//can we use 3x3 otrhogonalization on a 4x4 matrix just using the middle part?
		//-seems yes
		m.orthonormalizeUpperLeft();
        //Re-Apply scale because its removed at orthonormalization
//        m.mult(Matrix.getScalingMatrix(Vector3D.ZERO_VECTOR, scale.x, scale.y, scale.z), m);
        m.scale(scale);
        //Automatically inverts() the localMatrix, so exact inverse again! :)
        this.setLocalMatrix(m);
        
//        v2.transform(this.getLocalMatrix());
//        System.out.println("Diff: " + v2.getSubtracted(v1));
//        logger.debug("Determinant after orthogonalize: " + getLocalMatrix().determinant());
	}
	
	
	/**
	 * Sets user data for this component. This mechanism can be
	 * used to attach arbitrary information to this component by storing
	 * a key and a corresponding value object. The value object can then later
	 * be retrieved if the key is provided.
	 * 
	 * @param key the key
	 * @param value the value
	 * @see #getUserData
	 */
	public void setUserData(Object key, Object value){
		if (userData == null){ //lazily initialize map
//			userData = new WeakHashMap<Object, Object>(); //use weak map?
			userData = new HashMap<Object, Object>();
		}
		userData.put(key, value);
	}
	
	/**
	 * Gets the user data associated with the specified key.
	 * @param key the key
	 * 
	 * @return the user data
	 */
	public Object getUserData(Object key){
		if (userData == null){
			return null;
		}
		return userData.get(key);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "\"" + this.getName() + "\"" + " [" + super.toString() + "]";
	}
		
}
