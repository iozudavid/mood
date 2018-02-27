package com.knightlore.game.world;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.entity.Entity;

public abstract class GameWorld {
    
    public static final long TEST_SEED = 161803398874L;
    
    protected Map map;
    protected List<Entity> ents;
    
    public GameWorld() {
        this.ents = new ArrayList<Entity>();
        generateMap(16, 16, TEST_SEED);
    }

    protected void generateMap(int xSize, int ySize, long seed) {
        // First create the map
        MapGenerator generator = new MapGenerator();
        this.map = generator.createMap(xSize, ySize, seed);
    }
    
    public Map getMap() {
        return map;
    }
    
    public List<Entity> getEntities() {
        return ents;
    }

}
