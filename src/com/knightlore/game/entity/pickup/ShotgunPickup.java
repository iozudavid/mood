package com.knightlore.game.entity.pickup;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.game.entity.weapon.WeaponType;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class ShotgunPickup extends WeaponPickup {
    private static final DirectionalSprite DSPRITE = DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE;
    
    public ShotgunPickup(Vector2D position, PickupManager pickupManager) {
        super(position, DSPRITE, pickupManager);
    }
    
    public ShotgunPickup(UUID uuid, Vector2D position, PickupManager pickupManager) {
        super(uuid, position, DSPRITE, pickupManager);
    }
    
    /**
     * Called by the network when creating the client-side representation of
     * this object. Instantiates a copy of the client class, and deserializes
     * the state into it.
     *
     * @param uuid  The uuid provided to this object
     * @param state The initial state of this object
     * @returns The client-side network object
     * @see NetworkObject
     */
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new ShotgunPickup(uuid, Vector2D.ONE, null);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    @Override
    WeaponType getWeaponType() {
        return WeaponType.SHOTGUN;
    }
    
    @Override
    public String getName() {
        return "SHOTGUN";
    }
    
}
