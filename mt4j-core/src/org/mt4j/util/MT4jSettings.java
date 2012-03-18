/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.util;

import org.mt4j.AbstractMTApplication;
import org.mt4j.util.math.Vector3D;

/**
 * A class with some configurations to read the current settings from.
 * 
 * @author Christopher Ruff
 */
public class MT4jSettings {
	
	/** The const and settings. */
	private static MT4jSettings constAndSettings = null;
	
	/** Screen Size X. */
	public int windowWidth = 1024;
	
	/** Screen Size Y. */
	public int windowHeight = 768;
	
	//Draw Modes
	/** The Constant OPENGL_MODE. */
	public static final int OPENGL_MODE = 1;
	
	/** The Constant P3D_MODE. */
	public static final int P3D_MODE    = 2;
	
	/** Current DrawMode. */
	public int renderer = P3D_MODE;
	
	/** The num samples. */
	public int numSamples = 0;
	
	/** Frame Title. */
	public String frameTitle = "MT-Application";
	
	/** Maximum FrameRate. */
	public int maxFrameRate = 60;
	
	/** Start time of the app. */
	public long programStartTime = 0;
	
	/** The Constant ARCHITECTURE_32_BIT. */
	public static final int ARCHITECTURE_32_BIT = 32;
	
	/** The Constant ARCHITECTURE_64_BIT. */
	public static final int ARCHITECTURE_64_BIT = 64;
	
	/** The architecture. */
	public int architecture = ARCHITECTURE_32_BIT;

	/** The v sync. */
	public boolean vSync = false;
	
	/** The fullscreen. */
	public static boolean fullscreen = false;
	
	/** The display. */
	public int display = 1;
	
	/** The fullscreen exclusive. */
	public boolean fullscreenExclusive = false;
	

	/** The DEFAUL t_ fon t_ path. */
	public static String DEFAULT_SETTINGS_PATH = "";

	/** The DEFAUL t_ dat a_ folde r_ path. */
	public static String DEFAULT_DATA_FOLDER_PATH = "data" + AbstractMTApplication.separator;
	
	/** The DEFAUL t_ fon t_ path. */
	public static String DEFAULT_FONT_PATH = DEFAULT_DATA_FOLDER_PATH;
	
	/** The DEFAUL t_ image s_ path. */
	public static String DEFAULT_IMAGES_PATH = "data" + AbstractMTApplication.separator + "images" + AbstractMTApplication.separator;
			 
	
//	public static String DEFAULT_VIDEOS_PATH = new String(System.getProperty("user.dir") + File.separator + "data" /*+ File.separator + "videos"  */ +  File.separator);
	//Since gsvideo looks into the ./data directory by itself
	/** The DEFAUL t_ video s_ path. */
	public static String DEFAULT_VIDEOS_PATH = "";
	
	/** The DEFAUL t_ sv g_ path. */
	public static String DEFAULT_SVG_PATH = "data" + AbstractMTApplication.separator + "svg" + AbstractMTApplication.separator;
	
	/** The DEFAUL t_3 d_ mode l_ path. */
	public static String DEFAULT_3D_MODEL_PATH = "data" + AbstractMTApplication.separator + "models" + AbstractMTApplication.separator;
	

	/**
	 * Gets the path to the /data folder.
	 * 
	 * @return the default data path
	 */
	public String getDataFolderPath() {
		return DEFAULT_DATA_FOLDER_PATH;
	}
	
	
	/**
	 * Gets the default settings path.
	 * 
	 * @return the default settings path
	 */
	public String getDefaultSettingsPath() {
		return DEFAULT_SETTINGS_PATH;
	}

	/**
	 * Gets the default font path.
	 * 
	 * @return the default font path
	 */
	public String getDefaultFontPath(){
		return DEFAULT_FONT_PATH;
	}
	
	/**
	 * Gets the default images path.
	 * 
	 * @return the default images path
	 */
	public String getDefaultImagesPath(){
		return DEFAULT_IMAGES_PATH;
	}
	
	/**
	 * Gets the default videos path.
	 * 
	 * @return the default videos path
	 */
	public String getDefaultVideosPath(){
		return DEFAULT_VIDEOS_PATH;
	}
	
	/**
	 * Gets the default svg path.
	 * 
	 * @return the default svg path
	 */
	public String getDefaultSVGPath(){
		return DEFAULT_SVG_PATH;
	}
	
	/**
	 * Gets the default3 d model path.
	 * 
	 * @return the default3 d model path
	 */
	public String getDefault3DModelPath(){
		return DEFAULT_3D_MODEL_PATH;
	}
	
	
	/**
	 * Checks if is fullscreen.
	 * 
	 * @return true, if is fullscreen
	 */
	public boolean isFullscreen(){
		return fullscreen;
	}
	
	
	/**
	 * Gets the num samples.
	 * 
	 * @return the num samples
	 */
	public int getNumSamples() {
		return numSamples;
	}


	/**
	 * Checks if is multi sampling.
	 * 
	 * @return true, if is multi sampling
	 */
	public boolean isMultiSampling(){
		return getNumSamples() > 0;
	}


	/**
	 * Instantiates a new constants and settings.
	 */
	private MT4jSettings(){
	}

	/**
	 * Returns the GlobalConstants and Settings Object.
	 * Implements the singleton pattern.
	 * 
	 * @return ConstantsAndHelpers object
	 */
	public static MT4jSettings getInstance(){
		if (constAndSettings == null){
			constAndSettings = new MT4jSettings();
			return constAndSettings;
		}else{
			return constAndSettings;
		}
	}
	
	
	/**
	 * Gets the screen height.
	 * 
	 * @return the screen height
	 * @deprecated renamed to getWindowHeight() since this doesent return the screen height, but the MT4j window's height
	 */
	public int getScreenHeight() {
		return windowHeight;
	}

	/**
	 * Gets the screen width.
	 * 
	 * @return the screen width
	 * @deprecated renamed to getWindowWidth() since this doesent return the screen width, but the MT4j window's width
	 */
	public int getScreenWidth() {
		return windowWidth;
	}
	
	
	/**
	 * Gets the MT4j's window height.
	 * 
	 * @return the window height
	 */
	public int getWindowHeight() {
		return windowHeight;
	}

	/**
	 * Gets the MT4j's window width.
	 * 
	 * @return the window width
	 */
	public int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * Gets the screen center.
	 * 
	 * @return the screen center
	 * @deprecated - use getWindowCenter()
	 */
	public float[] getScreenCenter(){
		return new float[]{getWindowWidth()/2, getWindowHeight()/2 , 0};
	}
	
	/**
	 * Gets the window center.
	 *
	 * @return the window center
	 */
	public Vector3D getWindowCenter(){
		return new Vector3D (getWindowWidth()/2, getWindowHeight()/2 , 0);
	}
	
	/**
	 * Gets the renderer mode.
	 * 
	 * @return the renderer mode
	 */
	public int getRendererMode() {
		return renderer;
	}


	/**
	 * Gets the frame title.
	 * 
	 * @return the frame title
	 */
	public String getFrameTitle() {
		return frameTitle;
	}

	
	/**
	 * Gets the max frame rate.
	 * 
	 * @return the max frame rate
	 */
	public int getMaxFrameRate() {
		return maxFrameRate;
	}

	/**
	 * Gets the program start time.
	 * 
	 * @return the program start time
	 */
	public long getProgramStartTime() {
		return programStartTime;
	}


	/**
	 * Checks if is open gl mode.
	 * 
	 * @return true, if is open gl mode
	 */
	public boolean isOpenGlMode(){
		return this.getRendererMode() == MT4jSettings.OPENGL_MODE;
	}
	
	/**
	 * Checks if is p3d mode.
	 * 
	 * @return true, if is p3d mode
	 */
	public boolean isP3DMode(){
		return this.getRendererMode() == MT4jSettings.P3D_MODE;
	}


	/**
	 * Gets the architecture. (32/64 bit JVM)
	 *
	 * @return the architecture constant indicating architecture (32 or 64 bit)
	 */
	public int getArchitecture() {
		return this.architecture;
	}


	/**
	 * Checks if is vertical synchronization.
	 *
	 * @return true, if is vertical synchronization
	 */
	public boolean isVerticalSynchronization(){
		return this.vSync;
	}
	
	/**
	 * Gets the display.
	 *
	 * @return the display
	 */
	public int getDisplay() {
		return this.display;
	}


	/**
	 * Checks if is fullscreen exclusive.
	 *
	 * @return true, if is fullscreen exclusive
	 */
	public boolean isFullscreenExclusive() {
		return this.fullscreenExclusive;
	}
	

}
