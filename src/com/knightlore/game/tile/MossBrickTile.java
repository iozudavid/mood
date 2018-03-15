package com.knightlore.game.tile;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class MossBrickTile extends BrickTile {

    @Override
    public Graphic getWallTexture() {
        return Texture.MOSSY_BRICK;
    }

    @Override
    public double getOpacity() {
        return 1D;
    }

    @Override
    public double getSolidity() {
        return 1D;
    }

    @Override
    public Tile copy() {
        // TODO Auto-generated method stub
        return new MossBrickTile();
    }

    @Override
    public char toChar() {
        // TODO Auto-generated method stub
        return 'M';
    }

}
