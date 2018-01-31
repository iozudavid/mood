package com.knightlore.game.tile;

import com.knightlore.game.Player;
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
	public void onEntered(Player p) {
	}

	@Override
	public char toChar() {
		return '?';
	}

}
