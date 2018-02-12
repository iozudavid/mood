package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class UndecidedTile extends Tile {
	private static UndecidedTile instance = new UndecidedTile();

	private UndecidedTile() {
	}

	public static UndecidedTile getInstance() {
		return instance;
	}

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
