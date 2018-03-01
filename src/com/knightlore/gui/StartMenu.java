package com.knightlore.gui;

import com.knightlore.render.PixelBuffer;

public class StartMenu {

	private GUICanvas gui;
	private int screenHeight;
	private int screenWidth;
	private Image coverImage;
	private Image name; 
	private Button singlePlayerButton;
	private Text singlePlayerText;
	
	public StartMenu(int screenHeight, int screenWidth){
		this.gui = new GUICanvas();
		this.gui.init();
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		this.coverImage = new Image(0, 0, screenWidth, screenHeight, "res/graphics/knightlorecoverblur.png");
		this.name = new Image(middleWidth(500), calculateHeight(10), 500, 100, "res/graphics/logo.png");
		this.singlePlayerButton = new Button(middleWidth(250), calculateHeight(35), 250, 40, this.singlePlayerText);
		this.singlePlayerText = new Text(middleWidth(250), calculateHeight(35), 250, 40, "Single player");
		gui.addGUIObject(coverImage);
		gui.addGUIObject(name);
		gui.addGUIObject(singlePlayerButton);
		gui.addGUIObject(singlePlayerText);
	}
	

	public void render(PixelBuffer pix, int x, int y){
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
