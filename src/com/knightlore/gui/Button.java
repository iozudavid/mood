package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;

import com.knightlore.render.graphic.Graphic;

public class Button extends GUIObject {
	
	Graphic activeGraphic = null;
	
	private ButtonState state = ButtonState.UP;
	
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
	void OnMouseEnter(){
		state = ButtonState.HOVER;
	}
	
	void OnMouseOver(){
		if(state == ButtonState.UP){
			state = ButtonState.HOVER;
		}
	}
	
	@Override
	void OnMouseExit(){
		state = ButtonState.UP;
	}
	
	@Override
	void OnMouseDown(){
		state = ButtonState.DOWN;
	}
	
	@Override
	void OnMouseUp(){
		state = ButtonState.UP;
	}
	
	@Override
	boolean isSelectable(){
		return true;
	}

}
