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
    protected static final int TEST_XSIZE = 64;
    protected static final int TEST_YSIZE = 64;
    protected static final long TEST_SEED = 161803398874L;

    protected Map map;
    protected PlayerManager playerManager;
    protected AIManager aiManager;
    protected List<Entity> ents;

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
     * 
     * A null mapSeed will cause a map to be generated with the hard-coded test
     * seed.
     */
    public void setUpWorld(Long mapSeed) {
        if (mapSeed == null)
            mapSeed = TEST_SEED;
        map = new MapGenerator().createMap(TEST_XSIZE, TEST_YSIZE, mapSeed);
        ents = new LinkedList<>();
        aiManager = new AIManager(map);
        playerManager = new PlayerManager();
    }
}
