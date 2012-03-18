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
package org.mt4j.util;

import java.util.ArrayList;
import java.util.List;


import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTComplexPolygon;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.util.math.ConvexityUtil;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.xml.svg.CustomPathHandler;

import processing.core.PApplet;

public class ShapeBuilder {
	private CustomPathHandler pathHandler;
	private PApplet app;
	
	public ShapeBuilder(PApplet app){
		this.app = app;
		this.pathHandler = new CustomPathHandler();
		
	}
	
//	public void startPath() throws ParseException {
//		this.pathHandler.startPath();
//	}
	
	public void reset(){
		this.pathHandler = new CustomPathHandler();
	}

	/**
	 * Starts a new path at the specified absolute coordinate.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void movetoAbs(float x, float y){
		this.pathHandler.movetoAbs(x, y);
	}

	/**
	 * Starts a new path at the specified coordinate relative to the last coordinate.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void movetoRel(float x, float y){
		this.pathHandler.movetoRel(x, y);
	}
	
	public void arcAbs(float rx, float ry, float phi, boolean largeArc,	boolean sweep, float x, float y) {
		this.pathHandler.arcAbs(rx, ry, phi, largeArc, sweep, x, y);
	}

	public void arcRel(float rx, float ry, float phi, boolean largeArc,boolean sweep, float x, float y) {
		this.pathHandler.arcRel(rx, ry, phi, largeArc, sweep, x, y);
	}


	public void curvetoCubicAbs(float x1, float y1, float x2, float y2,float x, float y){
		this.pathHandler.curvetoCubicAbs(x1, y1, x2, y2, x, y);
	}

	public void curvetoCubicRel(float x1, float y1, float x2, float y2,float x, float y) {
		this.pathHandler.curvetoCubicRel(x1, y1, x2, y2, x, y);
	}

	public void curvetoCubicSmoothAbs(float x2, float y2, float x, float y){
		this.pathHandler.curvetoCubicSmoothAbs(x2, y2, x, y);
	}

	public void curvetoCubicSmoothRel(float x2, float y2, float x, float y){
		this.pathHandler.curvetoCubicSmoothRel(x2, y2, x, y);
	}

	public void curvetoQuadraticAbs(float x1, float y1, float x, float y) {
		this.pathHandler.curvetoQuadraticAbs(x1, y1, x, y);
	}

	public void curvetoQuadraticRel(float x1, float y1, float x, float y){
		this.pathHandler.curvetoQuadraticRel(x1, y1, x, y);
	}

	public void curvetoQuadraticSmoothAbs(float x, float y){
		this.pathHandler.curvetoQuadraticSmoothAbs(x, y);
	}

	public void curvetoQuadraticSmoothRel(float x, float y) {
		this.pathHandler.curvetoQuadraticSmoothRel(x, y);
	}

	public void linetoAbs(float x, float y){
		this.pathHandler.linetoAbs(x, y);
	}

	public void linetoHorizontalAbs(float x){
		this.pathHandler.linetoHorizontalAbs(x);
	}

	public void linetoHorizontalRel(float x){
		this.pathHandler.linetoHorizontalRel(x);
	}

	public void linetoRel(float x, float y){
		this.pathHandler.linetoRel(x, y);
	}

	public void linetoVerticalAbs(float y){
		this.pathHandler.linetoVerticalAbs(y);
	}

	public void linetoVerticalRel(float y){
		this.pathHandler.linetoVerticalRel(y);
	}

	public void setVerbose(boolean verbose) {
		this.pathHandler.setVerbose(verbose);
	}
	
	/**
	 * Closed the current path by adding the start point again.
	 */
	public void closePath(){
		this.pathHandler.closePath();
	}
	
	private void endPath(){
		this.pathHandler.endPath();
	}

	
	public AbstractShape getShape(){
		this.endPath();
		
		ArrayList<Vertex[]> contours = this.pathHandler.getContours();
		Vertex[] allPoints = this.pathHandler.getPathPointsArray();
		AbstractShape returnComponent = null;
		//Check for convexity
		int convexity = ConvexityUtil.classifyPolygon2(allPoints.length, allPoints);
		switch (convexity) {
			case ConvexityUtil.NotConvexDegenerate:
//				logger.debug("not Convex Degenerate");
			case ConvexityUtil.NotConvex:
//				logger.debug("not convex");
				returnComponent = createComplexPoly(contours, MTComplexPolygon.WINDING_RULE_ODD);
				break;
			case ConvexityUtil.ConvexDegenerate:
//				logger.debug("convex degenerate");
			case ConvexityUtil.ConvexCW:
//				logger.debug("convex clockwise");
			case ConvexityUtil.ConvexCCW:
//				logger.debug("convex counterclockwise");
				returnComponent = createPoly(allPoints);
				break;
			default:
				break;
		}
		
		//Create some default texture coords
		if (returnComponent != null && returnComponent.hasBounds() && returnComponent.getBounds() instanceof BoundsZPlaneRectangle){
			BoundsZPlaneRectangle bounds = (BoundsZPlaneRectangle) returnComponent.getBounds();
			float width = bounds.getWidthXY(TransformSpace.LOCAL);
			float height = bounds.getHeightXY(TransformSpace.LOCAL);
			float upperLeftX = bounds.getVectorsLocal()[0].x;
			float upperLeftY = bounds.getVectorsLocal()[0].y;
			Vertex[] verts = returnComponent.getVerticesLocal();
            for (Vertex vertex : verts) {
                vertex.setTexCoordU((vertex.x - upperLeftX) / width);
                vertex.setTexCoordV((vertex.y - upperLeftY) / height);
                //System.out.println("TexU:" + vertex.getTexCoordU() + " TexV:" + vertex.getTexCoordV());
            }
			returnComponent.getGeometryInfo().updateTextureBuffer(returnComponent.isUseVBOs());
		}
		return returnComponent;
	}

	private AbstractShape createPoly(Vertex[] verts) {
		int segments = 15; 
		if (ToolsGeometry.containsBezierVertices(verts))
			verts = ToolsGeometry.createVertexArrFromBezierArr(verts, segments);

		//Blow up vertex array, that will be used for picking etc
		//to at least be of size == 3 for generating normals
		if (verts.length <3){
			Vertex[] newVerts = new Vertex[3];
			if (verts.length == 2){
				newVerts[0] = verts[0];
				newVerts[1] = verts[1];
				newVerts[2] = (Vertex)verts[1].getCopy();
				verts = newVerts;
			}else if (verts.length == 1){
				newVerts[0] = verts[0];
				newVerts[1] = (Vertex)verts[0].getCopy();
				newVerts[2] = (Vertex)verts[0].getCopy();
				verts = newVerts;
			}else{
				//ERROR
			}
		}
		return new MTPolygon2D(verts, app);
	}

	
	private AbstractShape createComplexPoly(ArrayList<Vertex[]> contours, int windingRuleOdd) {
		int segments = 15; 
		List<Vertex[]> bezierContours = ToolsGeometry.createVertexArrFromBezierVertexArrays(contours, segments);
		return new MTComplexPolygon2D(app, bezierContours);
	}
	
	
	private class MTComplexPolygon2D extends MTComplexPolygon{
		public MTComplexPolygon2D(PApplet app, List<Vertex[]> contours) {
			super(app, contours);
			
		}
		@Override
		protected IBoundingShape computeDefaultBounds() {
			//Use z plane bounding rect instead default boundingsphere since always 2D!
			return new BoundsZPlaneRectangle(this);
		}
	}
	
	private class MTPolygon2D extends MTPolygon{
		public MTPolygon2D(Vertex[] vertices, PApplet applet) {
			super(applet, vertices);
		}
		
		protected IBoundingShape computeDefaultBounds() {
			//Use z plane bounding rect instead default boundingsphere since always 2D!
			return new BoundsZPlaneRectangle(this);
		}
	}
	

}
