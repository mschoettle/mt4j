package org.mt4j.util.animation.ani;

import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.AnimationManager;

import de.looksgood.ani.Ani;

public class AniAdapter extends Ani {
	private AniAnimation correspondingAnimation;
	private Object animationTarget;
	private float currentStepDelta;
	
	
	static{
		Ani.noAutostart();
	}
	
//	public AdaptedAni(Adapter theTarget, float from, float to, float theDuration, float theDelay,
//			String theFieldName, String theEasing,
//			String theCallback) {
//		super(theTarget, theDuration, theDelay, "x", to, theEasing,
//				theCallback);
//		
//		this.setBegin(from);
//		this.correspondingAnimation = theTarget;
//	}

	

	public AniAdapter(AniAnimation theTarget, float from, float to, float theDuration, float theDelay, String theFieldName, String theEasing, Object animationTarget) {
		super(theTarget, theDuration, theDelay, theFieldName, to, theEasing);
		this.setBegin(from);
		this.currentStepDelta = 0;
		
		this.correspondingAnimation = theTarget;
		this.animationTarget = animationTarget;
	}

	
	@Override
	public void seek(float theValue) {
		if (theValue == 0.0f){
			this.currentStepDelta = 0.0f;
			this.position = getBegin(); //FIXME TEST to fix bug with currentStepDelta
		}
		super.seek(theValue);
	}
	
	
	@Override
	protected void dispatchOnStart() {
		super.dispatchOnStart();
		correspondingAnimation.fireAnimationEvent(new AnimationEvent(this, AnimationEvent.ANIMATION_STARTED, correspondingAnimation, animationTarget));
	}
	
	
	@Override
	protected void updatePosition() {
		float lastPosition = this.getPosition();
		super.updatePosition();
		this.currentStepDelta = this.getPosition() - lastPosition;
//		if (this.currentStepDelta == -1.0f){
//			System.out.println();
//		}
		correspondingAnimation.fireAnimationEvent(new AnimationEvent(this, AnimationEvent.ANIMATION_UPDATED, correspondingAnimation, animationTarget));
	}
	
	
	
	@Override
	protected void dispatchOnEnd() {
		this.currentStepDelta = 0.0f; //Else we get the same delta as from the last step twice!
		super.dispatchOnEnd();
		correspondingAnimation.fireAnimationEvent(new AnimationEvent(this, AnimationEvent.ANIMATION_ENDED, correspondingAnimation, animationTarget));
	}
	
	
	@Override
	public void start() {
//		this.currentStepDelta = 0;
		AnimationManager.getInstance().registerAnimation(correspondingAnimation);
		super.start();
	}
	
	@Override
	public void end() {
		AnimationManager.getInstance().unregisterAnimation(correspondingAnimation);
		super.end();
//		this.currentStepDelta = 0;
	}
	
	
	public float getCurrentStepDelta(){
		return this.currentStepDelta;
	}
	

	@Override
	public void repeat(int theRepeatCount) {
		super.repeat(theRepeatCount);
	}


	@Override
	public void setCallback(String theCallback) {
		super.setCallback(theCallback);
	}

	@Override
	public void setDelay(float theDurationDelay) {
		super.setDelay(theDurationDelay);
	}

	@Override
	public void setDuration(float theDurationEasing) {
		super.setDuration(theDurationEasing);
	}


	
	
	
	

}
