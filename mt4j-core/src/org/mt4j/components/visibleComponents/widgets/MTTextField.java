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
package org.mt4j.components.visibleComponents.widgets;

import org.mt4j.components.TransformSpace;
import org.mt4j.util.font.IFont;
import org.mt4j.util.font.IFontCharacter;

import processing.core.PApplet;

/**
 * The Class MTTextField. This is a modifed text area, that
 * keeps a fixed size and ignores new lines ("\n").
 * So this is for single line of text with a fixed size no matter
 * how long the actual text is.
 * 
 * @author Christopher Ruff
 */
public class MTTextField extends MTTextArea {

	
	/**
	 * Instantiates a new mT text field.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param font the font
	 * @param applet the applet
	 * @deprecated constructor will be deleted! Please , use the constructor with the PApplet instance as the first parameter.
	 */
	public MTTextField(float x, float y, float width, float height, IFont font, PApplet applet) {
		this(applet, x, y, width, height, font);
	}
	
	
	/**
	 * Instantiates a new mT text field.
	 * @param applet the applet
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param font the font
	 */
	public MTTextField(PApplet applet, float x, float y, float width, float height, IFont font) {
		super(applet, x, y, width,height, font);
	}
	
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.widgets.MTTextArea#characterAdded(org.mt4j.components.visibleComponents.font.IFontCharacter)
	 */
	protected void characterAdded(IFontCharacter character){
		//Intercept new line characters
		if (character.getUnicode().equalsIgnoreCase("\n")){
			this.removeLastCharacter(); 
		}else{
			//Scroll the text to the left if end of field reached
			float localWidth = this.getWidthXY(TransformSpace.LOCAL);
			if (this.getText().length() > 0 && getLastCharEndPos() > localWidth) {
			float diff = getLastCharEndPos() - localWidth;
			this.scrollTextX(-diff);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.widgets.MTTextArea#characterRemoved(org.mt4j.components.visibleComponents.font.IFontCharacter)
	 */
	@Override
	protected void characterRemoved(IFontCharacter character) {
		//Scroll the text to the right if scrolled
		if (this.getText().length() > 0 && this.getScrollTextX() < 0){
			if (this.getMaxLineWidth() < this.getWidthXY(TransformSpace.LOCAL)){
				this.scrollTextX(Math.abs(this.getScrollTextX()));	
			}else{
				this.scrollTextX(this.getWidthXY(TransformSpace.LOCAL) - this.getLastCharEndPos());	
			}
		}
	}
	
	/**
	 * Gets the last char end pos.
	 * 
	 * @return the last char end pos
	 */
	private float getLastCharEndPos(){
		return this.getMaxLineWidth() + this.getScrollTextX();
	}
	

}
	
