package com.knightlore.game.tile;

import com.knightlore.render.Camera;
import com.knightlore.render.sprite.Texture;

public class PlayerSpawnTile extends Tile{

	private int teamNum = 0;
	
	public PlayerSpawnTile(int team){
		teamNum = team;
	}
	
	@Override
	public Texture getTexture() {
		return Texture.AIR;
	}

	@Override
	public void onShot() {
	}

	@Override
	public void onTouch(Camera c) {
	}

	public char toChar(){
		return (char) teamNum;
	}
	
}
