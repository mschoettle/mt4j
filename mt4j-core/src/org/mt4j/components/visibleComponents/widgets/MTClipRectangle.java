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
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;

/**
 * The Class MTClipRectangle. A rectangle whos children are clipped at the borders
 * of this rectangle so they are only visible inside of it.
 * 
 * @author Christopher Ruff
 */
public class MTClipRectangle extends MTRectangle {

	/**
	 * Instantiates a new mT clip rectangle.
	 * @param applet the applet
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 */
	public MTClipRectangle(PApplet applet, float x, float y, float z, float width, float height) {
		super(applet, x, y, z, width, height);

		this.setStrokeWeight(1);

		if (MT4jSettings.getInstance().isOpenGlMode()){
//			MTRectangle clipRect = new MTRectangle(x+0.25f, y+0.25f, z, width - 0.5f, height - 0.5f, applet);
			MTRectangle clipRect = new MTRectangle(applet, x, y, z, width, height);
			clipRect.setNoStroke(true);
			clipRect.setBoundsBehaviour(MTRectangle.BOUNDS_ONLY_CHECK);

//			GL gl = Tools3D.getGL(applet);
			GL10 gl = PlatformUtil.getGL();
			Clip clipMask = new Clip(gl, clipRect);
			this.setChildClip(clipMask);
		}
	}
	
	@Override
	public void setSizeLocal(float width, float height) {
		super.setSizeLocal(width, height);
		if (MT4jSettings.getInstance().isOpenGlMode() && this.getChildClip() != null && this.getChildClip().getClipShape() instanceof MTRectangle){ 
			MTRectangle clipRect = (MTRectangle)this.getChildClip().getClipShape();
			//clipRect.setVertices(Vertex.getDeepVertexArrayCopy(this.getVerticesLocal()));
			clipRect.setVertices(this.getVerticesLocal());
		}
	}
	
	@Override
	public void setWidthLocal(float width) {
		super.setWidthLocal(width);
		if (MT4jSettings.getInstance().isOpenGlMode() && this.getChildClip() != null && this.getChildClip().getClipShape() instanceof MTRectangle){ 
			MTRectangle clipRect = (MTRectangle)this.getChildClip().getClipShape();
			//clipRect.setVertices(Vertex.getDeepVertexArrayCopy(this.getVerticesLocal()));
			clipRect.setVertices(this.getVerticesLocal());
		}
	}
	
	@Override
	public void setHeightLocal(float height) {
		super.setHeightLocal(height);
		if (MT4jSettings.getInstance().isOpenGlMode() && this.getChildClip() != null && this.getChildClip().getClipShape() instanceof MTRectangle){ 
			MTRectangle clipRect = (MTRectangle)this.getChildClip().getClipShape(); //TODO getChildClip!?
			//clipRect.setVertices(Vertex.getDeepVertexArrayCopy(this.getVerticesLocal()));
			clipRect.setVertices(this.getVerticesLocal());
		}
	}
	
	

}
