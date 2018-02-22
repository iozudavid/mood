package com.knightlore.game.entity.pickup;

import java.util.UUID;

import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.graphic.sprite.ShotgunSprite;
import com.knightlore.utils.Vector2D;

public class ShotgunPickup extends PickupItem {
    // Returns a new 'blank' instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, Vector2D initialPos) {
        return new ShotgunPickup(uuid, initialPos);
    }

    public ShotgunPickup(UUID uuid, Vector2D position) {
        super(uuid, position);
    }

    public ShotgunPickup(Vector2D position) {
        super(position);
    }

    @Override
    public int getDrawSize() {
        return 5;
    }

    @Override
    public int getMinimapColor() {
        return 0xFF00FF;
    }

    @Override
    public DirectionalSprite getDirectionalSprite() {
        return ShotgunSprite.SHOTGUN_DIRECTIONAL_SPRITE;
    }

    // TODO
    // TODO
    // TODO
    // TODO SERIALIZE

}
