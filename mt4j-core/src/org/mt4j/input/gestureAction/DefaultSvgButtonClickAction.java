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
package org.mt4j.input.gestureAction;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;


/**
 * The Class DefaultSvgButtonClickAction.
 * @author Christopher Ruff
 */
public class DefaultSvgButtonClickAction extends DefaultButtonClickAction implements IGestureEventListener {

	/** The width obj space. */
	private Vector3D widthObjSpace;
	
	/** The center obj space. */
	private Vector3D centerObjSpace;
	
	/** The svg comp. */
	private MTComponent svgComp;
	
	/**
	 * Instantiates a new default svg button click action.
	 * 
	 * @param svgComp the svg comp
	 */
	public DefaultSvgButtonClickAction(MTComponent svgComp) { //, MTPolygon largestPolyInSvg
		super(DefaultSvgButtonClickAction.getLargestSvgComp(svgComp, null));
		this.svgComp = svgComp;
		this.width = this.getReferenceComp().getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
		this.widthObjSpace = this.getWidthVectorLocal();
		this.centerObjSpace =  this.getReferenceComp().getCenterPointLocal();
	}
	
	
	/**
	 * Gets the width obj space vector.
	 * 
	 * @return the width obj space vector
	 */
	private Vector3D getWidthVectorLocal(){
		if (this.getReferenceComp().hasBounds()){
			return this.getReferenceComp().getBounds().getWidthXYVectLocal();
		}else{
			throw new RuntimeException("Couldnt extract the width vector from the svg shape: '" + svgComp.getName() + "'. We need a component or boundingshape that defines the method getWidthXYVectObjSpace()");
		}
	}
	
	/**
	 * Returns the component with the largest x,y dimension to use for picking by default and scaling.
	 * 
	 * @param comp the comp
	 * @param compWithBiggestBoundingRect the comp with biggest bounding rect
	 * 
	 * @return the largest svg comp
	 */
	public static AbstractShape getLargestSvgComp(MTComponent comp, AbstractShape compWithBiggestBoundingRect){
		if (comp instanceof AbstractShape) {
			AbstractShape shape = (AbstractShape) comp;
			float rectWidthGlobal = shape.getWidthXY(TransformSpace.GLOBAL);
			float rectHeightGlobal = shape.getHeightXY(TransformSpace.GLOBAL);

			if (
					compWithBiggestBoundingRect != null
			){
//				System.out.println("Fromer biggest != null, checking if " + comp.getName() + " is bigger.");
				if (	rectWidthGlobal 	>= compWithBiggestBoundingRect.getWidthXY(TransformSpace.GLOBAL)//biggestWidth
					|| 	rectHeightGlobal 	>= compWithBiggestBoundingRect.getHeightXY(TransformSpace.GLOBAL)//biggestHeight 
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
			compWithBiggestBoundingRect = getLargestSvgComp(child, compWithBiggestBoundingRect);
		}
		return compWithBiggestBoundingRect;
	}
	
	
	
	@Override
	public MTComponent getCompToResize() {
		return this.svgComp;
	}
	
	@Override
	public float getCurrentUnscaledWidth(){
		Vector3D v = getWidthVectorLocal();
		
		Matrix refCompLocalToWorld = new Matrix(this.getReferenceComp().getGlobalMatrix());
		//Remove translation for direction vectors(width/height)
		refCompLocalToWorld.removeTranslationFromMatrix();
		
		//obj width vect to world space 
		v.transform(refCompLocalToWorld);
		
		Matrix svgButtonAbsInv = new Matrix(this.getCompToResize().getGlobalInverseMatrix());
		//TODO doch wieder localbase von svg dazutransformen?
		//svgbutton inverse parent relative machen
		svgButtonAbsInv.multLocal(this.getCompToResize().getLocalMatrix());
		//Remove translation for direction vectors(width/height)
		svgButtonAbsInv.removeTranslationFromMatrix();
		//Width vect in svgbutton parent relative space
		v.transform(svgButtonAbsInv);
		float width = v.length();
		return width;
	}
	
	
	@Override
	public void resize(float newWidth, float newHeight){ 
		Matrix refCompLocalToWorld = new Matrix(this.getReferenceComp().getGlobalMatrix());
		
		//Center into world space
		Vector3D refCompCenter = this.centerObjSpace.getCopy();
		refCompCenter.transform(refCompLocalToWorld);
		
		//Remove translation for direction vectors(width/height)
		refCompLocalToWorld.removeTranslationFromMatrix();
		
		//Width vect into world space
		Vector3D objSpaceWidth = this.widthObjSpace.getCopy();
		objSpaceWidth.transform(refCompLocalToWorld);
//		System.out.println(" world Width vect of reference component: " + objSpaceWidth);
		
		/////Transform width/height/center to svgbutton relative
		Matrix svgButtonAbsInv = new Matrix(this.getCompToResize().getGlobalInverseMatrix());
		
		//Center in svgbutton relative
		refCompCenter.transform(svgButtonAbsInv);
//		System.out.println("Centerpoint svgRelative: " + refCompCenter);
		
		//TODO doch wieder localbase von svg dazutransformen?
		//svgbutton inverse parent relative machen
		svgButtonAbsInv.multLocal(this.getCompToResize().getLocalMatrix());
		
//		//Center in svgbutton relative
//		refCompCenter.transform(svgButtonAbsInv);
//		System.out.println("Centerpoint svgRelative: " + refCompCenter);
		
		//Remove translation for direction vectors(width/height)
		svgButtonAbsInv.removeTranslationFromMatrix();
		
		//Width vect in svgbutton parent relative
		objSpaceWidth.transform(svgButtonAbsInv);
//		System.out.println(" svgbutton space Width vect of reference component: " + objSpaceWidth);
		
		float width = objSpaceWidth.length();
//		System.out.println("WIDTH: " + width);
//		System.out.println("new width to set: " + newWidth);

//		this.getCompToResize().scale(1/width, 1/width, 1, refCompCenter, TransformSpace.RELATIVE_TO_PARENT);
//		this.getCompToResize().scale(newWidth, newWidth, 1, refCompCenter, TransformSpace.RELATIVE_TO_PARENT);
		
		//Svgbutton so scalen, dass reference comp auf unit width 1 gescaled wird
		this.getCompToResize().scale(newWidth* (1/width),  newWidth* (1/width), 1, refCompCenter, TransformSpace.LOCAL);
		//Svgbutton so scalen, dass reference comp auf unit gew�nschte width gescaled wird
//		this.getCompToResize().scale(newWidth, newWidth, 1, refCompCenter, TransformSpace.LOCAL);
	}
	
	
	

}
