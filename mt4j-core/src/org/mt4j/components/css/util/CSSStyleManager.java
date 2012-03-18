package org.mt4j.components.css.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.css.parser.CSSHandler;
import org.mt4j.components.css.parser.CSSParserConnection;
import org.mt4j.components.css.style.CSSSelector;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.css.style.CSSStyleHierarchy;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;

/**
 * The Class CSSStyleManager.
 */
public class CSSStyleManager {
	
	/** The components (of registered MTComponents). */
	private List<CSSStylableComponent> components = new ArrayList<CSSStylableComponent>();

	/** The MTApplication. */
	private AbstractMTApplication app = null;

	/** The styles. */
	private List<CSSStyleHierarchy> styles = new ArrayList<CSSStyleHierarchy>();

	/** CSS Styles globally enabled. */
	private boolean globallyEnabled = false;

	/** CSS Styles globally disabled. */
	private boolean globallyDisabled = false;

	/** The default font. */
	private IFont defaultFont = null;



	/**
	 * Instantiates a new (empty) CSS style manager.
	 *
	 * @param app the MTApplication
	 */
	public CSSStyleManager(AbstractMTApplication app) {
		this.app = app;
	}


	/**
	 * Instantiates a new CSS style manager.
	 *
	 * @param styles the CSSStyles
	 * @param app the MTApplication
	 */
	public CSSStyleManager(List<CSSStyle> styles, AbstractMTApplication app) {
		for (CSSStyle s: styles) {
			this.styles.add(new CSSStyleHierarchy(s));
		}
		this.app = app;
	}
	
		
	/**
	 * Register component.
	 *
	 * @param c the c
	 */
	public void registerComponent(CSSStylableComponent c) {
		components.add(c);
	}


	/**
	 * Load styles from file.
	 *
	 * @param uri the uri of the file
	 */
	public void loadStyles(String uri) {
		CSSParserConnection pc = new CSSParserConnection(uri, app);
		CSSHandler handler = pc.getCssh();
		if (handler != null) {
			List<CSSStyle> newStyles = handler.getStyles();
			for (CSSStyle s: newStyles) {
				this.styles.add(new CSSStyleHierarchy(s));
			}
		}
	}
	
	/**
	 * Load styles from InputStream.
	 *
	 * @param input the input
	 */
	public void loadStyles(InputStream input) {
		CSSParserConnection pc = new CSSParserConnection(input, app);
		CSSHandler handler = pc.getCssh();
		
		if (handler != null) {
			List<CSSStyle> newStyles = handler.getStyles();
			for (CSSStyle s: newStyles) {
				this.styles.add(new CSSStyleHierarchy(s));
			}
		}
	}
	
	/**
	 * Load styles from src folder.
	 * css source file has to be located in /src/data/css/ folder
	 *
	 * @param uri the uri
	 */
	public void loadStylesFromSrcFolder(String uri) {
		this.loadStyles(Thread.currentThread().getContextClassLoader().getResourceAsStream("data/css/" + uri));
	}
	
	
	/**
	 * Load styles and override selectors.
	 *
	 * @param uri the uri
	 * @param selector the selector
	 */
	public void loadStylesAndOverrideSelector(String uri, CSSSelector selector) {
		CSSParserConnection pc = new CSSParserConnection(uri, app);
		CSSHandler handler = pc.getCssh();
		if (handler != null) {
			List<CSSStyle> newStyles = handler.getStyles();
			for (CSSStyle s: newStyles) {
				s.setSelector(selector);
				this.styles.add(new CSSStyleHierarchy(s));
			}
		}
	}
	
	/**
	 * Clear all styles.
	 */
	public void clearStyles() {
		this.styles.clear();
		applyStyles();
	}
	
	/**
	 * Checks if is globally enabled.
	 *
	 * @return true, if CSS Styles is globally enabled
	 */
	public boolean isGloballyEnabled() {
		return globallyEnabled;
	}


	/**
	 * Sets the CSS Styles globally enabled.
	 *
	 * @param globallyEnabled the new globally enabled
	 */
	public void setGloballyEnabled(boolean globallyEnabled) {
		this.globallyEnabled = globallyEnabled;
		if (globallyEnabled) this.globallyDisabled = false;
		applyStyles();
	}


	/**
	 * Checks if CSS Styles is globally disabled.
	 *
	 * @return true, if is globally disabled
	 */
	public boolean isGloballyDisabled() {
		return globallyDisabled;
	}


	/**
	 * Sets the CSS Styles globally disabled.
	 *
	 * @param globallyDisabled the new globally disabled
	 */
	public void setGloballyDisabled(boolean globallyDisabled) {
		this.globallyDisabled = globallyDisabled;
		if (globallyDisabled) this.globallyEnabled = false;
		applyStyles();
	}


	/**
	 * Gets the styles.
	 *
	 * @return the styles
	 */
	public List<CSSStyleHierarchy> getStyles() {
		return styles;
	}

	/**
	 * Sets the styles.
	 *
	 * @param styles the new styles
	 */
	public void setStyles(List<CSSStyleHierarchy> styles) {
		this.styles = styles;
		applyStyles();
	}
	
	/**
	 * Adds a style.
	 *
	 * @param style the style
	 */
	public void addStyle(CSSStyle style) {
		this.styles.add(new CSSStyleHierarchy(style));
		applyStyles();
	}
	
	/**
	 * Adds a style with a certain priority.
	 *
	 * @param style the style
	 * @param priority the priority
	 */
	public void addStyle(CSSStyle style, int priority) {
		this.styles.add(new CSSStyleHierarchy(style, priority));
		applyStyles();
	}
	
	/**
	 * Removes a style.
	 *
	 * @param style the style
	 */
	public void removeStyle(CSSStyle style) {
		this.styles.remove(style);
		applyStyles();
	}
	
	/**
	 * Applies the global style sheets on all registered components.
	 */
	public void applyStyles() {
		List<CSSStylableComponent> toDelete = new ArrayList<CSSStylableComponent>();
		for (CSSStylableComponent c: components) {
			if (c != null) {
				if (!this.isGloballyDisabled()) c.applyStyleSheet();
			} else {
				toDelete.add(c);
			}
		}
		components.removeAll(toDelete);
	}
	

	
	/**
	 * Gets the first style which contains a specific selector.
	 *
	 * @param s the Selector
	 * @return the first style which contains the selector
	 */
	public CSSStyle getFirstStyleForSelector(CSSSelector s) {
		for (CSSStyleHierarchy sty: styles) {
			if (sty.getStyle().getSelector().equals(s)){ 
				return sty.getStyle();
			}
		}
		return null;
	}
	
	/**
	 * Gets all relevant styles for a MTComponent.
	 *
	 * @param c the MTComponent
	 * @return the relevant styles
	 */
	public List<CSSStyleHierarchy> getRelevantStyles(MTComponent c) {
		if (!components.contains(c) && c instanceof CSSStylableComponent){ 
			components.add((CSSStylableComponent)c);
		}
		
		List<CSSStyleHierarchy> relevantStyles = new ArrayList<CSSStyleHierarchy>();

	
		
		for (CSSStyleHierarchy s: styles) {
			int temp = s.getStyle().getSelector().appliesTo(c);
			if (temp != 0)
				//Debug Only
				//Logger.getLogger("MT4J Extensions").debug("Relevant Style? " + temp + " (" + s.getStyle().getSelector() + ")");
			if (temp != 0) {
				relevantStyles.add(new CSSStyleHierarchy(s, temp % 100, (short)(temp / 100)));
			}

			
		}
		return relevantStyles;
	}
	
	
	/**
	 * Gets the default font.
	 *
	 * @param app the app
	 * @return the default font
	 */
	public IFont getDefaultFont(AbstractMTApplication app) {
		if (defaultFont == null)  {
			defaultFont = FontManager.getInstance().createFont(app,
					"SansSerif", 16, // Font size
					MTColor.WHITE);
		}
		
		return defaultFont;
	}
	
	
}
