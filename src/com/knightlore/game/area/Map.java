package com.knightlore.game.area;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		List<Vector2D> candidates = new ArrayList<>();
		for(int i=0; i<getWidth(); i++) {
			for(int j=0; j<getHeight(); j++) {
				if(getTile(i,j) == AirTile.getInstance()) {
					candidates.add(new Vector2D(i,j));
				}
			}
		}

		Random rand = new Random();
		int index = rand.nextInt(candidates.size());
		return candidates.get(index);
	}
}
