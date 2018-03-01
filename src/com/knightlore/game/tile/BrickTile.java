package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class BrickTile extends Tile {

    public BrickTile() {
    }

    @Override
    public Graphic getTexture() {
        return Texture.BRICK;
    }

    @Override
    public double getSolidity() {
        return 1D;
    }

    @Override
    public int getMinimapColor() {
        return 0x8C4100;
    }

    @Override
    public void onShot() {
        System.out.println("shot brick");
    }

    @Override
    public void onEntered(Player p) {
        System.out.println("touched brick");
    }

    @Override
    public char toChar() {
        return 'B';
    }
    
    @Override
    public boolean blockLOS() {
        return true;
    }

}
