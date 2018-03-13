package com.knightlore.render;

import com.knightlore.engine.audio.BackgroundMusic;
import com.knightlore.engine.audio.SoundResource;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public enum Environment {
    DARK_OUTDOORS(15, Texture.SLAB, Texture.MUD,
            BackgroundMusic.THEME_1), LIGHT_OUTDOORS(3, Texture.BRICK,
                    Texture.BRICK, BackgroundMusic.THEME_1), DUNGEON(20,
                            Texture.WOOD, Texture.MUD, BackgroundMusic.THEME_1);

    private final int darkness;
    private final Graphic floorTexture;
    private final Graphic ceilingTexture;

    private final BackgroundMusic bgMusic;

    Environment(int darkness, Graphic floorTexture, Graphic ceilingTexture,
            BackgroundMusic bgMusic) {
        this.darkness = darkness;
        this.floorTexture = floorTexture;
        this.ceilingTexture = ceilingTexture;
        this.bgMusic = bgMusic;
    }

    public BackgroundMusic getBgMusic() {
        return bgMusic;
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
