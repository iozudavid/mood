package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.sprite.Texture;

// it's a singleton to avoid having multiple copies of air
public class AirTile extends Tile {
	private static AirTile instance = new AirTile();

	private AirTile() {
	}

	public static AirTile getInstance() {
		return instance;
	}

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
