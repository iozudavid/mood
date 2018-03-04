package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;

public class PathTile extends Tile {

    private static PathTile instance = new PathTile();
    
    public static PathTile getInstance() {
        return instance;
    }
    
    @Override
    public Graphic getTexture() {
        return AirTile.getInstance().getTexture();
    }

    @Override
    public void onShot() {
    }

    @Override
    public void onEntered(Player player) {
    }

    @Override
    public TileType getTileType() {
        return TileType.path;
    }

    @Override
    public char toChar() {
        return '-';
    }
    
}
