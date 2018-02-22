package com.knightlore.gui;

import java.awt.Graphics;

public class Text extends GUIObject{
	protected String text;
	protected char[] rawChars;
	
	public Text(int x, int y, int depth, String text) {
		super(x, y, depth);
		SetText(text);
	}

	public void SetText(String newText) {
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
