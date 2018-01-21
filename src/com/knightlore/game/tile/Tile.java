package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.sprite.Texture;

public abstract class Tile {

	public abstract Texture getTexture();

	public double getSolidity() {
		return 1D;
	}

	public abstract void onShot();

	public abstract void onTouch(Camera c/* Player p */);

	public char toChar(){
		return ' ';
	}
	
	public Tile reflectTileX(){
		return this;
	}
	
	public Tile reflectTileY(){
		return this;
	}
	
}
