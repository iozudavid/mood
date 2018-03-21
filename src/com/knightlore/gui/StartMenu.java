package com.knightlore.gui;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.StartServer;
import com.knightlore.network.ConnectionDetails;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.funcptrs.VoidFunction;

public class StartMenu {

	private GUICanvas gui;
	private final int screenHeight;
	private final int screenWidth;
	private Image coverImage;
	private Image name; 
	private Button singlePlayerButton;
	private Button multiPlayerButton;
	private Button settingsButton;
	private Button quitButton;
	
	public StartMenu(int screenHeight, int screenWidth){
		this.gui = new GUICanvas(screenWidth, screenHeight);
		this.gui.init();
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		this.coverImage = new Image(0, 0, screenWidth, screenHeight, "res/graphics/knightlorecoverblur.png");
		this.name = new Image(GuiUtils.middleWidth(this.screenWidth, 500), GuiUtils.calculateHeight(this.screenHeight, 10), 500, 100, "res/graphics/logo.png");
		this.singlePlayerButton = new Button(GuiUtils.middleWidth(this.screenWidth, 300), GuiUtils.calculateHeight(this.screenHeight, 40), 300, 40, "Single Player",20);
		this.singlePlayerButton.setGraphic(new Image(0,0,0,0,"res/graphics/shotgun_to_right.png").graphic);
		this.singlePlayerButton.setGraphic2(new Image(0,0,0,0,"res/graphics/shotgun_to_left.png").graphic);
		this.multiPlayerButton = new Button(GuiUtils.middleWidth(this.screenWidth, 300), GuiUtils.calculateHeight(this.screenHeight, 50), 300, 40, "Multiplayer",20);
		this.multiPlayerButton.setGraphic(new Image(0,0,0,0,"res/graphics/multiplayer_to_right.png").graphic);
		this.multiPlayerButton.setGraphic2(new Image(0,0,0,0,"res/graphics/multiplayer_to_left.png").graphic);
		this.settingsButton = new Button(GuiUtils.middleWidth(this.screenWidth, 300), GuiUtils.calculateHeight(this.screenHeight, 60), 300, 40, "Settings",20);
		this.settingsButton.setGraphic(new Image(0,0,0,0,"res/graphics/settings_to_right.png").graphic);
		this.settingsButton.setGraphic2(new Image(0,0,0,0,"res/graphics/settings_to_left.png").graphic);
		this.quitButton = new Button(GuiUtils.middleWidth(this.screenWidth, 300), GuiUtils.calculateHeight(this.screenHeight, 70), 300, 40, "Quit",20);
		this.quitButton.setGraphic(new Image(0,0,0,0,"res/graphics/quit_to_right.png").graphic);
		this.quitButton.setGraphic2(new Image(0,0,0,0,"res/graphics/quit_to_left.png").graphic);
		
		this.singlePlayerButton.clickFunction = new VoidFunction() {
			
			@Override
			public void call() {
				//start new session
				//on the local machine
				ConnectionDetails.SERVER_HOSTNAME = "localhost";
				ConnectionDetails.PORT=5000;
				//remove this gui
				StartMenu.this.gui.destroy();
				GameEngine.getSingleton().startGame();
			}
		};
		
		this.settingsButton.clickFunction = new VoidFunction() {
            
            @Override
            public void call() {
                StartMenu.this.gui.destroy();
                GameEngine.getSingleton().guiState = GUIState.SettingsMenu;
            }
        };
		
		this.multiPlayerButton.clickFunction = new VoidFunction(){
			@Override
			public void call(){
				StartMenu.this.gui.destroy();
				GameEngine.getSingleton().guiState = GUIState.MultiplayerMenu;
			}
		};
		
		this.quitButton.clickFunction = new VoidFunction() {
			
			@Override
			public void call() {
				GameEngine.getSingleton().stop();
			}
		};
		
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
		
}
