package com.knightlore.gui;

import com.knightlore.render.PixelBuffer;

public class GameChat {

	private GUICanvas gui;
	private TextArea textArea;
	private TextField textField;
	private PixelBuffer pix;
	
	public GameChat(int screenWidth, int screenHeight){
		this.gui = new GUICanvas();
		this.gui.init();
		this.pix = new PixelBuffer(screenWidth, screenHeight);
		this.textField = new TextField(0, 0, (int)(screenWidth*0.3), 100);
		this.textArea = new TextArea(0,this.textField.getRectangle().height,(int)(screenWidth*0.3),(int)(screenHeight*0.3));
		this.gui.addGUIObject(this.textField);
		this.gui.addGUIObject(this.textArea);
	}
	
	public void render(PixelBuffer pix, int x, int y){
		this.gui.render(pix,x,y);
	}
	
	public PixelBuffer getPixelBuffer(){
		return this.pix;
	}
	
}
