package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.game.buff.BuffType;
import com.knightlore.game.buff.Fire;
import com.knightlore.game.buff.Push;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;
import com.knightlore.utils.Vector2D;

// it's a singleton to avoid having multiple copies of air
public class AirTile extends Tile {
    private static final AirTile instance = new AirTile();

    private AirTile() {
    }

    public static AirTile getInstance() {
        return instance;
    }

    @Override
    public Graphic getTexture() {
        return Texture.AIR;
    }

    @Override
    public double getSolidity() {
        return 0D;
    }

    @Override
    public void onShot() {
    }

    @Override
    public Tile copy() {
        return instance;
    }

    @Override
    public void onEntered(Entity entity) {
        // just for testing the fire debuff
        //entity.resetBuff(new Push(Vector2D.UP));
        entity.resetBuff(new Fire());
    }

    public char toChar() {
        return ' ';
    }
    
}
