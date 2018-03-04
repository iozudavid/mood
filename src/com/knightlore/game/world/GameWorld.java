package com.knightlore.game.world;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.area.Map;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Physics;
import com.knightlore.utils.RaycastHit;
import com.knightlore.utils.RaycastHitType;
import com.knightlore.utils.Vector2D;
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
    
    public RaycastHit raycast(Vector2D pos, Vector2D direction, int segments, double maxDist) {
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
                return new RaycastHit(RaycastHitType.wall, p, null);
            } else {
                for (int n = 0; n < ents.size(); n++) {
                    if (Physics.pointInRectangleDoubleTest(p, ents.get(n).getBoundingRectangle())) {
                        return new RaycastHit(RaycastHitType.entity, p, ents.get(n));
                    }
                }
            }
            p = p.add(step);
        }
        
        return new RaycastHit(RaycastHitType.nothing,Vector2D.ZERO,null);
    }
    
}
