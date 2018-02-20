package com.knightlore.game.entity;

import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.minimap.Minimap;
import com.knightlore.utils.Vector2D;

public class Zombie extends Entity {

    public Zombie(double size, Vector2D position) {
        this(size, position, Vector2D.UP);
    }

    public Zombie(double size, Vector2D position, Vector2D direction) {
        super(size, position, direction);
        zOffset = 100;
    }

    @Override
    public int getDrawSize() {
        return Minimap.SCALE / 2;
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
