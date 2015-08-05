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

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLTextureSettings;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.EXPANSION_FILTER;
import org.mt4j.util.opengl.GLTexture.SHRINKAGE_FILTER;
import org.mt4j.util.opengl.GLTexture.TEXTURE_TARGET;
import org.mt4j.util.opengl.GLTexture.WRAP_MODE;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Class MTBackgroundImage. Will display a pixel or svg image with the dimensions of the
 * screen. When using opengl and a pixel image the image can also be used tiled.
 * @author Christopher Ruff
 */
public class MTBackgroundImage extends MTPolygon {
	
	/** The svg image. */
	private MTSvg svgImage;

	/**
	 * Instantiates a new mT background image. 
	 * (Tiling works only with opengl)
	 * 
	 * @param mtApp the mt app
	 * @param bgImage the bg image
	 * @param tiled the tiled
	 */
	public MTBackgroundImage(AbstractMTApplication mtApp, PImage bgImage, boolean tiled) {
		super(mtApp, 
				new Vertex[]{
				new Vertex(0,0,0 , 0,0),
				new Vertex(mtApp.width,0,0, 1,0),
				new Vertex(mtApp.width,mtApp.height,0, 1,1),
				new Vertex(0,mtApp.height,0, 0,1)});
		
		boolean pot = Tools3D.isPowerOfTwoDimension(bgImage);
		
		if (tiled){
			//Generate texture coordinates to repeat the texture over the whole background (works only with OpenGL)
			float u = (float)mtApp.width/(float)bgImage.width;
			float v = (float)mtApp.height/(float)bgImage.height;
			
			Vertex[] backgroundVertices = this.getVerticesLocal();
			backgroundVertices[0].setTexCoordU(0);
			backgroundVertices[0].setTexCoordV(0);
			backgroundVertices[1].setTexCoordU(u);
			backgroundVertices[1].setTexCoordV(0);
			backgroundVertices[2].setTexCoordU(u);
			backgroundVertices[2].setTexCoordV(v);
			backgroundVertices[3].setTexCoordU(0);
			backgroundVertices[3].setTexCoordV(v);
			
//			this.setVertices(getVerticesLocal()); //For performance, just update the texture buffer
			//Update changed texture coordinates for opengl buffer drawing
			if (MT4jSettings.getInstance().isOpenGlMode())
				this.getGeometryInfo().updateTextureBuffer(this.isUseVBOs());
		}
		
		if (MT4jSettings.getInstance().isOpenGlMode()){
			GLTextureSettings g = new GLTextureSettings(TEXTURE_TARGET.TEXTURE_2D, SHRINKAGE_FILTER.BilinearNoMipMaps, EXPANSION_FILTER.Bilinear, WRAP_MODE.REPEAT, WRAP_MODE.REPEAT); 
			GLTexture tex;
			if (pot){
				tex = new GLTexture(mtApp, bgImage, g);
			}else{
				if (tiled){
					g.target = TEXTURE_TARGET.RECTANGULAR;
					//Because NPOT texture with GL_REPEAT isnt supported -> use mipMapping -> gluBuild2Dmipmapds strechtes the texture to POT size
//					g.shrinkFilter = SHRINKAGE_FILTER.BilinearNearestMipMap; 
					g.shrinkFilter = SHRINKAGE_FILTER.Trilinear;
					tex = new GLTexture(mtApp, bgImage, g);
				}else{
					g.target = TEXTURE_TARGET.RECTANGULAR;
					tex = new GLTexture(mtApp, bgImage, g);
				}
			}
			this.setTexture(tex);
		}else{
			this.setTexture(bgImage);
		}
		
		this.setNoStroke(true);
		this.setPickable(false);
	}
	
	/**
	 * Instantiates a new MT background image.
	 *
	 * @param pApplet the applet
	 * @param svgImage the svg image
	 * @param stretchToFitWidth the stretch to fit width
	 * @param stretchToFitHeight the stretch to fit height
	 */
	public MTBackgroundImage(PApplet pApplet, MTSvg svgImage, boolean stretchToFitWidth, boolean stretchToFitHeight) {
		super(pApplet, new Vertex[]{new Vertex(0,0,0 , 0,0),new Vertex(pApplet.width,0,0, 1,0),new Vertex(pApplet.width,pApplet.height,0, 1,1),new Vertex(0,pApplet.height,0, 0,1)});
		this.svgImage = svgImage;
		this.setPickable(false);
		//Actually dont draw this polygon - only its children (this.setVisible(false) would not draw the children)
		this.setNoFill(true);
		this.setNoStroke(true);
		//Because this is used in 2D on the z=0 plane probably. 
		this.setBounds(new BoundsZPlaneRectangle(this));
		this.addChild(svgImage);
		
		if (stretchToFitWidth && stretchToFitHeight){
			svgImage.setSizeXYRelativeToParent(this.getWidthXY(TransformSpace.LOCAL), this.getHeightXY(TransformSpace.LOCAL));
		}else if (stretchToFitWidth){
			svgImage.setWidthXYRelativeToParent(this.getWidthXY(TransformSpace.LOCAL));
		}else if (stretchToFitHeight){
			svgImage.setHeightXYRelativeToParent(this.getHeightXY(TransformSpace.LOCAL));
		}
		svgImage.setPositionRelativeToParent(this.getCenterPointLocal());//Center the svg on the center of this polygon
		svgImage.setPickable(false);
	}
	
	@Override
	protected void setDefaultGestureActions() {
		//register no gesture processors
	}
	
	@Override
	protected IBoundingShape computeDefaultBounds() {
		return	new BoundsZPlaneRectangle(this);
	}
	
	public MTSvg getSVGImage(){
		return this.svgImage;
	}

	
}
