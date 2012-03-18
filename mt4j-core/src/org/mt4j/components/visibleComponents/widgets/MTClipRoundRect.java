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
package org.mt4j.components.visibleComponents.widgets;

import org.mt4j.components.clipping.Clip;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * The Class MTClipRoundRect.A round rectangle whos children are clipped at the borders
 * of this round rectangle so they are only visible inside of it.
 * @author Christopher Ruff
 */
public class MTClipRoundRect extends MTRoundRectangle {

	/**
	 * Instantiates a new mT clip round rect.
	 * @param applet the applet
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 * @param arcWidth the arc width
	 * @param arcHeight the arc height
	 */
	public MTClipRoundRect(PApplet applet, float x, float y, float z, float width, float height, float arcWidth, float arcHeight) {
		super(applet, x, y, z, width, height, arcWidth, arcHeight);
		
		this.setStrokeWeight(1);
		
		if (MT4jSettings.getInstance().isOpenGlMode()){
//			MTRoundRectangle clipRect3 = new MTRoundRectangle(x+0.1f, y+0.1f, z, width - 0.8f, height - 0.8f, arcWidth, arcHeight, applet);
			MTRoundRectangle clipRect = new MTRoundRectangle(applet, x, y, z, width, height, arcWidth, arcHeight);
	        clipRect.setNoStroke(true);
	        clipRect.setBoundsBehaviour(MTRectangle.BOUNDS_ONLY_CHECK);
	        
//	        GL gl = ((PGraphicsOpenGL)applet.g).gl;
	        GL10 gl = PlatformUtil.getGL();
	        Clip clipMask = new Clip(gl, clipRect);
	        this.setChildClip(clipMask);
		}
        
	}
	
	
	@Override
	public void setSizeLocal(float width, float height) {
		super.setSizeLocal(width, height);
		if (MT4jSettings.getInstance().isOpenGlMode() && this.getClip() != null && this.getClip().getClipShape() instanceof MTRectangle){ 
			MTRectangle clipRect = (MTRectangle)this.getClip().getClipShape();
			//clipRect.setVertices(Vertex.getDeepVertexArrayCopy(this.getVerticesLocal()));
			clipRect.setVertices(this.getVerticesLocal());
		}
	}

}
