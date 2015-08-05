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
package org.mt4j.sceneManagement.transition;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTSceneTexture;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.ani.AniAnimation;

/**
 * The Class FlipTransition.
 * 
 * @author Christopher Ruff
 */
public class FlipTransition extends AbstractTransition {
	
	/** The app. */
	private AbstractMTApplication app;
	
	/** The finished. */
	private boolean finished;
	
	/** The last scene. */
	private Iscene lastScene;
	
	/** The next scene. */
	private Iscene nextScene;
	
	/** The last scene window. */
	private MTSceneTexture lastSceneWindow;
	
	/** The next scene window. */
	private MTSceneTexture nextSceneWindow;
	
	/** The anim2. */
	private IAnimation anim2;
	
	/** The anim. */
	private IAnimation anim;
	
	/** The duration. */
	private long duration;
	
	/** The last scene rectangle. */
	private MTRectangle lastSceneRectangle;
	
	/** The next scene rectangle. */
	private MTRectangle nextSceneRectangle;

	private float totalAngleAnim;

	private float totalAnim2;
	
	
	/**
	 * Instantiates a new flip transition.
	 * 
	 * @param mtApplication the mt application
	 */
	public FlipTransition(AbstractMTApplication mtApplication) {
		this(mtApplication, 2000);
	}
	
	
	/**
	 * Instantiates a new flip transition.
	 * 
	 * @param mtApplication the mt application
	 * @param duration the duration
	 */
	public FlipTransition(AbstractMTApplication mtApplication, long duration) {
		super(mtApplication, "Flip Transition");
		this.app = mtApplication;
		this.duration = duration;
		this.finished = true;
		
		
//		anim2 = new Animation("Flip animation 2", new MultiPurposeInterpolator(0,90, this.duration/2f, 0, 0.5f, 1) , this);
		anim2 = new AniAnimation(0, 90, (int)((float)this.duration/2f), AniAnimation.CIRC_OUT, this);
		anim2.addAnimationListener(new IAnimationListener(){
			public void processAnimationEvent(AnimationEvent ae) {
				nextSceneRectangle.rotateYGlobal(nextSceneRectangle.getCenterPointGlobal(), ae.getDelta());
				if (ae.getId() == AnimationEvent.ANIMATION_ENDED){
					finished = true;
				}
			}});
//		((Animation)anim2).setResetOnFinish(true);
		
//        anim = new Animation("Flip animation 1", new MultiPurposeInterpolator(0,90, this.duration/2f, 0.5f, 1, 1) , this);
		anim = new AniAnimation(0,90, (int)((float)this.duration/2f), AniAnimation.LINEAR, this);
        anim.addAnimationListener(new IAnimationListener(){
        	public void processAnimationEvent(AnimationEvent ae) {
        		lastSceneRectangle.rotateYGlobal(lastSceneRectangle.getCenterPointGlobal(), ae.getDelta());
        		if (ae.getId() == AnimationEvent.ANIMATION_ENDED){
//					nextSceneWindow.setVisible(true);
//					lastSceneWindow.setVisible(false);
					lastSceneRectangle.setVisible(false);
					nextSceneRectangle.setVisible(true);
					anim2.start();
				}
        	}});
//        ((Animation)anim2).setResetOnFinish(true);
		
	}


	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.transition.ITransition#isFinished()
	 */
	public boolean isFinished() {
		return finished;
	}

	
	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.transition.ITransition#setup(org.mt4j.sceneManagement.Iscene, org.mt4j.sceneManagement.Iscene)
	 */
	public void setup(Iscene lastScenee, Iscene nextScenee) {
		this.lastScene = lastScenee;
		this.nextScene = nextScenee;
		finished = false;
		
		//Disable the scene's global input processors. We will be redirecting the input
		//from the current scene to the window scene
		app.getInputManager().disableGlobalInputProcessors(lastScene);
		app.getInputManager().disableGlobalInputProcessors(nextScene);
		
		app.invokeLater(new Runnable() {
			public void run() {
				lastSceneWindow = new MTSceneTexture(app,0, 0, lastScene);
				nextSceneWindow = new MTSceneTexture(app,0, 0, nextScene);

				lastSceneRectangle = new MTRectangle(app,0, 0, app.width, app.height);
				lastSceneRectangle.setGeometryInfo(lastSceneWindow.getGeometryInfo());
				lastSceneRectangle.setTexture(lastSceneWindow.getTexture());
				lastSceneRectangle.setStrokeColor(new MTColor(0,0,0,255));

				nextSceneRectangle = new MTRectangle(app,0, 0, app.width, app.height);
				nextSceneRectangle.setGeometryInfo(nextSceneWindow.getGeometryInfo());
				nextSceneRectangle.setTexture(nextSceneWindow.getTexture());
				nextSceneRectangle.setStrokeColor(new MTColor(0,0,0,255));

				getCanvas().addChild(lastSceneRectangle);
				getCanvas().addChild(nextSceneRectangle);

				nextSceneRectangle.rotateY(nextSceneRectangle.getCenterPointGlobal(), 270, TransformSpace.GLOBAL);
				nextSceneRectangle.setVisible(false);

				//Draw scenes into texture once!
				lastSceneWindow.drawComponent(app.g);
				nextSceneWindow.drawComponent(app.g);
				
				anim.start();
			}
		});

//		this.getCanvas().addChild(this.lastSceneWindow);
//		this.getCanvas().addChild(this.nextSceneWindow);
//		this.nextSceneWindow.rotateY(this.nextSceneWindow.getCenterPointGlobal(), 270, TransformSpace.GLOBAL);
//		this.nextSceneWindow.setVisible(false);
		//TODO wihtout FBO copyPixels
	}
	
	
	@Override
	public void onLeave() {
		finished = true;
		this.lastScene = null;
		this.nextScene = null;
		
		this.lastSceneWindow.destroy();
		this.nextSceneWindow.destroy();
		lastSceneRectangle.destroy();
		nextSceneRectangle.destroy();
	}
	
	
}
