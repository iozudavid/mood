package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class MossBrickTile extends Tile {

    @Override
    public Graphic getTexture() {
        return Texture.MOSSY_BRICK;
    }

    @Override
    public void onShot() {
    }

    @Override
    public void onEntered(Player player) {
    }

    @Override
    public TileType getTileType() {
        return null;
    }

}
