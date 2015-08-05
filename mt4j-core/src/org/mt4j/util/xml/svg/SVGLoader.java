/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.util.xml.svg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.JPanel;

import org.apache.batik.bridge.AbstractSVGGradientElementBridge;
import org.apache.batik.bridge.AbstractSVGGradientElementBridge.SVGStopElementBridge;
import org.apache.batik.bridge.AbstractSVGGradientElementBridge.Stop;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.PaintServer;
import org.apache.batik.bridge.SVGTextElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.TextUtilities;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.ICCColor;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGGraphicsElement;
import org.apache.batik.dom.svg.SVGOMCircleElement;
import org.apache.batik.dom.svg.SVGOMClipPathElement;
import org.apache.batik.dom.svg.SVGOMDefsElement;
import org.apache.batik.dom.svg.SVGOMEllipseElement;
import org.apache.batik.dom.svg.SVGOMForeignObjectElement;
import org.apache.batik.dom.svg.SVGOMGElement;
import org.apache.batik.dom.svg.SVGOMLineElement;
import org.apache.batik.dom.svg.SVGOMLinearGradientElement;
import org.apache.batik.dom.svg.SVGOMMaskElement;
import org.apache.batik.dom.svg.SVGOMPathElement;
import org.apache.batik.dom.svg.SVGOMPolygonElement;
import org.apache.batik.dom.svg.SVGOMPolylineElement;
import org.apache.batik.dom.svg.SVGOMRadialGradientElement;
import org.apache.batik.dom.svg.SVGOMRectElement;
import org.apache.batik.dom.svg.SVGOMSVGElement;
import org.apache.batik.dom.svg.SVGOMSwitchElement;
import org.apache.batik.dom.svg.SVGOMTSpanElement;
import org.apache.batik.dom.svg.SVGOMTextElement;
import org.apache.batik.dom.svg.SVGOMToBeImplementedElement;
import org.apache.batik.dom.svg.SVGTextContentSupport;
import org.apache.batik.dom.svg.SVGURIReferenceGraphicsElement;
import org.apache.batik.dom.svg12.BindableElement;
import org.apache.batik.dom.svg12.SVGOMFlowRootElement;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint.CycleMethodEnum;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.font.AWTGVTFont;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.parser.PathParser;
import org.apache.batik.parser.TransformListParser;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.clipping.FillPaint;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.shapes.MTStencilPolygon;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.HelperMethods;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.SwingTextureRenderer;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.ConvexityUtil;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11Plus;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GluTrianglulator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGLengthList;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;
import org.w3c.dom.svg.SVGSVGElement;

import processing.core.PApplet;


/**
 * This class can be used to load and display scalable vector graphics (svg) files.
 * 
 * @author Christopher Ruff
 */
public class SVGLoader implements SVGConstants{
	private static final ILogger logger = MTLoggerFactory.getLogger(SVGLoader.class.getName());
	static{
		logger.setLevel(ILogger.ERROR);
	}
	
	/** The svg doc. */
	private SVGDocument    svgDoc;
	
	/** The user agent. */
	private UserAgent      userAgent;
	
	/** The loader. */
	private DocumentLoader loader;
	
	/** The ctx. */
	private BridgeContext  ctx;
	
	/** The builder. */
	private GVTBuilder     builder;
	
	/** The root gn. */
	private GraphicsNode   rootGN;
	
	/** The css engine. */
	protected SVGCSSEngine cssEngine; 
	
	/** The pa. */
	private PApplet pa;
	
	/** The opacity stack. */
	private Stack<Float> opacityStack;
	
	/** The default drag action. */
	private IGestureEventListener defaultDragAction;
	
	/** The default rotate action. */
	private IGestureEventListener defaultRotateAction;
	
	/** The default scale action. */
	private IGestureEventListener defaultScaleAction;
	
	/** The current local transform matrix. */
	private Matrix currentLocalTransformMatrix;

	
	
	/**
	 * Instantiates a new batik svg parser.
	 * 
	 * @param pa the pa
	 */
	public SVGLoader(PApplet pa){
		this.pa = pa;
		
		opacityStack = new Stack<Float>();
		
		currentLocalTransformMatrix = new Matrix();
		
		defaultDragAction 		= new DefaultDragAction();
		defaultRotateAction 	= new DefaultRotateAction();
		defaultScaleAction 		= new DefaultScaleAction();
	}
	
	
	/**
	 * Loads a "*.svg" file, parses it, creates drawable components and returns the
	 * toplevel component.
	 * 
	 * @param filedescr the absolute path of the svg file as a string
	 * 
	 * @return the MT base component
	 * 
	 * the created top level component of the svg
	 */
	public MTComponent loadSvg(String filedescr){
		return this.getCreatedSvgComponents(this.parseSvg(filedescr));
	}
	
	
	/**
	 * Uses the batik parser to genererate an svg document from an svg file.
	 * To create the components in that svg document, call <code>getCreatedSvgComponents(SVGDocument doc)</code>
	 * 
	 * @param filedescr the filedescr
	 * 
	 * @return the SVG document
	 */
	public SVGDocument parseSvg(String filedescr){
        Document doc;
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            
            File file = new File(filedescr);
            if (file.exists()){
                URI localFileAsUri = file.toURI(); 
                String uri = localFileAsUri.toASCIIString();
                doc = f.createDocument(uri);
            }else{
            	InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filedescr);
            	if (in == null){
            		in = pa.getClass().getResourceAsStream(filedescr);
            	}
            	doc = f.createDocument(filedescr, in);
            	
            	 /*on it (after casting it to  SVGOMDocument) to give it a
                URI of some sort.  If the document needs to be able to have relative
                reference to files on the local file system, give it a URI like
                "file:///some/where/file.svg";
                */ 
                //FIXME HACK! this seems to help the "org.apache.batik.bridge.BridgeException: Unable to make sense of URL for connection" error
                //occuring with windmill.svg if loading from inputstream instead of local file system file
                //FIXME but this might create errors when loading external file like images from the relative svg path?
            	doc.setDocumentURI("") ; 
//                String sub = filedescr.substring(0, filedescr.lastIndexOf(MTApplication.separator));
//                System.out.println("F: " + filedescr + " sub; " + sub);
//                svgDoc.setDocumentURI(sub+ MTApplication.separator) ; 
            }
            svgDoc = (SVGDocument)doc;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	        
        //Neccesary? For booting css 
        try{
	        userAgent = new UserAgentAdapter();
	        loader    = new DocumentLoader(userAgent);
	        ctx       = new BridgeContext(userAgent, loader);
	        ctx.setDynamicState(BridgeContext.DYNAMIC); //TODO use static?
	        builder   = new GVTBuilder();
	        rootGN    = builder.build(ctx, svgDoc);
	        
//	        ctx.getCSSEngineForElement(null).
        }catch(Exception e){
        	e.printStackTrace();
        }
        return svgDoc;
	}
	
	/**
	 * Creates and returns components of the provided svg document for displaying.
	 * 
	 * @param svgDoc the svg doc
	 * 
	 * @return the created svg components
	 */
	public MTComponent getCreatedSvgComponents(SVGDocument svgDoc){
		ArrayList<MTComponent> components = new ArrayList<MTComponent>();
		opacityStack.push(1.0f);
		traverseSVGDoc(svgDoc, components);
		opacityStack.pop();
		
		MTComponent[] comps = components.toArray(new MTComponent[components.size()]);
		//Only returning the 1st component, since this should be the top-level <svg> element and only 1!?
		return comps[0];
	}
	
	
	
	/**
	 * Traverse svg doc.
	 * 
	 * @param node the node
	 * @param comps the comps
	 */
	private void traverseSVGDoc(Node node, ArrayList<MTComponent> comps){
		logger.debug("Traversing: " + node.getNodeName());
		
		//Damit transformationen konsistent sind muss
		//jedes tag, da� eine transform attribut hat
		//behandelt werden!
		//Default
		currentLocalTransformMatrix = new Matrix();
		
		//If there is a TRANSFORM attribute parse that and set the 
		//current transformation matrix to be used with the svg components created after
		NamedNodeMap atts = node.getAttributes();
		if (atts != null){
			for (int i = 0; i < atts.getLength(); i++) {
				Node att = atts.item(i);
				if (att.getNodeName().equals(SVG_TRANSFORM_ATTRIBUTE)){
					CustomTransformHandler transFormHandler = new CustomTransformHandler();
					TransformListParser transFormListParser = new TransformListParser();
					transFormListParser.setTransformListHandler(transFormHandler);
					transFormListParser.parse(att.getNodeValue());
					//Overwrite current default matrix if the element has its own 
					//transform defined, will be used at gfx obj creation
					currentLocalTransformMatrix = transFormHandler.getResultMatrix();
				}
			}
		}
		
//		logger.debug("Node: " + node.getNodeName() + " Class: " + node.getClass());
		
		
		//For opacity inheritance
		  if (node instanceof SVGGraphicsElement){
			  SVGGraphicsElement svgGfx = (SVGGraphicsElement)node;
			  //Handle inherited opacity settings
			  float opac = queryPrimitiveFloatValue(svgGfx, "opacity", 1f);
			  opacityStack.push(opac *= opacityStack.peek());
		  }

		  // if G (GROUP) element, add all children to this element
		  if (  node instanceof SVGOMGElement 
			 || node instanceof SVGSVGElement
			 || node instanceof SVGOMSVGElement
		  ){
//			  SVGOMGElement gElem = (SVGOMGElement)node;
			  SVGElement gElem = (SVGElement)node;
			  MTComponent group = new MTComponent(pa);
			  group.setName(gElem.getTagName());

//			  Element viewPort = gElem.getViewportElement();
//			  logger.debug("Viewport " + viewPort.getNodeName());

			  //Set the <g> group to composite, meaning that it will
			  //be returned at picking, when one of the children gets picked
			  group.setComposite(true);

			  group.setLocalMatrix(currentLocalTransformMatrix);
			  
			  //IF its <svg> element get the transform 
			  //(to honor the viewBox and the width/height attributes
			  if (node instanceof SVGOMSVGElement ){ 
				  SVGOMSVGElement svgGom = ((SVGOMSVGElement)node);
				  Element viewPort = svgGom.getViewportElement();
				  if (viewPort != null)
					  logger.debug("Viewport " + viewPort.getNodeName());
				  
//				  SVGMatrix mat = svgGom.getScreenCTM();
				  
				  SVGAnimatedLength widthA = svgGom.getWidth();
				  SVGAnimatedLength heightA = svgGom.getHeight();
				  
				  SVGLength w = widthA.getBaseVal();
				  float width = w.getValue();
				  
				  SVGLength h = heightA.getBaseVal();
				  float height = h.getValue();
				  
				  logger.debug("-> SVG Width: " + width + " Height: " + height);
				  
				  
				  SVGMatrix mat = svgGom.getCTM();
				  /*
				  logger.debug("mat: " + mat.toString());
				  logger.debug(mat.getA());
				  logger.debug(mat.getB());
				  logger.debug(mat.getC());
				  logger.debug(mat.getD());
				  logger.debug(mat.getE());
				  logger.debug(mat.getF());
				  SVGRect bbox = svgGom.getBBox();
				  logger.debug("BBOx: X:" + bbox.getX() + " Y:" + bbox.getY() + " Width:" + bbox.getWidth() + " Height:" + bbox.getHeight());
				  */
				  
				  //Hack, because if no width/height is specified default of 1.0
				  //is assumed by batik -> things may get scaled too small
				  if ( !(width == 1 && height == 1) ){
					  currentLocalTransformMatrix = new Matrix(mat.getA(), mat.getC(), 	0, mat.getE(),
							  								   mat.getB(), mat.getD(), 	0, mat.getF(),
							  								   0, 			0, 			1, 			0,
							  								   0, 			0, 			0, 			1
					  );
					  //logger.debug("Matrix: " + currentLocalTransformMatrix);
					  group.setLocalMatrix(currentLocalTransformMatrix);
				  }
			  }

			  //Make the group pickable and manipulatable 
			  group.setPickable(true);
			  
			  group.registerInputProcessor(new DragProcessor(pa));
			  group.setGestureAllowance(DragProcessor.class, true);
			  group.addGestureListener(DragProcessor.class, (IGestureEventListener)defaultDragAction);
			  
			  group.registerInputProcessor(new RotateProcessor(pa));
			  group.addGestureListener(RotateProcessor.class, defaultRotateAction);
			  
			  group.registerInputProcessor(new ScaleProcessor(pa));
			  group.addGestureListener(ScaleProcessor.class,  defaultScaleAction);
			  
			  ArrayList<MTComponent> groupChildren = new ArrayList<MTComponent>();
			  //Traverse the children and add them to a new arraylist
			  traverseChildren(gElem, groupChildren);
			  
			  MTComponent[] childComps = groupChildren.toArray(new MTComponent[groupChildren.size()]);
			  //Add the children to the group
			  group.addChildren(childComps);
			  //Add the group to the arraylist of the parent
			  comps.add(group);
		  }else{//If NOT GROUP
			  if (node instanceof SVGGraphicsElement){
				  SVGGraphicsElement svgGfxElem = (SVGGraphicsElement)node;
				  //IF node isnt a group node just add it to the passed in comps arraylist
				  try{
					  //Create a component from the graphicsnode and add it to the parents arraylist
					  MTComponent liveComponent = handleGraphicsNode(svgGfxElem);
					  if (liveComponent != null){
						  comps.add(liveComponent);
					  }
				  }catch(Exception e){
					  logger.error("Error handling svg node: " + svgGfxElem.getTagName());
					  e.printStackTrace();
				  }
			  }

			  //FIXME IMPLEMENT
			  if (node instanceof SVGOMTSpanElement){
				  SVGOMTSpanElement tSpanElement = (SVGOMTSpanElement)node;
				  
			  }

			  //FIXME TEST
			  if (node instanceof SVGOMTextElement){
				  boolean useVectorFont = false;
				  
				  SVGOMTextElement textElement = (SVGOMTextElement)node;
				  //Get <text> position values (can be a list)
				  List<Float> xValues = getSVGLengthListAsFloat(textElement.getX().getBaseVal());
				  List<Float> yValues = getSVGLengthListAsFloat(textElement.getY().getBaseVal());
//				  /*//Not used 
				  String textContent = TextUtilities.getElementContent(textElement);
				  textContent = textContent.replaceAll("\\n","");
				  textContent = textContent.trim();
//				   */
				  /*
				  //TODO USE?
				  textElement.getTextLength();
				  textElement.getRotate();
				   */
				  if (textElement.getSVGContext() instanceof SVGTextElementBridge){
					  SVGTextElementBridge b = (SVGTextElementBridge)textElement.getSVGContext();
					  GraphicsNode gr = b.createGraphicsNode(ctx, textElement);
					  TextNode tNode = (TextNode)gr;
					  b.buildGraphicsNode(ctx, textElement, tNode);
					  List<?> textRuns = tNode.getTextRuns();
					  logger.debug("Text runs: " + textRuns);
					  //Get font size
					  float fontSize = b.getFontSize();

					  logger.debug("Text:" +  " x:" + xValues.get(0) + " y:" + yValues.get(0) + " FontSize: " + fontSize +  " Text: '" + textContent + "'");

					  //Get font FILL
					  Value fillOpacValue = CSSUtilities.getComputedStyle(textElement, SVGCSSEngine.FILL_OPACITY_INDEX);
					  float computedfillOpac = PaintServer.convertOpacity(fillOpacValue);
					  Value fillIndexValue = CSSUtilities.getComputedStyle(textElement, SVGCSSEngine.FILL_INDEX);
					  Object fill = SVGLoader.getFillOrStroke(textElement, fillIndexValue, computedfillOpac, ctx);
					  MTColor fillColor = new MTColor(150,150,150,255);
					  if (fill instanceof java.awt.Color) {
						  java.awt.Color color = (Color) fill;
						  fillColor.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
					  }

					  //Get STROKE
					  // Stroke Opacity \\
					  Value strokeOpacValue = CSSUtilities.getComputedStyle(textElement, SVGCSSEngine.STROKE_OPACITY_INDEX);
					  float computedStrokeOpacity = PaintServer.convertOpacity(strokeOpacValue);
					  // Stroke java.awt.Color \\
					  Value strokeIndexValue = CSSUtilities.getComputedStyle(textElement, SVGCSSEngine.STROKE_INDEX);
					  Object stroke = SVGLoader.getFillOrStroke(textElement, strokeIndexValue, computedStrokeOpacity, ctx);
					  MTColor strokeColor = new MTColor(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
					  if (stroke instanceof java.awt.Color) {
						  java.awt.Color color = (Color) stroke;
						  strokeColor.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
					  }

					  //Get the font family
					  Value fontFamilyValue = CSSUtilities.getComputedStyle(textElement, SVGCSSEngine.FONT_FAMILY_INDEX);
					  String fontFamily = "arial"; //DEFAULT
					  if (fontFamilyValue instanceof ListValue) {
						  ListValue listValue = (ListValue) fontFamilyValue;
						  Value firstValue = listValue.item(0); //Can be a List? -> take only the first one..
						  if (firstValue != null)
							  fontFamily = firstValue.getStringValue();
					  }
					  logger.debug("Font family: " + fontFamily);

					  IFont font;
					  if (useVectorFont)
						  //Vector font
						  font = FontManager.getInstance().createFont(pa, 
								  "arial.ttf", Math.round(fontSize), fillColor);
					  else
						  //Bitmap font
						  font = FontManager.getInstance().createFont(pa, 
//								  "Arial", Math.round(fontSize),
								  fontFamily, Math.round(fontSize), fillColor);
//					  /* 

					  IFont fontToUse = font;
					  IFont lastUsedFont = fontToUse;
					  List<MTTextArea> textAreas = new ArrayList<MTTextArea>();

					  AttributedCharacterIterator iter = tNode.getAttributedCharacterIterator();
					  if (font != null && iter != null){ //To avoid not loaded fonts or if text ist empty
						  for (int i = iter.getBeginIndex(); i < iter.getEndIndex(); i++) {
							  char currentChar = iter.setIndex(i);
							  Set<Attribute> keys = iter.getAllAttributeKeys();
							  Map<Attribute, Object> charAtts = iter.getAttributes();
							  
							  Object baseLineShift = charAtts.get(SVGTextElementBridge.BASELINE_SHIFT);
							  Object paintInfo = charAtts.get(SVGTextElementBridge.PAINT_INFO);

							  Object charX = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.X);
							  Object charY = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.Y);
							  Object charDX = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.DX);
							  Object charDY = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.DY);
							  Object charRotation = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.ROTATION);
							  Object gvtFont = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT);
							  Object gvtFonts = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.GVT_FONTS);
							  Object gvtFontFamilies = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT_FAMILIES);
							  Object textCompoundDelimiter = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
							  Object verticalOrientation = charAtts.get(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION);
							  logger.debug("Character: "  + currentChar + " CharX:" + charX + " CharY: " + charY + " CharDX: " +  charDX + " CharDY: " + charDY +  " Font: " + gvtFont +  " Fonts: " + gvtFonts +   " FontFamilies: " + gvtFontFamilies);
							  AWTGVTFont awtGvtFont = (AWTGVTFont)gvtFont;
							  if (awtGvtFont != null)
								  logger.debug("CharfontSize: " + awtGvtFont.getSize());

							  //FIXME REMOVE, Not working always 0,0
							  SVGPoint startPosOfChar = SVGTextContentSupport.getStartPositionOfChar(textElement, i);

							  /////////////////////////////////////
							  //Get the character information - font, colors
							  String newFamilyName = fontFamily;
							  float newFontSize = fontSize;
							  MTColor newFillColor = new MTColor(fillColor);
							  MTColor newStrokeColor = new MTColor(strokeColor);
							  boolean charHasColorInfo = false;
							  boolean charHasFontInfo = false;
							  //Get chars paint info
							  if (paintInfo != null && paintInfo instanceof TextPaintInfo){
								  charHasColorInfo = true;
								  TextPaintInfo texInfo = (TextPaintInfo)paintInfo;
								  if (texInfo.fillPaint instanceof java.awt.Color){
									  java.awt.Color color = (Color)texInfo.fillPaint;
									  newFillColor.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
								  }
								  if (texInfo.strokePaint instanceof java.awt.Color){
									  java.awt.Color color = (Color)texInfo.strokePaint;
									  newStrokeColor.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
								  }
							  }

							  //Get the chars font family and size
							  GVTFont aGvtFont = null;
							  if (gvtFonts!=null){
								  if (gvtFonts instanceof List) {
									  List<?> fonts = (List<?>) gvtFonts;
                                      for (Object o : fonts) {
                                          if (o instanceof GVTFont) {
                                              aGvtFont = (GVTFont) o;
                                              //logger.debug("Char font family: " + aGvtFont.getFamilyName() + " Size:" + aGvtFont.getSize());
                                          }
                                      }
								  }
							  }
							  if (aGvtFont != null){
								  charHasFontInfo = true;
								  newFamilyName = aGvtFont.getFamilyName();
								  newFontSize = aGvtFont.getSize();
							  }else{
								  logger.error("Character: " + currentChar + " has no font attached.");
							  }

							  if (charHasColorInfo && charHasFontInfo){
								  logger.debug("Character '" + currentChar + "'-> has font info -> load font!" +
										  " Family: " + newFamilyName +
										  " Fontsize: " + Math.round(newFontSize) +
										  " FillColor: " + newFillColor +
										  " StrokeColor: " + newStrokeColor);
								  
								  if (useVectorFont)
									  fontToUse = FontManager.getInstance().createFont(pa, 
											  "arial.ttf", Math.round(newFontSize), newFillColor);
								  else
									  fontToUse = FontManager.getInstance().createFont(pa, //uses cached font if available
//											  "Arial", Math.round(fontSize),
											  newFamilyName, Math.round(newFontSize), newFillColor);
								  if (fontToUse == null){
									  fontToUse = font;
								  }
							  }else{
								  fontToUse = font;
							  }
							  boolean fontChanged = !FontManager.isFontsAreEqual(fontToUse, lastUsedFont);
							  lastUsedFont = fontToUse;

//							  //FIXME REMOVE TEST
//							  fontChanged = true;
							  ///////////////////////////////////////
							  boolean textPositionChanged = charX != null || charY != null  || charDX != null || charDY != null;

							  //TODO if we forceAnewTextarea because of font change but ther is NO NEW POSITION, we
							  //have to set the textareas anchor to the lower left
							  //TODO problem if we have a tspan centered and a next tspan without new position
							  //-> the first tspan textarea gets centered on the position 
							  //but we would have to treat them (all in the same line) as 1 textarea when center positioning!
							  
							  //FIXME there are slight differences because we use a different SPACE character length and no font KERNING!
							  
							  //FIXME DO WIDHTOUT USERDATA
							  //FIXME bitmap font has no top border, vector has.. why?
							  //TODO -> eventuell doch in handleSvgNode machen?
							  //-> statt graphicsnode /stylable node �bergeben? - SVGOMTextElement is nicht instanceof graphicsnode..
							  
							  // we have to check font/color etc at every character, not only at new positon because 
							  //pos doesent change at tspans without new posinfo
							  //check if equal to last used font and if equal original text font
							  if ( fontChanged || textPositionChanged					
							  ){ //Make a new textarea if the text position changed or if the font changed at the current character
								  MTTextArea previousTextArea = null;
								  if (!textAreas.isEmpty()){
									  previousTextArea = textAreas.get(textAreas.size()-1);
								  }

								  float newXPos = 0; 
								  float newYPos = 0 ;

								  //If there is a previous text, get its ending coordinates 
								  //for the DX and DY shift info for the next text area
								  if (previousTextArea != null){
									  PositionAnchor oldAnchor = previousTextArea.getAnchor();
//									  previousTextArea.setAnchor(PositionAnchor.LOWER_RIGHT);
									  previousTextArea.setAnchor(PositionAnchor.UPPER_LEFT);
									  //Calculate last/current textposition for DX and DY use
									  //add up the last textareas start position end position(width)
									  Vector3D lastPos = previousTextArea.getPosition(TransformSpace.LOCAL);
//									  lastPos.addLocal(new Vector3D(previousTextArea.getWidthXY(TransformSpace.LOCAL) - 1 * previousTextArea.getInnerPaddingLeft(),0));
									  lastPos.addLocal(new Vector3D(previousTextArea.getWidthXY(TransformSpace.LOCAL) - 2 * previousTextArea.getInnerPaddingLeft(),0));
//									  newXPos = lastPos.x - previousTextArea.getInnerPaddingLeft();
									  newXPos = lastPos.x;
									  newXPos += (Float)previousTextArea.getUserData("XPos");

									  newYPos = lastPos.y;
//									  newYPos -= previousTextArea.getInnerPaddingTop();
//									  newYPos += fontToUse.getFontMaxDescent(); //FIXME WHY NEVESSARY?
									  newYPos += (Float)previousTextArea.getUserData("YPos");
									  previousTextArea.setAnchor(oldAnchor);
								  }

								  //IF absolute x or y is present overwrite the position values from the last textarea
								  if (charX != null)
									  newXPos = (Float)charX;
								  if (charY != null)
									  newYPos = (Float)charY;
								  if (charDX != null)
									  newXPos += (Float)charDX;
								  if (charDY != null)
									  newYPos += (Float)charDY;
								  
								  // Create the text area \\
								  MTTextArea t = new MTTextArea(pa, fontToUse);
								  t.setNoFill(true);
								  t.setNoStroke(true);
								  textAreas.add(t);
								  try{
									  t.setLocalMatrix(new Matrix(currentLocalTransformMatrix));
								  }catch(Exception e){
									  logger.error(e.getMessage());
								  }

								  //FIXME TEST
//								  if (previousTextArea != null && !textPositionChange){
//								  t.setAnchor(PositionAnchor.LOWER_LEFT);
//								  t.setUserData("posRelParent", new Vector3D(newXPos , newYPos - fontToUse.getFontMaxDescent() , 0));
//								  logger.debug("Character '" + currentChar + "' -> Anchor: LOWER_LEFT");
//								  }else{
								  Value v = CSSUtilities.getComputedStyle(textElement, SVGCSSEngine.TEXT_ANCHOR_INDEX);
								  //INFO: we have to move the BASELINE of the text to the svg position
								  //The textarea is usually fontmaxascent+fontmaxdescent+2*innerPadding big!
								  switch (v.getStringValue().charAt(0)) {
								  case 'e':
									  t.setAnchor(PositionAnchor.LOWER_RIGHT);
									  t.setUserData("posRelParent", new Vector3D((newXPos + t.getInnerPaddingLeft()) , newYPos - fontToUse.getFontMaxDescent()  +  t.getInnerPaddingTop() , 0));
//									  t.setPositionRelativeToParent(new Vector3D(newXPos, newYPos - font.getFontMaxDescent() , 0));
									  logger.debug("Character '" + currentChar + "' -> Anchor: LOWER_RIGHT");
									  break;
								  case 'm': //text-anchor="middle"
									  t.setAnchor(PositionAnchor.CENTER);
//									  t.setUserData("posRelParent", new Vector3D(newXPos, newYPos - fontToUse.getFontMaxAscent()*0.5f - fontToUse.getFontMaxDescent()*0.5f , 0));
//									  t.setUserData("posRelParent", new Vector3D(newXPos, newYPos - fontToUse.getFontAbsoluteHeight()*0.5f + t.getInnerPaddingTop() , 0));
//									  t.setPositionRelativeToParent(new Vector3D(newXPos, newYPos - font.getFontMaxAscent()*0.5f - font.getFontMaxDescent()*0.5f, 0)); //- font.getFontMaxAscent()*0.5f
									  logger.debug("Character '" + currentChar + "' -> Anchor: CENTER");
									  t.setUserData("posRelParent", new Vector3D((newXPos), (newYPos - fontToUse.getFontMaxDescent() + t.getInnerPaddingTop()) - t.getHeightXY(TransformSpace.LOCAL)/2f , 0));
									  break;
								  default: //text-anchor="start" //default!
									  t.setAnchor(PositionAnchor.LOWER_LEFT);
//									  t.setUserData("posRelParent", new Vector3D(newXPos -t.getInnerPaddingLeft(), newYPos - fontToUse.getFontMaxDescent() + t.getInnerPaddingTop() , 0));
									  t.setUserData("posRelParent", new Vector3D(newXPos -t.getInnerPaddingLeft(), newYPos - fontToUse.getFontMaxDescent() + t.getInnerPaddingTop() , 0));
									  
//									  t.setAnchor(PositionAnchor.UPPER_LEFT);
//									  t.setUserData("posRelParent", new Vector3D(newXPos -t.getInnerPaddingLeft(), newYPos, 0));
//								  t.setPositionRelativeToParent(new Vector3D(newXPos, newYPos - font.getFontMaxDescent() , 0));
								  logger.debug("Character '" + currentChar + "' -> Anchor: LOWER_LEFT");
								  }
								  t.setUserData("XPos", newXPos); 
								  t.setUserData("YPos", newYPos);
//								  }
							  }
							  //Add character to the current textarea in the list
							  if (!textAreas.isEmpty()){
								  textAreas.get(textAreas.size()-1).appendCharByUnicode(Character.toString(currentChar));
							  }
						  }
						  //Set the positions of the textareas
                          for (MTTextArea textArea : textAreas) {
                              logger.debug("Adding text area at: " + (Vector3D) textArea.getUserData("posRelParent"));
                              textArea.setPositionRelativeToParent((Vector3D) textArea.getUserData("posRelParent"));
                          }
						  comps.addAll(textAreas);
					  }

					  /*
					  //This gets only the text of this hierarchy level
					  StringBuffer result = new StringBuffer();
					  for (Node n = textElement.getFirstChild();
					  n != null;
					  n = n.getNextSibling()) {
						  switch (n.getNodeType()) {
						  case Node.ELEMENT_NODE:
							  break;
						  case Node.CDATA_SECTION_NODE:
						  case Node.TEXT_NODE:
							  result.append(n.getNodeValue());
						  }
					  }
					  logger.debug("TEXTTTT2: " + result);
					   */
//					  */////////////////////

				  }
			  }
		  }


		if (node instanceof SVGGraphicsElement){
			  //Remove inherited opacity attribute from stack
			  opacityStack.pop();
		}
		
		//Traverse the children, not if it was a group element 
		//because then the children are already
		//traversed in the if (group) block above
		if (   !(node instanceof SVGOMGElement)
			&& !(node instanceof SVGSVGElement) 
			&& !(node instanceof SVGOMSVGElement) 
		){
		  traverseChildren(node, comps);
		}
	}
	
	
	
	/**
	 * Traverse children.
	 * 
	 * @param node the node
	 * @param comps the comps
	 */
	private void traverseChildren(Node node, ArrayList<MTComponent> comps){
				
		//Check the children
		NodeList nl = node.getChildNodes();
		  for (int i = 0; i < nl.getLength(); i++) {
			Node currentNode = nl.item(i);
			traverseSVGDoc(currentNode, comps);
		}
	}
	
	
	/**
	 * Handle graphics node.
	 * 
	 * @param gfxElem the gfx elem
	 * 
	 * @return the mT base component
	 */
	private MTComponent handleGraphicsNode(SVGGraphicsElement gfxElem){
		  MTComponent returnComp = null;
//		  logger.debug("Handle Element: " + gfxElem.getTagName());
		  
		  //Print all css properties and values
//		  logger.debug("Style Css Text: " + style.getCssText());
		  
		  // SVG Defaults \\
		  float fillR 			= 255;
		  float fillG 			= 255;
		  float fillB 			= 255;
		  boolean noFill 		= false;
		  float strokeR 		= 0;
		  float strokeG 		= 0;
		  float strokeB 		= 0;
		  float strokeWidth 	= 1.0f;
		  boolean noStroke 		= false;
		  float strokeOpacity 	= 1;
		  float fillOpacity   	= 1;
		  int windingRule 		= GluTrianglulator.WINDING_RULE_NONZERO;
		  // SVG Defaults \\
		  
		  
		  // Opacity, not as a style attribute but a separate 
		  // as group opacity doesnt get computed right, so we 
		  // mannually track it on a stack
		  float opacity = opacityStack.peek();
		  //logger.debug("INHERITED OPACITY: " + opacity);
		  
		  
		  // FILL-RULE \\
		  Value fillRuleValue = CSSUtilities.getComputedStyle(gfxElem, SVGCSSEngine.FILL_RULE_INDEX);
		  String fillRule = fillRuleValue.getStringValue();
		  if (fillRule.equalsIgnoreCase("nonzero")){
			  windingRule = GluTrianglulator.WINDING_RULE_NONZERO;
		  }else if (fillRule.equalsIgnoreCase("evenodd")){
			  windingRule = GluTrianglulator.WINDING_RULE_ODD;
		  }else{
			  windingRule = GluTrianglulator.WINDING_RULE_NONZERO;
		  }
		  //logger.debug("fillRule: " + fillRule);
		  
		  
		  // Fill Opacity \\
		  fillOpacity =  PaintServer.convertOpacity(CSSUtilities.getComputedStyle(gfxElem, SVGCSSEngine.FILL_OPACITY_INDEX));
		  //Multiplicate inherited opacity with this components opacities
		  fillOpacity 	*= opacity;
		  //Save for eventual lineargradient creation later that needs the not interpolated value
		  float originalFillOpacity = fillOpacity;
		  //logger.debug("fill opacity unnormalized: " + fillOpacity);
		  
		  
		  // Fill java.awt.Color \\
		  Value fillIndexValue = CSSUtilities.getComputedStyle(gfxElem, SVGCSSEngine.FILL_INDEX);
		  Object fill = SVGLoader.getFillOrStroke(gfxElem, fillIndexValue, fillOpacity, ctx);
		  SVGOMLinearGradientElement linearGradient = null;
		  SVGOMRadialGradientElement radialGradient = null;
		  if (fill instanceof java.awt.Color) {
			  java.awt.Color color = (Color) fill;
			  fillR = color.getRed();
			  fillG = color.getGreen();
			  fillB = color.getBlue();
			  fillOpacity = color.getAlpha();
			  noFill = false;
			  //logger.debug("Fill: " + color +  " a=" + fillOpacity);
		  }else if (fill instanceof SVGOMLinearGradientElement) {
          	//TODO cache gradients so dass man nicht immer neu den gleichen
          	//machen muss!
			  linearGradient = (SVGOMLinearGradientElement) fill;
			  noFill = false;
		  }else if (fill instanceof SVGOMRadialGradientElement) {
			//TODO!! //FIXME TEST
			  radialGradient = (SVGOMRadialGradientElement)fill;
			  noFill = false;
		  }else{
			  noFill = true;
		  }
		  
		  
		  // Stroke Opacity \\
		  strokeOpacity = PaintServer.convertOpacity(CSSUtilities.getComputedStyle(gfxElem, SVGCSSEngine.STROKE_OPACITY_INDEX));
		  // Multiplicate inherited opacity with this components group opacities
		  strokeOpacity *= opacity;
		  
		  
		  // Stroke java.awt.Color \\
		  Value strokeIndexValue = CSSUtilities.getComputedStyle(gfxElem, SVGCSSEngine.STROKE_INDEX);
		  Object stroke = SVGLoader.getFillOrStroke(gfxElem, strokeIndexValue, strokeOpacity, ctx);
		  if (stroke instanceof java.awt.Color) {
			  java.awt.Color color = (Color) stroke;
			  strokeR = color.getRed();
			  strokeG = color.getGreen();
			  strokeB = color.getBlue();
			  strokeOpacity = color.getAlpha();
			  noStroke = false;
		  }else{
			  noStroke = true;
			  strokeR = fillR;
			  strokeG = fillG;
			  strokeB = fillB;
		  }
		  
		  
		  // Stroke Width \\
		  Stroke s = PaintServer.convertStroke(gfxElem);
		  if (s != null){
			  if (s instanceof BasicStroke) {
				  BasicStroke basicStroke = (BasicStroke) s;
				  strokeWidth = basicStroke.getLineWidth();
			  }
		  }else{
			  strokeWidth = 0.0f;
			  noStroke = true;
		  }
		  
		 /*
		 logger.debug("Fill COL: " + fillR + " " + fillG + " " + fillB + " " fillopacity);
		 logger.debug("STROKE COL: " + strokeR + " " + strokeG + " " + strokeB + " " strokeOpacity);
		 */
		  
		  // CHECK WHAT KIND OF GRAPHICS ELEMENT IT IS AND CREATE IT \\
		  if (gfxElem instanceof SVGOMPathElement){
			  SVGOMPathElement pathElem = (SVGOMPathElement)gfxElem;
			  
			  //FIXME handle clip-paths in the future
			  if (isUnderClipPath(pathElem)){
				  logger.error("Discarding clip-path path element. Not implemented.");
				  return null;
			  }
			  
			  //Create the shape
			  AbstractShape pathComp = getLivePathComponent(pathElem, noFill, windingRule);
			  
			  try{
				  pathComp.setLocalMatrix(currentLocalTransformMatrix);
			  }catch(Exception e){
				  logger.error(e.getMessage());
			  }
			  returnComp = pathComp;
		  }else if (gfxElem instanceof SVGOMPolygonElement){
			  SVGOMPolygonElement polygonElem = (SVGOMPolygonElement)gfxElem;
			  
			  //Create the shape
			  AbstractShape comp = getLivePolygonComponent(polygonElem, noFill, windingRule);
			  			  
			  try{
				  comp.setLocalMatrix(currentLocalTransformMatrix);
			  }catch(Exception e){
				  logger.error(e.getMessage());
			  }
			  returnComp = comp;
		  }else if (gfxElem instanceof SVGOMPolylineElement){
			  SVGOMPolylineElement polyLineElem = (SVGOMPolylineElement)gfxElem;
			  
			  //Create Vertex[] from points
			  SVGPointList pointList = polyLineElem.getPoints();
			  Vertex[] vertices = new Vertex[pointList.getNumberOfItems()];
			  for (int i = 0; i < pointList.getNumberOfItems(); i++) {
				SVGPoint p = pointList.getItem(i);
				vertices[i] = new Vertex(p.getX(), p.getY(),0);
			  }
			  
			  //Create the shape
			  AbstractShape comp = createPoly(vertices);
			  
			  try{
				  comp.setLocalMatrix(currentLocalTransformMatrix);
			  }catch(Exception e){
				  logger.error(e.getMessage());
			  }
			  returnComp = comp;
		  }else if (gfxElem instanceof SVGOMRectElement){ 
			  SVGOMRectElement rectElem = (SVGOMRectElement)gfxElem;
			  if (isUnderClipPath(rectElem)){
				  logger.error("discarding clip-path Rect");
				  return null;
			  }
			  
			  float x 		= rectElem.getX().getBaseVal().getValue();
			  float y 		= rectElem.getY().getBaseVal().getValue();
			  float width 	= rectElem.getWidth().getBaseVal().getValue();
			  float height 	= rectElem.getHeight().getBaseVal().getValue();
			  float rx 		= rectElem.getRx().getBaseVal().getValue();
			  float ry 		= rectElem.getRy().getBaseVal().getValue();
			  
			  AbstractShape comp;
			  //Create a normal rectangle or a round rectangle
			  if (rx != 0.0f || ry != 0.0f){
				  if (rx > width/2 )
					  rx = width/2;
				  if (ry > height/2 )
					  ry = height/2;
				  comp = new MTRoundRectangle(pa,x,y, 0,width,height, rx, ry);
			  }else{
				  comp = new MTRectangle(pa,x, y,width, height); 
			  }
			  
			  try{
				  comp.setLocalMatrix(currentLocalTransformMatrix);
			  }catch(Exception e){
				  logger.error(e.getMessage());
			  }
			  returnComp = comp;
		  }else if (gfxElem instanceof SVGOMEllipseElement){ 
			  SVGOMEllipseElement ellipseElem = (SVGOMEllipseElement)gfxElem;
			  float cx = ellipseElem.getCx().getBaseVal().getValue();
			  float cy = ellipseElem.getCy().getBaseVal().getValue();
			  float r  = ellipseElem.getRx().getBaseVal().getValue();
			  float r2 = ellipseElem.getRy().getBaseVal().getValue();
			  
			  Vertex middlePoint = new Vertex(cx,cy,0);
			  //Apply transformation, transform centerpoint and the radii
			  try{
				  middlePoint.transform(currentLocalTransformMatrix);
			  }catch(Exception e){
				  logger.error(e.getMessage());
			  }
			  
			  //somehow the circle radii need to be doubled
			  //or else theyre too small => processing bug?
//			  r*=2;
//			  r2*=2;
			  MTEllipse comp = new MTEllipse(pa, middlePoint, r, r2);
			  returnComp = comp;
		  }else if (gfxElem instanceof SVGOMCircleElement){ 
			  SVGOMCircleElement circleElem = (SVGOMCircleElement)gfxElem;
			  float cx = circleElem.getCx().getBaseVal().getValue();
			  float cy = circleElem.getCy().getBaseVal().getValue();
			  float r = circleElem.getR().getBaseVal().getValue();
			  float r2 = circleElem.getR().getBaseVal().getValue();
			  
			  Vertex middlePoint = new Vertex(cx,cy,0);
			  //Apply transformation, transform centerpoint and the radii
			  try{
				  middlePoint.transform(currentLocalTransformMatrix);
			  }catch(Exception e){
				  logger.error(e.getMessage());
			  }
			  
			  //somehow the circle radii need to be doubled
			  //or else theyre too small => processing bug?
//			  r*=2;
//			  r2*=2;
			  MTEllipse comp = new MTEllipse(pa, middlePoint, r, r2);
			  returnComp = comp;
		  }else if (gfxElem instanceof SVGOMLineElement){
			  SVGOMLineElement line = (SVGOMLineElement)gfxElem;
			  float x1 = line.getX1().getBaseVal().getValue();
			  float y1 = line.getY1().getBaseVal().getValue();
			  float x2 = line.getX2().getBaseVal().getValue();
			  float y2 = line.getY2().getBaseVal().getValue();
			  //logger.debug("Line x1: " + x1 + ",y1:" + y1 + ",x2:" + x2 + ",y2:" + y2);
			  
			  MTLine comp = new MTLine(pa, x1,y1 ,x2,y2);
			 
			  try{
				  comp.setLocalMatrix(currentLocalTransformMatrix);
			  }catch(Exception e){
				  logger.error(e.getMessage());
			  }
			  returnComp = comp;
		  }else if (gfxElem instanceof SVGOMClipPathElement){ 
		  }else if (gfxElem instanceof SVGOMDefsElement){ 
		  }else if (gfxElem instanceof SVGOMMaskElement){ 
		  }else if (gfxElem instanceof SVGOMSwitchElement){ 
		  }else if (gfxElem instanceof SVGOMFlowRootElement){ 
		  }else if (gfxElem instanceof SVGURIReferenceGraphicsElement){ 
		  }else if (gfxElem instanceof BindableElement){ 
		  }else if (gfxElem instanceof SVGOMForeignObjectElement){ 
		  }else if (gfxElem instanceof SVGOMToBeImplementedElement){ 
		  }
		  
		  //Do the finishing touch of the svg graphics element
		  if (returnComp != null){
			  returnComp.setName(gfxElem.getTagName());
			  
			  //Set style infos
			  if (returnComp instanceof AbstractVisibleComponent){
				  AbstractVisibleComponent comp = (AbstractVisibleComponent)returnComp;
				  //Set Fill
				  comp.setFillColor(new MTColor(fillR, fillG, fillB, fillOpacity));
				  comp.setNoFill(noFill);
				  //Set Stroke
				  comp.setStrokeColor(new MTColor(strokeR, strokeG, strokeB, strokeOpacity));
				  //Opengl cant handle big lines well
				  //So cap at width 3
				  if (strokeWidth > 2.0f)
					  strokeWidth = 2.0f;
				  comp.setStrokeWeight(strokeWidth);
				  comp.setNoStroke(noStroke);
				  //Other
				  comp.setDrawSmooth(true);
				  comp.setPickable(false);
				  
				  //Hack for smoothing non stroked components with a stroke same as fillcolor
				  if (comp.isNoStroke()
					 && linearGradient == null
				  ){  
					  comp.setStrokeColor(new MTColor(fillR, fillG, fillB, fillOpacity)); //fillOpacity
					  comp.setStrokeWeight(0.6f);
					  //Ellipse doesent smooth right with 0.1f strokeweight
					  if (comp instanceof MTEllipse){
						  comp.setStrokeWeight(1.0f);
					  }
					  comp.setNoStroke(false);
				  }

				  //Some settings for Geometric shapes (actually should all be)
				  if (comp instanceof AbstractShape ){
					  AbstractShape shape = (AbstractShape)comp;
					  //Set a bounding rectangle to check first at picking
					  if (shape.getVerticesLocal().length >= 3){
						  shape.setBoundsBehaviour(AbstractShape.BOUNDS_CHECK_THEN_GEOMETRY_CHECK);
						  //shape.setBoundingShape(new BoundsZPlaneRectangle(shape)); //already done by override, (ie svgpoly)
						  
						  //Create amd apply the linear gradient if existant and if we are in opengl rendering mode
						  if (MT4jSettings.getInstance().isOpenGlMode()){
							  if (linearGradient != null){
								  FillPaint gradient = this.createLinearGradient(linearGradient, gfxElem, originalFillOpacity, shape);
								  if (gradient != null){
									  shape.setFillPaint(gradient);
								  }
							  }
							  if (radialGradient != null){
								  FillPaint gradient = this.createRadialGradient(radialGradient, gfxElem, opacity, shape);
								  if (gradient != null){
									  shape.setFillPaint(gradient);
								  }
							  }
							//Per default use direct gl drawing and displaylists in OGL mode
							if (pa instanceof AbstractMTApplication) {
								AbstractMTApplication app = (AbstractMTApplication) pa;
								app.invokeLater(new InvokeLaterAction(shape));
							}
						  }
						  //IF shape has no or only 1 vertex return null
					  }else if (shape.getVerticesLocal().length < 2){
						  return null;
					  }else{
						  shape.setBoundsBehaviour(AbstractShape.BOUNDS_DONT_USE);
						  shape.setBounds(null);
//						  shape.setUseDirectGL(false);
					  }
					  
					  //Allow for picking the shape
					  shape.setPickable(true);
					  
					  //Assign default gestures 
//					  shape.assignGestureClassAndAction(DragGestureAnalyzer.class, defaultDragAction);
//					  shape.registerInputAnalyzer(new DragDetector(pa));
//					  shape.setGestureAllowance(DragDetector.class, true);
//					  shape.addGestureListener(DragDetector.class, (IGestureEventListener)defaultDragAction);
//					  shape.registerInputAnalyzer(new RotationDetector(pa));
//					  shape.addGestureListener(RotationDetector.class, new DefaultRotateAction());
//					  shape.registerInputAnalyzer(new ScaleDetector(pa));
//					  shape.addGestureListener(ScaleDetector.class,  new DefaultScaleAction());
				  }
			  }
		  }
		  return returnComp;
	}
	
	
	private class InvokeLaterAction implements Runnable{
		private AbstractShape shape;
		public InvokeLaterAction(AbstractShape shape) {
			super();
			this.shape = shape;
		}
		//@Override	
		public void run() {
			shape.setUseDirectGL(true);
		    shape.generateAndUseDisplayLists();
		}
		
	}
	
	/**
     *
     * @param paintedElement the element interested in a Paint
     * @param paintDef the paint definition
     * @param opacity the opacity to consider for the Paint
     * @param ctx the bridge context
     */
    public static Object getFillOrStroke(Element paintedElement,
                                        Value paintDef,
                                        float opacity,
                                        BridgeContext ctx) {
        if (paintDef.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            switch (paintDef.getPrimitiveType()) {
            case CSSPrimitiveValue.CSS_IDENT:
                return null; // none
            case CSSPrimitiveValue.CSS_RGBCOLOR:
                return PaintServer.convertColor(paintDef, opacity);
            case CSSPrimitiveValue.CSS_URI:
            	 String uri = paintDef.getStringValue();
	                Element paintElement = ctx.getReferencedElement(paintedElement, uri);
	                //logger.debug("Fill -> Uri: \"" + uri + "\" -> Referenced Element: \"" + paintElement.getNodeName() + "\" Class: \"" + paintElement.getClass() + "\"");
	                if (paintElement instanceof SVGOMLinearGradientElement){
	                	SVGOMLinearGradientElement linearGradient = (SVGOMLinearGradientElement)paintElement;
	                	return linearGradient;
//	                	Bridge bridge = ctx.getBridge(paintElement);
//	                	logger.debug("Bridge: " + bridge.getLocalName() + " Class: " + bridge.getClass());
//	                	SVGLinearGradientElementBridge l = (SVGLinearGradientElementBridge)bridge;
//	                	SVGOMLinearGradientElement linearGradient = (SVGOMLinearGradientElement)paintElement;
//	                	SVGAnimatedEnumeration spreadMethod = linearGradient.getSpreadMethod();
	                }else  if (paintElement instanceof SVGOMRadialGradientElement){
	                	SVGOMRadialGradientElement radialGradElement = (SVGOMRadialGradientElement)paintElement;
//	                	logger.error("Radial gradient encountered -> Not supported yet.");
	                	return radialGradElement;
	                }else{
	                	logger.error("Couldnt read referenced Fill or Stroke from URI.");
	                	return null;
	                }
            	/*
                return PaintServer.convertURIPaint(paintedElement,
                                       paintedNode,
                                       paintDef,
                                       opacity,
                                       ctx);
            	 */
            default:
                throw new IllegalArgumentException
                    ("Paint argument is not an appropriate CSS value");
            }
        } else { // List
            Value v = paintDef.item(0);
            switch (v.getPrimitiveType()) {
            case CSSPrimitiveValue.CSS_RGBCOLOR:
                return PaintServer.convertRGBICCColor(paintedElement, v,
                                          (ICCColor)paintDef.item(1),
                                          opacity, ctx);

            case CSSPrimitiveValue.CSS_URI: {
//                Paint result = PaintServer.silentConvertURIPaint(paintedElement,
//                                                     paintedNode,
//                                                     v, opacity, ctx);
//                if (result != null) 
//                	return result;
            	
            	String uri = v.getStringValue();
                Element paintElement = ctx.getReferencedElement(paintedElement, uri);
                //logger.debug("Fill -> Uri: \"" + uri + "\" -> Referenced Element: \"" + paintElement.getNodeName() + "\" Class: \"" + paintElement.getClass() + "\"");
                
                if (paintElement instanceof SVGOMLinearGradientElement){
                	SVGOMLinearGradientElement linearGradient = (SVGOMLinearGradientElement)paintElement;
                	return linearGradient;
                }else  if (paintElement instanceof SVGOMRadialGradientElement){
                	SVGOMRadialGradientElement radialGradElement = (SVGOMRadialGradientElement)paintElement;
//                	logger.error("Radial gradient encountered -> Not supported yet.");
                	return radialGradElement;
                }

                v = paintDef.item(1);
                switch (v.getPrimitiveType()) {
                case CSSPrimitiveValue.CSS_IDENT:
                    return null; // none
                case CSSPrimitiveValue.CSS_RGBCOLOR:
                    if (paintDef.getLength() == 2) {
                        return PaintServer.convertColor(v, opacity);
                    } else {
                        return PaintServer.convertRGBICCColor(paintedElement, v,
                                                  (ICCColor)paintDef.item(2),
                                                  opacity, ctx);
                    }
                default:
                    throw new IllegalArgumentException
                        ("Paint argument is not an appropriate CSS value");
                }
            }
            default:
                // can't be reached
                throw new IllegalArgumentException
                    ("Paint argument is not an appropriate CSS value");
            }
        }
    }

    
    //TODO
	// - spreadMethod implement linear
    // - linearGradient klasse machen mit der man ein gradient erstellen kann
    //	 mit stops[] offsets[] colors[] xy, bbox/userSpace
    // - wie shapes ohne stroke mit outline= gradient zeichnen f�r antialiasing?
    //	 evtl gradientshape normal zeichnen, aber mit realshape clipmasken?
    
    private FillPaint createRadialGradient(Element paintElement, SVGGraphicsElement gfxElem, float opacity, AbstractShape shape){
    	//Get the <stop> elements
        List<Stop> stops = this.extractStops(paintElement, opacity, ctx);
        // if no stops are defined, painting is the same as 'none'
        if (stops == null) {
            return null;
        }
        int stopLength = stops.size();
        // if one stops is defined, painting is the same as a single color
        if (stopLength == 1) {
        	return null;
        }

        float [] offsets = new float[stopLength];
        java.awt.Color [] colors  = new java.awt.Color[stopLength];
        Iterator<Stop> iter = stops.iterator();
        for (int i=0; iter.hasNext(); ++i) {
        	Stop stop = iter.next();
        	offsets[i] = stop.offset;
        	colors[i] = stop.color;
        }
        
        //Get the spread method of the gradient 
        MultipleGradientPaint.CycleMethodEnum spreadMethod = getSpreadMethod(paintElement);
        
        //'color-interpolation' CSS property
        MultipleGradientPaint.ColorSpaceEnum colorSpace = CSSUtilities.convertColorInterpolation(paintElement);

        //Get the gradient transform - //'gradientTransform' attribute - default is an Identity matrix
        AffineTransform transform = getGradientTransform(paintElement);
        
        //////////////////////////////////buildgradient function
        // 'cx' attribute - default is 50%
        String cxStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_CX_ATTRIBUTE, ctx);
        if (cxStr.length() == 0) {
            cxStr = SVG_RADIAL_GRADIENT_CX_DEFAULT_VALUE;
        }
        // 'cy' attribute - default is 50%
        String cyStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_CY_ATTRIBUTE, ctx);
        if (cyStr.length() == 0) {
            cyStr = SVG_RADIAL_GRADIENT_CY_DEFAULT_VALUE;
        }
        // 'r' attribute - default is 50%
        String rStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_R_ATTRIBUTE, ctx);
        if (rStr.length() == 0) {
            rStr = SVG_RADIAL_GRADIENT_R_DEFAULT_VALUE;
        }
        // 'fx' attribute - default is same as cx
        String fxStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_FX_ATTRIBUTE, ctx);
        if (fxStr.length() == 0) {
            fxStr = cxStr;
        }
        // 'fy' attribute - default is same as cy
        String fyStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_FY_ATTRIBUTE, ctx);
        if (fyStr.length() == 0) {
            fyStr = cyStr;
        }

        // 'gradientUnits' attribute - default is objectBoundingBox
        short coordSystemType;
        String s = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_GRADIENT_UNITS_ATTRIBUTE, ctx);
        if (s.length() == 0) {
            coordSystemType = SVGUtilities.OBJECT_BOUNDING_BOX;
        } else {
            coordSystemType = SVGUtilities.parseCoordinateSystem(paintElement, SVG_GRADIENT_UNITS_ATTRIBUTE, s, ctx);
        }
        
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, paintElement);

        float r = SVGUtilities.convertLength(rStr,
                                             SVG_R_ATTRIBUTE,
                                             coordSystemType,
                                             uctx);
        // A value of zero will cause the area to be painted as a single color
        // using the color and opacity of the last gradient stop.
        if (r == 0) {
        	return null;
        } 

        Point2D c = SVGUtilities.convertPoint(cxStr,
        		SVG_CX_ATTRIBUTE,
        		cyStr,
        		SVG_CY_ATTRIBUTE,
        		coordSystemType,
        		uctx);

        Point2D f = SVGUtilities.convertPoint(fxStr,
        		SVG_FX_ATTRIBUTE,
        		fyStr,
        		SVG_FY_ATTRIBUTE,
        		coordSystemType,
        		uctx);
        
        //Get gradient vector
        logger.debug("C: " + c + " F: " +f);
        
        CycleMethod awtCycleMethod = CycleMethod.NO_CYCLE;
        if (spreadMethod == MultipleGradientPaint.REPEAT){
        	awtCycleMethod = CycleMethod.REPEAT;
        }else if(spreadMethod ==  MultipleGradientPaint.REFLECT){
        	awtCycleMethod = CycleMethod.REFLECT;
        }

        if (pa instanceof AbstractMTApplication) {
        	AbstractMTApplication app = (AbstractMTApplication) pa;

        	//Calculate a bounding rectangle from the rotated shape
        	BoundsZPlaneRectangle boundsZ = new BoundsZPlaneRectangle(shape, shape.getVerticesLocal());
        	Vector3D[] boundsVecs = boundsZ.getVectorsLocal();
        	float bBoxWidth  = boundsZ.getWidthXY(TransformSpace.LOCAL);//boundsVecs[1].x - boundsVecs[0].x;
        	float bBoxHeight = boundsZ.getHeightXY(TransformSpace.LOCAL);//boundsVecs[2].y - boundsVecs[1].y;

        	SwingTextureRenderer swingTex;
        	final MTRectangle rectangle;

        	//Trial to make the texture as big as the bigger side of the bounding rectangle of the shape
        	//to allow for automatic texture stretching to fit when texture is applied
        	int size = -1; 
        	if (coordSystemType == SVGUtilities.OBJECT_BOUNDING_BOX){ 
        		if (bBoxWidth >= bBoxHeight){
        			size = Math.round(bBoxWidth);
        			r *= bBoxWidth;
        		}else{
        			size = Math.round(bBoxHeight);
        			r *= bBoxHeight;
        		}

        		AffineTransform Mx = new AffineTransform();
        		Rectangle2D bounds = new Rectangle(Math.round(boundsVecs[0].x), Math.round(boundsVecs[0].y), size, size);
        		if (bounds != null) {
        			//we dont translate the center and focal point
        			//instead we create the gradient mask shape at that position
//      			Mx.translate(bounds.getX(), bounds.getY());
        			Mx.scale(bounds.getWidth(), bounds.getHeight());
        		}
        		Mx.concatenate(transform);
        		transform = Mx;

        		//Transform gradient vector points with gradientTransform 
        		transform.transform(c, c);
        		transform.transform(f, f);

        		GradientPanel gradPanel = new GradientPanel(size, size, r, offsets, colors, (float)c.getX(), (float)c.getY(), (float)f.getX(), (float)f.getY(), awtCycleMethod);
//      		GradientPanel gradPanel = new GradientPanel(bBoxWidth, bBoxHeight, r, offsets, colors, (float)c.getX(), (float)c.getY(), (float)f.getX(), (float)f.getY());
        		swingTex = new SwingTextureRenderer(app, gradPanel);
        		swingTex.scheduleRefresh();
        		rectangle = new MTRectangle(pa, new Vertex(boundsVecs[0]), bBoxWidth, bBoxHeight);
        		rectangle.setName("Swing texture rendering");
        		rectangle.setTexture(swingTex.getTextureToRenderTo());
        		rectangle.setNoStroke(true);
        		rectangle.setPickable(false);
        		rectangle.setFillDrawMode(GL11Plus.GL_QUADS);

        		//Use displaylist by default for gradientshape
        		if (MT4jSettings.getInstance().isOpenGlMode()){
        			app.invokeLater(new InvokeLaterAction(rectangle));
        		}

        		//FIXME REMOVE TEST
        		/*//Draw the shape we draw in swing
					MTRectangle rectanglePaintedComp = new MTRectangle(new Vertex(boundsVecs[0]), size, size, pa);
					rectanglePaintedComp.setName("rectanglePaintedComp");
					rectanglePaintedComp.setTexture(swingTex.getTextureToRenderTo());
					rectanglePaintedComp.setFillColor(255, 255, 255, 150);
					shape.addChild(rectanglePaintedComp);
        		 */
        	}else{
        		//coordsystemtype = userSpaceOnUse!

        		//FIXME Problem at userOnSpace with proportional length (%)
        		//seems we have to take the width/height from the viewbox then!? and use bounding box code above? but we have to recalculate absoulte values then..

        		//Since we draw the gradient at 0,0 we have to transform the gradient points to there
        		AffineTransform Mx = new AffineTransform();
        		Mx.translate(-boundsVecs[0].x, -boundsVecs[0].y);
        		Mx.concatenate(transform);
        		transform = Mx;

        		//Transform gradient points with gradientTransform 
        		transform.transform(c, c);
        		transform.transform(f, f);

//      		GradientPanel gradPanel = new GradientPanel(size, size, r, offsets, colors, (float)c.getX(), (float)c.getY(), (float)f.getX(), (float)f.getY());
        		GradientPanel gradPanel = new GradientPanel(bBoxWidth, bBoxHeight, r, offsets, colors, (float)c.getX(), (float)c.getY(), (float)f.getX(), (float)f.getY(), awtCycleMethod);
        		swingTex = new SwingTextureRenderer(app, gradPanel);
        		swingTex.scheduleRefresh();
        		rectangle = new MTRectangle(pa, new Vertex(boundsVecs[0]), bBoxWidth, bBoxHeight);
        		final GLTexture tex = swingTex.getTextureToRenderTo();
        		rectangle.setName("Swing texture rendering");
        		rectangle.setTexture(tex);
        		rectangle.setNoStroke(true);
        		rectangle.setPickable(false);

//      		/*//
        		if (MT4jSettings.getInstance().isOpenGlMode()){
        			app.invokeLater(new InvokeLaterAction(rectangle));
        		}
//      		*/
        		//FIXME REMOVE TEST
        		/*//Draw the shape we draw in swing
					MTRectangle rectanglePaintedComp = new MTRectangle(new Vertex(boundsVecs[0]), bBoxWidth, bBoxHeight, pa);
					rectanglePaintedComp.setName("rectanglePaintedComp");
					rectanglePaintedComp.setTexture(swingTex.getTextureToRenderTo());
					rectanglePaintedComp.setFillColor(255, 255, 255, 150);
					shape.addChild(rectanglePaintedComp);
        		 */
        	}
//        	FillPaint gradStencil = new FillPaint(((PGraphicsOpenGL)pa.g).gl, rectangle);
        	FillPaint gradStencil = new FillPaint(PlatformUtil.getGL(), rectangle);
        	return gradStencil;
//      	return null;
        }
        return null;
    }


    
    /**
     * Helper class to paint a radial gradient with java2D into a texture.
     */
    private class GradientPanel extends JPanel{
    	private float width;
    	private float height;
    	
    	private float[] offsets;
    	private java.awt.Color[] colors;
    	private float cx;
    	private float cy;
    	private float fx;
    	private float fy;
    	private float radius;
    	
    	private CycleMethod cycleMethod;
    	
		public GradientPanel(float width, float height, float radius, float[] offsets,
				Color[] colors, float cx, float cy, float fx, float fy,
				CycleMethod cycleMethod
			) {
			super();
			this.width = width;
			this.height = height;
			this.radius = radius;
			this.offsets = offsets;
			this.colors = colors;
			this.cx = cx;
			this.cy = cy;
			this.fx = fx;
			this.fy = fy;
			this.cycleMethod = cycleMethod;
			this.setSize(Math.round(width), Math.round(height));
		}

		protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D)g;
	        int w = getWidth();
	        int h = getHeight();
	        Rectangle r = new Rectangle(0, 0, w, h); //original
	        RadialGradientPaint rgp = new RadialGradientPaint(
	        		cx, cy,
//	        		400, 250,
					radius,
//	        		100,
					fx, fy,
//					400, 250,
                    offsets,
                    colors,
					cycleMethod);
	        g2.setPaint(rgp);
	        g2.fill(r);
	    }
	}

	private FillPaint createLinearGradient(Element paintElement, SVGGraphicsElement gfxElem, float opacity, AbstractShape shape){
        // stop elements
        List<Stop> stops = this.extractStops(paintElement, opacity, ctx);
        // if no stops are defined, painting is the same as 'none'
        if (stops == null) {
            return null;
        }
        int stopLength = stops.size();
        // if one stops is defined, painting is the same as a single color
        if (stopLength == 1) {
        	return null;
        }
        
        //Get the spread method of the gradient 
        MultipleGradientPaint.CycleMethodEnum spreadMethod = getSpreadMethod(paintElement);

        //'color-interpolation' CSS property
        MultipleGradientPaint.ColorSpaceEnum colorSpace = CSSUtilities.convertColorInterpolation(paintElement);

        //Get the gradient transform - //'gradientTransform' attribute - default is an Identity matrix
        AffineTransform transform = getGradientTransform(paintElement);

        //logger.debug("Gradienttransform: " + transform);
		
		//////////////////////////////////buildgradient function
		// 'x1' attribute - default is 0%
		String x1Str = SVGUtilities.getChainableAttributeNS(paintElement, null, SVG_X1_ATTRIBUTE, ctx);
		if (x1Str.length() == 0) {
			x1Str = SVG_LINEAR_GRADIENT_X1_DEFAULT_VALUE;
		}
		// 'y1' attribute - default is 0%
		String y1Str = SVGUtilities.getChainableAttributeNS(paintElement, null, SVG_Y1_ATTRIBUTE, ctx);
		if (y1Str.length() == 0) {
			y1Str = SVG_LINEAR_GRADIENT_Y1_DEFAULT_VALUE;
		}
		// 'x2' attribute - default is 100%
		String x2Str = SVGUtilities.getChainableAttributeNS(paintElement, null, SVG_X2_ATTRIBUTE, ctx);
		if (x2Str.length() == 0) {
			x2Str = SVG_LINEAR_GRADIENT_X2_DEFAULT_VALUE;
		}
		// 'y2' attribute - default is 0%
		String y2Str = SVGUtilities.getChainableAttributeNS(paintElement, null, SVG_Y2_ATTRIBUTE, ctx);
		if (y2Str.length() == 0) {
			y2Str = SVG_LINEAR_GRADIENT_Y2_DEFAULT_VALUE;
		}

		// 'gradientUnits' attribute - default is objectBoundingBox
		short coordSystemType;
		String s2 = SVGUtilities.getChainableAttributeNS(paintElement, null, SVG_GRADIENT_UNITS_ATTRIBUTE, ctx);
		if (s2.length() == 0) {
			coordSystemType = SVGUtilities.OBJECT_BOUNDING_BOX;
		} else {
			coordSystemType = SVGUtilities.parseCoordinateSystem(paintElement, SVG_GRADIENT_UNITS_ATTRIBUTE, s2, ctx);
		}

		// additional transform to move to objectBoundingBox coordinate system
		//TODO gradienttransform
//		if (coordSystemType == SVGUtilities.OBJECT_BOUNDING_BOX) {
//			transform = SVGUtilities.toObjectBBox(transform, gfxElem);
//		}
		UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, gfxElem);

		Point2D p1 = SVGUtilities.convertPoint(x1Str,
				SVG_X1_ATTRIBUTE,
				y1Str,
				SVG_Y1_ATTRIBUTE,
				coordSystemType,
				uctx);

		Point2D p2 = SVGUtilities.convertPoint(x2Str,
				SVG_X2_ATTRIBUTE,
				y2Str,
				SVG_Y2_ATTRIBUTE,
				coordSystemType,
				uctx);
		
		
		//Transform gradient vector points with gradientTransform
		Point2D tp1 = null;
		tp1 = transform.transform(p1, tp1);
		p1 = tp1;
		Point2D tp2 = null;
		tp2 = transform.transform(p2, tp2);
		p2 = tp2;
		
		
		//Get gradient vector
		logger.debug("P1: " + p1 + " P2: " + p2);
		Vector3D ref = new Vector3D(1,0,0);
		Vector3D vP1 = new Vector3D((float)p1.getX(), (float)p1.getY(), 0);
		Vector3D vP2 = new Vector3D((float)p2.getX(), (float)p2.getY(), 0);
		Vector3D gradVect = vP2.getSubtracted(vP1);
		//logger.debug("Gradient vector: " + gradVect);
		
		//Get gradient vector angle to rotate the shape to be gradiented to the horizontal vector 1,0,0
		//Algorithm for linear gradients used here:
		//1. rotate the shape so that the gradient vector in the shape is parallel to 1,0,0 
		//2. calc bounding rectangle of shape + gradient rectangle (made of the x1,y1 x2,y2 gradient endpoints)
		//3. create quads with colored vertices at each (now horizontal) stop along the gradient
		//4. rotate the gradient shape with the quads back so it faces the original gradient vector direction
		
		float gradAngle = Vector3D.angleBetween(ref, gradVect);
		gradAngle = PApplet.degrees(gradAngle);
		
		Vector3D cross = ref.getCross(gradVect);
		//Get the direction of rotation
		if (cross.getZ() < 0){
			gradAngle*=-1;
		}
		
		logger.debug("Gradient angle: " + gradAngle + "�");
		
		logger.debug("Stops:");
		for (Stop stop : stops)
			logger.debug(" Stop -> Offset: " + stop.offset + " java.awt.Color: " + stop.color);
		
		if (coordSystemType == SVGUtilities.USER_SPACE_ON_USE){
			return this.setUpRotatedGradientUserSpace(shape, gradAngle, stops, p1, p2);
		}else{
			return this.setUpRotatedGradientBBox(shape, gradAngle, stops);
		}
		
		// If x1 = x2 and y1 = y2, then the area to be painted will be painted
		// as a single color using the color and opacity of the last gradient
		// stop.
//		if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
//			return colors[colors.length-1];
//		} else {
//			return new LinearGradientPaint(p1,
//					p2,
//					offsets,
//					colors,
//					spreadMethod,
//					colorSpace,
//					transform);
//		}
	}
	

	private FillPaint setUpRotatedGradientUserSpace(AbstractShape testShape, float angle, List<Stop> stops, Point2D p1, Point2D p2){
//		GL gl = ((PGraphicsOpenGL)pa.g).gl;
		GL10 gl = PlatformUtil.getGL();
		float gradAngle = angle;
		
		float invAngle = angle*-1;
		
		//Get copy of shapes vertices
		Vertex[] shapeVertsCopy = Vertex.getDeepVertexArrayCopy(testShape.getGeometryInfo().getVertices());
		//Rotate the vertices in the inverse direction of the gradients vector angle
		shapeVertsCopy = (Vertex[]) Vertex.rotateZVectorArray(shapeVertsCopy, testShape.getCenterPointLocal(), invAngle);
		
		Vertex vP1 = new Vertex((float)p1.getX(), (float)p1.getY(), 0);
		Vertex vP2 = new Vertex((float)p2.getX(), (float)p2.getY(), 0);
		float gradientRectWidth = vP2.getSubtracted(vP1).length();
		
		Vertex[] gradientRectVerts = new Vertex[]{
				vP1, 
				new Vertex((float)p2.getX(), (float)p1.getY(),0),
				vP2,
				new Vertex((float)p1.getX(), (float)p2.getY(),0) 
				};
		//Rotate the vertices in the inverse direction of the gradients vector angle
		gradientRectVerts = (Vertex[]) Vertex.rotateZVectorArray(gradientRectVerts, testShape.getCenterPointLocal(), invAngle);
		
		//Copy the rotated bounding shape vertices and the rotated gradient rectangle vertices into one array
		Vertex[] shapeAndGradVerts = new Vertex[shapeVertsCopy.length + gradientRectVerts.length];
		System.arraycopy(shapeVertsCopy, 0, shapeAndGradVerts, 0, shapeVertsCopy.length);
		System.arraycopy(gradientRectVerts, 0, shapeAndGradVerts, shapeVertsCopy.length, gradientRectVerts.length);
		
		//Create a temporary polygon with the roated vertices to calc BBox
		MTPolygon inverseRotatedShape = new MTPolygon(pa, shapeAndGradVerts);
		//Calculate a bounding rectangle from the rotated shape
		BoundsZPlaneRectangle inverseRotatedBounds = new BoundsZPlaneRectangle(inverseRotatedShape);
		Vector3D[] invBoundsVecs = inverseRotatedBounds.getVectorsLocal();
		
		//logger.debug("Gradient Rectangle width: " + gradientRectWidth);
		
		//Get the positions where the offsets are on the gradient vector
//		float bBoxWidth  = invBoundsVecs[1].x - invBoundsVecs[0].x;
//		logger.debug("BBox width: " + bBoxWidth);
//		float w = bBoxWidth/*/100*/;
		List<Float> xStops = new ArrayList<Float>();
		
		//- Go through stops
		//- multiply stop offsets with bbox width to get the position on gradient vector
		//logger.debug("->Gradient Vector stop positions:");
		for(Stop stop : stops){
			float offsetStopPosition = gradientRectWidth * stop.offset; //position auf gradient vector, stop(0) = vP1.x + offest
			xStops.add(offsetStopPosition);
			//logger.debug(" Offset-Stop-Position: " + offsetStopPosition);
		}
		
		//Calc new gradient polygon vertices with vertices at the stop locations
		Vertex[] newBounds = new Vertex[(xStops.size()-1) * 4];
		for (int i = 0; i < xStops.size()-1; i++) {
			float offset = xStops.get(i);
			Color stopColor = stops.get(i).color;
			
			float nextOffset = xStops.get(i+1);
			Color nextStopColor = stops.get(i+1).color;
			
			newBounds[i*4] 		= new Vertex(vP1.x + offset,     invBoundsVecs[0].y,0, 	stopColor.getRed(), stopColor.getGreen(), stopColor.getBlue(), stopColor.getAlpha());
			newBounds[i*4+1]	= new Vertex(vP1.x + nextOffset, invBoundsVecs[0].y,0, 	nextStopColor.getRed(), nextStopColor.getGreen(), nextStopColor.getBlue(), nextStopColor.getAlpha());
			newBounds[i*4+2] 	= new Vertex(vP1.x + nextOffset, invBoundsVecs[2].y,0, 	nextStopColor.getRed(), nextStopColor.getGreen(), nextStopColor.getBlue(), nextStopColor.getAlpha());
			newBounds[i*4+3]	= new Vertex(vP1.x + offset, 	 invBoundsVecs[2].y,0, 	stopColor.getRed(), stopColor.getGreen(), stopColor.getBlue(), stopColor.getAlpha());
		}
		
		//Put gradient rectangle quads into a list
		List<Vertex> gradientRectQuads = new ArrayList<Vertex>();
        for (Vertex vertex : newBounds) {
            gradientRectQuads.add(vertex);
        }
		
		/* Bounding shape with gradient rectangle inside (can also overlap outlines)
		 invBoundsVecs[0]   		invBoundsVecs[1]
			      | _______________ |
				   | 	|_____|    |
				   | 	|  G  |	   |
				   | vp1|____>|vp2 |
				   |____|_____|____|
		 */
		//Calc rectangle bands (quads) to fill the gradient shape with the gradVect end colors if the gradient vector is smaller than the shape to draw
		List<Vertex> leftQuad = new ArrayList<Vertex>();
		if (vP1.x > invBoundsVecs[0].x){
			//upper left of bounding rect
			Vertex v1 = new Vertex(invBoundsVecs[0].x, invBoundsVecs[0].y, 0, newBounds[0].getR(), newBounds[0].getG(), newBounds[0].getB(), newBounds[0].getA());
			//first stop on gradient vector upper
			Vertex v2 = new Vertex(newBounds[0].x, newBounds[0].y, 0, newBounds[0].getR(), newBounds[0].getG(), newBounds[0].getB(), newBounds[0].getA());
			//first stop on gradient vector lower
			Vertex v3 = new Vertex(newBounds[3].x, newBounds[3].y, 0, newBounds[3].getR(), newBounds[3].getG(), newBounds[3].getB(), newBounds[3].getA());
			//down left of bounding rect
			Vertex v4 = new Vertex(invBoundsVecs[3].x, invBoundsVecs[3].y, 0, newBounds[0].getR(), newBounds[0].getG(), newBounds[0].getB(), newBounds[0].getA());
			
			leftQuad.add(v1);
			leftQuad.add(v2);
			leftQuad.add(v3);
			leftQuad.add(v4);
		}
		
		//Add Right quad if gradient rectangle is smaler than overall bounds
		List<Vertex> rightQuad = new ArrayList<Vertex>();
		if (vP2.x < invBoundsVecs[1].x){
			Vertex gradientRectUpperRight = newBounds[newBounds.length-3];
			Vertex gradientRectLowerRight = newBounds[newBounds.length-2];
			
			Vertex v1 = new Vertex(gradientRectUpperRight.x, gradientRectUpperRight.y, 0, gradientRectUpperRight.getR(), gradientRectUpperRight.getG(), gradientRectUpperRight.getB(), gradientRectUpperRight.getA());
			Vertex v2 = new Vertex(invBoundsVecs[1].x, invBoundsVecs[1].y, 0, gradientRectUpperRight.getR(), gradientRectUpperRight.getG(), gradientRectUpperRight.getB(), gradientRectUpperRight.getA());
			Vertex v3 = new Vertex(invBoundsVecs[2].x, invBoundsVecs[2].y, 0, gradientRectUpperRight.getR(), gradientRectUpperRight.getG(), gradientRectUpperRight.getB(), gradientRectUpperRight.getA());
			Vertex v4 = new Vertex(gradientRectLowerRight.x, gradientRectLowerRight.y, 0, gradientRectUpperRight.getR(), gradientRectUpperRight.getG(), gradientRectUpperRight.getB(), gradientRectUpperRight.getA());
			
			rightQuad.add(v1);
			rightQuad.add(v2);
			rightQuad.add(v3);
			rightQuad.add(v4);
		}
		
		//Create new array for gradient shape with all quads inside
		List<Vertex> allGradientShapeVerts = new ArrayList<Vertex>();
		allGradientShapeVerts.addAll(leftQuad);
		allGradientShapeVerts.addAll(gradientRectQuads);
		allGradientShapeVerts.addAll(rightQuad);
		newBounds = allGradientShapeVerts.toArray(new Vertex[allGradientShapeVerts.size()]);
		
		//Rotate the vectors of the calculated bounding rect back to the original angle
		newBounds = (Vertex[]) Vector3D.rotateZVectorArray(newBounds, testShape.getCenterPointLocal(), gradAngle);
		
		//Create gradient shape to paint over the real shape
		MTPolygon p = new MTPolygon(pa, newBounds);
        p.setNoStroke(true);
        p.setPickable(false);
        p.setStrokeWeight(testShape.getStrokeWeight());
        p.setFillDrawMode(GL11Plus.GL_QUADS);
        //Use displaylist by default for gradientshape
        p.generateAndUseDisplayLists();
        
        FillPaint gradStencil = new FillPaint(gl, p);
		return gradStencil;
	}

	
	private FillPaint setUpRotatedGradientBBox(AbstractShape testShape, float angle, List<Stop> stops){
//			GL gl = ((PGraphicsOpenGL)pa.g).gl;
			GL10 gl = PlatformUtil.getGL();
			float gradAngle = angle;
			
			//Get copy of shapes vertices
			Vertex[] shapeVertsCopy = Vertex.getDeepVertexArrayCopy(testShape.getGeometryInfo().getVertices());
			//Rotate the vertices in the inverse direction of the gradients vector angle
			shapeVertsCopy = (Vertex[]) Vertex.rotateZVectorArray(shapeVertsCopy, testShape.getCenterPointLocal(), -gradAngle);
			
			//Create a temporary polygon with the roated vertices to calc BBox
			MTPolygon inverseRotatedShape = new MTPolygon(pa, shapeVertsCopy);
			//Calculate a bounding rectangle from the rotated shape
			BoundsZPlaneRectangle inverseRotatedBounds = new BoundsZPlaneRectangle(inverseRotatedShape);
			Vector3D[] invBoundsVecs = inverseRotatedBounds.getVectorsLocal();
			
			//Get the positions where the offsets are on the gradient vector
			float bBoxWidth  = invBoundsVecs[1].x - invBoundsVecs[0].x;
			logger.debug("BBox width: " + bBoxWidth);
			float w = bBoxWidth/*/100*/;
			List<Float> xStops = new ArrayList<Float>();
			//Go through stops and multiply stop offset with bbox width to get the position
			for(Stop stop : stops){
				float offsetStopPosition = w * stop.offset;
				xStops.add(offsetStopPosition);
				logger.debug("OffsetStopPosition: " + offsetStopPosition);
			}
			
			//Calc new gradient polygon vertices with vertices at the stop locations
			Vertex[] newBounds = new Vertex[(xStops.size()-1) * 4];
			for (int i = 0; i < xStops.size()-1; i++) {
				float offset = xStops.get(i);
				Color stopColor = stops.get(i).color;
				float nextOffset = xStops.get(i+1);
				Color nextStopColor = stops.get(i+1).color;
				newBounds[i*4] 		= new Vertex(invBoundsVecs[0].x + offset,     invBoundsVecs[0].y,0, 	stopColor.getRed(), stopColor.getGreen(), stopColor.getBlue(), stopColor.getAlpha());
				newBounds[i*4+1]	= new Vertex(invBoundsVecs[0].x + nextOffset, invBoundsVecs[0].y,0, 	nextStopColor.getRed(), nextStopColor.getGreen(), nextStopColor.getBlue(), nextStopColor.getAlpha());
				newBounds[i*4+2] 	= new Vertex(invBoundsVecs[0].x + nextOffset, invBoundsVecs[2].y,0, 	nextStopColor.getRed(), nextStopColor.getGreen(), nextStopColor.getBlue(), nextStopColor.getAlpha());
				newBounds[i*4+3]	= new Vertex(invBoundsVecs[0].x + offset, 	  invBoundsVecs[2].y,0, 	stopColor.getRed(), stopColor.getGreen(), stopColor.getBlue(), stopColor.getAlpha());
			}
			
			/*
			logger.debug("->New bounds:");
			for (int i = 0; i < newBounds.length; i++) {
				Vertex vertex = newBounds[i];
				logger.debug(vertex);
			}
			*/
			
			//Rotate the vectors of the calculated bounding rect back to the original angle
			newBounds = (Vertex[]) Vector3D.rotateZVectorArray(newBounds, testShape.getCenterPointLocal(), gradAngle);
			
			//Create gradient shape to paint over the real shape
			MTPolygon p = new MTPolygon(pa, newBounds);
	        p.setNoStroke(true);
	        p.setPickable(false);
	        p.setFillDrawMode(GL11Plus.GL_QUADS);
	        p.setStrokeWeight(testShape.getStrokeWeight());
	        //Use displaylist by default for gradientshape
	        p.generateAndUseDisplayLists();
			
	        FillPaint gradStencil = new FillPaint(gl, p);
	        return gradStencil;
		}
	    
	    
	
    private CycleMethodEnum getSpreadMethod(Element paintElement){
    	String s = "";
        //SPREADMETHOD 'spreadMethod' attribute - default is pad
        CycleMethodEnum spreadMethod = MultipleGradientPaint.NO_CYCLE;
        s = SVGUtilities.getChainableAttributeNS(paintElement, null, SVG_SPREAD_METHOD_ATTRIBUTE, ctx);
        if (s.length() != 0) {
//            spreadMethod = AbstractSVGGradientElementBridge.convertSpreadMethod(paintElement, s, ctx);
            if (SVG_REPEAT_VALUE.equals(s)) {
            	spreadMethod =  MultipleGradientPaint.REPEAT;
            }else 
	            if (SVG_REFLECT_VALUE.equals(s)) {
	            	spreadMethod =  MultipleGradientPaint.REFLECT;
            }else 
	            if (SVG_PAD_VALUE.equals(s)) {
	            	spreadMethod =  MultipleGradientPaint.NO_CYCLE;
            }else 
            	throw new BridgeException(ctx, paintElement, "ERR_ATTRIBUTE_VALUE_MALFORMED", new Object[] {SVG_SPREAD_METHOD_ATTRIBUTE, s});
        }
        return spreadMethod;
    }
    
    
    private AffineTransform getGradientTransform(Element paintElement){
    	String s = "";
    	  //'gradientTransform' attribute - default is an Identity matrix
        AffineTransform transform;
        s = SVGUtilities.getChainableAttributeNS(paintElement, null, SVG_GRADIENT_TRANSFORM_ATTRIBUTE, ctx);
        if (s.length() != 0) {
            transform = SVGUtilities.convertTransform(paintElement, SVG_GRADIENT_TRANSFORM_ATTRIBUTE, s, ctx);
        } else {
            transform = new AffineTransform();
        }
        return transform;
    }
	    
	 /**
     * Returns the stops elements of the specified gradient
     * element. Stops can be children of the gradients or defined on
     * one of its 'ancestor' (linked with the xlink:href attribute).
     *
     * @param paintElement the gradient element
     * @param opacity the opacity
     * @param ctx the bridge context to use
     */
    protected List<Stop> extractStops(Element paintElement, float opacity, BridgeContext ctx) {
        //List<Object> refs = new LinkedList<Object>();
        for (;;) {
            List<Stop> stops = extractLocalStop(paintElement, opacity, ctx);
            if (stops != null) {
            	boolean zeroOffset = false;
            	boolean oneOffset = false;
            	boolean zeroOffsetAdded = false;
            	boolean oneOffsetAdded = false;
            	//Stops in svg dont have to have ending stops at 0.0 and 1.0
            	//but we need them so we add them ourselves if not present
            	for (Stop stop :stops){
            		if (stop.offset == 0.0){
            			zeroOffset = true;
            		}
            		if (stop.offset == 1.0){
            			oneOffset  = true;
            		}
            	}
            	//Add a stop for beginning and end if not existant
            	if (!zeroOffset){
            		logger.debug("No offset at 0.0 location -> adding it.");
            		stops.add(0, new AbstractSVGGradientElementBridge.Stop(new java.awt.Color(0,0,0,0), 0.0f));
            		zeroOffsetAdded = true;
            	}
            	if (!oneOffset){
            		logger.debug("No offset at 1.0 location -> adding it.");
            		stops.add(stops.size(), new AbstractSVGGradientElementBridge.Stop(new java.awt.Color(0,0,0,0), 1.0f));
            		oneOffsetAdded = true;
            	}
            	
            	//Sort stops by offset position from 0.0 to 1.0
            	List<GradientStop> gradientStops = new ArrayList<GradientStop>();
            	for (Stop stop :stops){
            		gradientStops.add(new GradientStop(stop.offset, stop.color));
            	}
            	GradientStop[] gradStopArr = gradientStops.toArray(new GradientStop[gradientStops.size()]);
            	HelperMethods.quicksort(gradStopArr);
            	//Create new, sorted stop list, clamp color from self created zero/one offsets to nearest color
            	List<Stop> sortedStopList = new ArrayList<Stop>();
            	for (int i = 0; i < gradStopArr.length; i++) {
					GradientStop gradientStop = gradStopArr[i];
					if (zeroOffsetAdded 
						&& i == 0 
						&& gradStopArr.length >= i+1  
						&& gradStopArr[i+1] != null
					){
						gradientStop.color = gradStopArr[i+1].color;
					}
					if (oneOffsetAdded 
						&& i == gradStopArr.length-1 
						//&& gradStopArr.length >= i+1  
						&& gradStopArr[i-1] != null
					){
							gradientStop.color = gradStopArr[i-1].color;
					}
					sortedStopList.add(new AbstractSVGGradientElementBridge.Stop(gradientStop.color, gradientStop.offset));
				}
            	return sortedStopList;
            	
//                return stops; // stop elements found, exit
            }
            String uri = XLinkSupport.getXLinkHref(paintElement);
            if (uri.length() == 0) {
                return null; // no xlink:href found, exit
            }
            // check if there is circular dependencies
            /*
            String baseURI = XMLBaseSupport.getCascadedXMLBase(paintElement);
            ParsedURL purl = new ParsedURL(baseURI, uri);
            if (contains(refs, purl)) {
                throw new BridgeException(paintElement,
                                          ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES,
                                          new Object[] {uri});
            }
            refs.add(purl);
             */
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }
    

    /**
     * To compare stop offsets
     * @author Chris
     */
    private class GradientStop implements Comparable<GradientStop>{
    	float offset;
    	Color color;

    	public GradientStop(float offset, java.awt.Color color2){
    		this.offset = offset;
    		this.color = color2;
    	}

    	//@Override
    	public int compareTo(GradientStop o) {
    		if (this.offset < o.offset){
    			return -1;
    		}
    		else if(offset == o.offset){
    			return 0;
    		}
    		else if(this.offset > o.offset){
    			return 1;
    		}else{
    			return 0;
    		}
    	}
    }


    /**
     * Returns a list of <tt>Stop</tt> elements, children of the
     * specified paintElement can have or null if any.
     *
     * @param gradientElement the paint element
     * @param opacity the opacity
     * @param ctx the bridge context
     */
    protected static List<Stop> extractLocalStop(Element gradientElement, float opacity, BridgeContext ctx) {
    	LinkedList<Stop> stops = null;
    	Stop previous = null;
    	for (Node n = gradientElement.getFirstChild(); n != null; n = n.getNextSibling()){
    		if ((n.getNodeType() != Node.ELEMENT_NODE)) {
    			continue;
    		}

    		Element e = (Element)n;
    		Bridge bridge = ctx.getBridge(e);
    		if (bridge == null || !(bridge instanceof SVGStopElementBridge)) {
    			continue;
    		}
    		Stop stop = ((SVGStopElementBridge)bridge).createStop(ctx, gradientElement, e, opacity);
    		if (stops == null) {
    			stops = new LinkedList<Stop>();
    		}
    		if (previous != null) {
    			if (stop.offset < previous.offset) {
    				stop.offset = previous.offset;
    			}
    		}
    		stops.add(stop);
    		previous = stop;
    	}
    	return stops;
    }

   
	private List<Float> getSVGLengthListAsFloat(SVGLengthList valueList){
		  List<Float> values = new ArrayList<Float>();
		  for (int i = 0; i < valueList.getNumberOfItems(); i++) {
			  values.add(valueList.getItem(i).getValue());
		  }
		  if (values.isEmpty()){
			  values.add(0f);
		  }
		  return values;
	}
	
	
	/**
	 * Tries to retrieve a css property as a float number.
	 * If it fails, it returns the provided defaultvalue.
	 * 
	 * @param gfxElem the gfx elem
	 * @param queryProperty the query property
	 * @param defaultValue the default value
	 * 
	 * @return the float
	 */
	private float queryPrimitiveFloatValue(SVGGraphicsElement gfxElem, String queryProperty, float defaultValue){
		float returnValue = defaultValue;
		CSSStyleDeclaration style = gfxElem.getOwnerSVGElement().getComputedStyle(gfxElem, "");
		CSSValue cssValue = (CSSValue) style.getPropertyCSSValue(queryProperty);
//		  logger.debug("CSSValue.getCssText() of proerty " + queryProperty + ": " + cssValue.getCssText());
		if (cssValue != null){
			if (cssValue.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE){
				try{
					org.w3c.dom.css.CSSPrimitiveValue v = (org.w3c.dom.css.CSSPrimitiveValue)cssValue;
					if (v.getCssValueType() == CSSPrimitiveValue.CSS_NUMBER){
						returnValue = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
//						logger.debug(queryProperty + ": " + returnValue);
					}
				}catch(Exception e){
					logger.error(e.getMessage());
				}
			}
		}
		return returnValue;
	}
	

	
	/**
	 * Creates a polygon from an SVGOMPolygonElement element.
	 * 
	 * @param polyElem the poly elem
	 * @param noFill the no fill
	 * @param windingRule the winding rule
	 * 
	 * @return the live polygon component
	 */
	private AbstractShape getLivePolygonComponent(SVGOMPolygonElement polyElem, boolean noFill, int windingRule){
		AbstractShape returnComponent = null;
		
		//Create Vertex array from points
		  SVGPointList pointList = polyElem.getPoints();
		  Vertex[] vertices = new Vertex[pointList.getNumberOfItems()];
		  for (int i = 0; i < pointList.getNumberOfItems(); i++) {
			SVGPoint p = pointList.getItem(i);
			vertices[i] = new Vertex(p.getX(), p.getY(),0);
		  }
		  
		  //If polygon isnt closed, close it with the first vertex
		  if (!vertices[0].equalsVector(vertices[vertices.length-1])){
			  Vertex[] closedVertices = new Vertex[vertices.length+1];
			  System.arraycopy(vertices, 0, closedVertices, 0, vertices.length);
			  closedVertices[closedVertices.length-1] = (Vertex)vertices[0].getCopy();
			  vertices = closedVertices;
		  }
		  
			int convexity = ConvexityUtil.classifyPolygon2(vertices.length, vertices);
			switch (convexity) {
				case ConvexityUtil.NotConvexDegenerate:
				case ConvexityUtil.NotConvex:
					//If not filled, we dont worry about non-simple polygons
					if (noFill){
						returnComponent = createPoly(vertices);
					}else{
						ArrayList<Vertex[]> contours = new ArrayList<Vertex[]>();
						contours.add(vertices);
//						returnComponent = createStencilPoly(vertices, contours);
						returnComponent = createComplexPoly(contours, windingRule);
					}
					break;
				case ConvexityUtil.ConvexDegenerate:
				case ConvexityUtil.ConvexCW:
				case ConvexityUtil.ConvexCCW:
					returnComponent = createPoly(vertices);
					break;
				default:
					break;
			}
		return returnComponent;
	}
	
	/**
	 * Creates a polygon from a SVGOMPathElement.
	 * 
	 * @param pathElem the path elem
	 * @param noFill the no fill
	 * @param windingRule the winding rule
	 * 
	 * @return the live path component
	 */
	private AbstractShape getLivePathComponent(SVGOMPathElement pathElem, boolean noFill, int windingRule){
		  AbstractShape returnComponent 	= null;
		  CustomPathHandler pathHandler 	= new CustomPathHandler();
		  PathParser pathParser 			= new PathParser();
		  //pathHandler.setVerbose(true);
		  
		  /*
		  SVGPathSegList pathSegList = pathElem.getPathSegList();
		  SVGPathSeg seg = pathSegList.getItem(pathSegList.getNumberOfItems()-1);
		  logger.debug(seg.getPathSegTypeAsLetter());
		  */
		  
		  //Parse the "d" attribute 
		  String dAttValue = pathElem.getAttribute("d");
		  pathParser.setPathHandler(pathHandler);
		  pathParser.parse(dAttValue);
		  
			
		  	//Get the Vertices of the path
			Vertex[] originalPointsArray = pathHandler.getPathPointsArray();
			
			//Get Sub-Paths
			ArrayList<Vertex[]> contours = pathHandler.getContours();
			
			// For stencil-trick-polygons!! \\
//			/*
			//Get path vertices points
			LinkedList<Vertex> pathPoints = pathHandler.getPathPoints();
			
			if (pathHandler.getReverseMoveToStack().size() <= 1){
				//nicht adden
			}else{
				pathPoints.addAll(pathHandler.getReverseMoveToStack());
			}
			Vertex[] pathVertsStencilPrepared = pathPoints.toArray(new Vertex[pathPoints.size()]);
//			*/
			
			//Check if path vertices are empty
			if (originalPointsArray.length == 0){
				logger.debug("Empty path vertex array -> aborting");
				return null;
			}
			
			//TODO actually should calculate the real vertices from the vezier ones and then check
			//for convexity..else there might be false positives
			Vertex[] v;
//			if (containsBeziers(v)){
//				v = Tools3D.createVertexArrFromBezierArr(originalPointsArray, 11);
//			}
			v = originalPointsArray;
			
			//Check for convexity
			int convexity = ConvexityUtil.classifyPolygon2(v.length, v);
			switch (convexity) {
				case ConvexityUtil.NotConvexDegenerate:
//					logger.debug("not Convex Degenerate");
				case ConvexityUtil.NotConvex:
//					logger.debug("not convex");
					//If not filled, we can createa non stenciled polygon with no filling for better
					//performance
					if (noFill){
						returnComponent = createPoly(originalPointsArray);
					}else{
						returnComponent = createComplexPoly(contours, windingRule);
					}
					break;
				case ConvexityUtil.ConvexDegenerate:
//					logger.debug("convex degenerate");
				case ConvexityUtil.ConvexCW:
//					logger.debug("convex clockwise");
				case ConvexityUtil.ConvexCCW:
//					logger.debug("convex counterclockwise");
					returnComponent = createPoly(originalPointsArray);
					break;
				default:
					break;
			}
			
		return returnComponent;
	}
	
	/**
	 * Creates a Stencil-Trick-Polygon.
	 * 
	 * @param stencilPreparedVerts the stencil prepared verts
	 * @param subPaths the sub paths
	 * 
	 * @return the abstract shape
	 */
	private AbstractShape createStencilPoly(Vertex[] stencilPreparedVerts, ArrayList<Vertex[]> subPaths) {
//		logger.debug("Create stencil poly");
		//Blow up vertex array, that will be used for picking etc
		//to at least be of size == 3 for generating normals
		if (stencilPreparedVerts.length <3){
			Vertex[] newVerts = new Vertex[3];
			if (stencilPreparedVerts.length == 2){
				newVerts[0] = stencilPreparedVerts[0];
				newVerts[1] = stencilPreparedVerts[1];
				newVerts[2] = (Vertex)stencilPreparedVerts[1].getCopy();
				stencilPreparedVerts = newVerts;
			}else if (stencilPreparedVerts.length == 1){
				newVerts[0] = stencilPreparedVerts[0];
				newVerts[1] = (Vertex)stencilPreparedVerts[1].getCopy();
				newVerts[2] = (Vertex)stencilPreparedVerts[1].getCopy();
				stencilPreparedVerts = newVerts;
			}else{
				//ERROR
			}
		}
		
		MTStencilPolygon newShape = new MTStencilPolygon(pa, stencilPreparedVerts, subPaths);
		return newShape;
	}
	
	
	/**
	 * Creates a Complex (tesselated) polygon.
	 * 
	 * @param contours the contours
	 * @param windingRule the winding rule
	 * 
	 * @return the abstract shape
	 */
	private AbstractShape createComplexPoly(ArrayList<Vertex[]> contours, int windingRule) {
		int segments = 10; 
		List<Vertex[]> bezierContours = ToolsGeometry.createVertexArrFromBezierVertexArrays(contours, segments);
		
		GluTrianglulator triangulator = new GluTrianglulator(pa);
		
//		MTTriangleMesh mesh = triangulator.toTriangleMesh(bezierContours, windingRule);
		
		triangulator.tesselate(bezierContours, windingRule);
		List<Vertex> tris = triangulator.getTriList();
		Vertex[] verts = tris.toArray(new Vertex[tris.size()]);
		GeometryInfo geom = new GeometryInfo(pa, verts);
		
//		MTTriangleMesh mesh = new MTTriangleMesh(pa, geom);
		MTTriangleMesh mesh = new SVGMesh(pa, geom);
		
		//TODO put outline contourse in own class SVGMesh! 
		//not belonging in general mesh class
		mesh.setOutlineContours(bezierContours);
		
		triangulator.deleteTess(); //Delete triangulator (C++ object)
		return mesh;
	}
	

	
	
	private class SvgPolygon extends MTPolygon{
		public SvgPolygon(Vertex[] vertices, PApplet applet) {
			super(applet, vertices);
		}
		
		protected IBoundingShape computeDefaultBounds() {
			//Use z plane bounding rect instead default boundingsphere for svg!
			return new BoundsZPlaneRectangle(this);
		}
		
		@Override
		protected void setDefaultGestureActions() {
			
		}
	}
	
	private class SVGMesh extends MTTriangleMesh{
		public SVGMesh(PApplet applet, GeometryInfo geometryInfo) {
			super(applet, geometryInfo, false);
		}
		
		@Override
		protected IBoundingShape computeDefaultBounds() {
			//Use z plane bounding rect instead default boundingsphere for svg!
			return new BoundsZPlaneRectangle(this);
		}
		
		@Override
		protected void setDefaultGestureActions() {
			
		}
	}
	
	
	
	
//	/**
//	 * Creates a Complex (tesselated) polygon
//	 * @param contours
//	 * @param windingRule
//	 * @return
//	 */
//	private AbstractShape createComplexPoly(ArrayList<Vertex[]> contours, int windingRule) {
////		logger.debug("Create createComplexPoly poly");
////		if (contours.get(0).length <3)
////			logger.error("<3");
//		
//		//Blow up first contour, that will be used for picking etc
//		//to at least be of size == 3 for generating normals
//		if (contours.get(0).length <3){
//			if (contours.get(0).length == 2){
//				Vertex[] v = new Vertex[3];
//				v[0] = contours.get(0)[0];
//				v[1] = contours.get(0)[1];
//				v[2] = (Vertex)contours.get(0)[1].getCopy();
//				Vertex[] c = contours.get(0);
//				c = v;
//			}else if (contours.get(0).length == 1){
//				Vertex[] v = new Vertex[3];
//				v[0] = contours.get(0)[0];
//				v[1] = (Vertex)contours.get(0)[0].getCopy();
//				v[2] = (Vertex)contours.get(0)[0].getCopy();
//				Vertex[] c = contours.get(0);
//				c = v;
//			}else{
//				//ERROR
//			}
//		}
//		
//		MTComplexPolygon newShape = new MTComplexPolygon(contours, pa);
//		newShape.setWindingRule(windingRule);
//		return newShape;
//	}

	/**
	 * Creates a plain, normal polygon.
	 * 
	 * @param vertices the vertices
	 * 
	 * @return the abstract shape
	 */
	private AbstractShape createPoly(Vertex[] vertices) {
//		logger.debug("Create poly");
		Vertex[] verts = vertices; 

		if (ToolsGeometry.containsBezierVertices(verts))
			verts = ToolsGeometry.createVertexArrFromBezierArr(verts, 13);
		
		//Blow up vertex array, that will be used for picking etc
		//to at least be of size == 3 for generating normals
		if (verts.length <3){
			Vertex[] newVerts = new Vertex[3];
			if (verts.length == 2){
				newVerts[0] = verts[0];
				newVerts[1] = verts[1];
				newVerts[2] = (Vertex)verts[1].getCopy();
				verts = newVerts;
			}else if (verts.length == 1){
				newVerts[0] = verts[0];
				newVerts[1] = (Vertex)verts[0].getCopy();
				newVerts[2] = (Vertex)verts[0].getCopy();
				verts = newVerts;
			}else{
				//ERROR
			}
		}
		
		//For lines or polygons do this
//		return new MTPolygon(verts , pa);
		return new SvgPolygon(verts,pa);
	}
	
	/**
	 * Checks whether the given element is located inside a clip-path element.
	 * Used to determine whether to draw the component or not.
	 * 
	 * @param element the element
	 * 
	 * @return true, if checks if is under clip path
	 */
	private boolean isUnderClipPath(Node element){
		if (element.getParentNode() == null)
			return false;
		while (element.getParentNode() != null ) {
			Node parent = element.getParentNode();
			if (parent.getNodeName().equals(SVG_CLIP_PATH_TAG))
				return true;
			element = parent;
		}
		return false;
	}
	
	
	
	/**
	 * Gets the inherited opacity.
	 * 
	 * @param svgElem the svg elem
	 * @param element the element
	 * 
	 * @return the inherited opacity
	 */
	private float getInheritedOpacity(SVGSVGElement svgElem, Node element){
		float returnOpactiy = 1.0f;
		if (element.getParentNode() == null)
			return returnOpactiy;
		
		//Get attribute of this element
		try{
			if (element instanceof SVGGraphicsElement){
				SVGGraphicsElement gfx = (SVGGraphicsElement)element;
				float opacity = ((CSSPrimitiveValue)svgElem.getComputedStyle(gfx, "").getPropertyCSSValue(("opacity"))).getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				returnOpactiy *= opacity;
				logger.debug(gfx.getTagName() + ": found opacity: " + opacity);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		while (element.getParentNode() != null ) {
			Node parent = element.getParentNode();
			try{
				if (parent instanceof SVGGraphicsElement){
					SVGGraphicsElement gfx = (SVGGraphicsElement)parent;
					float opacity = ((CSSPrimitiveValue)svgElem.getComputedStyle(gfx, "").getPropertyCSSValue(("opacity"))).getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
					returnOpactiy *= opacity;
					logger.debug(gfx.getTagName() + ": found opacity: " + opacity);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			element = parent;
		}
		return returnOpactiy;
	}
	
	

}
