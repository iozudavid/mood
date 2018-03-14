package com.knightlore.game.tile;

import java.io.Serializable;

import com.knightlore.game.Team;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.ColorUtils;
import com.knightlore.render.graphic.Graphic;

/**
 * A tile is a unit 'block' of the map in the game world. The corridors, floors,
 * etc, are made up of individual tiles.
 * 
 * @author Joe Ellis
 *
 */
public abstract class Tile implements Serializable {

    /**
     * We calculate the minimap colour of any tile by simply averaging the
     * colours in the corresponding texture. This can be overridden.
     */
    private int minimapColor = ColorUtils.averageColor(getTexture().getPixels());

    public abstract Graphic getTexture();

    public double getOpacity() {
        return 1D;
    }

    public double getSolidity() {
        return 1D;
    }

    public int getMinimapColor() {
        return minimapColor;
    }

    public abstract void onShot();

    public abstract void onEntered(Entity entity);

    public char toChar() {
        return ' ';
    }

    public Tile reflectTileX() {
        return this;
    }

    public Tile reflectTileY() {
        return this;
    }

    public boolean blockLOS() {
        return false;
    }

    public Team getTeam() {
        return Team.none;
    }

    public abstract Tile copy();
}
