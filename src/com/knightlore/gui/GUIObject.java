package com.knightlore.gui;

import java.awt.Rectangle;

import com.knightlore.render.PixelBuffer;

/**
 * Base class for GUIObjects.
 *
 * @author David Iozu, James Adey
 */
public abstract class GUIObject {
    // these should be change-able at runtime by other classes
    // no getter and setter required
    public final int depth;
    public final Rectangle rect;
    public boolean isVisible;
    
    
    GUIObject(int x, int y, int width, int height, int depth) {
        this.depth = depth;
        rect = new Rectangle(x, y, width, height);
    }
    
    GUIObject(int x, int y, int width, int height) {
        this.depth = 0;
        rect = new Rectangle(x, y, width, height);
    }
    
    /**
     * Draw the actual GameObject on the screen.
     *
     * @param pix - PixelBuffer to render on.
     * @param x   - X Position to start rendering from
     * @param y   - Y Position to star rendering from
     */
    abstract void Draw(PixelBuffer pix, int x, int y);
    
    /**
     * @return this object's rectangle
     */
    public Rectangle getRectangle() {
        return this.rect;
    }
    
    /**
     * @return whether or not this object is selectable with mouse or not
     */
    boolean isSelectable() {
        return false;
    }
    
    /**
     * Called when object is clicked.
     */
    void OnClick() {
    
    }
    
    /**
     * Called when mouse exits object's rectangle.
     */
    void OnMouseExit() {
    
    }
    
    /**
     * Called when mouse enters object's rectangle.
     */
    void onMouseEnter() {
    
    }
    
    /**
     * Called when mouse is in object's rectangle.
     */
    void onMouseOver() {
    
    }
    
    /**
     * Called when mouse left click is up.
     */
    void onMouseUp() {
    
    }
    
    /**
     * Called when mouse left click is down.
     */
    void onMouseDown() {
    
    }
    
    void onGainedFocus() {
    
    }
    
    void onLostFocus() {
    
    }
}
