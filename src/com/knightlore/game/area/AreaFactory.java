package com.knightlore.game.area;

import java.util.Random;

import com.knightlore.game.area.subarea.SpawnArea;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.ExpectRoom;
import com.knightlore.game.tile.PlayerSpawnTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.Environment;

public final class AreaFactory {

	public static Map createRandomMap(Environment environment) {
		Random rand = new Random();
		int seedWidth = 20 + rand.nextInt(20);
		int seedHeight = 20 + rand.nextInt(5);
		Tile[][] grid = createEmptyGrid(seedWidth, seedHeight);
		SpawnArea spawnArea = createSpawnArea(10, 10);
		placeSubArea(grid, 1, 1, spawnArea);

		/*
		 * int r = 0; // make horizontal rows of free space for (int i = 0; i <
		 * TODO dead code? delete? w; i++) { r = rand.nextInt(100); if (r > 80)
		 * { for (int j = 0; j < h; j++) { m.map[i][j] = Tile.AIR; } } else if
		 * (r > 40) { for (int j = 0; j < (h / 2); j++) { m.map[i][j] =
		 * Tile.AIR; } } }
		 *
		 * // make vertical rows of free space for (int j = 0; j < h; j++) { r =
		 * rand.nextInt(100); if (rand.nextInt(100) > 50) { for (int i = 0; i <
		 * w; i++) { m.map[i][j] = Tile.AIR; } } else if (r > 20) { for (int i =
		 * 0; i < (w / 2); i++) { m.map[i][j] = Tile.AIR; } } }
		 */

		grid = makeSymX(grid);
		// m.makeSymY();
		addWalls(grid);
		return new Map(grid, environment);
	}

	private static void addWalls(Tile[][] grid) {
		int width = grid.length;
		int height = grid[0].length;
		for (int i = 0; i < width; i++) {
			grid[i][0] = new BrickTile();
			grid[i][height - 1] = new BrickTile();
		}

		for (int j = 0; j < height; j++) {
			grid[0][j] = new BrickTile();
			grid[width - 1][j] = new BrickTile();
		}
	}

	private static void placeSubArea(Tile[][] grid, int x, int y, Area subArea) {
		if (x + subArea.width >= grid.length || y + subArea.height >= grid[0].length) {
			throw new IndexOutOfBoundsException(
					"One of the sub areas during" + " map generation was partially generated outside of the map");
		}

		Tile[][] subGrid = subArea.grid;
		for (int i = 0; i < subArea.width; i++) {
			for (int j = 0; j < subArea.width; j++) {
				grid[x + i][y + j] = subGrid[i][j];
			}
		}
	}

	// reflection in x-axis
	private static Tile[][] makeSymX(Tile[][] grid) {
		int width = grid.length;
		int height = grid[0].length;
		Tile[][] symMap = new Tile[width][height * 2];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				Tile reflectTile = grid[i][j].reflectTileX();
				symMap[i][j] = grid[i][j];
				symMap[i][(height * 2 - 1) - j] = reflectTile;
			}
		}

		return symMap;
	}

	// reflection in y-axis
	private Tile[][] makeSymY(Tile[][] grid) {
		int width = grid.length;
		int height = grid[0].length;
		Tile[][] symMap = new Tile[width * 2][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Tile reflectTile = grid[i][j].reflectTileY();
				symMap[i][j] = grid[i][j];
				symMap[(width * 2 - 1) - i][j] = reflectTile;
			}
		}

		return symMap;
	}

	public static SpawnArea createSpawnArea(int width, int height) {
		Tile[][] grid = createEmptyGrid(width, height);
		grid[width / 2][height / 2] = new PlayerSpawnTile(1);
		for (int i = 0; i < width / 3; i++) {
			grid[i][height - 1] = new BrickTile();
		}

		for (int i = width * 2 / 3; i < width; i++) {
			grid[i][height - 1] = new BrickTile();
		}
		for (int j = 0; j < height / 3; j++) {
			grid[width - 1][j] = new BrickTile();
		}

		for (int j = height * 2 / 3; j < height; j++) {
			grid[width - 1][j] = new BrickTile();
		}

		grid[0][height - 1] = new ExpectRoom(ExpectRoom.DOWN);
		grid[width - 1][0] = new ExpectRoom(ExpectRoom.RIGHT);
		grid[width - 1][height - 1] = new ExpectRoom(ExpectRoom.BOTH);
		return new SpawnArea(grid);
	}

	private static Tile[][] createEmptyGrid(int width, int height) {
		Tile[][] grid = new Tile[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j] = AirTile.getInstance();
			}
		}

		return grid;
	}
}
