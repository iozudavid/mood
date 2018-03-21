package com.knightlore.gui;

import com.knightlore.render.PixelBuffer;

public class InGameMenu {

	private GUICanvas gui;
	private PixelBuffer pix;
	
	public InGameMenu(int screenHeight, int screenWidth){
		this.gui = new GUICanvas((int)(screenWidth*0.25), (int)(screenHeight*0.25));
		this.pix = new PixelBuffer((int)(screenWidth*0.25),(int)(screenHeight*0.25));
		this.pix.flood(-16711936);
	}
	
	public void render(){
		this.gui.render(pix,0,0);
	}
	
	public PixelBuffer getPixelBuffer(){
		return this.pix;
	}
	
}
