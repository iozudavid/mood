package com.knightlore.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;

public class StartMenu {

	private GUICanvas gui;
	private double height;
	private double width;
	private Image coverImage = new Image(0, 0, (int)width, (int)height, "res/graphics/knightlorecoverblur.png");
	private Image name = new Image(0, 0, 500, 100, "res/graphics/logo.png");
	
	public StartMenu(Dimension dimension){
		this.gui = new GUICanvas();
		this.height=dimension.getHeight();
		this.width=dimension.getWidth();
	}
	
	public void render(PixelBuffer pix, int x, int y){
		BufferedImage combined = new BufferedImage(pix.getWidth(), pix.getHeight(), BufferedImage.TYPE_INT_ARGB);

		// paint both images
		Graphics g = combined.getGraphics();
		g.drawImage(coverImage.sheet, 0, 0, null);
		int start = (int)(pix.getWidth() - name.getRectangle().getWidth());
		start /= 2;
		System.out.println(start);
		BufferedImage resizedImage = resize(name.sheet, (int)name.getRectangle().getWidth(), (int)name.getRectangle().getHeight());
		g.drawImage(resizedImage, start, (int)(0.1*pix.getHeight()), null);
		
		pix.drawGraphic(new Graphic(combined), 0, 0);
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    java.awt.Image tmp = img.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
		
}
