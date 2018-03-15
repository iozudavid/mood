package com.knightlore.gui;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GUIObject {	
    // these should be change-able at runtime by other classes
    // no getter and setter required
	public int depth;
	public Rectangle rect;
    public boolean isVisible;
	

	GUIObject(int x, int y,int width, int height, int depth){
		this.depth = depth;
		rect = new Rectangle(x,y,width,height);
	}
	
	GUIObject(int x, int y,int width, int height){
        this.depth = 0;
        rect = new Rectangle(x,y,width,height);
    }
	
	abstract void Draw(Graphics g, Rectangle parentRect);
	
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
