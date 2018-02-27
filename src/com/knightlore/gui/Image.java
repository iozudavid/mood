package com.knightlore.gui;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.knightlore.render.graphic.Graphic;

public class Image extends GUIObject {
    public Graphic graphic;


    public Image(int x, int y, int width, int height, int depth) {
        super(x,y,width,height,depth);
    }

    public Image(int x, int y, int width, int height) {
        super(x,y,width,height);
    }


	@Override
	void Draw(Graphics g, Rectangle parentRect) {

	}
}
