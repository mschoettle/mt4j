package org.mt4j.components.css.util;

/**
 * The Interface CSSStylableComponent.
 */
public interface CSSStylableComponent {
	
	/**
	 * Checks if the Component is CSS styled.
	 *
	 * @return true, if is cSS styled
	 */
	public boolean isCSSStyled();
	
	/**
	 * Enables the CSS (if everything is right).
	 */
	public void enableCSS();
	
	/**
	 * Disable the CSS.
	 */
	public void disableCSS();
	
	/**
	 * Appl�es the (global) style sheets.
	 */
	public void applyStyleSheet();
	
	/**
	 * Gets the css helper.
	 *
	 * @return the css helper
	 */
	public CSSHelper getCssHelper();
	
	
	/**
	 * Checks if css is force disabled.
	 *
	 * @return true, if is css force disabled
	 */
	public boolean isCssForceDisabled();
	
	/**
	 * Sets the css force disable.
	 *
	 * @param cssForceDisabled the new css force disable
	 */
	public void setCssForceDisable(boolean cssForceDisabled);
}
