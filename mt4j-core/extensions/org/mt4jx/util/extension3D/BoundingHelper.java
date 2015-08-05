package org.mt4jx.util.extension3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundingSphere;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public class BoundingHelper {


	/**
	 * returns the most outer 3D Vectors 
	 * 
	 * @param shapes
	 * @return vector[0] = minX; [1] = minY; [2] = maxX ; [3] = maxY[4]
	 */
	//TODO add depth points too not only 2D Shape of boundingsphere
	public static final int LEFT_BOUNDING_POINT = 1;
	public static final int RIGHT_BOUNDING_POINT = 2;
	public static final int UPPER_BOUNDING_POINT = 3;
	public static final int LOWER_BOUNDING_POINT = 4;	
	
	public static Vector3D getOuterPointsOfBounding(ArrayList<IBoundingShape> shapes,float atDepth,int pointConstant,Icamera cam,PApplet pApplet)
	{
		Iterator<IBoundingShape> iterator = shapes.iterator();
		
		Vector3D[] outerPoints = new Vector3D[4];
		outerPoints[0] = new Vector3D(0.0f,0.0f,0.0f);//minX
		outerPoints[1] = new Vector3D(0.0f,0.0f,0.0f);//minY
		outerPoints[2] = new Vector3D(0.0f,0.0f,0.0f);//maxX
		outerPoints[3] = new Vector3D(0.0f,0.0f,0.0f);//maxY
		
		float minX=0.0f,maxX=0.0f;
		float minY=0.0f,maxY=0.0f;
		List<Vector3D> allVectors = new ArrayList<Vector3D>();
		
		while(iterator.hasNext())
		{			
			IBoundingShape shape = iterator.next();
					
			//Vector3D center = shape.getCenterPointGlobal();
			
			Vector3D[] globalVectors;
			
			if(shape instanceof BoundingSphere)
			{
				BoundingSphere sphere = ((BoundingSphere)shape);			
				globalVectors = sphere.getVectorsOnBoundingSphereGlobal(1);					
			}else
			{
				 globalVectors = shape.getVectorsGlobal();
			}			
			
			for(Vector3D vec : globalVectors)
			{
				Vector3D projVec = Tools3D.projectPointToPlaneInPerspectiveMode(vec, cam.getFrustum(), atDepth,(AbstractMTApplication)pApplet);
				allVectors.add(projVec);
			}			
		}
		
		for(int i=0;i<allVectors.size();i++)
		{
			Vector3D vec = allVectors.get(i);
											
			if(vec.x>maxX)
			{
				maxX = vec.x;
				outerPoints[2].x = vec.x;
				outerPoints[2].y = vec.y;	
				outerPoints[2].z = vec.z;
				
			}else if(vec.x<minX)
			{
				minX = vec.x;
				outerPoints[0].x = vec.x;
				outerPoints[0].y = vec.y;
				outerPoints[0].z = vec.z;
				
			}
			
			if(vec.y>maxY)
			{
				maxY = vec.y;
				outerPoints[3].x = vec.x;
				outerPoints[3].y = vec.y;
				outerPoints[3].z = vec.z;
				
			}else if(vec.y<minY)
			{
				minY = vec.y;
				outerPoints[1].x = vec.x;
				outerPoints[1].y = vec.y;
				outerPoints[1].z = vec.z;
				
			}				
		}
		
		switch(pointConstant)
		{
		case BoundingHelper.LEFT_BOUNDING_POINT:
			return outerPoints[0];			
		case BoundingHelper.RIGHT_BOUNDING_POINT:
			return outerPoints[2];			
		case BoundingHelper.UPPER_BOUNDING_POINT:
			return outerPoints[3];			
		case BoundingHelper.LOWER_BOUNDING_POINT:
			return outerPoints[1];
		}
		
		return null;
			
	}
	
	/**
	 * This method is to get the bounding shapes of a group of several
	 * components by a recursive walkthrough
	 * 
	 * @param component
	 * @return
	 */
	public static ArrayList<IBoundingShape> getBoundingShapes(MTComponent component) {
		ArrayList<IBoundingShape> shapes = new ArrayList<IBoundingShape>();

		if (component.getChildCount() != 0) {
			for (MTComponent ch : component.getChildren()) {
				shapes.addAll(getBoundingShapes(ch));
			}
		}

		if (component instanceof AbstractShape) {		
			if(((AbstractShape)component).getBounds()!=null)
			{
				shapes.add(((AbstractShape) component).getBounds());
			}
		}

		return shapes;
	}
	
	
	
}
