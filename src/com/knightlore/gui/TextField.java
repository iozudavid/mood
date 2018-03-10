package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.knightlore.engine.input.InputManager;
import com.knightlore.utils.Vector2D;

public class TextField extends GUIObject {
    private static final Color upColour = Color.WHITE;
    private static final Color downColour = Color.LIGHT_GRAY;
    private static final Color hoverColour = Color.GRAY;

	private SelectState state = SelectState.UP;
	private String text;
	private String insertString;
	private char[] rawChars;
	private int insertPosition = 0;
	private Graphics g;
		
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
		g.setColor(Color.DARK_GRAY);
    	g.fillRect(rect.x-2, rect.y-2, rect.width+2, rect.height+2);
    	g.setColor(Color.BLACK);
    	g.fillRect(rect.x-1, rect.y-1, rect.width+1, rect.height+1);
    	g.setColor(activeColor());
		int hOffset = g.getFontMetrics().getHeight();
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		// draw the characters of the string
		g.setColor(Color.BLACK);
		g.drawChars(rawChars, 0, rawChars.length, rect.x, rect.y+hOffset);
		this.g=g;
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
		if(text.length()==0)
			insertString = c + "|";
		else
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
	
	void onDeleteChar(){
		if(text.length()==0 || insertPosition==0)
			return;
		else if(insertPosition >= text.length()){
			insertPosition = text.length();
			if(text.length()==1){
				insertPosition = 0;
				insertString = "|";
			}
			else{
				insertString = text.substring(0,insertPosition-1)+'|';
				insertPosition--;
			}
			displayText(insertString);
		} else {
			insertString = text.substring(0, insertPosition - 1) + '|' + text.substring(insertPosition);
			insertPosition--;
			displayText(insertString);
		}
		text = insertString.replace("|", "");
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
		Vector2D mousePos = InputManager.getMousePos();
		double deltaPos = mousePos.getX()-this.rect.x;
		int chooseLocation = 0;
		for(char c : this.text.toCharArray()){
			chooseLocation++;
			int width = this.g.getFontMetrics().charWidth(c);
			double oldDelta = deltaPos;
			deltaPos -= width;
			if(deltaPos<0){
				if(oldDelta<Math.abs(deltaPos))
					this.insertPosition = chooseLocation--;
				else
					this.insertPosition = chooseLocation;
				break;
			}
		}
		if(deltaPos>0)
			this.insertPosition = text.length();
		
		insertString = text.substring(0, chooseLocation) + "|" + text.substring(chooseLocation);
		displayText(insertString);
		state = SelectState.DOWN;
	}
	
	@Override
	void onMouseUp() {
		state = SelectState.UP;
	}
}
