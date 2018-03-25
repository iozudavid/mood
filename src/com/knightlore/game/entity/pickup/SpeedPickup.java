package com.knightlore.game.entity.pickup;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.game.buff.Speed;
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
    
    public SpeedPickup(Vector2D position, PickupManager pickupManager) {
        this(UUID.randomUUID(), position, pickupManager);
    }
    
    public SpeedPickup(UUID uuid, Vector2D position, PickupManager pickupManager) {
        super(uuid, position, DirectionalSprite.SPEED_DIRECTION_SPRITE,pickupManager);
        spawnDelay = 10;
    }

    @Override
    public int getMinimapColor() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getClientClassName() {
        return this.getClass().getName();
    }
    
    @Override
    public String getName() {
        return "SPEED";
    }
    
    @Override
    public void onCollide(Player player) {
        if(exists) {
            // update pickup manager
            addToPickupManager();
            // apply speed buff
            player.resetBuff(new Speed(player));
            // remove pickup
            this.destroy();
        }
    }

}
