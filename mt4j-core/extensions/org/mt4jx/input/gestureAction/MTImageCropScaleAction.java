/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4jx.input.gestureAction;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PImage;

/**
 * This gesture listener can be used in conjunction with the ScaleProcessor. It allows to define a 
 * rectangular area using 2 fingers, inside another MTRectangle. It will then crop the MTRectangle
 * to the defined size.
 *
 * @author Christopher Ruff
 */
public class MTImageCropScaleAction implements IGestureEventListener{
		private MTRectangle shape;
		private MTLine l1 ;
		private MTLine l2 ;
		private MTLine l3 ;
		private MTLine l4 ;
		
		public MTImageCropScaleAction(MTRectangle shape) {
			this.shape = shape;
		}

		public boolean processGestureEvent(MTGestureEvent ge) {
			ScaleEvent se = (ScaleEvent)ge;
			if (se.getId() == MTGestureEvent.GESTURE_STARTED){
				l1 = null; l2 = null; l3 = null; l4 = null;
				shape.setAnchor(PositionAnchor.UPPER_LEFT); //FIXME TEST
			}
			Vector3D firstCursorIntersection = shape.getIntersectionGlobal(Tools3D.getCameraPickRay(shape.getRenderer(), shape, se.getFirstCursor()));
			Vector3D secondCursorIntersection = shape.getIntersectionGlobal(Tools3D.getCameraPickRay(shape.getRenderer(), shape, se.getSecondCursor()));
			if (firstCursorIntersection != null && secondCursorIntersection != null){
				Vector3D firstCursorInComponent = shape.globalToLocal(firstCursorIntersection);
				Vector3D secondCursorInComponent = shape.globalToLocal(secondCursorIntersection);
				float[] minMax = ToolsGeometry.getMinXYMaxXY(new Vector3D[]{firstCursorInComponent, secondCursorInComponent});
				if (se.getId() == MTGestureEvent.GESTURE_STARTED){
					l1 = new MTLine(shape.getRenderer(), new Vertex(minMax[0],minMax[1]), new Vertex(minMax[2],minMax[1]));
					l2 = new MTLine(shape.getRenderer(), new Vertex(minMax[0],minMax[1]), new Vertex(minMax[0],minMax[3]));
					l3 = new MTLine(shape.getRenderer(), new Vertex(minMax[2],minMax[1]), new Vertex(minMax[2],minMax[3]));
					l4 = new MTLine(shape.getRenderer(), new Vertex(minMax[0],minMax[3]), new Vertex(minMax[2],minMax[3]));
					l1.setPickable(false); l2.setPickable(false); l3.setPickable(false); l4.setPickable(false);
					shape.addChild(l1);
					shape.addChild(l2);
					shape.addChild(l3);
					shape.addChild(l4);
				}else if (se.getId() == MTGestureEvent.GESTURE_UPDATED){
					l1.setVertices(new Vertex[]{new Vertex(minMax[0],minMax[1]), new Vertex(minMax[2],minMax[1])});
					l2.setVertices(new Vertex[]{new Vertex(minMax[0],minMax[1]), new Vertex(minMax[0],minMax[3])});
					l3.setVertices(new Vertex[]{new Vertex(minMax[2],minMax[1]), new Vertex(minMax[2],minMax[3])});
					l4 .setVertices(new Vertex[]{new Vertex(minMax[0],minMax[3]), new Vertex(minMax[2],minMax[3])});
				}
			}
			
			if (se.getId() == MTGestureEvent.GESTURE_ENDED && l1 != null && l2 != null){
//				Vector3D upperLeft = new Vector3D(minMax[0],minMax[1]);
//				Vector3D lowerRight = new Vector3D(minMax[2],minMax[3]);
//				Vector3D upperLeft = new Vector3D(l1.getVerticesLocal()[0].x, l1.getVerticesLocal()[0].y);
//				Vector3D lowerRight = new Vector3D(l4.getVerticesLocal()[1].x, l4.getVerticesLocal()[1].y);
				Vector3D upperLeft = new Vector3D(l1.getVerticesLocal()[0].x - shape.getVerticesLocal()[0].x, l1.getVerticesLocal()[0].y -  shape.getVerticesLocal()[0].y);
				Vector3D lowerRight = new Vector3D(l4.getVerticesLocal()[1].x - shape.getVerticesLocal()[0].x, l4.getVerticesLocal()[1].y - shape.getVerticesLocal()[0].y);
				try {
					//System.out.println("Get() x0:" + Math.round(upperLeft.x) + " y0:" +  Math.round(upperLeft.y) +  " x1: " +  Math.round(lowerRight.x) + " y2: " + Math.round(lowerRight.y)  +  "w: " +  Math.round(lowerRight.x-upperLeft.x) + " h: " + Math.round(lowerRight.y-upperLeft.y));
					PImage sliced = shape.getTexture().get(Math.round(upperLeft.x), Math.round(upperLeft.y), Math.round(lowerRight.x-upperLeft.x), Math.round(lowerRight.y-upperLeft.y));
					shape.setTexture(sliced);
					shape.setSizeLocal(sliced.width, sliced.height);
					//Compensate for position change
//					upperLeft.transformDirectionVector(shape.getGlobalMatrix()); //put local compensation vector into global space 
//					shape.translateGlobal(upperLeft);
					
					shape.translate(upperLeft, TransformSpace.LOCAL);
//					shape.translate(upperLeft.getSubtracted(shape.getPosition(TransformSpace.LOCAL)), TransformSpace.LOCAL);
//					shape.translate(shape.getPosition(TransformSpace.LOCAL).getSubtracted(upperLeft), TransformSpace.LOCAL);
					l1.destroy();
					l2.destroy();
					l3.destroy();
					l4.destroy();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return false;
		}
	
}
