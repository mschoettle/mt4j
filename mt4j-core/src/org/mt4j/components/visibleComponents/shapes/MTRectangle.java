/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.components.visibleComponents.shapes;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.TEXTURE_TARGET;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * A simple rectangular shape.
 * 
 * @author Christopher Ruff
 */
public class MTRectangle extends MTPolygon {
	
	/** The current anchor. */
	private PositionAnchor currentAnchor;
	
	/**
	 * The Enum PositionAnchor.
	 * 
	 * @author Christopher Ruff
	 */
	public enum PositionAnchor{
		
		/** The LOWE r_ left. */
		LOWER_LEFT,
		
		/** The LOWE r_ right. */
		LOWER_RIGHT,
		
		/** The UPPE r_ left. */
		UPPER_LEFT,
		
		/** The CENTER. */
		CENTER
	}
	
	
	/**
	 * Instantiates a new mT rectangle.
	 *
	 * @param texture the texture
	 * @param applet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTRectangle(PImage texture, PApplet applet) {
		this(applet, texture);
	}
	
	/**
	 * Instantiates a new mT rectangle.
	 * @param applet the applet
	 * @param texture the texture
	 */
	public MTRectangle(PApplet applet, PImage texture) {
//		this(applet ,0 ,0, 0, texture.width, texture.height);
		this(applet ,0 ,0, 0, texture.width, texture.height);
		
		
		//To avoid errors if this is created in non opengl thread so the gl texture wont be created correctly when setting setTexture
		this.setUseDirectGL(false);

		if (applet instanceof AbstractMTApplication) {
			AbstractMTApplication app = (AbstractMTApplication) applet;
			
			if (MT4jSettings.getInstance().isOpenGlMode()){
				if (app.isRenderThreadCurrent()){
					this.setUseDirectGL(true);
				}else{
					//IF we are useing OpenGL, set useDirectGL to true 
					//(=>creates OpenGL texture, draws with pure OpenGL commands)
					//in our main thread.
					app.invokeLater(new Runnable() {
						public void run() {
							setUseDirectGL(true);
						}
					});
				}
			}else{
				if (this.isUseDirectGL()){
					this.setUseDirectGL(false);
				}
			}
		}else{
			//Cant check if we are in renderthread -> dont use direct gl mode -> dont create Gl texture object
			if (this.isUseDirectGL()){
				this.setUseDirectGL(false);
				adaptTexCoordsForNPOTUse();
			}
		}

		this.setTexture(texture);
		this.setTextureEnabled(true);
	}
	
	//FIXME TEST -> adapt tex coords for non fitting, NPOT gl texture
	private void adaptTexCoordsForNPOTUse(){
		PImage tex = this.getTexture();
		if (tex instanceof GLTexture){
			Tools3D.adaptTextureCoordsNPOT(this, (GLTexture)tex);
		}
	}
	
	@Override
	public void setUseDirectGL(boolean drawPureGL) {
		super.setUseDirectGL(drawPureGL);
		adaptTexCoordsForNPOTUse();
	}
	
	@Override
	public void setTexture(PImage newTexImage) {
		super.setTexture(newTexImage);
		adaptTexCoordsForNPOTUse();
	}
	
	
	
	/**
	 * Instantiates a new mT rectangle.
	 *
	 * @param width the width
	 * @param height the height
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTRectangle(float width, float height, PApplet pApplet) {
		this(pApplet, width, height);
	}
	
	/**
	 * Instantiates a new mT rectangle with the upper left corner at 0,0,0
	 * @param pApplet the applet
	 * @param width the width
	 * @param height the height
	 */
	public MTRectangle(PApplet pApplet, float width, float height) {
		this(pApplet,new Vertex(0,0,0,0,0),width,height);
	}
	
	
	/**
	 * Instantiates a new mT rectangle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTRectangle(float x, float y, float width, float height, PApplet pApplet) {
		this(pApplet, x, y, width, height);
	}
	
	/**
	 * Instantiates a new mT rectangle.
	 * @param pApplet the applet
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public MTRectangle(PApplet pApplet, float x, float y, float width, float height) {
		this(pApplet,new Vertex(x,y,0,0,0),width,height);
	}
	
	
	/**
	 * Instantiates a new mT rectangle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTRectangle(float x, float y, float z, float width, float height, PApplet pApplet) {
		this(pApplet, x, y, z, width, height);
	}
	
	/**
	 * Instantiates a new mT rectangle.
	 * @param pApplet the applet
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 */
	public MTRectangle(PApplet pApplet, float x, float y, float z, float width, float height) {
		this(pApplet,new Vertex(x,y,z,0,0),width,height);
	}

	
	/**
	 * Instantiates a new mT rectangle.
	 *
	 * @param upperLeft the upper left
	 * @param width the width
	 * @param height the height
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTRectangle(Vertex upperLeft, float width, float height, PApplet pApplet) {
		this(pApplet, upperLeft, width, height);
	}
	
	
	/**
	 * Instantiates a new mT rectangle.
	 * @param pApplet the applet
	 * @param upperLeft the upper left
	 * @param width the width
	 * @param height the height
	 */
	public MTRectangle(PApplet pApplet, Vertex upperLeft, float width, float height) {
//		super(pApplet,
//				new Vertex[]{
//				new Vertex(upperLeft.x,			upperLeft.y, 		upperLeft.z, 0, 0), 
//				new Vertex(upperLeft.x+width, 	upperLeft.y, 		upperLeft.z, 1, 0), 
//				new Vertex(upperLeft.x+width, 	upperLeft.y+height, upperLeft.z, 1, 1), 
//				new Vertex(upperLeft.x,			upperLeft.y+height,	upperLeft.z, 0, 1), 
//				new Vertex(upperLeft.x,			upperLeft.y,		upperLeft.z, 0, 0)});
//		
//		this.setName("unnamed rectangle");
//		//
//		this.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
//		
//		currentAnchor = PositionAnchor.CENTER;
		this(pApplet, upperLeft, width, height, 1, 1);
	}
	
	public MTRectangle(PApplet pApplet, Vertex upperLeft, float width, float height, int textureMaxX, int textureMaxY) {
		super(pApplet,
				new Vertex[]{
				new Vertex(upperLeft.x,			upperLeft.y, 		upperLeft.z, 0, 0), 
				new Vertex(upperLeft.x+width, 	upperLeft.y, 		upperLeft.z, textureMaxX, 0), 
				new Vertex(upperLeft.x+width, 	upperLeft.y+height, upperLeft.z, textureMaxX, textureMaxY), 
				new Vertex(upperLeft.x,			upperLeft.y+height,	upperLeft.z, 0, textureMaxY), 
				new Vertex(upperLeft.x,			upperLeft.y,		upperLeft.z, 0, 0)});
		
		this.setName("unnamed rectangle");
		//
		this.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
		
		currentAnchor = PositionAnchor.CENTER;
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.MTPolygon#computeDefaultBounds()
	 */
	@Override
	protected IBoundingShape computeDefaultBounds(){
		return new BoundsZPlaneRectangle(this);
	}
	
	/**
	 * Gets the Position anchor.
	 * 
	 * @return the anchor
	 */
	public PositionAnchor getAnchor(){
		return this.currentAnchor;
	}
	
	/**
	 * Sets the anchor. The Anchor determines which reference point
	 * is used at set/getPosition(). The default anchor point is the rectangle's
	 * center.
	 * 
	 * @param anchor the new anchor
	 */
	public void setAnchor(PositionAnchor anchor){
		this.currentAnchor = anchor;
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setPositionGlobal(org.mt4j.util.math.Vector3D)
	 */
	@Override
	public void setPositionGlobal(Vector3D position) {
		switch (this.getAnchor()) {
		case CENTER:
			super.setPositionGlobal(position);
			break;
		case LOWER_LEFT:{
			Vertex[] vertices = this.getVerticesGlobal();
			Vertex lowerLeft = new Vertex(vertices[3]);
			this.translateGlobal(position.getSubtracted(lowerLeft));
		}break;
		case LOWER_RIGHT:{
			Vertex[] vertices = this.getVerticesGlobal();
			Vertex v = new Vertex(vertices[2]);
			this.translateGlobal(position.getSubtracted(v));
		}break;
		case UPPER_LEFT:{
			Vertex[] vertices = this.getVerticesGlobal();
			Vertex upperLeft = new Vertex(vertices[0]);
			this.translateGlobal(position.getSubtracted(upperLeft));
		}break;
		default:
			break;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setPositionRelativeToParent(org.mt4j.util.math.Vector3D)
	 */
	@Override
	public void setPositionRelativeToParent(Vector3D position) {
		switch (this.getAnchor()) {
		case CENTER:
			super.setPositionRelativeToParent(position);
			break;
		case LOWER_LEFT:{
			Vertex[] vertices = this.getVerticesLocal();
			Vertex lowerLeft = new Vertex(vertices[3]);
			lowerLeft.transform(this.getLocalMatrix());
			this.translate(position.getSubtracted(lowerLeft), TransformSpace.RELATIVE_TO_PARENT);
		}break;
		case LOWER_RIGHT:{
			Vertex[] vertices = this.getVerticesLocal();
			Vertex v = new Vertex(vertices[2]);
			v.transform(this.getLocalMatrix());
			this.translate(position.getSubtracted(v), TransformSpace.RELATIVE_TO_PARENT);
		}break;
		case UPPER_LEFT:{
			Vertex[] vertices = this.getVerticesLocal();
			Vertex v = new Vertex(vertices[0]);
			v.transform(this.getLocalMatrix());
			this.translate(position.getSubtracted(v), TransformSpace.RELATIVE_TO_PARENT);
		}break;
		default:
			break;
		}
	}

	
	/**
	 * Gets the position. The position is dependant on the
	 * set PositionAnchor. The default is the PositionAnchor.CENTER.
	 * 
	 * @param transformSpace the transform space
	 * @return the position
	 */
	public Vector3D getPosition(TransformSpace transformSpace){
		Vector3D v;
		switch (transformSpace) {
		case LOCAL:
			switch (this.getAnchor()) {
			case CENTER:
				return this.getCenterPointLocal();
			case LOWER_LEFT:
				return new Vector3D(this.getVerticesLocal()[3]);
			case LOWER_RIGHT:
				return new Vector3D(this.getVerticesLocal()[2]);
			case UPPER_LEFT:
				return new Vector3D(this.getVerticesLocal()[0]);
			default:
				break;
			}
			break;
		case RELATIVE_TO_PARENT:
			switch (this.getAnchor()) {
			case CENTER:
				return this.getCenterPointRelativeToParent();
			case LOWER_LEFT:
				v = new Vector3D(this.getVerticesLocal()[3]);
				v.transform(this.getLocalMatrix());
				return v;
			case LOWER_RIGHT:
				v = new Vector3D(this.getVerticesLocal()[2]);
				v.transform(this.getLocalMatrix());
				return v;
			case UPPER_LEFT:
				v = new Vector3D(this.getVerticesLocal()[0]);
				v.transform(this.getLocalMatrix());
				return v;
			default:
				break;
			}
			break;
		case GLOBAL:
			switch (this.getAnchor()) {
			case CENTER:
				return this.getCenterPointGlobal();
			case LOWER_LEFT:
				v = new Vector3D(this.getVerticesLocal()[3]);
				v.transform(this.getGlobalMatrix());
				return v;
			case LOWER_RIGHT:
				v = new Vector3D(this.getVerticesLocal()[2]);
				v.transform(this.getGlobalMatrix());
				return v;
			case UPPER_LEFT:
				v = new Vector3D(this.getVerticesLocal()[0]);
				v.transform(this.getGlobalMatrix());
				return v;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return null;
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.MTPolygon#get2DPolygonArea()
	 */
	@Override
	public double get2DPolygonArea() {
		return (getHeightXY(TransformSpace.RELATIVE_TO_PARENT)*getWidthXY(TransformSpace.RELATIVE_TO_PARENT));
	}

	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.MTPolygon#getCenterOfMass2DLocal()
	 */
	@Override
	public Vector3D getCenterOfMass2DLocal() {
		Vertex[] v = this.getVerticesLocal();
		return new Vector3D(
				v[0].getX() + ((v[1].getX() - v[0].getX())/2),
				v[1].getY() + ((v[2].getY() - v[1].getY())/2),
				v[0].getZ());
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.MTPolygon#getCenterPointLocal()
	 */
	@Override
	public Vector3D getCenterPointLocal(){
		return this.getCenterOfMass2DLocal();
	}

	
	/**
	 * Sets the size locally, meaning that not the transformation of the rectangle is changed, (as setSize/setWidth, scale etc. would do) but the vertices 
	 * of the rectangle themselves. This is useful if we dont want the rectangles children to be scaled as well, for example.
	 * <br>Note: The scaling is done from the rectangles upper left corner - not the center!
	 * 
	 * @param width the width
	 * @param height the height
	 */
	public void setSizeLocal(float width, float height){
		if (width > 0 && height > 0){
			Vertex[] v = this.getVerticesLocal();
			this.setVertices(new Vertex[]{
					new Vertex(v[0].x,			v[0].y, 		v[0].z, v[0].getTexCoordU(), v[0].getTexCoordV(), v[0].getR(), v[0].getG(), v[0].getB(), v[0].getA()), 
					new Vertex(v[0].x+width, 	v[1].y, 		v[1].z, v[1].getTexCoordU(), v[1].getTexCoordV(), v[1].getR(), v[1].getG(), v[1].getB(), v[1].getA()), 
					new Vertex(v[0].x+width, 	v[1].y+height, 	v[2].z, v[2].getTexCoordU(), v[2].getTexCoordV(), v[2].getR(), v[2].getG(), v[2].getB(), v[2].getA()), 
					new Vertex(v[3].x,			v[0].y+height,	v[3].z, v[3].getTexCoordU(), v[3].getTexCoordV(), v[3].getR(), v[3].getG(), v[3].getB(), v[3].getA()), 
					new Vertex(v[4].x,			v[4].y,			v[4].z, v[4].getTexCoordU(), v[4].getTexCoordV(), v[4].getR(), v[4].getG(), v[4].getB(), v[4].getA()), 
			});
		}
	}
	
	
	/**
	 * Sets the height locally, meaning that not the transformation of the rectangle is changed, (as setSize/setWidth, scale etc. would do) but the vertices 
	 * of the rectangle themselves. This is useful if we dont want the rectangles children to be scaled as well, for example.
	 * <br>Note: The scaling is done from the rectangles upper left corner - not the center!
	 * 
	 * @param height the new height local
	 */
	public void setHeightLocal(float height){
		Vertex[] v = this.getVerticesLocal();
		this.setVertices(new Vertex[]{
				new Vertex(v[0].x,	v[0].y, 		v[0].z, v[0].getTexCoordU(), v[0].getTexCoordV(), v[0].getR(), v[0].getG(), v[0].getB(), v[0].getA()), 
				new Vertex(v[1].x, 	v[1].y, 		v[1].z, v[1].getTexCoordU(), v[1].getTexCoordV(), v[1].getR(), v[1].getG(), v[1].getB(), v[1].getA()), 
				new Vertex(v[2].x, 	v[1].y+height, 	v[2].z, v[2].getTexCoordU(), v[2].getTexCoordV(), v[2].getR(), v[2].getG(), v[2].getB(), v[2].getA()), 
				new Vertex(v[3].x,	v[1].y+height,	v[3].z, v[3].getTexCoordU(), v[3].getTexCoordV(), v[3].getR(), v[3].getG(), v[3].getB(), v[3].getA()), 
				new Vertex(v[4].x,	v[4].y,			v[4].z, v[4].getTexCoordU(), v[4].getTexCoordV(), v[4].getR(), v[4].getG(), v[4].getB(), v[4].getA()), 
		});
	}
	
	
	/**
	 * Sets the width locally, meaning that not the transformation of the rectangle is changed, (as setSize/setWidth, scale etc. would do) but the vertices 
	 * of the rectangle themselves. This is useful if we dont want the rectangles children to be scaled as well, for example.
	 * <br>Note: The scaling is done from the rectangles upper left corner - not the center!
	 * @param width the new width local
	 */
	public void setWidthLocal(float width){
		if (width > 0){
			Vertex[] v = this.getVerticesLocal();
			this.setVertices(new Vertex[]{
					new Vertex(v[0].x,			v[0].y, v[0].z, v[0].getTexCoordU(), v[0].getTexCoordV(), v[0].getR(), v[0].getG(), v[0].getB(), v[0].getA()), 
					new Vertex(v[0].x+width, 	v[1].y, v[1].z, v[1].getTexCoordU(), v[1].getTexCoordV(), v[1].getR(), v[1].getG(), v[1].getB(), v[1].getA()), 
					new Vertex(v[0].x+width, 	v[2].y, v[2].z, v[2].getTexCoordU(), v[2].getTexCoordV(), v[2].getR(), v[2].getG(), v[2].getB(), v[2].getA()), 
					new Vertex(v[3].x,			v[3].y,	v[3].z, v[3].getTexCoordU(), v[3].getTexCoordV(), v[3].getR(), v[3].getG(), v[3].getB(), v[3].getA()), 
					new Vertex(v[4].x,			v[4].y,	v[4].z, v[4].getTexCoordU(), v[4].getTexCoordV(), v[4].getR(), v[4].getG(), v[4].getB(), v[4].getA()), 
			});
		}
	}
	
	
	@Override
	protected void applyStyleSheetCustom(CSSStyle virtualStyleSheet) {
		super.applyStyleSheetCustom(virtualStyleSheet);
		
		if (virtualStyleSheet.isWidthPercentage()
				&& virtualStyleSheet.isHeightPercentage()) {
			if (this.getParent() != null) {
				if (virtualStyleSheet.getWidth() > 0)
					this.setWidthLocal(virtualStyleSheet.getWidth() / 100f
							* this.getParent().getBounds()
									.getWidthXY(TransformSpace.RELATIVE_TO_PARENT));

				if (virtualStyleSheet.getHeight() > 0)
					this.setHeightLocal(virtualStyleSheet.getHeight()/ 100f
							* this.getParent().getBounds()
									.getHeightXY(TransformSpace.RELATIVE_TO_PARENT));

			}
		} else if (virtualStyleSheet.isWidthPercentage()) {
			if (virtualStyleSheet.getWidth() > 0)
				this.setWidthLocal(virtualStyleSheet.getWidth() / 100f
						* this.getParent().getBounds()
								.getWidthXY(TransformSpace.RELATIVE_TO_PARENT));

			if (virtualStyleSheet.getHeight() > 0)
				this.setHeightLocal(virtualStyleSheet.getHeight());
		} else if (virtualStyleSheet.isHeightPercentage()) {
			if (virtualStyleSheet.getWidth() > 0)
				this.setWidthLocal(virtualStyleSheet.getWidth());

			if (virtualStyleSheet.getHeight() > 0)
				this.setHeightLocal(virtualStyleSheet.getHeight() / 100f
						* this.getParent().getBounds()
								.getHeightXY(TransformSpace.RELATIVE_TO_PARENT));

		} else {
			if (virtualStyleSheet.getWidth() > 0)
				this.setWidthLocal(virtualStyleSheet.getWidth());

			if (virtualStyleSheet.getHeight() > 0)
				this.setHeightLocal(virtualStyleSheet.getHeight());
		}
	}

}
