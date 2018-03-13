package com.knightlore.game.tile;

import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class LavaTile extends Tile {

    @Override
    public Graphic getTexture() {
        return Texture.LAVA;
    }

    @Override
    public void onShot() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onEntered(Entity entity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Tile copy() {
        return new LavaTile();
    }
    
    @Override
    public double getSolidity() {
        return 0.75D;
    }
    
    @Override
    public double getOpacity() {
        return 0D;
    }

}
