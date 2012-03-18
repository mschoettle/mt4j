/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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


/**
 * The Class NodeRenderer.
 * 
 * @author Christopher Ruff
 */
public class NodeRenderer {
	
	//Externalize drawing so that we can say nodedrawer.drawNode(mtcanvas); with all children..
	
//	/**
//	 * Draw the whole canvas update recursive.
//	 * 
//	 * @param currentcomp the currentcomp
//	 * @param updateTime the update time
//	 * @param graphics the renderer
//	 */
//	private void drawUpdateRecursive(MTComponent currentcomp, long updateTime, PGraphics graphics, boolean frustumCulling){
//		if (currentcomp.isVisible()){
//			//Update current component
//			currentcomp.updateComponent(updateTime);
//			
//			if (currentcomp.getAttachedCamera() != null){
//				//Saves transformations up to this object
//				graphics.pushMatrix();
//				
//				//Resets the modelview completely with a new camera matrix
//				currentcomp.getAttachedCamera().update();
//				
//				if (currentcomp.getParent() != null){
//					//Applies all transforms up to this components parent
//					//because the new camera wiped out all previous transforms
//					Matrix m = currentcomp.getParent().getGlobalMatrix();
//					PGraphics3D pgraphics3D = (PGraphics3D)graphics;
//					pgraphics3D.modelview.apply(
//							m.m00, m.m01, m.m02,  m.m03,
//							m.m10, m.m11, m.m12,  m.m13,
//							m.m20, m.m21, m.m22,  m.m23,
//							m.m30, m.m31, m.m32,  m.m33
//					);
//				}
//				
//				//Apply local transform etc
//				currentcomp.preDraw(graphics);
//				
//				//Check visibility with camera frustum
//				if (frustumCulling){
//					if (currentcomp.isContainedIn(currentcomp.getViewingCamera().getFrustum())){	
//						// DRAW THE COMPONENT  \\
//						currentcomp.drawComponent(graphics);
//					}
//				}else{
//					// DRAW THE COMPONENT  \\
//					currentcomp.drawComponent(graphics);
//				}
//				
//				currentcomp.postDraw(graphics);
//
//				//Draw Children
//				for (MTComponent child : currentcomp.getChildList())
//					drawUpdateRecursive(child, updateTime, graphics, frustumCulling);
//
//				currentcomp.postDrawChildren(graphics);
//				
//				//Restores the transforms of the previous camera etc
//				graphics.popMatrix(); 
//			}else{//If no custom camera was set
//				//TODO in abstactvisiblecomp wird outine über gradients und clips
//				//gezeichnet obwohl hier invisble war! FIXME!
//				//evtl applymatrix unapply in eigene methode? dann nur das ausführen, kein pre/post draw!
//				
//				//TODO vater an kinder listener -> resize - new geometry -> resize own 
//				
//				currentcomp.preDraw(graphics);
//				
//				if (frustumCulling){
//					//Check visibility with camera frustum
//					if (currentcomp.isContainedIn(currentcomp.getViewingCamera().getFrustum())){
//						// DRAW THE COMPONENT  \\
//						currentcomp.drawComponent(graphics);
//					}
//				}else{
//					// DRAW THE COMPONENT  \\
//					currentcomp.drawComponent(graphics);
//				}
//				
//				currentcomp.postDraw(graphics);
//					
//				for (MTComponent child : currentcomp.getChildList())
//					drawUpdateRecursive(child, updateTime, graphics, frustumCulling);
//				
//				currentcomp.postDrawChildren(graphics);
//			}
//		}//if visible end
//	}

}
