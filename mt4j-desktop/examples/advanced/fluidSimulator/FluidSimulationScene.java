/***********************************************************************
 
 Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 *** The Mega Super Awesome Visuals Company ***
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of MSA Visuals nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ***********************************************************************/ 
package advanced.fluidSimulator;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import msafluid.MSAFluidSolver2D;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.PlatformUtil;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GL10;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

import com.sun.opengl.util.BufferUtil;

public class FluidSimulationScene extends AbstractScene{
	
	private final float FLUID_WIDTH = 120;
	private float invWidth, invHeight;    // inverse of screen dimensions
	private float aspectRatio, aspectRatio2;
	private MSAFluidSolver2D fluidSolver;
	private PImage imgFluid;
	private boolean drawFluid = true;
	
	private ParticleSystem particleSystem;
	/////////
	
	private AbstractMTApplication app;

	public FluidSimulationScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		
		if (!MT4jSettings.getInstance().isOpenGlMode()){
			System.err.println("Scene only usable when using the OpenGL renderer! - See settings.txt");
        	return;
        }
		
        //pa.hint( PApplet.ENABLE_OPENGL_4X_SMOOTH );    // Turn on 4X antialiasing
        invWidth = 1.0f/mtApplication.width;
        invHeight = 1.0f/mtApplication.height;
        aspectRatio = mtApplication.width * invHeight;
        aspectRatio2 = aspectRatio * aspectRatio;
     
        // Create fluid and set options
        fluidSolver = new MSAFluidSolver2D((int)(FLUID_WIDTH), (int)(FLUID_WIDTH * mtApplication.height/mtApplication.width));
//        fluidSolver.enableRGB(true).setFadeSpeed(0.003f).setDeltaT(0.5f).setVisc(0.00005f);
        fluidSolver.enableRGB(true).setFadeSpeed(0.003f).setDeltaT(0.8f).setVisc(0.00004f);
     
        // Create image to hold fluid picture
        imgFluid = mtApplication.createImage(fluidSolver.getWidth(), fluidSolver.getHeight(), PApplet.RGB);
        
        // Create particle system
        particleSystem = new ParticleSystem(mtApplication, fluidSolver);
        
        this.getCanvas().addInputListener(new IMTInputEventListener() {
        	//@Override
        	public boolean processInputEvent(MTInputEvent inEvt){
        		if(inEvt instanceof AbstractCursorInputEvt){
        			AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt)inEvt;
        			if (posEvt.hasTarget() && posEvt.getTarget().equals(getCanvas())){
        				InputCursor m = posEvt.getCursor();
        				AbstractCursorInputEvt prev = m.getPreviousEventOf(posEvt);
        				if (prev == null)
        					prev = posEvt;

        				Vector3D pos = new Vector3D(posEvt.getX(), posEvt.getY(), 0);
        				Vector3D prevPos = new Vector3D(prev.getX(), prev.getY(), 0);

        				//System.out.println("Pos: " + pos);
        				float mouseNormX = pos.x * invWidth;
        				float mouseNormY = pos.y * invHeight;
        				//System.out.println("MouseNormPosX: " + mouseNormX + "," + mouseNormY);
        				float mouseVelX = (pos.x - prevPos.x) * invWidth;
        				float mouseVelY = (pos.y - prevPos.y) * invHeight;
        				/*
	        			System.out.println("Mouse vel X: " + mouseVelX + " mouseNormX:" + mouseNormX);
	        			System.out.println("Mouse vel Y: " + mouseVelY + " mouseNormY:" + mouseNormY);
        				 */
        				addForce(mouseNormX, mouseNormY, mouseVelX, mouseVelY);
        			}
        		}
        		return false;
        	}
		});
        //FIXME make componentInputProcessor?
        
        this.getCanvas().addChild(new FluidImage(mtApplication));
        
        
        this.getCanvas().setDepthBufferDisabled(true);
	}
	
	
	/**
	 * The Class FluidImage.
	 */
	private class FluidImage extends MTComponent{
		public FluidImage(PApplet applet) {
			super(applet);
		}
		//@Override
		public void drawComponent(PGraphics g) {
			super.drawComponent(g);
			drawFluidImage();
			
			g.noSmooth();
			g.fill(255,255,255,255);
			g.tint(255,255,255,255);
			
			//FIXME TEST
//			PGraphicsOpenGL pgl = (PGraphicsOpenGL)g; 
//			GL gl = pgl.gl;
			GL10 gl = PlatformUtil.getGL();
			gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL.GL_COLOR_ARRAY);
			gl.glDisable(GL.GL_LINE_SMOOTH);
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}
	
	
	//@Override
	public void drawAndUpdate(PGraphics graphics, long timeDelta) {
//		this.drawFluidImage();
		super.drawAndUpdate(graphics, timeDelta);
		
//		app.noSmooth();
//		app.fill(255,255,255,255);
//		app.tint(255,255,255,255);
//		
//		//FIXME TEST
//		PGraphicsOpenGL pgl = (PGraphicsOpenGL)app.g; 
//		GL gl = pgl.gl;
//		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
//		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
//		gl.glDisable(GL.GL_LINE_SMOOTH);
//		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
//		mtApp.colorMode(PApplet.RGB, 255);  
	}
	
	
	
	// add force and dye to fluid, and create particles
	private void addForce(float x, float y, float dx, float dy) {
	    float speed = dx * dx  + dy * dy * aspectRatio2;    // balance the x and y components of speed with the screen aspect ratio
	 
	    if(speed > 0) {
	        if(x < 0){ 
	        	x = 0; 
	        }else if(x > 1){
	        	x = 1;
	        }if(y < 0){ 
	        	y = 0; 
	        }else if(y > 1){ 
	        	y = 1;
	        }
	        
	        float colorMult = 5;
	        float velocityMult = 20.0f;
	 
	        int index = fluidSolver.getIndexForNormalizedPosition(x, y);
	 
//	        PApplet.color drawColor;
	        app.colorMode(PApplet.HSB, 360, 1, 1);
	        float hue = ((x + y) * 180 + app.frameCount) % 360;
	        int drawColor = app.color(hue, 1, 1);
	        app.colorMode(PApplet.RGB, 1);  
	 
	        fluidSolver.rOld[index]  += app.red(drawColor) 	* colorMult;
	        fluidSolver.gOld[index]  += app.green(drawColor) 	* colorMult;
	        fluidSolver.bOld[index]  += app.blue(drawColor) 	* colorMult;
	 
	        //Particles
	        particleSystem.addParticles(x * app.width, y * app.height, 10);
	        
	        fluidSolver.uOld[index] += dx * velocityMult;
	        fluidSolver.vOld[index] += dy * velocityMult;
	        
//	        mtApp.noSmooth();
//			mtApp.fill(255,255,255,255);
//			mtApp.tint(255,255,255,255);
			
			//FIXME TEST
			app.colorMode(PApplet.RGB, 255);  
	    }
	}
	
	
	private void drawFluidImage(){
		app.colorMode(PApplet.RGB, 1);  
		 
		fluidSolver.update();
	    if(drawFluid) {
	        for(int i=0; i<fluidSolver.getNumCells(); i++) {
	            int d = 2;
	            imgFluid.pixels[i] = app.color(fluidSolver.r[i] * d, fluidSolver.g[i] * d, fluidSolver.b[i] * d);
	        }  
	        imgFluid.updatePixels();//  fastblur(imgFluid, 2);
	        
//	        app.image(imgFluid, 0, 0, app.width, app.height); //FIXME this messes up blend transition!
	        
	        app.textureMode(PConstants.NORMAL);
//	        app.textureMode(app.IMAGE);
//	        app.beginShape(MTApplication.QUADS);
	        app.beginShape();
	        app.texture(imgFluid);
	        app.vertex(0, 0, 0, 0);
	        app.vertex(app.width, 0, 1, 0);
	        app.vertex(app.width, app.height, 1, 1);
	        app.vertex(0, app.height, 0, 1);
	        app.endShape();

	    } 
	    particleSystem.updateAndDraw();
	    
	    app.colorMode(PApplet.RGB, 255);  
	}

	
	public void onEnter() {
		getMTApplication().registerKeyEvent(this);
	}
	
	public void onLeave() {	
		getMTApplication().unregisterKeyEvent(this);
		/*
		mtApp.noSmooth();
		mtApp.fill(255,255,255,255);
		mtApp.tint(255,255,255,255);
		PGraphicsOpenGL pgl = (PGraphicsOpenGL)mtApp.g; 
		GL gl = pgl.gl;
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		gl.glDisable(GL.GL_LINE_SMOOTH);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mtApp.colorMode(PApplet.RGB, 255);  
		 */
	}
	
	
	/**
	 * 
	 * @param e
	 */
	public void keyEvent(KeyEvent e){
		int evtID = e.getID();
		if (evtID != KeyEvent.KEY_PRESSED)
			return;
		switch (e.getKeyCode()){
		case KeyEvent.VK_BACK_SPACE:
			app.popScene();
			break;
			default:
				break;
		}
	}

	
	
	private class Particle {
		private final static float MOMENTUM = 0.5f;
		private final static float FLUID_FORCE = 0.6f;

		private float x, y;
		private float vx, vy;
		//private float radius;       // particle's size
		protected float alpha;
		private float mass;
		private PApplet p;
		private float invWidth;
		private float invHeight;
		private MSAFluidSolver2D fluidSolver;
		
		
		public Particle(PApplet p, MSAFluidSolver2D fluidSolver, float invWidth, float invHeight){
			this.p = p;
			this.invWidth = invWidth;
			this.invHeight = invHeight;
			this.fluidSolver = fluidSolver;
		}

	   public void init(float x, float y) {
	       this.x = x;
	       this.y = y;
	       vx = 0;
	       vy = 0;
	       //radius = 5;
	       alpha = p.random(0.3f, 1);
	       mass = p.random(0.1f, 1);
	   }


	   public void update() {
	       // only update if particle is visible
	       if(alpha == 0) return;

	       // read fluid info and add to velocity
	       int fluidIndex = fluidSolver.getIndexForNormalizedPosition(x * invWidth, y * invHeight);
	       vx = fluidSolver.u[fluidIndex] * p.width * mass * FLUID_FORCE + vx * MOMENTUM;
	       vy = fluidSolver.v[fluidIndex] * p.height * mass * FLUID_FORCE + vy * MOMENTUM;

	       // update position
	       x += vx;
	       y += vy;

	       // bounce of edges
	       if(x<0) {
	           x = 0;
	           vx *= -1;
	       }else if(x > p.width) {
	           x = p.width;
	           vx *= -1;
	       }

	       if(y<0) {
	           y = 0;
	           vy *= -1;
	       }else if(y > p.height) {
	           y = p.height;
	           vy *= -1;
	       }

	       // hackish way to make particles glitter when the slow down a lot
	       if(vx * vx + vy * vy < 1) {
	           vx = p.random(-1, 1);
	           vy = p.random(-1, 1);
	       }

	       // fade out a bit (and kill if alpha == 0);
	       alpha *= 0.999;
	       if(alpha < 0.01) 
	    	   alpha = 0;

	   }


	   public void updateVertexArrays(int i, FloatBuffer posBuffer, FloatBuffer colBuffer) {
	       int vi = i * 4;
	       posBuffer.put(vi++, x - vx);
	       posBuffer.put(vi++, y - vy);
	       posBuffer.put(vi++, x);
	       posBuffer.put(vi++, y);

	       int ci = i * 6;
	       colBuffer.put(ci++, alpha);
	       colBuffer.put(ci++, alpha);
	       colBuffer.put(ci++, alpha);
	       colBuffer.put(ci++, alpha);
	       colBuffer.put(ci++, alpha);
	       colBuffer.put(ci++, alpha);
	   }


	   public void drawOldSchool(GL gl) {
	       gl.glColor3f(alpha, alpha, alpha);
	       gl.glVertex2f(x-vx, y-vy);
	       gl.glVertex2f(x, y);
	   }

	}//end particle class
	
	
	
	
	public class ParticleSystem{
		private FloatBuffer posArray;
		private FloatBuffer colArray;
		private final static int maxParticles = 5000;
		private int curIndex;
		
		boolean renderUsingVA = true;
		
		private Particle[] particles;
		private PApplet p;
		private MSAFluidSolver2D fluidSolver;
		private float invWidth;
		private float invHeight;
		
		private boolean drawFluid;
		
		public ParticleSystem(PApplet p, MSAFluidSolver2D fluidSolver) {
			this.p = p;
			this.fluidSolver = fluidSolver;
			this.invWidth = 1.0f/p.width;
			this.invHeight = 1.0f/p.height;
			
			this.drawFluid = true;
			
			particles = new Particle[maxParticles];
			
			for(int i=0; i<maxParticles; i++) {
				particles[i] = new Particle(p, this.fluidSolver, invWidth, invHeight);
			}
			
			curIndex = 0;

			posArray = BufferUtil.newFloatBuffer(maxParticles * 2 * 2);// 2 coordinates per point, 2 points per particle (current and previous)
			colArray = BufferUtil.newFloatBuffer(maxParticles * 3 * 2);
		}


		public void updateAndDraw(){
//			PGraphicsOpenGL pgl = (PGraphicsOpenGL)p.g;         // processings opengl graphics object
//			GL gl = pgl.beginGL();                // JOGL's GL object
			GL10 gl = PlatformUtil.beginGL();

			gl.glEnable( GL.GL_BLEND );             // enable blending
			
			/*
			if(!drawFluid) 
				fadeToColor(p, gl, 0, 0, 0, 0.05f);
			*/

			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);  // additive blending (ignore alpha)
			gl.glEnable(GL.GL_LINE_SMOOTH);        // make points round
			gl.glLineWidth(1);


			if(renderUsingVA) {
				for(int i=0; i<maxParticles; i++) {
					if(particles[i].alpha > 0) {
						particles[i].update();
						particles[i].updateVertexArrays(i, posArray, colArray);
					}
				}    
				gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
				gl.glVertexPointer(2, GL.GL_FLOAT, 0, posArray);

				gl.glEnableClientState(GL.GL_COLOR_ARRAY);
				gl.glColorPointer(3, GL.GL_FLOAT, 0, colArray);

				gl.glDrawArrays(GL.GL_LINES, 0, maxParticles * 2);
			} 
			else {
				/*
				gl.glBegin(GL.GL_LINES);               // start drawing points
				for(int i=0; i<maxParticles; i++) {
					if(particles[i].alpha > 0) {
						particles[i].update();
						particles[i].drawOldSchool(gl);    // use oldschool renderng
					}
				}
				gl.glEnd();
				*/
			}

//			gl.glDisable(GL.GL_BLEND);
			//Reset blendfunction
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//			pgl.endGL();
			PlatformUtil.endGL();
		}

		/*
		public void fadeToColor(PApplet p, GL10 gl, float r, float g, float b, float speed) {
//			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glColor4f(r, g, b, speed);
			gl.glBegin(GL.GL_QUADS);
				gl.glVertex2f(0, 0);
				gl.glVertex2f(p.width, 0);
				gl.glVertex2f(p.width, p.height);
				gl.glVertex2f(0, p.height);
			gl.glEnd();
		}
		*/
		

		public void addParticles(float x, float y, int count ){
			for(int i=0; i<count; i++) addParticle(x + p.random(-15, 15), y + p.random(-15, 15));
		}


		public void addParticle(float x, float y) {
			particles[curIndex].init(x, y);
			curIndex++;
			if(curIndex >= maxParticles) curIndex = 0;
		}



		public boolean isDrawFluid() {
			return drawFluid;
		}

		public void setDrawFluid(boolean drawFluid) {
			this.drawFluid = drawFluid;
		}

	}//end psystem class

}
