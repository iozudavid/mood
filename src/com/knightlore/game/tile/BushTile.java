package com.knightlore.game.tile;

import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class BushTile extends Tile {

    @Override
    public Graphic getWallTexture() {
        return Texture.BUSH;
    }

    @Override
    public double getOpacity() {
        return 1D;
    }

    @Override
    public void onShot() {
    }

    @Override
    public void onEntered(Entity entity) {
    }

    @Override
    public String toString() {
        return "Bush";
    }

    @Override
    public char toChar() {
        return 'b';
    }

    @Override
    public Tile copy() {
        return new BushTile();
    }
}
