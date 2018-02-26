package com.knightlore.gui;

import java.awt.Graphics;

import com.knightlore.render.graphic.Graphic;

public class Image extends GUIObject {
    public Graphic graphic;

	Image(int x, int y, int depth) {
		super(x, y, depth);
	}

	@Override
	void Draw(Graphics g) {
	}
}
