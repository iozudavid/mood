package com.knightlore.render;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public enum Environment {
    DARK_OUTDOORS(15, Texture.SLAB, Texture.WOOD) {
        @Override
        public int getMinimapBaseColor() {
            return 0x33333;
        }
    },
    LIGHT_OUTDOORS(3, Texture.BRICK, Texture.BRICK) {
        @Override
        public int getMinimapBaseColor() {
            return 0x074A00;
        }
    },
    DUNGEON(20, Texture.WOOD, Texture.MUD) {
        @Override
        public int getMinimapBaseColor() {
            return 0x000000;
        }
    };

    private final int darkness;
    private final Graphic floorTexture;
    private final Graphic ceilingTexture;

    Environment(int darkness, Graphic floorTexture, Graphic ceilingTexture) {
        this.darkness = darkness;
        this.floorTexture = floorTexture;
        this.ceilingTexture = ceilingTexture;
    }

    public abstract int getMinimapBaseColor();

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
