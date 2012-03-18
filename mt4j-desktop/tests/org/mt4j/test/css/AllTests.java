package org.mt4j.test.css;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.molokoid.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(ColorMeasureTest.class);
		suite.addTestSuite(FontParserTest.class);
		suite.addTestSuite(BorderTest.class);
		suite.addTestSuite(CSSHandlerTest.class);
		suite.addTestSuite(SelectorTest.class);
		suite.addTestSuite(SelectorIntegrationTest.class);
		//$JUnit-END$
		return suite;
	}

}
