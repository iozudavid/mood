package com.knightlore.render.graphic;

import java.awt.image.BufferedImage;

public abstract class Graphic {

	protected int width, height;
	protected int[] pixels;

	public Graphic(BufferedImage img, int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
		img.getRGB(0, 0, width, height, pixels, 0, width);
	}

	public int[] getPixels() {
		return pixels;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
