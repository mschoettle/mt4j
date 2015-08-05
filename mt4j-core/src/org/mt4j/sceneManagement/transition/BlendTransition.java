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
import org.mt4j.components.visibleComponents.widgets.MTSceneTexture;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.ani.AniAnimation;

import processing.core.PGraphics;

/**
 * The Class BlendTransition.
 * 
 * @author Christopher Ruff
 */
public class BlendTransition extends AbstractTransition {
	
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
	
	/** The anim. */
	private IAnimation anim;
	
	/** The duration. */
	private int duration;
	
	/** The last scene rectangle. */
	private MTRectangle lastSceneRectangle;
	
	/**
	 * Instantiates a new blend transition.
	 * 
	 * @param mtApplication the mt application
	 */
	public BlendTransition(AbstractMTApplication mtApplication) {
		this(mtApplication, 2000);
	}
	
	
	/**
	 * Instantiates a new blend transition.
	 * 
	 * @param mtApplication the mt application
	 * @param duration the duration
	 */
	public BlendTransition(AbstractMTApplication mtApplication, int duration) {
		super(mtApplication, "Blend Transition");
		this.app = mtApplication;
		this.duration = duration;
		this.finished = true;
		
//		anim = new Animation("Blend animation ", new MultiPurposeInterpolator(255,0, this.duration, 0, 0.7f, 1) , this);
		anim = new AniAnimation(255,0, this.duration, AniAnimation.CIRC_OUT, this);
		anim.addAnimationListener(new IAnimationListener(){
			public void processAnimationEvent(AnimationEvent ae) {
				float val = ae.getValue();
				switch (ae.getId()) {
				case AnimationEvent.ANIMATION_STARTED:
					lastSceneRectangle.setVisible(true);
				case AnimationEvent.ANIMATION_UPDATED:
					lastSceneRectangle.setFillColor(new MTColor(255,255,255, val));
					break;
				case AnimationEvent.ANIMATION_ENDED:
					lastSceneRectangle.setVisible(false);
					lastSceneRectangle.setFillColor(new MTColor(255,255,255, val));
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
	 * @see org.mt4j.sceneManagement.AbstractScene#drawAndUpdate(processing.core.PGraphics, long)
	 */
	@Override
	public void drawAndUpdate(PGraphics graphics, long timeDelta) {
		this.nextScene.drawAndUpdate(graphics, timeDelta);
//		this.clear(graphics)
		super.drawAndUpdate(graphics, timeDelta);
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
				lastSceneWindow = new MTSceneTexture(app,0, 0, Math.round(app.width/2f), Math.round(app.height/2f), lastScene);
				lastSceneRectangle = new MTRectangle(app,0, 0, app.width, app.height);
				
				lastSceneRectangle.setGeometryInfo(lastSceneWindow.getGeometryInfo());
				lastSceneRectangle.setTexture(lastSceneWindow.getTexture());
				lastSceneRectangle.setStrokeColor(new MTColor(0,0,0,255));
				lastSceneRectangle.setVisible(false);
				lastSceneRectangle.setNoStroke(true);
				lastSceneRectangle.setDepthBufferDisabled(true);
				getCanvas().addChild(lastSceneRectangle);
				
				setClear(false);

				//Draw scene into texture once!
				lastSceneWindow.drawComponent(app.g);
				
				anim.start();
			}
		});
	}
	
	
	public void onLeave() {
		finished = true;
		this.lastScene = null;
		this.nextScene = null;
		
		this.lastSceneWindow.destroy();
		lastSceneRectangle.destroy();
	}
	

}

