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
        super(uuid, position, pickupManager);
        sprite = DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE;
        spawnDelay = 10;
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
    public String getName() {
        return "SHOTGUN";
    }

    @Override
    public void onCollide(Player player) {
        System.out.println("Collided with shotgun pickup!");
        // update pickup manager
        addToPickupManager();
        // give player shotgun
        //player.giveWeapon(new Shotgun())
        // existence of object set to false
        setExists(false);
    }

}
