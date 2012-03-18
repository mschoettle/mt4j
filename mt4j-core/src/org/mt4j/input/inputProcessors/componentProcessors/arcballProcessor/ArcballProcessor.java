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
package org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor;

import java.util.List;

import org.mt4j.components.MTComponent;
import org.mt4j.components.bounds.BoundingSphere;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Quaternion;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * The Class ArcballProcessor. Fires ArcBallGestureEvent events.
 * 
 * @author Christopher Ruff
 */
public class ArcballProcessor extends AbstractCursorProcessor {
	private PApplet applet;
	private Matrix identityDummy;
	private BoundingSphere bSphere;
	
	private MTComponent shape;
	
	private float sizeScaled = 1;
	
	private IArcball ac;
	
	/**
	 * Instantiates a new arcball processor.
	 * 
	 * @param applet the applet
	 * @param shape the shape
	 */
	public ArcballProcessor(PApplet applet, AbstractShape shape){
		this(applet, shape, new BoundingSphere(shape));
	}
	
	
	public ArcballProcessor(PApplet applet, MTComponent component, BoundingSphere bSphere){
		this.applet = applet;
		
		if (identityDummy == null)
			identityDummy = new Matrix();
		
		this.bSphere = bSphere;
		this.bSphere.setRadius(bSphere.getRadius() * sizeScaled);
		this.shape = component;
//		((BoundingSphere)shape.getBoundingShape()).setRadius(((BoundingSphere)shape.getBoundingShape()).getRadius()*2);
		
		this.ac = null;
		
		this.setLockPriority(1);
		
		logger.debug("Bounding sphere center: " + bSphere.getCenter() + " Radius: " + bSphere.getRadius());
	}

	
	
	@Override
	public void cursorStarted(InputCursor m, AbstractCursorInputEvt positionEvent) {
		InputCursor[] theLockedCursors = getLockedCursorsArray();
		//if gesture isnt started and no other cursor on comp is locked by higher priority gesture -> start gesture
		if (theLockedCursors.length == 0 && this.canLock(getCurrentComponentCursorsArray())){ 
			if (this.canLock(m)){//See if we can obtain a lock on this cursor (depends on the priority)
				this.getLock(m);
				ac = new MyArcBall(m);
				logger.debug(this.getName() + " successfully locked cursor (id:" + m.getId() + ")");
				this.fireGestureEvent(new ArcBallGestureEvent(this, ArcBallGestureEvent.GESTURE_STARTED, positionEvent.getCurrentTarget(), identityDummy));
			}
		}
	}


	@Override
	public void cursorUpdated(InputCursor m, AbstractCursorInputEvt positionEvent) {
		if (getLockedCursors().contains(m)){
			Matrix mat = ac.getNewRotation(m);
			this.fireGestureEvent(new ArcBallGestureEvent(this, ArcBallGestureEvent.GESTURE_UPDATED, positionEvent.getCurrentTarget(), mat));
		}
	}
	
	
	@Override
	public void cursorEnded(InputCursor c, AbstractCursorInputEvt positionEvent) {
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - cursor: " + c.getId());
		if (getLockedCursors().contains(c)){ //cursors was a actual gesture cursors
			//Check if we can resume the gesture with another cursor
			InputCursor[] availableCursors = getFreeComponentCursorsArray();
			if (availableCursors.length > 0 && this.canLock(getCurrentComponentCursorsArray())){ 
				InputCursor otherCursor = availableCursors[0]; 
				ac = new MyArcBall(otherCursor);
				this.getLock(otherCursor);
			}else{
				this.fireGestureEvent(new ArcBallGestureEvent(this, ArcBallGestureEvent.GESTURE_ENDED, positionEvent.getCurrentTarget(), identityDummy));
			}
		}
	}
	
	
	

	@Override
	public void cursorLocked(InputCursor c, IInputProcessor lockingAnalyzer) {
		if (lockingAnalyzer instanceof AbstractComponentProcessor){
			logger.debug(this.getName() + " Recieved cursor LOCKED by (" + ((AbstractComponentProcessor)lockingAnalyzer).getName()  + ") - cursor ID: " + c.getId());
		}else{
			logger.debug(this.getName() + " Recieved cursor LOCKED by higher priority signal - cursor ID: " + c.getId());
		}

		this.fireGestureEvent(new ArcBallGestureEvent(this, ArcBallGestureEvent.GESTURE_CANCELED, c.getCurrentTarget(), identityDummy));
		logger.debug(this.getName() + " cursor:" + c.getId() + " cursor LOCKED. Was an active cursor in this gesture!");
	}



	@Override
	public void cursorUnlocked(InputCursor c) {
		logger.debug(this.getName() + " Recieved UNLOCKED signal for cursor ID: " + c.getId());

		List<InputCursor> locked = getLockedCursors();
		if (locked.size() >= 1)
			return;
		
		if (getFreeComponentCursors().size() > 0 && this.canLock(getCurrentComponentCursorsArray())){ 
			ac = new MyArcBall(c);
			this.getLock(c);
			this.fireGestureEvent(new ArcBallGestureEvent(this, ArcBallGestureEvent.GESTURE_RESUMED, c.getCurrentTarget(), identityDummy));
		}
	}

	
	/**
	 * 
	 * @author Chris
	 *
	 */
	public interface IArcball{
		public Matrix getNewRotation(InputCursor m);
	}
	
	
	/**
	 * 
	 * @author Chris
	 *
	 */
	private class MyArcBall implements IArcball{
		private Vector3D lastPoint;
		
		private Quaternion q;
		private Matrix returnMatrix;
		private InputCursor m;
		
		private boolean doInWorldCoords = true;
		
		private boolean camInSphere = false;

		private float camDistToInterSection;
		
		public MyArcBall(InputCursor m){
			this.m = m;
			
			lastPoint = getSphereIntersectionObjSpace();
			
			if (lastPoint == null){
				lastPoint = new Vector3D(); //TODO hack: we should abort the gesture 
			}
			
			// TEST
			if (doInWorldCoords) 
				lastPoint.transform(shape.getGlobalMatrix());
			
			q = new Quaternion();
			returnMatrix = new Matrix();
			
			camDistToInterSection = 1;
		}
		
		
		public Matrix getNewRotation(InputCursor m){
			returnMatrix.loadIdentity();
			
			Vector3D newInterSection = getSphereIntersectionObjSpace();
	    	
	    	if (newInterSection != null){
	    		if (doInWorldCoords)
	    		newInterSection.transform(shape.getGlobalMatrix());
	    		
	    		logger.debug("Sphere hit, hitpoint: " + newInterSection);
	    		
	    		Vector3D center = bSphere.getCenterPointLocal();
	    		
	    		if (doInWorldCoords) //TODO center world cachen?
	    		center.transform(shape.getGlobalMatrix());
		    	
		    	Vector3D a = lastPoint.getSubtracted(center);
		    	Vector3D b = newInterSection.getSubtracted(center);
		    	
		    	//float dot = a.dot(b);
//		    	float angle = Vector3D.angleBetween(a, b);
		    	float angle = (float)myAngleBetween(a, b);
//		    	angle *= sizeScaled * 1.5f;
		    	
		    	Vector3D rotationAxis = a.crossLocal(b);
		    	
		    	//Inverse the angle if we are inside the boundingsphere and 
		    	//hit the inner side
		    	if (camInSphere){ //we hit the backside of the boundingsphere, have to invert direction
//		    		angle *= -1;
//		    		rotationAxis.rotateZ(PApplet.radians(180)); //better than angle*-1
		    		rotationAxis.rotateZ(ToolsMath.PI); //better than angle*-1
//		    		rotationAxis.rotateX(PApplet.radians(180)); //better than angle*-1
//		    		rotationAxis.scaleLocal(-1); //like angle*-1
//		    		rotationAxis.rotateAroundAxisLocal(rotationAxis, PApplet.radians(90));
		    	}
		    	rotationAxis.normalizeLocal(); 
		    	
		    	//TODO map points that didnt intersect to sphere 
		    	
		    	//TODO measure distance from cam to sphere intersection point and multiply angle
		    	//so that if distance big -> less angle, if distance small -> more angle
//		    	System.out.println("Distance Camera to Sphere Intersection: " + camDistToInterSection);
//		    	angle *= 1+ 1/camDistToInterSection;
		    	float dist = Vector3D.distance(shape.getViewingCamera().getPosition() , newInterSection); 
//		    	System.out.println("Dist: " + dist + " Angle: " + angle);
		    	//Hack to make rotation faster if near sphere
		    	float angleScaleFactor = 500f / dist;
		    	if (angleScaleFactor < 1.5f)
		    		angleScaleFactor = 1.5f;
		    	if (angleScaleFactor > 80f)
		    		angleScaleFactor = 80f;
//		    	System.out.println("Angle Scale factor: " + angleScaleFactor);
		    	angle *= angleScaleFactor;
//		    	System.out.println();
		    	
		    	
//		    	logger.debug("New hitpoint" + NewPt);
//		    	logger.debug("Axis: " + cross);
//		    	logger.debug("Angle: " +angle + " \n");
		    	
//		    	q.fromAngleNormalAxis(angle, cross);
//		    	q.toRotationMatrix(returnMatrix);
		    	
		    	returnMatrix.fromAngleNormalAxis(angle, rotationAxis);
		    	//logger.debug(returnMatrix);
		    	
		    	this.lastPoint.setValues(newInterSection);
		    	
		    	//TODO why often invalid matrix? because we didnt norm the sphere to -1..1?
		    	if (!returnMatrix.isValid()) {
//		    		logger.debug("NaN");
		    		returnMatrix.loadIdentity();
		    		return returnMatrix;
		    	}
		    	
//		    	returnMatrix = Matrix.getZRotationMatrix(new Vector3D(), 1);
//		    	Matrix.toRotationAboutPointMatrixAndInverse(returnMatrix, null, new Vector3D());
		    	
		    	//to rotate relative to world transform rotation point
		    	if (doInWorldCoords)
		    	center = MTComponent.getGlobalVecToParentRelativeSpace(shape, center); 
		    	
		    	//To rotate about the center of the object
		    	Matrix.toRotationAboutPoint(returnMatrix, center);
	    	}else{
	    		logger.debug("Sphere wasnt hit!");
	    	}
	    	return returnMatrix;
	    }
		
		
		private double myAngleBetween(Vector3D a, Vector3D b) {
			float dot = a.dot(b);
	        double theta = Math.acos(dot / (length(a) * (length(b)) ));
	        return theta;
		}

		/**
	     * Calculate the magnitude (length) of the vector.
	     * 
	     * @return      the magnitude of the vector
	     */
	    public double length(Vector3D v) {
	        return  Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z);
	    }
	    
		private Vector3D getSphereIntersectionObjSpace(){
//			Icamera cam = shape.getAncestor().getGlobalCam();
//			
//	    	Vector3D rayStartPoint = cam.getPosition();
//	    	Vector3D pointInRayDir = Tools3D.unprojectScreenCoords(applet, m.getLastEvent().getPositionX(), m.getLastEvent().getPositionY());
//	    	
//	    	Ray orgRay = new Ray(rayStartPoint, pointInRayDir);
	    	
	    	Ray realRayForThisObj = Tools3D.getCameraPickRay(applet, shape, m.getCurrentEvent().getX(), m.getCurrentEvent().getY());
	    		
//	    	Ray realRayForThisObj = Tools3D.toComponentCameraPickRay(applet, shape, orgRay);
			
			//TRIAL
			Ray invertedRay = Ray.getTransformedRay(realRayForThisObj, shape.getGlobalInverseMatrix());
	    	
			Vector3D is = bSphere.getIntersectionLocal(invertedRay); 
			
			//Test to detect whether were inside the sphere
//			Vector3D camPos = cam.getPosition();
			Vector3D camPos = shape.getViewingCamera().getPosition();
			camPos.transform(shape.getGlobalInverseMatrix());
			
			if (is != null){
				camDistToInterSection = Vector3D.distance(camPos, is); 
				//bSphere.distanceToEdge(camPos);
			}

            camInSphere = bSphere.containsPointLocal(camPos);
			return is;
		}
		
	}
	

	
	
	
	
	
	
//	private class ArcBallContext  implements IArcball{
//		 private static final float Epsilon = 1.0e-5f;
//
//		 Quaternion q;
//		 Vector3D StVec;          //Saved click vector
//		    Vector3D EnVec;          //Saved drag vector
//		    float adjustWidth;       //Mouse bounds width
//		    float adjustHeight;      //Mouse bounds height
//
//		    public ArcBallContext(float NewWidth, float NewHeight) {
//		        StVec = new Vector3D();
//		        EnVec = new Vector3D();
//		        setBounds(NewWidth, NewHeight);
//		        q = new Quaternion();
//		    }
//
//		    public void mapToSphere(Vector3D point, Vector3D outVector) {
//		        //Copy paramter into temp point
//		        Vector3D tempPoint = new Vector3D(point.x, point.y, point.z);
//
//		        //Adjust point coords and scale down to range of [-1 ... 1]
//		        tempPoint.x = (tempPoint.x * this.adjustWidth) - 1.0f;
//		        tempPoint.y = 1.0f - (tempPoint.y * this.adjustHeight);
//
//		        //Compute the square of the length of the vector to the point from the center
//		        float length = (tempPoint.x * tempPoint.x) + (tempPoint.y * tempPoint.y);
//
//		        //If the point is mapped outside of the sphere... (length > radius squared)
//		        if (length > 1.0f) {
//		            //Compute a normalizing factor (radius / sqrt(length))
//		            float norm = (float) (1.0 / Math.sqrt(length));
//
//		            //Return the "normalized" vector, a point on the sphere
//		            outVector.x = tempPoint.x * norm;
//		            outVector.y = tempPoint.y * norm;
//		            outVector.z = 0.0f;
//		        } else  {   //Else it's on the inside
//		            //Return a vector to a point mapped inside the sphere sqrt(radius squared - length)
//		            outVector.x = tempPoint.x;
//		            outVector.y = tempPoint.y;
//		            outVector.z = (float) Math.sqrt(1.0f - length);
//		        }
//		    }
//
//		    
//		    public void setBounds(float NewWidth, float NewHeight) {
//		        assert((NewWidth > 1.0f) && (NewHeight > 1.0f));//TODO REMOVE
//
//		        //Set adjustment factor for width/height
//		        adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
//		        adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
//		    }
//
//		    //Mouse down
//		    public void click(Vector3D NewPt) {
//		        mapToSphere(NewPt, this.StVec);
//		    }
//		    
//		  //Mouse down
//		    public void click(InputCursor m){
////		    	Icamera cam = shape.getAncestor().getGlobalCam();
////		    	Vector3D rayStartPoint = cam.getPosition();
////		    	Vector3D pointInRayDir = Tools3D.unprojectScreenCoords(applet, m.getLastEvent().getPositionX(), m.getLastEvent().getPositionY());
////		    	Ray orgRay = new Ray(rayStartPoint, pointInRayDir);
////		    	Ray realRayForThisObj = Tools3D.toComponentCameraPickRay(applet, shape, orgRay);
//				
//		    	Ray realRayForThisObj = Tools3D.getCameraPickRay(applet, shape, m.getCurrentEvent().getX(), m.getCurrentEvent().getY());
//		    	
//				//TRIAL
//				Ray invertedRay = Ray.getTransformedRay(realRayForThisObj, shape.getGlobalInverseMatrix());
//		    	
//		    	Vector3D NewPt = bSphere.getIntersectionLocal(invertedRay);
//		    	
//		    	
//		    	if (NewPt != null){
//		    		PGraphicsOpenGL pgl = ((PGraphicsOpenGL)applet.g); 
//					GL gl 	= pgl.beginGL();  
//						gl.glPushMatrix();
//							gl.glMultMatrixf(shape.getGlobalMatrix().toFloatBuffer());
//							NewPt = Tools3D.projectGL(gl, pgl.glu, NewPt, NewPt);
//						gl.glPopMatrix();
//			    	pgl.endGL();
//			    	
//		    		this.mapToSphere(NewPt, this.StVec);
//		    	}else{
//		    		logger.error(getName() + " Didnt hit sphere!");
//		    	}
//		    }
//		    
//		    public Matrix getNewRotation(InputCursor m){
////		    	Icamera cam = shape.getAncestor().getGlobalCam();
////		    	Vector3D rayStartPoint = cam.getPosition();
////		    	Vector3D pointInRayDir = Tools3D.unprojectScreenCoords(applet, m.getLastEvent().getPositionX(), m.getLastEvent().getPositionY());
////		    	Ray orgRay = new Ray(rayStartPoint, pointInRayDir);
////		    	Ray realRayForThisObj = Tools3D.toComponentCameraPickRay(applet, shape, orgRay);
//				
//		    	Ray realRayForThisObj = Tools3D.getCameraPickRay(applet, shape, m.getCurrentEvent().getX(), m.getCurrentEvent().getY());
//		    	
//				//TRIAL
//				Ray invertedRay = Ray.getTransformedRay(realRayForThisObj, shape.getGlobalInverseMatrix());
//		    	
//		    	Vector3D NewPt = bSphere.getIntersectionLocal(invertedRay);
//		    	
//		    	if (NewPt != null){
//		    		PGraphicsOpenGL pgl = ((PGraphicsOpenGL)applet.g); 
//					GL gl 	= pgl.beginGL();  
//						gl.glPushMatrix();
//							gl.glMultMatrixf(shape.getGlobalMatrix().toFloatBuffer());
//							NewPt = Tools3D.projectGL(gl, pgl.glu, NewPt, NewPt);
//						gl.glPopMatrix();
//			    	pgl.endGL();
//			    	
//		    		logger.debug(NewPt);
//		    		this.drag(NewPt, q);
//		    	}else{
//		    		return Matrix.get4x4Identity();
//		    	}
//		    	
//		    	return q.toRotationMatrix();
//		    }
//
//		    //Mouse drag, calculate rotation
//		    public void drag(Vector3D NewPt, Quaternion NewRot) {
////		    	this.EnVec.setValues(NewPt);
//		    	
//		        //Map the point to the sphere
//		        this.mapToSphere(NewPt, EnVec);
//
//		        //Return the quaternion equivalent to the ration
//		        if (NewRot != null) {
////		            Vector3D Perp = new Vector3D();
//
//		            //Compute the vector perpendicular to the begin and end vectors
////		            Vector3D.cross(Perp, StVec, EnVec);
//		            Vector3D Perp = StVec.getCross(EnVec);
//
//		            //Compute the length of the perpendicular vector
//		            if (Perp.length() > Epsilon){    //if its non-zero
//		                //We're ok, so return the perpendicular vector as the transform after all
//		                NewRot.x = Perp.x;
//		                NewRot.y = Perp.y;
//		                NewRot.z = Perp.z;
//		                //In the quaternion values, w is cosine (theta / 2), where theta is rotation angle
////		                NewRot.w = Vector3D.dot(StVec, EnVec);
//		                NewRot.w = StVec.dot(EnVec);
//		            } else  {                                  //if its zero
//		                //The begin and end vectors coincide, so return an identity transform
//		                NewRot.x = NewRot.y = NewRot.z = NewRot.w = 0.0f;
//		            }
//		        }
//		    }
//
//
//		
//		
//	}
//	
//	
	
	
//	private class ArcBall {
//
//		  PApplet parent;
//		  
//		  float center_x, center_y, center_z, radius;
//		  Vector3D v_down, v_drag;
//		  Quaternion q_now, q_down, q_drag;
//		  Vector3D[] axisSet;
//		  int axis;
//
//		  /** defaults to radius of min(width/2,height/2) and center_z of -radius */
//		  public ArcBall(PApplet parent) {
//		    this(parent.g.width/2.0f,parent.g.height/2.0f,-PApplet.min(parent.g.width/2.0f,parent.g.height/2.0f),PApplet.min(parent.g.width/2.0f,parent.g.height/2.0f), parent);
//		  }
//
//		  public ArcBall(float center_x, float center_y, float center_z, float radius, PApplet parent) {
//
//		    this.parent = parent;
//
//		    parent.registerMouseEvent(this);
//		    parent.registerPre(this);
//
//		    this.center_x = center_x;
//		    this.center_y = center_y;
//		    this.center_z = center_z;
//		    this.radius = radius;
//
//		    v_down = new Vector3D();
//		    v_drag = new Vector3D();
//
//		    q_now = new Quaternion();
//		    q_down = new Quaternion();
//		    q_drag = new Quaternion();
//
//		    axisSet = new Vector3D[] { 
//		      new Vector3D(1.0f, 0.0f, 0.0f), new Vector3D(0.0f, 1.0f, 0.0f), new Vector3D(0.0f, 0.0f, 1.0f) };
//		    axis = -1;  // no constraints...
//		  }
//
//		  public void mouseEvent(MouseEvent event) {
//		    int id = event.getID();
//		    if (id == MouseEvent.MOUSE_DRAGGED) {
//		      mouseDragged();
//		    } 
//		    else if (id == MouseEvent.MOUSE_PRESSED) {
//		      mousePressed();
//		    }
//		  }
//		  
//		  public Matrix drag(InputCursor m){
//		   //TODO
//			  Icamera cam = shape.getAncestor().getGlobalCam();
//		    	
//		    	Vector3D rayStartPoint = cam.getPosition();
//		    	Vector3D pointInRayDir = Tools3D.unprojectScreenCoords(applet, m.getLastEvent().getPositionX(), m.getLastEvent().getPositionY());
//		    	
//		    	Ray orgRay = new Ray(rayStartPoint, pointInRayDir);
//		    		
//		    	Ray realRayForThisObj = Tools3D.getRealPickRay(shape, orgRay);
//				
//				//TRIAL
//				Ray invertedRay = Ray.getTransformedRay(realRayForThisObj, shape.getAbsoluteWorldToLocalMatrix());
//		    	
//		    	Vector3D NewPt = bSphere.getIntersectionPoint(invertedRay);
//		    	
//		    	if (NewPt != null){
//		    		logger.debug(NewPt);
//		    		this.mouseDragged();
//		    		
////		    		this.drag(NewPt, q);
//		    	}else{
//		    		return Matrix.get4x4Identity();
//		    	}
//		  }
//
//		  public void mousePressed() {
//		    v_down = mouse_to_sphere(parent.mouseX, parent.mouseY);
//		    q_down.set(q_now);
////		    q_drag.reset();
//		    q_drag.loadIdentity();
//		  }
//
//		  
//		  public void mouseDragged(float x, float y) {
//			    v_drag = mouse_to_sphere(y, y);
////			    q_drag.set(Vector3D.dot(v_down, v_drag), Vector3D.cross(v_down, v_drag));
//			    q_drag.set(v_down.dot(v_drag), v_down.getCross(v_drag));
//			  }
//		  
////		  public void mouseDragged() {
////		    v_drag = mouse_to_sphere(parent.mouseX, parent.mouseY);
//////		    q_drag.set(Vector3D.dot(v_down, v_drag), Vector3D.cross(v_down, v_drag));
////		    q_drag.set(v_down.dot(v_drag), v_down.getCross(v_drag));
////		  }
//
//		  public void pre() {
//		    parent.translate(center_x, center_y, center_z);
////		    q_now = Quaternion.mul(q_drag, q_down);
//		    q_now = q_drag.mult(q_down);
//		    	
//		    applyQuaternion2Matrix(q_now);
//		    parent.translate(-center_x, -center_y, -center_z);
//		  }
//
//		  Vector3D mouse_to_sphere(float x, float y) {
//		    Vector3D v = new Vector3D();
//		    v.x = (x - center_x) / radius;
//		    v.y = (y - center_y) / radius;
//
//		    float mag = v.x * v.x + v.y * v.y;
//		    if (mag > 1.0f) {
////		      v.normalize();
//		    	v.normalizeLocal();
//		    }
//		    else {
//		      v.z = PApplet.sqrt(1.0f - mag);
//		    }
//
//		    return (axis == -1) ? v : constrain_vector(v, axisSet[axis]);
//		  }
//
//		  Vector3D constrain_vector(Vector3D vector, Vector3D axis) {
//		    Vector3D res = new Vector3D();
////		    res.sub(vector, Vector3D.mul(axis, Vector3D.dot(axis, vector)));
//		    res.subtractLocal(axis.getScaled(axis.dot(vector)));
//		    
////		    res.normalize();
//		    res.normalizeLocal();
//		    return res;
//		  }
//
//		  void applyQuaternion2Matrix(Quaternion q) {
//		    // instead of transforming q into a matrix and applying it...
//
//		    float[] aa = q.getValue();
//		    parent.rotate(aa[0], aa[1], aa[2], aa[3]);
//		  }
//
//	}
	
	@Override
	public String getName() {
		return "Arcball Processor";
	}


}
