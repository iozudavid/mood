package com.knightlore.game.tile;

import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class UndecidedTile extends Tile {
    private static final UndecidedTile instance = new UndecidedTile();

    private UndecidedTile() {
    }

    public static UndecidedTile getInstance() {
        return instance;
    }

    @Override
    public Graphic getTexture() {
        return Texture.AIR;
    }

    @Override
    public void onShot() {
    }

    @Override
    public void onEntered(Entity entity) {
    }

    @Override
    public char toChar() {
        return '?';
    }

    @Override
    public Tile copy() {
        return instance;
    }

}
