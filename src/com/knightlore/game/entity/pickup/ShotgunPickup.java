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
    
    static final long PICKUP_SPAWN_PROTECT_TIME = 10;
    long spawnProtectTime = 0;
    
    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new ShotgunPickup(uuid, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    public ShotgunPickup(Vector2D position) {
        this(UUID.randomUUID(), position);
    }
    
    public ShotgunPickup(UUID uuid, Vector2D position) {
        super(uuid, position);
        sprite = DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE;
        spawnProtectTime = GameEngine.ticker.getTime() + PICKUP_SPAWN_PROTECT_TIME;
    }
    
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
    public void onCollide(Player player) {
        if (exists) {
            this.destroy();
        }
    }
    
    public void onDestroy() {
        System.out.println("shotgun pickup collected");
        super.onDestroy();
        
    }
    
}
