package com.knightlore.render.graphic;

import java.awt.image.BufferedImage;

public class Texture extends Graphic {

	public static final Texture EMPTY = null;

	public static final Texture AIR = EMPTY;
	public static final Texture BRICK = GraphicSheet.TEXTURES.graphicAt(0, 0);
	public static final Texture BUSH = GraphicSheet.TEXTURES.graphicAt(1, 0);

	public Texture(BufferedImage img) {
		super(img, img.getWidth(), img.getHeight());
	}

	public int getSize() {
		assert width == height; // TODO
		return width;
	}

}
