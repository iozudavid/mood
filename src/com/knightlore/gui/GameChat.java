package com.knightlore.gui;

import com.knightlore.render.PixelBuffer;

public class GameChat {

	private GUICanvas gui;
	private TextArea textArea;
	private TextField textField;
	private PixelBuffer pix;
	
	public GameChat(int screenWidth, int screenHeight){
		this.gui = new GUICanvas((int)(screenWidth*0.3),(int)(screenHeight*0.35));
		this.gui.init();
		this.pix = new PixelBuffer((int)(screenWidth*0.3),(int)(screenHeight*0.35));
		this.pix.flood(-16711936);
		this.textArea = new TextArea(0,0,(int)(screenWidth*0.3),(int)(screenHeight*0.3));
		this.textField = new TextField(0, (int)(this.textArea.getRectangle().getY()+this.textArea.getRectangle().getHeight()), (int)(screenWidth*0.3), (int)(screenHeight*0.05));
		this.textArea.addText("dsasad");
		this.textArea.addText("dsasadadsadsasdas");
		this.textArea.addText("dsasadadsadsasdasasdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
		this.gui.addGUIObject(this.textArea);
		this.gui.addGUIObject(this.textField);
		
	}
	
	public void render(){
		this.gui.render(pix,0,0);
	}
	
	public PixelBuffer getPixelBuffer(){
		return this.pix;
	}
	
	public TextArea getTextArea(){
		return this.textArea;
	}
	
}
