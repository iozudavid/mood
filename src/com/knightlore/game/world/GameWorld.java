package com.knightlore.game.world;

import java.util.LinkedList;
import java.util.List;

import com.knightlore.ai.AIManager;
import com.knightlore.game.PlayerManager;
import com.knightlore.game.area.Map;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.pruner.Pruner;

public abstract class GameWorld {
    private static final int TEST_XSIZE = 16;
    private static final int TEST_YSIZE = 16;
    private static final long TEST_SEED = 161803398874L;
    
    protected final Map map = new MapGenerator().createMap(TEST_XSIZE, TEST_YSIZE, TEST_SEED);
    protected final PlayerManager playerManager = new PlayerManager();
    protected final AIManager aiManager = new AIManager(map);
    protected final List<Entity> ents = new LinkedList<>();
    
    public GameWorld() {
    }
    
    public void update() {
        Pruner.prune(ents);
    }
    
    public Map getMap() {
        return map;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public AIManager getAiManager() {
        return aiManager;
    }
    
    public List<Entity> getEntities() {
        return ents;
    }
    
    /**
     * Populate the world with things initially.
     */
    public abstract void setUpWorld();
}
