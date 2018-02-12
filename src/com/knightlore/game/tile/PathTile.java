package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.sprite.Texture;

public class PathTile extends Tile{

	// Tile used exclusively in procedural generation
	public Texture getTexture() {
		return Texture.AIR;
	}

	public void onShot() {
	}

	public void onTouch(Camera c) {
	}
	
	public char toChar() {
		return '-';
	}

}
