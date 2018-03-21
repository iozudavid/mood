package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class CheckBox extends GUIObject{
    
    private boolean switcher;
    
    public CheckBox(int x, int y, int width, int height, int depth, boolean switcher) {
        super(x, y, width, height, depth);
        this.switcher = switcher;
    }

    @Override
    void Draw(Graphics g, Rectangle parentRect) {
        g.setColor(Color.BLACK);
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
        g.drawRect(rect.x+1, rect.y+1, rect.width-1, rect.height-1);
        g.drawRect(rect.x+2, rect.y+2, rect.width-2, rect.height-2);
        if(switcher){
           g.setColor(Color.BLACK);
           //also make a bold tick
           g.drawLine(rect.x, rect.y+(int)((double)((double)rect.height/2D)), rect.x+(int)((double)((double)rect.width/2D)), rect.y+rect.height);
           g.drawLine(rect.x+(int)((double)((double)rect.width/2D)), rect.y+rect.height, rect.x+rect.width, rect.y);
           g.drawLine(rect.x, rect.y+(int)((double)((double)rect.height/2D))-1, rect.x+(int)((double)((double)rect.width/2D))+1, rect.y+rect.height);
           g.drawLine(rect.x+(int)((double)((double)rect.width/2D))+1, rect.y+rect.height, rect.x+rect.width, rect.y);
           g.drawLine(rect.x, rect.y+(int)((double)((double)rect.height/2D))+1, rect.x+(int)((double)((double)rect.width/2D))-1, rect.y+rect.height);
           g.drawLine(rect.x+(int)((double)((double)rect.width/2D))-1, rect.y+rect.height, rect.x+rect.width, rect.y);
           g.drawLine(rect.x, rect.y+(int)((double)((double)rect.height/2D))-2, rect.x+(int)((double)((double)rect.width/2D))+2, rect.y+rect.height);
           g.drawLine(rect.x+(int)((double)((double)rect.width/2D))+2, rect.y+rect.height, rect.x+rect.width, rect.y);
           g.drawLine(rect.x, rect.y+(int)((double)((double)rect.height/2D))+2, rect.x+(int)((double)((double)rect.width/2D))-2, rect.y+rect.height);
           g.drawLine(rect.x+(int)((double)((double)rect.width/2D))-2, rect.y+rect.height, rect.x+rect.width, rect.y);
           
        }
    }
    
    @Override
    boolean isSelectable() {
        return true;
    }
    
    public void setBobingMode(boolean b){
        this.switcher = b;
    }
    
    @Override
    void OnClick() {
        this.switcher = !this.switcher;
    }
    
    public boolean getBobingMode(){
        return this.switcher;
    }
    
}
