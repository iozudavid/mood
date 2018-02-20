package com.knightlore.world;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.Environment;

/**
 * This class represents the physical world in which the game takes place.
 * 
 * @author James Adey
 *
 */
public abstract class GameWorld {

    /**
     * The map associated with the game world.
     */
    protected Map map;

    /**
     * List of entities that exist in the world.
     */
    protected List<Entity> entities;

    /**
     * The environment in the world.
     */
    protected Environment environment;

    public GameWorld(Environment environment) {
        this.entities = new ArrayList<Entity>();
        this.environment = environment;
    }

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

    /**
     * Gets the world's map.
     * 
     * @return the map.
     */
    public Map getMap() {
        return map;
    }

    /**
     * Gets the list of entities that exist in the world.
     * 
     * @return the list of entities.
     */
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * Gets the environment of the game world.
     * 
     * @return an environment object.
     */
    public Environment getEnvironment() {
        return environment;
    }

}
