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

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.StateChange;
import org.mt4j.components.StateChangeEvent;
import org.mt4j.components.StateChangeListener;
import org.mt4j.components.interfaces.IMTController;
import org.mt4j.util.camera.MTCamera;

/**
 * This is a component that will try to always stay on top and will stay in place even
 * if the scene's camera is moved.
 * <br>Thus it can be used as a container for a GUI or a HUD (head up display). 
 * 
 * @author Christopher Ruff
 */
public class MTOverlayContainer extends MTComponent {
	
	/** The app. */
	private AbstractMTApplication app;
	
	//TODO overlay layer priorites / layer numbers to sort them by priority
	
	/**
	 * Instantiates a new mT overlay container.
	 * 
	 * @param applet the applet
	 */
	public MTOverlayContainer(AbstractMTApplication applet) {
		this(applet, "unnamed overlay container");
	}

	/**
	 * Instantiates a new mT overlay container.
	 * 
	 * @param app the app
	 * @param name the name
	 */
	public MTOverlayContainer(AbstractMTApplication app, String name) {
		super(app, name, new MTCamera(app));
		this.app = app;
		
		this.setDepthBufferDisabled(true);

		//Send overlay group to front again if it isnt - check each frame if its on front!
		this.setController(new IMTController() {
			public void update(long timeDelta) {
				putLastInParentList();
			}
		});
		
		//Always put last in parents children list if added to a parent,
		//so the chance is higher that this is really drawn ontop of other stuff
		this.addStateChangeListener(StateChange.ADDED_TO_PARENT, new StateChangeListener() {
			public void stateChanged(StateChangeEvent evt) {
				putLastInParentList();
			}
		});
	}
	
	
	private void putLastInParentList(){
		MTComponent parent = getParent();
		if (parent != null){
			int childCount = parent.getChildCount();
			
			if (	childCount > 0
				&& !parent.getChildByIndex(childCount-1).equals(MTOverlayContainer.this)
			){
				MTComponent lastChild = parent.getChildByIndex(childCount-1);
				if (	!(lastChild instanceof MTOverlayContainer)
					&& 	!(lastChild.getName().equalsIgnoreCase("Cursor Trace group"))
				){
					//last component in canvas child list is not a overlay container:
					MTOverlayContainer.this.app.invokeLater(new Runnable() {
						public void run(){
							MTComponent parent = getParent();
							if (parent != null){
								parent.removeChild(MTOverlayContainer.this);
								parent.addChild(MTOverlayContainer.this);
							}
						}
					});
				}else{
					//last component in canvas already is a different overlay container:
//					int insertionIndex = getInsertionIndex(parent);
					
				}
			}
		}
	}
	
	/*
	private int getInsertionIndex(MTComponent parent){
		int count = parent.getChildCount();
		for (int i = 1; i <= count; i++) {
//			int index = -i;
			int checkIndex = count-i;
			MTComponent child = parent.getChildByIndex(checkIndex);
			System.out.println("Check at: " + checkIndex + " ->" + child);
			
			if (child instanceof MTOverlayContainer){
				
			}else{
				System.out.println("Found insertion place for : " + this + " at: " + checkIndex);
				return checkIndex;
			}
		}
		return 0;
	}
	*/



}
