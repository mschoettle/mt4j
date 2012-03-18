/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.util.font.fontFactories;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.batik.parser.PathParser;
import org.apache.batik.util.SVGConstants;
import org.mt4j.components.visibleComponents.font.VectorFontCharacter;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.font.IFontCharacter;
import org.mt4j.util.font.VectorFont;
import org.mt4j.util.font.fontFactories.IFontFactory;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.xml.XmlHandler;
import org.mt4j.util.xml.svg.CustomPathHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import processing.core.PApplet;

/**
 * Creates vector font from an svg font file.
 * @author Christopher Ruff
 *
 */
public class SvgFontFactory extends DefaultHandler implements IFontFactory {
//	//Register the factory
//	static{
//		FontManager.getInstance().registerFontFactory(".ttf", new SvgFontFactory());
//	}
	
	private PApplet pa;
//	private ArrayList<Vertex[]> pathVertexArrays;
	
//	private HashMap<Vertex[], ArrayList<Vertex[]>> vertexArrToOutlinesList;
//	private HashMap<BezierVertex, Vertex> cubicBezVertTOQuadricControlPoint;
	
	private boolean verbose;
	
	private String currentGlyphName;
	private String currentUnicode;
	private int currenthorizontalAdvX;
	
	private String fontID;
	private String fontFamily;
	private int fontDefaultXAdvancing;
	
	private ArrayList<VectorFontCharacter> characters;
	
	private VectorFont svgFont;
	private int fontMaxAscent;
	private int fontMaxDescent;
	private int font_units_per_em;
	
	private int fontSize;
	
	private CustomPathHandler pathHandler; 
	private PathParser pathParser;
	private float scaleFactor;
	private MTColor fillColor;
//	private MTColor strokeColor;
	private boolean antiAliased;
	private String fontFileName;
	
	
	public SvgFontFactory() {  }
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.font.fontFactories.IFontFactory#getCopy(org.mt4j.components.visibleComponents.font.IFont)
	 */
	public IFont getCopy(IFont font) {
		if (font instanceof VectorFont) {
			VectorFont vf = (VectorFont) font;
			VectorFont copy = new VectorFont( (VectorFontCharacter[]) vf.getCharacters(), vf.getDefaultHorizontalAdvX(), vf.getFontFamily(), vf.getFontMaxAscent(), vf.getFontMaxDescent(), vf.getUnitsPerEM(), vf.getOriginalFontSize(), vf.getFillColor(), /*vf.getStrokeColor(),*/ vf.isAntiAliased(), font.getFontFileName());
			return copy;
		}
		return null;
	}
	
	public IFont createFont(PApplet pa, String fontName, int fontSize, MTColor color) {
		return this.createFont(pa, fontName, fontSize, color, color, true);
	}

	public IFont createFont(PApplet pa, String fontName, int fontSize, MTColor color, boolean antiAliased) {
		return this.createFont(pa, fontName, fontSize, color, color, antiAliased);
	}
	
	public IFont createFont(
			PApplet pa, 
			String svgFontFileName, 
			int fontSize, 
			MTColor fillColor, 
			MTColor strokeColor
	){
		return this.createFont(pa, svgFontFileName, fontSize, fillColor, strokeColor, true);
	}

	public IFont createFont(
			PApplet pa, 
			String svgFontFileName, 
			int fontSize, 
			MTColor fillColor, 
			MTColor strokeColor,
			boolean antiAliased
	){
		this.pa = pa;
		this.fontSize 		= fontSize;
		this.fillColor = fillColor;
//		this.strokeColor = strokeColor;
		this.antiAliased = antiAliased;
		
		//List of all (prepared for stencil drawing) Glyph path vertices
//		pathVertexArrays 					= new ArrayList<Vertex[]>();
		
//		//Hashmap to get the list of partial- or sub-paths of a given glyph vertex array
//		//the subpaths are needed to draw the outlines of the glyph
//		vertexArrToOutlinesList 			= new HashMap<Vertex[], ArrayList<Vertex[]>>();
		
//		//Because all quadric curves get converted to cubics,
//		//but the original quadric controlpoint is needed for "T" tag
//		cubicBezVertTOQuadricControlPoint 	= new HashMap<BezierVertex, Vertex>();
		
		//List of all characters created during parsing
		characters 							= new ArrayList<VectorFontCharacter>();
		
		this.fontSize = fontSize;
		
		//Defaults
		currentGlyphName 		= "";
		currentUnicode 			= "";
		currenthorizontalAdvX 	= 500;
		fontMaxAscent 			= 900;
		fontMaxDescent 			= -200;
		font_units_per_em 		= 1000;
		fontID 					= "";
		fontDefaultXAdvancing   = 500;
		scaleFactor = 0.2f;
		
		svgFont = null;
		verbose = false;
		
		pathHandler = new CustomPathHandler();
		pathParser 	= new PathParser();
		pathParser.setPathHandler(pathHandler);
		
		this.fontFileName = svgFontFileName;
		
		XmlHandler.getInstance().saxParse(
				svgFontFileName, 
				this);
		VectorFont font = this.getFont();
		font.setFontFileName(svgFontFileName);
		
		return font;
	}
	
	
	public String getFontFileSuffix() {
		return ".svg";
	}
	
	
	@Override
	public void startDocument() throws SAXException {
//		pathVertexArrays.clear();
//		vertexArrToOutlinesList.clear();
//		cubicBezVertTOQuadricControlPoint.clear();
		characters.clear();
		
		if (verbose)
			System.out.println("start Document ");
	}
	

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes){
		//System.out.println("Start element: " + qName);
		
		if(qName.equalsIgnoreCase("font") || qName.equalsIgnoreCase("font-face")){
			for ( int i = 0; i < attributes.getLength(); i++ ){ 
				String currentAttributeName = attributes.getQName(i);
				String currentAttribueValue = attributes.getValue(i);
				
				if (currentAttributeName.equalsIgnoreCase("id")){
					fontID = currentAttribueValue;
				}else if (currentAttributeName.equalsIgnoreCase("font-family")){
					fontFamily = currentAttribueValue;
				}else if (currentAttributeName.equalsIgnoreCase("horiz-adv-x")){
					fontDefaultXAdvancing = Integer.parseInt(currentAttribueValue);
				}else if (currentAttributeName.equalsIgnoreCase("ascent")){
					fontMaxAscent = Integer.parseInt(currentAttribueValue);
				}else if (currentAttributeName.equalsIgnoreCase("descent")){
					fontMaxDescent = Integer.parseInt(currentAttribueValue);
				}else if (currentAttributeName.equalsIgnoreCase("units-per-em")){
					font_units_per_em = Integer.parseInt(currentAttribueValue);
					
					//Get the desired fontsize scaling factor 
					int unitsPerEm = this.font_units_per_em;
					int resolution = Toolkit.getDefaultToolkit().getScreenResolution();
					//System.out.println("Screen resolution: " + resolution);
					this.scaleFactor = ((float)fontSize * (float)resolution) / (72F * (float)unitsPerEm);
					//System.out.println("->Scalefactor: " + this.scaleFactor);
				}
			}
			
		}
		
		if(qName.equalsIgnoreCase(SVGConstants.SVG_GLYPH_TAG)|| qName.equalsIgnoreCase(SVGConstants.SVG_MISSING_GLYPH_TAG)){
			VectorFontCharacter currentCharacter = null;
			
			for ( int i = 0; i < attributes.getLength(); i++ ){ 
				String currentAttributeName = attributes.getQName(i);
				String currentAttribueValue = attributes.getValue(i);
				
				if (currentAttributeName.equalsIgnoreCase("d")){
//					ArrayList<SvgFontCharacter> characters = extractPath(currentAttribueValue); 
					
					//Parse the Paths's "d" attribute and create a new character
					pathHandler = new CustomPathHandler();
					pathParser.setPathHandler(pathHandler);
					pathParser.parse(currentAttribueValue);
					currentCharacter = this.createCharacter(pathHandler);
					
					//Set unicode/name for missing glyph by hand
					if (qName.equalsIgnoreCase("missing-glyph")){
						currentUnicode = "missing-glyph";
						currentGlyphName = "missing-glyph";
					}
				}else if (currentAttributeName.equalsIgnoreCase("unicode")){
					currentUnicode = currentAttribueValue;
					if (currentUnicode.equalsIgnoreCase(" ")){
						Vertex[] spaceVerts = new Vertex[]{new Vertex(0,0,0), new Vertex(fontDefaultXAdvancing,0,0),new Vertex(fontDefaultXAdvancing,100,0), /*new Vertex(0,100,0)*/};
						ArrayList<Vertex[]> spaceContours = new ArrayList<Vertex[]>();
						spaceContours.add(spaceVerts);
						VectorFontCharacter spaceCharacter = new VectorFontCharacter(pa, spaceContours);
//						VectorFontCharacter spaceCharacter = new VectorFontCharacter(new Vertex[]{new Vertex(0,0,0), new Vertex(100,0,0),new Vertex(100,100,0),new Vertex(0,100,0)}, new ArrayList<Vertex[]>(), pa);
						spaceCharacter.setPickable(false);
						if (MT4jSettings.getInstance().isOpenGlMode()){
							spaceCharacter.setUseDirectGL(true);
//							spaceCharacter.generateAndUseDisplayLists();
						}
						spaceCharacter.setVisible(false);
						spaceCharacter.setNoFill(true);
						spaceCharacter.setNoStroke(true);
						currentCharacter = spaceCharacter;
					}
				}else if (currentAttributeName.equalsIgnoreCase("glyph-name")){
					currentGlyphName = currentAttribueValue;
					if (currentUnicode.equalsIgnoreCase("space")){
						Vertex[] spaceVerts = new Vertex[]{new Vertex(0,0,0), new Vertex(fontDefaultXAdvancing,0,0),new Vertex(fontDefaultXAdvancing,100,0), /*new Vertex(0,100,0)*/};
						ArrayList<Vertex[]> spaceContours = new ArrayList<Vertex[]>();
						spaceContours.add(spaceVerts);
						VectorFontCharacter spaceCharacter = new VectorFontCharacter(pa, spaceContours);
//						VectorFontCharacter spaceCharacter = new VectorFontCharacter(new Vertex[]{new Vertex(0,0,0), new Vertex(100,0,0),new Vertex(100,100,0),new Vertex(0,100,0)}, new ArrayList<Vertex[]>(), pa);
						spaceCharacter.setPickable(false);
						if (MT4jSettings.getInstance().isOpenGlMode()){
							spaceCharacter.setUseDirectGL(true);
//							spaceCharacter.generateAndUseDisplayLists();
						}
						spaceCharacter.setVisible(false);
						spaceCharacter.setNoFill(true);
						spaceCharacter.setNoStroke(true);
						currentCharacter = spaceCharacter;
					}
				}else if (currentAttributeName.equalsIgnoreCase("horiz-adv-x")){
					currenthorizontalAdvX = Integer.parseInt(currentAttribueValue);
				}
			}
			
			if (currentCharacter != null){
				currentCharacter.setName(currentGlyphName);
				currentGlyphName = "";
				
				currentCharacter.setUnicode(currentUnicode);
				currentUnicode = "";
				
				if (currenthorizontalAdvX != 0){
					currentCharacter.setHorizontalDist(currenthorizontalAdvX);
				}else{
					currentCharacter.setHorizontalDist(fontDefaultXAdvancing); //Default if character, nor the font specified this
				}
				
				
				//TODO this caused performance problems in the past - reason UNKOWN! DISABLE IF PERFORMANCE DROPS SIGNIFICANTLY!
				//Scale character advance distances according to fontsize
//				float tmp = currentCharacter.getHorizontalDist() * (float)(1.0/(float)font_units_per_em);
//				currentCharacter.setHorizontalDist(Math.round(tmp * fontSize));
				currentCharacter.setHorizontalDist(Math.round(currentCharacter.getHorizontalDist() * this.scaleFactor));
				
				//Set space's vertices to its horizontal advancement value
				//the vertices are important for showing the textbox caret
				//at the right position
				if (currentCharacter.getUnicode().equals(" ") || currentUnicode.equalsIgnoreCase("space")){
					int xadvance = currentCharacter.getHorizontalDist();
					currentCharacter.setVertices(new Vertex[]{new Vertex(0,0,0), new Vertex(xadvance,0,0),new Vertex(xadvance,100,0) /*,new Vertex(0,100,0)*/});
				}
				
				characters.add(currentCharacter);
				
				currenthorizontalAdvX = 0; //Reset
			}
		}

	}
	
	
	/**
	 * 
	 * @param pathHandler
	 * @return
	 */
	private VectorFontCharacter createCharacter(CustomPathHandler pathHandler){
		//	Get the Vertices of the path
		//Vertex[] originalPointsArray = pathHandler.getPathPointsArray();
		
		//Get stencil prepared vertices (with reversed moveTo vertices added)
		LinkedList<Vertex> pathPoints = pathHandler.getPathPoints();
		
		/*
		//Do this if you want stencil polygons
		if (pathHandler.getReverseMoveToStack().size() <= 1){
			//nicht adden
		}else{
			pathPoints.addAll(pathHandler.getReverseMoveToStack());
		}
		*/
		Vertex[] allPathVertices = pathPoints.toArray(new Vertex[pathPoints.size()]);
		
		//Get Sub-Paths
		ArrayList<Vertex[]> contours = pathHandler.getContours();
		
		//Not really needed actually..
		//add point array to list of all arrays
//		pathVertexArrays.add(allPathVertices);
		//Put the partial path list of this path into hashmap
//		vertexArrToOutlinesList.put(allPathVertices, contours); //FIXME TEST
		
		//	CREATE THE Character \\
		//Rotate because font characters 0,0 is at bottom left
		Vertex.xRotateVectorArray(allPathVertices, new Vector3D((float)(currenthorizontalAdvX/2.0),0,0), 180);
		//Scale character to desired fontsize
//		Vertex.scaleVectorArray(allPathVertices, new Vector3D(0,0,0), (float)(1.0/(float)font_units_per_em));
//		Vertex.scaleVectorArray(allPathVertices, new Vector3D(0,0,0), fontSize);
		Vertex.scaleVectorArray(allPathVertices, new Vector3D(0,0,0), this.scaleFactor);
		
		//TODO nur contours als parameter is intuitiver
		VectorFontCharacter character = new VectorFontCharacter(pa,  contours);
		
		if (MT4jSettings.getInstance().isOpenGlMode())
			character.setUseDirectGL(true);

		//Color
//		character.setStrokeColor(new MTColor(strokeColor.getR(), strokeColor.getG(), strokeColor.getB(), strokeColor.getAlpha()));
		character.setStrokeColor(new MTColor(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha()));
		character.setFillColor(new MTColor(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha()));
		
		character.setStrokeWeight(0.7f);
		character.setPickable(false);
		
		if (!antiAliased){
			character.setNoStroke(true);	
		}else{
			if (MT4jSettings.getInstance().isMultiSampling() /*&& fillColor.equals(strokeColor)*/){
				character.setNoStroke(true);
			}else{
				character.setNoStroke(false);	
			}
		}
	
		if (MT4jSettings.getInstance().isOpenGlMode())
			character.generateAndUseDisplayLists();
		
		return character;
	}
		
	
	
	@Override
	public void endDocument() throws SAXException {
		//Manually add a NEWLINE character to the font
		Vertex[] nlVerts = new Vertex[]{new Vertex(0,0,0), new Vertex(200,0,0),new Vertex(200,100,0),/*new Vertex(0,100,0)*/};
		ArrayList<Vertex[]> nlContours = new ArrayList<Vertex[]>();
		nlContours.add(nlVerts);
		VectorFontCharacter newLine = new VectorFontCharacter(pa, nlContours);
//		VectorFontCharacter newLine = new VectorFontCharacter(new Vertex[]{new Vertex(0,0,0), new Vertex(200,0,0),new Vertex(200,100,0),new Vertex(0,100,0)}, new ArrayList<Vertex[]>(), pa);
		newLine.setPickable(false);						    		
		newLine.setVisible(false);
		newLine.setNoFill(true);
		newLine.setNoStroke(true);
		newLine.setHorizontalDist(0);
		newLine.setUnicode("\n");
		newLine.setName("newline");
		characters.add(newLine);
		
		//Manually add a TAB character to the font
		int defaultTabWidth = 200;
		Vertex[] tabVerts = new Vertex[]{new Vertex(0,0,0), new Vertex(200,0,0),new Vertex(200,100,0),/*new Vertex(0,100,0)*/};
		ArrayList<Vertex[]> tabContours = new ArrayList<Vertex[]>();
		tabContours.add(tabVerts);
		VectorFontCharacter tab = new VectorFontCharacter(pa, tabContours);
//		VectorFontCharacter tab = new VectorFontCharacter(new Vertex[]{new Vertex(0,0,0), new Vertex(defaultTabWidth,0,0),new Vertex(defaultTabWidth,100,0),new Vertex(0,100,0)}, new ArrayList<Vertex[]>(), pa);
		tab.setPickable(false);
		try {
			IFontCharacter space = svgFont.getFontCharacterByUnicode(" ");
			int tabWidth = 4*space.getHorizontalDist();
			tab.setHorizontalDist(tabWidth);
			tab.setVertices(new Vertex[]{new Vertex(0,0,0), new Vertex(tabWidth,0,0),new Vertex(tabWidth,100,0) /*,new Vertex(0,100,0)*/} );
		} catch (Exception e) {
			tab.setHorizontalDist(defaultTabWidth);
		}
		tab.setUnicode("\t");
		tab.setName("tab");
		tab.setVisible(false);
		tab.setNoFill(true);
		tab.setNoStroke(true);
		characters.add(tab);
		
		//Create a new SVG-FONT
//		VectorFont svgFont = new VectorFont(this.getCharacters(), fontDefaultXAdvancing, fontFamily, fontMaxAscent, fontMaxDescent, font_units_per_em, fontSize,
		VectorFont svgFont = new VectorFont(characters.toArray(new VectorFontCharacter[characters.size()]), fontDefaultXAdvancing, fontFamily, fontMaxAscent, fontMaxDescent, font_units_per_em, fontSize,
				fillColor,
//				strokeColor,
				antiAliased,
				fontFileName
		);
		
		//TODO this caused performance problems - reason UNKOWN! DISABLE IF PERFORMANCE DROPS SIGNIFICANTLY!
		//Set font max descent and ascent according to font size
//		float tmp = fontMaxAscent * (float)(1.0/(float)font_units_per_em);
//		fontMaxAscent = Math.round(tmp * fontSize);
//		float tmp2 = fontMaxDescent * (float)(1.0/(float)font_units_per_em);
//		fontMaxDescent = Math.round(tmp2 * fontSize);
		fontMaxAscent = Math.round(fontMaxAscent * this.scaleFactor);
		fontMaxDescent = Math.round(fontMaxDescent * this.scaleFactor);
		
		svgFont.setFontMaxAscent(fontMaxAscent);
		svgFont.setFontMaxDescent(fontMaxDescent);
		
		svgFont.setFontId(fontID);
		
		this.svgFont = svgFont;
		
//		pathVertexArrays.clear();
//		vertexArrToOutlinesList.clear();
//		cubicBezVertTOQuadricControlPoint.clear();
		characters.clear();
	}

	

	/**
	 * Returns the created SvgFont Object.
	 * 
	 * @return the font
	 */
	public VectorFont getFont() {
		return svgFont;
	}

	/**
	 * Enables debug messages.
	 * @param verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	@Override
	public void skippedEntity(String arg0) throws SAXException {
		super.skippedEntity(arg0);
		if (verbose)
			System.out.println("Skipped entity " + arg0);
	}
	
	@Override
	public void error(SAXParseException arg0) throws SAXException {
		super.error(arg0);
		System.err.println(arg0.getMessage());
	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException {
		super.fatalError(arg0);
		System.err.println(arg0.getMessage());
	}

	@Override
	public void warning(SAXParseException arg0) throws SAXException {
		super.warning(arg0);
		System.err.println(arg0.getMessage());
	}




}
