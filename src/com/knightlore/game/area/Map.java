package com.knightlore.game.area;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.knightlore.game.Team;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.TileType;
import com.knightlore.utils.Vector2D;

public class Map extends Area {
    // TODO: read in maps from file
    private final long seed;

    public Map(Tile[][] grid, long seed) {
        super(grid);
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }

    public Vector2D getRandomSpawnPoint() {
        List<Vector2D> candidates = new ArrayList<>();
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (getTile(i, j) == AirTile.getInstance()) {
                    candidates.add(new Vector2D(i, j));
                }
            }
        }

        Random rand = new Random();
        int index = rand.nextInt(candidates.size());
        return candidates.get(index);
    }
    
    public List<Vector2D> getTilePoints(Team team, TileType type){
        
        List<Vector2D> positions = new ArrayList<>();
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                
                Tile tile = getTile(i,j);
                // guard clauses
                if (tile.getTileType() != type) {
                    continue;
                }
                if (tile.getTeam() != team) {
                    continue;
                }
                
                positions.add(new Vector2D(i,j));
                
            }
        }
        return positions;
    }
    
}
