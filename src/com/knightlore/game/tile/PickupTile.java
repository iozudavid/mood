package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.pickup.PickupItem;
import com.knightlore.game.entity.pickup.PickupType;
import com.knightlore.game.entity.weapon.*;

public class PickupTile extends Tile {

    private PickupType pickupType;
    
    public PickupTile(PickupType pickupType) {
        this.pickupType = pickupType;
        pathable = false;
    }
    
    @Override
    public Graphic getWallTexture() {
        // TODO: Return some texture
        return AirTile.getInstance().getWallTexture();
    }

    @Override
    public Graphic getFloorTexture() {
        return Texture.WEAPON_SPAWN;
    }
    
    @Override
    public void onShot() {
    }
    
    public PickupType getPickupType() {
        return pickupType;
    }

    @Override
    public double getSolidity() {
        return 0.0;
    }
    
    @Override
    public double getOpacity() {
        return 0.0;
    }
    
    @Override
    public char toChar() {
        return 'p';
    }

    @Override
    public Tile copy() {
        return new PickupTile(pickupType);
    }

    @Override
    public void onEntered(Entity entity) {
    }
    
}
