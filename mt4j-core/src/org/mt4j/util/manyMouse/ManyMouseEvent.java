

package org.mt4j.util.manyMouse;

/*
 * Java bindings to the ManyMouse C code, via JNI.
 *
 * Please see the file LICENSE.txt in the source's root directory.
 *
 *  This file written by Ryan C. Gordon.
 */

/**
 * The Class ManyMouseEvent.
 */
public class ManyMouseEvent
{
    // Event types...
    // !!! can be real enums in Java 5.0.
    /** The Constant ABSMOTION. */
    public static final int ABSMOTION = 0;
    
    /** The Constant RELMOTION. */
    public static final int RELMOTION = 1;
    
    /** The Constant BUTTON. */
    public static final int BUTTON = 2;
    
    /** The Constant SCROLL. */
    public static final int SCROLL = 3;
    
    /** The Constant DISCONNECT. */
    public static final int DISCONNECT = 4;
    
    /** The Constant MAX. */
    public static final int MAX = 5;  // Only for reference: should not be set.

    /** The type. */
    public int type;
    
    /** The device. */
    public int device;
    
    /** The item. */
    public int item;
    
    /** The value. */
    public int value;
    
    /** The minval. */
    public int minval;
    
    /** The maxval. */
    public int maxval;
} // ManyMouseEvent

// end of ManyMouseEvent.java ...

