package com.knightlore.game.world;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.pruner.Pruner;

public abstract class GameWorld {
    
    public static final int TEST_XSIZE = 16;
    public static final int TEST_YSIZE = 16;
    public static final long TEST_SEED = 161803398874L;
    
    protected Map map;
    protected List<Entity> ents;
    
    public GameWorld() {
        this.ents = new ArrayList<Entity>();
        this.map = generateMap(TEST_XSIZE, TEST_YSIZE, TEST_SEED);
    }
    
    public void update() {
        Pruner.prune(ents);
    }
    
    public abstract void update();

    private Map generateMap(int xSize, int ySize, long seed) {
        return new MapGenerator().createMap(xSize, ySize, seed);
    }
    
    public Map getMap() {
        return map;
    }
    
    public List<Entity> getEntities() {
        return ents;
    }
    
    /**
     * Populate the world with things initially.
     */
    public abstract void setUpWorld();

}
