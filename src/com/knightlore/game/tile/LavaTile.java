package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.game.buff.BuffType;
import com.knightlore.game.buff.Fire;
import com.knightlore.game.buff.Slow;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.ZombieServer;
import com.knightlore.render.graphic.Graphic;

public class LavaTile extends Tile {
    
    @Override
    public Graphic getTexture() {
        // TODO: Some lava texture
        return AirTile.getInstance().getTexture();
    }

    public double getOpacity() {
        return 0D;
    }

    public double getSolidity() {
        return 0D;
    }
    
    @Override
    public void onShot() {
    }

    @Override
    public char toChar() {
        return 'L';
    }

    @Override
    public Tile copy() {
        // TODO Auto-generated method stub
        return new LavaTile();
    }

    @Override
    public void onEntered(Entity entity) {
        
        // not quite sure which zombie class to use...
        if(entity instanceof Player || entity instanceof ZombieServer) {
            entity.resetBuff(new Fire(entity));
        }

    }
    
}
