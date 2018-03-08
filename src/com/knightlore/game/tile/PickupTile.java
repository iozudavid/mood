package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.game.entity.pickup.PickupItem;
import com.knightlore.game.entity.pickup.PickupType;
import com.knightlore.game.entity.weapon.*;

public class PickupTile extends Tile {

    private PickupType pickupType;
    
    public PickupTile(PickupType pickupType) {
        this.pickupType = pickupType;
    }
    
    @Override
    public Graphic getTexture() {
        // TODO: Return some texture
        return AirTile.getInstance().getTexture();
    }

    @Override
    public void onShot() {
    }

    @Override
    public void onEntered(Player player) {
    }
    
    public PickupType getPickupType() {
        return pickupType;
    }

    @Override
    public char toChar() {
        return 'p';
    }

    @Override
    public Tile copy() {
        return new PickupTile(pickupType);
    }
    
}
