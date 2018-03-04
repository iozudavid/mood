package com.knightlore.game.tile;

import com.knightlore.game.Player;
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
        // TODO: Damage player

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
