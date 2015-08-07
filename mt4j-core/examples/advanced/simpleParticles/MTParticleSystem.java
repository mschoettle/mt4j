package advanced.simpleParticles;

import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class MTParticleSystem extends AbstractVisibleComponent {

	private ParticleSystem ps;


	public MTParticleSystem(PApplet pApplet, float x, float y, float width, float height) {
		super(pApplet);

		BoundsZPlaneRectangle bounds = new BoundsZPlaneRectangle(this, x, y, width, height);
		this.setBounds(bounds);
		
		ps = new ParticleSystem(pApplet, 0, new PVector((x+width)/2f, (y+height)/2f, 0));
//		ps.addParticle();
		
//		String p = MT4jSettings.DEFAULT_IMAGES_PATH + MTApplication.separator + "Particles" + MTApplication.separator;
//		final PImage texture = pApplet.loadImage(MT4jSettings.DEFAULT_IMAGES_PATH + "018533-glossy-black-3d-button-icon-symbols-shapes-shapes-toggle-up64.png");
//		final PImage texture = pApplet.loadImage(p + "fire.jpg");
//		final PImage texture = pApplet.loadImage(p + "particle.bmp");
//		final PImage texture = pApplet.loadImage(p + "whitefire.png");
//		final PImage texture = pApplet.loadImage(p + "glow1.png");
//		final PImage texture = pApplet.loadImage(p + "particle.png");
		
//		this.registerInputProcessor(new MultipleDragProcessor(pApplet));
//		this.addGestureListener(MultipleDragProcessor.class, new IGestureEventListener() {
//			public boolean processGestureEvent(MTGestureEvent ge) {
//				DragEvent de = (DragEvent)ge;
//				
//				Vector3D local = globalToLocal(de.getTo());
////				ps.addParticle(local.x, local.y);
//				ps.addParticle(new ImageParticle(getRenderer(), new PVector(local.x,local.y), texture));
////				ps.addParticle(new ImageParticle(getRenderer(), new PVector(local.x,local.y), texture));
//				
//				//TODO only do new point if > some distance threshold
//				return false;
//			}
//		});
	}

	public ParticleSystem getParticleSystem(){
		return this.ps;
	}
	
	@Override
	public void drawComponent(PGraphics g) {
		ps.run(g);
	}
	

}
