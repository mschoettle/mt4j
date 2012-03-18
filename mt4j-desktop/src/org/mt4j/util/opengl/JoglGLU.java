package org.mt4j.util.opengl;

import java.nio.Buffer;

import javax.media.opengl.glu.GLU;

public class JoglGLU implements IGLU{
	protected GLU glu;
	
	public JoglGLU(GLU glu){
		this.glu = glu;
	}
	
	@Override
	public int gluBuild2DMipmaps(int target, int internalFormat, int width,
			int height, int format, int type, Buffer data) {
		return glu.gluBuild2DMipmaps(target, internalFormat, width, height, format, type, data);
	}
	
	

}
