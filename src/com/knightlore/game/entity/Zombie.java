package com.knightlore.game.entity;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.minimap.Minimap;
import com.knightlore.utils.Vector2D;

public class Zombie extends Entity {
    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new Zombie(uuid, 0, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    public Zombie(double size, Vector2D position) {
        this(size, position, Vector2D.UP);
    }

    public Zombie(double size, Vector2D position, Vector2D direction) {
        super(size, position, direction);
        zOffset = 100;
    }

    protected Zombie(UUID uuid, double size, Vector2D position, Vector2D direction) {
        super(uuid, size, position, direction);
        zOffset = 100;
    }

    @Override
    public int getMinimapColor() {
        // make it white
        return 0xFFFFFF;
    }

    @Override
    public DirectionalSprite getDirectionalSprite() {
        return DirectionalSprite.PLAYER_DIRECTIONAL_SPRITE;
    }

    @Override
    public void onUpdate() {

    }

    // TODO TODO TODO
    // TODO TODO TODO // TODO TODO TODO // TODO TODO TODO // TODO TODO TODO //
    // TODO TODO TODO SERIALIZE

}
