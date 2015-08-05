package org.mt4j.test.css;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mt4j.components.css.parser.CSSParserConnection;
import org.mt4j.components.css.style.CSSSelector;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.css.util.CSSKeywords.CSSBorderStyle;
import org.mt4j.components.css.util.CSSKeywords.CSSSelectorType;
import org.mt4j.util.MTColor;


public class BorderTest extends TestCase {
	private StartTestApp app = new StartTestApp();
	private CSSParserConnection pc;
	private List<CSSStyle> styles;

	@Before
	public void setUp() {
		pc = new CSSParserConnection("junit/bordertest.css", app);
		styles= pc.getCssh().getStyles();
	}
	
	
	protected void tearDown() {
		//app.destroy();
	}
	
	@Test 
	public void testWidth() {
		CSSSelector borderwidth = new CSSSelector("borderwidth", CSSSelectorType.ID);
		boolean exists = false;
		for (CSSStyle s: styles) {
			if (s.getSelector().equals(borderwidth)) {
				exists = !exists;
				float comp = (1200f/72f)*4f;
				assertTrue(closeTo(s.getBorderWidth(),comp));
			}
		}
		assertTrue(exists);
	}
	
	@Test
	public void testStyle() {
		CSSSelector borderstyle = new CSSSelector("borderstyle", CSSSelectorType.ID);
		boolean exists = false;
		for (CSSStyle s: styles) {
			if (s.getSelector().equals(borderstyle)) {
				exists = !exists;
				assertTrue(s.getBorderStyle() == CSSBorderStyle.DASHED);
			}
		}
		assertTrue(exists);
	}
	
	@Test
	public void testColor() {
		CSSSelector bordercolor = new CSSSelector("bordercolor", CSSSelectorType.ID);
		boolean exists = false;
		for (CSSStyle s: styles) {
			if (s.getSelector().equals(bordercolor)) {
				exists = !exists;
				assertTrue(s.getBorderColor().equals(new MTColor(255,0,0,255)));
			}
		}
		assertTrue(exists);
	}
	
	private boolean closeTo(float a, float b) {
		float c = a-b;
		if (c < 0) c *= -1f;
		if (c < 0.001) return true;
		return false;
	}
}
