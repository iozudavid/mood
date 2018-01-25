package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.sprite.Texture;

public class BushTile extends Tile {

	@Override
	public Texture getTexture() {
		return Texture.BUSH;
	}

	@Override
	public double getOpacity() {
		return 1D;
	}

	@Override
	public void onShot() {
	}

	@Override
	public void onTouch(Camera c) {
	}

}
