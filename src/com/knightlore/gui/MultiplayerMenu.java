package com.knightlore.gui;

import com.knightlore.render.PixelBuffer;

public class MultiplayerMenu {
	
	private GUICanvas gui;
	private final int screenHeight;
	private final int screenWidth;
	private Image coverImage;
	private Text ipText;
	private TextField ipTextField;

	public MultiplayerMenu(int screenHeight, int screenWidth){
		this.gui = new GUICanvas();	
		this.gui.init();
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		this.coverImage = new Image(0, 0, this.screenWidth, this.screenHeight, "res/graphics/mppadjusted.png");
		this.ipText = new Text(GuiUtils.middleWidth(this.screenWidth, 100), GuiUtils.calculateHeight(this.screenHeight, 25), 100, 250, "Ip address", 20);
		this.ipTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300), GuiUtils.calculateHeight(this.screenHeight, 32), 300, 40, "localhost");
		this.gui.addGUIObject(this.coverImage);
		this.gui.addGUIObject(this.ipText);
		this.gui.addGUIObject(this.ipTextField);
	}
	
	public void render(PixelBuffer pix, int x, int y){
		this.gui.render(pix, x, y);
	}
	
	
	
}
