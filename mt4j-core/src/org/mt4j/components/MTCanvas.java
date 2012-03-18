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
package org.mt4j.components;

import java.util.List;

import org.mt4j.components.clusters.Cluster;
import org.mt4j.components.clusters.ClusterManager;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.IHitTestInfoProvider;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.Matrix;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PMatrix3D;

/**
 * MTCanvas is the root node of the component hierarchy of a MT4j scene.
 * To make a mt4j component visible and interactable you have to add it to
 * the scene's canvas or to a component which is already attached to the canvas.
 * The canvas then recursivly updates and draws all attached components each frame.
 * 
 * @author Christopher Ruff
 */
public class MTCanvas extends MTComponent implements IHitTestInfoProvider{
	
	/** The cluster manager. */
	private ClusterManager clusterManager;
	
//	/** The last time hit test. */
//	private long lastTimeHitTest;
//	
//	/** The cache time delta. */
//	private long cacheTimeDelta;
//	
//	/** The cache clear time. */
//	private int cacheClearTime;
//	
//	/** The position to component. */
//	private HashMap<Position, IMTComponent3D> positionToComponent;
//	
//	/** The timer. */
//	private Timer timer;
//	
//	/** The use hit test cache. */
//	private boolean useHitTestCache;
	
	/** The frustum culling switch. */
	private boolean frustumCulling;
	
	private int culledObjects = 0;

	private long lastUpdateTime;

//	private PMatrix3D modelViewP5;

	
	
	/**
	 * The Constructor.
	 *
	 * @param pApplet the applet
	 * @param attachedCamera the attached camera
	 */
	public MTCanvas(PApplet pApplet, Icamera attachedCamera) {
		this(pApplet, "unnamed MT Canvas", attachedCamera);
	}

	/**
	 * The Constructor.
	 * 
	 * @param pApplet the applet
	 * @param name the name
	 * @param attachedCamera the attached camera
	 */
	public MTCanvas(PApplet pApplet, String name, Icamera attachedCamera) {
		super(pApplet, name, attachedCamera);
//		//Cache settings
//		lastTimeHitTest = 0;
//		cacheTimeDelta = 100;
//		cacheClearTime = 20000;
//		useHitTestCache = true;
		
		lastUpdateTime = 0;
		
		clusterManager = new ClusterManager(this);
		
//		positionToComponent = new HashMap<Position, IMTComponent3D>();
//		
//		//Schedule a Timer task to clear the object cache so it wont 
//		//get filled infinitely
//		timer = new Timer(cacheClearTime, new ActionListener(){
//			public void actionPerformed(ActionEvent arg0) {
////				System.out.println("Hit test chache entries: " + positionToComponent.size() + " ... cleared!");
//				positionToComponent.clear();
//			}
//		});
////		timer.start(); //FIXME TEST 
//		this.useHitTestCache = false; //FIXME TEST DISABLE HIT CACHE
		
//		this.setCollidable(false);
		
		this.setGestureAllowance(RotateProcessor.class, false);
		this.setGestureAllowance(ScaleProcessor.class, false);
		this.setGestureAllowance(TapProcessor.class, false);
		this.setGestureAllowance(DragProcessor.class, false);
		
		this.setPickable(false);
		
		//Frustum culling default
		frustumCulling = false;
		
//		this.modelViewP5 = GraphicsUtil.getModelView(); //TODO does this even work? -> better get it each time
	}
	
	@Override
	protected void destroyComponent() {
		super.destroyComponent();
//		
//		if (this.timer != null && timer.isRunning()){
//			timer.stop();
//		}
//		
//		if (positionToComponent != null){
//			positionToComponent.clear();
//		}
	}
	

	/**
	 * Method for asking the canvas whether and which object is at the specified
	 * screen position.
	 * <p>
	 * IMPORTANT: this method returns the MTCanvas instance if no other object is hit.
	 * This means that the MTCanvas instance acts like the background => Gestures that
	 * are supposed to be performed on the background have to check if they hit the canvas.
	 * And the gestureevents should then have the canvas as their targetComponent!
	 * Also, you have to be careful in other gestures, as even when you dont hit an object, you will
	 * get the mtcanvas returned as the hit component - not null! 
	 * <p>Note: if the hit component is part of a cluster, the cluster is returned!
	 * 
	 * @param x the screen x coordinate
	 * @param y the screen y coordinate
	 * 
	 * @return the object at that position or this MTCanvas instance if no component was hit
	 */
	public IMTComponent3D getComponentAt(float x, float y) { 
		IMTComponent3D closest3DComp = null;
		try{
//			long now = System.currentTimeMillis();
//			if (useHitTestCache){
//				if (now - lastTimeHitTest > cacheTimeDelta){ //If the time since last check surpassed => do new hit-test!
//					//Benchmark the picking
////					long a = System.nanoTime();
//					closest3DComp = this.pick(x, y).getNearestPickResult();
//					//Benchmark the picking
////					long b = System.nanoTime();
////					System.out.println("Time for picking the scene: " + (b-a));
//					/*
//					for (MTBaseComponent c : pickResult.getPickList())
//						System.out.println(c.getName());
//					if (closest3DComp != null)
//						System.out.println("Using: " + closest3DComp.getName());
//					*/
//					if (closest3DComp == null){
//						closest3DComp = this;
//					}
//					positionToComponent.put(new Position(x,y), closest3DComp);
//				}else{
//					//Check whats in the cache
//					IMTComponent3D cachedComp = positionToComponent.get(new Position(x,y));
//					if (cachedComp != null){ //Use cached obj
//						closest3DComp = cachedComp;
//						positionToComponent.put(new Position(x,y), closest3DComp);
//					}else{
//						closest3DComp = this.pick(x, y).getNearestPickResult();
//						if (closest3DComp == null){
//							closest3DComp = this;
//						}
//						positionToComponent.put(new Position(x,y), closest3DComp);
//					}
//				}
//			}else{//IF no hittest cache is being used
				closest3DComp = this.pick(x, y).getNearestPickResult();
				if (closest3DComp == null){
					closest3DComp = this;
				}
//			}
//			lastTimeHitTest = now;
			
	//		/*//TODO anders machen..z.b. geclusterte comps einfach als kinder von
			//�bergeordnetem clusterpoly machen? aber mit clusterPoly.setComposite(TRUE);
			//Clusterpoly pickable machen damit das hier nicht gebraucht wird?
			Cluster sel = this.getClusterManager().getCluster(closest3DComp);
			  if (sel != null){
				  closest3DComp = sel;
			  }
	//		 */
			  
//			  //FIXME TEST for stencil clipped scene windows -> we have to return the scenes canvas for some gestures!
//			  if (closest3DComp != null && closest3DComp instanceof mtClipSceneWindow)
		  
		}catch(Exception e){
			System.err.println("Error while trying to pick an object: ");
			e.printStackTrace();
		}
		/*
		if (closest3DComp != null)
			System.out.println("Picked: '" + closest3DComp.getName() + "' at pos (" + x + "," + y + ")");
		else
			System.out.println("Picked: '" + closest3DComp + "' at pos (" + x + "," + y + ")");
		*/
		return closest3DComp;
	}
	
	
	public boolean isBackGroundAt(float x, float y) {
		return this.getComponentAt(x, y).equals(this);
	}
	
	
	//FIXME TEST	
	@Override
	public void updateComponent(long timeDelta) {
		super.updateComponent(timeDelta);
		this.lastUpdateTime = timeDelta;
	}
	//FIXME TEST
	private boolean calledFromDrawComponent = false;
	//FIXME TEST
//	/* 
//	 * Actually this canvases drawComponent method should be called
//	 * ever because the canvas should not be added as a child to any component.
//	 * Anyway - this code will still make it possible to use it as a child of other components
//	 * (non-Javadoc)
//	 * @see org.mt4j.components.MTComponent#drawComponent(processing.core.PGraphics)
//	 */
//	@Override
//	public void drawComponent(PGraphics g) { //FIXME this would draw the canvas 2 times..
//		super.drawComponent(g);
//		
//		//Call the canvases scenes draw method to also draw
//		//stuff defined in an overrriden scenes draw method
//		if (this.getRenderer() instanceof MTApplication){
//			MTApplication app = (MTApplication)this.getRenderer();
//			Iscene[] scenes = app.getScenes();
//			for (int i = 0; i < scenes.length; i++) {
//				Iscene iscene = scenes[i];
//				if (iscene instanceof AbstractScene){
//					AbstractScene as = (AbstractScene)iscene;
//					if (as.getCanvas().equals(this)){
//						this.calledFromDrawComponent = true;
////						this.drawAndUpdateCanvas(g, this.lastUpdateTime);
//						as.drawAndUpdate(g, this.lastUpdateTime);
//						this.calledFromDrawComponent = false;
//					}
//				}
//			}
//		}
//		
////		this.calledFromDrawComponent = true;
////		this.drawAndUpdateCanvas(g, this.lastUpdateTime);
////		this.calledFromDrawComponent = false;
//	}
	
	
	/**
	 * Updates and then draws every visible object in the canvas.
	 * First calls the <code>updateComponent(long timeDelta)</code> method. Then
	 * the <code>drawComponent()</code> method of each object in the scene graph.
	 * Also handles the setting of cameras attached to the objects.
	 * @param graphics 
	 * 
	 * @param updateTime the time passed since the last update (in ms)
	 */
	public void drawAndUpdateCanvas(PGraphics graphics, long updateTime){
		this.culledObjects = 0;
		
		//FIXME THIS IS A HACK! WE SHOULD REPLACE CLUSTERS WITH NORMAL COMPONENTS INSTEAD!
		//Update cluster components 
		Cluster[] clusters = getClusterManager().getClusters();
        for (Cluster cluster : clusters) {
            cluster.updateComponent(updateTime);
        }
		
		this.drawUpdateRecursive(this, updateTime, graphics);
//		System.out.println("Culled objects: " + culledObjects);
	}

	
	/**
	 * Draw the whole canvas update recursive.
	 * 
	 * @param currentcomp the currentcomp
	 * @param updateTime the update time
	 * @param graphics the renderer
	 */
	private void drawUpdateRecursive(MTComponent currentcomp, long updateTime, PGraphics graphics){
		if (currentcomp.isVisible()){
			//Update current component
			currentcomp.updateComponent(updateTime);
			
			if (currentcomp.getAttachedCamera() != null){
				//Saves transformations up to this object
				graphics.pushMatrix();
				
				//Resets the modelview completely with a new camera matrix
				currentcomp.getAttachedCamera().update();
				
				if (currentcomp.getParent() != null){
					//Applies all transforms up to this components parent
					//because the new camera wiped out all previous transforms
					Matrix m = currentcomp.getParent().getGlobalMatrix();
//					PGraphics3D pgraphics3D = (PGraphics3D)graphics;
//					pgraphics3D.modelview.apply(
//					modelViewP5.apply(
					
//					GraphicsUtil.getModelView().apply(
//							m.m00, m.m01, m.m02,  m.m03,
//							m.m10, m.m11, m.m12,  m.m13,
//							m.m20, m.m21, m.m22,  m.m23,
//							m.m30, m.m31, m.m32,  m.m33
//					);
					
					if (PlatformUtil.isAndroid()){
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
				}
				
				//Apply local transform etc
				currentcomp.preDraw(graphics);
				
				//Check visibility with camera frustum
				if (frustumCulling){
					if (currentcomp.isContainedIn(currentcomp.getViewingCamera().getFrustum())){
						if (!this.calledFromDrawComponent){ //FIXME TEST
						// DRAW THE COMPONENT  \\
						currentcomp.drawComponent(graphics);
						}
					}else{
						culledObjects++;
						//System.out.println("Not visible: " + currentcomp.getName());
					}
				}else{
					if (!this.calledFromDrawComponent){ //FIXME TEST
					// DRAW THE COMPONENT  \\
					currentcomp.drawComponent(graphics);
					}
				}
				
				currentcomp.postDraw(graphics);

				//Draw Children  //FIXME for each loop sometimes throws concurrentmodification error because of fail-fast iterator
//				for (MTComponent child : currentcomp.getChildList())
//					drawUpdateRecursive(child, updateTime, graphics);
				
				List<MTComponent> childs = currentcomp.getChildList();
				int childCount = childs.size();
				for (int i = 0; i < childCount; i++) {
					drawUpdateRecursive(childs.get(i), updateTime, graphics);
				}

				currentcomp.postDrawChildren(graphics);
				
				//Restores the transforms of the previous camera etc
				graphics.popMatrix(); 
			}else{//If no custom camera was set
				//TODO in abstactvisiblecomp wird outine �ber gradients und clips
				//gezeichnet obwohl hier invisble war! FIXME!
				//evtl applymatrix unapply in eigene methode? dann nur das ausf�hren, kein pre/post draw!
				
				//TODO vater an kinder listener -> resize - new geometry -> resize own 
				
				currentcomp.preDraw(graphics);
				
				if (frustumCulling){
					//Check visibility with camera frustum
					if (currentcomp.isContainedIn(currentcomp.getViewingCamera().getFrustum())){
						// DRAW THE COMPONENT  \\
						currentcomp.drawComponent(graphics);
					}else{
						culledObjects++;
						//System.out.println("Not visible: " + currentcomp.getName());
					}
				}else{
					// DRAW THE COMPONENT  \\
					currentcomp.drawComponent(graphics);
				}
				
				currentcomp.postDraw(graphics);
					
//				for (MTComponent child : currentcomp.getChildList()) //FIXME for each loop sometimes throws concurrentmodification error because of fail-fast iterator
//					drawUpdateRecursive(child, updateTime, graphics);
				
				List<MTComponent> childs = currentcomp.getChildList();
				int childCount = childs.size();
				for (int i = 0; i < childCount; i++) {
					drawUpdateRecursive(childs.get(i), updateTime, graphics);
				}
				
				currentcomp.postDrawChildren(graphics);
			}
		}//if visible end
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.components.MTComponent#processInputEvent(org.mt4j.input.inputData.MTInputEvent)
	 */
	@Override
	public boolean processInputEvent(MTInputEvent inEvt) {
		//TODO not very elegant - better approach??
		if (inEvt.hasTarget() && inEvt.getEventPhase() != MTInputEvent.BUBBLING_PHASE){
			if (!inEvt.getTarget().equals(this)){ //Avoid recursion
				//Send directed event to the target component
				return inEvt.getTarget().processInputEvent(inEvt);
			}
		}
		
//		return true;  //this.handleEvent
		//handle in superclass
		
		//The MTCanvas get events targeted at him AND events that have no target!
		return super.processInputEvent(inEvt);
	}
	
		
	
	/**
	 * Gets the cluster manager.
	 * 
	 * @return the cluster manager
	 */
	public ClusterManager getClusterManager() {
		return clusterManager;
	}


	/**
	 * Sets the cluster manager.
	 * 
	 * @param selectionManager the new cluster manager
	 */
	public void setClusterManager(ClusterManager selectionManager) {
		this.clusterManager = selectionManager;
	}


//	/**
//	 * Gets the cache time delta.
//	 * 
//	 * @return the cache time delta
//	 */
//	public long getCacheTimeDelta() {
//		return cacheTimeDelta;
//	}
//
//	/**
//	 * If repeated calls to getObjectAt(float x, float y) in MTCanvas class
//	 * are called during the provided cacheTimeDelta, the Canvas looks into his
//	 * cache instead of querying all objects again
//	 * Default value is: 80.
//	 * 
//	 * @param cacheTimeDelta the cache time delta
//	 */
//	public void setCacheTimeDelta(long cacheTimeDelta) {
//		this.cacheTimeDelta = cacheTimeDelta;
//	}
//
//	/**
//	 * Checks if is use hit test cache.
//	 * 
//	 * @return true, if is use hit test cache
//	 */
//	public boolean isUseHitTestCache() {
//		return useHitTestCache;
//	}
//
//	
//	/**
//	 * The canvas can be set to look into a hit test cache if
//	 * repeated calls to getComponentAt() with the same coordinates
//	 * during a short period of time are made.
//	 * This period of time can be set with
//	 * <code>setCacheTimeDelta(long cacheTimeDelta)</code>
//	 * <p>
//	 * This is useful for example when a click is made many gestureanalyzers
//	 * call getObjectAt() almost concurrently.
//	 * 
//	 * @param useHitTestCache the use hit test cache
//	 */
//	public void setUseHitTestCache(boolean useHitTestCache) {
//		if (useHitTestCache && !timer.isRunning())
//			timer.start();
//		else if (!useHitTestCache && timer.isRunning())
//			timer.stop();
//		
//		this.useHitTestCache = useHitTestCache;
//	}
//
//
//	/**
//	 * Gets the cache clear time.
//	 * 
//	 * @return the cache clear time
//	 */
//	public int getCacheClearTime() {
//		return cacheClearTime;
//	}
//
//	/**
//	 * Sets the time intervals in ms in which the canvas clears its hit test cache
//	 * Default value is: 20000 ms
//	 * <p>
//	 * This is important to prevent the hit test cache from growing indefinitely.
//	 * 
//	 * @param cacheClearTime the cache clear time
//	 */
//	public void setCacheClearTime(int cacheClearTime) {
//		timer.setDelay(cacheClearTime);
//		this.cacheClearTime = cacheClearTime;
//	}


	public boolean isFrustumCulling() {
		return frustumCulling;
	}

	public void setFrustumCulling(boolean frustumCulling) {
		this.frustumCulling = frustumCulling;
	}
	
	
	

	
	
//	/**
//	 * Class used for the pickobject cache.
//	 */
//	private class Position{
//		/** The y. */
//		float x,y;
//		
//		/**
//		 * Instantiates a new position.
//		 * 
//		 * @param x the x
//		 * @param y the y
//		 */
//		public Position(float x, float y){
//			this.x = x;
//			this.y = y;
//		}
//		
//		/**
//		 * Gets the x.
//		 * 
//		 * @return the x
//		 */
//		public float getX() {return x;}
//		
//		/**
//		 * Gets the y.
//		 * 
//		 * @return the y
//		 */
//		public float getY() {return y;}
//		
//		/* (non-Javadoc)
//		 * @see java.lang.Object#equals(java.lang.Object)
//		 */
//		@Override
//		public boolean equals(Object arg0) {
//			return (arg0 instanceof Position && ((Position)arg0).getX() == this.getX() && ((Position)arg0).getY() == this.getY());
//		}
//		
//		/* (non-Javadoc)
//		 * @see java.lang.Object#hashCode()
//		 */
//		@Override
//		public int hashCode() {
//			return ((int)x+(int)y);
//		}
//	}
}
