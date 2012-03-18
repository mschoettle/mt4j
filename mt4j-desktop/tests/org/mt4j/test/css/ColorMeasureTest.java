package org.mt4j.test.css;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mt4j.components.css.parser.CSSParserConnection;
import org.mt4j.components.css.style.CSSSelector;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.css.util.CSSKeywords.CSSSelectorType;
import org.mt4j.util.MTColor;



public class ColorMeasureTest extends TestCase {
	private StartTestApp app = new StartTestApp();
	private CSSParserConnection pc;
	private List<CSSStyle> styles;
	
	@Before
	public void setUp() {
		pc = new CSSParserConnection("junit/colormeasuretest.css", app);
		styles= pc.getCssh().getStyles();
	}
	
	
	protected void tearDown() {
		//app.destroy();
	}
	
	@Test
	public void testParserConnector() throws InterruptedException {
		assertTrue (pc != null);
	}
	
	@Test
	public void testHexColors() {
		CSSSelector reference = new CSSSelector("testhexcolor", CSSSelectorType.ID);
		boolean exists = false;
		for (CSSStyle s: styles) {
			if (s.getSelector().equals(reference)) {
				exists = true;
				assertTrue (s.getBackgroundColor().equals(new MTColor(224,224,224,255)));
			}
		}
		assertTrue (exists);
	}
	@Test
	public void testRGBColors() {
		CSSSelector reference = new CSSSelector("testrgbcolor", CSSSelectorType.ID);
		boolean exists = false;
		for (CSSStyle s: styles) {
			if (s.getSelector().equals(reference)) {
				exists = true;
				assertTrue (s.getBackgroundColor().equals(new MTColor(0.6f * 255f,0.6f * 255f,0.4f*255f,255)));
			}
		}
		assertTrue (exists);
	}
	@Test
	public void testNameColors() {
		CSSSelector white = new CSSSelector("testNameColorWhite", CSSSelectorType.ID);
		CSSSelector red = new CSSSelector("testNameColorRed", CSSSelectorType.ID);
		CSSSelector green = new CSSSelector("testNameColorGreen", CSSSelectorType.ID);
		CSSSelector blue = new CSSSelector("testNameColorBlue", CSSSelectorType.ID);
		CSSSelector purple = new CSSSelector("testNameColorPurple", CSSSelectorType.ID);
		int exists = 0;
		for (CSSStyle s: styles) {
			if (s.getSelector().equals(white)) {
				exists++;
				assertTrue (s.getBackgroundColor().equals(new MTColor(255,255,255,255)));
			}
			if (s.getSelector().equals(red)) {
				exists++;
				assertTrue (s.getBackgroundColor().equals(new MTColor(255,0,0,255)));
			}
			if (s.getSelector().equals(green)) {
				exists++;
				assertTrue (s.getBackgroundColor().equals(new MTColor(0,128,0,255)));
			}
			if (s.getSelector().equals(blue)) {
				exists++;
				assertTrue (s.getBackgroundColor().equals(new MTColor(0,0,255,255)));
			}
			if (s.getSelector().equals(purple)) {
				exists++;
				assertTrue (s.getBackgroundColor().equals(new MTColor(128,0,128,255)));
			}
		}
		assertTrue (exists == 5);
	}
	@Test
	public void testDimensions () {
		CSSSelector testWidth = new CSSSelector("testWidth", CSSSelectorType.ID);
		CSSSelector testHeight = new CSSSelector("testHeight", CSSSelectorType.ID);
		CSSSelector testWidthPercentage = new CSSSelector("testWidthPercentage", CSSSelectorType.ID);
		CSSSelector testHeightPercentage = new CSSSelector("testHeightPercentage", CSSSelectorType.ID);
		int exists = 0;
		for (CSSStyle s: styles) {
			if (s.getSelector().equals(testWidth)) {
				exists++;
				assertTrue (s.getWidth() == 100f);
			}
			if (s.getSelector().equals(testHeight)) {
				exists++;
				assertTrue (s.getHeight() == 100f);
			}
			if (s.getSelector().equals(testWidthPercentage)) {
				exists++;
				assertTrue (s.getWidth() == 25f && s.isWidthPercentage());
			}
			if (s.getSelector().equals(testHeightPercentage)) {
				exists++;
				assertTrue (s.getHeight() == 25f && s.isHeightPercentage());
			}

		}
		assertTrue (exists == 4);
		
		
	}
	@Test
	public void testMeasures() {
		CSSSelector testMeasuresPx = new CSSSelector("testMeasuresPx", CSSSelectorType.ID);
		CSSSelector testMeasuresCm = new CSSSelector("testMeasuresCm", CSSSelectorType.ID);
		CSSSelector testMeasuresIn = new CSSSelector("testMeasuresIn", CSSSelectorType.ID);
		CSSSelector testMeasuresMm = new CSSSelector("testMeasuresMm", CSSSelectorType.ID);
		CSSSelector testMeasuresPt = new CSSSelector("testMeasuresPt", CSSSelectorType.ID);
		CSSSelector testMeasuresPc = new CSSSelector("testMeasuresPc", CSSSelectorType.ID);
		CSSSelector testMeasuresEm = new CSSSelector("testMeasuresEm", CSSSelectorType.ID);
		CSSSelector testMeasuresInt = new CSSSelector("testMeasuresInt", CSSSelectorType.ID);
		CSSSelector testMeasuresReal = new CSSSelector("testMeasuresReal", CSSSelectorType.ID);
		CSSSelector testMeasuresPerc = new CSSSelector("testMeasuresPerc", CSSSelectorType.ID);
		int exists = 0;
		for (CSSStyle s: styles) {
		if (s.getSelector().equals(testMeasuresPx)) {
			exists++;
			assertTrue (s.getWidth() == 100f);
		}
		if (s.getSelector().equals(testMeasuresCm)) {
			exists++;
			assertTrue (s.getWidth() == (10f/254f) * 100f * 100f);
		}
		if (s.getSelector().equals(testMeasuresIn)) {
			exists++;
			assertTrue (s.getWidth() == 100f * 100);
		}
		if (s.getSelector().equals(testMeasuresMm)) {
			exists++;
			assertTrue (s.getWidth() == (1f/254f) * 100f * 100f);
		}
		if (s.getSelector().equals(testMeasuresPt)) {
			exists++;
			assertTrue (s.getWidth() == (1f/72f) * 100f * 100f);
		}
		if (s.getSelector().equals(testMeasuresPc)) {
			exists++;
			assertTrue (s.getWidth() == (12f/72f) * 100f * 100f);
		}
		if (s.getSelector().equals(testMeasuresEm)) {
			exists++;
			assertTrue (s.getWidth() == 16f/72f * 100f * 100f);
		}
		if (s.getSelector().equals(testMeasuresInt)) {
			exists++;
			assertTrue (s.getWidth() == 100f);
		}
		if (s.getSelector().equals(testMeasuresReal)) {
			exists++;
			assertTrue (s.getWidth() < 123.4001 && s.getWidth() > 123.3999);
		}
		if (s.getSelector().equals(testMeasuresPerc)) {
			exists++;
			assertTrue (s.getWidth() == 25f && s.isWidthPercentage());
		}

		}
		assertTrue (exists == 10);
		
		
	}
	
}
