package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.game.entity.weapon.*;

public class WeaponTile extends Tile {

    Weapon weapon;
    
    public WeaponTile(Weapon weapon) {
        this.weapon = weapon;
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

    /*
    @Override
    public TileType getTileType() {
        return TileType.weapon;
    }
    */
    
    public Weapon getWeapon() {
        return weapon;
    }

    @Override
    public char toChar() {
        return 'W';
    }

    @Override
    public Tile copy() {
        // TODO Auto-generated method stub
        return new WeaponTile(weapon);
    }
    
}
