package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;

public class BreakibleTile extends Tile {

    private int health = 100;
    private static BreakibleTile instance = new BreakibleTile();
    
    private BreakibleTile() {    
    }
    
    public static BreakibleTile getInstance() {
        return instance;
    }
    
    @Override
    public Graphic getTexture() {
        // TODO: Add a texture for breakible tiles
        return BrickTile.getInstance().getTexture();
    }

    @Override
    public void onShot() {
        // TODO: Deal damage to tile

    }
    
    @Override
    public char toChar() {
        return 'b';
    }

    @Override
    public Tile copy() {
        // TODO Auto-generated method stub
        return new BreakibleTile();
    }

    @Override
    public void onEntered(Entity entity) {
    }

}
