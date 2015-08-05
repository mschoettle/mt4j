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
package org.mt4j.components.visibleComponents.widgets.keyboard;

import java.util.List;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GL11Plus;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * Key class used in the mt keyboard.
 * 
 * @author Christopher Ruff
 */
public class MTKey extends 
	//MTComplexPolygon 
	MTTriangleMesh
	{
	
	/** The pa. */
	private PApplet pa;
	
	/** The original height. */
	private float originalWidth,originalHeight;
	
	/** The pressed. */
	private boolean pressed;
	
	/** The button back ground. */
	private MTPolygon buttonBackGround;
	
	/** The character unicode to write. */
	private String characterUnicodeToWrite;
	
	/** The character unicode to write shifted. */
	private String characterUnicodeToWriteShifted;
	
	/**
	 * Instantiates a new mT key.
	 * @param pApplet the applet
	 * @param geom the geom
	 * @param characterUnicodeToWrite the character unicode to write
	 * @param characterUnicodeToWriteShifted the character unicode to write shifted
	 */
//	public MTKey(Vertex[] vertices, /*List<Vertex[]> contours,*/ PApplet pApplet,String characterUnicodeToWrite, String characterUnicodeToWriteShifted) {
	public MTKey(/*List<Vertex[]> contours,*/ PApplet pApplet, GeometryInfo geom,String characterUnicodeToWrite, String characterUnicodeToWriteShifted) {
//		super(vertices, outLines, pApplet);
		
//		/*
//		super(pApplet, new GeometryInfo(pApplet, new Vertex[]{new Vertex(),new Vertex(),new Vertex()}));
//		//Caluculate vertices from bezierinformation
//		int segments = 10; 
//		List<Vertex[]> bezierContours = Tools3D.createVertexArrFromBezierVertexArrays(contours, segments);
//		//Triangulate bezier contours
//		GluTrianglulator triangulator = new GluTrianglulator(pApplet);
//		List<Vertex> tris = triangulator.tesselate(bezierContours);
//		//Set new geometry info with triangulated vertices
//		this.setGeometryInfo(new GeometryInfo(pApplet, tris.toArray(new Vertex[tris.size()])));
//		//Set Mesh outlines
//		this.setOutlineContours(bezierContours);
//		//Delete triangulator (C++ object)
//		triangulator.deleteTess(); 
//		*/
		//TODO just supply font geometry info directly!
//		super(pApplet, new GeometryInfo(pApplet, vertices));
		super(pApplet, geom, false);
		this.pa = pApplet;
		this.pressed = false;
		this.characterUnicodeToWrite 		= characterUnicodeToWrite;
		this.characterUnicodeToWriteShifted = characterUnicodeToWriteShifted;
		
		this.setNoStroke(false);
		this.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
		
//		if (MT4jSettings.getInstance().isOpenGlMode()){
//			this.setUseDirectGL(true);
//			this.generateAndUseDisplayLists();
//		}
//		if (MT4jSettings.getInstance().isOpenGlMode()){
//			buttonBackGround.setUseDirectGL(true);
//			if (this.isUseDisplayList()){
//				buttonBackGround.generateAndUseDisplayLists();
//			}
//		}
	}
	
	@Override
	protected void setDefaultGestureActions() {
//		super.setDefaultGestureActions();
	}
	
	@Override
	public void setOutlineContours(List<Vertex[]> contours) {
		super.setOutlineContours(contours);
		
		buttonBackGround = new ButtonBackground(contours.get(0),pa);
		buttonBackGround.setPickable(false);
		buttonBackGround.setStrokeWeight(1.0f);
		buttonBackGround.setDrawSmooth(false);
		buttonBackGround.setNoFill(false);
		buttonBackGround.setNoStroke(true);
		buttonBackGround.setStrokeColor(new MTColor(210, 210, 210, 255));
		buttonBackGround.setFillColor(new MTColor(220, 220, 220, 255));
		//change drawmode to polygon, triangle fan doesent work (!?)
//		buttonBackGround.setFillDrawMode(GL11.GL_POLYGON);
//		buttonBackGround.scaleGlobal(0.95f, 0.95f, 1, buttonBackGround.getCenterPointGlobal());
	}
	
	//FIXME when we use the fonts geometry - does it already have displaylists? else we
	//creat new ones on the graphics card everytime!!! 
	@Override
	public void setUseDisplayList(boolean useDisplayList) {
		super.setUseDisplayList(useDisplayList);
		
		//Also use display list for the buttons background
		if (useDisplayList){
			if (MT4jSettings.getInstance().isOpenGlMode()){
				if (buttonBackGround != null && buttonBackGround.isUseDirectGL() && !buttonBackGround.isUseDisplayList()){
					buttonBackGround.generateAndUseDisplayLists();
				}
			}
		}
	}
	
	
	
	@Override
	public void setVertices(Vertex[] vertices) {
		super.setVertices(vertices);
		this.originalWidth 	= this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
		this.originalHeight = this.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
	}
	
	@Override
	public void setGeometryInfo(GeometryInfo geometryInfo) {
		super.setGeometryInfo(geometryInfo);
		this.originalWidth 	= this.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
		this.originalHeight = this.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
	}
	
	private class ButtonBackground extends MTPolygon{
		public ButtonBackground(Vertex[] vertices, PApplet applet) {
			super(applet, vertices);
			/*
			//Color the background
			float[] minMax = Tools3D.getMinXYMaxXY(vertices);
			float minX = minMax[0];
			float minY = minMax[1];
			float maxX = minMax[2];
			float maxY = minMax[3];
			Vector3D upperLeft = new Vector3D(minX, minY);
			Vector3D lowerRight = new Vector3D(maxX, maxY);
			float diagonal = new Vector3D(lowerRight.getSubtracted(upperLeft)).length();
			
			for (int i = 0; i < vertices.length; i++) {
				Vertex v = vertices[i];
//				float dist = Vector3D.distance(Vector3D.ZERO_VECTOR, v);
//				float value = Tools3D.map(dist, 0, width, 0, 255);
				float dist = Vector3D.distance2D(upperLeft, v);
				float value = Tools3D.map(dist, diagonal, 0, 180, 255);
				v.setRGBA(value, value, value, 255);
//				float val = v.y - halfY;
//				value = Tools3D.map(val, halfY, 0, 0, 255);
//				v.setRGBA(value, value, value, 255);
			}
			this.getGeometryInfo().updateVerticesColorBuffer();
			*/
		}
		
		@Override
		protected IBoundingShape computeDefaultBounds() {
			//no need for a bounding shape -> not pickable etc anyway
			return null;
		}
		
		@Override
		protected void setDefaultGestureActions() {
			//no gestures
		}
	}
	
	
	@Override
	protected IBoundingShape computeDefaultBounds(){
		return new BoundsZPlaneRectangle(this);
	}
	
	
	/**
	 * Gets the character to write.
	 * 
	 * @return the character to write
	 */
	public String getCharacterToWrite(){
		return characterUnicodeToWrite;
	}

	/**
	 * Gets the character to write shifted.
	 * 
	 * @return the character to write shifted
	 */
	public String getCharacterToWriteShifted(){
		return characterUnicodeToWriteShifted;
	}

	@Override
	/**
	 * Overridden to also draw the key background (would be translucent else)
	 */
	public void drawComponent(PGraphics g) {
		if (this.isUseDirectGL()){
			if (this.isUseDisplayList()){
//				GL gl = Tools3D.beginGL(g);
				GL10 gl = PlatformUtil.beginGL();
				GL11Plus gl11Plus = PlatformUtil.getGL11Plus();
				int[] pds = buttonBackGround.getGeometryInfo().getDisplayListIDs();
				//Draw only filling of background polygon, without outer stroke
//				gl.glCallList(pds[0]);
				gl11Plus.glCallList(pds[0]);
				gl.glColor4f(this.getFillColor().getR(), this.getFillColor().getG(), this.getFillColor().getB(), this.getFillColor().getAlpha()); //needed when we use the displaylist of the key font, which be default doesent set its own fillcolor
				super.drawComponent(gl); 
//				Tools3D.endGL(g);
				PlatformUtil.endGL();
			}else{
				buttonBackGround.drawComponent(g);
				super.drawComponent(g); 
			}
		}else{
			buttonBackGround.drawComponent(g);
			super.drawComponent(g); 
		}
	}
	
	
	
	
	@Override
	public void destroyComponent() {
		super.destroyComponent();
		
		buttonBackGround.destroy();
		if (buttonBackGround.getGeometryInfo().getDisplayListIDs()[0] != -1){
			buttonBackGround.disableAndDeleteDisplayLists();
		}
	}

	@Override
	protected void destroyDisplayLists() {
		//Dont destroy display lists -> they are shared with the font!
		//System.out.println("NOT Destroying font char display lists: " + this);
	}

	/**
	 * Gets the original height.
	 * 
	 * @return the original height
	 */
	public float getOriginalHeight() {
		return originalHeight;
	}

	/**
	 * Gets the original width.
	 * 
	 * @return the original width
	 */
	public float getOriginalWidth() {
		return originalWidth;
	}


	/**
	 * Checks if is pressed.
	 * 
	 * @return true, if is pressed
	 */
	public boolean isPressed() {
		return pressed;
	}
	
	/**
	 * Sets the pressed.
	 * 
	 * @param pressed the new pressed
	 */
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}


}
