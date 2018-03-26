package com.knightlore.game.tile;

import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

// it's a singleton to avoid having multiple copies of air
public class AirTile extends Tile {
    private static final AirTile instance = new AirTile();
    
    private AirTile() {
    }
    
    public static AirTile getInstance() {
        return instance;
    }
    
    @Override
    public Graphic getWallTexture() {
        return Texture.AIR;
    }
    
    @Override
    public double getSolidity() {
        return 0D;
    }
    
    @Override
    public Tile copy() {
        return instance;
    }
    
    @Override
    public void onEntered(Entity entity) {
    }
    
    @Override
    public String toString() {
        return "Air";
    }
    
    public char toChar() {
        return ' ';
    }
    
}
