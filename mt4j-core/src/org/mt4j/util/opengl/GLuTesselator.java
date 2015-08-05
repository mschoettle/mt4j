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
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

/**
 * A wrapper class for use of the GLU-Tesselator.
 * The tesselator can break down a shape made of of vertices into triangles.
 * By providing information about the objects to tesselate, we can create
 * polygons with holes, for example.
 * Can be used to record a displaylist or to tesselate and draw in immediate mode. Opengl drawing commands
 * are invoked during tesselation.
 * Only works when using opengl renderer.
 * 
 * @author C.Ruff
 */
@Deprecated
public class GLuTesselator extends GLUtessellatorCallbackAdapter{
	   
   	/** The gl. */
   	private GL gl;
       
       /** The glu. */
       private GLU glu;
       
       /** The tesselator. */
       private GLUtessellator tesselator;
//       private static GLuTesselator instance;
       
       
       /** The last color. */
       private double[] lastColor;
       
       //TODO Triangle meshes zurückggeben!?
      // when you register a GLU_TESS_EDGE_FLAG (or GLU_TESS_EDGE_FLAG_DATA) callback, the 
      // GLU library converts all triangle strips and triangle fans to simple triangle lists.
       
       /**
        * Creates a new tesselator object and defines callback methods, which the
        * tesselator will later call. These are defined in this class.
        * 
        * @param gl the gl
        * @param glu the glu
        */
       public GLuTesselator(GL gl, GLU glu) {
           this.gl = gl;
           this.glu = glu;
           
           lastColor = new double[]{-1,-1,-1};
           
           tesselator = glu.gluNewTess(); 
           
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, this);// glVertex3dv);
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, this);// beginCallback);
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, this);// endCallback);
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, this);// combineCallback);
           glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, this);// errorCallback);
       }
       
       
       
//       /**
//        * 
//        * @param pa
//        * @return
//        */
//	   public static GLuTesselator getInstance(PApplet pa){
//	   		if (instance == null){
//	   			GL gl=((PGraphicsOpenGL)pa.g).gl;
//	   			GLU glu = ((PGraphicsOpenGL)pa.g).glu;
//	   			instance = new GLuTesselator(gl,glu);
//	   			return instance;
//	   		}else
//	   			return instance;
//	   	}
   	
	   
       /**
        * Delete tess.
        */
       public void deleteTess(){
    	   glu.gluDeleteTess(tesselator);
    	   tesselator = null;
//    	   instance = null;
       }
       
       
	   	/**
	   	 * Tesselates the contours, draws it while recording a displaylist and returns
	   	 * the id of the displaylist for later use.
	   	 * 
	   	 * @param contours the contours
	   	 * @param pa the pa
	   	 * 
	   	 * @return the int
	   	 */
		public int tesselateToDisplayList(List<Vertex[]> contours, PApplet pa){
			return tesselateToDisplayList(contours, pa, GLU.GLU_TESS_WINDING_ODD);
		}
		
		/**
		 * Tesselates the contours, draws it while recording a displaylist and returns
		 * the id of the displaylist for later use.
		 * The winding rule determines how the contours are tesselated and which part of
		 * the objects is "inside" and what is "outside".
		 * 
		 * @param contours the contours
		 * @param pa the pa
		 * @param windingRule the winding rule
		 * 
		 * @return the int
		 */
	   	public int tesselateToDisplayList(List<Vertex[]> contours, PApplet pa, int windingRule){
	   		int listId = gl.glGenLists(1);
			gl.glNewList(listId, GL.GL_COMPILE);
			this.tesselate(contours, windingRule);
			gl.glEndList();
			return listId;
	   	}
	   	
	    /**
    	 * Tesselate.
    	 * 
    	 * @param contour the contour
    	 * @param windingRule the winding rule
    	 */
    	public void tesselate(Vertex[] contour, int windingRule){
	    	List<Vertex[]> v = new ArrayList<Vertex[]>();
	    	v.add(contour);
	    	this.tesselate(v, windingRule);
	    }
	   	
	   	
	    /**
    	 * Tesselate.
    	 * 
    	 * @param contours the contours
    	 */
    	public void tesselate(List<Vertex[]> contours){
	    	this.tesselate(contours, GLU.GLU_TESS_WINDING_ODD);
	    }
	    
	    
       
	    /**
    	 * Tesselate.
    	 * 
    	 * @param contours the contours
    	 * @param windingRule the winding rule
    	 */
    	public void tesselate(List<Vertex[]> contours, int windingRule){
	    	glu.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, windingRule);
//	    	   gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			   
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
	    		   
	    	lastColor = new double[]{-1,-1,-1};
	    }
	    

	    /**
    	 * Tesselate contour.
    	 * 
    	 * @param contour the contour
    	 * @param windingRule the winding rule
    	 */
    	private void tesselateContour(Vertex[] contour, int windingRule){
	    	glu.gluTessBeginContour(tesselator);
            for (Vertex v : contour) {
                double[] pv = {v.x, v.y, v.z, v.getR() / 255.0, v.getG() / 255.0, v.getB() / 255.0, v.getA() / 255.0}; //{v.x,v.y,v.z};
                glu.gluTessVertex(tesselator, pv, 0, pv);
            }
	    	glu.gluTessEndContour(tesselator);
	    }
	    
       
       //remove?
       /* (non-Javadoc)
        * @see javax.media.opengl.glu.GLUtessellatorCallbackAdapter#begin(int)
        */
       public void begin(int type) {
           gl.glBegin(type);
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
    	        if (pointer.length == 7){ 
    	        	//Set color only if different from last set color
    	        	if (   lastColor[0] != pointer[3]
    	        		|| lastColor[1] != pointer[4]
    	        		|| lastColor[2] != pointer[5]){
    	        			//Set new color
    	        			gl.glColor4dv(pointer, 3);
    	        		lastColor[0] = pointer[3];
        	        	lastColor[1] = pointer[4];
        	        	lastColor[2] = pointer[5];
    	        	}
//    	        	gl.glColor4dv(pointer, 3);
    	        }
    	        gl.glVertex3dv(pointer, 0);
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
           gl.glEnd();
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
}
