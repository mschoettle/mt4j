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

import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.css.style.CSSStyle;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.TEXTURE_TARGET;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * A simple ellipse shape.
 * 
 * @author Christopher Ruff
 */
public class MTEllipse extends MTPolygon {
	
	/** The radius x. */
	private float radiusX;
	
	/** The radius y. */
	private float radiusY;
	
	/** The center point. */
	private Vector3D centerPoint;
	
	/** The theta. */
	private float theta;
	
	/** The degrees. */
	private float degrees;

	private int segments;
	
	/**
	 * Instantiates a new mT ellipse.
	 * 
	 * @param pApplet the applet
	 * @param centerPoint the center point
	 * @param radiusX the radius x
	 * @param radiusY the radius y
	 */
	public MTEllipse(PApplet pApplet, Vector3D centerPoint, float radiusX, float radiusY) {
		this(pApplet, centerPoint, radiusX, radiusY, 45);
	}
	
	/**
	 * Instantiates a new mT ellipse.
	 * 
	 * @param pApplet the applet
	 * @param centerPoint the center point
	 * @param radiusX the radius x
	 * @param radiusY the radius y
	 * @param segments the segments
	 */
	public MTEllipse(PApplet pApplet, Vector3D centerPoint, float radiusX, float radiusY, int segments) {
		super(pApplet, new Vertex[0]);
		this.radiusX 		= radiusX;
		this.radiusY 		= radiusY;
		this.centerPoint 	= centerPoint;
		this.segments = segments;
		theta = 0.0f;
		degrees = (float)Math.toRadians(360);
		
		this.setStrokeWeight(1);
		this.setNoFill(false);
		this.setNoStroke(false);
		
		this.create();
		
		this.setBoundsBehaviour(AbstractShape.BOUNDS_CHECK_THEN_GEOMETRY_CHECK);
		
		this.setName("unnamed MTEllipse");
	}

	/**
	 * Sets the degrees for the ellipse. 360 draws a full circle/ellipse
	 * while smaller values only draw part of the circle/ellipse.
	 * To take effect, <code>recreate()</code> has to be called.
	 * 
	 * @param degrees the new degrees
	 */
	public void setDegrees(float degrees){
		this.degrees = (float)Math.toRadians(degrees);
	}
	
	
	/**
	 * Gets the degrees for the ellipse. 360 draws a full circle/ellipse
	 * while smaller values only draw part of the circle/ellipse.
	 * 
	 * @return the degrees
	 */
	public float getDegrees(){
		return (float)Math.toDegrees(this.degrees);
	}
	
	
	/**
	 * (Re-)creates the ellipse using its current settings.
	 */
	public void create(){
		this.setVertices(this.getVertices(segments));
	}
	
	
	@Override
	protected IBoundingShape computeDefaultBounds() {
		return new BoundsZPlaneRectangle(this);
	}

	/**
	 * Gets the vertices.
	 * 
	 * @param resolution the resolution
	 * 
	 * @return the vertices
	 */
	protected Vertex[] getVertices(int resolution){
		Vertex[] verts = new Vertex[resolution+1];
		
		float t;
		float inc = degrees / (float)resolution;
		
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		
		MTColor fillColor = this.getFillColor();
		
		for (int i = 0; i < resolution; i++){
			t = 0 + (i * inc);
			float x = (float) (centerPoint.x - (radiusX * Math.cos(t) * cosTheta)
					+ (radiusY * Math.sin(t) * sinTheta) );
			float y = (float) (centerPoint.y - (radiusX * Math.cos(t) * sinTheta)
					- (radiusY * Math.sin(t) * cosTheta) );
			
			verts[i] = new Vertex(x, y, centerPoint.z, fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
		}
		verts[verts.length-1] = (Vertex) verts[0].getCopy(); //NEED TO USE COPY BECAUSE TEX COORDS MAY GET SCALED DOUBLE IF SAME VERTEX OBJECT!
		//System.out.println("Points: " + verts.length);
		
		//Create tex coords
		float width = radiusX*2;
		float height = radiusY*2;
		float upperLeftX = centerPoint.x-radiusX;
		float upperLeftY = centerPoint.y-radiusY;
        for (Vertex vertex : verts) {
            vertex.setTexCoordU((vertex.x - upperLeftX) / width);
            vertex.setTexCoordV((vertex.y - upperLeftY) / height);
			//System.out.println("TexU:" + vertex.getTexCoordU() + " TexV:" + vertex.getTexCoordV());
        }
		return verts;
	}


	@Override
	public Vector3D getCenterPointLocal() {
		return new Vector3D(this.centerPoint);
	}


	@Override
	protected void applyStyleSheetCustom(CSSStyle virtualStyleSheet) {
		super.applyStyleSheetCustom(virtualStyleSheet);
		
		if (virtualStyleSheet.isModifiedHeight()) {
			if (virtualStyleSheet.isHeightPercentage()) {
				if (getParent() != null)
					setHeightXYRelativeToParent(virtualStyleSheet.getHeight() / 100f
							* getParent().getBounds()
									.getHeightXY(TransformSpace.RELATIVE_TO_PARENT));
			} else {
				setHeightXYRelativeToParent(virtualStyleSheet.getHeight());
			}
		}
		
		if (virtualStyleSheet.isModifiedWidth()) {
			if (virtualStyleSheet.isWidthPercentage()) {
				if (getParent() != null)
					setWidthXYRelativeToParent(virtualStyleSheet.getWidth() / 100f
							* getParent().getBounds()
									.getWidthXY(TransformSpace.RELATIVE_TO_PARENT));
			} else {
				setWidthXYRelativeToParent(virtualStyleSheet.getWidth());
			}
		}
	}

	public float getRadiusX() {
		return radiusX;
	}

	public float getRadiusY() {
		return radiusY;
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

	
}
