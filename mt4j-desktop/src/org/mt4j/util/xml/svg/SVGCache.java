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
package org.mt4j.util.xml.svg;

import java.io.File;

//TODO IMPLEMENT!

import org.mt4j.components.MTComponent;

import processing.core.PApplet;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

/**
 * The Class SVGCache.
 */
public class SVGCache {
	
	/** The svg cache. */
	private static SVGCache svgCache;
	
	/** The cache. */
	private Cache cache;

	/**
	 * Gets the single instance of SVGCache.
	 * 
	 * @return single instance of SVGCache
	 */
	public static SVGCache getInstance(){
		if (svgCache == null){
			svgCache = new SVGCache();
			return svgCache;
		}else{
			return svgCache;
		}
	}
	
	/**
	 * Instantiates a new sVG cache.
	 */
	private SVGCache(){
		try {
			cache = CacheManager.getInstance().getCache();
		} catch (CacheException e) {
			e.printStackTrace();
		}
		
//			Shut down the cache manager
//			CacheManager.getInstance().shutdown();
	}
	
	
	//TODO wenn in cache, ganze svg gruppenhierarchie clonen 
	// und clon zurückgeben
	/**
	 * Load svg file.
	 * 
	 * @param fileName the file name
	 * @param pa the pa
	 * 
	 * @return the mT base component
	 */
	public MTComponent loadSVGFile(String fileName, PApplet pa){
		MTComponent returnComponent = null;
		
		//Get the object back out of the cache
		returnComponent = (MTComponent) cache.retrieve(fileName);
		
		if (returnComponent == null){
			System.out.println("Found no cached obj for filepath: " + fileName);
			if (new File(fileName).exists()){
//				BatikSvgParser batikSvgParser = new BatikSvgParser(pa);
//				SVGDocument svgDoc = batikSvgParser.parseSvg(fileName);
//				MTBaseComponent[] comps = batikSvgParser.getCreatedSvgComponents(svgDoc);
//				MTBaseComponent group = new MTBaseComponent(pa);
//				//Wrap the svg in a group
//				group.setName("svg: " + fileName);
//				group.addChildren(comps);
				
				SVGLoader batikSvgParser = new SVGLoader(pa);
				MTComponent svg = batikSvgParser.loadSvg(fileName);
				
				returnComponent = svg;
				
				//TODO store copy? because sonst wird matrix etc verändert wenn damit gearbeitet wurde
				cache.store(fileName, svg);
			}else{
				System.out.println("File doesent exist! aborting..." + fileName);
			}
		}else{
			System.out.println("Found cached svg.");
		}
		
		return returnComponent;
	}
	

	//TODO für alle componenten clone implementieren?
	/**
	 * Copy svg.
	 * 
	 * @param original the original
	 * 
	 * @return the mT base component
	 */
	private MTComponent copySvg(MTComponent original){
		
		return null;
	}
	
	
	/**
	 * Copy svg recursive.
	 * 
	 * @param current the current
	 */
	private void copySvgRecursive(MTComponent current){
		
		
		
	}
	
	
	

}
