package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.sprite.Texture;

public class AirTile extends Tile {

	@Override
	public Texture getTexture() {
		return Texture.AIR;
	}
	
	@Override
	public double getSolidity() {
		return 0D;
	}

	@Override
	public void onShot() {
	}

	@Override
	public void onTouch(Camera c) {
	}

}
