package com.knightlore.game.subArea;

import com.knightlore.game.tile.*;

public class SpawnArea extends SubArea{
	
	// at the moment, assume is generated in top-left
	public SpawnArea(int w, int h){
		super.width = w;
		super.height = h;
		
		super.grid = new Tile[w][h];
		
		for(int i=0; i<w;i++){
			for(int j=0; j<h; j++){
				super.grid[i][j] = new UndecidedTile();
			}
		}
		
		super.grid[w/2][h/2] = new PlayerSpawnTile(1);
		
		for(int i=0; i<w/3; i++){
			super.grid[i][h-1] = new BrickTile();
		}
		
		for(int i=w*2/3; i<w; i++){
			super.grid[i][h-1] = new BrickTile();
		}
		for(int j=0; j<h/3; j++){
			super.grid[w-1][j] = new BrickTile();
		}
		
		for(int j=h*2/3; j<h; j++){
			super.grid[w-1][j] = new BrickTile();
		}
		
		// expecting rooms
		super.grid[0][h-1] = new ExpectRoom(ExpectRoom.DOWN);
		super.grid[w-1][0] = new ExpectRoom(ExpectRoom.RIGHT);
		super.grid[w-1][h-1] = new ExpectRoom(ExpectRoom.BOTH);
		
	}
	
	public static void main(String args[]){
		SpawnArea sp = new SpawnArea(10,10);
		System.out.println(sp.toString());
	}
	
	
}
