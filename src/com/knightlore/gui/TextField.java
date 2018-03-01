package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class TextField extends GUIObject {
    private static final Color upColour = Color.LIGHT_GRAY;
    private static final Color downColour = Color.DARK_GRAY;
    private static final Color hoverColour = Color.WHITE;

	private SelectState state = SelectState.UP;
	private String text;
	private String insertString;
	private char[] rawChars;
	private int insertPosition = 0;
		
	public TextField(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    TextField(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
    }
	
    public TextField(int x, int y, int width, int height, String text) {
    	this(x, y, width, height, 0, text);
    }

    public void setText(String newText){
    	text = newText;
    	displayText(text);
   	}

	
	public TextField(int x, int y, int width, int height, int depth, String text) {
		super(x, y, width, height, depth);
		setText(text);
		displayText(text);
	}
	
	public String getText() {
	    return text;
	}
	
	public void displayText(String t){
		rawChars = t.toCharArray();
	}
	
	public Color activeColor () {
		switch(state){
			case UP:
				return upColour;

			case HOVER:
				return hoverColour;

			case DOWN:
				return downColour;
		}

		throw new IllegalStateException("State " + state + " is not legal");
	}
	
	@Override
	void Draw(Graphics g, Rectangle parentRect) {
		// draw a background
		g.setColor(activeColor());
		int hOffset = g.getFontMetrics().getHeight();
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		// draw the characters of the string
		g.setColor(Color.BLACK);
		g.drawChars(rawChars, 0, rawChars.length, rect.x, rect.y+hOffset);
	}
	
	@Override
	boolean isSelectable() {
		return true;
	}
	
	@Override
	void onGainedFocus() {
		System.out.println("GAINED FOCUS");
		GUICanvas.setActiveTextField(this);
		
	}
	
	@Override
	void onLostFocus() {
		System.out.println("LOST FOCUS");
		GUICanvas.activeTextField = null;
		displayText(text);
	}

	void onInputChar(char c) {
		System.out.println(c);
		insertString = text.substring(0, insertPosition)+c+'|'+text.substring(insertPosition);
		text = insertString.replace("|", "");
		insertPosition++;
		displayText(insertString);
	}
	
	void onLeftArrow() {
		insertPosition --;
		if(insertPosition < 0){
			insertPosition = 0;
		}

		insertString = text.substring(0, insertPosition)+'|'+text.substring(insertPosition);
		displayText(insertString);
	}
	
	void onRightArrow () {
		insertPosition++;
		if(insertPosition > text.length()){
			insertPosition = text.length();
		}
		insertString = text.substring(0, insertPosition)+'|'+text.substring(insertPosition);
		displayText(insertString);
	}
	
	@Override
	void onMouseEnter() {
		state = SelectState.HOVER;
	}
	
	void onMouseOver() {
		if(state == SelectState.UP) {
			state = SelectState.HOVER;
		}
	}
	
	@Override
	void OnMouseExit() {
		state = SelectState.UP;
	}
	
	@Override
	void onMouseDown() {
		state = SelectState.DOWN;
	}
	
	@Override
	void onMouseUp() {
		state = SelectState.UP;
	}
}
