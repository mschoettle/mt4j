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
package org.mt4j.components.visibleComponents.shapes.mesh;

import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.bounds.OrientedBoundingBox;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.util.TriangleNormalGenerator;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLTexture;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Class MTCube.
 * @author Christopher Ruff
 */
public class MTCube extends MTTriangleMesh {

	//TODO texture coords
	
	/**
	 * Instantiates a new mT cube.
	 * 
	 * @param pApplet the applet
	 * @param size the size
	 */
	public MTCube(PApplet pApplet, float size) {
		super(pApplet, 	
				new GeometryInfo(pApplet, new Vertex[]{
//		        		 new Vertex( 0.0f, 0.0f, 1.0f)
//		        		,new Vertex( 1.0f, 0.0f, 1.0f)
//		        		,new Vertex( 1.0f, 1.0f, 1.0f)
//		        		,new Vertex( 0.0f, 1.0f, 1.0f)
//
//		        		,new Vertex( 0.0f, 0.0f, 0.0f)
//		        		,new Vertex( 1.0f, 0.0f, 0.0f)
//
//		        		,new Vertex( 1.0f, 1.0f, 0.0f)
//		        		,new Vertex( 0.0f, 1.0f, 0.0f)
						 	new Vertex( -size*0.5f, -size*0.5f, size*0.5f, 0,0)
			        		,new Vertex( size*0.5f, -size*0.5f, size*0.5f, 1,0)
			        		,new Vertex( size*0.5f, size*0.5f, size*0.5f,  1,1)
			        		,new Vertex(-size*0.5f, size*0.5f, size*0.5f,  0,1)

			        		,new Vertex(-size*0.5f, -size*0.5f, -size*0.5f, 1,0)
			        		,new Vertex( size*0.5f, -size*0.5f, -size*0.5f, 0,0)

			        		,new Vertex( size*0.5f, size*0.5f, -size*0.5f, 0,1)
			        		,new Vertex( -size*0.5f, size*0.5f, -size*0.5f, 1,1)
		        }
//				,new Vector3D[]{
//						
//				}
		        ,new short[]{
						  //front
						   0 ,1 ,2 
						  ,0 ,2 ,3 
						  
						  //right
						  ,1 ,5 ,6 
						  ,1 ,6 ,2 
						  
						  //left
						  ,0 ,3 ,4 
						  ,3 ,7 ,4 
						  
						  //bottom
						  ,2 ,7 ,6 
						  ,2 ,3 ,7 
						  
						  //back
						  ,4 ,5 ,6 
						  ,4 ,6 ,7 
						  
						  //top
						  ,0 ,4 ,5 
						  ,0 ,5 ,1 
					})
		);
		//Set crease angle 89 normals
		this.getGeometryInfo().setNormals(new TriangleNormalGenerator().generateTriangleNormals(pApplet, this.getGeometryInfo(), 89).getNormals(), true, false);
		this.setName("unnamed mt cube");
		this.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
	}

	@Override
	protected void setDefaultGestureActions() {
		this.registerInputProcessor(new DragProcessor(this.getRenderer()));
		this.addGestureListener(DragProcessor.class, new DefaultDragAction());
		
		this.registerInputProcessor(new RotateProcessor(this.getRenderer()));
		this.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
	}
	
	@Override
	protected IBoundingShape computeDefaultBounds() {
		return new OrientedBoundingBox(this);
	}
	
	//FIXME TEST -> adapt tex coords for non fitting, NPOT gl texture
	private void adaptTexCoordsForNPOTUse(){
		PImage tex = this.getTexture();
		if (tex instanceof GLTexture){
			Tools3D.adaptTextureCoordsNPOT(this, (GLTexture)tex);
		}
	}
	
	@Override
	public void setUseDirectGL(boolean drawPureGL) {
		super.setUseDirectGL(drawPureGL);
		adaptTexCoordsForNPOTUse();
	}
	
	@Override
	public void setTexture(PImage newTexImage) {
		super.setTexture(newTexImage);
		adaptTexCoordsForNPOTUse();
	}
	
	
}
