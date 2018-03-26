package com.knightlore.game.tile;

import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class BrickTile extends Tile {
    
    private static final BrickTile instance = new BrickTile();
    
    public BrickTile() {
    }
    
    public static BrickTile getInstance() {
        return instance;
    }
    
    @Override
    public Graphic getWallTexture() {
        return Texture.BRICK;
    }
    
    @Override
    public double getSolidity() {
        return 1D;
    }
    
    @Override
    public void onEntered(Entity entity) {
    }
    
    @Override
    public String toString() {
        return "Brick";
    }
    
    @Override
    public char toChar() {
        return 'B';
    }
    
    @Override
    public boolean blockLOS() {
        return true;
    }
    
    @Override
    public Tile copy() {
        return new BrickTile();
    }
}
