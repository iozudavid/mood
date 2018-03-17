package com.knightlore.game.entity;

import java.util.UUID;

import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public abstract class ZombieShared extends Entity {
    public ZombieShared(Vector2D position) {
        this(position, Vector2D.UP);
    }

    protected ZombieShared(Vector2D position, Vector2D direction) {
        super(0.25D, position, direction);
        zOffset = 100;
    }

    protected ZombieShared(UUID uuid, double size, Vector2D position, Vector2D direction) {
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
    public String getClientClassName() {
        // One class for both client and server.
        return ZombieClient.class.getName();
    }
    
    @Override
    public String getName() {
        return "Zombie";
    }
}
