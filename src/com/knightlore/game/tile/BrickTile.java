package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.sprite.Texture;

public class BrickTile extends Tile {

	@Override
	public Texture getTexture() {
		return Texture.BRICK;
	}

	@Override
	public double getSolidity() {
		return 0.95D;
	}

	@Override
	public void onShot() {
		System.out.println("shot brick");
	}

	@Override
	public void onTouch(Camera c) {
		System.out.println("touched brick");
	}

	@Override
	public char toChar(){
		return 'B';
	}
	
}
