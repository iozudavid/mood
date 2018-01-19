package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.sprite.Texture;

public class UndecidedTile extends Tile{

	@Override
	public Texture getTexture() {
		return Texture.AIR;
	}

	@Override
	public void onShot() {		
	}

	@Override
	public void onTouch(Camera c) {		
	}

	@Override
	public char toChar(){
		return '?';
	}
	
}
