package com.knightlore.gui;

import com.knightlore.render.PixelBuffer;

/**
 * Class creating a checkbox whether ticked or not.
 *
 * @author David Iozu
 */
public class CheckBox extends GUIObject {
    
    private boolean switcher;
    
    public CheckBox(int x, int y, int width, int height, int depth, boolean switcher) {
        super(x, y, width, height, depth);
        this.switcher = switcher;
    }
    
    /**
     * Draw the checkbox.
     */
    @Override
    void Draw(PixelBuffer pix, int x, int y) {
        pix.drawRect(0x000000, rect.x, rect.y, rect.width, rect.height);
        pix.drawRect(0x000000, rect.x + 1, rect.y + 1, rect.width - 1, rect.height - 1);
        pix.drawRect(0x000000, rect.x + 2, rect.y + 2, rect.width - 2, rect.height - 2);
        if (switcher) {
            //also make a bold tick
            pix.drawLine(0x000000, rect.x, rect.y + (int)((double)((double)rect.height / 2D)), rect.x + (int)((double)((double)rect.width / 2D)), rect.y + rect.height);
            pix.drawLine(0x000000, rect.x + (int)((double)((double)rect.width / 2D)), rect.y + rect.height, rect.x + rect.width, rect.y);
            pix.drawLine(0x000000, rect.x, rect.y + (int)((double)((double)rect.height / 2D)) - 1, rect.x + (int)((double)((double)rect.width / 2D)) + 1, rect.y + rect.height);
            pix.drawLine(0x000000, rect.x + (int)((double)((double)rect.width / 2D)) + 1, rect.y + rect.height, rect.x + rect.width, rect.y);
            pix.drawLine(0x000000, rect.x, rect.y + (int)((double)((double)rect.height / 2D)) + 1, rect.x + (int)((double)((double)rect.width / 2D)) - 1, rect.y + rect.height);
            pix.drawLine(0x000000, rect.x + (int)((double)((double)rect.width / 2D)) - 1, rect.y + rect.height, rect.x + rect.width, rect.y);
            pix.drawLine(0x000000, rect.x, rect.y + (int)((double)((double)rect.height / 2D)) - 2, rect.x + (int)((double)((double)rect.width / 2D)) + 2, rect.y + rect.height);
            pix.drawLine(0x000000, rect.x + (int)((double)((double)rect.width / 2D)) + 2, rect.y + rect.height, rect.x + rect.width, rect.y);
            pix.drawLine(0x000000, rect.x, rect.y + (int)((double)((double)rect.height / 2D)) + 2, rect.x + (int)((double)((double)rect.width / 2D)) - 2, rect.y + rect.height);
            pix.drawLine(0x000000, rect.x + (int)((double)((double)rect.width / 2D)) - 2, rect.y + rect.height, rect.x + rect.width, rect.y);
            
        }
    }
    
    @Override
    boolean isSelectable() {
        return true;
    }
    
    /**
     * Switch the tick to untick and vice-versa.
     */
    @Override
    void OnClick() {
        this.switcher = !this.switcher;
    }
    
    /**
     * @return whether it is ticked or not
     */
    public boolean getBobingMode() {
        return this.switcher;
    }
    
    /**
     * Set whether is it ticked or not
     *
     * @param b - set the tick or untick state.
     */
    public void setBobingMode(boolean b) {
        this.switcher = b;
    }
    
}
