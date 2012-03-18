package advanced.simpleParticles;

import org.mt4j.util.MTColor;
import org.mt4j.util.math.ToolsMath;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class ImageParticle extends Particle {

	private PImage texture;
	private MTColor color;
	
	
	public ImageParticle(PApplet app, PVector l, PImage texture) {
		super(app, l);
		this.texture = texture;
		
		this.color = new MTColor(ToolsMath.getRandom(50, 255), ToolsMath.getRandom(50, 255), ToolsMath.getRandom(50, 255), 255);
	}
	
	@Override
	public void render(PGraphics g) {
//		super.render();
		
		g.pushStyle();
		g.noStroke();
//		g.fill(255, timer);
		g.tint(this.color.getR(),this.color.getG(), this.color.getB(), timer+155);
		
		g.pushMatrix();
		g.translate(loc.x, loc.y); //TODO too expensive, set the vertex coords instead?
		g.scale(ToolsMath.map(timer, 0, 100, 0.5f, 8f)); //makesem smaller
//		g.scale(ToolsMath.map(timer, 100, 0, 0.5f, 2.5f)); //makes'em bigger
		g.rotate(vel.heading2D());
		
		float widthHalf = 10;
		float heightHalf = 10;
//		float widthHalf = texture.width;
//		float heightHalf = texture.height;
		
		g.textureMode(PConstants.NORMAL);
		g.beginShape();
		g.texture(this.texture);
		
		g.vertex(-widthHalf, -heightHalf, 0, 0, 0);
		
		g.vertex(widthHalf, -heightHalf, 0, 1, 0);
		
		g.vertex(widthHalf, heightHalf, 0, 1, 1);
		
		g.vertex(-widthHalf, heightHalf, 0, 0, 1);

		
		g.endShape();
		
		g.popMatrix();
		
		g.popStyle();
	}

}
