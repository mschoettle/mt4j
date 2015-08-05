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
package org.mt4j.components.visibleComponents.widgets.keyboard;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mt4j.IMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsArbitraryPlanarPolygon;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.font.VectorFontCharacter;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTSvgButton;
import org.mt4j.input.IKeyListener;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.Animation;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.MultiPurposeInterpolator;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

/**
 * A multitouch keyboard using vector graphics.
 * 
 * @author Christopher Ruff
 */
public class MTKeyboard extends MTRoundRectangle implements IKeyListener {
	
	/** The pa. */
	private PApplet pa;
	
	/** The key font. */
	private IFont keyFont;
	
	/** The key list. */
	private ArrayList<MTKey> keyList;
	
	/** The shift changers. */
	private ArrayList<MTKey> shiftChangers;
	
	/** The shift pressed. */
	private boolean shiftPressed;
	
	/** The key click action. */
	private KeyClickAction keyClickAction;

	/** The text input acceptors. */
	private List<ITextInputListener> textInputAcceptors;
	
	private boolean hardwareInput;
	

	/**
	 * Creates a new keyboard without an text input acceptor.
	 * 
	 * @param pApplet the applet
	 */
	public MTKeyboard(PApplet pApplet) {
		super(pApplet,0,0, 0, 700, 245,30, 30);
		this.pa = pApplet;
		//Set drawing mode
		this.setDrawSmooth(true);
		
		this.setHardwareInputEnabled(true);
		
		this.setName("unnamed mt-keyboard");
		this.textInputAcceptors = new ArrayList<ITextInputListener>();
		
		if (MT4jSettings.getInstance().isOpenGlMode())
			this.setUseDirectGL(true);
		
		//TODO button textarea clear 
		//TODO keyboard animated creation
		
		MTColor keyColor = new MTColor(0,0,0,255);
		
		// INIT FIELDS
		//Load the Key font
		keyFont = FontManager.getInstance().createFont(pa, 
				"keys.svg", 30, keyColor); 
		
		keyList 		= new ArrayList<MTKey>();
		shiftChangers 	= new ArrayList<MTKey>();
		shiftPressed 	= false;
		keyClickAction 	= new KeyClickAction();
		
//		/*
		//TODO load button only once!
		MTSvgButton keybCloseSvg = new MTSvgButton(pa, MT4jSettings.getInstance().getDefaultSVGPath()
						+ "keybClose.svg");
		//Transform
		keybCloseSvg.scale(0.8f, 0.8f, 1, new Vector3D(0,0,0));
		keybCloseSvg.translate(new Vector3D(640,5,0));
		keybCloseSvg.setBoundsPickingBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
		keybCloseSvg.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
					onCloseButtonClicked();
				}
				return false;
			}
		});
		this.addChild(keybCloseSvg);
//		*/
		
		
		/*
		 We get the key's shapes from the keys.svg svg font. 
		 To save memory, we share the font's characters geometryinfo with the MTKey shapes.
		 */
		
		//INITIALIZE SPACE-Button "by hand"
		VectorFontCharacter SpaceF = (VectorFontCharacter) keyFont.getFontCharacterByUnicode("k");
		MTKey space = new MTKey(/*spaceOutlines,*/pa, SpaceF.getGeometryInfo(), " ", " ");
		space.setName(SpaceF.getName());
		//Set the contours to draw the outline - do this after settings the geominfo
		//so the outline displaylist will be overridden with the new contours list
		space.setOutlineContours(SpaceF.getContours()); //FIXME do we have to in opengl mode? -> we will use displayLists anyway..
		if (MT4jSettings.getInstance().isOpenGlMode()){
			space.setUseDirectGL(true);
//			System.out.println(SpaceF.getDisplayListIDs()[0] + "  - " + SpaceF.getDisplayListIDs()[1]);
			space.getGeometryInfo().setDisplayListIDs(SpaceF.getGeometryInfo().getDisplayListIDs()); //Wenn nicht displaylisten, mï¿½ssen wir geometry auch ï¿½bernehmen!
			space.setUseDisplayList(true);
		}
		space.setFillColor(keyColor);
		space.setNoStroke(false);
		space.setDrawSmooth(true);
		space.setPickable(true);
		space.setGestureAllowance(DragProcessor.class, false);
		space.setGestureAllowance(RotateProcessor.class, false);
		space.setGestureAllowance(ScaleProcessor.class, false);
		space.unregisterAllInputProcessors();
		scaleKey(space, 40);
		space.setPositionRelativeToParent(new Vector3D(350,210,0));
		space.setGestureAllowance(TapProcessor.class, true);
		space.registerInputProcessor(new TapProcessor(pa));
		space.addGestureListener(TapProcessor.class, keyClickAction);
		SpaceF = null;
		keyList.add(space);
		this.addChild(space);

		KeyInfo[] keyInfos = this.getKeysLayout();
		
		//CREATE THE KEYS \\
        for (KeyInfo keyInfo : keyInfos) {
            VectorFontCharacter fontChar = (VectorFontCharacter) keyFont.getFontCharacterByUnicode(keyInfo.keyfontUnicode);
            MTKey key = new MTKey(pa, fontChar.getGeometryInfo(), keyInfo.charUnicodeToWrite, keyInfo.charUnicodeToWriteShifted);
            key.setName(fontChar.getName());
            key.setPickable(true);
            key.setFillColor(keyColor);
            key.unregisterAllInputProcessors();

            key.setOutlineContours(fontChar.getContours());
            if (MT4jSettings.getInstance().isOpenGlMode()) {
                key.setUseDirectGL(true);
                //Use the display lists already created for the font characters of the key-font
                key.getGeometryInfo().setDisplayListIDs(fontChar.getGeometryInfo().getDisplayListIDs());
                key.setUseDisplayList(true);
            }

            scaleKey(key, 40);

            //Scale ENTER and BACKSPACE
            if (key.getCharacterToWrite().equals("\n")) {
                key.scale(1.70f, 1.70f, 1, key.getCenterPointLocal(), TransformSpace.LOCAL);
            }

            key.setPositionRelativeToParent(keyInfo.position);

            //this is a hack to fit the bounding shape of the "enter" key to its non-rectangular shape
            if (key.getCharacterToWrite().equals("\n")) {
                Vector3D[] v = key.getBounds().getVectorsLocal();
                float indent = (v[1].getX() - v[0].getX()) / 2f;
                Vertex[] vNew = new Vertex[]{
                        new Vertex(v[0].getX(), v[0].getY() + indent, 0)
                        , new Vertex(v[0].getX() + indent - indent / 8f, v[0].getY() + indent, 0) //
                        , new Vertex(v[0].getX() + indent - indent / 8f, v[0].getY(), 0) //
                        , new Vertex(v[1])
                        , new Vertex(v[2])
                        , new Vertex(v[3])
                        , new Vertex(v[0].getX(), v[0].getY() + indent, 0)
                };
                BoundsArbitraryPlanarPolygon newBounds = new BoundsArbitraryPlanarPolygon(key, vNew); //Expensive..
                key.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
                key.setBounds(newBounds);
            }

            keyList.add(key);
            key.setGestureAllowance(TapProcessor.class, true);
            TapProcessor tp = new TapProcessor(pa);
//            tp.setLockPriority(1.5f); 
            tp.setLockPriority(5f); //FIXME TEST
            tp.setStopPropagation(false);
            key.registerInputProcessor(tp);
            key.addGestureListener(TapProcessor.class, keyClickAction);

            //Add keys that change during SHIFT to a list
            if (keyInfo.visibilityInfo == KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED) {
                shiftChangers.add(key);
            } else if (keyInfo.visibilityInfo == KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED) {
                key.setVisible(false);
                shiftChangers.add(key);
            }

            fontChar = null;
            this.addChild(key);
        }
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting
		this.setDepthBufferDisabled(true);
	}
	
	
	private KeyInfo[] getKeysLayout(){
		Locale l = Locale.getDefault();
		/*
	      System.out.println("   Language, Country, Variant, Name");
	      System.out.println("");
	      System.out.println("Default locale: ");
	      System.out.println("   "+l.getLanguage()+", "+l.getCountry()+", "
	         +", "+l.getVariant()+", "+l.getDisplayName());
	    */
	    if (l.getLanguage().equalsIgnoreCase(Locale.GERMANY.getLanguage())){
	    	return getGermanLayout();
	    }else{
	    	return getUSLayout();
	    }
	}
	
	
	private KeyInfo[] getGermanLayout(){
		ArrayList<KeyInfo> keyInfos = new ArrayList<KeyInfo>();
		
		float lineY = 35;
//		float advanceMent = keyFont.getFontCharacterByUnicode("A").getHorizontalDist()-10;
		float advanceMent = 42;
		float startX = 60;
//		keyInfos.add(new KeyInfo("^", "^", "^", new Vector3D(startX,lineY), 			  KeyInfo.NORMAL_KEY)); //ESC key
		
		keyInfos.add(new KeyInfo("1", "1", "1", new Vector3D(startX+1*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("!", "!", "!", new Vector3D(startX+1*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("2", "2", "2", new Vector3D(startX+2*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("\"", "\"", "\"", new Vector3D(startX+2*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("3", "3", "3", new Vector3D(startX+3*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		
		keyInfos.add(new KeyInfo("4", "4", "4", new Vector3D(startX+4*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("$", "$", "$", new Vector3D(startX+4*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("5", "5", "5", new Vector3D(startX+5*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("%", "%", "%", new Vector3D(startX+5*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("6", "6", "6", new Vector3D(startX+6*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("&", "&", "&", new Vector3D(startX+6*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("7", "7", "7", new Vector3D(startX+7*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("/", "/", "/", new Vector3D(startX+7*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("8", "8", "8", new Vector3D(startX+8*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("(", "(", "(", new Vector3D(startX+8*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("9", "9", "9", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo(")", ")", ")", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("0", "0", "0", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("=", "=", "=", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("\\", "\\", "\\", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("?", "?", "?", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		//////////////////
		lineY = 77;
		startX = 80; // | + 27
		
		keyInfos.add(new KeyInfo("Q", "q", "Q", new Vector3D(startX+1*advanceMent,lineY),  KeyInfo.NORMAL_KEY)); 
		keyInfos.add(new KeyInfo("W", "w", "W", new Vector3D(startX+2*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("E", "e", "E", new Vector3D(startX+3*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("R", "r", "R", new Vector3D(startX+4*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("T", "t", "T", new Vector3D(startX+5*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("Z", "z", "Z", new Vector3D(startX+6*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("U", "u", "U", new Vector3D(startX+7*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("I", "i", "I", new Vector3D(startX+8*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("O", "o", "O", new Vector3D(startX+9*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("P", "p", "P", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		//Ü
		keyInfos.add(new KeyInfo("111", "ü", "Ü", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		
		keyInfos.add(new KeyInfo("+", "+", "+", new Vector3D(startX+12*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("*", "*", "*", new Vector3D(startX+12*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		lineY = 119;
		startX = 136;  //+58
		keyInfos.add(new KeyInfo("A", "a", "A", new Vector3D(startX,lineY), 			   KeyInfo.NORMAL_KEY)); //
		keyInfos.add(new KeyInfo("S", "s", "S", new Vector3D(startX+1*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("D", "d", "D", new Vector3D(startX+2*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("F", "f", "F", new Vector3D(startX+3*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("G", "g", "G", new Vector3D(startX+4*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("H", "h", "H", new Vector3D(startX+5*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("J", "j", "J", new Vector3D(startX+6*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("K", "k", "K", new Vector3D(startX+7*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("L", "l", "L", new Vector3D(startX+8*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		//Ö
		keyInfos.add(new KeyInfo("1111", "ö", "Ö", new Vector3D(startX+9*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		//Ä
        keyInfos.add(new KeyInfo("11", "ä", "Ä", new Vector3D(startX+10*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		
		////////////////////
		lineY = 161;
		startX = 70; // -60
		keyInfos.add(new KeyInfo("<", "<", "<", new Vector3D(startX+1*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo(">", ">", ">", new Vector3D(startX+1*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("Y", "y", "Y", new Vector3D(startX+2*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("X", "x", "X", new Vector3D(startX+3*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("C", "c", "C", new Vector3D(startX+4*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("V", "v", "V", new Vector3D(startX+5*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("B", "b", "B", new Vector3D(startX+6*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("N", "n", "N", new Vector3D(startX+7*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("M", "m", "M", new Vector3D(startX+8*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		
		keyInfos.add(new KeyInfo(",", ",", ",", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo(";", ";", ";", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo(".", ".", ".", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo(":", ":", ":", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("-", "-", "-", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		
		keyInfos.add(new KeyInfo("#", "#", "#", new Vector3D(startX+12*advanceMent,lineY),  KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("'", "'", "'", new Vector3D(startX+12*advanceMent,lineY),  KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		///////////
		//Special keys
		keyInfos.add(new KeyInfo("z", "back", "back", 	new Vector3D(580,35),  KeyInfo.NORMAL_KEY));//Backspace
		keyInfos.add(new KeyInfo("v", "\t", "\t", 	new Vector3D(62,77),  KeyInfo.NORMAL_KEY)); //Tab
		keyInfos.add(new KeyInfo("j", "shift", "shift", new Vector3D(78,120), KeyInfo.NORMAL_KEY)); //Shift
		keyInfos.add(new KeyInfo("f", "\n", "\n", 		new Vector3D(615, 105),KeyInfo.NORMAL_KEY)); //Enter
		
		return keyInfos.toArray(new KeyInfo[keyInfos.size()]);
	}
	
	
	
	//FIXME no "@" available in key font?
	
	private KeyInfo[] getUSLayout(){
		ArrayList<KeyInfo> keyInfos = new ArrayList<KeyInfo>();
		
		float lineY = 35;
//		float advanceMent = keyFont.getFontCharacterByUnicode("A").getHorizontalDist()-10;
		float advanceMent = 42;
		float startX = 60;
//		keyInfos.add(new KeyInfo("^", "^", "^", new Vector3D(startX,lineY), 			  KeyInfo.NORMAL_KEY)); //ESC key
		
		keyInfos.add(new KeyInfo("1", "1", "1", new Vector3D(startX+1*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("!", "!", "!", new Vector3D(startX+1*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("2", "2", "2", new Vector3D(startX+2*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		//FIXME should be "@" here
		
		keyInfos.add(new KeyInfo("3", "3", "3", new Vector3D(startX+3*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("#", "#", "#", new Vector3D(startX+3*advanceMent,lineY),  KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("4", "4", "4", new Vector3D(startX+4*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("$", "$", "$", new Vector3D(startX+4*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("5", "5", "5", new Vector3D(startX+5*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("%", "%", "%", new Vector3D(startX+5*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("6", "6", "6", new Vector3D(startX+6*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		//FIXME "^" missing
		
		keyInfos.add(new KeyInfo("7", "7", "7", new Vector3D(startX+7*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("&", "&", "&", new Vector3D(startX+7*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("8", "8", "8", new Vector3D(startX+8*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("*", "*", "*", new Vector3D(startX+8*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("9", "9", "9", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("(", "(", "(", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("0", "0", "0", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo(")", ")", ")", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("=", "=", "=", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("+", "+", "+", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		
		//////////////////
		lineY = 77;
		startX = 80; // | + 27
		
		keyInfos.add(new KeyInfo("Q", "q", "Q", new Vector3D(startX+1*advanceMent,lineY),  KeyInfo.NORMAL_KEY)); 
		keyInfos.add(new KeyInfo("W", "w", "W", new Vector3D(startX+2*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("E", "e", "E", new Vector3D(startX+3*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("R", "r", "R", new Vector3D(startX+4*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("T", "t", "T", new Vector3D(startX+5*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("Y", "y", "Y", new Vector3D(startX+6*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		
		keyInfos.add(new KeyInfo("U", "u", "U", new Vector3D(startX+7*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("I", "i", "I", new Vector3D(startX+8*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("O", "o", "O", new Vector3D(startX+9*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("P", "p", "P", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		//ï¿½
//		keyInfos.add(new KeyInfo("111", "ï¿½", "ï¿½", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		
		//FIXME woanders hin
		keyInfos.add(new KeyInfo("\\", "\\", "\\", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		
		keyInfos.add(new KeyInfo("-", "-", "-", new Vector3D(startX+12*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		
		
		lineY = 119;
		startX = 136;  //+58
		keyInfos.add(new KeyInfo("A", "a", "A", new Vector3D(startX,lineY), 			   KeyInfo.NORMAL_KEY)); //
		keyInfos.add(new KeyInfo("S", "s", "S", new Vector3D(startX+1*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("D", "d", "D", new Vector3D(startX+2*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("F", "f", "F", new Vector3D(startX+3*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("G", "g", "G", new Vector3D(startX+4*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("H", "h", "H", new Vector3D(startX+5*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("J", "j", "J", new Vector3D(startX+6*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("K", "k", "K", new Vector3D(startX+7*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("L", "l", "L", new Vector3D(startX+8*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		//ï¿½
//		keyInfos.add(new KeyInfo("1111", "ï¿½", "ï¿½", new Vector3D(startX+9*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		//ï¿½
//		keyInfos.add(new KeyInfo("11", "ï¿½", "ï¿½", new Vector3D(startX+10*advanceMent,lineY),  KeyInfo.NORMAL_KEY));
		
		keyInfos.add(new KeyInfo(";", ";", ";", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo(":", ":", ":", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo("'", "'", "'", new Vector3D(startX+10*advanceMent,lineY),  KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("\"", "\"", "\"", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		////////////////////
		lineY = 161;
		startX = 70; // -60
		
		
		keyInfos.add(new KeyInfo("Z", "z", "Z", new Vector3D(startX+2*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("X", "x", "X", new Vector3D(startX+3*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("C", "c", "C", new Vector3D(startX+4*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("V", "v", "V", new Vector3D(startX+5*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("B", "b", "B", new Vector3D(startX+6*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("N", "n", "N", new Vector3D(startX+7*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		keyInfos.add(new KeyInfo("M", "m", "M", new Vector3D(startX+8*advanceMent,lineY), KeyInfo.NORMAL_KEY));
		
		keyInfos.add(new KeyInfo(",", ",", ",", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("<", "<", "<", new Vector3D(startX+9*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		keyInfos.add(new KeyInfo(".", ".", ".", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo(">", ">", ">", new Vector3D(startX+10*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
				
		//FIXME wohin
		keyInfos.add(new KeyInfo("/", "/", "/", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED));
		keyInfos.add(new KeyInfo("?", "?", "?", new Vector3D(startX+11*advanceMent,lineY), KeyInfo.KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED));
		
		///////////
		//Special keys
		keyInfos.add(new KeyInfo("z", "back", "back", 	new Vector3D(580,35),  KeyInfo.NORMAL_KEY));//Backspace
		keyInfos.add(new KeyInfo("v", "\t", "\t", 	new Vector3D(62,77),  KeyInfo.NORMAL_KEY)); //Tab
		keyInfos.add(new KeyInfo("j", "shift", "shift", new Vector3D(78,120), KeyInfo.NORMAL_KEY)); //Shift
		keyInfos.add(new KeyInfo("f", "\n", "\n", 		new Vector3D(615, 105),KeyInfo.NORMAL_KEY)); //Enter
		
		return keyInfos.toArray(new KeyInfo[keyInfos.size()]);
	}
	
	
	private void scaleKey(MTKey key, float scale){
		Vector3D scalingPoint = key.getCenterPointLocal();
		key.scale(scale * (1/key.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)), scale* (1/key.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)), 1, scalingPoint);
	}
	
	
	private boolean setWidthRelativeToParent(float width){
		if (width > 0){
			Vector3D centerPoint;
			if (this.hasBounds()){
				centerPoint = this.getBounds().getCenterPointLocal();
				centerPoint.transform(this.getLocalMatrix());
			}else{
				centerPoint = this.getCenterPointGlobal();
				centerPoint.transform(this.getGlobalInverseMatrix());
			}
			this.scale(width * (1/this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)) , width* (1/this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)), 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	@Override
	protected void setDefaultGestureActions() {
//		super.setDefaultGestureActions();
		
		this.addGestureListener(DragProcessor.class, new InertiaDragAction());
		
		DragProcessor dp = new DragProcessor(getRenderer());
//		dp.setLockPriority(0.5f);
		registerInputProcessor(dp);
		addGestureListener(DragProcessor.class, new DefaultDragAction());
		dp.setBubbledEventsEnabled(true); //FIXME TEST
		
		RotateProcessor rp = new RotateProcessor(getRenderer());
//		rp.setLockPriority(0.8f);
		registerInputProcessor(rp);
		addGestureListener(RotateProcessor.class, new DefaultRotateAction());
		rp.setBubbledEventsEnabled(true);  //FIXME TEST
		
		ScaleProcessor sp = new ScaleProcessor(getRenderer());
//		sp.setLockPriority(0.8f);
		registerInputProcessor(sp);
		addGestureListener(ScaleProcessor.class, new DefaultScaleAction());
		sp.setBubbledEventsEnabled(true);  //FIXME TEST
	}
	
	
	/**
	 * The Class KeyInfo.
	 * 
	 * @author C.Ruff
	 */
	private class KeyInfo{
		/** The keyfont unicode. */
		String keyfontUnicode;
		
		/** The char unicode to write. */
		String charUnicodeToWrite;
		
		/** The char unicode to write shifted. */
		String charUnicodeToWriteShifted;
		
		/** The position. */
		Vector3D position;
		
		/** The visibility info. */
		int visibilityInfo;
		
		/** The Constant NORMAL_KEY. */
		public static final int NORMAL_KEY 								= 0;
		
		/** The Constant KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED. */
		public static final int KEY_ONLY_VISIBLE_WHEN_SHIFT_NOTPRESSED 	= 1;
		
		/** The Constant KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED. */
		public static final int KEY_ONLY_VISIBLE_WHEN_SHIFT_PRESSED 	= 2;
		
		/**
		 * Instantiates a new key info.
		 * 
		 * @param keyfontUnicode the keyfont unicode
		 * @param charUnicodeToWrite the char unicode to write
		 * @param charUnicodeToWriteShifted the char unicode to write shifted
		 * @param position the position
		 * @param visibilityInfo the visibility info
		 */
		public KeyInfo(String keyfontUnicode, String charUnicodeToWrite, String charUnicodeToWriteShifted, Vector3D position, int visibilityInfo) {
			super();
			this.keyfontUnicode = keyfontUnicode;
			this.charUnicodeToWrite = charUnicodeToWrite;
			this.charUnicodeToWriteShifted = charUnicodeToWriteShifted;
			this.position = position;
			this.visibilityInfo = visibilityInfo;
		}
	}
	
	
	
	/**
	 * The Class KeyClickAction.
	 * 
	 * @author C.Ruff
	 */
	private class KeyClickAction implements IGestureEventListener {
		/** The key press indent. */
		private int keyPressIndent;
		
		/**
		 * Instantiates a new key click action.
		 */
		public KeyClickAction(){
			keyPressIndent = 3;
		}
		
		public boolean processGestureEvent(MTGestureEvent g) {
			if (g instanceof TapEvent){
				TapEvent clickEvent = (TapEvent)g;
				IMTComponent3D clicked = clickEvent.getTarget();
				if (clicked != null && clicked instanceof MTKey){
					MTKey clickedKey = (MTKey)clicked;
					switch (clickEvent.getTapID()) {
					case TapEvent.TAP_DOWN:
						pressKey(clickedKey);
						onKeyboardButtonDown(clickedKey, shiftPressed);
						break;
					case TapEvent.TAP_UP:
						unpressKey(clickedKey);
						onKeyboardButtonUp(clickedKey, shiftPressed);
						break;
					case TapEvent.TAPPED:
						unpressKey(clickedKey);
						onKeyboardButtonClicked(clickedKey, shiftPressed);
						break;
					default:
						break;
					}//switch
				}//instance of key
			}//instanceof clickevent
			return false;
		}//method
		
		
		private void pressKey(MTKey clickedKey) {
			clickedKey.setPressed(true);
			float keyHeight = clickedKey.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
			float keyWidth 	= clickedKey.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
			
			setSizeXYRelativeToParent(clickedKey, keyWidth-keyPressIndent, keyHeight-keyPressIndent);
			
			if (clickedKey.getCharacterToWrite().equals("shift")){
				shiftPressed = true;
				// Make certain keys visible / not visible when shift pressed!
				for (MTKey key: shiftChangers){
					key.setVisible(!key.isVisible());
				}
			}
		}

		private void unpressKey(MTKey clickedKey){
			clickedKey.setPressed(false);
			float kHeight = clickedKey.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
			float kWidth = clickedKey.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
			setSizeXYRelativeToParent(clickedKey, kWidth+keyPressIndent, kHeight+keyPressIndent);
			
			//System.out.println("Button CLICKED: " + clickedKey.getCharacterToWrite());
			if (clickedKey.getCharacterToWrite().equals("shift")){
				shiftPressed = false;
				//Set shift visible keys visible/not visible
				for (MTKey key: shiftChangers){
					key.setVisible(!key.isVisible());
				}
			}
		}
		
		/**
		 * Sets the size xy relative to parent.
		 * 
		 * @param shape the shape
		 * @param width the width
		 * @param height the height
		 * 
		 * @return true, if successful
		 */
		private boolean setSizeXYRelativeToParent(AbstractShape shape, float width, float height){
			if (width > 0 && height > 0){
				Vector3D centerPoint;
				if (shape.hasBounds()){
					centerPoint = shape.getBounds().getCenterPointLocal();
					centerPoint.transform(shape.getLocalMatrix()); //TODO neccessary?
				}else{
					centerPoint = shape.getCenterPointGlobal();
					centerPoint.transform(shape.getGlobalInverseMatrix());
				}
				shape.scale(width* (1/shape.getWidthXY(TransformSpace.RELATIVE_TO_PARENT)), height*(1/shape.getHeightXY(TransformSpace.RELATIVE_TO_PARENT)), 1, centerPoint);
				return true;
			}else
				return false;
		}
	}//class

	
	/**
	 * Called after keyboard button pressed.
	 * 
	 * @param clickedKey the clicked key
	 * @param shiftPressed the shift pressed
	 */
	protected void onKeyboardButtonDown(MTKey clickedKey, boolean shiftPressed){
		ITextInputListener[] listeners = this.getTextInputListeners();
        for (ITextInputListener textInputListener : listeners) {
            if (clickedKey.getCharacterToWrite().equals("back")) {
                textInputListener.removeLastCharacter();
            } else if (clickedKey.getCharacterToWrite().equals("shift")) {
                //no nothing
            } else {
                String charToAdd = shiftPressed ? clickedKey.getCharacterToWriteShifted() : clickedKey.getCharacterToWrite();
                textInputListener.appendCharByUnicode(charToAdd);
            }
        }
	}
	
	/**
	 * Keyboard button up.
	 * 
	 * @param clickedKey the clicked key
	 * @param shiftPressed the shift pressed
	 */
	protected void onKeyboardButtonUp(MTKey clickedKey, boolean shiftPressed){ 
		
	}
	
	/**
	 * Keyboard button clicked.
	 * 
	 * @param clickedKey the clicked key
	 * @param shiftPressed the shift pressed
	 */
	protected void onKeyboardButtonClicked(MTKey clickedKey, boolean shiftPressed){
		
	}
	
	
	public synchronized void addTextInputListener(ITextInputListener textListener){
		if (!this.textInputAcceptors.contains(textListener)){
			this.textInputAcceptors.add(textListener);
		}
	}
	
	public synchronized ITextInputListener[] getTextInputListeners(){
		return this.textInputAcceptors.toArray(new ITextInputListener[this.textInputAcceptors.size()]);
	}
	
	public synchronized void removeTextInputListener(ITextInputListener textListener){
		if (this.textInputAcceptors.contains(textListener)){
			this.textInputAcceptors.remove(textListener);
		}
	}
	
	

	public void close(){
		this.closeKeyboard();
	}
	
	protected void closeKeyboard(){
		float width = this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
		IAnimation keybCloseAnim = new Animation("Keyboard Fade", new MultiPurposeInterpolator(width, 1, 300, 0.2f, 0.5f, 1), this);
		keybCloseAnim.addAnimationListener(new IAnimationListener(){
			public void processAnimationEvent(AnimationEvent ae) {
				switch (ae.getId()) {
				case AnimationEvent.ANIMATION_STARTED:
				case AnimationEvent.ANIMATION_UPDATED:
					float currentVal = ae.getAnimation().getValue();
					setWidthRelativeToParent(currentVal);
					break;
				case AnimationEvent.ANIMATION_ENDED:
					setVisible(false);
					destroy();
					break;	
				default:
					break;
				}//switch
			}//processanimation
		});
		keybCloseAnim.start();
	}

	
	protected void onCloseButtonClicked(){
		this.close();
	}
	
	
	public void setHardwareInputEnabled(boolean hardwareInput){
		try {
			IMTApplication app = (IMTApplication) getRenderer();
//			if (hardwareInput) {
//				app.registerKeyEvent(this);
//			}else{
//				app.unregisterKeyEvent(this);
//			}
			if (hardwareInput) {
				app.addKeyListener(this);
			}else{
				app.removeKeyListener(this);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		this.hardwareInput = hardwareInput;
	}
	
	
	public boolean isHardwareInputEnabled(){
		return this.hardwareInput;
	}

	@Override
	public void keyPressed(char key, int keyCode) {
		//TODO
//		//System.out.println("Key input: " + keyCode);
//		ITextInputListener[] listeners = this.getTextInputListeners();
//		for (ITextInputListener textInputListener : listeners) {
//			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
//				textInputListener.removeLastCharacter();
//			} else if (e.getKeyCode() == KeyEvent.VK_SHIFT
//					|| e.getKeyCode() == KeyEvent.VK_ALT
//					|| e.getKeyCode() == KeyEvent.VK_ALT_GRAPH
//					|| e.getKeyCode() == KeyEvent.VK_CONTROL
//			) {
//				//do nothing
//			} else {
//				textInputListener.appendCharByUnicode(keyCode);
//			}
//		}
	}
	
	@Override
	public void keyRleased(char key , int keyCode) {	}



	public void keyEvent(KeyEvent e){
		if (this.isEnabled()){
			if (e.getID()!= KeyEvent.KEY_PRESSED) return;

			String keyCharString = String.valueOf(e.getKeyChar());
			//System.out.println("Key input: " + keyCode);
			ITextInputListener[] listeners = this.getTextInputListeners();
            for (ITextInputListener textInputListener : listeners) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    textInputListener.removeLastCharacter();
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT
                        || e.getKeyCode() == KeyEvent.VK_ALT
                        || e.getKeyCode() == KeyEvent.VK_ALT_GRAPH
                        || e.getKeyCode() == KeyEvent.VK_CONTROL
                        ) {
                    //do nothing
                } else {
                    textInputListener.appendCharByUnicode(keyCharString);
                }
            }
		}
	} 

	
	@Override
	protected void destroyComponent() {
		super.destroyComponent();
		keyFont = null;
		keyList.clear();
		shiftChangers.clear();
		textInputAcceptors.clear();
		
		if (this.isHardwareInputEnabled()){
			try {
				((IMTApplication) getRenderer()).removeKeyListener(this);
				getRenderer().unregisterKeyEvent(this);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	
	/**
	 * Snap to keyboard.
	 * 
	 * @param mtKeyboard the mt keyboard
	 */
	public void snapToKeyboard(MTTextArea textArea){
		//OLD WAY
//		this.translate(new Vector3D(30, -(getFont().getFontAbsoluteHeight() * (getLineCount())) + getFont().getFontMaxDescent() - borderHeight, 0));
		this.addChild(textArea);
		textArea.setPositionRelativeToParent(new Vector3D(40, -textArea.getHeightXY(TransformSpace.LOCAL)*0.5f));
	}

}
