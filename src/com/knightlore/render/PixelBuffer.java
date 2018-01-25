package com.knightlore.render;

import com.knightlore.render.sprite.Texture;

public class PixelBuffer {

	private final int WIDTH, HEIGHT;
	private int[] pixels;

	public PixelBuffer(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		pixels = new int[WIDTH * HEIGHT];
	}

	public void drawGraphic(Texture texture, int x, int y) {
		for (int yy = 0; yy < texture.getSize(); yy++) {
			for (int xx = 0; xx < texture.getSize(); xx++) {
				int drawX = x + xx;
				int drawY = y + yy;
				if (drawX < 0 || drawX >= WIDTH || drawY < 0 || drawY >= HEIGHT)
					continue;
				pixels[drawX + drawY * WIDTH] = texture.getPixels()[xx + yy * texture.getSize()];
			}
		}
	}
	
	public void copy(int[] c) {
		for(int i = 0; i < WIDTH * HEIGHT; i++) {
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
