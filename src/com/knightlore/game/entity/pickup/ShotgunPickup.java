package com.knightlore.game.entity.pickup;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.game.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.graphic.sprite.ShotgunSprite;
import com.knightlore.utils.Vector2D;

public class ShotgunPickup extends PickupItem {

    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new ShotgunPickup(uuid, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    public ShotgunPickup(UUID uuid, Vector2D position) {
        super(uuid, position);
    }

    public ShotgunPickup(Vector2D position) {
        super(position);
        sprite = DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE;
    }

    @Override
    public int getMinimapColor() {
        return 0xFF00FF;
    }

    @Override
    public DirectionalSprite getDirectionalSprite() {
        return ShotgunSprite.SHOTGUN_DIRECTIONAL_SPRITE;
    }

    @Override
    public String getClientClassName() {
        // One class for both client and server.
        return this.getClass().getName();
    }

    @Override
    public void onCollide(Player player) {
        setExists(false);
    }

}
