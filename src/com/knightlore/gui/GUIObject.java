package com.knightlore.gui;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GUIObject {
	
	public int depth;
	public Rectangle rect;
	
	GUIObject(int x, int y, int depth){
		this.depth = depth;
		rect = new Rectangle(x,y);
	}
	
	abstract void Draw(Graphics g);
	
	boolean isSelectable () {
		return false;
	}
	 
	void OnClick(){
		
	}
	
	void OnMouseExit(){
		
	}
	
	void OnMouseEnter(){
		
	}
	
	void OnMouseOver(){
		
	}
	
	void OnMouseUp(){
		
	}
	
	void OnMouseDown(){
		
	}
}
