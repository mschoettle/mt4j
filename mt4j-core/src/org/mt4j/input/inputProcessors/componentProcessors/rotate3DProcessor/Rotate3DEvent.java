package org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.math.Vector3D;

public class Rotate3DEvent extends MTGestureEvent {

	
	/** The first finger motion. */
	private InputCursor firstFingerMotion;
	
	/** The second finger motion. */
	private InputCursor secondFingerMotion;
	
	/** The rotation finger motion */
	 private InputCursor thirdFingerMotion;
	
	/** The rotation point. */
	private Vector3D rotationPoint;
	
	/** The translation vector. */
	private Vector3D translationVector;
		
	private float rotationDegreesX,rotationDegreesY,rotationDegreesZ;
	
	private short rotationDirection;
	
	private Vector3D rotationAxis;
	

	/**
	 * Instantiates a new rotate event.
	 * 
	 * @param source the source
	 * @param id the id
	 * @param targetComponent the target component
	 * @param firstFingerMotion the first finger motion
	 * @param secondFingerMotion the second finger motion
	 * @param translationVector the translation vector
	 * @param rotationPoint the rotation point
	 * @param rotationDegrees the rotation degrees
	 */
	public Rotate3DEvent(IInputProcessor source, int id, IMTComponent3D targetComponent, InputCursor firstFingerMotion, InputCursor secondFingerMotion, InputCursor thirdFingerMotion, Vector3D translationVector, Vector3D rotationPoint,short rotationDirection, float rotationDegreesX,float rotationDegreesY,float rotationDegreesZ,Vector3D rotationAxis) {
		super(source, id, targetComponent);
		this.firstFingerMotion = firstFingerMotion;
		this.secondFingerMotion = secondFingerMotion;
		this.thirdFingerMotion = thirdFingerMotion;
		this.translationVector = translationVector;
		this.rotationPoint = rotationPoint;
		this.setRotationDegreesX(rotationDegreesX);
		this.rotationDegreesY = rotationDegreesY;
		this.setRotationDegreesZ(rotationDegreesZ);
		this.setRotationDirection(rotationDirection);
		this.setRotationAxis(rotationAxis);
	}

	/**
	 * Gets the first finger motion.
	 * 
	 * @return the first finger motion
	 */
	public InputCursor getFirstCursor() {
		return firstFingerMotion;
	}

	/**
	 * Gets the rotation point.
	 * 
	 * @return the rotation point
	 */
	public Vector3D getRotationPoint() {
		return rotationPoint;
	}

	/**
	 * Gets the second finger motion.
	 * 
	 * @return the second finger motion
	 */
	public InputCursor getSecondCursor() {
		return secondFingerMotion;
	}

	/**
	 * Gets the translation vector.
	 * 
	 * @return the translation vector
	 */
	public Vector3D getTranslationVector() {
		return translationVector;
	}

	

	/**
	 * Sets the first finger motion.
	 * 
	 * @param firstFingerMotion the new first finger motion
	 */
	public void setFirstCursor(InputCursor firstFingerMotion) {
		this.firstFingerMotion = firstFingerMotion;
	}



	/**
	 * Sets the rotation point.
	 * 
	 * @param rotationPoint the new rotation point
	 */
	public void setRotationPoint(Vector3D rotationPoint) {
		this.rotationPoint = rotationPoint;
	}

	/**
	 * Sets the second finger motion.
	 * 
	 * @param secondFingerMotion the new second finger motion
	 */
	public void setSecondCursor(InputCursor secondFingerMotion) {
		this.secondFingerMotion = secondFingerMotion;
	}

	/**
	 * Sets the translation vector.
	 * 
	 * @param translationVector the new translation vector
	 */
	public void setTranslationVector(Vector3D translationVector) {
		this.translationVector = translationVector;
	}

	public void setRotationDegreesY(float rotationDegreesY) {
		this.rotationDegreesY = rotationDegreesY;
	}

	public float getRotationDegreesY() {
		return rotationDegreesY;
	}

	public void setRotationDegreesZ(float rotationDegreesZ) {
		this.rotationDegreesZ = rotationDegreesZ;
	}

	public float getRotationDegreesZ() {
		return rotationDegreesZ;
	}

	public void setRotationDegreesX(float rotationDegreesX) {
		this.rotationDegreesX = rotationDegreesX;
	}

	public float getRotationDegreesX() {
		return rotationDegreesX;
	}

	public void setRotationDirection(short rotationDirection) {
		this.rotationDirection = rotationDirection;
	}

	public short getRotationDirection() {
		return rotationDirection;
	}

	public void setRotationAxis(Vector3D rotationAxis) {
		this.rotationAxis = rotationAxis;
	}

	public Vector3D getRotationAxis() {
		return rotationAxis;
	}
	
	
	
}
