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

	private final BufferedImage img;
	private final int width, height;

	private final PixelBuffer mainPixelBuffer;
	private final int[] imagePixels;

	public Screen(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		mainPixelBuffer = new PixelBuffer(width, height);
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

		g.fillRect(0, 0, getWidth(), getHeight());
		renderable.render(mainPixelBuffer, x, y);

		mainPixelBuffer.copy(imagePixels);
		g.drawImage(getImage(), x, y, width, height, null);
		g.dispose();

		bs.show();
	}

	public BufferedImage getImage() {
		return img;
	}

	public PixelBuffer getMainPixelBuffer() {
		return mainPixelBuffer;
	}

}
