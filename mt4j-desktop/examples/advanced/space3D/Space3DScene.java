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
package advanced.space3D;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.MTLight;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.mesh.MTSphere;
import org.mt4j.components.visibleComponents.shapes.mesh.MTSphere.TextureMode;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.Animation;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.MultiPurposeInterpolator;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GLMaterial;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.EXPANSION_FILTER;
import org.mt4j.util.opengl.GLTexture.SHRINKAGE_FILTER;
import org.mt4j.util.opengl.GLTexture.TEXTURE_TARGET;
import org.mt4j.util.opengl.GLTexture.WRAP_MODE;
import org.mt4j.util.opengl.GLTextureSettings;


public class Space3DScene extends AbstractScene {
	private AbstractMTApplication pa;
	private MTSphere earth;

	//TODO make earth spinnable with velocity
	
	//Loads from file system only
//	private String imagesPath = System.getProperty("user.dir") + File.separator + "examples" + File.separator +"advanced"+ File.separator+ File.separator + "space3D"  + File.separator + "data" +  File.separator ;
	//allows loading from jar
	private String imagesPath =  "advanced" + AbstractMTApplication.separator + "space3D" + AbstractMTApplication.separator + "data" +  AbstractMTApplication.separator ;
	
	/**
	 * Instantiates a new model display scene.
	 * 
	 * @param mtApplication the mt application
	 * @param name the name
	 */
	public Space3DScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.pa = mtApplication;
		
		if (!MT4jSettings.getInstance().isOpenGlMode()){
			System.err.println("Scene only usable when using the OpenGL renderer! - See settings.txt");
        	return;
        }
		
		this.setClearColor(new MTColor(200, 200, 200, 255));
		this.registerGlobalInputProcessor(new CursorTracer(pa, this));
		
		//Add a background image for the scene
		this.getCanvas().addChild(new MTBackgroundImage(pa, pa.loadImage(imagesPath + "3040.jpg"), true));
		
		//Init light settings
		MTLight.enableLightningAndAmbient(pa, 150, 150, 150, 255);
		//Create a light source //I think GL_LIGHT0 is used by processing!
//		MTLight light = new MTLight(pa, GL.GL_LIGHT3, new Vector3D(0,0,0));
		MTLight light = new MTLight(pa, GL.GL_LIGHT3, new Vector3D(pa.width/5f,-pa.height/10f,0));
		
		//Set up a material to react to the light
		GLMaterial material = new GLMaterial(PlatformUtil.getGL());
		material.setAmbient(new float[]{ .1f, .1f, .1f, 1f });
		material.setDiffuse(new float[]{ 1.0f, 1.0f, 1.0f, 1f } );
		material.setEmission(new float[]{ .0f, .0f, .0f, 1f });
		material.setSpecular(new float[]{ 1.0f, 1.0f, 1.0f, 1f });  // almost white: very reflective
		material.setShininess(127);// 0=no shine,  127=max shine
		
		//Create the earth
		earth = new MTSphere(pa, "earth", 40, 40, 80, TextureMode.Projected); //TextureMode.Polar);
		earth.setLight(light);
		earth.setMaterial(material);
		earth.rotateX(earth.getCenterPointRelativeToParent(), -90);
		earth.setTexture(new GLTexture(pa,imagesPath + "worldMap.jpg", new GLTextureSettings(TEXTURE_TARGET.TEXTURE_2D, SHRINKAGE_FILTER.Trilinear, EXPANSION_FILTER.Bilinear, WRAP_MODE.CLAMP_TO_EDGE, WRAP_MODE.CLAMP_TO_EDGE)));
        earth.generateAndUseDisplayLists();
        earth.setPositionGlobal(new Vector3D(pa.width/2f, pa.height/2f, 250)); //earth.setPositionGlobal(new Vector3D(200, 200, 250));
        //Animate earth rotation
        new Animation("rotation animation", new MultiPurposeInterpolator(0,360, 17000, 0, 1, -1) , earth).addAnimationListener(new IAnimationListener(){
        	public void processAnimationEvent(AnimationEvent ae) {
        		earth.rotateY(earth.getCenterPointLocal(), ae.getDelta(), TransformSpace.LOCAL);
        	}}).start();
        
        //Put planets in a group that can be manipulated by gestures
        //so the rotation of the planets doesent get changed by the gestures
		MTComponent group = new MTComponent(mtApplication);
		group.setComposite(true); //This makes the group "consume" all picking and gestures of the children
		group.registerInputProcessor(new DragProcessor(mtApplication));
		group.addGestureListener(DragProcessor.class, new DefaultDragAction());
		group.addGestureListener(DragProcessor.class, new InertiaDragAction(80, 0.8f, 10));
		group.registerInputProcessor(new RotateProcessor(mtApplication));
		group.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
		//Scale the earth from the center. Else it might get distorted
		group.registerInputProcessor(new ScaleProcessor(mtApplication));
		group.addGestureListener(ScaleProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				ScaleEvent se = (ScaleEvent)ge;
				earth.scaleGlobal(se.getScaleFactorX(), se.getScaleFactorY(), se.getScaleFactorX(), earth.getCenterPointGlobal());
				return false;
			}
		});
		this.getCanvas().addChild(group);
        group.addChild(earth);
        
        //Create the moon
        final MTSphere moonSphere = new MTSphere(pa, "moon", 35, 35, 25, TextureMode.Polar);
       	moonSphere.setMaterial(material);
        moonSphere.translate(new Vector3D(earth.getRadius() + moonSphere.getRadius() + 50, 0,0));
        moonSphere.setTexture(new GLTexture(pa, imagesPath + "moonmap1k.jpg", new GLTextureSettings(TEXTURE_TARGET.RECTANGULAR, SHRINKAGE_FILTER.Trilinear, EXPANSION_FILTER.Bilinear, WRAP_MODE.CLAMP_TO_EDGE, WRAP_MODE.CLAMP_TO_EDGE)));
        moonSphere.generateAndUseDisplayLists();
        moonSphere.unregisterAllInputProcessors();
        //Rotate the moon around the earth
        new Animation("moon animation", new MultiPurposeInterpolator(0,360, 12000, 0, 1, -1) , moonSphere).addAnimationListener(new IAnimationListener(){
        	public void processAnimationEvent(AnimationEvent ae) {
        		moonSphere.rotateZ(earth.getCenterPointLocal(), ae.getDelta(), TransformSpace.RELATIVE_TO_PARENT);
        	}}).start();
        //Rotate the moon around ints own center
        new Animation("moon animation around own axis", new MultiPurposeInterpolator(0,360, 9000, 0, 1, -1) , moonSphere).addAnimationListener(new IAnimationListener(){
        	public void processAnimationEvent(AnimationEvent ae) {
        		moonSphere.rotateZ(moonSphere.getCenterPointLocal(), -3*ae.getDelta(), TransformSpace.LOCAL);
        		moonSphere.rotateY(moonSphere.getCenterPointLocal(), 0.5f*ae.getDelta(), TransformSpace.LOCAL);
        	}}).start();
        earth.addChild(moonSphere);
	}


	public void onEnter() {
		getMTApplication().registerKeyEvent(this);
	}
	
	public void onLeave() {	
		getMTApplication().unregisterKeyEvent(this);
	}
	
	public void keyEvent(KeyEvent e){
		int evtID = e.getID();
		if (evtID != KeyEvent.KEY_PRESSED)
			return;
		switch (e.getKeyCode()){
		case KeyEvent.VK_F:
			System.out.println("FPS: " + pa.frameRate);
			break;
			default:
				break;
		}
	}

}
