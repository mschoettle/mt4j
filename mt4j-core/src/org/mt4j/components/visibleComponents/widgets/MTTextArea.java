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
package org.mt4j.components.visibleComponents.widgets;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.css.style.CSSFont;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.keyboard.ITextInputListener;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.font.IFontCharacter;
import org.mt4j.util.font.ITextureFont;
import org.mt4j.util.font.ITextureFontCharacter;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * The Class MTTextArea. This widget allows to display text with a specified font.
 * If the constructor with no fixed text are dimensions is used, the text area will
 * expand itself to fit the text in. 
 * <br>
 * If the constructor with fixed dimensions is used, the text will have word wrapping
 * and be clipped to the specified dimensions.
 * 
 * @author Christopher Ruff
 */
public class MTTextArea extends MTRectangle implements ITextInputListener, Comparable<Object>{

//Standard expand direction is {@link ExpandDirection#UP} for
// backward compatibility.
    /**
     * Determines the vertical expand direction of the {@link MTTextArea}
     * if the text area will expand itself to fit the text in.
     *
     * 
     */
    public enum ExpandDirection {
        /** Expand the {@link MTTextArea} in top direction if necessary. */  
        UP,
        /** Expand the {@link MTTextArea} in bottom direction if necassary. */
        DOWN
    }
    
	/** The pa. */
	private PApplet pa;
	
	/** The character list. */
	private ArrayList<IFontCharacter> characterList;
	
	/** The font. */
	private IFont font;
	
	/** The font b box height. */
	private int fontHeight;
	
	/** The show caret. */
	private boolean showCaret;
	
	/** The show caret time. */
	private long showCaretTime; //ms
	
	/** The caret time counter. */
	private int caretTimeCounter = 0;
	
	/** The enable caret. */
	private boolean enableCaret;
	
	/** The caret width. */
	private float caretWidth;

	private int innerPaddingTop;
	private int innerPaddingLeft;
	
	private float totalScrollTextX;
	private float totalScrollTextY;
	
	private static final int MODE_EXPAND = 0;
	private static final int MODE_WRAP = 1;
	
	private int mode;

	private static ArtificalLineBreak artificialLineBreak;

    private ExpandDirection expandDirection ;
    
    
    private boolean enableKerning; 
	
	//TODO different font sizes in one textarea?
	//TODO (create mode : expand vertically but do word wrap horizontally?
    
    /**
     * Instantiates a new text area. This constructor creates
     * a text area with variable dimensions that expands itself when text is added.
     * A default font is used.
     *
     * @param pApplet the applet
     */
	public MTTextArea(PApplet pApplet) {
		this(pApplet, FontManager.getInstance().getDefaultFont(pApplet));
	}
	
	
    private boolean ignoreCSSFont = false;
    
    /**
     * Instantiates a new text area. This constructor creates
     * a text area with variable dimensions that expands itself when text is added.
     *
     * @param pApplet the applet
     * @param font the font
     */
	public MTTextArea(AbstractMTApplication pApplet, CSSFont font) {
		this(pApplet, FontManager.getInstance().getDefaultFont(pApplet));
		this.getCssHelper().getPrivateStyleSheets().add(new CSSStyle(font,pApplet));
	}
	
    
	/**
	 * Instantiates a new text area. This constructor creates
	 * a text area with variable dimensions that expands itself when text is added.
	 * 
	 * @param pApplet the applet
	 * @param font the font
	 */
	public MTTextArea(PApplet pApplet, IFont font) {
		super(	pApplet, 0, 	//upper left corner
				0, 	//width
				0,  //height
				0);
		
		init(pApplet, font, MODE_EXPAND);
		
		//Position textarea at 0,0
		this.setUpperLeftPos(Vector3D.ZERO_VECTOR);
		
		//Expand vertically at enter 
		this.setHeightLocal(this.getTotalLinesHeight());
		this.setWidthLocal(getMaxLineWidth());
		
		//Disable font being overwritten by CSS
		this.ignoreCSSFont = true;
	}
	
	
	
	/**
	 * Instantiates a new mT text area.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTTextArea(float x, float y, float width, float height, PApplet pApplet) {
		this(pApplet, x, y, width, height, FontManager.getInstance().getDefaultFont(pApplet));
	}
	
    /**
     * Instantiates a new mT text area.
     * This constructor creates a textarea with fixed dimensions.
     * If the text exceeds the dimensions the text is clipped.
     * A default font is used.
     * @param pApplet the applet
     * @param x the x
     * @param y the y
     * @param width the width
     * @param height the height
     */
	public MTTextArea(PApplet pApplet, float x, float y, float width, float height) {
		this(pApplet, x, y, width, height, FontManager.getInstance().getDefaultFont(pApplet));
	}
	
	
	/**
	 * Instantiates a new mT text area.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param font the font
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTTextArea(float x, float y, float width, float height, IFont font, PApplet pApplet) {
		this(pApplet, x, y, width, height, font);
	}
    
	/**
	 * Instantiates a new mT text area. 
	 * This constructor creates a textarea with fixed dimensions. 
	 * If the text exceeds the dimensions the text is clipped.
	 * @param pApplet the applet
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param font the font
	 */
	public MTTextArea(PApplet pApplet, float x, float y, float width, float height, IFont font) {
		super(	pApplet, 0, 	//upper left corner
				0, 	//width
				width,  //height
				height);
		
		init(pApplet, font, MODE_WRAP);
		
		//Position textarea at x,y
		this.setUpperLeftPos(new Vector3D(x,y,0));
		this.setUpperLeftPos(new Vector3D(x,y,0));
		
		//Disable font being overwritten by CSS
		this.ignoreCSSFont = true;
	}
	
	
	
	public boolean isIgnoreCSSFont() {
		return ignoreCSSFont;
	}

	public void setIgnoreCSSFont(boolean ignoreCSSFont) {
		this.ignoreCSSFont = ignoreCSSFont;
	}

	private void setUpperLeftPos(Vector3D pos){
		//Position textarea at 0,0
		PositionAnchor prevAnchor = this.getAnchor();
		this.setAnchor(PositionAnchor.UPPER_LEFT);
		this.setPositionGlobal(pos);
		this.setAnchor(prevAnchor);
	}
	
	public MTTextArea(AbstractMTApplication app) {
		this(app, app.getCssStyleManager().getDefaultFont(app));
	}
	
	private void init(PApplet pApplet, IFont font, int mode){
		this.pa = pApplet;
		this.font = font;
		this.expandDirection = ExpandDirection.DOWN;
		
		this.mode = mode;
		switch (this.mode) {
		case MODE_EXPAND:
			//We dont have to clip since we expand the area
			break;
		case MODE_WRAP:
			if (MT4jSettings.getInstance().isOpenGlMode()){ 
				//Clip the text to the area
				this.setClip(new Clip(pApplet, this.getVerticesLocal()[0].x, this.getVerticesLocal()[0].y, this.getWidthXY(TransformSpace.LOCAL), this.getHeightXY(TransformSpace.LOCAL)));
			}
			break;
		default:
			break;
		}
		
		characterList = new ArrayList<IFontCharacter>();
		
//		if (MT4jSettings.getInstance().isOpenGlMode())
//			this.setUseDirectGL(true);
		
		fontHeight = font.getFontAbsoluteHeight();
		
		caretWidth = 0; 
		innerPaddingTop = 5;
		innerPaddingLeft = 8;
		
		showCaret 	= false;
		enableCaret = false;
		showCaretTime = 600;
		
		enableKerning = true;
		
		this.setStrokeWeight(1.5f);
		this.setStrokeColor(new MTColor(255, 255, 255, 255));
		this.setDrawSmooth(true);
		
//		//Draw this component and its children above 
//		//everything previously drawn and avoid z-fighting
//		this.setDepthBufferDisabled(true);
		
		this.totalScrollTextX = 0.0f;
		this.totalScrollTextY = 0.0f;
		
		if (artificialLineBreak == null){
			artificialLineBreak = new ArtificalLineBreak();
		}
		
		this.isTextureFont = (font instanceof ITextureFont);
	}
	
	
	/**
	 * Sets the font.
	 * @param font the new font
	 */
	public void setFont(IFont font){
		if (this.characterList != null) {
			this.font = font;
			this.fontHeight = font.getFontAbsoluteHeight();
			this.isTextureFont = (font instanceof ITextureFont);
			this.updateLayout();
		}
	}

	
	@Override
	public void updateComponent(long timeDelta) {
		super.updateComponent(timeDelta);
		if (enableCaret){
			caretTimeCounter+=timeDelta;
			if (caretTimeCounter >= showCaretTime && !showCaret){
				showCaret 		 = true;
				caretTimeCounter = 0;
			}else if (caretTimeCounter >= showCaretTime && showCaret){
				showCaret 		 = false;
				caretTimeCounter = 0;
			}
		}
	}
	
	
	@Override
	public void preDraw(PGraphics graphics) {
		super.preDraw(graphics);
		
		//Hack for drawing anti aliased stroke outline over the clipped area
		noStrokeSettingSaved = this.isNoStroke();
		if (this.mode == MODE_WRAP && this.getClip() != null && !this.isNoStroke()){
			this.setNoStroke(true);	
		}
	}
	
	//TEST: Align/round text with screen pixels to avoid visual artifacts with texture fonts
	private boolean textPositionRounding = true;
	private boolean snapVectorDirty = false;
	private Vector3D defaultScale = new Vector3D(1,1,1);
	private Vector3D globalTranslation = new Vector3D();
	private Vector3D rounded = new Vector3D();
	private float tolerance = 0.05f;
	private boolean isTextureFont = false;
	private Vector3D diff = new Vector3D(0,0,0);
	
	public void setTextPositionRounding(boolean snap){
		this.textPositionRounding = snap;
	}
	
	public boolean isTextPositionRounding(){
		return this.textPositionRounding;
	}
	
	@Override
	public void setMatricesDirty(boolean baseMatrixDirty) {
		super.setMatricesDirty(baseMatrixDirty);
		if (baseMatrixDirty)
			snapVectorDirty = baseMatrixDirty;
	}
	
	
	
	@Override
	public void drawComponent(PGraphics g) {
		super.drawComponent(g);
		
		//FIXME snapping wont be useful if textarea is created at non-integer value!? and if Camera isnt default camera
		//if global matrix set dirty and comp not scaled -> calculate new diff vector -> apply
		//if snap enabled -> apply diff vector
		boolean applySnap = false;
		if (isTextureFont && textPositionRounding){
			if (snapVectorDirty){ //Calc new snap vector
				Matrix m = this.getGlobalMatrix();
				if (m.getScale().equalsVectorWithTolerance(defaultScale, tolerance)){ //Only if no scale applied
					applySnap = true;
					globalTranslation.setXYZ(m.m03, m.m13, m.m23);
					rounded.setXYZ(Math.round(globalTranslation.x), Math.round(globalTranslation.y), Math.round(globalTranslation.z));
//					rounded.setXYZ((int)globalTranslation.x, (int)globalTranslation.y, (int)globalTranslation.z);
					rounded.subtractLocal(globalTranslation);
					
					diff.setXYZ(rounded.x, rounded.y, rounded.z);
					snapVectorDirty = false;
					
					g.pushMatrix();
					g.translate(diff.x, diff.y, diff.z);
				}else{ //global matrix was set dirty but the textarea is scaled -> dont apply snapvector because it gets blurry anyway if scaled
//					snapVectorDirty = false; //because only if scale changes back to 1,1,1 we have to calc new snapvector again
					applySnap = false;
				}
			}else{ //new Snap vector already calculated since global matrix was changed 
				applySnap = true;
				g.pushMatrix();
				g.translate(diff.x, diff.y, diff.z);
			}
		}
		
		//Add caret if its time 
		if (enableCaret && showCaret){
			characterList.add(this.getFont().getFontCharacterByUnicode("|"));
		}
		
		int charListSize = characterList.size();
		int thisLineTotalXAdvancement = 0;
		int lastXAdvancement = innerPaddingLeft;

		//Account for TOP inner padding if using WRAP mode -> translate text
		switch (this.mode) {
		case MODE_EXPAND:
			//Dont need to translate for innerpadding TOP because we do that in setHeight() making the whole textarea bigger
			g.pushMatrix(); 
			g.translate(0, innerPaddingTop);
			break;
		case MODE_WRAP:
			//Need to translate innerpadding TOP because we shouldnt make the textarea bigger like in expand mode
			g.pushMatrix();
			g.translate(0, innerPaddingTop);
			break;
		default:
			break;
		}
		
//		/*//
		//To set caret at most left start pos when charlist empty (looks better)
		if (enableCaret && showCaret && charListSize == 1){
			lastXAdvancement = 0;
		}
//		*/
		
		if (this.isUseDirectGL()){
//			GL gl = Tools3D.beginGL(pa);
			GL10 gl = PlatformUtil.beginGL();
			if (totalScrollTextX != 0.0f || totalScrollTextY != 0.0f){
				gl.glTranslatef(totalScrollTextX, totalScrollTextY + font.getFontMaxAscent(), 0);
			}else{
				gl.glTranslatef(0, font.getFontMaxAscent(), 0);
			}
			
			/*
			//Disabled so that no new list is created everytime something changes
			if (!enableCaret && useDisplayList && this.contentDisplayListDirty){
				//Re-Create displaylist
				this.useContentDisplayList();
			}
			*/
			
			//TODO avoid many stateChanges
			//in bitmap font mode for example:
			//enable textures, enable vertex arrays and color only once!
			
			drawCharactersGL(gl, this.font, characterList, charListSize, lastXAdvancement, thisLineTotalXAdvancement);
			
//			Tools3D.endGL(pa);
			PlatformUtil.endGL();
		}
		else{ //P3D rendering
			g.pushMatrix(); //FIXME TEST text scrolling - but IMHO better done with parent list/scroll container
			g.translate(totalScrollTextX, totalScrollTextY + font.getFontMaxAscent(), 0);
			
			//Set the color for all characters (since the characters dont set their own fill/stroke color anymore
			MTColor fillColor = this.getFont().getFillColor();
			g.fill(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
			g.stroke(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
			g.tint(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha()); 
			
			for (int i = 0; i < charListSize; i++) {
				IFontCharacter character = characterList.get(i);
				//Step to the right by the amount of the last characters x advancement
				pa.translate(lastXAdvancement, 0, 0); //original
				//Save total amount gone to the right in this line
				thisLineTotalXAdvancement += lastXAdvancement;
				lastXAdvancement = 0;
				
				//Draw the letter
				character.drawComponent(g);
				
				//Check if newLine occurs, goto start at new line
				if (character.getUnicode().equals("\n")){
					pa.translate(-thisLineTotalXAdvancement, fontHeight, 0);
					thisLineTotalXAdvancement = 0;
					lastXAdvancement = innerPaddingLeft;
				}else{
					//If caret is showing and we are at index one before caret calc the advancement
					if (enableCaret && showCaret && i == charListSize - 2){
						if (character.getUnicode().equals("\t")){
							lastXAdvancement = character.getHorizontalDist() - character.getHorizontalDist( ) / 20;
						}else{
							//approximated value, cant get the real one
							lastXAdvancement = 2 + character.getHorizontalDist() - (character.getHorizontalDist() / 3);
						}
					}else{
						lastXAdvancement = character.getHorizontalDist();
					}
				}
			}
			
			g.tint(255,255,255,255); //Reset Tint
			
			g.popMatrix();//FIXME TEST text scrolling - but IMHO better done with parent list/scroll container
		}
		
		//Innerpadding TOP for wrapped textarea -> translates the text content downwards
		switch (this.mode) {
		case MODE_EXPAND:
			g.popMatrix();
			break;
		case MODE_WRAP:
			//Need to translate innerpadding because we shouldnt make the textarea bigger
			g.popMatrix();
			break;
		default:
			break;
		}
		
		//remove caret
		if (enableCaret && showCaret){
			characterList.remove(charListSize-1);
		}
		
		//FIXME TEST
		if (isTextureFont && textPositionRounding && applySnap){
			g.popMatrix();
		}
	}
	
	
	public void setFontColor(MTColor fontColor){
		this.getFont().setFillColor(fontColor);
	}
	
	private void drawCharactersGL(GL10 gl, IFont font, List<IFontCharacter> characterList, int charListSize, int lastXAdv, int lineTotalAdv){
		int lastXAdvancement = lastXAdv;
		int thisLineTotalXAdvancement = lineTotalAdv;
		
		font.beginBatchRenderGL(gl, font); 
		
		for (int i = 0; i < charListSize; i++) {
			IFontCharacter character = characterList.get(i);
			//Step to the right by the amount of the last characters x advancement
			gl.glTranslatef(lastXAdvancement, 0, 0);
			//Save total amount gone to the right in this line 
			thisLineTotalXAdvancement += lastXAdvancement;
			lastXAdvancement = 0;

			//Draw the letter
			character.drawComponent(gl);

			//Check if newLine occurs, goto start at new line
			if (character.getUnicode().equals("\n")){
				gl.glTranslatef(-thisLineTotalXAdvancement, fontHeight, 0);
				thisLineTotalXAdvancement = 0;
				lastXAdvancement = innerPaddingLeft;
			}else{
				//If caret is showing and we are at index one before caret calc the advancement to include the caret in the text area
				if (enableCaret && showCaret && i == charListSize-2){
					if (character.getUnicode().equals("\t")){
//						lastXAdvancement = character.getHorizontalDist() - character.getHorizontalDist() / 20;
						lastXAdvancement = character.getHorizontalDist() - character.getHorizontalDist() / 20;
					}else{
						//approximated value, cant get the real one
//						lastXAdvancement = 2 + character.getHorizontalDist() - (character.getHorizontalDist() / 3);
						lastXAdvancement = 2 + character.getHorizontalDist() - (character.getHorizontalDist() / 3);
					}
				}else{
//					lastXAdvancement = character.getHorizontalDist();
					if (enableKerning && i+1 < charListSize){
						lastXAdvancement = character.getHorizontalDist() + character.getKerning(characterList.get(i+1).getUnicode());
//						if (character.getKerning(characterList.get(i+1).getUnicode()) != 0 ){
//							System.out.println("Kerning (" + character.getKerning(characterList.get(i+1).getUnicode()) + ") between: " + character.getUnicode() + " and " + characterList.get(i+1).getUnicode());
//						}
//						System.out.println(character.getKerning(characterList.get(i+1).getUnicode()));
					}else{
						lastXAdvancement = character.getHorizontalDist();
					}
					
				}
			}
		}
		
		font.endBatchRenderGL(gl, font);
	}
	
	private boolean noStrokeSettingSaved;
	
	@Override
	public void postDraw(PGraphics graphics) {
		super.postDraw(graphics);
		//Hack for drawing anti aliased stroke outline over the clipped area
		if (this.mode == MODE_WRAP && this.getClip()!= null && !noStrokeSettingSaved){
			this.setNoStroke(noStrokeSettingSaved);
			boolean noFillSavedSetting = this.isNoFill();
			this.setNoFill(true);
			super.drawComponent(graphics);//Draw only stroke line after we ended clipping do preserve anti aliasing - hack
			this.setNoFill(noFillSavedSetting);
		}
	}
	
	//FIXME TEST scrolling (used in MTTextField for example)
	protected void scrollTextX(float amount){
		this.totalScrollTextX += amount;
	}
	protected void scrollTextY(float amount){
		this.totalScrollTextY += amount;
	}
	protected float getScrollTextX() {
		return this.totalScrollTextX;
	}
	protected float getScrollTextY() {
		return this.totalScrollTextY;
	}
	
	
	//FIXME TEST ?
	/**
	 * Changes the texture filtering for the textarea's bitmap font.
	 * (if a bitmap font is used).
	 * If the parameter is "true" this will allow the text being scaled without getting
	 * too pixelated. If the text isnt going to be scaled ever, it is best to leave or
	 * set this to "false" for a sharper text.
	 * <br>NOTE: Only applies if OpenGL is the renderer and the textarea uses a bitmap font.
	 * <br>NOTE: This affects the whole bitmap font so if it is used elsewhere it is changed 
	 * there, too.
	 * 
	 * @param scalable the new bitmap font scalable
	 */
	public void setBitmapFontTextureFiltered(boolean scalable){
		if (MT4jSettings.getInstance().isOpenGlMode() && isTextureFont){
			IFont font = (IFont)this.getFont();
			IFontCharacter[] characters = font.getCharacters();
            for (IFontCharacter fontCharacter : characters) {
                if (fontCharacter instanceof ITextureFontCharacter) {
                	ITextureFontCharacter bChar = (ITextureFontCharacter) fontCharacter;
                    bChar.setTextureFiltered(scalable);
                }
            }
		}
	}
	
	
	
	/**
	 * Sets the width local.
	 * 
	 * @param width the new width local
	 */
	@Override
	public void setWidthLocal(float width){
		super.setWidthLocal(width);
		switch (this.mode) {
		case MODE_EXPAND:
			
			break;
		case MODE_WRAP:
			//if in MODE_WRAP also reset the size of the CLIP SHAPE!
			if (MT4jSettings.getInstance().isOpenGlMode() && this.getClip() != null && this.getClip().getClipShape() instanceof MTRectangle){ 
				MTRectangle clipRect = (MTRectangle)this.getClip().getClipShape();
				//				clipRect.setWidthLocal(this.getWidthXY(TransformSpace.LOCAL));
				//Clip the text to the area
				//				this.setClip(new Clip(pApplet, this.getVerticesLocal()[0].x, this.getVerticesLocal()[0].y, this.getWidthXY(TransformSpace.LOCAL), this.getHeightXY(TransformSpace.LOCAL)));
				//				clipRect.setVertices(Vertex.getDeepVertexArrayCopy(this.getVerticesLocal()));
				clipRect.setVertices(this.getVerticesLocal());
			}
			this.updateLayout();
			break;
		default:
			break;
		}
	}
	/**
	 * Sets the height local.
	 * 
	 * @param height the new height local
	 */
	@Override
	public void setHeightLocal(float height){ 
		Vertex[] v = this.getVerticesLocal();
		switch (this.mode) {
		case MODE_EXPAND:
			this.setVertices(new Vertex[]{
					new Vertex(v[0].x,	0, 								v[0].z, v[0].getTexCoordU(), v[0].getTexCoordV(), v[0].getR(), v[0].getG(), v[0].getB(), v[0].getA()), 
					new Vertex(v[1].x, 	0, 								v[1].z, v[1].getTexCoordU(), v[1].getTexCoordV(), v[1].getR(), v[1].getG(), v[1].getB(), v[1].getA()), 
					new Vertex(v[2].x, 	height + (2 * innerPaddingTop), v[2].z, v[2].getTexCoordU(), v[2].getTexCoordV(), v[2].getR(), v[2].getG(), v[2].getB(), v[2].getA()), 
					new Vertex(v[3].x,	height + (2 * innerPaddingTop),	v[3].z, v[3].getTexCoordU(), v[3].getTexCoordV(), v[3].getR(), v[3].getG(), v[3].getB(), v[3].getA()), 
					new Vertex(v[4].x,	0,								v[4].z, v[4].getTexCoordU(), v[4].getTexCoordV(), v[4].getR(), v[4].getG(), v[4].getB(), v[4].getA()),
			});
			break;
		case MODE_WRAP:
			super.setHeightLocal(height);
			//if in MODE_WRAP also reset the size of the CLIP SHAPE!
			if (MT4jSettings.getInstance().isOpenGlMode() && this.getClip() != null && this.getClip().getClipShape() instanceof MTRectangle){ 
				MTRectangle clipRect = (MTRectangle)this.getClip().getClipShape();
				//				clipRect.setVertices(Vertex.getDeepVertexArrayCopy(this.getVerticesLocal()));
				clipRect.setVertices(this.getVerticesLocal());
			}
			this.updateLayout();
			break;
		default:
			break;
		}
	}

    /**
     * Returns the currently active expand direction of the text area.
     * (Only has an impact if the wrap mode of the textarea equals MODE_EXPAND!)
     *
     * @return the active expand direction
     */
    public ExpandDirection getExpandDirection() {
        return expandDirection;
    }

    /**
     * Sets the expand direction to be used by the text area if a new line is added.
     *(Only has an impact if the wrap mode of the textarea equals MODE_EXPAND!)
     * @see {@link ExpandDirection}
     * @param direction the expand direction to be used
     */
    public void setExpandDirection(ExpandDirection direction) {
        expandDirection = direction;
        this.updateLayout(); //This wont translate the area to its original place if expand mode was UP and is now down..
    }

    @Override
	public void setSizeLocal(float width, float height) {
		if (width > 0 && height > 0){
			Vertex[] v = this.getVerticesLocal();
			switch (this.mode) {
			case MODE_EXPAND:
				this.setVertices(new Vertex[]{
						new Vertex(v[0].x,			0, 								v[0].z, v[0].getTexCoordU(), v[0].getTexCoordV(), v[0].getR(), v[0].getG(), v[0].getB(), v[0].getA()), 
						new Vertex(v[0].x+width, 	0, 								v[1].z, v[1].getTexCoordU(), v[1].getTexCoordV(), v[1].getR(), v[1].getG(), v[1].getB(), v[1].getA()), 
						new Vertex(v[0].x+width, 	height + (2 * innerPaddingTop), v[2].getTexCoordV(), v[2].getR(), v[2].getG(), v[2].getB(), v[2].getA()), 
						new Vertex(v[3].x,			height + (2 * innerPaddingTop),	v[3].z, v[3].getTexCoordU(), v[3].getTexCoordV(), v[3].getR(), v[3].getG(), v[3].getB(), v[3].getA()), 
						new Vertex(v[4].x,			0,								v[4].z, v[4].getTexCoordU(), v[4].getTexCoordV(), v[4].getR(), v[4].getG(), v[4].getB(), v[4].getA()), 
				});
				break;
			case MODE_WRAP:
				super.setSizeLocal(width, height);
				//if in MODE_WRAP also reset the size of the CLIP SHAPE!
				if (MT4jSettings.getInstance().isOpenGlMode() && this.getClip() != null && this.getClip().getClipShape() instanceof MTRectangle){ 
					MTRectangle clipRect = (MTRectangle)this.getClip().getClipShape();
					//clipRect.setVertices(Vertex.getDeepVertexArrayCopy(this.getVerticesLocal()));
					clipRect.setVertices(this.getVerticesLocal());
				}
				this.updateLayout();
				break;
			default:
				break;
			}
		}
	}
	
	
	/**
	 * Appends the string to the textarea.
	 * 
	 * @param string the string
	 */
	synchronized public void appendText(String string){
		for (int i = 0; i < string.length(); i++) {
			appendCharByUnicode(string.substring(i, i+1));
		}
	}
	
	/**
	 * Sets the provided string as the text of this textarea.
	 * 
	 * @param string the string
	 */
	synchronized public void setText(String string){
		clear();
		for (int i = 0; i < string.length(); i++) {
			appendCharByUnicode(string.substring(i, i+1));
		}
		
		//FIXME TEST
		/*
		if (MT4jSettings.getInstance().isOpenGlMode()){
			if (getRenderer() instanceof MTApplication) {
				MTApplication app = (MTApplication) getRenderer();
				if (app.isRenderThreadCurrent()){
					this.useContentDisplayList();
				}else{
					app.invokeLater(new Runnable() {
						public void run() {
							useContentDisplayList();
						}
					});
				}
			}else{
				this.useContentDisplayList();
			}
		}
		*/
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.widgets.keyboard.ITextInputListener#getText()
	 */
	public String getText(){
		String returnString = "";
        for (IFontCharacter character : this.characterList) {
            String unicode = character.getUnicode();
            if (!character.equals(MTTextArea.artificialLineBreak)) {
                returnString += unicode;
            }
        }
		return returnString;
	}
	
	
	/**
	 * Append char by name.
	 * @param characterName the character name
	 */
	synchronized public void appendCharByName(String characterName){
		//Get the character from the font
		IFontCharacter character = font.getFontCharacterByName(characterName);
		if (character == null){
			System.err.println("Error adding character with name '" + characterName + "' to the textarea. The font couldnt find the character. -> Trying to use 'missing glyph'");
			character = font.getFontCharacterByName("missing-glyph");
			if (character != null)
				addCharacter(character);
		}else{
			addCharacter(character);
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.widgets.keyboard.ITextInputListener#appendCharByUnicode(java.lang.String)
	 */
	synchronized public void appendCharByUnicode(String unicode){
		//Get the character from the font
		IFontCharacter character = font.getFontCharacterByUnicode(unicode);
		if (character == null){
//			System.err.println("Error adding character with unicode '" + unicode + "' to the textarea. The font couldnt find the character. ->Trying to use 'missing glyph'");
			character = font.getFontCharacterByUnicode("missing-glyph");
			if (character != null)
				addCharacter(character);
		}else{
			addCharacter(character);
		}
	}
	
	
	/**
	 * Gets the characters. Also returns articifially added new line characters that were
	 * added by the MTTextArea
	 * @return the characters
	 */
	public IFontCharacter[] getCharacters(){
		return this.characterList.toArray(new IFontCharacter[this.characterList.size()]);
	}
	
	
	/**
	 * Adds the character.
	 * 
	 * @param character the character
	 */
	private void addCharacter(IFontCharacter character){
		this.characterList.add(character);
		this.characterAdded(character);
	}
	
	/**
	 * Invoked everytime a character is added.
	 *
	 * @param character the character
	 */
	protected void characterAdded(IFontCharacter character){
		switch (this.mode) {
		case MODE_EXPAND:
			if (character.getUnicode().equals("\n")){
				//Expand vertically at enter 
				this.setHeightLocal(this.getTotalLinesHeight());
				//Moves the Textarea up at a enter character instead of down
                if (getExpandDirection() == ExpandDirection.UP)
                    this.translate(new Vector3D(0, -fontHeight, 0));
            }else{
				//Expand the textbox to the extend of the widest line width
				this.setWidthLocal(getMaxLineWidth());
			}
			break;
		case MODE_WRAP:
			float localWidth = this.getWidthXY(TransformSpace.LOCAL);
//			float maxLineWidth = this.getMaxLineWidth(); 
			float maxLineWidth = this.getLastLineWidth();
			
			if (this.characterList.size() > 0 && maxLineWidth > localWidth ) {
//			if (this.characterList.size() > 0 && maxLineWidth > (localWidth - 2 * this.getInnerPaddingLeft())) {
//				this.characterList.add(this.characterList.size() -1 , this.font.getFontCharacterByUnicode("\n"));
				try {
					int lastSpacePos = getLastWhiteSpace();
					if (lastSpacePos != -1 ){ //&& !this.characterList.get(characterList.size()-1).getUnicode().equals("\n")
//						this.characterList.add(lastSpacePos + 1, this.font.getFontCharacterByUnicode("\n"));
						this.characterList.add(lastSpacePos + 1, MTTextArea.artificialLineBreak);
					}else{
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}
	
	
	private int getLastWhiteSpace(){
		for (int i = this.characterList.size()-1; i > 0; i--) {
			IFontCharacter character = this.characterList.get(i);
			if (character.getUnicode().equals(" ")){
				return i;
			}else if (character.getUnicode().equals("\n")){// stop search when newline found before first whitespace
				return -1;
			}
		}
		return -1;
	}
	
	
	/**
	 * When Character removed.
	 *
	 * @param character the character
	 */
	protected void characterRemoved(IFontCharacter character){
		switch (this.mode) {
		case MODE_EXPAND:
			//Resize text field
			if (character.getUnicode().equals("\n")){
				//Reduce field vertically at enter
				this.setHeightLocal(this.getTotalLinesHeight());
				//makes the textarea go down when a line is removed instead staying at the same loc.
				if (getExpandDirection() == ExpandDirection.UP)
                    translate(new Vector3D(0, fontHeight, 0));
			}else{
				//Reduce field horizontally
				this.setWidthLocal(getMaxLineWidth());
			}
			break;
		case MODE_WRAP:
			
			break;
		default:
			break;
		}
	}
	
	
	
	/**
	 * resets the textarea, clears all characters.
	 */
	public void clear(){
		while (!characterList.isEmpty()){
			removeLastCharacter();
		}
	}
	
	
	/**
	 * Removes the last character in the textarea.
	 */
	synchronized public void removeLastCharacter(){
		if (this.characterList.isEmpty())
			return;
		
		//REMOVE THE CHARACTER
		IFontCharacter lastCharacter = this.characterList.get(this.characterList.size()-1);
		this.characterList.remove(this.characterList.size()-1);
		
		this.characterRemoved(lastCharacter);
	}
	
	
	/**
	 * Gets the last line width.
	 *
	 * @return the last line width
	 */
	protected float getLastLineWidth(){
		float currentLineWidth = 2 * this.getInnerPaddingLeft() + caretWidth;
        for (IFontCharacter character : this.characterList) {
            if (character.getUnicode().equals("\n")) {
                currentLineWidth = 2 * this.getInnerPaddingLeft() + caretWidth;
            } else {
                currentLineWidth += character.getHorizontalDist();
            }
        }
		return currentLineWidth;
	}
	
	
	/**
	 * Gets the max line width. The padding is also added.
	 * 
	 * @return the max line width
	 */
	protected float getMaxLineWidth(){
		float currentLineWidth = 2 * this.getInnerPaddingLeft() + caretWidth;
		float maxWidth = currentLineWidth;

		int characterListSize = this.characterList.size();
		for (int i = 0; i < characterListSize; i++) {
			IFontCharacter character = characterList.get(i);
			 if (character.getUnicode().equals("\n")) {
	                if (currentLineWidth > maxWidth) {
	                    maxWidth = currentLineWidth;
	                }
	                currentLineWidth = 2 * this.getInnerPaddingLeft() + caretWidth;
	            } else {
	            	
	            	if (enableKerning && i-1 > 0){
	            		int kern = characterList.get(i-1).getKerning(character.getUnicode());
	            		currentLineWidth += character.getHorizontalDist() + kern;
	            	}else{
	            		currentLineWidth += character.getHorizontalDist();	
	            	}
	                
	                if (currentLineWidth > maxWidth) {
	                    maxWidth = currentLineWidth;
	                }
	            }
		}
		
//        for (IFontCharacter character : this.characterList) {
//            if (character.getUnicode().equals("\n")) {
//                if (currentLineWidth > maxWidth) {
//                    maxWidth = currentLineWidth;
//                }
//                currentLineWidth = 2 * this.getInnerPaddingLeft() + caretWidth;
//            } else {
//                currentLineWidth += character.getHorizontalDist();
//                if (currentLineWidth > maxWidth) {
//                    maxWidth = currentLineWidth;
//                }
//            }
//        }
		return maxWidth;
	}

	
	/**
	 * Gets the total lines height. Padding is not included
	 * 
	 * @return the total lines height
	 */
	protected float getTotalLinesHeight(){
		float height = fontHeight ;//
        for (IFontCharacter character : this.characterList) {
            if (character.getUnicode().equals("\n")) {
                height += fontHeight;
            }
        }
		return height;
	}
	
	
	/**
	 * Sets the padding (Top: 5 + value, Left: 8 + value)
	 * @param padding
	 */
	
	public void setPadding(float padding) {
		innerPaddingTop = 5 + (int)padding;
		innerPaddingLeft = 8 + (int)padding;
		this.updateLayout();
	}
	
	
	public void setInnerPadding(int innerPadding){
		this.setInnerPaddingTop(innerPadding);
		this.setInnerPaddingLeft(innerPadding);
	}

	public float getInnerPaddingTop() {
		return this.innerPaddingTop;
	}

	public void setInnerPaddingTop(int innerPaddingTop) {
		this.innerPaddingTop = innerPaddingTop;
		switch (this.mode) {
		case MODE_EXPAND:
			//At MODE_EXPAND we re-set the text so the size gets re-calculated
			//We can safely do this since in EXPAND mode we didnt add any artificial control characters
			this.updateLayout();
			break;
		case MODE_WRAP:
			//At MODE_WRAP the padding is done with gl_Translate calls so we dont have to reset the size
			//TODO also reset? this.setText(this.getText());?
			break;
		default:
			break;
		}
	}

	public float getInnerPaddingLeft() {
		return this.innerPaddingLeft;
	}

	public void setInnerPaddingLeft(int innerPaddingLeft) {
		this.innerPaddingLeft = innerPaddingLeft;
		switch (this.mode) {
		case MODE_EXPAND:
			//At MODE_EXPAND we re-set the text so the size gets re-calculated
			//We can safely do this since in EXPAND mode we didnt add any artificial control characters
			this.updateLayout();
			break;
		case MODE_WRAP:
			// WE HAVE TO RESET THE ORIGINAL TEXT BECAUSE WE BREAK THE LINE AT DIFFERENT POSITIONS IF THE INNERPADDING IS CHANGED!
			this.updateLayout();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Updates layout. (just does this.setText(this.getText()))
	 */
	protected void updateLayout(){
		if (this.mode == MODE_EXPAND){
			this.setHeightLocal(this.getTotalLinesHeight());
			this.setWidthLocal(getMaxLineWidth());
		}
		this.setText(this.getText());
	}


	/**
	 * Gets the line count.
	 * 
	 * @return the line count
	 */
	public int getLineCount(){
		int count = 0;
        for (IFontCharacter character : this.characterList) {
            if (character.getUnicode().equals("\n")) {
                count++;
            }
        }
		return count;
	}
	
	
	
	/**
	 * Gets the font.
	 * 
	 * @return the font
	 */
	public IFont getFont() {
		return font;
	}

//	/**
//	 * Snap to keyboard.
//	 * 
//	 * @param mtKeyboard the mt keyboard
//	 */
//	public void snapToKeyboard(MTKeyboard mtKeyboard){
//		//OLD WAY
////		this.translate(new Vector3D(30, -(getFont().getFontAbsoluteHeight() * (getLineCount())) + getFont().getFontMaxDescent() - borderHeight, 0));
//		mtKeyboard.addChild(this);
//		this.setPositionRelativeToParent(new Vector3D(40, -this.getHeightXY(TransformSpace.LOCAL)*0.5f));
//	}


	/**
	 * Checks if is enable caret.
	 * 
	 * @return true, if is enable caret
	 */
	public boolean isEnableCaret() {
		return enableCaret;
	}


	/**
	 * Sets the enable caret.
	 * 
	 * @param enableCaret the new enable caret
	 */
	public void setEnableCaret(boolean enableCaret) {
		if (this.getFont().getFontCharacterByUnicode("|") != null){
			this.enableCaret = enableCaret;
			
			if (enableCaret){
				this.caretWidth = 10;
			}else{
				this.caretWidth = 0;
			}
			
			if (this.mode == MODE_EXPAND){
				this.setWidthLocal(this.getMaxLineWidth());
			}
		}else{
			System.err.println("Cant enable caret for this textfield, the font doesent include the letter '|'");
		}
	}


	public int compareTo(Object o) {
		if (o instanceof MTTextArea) {
			MTTextArea ta = (MTTextArea)o;
			return this.getText().compareToIgnoreCase(ta.getText());
		} else {
			return 0;
		}
	}
	
	
	
	/**
	 * Artifical line break to be used instead of the regular line break
	 * to indicate that this linebreak was added by the text area itself for
	 * layout reasons and doesent really belong to the supplied text.
	 * 
	 * @author Christopher Ruff
	 */
	protected class ArtificalLineBreak implements IFontCharacter{
		public void drawComponent(PGraphics g) {}
		public void drawComponent(GL10 gl) {	}
		public void destroy() {	}
		public int getHorizontalDist() {
			return 0;
		}
		public String getUnicode() {
			return "\n";
		}
		@Override
		public int getKerning(String character) {
			return 0;
		}
		
	}
	
	
	@Override
	protected void applyStyleSheetCustom(CSSStyle virtualStyleSheet) {
		super.applyStyleSheetCustom(virtualStyleSheet);

		if (this.getRenderer() instanceof AbstractMTApplication) {
			AbstractMTApplication app = (AbstractMTApplication) this.getRenderer();
			if (!virtualStyleSheet.getFont().equals(
					app.getCssStyleManager().getDefaultFont(app))
					&& !this.isIgnoreCSSFont()) {
				this.setFont(virtualStyleSheet.getFont());
			}
			if (virtualStyleSheet.isModifiedPaddingWidth()) {
				this.setPadding(virtualStyleSheet.getPaddingWidth());
			}
		}
	}


	/**
	 * Checks if is kerning.
	 *
	 * @return true, if is kerning
	 */
	public boolean isKerning() {
		return enableKerning;
	}


	/**
	 * Enables / disables kerning. At the moment this isnt supported by all font types.
	 *
	 * @param enableKerning the new kerning
	 */
	public void setKerning(boolean enableKerning) {
		this.enableKerning = enableKerning;
	}
	
	

	
}
