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
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.ani.AniAnimation;
import org.mt4j.util.math.Vector3D;

/**
 * The Class SlideTransition.
 * 
 * @author Christopher Ruff
 */
public class SlideTransition extends AbstractTransition {
	
	/** The app. */
	private AbstractMTApplication app;
	
	/** The finished. */
	private boolean finished;
	
	/** The last scene. */
	private Iscene lastScene;
	
	/** The next scene. */
	private Iscene nextScene;
	
	/** The anim. */
	private IAnimation anim;
	
	/** The duration. */
	private int duration;
	
	/** The last scene rectangle. */
	private MTRectangle lastSceneRectangle;
	
	/** The next scene rectangle. */
	private MTRectangle nextSceneRectangle;
	
	public boolean slideLeft;
	
	
	/**
	 * Instantiates a new slide transition.
	 * 
	 * @param mtApplication the mt application
	 */
	public SlideTransition(AbstractMTApplication mtApplication) {
		this(mtApplication, 2000);
	}
	
	public SlideTransition(AbstractMTApplication mtApplication, long duration) {
		this(mtApplication, duration, true);
	}
	
	/**
	 * Instantiates a new slide transition.
	 * 
	 * @param mtApplication the mt application
	 * @param duration the duration
	 */
	public SlideTransition(AbstractMTApplication mtApplication, long duration, boolean slideLeft) {
		super(mtApplication, "Slide Transition");
		this.app = mtApplication;
		this.duration = (int) duration;
		this.finished = true;
		this.slideLeft = slideLeft;
		
//		anim = new Animation("Flip animation 2", new MultiPurposeInterpolator(app.width, 0, this.duration, 0.0f, 0.7f, 1) , this);
		anim = new AniAnimation(app.width, 0, this.duration, AniAnimation.CIRC_OUT, this);
		if (!slideLeft)
			((AniAnimation)anim).reverse();
		anim.addAnimationListener(new IAnimationListener(){
			public void processAnimationEvent(AnimationEvent ae) {
				switch (ae.getId()) {
				case AnimationEvent.ANIMATION_STARTED:
				case AnimationEvent.ANIMATION_UPDATED:
					nextSceneRectangle.translateGlobal(new Vector3D(ae.getDelta(),0,0));
					lastSceneRectangle.translateGlobal(new Vector3D(ae.getDelta(),0,0));
					break;
				case AnimationEvent.ANIMATION_ENDED:
					nextSceneRectangle.translateGlobal(new Vector3D(ae.getDelta(),0,0));
					lastSceneRectangle.translateGlobal(new Vector3D(ae.getDelta(),0,0));
					finished = true;
					break;
				default:
					break;
				}
			}});
//		((Animation)anim).setResetOnFinish(true);
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
				lastSceneRectangle = new MTRectangle(app,0, 0, app.width, app.height);
				lastSceneRectangle.setTexture(app.g.get());
				lastSceneRectangle.setStrokeColor(MTColor.BLACK);

				nextSceneRectangle = new MTRectangle(app,0, 0, app.width, app.height);
				nextScene.drawAndUpdate(app.g, 0);
				nextSceneRectangle.setTexture(app.g.get());
				nextSceneRectangle.setStrokeColor(MTColor.BLACK);

				getCanvas().addChild(lastSceneRectangle);
				getCanvas().addChild(nextSceneRectangle);
				
				if (slideLeft) {
					nextSceneRectangle.translateGlobal(new Vector3D(app.width,0,0));
				} else {
					nextSceneRectangle.translateGlobal(new Vector3D(-app.width,0,0));
				}
				
				nextSceneRectangle.setVisible(true);
				
				anim.start();
			}
		});
	}
	
	
	@Override
	public void onLeave() {
		finished = true;
		this.lastScene = null;
		this.nextScene = null;
		
		lastSceneRectangle.destroy();
		nextSceneRectangle.destroy();
	}
	

}
