package com.knightlore.assets;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SpriteSheetMaker {

	static String baseFileName;
	static String mode;
	
	static int width;
	static int height;
	
	static Color alphaColor = Color.green;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 3) {
			// read operation mode
			mode = args[0];
			// calc dimensions
			String[] dimString = args[1].split("x");
			width = Integer.parseInt(dimString[0]);
			height = Integer.parseInt(dimString[1]);
			// get file name
			baseFileName = args[2];
			switch (mode) {
			case "-static":
				createStatic();
				break;

			case "-animated":

				break;
			}
		}
		else{
			System.out.println("expected 3 arguments, got "+args.length);
			System.out.println("<mode> <dimensions> <name>");
		}

	}

	static final int NUM_DIRECTIONS = 8;

	static void createStatic() {

		int angle = 360 / NUM_DIRECTIONS;
		
		int totalWidth = width;
		int totalHeight = height*NUM_DIRECTIONS;
		
		BufferedImage outImg = new BufferedImage(totalWidth,totalHeight,BufferedImage.TYPE_INT_ARGB);
		
		
		// get graphics setup for fast copying
		Graphics2D g2d = outImg.createGraphics();
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		g2d.setComposite(alpha);
		
		// load in images, and output into buffer 
		BufferedImage img = null;
		String fileName = null;
		for (int i = 0; i < NUM_DIRECTIONS; i++) {
			// get file name
			fileName = baseFileName + "_" + (angle * i) + ".png";
			File file = new File(fileName);
			// try to read the image
			try {
				img = ImageIO.read(file);
			} catch (Exception e) {
				System.err.println(e);
				System.out.println("Failed to read images, see above error ^");
				return;
			}
			
			// wipe alpha out of the image
			for(int x=0;x<width;x++){
				for(int y=0;y<height;y++){
					int col = img.getRGB(x, y);
					int r = (col & 0x00ff0000) >> 16;
					int g = (col & 0x0000ff00) >> 8;
					int b = (col & 0x000000ff);
					// set alpha
					if(r == 0 && g == 255 && b == 0){
						img.setRGB(x, y, 0x00ffffff);
					}
				}
			}
			
			// calc position
			int yPos = i*height;
			// splat image outside
			g2d.drawImage(img, 0, yPos, null);
			System.out.println("Copied "+fileName);

		}
		
		
		String outFileName = baseFileName + "_sprites.png";
		File outFile = new File(outFileName);
		
		try {
			ImageIO.write(outImg, "png", outFile);
		} catch (Exception e) {
			System.err.println(e);
			System.out.println("Failed to write final sprite sheet");
		}finally{
			g2d.dispose();	
		}
		
		System.out.println("Spritesheet written to: "+outFileName);
		System.out.println("Done.");
	}

}
