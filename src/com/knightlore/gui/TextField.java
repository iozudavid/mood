package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;

public class TextField extends GUIObject {
	
	private SelectState state = SelectState.UP;
	private String text;
	private String insertString;
	private char[] rawChars;
	private int maxLength;
	private int insertPosition = 0;
	
	public Color upColour = Color.LIGHT_GRAY;
	public Color downColour = Color.DARK_GRAY;
	public Color hoverColour = Color.WHITE;
	
	public TextField(int x, int y, int depth, String text) {
		super(x, y, depth);
		SetText(text);
		DisplayText(text);
	}
	
	public void SetText(String newText){
		text = newText;
	}
	
	public void DisplayText(String t){
		rawChars = t.toCharArray();
	}
	
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
	
	@Override
	void Draw(Graphics g) {
		// draw a background
		g.setColor(activeColor());
		int hOffset = g.getFontMetrics().getHeight();
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		// draw the characters of the string
		g.setColor(Color.BLACK);
		g.drawChars(rawChars, 0, rawChars.length, rect.x, rect.y+hOffset);
	}
	
	@Override
	boolean isSelectable(){
		return true;
	}
	
	@Override
	void onGainedFocus(){
		System.out.println("GAINED FOCUS");
		GUICanvas.setActiveTextField(this);
		
	}
	
	@Override
	void onLostFocus(){
		System.out.println("LOST FOCUS");
		SetText(text);
	}

	void onInputChar(char c) {
		System.out.println(c);
		insertString = text.substring(0, insertPosition)+c+'|'+text.substring(insertPosition);
		text = insertString.replace("|", "");
		DisplayText(insertString);
	}
	
	void onLeftArrow(){
		insertPosition --;
		if(insertPosition < 0){
			insertPosition = 0;
		}
		insertString = text.substring(0, insertPosition)+'|'+text.substring(insertPosition);
		DisplayText(insertString);
	}
	
	void onRightArrow () {
		insertPosition++;
		if(insertPosition > text.length()){
			insertPosition = text.length();
		}
		insertString = text.substring(0, insertPosition)+'|'+text.substring(insertPosition);
		DisplayText(insertString);
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
}
