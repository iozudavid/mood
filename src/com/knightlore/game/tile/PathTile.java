package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
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
    public char toChar() {
        return '-';
    }

    @Override
    public Tile copy() {
        // TODO Auto-generated method stub
        return new PathTile();
    }

    @Override
    public void onEntered(Entity entity) {
        // TODO Auto-generated method stub
        
    }
    
}
