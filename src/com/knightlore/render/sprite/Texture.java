package com.knightlore.render.sprite;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
	
	public static final Texture EMPTY = null;

	public static final Texture AIR = EMPTY;
	public static final Texture BRICK = TextureSheet.TEXTURES.spriteAt(0, 0);
	public static final Texture BUSH = TextureSheet.TEXTURES.spriteAt(2, 0);
	
	private BufferedImage img;
	private int size;
	private int[] pixels;

	public Texture(String filename) {
		load(filename);
	}

	public Texture(BufferedImage img) {
		this.img = img;
		size = img.getWidth();
		pixels = new int[size * size];
		img.getRGB(0, 0, size, size, pixels, 0, size);
	}

	private void load(String filename) {
		try {
			img = ImageIO.read(new FileInputStream(filename));
			size = img.getWidth();
			pixels = new int[size * size];
			img.getRGB(0, 0, size, size, pixels, 0, size);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return img;
	}

	public int[] getPixels() {
		return pixels;
	}

	public int getSize() {
		return size;
	}

}
