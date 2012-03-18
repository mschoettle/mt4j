package advanced.simpleParticles;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.mt4j.util.PlatformUtil;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

// A class to describe a group of Particles
// An ArrayList is used to manage the list of Particles 

public class ParticleSystem {

	private ArrayList<Particle> particles;    // An arraylist for all the particles
	private PVector origin;        // An origin point for where particles are born
	private PApplet app;

	public ParticleSystem(PApplet app, int num, PVector v) {
		this.app = app;
		particles = new ArrayList<Particle>();              // Initialize the arraylist
		origin = v.get();                        // Store the origin point
		for (int i = 0; i < num; i++) {
			particles.add(new Particle(app, origin));    // Add "num" amount of particles to the arraylist
		}
	}

	public void run(PGraphics g) {
//		GL gl = Tools3D.getGL(g);
		GL10 gl = PlatformUtil.getGL();
		gl.glDisable(GL.GL_DEPTH_TEST);
//		gl.glDepthMask(false);//depth testing - makes depth buffer read-only
//		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE);//define blending as alpha blending
		gl.glBlendFunc(GL.GL_ONE,GL.GL_ONE);//define blending as alpha blending
		
		// Cycle through the ArrayList backwards b/c we are deleting
		for (int i = particles.size()-1; i >= 0; i--) {
			Particle p = (Particle) particles.get(i);
			p.run(g);
			if (p.isDead()) {
				particles.remove(i);
			}
		}
		
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//		gl.glDepthMask(true);//depth testing - makes depth buffer read-only
		gl.glEnable(GL.GL_DEPTH_TEST);
	}

	public void addParticle() {
		particles.add(new Particle(app, origin));
	}

	public void addParticle(float x, float y) {
		particles.add(new Particle(app, new PVector(x,y)));
	}

	public void addParticle(Particle p) {
		particles.add(p);
	}
	
	public void addParticle(Particle p, float x, float y){
		p.loc.set(x,y,0);
		particles.add(p);
	}

	// A method to test if the particle system still has particles
	public boolean isDead() {
		if (particles.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

}

