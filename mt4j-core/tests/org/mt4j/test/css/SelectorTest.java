package org.mt4j.test.css;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mt4j.components.css.parser.CSSParserConnection;
import org.mt4j.components.css.style.CSSSelector;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.css.util.CSSKeywords.CSSSelectorType;
import org.mt4j.util.MTColor;


public class SelectorTest extends TestCase {
	StartTestApp app = new StartTestApp();
	CSSParserConnection pc;
	List<CSSStyle> styles;
	HashMap<CSSSelector, MTColor> hm = new HashMap<CSSSelector, MTColor>();
	
	@Before
	public void setUp() {
		pc = new CSSParserConnection("junit/selectortest.css", app);
		styles= pc.getCssh().getStyles();

		hm.clear();
		for (CSSStyle s: styles) {
			hm.put(s.getSelector(), s.getBackgroundColor());
		}
	}
	
	
	protected void tearDown() {
		//app.destroy();
	}
	
	@Test
	public void testSimpleSelectors() {
		CSSSelector h1 = new CSSSelector("H1", CSSSelectorType.TYPE);
		CSSSelector id = new CSSSelector("ID", CSSSelectorType.ID);
		CSSSelector c0 = new CSSSelector("c0", CSSSelectorType.CLASS);
		
		MTColor green = new MTColor(0,128,0,255);
		MTColor red = new MTColor(255,0,0,255);
		MTColor blue = new MTColor(0,0,255,255);

		assertTrue(hm.get(h1).equals(red));
		assertTrue(hm.get(id).equals(blue));
		assertTrue(hm.get(c0).equals(green));

	}
	
	@Test
	public void testMultipleSelectors() {
		CSSSelector h2 = new CSSSelector("H2", CSSSelectorType.TYPE);
		CSSSelector id3 = new CSSSelector("ID3", CSSSelectorType.ID);
		CSSSelector c4 = new CSSSelector("c4", CSSSelectorType.CLASS);
		
		MTColor black = new MTColor(0,0,0,255);
		MTColor purple = new MTColor(128,0,128,255);
		MTColor silver = new MTColor(192,192,192,255);
		
		assertTrue(hm.get(h2).equals(black));
		assertTrue(hm.get(id3).equals(purple));
		assertTrue(hm.get(c4).equals(silver));
	}
	
	@Test
	public void testUniversalSelector() {
		CSSSelector star = new CSSSelector("*", CSSSelectorType.UNIVERSAL);
		CSSSelector h5 = new CSSSelector("H5", CSSSelectorType.TYPE);
		CSSSelector id5 = new CSSSelector("ID5", CSSSelectorType.ID);
		CSSSelector c5 = new CSSSelector("c5", CSSSelectorType.CLASS);
		
		MTColor olive = new MTColor(128,128,0,255);
		MTColor white = new MTColor(255,255,255,255);
		MTColor gray = new MTColor(128,128,128,255);
		MTColor maroon = new MTColor(128,0,0,255);
		
		assertTrue(hm.get(star).equals(olive));
		assertTrue(hm.get(h5).equals(white));
		assertTrue(hm.get(id5).equals(gray));
		assertTrue(hm.get(c5).equals(maroon));
		

		
	}
	
	@Test
	public void testCascadingSelectors () {
		CSSSelector h6h6 = new CSSSelector("H6", CSSSelectorType.TYPE);
		h6h6.setSecondary("H6");
		h6h6.setSecondaryType(CSSSelectorType.TYPE);
		CSSSelector h6c6  = new CSSSelector("H6", CSSSelectorType.TYPE);
		h6c6.setSecondary("c6");
		h6c6.setSecondaryType(CSSSelectorType.CLASS);
		CSSSelector h6id6 = new CSSSelector("H6", CSSSelectorType.TYPE);
		h6id6.setSecondary("ID6");
		h6id6.setSecondaryType(CSSSelectorType.ID);
		
		CSSSelector c6h6 = new CSSSelector("c6", CSSSelectorType.CLASS);
		c6h6.setSecondary("H6");
		c6h6.setSecondaryType(CSSSelectorType.TYPE);
		CSSSelector c6c6  = new CSSSelector("c6", CSSSelectorType.CLASS);
		c6c6.setSecondary("c6");
		c6c6.setSecondaryType(CSSSelectorType.CLASS);
		CSSSelector c6id6 = new CSSSelector("c6", CSSSelectorType.CLASS);
		c6id6.setSecondary("ID6");
		c6id6.setSecondaryType(CSSSelectorType.ID);
		
		CSSSelector id6h6 = new CSSSelector("ID6", CSSSelectorType.ID);
		id6h6.setSecondary("H6");
		id6h6.setSecondaryType(CSSSelectorType.TYPE);
		CSSSelector id6c6  = new CSSSelector("ID6", CSSSelectorType.ID);
		id6c6.setSecondary("c6");
		id6c6.setSecondaryType(CSSSelectorType.CLASS);
		CSSSelector id6id6 = new CSSSelector("ID6", CSSSelectorType.ID);
		id6id6.setSecondary("ID6");
		id6id6.setSecondaryType(CSSSelectorType.ID);
		
		CSSSelector h6id7  = new CSSSelector("H6", CSSSelectorType.TYPE);
		h6id7.setSecondary("ID7");
		h6id7.setSecondaryType(CSSSelectorType.ID);
		CSSSelector listrong = new CSSSelector("LI", CSSSelectorType.TYPE);
		listrong.setSecondary("STRONG");
		listrong.setSecondaryType(CSSSelectorType.TYPE);
		
		
		MTColor purple = new MTColor(128,0,128,255);
		MTColor green = new MTColor(0,128,0,255);
		MTColor red = new MTColor(255,0,0,255);
		MTColor blue = new MTColor(0,0,255,255);
		
		assertTrue(hm.get(h6h6).equals(red));
		assertTrue(hm.get(h6c6).equals(blue));
		assertTrue(hm.get(h6id6).equals(green));
		assertTrue(hm.get(c6h6).equals(red));
		assertTrue(hm.get(c6c6).equals(blue));
		assertTrue(hm.get(c6id6).equals(green));
		assertTrue(hm.get(id6h6).equals(red));
		assertTrue(hm.get(id6c6).equals(blue));
		assertTrue(hm.get(id6id6).equals(green));
		assertTrue(hm.get(h6id7).equals(purple));
		assertTrue(hm.get(listrong).equals(purple));
		
	}
	@Test
	public void testChildSelectors() {
		MTColor green = new MTColor(0,128,0,255);
		MTColor red = new MTColor(255,0,0,255);
		MTColor blue = new MTColor(0,0,255,255);
		
		CSSSelector h7h8 = new CSSSelector("H7", CSSSelectorType.TYPE);
		h7h8.setChild(new CSSSelector("H8", CSSSelectorType.TYPE));
		
		CSSSelector h7id7c7 = new CSSSelector("H7", CSSSelectorType.TYPE);
		CSSSelector child = new CSSSelector("ID7", CSSSelectorType.ID);
		child.setSecondary("c7", CSSSelectorType.CLASS);
		h7id7c7.setChild(child);
		
		CSSSelector h7h8id7 = new CSSSelector("H7", CSSSelectorType.TYPE);
		h7h8id7.setSecondary("H8", CSSSelectorType.TYPE);
		h7h8id7.setChild(new CSSSelector("ID7", CSSSelectorType.ID));
		h7h8id7.getChild().setSecondary("STRONG", CSSSelectorType.TYPE);
		assertTrue(hm.get(h7h8).equals(red));
		assertTrue(hm.get(h7id7c7).equals(blue));
		assertTrue(hm.get(h7h8id7).equals(green));
		
	}
}