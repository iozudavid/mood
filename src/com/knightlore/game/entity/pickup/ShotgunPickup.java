package com.knightlore.game.entity.pickup;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

/**
 * A shotgun pickup item.
 * 
 * @author Joe Ellis
 *
 */
public class ShotgunPickup extends PickupItem {
    
    private static final long PICKUP_SPAWN_PROTECT_TIME = 10;
    private long spawnProtectTime = 0;
    
    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new ShotgunPickup(uuid, Vector2D.ONE, null);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    public ShotgunPickup(Vector2D position, PickupManager pickupManager) {
        this(UUID.randomUUID(), position, pickupManager);
    }

    public ShotgunPickup(UUID uuid, Vector2D position, PickupManager pickupManager) {
        super(uuid, position, DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE, pickupManager);
        spawnDelay = 10;
    }
    
    // FIGURE OUT
    //private ShotgunPickup(UUID uuid, Vector2D position) {
    //    super(uuid, position, DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE);
    //    spawnProtectTime = GameEngine.ticker.getTime() + PICKUP_SPAWN_PROTECT_TIME;
    //}
    // FIGURE OUT
    
    @Override
    public int getMinimapColor() {
        return 0xFF00FF;
    }
    
    @Override
    public String getClientClassName() {
        // One class for both client and server.
        return this.getClass().getName();
    }
    
    @Override
    public String getName() {
        return "SHOTGUN";
    }

    @Override
    public void onCollide(Player player) {
        if(exists) {
        // update pickup manager
        addToPickupManager();
        // existence of object set to false
        this.destroy();
        }
    }
    
    public void onDestroy() {
        System.out.println("shotgun pickup collected");
        super.onDestroy();
        
    }
    
}
