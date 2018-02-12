package com.knightlore.game.area;

import java.util.ArrayList;
import java.util.Random;

import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.Environment;
import com.knightlore.utils.Vector2D;

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
	
	public Vector2D getRandomSpawnPoint() {
		//create a list of valid spawn points
		ArrayList<Vector2D> candidates = new ArrayList<Vector2D>();
		for(int i=0; i<getWidth(); i++) {
			for(int j=0; j<getHeight(); j++) {
				if(getTile(i,j) == AirTile.getInstance()) {
					candidates.add(new Vector2D(i,j));
				}
			}
		}
		//return a random spawn point
		Random rand = new Random();
		int index = rand.nextInt(candidates.size());
		return candidates.get(index);
	}
}
