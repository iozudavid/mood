package com.knightlore.game.entity.pickup;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.game.entity.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class SpeedPickup extends PickupItem {

    
 // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new SpeedPickup(uuid, Vector2D.ONE, null);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    public SpeedPickup(UUID uuid, Vector2D position, PickupManager pickupManager) {
        this(UUID.randomUUID(), position, DirectionalSprite.SPEED_DIRECTION_SPRITE,pickupManager);
    }
    
    public SpeedPickup(UUID uuid, Vector2D position, DirectionalSprite sprite, PickupManager pickupManager) {
        super(uuid, position, DirectionalSprite.SPEED_DIRECTION_SPRITE, pickupManager);
    }

    @Override
    public int getMinimapColor() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void onCollide(Player player) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getClientClassName() {
        // TODO Auto-generated method stub
        return null;
    }

}
