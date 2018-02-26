package com.knightlore.gui;

import java.awt.Dimension;

import com.knightlore.render.PixelBuffer;

public class StartMenu {

	private GUICanvas gui;
	private double height;
	private double width;
	private Image img = new Image(0, 0, 100, "res/graphics/knightlorecoverblur.png");
	
	public StartMenu(Dimension dimension){
		this.gui = new GUICanvas();
		this.height=dimension.getHeight();
		this.width=dimension.getWidth();
	}
	
	public void render(PixelBuffer pix, int x, int y){
		img.rect.width=pix.getWidth();
		img.rect.height=pix.getHeight();
		Button b = new Button(5, 5, 0);
        b.rect.width = 100;
        b.rect.height = 30;
        TextField tf = new TextField(100, 100, 0, "Sample Text");
        tf.rect.width = pix.getWidth();
        tf.rect.height = pix.getHeight();
       // gui.addGUIObject(tf);
    //    gui.addGUIObject(b);
	//	gui.addGUIObject(img);
		//gui.render(pix, x, y);
        pix.drawGraphic(img.graphic, 0, 0);
	}
		
}
