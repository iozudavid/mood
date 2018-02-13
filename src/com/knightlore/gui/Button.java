package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.funcptrs.VoidFunction;

public class Button extends GUIObject {
    
    public Button(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    Button(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
    }

    private Graphic activeGraphic = null;
    
    private SelectState state = SelectState.UP;
    
    // no harm in changing these externally
    public Color upColour = Color.LIGHT_GRAY;
    public Color downColour = Color.DARK_GRAY;
    public Color hoverColour = Color.WHITE;
    
    public VoidFunction clickFunction;
    
    public Color activeColor() {
        switch (state) {
        case UP:
            return upColour;
        
        case HOVER:
            return hoverColour;
        
        case DOWN:
            return downColour;
        
        }
        return upColour;
    }
    
    
    
    @Override
    void Draw(Graphics g, Rectangle parentRect) {
        
        if (activeGraphic != null) {
            
        } else {
            g.setColor(activeColor());
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        
    }
    
    @Override
    void OnClick() {
        if (clickFunction == null) {
            return;
        }
        clickFunction.call();
    }
    
    @Override
    void onMouseEnter() {
        state = SelectState.HOVER;
    }
    
    void onMouseOver() {
        if (state == SelectState.UP) {
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
    
    @Override
    boolean isSelectable() {
        return true;
    }
    
}
