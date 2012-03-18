package org.mt4j.util.opengl;

import java.nio.Buffer;

public interface GL11Plus  extends GL11, GL20 {
	//Added for backwards compatibility
	public static final int GL_LINE_STIPPLE = 0x0B24;
	public static final int GL_COMPILE = 0x1300;
	public static final int GL_COMPILE_AND_EXECUTE = 4865;
	public static final int GL_QUADS = 0x0007; 

	public static final long SIZEOF_BYTE   = 1L;
	public static final long SIZEOF_SHORT  = 2L;
	public static final long SIZEOF_INT    = 4L;
	public static final long SIZEOF_FLOAT  = 4L;
	public static final long SIZEOF_LONG   = 8L;
	public static final long SIZEOF_DOUBLE = 8L;
	
	public static final int GL_UNSIGNED_INT = 0x1405;
	public static final int GL_DEPTH_STENCIL_EXT           =   0x84F9;
	public static final int GL_UNSIGNED_INT_24_8_EXT       =   0x84FA;
	public static final int GL_DEPTH24_STENCIL8_EXT        =   0x88F0;
	public static final int GL_TEXTURE_STENCIL_SIZE_EXT    =   0x88F1;
	
	public static final int GL_VIEWPORT_BIT                =  0x00000800;
	
	/* GL_EXT_framebuffer_object */
	public static final int GL_INVALID_FRAMEBUFFER_OPERATION_EXT                = 0x0506;
	public static final int GL_MAX_RENDERBUFFER_SIZE_EXT                        = 0x84E8;
	public static final int GL_FRAMEBUFFER_BINDING_EXT                          = 0x8CA6;
	public static final int GL_RENDERBUFFER_BINDING_EXT                         = 0x8CA7;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE_EXT           = 0x8CD0;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME_EXT           = 0x8CD1;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL_EXT         = 0x8CD2;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE_EXT  = 0x8CD3;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_3D_ZOFFSET_EXT    = 0x8CD4;
	public static final int GL_FRAMEBUFFER_COMPLETE_EXT                         = 0x8CD5;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT            = 0x8CD6;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT    = 0x8CD7;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT            = 0x8CD9;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT               = 0x8CDA;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT           = 0x8CDB;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT           = 0x8CDC;
	public static final int GL_FRAMEBUFFER_UNSUPPORTED_EXT                      = 0x8CDD;
	public static final int GL_MAX_COLOR_ATTACHMENTS_EXT                        = 0x8CDF;
	public static final int GL_COLOR_ATTACHMENT0_EXT                            = 0x8CE0;
	public static final int GL_COLOR_ATTACHMENT1_EXT                            = 0x8CE1;
	public static final int GL_COLOR_ATTACHMENT2_EXT                            = 0x8CE2;
	public static final int GL_COLOR_ATTACHMENT3_EXT                            = 0x8CE3;
	public static final int GL_COLOR_ATTACHMENT4_EXT                            = 0x8CE4;
	public static final int GL_COLOR_ATTACHMENT5_EXT                            = 0x8CE5;
	public static final int GL_COLOR_ATTACHMENT6_EXT                            = 0x8CE6;
	public static final int GL_COLOR_ATTACHMENT7_EXT                            = 0x8CE7;
	public static final int GL_COLOR_ATTACHMENT8_EXT                            = 0x8CE8;
	public static final int GL_COLOR_ATTACHMENT9_EXT                            = 0x8CE9;
	public static final int GL_COLOR_ATTACHMENT10_EXT                           = 0x8CEA;
	public static final int GL_COLOR_ATTACHMENT11_EXT                           = 0x8CEB;
	public static final int GL_COLOR_ATTACHMENT12_EXT                           = 0x8CEC;
	public static final int GL_COLOR_ATTACHMENT13_EXT                           = 0x8CED;
	public static final int GL_COLOR_ATTACHMENT14_EXT                           = 0x8CEE;
	public static final int GL_COLOR_ATTACHMENT15_EXT                           = 0x8CEF;
	public static final int GL_DEPTH_ATTACHMENT_EXT                             = 0x8D00;
	public static final int GL_STENCIL_ATTACHMENT_EXT                           = 0x8D20;
	public static final int GL_FRAMEBUFFER_EXT                                  = 0x8D40;
	public static final int GL_RENDERBUFFER_EXT                                 = 0x8D41;
	public static final int GL_RENDERBUFFER_WIDTH_EXT                           = 0x8D42;
	public static final int GL_RENDERBUFFER_HEIGHT_EXT                          = 0x8D43;
	public static final int GL_RENDERBUFFER_INTERNAL_FORMAT_EXT                 = 0x8D44;
	public static final int GL_STENCIL_INDEX1_EXT                               = 0x8D46;
	public static final int GL_STENCIL_INDEX4_EXT                               = 0x8D47;
	public static final int GL_STENCIL_INDEX8_EXT                               = 0x8D48;
	public static final int GL_STENCIL_INDEX16_EXT                              = 0x8D49;
	public static final int GL_RENDERBUFFER_RED_SIZE_EXT                        = 0x8D50;
	public static final int GL_RENDERBUFFER_GREEN_SIZE_EXT                      = 0x8D51;
	public static final int GL_RENDERBUFFER_BLUE_SIZE_EXT                       = 0x8D52;
	public static final int GL_RENDERBUFFER_ALPHA_SIZE_EXT                      = 0x8D53;
	public static final int GL_RENDERBUFFER_DEPTH_SIZE_EXT                      = 0x8D54;
	public static final int GL_RENDERBUFFER_STENCIL_SIZE_EXT                    = 0x8D55;

	
	public static final int GL_TEXTURE_RECTANGLE_ARB = 0x84F5;
	public static final int GL_TEXTURE_1D    = 0x0DE0;
	public static final int GL_CLAMP = 0x2900;
	public static final int GL_CLAMP_TO_BORDER = 0x812D;
	
	public static final int GL_BGRA  = 0x80E1;




	
	public int glGenLists(int id);

	public void glNewList(int id, int mode);
	
	public void glCallList(int id);

	public void glCallLists(int arg0, int arg1, Buffer arg2);

	public void glEndList();
	
	public void glDeleteLists(int list, int range);
	

	public void glPushAttrib(int att);
	
	public void glPopAttrib();
	
	
	public void glLineStipple(int t, short stipple);

	public void glTexImage1D(
			int target, 
			int level, 
			int internalFormat, 
			int width, 
			int border, 
			int format, 
			int type, 
			Buffer data);

	public void glTexSubImage1D(
			int target, 
			int level, 
			int xoffset,
			int width, 
			int format, 
			int type, 
			Buffer pixels);
	
	public void glGetTexImage(
			int  	target, 
			int  	level, 
			int  	format, 
			int  	type, 
			Buffer	img);
	

	public void glColorMaterial(int face, int mode);
	
	
	public boolean isExtensionAvailable(String name);
	

	public void setSwapInterval(int interval);

}
