package com.knightlore.render;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.knightlore.render.sprite.Texture;

/**
 * The Screen is the Swing component to which all game content is rendered to.
 * 
 * @author Joe Ellis
 *
 */
public class Screen extends Canvas {

	private final BufferedImage img;
	private final int width, height;
	private final int[] pixels, imagePixels;

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
	public void render(int x, int y, Renderable renderable) {

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

	public void drawGraphic(Texture texture, int x, int y) {
		for (int yy = 0; yy < texture.getSize(); yy++) {
			for (int xx = 0; xx < texture.getSize(); xx++) {
				int drawX = x + xx;
				int drawY = y + yy;
				if (drawX < 0 || drawX >= width || drawY < 0 || drawY >= height)
					continue;
				pixels[drawX + drawY * width] = texture.getPixels()[xx + yy * texture.getSize()];
			}
		}
	}

	public void fillPixel(int color, int x, int y) {
		fillRect(color, x, y, 1, 1);
	}

	public void fillRect(int color, int x, int y, int w, int h) {
		for (int yy = y; yy < y + h; yy++) {
			for (int xx = x; xx < x + w; xx++) {
				if (xx < 0 || xx >= width || yy < 0 || yy >= height)
					continue;
				pixels[xx + yy * width] = color;
			}
		}
	}

	public int mixColor(int color1, int color2, double mix) {
		Color c1 = new Color(color1);
		Color c2 = new Color(color2);

		int r = (int) (c1.getRed() * (1 - mix) + c2.getRed() * mix);
		int g = (int) (c1.getGreen() * (1 - mix) + c2.getGreen() * mix);
		int b = (int) (c1.getBlue() * (1 - mix) + c2.getBlue() * mix);
		Color result = new Color(r, g, b);
		return result.getRGB();
	}

	private void copy() {
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
