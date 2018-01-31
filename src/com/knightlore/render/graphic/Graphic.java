package com.knightlore.render.graphic;

import java.awt.image.BufferedImage;

public class Graphic {

	public static final Graphic EMPTY = null;

	protected int width, height;
	protected int[] pixels;

	public Graphic(BufferedImage img) {
		this.width = img.getWidth();
		this.height = img.getHeight();
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

	public int getSize() {
		return Math.max(width, height);
	}

}
