package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.LassoGrouping;

import java.util.ArrayList;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ISelection;
import org.mt4jx.util.extension3D.ComponentHelper;

import processing.core.PApplet;

public class LassoSelection implements ISelection {
	/** The polygon. */
	private MTPolygon polygon;
	
	/** The last position. */
	private Vector3D lastPosition;
	
	/** The new position. */
	private Vector3D newPosition;
	
	/** The cursor. */
	private InputCursor cursor;
	
	/** The gesture aborted. */
	protected boolean gestureAborted;
	
	/** The selected comps. */
	private ArrayList<MTComponent> selectedComps;
	
	private PApplet pApplet;
	
	private Icamera camera;
	
	private LassoGroupSelectionManager selectionManager;
	
	private Vector3D pointInPlane;
	
	private Vector3D planeNormal;

	public LassoSelection(PApplet pApplet,Icamera cam,LassoGroupSelectionManager selectionManager)
	{
		this.pApplet = pApplet;
		this.camera = cam;		
		this.selectionManager = selectionManager;
		
		gestureAborted = false;
		
	}
	
	public void startSelection(InputCursor cursor)
	{
		this.cursor = cursor;
		
		selectedComps = new ArrayList<MTComponent>();

		pointInPlane = new Vector3D(((AbstractMTApplication)pApplet).width/2.f,((AbstractMTApplication)pApplet).height/2.f,camera.getFrustum().getZValueOfNearPlane());
		planeNormal = new Vector3D(0,0,1);
		
		Vector3D newPos = ToolsGeometry.getRayPlaneIntersection(
				Tools3D.getCameraPickRay(pApplet, camera, cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY()), 
				planeNormal, 
				pointInPlane);
		
		if (newPos == null){	
			System.out.println("New pos null aborted");
			gestureAborted = true;
			selectionManager.abortGesture(cursor);
			return;
		}
		
		this.newPosition = newPos;
		this.lastPosition = newPos;
		
		/*polygon = new MTStencilPolygon(
				new Vertex[]{
						new Vertex(newPos.getX(), newPos.getY(), newPos.getZ()),
						new Vertex(newPos.getX()+0.1f, newPos.getY(), newPos.getZ()),
						new Vertex(newPos.getX(), newPos.getY()+0.1f, newPos.getZ()),
						new Vertex(newPos.getX(), newPos.getY(), newPos.getZ())},
				pa);*/
		
		setPolygon(new MTPolygon(pApplet,
				new Vertex[]{
				new Vertex(newPos.getX(), newPos.getY(), newPos.getZ()),
				new Vertex(newPos.getX()+0.1f, newPos.getY(), newPos.getZ()),
				new Vertex(newPos.getX(), newPos.getY()+0.1f, newPos.getZ()),
				new Vertex(newPos.getX(), newPos.getY(), newPos.getZ())}));
		Vertex[] vertices = getPolygon().getVerticesLocal();
		
		for(Vertex v : vertices)
		{
			v.setZ(newPos.getZ()-0.01f);
		}
		
		getPolygon().setPickable(true);
		getPolygon().setNoStroke(false);
		getPolygon().setNoFill(false);
		getPolygon().setFillColor(new MTColor(100, 150, 250,55));
//		polygon.setStrokeColor(150,150,250,255);
		getPolygon().setStrokeColor(new MTColor(0,0,0,255));
		getPolygon().setStrokeWeight(1.5f);
		getPolygon().setDrawSmooth(true);
		getPolygon().setUseDirectGL(true);
		getPolygon().setLineStipple((short)0xBBBB);
		getPolygon().setName("SelectPoly");
		
		getPolygon().setGestureAllowance(RotateProcessor.class, false);
		getPolygon().setGestureAllowance(ScaleProcessor.class, false);
		getPolygon().setGestureAllowance(TapProcessor.class, false);
		
		getPolygon().setGestureAllowance(DragProcessor.class, false);
		
		getPolygon().setBoundsAutoCompute(false);
		getPolygon().setBoundsBehaviour(AbstractShape.BOUNDS_DONT_USE);
		
		System.out.println("polygon done");
//		polygon.setComposite(true);
		//selectedComps = new ArrayList<IdragClusterable>();
	}

		
	public void updateCursorInput(InputCursor inputCursor) {
		
		if(!gestureAborted)
		{
			lastPosition = newPosition;
				
			this.newPosition = Tools3D.unprojectScreenCoords(pApplet, camera, cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());

			Vector3D rayStartPoint = camera.getPosition(); //default cam
			Vector3D newPos = ToolsGeometry.getRayPlaneIntersection(new Ray(rayStartPoint, newPosition), planeNormal,pointInPlane);
			
			newPosition = newPos;

			if (newPosition != null && !lastPosition.equalsVector(newPosition)){
				Vertex[] newArr = new Vertex[this.getPolygon().getVertexCount()+1];

				Vertex[] polyVertices = this.getPolygon().getVerticesGlobal();

				//set the old last point to the next index
				System.arraycopy(polyVertices, 0, newArr, 0, this.getPolygon().getVertexCount());
				newArr[newArr.length-1] = polyVertices[0]; //close poly correctly

				//Create the new vertex
				Vertex newVert = new Vertex(newPosition.getX(), newPosition.getY(), newPosition.getZ(), 100,150,250,255);
				newVert.setA(120);
				newArr[newArr.length-2] = newVert; //set the new value to be the length-2 one
				
				getPolygon().setVertices(newArr);					
			}
		}
	}

	public ISelection getCopy() {
		LassoSelection sel = new LassoSelection(pApplet,camera,selectionManager);
		return sel;
	}

	public ArrayList<MTComponent> getSelectedComponents() {
		selectedComps = new ArrayList<MTComponent>();
		for (int i = 0; i < selectionManager.getDragSelectables().size(); i++) {
			MTComponent currentCard = selectionManager.getDragSelectables().get(i);
			//project center point on z plane
			
			Vector3D projectedCenterPoint = ComponentHelper.getCenterPointGlobal(currentCard).getCopy();
			projectedCenterPoint = Tools3D.projectPointToPlaneInPerspectiveMode(projectedCenterPoint, camera.getFrustum(), this.getPolygon().getCenterPointGlobal().z,(AbstractMTApplication)pApplet);
						
			if (this.getPolygon().containsPointGlobal(projectedCenterPoint)){				
				selectedComps.add(currentCard);				
			}
		}
		return selectedComps;
	}
	
	public MTPolygon getPolygon() {
		return polygon;
	}
	
	private void setPolygon(MTPolygon polygon) {
		this.polygon = polygon;
	}

}
