package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.Texture;

public class UndecidedTile extends Tile {

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

	@Override
	public char toChar() {
		return '?';
	}

}
