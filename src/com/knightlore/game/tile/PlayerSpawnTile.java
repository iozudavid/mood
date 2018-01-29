package com.knightlore.game.tile;

import com.knightlore.engine.GameEngine;
import com.knightlore.render.Camera;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.Texture;

public class PlayerSpawnTile extends Tile {

	private int teamNum = 0;

	public PlayerSpawnTile(int team) {
		teamNum = team;
	}

	@Override
	public Graphic getTexture() {
		return Texture.BUSH;
	}

	@Override
	public double getOpacity() {
		double opacity = 0.5 + (Math.sin(GameEngine.ticker.getTime() * 0.05)) / 4;
		return opacity;
	}

	@Override
	public double getSolidity() {
		return 0.1D;
	}

	@Override
	public void onShot() {
	}

	@Override
	public void onTouch(Camera c) {
	}

	public char toChar() {
		return (char) (teamNum + '0');
	}

	public Tile reflectTileX() {
		if (teamNum == 1) {
			return new PlayerSpawnTile(2);
		} else {
			return new PlayerSpawnTile(0);
		}
	}

	public Tile reflectTileY() {
		if (teamNum == 1) {
			return new PlayerSpawnTile(3);
		} else if (teamNum == 2) {
			return new PlayerSpawnTile(4);
		} else {
			return new PlayerSpawnTile(0);
		}
	}

}
