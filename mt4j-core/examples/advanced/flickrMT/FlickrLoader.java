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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.mt4j.components.visibleComponents.widgets.progressBar.AbstractProgressThread;
import org.xml.sax.SAXException;

import processing.core.PApplet;
import processing.core.PImage;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.photos.GeoData;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.photos.geo.GeoInterface;
import com.aetrion.flickr.places.Place;
import com.aetrion.flickr.places.PlacesInterface;
import com.aetrion.flickr.places.PlacesList;

/**
 * The Class FlickrLoader.
 */
public class FlickrLoader extends AbstractProgressThread {
	/** The images. */
	private PImage[] images;
	
	/** The pa. */
	private PApplet pa;
	
	/** The use places for geo search. */
	private boolean usePlacesForGeoSearch;
	
//	private ImageCard mtFotos[];
	
	
//	private FlickrHelper flickHelper;
	
	/** The search parameters. */
private SearchParameters searchParameters;
	
	/** The foto count. */
	private int fotoCount;
	
	/** The search page offset. */
	private int searchPageOffset;
	
	/** The flickr key. */
	private String flickrKey;
	
	/** The flickr secret. */
	private String flickrSecret;
	
	// A Flickr object for making requests
	/** The f. */
	private Flickr f;
	
	/** The auth interface. */
	private AuthInterface authInterface;
	
	/** The request context. */
	private RequestContext requestContext;
	
	/** The photos. */
	private List<Photo> photos;
	
	/**
	 * The Constructor.
	 * 
	 * @param pa the pa
	 * @param flickrKey the flickr key
	 * @param flickrSecret the flickr secret
	 * @param sleepTime the sleep time
	 */
	public FlickrLoader(PApplet pa, String flickrKey, String flickrSecret, long sleepTime) {
		this(pa, flickrKey, flickrSecret, new String[]{}, sleepTime);
	}
	
	
	/**
	 * The Constructor.
	 * 
	 * @param pa the pa
	 * @param flickrKey the flickr key
	 * @param flickrSecret the flickr secret
	 * @param photoSearchString the photo search string
	 * @param sleepTime the sleep time
	 */
	public FlickrLoader(PApplet pa, String flickrKey, String flickrSecret, String photoSearchString, long sleepTime) {
		this(pa, flickrKey, flickrSecret, new String[]{photoSearchString}, sleepTime);
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param pa the pa
	 * @param flickrKey the flickr key
	 * @param flickrSecret the flickr secret
	 * @param photoSearchString the photo search string
	 * @param sleepTime the sleep time
	 */
	public FlickrLoader(PApplet pa, String flickrKey, String flickrSecret, String[] photoSearchString, long sleepTime) {
		this(pa, flickrKey, flickrSecret, new SearchParameters(), sleepTime);
		
		SearchParameters sp = new SearchParameters();
		// Simple example, just looking for a single tag
		sp.setTags(photoSearchString);
		this.setSearchParameters(sp);
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param pa the pa
	 * @param flickrKey the flickr key
	 * @param flickrSecret the flickr secret
	 * @param sleepTime the sleep time
	 * @param sp the sp
	 */
	public FlickrLoader(PApplet pa, String flickrKey, String flickrSecret, SearchParameters sp, long sleepTime) {
		super(sleepTime);
		this.pa = pa;
		
		try {
			f = new Flickr(flickrKey, flickrSecret, new REST());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		authInterface	= f.getAuthInterface();
		requestContext 	= RequestContext.getRequestContext();
		
		this.searchParameters 	= sp;
		this.fotoCount 			= 5;
		this.searchPageOffset 	= 0;
		
		this.photos = new ArrayList<Photo>();
		
		this.usePlacesForGeoSearch = true;
	}

	
	/* (non-Javadoc)
	 * @see com.jMT.components.visibleComponents.progressBar.AbstractProgressThread#run()
	 */
	@Override
	public void run() {
		if (this.getSearchParameters() != null){
			
			/*
			//Print search tags
			System.out.print("\nSearching for flickr tags: " );
			String[] tags = this.getSearchParameters().getTags();
			if (tags != null){
				for (int i = 0; i < tags.length; i++) {
					String string = tags[i];
					System.out.print(string + ", ");
				}
				System.out.println();
			}
			*/
			
			boolean isGeoSearch = this.isGeoSearch(this.getSearchParameters());
			PhotoList fotoList = this.getSearchedFotoList(this.getSearchParameters(), this.getFotoLoadCount(), this.getSearchPageOffset(), isGeoSearch);
			
			if (fotoList != null && fotoList.size() > 0){
//				System.out.println("Found " + fotoList.size() + " fotos.");
				
				this.setTarget(fotoList.size());
//				mtFotos = new ImageCard[fotoList.size()];
				
				//Go through all found fotos
				for (int i = 0; i < fotoList.size(); i++) {
					
					try {
						Thread.sleep(this.getSleepTime()); 
					} catch (InterruptedException e) {
						e.printStackTrace();
						//pa.unregisterPre(this);
						this.setFinished(true);
						break;
					}
					
					Photo foto = (Photo) fotoList.get(i);
					String fotoName = foto.getTitle();
					
					/*
					if (foto.hasGeoData()){
						System.out.println("Foto: " + fotoName + " has geodata.");
						System.out.println(fotoName + "-> Lat:" + foto.getGeoData().getLatitude() + " Lon:" + foto.getGeoData().getLongitude() + " PlaceID: " + foto.getPlaceId());
					}
					*/
					
					this.setCurrentAction("Loading: " + fotoName);
					
					this.processFoto(foto);

					this.setCurrent(i+1);
				}
			}else{
				System.err.println("Foto list returned null or list is empty!");
				//Fire event that all loaded
				this.setFinished(true);
			}
		}//if sp != null
		else{
			System.err.println("No search parameters for flickr search specified!");
			//Fire event that all loaded
			this.setFinished(true);
		}
//		this.setFinished(true);
	}
	
	
	/**
	 * This method gets called in the loading thread of the fotos. This
	 * method can be overridden and used to process the newly loaded foto in different ways.
	 * 
	 * @param foto the foto
	 */
	protected void processFoto(Photo foto){
	}

	
	/**
	 * Searches fotos on flickr with the specified searchparameters.
	 * 
	 * @param sp the sp
	 * @param n number of fotos to search for
	 * @param startPage offset of the result pages to start from
	 * @param isGeoSearch the is geo search
	 * 
	 * @return the searched foto list
	 * 
	 * the list of found fotos
	 */
	public PhotoList getSearchedFotoList(SearchParameters sp, int n, int startPage, boolean isGeoSearch) {
		PhotoList photoList = null;
		//Interface with Flickr photos
		PhotosInterface photoInterface = f.getPhotosInterface();
		
		try {
			//TODO was kann man damit machen?
			//GeoInterface g = photos.getGeoInterface();
			//search results for exact location, display on map
			
			/*
			//Search in a location bounding box
			sp.setBBox(
					"16.3680536232", "48.2057162608", 
					"16.3760536232", "48.2111162608");
			sp.setAccuracy(1);
			sp.setHasGeo(true);
			*/
			
			//Search in a location with a radius
			/*
			sp.setLatitude("48.7771056");
			sp.setLongitude("9.1807688");
			sp.setRadius(5);
			sp.setRadiusUnits("km");
			sp.setHasGeo(true);
			sp.setAccuracy(Flickr.ACCURACY_REGION);
			*/
			
			
//			/*
			//Instead of taking the location, search for a nearby flickr "place"
			if (isGeoSearch && sp.getLongitude() != null && sp.getLatitude() != null
				&& usePlacesForGeoSearch
			){
				//TODO TEST DISABLE FIXME
				//Search for places at a location
				PlacesInterface p = f.getPlacesInterface();
				PlacesList placesList = null;
//				placesList = p.findByLatLon(48.7771056f, 	9.1807688f, 14);
//				placesList = p.findByLatLon(40.689, -74.044, 12); //liberty islang
				placesList = p.findByLatLon(Double.parseDouble(sp.getLatitude()), Double.parseDouble(sp.getLongitude()), sp.getAccuracy());

				sp.setLatitude(null);
				sp.setLongitude(null);
				
				System.out.println("Places found: " + placesList.size());
                for (Object aPlacesList : placesList) {
                    Place place = (Place) aPlacesList;
                    String placeID = place.getPlaceId();
                    //System.out.println("Place ID: " + placeID);

                    sp.setPlaceId(placeID);
                    sp.setWoeId(place.getWoeId());

//					Location placeResolved = p.resolvePlaceURL(place.getPlaceUrl());
//					System.out.println("Place: " + placeResolved);

//					sp.setBBox(
//					48.5129f, 2.1740f, 
//					48.5130f, 2.1741f);
                }
			}
//			else if (isGeoSearch && sp.getLongitude() != null && sp.getLatitude() != null
//					&& !usePlacesForGeoSearch){
//				
//			}
//			*/
			
			//We're looking for n images, starting at "page" startPage
			//PhotoList 
			photoList = photoInterface.search(sp, n, startPage);
			
			//Check if no fotos were found and search again with reduced accurracy
			if (isGeoSearch
				&&	photoList 		!= null
				){
				System.out.println("Found " + photoList.size() + " fotos.");
				
				if (photoList.size() <= 0){
					System.out.println("Found no fotos, reducing accuracy and trying again.");
					int a = sp.getAccuracy();
					
					for (int i = 1; i < 6; i++) {
						a--;
						if (a < 1){
							a++;
							break;
						}
					}
					
					System.out.println("Using new accuracy: " + a);
					sp.setAccuracy(a);
					photoList = photoInterface.search(sp, n, startPage);
					if (photoList != null)
						System.out.println("Found " + photoList.size() + " fotos.");
				}
			}
			
			if (isGeoSearch){
				//System.out.println("Trying to attach geo information:");
				if (photoList != null){
					GeoInterface g = photoInterface.getGeoInterface();
					
					//Nullpointer error - bug in flickj library,
					//photosForLocation return aber evtl nur eigene fotos!
//					PhotoList fot = g.photosForLocation(new GeoData( new Float(48.7771056f).toString(), new Float(9.1807688f).toString(), new Integer(1).toString()), null, 5, 0);
//					System.out.println("photosForLocation (location = stutgart mitte) returned fotos: " +  fot.size());
					
					//Go through all found fotos
                    for (Object aPhotoList : photoList) {
                        Photo foto = (Photo) aPhotoList;
                        //Add to result list
                        photos.add(foto);

                        String id = foto.getId();
                        try {
                            GeoData loc = g.getLocation(id);
                            if (loc != null) {
                                foto.setGeoData(loc);
                            }
                        } catch (Exception e) {
                            System.err.println("Error fetching geodata for foto");
                            e.printStackTrace();
                        }
                    }
				}
			}else{
				//Add to result list
				if (photoList != null){
                    for (Object aPhotoList : photoList) {
                        photos.add((Photo) aPhotoList);
                    }
				}
			}
			
			//Geht nur mit eigenen fotos!?
//			photos.getWithGeoData(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);

			return photoList;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FlickrException e) {
			e.printStackTrace();
		}
		return photoList;
	}
	
	
	/**
	 * Checks if is geo search.
	 * 
	 * @param sp the sp
	 * 
	 * @return true, if is geo search
	 */
	private boolean isGeoSearch(SearchParameters sp){
		return
		(
			   (sp.getLatitude() != null //TODO is it null when not set?
			&&	!sp.getLatitude().equalsIgnoreCase(""))
			||
				(sp.getLongitude() != null
			&& 	!sp.getLongitude().equalsIgnoreCase(""))
			||
				(sp.getBBox() != null
			&&	 sp.getBBox().length > 0)
			||
			(sp.getWoeId() != null
			&& 	!sp.getWoeId().equalsIgnoreCase(""))
			||
			(sp.getPlaceId() != null
			&& 	!sp.getPlaceId().equalsIgnoreCase(""))
			|| sp.getRadius() != -1
		);
	}
	
	/**
	 * Gets the foto urls.
	 * 
	 * @param list the list
	 * 
	 * @return the foto urls
	 */
	private String[] getFotoUrls(PhotoList list){
		//Grab all the image paths and store in String array
		String[] smallURLs = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Photo p = (Photo) list.get(i);
//			smallURLs[i] = p.getSmallUrl();
//			smallURLs[i] = p.getUrl();
			smallURLs[i] = p.getMediumUrl();
		}
		return smallURLs;
	}
	
	
	
	/**
	 * Gets the foto load count.
	 * 
	 * @return the foto load count
	 */
	public int getFotoLoadCount() {
		return fotoCount;
	}
	
	/**
	 * Sets the foto load count.
	 * 
	 * @param fotosCount the new foto load count
	 */
	public void setFotoLoadCount(int fotosCount) {
		this.fotoCount = fotosCount;
	}

	
	/**
	 * Gets the search parameters.
	 * 
	 * @return the search parameters
	 */
	public SearchParameters getSearchParameters() {
		return searchParameters;
	}
	
	/**
	 * Sets the search parameters.
	 * 
	 * @param searchParameters the new search parameters
	 */
	public void setSearchParameters(SearchParameters searchParameters) {
		this.searchParameters = searchParameters;
	}
	
	
	/**
	 * Gets the search page offset.
	 * 
	 * @return the search page offset
	 */
	private int getSearchPageOffset() {
		return this.searchPageOffset;
	}
	
	/**
	 * Sets the search page offset.
	 * 
	 * @param searchPageOffset the new search page offset
	 */
	public void setSearchPageOffset(int searchPageOffset) {
		this.searchPageOffset = searchPageOffset;
	}

	
	/**
	 * Gets the flickr key.
	 * 
	 * @return the flickr key
	 */
	public String getFlickrKey() {
		return flickrKey;
	}
	
	/**
	 * Sets the flickr key.
	 * 
	 * @param flickrKey the new flickr key
	 */
	public void setFlickrKey(String flickrKey) {
		this.flickrKey = flickrKey;
	}


	/**
	 * Gets the flickr secret.
	 * 
	 * @return the flickr secret
	 */
	public String getFlickrSecret() {
		return flickrSecret;
	}
	
	/**
	 * Sets the flickr secret.
	 * 
	 * @param flickrSecret the new flickr secret
	 */
	public void setFlickrSecret(String flickrSecret) {
		this.flickrSecret = flickrSecret;
	}
	

	/**
	 * Gets the photos.
	 * 
	 * @return the photos
	 */
	public Photo[] getPhotos() {
		return this.photos.toArray(new Photo[this.photos.size()]);
	}


	/**
	 * Checks if is use places for geo search.
	 * 
	 * @return true, if is use places for geo search
	 */
	public boolean isUsePlacesForGeoSearch() {
		return usePlacesForGeoSearch;
	}


	/**
	 * Uses flickr "places" for the geo search
	 * 
	 * @param usePlacesForGeoSearch the new use places for geo search
	 */
	public void setUsePlacesForGeoSearch(boolean usePlacesForGeoSearch) {
		this.usePlacesForGeoSearch = usePlacesForGeoSearch;
	}
	

	



}
