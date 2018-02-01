package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.sprite.Texture;

public class UndecidedTile extends Tile {
	private static UndecidedTile instance = new UndecidedTile();

	private UndecidedTile() {
	}

	public static UndecidedTile getInstance() {
		return instance;
	}

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
