package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class BushTile extends Tile {

    @Override
    public Graphic getTexture() {
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
    public void onEntered(Player p) {
    }

}
