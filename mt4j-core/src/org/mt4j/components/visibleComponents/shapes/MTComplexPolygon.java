package org.mt4j.components.visibleComponents.shapes;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.css.util.CSSHelper;
import org.mt4j.components.css.util.CSSStylableComponent;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GluTrianglulator;

import processing.core.PApplet;

/**
 * This class is used to display non-simple polygons like concave polygons, polygons with wholes or more than only 1 contour 
 * since MTPolygon can only handle convex polygons. 
 * <br><br>Note:  calls to setVertices() will be more expensive here since a triangulation algorithm is used to split the contours
 * up into triangles.
 * <br>Note: we should only provide 2D vertices (all vertices with the same z value) to not confuse the triangulation algorithm
 * <br>Note: setGeometryInfo() should never be called directly on this class since the geometry has to first be calculated
 * from the specified vertices. Use setVertices() instead if you want to change the shape.
 */
public class MTComplexPolygon extends MTTriangleMesh implements CSSStylableComponent{
	
	/** The Constant WINDING_RULE_ODD. */
	public static final int WINDING_RULE_ODD 		= GluTrianglulator.WINDING_RULE_ODD;
	
	/** The Constant WINDING_RULE_NONZERO. */
	public static final int WINDING_RULE_NONZERO 	= GluTrianglulator.WINDING_RULE_NONZERO;

	//TODO override the intersection/containspoint methods to only use outline?
	//-> else we do expensive checks against each triangle
	
	/**
	 * Instantiates a new mT complex polygon with only one contour.
	 *
	 * @param app the app
	 * @param vertices the vertices
	 */
	public MTComplexPolygon(PApplet app, Vertex[] vertices) {
		super(app, new GeometryInfo(app, new Vertex[]{}), false);
		this.setVertices(vertices);
		this.setNoStroke(false);
		
//		 /*
        if (app instanceof AbstractMTApplication) {
                this.mtApp = (AbstractMTApplication)app;
                this.cssHelper = new CSSHelper(this, mtApp);
                if (this.mtApp.getCssStyleManager().isGloballyEnabled()) {
                        this.enableCSS();
                }
        }
//        */
	}
	
	/**
	 * Instantiates a new mT complex polygon using multiple contours and the ODD winding rule.
	 * So if a second contour is inside of the first contour, the second contour would cut a hole into the first one.
	 *
	 * @param app the app
	 * @param contours the contours
	 */
	public MTComplexPolygon(PApplet app, List<Vertex[]> contours) {
		this(app, contours, GluTrianglulator.WINDING_RULE_ODD);
	}
	
	/**
	 * Instantiates a new mT complex polygon using multiple contours and the specified winding rule.
	 * So if a second contour is inside of the first contour, the second contour would cut a hole into the first one.
	 * @param app the app
	 * @param contours the contours
	 * @param windingRule the winding rule
	 */
	public MTComplexPolygon(PApplet app, List<Vertex[]> contours, int windingRule) {
		super(app, new GeometryInfo(app, new Vertex[]{}), false);
		this.setVertices(contours, windingRule);
		this.setNoStroke(false);
		
//		 /*
        if (app instanceof AbstractMTApplication) {
                this.mtApp = (AbstractMTApplication)app;
                this.cssHelper = new CSSHelper(this, mtApp);
                if (this.mtApp.getCssStyleManager().isGloballyEnabled()) {
                        this.enableCSS();
                }
        }
//        */
	}
	
	
	/**
	 * Sets the vertices.
	 *
	 * @param contours the contours
	 * @param windingRule the winding rule
	 */
	public void setVertices(List<Vertex[]> contours, int windingRule) {
		this.setOutlineContours(contours);
		
		GluTrianglulator triangulator = new GluTrianglulator(getRenderer());
		List<Vertex> tris = triangulator.tesselate(contours, windingRule);
		triangulator.deleteTess();
		
		super.setVertices(tris.toArray(new Vertex[tris.size()]));
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh#setVertices(org.mt4j.util.math.Vertex[])
	 */
	@Override
	public void setVertices(Vertex[] vertices) {
		List<Vertex[]> contours = new ArrayList<Vertex[]>();
		contours.add(vertices);
		this.setOutlineContours(contours);
		
		GluTrianglulator triangulator = new GluTrianglulator(getRenderer());
		Vertex[] tris = triangulator.tesselate(vertices);
		triangulator.deleteTess();
		
		super.setVertices(tris);
	}

//	 /*
	//CSS Stuff
    /** The mt app. */
	private AbstractMTApplication mtApp;
    
    /** The css styled. */
    private boolean cssStyled = false;
    
    /** The css helper. */
    private CSSHelper cssHelper;
    
    /* (non-Javadoc)
     * @see org.mt4j.components.css.util.CSSStylableComponent#isCSSStyled()
     */
    public boolean isCSSStyled() {
            return cssStyled;
    }

    /* (non-Javadoc)
     * @see org.mt4j.components.css.util.CSSStylableComponent#enableCSS()
     */
    public void enableCSS() {
    	if (mtApp != null && cssHelper != null) {
    		cssStyled = true;
    	}
    	applyStyleSheet();
    }

    /* (non-Javadoc)
     * @see org.mt4j.components.css.util.CSSStylableComponent#disableCSS()
     */
    public void disableCSS() {
            cssStyled = false;
            
    }

    /* (non-Javadoc)
     * @see org.mt4j.components.css.util.CSSStylableComponent#applyStyleSheet()
     */
    public void applyStyleSheet() {
    	//Custom behaviour goes here..

    	if (cssStyled && mtApp != null && cssHelper != null) {
    		cssHelper.applyStyleSheet(this);
    	}

    }
    
    /** The css force disabled. */
    private boolean cssForceDisabled = false;
    
    /* (non-Javadoc)
     * @see org.mt4j.components.css.util.CSSStylableComponent#isCssForceDisabled()
     */
    public boolean isCssForceDisabled() {
            return cssForceDisabled;
    }

    /* (non-Javadoc)
     * @see org.mt4j.components.css.util.CSSStylableComponent#setCssForceDisable(boolean)
     */
    public void setCssForceDisable(boolean cssForceDisabled) {
            this.cssForceDisabled = cssForceDisabled;
            
    }

    /* (non-Javadoc)
     * @see org.mt4j.components.css.util.CSSStylableComponent#getCssHelper()
     */
    public CSSHelper getCssHelper() {
            return this.cssHelper;
    }
//    */

}
