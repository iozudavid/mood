package com.knightlore.gui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class GUIPanel extends GUIObject {

    private List<GUIObject> children;
    
    public GUIPanel(int x, int y, int width, int height, int depth) {
        super(x,y,width,height,depth);
        children = new ArrayList<>();
    }

    public GUIPanel(int x, int y, int width, int height) {
        super(x,y,width,height);
        children = new ArrayList<>();
    }
    
    public void AddGUIObject(GUIObject obj) {
        children.add(obj);
    }
    
    public void RemoveGUIObject(GUIObject obj) {
        children.add(obj);
    }
    
    @Override
    void Draw(Graphics g,Rectangle parentRect) {
        if(!isVisible) {
            return;
        }
    }
    
}
