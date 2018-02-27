package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

// it's a singleton to avoid having multiple copies of air
public class AirTile extends Tile {

    private static AirTile instance = new AirTile();

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
    public void onEntered(Player p) {
    }

}
