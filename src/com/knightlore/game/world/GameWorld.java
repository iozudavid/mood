package com.knightlore.game.world;

import java.util.LinkedList;
import java.util.List;

import com.knightlore.ai.AIManager;
import com.knightlore.game.ClientFFAGame;
import com.knightlore.game.FFAGame;
import com.knightlore.game.GameManager;
import com.knightlore.game.PlayerManager;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.physics.Physics;
import com.knightlore.utils.physics.RaycastHit;
import com.knightlore.utils.physics.RaycastHitType;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.pruner.Pruner;

public abstract class GameWorld {

    protected static final int TEST_XSIZE = 16;
    protected static final int TEST_YSIZE = 32;
    protected static final long TEST_SEED = 25L;
    
    protected Map map;
    protected PlayerManager playerManager;
    protected GameManager gameManager = null;
    protected AIManager aiManager;
    protected List<Entity> ents;

    public void update() {
    }
    
    private Map generateMap(int xSize, int ySize, long seed) {
        return new MapGenerator().createMap(xSize, ySize, seed);
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
    
    public RaycastHit raycast(Vector2D pos, Vector2D direction, int segments, double maxDist, Entity ignore) {
        if (segments <= 0) {
            System.err.println("can't raycast with <= 0 segments");
            return null;
        }
        direction = direction.normalised();
        
        Vector2D step = Vector2D.mul(direction.normalised(), maxDist / segments);
        
        Vector2D p = pos;
        int x, y;
        
        for (int i = 0; i < segments; i++) {
            x = (int) p.getX();
            y = (int) p.getY();
            if (map.getTile(x, y).blockLOS()) {
                return new RaycastHit(RaycastHitType.WALL, p, null);
            } else {
                for (int n = 0; n < ents.size(); n++) {
                    if(ents.get(n) == ignore) {
                        continue;
                    }
                    if (Physics.pointInRectangleDoubleTest(p, ents.get(n).getBoundingRectangle())) {
                        return new RaycastHit(RaycastHitType.ENTITY, p, ents.get(n));
                    }
                }
            }
            p = p.add(step);
        }
        
        return new RaycastHit(RaycastHitType.NOTHING, Vector2D.ZERO, null);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void changeGameManager(GameManager game) {
        gameManager = game;
    }
    
}
