package com.knightlore.gui;

import java.awt.Color;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.font.Font;

/**
 * Create and render text.
 *
 * @author David Iozu, James Adey
 */
public class Text extends GUIObject {
    private final double fontSize;
    public Color currentColor = Color.BLACK;
    protected String text;
    protected char[] rawChars;
    
    public Text(int x, int y, int width, int height, String text, double fontSize) {
        super(x, y, width, height, 0);
        this.fontSize = fontSize / 15D;
        SetText(text);
    }
    
    public Text(int x, int y, int width, int height, int depth, String text, double fontSize) {
        super(x, y, width, height, depth);
        this.fontSize = fontSize / 15D;
        SetText(text);
    }
    
    /**
     * Set the current text.
     *
     * @param newText - new text to be set
     */
    public void SetText(String newText) {
        if (newText == null) {
            text = "";
        } else {
            text = newText;
        }
        rawChars = text.toCharArray();
    }
    
    /**
     * Draw the text on the screen.
     */
    @Override
    void Draw(PixelBuffer pix, int x, int y) {
        int hOffset = Font.DEFAULT_WHITE.getHeight();
        // draw the characters of the string
        if (this.currentColor == Color.RED) {
            pix.drawString(Font.DEFAULT_RED, new String(rawChars), rect.x, rect.y + hOffset, this.fontSize, 2);
        } else if (this.currentColor == Color.BLACK) {
            pix.drawString(Font.DEFAULT_BLACK, new String(rawChars), rect.x, rect.y + hOffset, this.fontSize, 2);
        } else {
            pix.drawString(Font.DEFAULT_WHITE, new String(rawChars), rect.x, rect.y + hOffset, this.fontSize, 2);
        }
        
    }
}
