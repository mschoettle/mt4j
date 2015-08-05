package examples.extension3D;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.MTLight;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.input.IMTEventListener;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.Rotate3DAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.IVisualizeMethodProvider;
import org.mt4j.input.inputProcessors.componentProcessors.rotate3DProcessor.Rotate3DProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.modelImporter.ModelImporterFactory;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GLMaterial;
import org.mt4jx.input.gestureAction.CreateDragHelperAction;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ClusterDataManager;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.ClusterHub;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.FingerTapGrouping.FingerTapSelectionManager;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.GroupVisualizations.BlinkingLineVisualizationAction;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.GroupVisualizations.LineVisualizationAction;
import org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.GroupVisualizations.LineVisualizationWithOutlinesAction;
import org.mt4jx.util.extension3D.ComponentHelper;
import org.mt4jx.util.extension3D.collision.CollisionManager;

import processing.core.PGraphics;

public class Extension3DScene extends AbstractScene {
	private AbstractMTApplication mtApp;
	
	private CollisionManager collisionManager;
	
	private ClusterHub clusterHub;
	
	private ArrayList<Rotate3DAction> drawAction = new ArrayList<Rotate3DAction>(); //REMOVE
	
	
	private MTComponent comp = null;
	Vector3D grundflaecheTranslation = null;
	public Extension3DScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		mtApp = mtApplication;
	
		Icamera cam = this.getSceneCam();
		
		collisionManager = new CollisionManager(this,mtApp);
		
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
		
		
		//Init light settings
		MTLight.enableLightningAndAmbient(mtApplication, 150, 150, 150, 255);
		//Create a light source //I think GL_LIGHT0 is used by processing!
		MTLight light = new MTLight(mtApplication, GL.GL_LIGHT3, new Vector3D(0,0,0));
		
		//Set up a material to react to the light
		GLMaterial material = new GLMaterial(PlatformUtil.getGL());
		material.setAmbient(new float[]{ .3f, .3f, .3f, 1f });
		material.setDiffuse(new float[]{ .9f, .9f, .9f, 1f } );
		material.setEmission(new float[]{ .0f, .0f, .0f, 1f });
		material.setSpecular(new float[]{ 1.0f, 1.0f, 1.0f, 1f });  // almost white: very reflective
		material.setShininess(110);// 0=no shine,  127=max shine
		

		MTComponent machine4;
		
		machine4 = getMeshGroup(mtApplication, new Vector3D(-400.0f,-700.0f,1200.0f), System.getProperty("user.dir")  + File.separator + "extensions" +File.separator + "examples" +    File.separator + "extension3D"  + File.separator + "data" +  File.separator +
				"elevtruck" + File.separator + "elev_truck.obj",light,material,"elevTruck");
		
		
				
		machine4.rotateX(ComponentHelper.getCenterPointGlobal(machine4), -90.0f);
		
		machine4.scale(0.5f,0.5f,0.5f,ComponentHelper.getCenterPointGlobal(machine4));
		
		MTComponent machine5;
		
		machine5 = getMeshGroup(mtApplication, new Vector3D(-100.0f,-700.0f,1200.0f), System.getProperty("user.dir")  + File.separator + "extensions" +  File.separator + "examples" + File.separator + "extension3D"  + File.separator + "data" +  File.separator +
				"elevtruck" + File.separator + "elev_truck.obj",light,material,"elevTruck");
		
		
				
		machine5.rotateX(ComponentHelper.getCenterPointGlobal(machine5), -90.0f);
		
		machine5.scale(0.5f,0.5f,0.5f,ComponentHelper.getCenterPointGlobal(machine5));
		
		MTComponent robotArm;
		
		robotArm = getMeshGroup(mtApplication, new Vector3D(-450.0f,-150.0f,-200.0f), System.getProperty("user.dir")  + File.separator + "extensions" + File.separator + "examples" +  File.separator + "extension3D"  + File.separator + "data" +  File.separator +
				"robotArm" + File.separator + "robotArm.obj",light,material,"robotArm");
			
		robotArm.scale(0.4f,0.4f,0.4f,ComponentHelper.getCenterPointGlobal(robotArm));
		
		MTComponent robotArm2;
		
		robotArm2 = getMeshGroup(mtApplication, new Vector3D(-150.0f,-150.0f,-200.0f), System.getProperty("user.dir")  + File.separator + "extensions" +  File.separator + "examples" + File.separator + "extension3D"  + File.separator + "data" +  File.separator +
				"robotArm" + File.separator + "robotArm.obj",light,material,"robotArm");
			
		robotArm2.scale(0.4f,0.4f,0.4f,ComponentHelper.getCenterPointGlobal(robotArm2));
			
				
		MTComponent grundflaecheGroup = getGroundMesh(mtApplication, System.getProperty("user.dir")  + File.separator + "extensions" + File.separator + "examples" + File.separator + "extension3D"  + File.separator + "data" +  File.separator +
				"floor" + File.separator + "grundflaeche3.obj",light,material,cam);
		
		/**/
		//NORMAL 3D OBJECTS VERSION
		/*MTCube cube1 = new MTCube(mtApplication, 50.0f);
		MTComponent group10 = getMeshGroupForSimpleObject(mtApplication,cube1,new Vector3D(0.0f,0.0f,0.0f),light,material,"cube");
		
		MTSphere sphere1 = new MTSphere(mtApplication,"sphere1",32,32,100.0f);
		MTComponent group20 = getMeshGroupForSimpleObject(mtApplication,sphere1,new Vector3D(0.0f,200.0f,0.0f),light,material,"sphere1");
		MTSphere sphere2 = new MTSphere(mtApplication,"sphere2",32,32,100.0f);
		MTComponent group30 = getMeshGroupForSimpleObject(mtApplication,sphere2,new Vector3D(200.0f,000.0f,0.0f),light,material,"sphere2");*/
		//END NORMAL 3D OBJECTS VERSION DO NOT FORGET selectionManager addclusterable below 
		
		
		ClusterDataManager clusterManager = new ClusterDataManager(mtApplication,this.getCanvas(),collisionManager);
		clusterHub = new ClusterHub();
		clusterManager.addClusterEventListener(clusterHub);
				
		LineVisualizationAction visAction = new LineVisualizationAction(mtApplication);
		clusterHub.addEventListener(visAction);
		
		//BlinkingLineVisualizationAction visAction2 = new BlinkingLineVisualizationAction(mtApplication);
		//clusterHub.addEventListener(visAction2);
		
		//LASSO GROUPING
		//LassoGroupSelectionManager selectionManager = new LassoGroupSelectionManager(this.getCanvas(),clusterManager);
		//selectionManager.addSelectionListener(clusterHub);
		//this.getCanvas().registerInputProcessor(selectionManager);
		//LASSO GROUPING END
		
		//FINGERTAP GROUPING
		FingerTapSelectionManager selectionManager = new FingerTapSelectionManager(clusterManager,this.getCanvas());
		selectionManager.addSelectionListener(clusterHub);
		this.registerGlobalInputProcessor(selectionManager);
		//FINGERTAP GROUPING END
					
		
		selectionManager.addClusterable(machine4);
		selectionManager.addClusterable(machine5);
		selectionManager.addClusterable(robotArm);
		selectionManager.addClusterable(robotArm2);
		
	
		//NORMAL 3D OBJECTS VERSION
		//selectionManager.addClusterable(group10);
		//selectionManager.addClusterable(group20);
		//selectionManager.addClusterable(group30);
		//NORMAL 3D OBJECTS VERSION END
		
		collisionManager.addObjectsToCollisionDomain();
		
	}


	public MTTriangleMesh getBiggestMesh(MTTriangleMesh[] meshes){
		MTTriangleMesh currentBiggestMesh = null;
		//Get the biggest mesh and extract its width
		float currentBiggestWidth = Float.MIN_VALUE;
		for (int i = 0; i < meshes.length; i++) {
			MTTriangleMesh triangleMesh = meshes[i];
			float width = triangleMesh.getWidthXY(TransformSpace.GLOBAL);
			if (width > currentBiggestWidth){
				currentBiggestWidth = width;
				currentBiggestMesh = triangleMesh;
			}
		}
		return currentBiggestMesh;
	}
	
	
	public void onEnter() {
		getMTApplication().registerKeyEvent(this);
	}
	
	public void onLeave() {
		getMTApplication().unregisterKeyEvent(this);
	}
	
	public void keyEvent(KeyEvent e){
		//System.out.println(e.getKeyCode());
		int evtID = e.getID();
		if (evtID != KeyEvent.KEY_PRESSED)
			return;
		switch (e.getKeyCode()){
		case KeyEvent.VK_F:
			System.out.println("FPS: " + mtApp.frameRate);
			break;
		case KeyEvent.VK_PLUS:
			this.getSceneCam().moveCamAndViewCenter(0, 0, -10);
			break;
		case KeyEvent.VK_MINUS:
			this.getSceneCam().moveCamAndViewCenter(0, 0, +10);			
			break;
		case KeyEvent.VK_1:
			LineVisualizationAction visAction = new LineVisualizationAction(mtApp);
			removeAllVisualization(clusterHub);
			clusterHub.addEventListener(visAction);
			break;
		case KeyEvent.VK_2:
			BlinkingLineVisualizationAction visAction2 = new BlinkingLineVisualizationAction(this.mtApp);
			removeAllVisualization(clusterHub);
			clusterHub.addEventListener(visAction2);
			break;
		case KeyEvent.VK_3:
			LineVisualizationWithOutlinesAction visAction3 = new LineVisualizationWithOutlinesAction(this.mtApp);
			removeAllVisualization(clusterHub);
			clusterHub.addEventListener(visAction3);
			break;
		case KeyEvent.VK_4:
			this.getCanvas().rotateY(new Vector3D(400.0f,300.0f,0.0f),-90.0f);
			break;
			default:
				break;
		}
	}
	
	public void removeAllVisualization(ClusterHub cHub)
	{
		ArrayList<IMTEventListener> toRemove = new ArrayList<IMTEventListener>();
		
		for(IMTEventListener listener : cHub.getListeners())
		{
			if(listener instanceof IVisualizeMethodProvider)
			{
				toRemove.add(listener);
			}
		}	
		cHub.getListeners().removeAll(toRemove);
		
	}
	
	public void drawAndUpdate(PGraphics g, long timeDelta) {
        super.drawAndUpdate(g, timeDelta);
        g.pushMatrix();
//        Tools3D.beginGL(mtApp);
//        GL gl = Tools3D.getGL(mtApp);
        GL10 gl = PlatformUtil.beginGL();
        if(drawAction!=null)
        {
        	for(Rotate3DAction act:drawAction)
        	{
        		if(act.isDrawAble())
        		{
        			act.draw();
        		}
        	}
        }
        
//        Tools3D.endGL(mtApp);
        PlatformUtil.endGL();
        g.popMatrix();
    }
	
	private MTComponent getMeshGroupForSimpleObject(AbstractMTApplication mtApplication,MTTriangleMesh inputMesh,Vector3D translation,MTLight light,GLMaterial material,String name)
	{		
		
		
		//Create a group and set the light for the whole mesh group ->better for performance than setting light to more comps
		//MTComponent group1 = new MTComponent(mtApplication);
		final MTComponent meshGroup = new MTComponent(mtApplication, "Mesh group");
		
		meshGroup.setLight(light);
		this.getCanvas().addChild(meshGroup);
		//Desired position for the meshes to appear at
		Vector3D destinationPosition = new Vector3D(mtApplication.width/2+200.0f, mtApplication.height/2, 50);
	
		//Desired scale for the meshes
		float destinationScale = mtApplication.width*0.94f;

		//Load the meshes with the ModelImporterFactory (A file can contain more than 1 mesh)
		MTTriangleMesh[] meshes = new MTTriangleMesh[1];
		meshes[0]  = inputMesh;
		
		//Get the biggest mesh in the group to use as a reference for setting the position/scale
		final MTTriangleMesh biggestMesh = this.getBiggestMesh(meshes);
		
		Vector3D translationToScreenCenter = new Vector3D(destinationPosition);
		translationToScreenCenter.subtractLocal(biggestMesh.getCenterPointGlobal());
		
		Vector3D scalingPoint = new Vector3D(biggestMesh.getCenterPointGlobal());
		float biggestWidth = biggestMesh.getWidthXY(TransformSpace.GLOBAL);	
		float scale = destinationScale/biggestWidth;
		
		//Move the group the the desired position
		meshGroup.translateGlobal(translationToScreenCenter.getAdded(translation));
		meshGroup.scale(scale/10, scale/10, scale/10,translationToScreenCenter.getAdded(translation));
	
		meshGroup.setName(name);
					
		//meshGroup.addChild(meshGroup);
		for (int i = 0; i < meshes.length; i++) {
			MTTriangleMesh mesh = meshes[i];
			mesh.setName(name + " " + i);
			meshGroup.addChild(mesh);
			mesh.unregisterAllInputProcessors(); //Clear previously registered input processors
			mesh.setPickable(true);
			//If the mesh has more than 20 vertices, use a display list for faster rendering
			if (mesh.getVertexCount() > 20)
				mesh.generateAndUseDisplayLists();
			//Set the material to the mesh  (determines the reaction to the lightning)
			if (mesh.getMaterial() == null)
				mesh.setMaterial(material);
		
			mesh.setDrawNormals(false);
			
		}
		
		meshGroup.rotateX(translationToScreenCenter.getAdded(translation),90.0f);
		//add to Collision World
		for(int i=0;i<meshes.length;i++)
		{
			collisionManager.addMeshToCollisionGroup(meshGroup, meshes[i], translationToScreenCenter.getAdded(translation));			
		}
	
		settingsForNormalMeshGroup(mtApplication,meshGroup);
		
		return meshGroup;
	}
	
	private MTComponent getMeshGroup(AbstractMTApplication mtApplication,Vector3D translation,String filename,MTLight light,GLMaterial material,String name)
	{		
		
		
		//Create a group and set the light for the whole mesh group ->better for performance than setting light to more comps
		//MTComponent group1 = new MTComponent(mtApplication);
		final MTComponent meshGroup = new MTComponent(mtApplication, "Mesh group");
		
		meshGroup.setLight(light);
		this.getCanvas().addChild(meshGroup);
		//Desired position for the meshes to appear at
		Vector3D destinationPosition = new Vector3D(mtApplication.width/2+200.0f, mtApplication.height/2, 50);
	
		//Desired scale for the meshes
		float destinationScale = mtApplication.width*0.94f;

		//Load the meshes with the ModelImporterFactory (A file can contain more than 1 mesh)
		MTTriangleMesh[] meshes = ModelImporterFactory.loadModel(mtApp,filename, 180, true, false );
		
		//Get the biggest mesh in the group to use as a reference for setting the position/scale
		final MTTriangleMesh biggestMesh = this.getBiggestMesh(meshes);
		
		Vector3D translationToScreenCenter = new Vector3D(destinationPosition);
		translationToScreenCenter.subtractLocal(biggestMesh.getCenterPointGlobal());
		
		Vector3D scalingPoint = new Vector3D(biggestMesh.getCenterPointGlobal());
		float biggestWidth = biggestMesh.getWidthXY(TransformSpace.GLOBAL);	
		float scale = destinationScale/biggestWidth;
		
		//Move the group the the desired position
		meshGroup.translateGlobal(translationToScreenCenter.getAdded(translation));
		meshGroup.scale(scale/2, scale/2, scale/2,translationToScreenCenter.getAdded(translation));
	
		meshGroup.setName(name);
					
		//meshGroup.addChild(meshGroup);
		for (int i = 0; i < meshes.length; i++) {
			MTTriangleMesh mesh = meshes[i];
			mesh.setName(name + " " + i);
			meshGroup.addChild(mesh);
			mesh.unregisterAllInputProcessors(); //Clear previously registered input processors
			mesh.setPickable(true);
			//If the mesh has more than 20 vertices, use a display list for faster rendering
			if (mesh.getVertexCount() > 20)
				mesh.generateAndUseDisplayLists();
			//Set the material to the mesh  (determines the reaction to the lightning)
			if (mesh.getMaterial() == null)
				mesh.setMaterial(material);
		
			mesh.setDrawNormals(false);
			
		}
		
		meshGroup.rotateX(translationToScreenCenter.getAdded(translation),90.0f);
		//add to Collision World
		for(int i=0;i<meshes.length;i++)
		{
			collisionManager.addMeshToCollisionGroup(meshGroup, meshes[i], translationToScreenCenter.getAdded(translation));			
		}
	
		settingsForNormalMeshGroup(mtApplication,meshGroup);
		
		return meshGroup;
	}
	
	private MTComponent getGroundMesh(AbstractMTApplication mtApplication,String filename,MTLight light,GLMaterial material,Icamera cam)
	{
		MTComponent grundflaecheGroup = new MTComponent(mtApplication);
	
		MTTriangleMesh[] grundflaeche = ModelImporterFactory.loadModel(mtApp,filename, 0, true, false );
		grundflaecheGroup.setLight(light);
		this.getCanvas().addChild(grundflaecheGroup);
		
		
		grundflaecheTranslation = new Vector3D(mtApp.getWidth()/2.f,mtApp.getHeight()/2.f,-300.0f);
				
		final MTTriangleMesh biggestMeshGrundflaeche = this.getBiggestMesh(grundflaeche);
		grundflaecheGroup.translateGlobal(grundflaecheTranslation);
		grundflaecheGroup.rotateXGlobal(grundflaecheTranslation,90.0f);
		
		float biggestWidthGrundflaeche = biggestMeshGrundflaeche.getWidthXY(TransformSpace.GLOBAL);
		float biggestHeightGrundflaeche = biggestMeshGrundflaeche.getHeightXY(TransformSpace.GLOBAL);
				
		grundflaecheGroup.scale(cam.getFrustum().getWidthOfPlane(-300.0f)/biggestWidthGrundflaeche,
								      cam.getFrustum().getHeightOfPlane(-300.0f)/biggestHeightGrundflaeche,1.0f,grundflaecheTranslation);

		grundflaecheGroup.setComposite(true);
		//grundflaecheGroup.setPickable(false);
		grundflaecheGroup.setName("grundflaeche");

		for(int i=0;i<grundflaeche.length;i++)
		{			
			grundflaecheGroup.addChild(grundflaeche[i]);
			grundflaeche[i].unregisterAllInputProcessors(); //Clear previously registered input processors
			grundflaeche[i].setPickable(false);
		
			//If the mesh has more than 20 vertices, use a display list for faster rendering
			if (grundflaeche[i].getVertexCount() > 20)
				grundflaeche[i].generateAndUseDisplayLists();
			//Set the material to the mesh  (determines the reaction to the lightning)
			if (grundflaeche[i].getMaterial() == null)
				grundflaeche[i].setMaterial(material);
		
			grundflaeche[i].setDrawNormals(false);
		}	
		for(int i=0;i<grundflaeche.length;i++)
		{
			collisionManager.addMeshToCollisionGroup(grundflaecheGroup,grundflaeche[i], grundflaecheTranslation);
		}
		
		return grundflaecheGroup;
	}

	private void settingsForNormalMeshGroup(AbstractMTApplication mtApplication,final MTComponent meshGroup)
	{
		meshGroup.setComposite(true); //-> Group gets picked instead of its children
		
		
		meshGroup.registerInputProcessor(new ScaleProcessor(mtApplication));
		meshGroup.addGestureListener(ScaleProcessor.class, new IGestureEventListener(){
			//@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
					ScaleEvent se = (ScaleEvent)ge;				
													
					meshGroup.scaleGlobal(se.getScaleFactorX(), se.getScaleFactorY(), se.getScaleFactorX(), ComponentHelper.getCenterPointGlobal(meshGroup));
				return false;
			}
		});
				
		meshGroup.registerInputProcessor(new RotateProcessor(mtApplication));
		meshGroup.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
		
		meshGroup.setGestureAllowance(RotateProcessor.class,true);
			
		meshGroup.addGestureListener(DragProcessor.class, new CreateDragHelperAction(mtApplication,this.getCanvas(),this.getSceneCam(),meshGroup));
				
		meshGroup.registerInputProcessor(new Rotate3DProcessor(mtApplication,meshGroup));
		 Rotate3DAction act = new Rotate3DAction(mtApplication,meshGroup);
		 drawAction.add(act);
		meshGroup.addGestureListener(Rotate3DProcessor.class,act);
		meshGroup.setGestureAllowance(Rotate3DProcessor.class,true);
		
		meshGroup.registerInputProcessor(new DragProcessor(mtApplication));
		meshGroup.addGestureListener(DragProcessor.class,new DefaultDragAction());
		meshGroup.setGestureAllowance(DragProcessor.class,true);

	}
	


}
