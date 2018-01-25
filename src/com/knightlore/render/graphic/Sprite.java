package com.knightlore.render.graphic;

import java.awt.image.BufferedImage;

public class Sprite extends Graphic {

	public Sprite(BufferedImage img) {
		super(img, img.getWidth(), img.getHeight());
	}

}
