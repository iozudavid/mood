package com.knightlore.render.hud;

import com.knightlore.game.area.generation.PerlinNoiseGenerator;
import com.knightlore.render.ColorUtils;
import com.knightlore.render.PixelBuffer;

public class HUD {

    private PixelBuffer display;
    private Boolean reRender = true;

    private final int bgResolution = 20;
    private int[] bg;

    public HUD(int width, int height) {
        display = new PixelBuffer(width, height);
        createBG();
    }

    public void render() {
        if (!reRender)
            return;

        reRender = false;
        for (int y = 0; y < display.getHeight(); y++) {
            for (int x = 0; x < display.getWidth(); x++) {
                final int bgX = x / bgResolution, bgY = y / bgResolution;
                int color = bg[(bgY * display.getWidth() + bgX) % 100];
                display.fillRect(color, x, y, bgResolution, bgResolution);
            }
        }
    }

    public void createBG() {
        bg = new int[bgResolution * bgResolution];

        final int BASE_COLOR = 0x4F2D00;
        final int OTHER_COLOR = 0x331D00;
        PerlinNoiseGenerator gen = new PerlinNoiseGenerator(display.getWidth(), display.getHeight(), 125L);
        double[][] noise = gen.createPerlinNoise();

        for (int y = 0; y < bgResolution; y++) {
            for (int x = 0; x < bgResolution; x++) {
                bg[y * bgResolution + x] = ColorUtils.mixColor(BASE_COLOR, OTHER_COLOR, noise[x][y] / 4);
            }
        }
    }

    public PixelBuffer getPixelBuffer() {
        return display;
    }

}
