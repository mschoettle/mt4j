

package org.mt4j.util.manyMouse;

import java.io.File;



/*
 * Java bindings to the ManyMouse C code, via JNI.
 *
 * Please see the file LICENSE.txt in the source's root directory.
 *
 *  This file written by Ryan C. Gordon.
 */

/**
 * The Class ManyMouse.
 */
public class ManyMouse{
	
	// JNI link.
	static { 
		//TODO compile library on linux/osx
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
			System.loadLibrary("ManyMouse"); 
		}else if  (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
//			System.loadLibrary("ManyMouse");  //FIXME why not working?
			System.load(System.getProperty("user.dir") + File.separator +  "ManyMouse.so");
		}
	}
//	static { System.load("D:\\Eclipse Workspace\\MTMetaCollab\\ManyMouse.dll"); }
    
    // Native method hooks.
    /**
	 * Inits the.
	 * 
	 * @return the int
	 */
    public native static synchronized int Init();
    
    /**
     * Java_ many mouse_ init.
     * 
     * @return the int
     */
    public native static synchronized int Java_ManyMouse_Init();
    
    /**
     * Many mouse_ init.
     * 
     * @return the int
     */
    public native static synchronized int ManyMouse_Init();
    
    /**
     * Quit.
     */
    public native static synchronized void Quit();
    
    /**
     * Device name.
     * 
     * @param index the index
     * 
     * @return the string
     */
    public native static synchronized String DeviceName(int index);
    
    /**
     * Poll event.
     * 
     * @param event the event
     * 
     * @return true, if successful
     */
    public native static synchronized boolean PollEvent(ManyMouseEvent event);


} // ManyMouse

// end of ManyMouse.java ...

