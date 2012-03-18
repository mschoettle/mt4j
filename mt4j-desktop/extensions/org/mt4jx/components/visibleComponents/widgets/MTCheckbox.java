package org.mt4jx.components.visibleComponents.widgets;

import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;

import processing.core.PApplet;

/**
 * The Class MTCheckbox.
 */
public class MTCheckbox extends MTForm implements BooleanForm{

	/** The boolean value. */
	private boolean booleanValue = false;;	
	
	/** The background color. */
	private MTColor backgroundColor;
	
	/** The stroke color. */
	private MTColor strokeColor;
	
	/**
	 * Instantiates a new MTCheckBox
	 * @param pApplet the applet
	 * @param size the size of the side of the square
	 */
	public MTCheckbox(PApplet pApplet, float size) {
		super(pApplet, 0, 0, size, size, MTForm.BOOLEAN);
		
		this.setCssForceDisable(true);
		
		this.backgroundColor = new MTColor(MTColor.YELLOW);
		this.setFillColor(backgroundColor);
		this.setNoFill(true);
		this.setNoStroke(false);
		this.setStrokeWeight(2f);
		this.style();
	}
	
	
	@Override
	protected void setDefaultGestureActions() {
		this.registerInputProcessor(new TapProcessor(getRenderer()));
		this.addGestureListener(TapProcessor.class, new BooleanTapListener());
	}

	private void style() {
		//Is the Item CSSStyle? If no, take standard colors
		if (this.isCSSStyled()) {
			CSSStyle vss = this.getCssHelper().getVirtualStyleSheet();
			this.setStrokeWeight(vss.getBorderWidth());
			this.setLineStipple(vss.getBorderStylePattern());
			
			if (vss.isModifiedBackgroundColor()) this.backgroundColor = vss.getBackgroundColor();
			else backgroundColor = MTColor.YELLOW;
			if (vss.isModifiedBorderColor()) strokeColor = vss.getBorderColor();
			else strokeColor = MTColor.WHITE;
		} else {
			this.backgroundColor = MTColor.YELLOW;
			this.strokeColor = MTColor.WHITE;
		}
		
		if (!brightEnough(backgroundColor)) {
			this.backgroundColor = MTColor.YELLOW;
		}
		this.setFillColor(backgroundColor);
		this.setStrokeColor(strokeColor);
	}
	
	private boolean brightEnough(MTColor color) {
		return color.getR() + color.getG() + color.getB() > 200 && color.getAlpha() > 200;

	}
	
	@Override
	public void applyStyleSheet() {
		System.out.println("Styling now. CSSID: " + this.getCSSID());
		style();
	}
	
	/* (non-Javadoc)
	 * @see org.mt4jx.components.generic.MTForm#getBooleanValue()
	 */
	@Override
	public boolean getBooleanValue() {
		return booleanValue;
	}

	/* (non-Javadoc)
	 * @see org.mt4jx.components.generic.MTForm#getStringValue()
	 */
	@Override
	public String getStringValue() {
		return String.valueOf(this.getBooleanValue());
	}

	/* (non-Javadoc)
	 * @see org.mt4jx.components.generic.MTForm#getNumericValue()
	 */
	@Override
	public float getNumericValue() {
		if (this.getBooleanValue() == true) return 1;
		else return 0;
	}

	/* (non-Javadoc)
	 * @see org.mt4jx.components.generic.MTForm#setBooleanValue(boolean)
	 */
	@Override
	public void setBooleanValue(boolean value) {
		this.booleanValue = value;
		if (this.booleanValue == true) {
			this.setNoFill(false);
		} else {
			this.setNoFill(true);
		}
	}


}
