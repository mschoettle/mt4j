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
package org.mt4j.components.visibleComponents.widgets;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.xml.svg.SVGLoader;

import processing.core.PApplet;

/**
 * The Class MTSvg. Loads and displays scalable vector graphics (SVG) files.
 * 
 * @author Christopher Ruff
 */
public class MTSvg extends MTComponent {

	/** The width vect. */
	private Vector3D widthVect;
	
	/** The height vect. */
	private Vector3D heightVect;
	
	/** The width. */
	private float width;
	
	/** The height. */
	private float height;

	/** The center point local. */
	private Vector3D centerPointLocal;
	
	private IBoundingShape bounds;

	//TODO we could extens mtpolygon and set the polygon points to be the svg's bounding rectangle
	
	/**
	 * Instantiates a new mT svg.
	 * 
	 * @param applet the applet
	 * @param fileName the file name
	 */
	public MTSvg(PApplet applet, String fileName) {
		super(applet);
		
		this.setName("SVG: " + fileName);
		this.setComposite(true);
		
		this.registerInputProcessor(new DragProcessor(applet));
		this.addGestureListener(DragProcessor.class, new DefaultDragAction());
		this.registerInputProcessor(new RotateProcessor(applet));
		this.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
		this.registerInputProcessor(new ScaleProcessor(applet));
		this.addGestureListener(ScaleProcessor.class, new DefaultScaleAction());
		
		SVGLoader loader = new SVGLoader(applet);
		MTComponent svg = loader.loadSvg(fileName);
		this.addChild(svg);
		
		//as long as the MTSvg isnt attached to a parent and the transform=identity, 
		//=> world width=parent relative width= local width
		//wenn added to parent -> parentrelative width vect * globalMatrix
		float[] bounds = calcBounds(this, new float[]{Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE});
//		System.out.println("SVG Bounds -> minX: " + bounds[0] + " minY: " + bounds[2] + " maxX: " + bounds[1] + " maxY: " + bounds[3]);
		float minX = bounds[0];
		float minY = bounds[2];
		float maxX = bounds[1];
		float maxY = bounds[3];
		Vector3D upperLeft 	= new Vector3D(minX, minY, 0);
		Vector3D upperRight = new Vector3D(maxX, minY, 0);
		Vector3D lowerRight	= new Vector3D(maxX, maxY, 0);
		
		this.widthVect  	= upperRight.getSubtracted(upperLeft);
		this.heightVect 	= lowerRight.getSubtracted(upperRight);
		this.width 	= maxX-minX;
		this.height = maxY-minY;
		
		this.centerPointLocal = new Vector3D(
				upperLeft.x + ((upperRight.x - upperLeft.x)/2f), 
				upperRight.y + ((lowerRight.y - upperRight.y)/2f),
				0);
		
		//System.out.println("Center local " + centerPointLocal);
		
//		this.addStateChangeListener(StateChange.ADDED_TO_PARENT, new StateChangeListener(){
//			@Override
//			public void stateChanged(StateChangeEvent evt) {
//				Vector3D widthCopy = widthVect.getCopy();
//				
//			}
//		});
		
		//We dont set these bounds using this.setBounds() because then the svg would get picked
		//by the rectangular bounds and not the child shapes, which is more accurate
		this.bounds = new BoundsZPlaneRectangle(this, upperLeft.x, upperLeft.y, width, height);
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting
		this.setDepthBufferDisabled(true);
	}
	
	
	/**
	 * Calc bounds.
	 * 
	 * @param currentComp the current comp
	 * @param bounds the bounds
	 * 
	 * @return the float[]
	 */
	private  float[] calcBounds(MTComponent currentComp, float[] bounds){
		if (currentComp instanceof AbstractShape){
			AbstractShape shape = (AbstractShape)currentComp;
			Vertex[] globalVecs = shape.getVerticesGlobal();
            for (Vertex v : globalVecs) {
                if (v.x < bounds[0])
                    bounds[0] = v.x;
                if (v.x > bounds[1])
                    bounds[1] = v.x;
                if (v.y < bounds[2])
                    bounds[2] = v.y;
                if (v.y > bounds[3])
                    bounds[3] = v.y;
            }
		}
		
		for(MTComponent child : currentComp.getChildren()){
			bounds = calcBounds(child, bounds);
		}
		return bounds;
	}
	
	
	
//	public float getWidthXY(TransformSpace transformSpace) {
//		switch (transformSpace) {
//		case LOCAL:
//			return this.getWidthXYLocal();
//		case RELATIVE_TO_PARENT:
//			return this.getWidthXYRelativeToParent();
//		case GLOBAL:
//			return this.getWidthXYGlobal();
//		default:
//			return -1;
//		}
//	}
	
	
	/**
 * Calculates the width of this shape, by using its
 * bounding rect.
 * Uses the objects local transform. So the width will be
 * relative to the parent only - not the whole world
 * 
 * @return the width xy relative to parent
 * 
 * the width
 */
	public float getWidthXYRelativeToParent() {
		Vector3D p = this.widthVect.getCopy();
		Matrix m = new Matrix(this.getLocalMatrix());
		m.removeTranslationFromMatrix();
		p.transform(m);
		return p.length();
	}
	
	
	/**
	 * Gets the "Width vector" and transforms it to world space, then calculates
	 * its length.
	 * 
	 * @return the width xy global
	 * 
	 * the Width relative to the world space
	 */
	public float getWidthXYGlobal() {
		Vector3D p = this.widthVect.getCopy();
		Matrix m = new Matrix(this.getGlobalMatrix());
		m.removeTranslationFromMatrix();
		p.transform(m);
		return p.length();
	}
	
	
	
//	public float getHeightXY(TransformSpace transformSpace) {
//		switch (transformSpace) {
//		case LOCAL:
//			return this.getHeightXYLocal();
//		case RELATIVE_TO_PARENT:
//			return this.getHeightXYRelativeToParent();
//		case GLOBAL:
//			return this.getHeightXYGlobal();
//		default:
//			return -1;
//		}
//	}
	
	/**
 * Gets the "height vector" and transforms it to parent relative space, then calculates
 * its length.
 * 
 * @return the height xy relative to parent
 * 
 * the height relative to its parent space frame
 */
	public float getHeightXYRelativeToParent() {
		Vector3D p = this.heightVect.getCopy();
		Matrix m = new Matrix(this.getLocalMatrix());
		m.removeTranslationFromMatrix();
		p.transform(m);
		return p.length();
	}
	
	/**
	 * Gets the "height vector" and transforms it to world space, then calculates
	 * its length.
	 * 
	 * @return the height xy global
	 * 
	 * the height relative to the world space
	 */
	public float getHeightXYGlobal() {
		Vector3D p = this.heightVect.getCopy();
		Matrix m = new Matrix(this.getGlobalMatrix());
		m.removeTranslationFromMatrix();
		p.transform(m);
		return p.length();
	}
	
	/**
	 * Scales the shape to the given height relative to parent space.
	 * Aspect ratio is preserved! The scaling is done Axis aligned, so
	 * shearing might occour if rotated!
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param height the height
	 * 
	 * @return true, if the height isnt negative
	 */
	public boolean setHeightXYRelativeToParent(float height){
		if (height > 0){
			Vector3D centerPoint = this.getCenterPointRelativeToParent();
			float factor = (1f/this.getHeightXYRelativeToParent()) * height;
			this.scale(factor, factor, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	/**
	 * Scales the shape to the given height relative to world space.
	 * Aspect ratio is preserved! The scaling is done Axis aligned, so
	 * shearing might occour if rotated!
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param height the height
	 * 
	 * @return true, if sets the height xy global
	 */
	public boolean setHeightXYGlobal(float height){
		if (height > 0){
			Vector3D centerPoint = this.getCenterPointGlobal();
			float factor = (1f/this.getHeightXYGlobal())* height;
			this.scaleGlobal(factor, factor, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	/**
	 * Scales the shape to the given width relative to parent space.
	 * Aspect ratio is preserved!
	 * <br>NOTE: The scaling is done Axis aligned, so
	 * shearing might occour if rotated before!
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param width the width
	 * 
	 * @return true, if the width isnt negative
	 */
	public boolean setWidthXYRelativeToParent(float width){
		if (width > 0){
			Vector3D centerPoint = this.getCenterPointRelativeToParent();
			float factor = (1f/this.getWidthXYRelativeToParent()) * width;
			this.scale(factor, factor, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	/**
	 * Scales the shape to the given width relative to world space.
	 * Aspect ratio is preserved! The scaling is done Axis aligned, so
	 * shearing might occour if rotated!
	 * <br>Uses the shapes bounding shape for calculation.
	 * 
	 * @param width the width
	 * 
	 * @return true, if sets the width xy global
	 */
	public boolean setWidthXYGlobal(float width){
		if (width > 0){
			Vector3D centerPoint = this.getCenterPointGlobal();
			float factor = (1f/this.getWidthXYGlobal())* width;
			this.scaleGlobal(factor, factor, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	/**
	 * Sets the size xy relative to parent.
	 * 
	 * @param width the width
	 * @param height the height
	 * 
	 * @return true, if successful
	 */
	public boolean setSizeXYRelativeToParent(float width, float height){
		if (width > 0 && height > 0){
			Vector3D centerPoint = this.getCenterPointRelativeToParent();
			this.scale( (1f/this.getWidthXYRelativeToParent()) * width, (1f/this.getHeightXYRelativeToParent()) * height, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	/**
	 * Sets the size xy global.
	 * 
	 * @param width the width
	 * @param height the height
	 * 
	 * @return true, if successful
	 */
	public boolean setSizeXYGlobal(float width, float height){
		if (width > 0 && height > 0){
			Vector3D centerPoint = this.getCenterPointGlobal();
			this.scaleGlobal( (1f/this.getWidthXYGlobal())* width , (1f/this.getHeightXYGlobal()) * height, 1, centerPoint);
			return true;
		}else
			return false;
	}
	
	
	
	
	/**
	 * Gets the center point local.
	 * 
	 * @return the center point local
	 */
	public Vector3D getCenterPointLocal(){
		return this.centerPointLocal.getCopy();
	}
	
	/**
	 * Gets the center point relative to parent.
	 * 
	 * @return the center point relative to parent
	 */
	public Vector3D getCenterPointRelativeToParent(){
		Vector3D c = this.centerPointLocal.getCopy();
		c.transform(this.getLocalMatrix());
		return c;
	}
	
	/**
	 * Gets the center point global.
	 * 
	 * @return the center point global
	 */
	public Vector3D getCenterPointGlobal(){
		Vector3D c = this.centerPointLocal.getCopy();
		c.transform(this.getGlobalMatrix());
		return c;
	}
	
	/**
	 * Sets the global position of the component. (In global coordinates)
	 * <br>Note: The center of this component is the reference point, not the upper left corner.
	 * 
	 * @param pos the pos
	 */
	public void setPositionGlobal(Vector3D pos){
		this.translateGlobal(pos.getSubtracted(this.getCenterPointGlobal()));
	}
	
	/**
	 * Sets the position of the component, relative to its parent component.
	 * <br>Note: The center of this component is the reference point, not the upper left corner.
	 * 
	 * @param pos the pos
	 */
	public void setPositionRelativeToParent(Vector3D pos){
		this.translate(pos.getSubtracted(this.getCenterPointRelativeToParent()), TransformSpace.RELATIVE_TO_PARENT);
	}
	
	
	/**
	 * Returns the component with the largest x,y dimension to use for picking by default.
	 * 
	 * @param comp the comp
	 * @param compWithBiggestBoundingRect the comp with biggest bounding rect
	 * @param biggestWidth the biggest width
	 * @param biggestHeight the biggest height
	 * 
	 * @return the largest svg comp
	 */
	private AbstractShape getLargestSvgComp(MTComponent comp, AbstractShape compWithBiggestBoundingRect, float biggestWidth, float biggestHeight){
////		System.out.println("Checking: " + comp.getName());
		if (comp instanceof AbstractShape) {
			AbstractShape shape = (AbstractShape) comp;
			float rectWidthGlobal = shape.getWidthXY(TransformSpace.GLOBAL);
			float rectHeightGlobal = shape.getHeightXY(TransformSpace.GLOBAL);
			if (
//					compWithBiggestBoundingRect != null
					   biggestWidth > 0 
					&& biggestHeight > 0
			){
//				System.out.println("Fromer biggest != null, checking if " + comp.getName() + " is bigger.");
				if (	rectWidthGlobal 	>= compWithBiggestBoundingRect.getWidthXY(TransformSpace.GLOBAL)
					|| 	rectHeightGlobal 	>= compWithBiggestBoundingRect.getHeightXY(TransformSpace.GLOBAL)
				){
//					System.out.println(comp.getName() + " is bigger!");
					compWithBiggestBoundingRect = shape;
				}else{
//					System.out.println(compWithBiggestBoundingRect.getName() + " is still bigger");
				}
			}else{
//				System.out.println("Reference is null, take " + comp.getName() + " as the new biggest.");
				compWithBiggestBoundingRect = shape;
			}
		}

		for(MTComponent child : comp.getChildren()){
			compWithBiggestBoundingRect = getLargestSvgComp(child, compWithBiggestBoundingRect, biggestWidth, biggestHeight);
		}
		return compWithBiggestBoundingRect;
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.MTComponent#setPickable(boolean)
	 */
	@Override
	public void setPickable(boolean pickable) {
		super.setPickable(pickable);
		setPickableRecursive(this, pickable);
	}
	
	/**
	 * Sets the pickable recursive.
	 * 
	 * @param current the current
	 * @param pickable the pickable
	 */
	private void setPickableRecursive(MTComponent current, boolean pickable){
		if (!current.equals(this))
				current.setPickable(pickable);
        for (MTComponent child : current.getChildren()) {
            setPickableRecursive(child, pickable);
        }
	}
	
	
	@Override
	protected boolean componentContainsPointLocal(Vector3D testPoint) {
		if (this.hasBounds()){
			return this.getBounds().containsPointLocal(testPoint);
		}else{
			return this.bounds.containsPointLocal(testPoint);
		}
	}
}
