package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Text extends GUIObject {
    protected String text;
    protected char[] rawChars;
    private int fontSize;
    
    public Text(int x, int y, int width, int height, String text, int fontSize) {
        super(x, y, width, height, 0);
        this.fontSize = fontSize;
        SetText(text);
    }
    
    public Text(int x, int y, int width, int height, int depth, String text, int fontSize) {
        super(x, y, width, height, depth);
        this.fontSize = fontSize;
        SetText(text);
    }
    
    public void SetText(String newText) {
        if (newText == null) {
            text = "";
        } else {
            text = newText;
        }
        rawChars = text.toCharArray();
    }
    
    @Override
    void Draw(Graphics g, Rectangle parentRect) {
        g.setColor(Color.BLACK);
        g.setFont(new java.awt.Font("Bookman Old Style Bold", 10, fontSize));
        int hOffset = g.getFontMetrics().getHeight();
        // draw the characters of the string
        g.drawChars(rawChars, 0, rawChars.length, rect.x, rect.y + hOffset);
    }
}
