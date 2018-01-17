package com.knightlore.render.sprite;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TextureSheet {

	public final static TextureSheet TEXTURES = new TextureSheet("res/graphics/textures.png", 16);

	private final String path;
	private final int cellSize;
	private BufferedImage sheet;

	public TextureSheet(String path, int cellSize) {
		this.path = path;
		this.cellSize = cellSize;
		load(path);
	}

	private void load(String path) {
		try {
			sheet = ImageIO.read(new FileInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Texture spriteAt(int x, int y) {
		BufferedImage subImg = sheet.getSubimage(x * cellSize, y * cellSize, cellSize, cellSize);
		return new Texture(subImg);
	}

}
