package com.knightlore.game.entity.pickup;

import java.util.PriorityQueue;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.PickupTile;
import com.knightlore.utils.Vector2D;

public class PickupManager implements TickListener{

    private double currentTime = 0.0;
    private GameEngine engine;
    
    // keep track of the pickup object and when to place
    private PriorityQueue<PickupPlacement> pickupQueue 
      = new PriorityQueue<PickupPlacement>();
    
    public PickupManager(Map map) {
        
        this.engine = GameEngine.getSingleton();

        for(int x=0; x< map.getWidth(); x++) {
            for(int y=0; y< map.getHeight(); y++) {
                if(!(map.getTile(x, y) instanceof PickupTile)) {
                    continue;
                }
                PickupTile tile = (PickupTile) map.getTile(x, y);
                Vector2D vector = Vector2D.fromGridRef(x, y);
                PickupType type = tile.getPickupType();
                PickupItem item;
                switch(type){
                    case shotgun : item = new ShotgunPickup(vector, this);
                                   break;
                    case health  : item = new HealthPickup(vector , this);
                                   break;
                    default      : item = new HealthPickup(vector, this);
                }
                pickupQueue.add(new PickupPlacement(0 , item));                
            }
        }
        
        GameEngine.ticker.addTickListener(this);
    }

    public void addToQueue(PickupItem item) {
        System.out.println("Adding to queue item!");
        double nextTime = currentTime + item.getSpawnDelay();
        pickupQueue.add(new PickupPlacement(nextTime, item));
    }
    
    @Override
    public void onTick() {

        // place pickups if appropriate
        while(pickupQueue.peek() != null && pickupQueue.peek().getPlacementTime() <= currentTime) {
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

    @Override
    public long interval() {
        // once per second
        return (long) GameEngine.UPDATES_PER_SECOND;
    }
    
}
