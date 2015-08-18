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
package org.mt4j.util.opengl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;



/**
 * This tesselator produces no triangle strips/fans but only pure triangles.
 * 
 * @author Chris
 */
public class GluTrianglulator extends GLUtessellatorCallbackAdapter{
       
       /** The glu. */
       private GLU glu;
       
       /** The tesselator. */
       private GLUtessellator tesselator;
       
       /** The tri list. */
       private List<Vertex> triList;
       
       /** The p. */
       private PApplet p;
       
       public static final int WINDING_RULE_ABS_GEQ_TWO = GLU.GLU_TESS_WINDING_ABS_GEQ_TWO;
       public static final int WINDING_RULE_NEGATIVE = GLU.GLU_TESS_WINDING_NEGATIVE;
       public static final int WINDING_RULE_NONZERO = GLU.GLU_TESS_WINDING_NONZERO;
       public static final int WINDING_RULE_ODD = GLU.GLU_TESS_WINDING_ODD;
       public static final int WINDING_RULE_POSITIVE = GLU.GLU_TESS_WINDING_POSITIVE;
       
       
       /**
        * Creates a new GLU tesselator object and defines callback methods, which the
        * tesselator will later call. These are defined in this class.
        * <br><strong>NOTE:</strong> always remember to delete the used triangulator with
        * deleteTess() after use to prevent memory leaking!
        * 
        * @param p the processing context
        */
       public GluTrianglulator(PApplet p) {
    	   this.p = p;
           this.glu = new GLU();
           
           tesselator = glu.gluNewTess(); 
           
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, this);// glVertex3dv);
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, this);// beginCallback);
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, this);// endCallback);
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, this);// combineCallback);
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, this);// errorCallback);
           
           // when you register a GLU_TESS_EDGE_FLAG (or GLU_TESS_EDGE_FLAG_DATA) callback, the 
           // GLU library converts all triangle strips and triangle fans to simple triangle lists.
           glu.gluTessCallback(this.getTesselator(), GLU.GLU_TESS_EDGE_FLAG, this);// errorCallback);
           
           triList = new ArrayList<Vertex>();
       }
       
       
    /* (non-Javadoc)
     * @see javax.media.opengl.glu.GLUtessellatorCallbackAdapter#edgeFlag(boolean)
     */
    public void edgeFlag(boolean boundaryEdge){
    	//even the empty implementation of this method forces the tesselator to
    	//procuce _triangle_ lists only! - no fans or strips
    }
       
   	
    /**
     * Delete tess.
     */
    public void deleteTess(){
    	if (tesselator != null){
	    	glu.gluDeleteTess(tesselator);
	    	tesselator = null;
    	}
    }
    
    @Override
    protected void finalize() throws Throwable {
    	if (this.p instanceof AbstractMTApplication ) {
			AbstractMTApplication mtApp = (AbstractMTApplication) this.p;
			mtApp.invokeLater(new Runnable() {
				public void run() {
					deleteTess();
				}
			});
		}else{
			//TODO use registerPre()?
			//is the object even valid after finalize() is called??
		}
		super.finalize();
    }
    
    
    /**
     * Triangulates the given vertex arrays and creates a single triangle mesh.
     *
     * @param contours the contours
     * @return the MT triangle mesh
     */
    public MTTriangleMesh toTriangleMesh(Vertex[] contours){
    	List<Vertex[]> contoursList = new ArrayList<Vertex[]>();
    	contoursList.add(contours);
    	return this.toTriangleMesh(contoursList, WINDING_RULE_ODD);
    }
    
    /**
     * Triangulates the given vertex arrays and creates a single triangle mesh.
     *
     * @param contours the contours
     * @return the MT triangle mesh
     */
    public MTTriangleMesh toTriangleMesh(List<Vertex[]> contours){
    	return this.toTriangleMesh(contours, WINDING_RULE_ODD);
    }

    /**
        * Triangulates the given vertex arrays and creates a single triangle mesh.
        * 
        * @param contours the contours
        * @param windingRule the winding rule
        * 
        * @return the MT triangle mesh
        */
       public MTTriangleMesh toTriangleMesh(List<Vertex[]> contours, int windingRule){
    	   this.triList.clear();
    	   this.tesselate(contours, windingRule);
    	   List<Vertex> tris = this.getTriList();
    	   Vertex[] verts = tris.toArray(new Vertex[tris.size()]);
    	   GeometryInfo geom = new GeometryInfo(p, verts);
    	   MTTriangleMesh mesh = new MTTriangleMesh(p, geom, false);
    	   return mesh;
       }
       
	    /**
   	 * Tesselate.
   	 * 
   	 * @param contours the contours
   	 * 
   	 * @return the list< vertex>
   	 */
   	public List<Vertex> tesselate(List<Vertex[]> contours){
   		this.triList.clear();
	    	this.tesselate(contours, GLU.GLU_TESS_WINDING_ODD);
	    	return this.getTriList();
	    }
   	
   	
   	
   	public Vertex[] tesselate(Vertex[] contour){
		this.triList.clear();
    	List<Vertex[]> v = new ArrayList<Vertex[]>();
    	v.add(contour);
    	this.tesselate(v, WINDING_RULE_ODD);
    	return this.getTriList().toArray(new Vertex[this.getTriList().size()]);
    }
   	
   	
	    /**
    	 * Tesselates/triangulates the given contours with the given
    	 * winding rule (ie. GLU.GLU_TESS_WINDING_ODD).
    	 * 
    	 * @param contour the contour
    	 * @param windingRule the winding rule
    	 * 
    	 * @return the list< vertex>
    	 */
    	public List<Vertex> tesselate(Vertex[] contour, int windingRule){
    		this.triList.clear();
	    	List<Vertex[]> v = new ArrayList<Vertex[]>();
	    	v.add(contour);
	    	this.tesselate(v, windingRule);
	    	return this.getTriList();
	    }
    	
	   	
	    
	    /**
    	 * Triangulates the given vertex contours and returns a list of triangles.
    	 *
    	 * @param contours the vertex arrays to triangulate into one list of triangles
    	 * @param windingRule the winding rule which determines which parts of the specified shape is "inside" or "outside" the shape.
    	 * @return the produced triangles list
    	 * @see GLU#GLU_TESS_WINDING_ODD
    	 * 
    	 */
	    public List<Vertex> tesselate(List<Vertex[]> contours, int windingRule){
	    	this.triList.clear();
	    	glu.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, windingRule);
//	    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
	    	
	    	//FIXME TEST!
	    	glu.gluTessNormal(tesselator, 0.0, 0.0, 1.0);

	    	glu.gluTessBeginPolygon(tesselator, null);

	    	//Go through all contours
	    	for (Vertex[] varr : contours){
	    		/*
	    			   glu.gluTessBeginContour(tesselator);
	    			   //Go through all vertices of the contour
	    			   for(Vertex v : varr){
	    				   double[] pv = {v.x, v.y, 0, 
	    						   v.getR()/255.0, v.getG()/255.0, v.getB()/255.0, v.getA()/255.0}; //{v.x,v.y,v.z};
	    				   glu.gluTessVertex(tesselator, pv, 0, pv);
	    			   }
	    			   glu.gluTessEndContour(tesselator);
	    		 */
	    		tesselateContour(varr, windingRule);
	    	}
	    	glu.gluTessEndPolygon(tesselator);
	    	return this.getTriList();
	    }


	    /**
    	 * Tesselate contour.
    	 * 
    	 * @param contour the contour
    	 * @param windingRule the winding rule
    	 * 
    	 * @return the list< vertex>
    	 */
    	private List<Vertex> tesselateContour(Vertex[] contour, int windingRule){
    		if (contour.length == 3){
                triList.addAll(Arrays.asList(contour));
                //for (Vertex v : contour){
    			//	triList.add(v);
    			//}
    			return this.triList;
    		}
    		
	    	glu.gluTessBeginContour(tesselator);
            for (Vertex v : contour) {
                double[] pv = {v.x, v.y, v.z, v.getR() / 255.0, v.getG() / 255.0, v.getB() / 255.0, v.getA() / 255.0}; //{v.x,v.y,v.z};
                glu.gluTessVertex(tesselator, pv, 0, pv);
            }
	    	glu.gluTessEndContour(tesselator);
	    	
	    	return this.getTriList();
	    }
	    
       
       //remove?
       /* (non-Javadoc)
        * @see javax.media.opengl.glu.GLUtessellatorCallbackAdapter#begin(int)
        */
       public void begin(int type) {
    	   /*
           switch (type) {
			case GL.GL_TRIANGLE_FAN: System.out.println("GL_TRIANGLE_FAN");	break;
			case GL.GL_TRIANGLE_STRIP:	System.out.println("GL_TRIANGLE_STRIP"); break;
			case GL.GL_TRIANGLES:System.out.println("GL_TRIANGLES");break;
			case GL.GL_POLYGON:System.out.println("GL_TRIANGLES");break;
			case GL.GL_POINTS: System.out.println("GL_POINTS");	break;
			case GL.GL_LINES:	System.out.println("GL_LINES"); break;
			case GL.GL_LINE_LOOP: System.out.println("GL_LINE_LOOP");	break;
			case GL.GL_LINE_STRIP:	System.out.println("GL_LINE_STRIP"); break;
			case GL.GL_QUADS :System.out.println("GL_QUADS");break;
			case GL.GL_QUAD_STRIP:System.out.println("GL_QUAD_STRIP");break;
			default:
				System.out.println("OTHER?!"); break;
			}
			*/
       }
       
       
       //TODO rather save the vertex data in a list for later use, but we 
       //would have to remember which drawing mode the tesselator inteded
       //the vertices for..
       /**
        * Callback function.
        * Gets called by the tesselator when a new vertex should
        * be drawn.
        * 
        * @param vertexData the vertex data
        */
       public void vertex(Object vertexData) {
//    	   double[] dv = (double[]) vertexData;
//    	   gl.glColor4d(dv[3], dv[4], dv[5], dv[6]);
    	   
    	   //System.out.println(((double[]) vertexData).length);
//    	   /*
    	   double[] pointer;
    	      if (vertexData instanceof double[]){
    	        pointer = (double[]) vertexData;
    	        Vertex v = new Vertex();
    	        
    	        if (pointer.length >= 7){ 
//    	        	gl.glColor4dv(pointer, 3);
//    	        	v.setR((float) pointer[3]);
//    	        	v.setG((float) pointer[4]);
//    	        	v.setB((float) pointer[5]);
    	        	
    	        	v.setR((float) pointer[3]*255); //FIXME wirklich *255 interpolaten?
    	        	v.setG((float) pointer[4]*255);
    	        	v.setB((float) pointer[5]*255);
    	        }
//    	        gl.glVertex3dv(pointer, 0);  //TODO
    	       
    	        v.x = (float) pointer[0];
    	        v.y = (float) pointer[1];
    	        v.z = (float) pointer[2];
    	        triList.add(v);
    	      }
//    	    */
       }
       
       

       /* (non-Javadoc)
        * @see javax.media.opengl.glu.GLUtessellatorCallbackAdapter#vertexData(java.lang.Object, java.lang.Object)
        */
       @Override
	public void vertexData(Object vertexData, Object polygonData) {
    	   /*
		if (polygonData instanceof Vertex){
			Vertex v = (Vertex)polygonData;
			gl.glColor4f(v.getR()/255,v.getG()/255,v.getB()/255,v.getA()/255);
		}
    	   gl.glVertex3dv((double[]) vertexData, 0);
    	  */
    	  
//    	   double[] dv = (double[]) vertexData;
//    	   gl.glVertex3d(dv[0],dv[1],dv[2]);
    	   
//    	   double[] dv = (double[]) polygonData;
//    	   gl.glColor4d(dv[4], dv[5], dv[6], dv[7]);
    	   
//    	   gl.glVertex3dv((double[]) vertexData, 0);
	}

       /**
        * CombineCallback is used to create a new vertex when edges intersect.
        * coordinate location is trivial to calculate, but weight[4] may be
        * used to average color, normal, or texture coordinate data.
        * <p>
        * This is called before the call to the vertex-callback method,
        * 
        * @param coords the coords
        * @param data the data
        * @param weight the weight
        * @param outData the out data
        */
       public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
//           double[] vertex = new double[3];
//           vertex[0] = coords[0];
//           vertex[1] = coords[1];
//           vertex[2] = coords[2];
//           
//           outData[0] = vertex;
    	   
    	   //TODO calculate weighted normal!?
           
           //Interpolate the new vertix' color with the 4 intersecting verts
           double[] vertex = new double[7];
	         vertex[0] = coords[0];
	         vertex[1] = coords[1];
	         vertex[2] = coords[2];
	         vertex[3] = weight[0]*((double[]) data[0])[3] +
			             weight[1]*((double[]) data[0])[3] +
			             weight[2]*((double[]) data[0])[3] +
			             weight[3]*((double[]) data[0])[3];
	         
	         vertex[4] = weight[0]*((double[]) data[0])[4] +
			             weight[1]*((double[]) data[0])[4] +
			             weight[2]*((double[]) data[0])[4] +
			             weight[3]*((double[]) data[0])[4];
	         
	         vertex[5] = weight[0]*((double[]) data[0])[5] +
			             weight[1]*((double[]) data[0])[5] +
			             weight[2]*((double[]) data[0])[5] +
			             weight[3]*((double[]) data[0])[5];
	         
	         vertex[6] = weight[0]*((double[]) data[0])[6] +
			             weight[1]*((double[]) data[0])[6] +
			             weight[2]*((double[]) data[0])[6] +
			             weight[3]*((double[]) data[0])[6];
	         
	         outData[0] = vertex;
       }
           
       /* (non-Javadoc)
        * @see javax.media.opengl.glu.GLUtessellatorCallbackAdapter#end()
        */
       public void end() {
    	   
       }
       
       
       /* (non-Javadoc)
        * @see javax.media.opengl.glu.GLUtessellatorCallbackAdapter#error(int)
        */
       public void error(int errnum) {
           System.err.println("Tessellation Error: " + glu.gluErrorString(errnum));
//           System.exit(0);
       }

       
       /**
        * Gets the tesselator.
        * 
        * @return the tesselator
        */
       public GLUtessellator getTesselator(){
    	   return this.tesselator;
       }
       
       /**
        * Gets the tri list.
        * 
        * @return the tri list
        */
       public List<Vertex> getTriList(){
    	   return this.triList;
       }
	
}
