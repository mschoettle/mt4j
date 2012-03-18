package org.mt4jx.components.visibleComponents.widgets;

import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * The Class MTOptionBox.
 */
public class MTOptionBox extends MTForm implements BooleanForm {
	
	/** The boolean value. */
	private boolean booleanValue = false;
	
//	/** The background color. */
//	private MTColor backgroundColor;
//	
//	/** The stroke color. */
//	private MTColor strokeColor;
	
	/** The option box. */
	private MTEllipse optionBox;
	
	/** The group. */
	private OptionGroup group;
	
	/**
	 * Instantiates a new MTOptionBox
	 * @param app the PApplet
	 * @param size the size of the ellipse
	 * @param group the OptionGroup
	 */
	public MTOptionBox(PApplet app,
			float size, OptionGroup group) {
		super(app, 0, 0, size, size, MTForm.BOOLEAN);
		group.addOptionBox(this);
		this.setCssForceDisable(true);
		this.setNoStroke(true);
		this.setNoFill(true);
		this.group = group;
		
		optionBox = new MTEllipse(app, new Vector3D(size/2f,size/2f), size/2f, size/2f);
		optionBox.setCssForceDisable(true);
		this.addChild(optionBox);
		
		
		this.style();
		
		optionBox.setPickable(false);
		optionBox.setNoFill(true);
		
		this.setGestureAllowance(TapProcessor.class, true);
		this.registerInputProcessor(new TapProcessor(app));
		this.addGestureListener(TapProcessor.class, new BooleanTapListener());
		
		this.setGestureAllowance(DragProcessor.class, false);
		this.setGestureAllowance(ScaleProcessor.class, false);
		this.setGestureAllowance(ZoomProcessor.class, false);
		this.setGestureAllowance(RotateProcessor.class, false);
		
	}

	private void style() {
		
		//Check if it's CSS styled
		if (this.isCSSStyled() && optionBox != null && this.getCssHelper() != null) {
			
			CSSStyle vss = this.getCssHelper().getVirtualStyleSheet();
			
			this.setStrokeWeight(vss.getBorderWidth());
			this.setLineStipple(vss.getBorderStylePattern());
			
			
			if (vss.isModifiedBorderColor()) optionBox.setStrokeColor(vss.getBorderColor());
			else optionBox.setStrokeColor(MTColor.WHITE);
			
			if (vss.isModifiedBackgroundColor() && brightEnough(vss.getBackgroundColor())) {
				if (vss.getBackgroundColor().getAlpha() < 220) {
					MTColor color = vss.getBackgroundColor().getCopy();
					color.setAlpha(220);
					optionBox.setFillColor(color);
				} else optionBox.setFillColor(vss.getBackgroundColor());
			}
			else optionBox.setFillColor(MTColor.YELLOW);
			
			if (vss.isModifiedBorderWidth()) optionBox.setStrokeWeight(vss.getBorderWidth());
			else optionBox.setStrokeWeight(2f);
			
			
		} else if (optionBox != null){
			//Else set default values
			optionBox.setStrokeColor(MTColor.WHITE);
			optionBox.setFillColor(MTColor.YELLOW);
			optionBox.setStrokeWeight(2f);
		}
	}
	
	private boolean brightEnough(MTColor color) {
		return color.getR() + color.getG() + color.getB() > 200 && color.getAlpha() > 200;
		
		
		
	}
	
	@Override
	public void applyStyleSheet() {
		super.applyStyleSheet();
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
			optionBox.setNoFill(false);
			if (group != null)
			group.setEnabled(this);
		} else {
			optionBox.setNoFill(true);
		}
		
	}
	
	/**
	 * Disable.
	 */
	public void disable() {
		this.booleanValue = false;
		optionBox.setNoFill(true);
	}

}
