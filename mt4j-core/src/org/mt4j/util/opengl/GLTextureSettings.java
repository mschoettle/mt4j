/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.util.opengl;

import org.mt4j.util.opengl.GLTexture.EXPANSION_FILTER;
import org.mt4j.util.opengl.GLTexture.SHRINKAGE_FILTER;
import org.mt4j.util.opengl.GLTexture.TEXTURE_TARGET;
import org.mt4j.util.opengl.GLTexture.WRAP_MODE;


/**
 * The Class GLTextureSettings.
 *
 * @author Christopher Ruff
 */
public class GLTextureSettings {
	
	/** The shrink filter. */
	public SHRINKAGE_FILTER shrinkFilter;
	
	/** The expansion filter. */
	public EXPANSION_FILTER expansionFilter;
	
	/** The wrapping horizontal. */
	public WRAP_MODE wrappingHorizontal;
	
	/** The wrapping vertical. */
	public WRAP_MODE wrappingVertical;
	
	/** The target. */
	public TEXTURE_TARGET target;
	
//	public INTERNAL_FORMAT textureInternalFormat;
	
	//INFO we may not use mip maps by default because in abstractshape we would create a gltexture form a pimage automatically
	//but we will un normalize the tex coords while they should still be normalized if we use gluBuildMipmaps2d which scales NPOT tex to POT
    
    /**
 * Instantiates a new gL texture settings.
 */
public GLTextureSettings(){
    	this(TEXTURE_TARGET.TEXTURE_2D, SHRINKAGE_FILTER.BilinearNoMipMaps, EXPANSION_FILTER.Bilinear, WRAP_MODE.CLAMP_TO_EDGE, WRAP_MODE.CLAMP_TO_EDGE);
    }
    
    /**
     * Instantiates a new gL texture settings.
     *
     * @param target the target
     * @param shrinkFilter the shrink filter
     * @param expansionFilter the expansion filter
     * @param wrappingHorizontal the wrapping horizontal
     * @param wrappingVertical the wrapping vertical
     */
    public GLTextureSettings(TEXTURE_TARGET target, SHRINKAGE_FILTER shrinkFilter, EXPANSION_FILTER expansionFilter, WRAP_MODE wrappingHorizontal, WRAP_MODE wrappingVertical){
    	this.target = target;
		
		// Filter mode
		this.shrinkFilter 		= shrinkFilter;
		this.expansionFilter 	= expansionFilter;
		
		// Texture Wrapping mode
		this.wrappingHorizontal = wrappingHorizontal; //TODO use clamp_to_edge - but what examples etc would that break?
		this.wrappingVertical 	= wrappingVertical;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
//    	return super.equals(obj);
    	
    	if (obj instanceof GLTextureSettings) {
			GLTextureSettings settings = (GLTextureSettings) obj;
			return( 
					this.target == settings.target
					&& this.shrinkFilter == settings.shrinkFilter
					&& this.expansionFilter == settings.expansionFilter
					&& this.wrappingHorizontal == settings.wrappingHorizontal
					&& this.wrappingVertical == settings.wrappingVertical
//					&& this.textureInternalFormat == settings.textureInternalFormat //FIXME REMOVE!?
			);
		}else{
			return false;
		}
    }
	

}
