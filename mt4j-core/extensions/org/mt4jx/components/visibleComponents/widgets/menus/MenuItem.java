package org.mt4jx.components.visibleComponents.widgets.menus;

import org.mt4j.input.inputProcessors.IGestureEventListener;

import processing.core.PImage;

public class MenuItem {
	private String menuText = "";
	private short type = 0;
	private PImage menuImage = null;
	private IGestureEventListener gestureListener = null;
	
	
	public static short TEXT = 1;
	public static short PICTURE = 2;
	public static short NONE = 0;
	
	public MenuItem(String text, IGestureEventListener al) {
		this.type = TEXT;
		this.menuText = text;
		this.gestureListener = al;
	}
	public MenuItem(PImage image, IGestureEventListener al) {
		this.type = PICTURE;
		this.menuImage = image;
		this.gestureListener = al;
	}
	public String getMenuText() {
		return menuText;
	}
	public void setMenuText(String menuText) {
		this.menuText = menuText;
		this.type = TEXT;
	}
	public PImage getMenuImage() {
		return menuImage;
	}
	public void setMenuImage(PImage menuImage) {
		this.menuImage = menuImage;
		this.type = PICTURE;
	}
	public short getType() {
		return type;
	}
	public IGestureEventListener getGestureListener() {
		return gestureListener;
	}
	public void setGestureListener(IGestureEventListener gestureListener) {
		this.gestureListener = gestureListener;
	}
	
	
	
}
