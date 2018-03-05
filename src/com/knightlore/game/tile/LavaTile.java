package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.game.buff.BuffType;
import com.knightlore.game.buff.Fire;
import com.knightlore.game.buff.Slow;
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
    public void onEntered(Player player) {
        // only apply a buff to player if it
        // doesn't have it
        if(!player.hasBuff(BuffType.fire)) {
            player.addBuff(new Fire());
        }
        
        if(!player.hasBuff(BuffType.slow)) {
            player.addBuff(new Slow());
        }
        
    }

    @Override
    public TileType getTileType() {
        return TileType.lava;
    }

    @Override
    public char toChar() {
        return 'L';
    }
    
}
