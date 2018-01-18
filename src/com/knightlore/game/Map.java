package com.knightlore.game;

import java.util.Random;

import com.knightlore.game.subArea.SpawnArea;
import com.knightlore.game.subArea.SubArea;
import com.knightlore.game.tile.*;
import com.knightlore.render.environment.IEnvironment;

public class Map {

	/*
	 * TODO: read in maps from files/procedurally generate.
	 */

	private int[][] spawnPosition; // will need to change with multiple players
	private int width, height;
	public Tile[][] map;

	/**
	 * Maps have associated environments.
	 */
	private IEnvironment environment;

	public Tile[][] getMapArray() {
		return map;
	}

	public IEnvironment getEnvironment() {
		return environment;
	}

	public Map(int w, int h) {
		width = w;
		height = h;
		map = new Tile[w][h];
		environment = IEnvironment.LIGHT_OUTDOORS;
	}

	public void addWalls() {
		for (int i = 0; i < width; i++) {
			map[i][0] = new BrickTile();
			map[i][height - 1] = new BrickTile();
		}
		for (int j = 0; j < height; j++) {
			map[0][j] = new BrickTile();
			map[width - 1][j] = new BrickTile();
		}
	}

	public static Map randMap() {
		Random rand = new Random();
		int w = 20 + rand.nextInt(20);
		int h = 20 + rand.nextInt(5);
		Map m = new Map(w, h);
		
		// make everything an undecided tile
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				m.map[i][j] = new UndecidedTile();
			}
		}

		m.placeSubArea(1,1, new SpawnArea(10,10));
		
		/*
		int r = 0;
		// make horizontal rows of free space
		for (int i = 0; i < w; i++) {
			r = rand.nextInt(100);
			if (r > 80) {
				for (int j = 0; j < h; j++) {
					m.map[i][j] = Tile.AIR;
				}
			} else if (r > 40) {
				for (int j = 0; j < (h / 2); j++) {
					m.map[i][j] = Tile.AIR;
				}
			}
		}

		// make vertical rows of free space
		for (int j = 0; j < h; j++) {
			r = rand.nextInt(100);
			if (rand.nextInt(100) > 50) {
				for (int i = 0; i < w; i++) {
					m.map[i][j] = Tile.AIR;
				}
			} else if (r > 20) {
				for (int i = 0; i < (w / 2); i++) {
					m.map[i][j] = Tile.AIR;
				}
			}
		}
		*/
		
		m.removeUndecided();
		m.makeSymX();
		//m.makeSymY();
		m.addWalls();
		return m;
	}

	private boolean placeSubArea(int x, int y, SubArea subArea){
		
		if(x + subArea.width >= width || y + subArea.height >= height){
			System.out.println("THIS");
			return false;
		}
		
		Tile[][] subGrid = subArea.grid;
		for(int i=0; i<subArea.width; i++){
			for(int j=0; j<subArea.width; j++){
				map[x+i][y+j] = subGrid[i][j];
			}
		}
		
		
		return true;
	}
	
	// replace all undecided with air
	private void removeUndecided(){
		for (int i=0; i< width; i++){
			for (int j=0; j<height; j++){
				if (map[i][j].toChar() == '?')
					map[i][j] = Tile.AIR;
			}
		}
	}
	
	// reflection in x-axis
	private void makeSymX() {
		Tile[][] symMap = new Tile[width][height * 2];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				Tile reflectTile = map[i][j].reflectTileX();
				symMap[i][j] = map[i][j];
				symMap[i][(height * 2 - 1) - j] = reflectTile;
			}
		}
		height = height * 2;
		map = symMap;
	}

	// reflection in y-axis
	private void makeSymY() {
		Tile[][] symMap = new Tile[width * 2][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Tile reflectTile = map[i][j].reflectTileY();
				symMap[i][j] = map[i][j];
				symMap[(width * 2 - 1) - i][j] = reflectTile;
			}
		}
		width = width * 2;
		map = symMap;
	}
	
	private static Map joinUpR(Map m1, Map m2) {

		// ensure same height
		assert (m1.height == m2.height);

		Map m3 = new Map(m1.width + m2.width, m1.height);

		for (int i = 0; i < m1.height; i++) {
			for (int j = 0; j < m1.width; j++) {
				m3.map[i][j] = m1.map[i][j];
			}
			for (int k = 0; k < m2.width; k++) {
				m3.map[i][m1.width + 1 + k] = m2.map[i][k];
			}
		}

		return m3;

	}

	public String toString() {

		String s = "MAP\n" + "WIDTH = " + width + "\n" + "HEIGHT = " + height + "\n";
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				s = s + map[i][j].toChar();
			}
			s = s + "\n";
		}

		return s;
	}

	// dumb test
	public static void main(String args[]) {
	 Map m = randMap();
	// // m = joinUpR(m,m);
	 System.out.println(m.toString());
	}

}
