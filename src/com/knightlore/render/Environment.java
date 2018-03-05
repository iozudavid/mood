package com.knightlore.render;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public enum Environment {
    DARK_OUTDOORS(15, Texture.SLAB, Texture.MUD), LIGHT_OUTDOORS(3, Texture.BRICK, Texture.BRICK), DUNGEON(20,
            Texture.WOOD, Texture.MUD);

    private final int darkness;
    private final Graphic floorTexture;
    private final Graphic ceilingTexture;

    Environment(int darkness, Graphic floorTexture, Graphic ceilingTexture) {
        this.darkness = darkness;
        this.floorTexture = floorTexture;
        this.ceilingTexture = ceilingTexture;
    }

    public int getMinimapBaseColor() {
        int avg = ColorUtils.averageColor(getFloorTexture().getPixels());
        return ColorUtils.quickDarken(avg);
    }

    public int getDarkness() {
        return darkness;
    }

    public Graphic getFloorTexture() {
        return floorTexture;
    }

    public Graphic getCeilingTexture() {
        return ceilingTexture;
    }

}
