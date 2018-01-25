package com.knightlore.render.graphic;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GraphicSheet {

	public final static GraphicSheet TEXTURES = new GraphicSheet("res/graphics/textures.png", 16);

	private final int cellSize;
	private BufferedImage sheet;

	public GraphicSheet(String path, int cellSize) {
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

	public Texture graphicAt(int x, int y) {
		return graphicAt(x, y, 1, 1);
	}

	public Texture graphicAt(int x, int y, int xx, int yy) {
		BufferedImage subImg = sheet.getSubimage(x * cellSize, y * cellSize, xx * cellSize, yy * cellSize);
		return new Texture(subImg);
	}

}
