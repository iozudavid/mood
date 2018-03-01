package com.knightlore.gui;

import java.awt.Graphics;
import java.awt.Rectangle;

public class Text extends GUIObject{
	protected String text;
	protected char[] rawChars;

    public Text(int x, int y, int width, int height, String text) {
		super(x, y, width, height,0);
		SetText(text);
	}
	
	public Text(int x, int y, int width, int height, int depth, String text) {
		super(x, y, width, height,depth);
		SetText(text);
	}

	public void SetText(String newText) {
		text = newText;
		rawChars = text.toCharArray();
	}
	
	@Override
	void Draw(Graphics g, Rectangle parentRect) {
		int hOffset = g.getFontMetrics().getHeight();
		// draw the characters of the string
		g.drawChars(rawChars, 0, rawChars.length, rect.x, rect.y + hOffset);
	}
}
