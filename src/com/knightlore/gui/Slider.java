package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Slider extends GUIObject{
    
    private Rectangle sliderPos;
    private final float defaultValue = 0.8f;
    
    public Slider(int x, int y, int width, int height, int depth){
        super(x,y,width,height,depth);
        System.out.println(((double)defaultValue*(double)(x+width)));
        this.sliderPos = new Rectangle((int)((double)(x-height)/2D), (int)(y+((double)defaultValue*(double)(width))), 5, height+5);
    }
    
    public Slider(int x, int y, int width, int height){
        this(x,y,width,height,0);
    }

    @Override
    void Draw(Graphics g, Rectangle parentRect) {
        g.setColor(Color.WHITE);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.fillRect(sliderPos.x, sliderPos.y, sliderPos.width, sliderPos.height);
    }
}
