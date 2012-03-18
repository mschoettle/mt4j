package org.mt4j.components.css.style;

import org.mt4j.components.css.util.CSSKeywords.Position;
import org.mt4j.components.css.util.CSSKeywords.PositionType;

/**
 * The Class CSSBackgroundPosition.
 */
public class CSSBackgroundPosition {
	
	/** The x pos. */
	float xPos = 0;
	
	/** The y pos. */
	float yPos = 0;
	
	/** The x position type. */
	PositionType xType = PositionType.KEYWORD;
	
	/** The y position type. */
	PositionType yType = PositionType.KEYWORD;
	
	/** The x position as keyword. */
	Position xKeywordPosition = Position.CENTER;
	
	/** The y position as keyword. */
	Position yKeywordPosition = Position.CENTER;
	
	/** Is the position unchanged. */
	boolean unchanged = true;
	
	/**
	 * Instantiates a new CSSBackgroundPosition.
	 */
	public CSSBackgroundPosition() {
		
	}
	
	/**
	 * Instantiates a new CSSBackgroundPosition.
	 *
	 * @param x the x
	 * @param y the y
	 * @param isRelativeX is x relative?
	 * @param isRelativeY is y relative?
	 */
	public CSSBackgroundPosition(float x, float y, boolean isRelativeX, boolean isRelativeY) {
		xPos = x;
		yPos = y;
		
		if (isRelativeX) xType = PositionType.RELATIVE;
		else xType = PositionType.ABSOLUTE;
		
		if (isRelativeY) yType = PositionType.RELATIVE;
		else yType = PositionType.ABSOLUTE;
		unchanged = false;
	}
	
	/**
	 * Instantiates a new CSSBackgroundPosition.
	 *
	 * @param x the x
	 * @param y the y
	 * @param isRelativeY is y relative?
	 */
	public CSSBackgroundPosition(Position x, float y, boolean isRelativeY) {
		xKeywordPosition = x;
		xType = PositionType.KEYWORD;
		
		if (isRelativeY) yType = PositionType.RELATIVE;
		else yType = PositionType.ABSOLUTE;
		unchanged = false;
	}
	
	/**
	 * Instantiates a new CSSBackgroundPosition.
	 *
	 * @param x the x
	 * @param y the y
	 * @param isRelativeX is x relative?
	 */
	public CSSBackgroundPosition(float x, Position y, boolean isRelativeX) {
		yKeywordPosition = y;
		yType = PositionType.KEYWORD;
		
		if (isRelativeX) xType = PositionType.RELATIVE;
		else xType = PositionType.ABSOLUTE;
		unchanged = false;
	}
	
	/**
	 * Instantiates a new CSSBackgroundPosition.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public CSSBackgroundPosition(Position x, Position y) {
		xKeywordPosition = x;
		xType = PositionType.KEYWORD;
		
		yKeywordPosition = y;
		yType = PositionType.KEYWORD;
		unchanged = false;
	}

	/**
	 * Gets the x pos.
	 *
	 * @return the x pos
	 */
	public float getxPos() {
		return xPos;
	}

	/**
	 * Gets the y pos.
	 *
	 * @return the y pos
	 */
	public float getyPos() {
		return yPos;
	}

	/**
	 * Gets the x type.
	 *
	 * @return the x type
	 */
	public PositionType getxType() {
		return xType;
	}

	/**
	 * Gets the y type.
	 *
	 * @return the y type
	 */
	public PositionType getyType() {
		return yType;
	}

	/**
	 * Gets the x position keyword.
	 *
	 * @return the x position keyword
	 */
	public Position getxKeywordPosition() {
		return xKeywordPosition;
	}

	/**
	 * Gets the y position keyword.
	 *
	 * @return the y keyword position
	 */
	public Position getyKeywordPosition() {
		return yKeywordPosition;
	}

	/**
	 * Checks if the position is unchanged.
	 *
	 * @return true, if is unchanged
	 */
	public boolean isUnchanged() {
		return unchanged;
	}

	/**
	 * Sets the x pos.
	 *
	 * @param xPos the new x pos
	 */
	public void setxPos(float xPos) {
		this.xPos = xPos;
	}

	/**
	 * Sets the y pos.
	 *
	 * @param yPos the new y pos
	 */
	public void setyPos(float yPos) {
		this.yPos = yPos;
	}

	/**
	 * Sets the x type.
	 *
	 * @param xType the new x type
	 */
	public void setxType(PositionType xType) {
		this.xType = xType;
	}

	/**
	 * Sets the y type.
	 *
	 * @param yType the new y type
	 */
	public void setyType(PositionType yType) {
		this.yType = yType;
	}

	/**
	 * Sets the x position keyword.
	 *
	 * @param xKeywordPosition the new x position keyword
	 */
	public void setxKeywordPosition(Position xKeywordPosition) {
		this.xKeywordPosition = xKeywordPosition;
	}

	/**
	 * Sets the y position keyword.
	 *
	 * @param yKeywordPosition the new y position keyword
	 */
	public void setyKeywordPosition(Position yKeywordPosition) {
		this.yKeywordPosition = yKeywordPosition;
	}

	/**
	 * Sets the unchanged.
	 *
	 * @param unchanged the new unchanged
	 */
	public void setUnchanged(boolean unchanged) {
		this.unchanged = unchanged;
	}

	
}
