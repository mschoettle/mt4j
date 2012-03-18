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

/**
 * The Class MultiPurposeInterpolator.
 * 
 * <br><br>
 * Interpolator class, used to get interpolated values between
 * start and the destination values
 * 
 * @author Christopher Ruff
 */
public class MultiPurposeInterpolator implements Iinterpolator { 
	
	/** The real from. */
	private float
		normalizedFrom,
		normalizedValue,
		velocity,
		v0,
		normalizedLastStepDelta,
		normalizedTarget,
		normalizedRemainingTime,
//		normalizedTotalTime,
		realTarget,
		realFrom;
		
	/** The normalized tfactor. */
	private float normalizedTfactor;
	
	/** The normalized dfactor. */
	private float normalizedDfactor;

	/** The t. */
	private float t;
	
	/** The t1. */
	private float t1;
	
	/** The t2. */
	private float t2;
		
	/** The time taken. */
	private long
		startTime,
		timeTaken;
	
	/** The original loop count. */
	private int loopCount, originalLoopCount;
	
	/** The debug. */
	private boolean debug;
	
	/** The alternating. */
	private boolean alternating;
	
	/** The alternate factor. */
	private int alternateFactor;
	
	/**
	 * Initializes a new Interpolator object, used to get interpolated values between
	 * the start and the destination.
	 * 
	 * @param from the value to start the interpolation from
	 * @param to the value to interpolate to
	 * @param interpolationDuration the duration of interpolation
	 * @param accelerationEndTime defines the normalized time until when the Easing IN takes place (normalized, value from 0..1) (i.e. 0.25f)
	 * @param decelerationStartTime defines the time from when the Easing OUT takes place (normalized, value from 0..1) (i.e. 0.75f)
	 * @param loopCount how often to loop the interpolation, Value of "-1" loops infinitely
	 */
	public MultiPurposeInterpolator(float from, float to, float interpolationDuration, float accelerationEndTime, float decelerationStartTime, int loopCount){ 
		if (interpolationDuration <=0) {
			throw new RuntimeException("You have to specify a time value greater than 0ms");
		} else if (loopCount == 0) {
			throw new RuntimeException("You have to specify a loopCount value that is not '0'");
		} else if (this.t1 > this.t2) {
			throw new RuntimeException("Value of t1 has to be smaller or equal to the value of t2");
		} else if (this.t1 > 1 || this.t1 < 0 || this.t2 > 1 || this.t2 < 0) {
			throw new RuntimeException("Values of t1 and t2 have to be: 0.0 < t1,t2 < 1.0");
		}
		
		this.debug = false;
		
		this.normalizedTfactor = 1/interpolationDuration;
		this.normalizedDfactor = 1/(to-from);
		
		if (this.debug){
			System.out.println("Normalized TotalTime Factor: " + this.normalizedTfactor );
			System.out.println("Normalized Total Distance Factor: " + this.normalizedDfactor );
		}
		
		this.alternating = false;
		
		this.alternateFactor = -1;
		
		//these are also normalized values
		//Defines the time until when the Easing IN takes place (normalized, value from 0..1);
		this.t1 						= accelerationEndTime;
		//Defines the time from when the Easing OUT takes place (normalized, value from 0..1);
		this.t2 						= decelerationStartTime;
		//Marks the normalized time we are at form 0..1, of the begining to the finish of interpolation
		this.t  						= 0;
		
		this.normalizedFrom 			= 0;
		this.normalizedTarget 			= 1;
		this.realFrom 					= from;
		this.realTarget 				= to;
		
//		this.normalizedTotalTime 		= 1;
		this.loopCount					= loopCount;
		this.originalLoopCount			= loopCount;
		
		this.normalizedRemainingTime	= 1;
		this.normalizedValue 			= 0;
		
		this.timeTaken 					= 0;
		this.startTime 					= 0;
		
		/*
		 * calculate the maximum velocity at the middle part of the velocity/time curve
		 */
		this.v0 				= 2 / (1 + this.t2 - this.t1); //jedes mal hier ausrechnen oder in construktor?
		
		this.resetForNextLoop();
	}
	
	/**
	 * resets the interpolator for the next loop.
	 */
	private void resetForNextLoop(){
		/*
		 * reset values
		 */
		this.alternateFactor			*=	-1;
		
		this.startTime 					= System.currentTimeMillis(); 
		
		this.t  						= 0;
		this.normalizedRemainingTime	= 1;
		
		this.normalizedLastStepDelta 	= 0.0f;
		this.normalizedValue 			= this.normalizedFrom;
		
		this.velocity 					= 0;
	}
	
	
	/* (non-Javadoc)
	 * @see util.animation.Iinterpolator#resetInterpolator()
	 */
	public void resetInterpolator(){
		/*
		 * reset values
		 */
		this.startTime 					= System.currentTimeMillis(); 
		
		this.alternateFactor 			= 1;
		
		this.t  						= 0;
		this.normalizedRemainingTime	= 1;
		
		this.normalizedLastStepDelta 	= 0.0f;
		this.normalizedValue 			= this.normalizedFrom;
		
		this.timeTaken 					= 0;
		
		this.v0 						= 2 / (1 + this.t2 - this.t1); 
		
		this.loopCount 					= this.originalLoopCount;
		
		this.velocity 					= 0;
	}
	
	/**
	 * Does the next interpolation step, taking the timeDelta into account
	 * <p>
	 * This implementation makes sure the destination is reached exactly, and sets the value
	 * to the target value if the next interpolation step would surpass the target value, or if the
	 * time is up.
	 * 
	 * @param deltaTime amount of time to interpolate
	 * 
	 * @return true, if interpolate
	 * 
	 * <b>false</b> if this call to interpolate() would lead to a higher value than the desired
	 * target value or the time is up, <b>true</b> if the target isnt reached in this interpolation step
	 * and there is still remaining time
	 */
	public boolean interpolate(float deltaTime) {
		/*
		 * Check if the interpolation has finished 
		 * -> restart again if there are loops left and the target was reached from previous interpolation
		 * -> do nothing if there are no more loops 
		*/
		if (this.isTargetReached() && (this.loopCount == -1 || this.loopCount > 0 )){
			if(this.debug){
				System.out.println("Target reached or infinitely looped, or still more loops to go -> resetting the values before interpolating again");
			}
			this.resetForNextLoop();
		}else if (this.isTargetReached() && this.loopCount == 0){
			return false;
		}
		
		/*
		 * calculate normalized timeDelta and add it to our current time t,
		 * calculate the remaining time
		 */
		float normalizeDeltaTime = (deltaTime * this.normalizedTfactor);
		
		this.t += normalizeDeltaTime; //map timeDelta to 0..1
		this.normalizedRemainingTime -= normalizeDeltaTime;
		
		if(this.debug){
			System.out.println("Normalized deltatime: " + normalizeDeltaTime);
			System.out.println("T: " + this.t + " Remaining: " + this.normalizedRemainingTime);
		}
		
		
		/*
		 * calucalte velocity
		 */
		if (this.t < this.t1){ //anfang, beschleunigung
//			d = velocity *t*t/(2*t1); 
			this.velocity = this.v0 * (this.t / this.t1);
		} 
		else{ 
//			d = velocity * (t1/2); 
			if (this.t < this.t2){ //mitte constante geschwindigkeit  
//				d += (t-t1)*velocity; 
				this.velocity = this.v0;
			} 
			else{ //letztes stück geschwindigkeit linear abnehmen lassen
//				d += (t2-t1)*velocity;
//				d += (t-t*t/2-t2+t2*b/2) * velocity/(1-t2); 
				this.velocity = this.v0 * (1 - (this.t - this.t2) / (1 - this.t2) );
				
				/*
				if (t2 == 1 || t == 1){ //FIXME added to avoid infinity velocity when t==t2
					this.velocity = 0;
				}
				*/
			} 
		} 
		
		if (Float.isInfinite(velocity) 
			|| Float.isNaN(velocity)){
//			System.out.println("Velocity: " + velocity);
			this.velocity = 0;
		}
		
		/*
		 * calculate the normalized step and overall value by multiplying
		 * the velocity with the normalized deltaTime
		 */
		float normalizedTmpStepDelta	= (this.velocity * normalizeDeltaTime);
		float normalizedTmpValue 		= this.normalizedValue + normalizedTmpStepDelta;
		
		if(this.debug){
			float tmpRealValue = normalizedTmpValue *(this.realTarget - this.realFrom); 
			System.out.println("Velocity: " + this.velocity);
			System.out.println("Normalized TMP Value: " + normalizedTmpValue);
			System.out.println("-> current value: " + tmpRealValue);
		}
		
		this.timeTaken = System.currentTimeMillis() - this.startTime;
		
		/*
		 * Checks if the target would be exceeded by this step, or if the 
		 * time is up - if so, sets the value to match the target so we 
		 * always end up at the desired target value 
		 * -> one loop is complete
		 */
		// Checken ob der berechnete wert über den target wert hinausschiesst -> loop beenden
		if (normalizedTmpValue >= this.normalizedTarget){ 
			this.normalizedLastStepDelta 	= this.normalizedTarget - this.normalizedValue;
			this.normalizedValue 			= this.normalizedTarget;
			
			/*
			if (Float.isInfinite(normalizedLastStepDelta)
					|| Float.isNaN(normalizedLastStepDelta)
				){
					System.out.println("Stepdelta malformed! trying to correct..");
					System.out.println("Velocity: " + velocity);
					System.out.println();
					float normStepDelta = (this.normalizedTarget - this.normalizedValue);
					this.normalizedLastStepDelta = normStepDelta;
				}
			*/
			
			if (this.loopCount != -1) {
				this.loopCount--;
			}
			
			if(this.debug) {
				System.out.println("Interpolation duration: " + this.timeTaken);
			}
			return false;
		}
		// Checken ob die Zeit, die die animation hatte abgelaufen ist -> loop benden
		else if (this.normalizedRemainingTime <= 0){
			this.normalizedLastStepDelta 	= this.normalizedTarget - this.normalizedValue;
			this.normalizedValue 			= this.normalizedTarget;
			
			
			/*
			// FIXME
			// WHAT THE F*CK !?? WARUM KOMMT MANCHMAL NAN ODER INFINITE RAUS 
			// BEIM RECHNEN VON 1.0 - 1.0 und speichern in normalizedLastStepDelta
			// BEIM NEUEN BERECHNEN STIMMT DER WERT DANN??
			if (Float.isInfinite(normalizedLastStepDelta)
				|| Float.isNaN(normalizedLastStepDelta)
			){
				System.out.println("Stepdelta malformed! trying to correct..");
				System.out.println("Velocity: " + velocity);
				System.out.println();
				float normStepDelta = (this.normalizedTarget - this.normalizedValue);
				this.normalizedLastStepDelta = normStepDelta;
			}
			*/
			
			if (this.loopCount != -1) {
				this.loopCount--;
			}
			
			if(this.debug) {
				System.out.println("Interpolation duration: " + this.timeTaken);
			}
			return false;
		}
		// Just save the values for the next step -> loop nicht beenden
		else{ 
			this.normalizedLastStepDelta = normalizedTmpStepDelta;
			this.normalizedValue 		 = normalizedTmpValue;
			
			/*
			if (Float.isInfinite(normalizedLastStepDelta)
					|| Float.isNaN(normalizedLastStepDelta)
				){
					System.out.println("Stepdelta malformed! trying to correct..");
					System.out.println("Velocity: " + velocity);
					System.out.println();
					float normStepDelta = (this.normalizedTarget - this.normalizedValue);
					this.normalizedLastStepDelta = normStepDelta;
//					this.normalizedLastStepDelta = 0;
//					this.normalizedLastStepDelta = this.normalizedTarget - this.normalizedValue;
				}
				*/
			
			return true;
		}
	}

	/**
	 * checks if the interpolation has reached its target
	 * and there are no more loops left.
	 * 
	 * @return true: if the interpolation is finished, then you shouldnt call interpolate() again,
	 * false: if there are still loops to do, and/or the target hasnt been reached
	 */
	public boolean isFinished(){
		if (this.normalizedTarget != this.normalizedValue || this.loopCount == -1 ) {
			return false;
		} else if (this.normalizedTarget == this.normalizedValue && this.loopCount > 0) {
			return false;
		} else if (this.normalizedTarget == this.normalizedValue && this.loopCount == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if is target reached.
	 * 
	 * @return true, if is target reached
	 */
	private boolean isTargetReached(){
		return this.normalizedValue == this.normalizedTarget;
	}
	
	/**
	 * Gets the time taken.
	 * 
	 * @return the time taken
	 */
	public float getTimeTaken() {
		return this.timeTaken;
	}
	
	
	/**
	 * Checks if is alternating.
	 * 
	 * @return true, if is alternating
	 */
	public boolean isAlternating() {
		return this.alternating;
	}

	/**
	 * Sets the alternating.
	 * 
	 * @param alternating the new alternating
	 */
	public void setAlternating(boolean alternating) {
		this.alternating = alternating;
	}

	/**
	 * Returns the un-normalized value of the current interpolation.
	 * 
	 * @return the current value
	 */
	public float getCurrentValue() { //un-normalize the value to get the real value
		float currentValue = (this.normalizedValue * (this.realTarget - this.realFrom)) + this.realFrom;
		if (this.isAlternating()) {
			return this.alternateFactor * currentValue;
		} else {
			return currentValue;
		}
	}
	
	
	/**
	 * Returns the unnormalized delta value from the last interpolated value to the current,
	 * after a interpolationstep
	 * - useful to determine how much the value increased/decreased.
	 * 
	 * @return the current step delta
	 */
	public float getCurrentStepDelta() {//un-normalize the step to get the real step
		float stepDelta = this.normalizedLastStepDelta * (this.realTarget - this.realFrom);
		
//		if (Float.isNaN(stepDelta)){
//			System.out.println("Stepdelta is NAN! :" + stepDelta );
//		}
//		if (stepDelta > 50 || stepDelta < 0){
//			System.out.println(stepDelta);
//			System.out.println("Stepdelta malformed: " + stepDelta);
//		}
		
		if (this.isAlternating()) {
			return this.alternateFactor * stepDelta;
		} else{ 
			return stepDelta;
		}
	}
	
	
}