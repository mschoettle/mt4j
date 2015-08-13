package org.mt4j.input.inputData;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputSources.AbstractInputSource;

import processing.core.PConstants;

/**
 * The Class MTMouseInputEvt.
 */
public class MTMouseInputEvt extends MTFingerInputEvt {

    // These correspond to the java.awt.Event modifiers (not to be confused with
    // the newer getModifiersEx), though they're not guaranteed to in the future.
    static public final int SHIFT = 1 << 0;
    static public final int CTRL = 1 << 1;
    static public final int META = 1 << 2;
    static public final int ALT = 1 << 3;
    /** The mouse modifiers. */
    private int mouseModifiers;

    /** The mouse button. */
    private int mouseButton;

    /**
     * Instantiates a new mT mouse input evt.
     *
     * @param source the source
     * @param target the target
     * @param mouseModifiers the mouse modifiers
     * @param positionX the position x
     * @param positionY the position y
     * @param id the id
     * @param m the m
     * @param mouseButton the mouseButton
     */
    public MTMouseInputEvt(AbstractInputSource source, IMTComponent3D target, int mouseModifiers, float positionX,
            float positionY, int id, InputCursor m, int mouseButton) {
        super(source, target, positionX, positionY, id, m);
        this.mouseModifiers = mouseModifiers;
        this.mouseButton = mouseButton;
    }

    /**
     * Instantiates a new mT mouse input evt.
     *
     * @param source the source
     * @param mouseModifiers the mouse modifiers
     * @param positionX the position x
     * @param positionY the position y
     * @param id the id
     * @param m the m
     * @param mouseButton the mouseButton
     */
    public MTMouseInputEvt(AbstractInputSource source, int mouseModifiers, float positionX, float positionY, int id,
            InputCursor m, int mouseButton) {
        super(source, positionX, positionY, id, m);
        this.mouseModifiers = mouseModifiers;
        this.mouseButton = mouseButton;
    }

    /**
     * Gets the modifiers.
     *
     * @return the modifiers
     */
    public int getModifiers() {
        return this.mouseModifiers;
    }

    /**
     * Gets the pressed button.
     *
     * @see processing.core.PConstants.LEFT
     * @see processing.core.PConstants.RIGHT
     * @see processing.core.PConstants.CENTER
     *
     * @return the pressed button.
     */
    public int getMouseButton() {
        return mouseButton;
    }

    public boolean isShiftDown() {
        return (mouseModifiers & SHIFT) != 0;
    }

    public boolean isControlDown() {
        return (mouseModifiers & CTRL) != 0;
    }

    public boolean isMetaDown() {
        return (mouseModifiers & META) != 0;
    }

    public boolean isAltDown() {
        return (mouseModifiers & ALT) != 0;
    }

    public boolean isLeftButton() {
        return this.mouseButton == PConstants.LEFT;
    }

    public boolean isRightButton() {
        return this.mouseButton == PConstants.RIGHT;
    }

    public boolean isCenterButton() {
        return this.mouseButton == PConstants.CENTER;
    }

}
