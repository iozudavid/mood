package com.knightlore.game.entity.pickup;

import java.util.PriorityQueue;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.PickupTile;
import com.knightlore.utils.Vector2D;

public class PickupManager implements TickListener {
    
    private final GameEngine engine;
    private final PriorityQueue<PickupPlacement> pickupQueue
            = new PriorityQueue<>();
    private double currentTime = 0.0;
    
    /**
     * The pickupManager is used to keep track of the placement of
     * pickup items on Pickup Tiles. These items, upon being collided with,
     * are destroyed and then replaced after their spawn delay has expired.
     *
     * @param map
     */
    public PickupManager(Map map) {
        
        this.engine = GameEngine.getSingleton();
        
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (!(map.getTile(x, y) instanceof PickupTile)) {
                    continue;
                }
                PickupTile tile = (PickupTile)map.getTile(x, y);
                Vector2D vector = Vector2D.fromGridRef(x, y);
                PickupType type = tile.getPickupType();
                PickupItem item;
                switch (type) {
                    case SHOTGUN:
                        item = new ShotgunPickup(vector, this);
                        break;
                    case HEALTH:
                        item = new HealthPickup(vector, this);
                        break;
                    case PISTOL:
                        item = new PistolPickup(vector, this);
                        break;
                    case SPEED:
                        item = new SpeedPickup(vector, this);
                        break;
                    default:
                        item = new HealthPickup(vector, this);
                }
                pickupQueue.add(new PickupPlacement(0, item));
            }
        }
        
        GameEngine.ticker.addTickListener(this);
    }
    
    /**
     * Adds a pickup item to the queue, so it can be added to the game engine
     * after it's spawn delay.
     *
     * @param item
     */
    public void addToQueue(PickupItem item) {
        double nextTime = currentTime + item.getSpawnDelay();
        pickupQueue.add(new PickupPlacement(nextTime, item));
    }
    
    /**
     * On tick, the pickup manager iterates through its queue of pickup placements -
     * placing items as needed, and adjusts it's current time appropriates.
     */
    @Override
    public void onTick() {
        
        // place pickups if appropriate
        while (pickupQueue.peek() != null && pickupQueue.peek().getPlacementTime() <= currentTime) {
            // get and remove from queue
            PickupPlacement pp = pickupQueue.poll();
            // add pickup to the engine
            System.out.println("Adding to engine a pickup!");
            PickupItem item = pp.getItem();
            item.init();
            //List<Entity> ents = engine.getWorld().getEntities();
            //ents.add(item);
            engine.getWorld().addEntity(item);
        }
        // increment time (maybe try shorter intervals)
        currentTime += 1;
    }
    
    /**
     * The pickup manager is checked every second.
     */
    @Override
    public long interval() {
        // once per second
        return (long)GameEngine.UPDATES_PER_SECOND;
    }
    
}
