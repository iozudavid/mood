package com.knightlore.render;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * The Screen is the Swing component to which all game content is rendered to.
 * 
 * @author Joe Ellis
 *
 */
public class Screen extends Canvas {

	private BufferedImage img;

	private final int width, height;
	private int[] pixels, imagePixels;

	public Screen(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = new int[width * height];
		imagePixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
	}

	/**
	 * Render the scene.
	 */
	public void render(int x, int y, IRenderable renderable) {

		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		renderable.render(this, x, y);

		copy();
		g.drawImage(getImage(), x, y, width, height, null);
		g.dispose();

		bs.show();
	}

	public void fillRect(int color, int x, int y, int w, int h) {
		for (int yy = y; yy < y + h; yy++) {
			for (int xx = x; xx < x + w; xx++) {
				if (xx < 0 || xx >= this.width || yy < 0 || yy >= this.height)
					continue;
				pixels[xx + yy * this.width] = color;
			}
		}
	}

	public void copy() {
		for (int i = 0; i < width * height; i++) {
			imagePixels[i] = pixels[i];
		}
	}

	public BufferedImage getImage() {
		return img;
	}

	public int[] getPixels() {
		return pixels;
	}

}
