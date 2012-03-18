package org.mt4jx.components.visibleComponents.widgets;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.keyboard.MTKeyboard;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import processing.core.PGraphics;

/**
 * The Class MTSuggestionTextArea.
 */
public class MTSuggestionTextArea extends MTTextArea {

	/** The available suggestions. */
	private List<String> availableValues;

	/** The MTApplication. */
	private AbstractMTApplication app;

	/** The suggestion box. */
	private MTTextArea suggestionBox;

	/** The keyboard. */
	private MTKeyboard keyboard;

	/** The width. */
	private float width = -1f;

	/** The current suggestions. */
	private List<String> currentSuggestions = new ArrayList<String>();

	/** The o (Counting Variable). */
	private int o = 0;

	/** The p (Counting Variable). */
	private int p = 0;

	/**
	 * Instantiates a new MTSuggestionTextArea.
	 * 
	 * @param app
	 *            the MTApplication
	 * @param width
	 *            the width
	 */
	public MTSuggestionTextArea(AbstractMTApplication app, float width) {
		this(app, width, new ArrayList<String>());
	}

	/**
	 * Instantiates a new MTSuggestionTextArea.
	 * 
	 * @param app
	 *            the MTApplication
	 * @param width
	 *            the width of the text input box
	 * @param suggestions
	 *            the suggestions as List<String>
	 */
	public MTSuggestionTextArea(AbstractMTApplication app, float width,
			List<String> suggestions) {
		// Instantiate with default font, can be changed using CSS
		super(app);
		this.init(app, width, suggestions);
	}

	/**
	 * Instantiates a new MTSuggestionTextArea.
	 * 
	 * @param app
	 *            the MTApplication
	 * @param font
	 *            the font
	 * @param width
	 *            the width of the text input box
	 */
	public MTSuggestionTextArea(AbstractMTApplication app, IFont font, float width) {
		this(app, font, width, new ArrayList<String>());
	}

	/**
	 * Instantiates a new MTSuggestionTextArea.
	 * 
	 * @param app
	 *            the MTApplication
	 * @param font
	 *            the font
	 * @param width
	 *            the width of the text input box
	 * @param suggestions
	 *            the suggestions as List<String>
	 */
	public MTSuggestionTextArea(AbstractMTApplication app, IFont font, float width,
			List<String> suggestions) {
		super(app, font);
		this.init(app, width, suggestions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mt4j.components.visibleComponents.widgets.MTTextArea#drawComponent
	 * (processing.core.PGraphics)
	 */
	@Override
	public void drawComponent(PGraphics g) {
		super.drawComponent(g);
		if (keyboard != null) {
			if (o++ > 30) {
				o = 0;
				drawSuggestionBox();

			}
			if (p++ > 4) {
				this.setWidthLocal(width);
			}
		} else {
			if (suggestionBox.isVisible() == true)
				suggestionBox.setVisible(false);
		}

	}

	/**
	 * Gets the relevant strings (for the current content of the TextArea).
	 * 
	 * @return the relevant strings
	 */
	public List<String> getRelevantStrings() {
		List<String> newList = new ArrayList<String>();
		if (!availableValues.isEmpty()) {
			String currentText = this.getText();
			for (String s : availableValues) {
				if (currentText != "") {
					if (s.toUpperCase().contains(currentText.toUpperCase())) {
						newList.add(s.replaceAll("\n", " "));
					}
				} else {
					newList.add(s.replace("\n", ""));
				}
			}
		}
		return newList;
	}

	/**
	 * Style suggestion box.
	 */
	public void styleSuggestionBox() {
		suggestionBox.setNoFill(this.isNoFill());
		suggestionBox.setNoStroke(this.isNoStroke());
		suggestionBox.setFillColor(this.getFillColor());
		suggestionBox.setStrokeColor(new MTColor(this.getStrokeColor().getR(),
				this.getStrokeColor().getG(), this.getStrokeColor().getG(),
				this.getStrokeColor().getAlpha() * 0.75f));
		suggestionBox.setStrokeWeight(0.25f);
	}

	/**
	 * Calculate the Coordinates needed for placing the Rectangle.
	 * 
	 * @param rect
	 *            the Rectangle
	 * @param ta
	 *            the TextArea
	 * @param xo
	 *            the x-offset
	 * @param yo
	 *            the y-offset
	 * @return the position as Vector3D
	 */
	private Vector3D calcPos(MTRectangle rect, MTPolygon ta, float xo, float yo) {
		return new Vector3D((ta.getWidthXY(TransformSpace.LOCAL) / 2)
				+ rect.getVerticesLocal()[0].getX() + xo, 
				(ta.getHeightXY(TransformSpace.LOCAL) / 2)
				+ rect.getVerticesLocal()[0].getY() + yo);
	}

	/**
	 * Draw suggestion box.
	 */
	private void drawSuggestionBox() {
		String suggestions = "";
		int i = 0;
		List<String> strings = this.getRelevantStrings();
		if (strings.size() > 0) {
			suggestionBox.setVisible(true);
			currentSuggestions.clear();
			for (String s : strings) {
				if (i != 0 && i < 5)
					suggestions += "\n";

				if (i++ < 5) {
					suggestions += s;
					currentSuggestions.add(s);
				} else {
					break;
				}

			}
			suggestionBox.setText(suggestions);
			suggestionBox.setWidthLocal(width);
			suggestionBox.setPositionRelativeToParent(calcPos(this,
					suggestionBox, 0, this.getHeightXY(TransformSpace.LOCAL)));
		} else {
			suggestionBox.setVisible(false);
		}
		if (keyboard == null)
			suggestionBox.setVisible(false);
	}

	/**
	 * Inits the MTSuggestionTextArea
	 * 
	 * @param app
	 *            the app
	 * @param width
	 *            the width
	 * @param suggestions
	 *            the suggestions as List<String>
	 */
	private void init(AbstractMTApplication app, float width, List<String> suggestions) {
		this.width = width;
		
		this.availableValues = suggestions;
		this.app = app;
		this.setFont(this.getCssHelper().getVirtualStyleSheet().getFont());
		this.setWidthLocal(width);
		
		this.removeAllChildren();
		this.removeAllGestureEventListeners(TapProcessor.class);
		
		// Add Tap listener to evoke Keyboard
		this.setGestureAllowance(TapProcessor.class, true);
		this.registerInputProcessor(new TapProcessor(app));
		this.addGestureListener(TapProcessor.class, new EditListener(this));

		// Create Suggestion Box
		suggestionBox = new MTTextArea(app, this.getFont());
		suggestionBox.setWidthLocal(width);
		suggestionBox.setCssForceDisable(true);
		styleSuggestionBox();

		suggestionBox.removeAllGestureEventListeners();
		suggestionBox.setGestureAllowance(TapProcessor.class, true);
		suggestionBox.registerInputProcessor(new TapProcessor(app));
		suggestionBox.addGestureListener(TapProcessor.class,
				new SuggestionBoxListener());

		this.addChild(suggestionBox);
		drawSuggestionBox();
	}
	
	public void init() {
		if (app != null && width != -1f && this.availableValues != null) {
			init(app, width, this.availableValues);
		}
	}
	
	
	@Override
	protected void applyStyleSheetCustom(CSSStyle virtualStyleSheet) {
		super.applyStyleSheetCustom(virtualStyleSheet);
		this.init();
	}


	/**
	 * The listener interface for receiving suggestionBox events. The class that
	 * is interested in processing a suggestionBox event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addSuggestionBoxListener<code> method. When
	 * the suggestionBox event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see SuggestionBoxEvent
	 */
	public class SuggestionBoxListener implements IGestureEventListener {
	
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
					Vector3D w = Tools3D.project(app, app.getCurrentScene()
							.getSceneCam(), te.getLocationOnScreen());
					Vector3D x = suggestionBox.globalToLocal(w);
					float zero = suggestionBox.getVerticesLocal()[0].y;
					float heightPerLine = suggestionBox
							.getHeightXY(TransformSpace.LOCAL)
							/ (float) (suggestionBox.getLineCount() + 1);
					int line = (int) ((x.y - zero) / heightPerLine);
	
					if (currentSuggestions.size() > line) {
						setText(currentSuggestions.get(line));
					}
	
				}
			}
			return false;
		}
	
	}

	/**
	 * The listener interface for receiving keyboard events. The class that is
	 * interested in processing a keyboard event implements this interface, and
	 * the object created with that class is registered with a component using
	 * the component's <code>addKeyboardListener<code> method. When
	 * the keyboard event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see KeyboardEvent
	 */
	public class KeyboardListener implements StateChangeListener {
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.mt4j.components.StateChangeListener#stateChanged(org.mt4j.components
		 * .StateChangeEvent)
		 */
		public void stateChanged(StateChangeEvent evt) {
			if (evt.getState() == StateChange.COMPONENT_DESTROYED) {
				keyboard = null;
			}
	
		}
	
	}

	/**
	 * The listener interface for receiving edit events. The class that is
	 * interested in processing a edit event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addEditListener<code> method. When
	 * the edit event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see EditEvent
	 */
	public class EditListener implements IGestureEventListener {
	
		/** The ta. */
		private MTTextArea ta;
	
		/**
		 * Instantiates a new edits the listener.
		 * 
		 * @param ta
		 *            the ta
		 */
		public EditListener(MTTextArea ta) {
			this.ta = ta;
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
					System.out.println(ta.getText() + ": " + te.getTapID() + " (" + te.getId() + ")");
					if (keyboard == null
							&& te.getTapID() == TapEvent.TAPPED) {
						keyboard = new MTKeyboard(app);
						addChild(keyboard);
						keyboard.addTextInputListener(ta);
						keyboard.addStateChangeListener(
								StateChange.COMPONENT_DESTROYED,
								new KeyboardListener());
	
						keyboard.setCssForceDisable(true);
	
						keyboard.setNoFill(ta.isNoFill());
						keyboard.setNoStroke(ta.isNoStroke());
						keyboard.setFillColor(ta.getFillColor());
						keyboard.setStrokeColor(new MTColor(ta.getStrokeColor().getR(),
								ta.getStrokeColor().getG(), ta.getStrokeColor().getG(),
								ta.getStrokeColor().getAlpha() * 0.75f));
						keyboard.setStrokeWeight(ta.getStrokeWeight());
	
						keyboard.setPositionRelativeToParent(calcPos(
								ta,
								keyboard,
								0,
								ta.getHeightXY(TransformSpace.LOCAL)
								+ suggestionBox
								.getHeightXY(TransformSpace.LOCAL)));
					}
				}
			}
			return false;
		}
	
	}
}
