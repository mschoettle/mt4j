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
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.Animation;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.MultiPurposeInterpolator;

import processing.core.PGraphics;

/**
 * The Class FadeTransition.
 * 
 * @author Christopher Ruff
 */
public class FadeTransition extends AbstractTransition {
	
	/** The app. */
	private AbstractMTApplication app;
	
	/** The finished. */
	private boolean finished;
	
	/** The anim. */
	private Animation anim;
	
	/** The anim2. */
	private Animation anim2;
	
	/** The full screen quad. */
	private MTRectangle fullScreenQuad;
	
	/** The scene to draw. */
	private Iscene sceneToDraw;
	
	/** The last scene. */
	private Iscene lastScene;
	
	/** The next scene. */
	private Iscene nextScene;
	
	/** The duration. */
	private long duration;
	
	
	/**
	 * Instantiates a new fade transition.
	 * 
	 * @param mtApplication the mt application
	 */
	public FadeTransition(AbstractMTApplication mtApplication) {
		this(mtApplication, 2000);
	}
	
	
	/**
	 * Instantiates a new fade transition.
	 * 
	 * @param mtApplication the mt application
	 * @param duration the duration
	 */
	public FadeTransition(AbstractMTApplication mtApplication, long duration) {
		super(mtApplication, "Fade Transition");
		this.app = mtApplication;
		this.duration = duration;
		
		this.setClear(true);
			
		finished = false;
		
		anim2 = new Animation("Fade animation 2", new MultiPurposeInterpolator(255,0, this.duration/2f, 0, 0.8f, 1) , this);
		anim2.addAnimationListener(new IAnimationListener(){
			//@Override
			public void processAnimationEvent(AnimationEvent ae) {
				switch (ae.getId()) {
				case AnimationEvent.ANIMATION_STARTED:
				case AnimationEvent.ANIMATION_UPDATED:
					fullScreenQuad.setFillColor(new MTColor(0,0,0, ae.getValue()));
					break;
				case AnimationEvent.ANIMATION_ENDED:
					fullScreenQuad.setFillColor(new MTColor(0,0,0, ae.getValue()));
					finished = true;
					break;
				default:
					break;
				}
			}});
		anim2.setResetOnFinish(true);
		
        anim = new Animation("Fade animation 1", new MultiPurposeInterpolator(0,255, this.duration/2f, 0, 1, 1) , this);
        anim.addAnimationListener(new IAnimationListener(){
        	//@Override
        	public void processAnimationEvent(AnimationEvent ae) {
        		switch (ae.getId()) {
				case AnimationEvent.ANIMATION_STARTED:
				case AnimationEvent.ANIMATION_UPDATED:
					fullScreenQuad.setFillColor(new MTColor(0,0,0, ae.getValue()));
					break;
				case AnimationEvent.ANIMATION_ENDED:
					sceneToDraw = nextScene;
					anim2.start();
					break;
				default:
					break;
				}
        	}});
       anim.setResetOnFinish(true);
       
//       fullScreenQuad = new MTRectangle(app,0, 0, app.width, app.height);
       fullScreenQuad = new MTRectangle(app,0, 0, MT4jSettings.getInstance().getWindowWidth(), MT4jSettings.getInstance().getWindowHeight());
       fullScreenQuad.setFillColor(new MTColor(0,0,0,0));
       fullScreenQuad.setNoStroke(true);
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.transition.ITransition#setup(org.mt4j.sceneManagement.Iscene, org.mt4j.sceneManagement.Iscene)
	 */
	public void setup(Iscene lastScene, Iscene nextScene) {
		this.lastScene = lastScene;
		this.nextScene = nextScene;
//		sceneToDraw = this.getPreviousScene();
		sceneToDraw = this.lastScene;
		finished = false;
		anim.start();
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.AbstractScene#drawAndUpdate(processing.core.PGraphics, long)
	 */
	@Override
	public void drawAndUpdate(PGraphics graphics, long timeDelta) {
		super.drawAndUpdate(graphics, timeDelta);
		sceneToDraw.drawAndUpdate(graphics, timeDelta);
		fullScreenQuad.drawComponent(graphics);
	}
	
	
	@Override
	public void onLeave() {
		anim.stop();
		anim2.stop();
		finished = true;
		this.lastScene = null;
		this.nextScene = null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.sceneManagement.transition.ITransition#isFinished()
	 */
	public boolean isFinished() {
		return finished;
	}



}
