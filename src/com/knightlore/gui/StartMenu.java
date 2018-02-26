package com.knightlore.gui;

import java.awt.image.BufferedImage;

import com.knightlore.render.PixelBuffer;

public class StartMenu {

	private GUICanvas gui;
	private int screenHeight;
	private int screenWidth;
	private Image coverImage;
	private Image name; 
	
	public StartMenu(int screenHeight, int screenWidth){
		this.gui = new GUICanvas();
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		coverImage = new Image(0, 0, screenWidth, screenHeight, "res/graphics/knightlorecoverblur.png");
		this.name = new Image(middleWidth(500), calculateHeight(10), 500, 100, "res/graphics/logo.png");
	}
	

	public void render(PixelBuffer pix, int x, int y){

		Button singlePlayer = new Button(middleWidth(250), calculateHeight(35), 250, 40);
		
		gui.addGUIObject(coverImage);
		gui.addGUIObject(name);
		gui.addGUIObject(singlePlayer);
		gui.render(pix, x, y);
	}
	
	public int middleWidth(int objWidth){
		int middle = this.screenWidth - objWidth;
		return middle/2;
	}
	
	public int calculateHeight(int procent){
		double decimal = (double)procent/(double)100;
		return (int)(decimal*this.screenHeight);
	}
		
}
