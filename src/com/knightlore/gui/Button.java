package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;

import com.knightlore.render.graphic.Graphic;

public class Button extends GUIObject {
	
	Graphic activeGraphic = null;
	
	private SelectState state = SelectState.UP;
	
	public Color upColour = Color.LIGHT_GRAY;
	public Color downColour = Color.DARK_GRAY;
	public Color hoverColour = Color.WHITE;
	
	public Color activeColor (){
		switch(state){
		case UP:
			return upColour;
			
		case HOVER:
			return hoverColour;
			
		case DOWN:
			return downColour;
			
		}
		return upColour;
	}
	
	public Button(int x, int y, int depth) {
		super(x, y, depth);
	}
	
	@Override
	void Draw(Graphics g) {
		
		if(activeGraphic != null){
			
		}
		else{
			g.setColor(activeColor());
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
		
	}
	
	@Override
	void OnClick(){
		System.out.println("Button clicked");
	}
	
	@Override
	void onMouseEnter(){
		state = SelectState.HOVER;
	}
	
	void onMouseOver(){
		if(state == SelectState.UP){
			state = SelectState.HOVER;
		}
	}
	
	@Override
	void OnMouseExit(){
		state = SelectState.UP;
	}
	
	@Override
	void onMouseDown(){
		state = SelectState.DOWN;
	}
	
	@Override
	void onMouseUp(){
		state = SelectState.UP;
	}
	
	@Override
	boolean isSelectable(){
		return true;
	}

}
