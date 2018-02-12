package com.knightlore.game.area;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.Environment;

public class Map extends Area {
    // TODO: read in maps from file
	private final long seed;
	private final Environment environment;

	public Environment getEnvironment() {
		return environment;
	}

	public Map(Tile[][] grid, Environment environment, long seed) {
		super(grid);
		this.environment = environment;
		this.seed = seed;
	}

	public long getSeed() {
		return seed;
	}
}
