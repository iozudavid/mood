package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.Texture;

public class ExpectRoom extends Tile{

	private char direction;
	
	public static final char RIGHT = 'r';
	public static final char DOWN  = 'd';
	public static final char BOTH  = 'b';
	
	public ExpectRoom(char c){
		assert(c == RIGHT || c == DOWN || c == BOTH);
		direction = c;
	}
	
	@Override
	public Graphic getTexture() {
		return Texture.AIR;
	}

	@Override
	public void onShot() {
	}

	@Override
	public void onTouch(Camera c) {
	}

	public char toChar(){
		return direction;
	}
	
}
