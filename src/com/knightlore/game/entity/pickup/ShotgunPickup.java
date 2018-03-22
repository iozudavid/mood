package com.knightlore.game.entity.pickup;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.game.entity.weapon.WeaponType;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class ShotgunPickup extends WeaponPickup {
    private static final DirectionalSprite DSPRITE = DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE;
    
    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new ShotgunPickup(uuid, Vector2D.ONE, null);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    public ShotgunPickup(Vector2D position, PickupManager pickupManager) {
        super(position, DSPRITE, pickupManager);
    }

    public ShotgunPickup(UUID uuid, Vector2D position,
            PickupManager pickupManager) {
        super(uuid, position, DSPRITE, pickupManager);
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
