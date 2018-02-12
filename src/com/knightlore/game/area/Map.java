package com.knightlore.game.area;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.Environment;

public class Map extends Area {
	
    // TODO: read in maps from files/procedurally generate.
    // Maps have associated environments
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

	// dumb test
	public static void main(String args[]) {
		MapGenerator generator = new MapGenerator();
		Map m = generator.createMap(100, 100, Environment.LIGHT_OUTDOORS);
		// // m = joinUpR(m,m);
		System.out.println(m.toString());
	}

}
