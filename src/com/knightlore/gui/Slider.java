package com.knightlore.gui;

import java.awt.Color;
import java.awt.Rectangle;

import com.knightlore.engine.input.InputManager;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.Vector2D;

/**
 * Class used to create a slider useful to adjust values.
 *
 * @author David Iozu
 */
public class Slider extends GUIObject {
    
    public final Color upColour = Color.GRAY;
    public final Color hoverColour = Color.DARK_GRAY;
    public final Color downColour = Color.BLACK;
    
    private Rectangle sliderPos;
    private float defaultValue = 0.8f;
    private float actualValue = defaultValue;
    private SelectState state = SelectState.UP;
    private boolean isClicked = false;
    
    public Slider(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
        this.sliderPos = new Rectangle((int)(x + ((double)defaultValue * (double)(width))),
                (int)(y - (double)(height) / 2D), 5, height * 2);
    }
    
    public Slider(int x, int y, int width, int height) {
        this(x, y, width, height, 0);
    }
    
    public Slider(int x, int y, int width, int height, int depth, float val) {
        super(x, y, width, height, depth);
        this.defaultValue = val;
        this.actualValue = this.defaultValue;
        this.sliderPos = new Rectangle((int)(x + ((double)defaultValue * (double)(width))),
                (int)(y - (double)(height) / 2D), 5, height * 2);
    }
    
    /**
     * @return the appropriate color for the current state.
     */
    public Color activeColor() {
        switch (state) {
            case UP:
                return upColour;
            
            case HOVER:
                return hoverColour;
            
            case DOWN:
                return downColour;
            
            default:
                return upColour;
        }
        
    }
    
    @Override
    boolean isSelectable() {
        return true;
    }
    
    /**
     * Draw the slider on the current position.
     */
    @Override
    void Draw(PixelBuffer pix, int x, int y) {
        int color = Color.DARK_GRAY.getRGB();
        pix.fillRect(color, rect.x, rect.y, rect.width, rect.height);
        color = this.activeColor().getRGB();
        this.sliderPos = new Rectangle((int)(rect.x + ((double)actualValue * (double)(rect.width))),
                (int)sliderPos.getY(), (int)sliderPos.getWidth(), (int)sliderPos.getHeight());
        pix.fillRect(color, sliderPos.x, sliderPos.y, sliderPos.width, sliderPos.height);
    }
    
    /**
     * Recalculate the slider position.
     */
    @Override
    void OnClick() {
        this.isClicked = true;
        this.calculatePosition();
    }
    
    /**
     * Change the state to hover.
     */
    @Override
    void onMouseEnter() {
        this.isClicked = false;
        state = SelectState.HOVER;
    }
    
    /**
     * If slider clicked, rerender the slider on the new position.
     */
    void onMouseOver() {
        if (this.isClicked) {
            this.calculatePosition();
        }
        state = SelectState.HOVER;
    }
    
    /**
     * Change the state to up.
     */
    @Override
    void OnMouseExit() {
        this.isClicked = false;
        state = SelectState.UP;
    }
    
    /**
     * Change the state to down.
     */
    @Override
    void onMouseDown() {
        this.isClicked = true;
        state = SelectState.DOWN;
    }
    
    /**
     * Change the state to up.
     */
    @Override
    void onMouseUp() {
        this.isClicked = false;
        state = SelectState.UP;
    }
    
    /**
     * Takes the mouse position and recalculate the slider position depending on
     * the mouse position
     */
    private void calculatePosition() {
        Vector2D mousePos = InputManager.getMousePos();
        double mouseXPos = mousePos.getX();
        if (mouseXPos < this.rect.getX()) {
            this.actualValue = 0f;
        } else if (mouseXPos > this.rect.getX() + this.rect.getWidth()) {
            this.actualValue = 1f;
        } else {
            float distanceFromStart = (float)(mouseXPos - this.rect.getX());
            float allDistance = (float)(this.rect.getWidth());
            this.actualValue = distanceFromStart / allDistance;
        }
    }
    
    public float getValue() {
        return this.actualValue;
    }
    
    /**
     * Set a new value for the slider.
     *
     * @param v - value to be set
     */
    public void setValue(float v) {
        this.actualValue = v;
    }
    
}
