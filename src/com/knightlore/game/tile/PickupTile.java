package com.knightlore.game.tile;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.pickup.PickupType;

import java.awt.*;

public class PickupTile extends Tile {

    private final PickupType pickupType;
    
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
    public int getMinimapColor() {
        if (pickupType == PickupType.HEALTH) {
            return Color.PINK.getRGB();
        }

        return Color.darkGray.getRGB();
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
    public String toString() {
        return pickupType.toString() + " pickup";
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
