package com.knightlore.game.tile;

import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;

public class PathTile extends Tile {

    /** PathTile is used exclusively in procedural generation
     *  and replaced with air before the map is returned 
     */
    private static PathTile instance = new PathTile();
    
    public static PathTile getInstance() {
        return instance;
    }
    
    @Override
    public Graphic getWallTexture() {
        return AirTile.getInstance().getWallTexture();
    }

    @Override
    public void onShot() {
    }

    @Override
    public char toChar() {
        return '-';
    }

    @Override
    public Tile copy() {
        return new PathTile();
    }

    @Override
    public void onEntered(Entity entity) {
    }
    
}
