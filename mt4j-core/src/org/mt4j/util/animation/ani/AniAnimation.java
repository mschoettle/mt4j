package org.mt4j.util.animation.ani;

import org.mt4j.AbstractMTApplication;
import org.mt4j.util.animation.AbstractAnimation;
import org.mt4j.util.animation.AnimationEvent;

import de.looksgood.ani.Ani;
import de.looksgood.ani.AniConstants;

public class AniAnimation extends AbstractAnimation /*implements AniConstants*/{
	public float x;
	private AniAdapter ani;
	
	public static final String LINEAR = AniConstants.LINEAR;
	public static final String QUAD_IN = AniConstants.QUAD_IN;
	public static final String QUAD_OUT = AniConstants.QUAD_OUT;
	public static final String QUAD_IN_OUT = AniConstants.QUAD_IN_OUT;
	public static final String CUBIC_IN = AniConstants.CUBIC_IN;
	public static final String CUBIC_IN_OUT = AniConstants.CUBIC_IN_OUT;
	public static final String CUBIC_OUT = AniConstants.CUBIC_OUT;
	public static final String QUART_IN = AniConstants.QUART_IN;
	public static final String QUART_OUT = AniConstants.QUART_OUT;
	public static final String QUART_IN_OUT = AniConstants.QUART_IN_OUT;
	public static final String QUINT_IN = AniConstants.QUINT_IN;
	public static final String QUINT_OUT = AniConstants.QUINT_OUT;
	public static final String QUINT_IN_OUT = AniConstants.QUINT_IN_OUT;
	public static final String SINE_IN = AniConstants.SINE_IN;
	public static final String SINE_OUT = AniConstants.SINE_OUT;
	public static final String SINE_IN_OUT = AniConstants.SINE_IN_OUT;
	public static final String CIRC_IN = AniConstants.CIRC_IN;
	public static final String CIRC_OUT = AniConstants.CIRC_OUT;
	public static final String CIRC_IN_OUT = AniConstants.CIRC_IN_OUT;
	public static final String EXPO_IN = AniConstants.EXPO_IN;
	public static final String EXPO_OUT = AniConstants.EXPO_OUT;
	public static final String EXPO_IN_OUT = AniConstants.EXPO_IN_OUT;
	public static final String BACK_IN = AniConstants.BACK_IN;
	public static final String BACK_OUT = AniConstants.BACK_OUT;
	public static final String BACK_IN_OUT = AniConstants.BACK_IN_OUT;
	public static final String BOUNCE_IN = AniConstants.BOUNCE_IN;
	public static final String BOUNCE_OUT = AniConstants.BOUNCE_OUT;
	public static final String BOUNCE_IN_OUT = AniConstants.BOUNCE_IN_OUT;
	public static final String ELASTIC_IN = AniConstants.ELASTIC_IN;
	public static final String ELASTIC_OUT = AniConstants.ELASTIC_OUT;
	public static final String ELASTIC_IN_OUT = AniConstants.ELASTIC_IN_OUT;
	
	public AniAnimation(float from, float to, int theDuration, Object animationTarget) {
		this(from, to, theDuration, 0, 1, Ani.getDefaultEasing(), animationTarget);
	}
	
	public AniAnimation(float from, float to, int theDuration, int repeatCount, Object animationTarget) {
		this(from, to, theDuration, 0, repeatCount, Ani.getDefaultEasing(), animationTarget);
	}
	
	public AniAnimation(float from, float to, int theDuration, String theEasing, Object animationTarget) {
		this(from, to, theDuration, 0, 1, theEasing, animationTarget);
	}

	public AniAnimation(float from, float to, int theDuration, int repeatCount, String theEasing, Object animationTarget) {
		this(from, to, theDuration, 0, repeatCount, theEasing, animationTarget);
	}
	
	public AniAnimation(float from, float to, int theDuration, int theDelay, int repeatCount, String theEasing, Object animationTarget) {
		super(animationTarget);
//		this.setInterpolator(this);
		this.ani = new AniAdapter(this, from, to, (float)theDuration/1000f, (float)theDelay/1000f, "x", theEasing, animationTarget);
		this.ani.setBegin(from);
		if (repeatCount == -1){
			this.ani.repeat();
		}else{
			this.ani.repeat(repeatCount);	
		}
	}
	
	
	public static void init(AbstractMTApplication pApplet){
		Ani.init(pApplet);
	}
	

	protected AniAdapter getAni(){
		return this.ani;
	}

	
	public void start(){
		this.ani.start();
	}
	
	public void stop(){
		this.ani.end();
	}
	
	public void reverse(){
		this.getAni().reverse();
	}
	
	public void setRepeat(int repeatCount){
		this.getAni().noRepeat();
		if (repeatCount == -1){
			this.getAni().repeat();
		}else{
			this.getAni().repeat(repeatCount);	
		}
	}
	
	public int getRepeat() {
		return this.getAni().getRepeatCount();
	}
	
	
	public void fireAnimationEvent(AnimationEvent ae){
		super.fireAnimationEvent(ae);
	}
	
	public float getDelta() {
		return this.getAni().getCurrentStepDelta();
	}

	public float getValue() {
		return this.getAni().getPosition();
	}
	
	
	public void restart() {
		if (this.getAni().isPlaying())
			this.getAni().end();
		this.getAni().start();
	}


	public void setTriggerTime(long triggerTime) {
		this.getAni().setDelay((float)triggerTime/1000f);
	}

	public long getTriggerTime() {
		return Math.round(this.getAni().getDelay() * 1000);
	}
	
	
	public boolean isFinished() {
		return this.getAni().getPosition() == this.getAni().getEnd();
	}

	

}
