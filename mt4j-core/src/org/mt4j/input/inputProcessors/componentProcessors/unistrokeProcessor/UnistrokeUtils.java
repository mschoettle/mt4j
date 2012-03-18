package org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeTemplates.Template;
import org.mt4j.util.math.Vector3D;

/**
 * The Class MTDollarUtils, all calculations for the Gesture Recognizer
 * Based on the Code from 
 * http://depts.washington.edu/aimgroup/proj/dollar/
 * http://www.openprocessing.org/visuals/?visualID=600
 */
public class UnistrokeUtils {
	
	/** The Infinity Constant. */
	private final float Infinity = 1e9f;
	
	/** The Number of points after resampling */
	private final int NumPoints = 128;
	
	/** The Square size. */
	private final float SquareSize = 250;
	
	/** The Half diagonal. */
	private final float HalfDiagonal = (float)(0.5 * Math.sqrt(250.0 * 250.0 + 250.0 * 250.0));
	
	/** The Angle range. */
	private final float AngleRange = 45;
	
	/** The Angle precision. */
	private final float AnglePrecision = 2;
	
	/** The Phi Constant (Golden Ratio) */
	private final float Phi = (float)(0.5 * (-1.0 + Math.sqrt(5.0))); // Golden Ratio
	
	/** The recognizer. */
	private final Recognizer recognizer;
	
	/** The thisclass. */
	private final UnistrokeUtils thisclass;

	/**
	 * Instantiates a new mT dollar utils.
	 */
	public UnistrokeUtils () {
		this.thisclass = this;
		this.recognizer = new Recognizer();

	}



	/**
	 * The Enum Direction.
	 */
	public enum Direction {
		
			/** The CLOCKWISE. */
			CLOCKWISE, 
		 /** The COUNTERCLOCKWISE. */
		 COUNTERCLOCKWISE;
	}

	/**
	 * The Enum DollarGesture.
	 */
	public enum UnistrokeGesture {
		
		/** The TRIANGLE. */
		TRIANGLE, 
 /** The X. */
 X, 
 /** The RECTANGLE. */
 RECTANGLE, 
 /** The CIRCLE. */
 CIRCLE, 
 /** The CHECK. */
 CHECK, 
 /** The CARET. */
 CARET, 
 /** The QUESTION. */
 QUESTION, 
 /** The ARROW. */
 ARROW, 
 /** The LEFTSQUAREBRACKET. */
 LEFTSQUAREBRACKET, 
 /** The RIGHTSQUAREBRACKET. */
 RIGHTSQUAREBRACKET, 
 /** The V. */
 V, 
 /** The DELETE. */
 DELETE, 
 /** The LEFTCURLYBRACE. */
 LEFTCURLYBRACE, 
 /** The RIGHTCURLYBRACE. */
 RIGHTCURLYBRACE, 
 /** The STAR. */
 STAR, 
 /** The PIGTAIL. */
 PIGTAIL, 
 /** The NOGESTURE. */
 NOGESTURE, 
 /** The CUSTOMGESTURE. */
 CUSTOMGESTURE,
 PACKAGE;
	}

	/**
	 * The Class Rectangle.
	 */
	class Rectangle
	{
	  
  	/** The X. */
  	float X;
	  
  	/** The Y. */
  	float Y;
	  
  	/** The Width. */
  	float Width;
	  
  	/** The Height. */
  	float Height;
	  
  	/**
  	 * Instantiates a new rectangle.
  	 * 
  	 * @param x the x value
  	 * @param y the y value
  	 * @param width the width
  	 * @param height the height
  	 */
  	Rectangle( float x, float y, float width, float height)
	  {
	    X = x;
	    Y = y;
	    Width = width;
	    Height = height;
	  }
	}

	/**
	 * Gets the recognizer.
	 * 
	 * @return the recognizer
	 */
	public Recognizer getRecognizer() {
		return recognizer;
	}

	public Recorder getRecorder() {
		return new Recorder();
	}
	
	public class Recorder {
		
		public Recorder(){
			
		}
		
		public void record(List<Vector3D> points) {
			points = Resample(points, 64, Direction.CLOCKWISE);

			
			System.out.println("Begin Gesture");
			int position = 1;
			for (Vector3D point: points) {
				if (position < 4) {
					position++;
					System.out.print("new Vector3D(" + (int)point.getX() + "," + (int)point.getY() + "),");
				} else {
					position = 1;
					System.out.println("new Vector3D(" + (int)point.getX() + "," + (int)point.getY() + "),");
				}
				
				
			}
			System.out.println("End Gesture");
		}
		
	}
	
	
	/**
	 * The Class Recognizer.
	 */
	public class Recognizer {
		
		/** The List of registered Templates. */
		List<Template> Templates = new ArrayList<Template>();
		
		/** The available dollar template class. */
		UnistrokeTemplates dollarTemplates;

		/**
		 * Instantiates a new recognizer.
		 */
		public Recognizer() {
			dollarTemplates = new UnistrokeTemplates(Templates, thisclass);

		}

		/**
		 * Adds a template.
		 * 
		 * @param gesture the gesture
		 * @param direction the direction
		 */
		public void addTemplate(UnistrokeGesture gesture, Direction direction) {
			dollarTemplates.addTemplate(gesture, direction);
		}
		

		/**
		 * Recognize.
		 * 
		 * @param points the points
		 * @return the dollar gesture
		 */
		UnistrokeGesture Recognize(List<Vector3D> points) {
				points = Resample(points, getNumPoints(), Direction.CLOCKWISE);
			points = RotateToZero(points);
			points = ScaleToSquare(points, getSquareSize());
			points = TranslateToOrigin(points);
			float best = getInfinity();
			float sndBest = getInfinity();
			UnistrokeGesture g = null;
			Direction di = null;

			for (Template template : Templates) {
				float d = DistanceAtBestAngle(points, template, -getAngleRange(), getAngleRange(), getAnglePrecision());
				if (d < best) {
					sndBest = best;
					best = d;
					g = template.gesture;
					di = template.direction;
				} else if (d < sndBest) {
					sndBest = d;
				}
			}
			float score = 1.0f - (best / getHalfDiagonal());
			float otherScore = 1.0f - (sndBest / getHalfDiagonal());
			float ratio = otherScore / score;

			System.out.println("Gesture recognition score: " + score);
			if (g != null && score > 0.7) {
				
				return g;
			} else {
				
				return UnistrokeGesture.NOGESTURE;
			}
		}

	}
	
	/**
	 * Resample the points so they are evenly distributed
	 * 
	 * @param points the points before resampling
	 * @param n the number of points after resampling
	 * @param dir the direction
	 * @return the resampled list of points
	 */
	List<Vector3D> Resample(List<Vector3D> points, int n, Direction dir)
	{
	   float I = PathLength(points) / ( (float)n -1.0f );
	   float D = 0.0f;
	   List<Vector3D> newpoints = new ArrayList<Vector3D>();
	   Stack<Vector3D> stack = new Stack<Vector3D>();



	   if (dir == Direction.CLOCKWISE) {
		   for(Vector3D point: points)
		   {
		     stack.insertElementAt(point, 0);
		   }
	   } else {
		   for(Vector3D point: points)
		   {
		     stack.push(point);
		   }
	   }

	   while( !stack.isEmpty())
	   {
	       Vector3D pt1 = stack.pop();

	       if( stack.isEmpty())
	       {
	         newpoints.add(pt1);
	         continue;
	       }
	       Vector3D pt2 = stack.peek();
	       float d = pt1.distance2D(pt2);
	       if( (D + d) >= I)
	       {
	          float qx = pt1.getX() + (( I - D ) / d ) * (pt2.getX() - pt1.getX());
	          float qy = pt1.getY() + (( I - D ) / d ) * (pt2.getY() - pt1.getY());
	          Vector3D q = new Vector3D( qx, qy);
	          newpoints.add(q);
	          stack.push( q );
	          D = 0.0f;
	       } else {
	         D += d;
	       }
	   }

	   if( newpoints.size() == (n -1) )
	   {
	     newpoints.add(points.get(points.size() - 1));
	   }
	   return newpoints;

	}


	/**
	 * Scale to square.
	 * 
	 * @param points the points
	 * @param sz the size
	 * @return the modified list
	 */
	List<Vector3D> ScaleToSquare( List<Vector3D> points, float sz)
	{
	    Rectangle B = BoundingBox( points );
	    List<Vector3D> newpoints = new ArrayList<Vector3D>();
	    for(Vector3D point: points)
	    {
	       float qx = point.getX() * (sz / B.Width);
	       float qy = point.getY() * (sz / B.Height);
	       newpoints.add(new Vector3D(qx, qy));
	    }
	    return  newpoints;
	}

	/**
	 * Distance at best angle.
	 * 
	 * @param points the points
	 * @param T the Template to compare to
	 * @param a the Theta a
	 * @param b the Theta b
	 * @param threshold the threshold
	 * @return the Distance at best Angle
	 */
	float DistanceAtBestAngle( List<Vector3D> points, Template T, float a, float b, float threshold)
	{
	   float x1 = Phi * a + (1 - Phi) * b;
	   float f1 = DistanceAtAngle(points, T, x1);
	   float x2 = (1 - Phi) * a + Phi * b;
	   float f2 = DistanceAtAngle(points, T, x2);
	   while( Math.abs( b - a ) > threshold)
	   {
	     if( f1 < f2 )
	     {
	       b = x2;
	       x2 = x1;
	       f2 = f1;
	       x1 = Phi * a + (1.0f - Phi) * b;
	       f1 = DistanceAtAngle(points, T, x1);
	     }
	     else
	     {
	       a = x1;
	       x1 = x2;
	       f1 = f2;
	       x2 = (1.0f - Phi) * a + Phi * b;
	       f2 = DistanceAtAngle(points, T, x2);
	     }
	   }
	   return Math.min(f1, f2);
	}


	/**
	 * Distance at angle.
	 * 
	 * @param points the points
	 * @param T the t
	 * @param theta the angle theta
	 * @return the distance at angle theta
	 */
	float DistanceAtAngle( List<Vector3D> points, Template T, float theta)
	{
	  RotateBy( points, theta);
	  return PathDistance( points, T.Points);
	}


	/**
	 * Translate to origin.
	 * 
	 * @param points the points
	 * @return the translated points
	 */
	List<Vector3D> TranslateToOrigin( List<Vector3D> points)
	{
	   Vector3D c = Centroid( points);
	   List<Vector3D> newpoints = new ArrayList<Vector3D>();
	   for(Vector3D point: points)
	   {
	     float qx = point.getX() - c.getX();
	     float qy = point.getY() - c.getY();
	     newpoints.add(new Vector3D(qx, qy));
	   }
	   return newpoints;
	}





	/**
	 * Path length.
	 * 
	 * @param points the points
	 * @return the path length
	 */
	private float PathLength (List<Vector3D> points) {
		float length = 0;
		Vector3D lastPosition = null;
		for (Vector3D v: points) {
			if (lastPosition == null) lastPosition = v;
			length += v.distance2D(lastPosition);
			lastPosition = v;
		}

		return length;
	}

	/**
	 * Path distance.
	 * 
	 * @param pts1 the first set of points
	 * @param pts2 the second set of points
	 * @return the Path distance
	 */
	float PathDistance( List<Vector3D> pts1, List<Vector3D> pts2)
	{
	   if( pts1.size() != pts2.size())
	   {
	     
	     return Infinity;
	   }
	   float d = 0.0f;
	   for( int i = 0; i < pts1.size(); i++)
	   {
	     d += pts1.get(i).distance2D( pts2.get(i));
	   }
	   return d / (float)pts1.size();
	}
	
	/**
	 * Bounding box.
	 * 
	 * @param points the points inside the Bounding Box
	 * @return the rectangle
	 */
	Rectangle BoundingBox( List<Vector3D> points)
	{
	  float minX = Infinity;
	  float maxX = -Infinity;
	  float minY = Infinity;
	  float maxY = -Infinity;

	  for(Vector3D point: points)
	  {
	    minX = Math.min( point.getX(), minX);
	    maxX = Math.max( point.getX(), maxX);
	    minY = Math.min( point.getY(), minY);
	    maxY = Math.max( point.getY(), maxY);
	  }
	  return new Rectangle( minX, minY, maxX - minX, maxY - minY);
	}



	/**
	 * Centroid.
	 * 
	 * @param points the points
	 * @return the Centroid
	 */
	Vector3D Centroid(List<Vector3D> points)
	{
	  Vector3D centroid = new Vector3D(0, 0);
	  for(Vector3D point: points)
	  {
		  centroid.setX(centroid.getX() + point.getX());
		  centroid.setY(centroid.getY() + point.getY());
	  }
	  centroid.setX(centroid.getX() / points.size());
	  centroid.setY(centroid.getY() / points.size());
	  return centroid;
	}

	/**
	 * Rotate by.
	 * 
	 * @param points the points
	 * @param theta the theta
	 * @return rotated list of points
	 */
	List<Vector3D>  RotateBy( List<Vector3D> points, float theta)
	{
	   Vector3D c = Centroid( points );
	   float Cos = (float)Math.cos( theta );
	   float Sin = (float)Math.sin( theta );

	   List<Vector3D> newpoints = new ArrayList<Vector3D>();
	   for(Vector3D point: points)
	   {
	     float qx = (point.getX() - c.getX()) * Cos - (point.getY() - c.getY()) * Sin + c.getX();
	     float qy = (point.getX() - c.getX()) * Sin + (point.getY() - c.getY()) * Cos + c.getY();
	     newpoints.add(new Vector3D( qx, qy ));
	   }
	   return newpoints;
	}

	/**
	 * Rotate to zero.
	 * 
	 * @param points the points
	 * @return rotated list of points
	 */
	List<Vector3D> RotateToZero( List<Vector3D>  points)
	{
	   //FIXME: Check for empty list
		Vector3D c = Centroid( points );
	   float theta = (float)Math.atan2( c.getY() - points.get(0).getY(), c.getX() - points.get(0).getX());
	   return RotateBy( points, -theta);

	}


	/**
	 * Gets the infinity.
	 * 
	 * @return the infinity
	 */
	public float getInfinity() {
		return Infinity;
	}


	/**
	 * Gets the num points.
	 * 
	 * @return the num points
	 */
	public int getNumPoints() {
		return NumPoints;
	}


	/**
	 * Gets the square size.
	 * 
	 * @return the square size
	 */
	public float getSquareSize() {
		return SquareSize;
	}


	/**
	 * Gets the half diagonal.
	 * 
	 * @return the half diagonal
	 */
	public float getHalfDiagonal() {
		return HalfDiagonal;
	}


	/**
	 * Gets the angle range.
	 * 
	 * @return the angle range
	 */
	public float getAngleRange() {
		return AngleRange;
	}


	/**
	 * Gets the angle precision.
	 * 
	 * @return the angle precision
	 */
	public float getAnglePrecision() {
		return AnglePrecision;
	}


	/**
	 * Gets the phi.
	 * 
	 * @return the phi
	 */
	public float getPhi() {
		return Phi;
	}
}
