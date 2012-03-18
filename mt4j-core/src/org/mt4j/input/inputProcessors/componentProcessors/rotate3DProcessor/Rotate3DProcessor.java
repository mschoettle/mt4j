package org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.PickResult;
import org.mt4j.components.PickResult.PickEntry;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractComponentProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.AbstractCursorProcessor;
import org.mt4j.util.camera.IFrustum;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public class Rotate3DProcessor extends AbstractCursorProcessor {

	private PApplet pApplet;
	
	/** The un used cursors. */
	private List<InputCursor> unUsedCursors;
	
	/** The locked cursors. */
	private List<InputCursor> lockedCursors;
	
	/** The rc. */
	private RotationContext rc;
	
	/** The drag plane normal. */
	private Vector3D dragPlaneNormal;
	
	/** The target Obj */
	private MTComponent targetComp;
	
	/** is rotation paused by collision */
	private boolean gesturePaused;
	
	/** has rotation resumed after pausing from collision*/
	private boolean resumed;
	
	public Rotate3DProcessor(PApplet graphicsContext,MTComponent targetComp){
		this.pApplet = graphicsContext;
		this.unUsedCursors 	= new ArrayList<InputCursor>();
		this.lockedCursors 	= new ArrayList<InputCursor>();
		this.dragPlaneNormal = new Vector3D(0,0,1);
		this.setLockPriority(3);
		this.targetComp = targetComp;
	}
	
	
	@Override
	public void cursorEnded(InputCursor inputCursor, AbstractCursorInputEvt currentEvent) {
		IMTComponent3D comp = currentEvent.getTarget();
		logger.debug(this.getName() + " INPUT_ENDED RECIEVED - MOTION: " + inputCursor.getId());
		
		if (lockedCursors.size() == 3 && lockedCursors.contains(inputCursor)){
			//there must be 3 cursors for a 3d rotation
			InputCursor firstCursor;
			InputCursor secondCursor;
			InputCursor thirdCursor;
			
			//TODO proof if this could be done better
			if (lockedCursors.get(0).equals(inputCursor)){
				firstCursor = inputCursor;
				secondCursor = lockedCursors.get(1);
				thirdCursor = lockedCursors.get(2);
			}else if(lockedCursors.get(1).equals(inputCursor)){
				firstCursor = lockedCursors.get(0);
				secondCursor = inputCursor;
				thirdCursor = lockedCursors.get(2);
			}else
			{
				firstCursor = lockedCursors.get(0);
				secondCursor = lockedCursors.get(1);
				thirdCursor = inputCursor;
			}
			
			lockedCursors.remove(inputCursor);
			ArrayList<InputCursor> leftOverCursors = new ArrayList<InputCursor>();
			leftOverCursors.add(lockedCursors.get(0));
			leftOverCursors.add(lockedCursors.get(1));
			
			if (unUsedCursors.size() > 0){ //Check if there are other cursors we could use for scaling if one was removed
				InputCursor futureCursor = unUsedCursors.get(0);
				if (this.canLock(futureCursor)){ //check if we have priority to claim another cursor and use it
					rc = new RotationContext(futureCursor, leftOverCursors.get(0),leftOverCursors.get(1), comp);
					if (!rc.isGestureAborted()){
						this.getLock(futureCursor);
						unUsedCursors.remove(futureCursor);
						lockedCursors.add(futureCursor);
						logger.debug(this.getName() + " continue with different cursors (ID: " + futureCursor.getId() + ")" + " " + "(ID: " + leftOverCursors.get(0).getId() + ")");
						//TODO fire start evt?
					}else{ //couldnt start gesture - cursor's not on component 
						this.endGesture(leftOverCursors, comp, firstCursor, secondCursor,thirdCursor);
					}
				}else{ //we dont have permission to use other cursor  - End gesture
					this.endGesture(leftOverCursors, comp, firstCursor, secondCursor,thirdCursor);
				}
			}else{ //no more unused cursors on comp - End gesture
				this.endGesture(leftOverCursors, comp, firstCursor, secondCursor,thirdCursor);
			}
			this.unLock(inputCursor); //FIXME TEST
		}else{ //cursor was not a scaling involved cursor
			if (unUsedCursors.contains(inputCursor)){
				unUsedCursors.remove(inputCursor);
			}
			if(lockedCursors.contains(inputCursor))
			{
				lockedCursors.remove(inputCursor);
			}
		}
		
	}
	
	private void endGesture(ArrayList<InputCursor> leftOverCursors, IMTComponent3D component, InputCursor firstCursor, InputCursor secondCursor,InputCursor thirdCursor){
		lockedCursors.clear();
		unUsedCursors.addAll(leftOverCursors);
		Iterator<InputCursor> iter = leftOverCursors.iterator();
		while(iter.hasNext())
		{
			InputCursor iCursor = iter.next();
			this.unLock(iCursor);
		}
		
		this.fireGestureEvent(new Rotate3DEvent(this, MTGestureEvent.GESTURE_ENDED, component, firstCursor, secondCursor,thirdCursor, Vector3D.ZERO_VECTOR, rc.getRotationPoint(), rc.getRotationDirection(),0,0,0,rc.getRotationAxis()));
	}

	@Override
	public void cursorLocked(InputCursor cursor,
			IInputProcessor lockingprocessor) {
		if (lockingprocessor instanceof AbstractComponentProcessor){
			logger.debug(this.getName() + " Recieved MOTION LOCKED by (" + ((AbstractComponentProcessor)lockingprocessor).getName()  + ") - cursor ID: " + cursor.getId());			
		}else{
			logger.debug(this.getName() + " Recieved MOTION LOCKED by higher priority signal - cursor ID: " + cursor.getId());
		}
		
		if (lockedCursors.contains(cursor)){ 
			//cursors was used here! -> we have to stop the gesture
			//put all used cursors in the unused cursor list and clear the usedcursorlist
			unUsedCursors.addAll(lockedCursors); 
			lockedCursors.clear();
			//TODO fire ended evt?
			logger.debug(this.getName() + " cursor:" + cursor.getId() + " MOTION LOCKED. Was an active cursor in this gesture!");
		}
		
	}

	@Override
	public void cursorStarted(InputCursor inputCursor,
			AbstractCursorInputEvt currentEvent) {
		IMTComponent3D comp = currentEvent.getTarget();
		if (lockedCursors.size() >= 3){ //gesture with 3 fingers already in progress
			unUsedCursors.add(inputCursor);
			logger.debug(this.getName() + " has already enough cursors for this gesture - adding to unused ID:" + inputCursor.getId());
		}else{ //no gesture in progress yet
			
			//save current selected Object inside of Cluster3DExt for correct rotation
			if(currentEvent.getTarget() instanceof Cluster3DExt)
			{
				Cluster3DExt cluster = (Cluster3DExt)currentEvent.getTarget();
				MTComponent sourceComponent = (MTComponent) currentEvent.getTarget();
				Vector3D currentPos = new Vector3D(currentEvent.getPosX(),currentEvent.getPosY(),0.0f);				
				MTCanvas parentCanvas = this.getParentCanvas(sourceComponent);
				cluster.setComposite(false);
                PickResult prCanvas = parentCanvas.pick(currentPos.getX(), currentPos.getY(), true);
                cluster.setComposite(true);
                List<PickEntry> plCanvas = prCanvas.getPickList();
                //get the most top element in the picklist
                if(plCanvas.size()>0)
                {
                	PickEntry currentPickEntry = plCanvas.get(plCanvas.size()-1);
                	
                	MTComponent currentComponent = currentPickEntry.hitObj;                	
                	cluster.setCurrentlySelectedChildren(currentComponent);
                }
                
			}
			
			if (unUsedCursors.size() == 2){//in this case a new rotation3d can be started
				logger.debug(this.getName() + " has already has 2 unused cursor - we can try start gesture! used with ID:" + unUsedCursors.get(0).getId() + " and new cursor ID:" + inputCursor.getId());
				InputCursor otherCursor = unUsedCursors.get(0);//use the both unused Cursors for the rotation axis
				InputCursor secondCursor = unUsedCursors.get(1);
				
				if (this.canLock(otherCursor,secondCursor,inputCursor)){
					rc = new RotationContext(otherCursor, secondCursor,inputCursor,comp);//create new RotationContext
					if (!rc.isGestureAborted()){
						this.getLock(otherCursor, inputCursor,secondCursor);
						unUsedCursors.remove(otherCursor);//remove the cursors from unUsedCursors
						unUsedCursors.remove(secondCursor);
						lockedCursors.add(otherCursor);//put all cursors in lockedCursors
						lockedCursors.add(inputCursor);
						lockedCursors.add(secondCursor);
						logger.debug(this.getName() + " we could lock both cursors!");
						//this.fireGestureEvent(new Rotate3DEvent(this, MTGestureEvent.GESTURE_DETECTED, comp, otherCursor, secondCursor,inputCursor, Vector3D.ZERO_VECTOR, rc.getRotationPoint(),rc.getRotationDirection(),rc.getRotationDegreesX(),rc.getRotationDegreesY(),rc.getRotationDegreesZ(),rc.getRotationAxis()));
					}else{
						rc = null;
						unUsedCursors.add(inputCursor);	
					}
				}else{
					logger.debug(this.getName() + " we could NOT lock both cursors!");
					unUsedCursors.add(inputCursor);	
				}
			}else if(lockedCursors.size()==2&&inputCursor==rc.getRotateFingerCursor())
			{
				InputCursor otherCursor = lockedCursors.get(0);
				InputCursor secondCursor = lockedCursors.get(1);
				
				if (this.canLock(inputCursor)){
					rc = new RotationContext(otherCursor, secondCursor,inputCursor,comp);
					if (!rc.isGestureAborted()){
						this.getLock(inputCursor);
						lockedCursors.add(inputCursor);						
						logger.debug(this.getName() + " we could lock both cursors!");
						this.fireGestureEvent(new Rotate3DEvent(this, MTGestureEvent.GESTURE_STARTED, comp, otherCursor, secondCursor,inputCursor, Vector3D.ZERO_VECTOR, rc.getRotationPoint(),rc.getRotationDirection(), rc.getRotationDegreesX(),rc.getRotationDegreesY(),rc.getRotationDegreesZ(),rc.getRotationAxis()));
					}else{
						rc = null;
						unUsedCursors.add(inputCursor);	
					}
				}
			}
			else{
				logger.debug(this.getName() + " we didnt have a unused cursor previously to start gesture now");
				unUsedCursors.add(inputCursor);
			}
			
		}
		
	}
	
	public void cursorUnlocked(InputCursor cursor) {
		logger.debug(this.getName() + " Recieved UNLOCKED signal for cursor ID: " + cursor.getId());
		
		if (lockedCursors.size() >= 3){ //we dont need the unlocked cursor, gesture still in progress
			return;
		}
		
		if (unUsedCursors.contains(cursor)){ //should always be true here!?
			if (unUsedCursors.size() >= 3){ //we can try to resume the gesture
				InputCursor firstCursor = unUsedCursors.get(0);
				InputCursor secondCursor = unUsedCursors.get(1);
				InputCursor thirdCursor = unUsedCursors.get(2);
				//See if we can obtain a lock on both cursors
				if (this.canLock(firstCursor, secondCursor,thirdCursor)){
					IMTComponent3D comp = firstCursor.getFirstEvent().getTarget();
					rc = new RotationContext(firstCursor, secondCursor,thirdCursor, comp);
					if (!rc.isGestureAborted()){ //Check if we could start gesture (ie. if fingers on component)
						this.getLock(firstCursor, secondCursor,thirdCursor);
						lockedCursors.add(firstCursor);
						lockedCursors.add(secondCursor);
						lockedCursors.add(thirdCursor);
						logger.debug(this.getName() + " we could lock cursors: " + firstCursor.getId() +", " + secondCursor.getId() + ", " + thirdCursor.getId());
						unUsedCursors.remove(firstCursor);
						unUsedCursors.remove(secondCursor);
						unUsedCursors.remove(thirdCursor);
					}else{
						rc = null;
						logger.debug(this.getName() + " we could NOT resume gesture - cursors not on component: " + firstCursor.getId() +", " + secondCursor.getId());
					}
					//TODO fire started evt?
				}else{
					logger.debug(this.getName() + " we could NOT lock cursors: " + firstCursor.getId() +", " + secondCursor.getId() + ", " + thirdCursor.getId());
				}
			}
		}else{
			logger.error(this.getName() + "hmmm - investigate why is cursor not in unusedList?");
		}
		
	}
	
	public void cursorUpdated(InputCursor inputCursor,
			AbstractCursorInputEvt currentEvent) {
		IMTComponent3D comp = currentEvent.getTarget();
		if (lockedCursors.size() == 3 && lockedCursors.contains(inputCursor)){
			rc.updateAndGetRotationAngle(inputCursor);
			this.fireGestureEvent(new Rotate3DEvent(this, MTGestureEvent.GESTURE_UPDATED, comp, rc.getPinFingerCursor(), rc.getPinFingerSecondCursor(),rc.getRotateFingerCursor(), Vector3D.ZERO_VECTOR, rc.getRotationPoint(),rc.getRotationDirection(),rc.getRotationDegreesX(),rc.getRotationDegreesY(),rc.getRotationDegreesZ(),rc.getRotationAxis() ));			
		}
		
	}

	 private MTCanvas getParentCanvas(MTComponent as) {
	         MTComponent tmp = as.getRoot();
	         if(tmp instanceof MTCanvas){
	                 return (MTCanvas)tmp;
	         }else{
	                 MTComponent mtc = as;
	                 while ((!(mtc == null)) && (!((mtc = mtc.getParent()) instanceof MTCanvas))) {
	                 }
	                 return (MTCanvas) mtc;
	         }
	 }

	/**
	 * The Class RotationContext.
	 */
	public class RotationContext {

		/** The pin finger start. */
		private Vector3D pinFingerStart;

		/** The pin finger last. */
		private Vector3D pinFingerLast;

		/** The pin finger new. */
		private Vector3D pinFingerNew;
		
		/** The second pin finger start. */
		private Vector3D pinFingerSecondStart;
		
		private Vector3D pinFingerSecondLast;
		
		private Vector3D pinFingerSecondNew;

		/** The rotate finger start. */
		private Vector3D rotateFingerStart;

		/** The rotate finger last. */
		private Vector3D rotateFingerLast;

		/** The rotate finger new. */
		private Vector3D rotateFingerNew;

		/** The last rotation vect. */
		private Vector3D lastRotationVect;

		/** The object. */
		private IMTComponent3D object;

		/** The rotation point. */
		private Vector3D rotationPoint;
		
		private Vector3D rotationAxis;

		/** The pin finger cursor. */
		private InputCursor pinFingerCursor; 
		
		/** The second pin finger cursor. */
		private InputCursor pinFingerSecondCursor; 

		/** The rotate finger cursor. */
		private InputCursor rotateFingerCursor;
		
		private Vector3D rotateCursorVectorLast;

		/** The new finger middle pos. */
		private Vector3D newFingerMiddlePos;

		/** The old finger middle pos. */
		private Vector3D oldFingerMiddlePos;

		/** The pin finger translation vect. */
		private Vector3D pinFingerTranslationVect;

		private boolean gestureAborted;

		private float percentageX=0.0f,percentageY=0.0f,percentageZ=0.0f;
		
		private float rotateLineLength = 0.0f;
		
		private float degreesPerLengthUnit = 0.01f;
		
		private short rotationDirection = 1;
		
		private Vector3D directionFinderLeft;
		
		private Vector3D directionFinderRight;
		/**
		 * Instantiates a new rotation context.
		 * 
		 * @param pinFingerCursor the pin finger cursor
		 * @param rotateFingerCursor the rotate finger cursor
		 * @param object the object
		 */
		public RotationContext(InputCursor pinFingerCursor, InputCursor pinFingerCursor2, InputCursor rotateFingerCursor, IMTComponent3D object){
			this.pinFingerCursor = pinFingerCursor;
			this.pinFingerSecondCursor = pinFingerCursor2;
			this.rotateFingerCursor = rotateFingerCursor;
			
			//check if first two fingers hit an object
			Vector3D interPoint = getIntersection(pApplet, object, pinFingerCursor);
  			Vector3D interPoint2 = getIntersection(pApplet, object, pinFingerSecondCursor);
			
			this.object = object;
			
			//if they hit take their x,y values and make an axis parallel 			
			if (interPoint !=null&& interPoint2 != null){	
				interPoint = projectPointToNearPlane(interPoint);
				interPoint2 = projectPointToNearPlane(interPoint2);
				pinFingerNew = interPoint;
				pinFingerSecondNew = interPoint2;
				setRotationAxis(getRotationAxis(interPoint,interPoint2));
			 	//rotationAxis = interPoint.getSubtracted(interPoint2);
			 	//z value of no interest
			 	//rotationAxis.z = 0.0f;			 				 	
			}else{
				logger.error(getName() + " Pinfinger NEW = NULL");
				pinFingerNew = new Vector3D();
				pinFingerSecondNew = new Vector3D();
				setRotationAxis(new Vector3D());
				//TODO ABORT THE Rotation HERE!
				gestureAborted = true;
			}
					
			//reset all fingers
			this.pinFingerStart = pinFingerNew.getCopy(); 
			this.pinFingerSecondStart = pinFingerSecondNew.getCopy();
			this.pinFingerLast	= pinFingerStart.getCopy(); 
			this.pinFingerSecondLast = pinFingerSecondStart.getCopy();
				
			this.rotateCursorVectorLast = new Vector3D(rotateFingerCursor.getCurrentEvtPosX(),rotateFingerCursor.getCurrentEvtPosY(),0.0f);
			
			updateCalculations();
		}
		
		private void updateCalculations()
		{
			float rotationAxisLength = getRotationAxis().length();
			
			MTComponent comp = (MTComponent)object;
			
			//get center point 			
			if(!(comp instanceof Cluster3DExt))
			{
				rotationPoint = getCenterPointGlobal(comp);
				
			}else
			{
				Cluster3DExt cl = (Cluster3DExt)comp;				
				rotationPoint = getCenterPointGlobal(cl.getCurrentlySelectedChildren());
			}
			
			
			Vector3D vec = rotationPoint.getCopy();
			vec.z = vec.z - 1.0f;
			
			//get direction in which the rotation should be
			directionFinderRight = vec.getSubtracted(rotationPoint);//rotationPoint.getSubtracted(vec);
			
			directionFinderRight = rotationAxis.getCross(directionFinderRight);
			
			directionFinderLeft = directionFinderRight.getInverted();
			//comp.translate(rotationPoint);
			
			//direction Finding
						
			//Vector3D com = object.getCenterOfMass();
			//rotationAxis.z = com.z;
			
			Vector3D rotationPartX = new Vector3D(getRotationAxis().x,0.0f,0.0f);
			Vector3D rotationPartY = new Vector3D(0.0f,getRotationAxis().y,0.0f);
			Vector3D rotationPartZ = new Vector3D(0.0f,0.0f,getRotationAxis().z);
							
//			Tools3D.endGL(pApplet);
			
			float dotX = rotationPartX.dot(getRotationAxis());
			float dotY = rotationPartY.dot(getRotationAxis());
			float dotZ = rotationPartZ.dot(getRotationAxis());
			
			//System.out.println("dotX " + dotX);
			//System.out.println("dotY " + dotY);
			//System.out.println("dot Z " + dotZ);
			
			//System.out.println("rotationAxis length" + getRotationAxis().length());
			//System.out.println();
			float degreesX = (float)Math.toDegrees(Math.acos(dotX/(rotationPartX.length()*getRotationAxis().length())));
			float degreesY = (float)Math.toDegrees(Math.acos(dotY/(rotationPartY.length()*getRotationAxis().length())));
			//float degreesZ = (float)Math.toDegrees(Math.acos(dotZ/(rotationPartZ.length()*rotationAxis.length())));
			
			if (Float.isNaN(degreesY)){
				degreesY = 0.0f;
			}
			if (Float.isNaN(degreesX)){
				degreesX = 0.0f;
			}
			
			percentageX = (degreesY/90.0f)*100.0f;
			percentageY = (degreesX/90.0f)*100.0f;
			
			if(getRotationAxis().x<0.0f)
			{
				percentageY = -percentageY;
			}
			
			//System.out.println("percentageX " + percentageX);
			//System.out.println("percentageY " + percentageY);
			percentageZ = 0.0f;
		}

		//get the rotation axis which looks always from the less y too the more y value
		private Vector3D getRotationAxis(Vector3D pointOne,Vector3D pointTwo)
		{
			Vector3D rotationAxis = pointOne.getSubtracted(pointTwo);
			Vector3D rotationAxis2 = pointTwo.getSubtracted(pointOne); 
			
			if(rotationAxis.y>=rotationAxis2.y)
			{
				return rotationAxis;
			}else
			{
				return rotationAxis2;
			}
		}
		
		private Vector3D projectPointToNearPlane(Vector3D point)
		{
			IFrustum frustum = object.getViewingCamera().getFrustum();
			//projiziere Punkt auf die Near Plane
			point = Tools3D.projectPointToPlaneInPerspectiveMode(point, frustum, frustum.getZValueOfNearPlane(),(AbstractMTApplication)object.getRenderer());
			return point;
		}
		
		/**
		 * Update and get rotation angle.
		 * 
		 * @param moveCursor the move cursor
		 * 
		 * @return the float
		 */
		public void updateAndGetRotationAngle(InputCursor moveCursor) {
//			/*
			float newAngleRad;
			float newAngleDegrees;

			//save the current pinfinger location as the old one
			if(!resumed)
			{
				this.pinFingerLast = this.pinFingerNew;
	
				//save the current pinfingertwo location as the old one
				this.pinFingerSecondLast = this.pinFingerSecondNew;
				
				//save the current pinfinger location as the old one
				this.rotateFingerLast = this.rotateFingerNew;
	
				//Check which finger moved and has to be updated
				if (moveCursor.equals(pinFingerCursor)){
					updatePinFinger();
					updateCalculations();
				}
				else if(moveCursor.equals(pinFingerSecondCursor))
				{
					updatePinFingerSecond();
					updateCalculations();
				}
				else if (moveCursor.equals(rotateFingerCursor)){
					
					updateRotateFinger(moveCursor);		
					//updateCalculations();
				}
				
			}else
			{				
				this.pinFingerNew = this.pinFingerLast;
				//save the current pinfingertwo location as the old one
				this.pinFingerSecondNew = this.pinFingerSecondLast; 
				
				//save the current pinfinger location as the old one
				this.rotateFingerNew = this.rotateFingerLast; 
				resumed = false;
			}
			////			*/
		}


		/**
		 * Update rotate finger.
		 */
		public void updateRotateFinger(InputCursor rotateCursor){
			//TODO save last position and use that one if new one is null.. everywhere!
			/*Vector3D newRotateFingerPos = ToolsIntersection.getRayPlaneIntersection(
					Tools3D.getCameraPickRay(pApplet, object,rotateFingerCursor.getCurrentEvent().getPosX(), rotateFingerCursor.getCurrentEvent().getPosY()), 
					dragPlaneNormal, 
					rotateFingerStart.getCopy());*/
			//Update the field
			//if (newRotateFingerPos != null){
			
			    float x = rotateCursorVectorLast.x - rotateCursor.getCurrentEvtPosX();
			    float y = rotateCursorVectorLast.y - rotateCursor.getCurrentEvtPosY();
			
			    rotateCursorVectorLast.x = rotateCursor.getCurrentEvtPosX();
				rotateCursorVectorLast.y = rotateCursor.getCurrentEvtPosY();
		    
				Vector3D rotateLengthVec = new Vector3D(x,y,0.0f);
				
				this.rotateLineLength = rotateLengthVec.length(); 
							
			    Vector3D rotateVector = new Vector3D(rotateCursor.getCurrentEvtPosX(),rotateCursor.getCurrentEvtPosY(),0.0f);
							    
				//System.out.println("rotate " + directionFinder.dot(rotateLengthVec));
						
				Vector3D finder = this.getRotationAxis().getAdded(rotateVector);
				
				float dotRight = directionFinderRight.dot(rotateLengthVec);
				
				float deg = this.getRotationAxis().dot(finder)/(finder.length()*this.getRotationAxis().length());
				
				/*if(this.getRotationAxis().normalizeLocal().x>rotateVector.normalizeLocal().x)
				{
					dotRight = -dot;
				}*/
				
				if(this.getRotationAxis().x>0.0f)
				{
					dotRight = -dotRight;
				}
				   /*if(directionFinder.dot(rotateLengthVec)>0.0f&&rotateLengthVec.x>yAxis.x)
					{
						setRotationDirection((short)-1);
					}else if(directionFinder.dot(rotateLengthVec)>0.0f&&rotateLengthVec.x<yAxis.x)
					{
						setRotationDirection((short)1);
					}else if(directionFinder.dot(rotateLengthVec)<0.0f&&rotateLengthVec.x>yAxis.x)
					{
						setRotationDirection((short)1);
					}else if(directionFinder.dot(rotateLengthVec)<0.0f&&rotateLengthVec.x<yAxis.x)
					{
						setRotationDirection((short)-1);
					}*/
				   
				 /*  if(directionFinder.dot(rotateLengthVec)>0.0f)
					{
						setRotationDirection((short)-1);
					}else 
					{
						setRotationDirection((short)1);
					}*/
				
					/*if(Math.toDegrees(deg)>0.0d)
					{
						setRotationDirection((short)-1);
					}else
					{
						setRotationDirection((short)1);
					}*/
				
					if(dotRight>0.0d)
					{
						setRotationDirection((short)-1);						
					}else
					{
						setRotationDirection((short)1);
					}
				//System.out.println("Rotate legnth" + rotateLineLength + " dir "  + rotationDirection);
			/*}else{
				logger.error(getName() + " new newRotateFinger Pos = null at update");
			}*/
		}


		/**
		 * Update pin finger.
		 */
		private void updatePinFinger(){  
			Vector3D newPinFingerPos = getPlaneIntersection(pApplet, dragPlaneNormal, pinFingerStart.getCopy(), pinFingerCursor);
			if (newPinFingerPos != null){
				newPinFingerPos = projectPointToNearPlane(newPinFingerPos);
				this.pinFingerNew = newPinFingerPos;				
				this.setRotationAxis(this.getRotationAxis(newPinFingerPos, this.pinFingerSecondNew));
				//Vector3D middlePoint = getMiddlePointBetweenPinFingers();
				//this.rotationPoint = targetComp.getCenterPointGlobal();
			}else{
				// do nothing
			}
		}
		
		private void updatePinFingerSecond() 
		{
			Vector3D newPinFingerPos = getPlaneIntersection(pApplet, dragPlaneNormal, pinFingerSecondStart.getCopy(), pinFingerSecondCursor);
			if(newPinFingerPos != null)
			{
				newPinFingerPos = projectPointToNearPlane(newPinFingerPos);
				this.pinFingerSecondNew = newPinFingerPos;
				this.setRotationAxis(this.getRotationAxis(newPinFingerPos, this.pinFingerNew));
				//Vector3D middlePoint = getMiddlePointBetweenPinFingers();
				//this.rotationPoint =  targetComp.getCenterPointGlobal();
			}else{
				// do nothing
			}
		}

		//if obj is drag enabled, und not scalable! send middlepoint delta f�r translate, 
		/**
		 * Gets the updated middle finger pos delta.
		 * 
		 * @return the updated middle finger pos delta
		 */
		public Vector3D getUpdatedMiddleFingerPosDelta(){
			newFingerMiddlePos = getMiddlePointBetweenFingers();
			Vector3D returnVect = newFingerMiddlePos.getSubtracted(oldFingerMiddlePos);

			this.oldFingerMiddlePos = newFingerMiddlePos;
			return returnVect;
		}

		/**
		 * Gets the middle point between fingers.
		 * 
		 * @return the middle point between fingers
		 */
		public Vector3D getMiddlePointBetweenFingers(){
			Vector3D bla = rotateFingerNew.getSubtracted(pinFingerNew); //= Richtungsvektor vom 1. zum 2. finger
			bla.scaleLocal(0.5f); //take the half
			return (new Vector3D(pinFingerNew.getX() + bla.getX(), pinFingerNew.getY() + bla.getY(), pinFingerNew.getZ() + bla.getZ()));
		}
		
		/**
		 * Gets the middle point between the two pin fingers()
		 * 
		 * @return the middle point between both
		 */
		public Vector3D getMiddlePointBetweenPinFingers()
		{
			Vector3D midPoint = pinFingerNew.getSubtracted(pinFingerSecondNew);
			midPoint.scaleLocal(0.5f);
			
			return (new Vector3D(pinFingerSecondNew.getX() + midPoint.getX(), pinFingerSecondNew.getY() + midPoint.getY(), pinFingerSecondNew.getZ() + midPoint.getZ()));
			
		}


		/**
		 * Gets the pin finger translation vect.
		 * 
		 * @return the pin finger translation vect
		 */
		public Vector3D getPinFingerTranslationVect() {
			return pinFingerTranslationVect;
		}

		/**
		 * Gets the pin finger start.
		 * 
		 * @return the pin finger start
		 */
		public Vector3D getPinFingerStart() {
			return pinFingerStart;
		}
		
		/**
		 * Gets the pin finger start.
		 * 
		 * @return the pin finger start
		 */
		public Vector3D getPinFingerSecondStart() {
			return pinFingerSecondStart;
		}

		/**
		 * Gets the rotate finger start.
		 * 
		 * @return the rotate finger start
		 */
		public Vector3D getRotateFingerStart() {
			return rotateFingerStart;
		}

		/**
		 * Gets the rotation point.
		 * 
		 * @return the rotation point
		 */
		public Vector3D getRotationPoint() {
			return rotationPoint;
		}

		/**
		 * Gets the pin finger cursor.
		 * 
		 * @return the pin finger cursor
		 */
		public InputCursor getPinFingerCursor() {
			return pinFingerCursor;
		}
		
		/**
		 * Gets the pin finger cursor.
		 * 
		 * @return the pin finger cursor
		 */
		public InputCursor getPinFingerSecondCursor() {
			return pinFingerSecondCursor;
		}

		/**
		 * Gets the rotate finger cursor.
		 * 
		 * @return the rotate finger cursor
		 */
		public InputCursor getRotateFingerCursor() {
			return rotateFingerCursor;
		}

		public boolean isGestureAborted() {
			return gestureAborted;
		}
		
		public float percentageX()
		{
			return this.percentageX;
		}
		
		public float percentageY()
		{
			return this.percentageY;
		}
		
		public float percentageZ()
		{
			return this.percentageZ;
		}
		
		public float getRotateLineLength()
		{
			return this.rotateLineLength;
		}
		
		public float getRotationDegreesX()
		{
			return this.degreesPerLengthUnit*percentageX*rotateLineLength;
		}
		
		public float getRotationDegreesY()
		{
			return this.degreesPerLengthUnit*percentageY*rotateLineLength;
		}
		
		public float getRotationDegreesZ()
		{
			return this.degreesPerLengthUnit*percentageZ*rotateLineLength;
		}

		public void setRotationDirection(short rotationDirection) {
			this.rotationDirection = rotationDirection;
		}

		public short getRotationDirection() {
			return rotationDirection;
		}

		public void setRotationAxis(Vector3D rotationAxis) {
			this.rotationAxis = rotationAxis;
		}

		public Vector3D getRotationAxis() {
			return rotationAxis;
		}

	}

	public String getName() {
		return "Rotate3DProcessor";
	}

	
public static Vector3D getCenterPointGlobal(MTComponent comp) {
		
		MTComponent[] children = comp.getChildren();
		if(children.length==0)
		{
			if(comp.hasBounds())
			{
				return comp.getBounds().getCenterPointGlobal();
			}else
			{
				return null;
			}
		}else
		{
			//float massSum = 0.0f;
			Vector3D vecSum = new Vector3D();
			for(MTComponent compChild : children)
			{	
				if(getCenterPointGlobal(compChild)!=null)
				{
					Vector3D vec = getCenterPointGlobal(compChild);					
					vecSum.addLocal(vec);
					
					//massSum += compChild.getMass();
					
				}
			}			
			
			return vecSum.getScaled(1.f/children.length);
		}	
	}
}

