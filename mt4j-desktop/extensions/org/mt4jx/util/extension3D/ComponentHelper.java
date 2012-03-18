package org.mt4jx.util.extension3D;

import org.mt4j.components.MTComponent;
import org.mt4j.components.PickInfo;
import org.mt4j.components.PickResult;
import org.mt4j.components.PickResult.PickEntry;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public class ComponentHelper {
	
	/**
	 * this is a different version of pickRecursive of MTComponent used to get the correct object when dealing with the Cluster 3D techniques
	 * Checks which object lies under the specified screen coordinates.
	 * The the results are stored in the returned PickResult object. This component and
	 * its children will be checked. 
	 * 
	 * @param x the x
	 * @param y the y
	 * @param onlyPickables check the only pickable components
	 * 
	 * @return the pick result
	 */
	public static PickResult pick(MTComponent comp,float x, float y, boolean onlyPickables){ //ADDTOMT4J change name
		PickResult pickResult = new PickResult();
		PickInfo pickInfo = new PickInfo(x,y, Tools3D.getCameraPickRay(comp.getRenderer(), comp, x, y));
		pickRecursive(comp,pickInfo, pickResult, Float.MAX_VALUE, pickInfo.getPickRay(), onlyPickables);
//		pickResult.printList();
		return pickResult;
	}
	
	/**
	 * this is a different version of pickRecursive of MTComponent used to get the correct object when dealing with the Cluster 3D techniques
	 * @param comp
	 * @param pickInfo
	 * @param pickResult
	 * @param currObjDist
	 * @param currentRay
	 * @param onlyPickables
	 * @return
	 */
	private static float pickRecursive(MTComponent comp,PickInfo pickInfo, PickResult pickResult, float currObjDist, Ray currentRay, boolean onlyPickables){//ADDTOMT4J change name
		Vector3D interSP	= null;
		float objDistance 	= 0;
		//TEST, Wenns probleme gibt das wieder aktivieren
//		currObjDist = pickResult.getDistanceNearestPickObj();
		
//		System.out.println("At: " + comp.getName() + " Current Distance: " + currObjDist);
		if (comp.isVisible() && 
			((onlyPickables && comp.isPickable()) || !onlyPickables) 
		){
			//Get the real ray for comp obj, takes the viewing camera and viewport of comp obj into account
			//-> changes rayStartPoint and point in ray direction
			if (comp.getAttachedCamera() != null){
				currentRay	= getChangedCameraPickRay(comp.getRenderer(), comp, pickInfo);
			}
			
			Ray invertedRay;
			if (comp.getGlobalInverseMatrix().isIdentity()){
				invertedRay = currentRay;
			}else{
				invertedRay = comp.globalToLocal(currentRay);
			}
			
			/*
			//FIXME REMOVE!!!!! 
			//comp adds lines indicating the world ray and the local object ray used for ray-test
			MTLine l1 = new MTLine(comp.getRenderer(), new Vertex(currentRay.getRayStartPoint()), new Vertex(currentRay.getPointInRayDirection()));
			comp.getAncestor().addChild(l1);
			MTLine l2 = new MTLine(comp.getRenderer(), new Vertex(invertedRay.getRayStartPoint()), new Vertex(invertedRay.getPointInRayDirection()));
			l2.setStrokeColor(255, 10, 10, 255);
			comp.getAncestor().addChild(l2);
			*/
			
			//Check if component is clipped and only proceed if the ray intersects the clip shape
			Clip clip = comp.getClip();
			if (clip == null || (clip != null && clip.getClipShapeIntersectionLocal(invertedRay) != null)){
				interSP = comp.getIntersectionLocal(invertedRay);
				if (interSP != null){
					//FIXME TRIAL - muss für die distance messung der world ray genommen
					//werden oder geht der invertierte ray? -> musss wohl der world ray sein
					interSP.transform(comp.getGlobalMatrix());
					// Get distance from raystart to the intersecting point
					objDistance = interSP.getSubtracted(currentRay.getRayStartPoint()).length();
					//System.out.println("Pick found: " + comp.getName() + " InterSP: " + interSP +  " ObjDist: " + objDistance +  " Mouse Pos: " + pickInfo.getScreenXCoordinate() + "," + pickInfo.getScreenYCoordinate() + " InvRay RS:" + invertedRay.getRayStartPoint() + ",RE: " + invertedRay.getPointInRayDirection());

//					//If the distance is the smallest yet = closest to the raystart: replace the returnObject and current distanceFrom
//					if ( (objDistance - HIT_TOLERANCE) <= currObjDist /*|| comp.isAlwaysDrawnOnTop()*/){//take isDrawnOnTop into account here?? -> OBJDistance auf 0 setzen?
//					currObjDist = objDistance;
//					pickResult.addPickedObject(comp, interSP, objDistance);
////					System.out.println("-> Now nearest: " + comp.getName());
//					}

					//FIXME TEST - ADD ALL PICKED OBJECTS - SORT LATER
					pickResult.addPickedObject(comp, interSP, objDistance);
				}
			}
			
			//Check for child clipping shape intersection, if not intersecting -> dont try to pick children
			Clip childClip = comp.getChildClip();
			if (childClip != null && childClip.getClipShapeIntersectionLocal(invertedRay) == null){
				return currObjDist;
			}
		}
		
		/* recursively check all children now */
		MTComponent[] childComponents = comp.getChildren();
		for (int i = 0; i < childComponents.length; i++) {
			MTComponent child = childComponents[i];
			if (child.isVisible()) { 
				if (comp.isComposite()){					
					//Start a new picking with a new Pickresult obj from here
					PickResult compositePickRes = new PickResult();
					float compDistance = pickRecursive(child,pickInfo, compositePickRes, Float.MAX_VALUE, currentRay, onlyPickables);

					//Add the composites picks to the overall picks
					if (compositePickRes.getNearestPickResult() != null){
//						System.out.println("In: " + comp.getName() + " Composites child picked, pick resultDistance: " + compDistance);
						/*//TODO müsste diese hier nach distanz geordnet in insgesamt pickresult einfügen..
						ArrayList<MTBaseComponent> pickList = compositePickRes.getPickList();
						for(MTBaseComponent comp : pickList){
							pickResult.addPickedObject(comp, compositePickRes.getInterSectionPointOfPickedObj(comp), compositePickRes.getDistanceOfPickedObj(comp));
						}  
						*/ 
						//Add comp composite as the last one picked with the distance of the last one picked in the composite pick
//						pickResult.addPickedObjects(compositePickRes.getPickList());
//						pickResult.addPickedObject(comp, compositePickRes.getInterSectionPointNearestPickedObj(), compositePickRes.getDistanceNearestPickObj());
						
//						if (//compDistance <= currObjDist 
//							(compDistance - HIT_TOLERANCE) <= currObjDist
//						){
////							System.out.println("Composites child picked and now nearest: " + comp.getName()+ " dist: " + compDistance);
//							pickResult.addPickedObject(comp, compositePickRes.getInterSectionPointNearestPickedObj(), compositePickRes.getDistanceNearestPickObj());
//							currObjDist = compDistance;
//						}
						
						//FIXME TEST - ADD ALL PICKED OBJECTS - SORT LATER
						PickEntry nearestPickEntry = compositePickRes.getNearestPickEntry();						
						pickResult.addPickedObject(nearestPickEntry.hitObj, nearestPickEntry.intersectionPoint, nearestPickEntry.cameraDistance);
						
					}
				}else{
					currObjDist = pickRecursive(child,pickInfo, pickResult, currObjDist, currentRay, onlyPickables);
				}
			}
		}
		
		return currObjDist;
	}
	
	/**
	 * Calculates the "real" picking ray for the object.
	 * <br>If the obj has a custom camera attached to it, this camera's position is the new ray origin and
	 * the point in the ray direction is the unprojected x,y, coordinates while this camera is active.
	 * 
	 * @param pa the papplet
	 * @param obj the obj
	 * @param ray the ray
	 * 
	 * @return the real pick ray
	 * 
	 * the new calculated ray, or the original ray, if the obj has no custom camera attached to it.
	 */
	private static Ray getChangedCameraPickRay(PApplet pa, IMTComponent3D obj, Ray ray){
		Vector3D pointInRayDirection = ray.getPointInRayDirection();
		Vector3D projected = Tools3D.project(pa, obj.getViewingCamera(), pointInRayDirection);
//		Vector3D projected = Tools3D.project(pa, pointInRayDirection);
		return getChangedCameraPickRay(pa, obj, new PickInfo(
				projected.x,
				projected.y,
				ray));
	}
	
	/**
	 * Calculates the "real" pickray for the object.
	 * <br>If the obj has a custom camera attached to it, this cameras position is the new ray origin and
	 * the point in the ray direction is the unprojected x,y, coordinates while this camera is active.
	 * 
	 * @param pa the pa
	 * @param obj the obj
	 * @param pickInfo the pick info
	 * @return the real pick ray
	 * 
	 * the new calculated ray, or the original ray, if the obj has no custom camera attached to it.
	 */
	private static Ray getChangedCameraPickRay(PApplet pa, IMTComponent3D obj, PickInfo pickInfo){
		if (obj.getViewingCamera() != null){ //FIXME TEST
			//Re-Project unprojected world coords to projected viewport screen coords (Tuio INput)
			float x = pickInfo.getScreenXCoordinate(); 
			float y = pickInfo.getScreenYCoordinate(); 
			
			return Tools3D.getCameraPickRay(pa, obj, x, y);
		}else{
			return pickInfo.getPickRay();
		}
		/*//FIXME disabled for performance for now!
		if (obj.hasCustomViewPort()){
			//Take VIEWPORT changes into account, too
			ViewportSetting customViewPort 		   = obj.getCustomViewportSetting();
			ViewportSetting defaultViewPortSetting = obj.getDefaultViewportSetting();
			rayStartPoint.setX(customViewPort.getStartX() + (rayStartPoint.getX() * (customViewPort.getWidth()/defaultViewPortSetting.getWidth())));
			rayStartPoint.setY(customViewPort.getStartY() + (rayStartPoint.getY() * (customViewPort.getHeight()/defaultViewPortSetting.getHeight())));
			pointInRayDirection.setX(customViewPort.getStartX() + (pointInRayDirection.getX() * (customViewPort.getWidth()/defaultViewPortSetting.getWidth())));
			pointInRayDirection.setY(customViewPort.getStartY() + (pointInRayDirection.getY() * (customViewPort.getHeight()/defaultViewPortSetting.getHeight())));
			/////
		}
		 */
//		return new Ray(rayStartPoint, pointInRayDirection);
	}
	
	/* (non-Javadoc)
	 * this is a different version of getIntersectionGlobal of MTComponent used to deal with collision engine
	 * @see org.mt4j.components.interfaces.IMTComponent3D#getIntersectionGlobalSpace(util.math.Ray)
	 */
	public static Vector3D getIntersectionGlobal(MTComponent comp,Ray ray){ //ADDTOMT4J Chris fragen, wegen pickable {
		float currentDistance = Float.MAX_VALUE; //high value so that the first time a object is found comp distance is exchanged with his
		float objDistance 		= 0;
		Vector3D returnPoint 	= null;
		Vector3D interSP 		= null;
		
		if (comp.isVisible()) { 
			//Get the real ray for comp obj, takes the custom camera and viewport of comp obj into account
			//-> changes rayStartPoint and point in ray direction
			if (comp.getAttachedCamera() != null){
				ray	= getChangedCameraPickRay(comp.getRenderer(), comp, ray);
			}
			
			//Transforms the ray into local object space 
			Ray invertedRay = comp.globalToLocal(ray);
			
			//Check if component is clipped and only proceed if the ray intersects the clip shape
			Clip clip = comp.getClip();
			if (clip == null || (clip != null && clip.getClipShapeIntersectionLocal(invertedRay) != null)){
				interSP = comp.getIntersectionLocal(invertedRay);
				if (interSP != null){
					//FIXME TRIAL - muss für die distance messung der world ray genommen
					//werden oder geht der invertierte ray?
					interSP.transform(comp.getGlobalMatrix());
					//Get distance from raystart to the intersecting point
					objDistance = interSP.getSubtracted(ray.getRayStartPoint()).length();

					//If the distance is the smalles yet = closest to the raystart replace the returnObject and current distanceFrom
					if ((objDistance - PickResult.HIT_TOLERANCE) < currentDistance ){
						returnPoint = interSP;
						currentDistance = objDistance;
					}
				}
			}

			//Check for child clip intersection, if not intersecting, dont try to pick children
			Clip childClip = comp.getChildClip();
			if (childClip != null && childClip.getClipShapeIntersectionLocal(invertedRay) == null){
				return returnPoint;
			}
			
	}
		
		/* Go through all Children */
//		for (int i = childComponents.size()-1; i >= 0; i--) {
		MTComponent[] childComponents = comp.getChildren();
		for (int i = 0; i < childComponents.length; i++) {	
			MTComponent child = childComponents[i];
				//Get the intersectionpoint ray/object if there is one
				interSP = getIntersectionGlobal(child,ray);
				
				if (interSP != null ){ //if ray intersects object at a point
					//System.out.println("Intersection at: " + interSP);
					//Get distance from raystart to the intersecting point
					objDistance = interSP.getSubtracted(ray.getRayStartPoint()).length();
					//If the distance is the smalles yet = closest to the raystart replace the returnObject and current distanceFrom
					if (objDistance < currentDistance ){
						returnPoint = interSP;
						currentDistance = objDistance;
					}
				}//if intersection!=null
		}// for
		return returnPoint;
	}
	

	public static Vector3D getCenterPointGlobal(MTComponent comp) {
		
		MTComponent[] children = comp.getChildren();
		if(children.length==0)
		{
			if(comp.hasBounds())
			{
				return comp.getBounds().getCenterPointGlobal();
			}else
			{
				return null;
			}
		}else
		{
			//float massSum = 0.0f;
			Vector3D vecSum = new Vector3D();
			for(MTComponent compChild : children)
			{	
				if(getCenterPointGlobal(compChild)!=null)
				{
					Vector3D vec = getCenterPointGlobal(compChild);					
					vecSum.addLocal(vec);
					
					//massSum += compChild.getMass();
					
				}
			}			
			
			return vecSum.getScaled(1.f/children.length);
		}	
	}
	
}
