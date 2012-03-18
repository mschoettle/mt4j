package org.mt4jx.util.extension3D;



public class VelocityMotionMapper implements MotionMapper {

	private float velocity = 0.0f;
	private long timeStart=0,timeStop=0;
	private long timeFirstStart = 0;
	private float lengthStart=0.0f,lengthStop=0.0f;
	private static int counter = 0;
	private float currentLength = 0.0f;
	private int velocityFactor = 0;
	
	public VelocityMotionMapper(int velocityFactor)
	{
		this.velocityFactor = velocityFactor;
	}
	
	public float calcCurrentValue() {
		
		long currentTime = System.currentTimeMillis();
		
		return velocity*velocityFactor*(currentTime-timeFirstStart);
		
	}

	public void setLengthRange(float min, float max) {
				
	}

	public void updateCurrentLength(float currentLength) {
		
		//if velocity isnt calculated til now, take the values for start and end
		//v = delta x / delta t
		if(timeStart==0)
		{
			timeStart = System.currentTimeMillis();
			if(timeFirstStart==0)
			{
				timeFirstStart = timeStart;
			}
			lengthStart = currentLength;
		}else
		{
			timeStop = System.currentTimeMillis();
			if(timeStop!=timeStart)
			{
				lengthStop = currentLength;
				calcVelocity();		
			}				
		}
		
		this.currentLength = currentLength;
		
	}
	
	private void reset()
	{
		timeStart = 0;
		timeStop = 0;
		lengthStart = 0;
		lengthStop = 0;
	}
	
	private void calcVelocity()
	{
		velocity = (lengthStop - lengthStart)/(timeStop-timeStart);		
	}

}
