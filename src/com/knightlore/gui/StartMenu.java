package com.knightlore.gui;

import com.knightlore.render.PixelBuffer;

public class StartMenu {

	private GUICanvas gui;
	private int screenHeight;
	private int screenWidth;
	private Image coverImage;
	private Image name; 
	private Button singlePlayerButton;
	private Button multiPlayerButton;
	private Button settingsButton;
	private Button quitButton;
	
	public StartMenu(int screenHeight, int screenWidth){
		this.gui = new GUICanvas();
		this.gui.init();
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		this.coverImage = new Image(0, 0, screenWidth, screenHeight, "res/graphics/knightlorecoverblur.png");
		this.name = new Image(middleWidth(500), calculateHeight(10), 500, 100, "res/graphics/logo.png");
		this.singlePlayerButton = new Button(middleWidth(300), calculateHeight(40), 300, 40, "Single Player",20);
		this.singlePlayerButton.setGraphic(new Image(0,0,0,0,"res/graphics/shotgun_to_right.png").graphic);
		this.singlePlayerButton.setGraphic2(new Image(0,0,0,0,"res/graphics/shotgun_to_left.png").graphic);
		this.multiPlayerButton = new Button(middleWidth(300), calculateHeight(50), 300, 40, "Multiplayer",20);
		this.multiPlayerButton.setGraphic(new Image(0,0,0,0,"res/graphics/multiplayer_to_right.png").graphic);
		this.multiPlayerButton.setGraphic2(new Image(0,0,0,0,"res/graphics/multiplayer_to_left.png").graphic);
		this.settingsButton = new Button(middleWidth(300), calculateHeight(60), 300, 40, "Settings",20);
		this.settingsButton.setGraphic(new Image(0,0,0,0,"res/graphics/settings_to_right.png").graphic);
		this.settingsButton.setGraphic2(new Image(0,0,0,0,"res/graphics/settings_to_left.png").graphic);
		this.quitButton = new Button(middleWidth(300), calculateHeight(70), 300, 40, "Quit",20);
		this.quitButton.setGraphic(new Image(0,0,0,0,"res/graphics/quit_to_right.png").graphic);
		this.quitButton.setGraphic2(new Image(0,0,0,0,"res/graphics/quit_to_left.png").graphic);
		gui.addGUIObject(coverImage);
		gui.addGUIObject(name);
		gui.addGUIObject(singlePlayerButton);
		gui.addGUIObject(multiPlayerButton);
		gui.addGUIObject(settingsButton);
		gui.addGUIObject(quitButton);
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
