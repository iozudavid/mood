package com.knightlore.game.area;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.knightlore.game.Team;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.PlayerSpawnTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.utils.Vector2D;

public class Map extends Area {
    private final long seed;

    public Map(Tile[][] grid, long seed) {
        super(grid);
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }

    /**
     * Finds a random air tile that exists
     * 
     * @returns the coordinate in the center of a tile
     */
    public Vector2D getRandomSpawnPoint() {
        List<Vector2D> candidates = new ArrayList<>();
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (getTile(i, j) == AirTile.getInstance()) {
                    candidates.add(new Vector2D(i+0.5, j+0.5));
                }
            }
        }

        Random rand = new Random();
        int index = rand.nextInt(candidates.size());
        return candidates.get(index);
    }

    public Vector2D getRandomSpawnPoint(Team team) {
        List<Vector2D> candidates = new ArrayList<>();
        for(int i = 0; i < getWidth(); i++) {
            for( int j=0; j < getHeight(); j++) {
                Tile currentTile = getTile(i,j);
                if(! (currentTile instanceof PlayerSpawnTile)) {
                    continue;
                }
                if(currentTile.getTeam() != team) {
                    continue;
                }
                if(!((PlayerSpawnTile) currentTile).isSpawnable()) {
                    continue;
                }
                candidates.add(new Vector2D(i + 0.5, j + 0.5));
            }
        }
        
        if(candidates.isEmpty()) {
            return getRandomSpawnPoint();
        }
        Random rand = new Random();
        int index = rand.nextInt(candidates.size());
        return candidates.get(index);
    }
    
}
