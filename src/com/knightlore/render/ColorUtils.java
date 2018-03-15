package com.knightlore.render;

import java.awt.Color;

/**
 * Numerous helper functions for working with colours.
 * 
 * @author Joe Ellis
 *
 */
public final class ColorUtils {

    // Defeat instatiation.
    private ColorUtils() {
    }

    /**
     * Mixes two colours together to a given proportion.
     * 
     * @param color1
     *            the first colour.
     * @param color2
     *            the second colour.
     * @param mix
     *            the proportion of the blend. 0.5 is an even mix. <0.5 leans
     *            more toward color1 and >0.5 leans more toward color2.
     * @return
     */
    public static int mixColor(int color1, int color2, double mix) {
        mix = Math.min(1, Math.max(0, mix));
        Color c1 = new Color(color1);
        Color c2 = new Color(color2);

        int r = (int) (c1.getRed() * (1 - mix) + c2.getRed() * mix);
        int g = (int) (c1.getGreen() * (1 - mix) + c2.getGreen() * mix);
        int b = (int) (c1.getBlue() * (1 - mix) + c2.getBlue() * mix);
        Color result = new Color(r, g, b);

        return result.getRGB();
    }

    /**
     * Darkens a colour according to a distance.
     * 
     * @param color
     *            the colour to darken.
     * @param environmentDarkFactor
     *            the darkness factor of the environment.
     * @param distance
     *            the distance.
     * @return the colour after the darkening.
     */
    public static int darken(int color, double environmentDarkFactor, double distance) {
        Color c = new Color(color);
        double fogFactor = distance * environmentDarkFactor;
        int red = (int) (Math.max(0, c.getRed() - fogFactor));
        int green = (int) (Math.max(0, c.getGreen() - fogFactor));
        int blue = (int) (Math.max(0, c.getBlue() - fogFactor));
        return new Color(red, green, blue).getRGB();
    }

    /**
     * Quick and dirty colour darken using bitwise operations.
     * 
     * @param color
     *            the colour the darken.
     * @return the colour darkened.
     */
    public static int quickDarken(int color) {
        return (color >> 1) & 0x7F7F7F;
    }

    /**
     * Calculates the average colour in an array of pixels. Averages each of the
     * r, g and b components and creates a new colour (avg(r), avg(g), avg(b)).
     * 
     * @param pixels
     *            the array of pixels to find the average colour of.
     * @return the average colour.
     */
    public static int averageColor(int[] pixels) {
        int r = 0, g = 0, b = 0;
        for (int p : pixels) {
            r += (p >> 16) & 255;
            g += (p >> 8) & 255;
            b += p & 255;
        }
        r /= pixels.length;
        g /= pixels.length;
        b /= pixels.length;

        return (r << 16) + (g << 8) + b;
    }

}
