package org.mt4j.util.opengl;

import java.nio.Buffer;

public interface IGLU {
	
	public int gluBuild2DMipmaps(
			int  	target, 
			int  	internalFormat, 
			int  	width, 
			int  	height, 
			int  	format, 
			int  	type, 
		 	Buffer 	data);

}
