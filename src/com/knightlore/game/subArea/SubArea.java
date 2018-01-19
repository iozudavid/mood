package com.knightlore.game.subArea;

import com.knightlore.game.tile.Tile;

public abstract class SubArea {
	
	public int width;
	public int height;
	
	public Tile[][] grid;
	
	public String toString() {

		String s = "SUBAREA\n" + "WIDTH = " + width + "\n" + "HEIGHT = " + height + "\n";
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				s = s + grid[i][j].toChar();
			}
			s = s + "\n";
		}

		return s;
	}
	
}
