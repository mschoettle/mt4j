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
package org.mt4j.util.xml.svg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.mt4j.util.math.BezierVertex;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vertex;


/**
 * The Class CustomPathHandler.
 */
public class CustomPathHandler implements PathHandler {
	
	/** The verbose. */
	private boolean verbose;
	
	/** The cubic bez vert to quadric control point. */
	private HashMap<BezierVertex, Vertex> cubicBezVertTOQuadricControlPoint;
	
	/** The reverse move to stack. */
	private Stack<Vertex> reverseMoveToStack;
	
	/** The sub paths. */
	private ArrayList<Vertex[]> subPaths; 
	
	/** The current sub path. */
	private ArrayList<Vertex> currentSubPath;
	
	/** The path points. */
	private LinkedList<Vertex> pathPoints;
	
	/**
	 * Instantiates a new custom path handler.
	 */
	public CustomPathHandler(){
		pathPoints = new LinkedList<Vertex>();
	    currentSubPath = new ArrayList<Vertex>();
	     	
	    subPaths = new ArrayList<Vertex[]>();
	     	
	    reverseMoveToStack = new Stack<Vertex>();	
	     	
	    verbose = false;
	    
		//Because all quadric curves get converted to cubics,
		//but the original quadric controlpoint is needed for "T" tag for reflecting
		cubicBezVertTOQuadricControlPoint 	= new HashMap<BezierVertex, Vertex>();
	}
	
	 

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#startPath()
	 */
	public void startPath() throws ParseException {
		if (verbose)
			System.out.println("Start Path");
	}

	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#movetoAbs(float, float)
	 */
	public void movetoAbs(float x, float y) throws ParseException {
		if (verbose)
			System.out.println("movetoAbs: x:" + x + " y:" + y);
		
		//If the last contour wasnt closed with Z, 
		//we save the last contour here,but without closing it
		if (!currentSubPath.isEmpty()){
			Vertex[] currentSplitPathArr = currentSubPath.toArray(new Vertex[currentSubPath.size()]);
			subPaths.add(currentSplitPathArr);
			currentSubPath.clear();
		}

		Vertex moveTo = new Vertex(x,y,0);
		pathPoints.add(moveTo);
		currentSubPath.add(moveTo);
		reverseMoveToStack.push((Vertex)moveTo.getCopy());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#movetoRel(float, float)
	 */
	public void movetoRel(float x, float y) throws ParseException {
		if (verbose)
			System.out.println("movetoRel: " + x + "," + y);
		
		//If the last contour wasnt closed with Z, 
		//we save the last contour here, without closing it
		if (!currentSubPath.isEmpty()){
			Vertex[] currentSplitPathArr = currentSubPath.toArray(new Vertex[currentSubPath.size()]);
			subPaths.add(currentSplitPathArr);
			currentSubPath.clear();
		}
		
		Vertex moveTo;
		if (!pathPoints.isEmpty() && pathPoints.getLast() != null){
			moveTo = new Vertex(pathPoints.getLast().getX() + x, pathPoints.getLast().getY() + y, 0);
		}else{
			moveTo = new Vertex(x, y, 0);
		}
		pathPoints.add(moveTo);
		currentSubPath.add(moveTo);
		reverseMoveToStack.push((Vertex)moveTo.getCopy());
	}
	

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#arcAbs(float, float, float, boolean, boolean, float, float)
	 */
	public void arcAbs(float rx, float ry, float phi, boolean large_arc, boolean sweep, float x, float y) throws ParseException {
		if (verbose)
			System.out.println("arcAbs: " + rx + " " + ry + " " + phi + " " + large_arc + " "  + sweep + " " + x + " " + y);
		
		Vertex lastPoint = pathPoints.getLast();
		List<Vertex> arcVertices = ToolsGeometry.arcTo(lastPoint.x, lastPoint.y, rx, ry, phi, large_arc, sweep, x, y, 40);
		
		//Prevent odd picking behavour, in which the normal is 
		//not correctly computed, because the 2 points are the same
		if (!arcVertices.isEmpty() 
			&& lastPoint != null 
			&& arcVertices.get(0).equalsVector(lastPoint)
		){
			arcVertices.remove(0);
		}
		pathPoints.addAll(arcVertices);
		currentSubPath.addAll(arcVertices);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#arcRel(float, float, float, boolean, boolean, float, float)
	 */
	public void arcRel(float rx, float ry, float phi, boolean large_arc, boolean sweep, float x, float y) throws ParseException {
		if (verbose)
			System.out.println("arcRel: " + rx + " " + ry + " " + phi + " " + large_arc + " "  + sweep + " " + x + " " + y);
		
		Vertex lastPoint = pathPoints.getLast();
		List<Vertex> arcVertices = ToolsGeometry.arcTo(lastPoint.x, lastPoint.y, rx, ry, phi, large_arc, sweep, lastPoint.x+x, lastPoint.y+y, 40);
		
		//Prevent odd picking behavour, in which the normal is 
		//not correctly computed, because the 2 points are the same
		if (!arcVertices.isEmpty() 
				&& lastPoint != null 
				&& arcVertices.get(0).equalsVector(lastPoint)
			){
				arcVertices.remove(0);
			}
		pathPoints.addAll(arcVertices);
		currentSubPath.addAll(arcVertices);
	}

	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#linetoAbs(float, float)
	 */
	public void linetoAbs(float x, float y) throws ParseException {
		if (verbose)
			System.out.println("linetoAbs x:" + x + " y:" + y);

		Vertex vert = new Vertex(x,y,0);
		pathPoints.add(vert);
		currentSubPath.add(vert);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#linetoRel(float, float)
	 */
	public void linetoRel(float x, float y) throws ParseException {
		if (verbose)
			System.out.println("linetoRel: " + x + "," + y);
		
		Vertex vert = new Vertex(pathPoints.getLast().getX() + x, pathPoints.getLast().getY() + y, 0);
		pathPoints.add(vert);
		currentSubPath.add(vert);
	}
	

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#linetoHorizontalAbs(float)
	 */
	public void linetoHorizontalAbs(float x) throws ParseException {
		if (verbose)
			System.out.println("linetoHorizontalAbs x:" + x);

		Vertex vert = new Vertex(x, pathPoints.getLast().getY(), 0);
		pathPoints.add(vert);
		currentSubPath.add(vert);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#linetoHorizontalRel(float)
	 */
	public void linetoHorizontalRel(float x) throws ParseException {
		if (verbose)
			System.out.println("linetoHorizontalRel: " + x);

		Vertex vert = new Vertex(pathPoints.getLast().getX() + x, pathPoints.getLast().getY(), 0);
		pathPoints.add(vert);
		currentSubPath.add(vert);
	}

	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#linetoVerticalAbs(float)
	 */
	public void linetoVerticalAbs(float y) throws ParseException {
		if (verbose)
			System.out.println("linetoVerticalAbs y:" + y);
		
		Vertex vert = new Vertex(pathPoints.getLast().getX(), y, 0);
		pathPoints.add(vert);
		currentSubPath.add(vert);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#linetoVerticalRel(float)
	 */
	public void linetoVerticalRel(float y) throws ParseException {
		if (verbose)
			System.out.println("linetoVerticalRel: " + y);
		
		Vertex vert = new Vertex(pathPoints.getLast().getX(), pathPoints.getLast().getY() + y, 0);
		pathPoints.add(vert);
		currentSubPath.add(vert);
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#curvetoQuadraticAbs(float, float, float, float)
	 */
	public void curvetoQuadraticAbs(float x1, float y1, float x, float y) throws ParseException {
		if (verbose)
			System.out.println("curvetoQuadraticAbs x1:" + x1 + " y1:" + y1 + " x:" + x+ " y:" + y);

		if (!pathPoints.isEmpty() && pathPoints.getLast() != null){
			Vertex lastEndPoint = new Vertex(pathPoints.getLast().getX(), pathPoints.getLast().getY(), pathPoints.getLast().getZ());
			Vertex quadControlPoint = new Vertex(x1,y1,0);
			//Put in startPoint = last QuadTo Endpoint of this smoothQuadTo, the calculated control point, and the endpoint of smoothQuadTo 
			BezierVertex b5 = ToolsGeometry.getCubicFromQuadraticCurve(lastEndPoint, quadControlPoint , new Vertex(x, y, 0)); 

			cubicBezVertTOQuadricControlPoint.put(b5, quadControlPoint);

			pathPoints.add(b5);
			currentSubPath.add(b5);
		}else{
			System.err.println("last point = null at curvetoQuadraticAbs");
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#curvetoQuadraticRel(float, float, float, float)
	 */
	public void curvetoQuadraticRel(float x1, float y1, float x, float y) throws ParseException {
		if (verbose)
			System.out.println("curvetoQuadraticRel: " + x1 + "," + y1 + "  " + x + "," + y);

		if (!pathPoints.isEmpty() && pathPoints.getLast() != null){
			Vertex lastPoint = pathPoints.getLast();
			
			Vertex lastEndPoint = new Vertex(lastPoint.getX(), lastPoint.getY(), lastPoint.getZ());
			Vertex quadControlPoint = new Vertex(lastPoint.getX() + x1, lastPoint.getY()+ y1, 0);
			
			//Put in startPoint = last QuadTo Endpoint of this smoothQuadTo, the calculated control point, and the endpoint of smoothQuadTo 
			BezierVertex b5 = ToolsGeometry.getCubicFromQuadraticCurve(
					lastEndPoint,
					quadControlPoint , 
					new Vertex(lastPoint.getX() + x, lastPoint.getY()+ y, 0)); 

			cubicBezVertTOQuadricControlPoint.put(b5, quadControlPoint);
			pathPoints.add(b5);
			currentSubPath.add(b5);
		}else{
			System.out.println("last point null at curvetoQuadraticRel");
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#curvetoQuadraticSmoothAbs(float, float)
	 */
	public void curvetoQuadraticSmoothAbs(float x, float y) throws ParseException {
		if (verbose)
			System.out.println("curvetoQuadraticSmoothAbs " + " x:" + x+ " y:" + y);

		Vertex lastPoint = pathPoints.getLast();
		if (lastPoint instanceof BezierVertex && cubicBezVertTOQuadricControlPoint.get(lastPoint) != null){
			Vertex lastEndPoint = new Vertex(pathPoints.getLast().getX(), pathPoints.getLast().getY(), pathPoints.getLast().getZ());

			//Get control point of last QuadTo
			Vertex lastQuadControlPoint = cubicBezVertTOQuadricControlPoint.get(lastPoint);
			cubicBezVertTOQuadricControlPoint.remove(lastPoint);

			//Rotate that controlpoint around the end point of the last QuadTo
			lastQuadControlPoint.rotateZ(lastEndPoint, 180); 

			//Put in startPoint = last QuadTo Endpoint of this smoothQuadTo, the calculated control point, and the endpoint of smoothQuadTo 
			BezierVertex b5 = ToolsGeometry.getCubicFromQuadraticCurve(lastEndPoint, lastQuadControlPoint , new Vertex(x, y, 0)); 

			//Save last quad control point
			cubicBezVertTOQuadricControlPoint.put(b5, lastQuadControlPoint);
			
			pathPoints.add(b5);
			currentSubPath.add(b5);
		}else{
			if (verbose)
				System.out.println("Couldnt get last controlpoint at: curvetoQuadraticSmoothAbs - using last point as controlpoint");
			
			//If we couldnt retrieve the controlpoint of the current point, 
			//we use the current point as the new controlpoint
			Vertex lastEndPoint 	= new Vertex(lastPoint.getX(),lastPoint.getY(),0);
			Vertex quadControlPoint = new Vertex(lastPoint.getX(),lastPoint.getY(),0);
			
			BezierVertex b5 = ToolsGeometry.getCubicFromQuadraticCurve(
					lastEndPoint, 
					quadControlPoint , 
					new Vertex(x, y, 0)); 
			
			cubicBezVertTOQuadricControlPoint.put(b5, quadControlPoint);
			pathPoints.add(b5);
			currentSubPath.add(b5);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#curvetoQuadraticSmoothRel(float, float)
	 */
	public void curvetoQuadraticSmoothRel(float x, float y) throws ParseException {
		if (verbose)
			System.out.println("curvetoQuadraticSmoothRel: " + x + "," + y);
		
		Vertex lastPoint = pathPoints.getLast();
		if (lastPoint instanceof BezierVertex && cubicBezVertTOQuadricControlPoint.get(lastPoint) != null){
			Vertex lastEndPoint = new Vertex(pathPoints.getLast().getX(), pathPoints.getLast().getY(), pathPoints.getLast().getZ());

			//Get control point of last QuadTo
			Vertex lastQuadControlPoint = cubicBezVertTOQuadricControlPoint.get(lastPoint);
			cubicBezVertTOQuadricControlPoint.remove(lastPoint);

			//Rotate that controlpoint around the end point of the last QuadTo
			lastQuadControlPoint.rotateZ(lastEndPoint, 180); 

			//Put in startPoint = last QuadTo Endpoint of this smoothQuadTo, the calculated control point, and the endpoint of smoothQuadTo 
			BezierVertex b5 = ToolsGeometry.getCubicFromQuadraticCurve(
					lastEndPoint, 
					lastQuadControlPoint , 
					new Vertex(lastPoint.getX() + x, lastPoint.getY() + y, 0)); 

			//Save last quad control point
			cubicBezVertTOQuadricControlPoint.put(b5, lastQuadControlPoint);
			pathPoints.add(b5);
			currentSubPath.add(b5);
		}else{
			if (verbose)
				System.out.println("couldnt get last control point at curvetoQuadraticSmoothRel - using last point as the control point");
			
			Vertex lastEndPoint 	= new Vertex(lastPoint.getX(),lastPoint.getY(),0);
			Vertex quadControlPoint =  new Vertex(lastPoint.getX(),lastPoint.getY(),0);
			
			BezierVertex b5 = ToolsGeometry.getCubicFromQuadraticCurve(
					lastEndPoint, 
					quadControlPoint , 
					new Vertex(lastPoint.getX() + x, lastPoint.getY() + y, 0)); 
			
			cubicBezVertTOQuadricControlPoint.put(b5, quadControlPoint);
			pathPoints.add(b5);
			currentSubPath.add(b5);
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#curvetoCubicAbs(float, float, float, float, float, float)
	 */
	public void curvetoCubicAbs(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
		if (verbose)
			System.out.println("curvetoCubicAbs x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2 +  " x:" + x+ " y:" + y);

		BezierVertex b = new BezierVertex(x1,y1,0, x2,y2,0, x,y,0);
		pathPoints.add(b);
		currentSubPath.add(b);
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#curvetoCubicRel(float, float, float, float, float, float)
	 */
	public void curvetoCubicRel(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
		if (verbose)
			System.out.println("curvetoCubicSmoothRel: " + x1 + "," + y1 + "  " + x2 + "," + y2 + "  "  + x + "," + y);

		Vertex lastPoint = pathPoints.getLast();
		BezierVertex b = new BezierVertex(
				lastPoint.getX()+ x1, lastPoint.getY() + y1,0, 
				lastPoint.getX()+ x2, lastPoint.getY() + y2,0, 
				lastPoint.getX()+ x, lastPoint.getY() + y,0);
		pathPoints.add(b);
		currentSubPath.add(b);
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#curvetoCubicSmoothAbs(float, float, float, float)
	 */
	public void curvetoCubicSmoothAbs(float x2, float y2, float x, float y) throws ParseException {
		if (verbose)
			System.out.println("curvetoCubicSmoothAbs x2:" + x2 + " y2:" + y2 + " x:" + x+ " y:" + y);

		Vertex lastPoint = pathPoints.getLast();
		if (lastPoint instanceof BezierVertex){
			BezierVertex lastBez = (BezierVertex)lastPoint;

			Vertex lastConPointCopy = (Vertex)lastBez.getSecondCtrlPoint().getCopy();
			//reflect the last controlpoint at the current point
			lastConPointCopy.rotateZ(lastPoint, 180);
			BezierVertex b = new BezierVertex(lastConPointCopy.getX(),lastConPointCopy.getY(),0, x2,y2,0, x,y,0);

			pathPoints.add(b);
			currentSubPath.add(b);
		}else{
			if (verbose)
				System.out.println("Couldnt get last controlpoint at: curvetoCubicSmoothAbs - using last point as first controlpoint");
			
			Vertex lastEndPoint = new Vertex(lastPoint.getX(),lastPoint.getY(),0);
			BezierVertex b = new BezierVertex(
					lastEndPoint.getX(),lastEndPoint.getY(),0, 
					x2,y2,0,
					x,y,0);
			
			pathPoints.add(b);
			currentSubPath.add(b);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#curvetoCubicSmoothRel(float, float, float, float)
	 */
	public void curvetoCubicSmoothRel(float x2, float y2, float x, float y) throws ParseException {
		if (verbose)
			System.out.println("curvetoCubicSmoothRel: " + x2 + "," + y2 + "  " + x + "," + y);
		
		Vertex lastPoint = pathPoints.getLast();
		if (lastPoint instanceof BezierVertex){
			BezierVertex lastBez = (BezierVertex)lastPoint;

			Vertex lastConPointCopy = (Vertex)lastBez.getSecondCtrlPoint().getCopy();
			//reflect the last controlpoint at the current point
			lastConPointCopy.rotateZ(lastPoint, 180);
			
			BezierVertex b = new BezierVertex(
					lastConPointCopy.getX()	,	lastConPointCopy.getY(),	0, 
					lastPoint.getX() + x2,  	lastPoint.getY() + y2,		0, 
					lastPoint.getX() + x,   	lastPoint.getY() + y,			0);

			pathPoints.add(b);
			currentSubPath.add(b);
		}else{
			if (verbose)
				System.out.println("Couldnt get last controlpoint at: curvetoCubicSmoothRel - using last point as first controlpoint");
			
			Vertex lastEndPoint = new Vertex(lastPoint.getX(),lastPoint.getY(),0);
			BezierVertex b 		= new BezierVertex(
					lastEndPoint.getX(),lastEndPoint.getY(),0, 
					lastEndPoint.getX()+ x2, lastEndPoint.getY()+ y2, 0,
					lastEndPoint.getX()+ x, lastEndPoint.getY()+ y, 0);
			
			pathPoints.add(b);
			currentSubPath.add(b);
		}
	}

	
	/**
	 * if "Z" is encountered.
	 * 
	 * @throws ParseException the parse exception
	 */
	public void closePath() throws ParseException {
		if (verbose)
			System.out.println("close Path");
		
		//Close the current contour with the previous MoveTo Vertex
		Vertex lastPointCopy = (Vertex)currentSubPath.get(0).getCopy();
		currentSubPath.add(lastPointCopy);
		pathPoints.add(lastPointCopy);
		
		//Save the current contour and clear the current for the next contour
		Vertex[] currentSplitPathArr = currentSubPath.toArray(new Vertex[currentSubPath.size()]);
		subPaths.add(currentSplitPathArr);
		currentSubPath.clear();
	}

	/* (non-Javadoc)
	 * @see org.apache.batik.parser.PathHandler#endPath()
	 */
	public void endPath() throws ParseException {
		if (verbose)
			System.out.println("End Path");

		//IF no Z command occured, were normally the last contour gets added, 
		//we have save the current contour here, but we dont close it
		if (!currentSubPath.isEmpty()){
			//Convert partial path list to array
			Vertex[] currentSplitPathArr = currentSubPath.toArray(new Vertex[currentSubPath.size()]);

			//Add partial path array to list of all partial paths of this glyph
			subPaths.add(currentSplitPathArr);
			currentSubPath.clear();
		}
	}


	/**
	 * Gets the path points.
	 * 
	 * @return the path points
	 */
	public LinkedList<Vertex> getPathPoints() {
		return pathPoints;
	}
	
	/**
	 * Gets the path points array.
	 * 
	 * @return the path points array
	 */
	public Vertex[] getPathPointsArray() {
		return pathPoints.toArray(new Vertex[pathPoints.size()]);
	}

	/**
	 * @deprecated Only used when the intention is to draw the shapes using the stencil buffer..
	 * 
	 * @return the reverse move to vertices
	 */
	public Vertex[] getReverseMoveToVertices() {
		return reverseMoveToStack.toArray(new Vertex[reverseMoveToStack.size()]);
	}

	/**
	 * @deprecated Only used when the intention is to draw the shapes using the stencil buffer..
	 * @return the reverse move to stack
	 */
	public Stack<Vertex> getReverseMoveToStack() {
		return reverseMoveToStack;
	}

	/**
	 * Returns all the encountered "sub-paths" of the parsed path-element.
	 * 
	 * @return the contours
	 */
	public ArrayList<Vertex[]> getContours() {
		return subPaths;
	}

	/**
	 * Sets the verbose.
	 * 
	 * @param verbose the new verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	

}
