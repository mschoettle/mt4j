package org.mt4j.components.css.style;

/**
 * The Class CSSStyleHierarchy: Adds hierarchical information to a CSSStyle (for prioritizing/sorting)
 */
public class CSSStyleHierarchy implements Comparable<Object>{
	
	/** The constant NA. */
	public final short NA = 0;
	
	/** The constant Pos1. */
	public final short POS1 = 1;
	
	/** The constant Pos2. */
	public final short POS2 = 2;
	
	/** The constant Pos3. */
	public final short POS3 = 3;
	
	/** The constant Pos4. */
	public final short POS4 = 4;

	/**
	 * Instantiates a new CSS style hierarchy using a CSSStyle only -> Priority = 0, Type = NA
	 *
	 * @param style the style
	 */
	public CSSStyleHierarchy(CSSStyle style) {
		this.style = style;
		this.priority = 0;
		this.type = NA;
		
	}
	
	/**
	 * Instantiates a new CSS style hierarchy using the type -> Priority = 0
	 *
	 * @param style the style
	 * @param type the type
	 */
	public CSSStyleHierarchy(CSSStyle style, short type) {
		this.style = style;
		this.priority = 0;
		this.type = type;
	}
	
	/**
	 * Instantiates a new CSS style hierarchy, using a different CSSStyleHierarchy
	 *
	 * @param sh the CSSStyleHierarchy to start with
	 * @param priority the priority
	 * @param type the type
	 */
	public CSSStyleHierarchy(CSSStyleHierarchy sh, int priority, short type) {
		this.style = sh.getStyle();
		this.priority = priority;
		this.type = type;
		
		
	}
	
	/** The priority. */
	int priority;
	
	/** The style. */
	CSSStyle style;
	
	/** The type. */
	short type;
	
	/**
	 * Instantiates a new CSS style hierarchy, using a CSSStyle and a priority
	 *
	 * @param style the style
	 * @param priority the priority
	 */
	public CSSStyleHierarchy(CSSStyle style, int priority) {
		this.style = style;
		this.priority = priority;
	}

	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * Sets the priority.
	 *
	 * @param priority the new priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public CSSStyle getStyle() {
		return style;
	}

	/**
	 * Sets the style.
	 *
	 * @param style the new style
	 */
	public void setStyle(CSSStyle style) {
		this.style = style;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public short getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(short type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		if (arg0 instanceof CSSStyleHierarchy) {
			CSSStyleHierarchy cs = (CSSStyleHierarchy) arg0;
			if (cs.getType() != (this.getType())) {
				if ((this.getType()) < (cs.getType())) {
					return -1;
				} else if ((this.getType()) > (cs.getType())) {
					return 1;
				} else {
					return 0;
				}
			} else {
				if (this.getPriority() < cs.getPriority()) {
					return -1;
				} else if (this.getPriority() > cs.getPriority()) {
					return 1;
				} else {
					return 0;
				}
			}
			
			
		}
		return 0;
	}


	
	
}
