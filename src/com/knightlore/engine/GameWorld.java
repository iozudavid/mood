package com.knightlore.engine;

import java.util.List;

import com.knightlore.game.Player;
import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.Environment;
import com.knightlore.render.minimap.IMinimapObject;

public abstract class GameWorld {

    // all worlds need a map
    protected Map map;

    /**
     * Called by the engine to initialise the world. This should be used to link
     * the world into the managers
     */
    public abstract void initWorld();

    /**
     * Called by the engine after the initWorld() This should be used to
     * populate the world with game objects
     */
    public abstract void populateWorld();

    /**
     * Called every time the game updates. This should be used to update non
     * gameObject parts of the world. GameObjects will be updated separately.
     */
    public abstract void updateWorld();

    public abstract GameWorld loadFromFile(String fileName);

    public abstract boolean saveToFile(String fileName);

    // Create a player in the world at a random position with a random UUID.
    public abstract Player createPlayer();

    public abstract List<Entity> getEntities();

    public abstract void addEntity(Entity ent);

    public abstract void removeEntity(Entity ent);

    public abstract Environment getEnvironment();

    public Map getMap() {
        return map;
    }

}
