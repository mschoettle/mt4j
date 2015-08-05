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
package advanced.flickrMT;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.visibleComponents.widgets.MTImage;

import processing.core.PApplet;

import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.SearchParameters;

/**
 * The Class FlickrMTFotoLoader.
 */
public class FlickrMTFotoLoader extends FlickrLoader {
	
	/** The mt fotos. */
	private List<MTImage> mtFotos;
	
	/** The pa. */
	private PApplet pa;
	
	private boolean getHighResolution;
	
	/**
	 * Instantiates a new flickr mt foto loader.
	 * 
	 * @param pa the pa
	 * @param flickrKey the flickr key
	 * @param flickrSecret the flickr secret
	 * @param sp the sp
	 * @param sleepTime the sleep time
	 */
	public FlickrMTFotoLoader(PApplet pa, String flickrKey,
			String flickrSecret, SearchParameters sp, long sleepTime) {
		super(pa, flickrKey, flickrSecret, sp, sleepTime);
		
		this.pa = pa;
		mtFotos = new ArrayList<MTImage>();
		
		this.getHighResolution = true;
	}

	/* (non-Javadoc)
	 * @see util.FlickrLoader#processFoto(com.aetrion.flickr.photos.Photo)
	 */
	protected void processFoto(Photo foto){
		String fotoUrl;
		if (this.isGetHighResolution()){
//			fotoUrl = foto.getUrl();
			fotoUrl = foto.getMediumUrl();
		}else{
			fotoUrl = foto.getSmallUrl();
		}
//		String fotoUrl = foto.getUrl();
//		String fotoUrl = foto.getSmallUrl();
//		String fotoUrl = foto.getMediumUrl();
		
		String fotoName = foto.getTitle();
		
		//Create image object
		MTImage photo = new MTImage(pa, pa.loadImage(fotoUrl));
		photo.setName(fotoName);
//		mtFotos[i] = photo;
		mtFotos.add(photo);
	}
	
	/**
	 * Returns the created scenegraph images.
	 * 
	 * @return the mt fotos
	 */
	public MTImage[] getMtFotos() {
		return this.mtFotos.toArray(new MTImage[this.mtFotos.size()]);
	}

	public boolean isGetHighResolution() {
		return getHighResolution;
	}

	public void setGetHighResolution(boolean getHighResolution) {
		this.getHighResolution = getHighResolution;
	}
	
	
	
	
}
