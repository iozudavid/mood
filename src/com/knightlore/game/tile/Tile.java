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
    private final int minimapColor = ColorUtils.averageColor(getWallTexture().getPixels());

    /**
     * Gets the wall texture of this tile.
     * 
     * @return the wall texture graphic of the tile.
     */
    public abstract Graphic getWallTexture();

    /**
     * Indicates whether this is overridden in procedural
     * generation when a path is placed
     */
    protected boolean pathable = true;
    
    /**
     * Gets the floor texture of this tile. By default, this just returns the
     * wall texture.
     * 
     * @return the floor texture graphic of the tile.
     */
    public Graphic getFloorTexture() {
        return getWallTexture();
    }

    /**
     * Gets the opacity of the tile. The opacity is a double in the range 0 <=
     * opacity <= 1. A value of 0 means that the block is completely
     * transparent, and only the floor texture should be rendered. An opacity of
     * 1 indicates the block is completely opaque. Intermediate values give
     * blocks which have some degree of transparency.
     * 
     * Completely opaque (1D) by default.
     * 
     * @return the opacity of the tile.
     */
    public double getOpacity() {
        return 1D;
    }

    /**
     * Gets the solidity of a tile. The solidity of a tile indicates how 'solid'
     * that tile is, in the range 0 <= solidity <= 1 inclusive. A solidity of 0
     * means the player can walk straight through the tile. A solidity of 1
     * means that it's impossible for the player to walk through the tile.
     * Intermediate values will slow the player down to varying degrees.
     * 
     * Completely solid (1D) by default.
     * 
     * @return the solidity of the tile
     */
    public double getSolidity() {
        return 1D;
    }

    /**
     * Gets the cost of moving throught the tile. E.g. moving through walls is
     * impossible hence infinite cost and moving through lava tile is much more
     * costly then moving through empty air tile
     *
     * Completely solid (1D) by default.
     *
     * @return the solidity of the tile
     */
    public double getCost() {
        if (getSolidity() >= 1D) {
            return Double.POSITIVE_INFINITY;
        }

        return 1D / (1D - getSolidity());
    }

    /**
     * Every tile appears as a coloured block on the minimap. This method gets
     * the colour that the block should appear on the minimap.
     * 
     * By default, this is the average of the colour in the tile's texture.
     * 
     * @return the colour of the tile on the minimap.
     */
    public int getMinimapColor() {
        return minimapColor;
    }

    /**
     * Called when the block is shot.
     */
    public abstract void onShot();

    /**
     * Called when an entity enters this block.
     * 
     * @param entity
     *            the entity that entered the block.
     */
    public abstract void onEntered(Entity entity);

    public char toChar() {
        return '?';
    }

    public Tile reflectTileX() {
        return this;
    }

    public Tile reflectTileY() {
        return this;
    }

    public boolean blockLOS() {
        return getSolidity() == 1D;
    }

    public Team getTeam() {
        return Team.NONE;
    }

    public abstract Tile copy();

    public void setPathable(boolean b) {
        pathable = b;
    }
    
    public boolean overiddenByPath() {
        return pathable;
    }
    
    @Override
    public boolean equals(Object o) {
        return (this == o || o.getClass().equals(this.getClass()));
    }
    
}
