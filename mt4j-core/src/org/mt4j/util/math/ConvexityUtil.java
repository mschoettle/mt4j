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
package org.mt4j.util.math;

/**
 * Class to check whether a series of vectors which belong to
 * a polygon are convex or concave.
 * <p>C code from the article
 * "Testing the Convexity of a Polygon"
 * by Peter Schorn and Frederick Fisher,
 *	(schorn@inf.ethz.ch, fred@kpc.com)
 * in "Graphics Gems IV", Academic Press, 1994
 * @author C.Ruff
 */
public class ConvexityUtil {

	/** The Constant NotConvex. */
	public static final int NotConvex = 0;
	
	/** The Constant NotConvexDegenerate. */
	public static final int NotConvexDegenerate = 1;
	
	/** The Constant ConvexDegenerate. */
	public static final int ConvexDegenerate = 2;
	
	/** The Constant ConvexCCW. */
	public static final int ConvexCCW = 3;
	
	/** The Constant ConvexCW. */
	public static final int ConvexCW = 4;

	/*
	 * C code from the article
	 * "Testing the Convexity of a Polygon"
	 * by Peter Schorn and Frederick Fisher,
	 *	(schorn@inf.ethz.ch, fred@kpc.com)
	 * in "Graphics Gems IV", Academic Press, 1994
	 */

	/* Reasonably Optimized Routine to Classify a Polygon's Shape */

	/*

	.. code omitted which reads polygon, stores in an array, and calls
		classifyPolygon2()
	*/

//	typedef enum { NotConvex, NotConvexDegenerate,
//		       ConvexDegenerate, ConvexCCW, ConvexCW } PolygonClass;
//
//	typedef double	Number;		/* float or double */
//
//	#define ConvexCompare(delta)						\
//    ( (delta[0] > 0) ? -1 :	/* x coord diff, second pt > first pt */\
//      (delta[0] < 0) ?	1 :	/* x coord diff, second pt < first pt */\
//      (delta[1] > 0) ? -1 :	/* x coord same, second pt > first pt */\
//      (delta[1] < 0) ?	1 :	/* x coord same, second pt > first pt */\
//      0 )			/* second pt equals first point */

	      
  	/**
	 * Convex compare.
	 * 
	 * @param delta the delta
	 * 
	 * @return the int
	 */
	private static int ConvexCompare(Vector3D delta){
  		int returnval = ( 
  				  (delta.x > 0) ? -1 :	/* x coord diff, second pt > first pt */
			      (delta.x < 0) ?  1 :	/* x coord diff, second pt < first pt */
			      (delta.y > 0) ? -1 :	/* x coord same, second pt > first pt */
			      (delta.y < 0) ?  1 :	/* x coord same, second pt > first pt */
			       0 );
  		
  		return returnval;
  	}
  	
	/**
	 * Convex get point delta.
	 * 
	 * @param delta the delta
	 * @param pprev the pprev
	 * @param pcur the pcur
	 */
	private static void ConvexGetPointDelta(Vector3D delta, Vector3D pprev, Vector3D pcur) {
	    pcur = pVert[iread++];						
	    delta.x = pcur.x - pprev.x;					
	    delta.y = pcur.y - pprev.y;	
	}
	
	
	/**
	 * Convex cross.
	 * 
	 * @param p the p
	 * @param q the q
	 * 
	 * @return the float
	 */
	private static float ConvexCross(Vector3D p, Vector3D q){
		return 	(p.x * q.y - p.y * q.x);
	}
//
	
//	#define ConvexCheckTriple						
//	    if ( (thisDir = ConvexCompare(dcur)) == -curDir ) {			
//		  ++dirChanges;							
//		  /* The following line will optimize for polygons that are  */
//		  /* not convex because of classification condition 4,	     */ 
//		  /* otherwise, this will only slow down the classification. */ 
//		  /* if ( dirChanges > 2 ) return NotConvex;		     */ 
//	    }									
//	    curDir = thisDir;							
//	    cross = ConvexCross(dprev, dcur);					
//	    if ( cross > 0 ) { if ( angleSign == -1 ) return NotConvex;		
//			       angleSign = 1;					
//			     }							
//	    else if (cross < 0) { if (angleSign == 1) return NotConvex;		
//				    angleSign = -1;				
//				  }						
//	    pSecond = pThird;		/* Remember ptr to current point. */	
//	    dprev[0] = dcur[0];		/* Remember current delta.	  */	
//	    dprev[1] = dcur[1];							

	    /**
 * Convex check triple.
 * 
 * @return the int
 */
private static int ConvexCheckTriple(){
		    if ( (thisDir = ConvexCompare(dcur)) == -curDir ) {			
				  ++dirChanges;		

//				  System.out.println("Dirchange! " + dirChanges);
				  /* The following line will optimize for polygons that are  */
				  /* not convex because of classification condition 4,	     */ 
				  /* otherwise, this will only slow down the classification. */ 
				  /* if ( dirChanges > 2 ) return NotConvex;		     */ 
			    }				
		    
			    curDir = thisDir;							
			    cross = ConvexCross(dprev, dcur);	
			    
			    if ( cross > 0 ) { 
			    	if ( angleSign == -1 ){ 
			    		return NotConvex;	
			    	}
					      angleSign = 1;					
			    } else if (cross < 0) { 
			    	if (angleSign == 1){ 
			    		return NotConvex; //TODO ander smachen
//			    		return NotConvexDegenerate;
			    	}
					    angleSign = -1;				
				}						
			    pSecond = pThird;		/* Remember ptr to current point. */	
			    dprev.x = dcur.x;		/* Remember current delta.	  */	
			    dprev.y = dcur.y;	
			    
			    return -1;
	    }
	    
	/** The nvert. */
	private static int nvert;
	
	/** The p vert. */
	private static Vector3D[] pVert;
	
	/** The iread. */
	private static int 
		curDir, 
		thisDir, 
		dirChanges = 0,
	    angleSign = 0, 
	    iread ;
	
	/** The cross. */
	private static float cross;
	
	/** The dprev. */
	private static Vector3D dprev = new Vector3D(0,0,0); 
	
	/** The dcur. */
	private static Vector3D dcur = new Vector3D(0,0,0); 
	
	/** The p second. */
	private static Vector3D pSecond = new Vector3D(0,0,0); 
	
	/** The p third. */
	private static Vector3D pThird = new Vector3D(0,0,0); 
	
	/** The p save second. */
	private static Vector3D pSaveSecond = new Vector3D(0,0,0); 

	
	/**
	 * Classify polygon2.
	 * 
	 * @param vertCount the vert count
	 * @param pVerts the verts
	 * 
	 * @return the int
	 */
	public static int classifyPolygon2(int vertCount, Vector3D[] pVerts){
	 nvert = vertCount;
	 pVert = Vector3D.getDeepVertexArrayCopy(pVerts);
//	 pVert = pVerts;
	 
	    /* if ( nvert <= 0 ) return error;	     if you care */

	    /* Get different point, return if less than 3 diff points. */
	    if ( nvert < 3 ) 
	    	return ConvexDegenerate;
	    
	    iread = 1;
	    while ( true ) {
			ConvexGetPointDelta(dprev, pVert[0], pSecond);
			
			if ( dprev.x != 0 || dprev.y != 0) 
				break;
			
			/* Check if out of points. Check here to avoid slowing down cases
			 * without repeated points.
			 */
			if ( iread >= nvert ) 
				return ConvexDegenerate;
	    }

	    pSaveSecond = pSecond;

	    curDir = ConvexCompare(dprev);	/* Find initial direction */

	    while ( iread < nvert ) {
			/* Get different point, break if no more points */
			ConvexGetPointDelta(dcur, pSecond, pThird );
			if ( dcur.x == 0.0  &&	 dcur.y == 0.0 ) 
				continue;
	
			if (ConvexCheckTriple() == NotConvex ){		/* Check current three points */
				return NotConvex;
			}
	    }

	    /* Must check for direction changes from last vertex back to first */
	    pThird = pVert[0];			/* Prepare for 'ConvexCheckTriple' */
	    dcur.x = pThird.x - pSecond.x;
	    dcur.y = pThird.y - pSecond.y;
	   
	    if ( ConvexCompare(dcur) != 0) {
	    	if (ConvexCheckTriple() == NotConvex ){		/* Check current three points */
				return NotConvex;
	    	}
	    }

	    /* and check for direction changes back to second vertex */
	    dcur.x = pSaveSecond.x - pSecond.x;
	    dcur.y = pSaveSecond.y - pSecond.y;
	    
	    if (ConvexCheckTriple() == NotConvex ){		/* Don't care about 'pThird' now */
			return NotConvex;			
	    }

	    /* Decide on polygon type given accumulated status */
	    if ( dirChanges > 2 )
	    	return (angleSign != 0 ) ? NotConvex : NotConvexDegenerate;

	    if ( angleSign > 0 ) 
	    	return ConvexCCW;
	    
	    if ( angleSign < 0 ) 
	    	return ConvexCW;
	    
	    return ConvexDegenerate;
	}



	
	
	
}
