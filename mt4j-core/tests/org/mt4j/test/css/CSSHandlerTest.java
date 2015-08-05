package org.mt4j.test.css;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mt4j.components.css.parser.CSSHandler;
import org.mt4j.components.css.style.CSSSelector;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;


public class CSSHandlerTest extends TestCase{
	private ILogger logger = MTLoggerFactory.getLogger("MT4J Extensions");
	private StartTestApp app = new StartTestApp();
	private List<CSSStyle> styles = new ArrayList<CSSStyle>();
	private CSSHandler cssh = new CSSHandler(app, styles);
	
	@Before
	public void setUp() {
	}
	
	
	@Test 
	public void testProcessElement() {
		CSSSelector test = cssh.processElement("P.c141");
		logger.debug(test);
		test = cssh.processElement("P#c141");
		logger.debug(test);
		test = cssh.processElement("#P.c141");
		logger.debug(test);
		test = cssh.processElement("#P c141");
		logger.debug(test);
		test = cssh.processElement("#P                                .c141");
		logger.debug(test);
	}

}
