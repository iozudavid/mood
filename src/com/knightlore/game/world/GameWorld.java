package com.knightlore.game.world;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.knightlore.game.manager.AIManager;
import com.knightlore.game.manager.GameManager;
import com.knightlore.game.entity.Player;
import com.knightlore.game.manager.PlayerManager;
import com.knightlore.GameSettings;
import com.knightlore.game.GameMode;
import com.knightlore.game.area.Map;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.area.generation.MapType;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.physics.RaycastHit;

/**
 * Representation of the World in the Game. Subclass this to make new worlds.
 * Has Client and Server subclass implementations
 * 
 * @author James Adey
 * @see ClientWorld
 * @see ServerWorld
 */
public abstract class GameWorld {
    
    protected Map map;
    protected PlayerManager playerManager;
    protected GameManager gameManager = null;
    private AIManager aiManager;
    private List<Entity> ents;
    
    private final ConcurrentLinkedQueue<Entity> entsToAdd = new ConcurrentLinkedQueue<Entity>();
    private final ConcurrentLinkedQueue<Entity> entsToRemove = new ConcurrentLinkedQueue<Entity>();
    
    /**
     * Updates the PlayerManager, and makes any required changes the entity
     * list.
     * 
     * @see PlayerManager
     */
    public void update() {
        playerManager.update();
        while (entsToAdd.peek() != null) {
            ents.add(entsToAdd.poll());
        }
        while (entsToRemove.peek() != null) {
            ents.remove(entsToRemove.poll());
        }
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
    
    public Iterator<Entity> getEntityIterator() {
        return ents.iterator();
    }
    
    /**
     * @returns a new copy of the entities stored as an array
     */
    public Entity[] getEntityArray() {
        return ents.toArray(new Entity[0]);
    }
    
    /**
     * Queues a new entity to be added to the world, will actually be added next
     * world update.
     * 
     * @param ent
     *            the Entity to add
     */
    public void addEntity(Entity ent) {
        entsToAdd.offer(ent);
    }
    
    /**
     * Queues a new entity to be removed from the world, will actually be
     * removed next world update.
     * 
     * @param ent
     *            the Entity to remove
     */
    public void removeEntity(Entity ent) {
        entsToRemove.offer(ent);
    }
    
    /**
     * Generates the map and creates the AIManager and the PlayerManager.
     * <p>
     * Note: a null map seed will cause the map to generate a random map.
     * 
     * @param mapSeed
     *            the desired seed for this map
     * @see MapGenerator
     * @see AIManager
     * @see PlayerManager
     */
    public void setUpWorld(Long mapSeed) {
        if (mapSeed == null) {
            mapSeed = new Random().nextLong();
        }
        MapType mapType = MapType.FFA;
        if(GameManager.desiredGameMode == GameMode.TDM) {
            mapType = MapType.TDM;
        }
        
        if(mapType == MapType.TDM) {
            // TDM maps need a minimum size
            if(GameSettings.mapWidth < 32) {
                GameSettings.mapWidth = 32;
            }
            if(GameSettings.mapHeight < 32) {
                GameSettings.mapHeight = 32;
            }
        }
        map = new MapGenerator().createMap(GameSettings.mapWidth, GameSettings.mapHeight, mapType, mapSeed);
        System.out.println("Generated map.");
        ents = new LinkedList<>();
        aiManager = new AIManager(map);
        playerManager = new PlayerManager();
    }
    
    /**
     * Traces an infinitely thin line(ray) from a start point, in a specific
     * direction. This method casts the line against the world, entities and
     * players, and determines the closets thing it intersects with.
     * <p>
     * Note: This method is a "quick" cast, so will check only
     * <code>segment</code> points along the line, this means it may miss some
     * intersections.
     * 
     * @param pos
     *            the start point of this ray
     * @param direction
     *            the direction (doesn't have to be normalised) that this ray
     *            travels in
     * @param segments
     *            how many points along the line to check
     * @param maxDist
     *            how far to cast the ray
     * @param ignore
     *            which entity to ignore whilst casting this ray
     * @returns a RaycastHit structure with information about what was hit
     * @see RaycastHit
     */
    public RaycastHit raycast(Vector2D pos, Vector2D direction, int segments, double maxDist, Entity ignore) {
        if (segments <= 0) {
            throw new IllegalStateException("can't raycast with <= 0 segments");
        }
        
        Vector2D step = Vector2D.mul(direction.normalised(), maxDist / segments);
        
        Vector2D p = pos;
        int x, y;
        
        for (int i = 0; i < segments; i++) {
            x = (int) p.getX();
            y = (int) p.getY();
            if (map.getTile(x, y).blockLOS()) {
                return new RaycastHit(null);
            }
            
            double sqrDist;
            double sqrSize;
            
            // cast against players
            Iterator<Player> playerIter = playerManager.getPlayerIterator();
            while (playerIter.hasNext()) {
                Player player = playerIter.next();
                if (player == ignore) {
                    continue;
                }
                sqrSize = player.getSize() * player.getSize();
                sqrDist = player.getPosition().sqrDistTo(p);
                if (sqrDist < sqrSize) {
                    return new RaycastHit(player);
                }
            }
            // now against entities
            Iterator<Entity> it = this.getEntityIterator();
            while (it.hasNext()) {
                
                Entity ent = it.next();
                if (ent == ignore) {
                    continue;
                }
                sqrSize = ent.getSize() * ent.getSize();
                sqrDist = ent.getPosition().sqrDistTo(p);
                if (sqrDist < sqrSize) {
                    return new RaycastHit(ent);
                }
            }
            p = p.add(step);
        }
        
        return new RaycastHit(null);
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
    
    /**
     * Changes the game manager, should be used for switching game modes. Called
     * to assign the game mode on the client once it receives it from the
     * server.
     * 
     * @param game
     *            the new game manager
     */
    public void changeGameManager(GameManager game) {
        gameManager = game;
    }
    
    /**
     * Called by the engine, once it has started the game and finished
     * initialising. Should be used for adding elements to the game world that
     * are dependent on other parts of the engine, i.e. GUI
     */
    public abstract void onPostEngineInit();
    
}
