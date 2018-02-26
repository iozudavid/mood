package com.knightlore.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.knightlore.render.graphic.Graphic;

public class Image extends GUIObject{

	public Graphic graphic;
	private BufferedImage sheet;
	
	Image(int x, int y, int depth, String path) {
		super(x, y, depth);
		try {
			this.sheet = ImageIO.read(new FileInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.graphic = new Graphic(sheet);
	}


	@Override
	void Draw(Graphics g) {
	//	g.fillRect(rect.x, rect.y, rect.width, rect.height);
		g.drawImage(sheet, rect.x, rect.y, rect.width, rect.height, null);
	}

}
