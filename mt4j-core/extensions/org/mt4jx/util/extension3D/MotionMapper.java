package org.mt4jx.util.extension3D;

public interface MotionMapper {

	public void updateCurrentLength(float currentLength);
	
	public float calcCurrentValue();
	
	public void setLengthRange(float min,float max);
			
}
