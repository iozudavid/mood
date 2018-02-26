package com.knightlore.gui;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GUIObject {
	public int depth;
	protected Rectangle rect;
	
	GUIObject(int x, int y, int width, int height, int depth) {
		this.depth = depth;
		rect = new Rectangle();
		rect.x = x;
		rect.y = y;
		rect.width = width;
		rect.height = height;
	}
	
	abstract void Draw(Graphics g);
	
	public Rectangle getRectangle(){
		return this.rect;
	}
	
	boolean isSelectable () {
		return false;
	}
	 
	void OnClick() {
		
	}
	
	void OnMouseExit() {
		
	}
	
	void onMouseEnter() {
		
	}
	
	void onMouseOver() {
		
	}
	
	void onMouseUp() {
		
	}
	
	void onMouseDown() {
		
	}

	void onGainedFocus() {
		
	}

	void onLostFocus() {
		
	}
}
