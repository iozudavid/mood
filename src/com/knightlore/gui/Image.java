package com.knightlore.gui;

import java.awt.Graphics;
import com.knightlore.render.sprite.Texture;

public class Image extends GUIObject{

	Image(int x, int y, int depth) {
		super(x, y, depth);
		// TODO Auto-generated constructor stub
	}

	public Texture texture;

	@Override
	void Draw(Graphics g) {
		
		g.drawImage(texture.getImage(), rect.x, rect.y, null);
		
	}

}
