package com.knightlore.game.entity.pickup;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.game.Player;
import com.knightlore.game.buff.BuffType;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class HealthPickup extends PickupItem {

 // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new HealthPickup(uuid, Vector2D.ONE, null);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    public HealthPickup(Vector2D position, PickupManager pickupManager) {
        this(UUID.randomUUID(), position, pickupManager);
    }
    
    public HealthPickup(UUID uuid, Vector2D position, PickupManager pickupManager) {
        super(uuid, position, pickupManager);
        sprite = DirectionalSprite.BLUE_PLAYER_DIRECTIONAL_SPRITE;
        spawnDelay = 10;
    }

    @Override
    public int getMinimapColor() {
        return 0xFF00FF;
    }

    @Override
    public String getClientClassName() {
        return this.getClass().getName();
    }
    
    @Override
    public String getName() {
        return "HEALTH";
    }
    
    @Override
    public void onCollide(Player player) {
        System.out.println("Collided with health pickup");
        // update pickup manager
        addToPickupManager();
        // heal player
        player.removeBuff(BuffType.fire);
        player.applyHeal(30);
        // set existence to false
        setExists(false);
    }

}
