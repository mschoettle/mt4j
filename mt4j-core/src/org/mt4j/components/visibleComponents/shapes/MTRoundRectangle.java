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
package org.mt4j.components.visibleComponents.shapes;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLTexture;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * This class can be used to display a rounded rectangle shape.
 * 
 * @author Christopher Ruff
 */
public class MTRoundRectangle extends MTPolygon {
	
	//Draw first lines
	private Vertex upperLineP1;
	private Vertex upperLineP2;
	
	private Vertex rLinep1;
	private Vertex rLinep2;
	
	//Draw the first arc
	private Vertex lowerLinep1;
	private Vertex lowerLinep2;
	
	private Vertex lLinep1;
	private Vertex lLinep2;
	
	private float arcWidth;
	private float arcHeight;
	private int arcSegments;
	
	private float x,y,z;
	private float width;
	private float height;
	
	
	/**
	 * Instantiates a new mT round rectangle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 * @param arcWidth the arc width
	 * @param arcHeight the arc height
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTRoundRectangle(float x, float y, float z, float width, float height, float arcWidth, float arcHeight,  PApplet pApplet) {
		this(pApplet, x, y, z, width, height, arcWidth, arcHeight);
	}
	
	/**
	 * Instantiates a new mT round rectangle.
	 * @param pApplet the applet
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 * @param arcWidth the arc width
	 * @param arcHeight the arc height
	 */
	public MTRoundRectangle(PApplet pApplet, float x, float y, float z, float width, float height, float arcWidth,  float arcHeight) {
		this(pApplet, x, y, z, width, height, arcWidth, arcHeight, 50);
	}

	
	/**
	 * Instantiates a new mT round rectangle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 * @param arcWidth the arc width
	 * @param arcHeight the arc height
	 * @param segments the segments
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTRoundRectangle(float x, float y, float z, float width, float height, float arcWidth, float arcHeight, int segments, PApplet pApplet) {
		this(pApplet, x, y, z, width, height, arcWidth, arcHeight, segments);
	}
	
	/**
	 * Instantiates a new mT round rectangle.
	 * @param pApplet the applet
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 * @param arcWidth the arc width
	 * @param arcHeight the arc height
	 * @param segments the segments
	 */
	public MTRoundRectangle(PApplet pApplet, float x, float y, float z, float width, float height, float arcWidth, float arcHeight, int segments) {
		super(pApplet, new Vertex[]{});
		
		this.x = x;
		this.y = y;
		this.z = z;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.width = width;
		this.height = height;
		
		//defines the resolution and thereby the vertex count of the arcs
		this.arcSegments = segments;
		
		//Arc Width may not be greater than the rectangles width
		//and Arc height may not be greater than rectangles height!
		this.setVertices(this.getRoundRectVerts(x, y, z, width, height, arcWidth, arcHeight, segments, true));
		
		this.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
	}

	@Override
	protected IBoundingShape computeDefaultBounds(){
		return new BoundsZPlaneRectangle(this);
	}

	/**
	 * Gets the round rect verts.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param width the width
	 * @param height the height
	 * @param arcWidth the arc width
	 * @param arcHeight the arc height
	 * @param segments the segments
	 * 
	 * @return the round rect verts
	 */
	private Vertex[] getRoundRectVerts(float x, float y, float z, float width, float height, float arcWidth, float arcHeight, int segments, boolean createTexCoords){
		MTColor currentFillColor = getFillColor();
		
		//Draw first lines
		Vertex upperLineP1 	= new Vertex(x + arcWidth, y, 0);
		Vertex upperLineP2 	= new Vertex(x + width - arcWidth , y, 0);
//		Vertex upperLineP2 	= new Vertex(x + arcWidth + width - 2*arcWidth , y, 0);
		
		Vertex rLinep1 = new Vertex(x + width, y + arcHeight			, 0);
		Vertex rLinep2 = new Vertex(x + width, y + height	- arcHeight, 0);
		
		//Draw the first arc
		List<Vertex> upperRightCorner = ToolsGeometry.arcTo(upperLineP2.x,upperLineP2.y, arcWidth, arcHeight, 0, false,true, rLinep1.x,rLinep1.y, arcSegments);
		Vertex lowerLinep1 = new Vertex(x + width - arcWidth	, y + height, 0);
		Vertex lowerLinep2 = new Vertex(x	+ arcWidth				, y + height, 0);
			
		
		List<Vertex> lowerRightCorner = ToolsGeometry.arcTo(rLinep2.x,rLinep2.y, arcWidth, arcHeight, 0, false,true, lowerLinep1.x,lowerLinep1.y, arcSegments);
		Vertex lLinep1 = new Vertex(x , y + height - arcHeight, 0);
		Vertex lLinep2 = new Vertex(x , y + arcHeight, 0);
			
		List<Vertex> lowerLeftCorner = ToolsGeometry.arcTo(lowerLinep2.x,lowerLinep2.y, arcWidth, arcHeight, 0, false,true, lLinep1.x,lLinep1.y, arcSegments);
		
		List<Vertex> upperLeftCorner = ToolsGeometry.arcTo(lLinep2.x,lLinep2.y, arcWidth, arcHeight, 0, false,true, upperLineP1.x,upperLineP1.y, arcSegments);
		
		ArrayList<Vertex> verts = new ArrayList<Vertex>(); 
		verts.add(upperLineP1); 
//		verts.add(upperLineP2);
		verts.addAll(upperRightCorner);
		verts.add(rLinep1); 
//		verts.add(rLinep2);
		verts.addAll(lowerRightCorner);
		verts.add(lowerLinep1); 
//		verts.add(lowerLinep2);
		verts.addAll(lowerLeftCorner);
		verts.add(lLinep1); 
//		verts.add(lLinep2);
		verts.addAll(upperLeftCorner);
		Vertex[] newVertices = verts.toArray(new Vertex[verts.size()]);
		
		//Set texture coordinates
		for (Vertex vertex : newVertices) {
			if (createTexCoords){
				vertex.setTexCoordU((vertex.x - x) / width);
				vertex.setTexCoordV((vertex.y - y) / height);
				//System.out.println("TexU:" + vertex.getTexCoordU() + " TexV:" + vertex.getTexCoordV());
			}
			vertex.setRGBA(currentFillColor.getR(), currentFillColor.getG(), currentFillColor.getB(), currentFillColor.getAlpha());
		}
		return newVertices;
	}
	
	
	
	public void setSizeLocal(float width, float height){
		if (width > 0 && height > 0){
			this.setVertices(this.getRoundRectVerts(x, y, z, width, height, arcWidth, arcHeight, arcSegments, true));
		}
	}
	
//	/**
//	 * Sets the size of the rectangle.
//	 * Changes the vertices themself, not the transform, to allow for hassle-free non-uniform scaling.
//	 * <p>Overridden because shearing will occur if the component was rotated and then scaled non-uniformly!
//	 * <br>This method preserves the orientation
//	 * 
//	 * @param width the width
//	 * @param height the height
//	 * 
//	 * @return true, if sets the size xy relative to parent
//	 */
//	@Override
//	public boolean setSizeXYRelativeToParent(float width, float height){
////		/*
//		if (width > 0 && height > 0){
//			this.setVertices(this.getRoundRectVerts(x, y, z, width, height, arcWidth, arcHeight, arcSegments, true));
//			return true;
//		}else{
//			return false;
//		}
//		
////		*/
//		
//		
////		if (width > 0 && height > 0){
////			Vertex[] v = this.getVerticesObjSpace();
////			this.setVertices(new Vertex[]{
////					new Vertex(v[0].x,			v[0].y, 		v[0].z, v[0].getTexCoordU(), v[0].getTexCoordV()), 
////					new Vertex(v[0].x+width, 	v[1].y, 		v[1].z, v[1].getTexCoordU(), v[1].getTexCoordV()), 
////					new Vertex(v[0].x+width, 	v[1].y+height, 	v[2].z, v[2].getTexCoordU(), v[2].getTexCoordV()), 
////					new Vertex(v[3].x,			v[0].y+height,	v[3].z, v[3].getTexCoordU(), v[3].getTexCoordV()), 
////					new Vertex(v[4].x,			v[4].y,			v[4].z, v[4].getTexCoordU(), v[4].getTexCoordV()), 
////			});
////			return true;
////		}else
////			return false;
//	}
	
//	/* (non-Javadoc)
//	 * @see com.jMT.components.visibleComponents.shapes.MTPolygon#setHeightXYRelativeToParent(float)
//	 */
//	@Override
//	public boolean setHeightXYRelativeToParent(float height){
//		if (height > 0){
//			Vertex[] v = this.getVerticesLocal();
//			this.setVertices(new Vertex[]{
//					new Vertex(v[0].x,	v[0].y, 		v[0].z, v[0].getTexCoordU(), v[0].getTexCoordV()), 
//					new Vertex(v[1].x, 	v[1].y, 		v[1].z, v[1].getTexCoordU(), v[1].getTexCoordV()), 
//					new Vertex(v[2].x, 	v[1].y+height, 	v[2].z, v[2].getTexCoordU(), v[2].getTexCoordV()), 
//					new Vertex(v[3].x,	v[1].y+height,	v[3].z, v[3].getTexCoordU(), v[3].getTexCoordV()), 
//					new Vertex(v[4].x,	v[4].y,			v[4].z, v[4].getTexCoordU(), v[4].getTexCoordV()), 
//			});
//			return true;
//		}else
//			return false;
//	}
	
//	/**
//	 * Scales the shape to the given width.
//	 * Uses the bounding rectangle for calculation!
//	 * Aspect ratio is preserved!
//	 * 
//	 * @param width the width
//	 * 
//	 * @return true, if the width isnt negative
//	 */
//	@Override
//	public boolean setWidthXYRelativeToParent(float width){
//		if (width > 0){
//			Vertex[] v = this.getVerticesLocal();
//			this.setVertices(new Vertex[]{
//					new Vertex(v[0].x,			v[0].y, 		v[0].z, v[0].getTexCoordU(), v[0].getTexCoordV()), 
//					new Vertex(v[0].x+width, 	v[1].y, 		v[1].z, v[1].getTexCoordU(), v[1].getTexCoordV()), 
//					new Vertex(v[0].x+width, 	v[2].y, 		v[2].z, v[2].getTexCoordU(), v[2].getTexCoordV()), 
//					new Vertex(v[3].x,			v[3].y,			v[3].z, v[3].getTexCoordU(), v[3].getTexCoordV()), 
//					new Vertex(v[4].x,			v[4].y,			v[4].z, v[4].getTexCoordU(), v[4].getTexCoordV()), 
//			});
//			return true;
//		}else
//			return false;
//	}
	
	
	
	
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
