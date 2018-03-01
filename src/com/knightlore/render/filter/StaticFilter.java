package com.knightlore.render.filter;

import com.knightlore.render.ColorUtils;
import com.knightlore.render.PixelBuffer;

public class StaticFilter implements IPixelBufferFilter {

    private double opacity;
    private int resolution;

    public StaticFilter(double opacity, int resolution) {
        this.opacity = opacity;
        this.resolution = resolution;
    }

    @Override
    public void apply(PixelBuffer pix) {
        int mix = (int) (Math.random() * 0xffffff);
        for (int y = 0; y < pix.getHeight(); y += resolution) {
            for (int x = 0; x < pix.getWidth(); x += resolution) {
                int color = pix.pixelAt(x, y);
                color = ColorUtils.mixColor(color, mix, opacity);
                pix.fillSquare(color, x, y, resolution);
            }
        }
    }

}
