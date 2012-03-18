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
package org.mt4j.components.clusters;


import java.util.ArrayList;
import java.util.Arrays;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsArbitraryPlanarPolygon;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.ConvexQuickHull2D;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

/**
 * The Class Cluster.
 * @author Christopher Ruff
 */
public class Cluster extends MTComponent { //extends MTComponent/implements IMTComponent3D?
	/** The selection polygon. */
 private MTPolygon selectionPolygon;
	
 //FIXME THIS WHOLE CLASS SHOULD BE RE-WORKED!
 
 //Disadvantages of the cluster:
 //each of the children are transformed, not only the cluster parent -> performance..
 //hardwired into mtcanvas -> should be removed..
 //could be replaced with a composite component -> but then we have the problem of picking child components of the group
 
 	private Icamera viewingCam;
 
 	@Override
 	protected Icamera searchViewingCamera() {
 		Icamera viewCam = super.searchViewingCamera();
 		if (viewCam == null && viewingCam != null){
 			return viewingCam;
 		}else{
 			return viewCam;
 		}
 	}
	
	/**
	 * Instantiates a new cluster.
	 * 
	 * @param pApplet the applet
	 * @param selectionPolygon the selection polygon
	 */
	public Cluster(PApplet pApplet,MTPolygon selectionPolygon) {
		this(pApplet, new MTComponent[]{}, selectionPolygon);
	}

	/**
	 * Info: Cluster can only hold instances of MTBaseComponent!.
	 * 
	 * @param pApplet the applet
	 * @param components the components
	 * @param selectionPolygon the selection polygon
	 */
	public Cluster(PApplet pApplet, MTComponent[] components, MTPolygon selectionPolygon) {
		super(pApplet);
		
		if (components.length > 0 && components[0] != null){
			this.viewingCam = components[0].getViewingCamera();
		}
		
		this.selectionPolygon = selectionPolygon;
		if (selectionPolygon != null){
			this.addChild(selectionPolygon);
		}

        for (MTComponent component3D : components) {
            this.addChild(component3D);
        }
		
		this.setName("unnamed Cluster");
	}
	
	

	/**
	 * Gets the cluster polygon.
	 * 
	 * @return the cluster polygon
	 */
	public MTPolygon getClusterPolygon() {
		return selectionPolygon;
	}

	/**
	 * Calculates the convex hull of all its children.
	 * Then changes the cluster-polygon to represent that convex hull
	 * and adds it as a child.
	 */
	public void packClusterPolygon(){
		ArrayList<Vector3D> allClusteredVerts = new ArrayList<Vector3D>();
		int shapes = 0;
		
		//Remove the old clusterpoly
		MTPolygon clusterPoly = getClusterPolygon();
		this.removeChild(clusterPoly);
		
		MTComponent[] children = this.getChildren();
        for (MTComponent component : children) {
            //Get vertices for convex hull of all selected components
            if (component instanceof AbstractShape) {
                shapes++;
                AbstractShape shape = (AbstractShape) component;
//				Vertex[] verts = shape.getVerticesPickingWorld();
                Vector3D[] verts = null;

//				if (shape.isBoundingShapeSet()){
//					 verts = shape.getBoundingShape().getVectorsGlobal();
//				}else{
//					 verts = shape.getVerticesGlobal();
//				}

                if (shape.hasBounds()) {
                    if (shape.getBounds() instanceof BoundsZPlaneRectangle || shape.getBounds() instanceof BoundsArbitraryPlanarPolygon) {
                        verts = shape.getBounds().getVectorsGlobal();
                    } else {
                        BoundsZPlaneRectangle b = new BoundsZPlaneRectangle(shape);
                        verts = b.getVectorsGlobal();
                    }
                } else {
                    BoundsZPlaneRectangle b = new BoundsZPlaneRectangle(shape);
                    verts = b.getVectorsGlobal();
//					 verts = shape.getVerticesGlobal();
                }

                allClusteredVerts.addAll(Arrays.asList(verts));
                //for (Vector3D v : verts){
                //	allClusteredVerts.add(v);
                //}
            }
        }
		
		if (shapes != 0 && shapes == children.length){ //If all children are of type abstractShape
			
			ArrayList<Vector3D> hull = ConvexQuickHull2D.getConvexHull2D(allClusteredVerts);
			if (hull.size() > 0){
				//Correctly close polygon with 1.st vertex again
				hull.add(hull.get(0).getCopy());
				
				Vertex[] newVerts = new Vertex[hull.size()];
				for (int i = 0; i < hull.size(); i++) {
					Vector3D vec = hull.get(i);
					newVerts[i] = new Vertex(vec);
				}
				
//				Vertex[] newVerts = (Vertex[])hull.toArray(new Vertex[hull.size()]);
//				System.out.println("Hull vertices: ");
				for (Vertex v : newVerts){
					v.setRGBA(100,150,250, 50);
				}
				
				clusterPoly.setVertices(newVerts);
				
				clusterPoly.setBoundsBehaviour(AbstractShape.BOUNDS_DONT_USE);
//				clusterPoly.setBoundingShape(new BoundsArbitraryPlanarPolygon(clusterPoly, clusterPoly.getVerticesLocal()));
				
				//Reset matrix of the clusterpoly because the new vertices are set at the global location
				clusterPoly.setLocalMatrix(new Matrix()); 
				
				//FIXME center are is negative if verts are in counterclockwise order?
//				Vector3D clusterCenter = clusterPoly.getCenterPointGlobal();
//				clusterPoly.scaleGlobal(1.1f, 1.1f, 1, new Vector3D(-1* clusterCenter.x, -1 * clusterCenter.y, clusterCenter.z));
				clusterPoly.scale(1.1f, 1.1f, 1, clusterPoly.getCenterPointLocal(), TransformSpace.LOCAL);
				this.addChild(clusterPoly);
			}else{
				System.err.println("Couldnt pack polygon.");
			}
		}
	}

	/**
	 * overridden to only put the cluster polygon last
	 * in its parent list.
	 */
	@Override
	public void sendToFront() {
		if (this.getClusterPolygon() != null){
			this.getClusterPolygon().sendToFront();
		}
		
		for (int i = 0; i < this.getChildren().length; i++) {
			MTComponent childComp = this.getChildren()[i];
			if (!childComp.equals(this.getClusterPolygon()))
				childComp.sendToFront();
		}
	}

	@Override
	public void addChild(int i, MTComponent tangibleComp) {
		//Overridden, so the component keeps it original parent
		this.getChildList().add(i, tangibleComp);
	}

	@Override
	public void addChild(MTComponent tangibleComp) {
		this.viewingCam = tangibleComp.getViewingCamera();
		this.getChildList().add(tangibleComp);
	}

	@Override
	public void addChildren(MTComponent[] tangibleComps) {
		if (tangibleComps.length > 0 && tangibleComps[0].getViewingCamera() != null){ //FIXME TEST -> 
			this.viewingCam = tangibleComps[0].getViewingCamera();
		}
        for (MTComponent object : tangibleComps) {
            //Add direct objects
            this.getChildList().add(object);
        }
	}

	@Override
	public void removeAllChildren() {
		this.viewingCam = null;
		this.getChildList().clear();
	}

	@Override
	public void removeChild(int i) {
		Icamera newViewCam = null; 
		for (MTComponent comp  : this.getChildren()) {
			if (comp.getViewingCamera() != null){
				newViewCam = comp.getViewingCamera();
				break;
			}
		}
		this.viewingCam = newViewCam;
		this.getChildList().remove(i);
	}

	@Override
	public void removeChild(MTComponent comp) {
		Icamera newViewCam = null; 
		for (MTComponent compo  : this.getChildren()) {
			if (compo.getViewingCamera() != null){
				newViewCam = compo.getViewingCamera();
				break;
			}
		}
		this.viewingCam = newViewCam;
		this.getChildList().remove(comp);
	}
	
	
	//TODO GANZE CLUSTERING ÜBERARBEITEN
	//SO GIBTS PROBLEME WENN GECLUSTERTE OBJECTE IN VERSCHIEDENEN PARENTS SIND
	// => DANN WERDEN SIE VERSCHIEDEN TRANSFORMIERT
	//  aM BESTEN ALLE ALS KINDER DES CLUSTERPOLYS MACHEN UND DAS AUF COMPOSITE=TRUE MACHEN
	//  ABER WOHIN GEHEN SIE WENN SIE AUS DEM CLUSTER GELÖST WERDEN? VIELLEICHT GIBTS DIE ALTE GRUPPE NICHT MEHR?
	//  DANN AUF DEN WORLD CANVAS SETZEN; => ABER AN GLEICHER STELLE!
	
	
	/**
	 * Transforms the shapes local coordinate space by the given matrix.
	 * 
	 * @param transformMatrix the transform matrix
	 */
	public void transform(Matrix transformMatrix) {
		for (MTComponent c : this.getChildList()){
			c.transform(transformMatrix);
		}
	}

	
	public void translateGlobal(Vector3D dirVect) {
		for (MTComponent c : this.getChildList()){
			c.translateGlobal(dirVect);
		}
	}
	
	public void translate(Vector3D dirVect) {
		for (MTComponent c : this.getChildList()){
			c.translate(dirVect);
		}
	}
	
	
	public void rotateXGlobal(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateXGlobal(rotationPoint, degree);
		}
	}
	
	public void rotateX(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateX(rotationPoint, degree);
		}
	}
	
	
	public void rotateYGlobal(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateYGlobal(rotationPoint, degree);
		}
	}
	
	public void rotateY(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateY(rotationPoint, degree);
		}
	}
	
	public void rotateZGlobal(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateZGlobal(rotationPoint, degree);
		}
	}
	
	
	public void rotateZ(Vector3D rotationPoint, float degree) {
		for (MTComponent c : this.getChildList()){
			c.rotateZ(rotationPoint, degree);
		}
	}

	public void scaleGlobal(float factor, Vector3D scaleReferencePoint) {
		this.scaleGlobal(factor, factor, factor, scaleReferencePoint);
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
		}
	}
	
	
	public void scale(float factor, Vector3D scaleReferencePoint) {
		this.scale(factor, factor, factor, scaleReferencePoint);
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
	}
}
