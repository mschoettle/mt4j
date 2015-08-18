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
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;



public class FontParserTest extends TestCase{
	private StartTestApp app = new StartTestApp();
	private CSSParserConnection pc;
	private List<CSSStyle> styles;
	private MTColor w = new MTColor(255,255,255,255);
	private HashMap<CSSSelector, IFont> hm = new HashMap<CSSSelector, IFont>();

	public FontParserTest() {
		pc = new CSSParserConnection("junit/fonttest.css", app);
		styles= pc.getCssh().getStyles();
		hm.clear();
		for (CSSStyle s: styles) {
			hm.put(s.getSelector(), s.getFont());
		}
	}
	
	@Before
	public void setUp() {

	}


	protected void tearDown() {
		//app.destroy();
	}

	@Test
	public void testFontFamilesSans() {
		CSSSelector sans = new CSSSelector("sans", CSSSelectorType.ID);
		CSSSelector sansbold = new CSSSelector("sansbold", CSSSelectorType.ID);
		CSSSelector sanslight = new CSSSelector("sanslight", CSSSelectorType.ID);
		CSSSelector sansitalic = new CSSSelector("sansitalic", CSSSelectorType.ID);
		CSSSelector sansitalicbold = new CSSSelector("sansitalicbold", CSSSelectorType.ID);
		CSSSelector sansitaliclight = new CSSSelector("sansitaliclight", CSSSelectorType.ID);
		CSSSelector sansoblique = new CSSSelector("sansoblique", CSSSelectorType.ID);
		CSSSelector sansobliquebold = new CSSSelector("sansobliquebold", CSSSelectorType.ID);
		CSSSelector sansobliquelight = new CSSSelector("sansobliquelight", CSSSelectorType.ID);
		CSSSelector sansnormal = new CSSSelector("sansnormal", CSSSelectorType.ID);
		CSSSelector sansnormalbold = new CSSSelector("sansnormalbold", CSSSelectorType.ID);
		CSSSelector sansnormallight = new CSSSelector("sansnormallight", CSSSelectorType.ID);

		IFont sansFont = FontManager.getInstance().createFont(app,"SansSerif", 16,w);
		IFont sansBoldFont = FontManager.getInstance().createFont(app,"SansSerif.bold", 16,w);
		IFont sansItalicFont = FontManager.getInstance().createFont(app,"SansSerif.italic", 16,w);
		IFont sansItalicBoldFont = FontManager.getInstance().createFont(app,"SansSerif.bolditalic", 16,w);
		IFont sansLightFont = FontManager.getInstance().createFont(app,"SansSerif", 16,w);


				assertTrue(sameFont(hm.get(sans), sansFont));

				assertTrue(sameFont(hm.get(sansbold), sansBoldFont));

				assertTrue(sameFont(hm.get(sanslight), sansLightFont));

				assertTrue(sameFont(hm.get(sansitalic), sansItalicFont));

				assertTrue(sameFont(hm.get(sansitalicbold), sansItalicBoldFont));

				assertTrue(sameFont(hm.get(sansitaliclight), sansItalicFont));

				assertTrue(sameFont(hm.get(sansoblique), sansItalicFont));

				assertTrue(sameFont(hm.get(sansobliquebold), sansItalicBoldFont));

				assertTrue(sameFont(hm.get(sansobliquelight), sansItalicFont));

				assertTrue(sameFont(hm.get(sansnormal), sansFont));

				assertTrue(sameFont(hm.get(sansnormalbold), sansBoldFont));

				assertTrue(sameFont(hm.get(sansnormallight), sansLightFont));


	}
	
	@Test
	public void testFontFamilesSerif() {
		CSSSelector serif = new CSSSelector("serif", CSSSelectorType.ID);
		CSSSelector serifbold = new CSSSelector("serifbold", CSSSelectorType.ID);
		CSSSelector seriflight = new CSSSelector("seriflight", CSSSelectorType.ID);
		CSSSelector serifitalic = new CSSSelector("serifitalic", CSSSelectorType.ID);
		CSSSelector serifitalicbold = new CSSSelector("serifitalicbold", CSSSelectorType.ID);
		CSSSelector serifitaliclight = new CSSSelector("serifitaliclight", CSSSelectorType.ID);
		CSSSelector serifoblique = new CSSSelector("serifoblique", CSSSelectorType.ID);
		CSSSelector serifobliquebold = new CSSSelector("serifobliquebold", CSSSelectorType.ID);
		CSSSelector serifobliquelight = new CSSSelector("serifobliquelight", CSSSelectorType.ID);
		CSSSelector serifnormal = new CSSSelector("serifnormal", CSSSelectorType.ID);
		CSSSelector serifnormalbold = new CSSSelector("serifnormalbold", CSSSelectorType.ID);
		CSSSelector serifnormallight = new CSSSelector("serifnormallight", CSSSelectorType.ID);

		IFont serifFont = FontManager.getInstance().createFont(app,"Serif", 16,w);
		IFont serifBoldFont = FontManager.getInstance().createFont(app,"Serif.bold", 16,w);
		IFont serifItalicFont = FontManager.getInstance().createFont(app,"Serif.italic", 16,w);
		IFont serifItalicBoldFont = FontManager.getInstance().createFont(app,"Serif.bolditalic", 16,w);
		IFont serifLightFont = FontManager.getInstance().createFont(app,"Serif", 16,w);

				assertTrue(sameFont(hm.get(serif), serifFont));

				assertTrue(sameFont(hm.get(serifbold), serifBoldFont));

				assertTrue(sameFont(hm.get(seriflight), serifLightFont));

				assertTrue(sameFont(hm.get(serifitalic), serifItalicFont));

				assertTrue(sameFont(hm.get(serifitalicbold), serifItalicBoldFont));

				assertTrue(sameFont(hm.get(serifitaliclight), serifItalicFont));

				assertTrue(sameFont(hm.get(serifoblique), serifItalicFont));

				assertTrue(sameFont(hm.get(serifobliquebold), serifItalicBoldFont));

				assertTrue(sameFont(hm.get(serifobliquelight), serifItalicFont));

				assertTrue(sameFont(hm.get(serifnormal), serifFont));

				assertTrue(sameFont(hm.get(serifnormalbold), serifBoldFont));

				assertTrue(sameFont(hm.get(serifnormallight), serifLightFont));

	}
	@Test
	public void testFontFamilesMono() {
		CSSSelector mono = new CSSSelector("mono", CSSSelectorType.ID);
		CSSSelector monobold = new CSSSelector("monobold", CSSSelectorType.ID);
		CSSSelector monolight = new CSSSelector("monolight", CSSSelectorType.ID);
		CSSSelector monoitalic = new CSSSelector("monoitalic", CSSSelectorType.ID);
		CSSSelector monoitalicbold = new CSSSelector("monoitalicbold", CSSSelectorType.ID);
		CSSSelector monoitaliclight = new CSSSelector("monoitaliclight", CSSSelectorType.ID);
		CSSSelector monooblique = new CSSSelector("monooblique", CSSSelectorType.ID);
		CSSSelector monoobliquebold = new CSSSelector("monoobliquebold", CSSSelectorType.ID);
		CSSSelector monoobliquelight = new CSSSelector("monoobliquelight", CSSSelectorType.ID);
		CSSSelector mononormal = new CSSSelector("mononormal", CSSSelectorType.ID);
		CSSSelector mononormalbold = new CSSSelector("mononormalbold", CSSSelectorType.ID);
		CSSSelector mononormallight = new CSSSelector("mononormallight", CSSSelectorType.ID);

		IFont monoFont = FontManager.getInstance().createFont(app,"Monospaced", 16,w);
		IFont monoBoldFont = FontManager.getInstance().createFont(app,"Monospaced.bold", 16,w);
		IFont monoItalicFont = FontManager.getInstance().createFont(app,"Monospaced.italic", 16,w);
		IFont monoItalicBoldFont = FontManager.getInstance().createFont(app,"Monospaced.bolditalic", 16,w);
		IFont monoLightFont = FontManager.getInstance().createFont(app,"Monospaced", 16,w);


				assertTrue(sameFont(hm.get(mono), monoFont));

				assertTrue(sameFont(hm.get(monobold), monoBoldFont));

				assertTrue(sameFont(hm.get(monolight), monoLightFont));

				assertTrue(sameFont(hm.get(monoitalic), monoItalicFont));

				assertTrue(sameFont(hm.get(monoitalicbold), monoItalicBoldFont));

				assertTrue(sameFont(hm.get(monoitaliclight), monoItalicFont));

				assertTrue(sameFont(hm.get(monooblique), monoItalicFont));

				assertTrue(sameFont(hm.get(monoobliquebold), monoItalicBoldFont));

				assertTrue(sameFont(hm.get(monoobliquelight), monoItalicFont));

				assertTrue(sameFont(hm.get(mononormal), monoFont));

				assertTrue(sameFont(hm.get(mononormalbold), monoBoldFont));

				assertTrue(sameFont(hm.get(mononormallight), monoLightFont));

	}
	@Test
	public void testFontFamilesOther() {
		CSSSelector arial = new CSSSelector("arial", CSSSelectorType.ID);
		CSSSelector arial12 = new CSSSelector("arial12", CSSSelectorType.ID);
		CSSSelector arialgreen = new CSSSelector("arialgreen", CSSSelectorType.ID);
		CSSSelector arialsmaller = new CSSSelector("arialsmaller", CSSSelectorType.ID);
		
		IFont arialFont = FontManager.getInstance().createFont(app,"arial.ttf", 16,w);
		IFont arial12Font = FontManager.getInstance().createFont(app, "arial.ttf", 12, w);
		MTColor g = new MTColor(0,128,0,255);
		IFont arialgreenFont = FontManager.getInstance().createFont(app, "arial.ttf", 16, g);
		IFont arialsmallerFont = FontManager.getInstance().createFont(app, "arial.ttf", 8, w);

				assertTrue(sameFont(hm.get(arial), arialFont));

				assertTrue(sameFont(hm.get(arial12), arial12Font));

				assertTrue(sameFont(hm.get(arialgreen), arialgreenFont));

				assertTrue(sameFont(hm.get(arialsmaller), arialsmallerFont));

	}
	
	public boolean sameFont(IFont f1, IFont f2) {
		boolean same = true;
		same = same && f1.getFillColor().equals(f2.getFillColor());
//		same = same && f1.getStrokeColor().equals(f2.getStrokeColor());
		same = same && f1.getOriginalFontSize() == f2.getOriginalFontSize();
		same = same && f1.getFontFileName().equalsIgnoreCase(f2.getFontFileName());
				

		return same;
	}

}
