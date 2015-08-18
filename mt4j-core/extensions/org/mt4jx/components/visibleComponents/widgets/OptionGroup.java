package org.mt4jx.components.visibleComponents.widgets;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class OptionGroup.
 */
public class OptionGroup {
	
	/** The associated option boxes. */
	List<MTOptionBox> optionBoxes = new ArrayList<MTOptionBox>();
	
	/**
	 * Instantiates a new option group.
	 */
	public OptionGroup() {
		
	}

	/**
	 * Gets the option boxes.
	 *
	 * @return the option boxes
	 */
	public List<MTOptionBox> getOptionBoxes() {
		return optionBoxes;
	}

	/**
	 * Sets the option boxes.
	 *
	 * @param optionBoxes the new option boxes
	 */
	public void setOptionBoxes(List<MTOptionBox> optionBoxes) {
		this.optionBoxes = optionBoxes;
	}
	
	/**
	 * Adds an option box.
	 *
	 * @param box the box
	 */
	public void addOptionBox(MTOptionBox box) {
		if (!this.optionBoxes.contains(box)) {
			this.optionBoxes.add(box);
		}
	}
	
	/**
	 * Removes an option box.
	 *
	 * @param box the box
	 */
	public void removeOptionBox(MTOptionBox box) {
		this.optionBoxes.remove(box);
	}
	
	/**
	 * Sets the specified box as enabled, disable the rest
	 *
	 * @param box the new enabled
	 */
	public void setEnabled(MTOptionBox box) {
		for (MTOptionBox ob: optionBoxes) {
			if (ob != box) {
				ob.disable();
			}
			
		}
	}
	
	/**
	 * Gets the option which is enabled (as int)
	 * From 1...n, where 1 is the first OptionBox added and n the last
	 *
	 * @return the option
	 */
	public short getOption() {
		short i=1;
		for (MTOptionBox ob: optionBoxes) {
			if (ob.getBooleanValue() == true) return i;
			i++;
		}
		return 0;
	}
	
	
}
