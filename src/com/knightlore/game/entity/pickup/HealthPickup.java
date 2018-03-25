package com.knightlore.game.entity.pickup;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.game.entity.Player;
import com.knightlore.game.buff.BuffType;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class HealthPickup extends PickupItem {

    /**
     * Called by the network when creating the client-side representation of
     * this object. Instantiates a copy of the client class, and deserializes
     * the state into it.
     * 
     * @param uuid
     *            The uuid provided to this object
     * @param state
     *            The initial state of this object
     * @returns The client-side network object
     * @see NetworkObject
     */
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
        super(uuid, position, DirectionalSprite.HEALTHKIT_DIRECTIONAL_SPRITE, pickupManager);
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
        if (exists) {
            // update pickup manager
            addToPickupManager();
            // heal player
            player.removeBuff(BuffType.FIRE);
            player.applyHeal(30);
            // set existence to false
            this.destroy();
        }
    }

}
