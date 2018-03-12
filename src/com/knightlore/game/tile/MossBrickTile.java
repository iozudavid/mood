package com.knightlore.game.tile;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class MossBrickTile extends BrickTile {

    @Override
    public Graphic getTexture() {
        return Texture.MOSSY_BRICK;
    }

    @Override
    public int getMinimapColor() {
        return super.getMinimapColor();
    }
    
    public char toChar() {
        return 'M';
    }
    
    public Tile copy() {
        return new MossBrickTile();
    }

    @Override
    public double getOpacity() {
        return 0;
    }

    @Override
    public double getSolidity() {
        return 1D;
    }

}
