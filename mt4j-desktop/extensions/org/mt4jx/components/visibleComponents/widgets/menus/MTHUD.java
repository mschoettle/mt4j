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
import org.mt4j.components.css.util.CSSHelper;
import org.mt4j.components.css.util.CSSStylableComponent;
import org.mt4j.components.css.util.CSSKeywords.CSSFontWeight;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTOverlayContainer;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.widgets.menus.MenuItem;

import processing.core.PConstants;
import processing.core.PImage;

/**
 * The Class MTHUD.
 */
public class MTHUD extends MTOverlayContainer implements CSSStylableComponent {

	/** The Constant LEFT. */
	public final static short LEFT = 1;

	/** The Constant RIGHT. */
	public final static short RIGHT = 2;

	/** The Constant TOP. */
	public final static short TOP = 3;

	/** The Constant BOTTOM. */
	public final static short BOTTOM = 4;

	/** The app. */
	private AbstractMTApplication app;

	/** The menu contents. */
	private List<MTRectangle> menuContents = new ArrayList<MTRectangle>();

	/** The menu items. */
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();

	/** The size. */
	private float size;

	/** The offset. */
	private float offset;

	/** The position. */
	private short position;

	/** The css helper. */
	private CSSHelper cssHelper;

	/** The mt app. */
	private AbstractMTApplication mtApp;

	/** The css styled. */
	private boolean cssStyled = false;

	/** The css force disabled. */
	private boolean cssForceDisabled = false;

	/**
	 * Instantiates a new MTHUD.
	 * 
	 * @param applet
	 *            the applet
	 * @param menuItems
	 *            the menu items
	 */
	public MTHUD(AbstractMTApplication applet, List<MenuItem> menuItems) {
		this(applet, menuItems, 64, 8, LEFT);
	}

	/**
	 * Instantiates a new MTHUD.
	 * 
	 * @param applet
	 *            the applet
	 * @param menuItems
	 *            the menu items
	 * @param size
	 *            the size
	 */
	public MTHUD(AbstractMTApplication applet, List<MenuItem> menuItems, float size) {
		this(applet, menuItems, size, (float) Math.sqrt(size), LEFT);
	}

	/**
	 * Instantiates a new MTHUD.
	 * 
	 * @param applet
	 *            the applet
	 * @param menuItems
	 *            the menu items
	 * @param size
	 *            the size
	 * @param offset
	 *            the offset (distance between items)
	 * @param position
	 *            the position (MTHUD.LEFT, MTHUD.RIGHT, MTHUD.TOP,
	 *            MTHUD.BOTTOM)
	 */
	public MTHUD(AbstractMTApplication applet, List<MenuItem> menuItems, float size, float offset, short position) {
		super(applet);
		this.app = applet;
		this.menuItems = menuItems;
		this.size = size;
		this.offset = offset;
		this.position = position;
		this.setCssForceDisable(true);

		this.cssHelper = new CSSHelper(this, applet);

		createMenuItems();

	}

	/**
	 * Instantiates a new MTHUD.
	 * 
	 * @param applet
	 *            the applet
	 * @param menuItems
	 *            the menu items
	 * @param size
	 *            the size
	 * @param position
	 *            the position (MTHUD.LEFT, MTHUD.RIGHT, MTHUD.TOP,
	 *            MTHUD.BOTTOM)
	 */
	public MTHUD(AbstractMTApplication applet, List<MenuItem> menuItems, float size, short position) {
		this(applet, menuItems, size, (float) Math.sqrt(size), position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mt4j.css.util.CSSStylableComponent#applyStyleSheet()
	 */
	public void applyStyleSheet() {
		if (cssStyled && mtApp != null && cssHelper != null) {
			cssHelper.applyStyleSheet(this);
		}
	}

	/**
	 * Creates the menu items from the complete list of MenuItems
	 */
	public void createMenuItems() {
		this.createMenuItems(menuItems);
	}

	/**
	 * Creates the menu items (from the list of items provided)
	 * 
	 * @param items
	 *            the items
	 */
	public void createMenuItems(List<MenuItem> items) {

		for (MTComponent c : this.getChildren()) {
			c.destroy();
		}
		this.removeAllChildren();
		menuContents.clear();

		for (MenuItem s : items) {

			if (s != null && s.getType() == MenuItem.TEXT) {
				// Create a new menu cell
				MTRectangle container = new MTRectangle(app, 0, 0, size, size);
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

				container.setGestureAllowance(TapProcessor.class, true);
				container.registerInputProcessor(new TapProcessor(app));
				container.addGestureListener(TapProcessor.class,
						s.getGestureListener());
				container.setCssForceDisable(true);
				container.setGestureAllowance(DragProcessor.class, false);
				container.setGestureAllowance(RotateProcessor.class, false);
				container.setGestureAllowance(ZoomProcessor.class, false);
				container.setGestureAllowance(ScaleProcessor.class, false);

				menuContents.add(container);

			} else if (s != null && s.getType() == MenuItem.PICTURE) {

				if (s.getMenuImage() != null) {
					PImage texture = null;
					// If Image doesn't fit, make it fit!
					if (s.getMenuImage().width != (int) size
							|| s.getMenuImage().height != (int) size) {
						texture = cropImage(s.getMenuImage(), (int) size, true);
					} else {
						texture = s.getMenuImage();
					}

					// Create a new menu cell
					MTRectangle container = new MTRectangle(app, 0, 0, size,
							size);
					this.addChild(container);
					// Set the background texture
					container.setTexture(texture);

					container.setChildClip(new Clip(container));

					container.setGestureAllowance(TapProcessor.class, true);
					container.registerInputProcessor(new TapProcessor(app));
					container.addGestureListener(TapProcessor.class,
							s.getGestureListener());
					container.setCssForceDisable(true);

					container.setGestureAllowance(DragProcessor.class, false);
					container.setGestureAllowance(RotateProcessor.class, false);
					container.setGestureAllowance(ZoomProcessor.class, false);
					container.setGestureAllowance(ScaleProcessor.class, false);

					menuContents.add(container);

				}

			}

		}

		this.styleChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mt4j.css.util.CSSStylableComponent#disableCSS()
	 */
	public void disableCSS() {
		cssStyled = false;

	}

	/**
	 * Display all items in the MenuList
	 */
	public void displayAll() {
		this.createMenuItems();
	}

	/**
	 * Display a subset of the menu
	 * 
	 * @param items
	 *            the items as int[] (items 1,2,3,...n)
	 */
	public void displaySubset(int[] items) {
		this.createMenuItems(this.getRelevantItems(items));
	}

	/**
	 * Display a subset of the menu
	 * 
	 * @param items
	 *            the items as List<MenuItem> to display
	 */
	public void displaySubset(List<MenuItem> items) {
		this.createMenuItems(this.getRelevantItems(items));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mt4j.css.util.CSSStylableComponent#enableCSS()
	 */
	public void enableCSS() {
		if (mtApp != null && cssHelper != null) {
			cssStyled = true;
		}
		applyStyleSheet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mt4j.css.util.CSSStylableComponent#getCssHelper()
	 */
	public CSSHelper getCssHelper() {
		return this.cssHelper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mt4j.css.util.CSSStylableComponent#isCssForceDisabled()
	 */
	public boolean isCssForceDisabled() {
		return cssForceDisabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mt4j.css.util.CSSStylableComponent#isCSSStyled()
	 */
	public boolean isCSSStyled() {
		return cssStyled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mt4j.css.util.CSSStylableComponent#setCssForceDisable(boolean)
	 */
	public void setCssForceDisable(boolean cssForceDisabled) {
		this.cssForceDisabled = cssForceDisabled;
	}

	/**
	 * Sets the menu items and reinstantiates the menu.
	 * 
	 * @param menuItems
	 *            the new menu items
	 */
	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
		createMenuItems();
	}

	/**
	 * Sets the size.
	 * 
	 * @param size
	 *            the new size
	 */
	public void setSize(float size) {
		this.size = size;
		this.createMenuItems();
	}

	/**
	 * Calculates the total height of a number of MTTextAreas.
	 * 
	 * @param components
	 *            the components
	 * @return the height
	 */
	private float calcTotalHeight(MTComponent[] components) {
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
	 * @param size
	 *            the size
	 * @param resize
	 *            Force-resize the image?
	 * @return the cropped image
	 */
	private PImage cropImage(PImage image, int size, boolean resize) {
		PImage workingCopy;
		try {
			workingCopy = (PImage) image.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning not supported!");
			workingCopy = image;
		}

		// Crops (and resizes) an Image to fit into the suqare
		PImage returnImage = app.createImage(size, size, PConstants.RGB);

		// Resize Image
		if (resize || workingCopy.width < size || workingCopy.height < size) {
			if (workingCopy.width < workingCopy.height) {
				workingCopy
						.resize(size,
								(int) ((float) workingCopy.height / ((float) workingCopy.width / (float) size)));
			} else {
				workingCopy
						.resize((int) ((float) workingCopy.width / ((float) workingCopy.height / (float) size)),
								size);
			}

		}

		// Crop Starting Points
		int x = (workingCopy.width / 2) - (size / 2);
		int y = (workingCopy.height / 2) - (size / 2);

		// Bugfixing: Don't Allow Out-of-Bounds coordinates
		if (x + size > workingCopy.width)
			x = workingCopy.width - size;
		if (x < 0)
			x = 0;
		if (x + size > workingCopy.width)
			size = workingCopy.width - x;
		if (y + size > workingCopy.height)
			x = workingCopy.height - size;
		if (y < 0)
			y = 0;
		if (y + size > workingCopy.height)
			size = workingCopy.height - y;

		// Crop Image
		returnImage.copy(workingCopy, x, y, size, size, 0, 0, size, size);

		return returnImage;
	}

	/**
	 * Gets the maximum font size for a certain width.
	 * 
	 * @param strings
	 *            the strings
	 * @param size
	 *            the width
	 * @return the maximum font size
	 */
	private int getNecessaryFontSize(List<MenuItem> strings, float size) {
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
	 * Gets the relevant items.
	 * 
	 * @param items
	 *            the items
	 * @return the relevant items
	 */
	private List<MenuItem> getRelevantItems(int[] items) {
		List<MenuItem> returnList = new ArrayList<MenuItem>();
		for (int i : items) {
			if (i > 0 && i <= menuItems.size()) {
				try {
					returnList.add(menuItems.get(i + 1));
				} catch (Exception e) {

				}
			}

		}
		return returnList;
	}

	/**
	 * Gets the relevant items.
	 * 
	 * @param items
	 *            the items
	 * @return the relevant items
	 */
	private List<MenuItem> getRelevantItems(List<MenuItem> items) {
		List<MenuItem> returnList = new ArrayList<MenuItem>();
		for (MenuItem i : items) {
			if (menuItems.contains(i)) {
				returnList.add(i);
			}

		}
		return returnList;
	}

	/**
	 * Style children.
	 */
	private void styleChildren() {
		int fontsize = getNecessaryFontSize(menuItems, size);
		CSSStyle vss = this.getCssHelper().getVirtualStyleSheet();
		CSSFont cf = this.getCssHelper().getVirtualStyleSheet().getCssfont().clone();
		// Style Font: Bold + fitting fontsize
		cf.setFontsize(fontsize);
		cf.setWeight(CSSFontWeight.BOLD);

		// Load Font
		CSSFontManager cfm = new CSSFontManager(app);
		IFont font = cfm.selectFont(cf);

		for (MTRectangle c : menuContents) {

			MTRectangle rect = c;

			c.setWidthLocal(size);
			c.setHeightLocal(size);

			// Set Stroke/Border
			rect.setStrokeColor(vss.getBorderColor());
			rect.setStrokeWeight(vss.getBorderWidth());

			// Set Font and Position for the child MTTextAreas
			if (((MTRectangle) c).getTexture() == null) {
				rect.setFillColor(vss.getBackgroundColor());
				for (MTComponent d : c.getChildren()) {
					if (d instanceof MTTextArea) {
						MTTextArea ta = (MTTextArea) d;
						ta.setFont(font);
					}
				}

				float height = calcTotalHeight(c.getChildren());
				float ypos = size / 2f - height / 2f;
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
				// Set FillColor for the image (neutral white)
				rect.setFillColor(MTColor.WHITE);
			}
			this.addChild(c);
		}
		float xoffset = offset / 2f;
		float yoffset = offset / 2f;

		switch (position) {
		case LEFT:
			for (MTRectangle r : menuContents) {
				r.setAnchor(PositionAnchor.UPPER_LEFT);

				if (yoffset + r.getHeightXY(TransformSpace.LOCAL) > MT4jSettings
						.getInstance().getWindowHeight()) {
					xoffset += r.getWidthXY(TransformSpace.LOCAL) + offset;
					yoffset = offset / 2f;
				}

				r.setPositionGlobal(new Vector3D(xoffset, yoffset));
				yoffset += r.getHeightXY(TransformSpace.LOCAL) + offset;

			}
			break;
		case RIGHT:
			xoffset = MT4jSettings.getInstance().getWindowWidth() - size
					- xoffset;
			for (MTRectangle r : menuContents) {
				r.setAnchor(PositionAnchor.UPPER_LEFT);

				if (yoffset + r.getHeightXY(TransformSpace.LOCAL) > MT4jSettings
						.getInstance().getWindowHeight()) {
					xoffset -= r.getWidthXY(TransformSpace.LOCAL) + offset;
					yoffset = offset / 2f;
				}

				r.setPositionGlobal(new Vector3D(xoffset, yoffset));
				yoffset += r.getHeightXY(TransformSpace.LOCAL) + offset;

			}
			break;
		case TOP:
			for (MTRectangle r : menuContents) {
				r.setAnchor(PositionAnchor.UPPER_LEFT);

				if (xoffset + r.getWidthXY(TransformSpace.LOCAL) > MT4jSettings
						.getInstance().getWindowWidth()) {
					xoffset = offset / 2f;
					yoffset += r.getHeightXY(TransformSpace.LOCAL) + offset;
				}

				r.setPositionGlobal(new Vector3D(xoffset, yoffset));
				xoffset += r.getWidthXY(TransformSpace.LOCAL) + offset;

			}
			break;
		case BOTTOM:
			yoffset = MT4jSettings.getInstance().getWindowHeight() - size
					- yoffset;
			for (MTRectangle r : menuContents) {
				r.setAnchor(PositionAnchor.UPPER_LEFT);

				if (xoffset + r.getWidthXY(TransformSpace.LOCAL) > MT4jSettings
						.getInstance().getWindowWidth()) {
					xoffset = offset / 2f;
					yoffset -= r.getHeightXY(TransformSpace.LOCAL) + offset;
				}

				r.setPositionGlobal(new Vector3D(xoffset, yoffset));
				xoffset += r.getWidthXY(TransformSpace.LOCAL) + offset;

			}

			break;
		default:
			break;

		}

	}

}
