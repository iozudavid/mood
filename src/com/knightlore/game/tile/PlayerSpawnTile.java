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
		return (char)(teamNum + '0');
	}
	
	public Tile reflectTileX(){
		if(teamNum == 1){
			return new PlayerSpawnTile(2);
		}else{
			return new PlayerSpawnTile(0);
		}
	}
	
	public Tile reflectTileY(){
		if(teamNum == 1){
			return new PlayerSpawnTile(3);
		}else if (teamNum == 2){
			return new PlayerSpawnTile(4);
		}else{
			return new PlayerSpawnTile(0);
		}
	}
	
}
