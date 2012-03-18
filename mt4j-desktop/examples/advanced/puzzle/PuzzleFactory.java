package advanced.puzzle;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTComplexPolygon;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.xml.svg.SVGLoader;

import processing.core.PApplet;
import processing.core.PImage;

public class PuzzleFactory {
	//TODO
	/*
	 - show mini picture of original picture for orientation
	 - snap pieces together? - how to break them apart again?
	 - clusterable tiles? 
	 - if solved - set all tiles noStroke(true) to see image clearly 
	 */

	public enum TileSide{
		pinOut,
		pinIn,
		linear
	}
	
	private float tileHeight;
	private float tileWidth;
	private Vertex[] downUpOrderVerticalRightOut;
	private Vertex[] upDownOrderVerticalLeftOut;
	private Vertex[] downUpOrderVerticalLeftOut;
	private Vertex[] leftRightHorizontalUpOut;
	private Vertex[] rightLeftHorizontalUpOut;
	private Vertex[] rightLeftOrderHorizontalDownOut;
	private Vertex[] leftRightOrderHorizontalDownOut;
	private Vertex[] upDownOrderVerticalRightOut;
	private PImage image;
	private float horizontalTileCount;
	private PApplet app;
	private float verticalTileCount;
	public static String svgPath =  "advanced"+AbstractMTApplication.separator+"puzzle"+AbstractMTApplication.separator+"data"+AbstractMTApplication.separator ;
	public static String svgname = "knobOutRight.svg";
	
	public PuzzleFactory(AbstractMTApplication app) {
		this.app = app;
	}

//	public PuzzleFactory(PApplet app, float tileWidth, float tileHeight){
//
//	}
	
	private void init(float tileWidth, float tileHeight){
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		initTiles();
	}
	
	private void init(PImage p, int horizontalTileCount){
//		if (MT4jSettings.getInstance().isOpenGlMode() && !(p instanceof GLTexture)){
//			GLTexture tex = new GLTexture(app, p);
//			this.image = tex;
//		}else{
//			this.image = p;
//		}
//		
//		this.horizontalTileCount = horizontalTileCount;
//		this.verticalTileCount = horizontalTileCount; //TODO
//		this.tileWidth = (float)p.width/horizontalTileCount;
//		this.tileHeight = (float)p.height/verticalTileCount; 
//		initTiles();
		this.init(p, horizontalTileCount, horizontalTileCount);
	}
	
	private void init(PImage p, int horizontalTileCount, int verticalTileCount){
		if (MT4jSettings.getInstance().isOpenGlMode() && !(p instanceof GLTexture)){
			GLTexture tex = new GLTexture(app, p);
			this.image = tex;
		}else{
			this.image = p;
		}
		
		this.horizontalTileCount = horizontalTileCount;
		this.verticalTileCount = verticalTileCount; //TODO
		this.tileWidth = (float)p.width/(float)horizontalTileCount;
		this.tileHeight = (float)p.height/(float)verticalTileCount; 
		initTiles();
	}
	
	private void initTiles(){
		SVGLoader l = new SVGLoader(app);
		MTComponent knob = l.loadSvg(svgPath + svgname);
		MTPolygon knobRight = (MTPolygon) knob.getChildByIndex(0).getChildByIndex(0);
		knobRight.setNoFill(false);
		knobRight.setUseDisplayList(false);
		float origHeight = knobRight.getHeightXY(TransformSpace.LOCAL);
		
		//Snap to upper left 0,0
		Vertex[] originalVerts = knobRight.getVerticesLocal();
		originalVerts = Vertex.translateArray(originalVerts, Vector3D.ZERO_VECTOR.getSubtracted(new Vector3D(originalVerts[0])));
		
		upDownOrderVerticalRightOut = Vertex.getDeepVertexArrayCopy(originalVerts);
		//Scale to desired height
		Vertex.scaleVectorArray(upDownOrderVerticalRightOut, Vector3D.ZERO_VECTOR, (1f/origHeight) * tileHeight, (1f/origHeight) * tileHeight, 1);
				
		downUpOrderVerticalRightOut = getInvertOrderCopy(upDownOrderVerticalRightOut);
//		MTPolygon p1 = new MTPolygon(getMTApplication(), downUpOrderVerticalRightOut);
//		getCanvas().addChild(p1);
		
		upDownOrderVerticalLeftOut = Vertex.getDeepVertexArrayCopy(upDownOrderVerticalRightOut);
		Vertex.scaleVectorArray(upDownOrderVerticalLeftOut, new Vector3D(0,origHeight/2f), -1, 1, 1);
//		MTPolygon p2 = new MTPolygon(getMTApplication(), vertsVerticalLeftOut);
//		getCanvas().addChild(p2);
		
		downUpOrderVerticalLeftOut = getInvertOrderCopy(upDownOrderVerticalLeftOut);
		
		leftRightHorizontalUpOut = Vertex.getDeepVertexArrayCopy(originalVerts);
		Vertex.rotateZVectorArray(leftRightHorizontalUpOut, Vector3D.ZERO_VECTOR, -90);
		//Scale to desired width
		Vertex.scaleVectorArray(leftRightHorizontalUpOut, Vector3D.ZERO_VECTOR, (1f/origHeight) * tileWidth, (1f/origHeight) * tileWidth, 1);
//		MTPolygon p3 = new MTPolygon(getMTApplication(), leftRightHorizontalUpOut);
//		getCanvas().addChild(p3);
		
		rightLeftHorizontalUpOut = getInvertOrderCopy(leftRightHorizontalUpOut);
		
		leftRightOrderHorizontalDownOut = Vertex.getDeepVertexArrayCopy(leftRightHorizontalUpOut);
		Vertex.scaleVectorArray(leftRightOrderHorizontalDownOut, new Vector3D(origHeight/2f,0), 1, -1, 1);
//		MTPolygon p4 = new MTPolygon(getMTApplication(), leftRightOrderHorizontalDownOut);
//		getCanvas().addChild(p4);
		
		rightLeftOrderHorizontalDownOut = getInvertOrderCopy(leftRightOrderHorizontalDownOut);
	}
	
	
	public AbstractShape[] createTiles(PImage p, int horizontalTileCount){
		return createTiles(p, horizontalTileCount, horizontalTileCount);
	}
	
	public AbstractShape[] createTiles(PImage p, int horizontalTileCount, int verticalTileCount){
		this.init(p, horizontalTileCount, verticalTileCount);
		
		List<AbstractShape> tiles = new ArrayList<AbstractShape>();
		TileSide[] sides = new TileSide[]{TileSide.pinIn, TileSide.pinOut};
		
		for (int i = 0; i < verticalTileCount; i++) {
			for (int j = 0; j < horizontalTileCount; j++) {
				TileSide top = TileSide.pinOut, right = TileSide.pinOut, bottom = TileSide.pinOut, left = TileSide.pinIn;
				
				//left und top have to be checked against the previous tiles, right and bottom can be random (if not linear)
				right = sides[Math.round(ToolsMath.getRandom(0, sides.length-1))];
				bottom = sides[Math.round(ToolsMath.getRandom(0, sides.length-1))];
				
				if (j == 0){
					//Left side has to be linear
					left = TileSide.linear;
					
					if (i == 0){
						//top side has to be linear
						top = TileSide.linear;
//						left = getFittingTileSideTo(getRightOfLeftTile(tiles, i, j));
					}else if (i == verticalTileCount -1){
						//Bottom side has to be linear
						bottom = TileSide.linear;
						top = getFittingTileSideTo(getBottomOfUpperTile(tiles, i, j));
//						left = getFittingTileSideTo(getRightOfLeftTile(tiles, i, j));
					}else{
						//in a middle vetical - up or bottom side have to have a pin
						top = getFittingTileSideTo(getBottomOfUpperTile(tiles, i, j));
//						left = getFittingTileSideTo(getRightOfLeftTile(tiles, i, j));
					}
				}else if (j == horizontalTileCount -1){
					right = TileSide.linear;
					
					//Right side has to be linear
					if (i == 0){
						//top side has to be linear
						top = TileSide.linear;
						left = getFittingTileSideTo(getRightOfLeftTile(tiles, i, j));
					}else if (i == verticalTileCount -1){
						//Bottom side has to be linear
						bottom = TileSide.linear;
						top = getFittingTileSideTo(getBottomOfUpperTile(tiles, i, j));
						left = getFittingTileSideTo(getRightOfLeftTile(tiles, i, j));
					}else{
						//in a middle vetical - up or bottom side have to have a pin
						top = getFittingTileSideTo(getBottomOfUpperTile(tiles, i, j));
						left = getFittingTileSideTo(getRightOfLeftTile(tiles, i, j));
					}
				}else{
					//in a middle horizontal, left or right side have to have a pin
					
					if (i == 0){
						//top side has to be linear
						top = TileSide.linear;
						left = getFittingTileSideTo(getRightOfLeftTile(tiles, i, j));
					}else if (i == verticalTileCount -1){
						//Bottom side has to be linear
						bottom = TileSide.linear;
						top = getFittingTileSideTo(getBottomOfUpperTile(tiles, i, j));
						left = getFittingTileSideTo(getRightOfLeftTile(tiles, i, j));
					}else{
						//in a middle vetical - up or bottom side have to have a pin
						top = getFittingTileSideTo(getBottomOfUpperTile(tiles, i, j));
						left = getFittingTileSideTo(getRightOfLeftTile(tiles, i, j));
					}
				}
				
				MTComplexPolygon tile = getPolygon(app, top, right, bottom, left, this.tileWidth, this.tileHeight);
				tile.setName(i + "" + j);
				tile.setUserData("i", i);
				tile.setUserData("j", j);
				tile.setUserData("top", top);
				tile.setUserData("right", right);
				tile.setUserData("bottom", bottom);
				tile.setUserData("left", left);
				//Create some default texture coords
				tile.setBounds(new BoundsZPlaneRectangle(tile));
				if (tile != null && tile.hasBounds() && tile.getBounds() instanceof BoundsZPlaneRectangle){
					BoundsZPlaneRectangle bounds = (BoundsZPlaneRectangle) tile.getBounds();
					
//					float width = bounds.getWidthXY(TransformSpace.LOCAL);
//					float height = bounds.getHeightXY(TransformSpace.LOCAL);
//					float upperLeftX = bounds.getVectorsLocal()[0].x;
//					float upperLeftY = bounds.getVectorsLocal()[0].y;
					
//					float upperLeftX = bounds.getVectorsLocal()[0].x  + j* tileWidth ;
//					float upperLeftY = bounds.getVectorsLocal()[0].y  + i * tileHeight;
					Vertex[] verts = tile.getVerticesLocal();
                    for (Vertex vertex : verts) {
                        //						vertex.setTexCoordU((vertex.x-upperLeftX )/width);
//						vertex.setTexCoordV((vertex.y-upperLeftY)/height);
//						vertex.setTexCoordU((vertex.x - upperLeftX  + (j * tileWidth)) / p.width);
//						vertex.setTexCoordV((vertex.y - upperLeftY + (i * tileHeight)) / p.height);

                        vertex.setTexCoordU((vertex.x + (j * tileWidth)) / p.width);
                        vertex.setTexCoordV((vertex.y + (i * tileHeight)) / p.height);

                        //System.out.println("TexU:" + vertex.getTexCoordU() + " TexV:" + vertex.getTexCoordV());
                    }
					tile.getGeometryInfo().updateTextureBuffer(tile.isUseVBOs());
					
					//Set the texture
					tile.setTexture(p);
//					tile.setNoStroke(true);
//					tile.setStrokeColor(MTColor.GREY);
					tile.setStrokeColor(new MTColor(80,80,80));
					tile.setStrokeWeight(0.7f);
					
					tiles.add(tile);
				}
				
			}
		}
		
		return tiles.toArray(new AbstractShape[tiles.size()]);
	}
	
	
	private TileSide getBottomOfUpperTile(List<AbstractShape> list, int currentI, int currentJ){
		if (currentI-1 < 0){
			return TileSide.linear;
		}
        for (AbstractShape tile : list) {
            int i = (Integer) tile.getUserData("i");
            int j = (Integer) tile.getUserData("j");
            if (i == currentI - 1 && j == currentJ) {
                return (TileSide) tile.getUserData("bottom");
            }
        }
		return TileSide.linear;
	}
	
	private TileSide getRightOfLeftTile(List<AbstractShape> list, int currentI, int currentJ){
		if (currentJ-1 < 0){
			return TileSide.linear;
		}
        for (AbstractShape tile : list) {
            int i = (Integer) tile.getUserData("i");
            int j = (Integer) tile.getUserData("j");
            if (i == currentI && j == currentJ - 1) {
                return (TileSide) tile.getUserData("right");
            }
        }
		return TileSide.linear;
	}
	
	private TileSide getFittingTileSideTo(TileSide otherSide){
		TileSide fitting = TileSide.linear;
		switch (otherSide) {
		case linear:
			fitting = TileSide.linear;
			break;
		case pinIn:
			fitting = TileSide.pinOut;
			break;
		case pinOut:
			fitting = TileSide.pinIn;
			break;
		default:
			break;
		}
		return fitting;
	}
	
	
	public MTComplexPolygon getPolygon(final PApplet app, TileSide top, TileSide right, TileSide bottom, TileSide left, float tileWidth, float tileHeight){
		this.init(tileWidth, tileHeight);
		Vertex[] v = getTile(top, right, bottom, left);
		MTComplexPolygon poly = new MTComplexPolygon(app, v);
		poly.removeAllGestureEventListeners(ScaleProcessor.class);
		poly.addGestureListener(DragProcessor.class, new InertiaDragAction());
		
		//FIXME TEST
		poly.removeAllGestureEventListeners(RotateProcessor.class);
		poly.addGestureListener(RotateProcessor.class, new RotationListener(poly));
		return poly;
	}
	
	
	//FIXME TEST
	private class RotationListener implements IGestureEventListener{
		Vector3D startP1;
		InputCursor oldC1;
		InputCursor oldC2;
		Vector3D planeNormal;
		private Vector3D lastMiddle;
		
		public RotationListener(IMTComponent3D comp){
			planeNormal = new Vector3D(0,0,1);
		}
		
		public boolean processGestureEvent(MTGestureEvent ge) {
			IMTComponent3D comp = ge.getTarget();
			RotateEvent re = (RotateEvent)ge;
			float deg = re.getRotationDegrees();
			InputCursor c1 = re.getFirstCursor();
			InputCursor c2 = re.getSecondCursor();
			
			switch (re.getId()) {
			case RotateEvent.GESTURE_STARTED:{
				oldC1 = c1;
				oldC2 = c2;
				startP1 = comp.getIntersectionGlobal(Tools3D.getCameraPickRay(app, comp, c1));
				Vector3D i1 = ToolsGeometry.getRayPlaneIntersection(Tools3D.getCameraPickRay(app, comp, c1), planeNormal, startP1);
				Vector3D i2 = ToolsGeometry.getRayPlaneIntersection(Tools3D.getCameraPickRay(app, comp, c2), planeNormal, startP1);
				lastMiddle = i1.getAdded(i2.getSubtracted(i1).scaleLocal(0.5f));
			}break;
			case RotateEvent.GESTURE_UPDATED:
				if (!oldC1.equals(c1) || !oldC2.equals(c2)){ //Because c1 and/or c2 can change if a finger with greater distance enters -> prevent jump
					Vector3D i1 = ToolsGeometry.getRayPlaneIntersection(Tools3D.getCameraPickRay(app, comp, c1), planeNormal, startP1);
					Vector3D i2 = ToolsGeometry.getRayPlaneIntersection(Tools3D.getCameraPickRay(app, comp, c2), planeNormal, startP1);
					lastMiddle = i1.getAdded(i2.getSubtracted(i1).scaleLocal(0.5f));
					oldC1 = c1;
					oldC2 = c2;
				}
				
				Vector3D i1 = ToolsGeometry.getRayPlaneIntersection(Tools3D.getCameraPickRay(app, comp, c1), planeNormal, startP1);
				Vector3D i2 = ToolsGeometry.getRayPlaneIntersection(Tools3D.getCameraPickRay(app, comp, c2), planeNormal, startP1);
				Vector3D middle = i1.getAdded(i2.getSubtracted(i1).scaleLocal(0.5f));
				
				Vector3D middleDiff = middle.getSubtracted(lastMiddle);
				comp.rotateZGlobal(middle, deg);
				comp.translateGlobal(middleDiff);
				lastMiddle = middle;
				break;
			case RotateEvent.GESTURE_ENDED:
				break;
			default:
				break;
			}
			return false;
		}
	}
	
//	private class MTComplexPolyClusterable extends MTComplexPolygon implements IdragClusterable{
//		public MTComplexPolyClusterable(PApplet app, Vertex[] vertices) {
//			super(app, vertices);
//		}
//
//		public boolean isSelected() {
//			return false;
//		}
//
//		public void setSelected(boolean selected) {
//		}
//		
//	}
	
	private Vertex[] getTile(TileSide top, TileSide right, TileSide bottom, TileSide left){
		List<Vertex> list = new ArrayList<Vertex>();
		
		switch (top) {
		case linear:
			list.add(new Vertex(0,0));
			list.add(new Vertex(tileWidth, 0));
			break;
		case pinIn:
			addAll(Vertex.getDeepVertexArrayCopy(leftRightOrderHorizontalDownOut), list);
			break;
		case pinOut:
			addAll(Vertex.getDeepVertexArrayCopy(leftRightHorizontalUpOut), list);
			break;
		default:
			break;
		}
		
		switch (right) {
		case linear:
//			list.add(new Vertex(tileWidth,0));
			list.add(new Vertex(tileWidth, tileHeight));
			break;
		case pinIn:
			addAll(getCopyOffset(this.upDownOrderVerticalLeftOut, tileWidth, 0), list);
			break;
		case pinOut:
			addAll(getCopyOffset(this.upDownOrderVerticalRightOut, tileWidth, 0), list);
			break;
		default:
			break;
		}
		
		
		switch (bottom) {
		case linear:
//			list.add(new Vertex(tileWidth, tileHeight));
			list.add(new Vertex(0, tileHeight));
			break;
		case pinIn:
			addAll(getCopyOffset(this.rightLeftHorizontalUpOut, 0, tileHeight), list);
			break;
		case pinOut:
			addAll(getCopyOffset(this.rightLeftOrderHorizontalDownOut, 0, tileHeight), list);
			break;
		default:
			break;
		}
		
		switch (left) {
		case linear:
//			list.add(new Vertex(0, tileHeight));
			list.add(new Vertex(0, 0));
			break;
		case pinIn:
			addAll(Vertex.getDeepVertexArrayCopy(this.downUpOrderVerticalRightOut), list);
			break;
		case pinOut:
			addAll(Vertex.getDeepVertexArrayCopy(this.downUpOrderVerticalLeftOut), list);
			break;
		default:
			break;
		}
		
		return list.toArray(new Vertex[list.size()]);
	}
	
	
	private void addAll(Vertex[] vertices, List<Vertex> list){
        for (Vertex vertex : vertices) {
            list.add(vertex);
        }
	}
	
	
	private Vertex[] getCopyOffset(Vertex[] verts, float xOffset, float yOffset){
		Vertex[] copy = new Vertex[verts.length];
//		Vertex[] copy = Vertex.getDeepVertexArrayCopy(verts);
		for (int i = 0; i < copy.length; i++) {
			copy[i] = (Vertex) new Vertex(verts[i]).addLocal(new Vertex(xOffset, yOffset));
		}
		return copy;
	}
	
	private Vertex[] getInvertOrderCopyOffset(Vertex[] verts, float xOffset, float yOffset){
		Vertex[] copy = new Vertex[verts.length];
//		Vertex[] copy = Vertex.getDeepVertexArrayCopy(verts);
		for (int i = 0; i < copy.length; i++) {
			copy[i] = (Vertex) new Vertex(verts[verts.length -i -1]).addLocal(new Vertex(xOffset, yOffset));
		}
		return copy;
	}
	
	
	private Vertex[] getInvertOrderCopy(Vertex[] verts){
		Vertex[] copy = new Vertex[verts.length];
//		Vertex[] copy = Vertex.getDeepVertexArrayCopy(verts);
		for (int i = 0; i < verts.length; i++) {
//			copy[i] = copy[copy.length -i -1];
			copy[i] = new Vertex(verts[verts.length -i -1]);
		}
		return copy;
	}
	
	
	

}
