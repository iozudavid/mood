package com.knightlore.game.entity;

import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Zombie extends Entity {

    public Zombie(double size, Vector2D position) {
        this(size, position, Vector2D.UP);
    }

    public Zombie(double size, Vector2D position, Vector2D direction) {
        super(size, DirectionalSprite.PLAYER_DIRECTIONAL_SPRITE, position, direction);
        zOffset = 100;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getDrawSize() {
        return 5;
    }

    @Override
    public int getMinimapColor() {
        // make it white
        return 0xFFFFFFFF;
    }

}
