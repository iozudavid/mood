package com.knightlore.render.minimap;

import com.knightlore.render.ColorUtils;
import com.knightlore.render.Environment;
import com.knightlore.render.PixelBuffer;

/**
 * A lighting mask obscures parts of the minimap based on the level of lighting
 * provided by the environment.
 * 
 * @author Joe Ellis
 *
 */
public class MinimapLightingMask {

    private Environment env;

    public MinimapLightingMask(Environment env) {
        this.env = env;
    }

    /**
     * Adjusts a given colour for the lighting in the environment.
     * 
     * @param pix
     *            the pixel buffer for the minimap.
     * @param color
     *            the colour that we're adjusting.
     * @param xx
     *            the x position of the current pixel on the minimap.
     * @param yy
     *            the y position of the current pixel on the minimap.
     * @return
     */
    public int adjustForLighting(PixelBuffer pix, int color, int xx, int yy) {
        final double DARKNESS_COEFFICIENT = 30000 * 2;
        double d = Math.pow(pix.getWidth() / 2 - xx, 2) + Math.pow(pix.getHeight() / 2 - yy, 2);
        color = ColorUtils.mixColor(color, 0x000000, (env.getDarkness()) / DARKNESS_COEFFICIENT * d);
        return color;
    }

}
