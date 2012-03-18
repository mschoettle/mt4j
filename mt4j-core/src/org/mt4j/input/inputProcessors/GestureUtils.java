package org.mt4j.input.inputProcessors;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public class GestureUtils {
	
	////
	/**
	 * Gets the intersection point of a cursor and a specified component.
	 * Can return null if the cursor doesent intersect the component.
	 *
	 * @param app the app
	 * @param c the c
	 * @return the intersection
	 */
	public static Vector3D getIntersection(PApplet app, InputCursor c){
		return getIntersection(app, c.getTarget(), c);
	}
	
	/**
	 * Gets the intersection point of a cursor and a specified component.
	 * Can return null if the cursor doesent intersect the component.
	 *
	 * @param app the app
	 * @param component the component
	 * @param c the c
	 * @return the intersection
	 */
	public static Vector3D getIntersection(PApplet app, IMTComponent3D component, InputCursor c){
		//First check intersection with the specified component
		Vector3D ret = component.getIntersectionGlobal(Tools3D.getCameraPickRay(app, component, c));
		
		//Then if no intersection -> check with the current target of the cursor
		IMTComponent3D currentTarget = c.getCurrentEvent().getCurrentTarget();
		if (ret == null && currentTarget != component && currentTarget != null){
			ret = c.getCurrentEvent().getCurrentTarget().getIntersectionGlobal(Tools3D.getCameraPickRay(app, currentTarget, c));
		}
		return ret;
	}
	
	public static Vector3D getPlaneIntersection(PApplet app, Vector3D planeNormal, Vector3D pointInPlane, InputCursor c){
		Vector3D intersection = ToolsGeometry.getRayPlaneIntersection(
				Tools3D.getCameraPickRay(app, c.getTarget(), c.getCurrentEvtPosX(), c.getCurrentEvtPosY()), 
				planeNormal, 
				pointInPlane);
		
		IMTComponent3D currentTarget = c.getCurrentEvent().getCurrentTarget();
		if (intersection == null && currentTarget != c.getTarget() && currentTarget != null){
			intersection = ToolsGeometry.getRayPlaneIntersection(
					Tools3D.getCameraPickRay(app, currentTarget, c.getCurrentEvtPosX(), c.getCurrentEvtPosY()), 
					planeNormal, 
					pointInPlane);
		}
		return intersection;
	}
	
	
	
	

	/**
	 * Checks if the distance between a reference cursor and a cursor is greater than the distance to another cursor.
	 *
	 * @param reference the reference
	 * @param oldCursor the old cursor
	 * @param newCursor the new cursor
	 * @return true, if is cursor distance greater
	 */
	public static boolean isCursorDistanceGreater(InputCursor reference, InputCursor oldCursor, InputCursor newCursor){
//		float distanceToOldCursor = reference.getPosition().distance2D(oldCursor.getPosition());
//		float distanceToNewCursor = reference.getPosition().distance2D(newCursor.getPosition());
//		return distanceToNewCursor > distanceToOldCursor;
		return getDistance(reference, newCursor) > getDistance(reference, oldCursor);
	}
	
	/**
	 * Gets the distance between two cursors.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the distance
	 */
	public static float getDistance(InputCursor a, InputCursor b){
		return a.getPosition().distance2D(b.getPosition());
	}

}
