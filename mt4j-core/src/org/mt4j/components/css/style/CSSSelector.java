package org.mt4j.components.css.style;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.css.util.CSSKeywords.CSSSelectorType;

/**
 * The Class CSSSelector.
 */
public class CSSSelector {
	
	/** The primary part of the selector */
	String primary = null;
	
	/** The type of the primary part of the selector */
	CSSSelectorType primaryType = null;
	
	/** The secondary part of the selector. */
	String secondary = null;
	
	/** The type of the secondary part of the selector. */
	CSSSelectorType secondaryType = null;
	
	
	
	/**
	 * Instantiates a new CSS selector.
	 *
	 * @param primary the primary part of the selector
	 * @param primaryType the type of the primary part of the selector
	 */
	public CSSSelector(String primary, CSSSelectorType primaryType) {
		super();
		this.primary = check(primary);
		this.primaryType = primaryType;
	}

	/** Does the selector have a child. */
	boolean selectChild = false;

	/** The child. */
	CSSSelector child = null;
	
	/**
	 * Gets the primary part of the selector
	 *
	 * @return the primary part of the selector
	 */
	public String getPrimary() {
		return primary;
	}

	/**
	 * Sets the primary part of the selector.
	 *
	 * @param primary the new primary part of the selector
	 */
	public void setPrimary(String primary) {
		this.primary = check(primary);
	}

	/**
	 * Gets the type of the primary part of the selector.
	 *
	 * @return the type of the primary part of the selector.
	 */
	public CSSSelectorType getPrimaryType() {
		return primaryType;
	}

	/**
	 * Sets the type of the primary part of the selector.
	 *
	 * @param primaryType the new type of the primary part of the selector.
	 */
	public void setPrimaryType(CSSSelectorType primaryType) {
		this.primaryType = primaryType;
	}

	/**
	 * Gets the secondary part of the selector.
	 *
	 * @return the secondary part of the selector
	 */
	public String getSecondary() {
		return secondary;
	}

	/**
	 * Sets the secondary part of the selector
	 *
	 * @param secondary the new secondary part of the selector
	 */
	public void setSecondary(String secondary) {
		this.secondary = check(secondary);
	}

	/**
	 * Sets the secondary.
	 *
	 * @param secondary the secondary part of the selector
	 * @param type the type of the secondary  part of the selector
	 */
	public void setSecondary(String secondary, CSSSelectorType type) {
		this.secondary = check(secondary);
		this.secondaryType = type;
	}
	
	/**
	 * Gets the type of the secondary part of the selector
	 *
	 * @return the type of the secondary part of the selector
	 */
	public CSSSelectorType getSecondaryType() {
		return secondaryType;
	}

	/**
	 * Sets the type of the secondary part of the selector
	 *
	 * @param secondaryType the new secondary type of the secondary part of the selector
	 */
	public void setSecondaryType(CSSSelectorType secondaryType) {
		this.secondaryType = secondaryType;
	}

	/**
	 * Checks if the selector has a child.
	 *
	 * @return true, if is select child
	 */
	public boolean isSelectChild() {
		return selectChild;
	}

	/**
	 * Sets, whether the selector has a child
	 *
	 * @param selectChild the new select child
	 */
	public void setSelectChild(boolean selectChild) {
		this.selectChild = selectChild;
	}

	/**
	 * Gets the child.
	 *
	 * @return the child
	 */
	public CSSSelector getChild() {
		if (selectChild) return child;
		else return null;
	}

	/**
	 * Sets the child.
	 *
	 * @param child the new child
	 */
	public void setChild(CSSSelector child) {
		if (child != null) {
		this.selectChild = true;
		this.child = child;
		} else {
			this.selectChild = false;
			this.child = null;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((child == null) ? 0 : child.hashCode());
		result = prime * result + ((primary == null) ? 0 : primary.hashCode());
		result = prime * result
				+ ((primaryType == null) ? 0 : primaryType.hashCode());
		result = prime * result
				+ ((secondary == null) ? 0 : secondary.hashCode());
		result = prime * result
				+ ((secondaryType == null) ? 0 : secondaryType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CSSSelector other = (CSSSelector) obj;
		if (child == null) {
			if (other.child != null)
				return false;
		} else if (!child.equals(other.child))
			return false;
		if (primary == null) {
			if (other.primary != null)
				return false;
		} else if (!primary.equalsIgnoreCase(other.primary))
			return false;
		if (primaryType == null) {
			if (other.primaryType != null)
				return false;
		} else if (!primaryType.equals(other.primaryType))
			return false;
		if (secondary == null) {
			if (other.secondary != null)
				return false;
		} else if (!secondary.equalsIgnoreCase(other.secondary))
			return false;
		if (secondaryType == null) {
			if (other.secondaryType != null)
				return false;
		} else if (!secondaryType.equals(other.secondaryType))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String childString = "";
		if (child != null) childString = " Child: " + child.toString();
		if (primary != null) {
			if (secondary != null) {
				return "Primary Selector: " + primary +
				" (" + primaryType + "), Secondary Selector: " + secondary + " (" + secondaryType + ")" + childString;
			} else {
				return "Primary Selector: " + primary +
				" (" + primaryType + ")" + childString;
			}
		}
		return "No Selector";
	}
	
	/**
	 * Replace all unwanted characters (" ", ".", "#")
	 *
	 * @param input the input
	 * @return the string
	 */
	private String check(String input) {
		return input.replace(" ", "").replace(".", "").replace("#", "");
	}
	
	/**
	 * Checks, whether the selector applies to a component
	 *
	 * @param c the MTComponent
	 * @return the priority of the selector (0 if does not apply)
	 * Selector: 	Pos1 	Pos2 > 	Pos3 	Pos 4
	 * 				100-199	200-299	300-399	400-499
	 */
	public int appliesTo(MTComponent c) {
		// Selector: 	Pos1 	Pos2 > 	Pos3 	Pos 4
		// 				100-199	200-299	300-399	400-499
		

		List<String> superclasses = getSuperclasses(c.getClass());
		try {
			if (secondary == null && child == null) {
				switch (primaryType) {
				case TYPE:
					String type = superclasses.get(0);
					if (primary.equalsIgnoreCase(type.replace(" ", ""))) {
						return 175;
					}
					break;
				case CLASS:
					int i = 0;
					for (String s: superclasses) {
						i++;
						if (primary.equalsIgnoreCase(s.replace(" ", ""))) {
							return 150 - i;
						}
					}
						
					break;
				case ID:
					if (c.getCSSID() != "" && primary.equalsIgnoreCase(c.getCSSID().replace(" ", ""))) {
						return 199;
					}
					break;
				case UNIVERSAL:
					return 100;
				default:
					break;
				}
				return 0;	
			} else if (child == null) {
				switch (secondaryType) {
				case TYPE:
					String type = superclasses.get(0);
					if (secondary.equalsIgnoreCase(type.replace(" ", ""))) {
						if (containsParent(c,1) != 0) return 200 + containsParent(c,1) - 25;
						
					}
					break;
				case CLASS:
					for (String s: superclasses) {
						if (secondary.equalsIgnoreCase(s.replace(" ", ""))) {
							if (containsParent(c,1) != 0)
							return 200 + containsParent(c,1) - 50;
							
						}
					}
						
					break;
				case ID:
					if (c.getCSSID() != "" && secondary.equalsIgnoreCase(c.getCSSID().replace(" ", ""))) {
						if (containsParent(c,1) != 0)
						return 200 +  containsParent(c,1);
						
					}
					break;
				case UNIVERSAL:
					if (containsParent(c,1) != 0)
					return 200 + (100 - containsParent(c,1));
					
				default:
					break;
				}
				return 0;
			} else {
				//return child.appliesTo(c);
				if (secondary == null && child.secondary == null) {
					switch (child.primaryType) {
					case TYPE:
						String type = superclasses.get(0);
						if (child.primary.equalsIgnoreCase(type.replace(" ", ""))) {
							if (containsParent(c,5) != 0)
							return 300 + containsParent(c,5) - 25;
						}
						break;
					case CLASS:
						for (String s: superclasses) {
							if (child.primary.equalsIgnoreCase(s.replace(" ", ""))) {
								if (containsParent(c,5) != 0)
								return 300 + containsParent(c,5) - 50;
							}
						}
							
						break;
					case ID:
						if (c.getCSSID() != "" && child.primary.equalsIgnoreCase(c.getCSSID().replace(" ", ""))) {
							if (containsParent(c,5) != 0)
							return 300 + containsParent(c,5);
						}
						break;
					case UNIVERSAL:
						if (containsParent(c,5) != 0)
						return 300 + (100 - containsParent(c,5));
					default:
						break;
					}
					return 0;
				} else if (secondary == null) {
					switch (child.secondaryType) {
					case TYPE:
						String type = superclasses.get(0);
						if (child.secondary.equalsIgnoreCase(type.replace(" ", ""))) {
							if (containsParent(c,4) != 0) 
								return 300 +  containsParent(c,4) -25;
						}
						break;
					case CLASS:
						for (String s: superclasses) {
							if (child.secondary.equalsIgnoreCase(s.replace(" ", ""))) {
								if (containsParent(c,4) != 0) 
								return 300 + containsParent(c,4) - 50;
							}
						}
							
						break;
					case ID:
						if (c.getCSSID() != "" && child.secondary.equalsIgnoreCase(c.getCSSID().replace(" ", ""))) {
							if (containsParent(c,4) != 0) 
							return 300 + containsParent(c,4);
						}
						break;
					case UNIVERSAL:
						if (containsParent(c,4) != 0) 
						return 300 + (100 - containsParent(c,4));
					default:
						break;
					}
					return 0;
				} else if (child.secondary == null) {
					switch (child.primaryType) {
					case TYPE:
						String type = superclasses.get(0);
						if (child.primary.equalsIgnoreCase(type.replace(" ", ""))) {
							if (containsParent(c,2) != 0) 
							return 300 + containsParent(c,2) - 25;
						}
						break;
					case CLASS:
						for (String s: superclasses) {
							if (child.primary.equalsIgnoreCase(s.replace(" ", ""))) {
								if (containsParent(c,2) != 0) 
								return 300 + containsParent(c,2) - 50;
							}
						}
							
						break;
					case ID:
						if (c.getCSSID() != "" && child.primary.equalsIgnoreCase(c.getCSSID().replace(" ", ""))) {
							if (containsParent(c,2) != 0) 
							return 300 + containsParent(c,2);
						}
						break;
					case UNIVERSAL:
						if (containsParent(c,2) != 0) 
						return 300 + (100 - containsParent(c,2));
					default:
						break;
					}
					return 0;
				} else {
					switch (child.secondaryType) {
					case TYPE:
						String type = superclasses.get(0);
						if (child.secondary.equalsIgnoreCase(type.replace(" ", ""))) {
							return 400 + containsParent(c,3) - 25;
						}
						break;
					case CLASS:
						for (String s: superclasses) {
							if (child.secondary.equalsIgnoreCase(s.replace(" ", ""))) {
								return 400 + containsParent(c,3) - 50;
							}
						}
							
						break;
					case ID:
						if (c.getCSSID() != "" && child.secondary.equalsIgnoreCase(c.getCSSID().replace(" ", ""))) {
							return 400 + containsParent(c,3);
						}
						break;
					case UNIVERSAL:
						return 400 + (100 - containsParent(c,3));
					default:
						break;
					}
					return 0;
				}
	
				
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * Checks, whether the Selector applies to a parent of the component
	 *
	 * @param c the MTComponent
	 * @param level the level, for which to search
	 * @return the hierarchy level, on which it was found (0, if not found)
	 */
	private int containsParent(MTComponent c, int level) {
		//Level:
		//1: Parent Match
		//2: Grandparent Parent > Match
		//3: Grandgrandparent Grandparent > Parent Match
		//4: Grandparent > Parent Match
		//5: Parent > Match
		try {
			if (c.getParent() != null) {
				switch (level) {
				case 1: 
					return searchLevelsOne(c);		
				
				case 2: 	
					return 	searchLevelsTwo(c);
				case 3:
					return 	searchLevelsThree(c);
				case 4:
					return 	searchLevelsFour(c);
				case 5:
					return searchLevelsFive(c);
				}
				
			}
		} catch (Exception e) {
			System.out.println("Selector not found - not enough levels?");
		}
		
		
		return 0;
	}
	
	/**
	 * Searches, whether a parent matches the primary part of the selector
	 *
	 * @param c the MTComponent
	 * @return the level, where the selector applies (0 if not found)
	 */
	private int searchLevelsOne(MTComponent c) {
		//Search all above levels for instance of Selector
		int numberOfLevels = numberOfLevels(c);

		try {
			if (numberOfLevels > 1) {
				for (int i = 2; i <= numberOfLevels; i++) {
					if (isMatch(primaryType, primary, getComponentAtLevel(c,i))) {
						return 99 - i;

					}
			
				}
				
				
			}
		} catch (Exception e) {

		}
			
		return 0;
	}
	
	
	
	/**
	 *  Searches, whether a grandparent matches the primary part of the selector and the parent matches the secondary part
	 *
	 * @param c the MTComponent
	 * @return the level, where the selector applies (0 if not found)
	 */
	private int searchLevelsTwo(MTComponent c) {
		//Search all upper levels for Grandparent, Level 1 = Match, Level 2 = Parent (Parent must be directly over child)
		int numberOfLevels = numberOfLevels(c);

		try {
			if (numberOfLevels > 2) {
				isMatch(secondaryType, secondary, getComponentAtLevel(c,2));
				for (int i = 3; i <= numberOfLevels; i++) {
					if (isMatch(primaryType, primary, getComponentAtLevel(c,i))) {
						return 100-i;
					}
			
				}


			}
		} catch (Exception e) {

		}

		return 0;
	}

	/**
	 * Searches, whether a grand-grandparent matches the primary part of the selector and the grandparent matches the secondary part, while the parent matches the primary part of the child
	 *
	 * @param c the c
	 * @return the level, where the selector applies (0 if not found)
	 */
	private int searchLevelsThree(MTComponent c) {
		int numberOfLevels = numberOfLevels(c);
//		boolean found = false;

		try {
			if (numberOfLevels > 3) {
				for (int i=3; i<=numberOfLevels-1; i++) {
					for (int j=i+1; j <= numberOfLevels; j++) {

						 if (isMatch(primaryType, primary, getComponentAtLevel(c,j)) &&
								isMatch(secondaryType, secondary, getComponentAtLevel(c, i)) &&
								isMatch(child.getPrimaryType(), child.getPrimary(), getComponentAtLevel(c,i-1))) {
							 return 104 - i - j;
						 }
					}
				}
			}
		} catch (Exception e) {

		}

		return 0;

	}
	
	/**
	 * Search whether a grandparent matches the primary part of the selector and a parent matches the primary part of the child
	 *
	 * @param c the c
	 * @return the level, where the selector applies (0 if not found)
	 */
	private int searchLevelsFour (MTComponent c) {
		//return 	isMatch(primaryType, primary, c.getParent().getParent()) &&
		//isMatch(child.getPrimaryType(), child.getPrimary(), c.getParent());
		int numberOfLevels = numberOfLevels(c);
//		boolean found = false;
		
		if (numberOfLevels > 2) {
			for (int i=3; i <= numberOfLevels; i++) {

				if (isMatch(primaryType, primary, getComponentAtLevel(c,i)) && 
						isMatch(child.getPrimaryType(), child.getPrimary(), getComponentAtLevel(c,i-1))) {
					return 100 - i;
				}
				
			}
			
			
		}
		
		
		return 0;
	}
	
	/**
	 * Searches, whether a parent matches the primary part of the selector
	 *
	 * @param c the MTComponent
	 * @return the level, where the selector applies (0 if not found)
	 */
	private int searchLevelsFive(MTComponent c) {
		//Search all above levels for instance of Selector
		int numberOfLevels = numberOfLevels(c);
//		boolean found = false;
//		int foundAtLevel = 0;
		try {
			if (numberOfLevels > 1) {
					if (isMatch(primaryType, primary, getComponentAtLevel(c,2))) {
						return 99 - 2;

					}
			

				
				
			}
		} catch (Exception e) {

		}
			
		return 0;
	}
	
	
	/**
	 * Gets the component at a certain level in the hierarchy
	 *
	 * @param c the MTComponent
	 * @param level the level, at which to look
	 * @return the component at that level
	 */
	private MTComponent getComponentAtLevel(MTComponent c,int level) {
		
		try {
			if (level <= 1) {
				return c;
			} else {
				return getComponentAtLevel(c.getParent(), level-1);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Determines the Number of levels in the hierarchy
	 *
	 * @param c the MTComponent
	 * @return the number of levels
	 */
	private int numberOfLevels(MTComponent c) {
		try {
			if (c instanceof MTCanvas || c == null) {
				return 0;
			} else {
				return numberOfLevels(c.getParent()) + 1;
			}
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Checks if the object name or ID matches the selector
	 *
	 * @param type the selector type
	 * @param selector the selector
	 * @param c the MTComponent
	 * @return true, if is a match
	 */
	private boolean isMatch(CSSSelectorType type, String selector, MTComponent c) {
		try {
			if (c != null && c instanceof MTComponent) {
			List<String> superclasses = getSuperclasses(c.getClass());
			switch (type) {
			case TYPE:
				if (superclasses.get(0).replace(" ", "").equalsIgnoreCase(selector)) return true; 
				break;
			case CLASS:
				for (String s: superclasses) {
					if (selector.equalsIgnoreCase(s.replace(" ", ""))) return true;
				}
				break;
			case ID: 
				if (c.getCSSID() != "" && c.getCSSID().replace(" ", "").equalsIgnoreCase(selector)) return true;
				break;
			default:
				System.out.println("WTF? Unknown Type " + type + " with Selector " + selector);
			
			}
			}
		} catch (Exception e) {
			
			System.out.println("Someting went wrong with finding " + selector + " in " + c.getClass().getSimpleName());
		}
		return false;
	}
	
	/**
	 * Gets the superclasses of a class
	 *
	 * @param c the Class
	 * @return the superclasses
	 */
	private List<String> getSuperclasses(Class<?> c) {
		List<String> superclasses = new ArrayList<String>();
		superclasses.add(c.getSimpleName().toUpperCase().replace(" ", ""));
		while (c.getSuperclass() != null) {
			c = c.getSuperclass();
			superclasses.add(c.getSimpleName().toUpperCase().replace(" ", ""));
		}
		
		
		return superclasses;
	}
	
}
