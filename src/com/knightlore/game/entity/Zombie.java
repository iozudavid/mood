package com.knightlore.game.entity;

import com.knightlore.game.area.Map;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.minimap.Minimap;
import com.knightlore.utils.Vector2D;

public class Zombie extends Entity {

    public Zombie(Map map, double size, Vector2D position) {
        super(map, size, DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE, position, Vector2D.UP);
    }

    public Zombie(Map map, double size, Vector2D position, Vector2D direction) {
        super(map, size, DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE, position, direction);
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
        return Minimap.SCALE / 2;
    }

    @Override
    public int getMinimapColor() {
        // make it white
        return 0xFFFFFF;
    }

    // TODO TODO TODO
    // TODO TODO TODO // TODO TODO TODO // TODO TODO TODO // TODO TODO TODO //
    // TODO TODO TODO SERIALIZE

}
