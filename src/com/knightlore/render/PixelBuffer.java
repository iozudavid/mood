package com.knightlore.render;

import com.knightlore.render.graphic.Graphic;

public class PixelBuffer {

	private final int WIDTH, HEIGHT;
	private int[] pixels;

	public PixelBuffer(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		pixels = new int[WIDTH * HEIGHT];
	}

	public void drawGraphic(Graphic graphic, int x, int y) {
		for (int yy = 0; yy < graphic.getHeight(); yy++) {
			for (int xx = 0; xx < graphic.getWidth(); xx++) {
				int drawX = x + xx;
				int drawY = y + yy;
				if (drawX < 0 || drawX >= WIDTH || drawY < 0 || drawY >= HEIGHT)
					continue;
				pixels[drawX + drawY * WIDTH] = graphic.getPixels()[xx + yy * graphic.getWidth()];
			}
		}
	}

	public void copy(int[] c) {
		for (int i = 0; i < WIDTH * HEIGHT; i++) {
			c[i] = pixels[i];
		}
	}

	public void fillPixel(int color, int x, int y) {
		fillRect(color, x, y, 1, 1);
	}

	public void fillRect(int color, int x, int y, int w, int h) {
		for (int yy = y; yy < y + h; yy++) {
			for (int xx = x; xx < x + w; xx++) {
				if (xx < 0 || xx >= WIDTH || yy < 0 || yy >= HEIGHT)
					continue;
				pixels[xx + yy * WIDTH] = color;
			}
		}
	}

	public int pixelAt(int x, int y) {
		int i = x + y * WIDTH;
		if (i < 0 || i >= pixels.length)
			return 0;
		return pixels[x + y * WIDTH];
	}

	public int[] getPixels() {
		return pixels;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

}
