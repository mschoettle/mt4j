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
package org.mt4j.components.visibleComponents.shapes;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.bounds.OrientedBoundingBox;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.ILassoable;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.animation.Animation;
import org.mt4j.util.animation.AnimationEvent;
import org.mt4j.util.animation.AnimationManager;
import org.mt4j.util.animation.IAnimation;
import org.mt4j.util.animation.IAnimationListener;
import org.mt4j.util.animation.MultiPurposeInterpolator;
import org.mt4j.util.animation.ani.AniAnimation;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GL10;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.EXPANSION_FILTER;
import org.mt4j.util.opengl.GLTexture.SHRINKAGE_FILTER;
import org.mt4j.util.opengl.GLTexture.TEXTURE_TARGET;
import org.mt4j.util.opengl.GLTexture.WRAP_MODE;
import org.mt4j.util.opengl.GLTextureSettings;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * Abstract superclass for all kinds of shapes defined by vertices.
 * 
 * @author Christopher Ruff
 */
public abstract class AbstractShape extends AbstractVisibleComponent implements ILassoable{
	private static final ILogger logger = MTLoggerFactory.getLogger(AbstractShape.class.getName());
	static{
		logger.setLevel(ILogger.ERROR);
	}
	
	//Texture Stuff
	/** The texture enabled. */
	private boolean textureEnabled; 
	
	/** The texture mode. */
	private int textureMode; // set defaults!
	
	/** The texture image. */
	private PImage textureImage;

	/** The draw direct gl. */
	private boolean drawDirectGL;
	
	/** The use vb os. */
	private boolean useVBOs;
	
	/** The use display list. */
	private boolean useDisplayList;
	
	/** The geometry of this shape. */
	private GeometryInfo geometryInfo;
	
	/** The vertices global. */
	private Vertex[] verticesGlobal;
	
	/** global vertices dirty. */
	private boolean globalVerticesDirty;
	
	//save the set texture dimensions so we can always scale from one NPOT texture to another NPOT texture coords
	//(even if texture was set to null in between)
	private Vector3D lastTextureDimension = new Vector3D(); 
	
	
	/**
	 * Instantiates a new abstract shape.
	 *
	 * @param vertices the vertices
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter. 
	 */
	public AbstractShape(Vertex[] vertices, PApplet pApplet) {
		this(pApplet, vertices);
	}
	
	/**
	 * Creates a new shape with the vertices provided.
	 * @param pApplet the applet
	 * @param vertices the vertices
	 */
	public AbstractShape(PApplet pApplet, Vertex[] vertices) {
		this(pApplet, new GeometryInfo(pApplet, vertices));
	}
	
	
	/**
	 * Instantiates a new abstract shape.
	 *
	 * @param geometryInfo the geometry info
	 * @param pApplet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public AbstractShape(GeometryInfo geometryInfo, PApplet pApplet) {
		this(pApplet, geometryInfo);
	}
	
	/**
	 * Creates a new geometry with the geometryInfo provided.
	 * @param pApplet the applet
	 * @param geometryInfo the geometry info
	 */
	public AbstractShape(PApplet pApplet, GeometryInfo geometryInfo) {
		super(pApplet,"unnamed  AbstractShape", /*null,*/ null);
		
		//Initialize fields 
		this.drawDirectGL = MT4jSettings.getInstance().isOpenGlMode();
		this.useVBOs 			= false;
		this.useDisplayList 	= false;
		this.textureMode = PConstants.NORMAL;
		this.setFillDrawMode(GL10.GL_TRIANGLE_FAN);
//		this.boundsGlobalVerticesDirty = true;
		this.boundsAutoCompute = true;
		
		this.setGeometryInfo(geometryInfo);
		
		//Default
		this.boundsBehaviour = BOUNDS_CHECK_THEN_GEOMETRY_CHECK;
		this.globalVerticesDirty = true;//
		
		this.setDefaultGestureActions();
		
		this.lassoed = false;
	}
	
	/*
	//FIXME TODO switch drawBounds! put draw() into IBoundingShape!
	@Override
	public void postDraw(PGraphics g) {
		super.postDraw(g);
		
		if (this.getBounds() instanceof OrientedBoundingBox){
			OrientedBoundingBox b = (OrientedBoundingBox)this.getBounds();
			b.drawBounds(g);
		}
		else if (this.getBounds() instanceof BoundsZPlaneRectangle){
			BoundsZPlaneRectangle b = (BoundsZPlaneRectangle)this.getBounds();
			b.drawBounds(g);
		}
		else if (this.getBounds() instanceof BoundingSphere){
			BoundingSphere b = (BoundingSphere)this.getBounds();
			b.drawBounds(g);
		}
		
	}
	*/
	
	/*
	//Test for drawing bounding shape aligned to coordinate axis, like getWidth/getHeightRelativeToParent would return
	@Override
	public void postDrawChildren(PGraphics g) {
		super.postDrawChildren(g);
		
		if (this.getBounds() instanceof BoundsZPlaneRectangle){
			BoundsZPlaneRectangle b = (BoundsZPlaneRectangle)this.getBounds();
//			b.drawBounds(g);
			g.pushMatrix();
			g.pushStyle();
			g.fill(250,150,150,180);
			
			Vector3D[] v = b.getVectorsGlobal();
			float[] minMax = ToolsGeometry.getMinXYMaxXY(v);
			
			g.beginShape();
			g.vertex(minMax[0], minMax[1], 0);
			g.vertex(minMax[2], minMax[1], 0);
			g.vertex(minMax[2], minMax[3], 0);
			g.vertex(minMax[0], minMax[3], 0);
			g.endShape();
//			
			g.popStyle();
			g.popMatrix();
		}
	}
	*/
	
	
	public static boolean createDefaultGestures = true;
	
	/**
	 * Assigns the default gesture to this component, drag, rotate, scale.
	 * <br>Gets called in the constructor.
	 * Can be overridden in subclasses to allow other/more default gestures.
	 */
	protected void setDefaultGestureActions(){
//		/*
		if (createDefaultGestures){
			this.registerInputProcessor(new RotateProcessor(this.getRenderer()));
			this.setGestureAllowance(RotateProcessor.class, true);
			this.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
			
			this.registerInputProcessor(new ScaleProcessor(this.getRenderer()));
			this.setGestureAllowance(ScaleProcessor.class, true);
			this.addGestureListener(ScaleProcessor.class, new DefaultScaleAction());
			
			this.registerInputProcessor(new DragProcessor(this.getRenderer()));
			this.setGestureAllowance(DragProcessor.class, true);
			this.addGestureListener(DragProcessor.class, new DefaultDragAction());
		}
//		*/
	}
	
//////////////// BOUNDING STUFF ///////////////////////////////
	/** The Constant BOUNDS_ONLY_CHECK. */
	public static final int BOUNDS_ONLY_CHECK 					= 1;
	
	/** The Constant BOUNDS_CHECK_THEN_GEOMETRY_CHECK. */
	public static final int BOUNDS_CHECK_THEN_GEOMETRY_CHECK 	= 2;
	
	/** The Constant BOUNDS_DONT_USE. */
	public static final int BOUNDS_DONT_USE						= 3;
	
	//FIXME RENAME, KEEP OLD ONES BUT AS DPERECATED
//	BOUNDSBEHAVIOUR_USE_GEOMETRY
//	BOUNDSBEHAVIOUR_USE_BOUNDS_AND_GEOMETRY
//	BOUNDSBEHAVIOUR_USE_BOUNDS
	
	/** The bounds picking behaviour. */
	private int boundsBehaviour;
	
	/** The bounds auto compute. */
	private boolean boundsAutoCompute;
	
	
	/**
	 * Sets the bounds behaviour. The behaviour influences 
	 * calculations in methods like <code>getIntersectionLocal</code> (used in picking) and
	 * <code>getComponentContainsPointLocal</code>.
	 * Allowed values are:
	 * <ul>
	 * <li><code>AbstractShape.BOUNDS_ONLY_CHECK</code>  <br>
	 * -> Uses the shape's bounding shape for the calculations <br>
	 * => faster, more inaccurate
	 * <li><code>AbstractShape.BOUNDS_DONT_USE</code>  <br>
	 *  -> Uses the shape's geometry for the calculations <br>
	 *  => slower, more accurate
	 * <li><code>AbstractShape.BOUNDS_CHECK_THEN_GEOMETRY_CHECK</code>   <br>
	 * -> Uses the shape's bounding shape first, and then also checks the geometry at picking. (Default)<br>
	 * => compromise between the other two 
	 * </ul>
	 * 
	 * @param boundsBehaviour the new bounds behaviour
	 */
	public void setBoundsBehaviour(int boundsBehaviour){
		this.boundsBehaviour = boundsBehaviour;
	}
	
	/**
	 * Gets the bounds behaviour.
	 * 
	 * @return the bounds behaviour constant
	 */
	private int getBoundsBehaviour(){
		return this.boundsBehaviour;
	}
	
	
	@Override
	public void setMatricesDirty(boolean baseMatrixDirty) {
		/* 
		 * Overridden, so the component is also informed of the need to update
		 * the bounds vertices
		 */
		if (baseMatrixDirty){
			this.globalVerticesDirty	= true;
		}
//		System.out.println("Set baseMatrixDirty dirty on obj: " + this.getName());
		super.setMatricesDirty(baseMatrixDirty);
	}
	
	/**
	 * Sets the bounds auto compute.
	 * 
	 * @param autoCompute the new bounds auto compute
	 */
	public void setBoundsAutoCompute(boolean autoCompute){
		this.boundsAutoCompute = autoCompute;
	}
	
	/**
	 * Checks if is bounds auto compute.
	 * 
	 * @return true, if is bounds auto compute
	 */
	public boolean isBoundsAutoCompute(){
		return this.boundsAutoCompute;
	}
	
	/**
	 * Computes a default bounding box for the shape.
	 * This gets called after setting creating a shape and its setGeometryInfo method is called.
	 */
	protected IBoundingShape computeDefaultBounds(){
		return new OrientedBoundingBox(this);
	}

////////////////BOUNDING STUFF ///////////////////////////////
	
	
	/**
	 * Sets a new geometryInfo with new vertices for this shape.
	 * <br>If running in OpenGL mode, this also creates new vertex buffers 
	 * for openGL use and eventually new Vertex Buffer Objects or 
	 * Displaylists depending on the objects settings! 
	 * So DONT create them (buffers or vbos) on the geometryinfo yourself manually, 
	 * prior to setting it here!
	 * <br>Also calls computeDefaultBounds() if setAutoComputeBounds() is true (default)
	 * to recreate the bounding shape.
	 * <br><strong>NOTE:</strong> Be aware, that an old geometryinfo of this shape may have 
	 * created VBOs or displaylists on the gfx card which we should delete if not needed
	 * anywhere else!
	 * 
	 * @param geometryInfo the geometry info
	 */
	public void setGeometryInfo(GeometryInfo geometryInfo){
		if (this.isUseDirectGL()){
			if (geometryInfo.getVertBuff() == null 	|| geometryInfo.getStrokeColBuff() == null){ 
				//new geometryinfo has no drawbuffers created yet -> create them!
				geometryInfo.generateOrUpdateBuffersLocal(this.getStyleInfo());
				if (this.isUseVBOs()){
					geometryInfo.generateOrUpdateAllVBOs();
				}
				if (this.isUseDisplayList()){
					this.getGeometryInfo().generateDisplayLists(this, true, true);
				}
			}else if (this.geometryInfo != null && geometryInfo.equals(this.geometryInfo)){
				// old geometryinfo is the same than the new one -> assumimg change -> create new buffers!
				geometryInfo.generateOrUpdateBuffersLocal(this.getStyleInfo());
				if (this.isUseVBOs()){
					geometryInfo.generateOrUpdateAllVBOs();
				}
				if (this.isUseDisplayList()){
					this.getGeometryInfo().generateDisplayLists(this, true, true);
				}
			}else{
				//the new geometryinfo already has opengl draw buffers and 
				//the old geometryinfo is null or not the same as the new one 
				//-> just use the new geometry's data without recreating!
				//=> geometry instancing!
			}
		}
		
		this.geometryInfo = geometryInfo;
		
		if (this.isBoundsAutoCompute()){
			if (geometryInfo.getVertices().length >= 3){
				this.setBounds(this.computeDefaultBounds());
			}else{
//				logger.error("Warning: could not compute bounds because too few vertices were supplied: " + this.getName() + " in " + this + " -> Setting boundingShape to null.");
				this.setBounds(null);
			}
		}else{
			this.setBounds(null);
		}
		this.globalVerticesDirty = true;
	}
	
	abstract protected void drawPureGl(GL10 gl);
	
	/**
	 * Gets the geometry info. The geometryinfo contains the 
	 * geometric information of this shape by managing the shapes
	 * vertices, OpenGL vertex buffer objects and OpenGL display list.  
	 * 
	 * @return the geometry info
	 * 
	 * the geometry information object of that shape
	 */
	public GeometryInfo getGeometryInfo() {
		return this.geometryInfo;
	}

	
	/**
	 * Sets new vertices for that shape.
	 * and generates new vertex arrays for opengl mode.
	 * <li>Re-computes and sets the shapes default bounding shape.
	 * 
	 * @param vertices the vertices
	 */
	public void setVertices(Vertex[] vertices){
		this.getGeometryInfo().reconstruct(
				vertices,
				this.getGeometryInfo().getNormals(), 
				this.getGeometryInfo().getIndices(), 
				this.isUseDirectGL(), 
				this.isUseVBOs(), 
				this.getStyleInfo());
		
		if (this.isBoundsAutoCompute()){
			if (geometryInfo.getVertices().length >= 3){
				this.setBounds(this.computeDefaultBounds());
			}else{
//				logger.error("Warning: could not compute bounds because too few vertices were supplied: " + this.getName() + " in " + this + " -> Setting boundingShape to null.");
				this.setBounds(null);
			}
		}else{
			this.setBounds(null);
		}
		//Sets the base matrix dirty, so that when inquiring info about
		//vertices, they get updated first
		this.globalVerticesDirty = true;
	}
	
	/**
	 * Returns the vertices of this shape without any transformations applied
	 * <br> <b>Caution:</b> If you alter them in anyway, changes will only
	 * be consistent by calling the <code>setVertices(Vertex[])</code> method with the changes vertices
	 * as an argument!.
	 * 
	 * @return the untransformed vertices
	 */
	public Vertex[] getVerticesLocal(){
		return this.getGeometryInfo().getVertices();
	}
	
	/**
	 * Returns the vertices of this shape in real world (global) coordinates
	 * <br> <b>Caution:</b> If you alter them in anyway, changes will only
	 * be consistent if you call the setVertices() method of the shape.
	 * <br><b>Caution:</b>This operation is not cheap since all vertices are 
	 * first copied and then transformed!
	 * <br><b>Note:</b> if a shape as a lot of vertices this will increase memory usage considerably
	 * because a complete copy of the shapes vertices is made and kept!
	 * @return the vertices in global coordinate space
	 */
	public Vertex[] getVerticesGlobal(){
		this.updateVerticesGlobal();
		return this.verticesGlobal;
	}

	/**
	 * Updates the verticesglobal array of the shape by
	 * multipying them with the current shape's global matrix.<br>
	 * <br>This calculates the real world space coordinates and saves it
	 * in the verticesglobal array. These vertices can be used to test at picking
	 * or just to know the real world global coordinates of the vertices.
	 */
	private void updateVerticesGlobal(){
		if (this.globalVerticesDirty){ 
			Vertex[] unTransformedCopy = Vertex.getDeepVertexArrayCopy(this.getGeometryInfo().getVertices());
			//transform the copied vertices and save them in the vertices array
			this.verticesGlobal = Vertex.transFormArray(this.getGlobalMatrix(), unTransformedCopy);
			this.globalVerticesDirty = false;
		}
	}
	
	
	/**
	 * Gets the vertex count.
	 * 
	 * @return the vertex count
	 * 
	 * the number of vertices for that shape
	 */
	public int getVertexCount(){
		return this.getGeometryInfo().getVertexCount();
	}

	/**
	 * Checks if is use direct gl.
	 * 
	 * @return true, if checks if is use direct gl
	 * 
	 * true, if the shape tries to draw itself with OpenGL commands
	 * rather than processing commands
	 */
	public boolean isUseDirectGL() {
		return this.drawDirectGL;
	}

	/**
	 * If set to true - which is the default if using the OpenGL render mode - 
	 * this shape will bypass processings rendering pipeline
	 * and use the OpenGL context directly for performance increases.<br>
	 * Setting this to false forces the use of the processing renderer.
	 * <p>
	 * If this is set to true, and additionally, setUseVBOs() is set to true, 
	 * the shape is drawn by using vertex buffer objects (VBO). <br>
	 * By calling setUseDisplayList(true) it is drawn using display lists.
	 *  
	 * @param drawPureGL the draw pure gl
	 */
	public void setUseDirectGL(boolean drawPureGL){
		if (MT4jSettings.getInstance().isOpenGlMode()){
			if (!this.isUseDirectGL()
				&&	drawPureGL 
				&& this.getGeometryInfo().getVertices() != null 
				&& this.getGeometryInfo().getVertexCount() > 0){
				//Generate buffers for opengl array use
				this.getGeometryInfo().generateOrUpdateBuffersLocal(this.getStyleInfo());
			}
			
			this.drawDirectGL = drawPureGL; 
			//Wrap the current texture into a gl texture object for openGl use
			if 	(this.drawDirectGL
				&& this.getTexture() != null){
				this.setTexture(this.getTexture());
			}
		}else{
			logger.error(this.getName() + " - Cant use direct GL mode if not in opengl mode! Object: " + this);
			this.drawDirectGL = false;
		}
	}
	
	/**
	 * Checks if this shape is drawn using VBOs.
	 * 
	 * @return true, if checks if is use vbos
	 * 
	 * true, if the shape tries to draw itself with OpenGL Vertex Buffer Objects
	 */
	public boolean isUseVBOs() {
		return this.useVBOs;
	}
	
	/**
	 * <br>Tries to use Vertex Buffer Objects for displaying this shape.<br>
	 * You have to be in OpenGL mode and set <code>setDrawDirectGL(true)</code>first.
	 * 
	 * @param useVBOs the use vb os
	 */
	public void setUseVBOs(boolean useVBOs) {
		if (MT4jSettings.getInstance().isOpenGlMode() && this.isUseDirectGL()){ 
			if (!this.isUseVBOs()){
				this.getGeometryInfo().generateOrUpdateAllVBOs();
			}
			
			//If we want to enable VBOs check if OpenGL 2.0 is supported
			if (!useVBOs){
				this.useVBOs = useVBOs;
			}else{
				if (this.getRenderer() instanceof AbstractMTApplication && ((AbstractMTApplication) this.getRenderer()).isGL11Available()) {
					this.useVBOs = useVBOs;
				}
			}
		}else{
			logger.error(this.getName() + " - Cant use VBOs if not in opengl mode and setDrawDirectGL has to be set to true! Object: " + this);
			this.useVBOs = false;
		}
	}
	

	/**
	 * Checks if is use display list.
	 * 
	 * @return true, if checks if is use display list
	 * 
	 * true, if the shape tries to draw itself with OpenGL display lists
	 */
	public boolean isUseDisplayList() {
		return this.useDisplayList;
	}
	
	/**
	 * Tries to use a opengl display list for rendering this shape.<br>
	 * You have to be in OpenGL mode and <code>setDrawDirectGL()</code> has to
	 * be set to "true" first!
	 * <br><strong>NOTE: </strong> the display list has to be created first
	 * to use it! This can be done by calling <code>generateDisplayLists</code>.
	 * Instead of these 2 steps we can also just call <code>generateAndUseDisplayLists()</code>
	 * <br><strong>NOTE: </strong> if the shape was using a display list before we should delete it before setting
	 * a new one!
	 * 
	 * @param useDisplayList the use display list
	 */
	public void setUseDisplayList(boolean useDisplayList) {
			if (MT4jSettings.getInstance().isOpenGlMode() && this.isUseDirectGL()){
				this.useDisplayList = useDisplayList;
				if (this.getGeometryInfo().getDisplayListIDs()[0] == -1 
					&& this.getGeometryInfo().getDisplayListIDs()[1] == -1	
					&& useDisplayList
				){
					logger.warn(this.getName() + " - Warning, no displaylists created yet on component: " + this);
				}
			}else{
				if (useDisplayList)
					logger.warn(this.getName() + " - Cant set display lists if not in opengl mode and setDrawDirectGL has to be set to true! Object: " + this);
				this.useDisplayList = false;
			}
	}
	
	
	/**
	 * Generates 2 openGL display lists for drawing this shape.
	 * <br>One for the interior (with textures etc.) and
	 * one for drawing the outline.
	 * <br><code>setUseDirectGL</code> has to be set to true first!
	 * <br>To use the display lists for drawing, call <code>setUseDisplayList()</code>
	 * This method only generates them!
	 * <br><strong>NOTE: </strong> if the shape was using a display list before we should delete it before setting
	 * a new one!
	 */
	public void generateDisplayLists(){
		if (MT4jSettings.getInstance().isOpenGlMode() && this.isUseDirectGL()){
//			this.getGeometryInfo().generateDisplayLists(
//					this.isTextureEnabled(), 
//					this.getTexture(), 
//					this.getFillDrawMode(), 
//					this.isDrawSmooth(), 
//					this.getStrokeWeight());
			this.getGeometryInfo().generateDisplayLists(this, true, true);
		}else{
			logger.error(this.getName() + " - Cannot create displaylist if not in openGL mode or if setUseDirectGL() hasnt been set to true!");
		}
	}

	/**
	 * Generates and uses openGL display lists for drawing this
	 * shape.
	 */
	public void generateAndUseDisplayLists(){
		this.generateDisplayLists();
		this.setUseDisplayList(true);
	}
	
	/**
	 * Deletes the displaylists of the object and sets
	 * setUseDisplayList() to false.
	 */
	public void disableAndDeleteDisplayLists(){
		this.getGeometryInfo().deleteDisplayLists();
		this.setUseDisplayList(false);
	}
	
	
	@Override
	public void setFillColor(MTColor color) {
		super.setFillColor(color);
		this.getGeometryInfo().setVerticesColorAll(color.getR(), color.getG(), color.getB(), color.getAlpha());
	}

	
	@Override
	public void setStrokeColor(MTColor strokeColor) {
		super.setStrokeColor(strokeColor);
		if (MT4jSettings.getInstance().isOpenGlMode() && this.isUseDirectGL())  
			this.getGeometryInfo().setStrokeColorAll(strokeColor.getR(), strokeColor.getG(), strokeColor.getB(), strokeColor.getAlpha());
	}
	
	
	/**
	 * Tells the shape to use its texture.
	 * A texture has to be set previously!
	 * 
	 * @param texture the texture
	 */
	public void setTextureEnabled(boolean texture){
		this.textureEnabled = texture;
	}
	
	/**
	 * Checks if is texture enabled.
	 * 
	 * @return true, if checks if is texture enabled
	 * 
	 * true, if the shape is to use a texture
	 */
	public boolean isTextureEnabled(){
		return this.textureEnabled;
	}
	
	
	/**
	 * Sets a texture for this shape.
	 * <br>Uses the texture coordinates in the provided vertices for drawing.
	 * <br>If openGL mode is used, it also creates a GLTexture object.
	 * <br>For best compatibility, power of two texture dimensions should be provided.
	 * If the provided texture is non power of two and you are in opengl mode, we try
	 * to use the RECTANGULAR texture extension.
	 * <br>If textures were disabled for this component, they are being enabled again.
	 * 
	 * @param newTexImage the new tex image
	 */
	public void setTexture(PImage newTexImage){
		if (newTexImage == null){
			this.textureImage = null;
			this.setTextureEnabled(false);
//			System.out.println("Set texture to null");
			return;
		}

		//TODO AbstractShape.updateGLTextureCoordinates(); ?
		//-> updateTextureBuffer still needed if custom tex coords wanted 
		//-> use before setTexture()!
		
		//TODO make sure that NORMAL texture coords are supplied and BEFORE setting the texture!
		
		//TODO Note that if we want to change the tex coords mannually, do it normalized, then for precaution update the buffer and then set the texture
		//if the tex coords have to be un/normalized the updating is done twice but else we might miss updating it when we update from POT to POT.. 
		//maybe make method updateTextureCoords() - maybe also call setTexture to un/normalize()
		if (!this.isTextureEnabled())
			this.setTextureEnabled(true);
		
		if (lastTextureDimension.equalsVector(Vector3D.ZERO_VECTOR)){
			lastTextureDimension.setXYZ(newTexImage.width, newTexImage.height, 0);
		}
		
		if (this.isUseDirectGL()){
			if (newTexImage instanceof GLTexture) {
				GLTexture glTex = (GLTexture) newTexImage;
				
				if (glTex.getTextureTargetEnum() == TEXTURE_TARGET.RECTANGULAR){
					this.setTextureMode(PConstants.IMAGE);
					
					if (this.getGeometryInfo().isTextureCoordsNormalized()){
						//0..1 -> 0..width
						this.unNormalizeFromPOTtoRectMode(newTexImage, this.getVerticesLocal());
						this.getGeometryInfo().setTextureCoordsNormalized(false);
					}else{
						//0..oldWidth -> 0..newWidth  GLTexture is NPOT but this component's texture coords have seemingly already been un-normalized
						//FIXME dont do it if it has the same dimensions!
						this.fromRectModeToRectMode(newTexImage, this.getVerticesLocal(), this.lastTextureDimension.x, this.lastTextureDimension.y);
					}
				}else{
					//GLTexture is POT -> normalize tex coords if neccessary
					this.setTextureMode(PConstants.NORMAL);
					
					if (this.getGeometryInfo().isTextureCoordsNormalized()){
						//0..1 -> 0..1
					}else{
						//0..width -> 0..1
						this.normalizeFromRectMode(newTexImage, this.getVerticesLocal(), this.lastTextureDimension.x, this.lastTextureDimension.y);
						this.getGeometryInfo().setTextureCoordsNormalized(true);
					}
				}
				this.textureImage = newTexImage;
				//save last tex dimensions? also if POT?
				this.lastTextureDimension.setXYZ(newTexImage.width, newTexImage.height, 0);
			}else{
				//We are in OpenGL mode but the new texture is not a GLTexture -> create new GLTexture from PImage
//				boolean isPOT = Tools3D.isPowerOfTwoDimension(newTexImage);
				GLTextureSettings ts = new GLTextureSettings();
				//Create new GLTexture from PImage
				ts.shrinkFilter 		= SHRINKAGE_FILTER.BilinearNoMipMaps;
				ts.expansionFilter 		= EXPANSION_FILTER.Bilinear;
				ts.wrappingHorizontal 	= WRAP_MODE.CLAMP_TO_EDGE;
				ts.wrappingVertical 	= WRAP_MODE.CLAMP_TO_EDGE;
				GLTexture newGLTexture = new GLTexture(this.getRenderer(), newTexImage, ts);
				
				this.textureImage = newGLTexture;
				
				if (newGLTexture.getTextureTargetEnum() == TEXTURE_TARGET.RECTANGULAR){
					this.setTextureMode(PConstants.IMAGE);
					
					if (this.getGeometryInfo().isTextureCoordsNormalized()){
						//0..1 -> 0..newWidth
						this.unNormalizeFromPOTtoRectMode(newTexImage, this.getVerticesLocal());
						this.getGeometryInfo().setTextureCoordsNormalized(false);
					}else{
						//0..oldWidth -> 0..newWidth
						//FIXME dont do it if it has the same dimensions!
						this.fromRectModeToRectMode(newTexImage, this.getVerticesLocal(), this.lastTextureDimension.x, this.lastTextureDimension.y);
					}
				}else{
					this.setTextureMode(PConstants.NORMAL);
					
					//We are in OpenGL mode, new texture is a PImage, is POT -> create POT GLTexture and un-normalize tex coords if neccessary
					if (this.getGeometryInfo().isTextureCoordsNormalized()){
						//0..1 -> 0..1
					}else{
						//normalize 0..width -> 0..1
						this.normalizeFromRectMode(newTexImage, this.getVerticesLocal(), this.lastTextureDimension.x, this.lastTextureDimension.y);
						this.getGeometryInfo().setTextureCoordsNormalized(true);
					}
				}
				
				this.lastTextureDimension.setXYZ(newTexImage.width, newTexImage.height, 0);
			}
		}else{
			//We dont use OpenGL -> just set the PImage texture
			this.textureImage = newTexImage;		
			this.lastTextureDimension.setXYZ(newTexImage.width, newTexImage.height, 0);
		}
	}
	
	
	
	private void unNormalizeFromPOTtoRectMode(PImage newTexture, Vertex[] verts){
        for (Vertex vertex : verts) {
            vertex.setTexCoordU(vertex.getTexCoordU() * (float) newTexture.width);
            vertex.setTexCoordV(vertex.getTexCoordV() * (float) newTexture.height);
//    		System.out.println("TexU:" + vertex.getTexCoordU() + " TexV:" + vertex.getTexCoordV()); //FIXME REMOVE
        }
		this.getGeometryInfo().updateTextureBuffer(this.isUseVBOs());
	}
	
	private void normalizeFromRectMode(PImage newTexture, Vertex[] verts, float oldTexWidth, float oldTexHeight){
        for (Vertex vertex : verts) {
            //    		vertex.setTexCoordU(ToolsMath.map(vertex.getTexCoordU(), 0, oldTexWidth, 0, 1));
//    		vertex.setTexCoordV(ToolsMath.map(vertex.getTexCoordV(), 0, oldTexWidth, 0, 1));
            vertex.setTexCoordU(vertex.getTexCoordU() / oldTexWidth);
            vertex.setTexCoordV(vertex.getTexCoordV() / oldTexHeight);
        }
		this.getGeometryInfo().updateTextureBuffer(this.isUseVBOs());
	}
	
	private void fromRectModeToRectMode(PImage newTexture, Vertex[] verts, float oldTexWidth, float oldTexHeight){
        for (Vertex vertex : verts) {
            vertex.setTexCoordU((vertex.getTexCoordU() / oldTexWidth) * (float) newTexture.width);
            vertex.setTexCoordV((vertex.getTexCoordV() / oldTexHeight) * (float) newTexture.height);
        }
		this.getGeometryInfo().updateTextureBuffer(this.isUseVBOs());
	}
	
	
	/**
	 * Gets the texture.
	 * 
	 * @return the texture
	 * 
	 * the texture object associated with this shape (either a PImage or GLTexture obj)
	 */
	public PImage getTexture() {
		return this.textureImage;
	}

	/**
	 * Sets the way texture coordinates are handled in processing. This setting
	 * is not considered if using OpenGL mode!
	 * Allowed values are: <code>PApplet.NORMAL</code> and <code>PApplet.IMAGE</code>
	 * <br>Default is <code>PApplet.NORMAL</code>.
	 * Which indicates that the texture coordinates should be in normalized
	 * range from 0.0 to 1.0!
	 * In image mode they have to range from 0..imageDimensions.
	 * 
	 * @param textureMode the texture mode
	 */
	public void setTextureMode(int textureMode){
		this.textureMode = textureMode;
	}
	
	/**
	 * Gets the processing texture mode.
	 * 
	 * @return the texture mode
	 */
	public int getTextureMode(){
		return this.textureMode;
	}
	
	
	/**
	 * Sets the global position of the component. (In global coordinates)
	 * 
	 * @param pos the pos
	 */
	public void setPositionGlobal(Vector3D pos){
		this.translateGlobal(pos.getSubtracted(this.getCenterPointGlobal()));
	}
	
	/**
	 * Sets the position of the component, relative to its parent coordinate frame.
	 * 
	 * @param pos the pos
	 */
	public void setPositionRelativeToParent(Vector3D pos){
		this.translate(pos.getSubtracted(this.getCenterPointRelativeToParent()), TransformSpace.RELATIVE_TO_PARENT);
	}
	
	/**
	 * Sets the position of this component, relative to the other specified component.
	 * 
	 * @param otherComp the other comp
	 * @param pos the pos
	 */
	public void setPositionRelativeToOther(MTComponent otherComp, Vector3D pos){
		Matrix m0 = MTComponent.getTransformToDestinationLocalSpace(otherComp, this);
		pos.transform(m0);
		
		Vector3D centerpointGlobal = this.getCenterPointGlobal();
		centerpointGlobal.transform(this.getGlobalInverseMatrix()); //to localobj space
		centerpointGlobal.transform(this.getLocalMatrix()); //to parent relative space
		
		Vector3D diff = pos.getSubtracted(centerpointGlobal);
		this.translate(diff, TransformSpace.RELATIVE_TO_PARENT);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.AbstractVisibleComponent#getIntersectionLocal(org.mt4j.util.math.Ray)
	 */
	@Override
	public Vector3D getIntersectionLocal(Ray ray) {
		// be aware that we dont call the super implementation here..!
		
		switch (this.getBoundsBehaviour()) {
		case AbstractShape.BOUNDS_DONT_USE:
//			System.out.println("\"" + this.getName() + "\": -> GEOMETRY only check");
			return this.getGeometryIntersectionLocal(ray);
		case AbstractShape.BOUNDS_ONLY_CHECK:
			if (this.hasBounds()){
//				System.out.println("\"" + this.getName() + "\": -> BOUNDS only check");
				return this.getBounds().getIntersectionLocal(ray);
			}else{
//				System.out.println("\"" + this.getName() + "\": -> GEOMETRY only check");
				return this.getGeometryIntersectionLocal(ray);
			}
		case AbstractShape.BOUNDS_CHECK_THEN_GEOMETRY_CHECK:
			if (this.hasBounds()){
//				System.out.println("\"" + this.getName() + "\": -> BOUNDS check then GEOMETRY check");
				Vector3D boundsIntersection = this.getBounds().getIntersectionLocal(ray);
				if (boundsIntersection != null){
					return this.getGeometryIntersectionLocal(ray);
				}else{
					return null;
				}
			}else{
				return this.getGeometryIntersectionLocal(ray);
			}
		default:
			break;
		}
		return null;
	}
	
	
	@Override
	protected boolean componentContainsPointLocal(Vector3D testPoint) {
		switch (this.getBoundsBehaviour()) {
		case AbstractShape.BOUNDS_DONT_USE:
//			System.out.println("\"" + this.getName() + "\": -> GEOMETRY only check");
			return this.isGeometryContainsPointLocal(testPoint);
		case AbstractShape.BOUNDS_ONLY_CHECK:
			if (this.hasBounds()){
//				System.out.println("\"" + this.getName() + "\": -> BOUNDS only check");
				return this.getBounds().containsPointLocal(testPoint);
			}else{
//				System.out.println("\"" + this.getName() + "\": -> GEOMETRY only check");
				return this.isGeometryContainsPointLocal(testPoint);
			}
		case AbstractShape.BOUNDS_CHECK_THEN_GEOMETRY_CHECK:
			if (this.hasBounds()){
//				System.out.println("\"" + this.getName() + "\": -> BOUNDS check then GEOMETRY check");
				if (this.getBounds().containsPointLocal(testPoint)){
					return this.isGeometryContainsPointLocal(testPoint);
				}else{
					return false;
				}
			}else{
				return this.isGeometryContainsPointLocal(testPoint);
			}
		default:
			break;
		}
		return false;
	}
	
	
	/**
	 * Tests if the ray intersects the shape and where.
	 * The ray is assumed to be transformed to local space already!
	 * 
	 * @param ray the ray
	 * 
	 * @return the geometry intersection
	 * 
	 * the intersection point or null if no intersection occured
	 */
	abstract public Vector3D getGeometryIntersectionLocal(Ray ray);
	
	/**
	 * Tests is the geometry of the shape contains the given point.
	 * The testpoint is assumed to be transformed to local space already!
	 * 
	 * @param testPoint the test point
	 * 
	 * @return true, if checks if is geometry contains point
	 */
	abstract public boolean isGeometryContainsPointLocal(Vector3D testPoint);
	
	/**
	 * Gets the center point global.
	 * First it gets the local center and then transforms it to the global frame.
	 * 
	 * @return the center point global
	 * 
	 * the center of this shape in global coordinates
	 */
	public final Vector3D getCenterPointGlobal(){
		Vector3D center = this.getCenterPointLocal();
		center.transform(this.getGlobalMatrix());
		return center;
	}
	
	/**
	 * Gets the center point relative to parent.
	 * First it gets the local center and then transforms it to the parent frame.
	 * @return the center of this shape in coordinates relative to the shapes parent coordiante frame.
	 */
	public final Vector3D getCenterPointRelativeToParent(){
		Vector3D center = this.getCenterPointLocal();
		center.transform(this.getLocalMatrix());
		return center;
	}
	
	/**
	 * Gets the center point in local object space.
	 * This should always return a COPY of the centerpoint of the implementing shape
	 * since the point may get transformed afterwards.
	 * @return  the center point of this shape in untransformed local object coordinates.
	 */
	abstract public Vector3D getCenterPointLocal();

	
	/**
	 * Get the height of the shape in the XY-Plane. Uses the x and y coordinate
	 * values for calculation. Usually the calculation is delegated to the shapes
	 * bounding shape.
	 * 
	 * @param transformSpace the space the width is calculated in, can be world space, parent relative- or object space
	 * 
	 * @return the height xy
	 * 
	 * the height
	 */
	public float getHeightXY(TransformSpace transformSpace) {
		switch (transformSpace) {
		case LOCAL:
			return this.getHeightXYLocal();
		case RELATIVE_TO_PARENT:
			return this.getHeightXYRelativeToParent();
		case GLOBAL:
			return this.getHeightXYGlobal();
		default:
			return -1;
		}
	}
	
	
	/**
	 * Gets the height xy obj space.
	 * @return the height xy obj space
	 */
	private float getHeightXYLocal() {
		return this.getHeightXYVectLocal().length();
	}
	
	/**
	 * Gets the "height vector" and transforms it to parent relative space, then calculates
	 * its length.
	 * 
	 * @return the height xy relative to parent
	 * 
	 * the height relative to its parent space frame
	 */
	protected float getHeightXYRelativeToParent() {
		if (this.hasBounds()){
			return this.getBounds().getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
		}else{
			OrientedBoundingBox tempBounds = new OrientedBoundingBox(this);
			return tempBounds.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
		}
	}
	
	
	/**
	 * Gets the "height vector" and transforms it to world space, then calculates
	 * its length.
	 * 
	 * @return the height xy global
	 * 
	 * the height relative to the world space
	 */
	protected float getHeightXYGlobal() {
		if (this.hasBounds()){
			return this.getBounds().getHeightXY(TransformSpace.GLOBAL);
		}else{
			OrientedBoundingBox tempBounds = new OrientedBoundingBox(this);
			return tempBounds.getHeightXY(TransformSpace.GLOBAL);
		}
	}
	
	
	/**
	 * Gets the "height vector" from its boundingshape. If no boundingshape is set,
	 * a temporary bounding rectangle in the xy-plane is calculated and its height
	 * is calculated as a vector with the height as its length in object space.
	 * 
	 * @return the height xy vect obj space
	 * 
	 * vector representing the height of the boundingshape of the shape
	 * @deprecated this method should actually be private. Use getHeightXY(Transformspace.LOCAL) instead!
	 */
	public Vector3D getHeightXYVectLocal() {
		if (this.hasBounds()){
			return this.getBounds().getHeightXYVectLocal();
		}else{
			OrientedBoundingBox tempBounds = new OrientedBoundingBox(this);
			return tempBounds.getHeightXYVectLocal();
		}
	}

	
	/**
	 * Get the width of the shape in the XY-Plane. Uses the x and y coordinate
	 * values for calculation. Usually the calculation is delegated to the shapes
	 * bounding shape.
	 * 
	 * @param transformSpace the space the width is calculated in, can be global space, parent relative- or object space
	 * 
	 * @return the width xy
	 * 
	 * the width
	 */
	public float getWidthXY(TransformSpace transformSpace) {
		switch (transformSpace) {
		case LOCAL:
			return this.getWidthXYLocal();
		case RELATIVE_TO_PARENT:
			return this.getWidthXYRelativeToParent();
		case GLOBAL:
			return this.getWidthXYGlobal();
		default:
			return -1;
		}
	}
	
	
	/**
	 * Gets the width xy obj space.
	 * 
	 * @return the width xy obj space
	 */
	private float getWidthXYLocal() {
		return this.getWidthXYVectLocal().length();
	}
	
	
	/**
	 * Calculates the width of this shape, by using its
	 * bounding shape.
	 * Uses the objects local transform. So the width will be
	 * relative to the parent only - not the whole world
	 * 
	 * @return the width xy relative to parent
	 * 
	 * the width
	 */
	protected float getWidthXYRelativeToParent() {
		if (this.hasBounds()){
			return this.getBounds().getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
		}else{
			OrientedBoundingBox tempBounds = new OrientedBoundingBox(this);
			return tempBounds.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
		}
	}
	
	/**
	 * Gets the "Width vector" and transforms it to world space, then calculates
	 * its length.
	 * 
	 * @return the width xy global
	 * 
	 * the Width relative to the world space
	 */
	protected float getWidthXYGlobal() {
		if (this.hasBounds()){
			return this.getBounds().getWidthXY(TransformSpace.GLOBAL);
		}else{
			OrientedBoundingBox tempBounds = new OrientedBoundingBox(this);
			return tempBounds.getWidthXY(TransformSpace.GLOBAL);
		}
	}
	
	/**
	 * Gets the "width vector" from its boundingshape. If no boundingshape is set,
	 * a temporary bounding rectangle in the xy-plane is calculated and its width
	 * is calculated as a vector with the width as its length in object space.
	 * 
	 * @return the width xy vect obj space
	 * 
	 * vector representing the width of the boundingshape of the shape
	 * @deprecated this method should actually be private. Use getWidthXY(Transformspace.LOCAL) instead!
	 */
	public Vector3D getWidthXYVectLocal() {
		if (this.hasBounds()){
			return this.getBounds().getWidthXYVectLocal();
		}else{
			OrientedBoundingBox tempBounds = new OrientedBoundingBox(this);
			return tempBounds.getWidthXYVectLocal();
		}
	}
	
	
	/**
	 * <li>Removes this component from its parent.
	 * <li>Calls <code>destroyComponent</code> on this component which
	 * can be used to free resources that the component used.
	 * <li>Recursively calls destroy() on its children
	 * <br>
	 * <p>
	 * By default, the openGl texture object and the VBOs associated with this shape will be deleted.
	 * Be careful when you share textures across more than one object!
	 * Destroying of displaylists and VBOs isnt done atm! Use disableAndDeleteDisplaylists() instead.
	 */
	@Override
	public void destroy(){
//		System.out.println(this + " -> DESTROY() -> (AbstractShape)");
		
		/*
		//Deletion ins now done in GeometryInfo's finalize() method
		if (this.geometryInfo != null){
			//Delete VBOs
			this.getGeometryInfo().deleteAllVBOs();
		}
		this.destroyDisplayLists();
		*/
		
		this.setBounds(null);
		
		/*
		//Delete openGL texture object
		if (this.getTexture() instanceof GLTexture){
			GLTexture tex = (GLTexture) this.getTexture();
			//Delete texture
			tex.destroy();
			this.setTexture(null);
			this.setTextureEnabled(false);
		} 
		*/
		
		super.destroy();
	}
	
	
	/**
	 * This is called during the shape's destroy() method.
	 * Override this and leave it empty if you dont want the 
	 * display list destroyed in your component 
	 * (makes sense with shared geometry infos/display lists)
	 */
	protected void destroyDisplayLists(){
//		/*
		//Delete displaylist
		this.disableAndDeleteDisplayLists();
//		*/
	}
	
	@Override
	abstract protected void destroyComponent();
	
	
	
	
	/**
	 * Tween translate to.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param interpolationDuration the interpolation duration
	 * @param interpolationFunction the interpolation function
	 * @return the i animation
	 */
	public IAnimation tweenTranslateTo(float x, float y, float z, int interpolationDuration, String interpolationFunction, int delay){
		Vector3D from 			= this.getCenterPointGlobal();
		Vector3D targetPoint 	= new Vector3D(x, y, z);
		Vector3D directionVect 	= targetPoint.getSubtracted(from);
		float distance = directionVect.length();
		AniAnimation animation = new AniAnimation(0, distance, interpolationDuration, interpolationFunction, this);
		animation.addAnimationListener(new TranslationAnimationListener(this, directionVect, new Vector3D(x,y,z)));
		animation.setTriggerTime(delay);
		animation.start();
		return animation;
	}
	
	
	/**
	 * Tween translate.
	 *
	 * @param direction the direction
	 * @param interpolationDuration the interpolation duration
	 * @param interpolationFunction the interpolation function
	 * @param delay the delay
	 * @return the i animation
	 */
	public IAnimation tweenTranslate(Vector3D direction, int interpolationDuration, String interpolationFunction, int delay){
		float distance = direction.length();
		AniAnimation animation = new AniAnimation(0, distance, interpolationDuration, interpolationFunction, this);
		animation.addAnimationListener(new TranslationAnimationListener(this, direction));
		animation.setTriggerTime(delay);
		animation.start();
		return animation;
	}
	
	
	/**
	 * Moves this shape to the specified global position using an animation specified
	 * by the last three parameters.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param interpolationDuration the interpolation duration
	 * @param accelerationEndTime the acceleration end time - normalized value 0..1
	 * @param decelerationStartTime the deceleration start time - normalized value 0..1
	 * @return the animation
	 */
	public IAnimation tweenTranslateTo(float x, float y, float z, float interpolationDuration, float accelerationEndTime, float decelerationStartTime){
		Vector3D from 			= this.getCenterPointGlobal();
//		Vector3D from 			= this.getCenterPointRelativeToParent();
		Vector3D targetPoint 	= new Vector3D(x, y, z);
		Vector3D directionVect 	= targetPoint.getSubtracted(from);
		
		//GO through all animations for this shape
		IAnimation[] animations = AnimationManager.getInstance().getAnimationsForTarget(this);
		for (int i = 0; i < animations.length; i++) {
			IAnimation animation = animations[i];
			
			//Go through all listeners of these animations
			IAnimationListener[] animationListeners = animation.getAnimationListeners();
			for (int j = 0; j < animationListeners.length; j++) {
				IAnimationListener listener = animationListeners[j];
				//IF a listener is a TranslationAnimationListener the animations is a translationTween 
				//and should be stopped before doing this new animation
				if (listener instanceof TranslationAnimationListener)
					animation.stop();
			}
		}
		return this.tweenTranslate(directionVect, interpolationDuration, accelerationEndTime, decelerationStartTime);
	}
	
	/**
	 * Moves this shape in the specified direction with an animation specified by the other parameters.
	 * 
	 * @param directionVect the direction vect
	 * @param interpolationDuration the interpolation duration
	 * @param accelerationEndTime the acceleration end time - normalized value 0..1
	 * @param decelerationStartTime the deceleration start time - normalized value 0..1
	 * @return the animation
	 */
	public IAnimation tweenTranslate(Vector3D directionVect, float interpolationDuration, float accelerationEndTime, float decelerationStartTime){
		return this.tweenTranslate(directionVect, interpolationDuration, accelerationEndTime, decelerationStartTime, 0);
	}
	
	/**
	 * Tween translate.
	 * 
	 * @param directionVect the direction vect
	 * @param interpolationDuration the interpolation duration
	 * @param accelerationEndTime the acceleration end time - normalized value 0..1
	 * @param decelerationStartTime the deceleration start time - normalized value 0..1
	 * @param triggerDelay the trigger delay
	 * @return the animation
	 */
	public IAnimation tweenTranslate(Vector3D directionVect, float interpolationDuration, float accelerationEndTime, float decelerationStartTime, int triggerDelay){
		float distance = directionVect.length();
		IAnimation animation = null;
		
//		/*
		MultiPurposeInterpolator interpolator = new MultiPurposeInterpolator(0, distance, interpolationDuration , accelerationEndTime, decelerationStartTime , 1);
		animation = new Animation("Tween translate of " + this.getName(), interpolator, this, triggerDelay);
		animation.addAnimationListener(new TranslationAnimationListener(this, directionVect));
		((Animation)animation).setResetOnFinish(false);
//		*/
		
		/*
		animation = new AniAnimation(0, distance, Math.round(interpolationDuration), AniAnimation.QUAD_IN_OUT, this);
		animation.addAnimationListener(new TranslationAnimationListener(this, directionVect));
		((AniAnimation)animation).setTriggerTime(triggerDelay);
		*/
		
		animation.start();
		return animation;
	}
	
	/**
	 * This private class acts as an AnimationListener for translation animations.
	 * 
	 * @author C.Ruff
	 */
	private class TranslationAnimationListener implements IAnimationListener{
		/** The direction vector. */
		private Vector3D directionVector;
		
		/** The normalized dir vect. */
		private Vector3D normalizedDirVect;
		
		/** The shape. */
		private AbstractShape shape;
		
		private Vector3D destinationPos;
		
		/**
		 * Instantiates a new translation animation listener.
		 * 
		 * @param shape the shape
		 * @param directionVector the direction vector
		 * @param destinationPosition 
		 */
		public TranslationAnimationListener(AbstractShape shape, Vector3D directionVector){
			this(shape, directionVector, null);
		}

		/**
		 * Instantiates a new translation animation listener.
		 * 
		 * @param shape the shape
		 * @param directionVector the direction vector
		 * @param destinationPosition 
		 */
		public TranslationAnimationListener(AbstractShape shape, Vector3D directionVector, Vector3D destinationPosition){
			this.directionVector = directionVector;
			this.normalizedDirVect = this.directionVector.getCopy();
			this.normalizedDirVect.normalizeLocal();
			this.shape = shape;
			this.destinationPos = destinationPosition;
		}
		
		/* (non-Javadoc)
		 * @see util.animation.IAnimationListener#processAnimationEvent(util.animation.AnimationEvent)
		 */
		public void processAnimationEvent(AnimationEvent ae) {
			Object target = ae.getTarget();
			if (target != null && target.equals(this.shape)){
				AbstractShape shape = (AbstractShape)target;
				float amount = ae.getAnimation().getDelta();
				
				Vector3D newTranslationVect = this.normalizedDirVect.getCopy();
				newTranslationVect.scaleLocal(amount);
				//Move shape
//				shape.translateGlobal(newTranslationVect);
//				shape.translate(newTranslationVect);
				shape.translate(newTranslationVect, TransformSpace.GLOBAL);
				
				if (ae.getId() == AnimationEvent.ANIMATION_ENDED && destinationPos != null){
					shape.setPositionGlobal(destinationPos); //Set position at the end to fight round-off errors during translation
				}
			}
		}
	}
	
	private boolean lassoed;
	/**
	 * Sets this selected by the lasso processor.
	 * 
	 * @param selected the new selected
	 */
	public void setSelected(boolean selected){
		this.lassoed = selected;
	}
	
	/**
	 * Checks if is selected.
	 * 
	 * @return true, if is selected
	 */
	public boolean isSelected(){
		return this.lassoed;
	}

	
}


