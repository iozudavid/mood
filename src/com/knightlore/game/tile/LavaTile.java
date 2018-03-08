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
            // implement buffs in entity
        }
        
        // only apply a buff to player if it
        // doesn't have it
        /*
        if(!entity.hasBuff(BuffType.fire)) {
            entity.addBuff(new Fire());
        }
        
        if(!entity.hasBuff(BuffType.slow)) {
            entity.addBuff(new Slow());
        }
        */
    }
    
}
