package com.knightlore.gui;

import com.knightlore.render.PixelBuffer;

public class GameChat {

	private GUICanvas gui;
	private TextArea textArea;
	private TextField textField;
	private PixelBuffer pix;
	private int screenWidth;
	private int screenHeight;
	private int count=0;
	private boolean interactive=true;
	
	public GameChat(int screenWidth, int screenHeight){
		this.gui = new GUICanvas((int)(screenWidth*0.3),(int)(screenHeight*0.35));
		this.gui.init();
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.pix = new PixelBuffer((int)(screenWidth*0.3),(int)(screenHeight*0.35));
		this.pix.flood(-16711936);
		this.textArea = new TextArea(0,0,(int)(screenWidth*0.3),(int)(screenHeight*0.3));
		this.textField = new TextField(0, (int)(this.textArea.getRectangle().getY()+this.textArea.getRectangle().getHeight()), (int)(screenWidth*0.3), (int)(screenHeight*0.05));
		this.textField.setSelect(false);
		this.textArea.addText("System: press t for team chat");
		this.textArea.addText("System: press y for all chat");
		this.gui.addGUIObject(this.textArea);
		this.gui.addGUIObject(this.textField);
		
	}
	
	public void render(){
		if(GUICanvas.activeTextField!=null){
			this.interactive=true;
			count=0;
		} else if(count>100){
			this.interactive = false;
			count=0;
		} 
		if(this.interactive==true)
			count++;
		this.textArea.setActive(GUICanvas.activeTextField!=null);
		if(GUICanvas.activeTextField!=null)
			this.textArea.setInteractive(true);
		else
			this.textArea.setInteractive(this.interactive);
		this.gui.render(pix,0,0);
	}
	
	public PixelBuffer getPixelBuffer(){
		PixelBuffer copy = this.pix;
		this.pix = new PixelBuffer((int)(screenWidth*0.3),(int)(screenHeight*0.35));
		this.pix.flood(-16711936);
		return copy;
	}
	
	public TextArea getTextArea(){
		this.interactive=true;
		this.count=0;
		return this.textArea;
	}
	
}
