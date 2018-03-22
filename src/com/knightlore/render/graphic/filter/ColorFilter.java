package com.knightlore.render.graphic.filter;

import com.knightlore.render.ColorUtils;

public class ColorFilter {

    public static final ColorFilter RED = new ColorFilter(0xFF0000, 0.5);
    public static final ColorFilter BLUE = new ColorFilter(0x0000FF, 0.5);

    private final int color;
    private final double blend;

    public ColorFilter(int color, double blend) {
        this.color = color;
        this.blend = blend;
    }

    public void apply(int[] pix, final int CHROMA_KEY) {
        for (int i = 0; i < pix.length; i++) {
            if (pix[i] == CHROMA_KEY) {
                continue;
            }
            pix[i] = ColorUtils.mixColor(pix[i], color, blend);
        }
    }

}
