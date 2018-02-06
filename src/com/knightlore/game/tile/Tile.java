package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;

public abstract class Tile {

	public abstract Graphic getTexture();

	public double getOpacity() {
		return 1D;
	}

	public double getSolidity() {
		return 1D;
	}
	
	public int getMinimapColor() {
		return 0;
	}

	public abstract void onShot();

	public abstract void onEntered(Player player);
	
	public char toChar() {
		return ' ';
	}

	public Tile reflectTileX() {
		return this;
	}

	public Tile reflectTileY() {
		return this;
	}

}
