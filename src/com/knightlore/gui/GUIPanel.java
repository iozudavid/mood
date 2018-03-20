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
        sort();
    }
    
    // lower depths are first, therefore, draw in order.
    // deeper things are drawn first
    private void sort() {
        for (int i = 1; i < children.size(); i++) {
            GUIObject left = children.get(i);
            GUIObject right = children.get(i-1);
            // swap
            if (left.depth < right.depth) {
                children.set(i, right);
                children.set(i-1, left);
            }
        }
    }
    
    public void RemoveGUIObject(GUIObject obj) {
        children.add(obj);
    }
    
    @Override
    void Draw(Graphics g,Rectangle parentRect) {
        if(!isVisible) {
            return;
        }
        // because sorted low -> high depth
        // standard iteration will draw them in order. lowest first
        for (GUIObject aChildren : children) {
            aChildren.Draw(g, rect);
        }
    }
    
}
