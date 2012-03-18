package org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor;

import java.util.ArrayList;

import org.mt4j.components.MTComponent;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Cluster3DExt extends MTComponent {

	private MTComponent visualComponentGroup;
	
	private MTComponent currentlySelectedChildren = null;
	
	private IVisualizeMethodProvider visualizeProvider;
	
	private PApplet pApplet;
	public Cluster3DExt(PApplet pApplet) {
		super(pApplet);		
		this.pApplet = pApplet;		
	}
	
	public Cluster3DExt(PApplet pApplet,ArrayList<MTComponent> components)
	{
		super(pApplet);
		this.pApplet = pApplet;
		for (int i = 0; i < components.size(); i++) {
			MTComponent component3D = components.get(i);
			this.addChild(component3D);
			this.setComposite(true);
		}
		
	}	
	
	/**
	 * Transforms the shapes local coordinate space by the given matrix.
	 * 
	 * @param transformMatrix the transform matrix
	 */
	public void transform(Matrix transformMatrix) {
		for (MTComponent c : this.getChildList()){
			c.transform(transformMatrix);
		}
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.transform(transformMatrix);
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#translateGlobal(util.math.Vector3D)
	 */
	public void translateGlobal(Vector3D dirVect) {
		for (MTComponent c : this.getChildList()){
			c.translateGlobal(dirVect);
		}
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{							
				comp.translateGlobal(dirVect);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#translate(util.math.Vector3D)
	 */
	public void translate(Vector3D dirVect) {
		for (MTComponent c : this.getChildList()){
			c.translate(dirVect);
		}
		if(visualComponentGroup!=null)
		{	
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.translate(dirVect);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#xRotateGlobal(util.math.Vector3D, float)
	 */
	public void rotateXGlobal(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateXGlobal(rotationPoint, degree);
		}
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.rotateXGlobal(rotationPoint, degree);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#xRotate(util.math.Vector3D, float)
	 */
	public void rotateX(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateX(rotationPoint, degree);
		}
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.rotateX(rotationPoint, degree);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#yRotateGlobal(util.math.Vector3D, float)
	 */
	public void rotateYGlobal(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateYGlobal(rotationPoint, degree);
		}
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.rotateYGlobal(rotationPoint, degree);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#yRotate(util.math.Vector3D, float)
	 */
	public void rotateY(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateY(rotationPoint, degree);
		}
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.rotateY(rotationPoint, degree);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#zRotateGlobal(util.math.Vector3D, float)
	 */
	public void rotateZGlobal(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateZGlobal(rotationPoint, degree);
		}
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.rotateZGlobal(rotationPoint, degree);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#zRotate(util.math.Vector3D, float)
	 */
	public void rotateZ(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateZ(rotationPoint, degree);
		}
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.rotateZ(rotationPoint, degree);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#scaleGlobal(float, util.math.Vector3D)
	 */
	public void scaleGlobal(float factor, Vector3D scaleReferencePoint) {
		this.scaleGlobal(factor, factor, factor, scaleReferencePoint);
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.scaleGlobal(factor, factor, factor, scaleReferencePoint);
			}
		}
	}
	
	/**
	 * scales the polygon around the scalingPoint, currently dosent support scaling around the Z axis.
	 * 
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * @param scalingPoint the scaling point
	 */
	public void scaleGlobal(float X, float Y, float Z, Vector3D scalingPoint) {
		for (MTComponent c : this.getChildList()){
			c.scaleGlobal(X,  Y,  Z, scalingPoint);
			if(visualComponentGroup!=null)
			{
				for(MTComponent comp : visualComponentGroup.getChildren())
				{
					comp.scaleGlobal(X,  Y,  Z, scalingPoint);
				}
			}

		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.jMT.components.MTBaseComponent#scale(float, util.math.Vector3D)
	 */
	public void scale(float factor, Vector3D scaleReferencePoint) {
		this.scale(factor, factor, factor, scaleReferencePoint);
		if(visualComponentGroup!=null)
		{	
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.scale(factor, factor, factor, scaleReferencePoint);
			}
		}
	}
	
	/**
	 * scales the polygon around the scalingPoint, currently dosent support scaling around the Z axis.
	 * 
	 * @param X the x
	 * @param Y the y
	 * @param Z the z
	 * @param scalingPoint the scaling point
	 */
	public void scale(float X, float Y, float Z, Vector3D scalingPoint) {
		for (MTComponent c : this.getChildList()){
			c.scale(X,  Y,  Z, scalingPoint);
		}
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{
				comp.scale(X,  Y,  Z, scalingPoint);
			}
		}
	}

	public void setVisualComponentGroup(MTComponent v_visualComponentGroup) {
		this.visualComponentGroup = v_visualComponentGroup;
		if(this.visualComponentGroup.isComposite()!=true)
		{
			this.visualComponentGroup.setComposite(true);			
		}
	}

	public MTComponent getVisualComponentGroup() {
		return visualComponentGroup;
	}
	
	@Override
	public void drawComponent(PGraphics g)
	{			
		if(getVisualizeProvider()!=null)
		{
			getVisualizeProvider().visualize(this);
		}
	}

	public void setVisualizeProvider(IVisualizeMethodProvider visualizeProvider) {		
		this.visualizeProvider = visualizeProvider;
	}

	public IVisualizeMethodProvider getVisualizeProvider() {
		return visualizeProvider;
	}
	public void setCurrentlySelectedChildren(MTComponent currentComponent) {
		this.currentlySelectedChildren = currentComponent;
	}

	public MTComponent getCurrentlySelectedChildren() {
		return currentlySelectedChildren;
	}
	/*@Override
	public void preDraw(PGraphics g)	 
	{		
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{				
				comp.preDraw(g);				
			}
		}
	}
	
	@Override
	public void drawComponent(PGraphics g)
	{	
		if(visualComponentGroup!=null)
		{
			//visualComponentGroup.drawComponent(g);
			for(MTComponent comp : visualComponentGroup.getChildren())
			{				
				comp.drawComponent(g);				
			}
		}
	}
	
	@Override
	public void postDraw(PGraphics g)
	{		
		if(visualComponentGroup!=null)
		{
			for(MTComponent comp : visualComponentGroup.getChildren())
			{				
				comp.preDraw(g);				
			}
		}
	}
	*/
}
