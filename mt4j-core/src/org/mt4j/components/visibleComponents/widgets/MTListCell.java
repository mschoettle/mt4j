package org.mt4j.components.visibleComponents.widgets;


import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.MTColor;

import processing.core.PApplet;

/**
 * The Class MTListCell.
 */
public class MTListCell 
//extends MTRectangle{
extends MTClipRectangle{

	 
	 
	 /**
 	 * Instantiates a new mT list cell.
 	 *
 	 * @param width the width
 	 * @param height the height
 	 * @param applet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
 	 */
 	public MTListCell(float width, float height, PApplet applet) {
		 this(applet, width, height);
	 }

	/**
	 * Instantiates a new mT list cell.
	 *
	 * @param applet the applet
	 * @param width the width
	 * @param height the height
	 */
	public MTListCell(PApplet applet, float width, float height) {
		super(applet, 0, 0, 0, width, height);
		this.setStrokeColor(new MTColor(0,0,0));
		this.setComposite(true);
		
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setDefaultGestureActions()
	 */
	@Override
	protected void setDefaultGestureActions() {
		this.registerInputProcessor(new DragProcessor(getRenderer()));
//		this.addGestureListener(DragProcessor.class, new DefaultDragAction());
	}
}
