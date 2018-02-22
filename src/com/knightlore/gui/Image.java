package com.knightlore.gui;

import java.awt.Graphics;

import com.knightlore.render.graphic.Graphic;

public class Image extends GUIObject {

	Image(int x, int y, int depth) {
		super(x, y, depth);
	}

	public Graphic graphic;

	@Override
	void Draw(Graphics g) {
	}

}
