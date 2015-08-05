/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package advanced.puzzle;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.components.visibleComponents.widgets.MTList;
import org.mt4j.components.visibleComponents.widgets.MTListCell;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.DefaultButtonClickAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.ani.AniAnimation;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

public class PuzzleScene extends AbstractScene{

	private PuzzleFactory pf;
	
	/** The images path. */
	private String imagesPath = "advanced"+AbstractMTApplication.separator+"puzzle"+AbstractMTApplication.separator+"data"+AbstractMTApplication.separator;
	
	/** The images names. */
	private String[] imagesNames = new String[]{
			"Pyramids.jpg", 
			"Grass.jpg",
			"Heidelberg.jpg"
			};
	
	private MTComponent puzzleGroup;
	private int horizontalTiles = 4;
	private int verticalTiles = 3;
	private MTList list;
	private MTRoundRectangle loadingScreen;

	public PuzzleScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		if (!MT4jSettings.getInstance().isOpenGlMode()){
			System.err.println(this.getName() + " is only usable with the OpenGL renderer.");
		}

		this.setClearColor(new MTColor(55,55,55));
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		this.getCanvas().setDepthBufferDisabled(true); //to avoid display errors because everything is 2D
		
		MTBackgroundImage background = new MTBackgroundImage(mtApplication, mtApplication.loadImage(imagesPath + "webtreats_wood-pattern1-512d.jpg") , true);
		this.getCanvas().addChild(background);
		
		this.puzzleGroup = new MTComponent(mtApplication);
		this.getCanvas().addChild(puzzleGroup);
		
		//Puzzle tile factory
		this.pf = new PuzzleFactory(getMTApplication());
		
		IFont font = FontManager.getInstance().createFont(mtApplication, "SansSerif", 16, MTColor.WHITE, false);
		
		//New Puzzle button
		MTRoundRectangle r = getRoundRectWithText(0, 0, 120, 35, "New Puzzle", font);
		r.registerInputProcessor(new TapProcessor(getMTApplication()));
		r.addGestureListener(TapProcessor.class, new DefaultButtonClickAction(r));
		r.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
					if (list.isVisible()){
						list.setVisible(false);
					}else{
						list.setVisible(true);
					}
				}
				return false;
			}
		});
		r.setPositionGlobal(new Vector3D(r.getWidthXY(TransformSpace.GLOBAL)/2f + 3 , r.getHeightXY(TransformSpace.GLOBAL)/2f + 3));
		this.getCanvas().addChild(r);
		
		//Image list
		float cellWidth = 180;
		float cellHeight = 40;
		MTColor cellFillColor = new MTColor(MTColor.BLACK);
		MTColor cellPressedFillColor = new MTColor(new MTColor(105,105,105));
		list = new MTList(getMTApplication(), r.getWidthXY(TransformSpace.GLOBAL) + 5, 0, cellWidth+2, imagesNames.length* cellHeight + imagesNames.length*3);
		list.setNoFill(true);
		list.setNoStroke(true);
		list.unregisterAllInputProcessors();
		list.setAnchor(PositionAnchor.UPPER_LEFT);
//		list.setPositionGlobal(Vector3D.ZERO_VECTOR);
		list.setVisible(false);
        for (String imageName : imagesNames) {
            list.addListElement(this.createListCell(imageName, font, cellWidth, cellHeight, cellFillColor, cellPressedFillColor));
        }
		this.getCanvas().addChild(list);
		
		//Loading window
		this.loadingScreen = getRoundRectWithText(0, 0, 130, 45, "  Loading...", font);
		this.loadingScreen.setFillColor(new MTColor(0,0,0,200));
		this.loadingScreen.setStrokeColor(new MTColor(0,0,0,200));
		this.loadingScreen.setPickable(false);
		this.loadingScreen.setPositionGlobal(MT4jSettings.getInstance().getWindowCenter());
		this.loadingScreen.setVisible(false);
		this.getCanvas().addChild(loadingScreen);
	}
	
	
	private MTRoundRectangle getRoundRectWithText(float x, float y, float width, float height, String text, IFont font){
		MTRoundRectangle r = new MTRoundRectangle(getMTApplication(), x, y, 0, width, height, 12, 12);
		r.unregisterAllInputProcessors();
		r.setFillColor(MTColor.BLACK);
		r.setStrokeColor(MTColor.BLACK);
		MTTextArea rText = new MTTextArea(getMTApplication(), font);
		rText.unregisterAllInputProcessors();
		rText.setPickable(false);
		rText.setNoFill(true);
		rText.setNoStroke(true);
		rText.setText(text);
		r.addChild(rText);
		rText.setPositionRelativeToParent(r.getCenterPointLocal());
		return r;
	}
	
	
	private MTListCell createListCell(final String imageName, IFont font, float cellWidth, float cellHeight, final MTColor cellFillColor, final MTColor cellPressedFillColor){
		final MTListCell cell = new MTListCell(getMTApplication(), cellWidth, cellHeight);
		cell.setFillColor(cellFillColor);
		MTTextArea listLabel = new MTTextArea(getMTApplication(), font);
		listLabel.setNoFill(true);
		listLabel.setNoStroke(true);
		listLabel.setText(imageName);
		cell.addChild(listLabel);
		listLabel.setPositionRelativeToParent(cell.getCenterPointLocal());
		cell.unregisterAllInputProcessors();
		cell.registerInputProcessor(new TapProcessor(getMTApplication(), 15));
		cell.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				switch (te.getTapID()) { 
				case TapEvent.TAP_DOWN:
					cell.setFillColor(cellPressedFillColor);
					break;
				case TapEvent.TAP_UP:
					cell.setFillColor(cellFillColor);
					break;
				case TapEvent.TAPPED:
					//System.out.println("Button clicked: " + label);
					cell.setFillColor(cellFillColor);
					list.setVisible(false);
					loadingScreen.setVisible(true);
					registerPreDrawAction(new IPreDrawAction() {
						public void processAction() {
							getMTApplication().invokeLater(new Runnable() {
								public void run() {
									loadNewPuzzle(imageName, horizontalTiles, verticalTiles);
									loadingScreen.setVisible(false);
								}
							});
						}
						public boolean isLoop() {return false;}
					});
					break;
				}
				return false;
			}
		});
		return cell;
	}
	
	private void loadNewPuzzle(String imageName, int horizontalTiles, int verticalTiles){
		for (MTComponent c : puzzleGroup.getChildren()){
			c.destroy();
		}
		PImage p = getMTApplication().loadImage(imagesPath + imageName);
		AbstractShape[] tiles = pf.createTiles(p, this.horizontalTiles, this.verticalTiles);
        for (final AbstractShape sh : tiles) {
            //Delay to smooth the animation because of loading hickups
            final float x = ToolsMath.getRandom(0, MT4jSettings.getInstance().getWindowWidth());
            final float y = ToolsMath.getRandom(0, MT4jSettings.getInstance().getWindowHeight());
            registerPreDrawAction(new IPreDrawAction() {
                public void processAction() {
                    getMTApplication().invokeLater(new Runnable() {
                        public void run() {
                            registerPreDrawAction(new IPreDrawAction() {
                                public void processAction() {
                                    getMTApplication().invokeLater(new Runnable() {
                                        public void run() {
                                            puzzleGroup.addChild(sh);
//											sh.tweenTranslateTo(x, y, 0, 400, 0f, 1.0f);
                                            sh.tweenTranslateTo(x, y, 0, 400, AniAnimation.CIRC_OUT, 0);
                                        }
                                    });
                                }

                                public boolean isLoop() {
                                    return false;
                                }
                            });
                        }
                    });
                }

                public boolean isLoop() {
                    return false;
                }
            });
            sh.rotateZ(sh.getCenterPointRelativeToParent(), ToolsMath.getRandom(0, 359));
        }
	}
	

	public void onEnter() {	}
	
	public void onLeave() {	}
	
	

}
