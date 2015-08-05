package org.mt4jx.components.visibleComponents.widgets;

import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;

import processing.core.PApplet;

/**
 * The Class MTForm.
 */
public abstract class MTForm extends MTRectangle{
	
	/** The type. */
	private short type = 0;
	
	/** The BOOLEAN. */
	public static short BOOLEAN = 1;
	
	/** The STRING. */
	public static short STRING = 2;
	
	/** The CUSTOM. */
	public static short CUSTOM = 3;
	
	/** The UNDEFINED. */
	public static short UNDEFINED = 0;
	
	/**
	 * Instantiates a new mT form.
	 * @param pApplet the applet
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param type the type (MTForm.BOOLEAN or MTForm.STRING)
	 */
	public MTForm(PApplet pApplet, float x, float y, float width, float height, short type) {
		super(pApplet, x, y, width, height);
		this.type = type;
	}
	
	/**
	 * Gets the boolean value.
	 *
	 * @return the boolean value
	 */
	public abstract boolean getBooleanValue();
	
	/**
	 * Sets the boolean value.
	 *
	 * @param value the new boolean value
	 */
	public abstract void setBooleanValue(boolean value);
	
	/**
	 * Gets the string value.
	 *
	 * @return the string value
	 */
	public abstract String getStringValue();
	
	/**
	 * Gets the numeric value.
	 *
	 * @return the numeric value
	 */
	public abstract float getNumericValue();
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public short getType() {
		return this.type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	protected void setType(short type) {
		this.type = type;
	}
	
	/**
	 * The listener interface for receiving booleanTap events.
	 * The class that is interested in processing a booleanTap
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addBooleanTapListener<code> method. When
	 * the booleanTap event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BooleanTapEvent
	 */
	public class BooleanTapListener implements IGestureEventListener {

		/* (non-Javadoc)
		 * @see org.mt4j.input.inputProcessors.IGestureEventListener#processGestureEvent(org.mt4j.input.inputProcessors.MTGestureEvent)
		 */
		public boolean processGestureEvent(MTGestureEvent ge) {
			if (ge instanceof TapEvent) {
				TapEvent te = (TapEvent)ge;
				if (te.getTapID() == TapEvent.TAPPED) {
					setBooleanValue(!getBooleanValue());
				}
				
				
			}
			return false;
		}
		
	}
	
}
