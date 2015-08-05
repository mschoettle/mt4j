package org.mt4jx.components.visibleComponents.widgets.menus;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.css.style.CSSFont;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.css.util.CSSFontManager;
import org.mt4j.components.css.util.CSSKeywords.CSSFontWeight;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PConstants;
import processing.core.PImage;

/**
 * The Class MTHexagonMenu. Menu that contains Hexagons as buttons containing
 * text or images
 */
public class MTHexagonMenu extends MTRectangle{

	/** The app. */
	private AbstractMTApplication app;

	/** The menu contents. */
	private List<MTPolygon> menuContents = new ArrayList<MTPolygon>();

	/** The layout. */
	private List<ArrayList<MTPolygon>> layout = new ArrayList<ArrayList<MTPolygon>>();

	/** The size. */
	private float size;

	/** The current. */
	private int current = 0;

	/** The max per line. */
	private int maxPerLine = 0;

	/** The bezel. */
	private float bezel = 4f;
	

	// List of the Child Polygons and their IGestureEventListeners
	/** The polygon listeners. */
	private List<PolygonListeners> polygonListeners = new ArrayList<PolygonListeners>();


	/** The menu items. */
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();


	/**
	 * Instantiates a new MTHexagonMenu.
	 * 
	 * @param app
	 *            the app
	 * @param position
	 *            the position of the Menu
	 * @param menuItems
	 *            the menu items
	 * @param size
	 *            the size (width of the Hexagon, the height is bigger)
	 */
	public MTHexagonMenu(AbstractMTApplication app, Vector3D position,
			List<MenuItem> menuItems, float size) {
		super(app, position.x, position.y, (float) (int) Math
						.sqrt(menuItems.size() + 1) * size, (float) (int) Math
				.sqrt(menuItems.size() + 1) * size);
		this.app = app;
		this.size = size;

		// Set the Rectangle to be invisible
		this.setCssForceDisable(true);
		this.setNoFill(true);
		this.setNoStroke(true);

		this.menuItems = menuItems;

		this.createMenuItems();
		
		// Register the TapProcessor
		this.setGestureAllowance(TapProcessor.class, true);
		this.registerInputProcessor(new TapProcessor(app));
		this.addGestureListener(TapProcessor.class, new TapListener(
				polygonListeners));
		this.setCssForceDisable(true);

	}


	/**
	 * Re-Creates the menu items.
	 */
	public void createMenuItems() {

		for (MTComponent c : this.getChildren()) {
			c.destroy();
		}
		this.removeAllChildren();
		menuContents.clear();
		polygonListeners.clear();
		
		for (MenuItem s : menuItems) {

			if (s != null && s.getType() == MenuItem.TEXT) {
				// Create Hexagon with Text Included
				MTPolygon container = getHexagon(size);
				this.addChild(container);

				// Add MTTextArea Children to take single lines of the Menu Text
				for (String t : s.getMenuText().split("\n")) {
					MTTextArea menuItem = new MTTextArea(app);
					menuItem.setText(t);
					menuItem.setCssForceDisable(true);
					menuItem.setFillColor(new MTColor(0, 0, 0, 0));
					menuItem.setStrokeColor(new MTColor(0, 0, 0, 0));
					menuItem.setPickable(false);
					container.addChild(menuItem);

				}
				container.setChildClip(new Clip(container));
				container.setPickable(false);
				polygonListeners.add(new PolygonListeners(container, s
						.getGestureListener()));
				menuContents.add(container);
			} else if (s != null && s.getType() == MenuItem.PICTURE) {

				if (s.getMenuImage() != null) {
					// Create Polygon for holding an Image
					PImage texture = null;
					MTPolygon container = getHexagon(size);
					container.setCssForceDisable(true);
					int height = (int) container
							.getHeightXY(TransformSpace.LOCAL);

					// If Image doesn't fit, make it fit!
					if (s.getMenuImage().width != size
							|| s.getMenuImage().height != height) {
						texture = cropImage(s.getMenuImage(), (int) size,
								height, true);
					} else {
						texture = s.getMenuImage();
					}

					this.addChild(container);

					// Normalize Texture Coordinates
					for (Vertex v : container.getVerticesLocal()) {
						v.setTexCoordU(v.getX() / (float) texture.width);
						if (v.getTexCoordU() > 1)
							v.setTexCoordU(1);
						v.setTexCoordV(v.getY() / (float) texture.height);
						if (v.getTexCoordV() > 1)
							v.setTexCoordV(1);
					}

					// Set the Texture
					container.getGeometryInfo()
							.setTextureCoordsNormalized(true);
					container.setTexture(texture);

					menuContents.add(container);
					container.setPickable(false);
					polygonListeners.add(new PolygonListeners(container, s
							.getGestureListener()));
				}

			}

		}

	


		// Apply Style to Children
		this.styleChildren(getNecessaryFontSize(menuItems, size));

	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public float getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 */
	public void setSize(float size) {
		this.size = size;
		this.createMenuItems();
	}

	/**
	 * Calculates the total height of a number of MTTextAreas.
	 *
	 * @param components the components
	 * @return the height
	 */
	private float calcTotalHeight(MTComponent[] components) {
		// Calculate the total height of several MTTextAreas
		float height = 0;
		for (MTComponent c : components) {
			if (c instanceof MTTextArea)
				height += ((MTTextArea) c).getHeightXY(TransformSpace.LOCAL);
		}

		return height;
	}

	/**
	 * Crop image.
	 * 
	 * @param image
	 *            the image
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param resize
	 *            resize the image?
	 * @return the cropped image
	 */
	private PImage cropImage(PImage image, int width, int height, boolean resize) {
		PImage workingCopy;
		try {
			workingCopy= (PImage) image.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning not supported!");
			workingCopy = image;
		}
		
		// Crops an Image to fit to the Hexagon Size
		PImage returnImage = app.createImage(width, height, PConstants.RGB);

		// Resize Image to match size, but retain aspect ration
		if (resize || workingCopy.width < size || workingCopy.height < size) {
			if (((float) workingCopy.width / (float) width) < ((float) workingCopy.height / (float) height)) {
				workingCopy.resize(
						width,
						(int) ((float) workingCopy.height / ((float) workingCopy.width / (float) width)));
			} else {
				workingCopy.resize(
						(int) ((float) workingCopy.width / ((float) workingCopy.height / (float) height)),
						height);
			}

		}
		
		
		
		// Crop Starting Points
		int x = (workingCopy.width / 2) - (width / 2);
		int y = (workingCopy.height / 2) - (height / 2);

		// Bugfixing: Don't Allow Out-of-Bounds coordinates
		if (x + width > workingCopy.width)
			x = workingCopy.width - width;
		if (x < 0)
			x = 0;
		if (x + width > workingCopy.width)
			width = workingCopy.width - x;
		if (y + height > workingCopy.height)
			x = workingCopy.height - height;
		if (y < 0)
			y = 0;
		if (y + height > workingCopy.height)
			height = workingCopy.height - y;

		// Crop Image
		returnImage.copy(workingCopy, x, y, width, height, 0, 0, width, height);

		return returnImage;
	}

	/**
	 * Creates a new Hexagon.
	 *
	 * @param size the width of the Hexagon
	 * @return the hexagon
	 */
	private MTPolygon getHexagon(float size) {
		// Create a new Polygon
		float hypotenuse = (float) ((size / 2f) / Math.cos(Math.toRadians(30)));
		//float ankathete = (float) (Math.cos(Math.toRadians(30)) * hypotenuse);
		float gegenkathete = (float) (Math.sin(Math.toRadians(30)) * hypotenuse);

		Vertex v1 = new Vertex(0, gegenkathete);
		Vertex v2 = new Vertex(size / 2f, 0);
		Vertex v3 = new Vertex(size, gegenkathete);
		Vertex v4 = new Vertex(size, gegenkathete + hypotenuse);
		Vertex v5 = new Vertex(size / 2f, 2 * gegenkathete + hypotenuse);
		Vertex v6 = new Vertex(0, gegenkathete + hypotenuse);
		Vertex v7 = new Vertex(0, gegenkathete);

		MTPolygon hexagon = new MTPolygon(app, new Vertex[] { v1, v2, v3, v4,
				v5, v6, v7 });

		return hexagon;
	}

	/**
	 * Gets the maximum font size for a certain width.
	 *
	 * @param strings the strings
	 * @param size the width
	 * @return the maximum font size
	 */
	private int getNecessaryFontSize(List<MenuItem> strings, float size) {
		// Calculate the Necessary font size for a SansSerif Bold font
		int maxNumberCharacters = 0;

		for (MenuItem s : strings) {

			if (s.getType() == MenuItem.TEXT) {
				if (s.getMenuText().contains("\n")) {
					for (String t : s.getMenuText().split("\n")) {

						if (t.length() > maxNumberCharacters)
							maxNumberCharacters = t.length();

					}
				} else {

					if (s.getMenuText().length() > maxNumberCharacters)
						maxNumberCharacters = s.getMenuText().length();

				}
			}
		}

		float spc = size / (float) maxNumberCharacters; // Space Per Character
		int returnValue = (int) (-0.5 + 1.725 * spc); // Determined using Linear
		// Regression
		return returnValue;

	}

	/**
	 * Returns the next n items.
	 *
	 * @param next the number of items to return
	 * @return the list of n next items
	 */
	private List<MTPolygon> next(int next) {
		// Return the next n MTPolygons from the list of children
		List<MTPolygon> returnValues = new ArrayList<MTPolygon>();
		for (int i = 0; i < next; i++) {
			returnValues.add(menuContents.get(current++));
		}

		return returnValues;
	}

	/**
	 * Distribute the contents of the menu to the rows.
	 */
	private void organizeHexagons() {
		// Distribute the items on the cell rows
		layout.clear();
		layout.add(new ArrayList<MTPolygon>());
		layout.add(new ArrayList<MTPolygon>());
		layout.add(new ArrayList<MTPolygon>());
		layout.add(new ArrayList<MTPolygon>());
		current = 0;
		switch (menuContents.size()) {
		case 0:
		case -1:
			break;

		case 1:
			layout.get(0).addAll(next(1));
			maxPerLine = 1;
			break;
		case 2:
			layout.get(0).addAll(next(2));
			maxPerLine = 2;
			break;
		case 3:
			layout.get(0).addAll(next(1));
			layout.get(1).addAll(next(2));
			maxPerLine = 2;
			break;
		case 4:
			layout.get(0).addAll(next(1));
			layout.get(1).addAll(next(2));
			layout.get(2).addAll(next(1));
			maxPerLine = 2;
			break;
		case 5:
			layout.get(0).addAll(next(2));
			layout.get(1).addAll(next(3));
			maxPerLine = 3;
			break;
		case 6:
			layout.get(0).addAll(next(1));
			layout.get(1).addAll(next(2));
			layout.get(1).addAll(next(3));
			maxPerLine = 3;
			break;
		case 7:
			layout.get(0).addAll(next(2));
			layout.get(1).addAll(next(3));
			layout.get(2).addAll(next(2));
			maxPerLine = 3;
			break;
		case 8:
			layout.get(0).addAll(next(3));
			layout.get(1).addAll(next(2));
			layout.get(2).addAll(next(3));
			maxPerLine = 3;
			break;
		case 9:
			layout.get(0).addAll(next(2));
			layout.get(1).addAll(next(3));
			layout.get(2).addAll(next(4));
			maxPerLine = 4;
			break;
		case 10:
			layout.get(0).addAll(next(3));
			layout.get(1).addAll(next(4));
			layout.get(2).addAll(next(3));
			maxPerLine = 4;
			break;
		case 11:
			layout.get(0).addAll(next(4));
			layout.get(1).addAll(next(3));
			layout.get(2).addAll(next(4));
			maxPerLine = 4;
			break;
		case 12:
			layout.get(0).addAll(next(3));
			layout.get(1).addAll(next(4));
			layout.get(2).addAll(next(5));
			maxPerLine = 5;
			break;
		case 13:
			layout.get(0).addAll(next(4));
			layout.get(1).addAll(next(5));
			layout.get(2).addAll(next(4));
			maxPerLine = 5;
			break;
		case 14:
			layout.get(0).addAll(next(3));
			layout.get(1).addAll(next(4));
			layout.get(2).addAll(next(3));
			layout.get(3).addAll(next(4));
			maxPerLine = 4;
			break;
		case 15:
			layout.get(0).addAll(next(4));
			layout.get(1).addAll(next(5));
			layout.get(2).addAll(next(6));
			maxPerLine = 6;
			break;
		case 16:
			layout.get(1).addAll(next(5));
			layout.get(2).addAll(next(6));
			layout.get(3).addAll(next(5));
			maxPerLine = 6;
			break;
		default:{
			System.err.println("Unsupported number of menu items in: " + this);
			}
		}

	}

	/**
	 * Style the cells of the menu.
	 *
	 * @param fontsize the font-size
	 */
	private void styleChildren(int fontsize) {
		organizeHexagons();
		CSSStyle vss = this.getCssHelper().getVirtualStyleSheet();
		CSSFont cf = this.getCssHelper().getVirtualStyleSheet().getCssfont().clone();
		// Style Font: Bold + fitting fontsize
		cf.setFontsize(fontsize);
		cf.setWeight(CSSFontWeight.BOLD);

		// Load Font
		CSSFontManager cfm = new CSSFontManager(app);
		IFont font = cfm.selectFont(cf);

		for (MTPolygon c : menuContents) {

			MTPolygon rect = c;

			// Set Stroke/Border
			rect.setStrokeColor(vss.getBorderColor());
			rect.setStrokeWeight(vss.getBorderWidth());

			// Set Font and Position for the child MTTextAreas
			if (((MTPolygon) c).getTexture() == null) {
				rect.setFillColor(vss.getBackgroundColor());
				for (MTComponent d : c.getChildren()) {
					if (d instanceof MTTextArea) {
						MTTextArea ta = (MTTextArea) d;
						ta.setFont(font);
					}
				}

				float height = calcTotalHeight(c.getChildren());
				float ypos = rect.getHeightXY(TransformSpace.LOCAL) / 2f
						- height / 2f;
				for (MTComponent d : c.getChildren()) {
					if (d instanceof MTTextArea) {
						MTTextArea ta = (MTTextArea) d;

						ta.setPositionRelativeToParent(new Vector3D(size / 2f,
								ypos + ta.getHeightXY(TransformSpace.LOCAL)
										/ 2f));
						ypos += ta.getHeightXY(TransformSpace.LOCAL);

					}

				}
			} else {
				// Set Fill Color for Pictures
				MTColor fillColor = MTColor.WHITE;
				// fillColor.setAlpha(vss.getOpacity());
				rect.setFillColor(fillColor);
			}

		}

		// Min/Max Values of the Children
		float minx = 16000, maxx = -16000, miny = 16000, maxy = -16000;

		float hypotenuse = (float) ((size / 2f) / Math.cos(Math.toRadians(30)));
		float gegenkathete = (float) (Math.sin(Math.toRadians(30)) * hypotenuse);
		int currentRow = 0;

		// Position the Polygons in the grid
		for (List<MTPolygon> lr : layout) {
			int currentColumn = 0;
			for (MTPolygon r : lr) {
				r.setPositionRelativeToParent((new Vector3D(this
						.getVerticesLocal()[0].x
						+ (size / 2f)
						+ (bezel / 2f)
						+ currentColumn++
						* (size + bezel)
						+ (maxPerLine - lr.size()) * (size / 2f + bezel / 2f),
						this.getVerticesLocal()[0].y
								+ (hypotenuse / 2 + bezel / 2f) + currentRow
								* (hypotenuse + gegenkathete + bezel))));

				// Determine Min/Max-Positions
//				for (Vertex v : r.getVerticesGlobal()) {
//					if (v.x < minx)
//						minx = v.x;
//					if (v.x > maxx)
//						maxx = v.x;
//					if (v.y < miny)
//						miny = v.y;
//					if (v.y > maxy)
//						maxy = v.y;
//				}
				
				//Determine Min/Max-Positions
				//We have to use the childrens relative-to parent vertices for the calculation of this component's local vertices
				Vertex[] unTransformedCopy = Vertex.getDeepVertexArrayCopy(r.getGeometryInfo().getVertices());
				//transform the copied vertices and save them in the vertices array
				Vertex[] verticesRelParent = Vertex.transFormArray(r.getLocalMatrix(), unTransformedCopy);
				for (Vertex v: verticesRelParent) {
					if (v.x < minx) minx = v.x;
					if (v.x > maxx) maxx = v.x;
					if (v.y < miny) miny = v.y;
					if (v.y > maxy) maxy = v.y;
				}
			}
			currentRow++;
		}

		// Set Vertices to include all children
//		this.setVertices(new Vertex[] { new Vertex(minx, miny),
//				new Vertex(maxx, miny), new Vertex(maxx, maxy),
//				new Vertex(minx, maxy), new Vertex(minx, miny) });
		
		MTColor fill = this.getFillColor();
		//Set Vertices to include all children
		this.setVertices(new Vertex[] {new Vertex(minx,miny, 0, fill.getR(), fill.getG(), fill.getB(), fill.getAlpha()), new Vertex(maxx,miny, 0, fill.getR(), fill.getG(), fill.getB(), fill.getAlpha()), new Vertex(maxx,maxy, 0, fill.getR(), fill.getG(), fill.getB(), fill.getAlpha()), new Vertex(minx,maxy, 0, fill.getR(), fill.getG(), fill.getB(), fill.getAlpha()),new Vertex(minx,miny, 0, fill.getR(), fill.getG(), fill.getB(), fill.getAlpha())});
	}

	/**
	 * The listener interface for receiving tap events. The class that is
	 * interested in processing a tap event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addTapListener<code> method. When
	 * the tap event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see TapEvent
	 */
	public class TapListener implements IGestureEventListener {
		// Tap Listener to reach through TapListeners to children
	
		/** The children. */
		List<PolygonListeners> children;
	
		/**
		 * Instantiates a new tap listener.
		 * 
		 * @param children
		 *            the children
		 */
		public TapListener(List<PolygonListeners> children) {
			this.children = children;
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.mt4j.input.inputProcessors.IGestureEventListener#processGestureEvent
		 * (org.mt4j.input.inputProcessors.MTGestureEvent)
		 */
		public boolean processGestureEvent(MTGestureEvent ge) {
			if (ge instanceof TapEvent) {
	
				TapEvent te = (TapEvent) ge;
				if (te.getTapID() == TapEvent.TAPPED) {
	
					for (PolygonListeners pl : children) {
						pl.component.setPickable(true);
						if (pl.component.getIntersectionGlobal(Tools3D
								.getCameraPickRay(app, pl.component, te
										.getCursor().getPosition().x, te
										.getCursor().getPosition().y)) != null) {
							pl.listener.processGestureEvent(ge);
						} else {
	
						}
						pl.component.setPickable(false);
					}
				}
			}
			return false;
		}
	
	}

	/**
	 * The Class PolygonListeners.
	 */
	public class PolygonListeners {
	
		/** The component. */
		public MTPolygon component;
	
		/** The listener. */
		public IGestureEventListener listener;
	
		/**
		 * Instantiates a new polygon listeners mapping.
		 *
		 * @param component the component
		 * @param listener the listener
		 */
		public PolygonListeners(MTPolygon component,
				IGestureEventListener listener) {
			this.component = component;
			this.listener = listener;
		}
	
	}

}