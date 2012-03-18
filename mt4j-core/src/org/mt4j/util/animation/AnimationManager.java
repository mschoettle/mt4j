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
package org.mt4j.util.animation;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The Class AnimationManager.
 * @author Christopher Ruff
 */
public class AnimationManager {
	
	/** The animations. */
	private ArrayList<IAnimation> animations;
	
	/** The instance. */
	private static AnimationManager instance = new AnimationManager();
	
	/** The animation mgr listener. */
	private ArrayList<IAnimationManagerListener> animationMgrListener;
	
	/**
	 * Instantiates a new animation manager.
	 */
	private AnimationManager(){
		animations = new ArrayList<IAnimation>();
		animationMgrListener = new ArrayList<IAnimationManagerListener>();
		
		animUpdateEvt = new AnimationUpdateEvent(this, 0);
	}
	
	/**
	 * Gets the single instance of AnimationManager.
	 * 
	 * @return single instance of AnimationManager
	 */
	static public AnimationManager getInstance(){
//		if (instance == null){
//			instance = new AnimationManager();
//			return instance;
//		}
//		else
			return instance;
	}
	
	
	/** The anim update evt. */
	private AnimationUpdateEvent animUpdateEvt;
	
	/**
	 * Update.
	 * 
	 * @param timeDelta the time delta
	 */
	public void update(long timeDelta){
//		AnimationUpdateEvent ev = new AnimationUpdateEvent(this, timeDelta);
		
		//INFO: animUpdatEvt is recycled everytime, so that no new object must be
		//allocated each frame! => the creation timestampt is wrong
		animUpdateEvt.setDeltaTime(timeDelta);
		fireAnimationUpdateEvent(animUpdateEvt);
	}
	
	
	/**
	 * Adds the animation.
	 * 
	 * @param a the a
	 */
	public synchronized  void registerAnimation(IAnimation a){
		if (!this.contains(a))
			animations.add(a);
	}

	/**
	 * Removes the animation.
	 * 
	 * @param a the a
	 */
	public  synchronized void unregisterAnimation(IAnimation a){
		if (animations.contains(a))
			animations.remove(a);
	}

	/**
	 * Clear.
	 */
	public void clear() {
		Iterator<IAnimation> i = animations.iterator();
		while (i.hasNext()) {
			IAnimation a = (IAnimation)i.next();
//			a.stop();
            if (a instanceof IAnimationManagerListener) {
                IAnimationManagerListener ial = (IAnimationManagerListener) a;
                removeAnimationManagerListener(ial);
            }
            a.stop();
        }
		animations.clear();
	}
	
	/**
	 * Gets the animations for target.
	 * 
	 * @param target the target
	 * 
	 * @return the animations for target
	 */
	public IAnimation[] getAnimationsForTarget(Object target){
		Iterator<IAnimation> i = animations.iterator();
		ArrayList<IAnimation> animations = new ArrayList<IAnimation>();
		while (i.hasNext()) {
			IAnimation a = i.next();
			if (a.getTarget().equals(target)){
				animations.add(a);
			}
		}
		return (animations.toArray(new IAnimation[animations.size()]));
	}

	/**
	 * Contains.
	 * 
	 * @param arg0 the arg0
	 * 
	 * @return true, if successful
	 */
	public boolean contains(IAnimation arg0) {
		return animations.contains(arg0);
	}

	/**
	 * Size.
	 * 
	 * @return the int
	 */
	public int size() {
		return animations.size();
	}
	
	
	/**
	 * Fire animation update event.
	 * 
	 * @param up the up
	 */
	private synchronized void fireAnimationUpdateEvent(AnimationUpdateEvent up) {
		//		synchronized(animationMgrListener) {
//		for (IAnimationManagerListener listener : animationMgrListener) {

						for (int i = 0; i < animationMgrListener.size(); i++) {
							IAnimationManagerListener listener = (IAnimationManagerListener)animationMgrListener.get(i);
			listener.updateAnimation(up);
		}
	}


	/**
	 * Adds the animation manager listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void addAnimationManagerListener(IAnimationManagerListener listener){
		if (!animationMgrListener.contains(listener)){
			animationMgrListener.add(listener);
		}
		
	}
	
	/**
	 * Removes the animation manager listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void removeAnimationManagerListener(IAnimationManagerListener listener){
		if (animationMgrListener.contains(listener)){
			animationMgrListener.remove(listener);
		}
	}
	
	/**
	 * Removes the all animation listeners.
	 */
	public synchronized void removeAllAnimationListeners(){
		animationMgrListener.clear();
	}
	
	/**
	 * Gets the animation manager listeners.
	 * 
	 * @return the animation manager listeners
	 */
	public synchronized IAnimationManagerListener[] getAnimationManagerListeners(){
		return animationMgrListener.toArray(new IAnimationManagerListener[this.animationMgrListener.size()]);
	}
	
}
