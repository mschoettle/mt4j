package org.mt4j.components.bounds;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.util.math.Matrix;

public interface IBoundingShapeMergable extends IBoundingShape {

//	/*
	public IBoundingShapeMergable merge(IBoundingShape shape);
	
	public IBoundingShape transform(Matrix transformMatrix);
	
	public MTComponent getPeerComponent();
	
	public void setPeerComponent(MTComponent peerComponent);
	
	public IBoundingShapeMergable getBoundsTransformed(TransformSpace transformSpace);
//	*/
}
