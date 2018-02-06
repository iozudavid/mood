package com.knightlore.gui;

import java.awt.Graphics;
import java.awt.Rectangle;


public class Text extends GUIObject{
	
	private String text;
	private char[] rawChars;
	
	Text(int x, int y, int depth) {
		super(x, y, depth);
	}

	

	public void SetText(String newText){
		text = newText;
		rawChars = text.toCharArray();
	}
	
	@Override
	void Draw(Graphics g) {
		
		int hOffset = g.getFontMetrics().getHeight();
		// draw the characters of the string
		g.drawChars(rawChars, 0, rawChars.length, rect.x, rect.y + hOffset);
	}

}
