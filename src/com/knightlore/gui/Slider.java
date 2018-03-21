package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.knightlore.engine.input.InputManager;
import com.knightlore.utils.Vector2D;

public class Slider extends GUIObject {

    public Color upColour = Color.GRAY;
    public Color hoverColour = Color.DARK_GRAY;
    public Color downColour = Color.BLACK;

    private Rectangle sliderPos;
    private final float defaultValue = 0.8f;
    private float actualValue = defaultValue;
    private SelectState state = SelectState.UP;
    private boolean isClicked=false;

    public Slider(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
        System.out.println(((double) defaultValue * (double) (x + width)));
        this.sliderPos = new Rectangle((int) (x + ((double) defaultValue * (double) (width))),
                (int) (y - (double) (height) / 2D), 5, height * 2);
    }

    public Slider(int x, int y, int width, int height) {
        this(x, y, width, height, 0);
    }

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

    @Override
    void Draw(Graphics g, Rectangle parentRect) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(this.activeColor());
        this.sliderPos = new Rectangle((int) (rect.x + ((double) actualValue * (double) (rect.width))),
                (int)sliderPos.getY(), (int)sliderPos.getWidth(), (int)sliderPos.getHeight());
        g.fillRect(sliderPos.x, sliderPos.y, sliderPos.width, sliderPos.height);
    }

    @Override
    void OnClick() {
        this.isClicked = true;
        this.calculatePosition();
    }

    @Override
    void onMouseEnter() {
        this.isClicked = false;
        state = SelectState.HOVER;
    }

    void onMouseOver() {
        if(this.isClicked){
            this.calculatePosition();
        }
        state = SelectState.HOVER;
    }

    @Override
    void OnMouseExit() {
        this.isClicked = false;
        state = SelectState.UP;
    }

    @Override
    void onMouseDown() {
        this.isClicked = true;
        state = SelectState.DOWN;
    }

    @Override
    void onMouseUp() {
        this.isClicked = false;
        state = SelectState.UP;
    }
    
    private void calculatePosition(){
        Vector2D mousePos = InputManager.getMousePos();
        double mouseXPos = mousePos.getX();
        if(mouseXPos<this.rect.getX())
            this.actualValue = 0f;
        else if(mouseXPos>this.rect.getX()+this.rect.getWidth())
            this.actualValue = 1f;
        else{
            float distanceFromStart = (float)(mouseXPos - this.rect.getX());
            float allDistance = (float)(this.rect.getWidth());
            float procent = (float) (distanceFromStart/allDistance);
            this.actualValue = procent;
        }
    }
    
    public float getValue(){
        return this.actualValue;
    }
    
}
