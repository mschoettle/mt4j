package org.mt4j.components.visibleComponents.shapes;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.css.util.CSSHelper;
import org.mt4j.components.css.util.CSSStylableComponent;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

/**
 * The Class MTCSSStylableShape.
 */
public abstract class MTCSSStylableShape extends AbstractShape implements CSSStylableComponent {
	
	/** The mt app. */
	private AbstractMTApplication mtApp;
	
	/** The css styled. */
	private boolean cssStyled = false;
	
	/** The css force disabled. */
	private boolean cssForceDisabled = false;
	
	/** The css helper. */
	private CSSHelper cssHelper;
	
	
	
	/**
	 * Instantiates a new mTCSS stylable shape.
	 * @param pApplet the applet
	 * @param vertices the vertices
	 */
	public MTCSSStylableShape(PApplet pApplet, Vertex[] vertices) {
		this(pApplet, new GeometryInfo(pApplet, vertices));
	}

	
	
	/**
	 * Instantiates a new mTCSS stylable shape.
	 * @param pApplet the applet
	 * @param geometryInfo the geometry info
	 */
	public MTCSSStylableShape(PApplet pApplet, GeometryInfo geometryInfo) {
		super(pApplet, geometryInfo);
		
		if (pApplet instanceof AbstractMTApplication) {
			this.mtApp = (AbstractMTApplication)pApplet;
			this.cssHelper = new CSSHelper(this, mtApp);
			if (this.mtApp.getCssStyleManager().isGloballyEnabled()) {
				this.enableCSS();
			}
		}
	}


	
	/* (non-Javadoc)
	 * @see org.mt4j.components.css.util.CSSStylableComponent#getCssHelper()
	 */
	public CSSHelper getCssHelper() {
		return cssHelper;
	}


	/* (non-Javadoc)
	 * @see org.mt4j.components.css.util.CSSStylableComponent#enableCSS()
	 */
	public void enableCSS() {
		if (mtApp != null && cssHelper != null) {
			cssStyled = true;
		}
		applyStyleSheet();
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.css.util.CSSStylableComponent#disableCSS()
	 */
	public void disableCSS() {
		cssStyled = false;
	}
		
	/* (non-Javadoc)
	 * @see org.mt4j.components.css.util.CSSStylableComponent#isCSSStyled()
	 */
	public boolean isCSSStyled() {
		return cssStyled;
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.components.css.util.CSSStylableComponent#isCssForceDisabled()
	 */
	public boolean isCssForceDisabled() {
		return cssForceDisabled;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.css.util.CSSStylableComponent#setCssForceDisable(boolean)
	 */
	public void setCssForceDisable(boolean cssForceDisabled) {
		this.cssForceDisabled = cssForceDisabled;
	}


	
	/* (non-Javadoc)
	 * @see org.mt4j.components.css.util.CSSStylableComponent#applyStyleSheet()
	 */
	public void applyStyleSheet(){
		if (this.isCSSStyled() && mtApp != null && this.getCssHelper() != null) {

			if (!isCssForceDisabled() && ((isCSSStyled() && !mtApp.getCssStyleManager().isGloballyDisabled()) || mtApp.getCssStyleManager().isGloballyEnabled())) {
				CSSStyle virtualStyleSheet = cssHelper.getVirtualStyleSheet(); //remember that this re-evaluates() and takes some time -> slow? 

				applyStyleSheetBasic(virtualStyleSheet);

				applyStyleSheetCustom(virtualStyleSheet);

				//Apply childrens styles
				for (MTComponent d : this.getChildren()) {
					if (d instanceof CSSStylableComponent) {
						CSSStylableComponent s = (CSSStylableComponent) d;
						s.applyStyleSheet();
					}
				}
			}

		}
	}

	/**
	 * Apply basic style sheet properties, applicable to all objects.
	 *
	 * @param virtualStyleSheet the virtual style sheet
	 */
	private void applyStyleSheetBasic(CSSStyle virtualStyleSheet) {
		if (virtualStyleSheet.isModifiedBackgroundColor())
			this.setFillColor(virtualStyleSheet.getBackgroundColor());
		if (virtualStyleSheet.isModifiedBorderColor())
			this.setStrokeColor(virtualStyleSheet.getBorderColor());
		if (virtualStyleSheet.isModifiedBorderWidth())
			this.setStrokeWeight(virtualStyleSheet.getBorderWidth());
		if (virtualStyleSheet.isModifiedVisibility())
			this.setVisible(virtualStyleSheet.isVisibility());
		
		if (virtualStyleSheet.isModifiedBorderStyle()) {
			if (virtualStyleSheet.getBorderStylePattern() >= 0) {
				this.setNoStroke(false);
				this.setLineStipple(virtualStyleSheet.getBorderStylePattern());
			} else {
				this.setNoStroke(true);
			}
		}
	}

	/**
	 * Apply style sheet custom.
	 *
	 * @param virtualStyleSheet the virtual style sheet
	 */
	abstract protected void applyStyleSheetCustom(CSSStyle virtualStyleSheet);


}
