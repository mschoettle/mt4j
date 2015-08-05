package advanced.simpleParticles;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Particle {
	PVector loc;
	PVector vel;
	PVector acc;
	float r;
	float timer;
	private PApplet app;

	// Another constructor (the one we are using here)
	public Particle(PApplet app, PVector l) {
		this.app = app;
		acc = new PVector(0, 0.05f, 0);
		vel = new PVector(app.random(-1,1), app.random(-2,0),0);
		loc = l.get();
		r = 10.0f;
//		timer = 100.0f;
		timer = 80.0f;
	}
	
	

	public void run(PGraphics g) {
		update();
		render(g);
	}

	// Method to update location
	public void update() {
		vel.add(acc);
		loc.add(vel);
		timer -= 1.0f;
	}

	// Method to display
	public void render(PGraphics g) {
		app.ellipseMode(PApplet.CENTER);
		app.stroke(255,timer);
		app.fill(100,timer);
		app.ellipse(loc.x,loc.y,r,r);
		displayVector(vel,loc.x,loc.y,10);
	}


	public void displayVector(PVector v, float x, float y, float scayl) {
		app.pushMatrix();
		float arrowsize = 4;
		// Translate to location to render vector
		app.translate(x,y);
		app.stroke(255);
		// Call vector heading function to get direction (note that pointing up is a heading of 0) and rotate
		app.rotate(v.heading2D());
		// Calculate length of vector & scale it to be bigger or smaller if necessary
		float len = v.mag()*scayl;
		// Draw three lines to make an arrow (draw pointing up since we've rotate to the proper direction)
		app.line(0,0,len,0);
		app.line(len,0,len-arrowsize,+arrowsize/2);
		app.line(len,0,len-arrowsize,-arrowsize/2);
		app.popMatrix();
	} 
	
	
	// Is the particle still useful?
	public boolean isDead() {
		if (timer <= 0.0) {
			return true;
		} else {
			return false;
		}
	}

}