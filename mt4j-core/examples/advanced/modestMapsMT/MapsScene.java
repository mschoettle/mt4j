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
package advanced.modestMapsMT;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.components.visibleComponents.widgets.MTList;
import org.mt4j.components.visibleComponents.widgets.MTListCell;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.components.visibleComponents.widgets.progressBar.AbstractProgressThread;
import org.mt4j.components.visibleComponents.widgets.progressBar.MTProgressBar;
import org.mt4j.input.IMTEventListener;
import org.mt4j.input.MTEvent;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.AnimationManager;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.ani.AniAnimation;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL11;

import processing.core.PImage;
import advanced.flickrMT.FlickrLoader;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.GeoData;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.SearchParameters;
import com.modestmaps.TestInteractiveMap;
import com.modestmaps.core.Point2f;
import com.modestmaps.geo.Location;
import com.modestmaps.providers.AbstractMapProvider;
import com.modestmaps.providers.BlueMarble;
import com.modestmaps.providers.CloudMade;
import com.modestmaps.providers.DailyPlanet;
import com.modestmaps.providers.Microsoft;
import com.modestmaps.providers.OpenStreetMaps;


/**
 * The Class MapsScene.
 * 
 * @author Christopher Ruff
 */
public class MapsScene extends AbstractScene implements MouseWheelListener, MouseListener {
	
	/** The map. */
	private TestInteractiveMap map;
	
	/** The p. */
	private AbstractMTApplication p;
	
	/** The tag container. */
	private MTComponent tagContainer;
	
	/** The foto container. */
	private MTComponent fotoContainer;
	
	/** The button container. */
	private MTComponent buttonContainer;
	
	/** The default center cam. */
	private MTCamera defaultCenterCam;
	
	/** The progress bar. */
	private MTProgressBar progressBar;
	
	/** The tag to photo. */
	private Map<MTEllipse, Photo> tagToPhoto;
	
	private boolean animateToBestZoomLevel = true;
	
	//TODO button/gesture for optimal zoom level - map.setZoom(map.bestZoomForScale((float) map.sc)); ?

	/**
	 * Instantiates a new maps scene.
	 * 
	 * @param mtApplication the mt application
	 * @param name the name
	 */
	public MapsScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.p = mtApplication;
		
		if (!MT4jSettings.getInstance().isOpenGlMode()){
			System.err.println("Scene only usable when using the OpenGL renderer! - See settings.txt");
        	return;
        }
		
		//Show our touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		defaultCenterCam = new MTCamera(p);
		
		//Container for the foto tags on the map
		tagContainer = new MTComponent(p);
		
		//Container for the Fotos
		fotoContainer = new MTComponent(p);
		fotoContainer.attachCamera(defaultCenterCam);
		
		//Container for the buttons
		buttonContainer = new MTComponent(p);
		buttonContainer.attachCamera(defaultCenterCam);
		
		//Create map
		AbstractMapProvider mapProvider = new Microsoft.HybridProvider();
		map = new TestInteractiveMap(mtApplication, mapProvider);
		map.setName("map");
		map.MAX_IMAGES_TO_KEEP = 256;
		map.sc = 4;  //Initial map scale
		//Map gestures
		map.registerInputProcessor(new DragProcessor(mtApplication));
		map.addGestureListener(DragProcessor.class, new MapDrag());
		
		map.registerInputProcessor(new ScaleProcessor(mtApplication));
		map.addGestureListener(ScaleProcessor.class, new MapScale());
		
		map.setGestureAllowance(RotateProcessor.class, false);
		this.getCanvas().addChild(map);
		
		
		//Set up the progressbar
//		progressBar = new MTProgressBar(p, p.loadFont(MT4jSettings.getInstance().getDefaultFontPath() + "Ziggurat.vlw"));
//		progressBar = new MTProgressBar(p, p.loadFont("arial"));
		progressBar = new MTProgressBar(p, p.createFont("arial", 18));
		progressBar.attachCamera(defaultCenterCam);
		progressBar.setDepthBufferDisabled(true);
		progressBar.setVisible(false);
		progressBar.setPickable(false);
		this.getCanvas().addChild(progressBar);
		
		this.getCanvas().addChild(tagContainer);
		this.getCanvas().addChild(fotoContainer);
		this.getCanvas().addChild(buttonContainer);
		
		//Button for foto search
		final MTImageButton fotoButton;
//		PImage fotoButtonImg = p.loadImage(System.getProperty("user.dir")+File.separator + "examples"+  File.separator +"advanced"+ File.separator+ File.separator +"modestMapsMT"+ File.separator +  File.separator + "data"+ File.separator + 
//				"foto6.png");
		//Load image from classpath
		PImage fotoButtonImg = p.loadImage( "advanced" + AbstractMTApplication.separator + "modestMapsMT" + AbstractMTApplication.separator + "data" + AbstractMTApplication.separator + 
		"foto6.png");
		fotoButtonImg.resize((int)(fotoButtonImg.width/1.5f), (int)(fotoButtonImg.height/1.5f));
		fotoButton = new MTImageButton(p, fotoButtonImg);
		fotoButton.setName("fotoButton");
		fotoButton.setNoStroke(true);
		fotoButton.setDepthBufferDisabled(true); //Draw on top of everything
//		fotoButton.translate(new Vector3D(MT4jSettings.getInstance().getScreenWidth() - fotoButton.getWidthXY(TransformSpace.RELATIVE_TO_PARENT) -5, MT4jSettings.getInstance().getScreenHeight()- fotoButton.getHeightXY(TransformSpace.RELATIVE_TO_PARENT) -5, 0));
		fotoButton.translate(new Vector3D(0, MT4jSettings.getInstance().getWindowHeight()- fotoButton.getHeightXY(TransformSpace.RELATIVE_TO_PARENT) , 0));
		fotoButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				if (te.isTapped()){
					Point[] p = getScreenPoints();
                    for (Point point : p) {
                        Location loc = map.pointLocation(point.x, point.y);
                        getPictures(loc, getAccuracyForZoom(map), true);
                    }
					getPictures(map.getCenter(), getAccuracyForZoom(map), false);
				}
				return true;
			}
		});
		fotoButton.setTextureEnabled(true);
		fotoButton.setUseDirectGL(true);
		this.buttonContainer.addChild(fotoButton);
		
		tagToPhoto = new HashMap<MTEllipse, Photo>();
		
		/// Create map provider menu \\\
		IFont font = FontManager.getInstance().createFont(p, "SansSerif.Bold", 15, MTColor.WHITE);
		MTRoundRectangle mapMenu = new MTRoundRectangle(p,0,0, 0,240, 335,20, 20);
//		mapMenu.setFillColor(new MTColor(110,110,110,180));
//		mapMenu.setStrokeColor(new MTColor(110,110,110,180));
		mapMenu.setFillColor(new MTColor(45,45,45,180));
		mapMenu.setStrokeColor(new MTColor(45,45,45,180));
		mapMenu.setPositionGlobal(new Vector3D(p.width/2f, p.height/2f));
		mapMenu.translateGlobal(new Vector3D(-p.width/2f - 80,0));
		getCanvas().addChild(mapMenu);
		
		float cellWidth = 155;
		float cellHeight = 40;
		MTColor cellFillColor = new MTColor(new MTColor(0,0,0,210));
		MTColor cellPressedFillColor = new MTColor(new MTColor(20,20,20,220));
		
		MTList list = new MTList(p,0, 0, 152, 7* cellHeight + 7*3);
		list.setChildClip(null); //FIXME TEST -> do no clipping for performance
		list.setNoFill(true);
		list.setNoStroke(true);
		list.unregisterAllInputProcessors();
		list.setAnchor(PositionAnchor.CENTER);
		list.setPositionRelativeToParent(mapMenu.getCenterPointLocal());
		mapMenu.addChild(list);
		
		list.addListElement(this.createListCell("Microsoft Aerial", font, new Microsoft.AerialProvider(), cellWidth, cellHeight, cellFillColor, cellPressedFillColor));
		list.addListElement(this.createListCell("Microsoft Road", font, new Microsoft.RoadProvider(), cellWidth, cellHeight, cellFillColor, cellPressedFillColor));
		list.addListElement(this.createListCell("Microsoft Hybrid", font, new Microsoft.HybridProvider(), cellWidth, cellHeight, cellFillColor, cellPressedFillColor));
		list.addListElement(this.createListCell("Open Street Maps", font, new OpenStreetMaps(), cellWidth, cellHeight, cellFillColor, cellPressedFillColor));
		list.addListElement(this.createListCell("Cloudmade Tourist", font, new CloudMade.Tourist(), cellWidth, cellHeight, cellFillColor, cellPressedFillColor));
		list.addListElement(this.createListCell("Blue Marble", font, new BlueMarble(), cellWidth, cellHeight, cellFillColor, cellPressedFillColor));
		list.addListElement(this.createListCell("Daily Planet", font, new DailyPlanet(), cellWidth, cellHeight, cellFillColor, cellPressedFillColor));
		
//		MultiPurposeInterpolator in = new MultiPurposeInterpolator(0,170, 700, 0.1f, 0.7f, 1);
//		final IAnimation slideOut = new Animation("slide out animation", in, mapMenu);
		final IAnimation slideOut = new AniAnimation(0, 170, 700, AniAnimation.BACK_OUT, mapMenu);
		slideOut.addAnimationListener(new IAnimationListener() {
			public void processAnimationEvent(AnimationEvent ae) {
				float delta = ae.getDelta();
				((IMTComponent3D)ae.getTarget()).translateGlobal(new Vector3D(delta,0,0));
				switch (ae.getId()) {
				case AnimationEvent.ANIMATION_ENDED:
					doSlideIn = true;
					animationRunning = false;
					break;
				}
			}
		});
		
//		final IAnimation slideIn = new Animation("slide out animation", in, mapMenu);
		final IAnimation slideIn = new AniAnimation(0, 170, 700, AniAnimation.BACK_OUT, mapMenu);
		slideIn.addAnimationListener(new IAnimationListener() {
			public void processAnimationEvent(AnimationEvent ae) {
				float delta = -ae.getDelta();
				((IMTComponent3D)ae.getTarget()).translateGlobal(new Vector3D(delta,0,0));
				switch (ae.getId()) {
				case AnimationEvent.ANIMATION_ENDED:
					doSlideIn = false;
					animationRunning = false;
					break;
				}
			}
		});
		
		mapMenu.unregisterAllInputProcessors();
		mapMenu.registerInputProcessor(new TapProcessor(mtApplication, 50));
		mapMenu.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				if (((TapEvent)ge).getTapID() == TapEvent.TAPPED){
					if (!animationRunning){
						animationRunning = true;
						if (doSlideIn){
							slideIn.start();
						}else{
							slideOut.start();
						}
					}
				}
				return false;
			}
		});

		updateTagContainerScale(); //needed to initialize..if not i observed strange behavior with the photo tags 
	}
	
	
	private boolean animationRunning = false;
	private boolean doSlideIn = false;
	
	private MTListCell createListCell(final String label, IFont font, final AbstractMapProvider mapProvider, float cellWidth, float cellHeight, final MTColor cellFillColor, final MTColor cellPressedFillColor){
		final MTListCell cell = new MTListCell(p, cellWidth, cellHeight);
		
		cell.setChildClip(null); //FIXME TEST, no clipping for performance!
		
		cell.setFillColor(cellFillColor);
		MTTextArea listLabel = new MTTextArea(p, font);
		listLabel.setNoFill(true);
		listLabel.setNoStroke(true);
		listLabel.setText(label);
		cell.addChild(listLabel);
		listLabel.setPositionRelativeToParent(cell.getCenterPointLocal());
		cell.unregisterAllInputProcessors();
		cell.registerInputProcessor(new TapProcessor(p, 15));
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
//					System.out.println("Button clicked: " + label);
					cell.setFillColor(cellFillColor);
					map.setMapProvider(mapProvider);
					break;
				}
				return false;
			}
		});
		return cell;
	}
	

	/**
	 * The Class MapDrag.
	 * @author C.Ruff
	 */
	private class MapDrag implements IGestureEventListener{
		public boolean processGestureEvent(MTGestureEvent g) {
			if (g instanceof DragEvent){
				DragEvent dragEvent = (DragEvent)g;
				Vector3D tVect = dragEvent.getTranslationVect();
				map.move(tVect.x, tVect.y);
				/*
				transVect.setXYZ(tVect.x, tVect.y, 0);
				fotoTagContainer.translate(transVect);
				*/
				updateTagContainerScale();
			}
			return false;
		}
	}

	/**
	 * The Class MapScale.
	 * 
	 * @author C.Ruff
	 */
	private class MapScale implements IGestureEventListener{
		private Vector3D lastMiddle;

//		private Vector3D scaleP =  new Vector3D(p.width/2, p.height/2, 0);
//		scaleP.setXYZ(p.width/2, p.height/2, 0);
		public boolean processGestureEvent(MTGestureEvent g) {
			if (g instanceof ScaleEvent){
				ScaleEvent se = (ScaleEvent)g;
				float scaleX = se.getScaleFactorX();
				//System.out.println("X:" + x + " Y:" +y);
				
				
				//Add a little panning to scale, so if we can pan while we scale
				InputCursor c1 = se.getFirstCursor();
				InputCursor c2 = se.getSecondCursor();
				if (se.getId() == MTGestureEvent.GESTURE_STARTED){
					Vector3D i1 = c1.getPosition();
					Vector3D i2 = c2.getPosition();
					lastMiddle = i1.getAdded(i2.getSubtracted(i1).scaleLocal(0.5f));
				}else if (se.getId() == MTGestureEvent.GESTURE_UPDATED){ 
					Vector3D i1 =  c1.getPosition();
					Vector3D i2 =  c2.getPosition();
					Vector3D middle = i1.getAdded(i2.getSubtracted(i1).scaleLocal(0.5f));
					Vector3D middleDiff = middle.getSubtracted(lastMiddle);
					map.move(middleDiff.x, middleDiff.y);
					lastMiddle = middle;
				}

				//Scale the map and the tags
				scaleMap(scaleX);
				
				if (animateToBestZoomLevel){
					//Stop previous animations
					IAnimation[] currentAnims = AnimationManager.getInstance().getAnimationsForTarget(map);
					for (IAnimation iAnimation : currentAnims) {
						iAnimation.stop();
					}
					
					//FIXME messes up tagContainer scale
					//Animate to the best zoom level for better clarity
					if (se.getId() == MTGestureEvent.GESTURE_ENDED){
						double current = map.sc;
						float currentF = (float)current;
						final int best = map.bestZoomForScale((float) map.sc);
						map.setZoom(best);
						float bestZoom = (float) map.sc;
						map.sc = current;
						//					System.out.println("current: " + currentF + " bestZoom: " + bestZoom);

						AniAnimation anim = new AniAnimation(currentF, bestZoom, 1000, map);
						anim.addAnimationListener(new IAnimationListener() {
							public void processAnimationEvent(AnimationEvent ae) {
//								map.sc += ae.getDelta();
								double nowScale = map.sc;
								double destScale = nowScale + ae.getDelta();
								double diff = destScale/nowScale;
								scaleMap((float) diff);
								
								if (ae.getId() == AnimationEvent.ANIMATION_ENDED){
									map.setZoom(best);
									map.setZoom(map.bestZoomForScale((float) map.sc));
									//								System.out.println("Ended: " + map.sc);
								}
							}
						});
						anim.start();
					}
				}
			}
			return false;
		}
	}
	
	
	/**
	 * Scale the map and also the tags.
	 * 
	 * @param scaleFactor the scale factor
	 */
	private void scaleMap(float scaleFactor){
		if (scaleFactor != 1){
			map.sc *= scaleFactor;
			updateTagContainerScale();
			updateTagShapeScale(scaleFactor);
		}
	}
	
	//TODO CLEANUP
	/** The model. */
//	private DoubleBuffer model = DoubleBuffer.allocate(16);
	private FloatBuffer model = FloatBuffer.allocate(16);
	/** The mgl. */
	private Matrix mgl = new Matrix();
	
	/**
	 * Kind of a hack to fit the scale of the foto tags to the map scale.
	 * Has to be called each time the map scale changes.
	 */
	private void updateTagContainerScale(){
		model.clear();
//		PGraphicsOpenGL pgl = ((PGraphicsOpenGL)p.g);
//		GL gl = pgl.beginGL();
//		GL gl = pgl.gl;
		GL11 gl = PlatformUtil.getGL11();
		
		gl.glPushMatrix();
		gl.glScalef(1, -1, 1);
		gl.glTranslatef(p.width/2, p.height/2, 0);
		gl.glScalef((float)map.sc, (float)map.sc, 1);
		gl.glTranslatef((float)map.tx, (float)map.ty, 0);
//		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model);
		gl.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, model);
		gl.glPopMatrix();
//		pgl.endGL();
		
		try {
			mgl.set(new float[]{
					(float)model.get(0), (float)model.get(4), (float)model.get(8),  (float)model.get(12),
					(float)model.get(1), (float)model.get(5), (float)model.get(9),  (float)model.get(13),
					(float)model.get(2), (float)model.get(6), (float)model.get(10), (float)model.get(14),
					(float)model.get(3), (float)model.get(7), (float)model.get(11), (float)model.get(15)});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		tagContainer.setLocalMatrix(mgl);
//		System.out.println(mgl);
		
		/*
		Matrix m = Matrix.getTranslationMatrix((float)p.width/2, (float)p.height/2, 0);
		Matrix s = Matrix.getScalingMatrix((float)map.sc, (float)map.sc, 1);
		Matrix t = Matrix.getTranslationMatrix((float)map.tx, (float)map.ty, 0);
		m.multLocal(s);
		m.multLocal(t);
//		m.translate((float)p.width/2, (float)p.height/2, 0);
//		m.scale((float)map.sc);
//		m.translate((float)map.tx, (float)map.ty);
		System.out.println("2:" + m);
//		fotoTagContainer.setLocalBasisMatrix(m);
		*/
	}
	
	
	/**
	 * Inversely scales the tagcontainers child shapes so they
	 * appear to be the same size, independent of the map's scale.
	 * 
	 * @param scale the scale
	 */
	private void updateTagShapeScale(float scale){
		MTComponent[] tags = tagContainer.getChildren();
		float scX = 1f/scale;
        for (MTComponent baseComponent : tags) {
            if (baseComponent instanceof AbstractShape) {
                AbstractShape shape = (AbstractShape) baseComponent;
//				System.out.println("Scaling: " + scX + " " + scY);
//				shape.scale(scX, scY, 1, shape.getCenterPointGlobal(), TransformSpace.GLOBAL);
                shape.scale(scX, scX, 1, shape.getCenterPointRelativeToParent(), TransformSpace.RELATIVE_TO_PARENT);
            }
        }
	}

	public void onEnter() {
		getMTApplication().registerKeyEvent(this);
		getMTApplication().addMouseWheelListener(this);
		getMTApplication().addMouseListener(this);
	}
	
	public void onLeave() {	
		getMTApplication().unregisterKeyEvent(this);
		getMTApplication().removeMouseWheelListener(this);
		getMTApplication().removeMouseListener(this);
	}
	
	/**
	 * Returns some more or less random points
	 * on the screen to search for nearby flickr fotos.
	 * 
	 * @return the screen points
	 */
	private Point[] getScreenPoints(){
		Point[] p = new Point[5];
		int sw = MT4jSettings.getInstance().getWindowWidth();
		int sh = MT4jSettings.getInstance().getWindowHeight();
		
		float wThird = sw/3f;
		float wThirdHalf = wThird/2f;
		float hHalf = sh/2f;
		
		p[0] = new Point( Math.round(wThirdHalf), Math.round(hHalf/2f) );
		p[1] = new Point( Math.round(wThirdHalf), sh - Math.round(hHalf/2f) );
		p[2] = new Point( Math.round(sw/2f), Math.round(sh/2f) );
		p[3] = new Point( sw - Math.round(wThirdHalf), Math.round(hHalf/2f) );
		p[4] = new Point( sw - Math.round(wThirdHalf), sh - Math.round(hHalf/2f) );
		return p;
	}
	
	
	/**
	 * Gets the pictures.
	 * 
	 * @param c the c
	 * @param theAccuracy the the accuracy
	 * @param usePlacesForGeoSearch the use places for geo search
	 * 
	 * @return the pictures
	 */
	private void getPictures(Location c, int theAccuracy, boolean usePlacesForGeoSearch) {
		//TODO zwei verschiednen accuracies suchen? zb, citty und street? von jedem die h�lfte zeigen
		//TODO wenn keine im screen neu suchen -> erst punkte zeigen, erst foto zeigen laden bei click drauf
		SearchParameters sp = new SearchParameters();
		int radius = 3;
		
//		/*
		sp.setLatitude(Float.toString(c.lat));
		sp.setLongitude(Float.toString(c.lon));
		sp.setRadius(radius);
		sp.setRadiusUnits("km");
		sp.setHasGeo(true);
		sp.setAccuracy(theAccuracy);
		
		sp.setSort(SearchParameters.INTERESTINGNESS_DESC);
//		sp.setSort(SearchParameters.INTERESTINGNESS_ASC);
//		sp.setSort(SearchParameters.RELEVANCE);
//		sp.setSort(SearchParameters.DATE_POSTED_DESC);
//		*/
		
		/*
		Location lowerLeft = map.pointLocation(0, p.height - 0);
		System.out.println("Lower Left: " + lowerLeft);
		
		Location upperLeft = map.pointLocation(0,0);
		System.out.println("upperleft: " + upperLeft);
		Location lowerRight = map.pointLocation(p.height, p.height); //taken height to form box
		System.out.println("LowerRight: " + lowerRight);
		float extent = (lowerRight.lat-upperLeft.lat );
//		extent = 0.05f;
		System.out.println("Box Extent: " + extent);
//		sp.setBBox(new Float(upperLeft.lat).toString(), new Float(upperLeft.lon).toString(), new Float(lowerRight.lat).toString(), new Float(lowerRight.lon).toString());
		sp.setBBox(new Float(upperLeft.lat).toString(), new Float(upperLeft.lon).toString(), new Float(upperLeft.lat +  extent).toString(), new Float(upperLeft.lon + extent).toString());
		
		if (extent <= 0){
			System.out.println("Extent < 0 - calcing new");
			extent = (upperLeft.lat - lowerRight.lat);
			sp.setBBox(new Float(lowerRight.lat).toString(), new Float(lowerRight.lon).toString(), new Float(lowerRight.lat +  extent).toString(), new Float(lowerRight.lon + extent).toString());
			System.out.println("New Box Extent: " + extent);
		}
		
		float[] box = new float[]{new Float(sp.getBBox()[0]),new Float(sp.getBBox()[1]),new Float(sp.getBBox()[2]),new Float(sp.getBBox()[3]) };
		for (int i = 0; i < box.length; i++) {
			float f = box[i];
			System.out.println(f);
		}
		
		System.out.println("Box width: " +  (box[2]-box[0]));
		System.out.println("Box height: " + (box[3]- box[1]));
		boolean isBox = (box[2]-box[0]) == (box[3]- box[1]);
		System.out.println("Is Box: " + isBox);
		
		//Lisboa bbox: 38,704, -9,215 |  (38,688, -9,171)
		sp.setAccuracy(13);
		*/
		
		//TODO radius so anpassen, dass ungef�hr der momentane 
		//map ausschnitt (scale beachten) gesucht wird
		System.out.println("Searching for fotos at map center location: " + c + " with radius: " + radius);
        String flickrApiKey = "";
        String flickrSecret = "";
        Properties properties = new Properties();
	    try {
	        properties.load(new FileInputStream(System.getProperty("user.dir")+File.separator+"examples"+ File.separator +"advanced"+ File.separator+File.separator+"flickrMT"+File.separator+"data" + File.separator + "FlickrApiKey.txt"));
	        flickrApiKey = properties.getProperty("FlickrApiKey", " ");
	        flickrSecret = properties.getProperty("FlickrSecret", " ");
	    } catch (Exception e) {
	    	System.err.println("Error while loading FlickrApiKey.txt file.");
	    }
	    
		final FlickrLoader flickrLoader = new FlickrLoader(p, flickrApiKey, flickrSecret, sp, 100);
        flickrLoader.setFotoLoadCount(3);
        flickrLoader.setUsePlacesForGeoSearch(usePlacesForGeoSearch);
        
        flickrLoader.addProgressFinishedListener(new IMTEventListener(){
			public void processMTEvent(MTEvent mtEvent) {
				System.out.println("Loading finished!");
				
				p.getCurrentScene().registerPreDrawAction(new IPreDrawAction(){
					public boolean isLoop() {
						return false;
					}

					public void processAction() {
						progressBar.setVisible(false);
						Photo[] photos = flickrLoader.getPhotos();
                        for (Photo foto : photos) {
                            String id = foto.getId();
                            //System.out.println("Foto ID:" + id);
                            boolean alreadyContained = false;
                            Collection<Photo> vlaues = tagToPhoto.values();
                            for (Photo photo : vlaues) {
                                if (photo.getId().equalsIgnoreCase(foto.getId())) {
                                    alreadyContained = true;
                                }
                            }

                            if (!alreadyContained/*!tagToPhoto.containsValue(foto)*/) {
                                String fotoName = foto.getTitle();
                                if (foto.hasGeoData()) {
                                    GeoData geo = foto.getGeoData();
                                    float lat = geo.getLatitude();
                                    float lon = geo.getLongitude();
                                    System.out.println("\"" + fotoName + "\"" + " Has GeoData! -> Lat:" + lat + " Lon:" + lon + " PlaceID: " + foto.getPlaceId());

                                    Point2f pointOnScreen = map.locationPoint(new Location(lat, lon));
//									System.out.println(" -> Point on Screen: " + pointOnScreen);

                                    Vector3D vecOnScreen = new Vector3D(0, 0, 0f);
//									Vector3D vecOnScreen 	= new Vector3D(pointOnScreen.x , pointOnScreen.y , 0.01f);
//									Vector3D vecOnScreen 	= new Vector3D(pointOnScreen.x -p.width/2 +128, pointOnScreen.y -p.height/2 +128, 0.01f);

                                    //System.out.println("-> Creating tag at: " + vecOnScreen);
                                    if (pointOnScreen.x >= 0 && pointOnScreen.x <= p.width
                                            && pointOnScreen.y >= 0 && pointOnScreen.y <= p.height
                                            ) {
                                        final MTEllipse tagCircle = new MTEllipse(p, vecOnScreen, 15, 15, 30);
                                        tagCircle.setPickable(true);
                                        tagCircle.setFillColor(new MTColor(90, 205, 230, 200));
                                        tagCircle.setDrawSmooth(true);
                                        tagCircle.setStrokeWeight(2);
                                        tagCircle.setStrokeColor(new MTColor(40, 130, 220, 255));
                                        tagCircle.translate(new Vector3D(pointOnScreen.x, pointOnScreen.y, 0.0f));
                                        tagCircle.transform(tagContainer.getGlobalInverseMatrix());
                                        tagCircle.setName(id);

                                        tagToPhoto.put(tagCircle, foto);

                                        tagContainer.addChild(tagCircle);

                                        tagCircle.unregisterAllInputProcessors();
                                        tagCircle.registerInputProcessor(new TapProcessor(p));
                                        tagCircle.addGestureListener(TapProcessor.class, new IGestureEventListener() {
                                            //@Override
                                            public boolean processGestureEvent(MTGestureEvent g) {
                                                if (g instanceof TapEvent) {
                                                    TapEvent ce = (TapEvent) g;
                                                    switch (ce.getTapID()) {
                                                        case TapEvent.TAP_DOWN:
                                                            IMTComponent3D e = ce.getTarget();
                                                            Photo foto = tagToPhoto.get(e);
                                                            if (foto != null) {
                                                                SinglePhotoLoader fotoLoader = new SinglePhotoLoader(foto, 50);
                                                                fotoLoader.start();

                                                                //Disable and remove the fototag
                                                                tagCircle.setGestureAllowance(TapProcessor.class, false);

                                                                p.getCurrentScene().registerPreDrawAction(new IPreDrawAction() {
                                                                    public boolean isLoop() {
                                                                        return false;
                                                                    }

                                                                    public void processAction() {
//																	fotoTagContainer.removeChild(tagCircle);
                                                                        tagToPhoto.remove(tagCircle);
                                                                        tagCircle.destroy();
                                                                    }

                                                                });
                                                            }
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                                return true;
                                            }
                                        });
                                    }//if point is on screen
                                    else {
                                        System.out.println("Foto not on screen: position:" + pointOnScreen + " Title: " + foto.getTitle() + " id:" + id);
                                    }
                                } else {
                                    System.out.println("Foto already loaded: " + foto.getTitle() + " id:" + id);
                                }

                                /*
                                        ImageCard[] images = flickrLoader.getMtFotos();
                                        ImageCard image = images[i];
                                        if (image != null){

                                            if (pointOnScreen.x >= 0 && pointOnScreen.x <= p.width
                                             && pointOnScreen.y >= 0 && pointOnScreen.y <= p.height){

                                                image.setUseDirectGL(true);
                                                image.setDisplayCloseButton(true);

        //										image.translate(new Vector3D((float)(-image.getWidthLocal()/2f) , (float)(-image.getHeightLocal()/2f) , 0.0f));

                                                image.translate(new Vector3D(pointOnScreen.x, pointOnScreen.y , 0.1f));

                                                image.transform(
                                                        fotoTagContainer.getAbsoluteWorldToLocalMatrix()
                                                );

        //										image.scale((float)(1f/map.sc), (float)(1f/map.sc), 1, image.getCenterPointGlobal(), TransformSpace.RELATIVE_TO_WORLD);

                                                fotoContainer.addChild(image);

        //										Vector3D centerPoint = image.getCenterPointGlobal(); //TODO rename ..Local to ParentRelative
        //										float width = 15;
        //										image.scaleGlobal(1/image.getWidthGlobal(), 1/image.getWidthGlobal(), 1, centerPoint);
        //										image.scaleGlobal(width, width, 1, centerPoint);

        //										Vector3D centerPoint = image.getCenterPointLocal(); //TODO rename ..Local to ParentRelative
        //										float width = 15;
        //										image.scale(1/image.getWidthLocal(), 1/image.getWidthLocal(), 1, centerPoint, TransformSpace.RELATIVE_TO_PARENT);
        //										image.scale(width, width, 1, centerPoint, TransformSpace.RELATIVE_TO_PARENT);

        //										image.translate(new Vector3D(pointOnScreen.x , pointOnScreen.y , 0.01f));
        //										image.setPositionGlobal(new Vector3D(pointOnScreen.x , pointOnScreen.y , 0.01f));
                                            }else{
                                                System.out.println("Image '" + image.getName() + "' out of screen -> remove.");
                                                image.destroy();
                                            }
                                        }
                                        */
                            }//if has geo
                        }
					}//prcessPreDrawAction()
				});//registerPreAction()
			}//ProcessMTEvent()
        });//addThreadFinishedListener()
        progressBar.setProgressInfoProvider(flickrLoader);
        progressBar.setVisible(true);
        
        //Run the thread
        flickrLoader.start();
	}
	
	
	
	/**
	 * Thread for loading a single flickr foto.
	 */
	private class SinglePhotoLoader extends AbstractProgressThread{
		
		/** The foto. */
		private Photo foto;
		
		/** The image. */
		private MTImage image;

		/**
		 * Instantiates a new single photo loader.
		 * 
		 * @param foto the foto
		 * @param sleepTime the sleep time
		 */
		public SinglePhotoLoader(Photo foto, long sleepTime) {
			super(sleepTime);
			this.foto = foto;
			this.setTarget(1); //1 action to do in the thread
		}

		//@Override
		/* (non-Javadoc)
		 * @see org.mt4j.components.visibleComponents.widgets.progressBar.AbstractProgressThread#run()
		 */
		public void run() {
			try {
				Thread.sleep(this.getSleepTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			String fotoUrl = foto.getSmallUrl();
			String fotoUrl = foto.getMediumUrl(); //Get the bigger photo
			image = new MTImage(p, p.loadImage(fotoUrl));

			this.addProgressFinishedListener(new IMTEventListener(){
				public void processMTEvent(MTEvent mtEvent) {
					p.getCurrentScene().registerPreDrawAction(new IPreDrawAction(){
						public boolean isLoop() {
							return false;
						}

						public void processAction() {
							//User direct gl..
							image.setDisplayCloseButton(true);
							image.scale(0.5f, 0.5f, 1, new Vector3D(0,0,0), TransformSpace.LOCAL);
							image.translate(new Vector3D(MT4jSettings.getInstance().getWindowWidth(), 0, 0));
							image.tweenTranslate(new Vector3D(-MT4jSettings.getInstance().getWindowWidth(), 0, 0), 600, 0.1f, 0.6f);
							fotoContainer.addChild(image);
						}

					});
				}
			});
			this.setCurrentAction("Loading: " + foto.getTitle());
			this.setCurrent(1); //Did 1/1 actions of the thread -> finished
		}
		
	}

	/**
	 * Tries to get the best suitable flickr zoom factor for map zoom factor.
	 * 
	 * @param map2 the map2
	 * 
	 * @return the zoom level
	 */
	private int getAccuracyForZoom(TestInteractiveMap map2) {
		//1-4 zoom = world
		//5-7  zoom = country
		//8-11 zoom = region
		//12-15 zoom = city
		//16-20 zoom = street
		int zoom = map.getZoom();
		if (zoom < 1){
			System.out.println("zoom < 1 -> Using accuracy: 'ACCURACY_WORLD'");
			return Flickr.ACCURACY_WORLD;
		}else if (zoom >= 1 && zoom <= 3 ){
			System.out.println("Using accuracy: 'ACCURACY_WORLD'");
			return Flickr.ACCURACY_WORLD;
		}else if (zoom >= 4 && zoom <= 7 ){
			System.out.println("Using accuracy: 'ACCURACY_COUNTRY'");
			return Flickr.ACCURACY_COUNTRY;
		}else if (zoom >= 8 && zoom <= 11 ){
			System.out.println("Using accuracy: 'ACCURACY_REGION'");
			return Flickr.ACCURACY_REGION;
		}else if (zoom >= 12 && zoom <= 14 ){
			System.out.println("Using accuracy: 'ACCURACY_CITY'");
			return Flickr.ACCURACY_CITY;
		}else if (zoom >= 15){
			System.out.println("Using accuracy: 'ACCURACY_STREET'");
			return Flickr.ACCURACY_STREET;
		}else{
			System.out.println("Couldnt determine right accuracy -> Using accuracy: 'ACCURACY_REGION'");
			return Flickr.ACCURACY_REGION;
		}
		
	}

	
	
	/**
	 * Gets the map.
	 * 
	 * @return the map
	 */
	public TestInteractiveMap getMap() {
		return map;
	}

	/**
	 * Sets the map.
	 * 
	 * @param map the new map
	 */
	public void setMap(TestInteractiveMap map) {
		this.map = map;
	}


	public void mouseWheelMoved(MouseWheelEvent e) {
	       int notches = e.getWheelRotation();
	       System.out.println(notches);
	       if (notches < 0) {
	    	   p.getCurrentScene().registerPreDrawAction(new IPreDrawAction(){
					public boolean isLoop() {
						return false;
					}
					public void processAction() {
						scaleMap(1.1f);
					}
	    	   });
	       } else {
	    	   p.getCurrentScene().registerPreDrawAction(new IPreDrawAction(){
					public boolean isLoop() {
						return false;
					}
					public void processAction() {
						scaleMap(0.9f);
					}
	    	   });
	       }
	}
	
	
	public void mouseClicked(MouseEvent arg0) {
		int b = arg0.getButton();
		switch (b) {
		case MouseEvent.BUTTON2:
//			/*
			System.out.println("Current zoom: " + map.sc);
			map.setZoom(map.bestZoomForScale((float) map.sc));
			 p.getCurrentScene().registerPreDrawAction(new IPreDrawAction(){
					public boolean isLoop() {
						return false;
					}
					public void processAction() {
						updateTagContainerScale();
					}
	    	   });
//			*/
			
			/*
			double current = map.sc;
			float currentF = (float)current;
			final int best = map.bestZoomForScale((float) map.sc);
			map.setZoom(best);
			float bestZoom = (float) map.sc;
			map.sc = current;
			System.out.println("current: " + currentF + " bestZoom: " + bestZoom);
			AniAnimation anim = new AniAnimation(currentF, bestZoom, 1000, map);
			anim.addAnimationListener(new IAnimationListener() {
				public void processAnimationEvent(AnimationEvent ae) {
					map.sc += ae.getDelta();
					if (ae.getId() == AnimationEvent.ANIMATION_ENDED){
						map.setZoom(best);
						map.setZoom(map.bestZoomForScale((float) map.sc));
						System.out.println("Ended: " + map.sc);
						p.getCurrentScene().registerPreDrawAction(new IPreDrawAction(){
							public boolean isLoop() {
								return false;
							}
							public void processAction() {
								updateTagContainerScale();
							}
						});
					}
				}
			});
			anim.start();
			*/
			
			break;
		case MouseEvent.BUTTON3:
			this.getPictures(map.pointLocation(p.mouseX, p.mouseY), this.getAccuracyForZoom(map), true);
			break;
		default:
			break;
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}
	public void mouseExited(MouseEvent arg0) {
	}
	public void mousePressed(MouseEvent arg0) {
	}
	public void mouseReleased(MouseEvent arg0) {
	}
	
	
	/**
	 * Key event.
	 * 
	 * @param e the e
	 */
	public void keyEvent(KeyEvent e){
		int evtID = e.getID();
		if (evtID != KeyEvent.KEY_PRESSED)
			return;

		switch (e.getKeyCode()){
		case KeyEvent.VK_G:
			tagContainer.scale(0.75f,0.75f,1, new Vector3D(0,0,0));
			tagContainer.scale(1f/0.75f,1f/0.75f,1, new Vector3D(0,0,0));
			break;
		case KeyEvent.VK_PLUS:
			map.zoomIn();
			this.updateTagContainerScale();
			break;
		case KeyEvent.VK_MINUS:
			map.zoomOut();
			this.updateTagContainerScale();
			break;
		case KeyEvent.VK_F12:
			p.saveFrame();
			break;
//		case KeyEvent.VK_A:
//				map.setMapProvider( new Microsoft.AerialProvider());
//			break;
//		case KeyEvent.VK_R:
//				map.setMapProvider( new Microsoft.RoadProvider());
//			break;
//		case KeyEvent.VK_H:
//				map.setMapProvider( new Microsoft.HybridProvider());
//			break;
//		case KeyEvent.VK_O:
//				map.setMapProvider( new OpenAerialMap()); //FIXME NOT WORKING ANYMORE
//			break;
//		case KeyEvent.VK_B:
//				map.setMapProvider( new BlueMarble());
//			break;
//		case KeyEvent.VK_S:
//			map.setMapProvider( new OpenStreetMaps());
//			break;
//		case KeyEvent.VK_C:
//			map.setMapProvider( new CloudMade.Tourist());
//			break;
//		case KeyEvent.VK_D:
//			map.setMapProvider( new DailyPlanet());
//			break;
		case KeyEvent.VK_BACK_SPACE:
			p.popScene();
			break;
		case KeyEvent.VK_F1:
			Location stuttgartLoc = new Location( 48.7771056f, 	9.1807688f);
			map.setCenterZoom(stuttgartLoc, 15);
			System.out.println("Center set to location: " + stuttgartLoc);
		    break;
		case KeyEvent.VK_F2:
			Location c = map.getCenter();
			this.getPictures(c, this.getAccuracyForZoom(map), true);
			break;
		case KeyEvent.VK_F3:
			Point[] p = this.getScreenPoints();
            for (Point point : p) {
                Location loc = map.pointLocation(point.x, point.y);
                this.getPictures(loc, this.getAccuracyForZoom(map), true);
            }
			this.getPictures(map.getCenter(), this.getAccuracyForZoom(map), false);
			break;
		case KeyEvent.VK_F9:
			Location lisbon = new Location(38.693f, -9.198f);
			map.setCenterZoom(lisbon, 15);
			System.out.println("Center set to location: " + lisbon);
			this.updateTagContainerScale();
			break;
		case KeyEvent.VK_T:
			System.out.println("Map zoom: " + map.getZoom() + " Map scale: " + map.sc);
			break;
		default:
			break;
		}
	}
	
}
