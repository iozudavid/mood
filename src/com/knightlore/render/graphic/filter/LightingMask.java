package com.knightlore.render.graphic.filter;

import com.knightlore.render.ColorUtils;
import com.knightlore.render.PixelBuffer;

/**
 * Masks can be applied to a pixel buffer to give a 'faded edges' look.
 * 
 * @author Joe Ellis
 *
 */
public class LightingMask {

    /**
     * The colour of the lighting mask.
     */
    private int color;

    public LightingMask(int color) {
        this.color = color;
    }

    /**
     * Apply a lighting mask to an entire pixelbuffer. Warning: this is
     * generally slow.
     * 
     * @param pix
     *            the pixel buffer.
     * @param eq
     *            the lighting mask equation.
     */
    public void apply(PixelBuffer pix, LightingMaskEquation eq) {
        for (int y = 0; y < pix.getHeight(); y++) {
            for (int x = 0; x < pix.getWidth(); x++) {
                pix.fillPixel(augmentColor(eq, pix.pixelAt(x, y), x, y, pix.getWidth(), pix.getHeight()), x, y);
            }
        }
    }

    /**
     * Augments a single colour.
     * 
     * @param eq
     *            the lighting mask equation.
     * @param color
     *            the colour to augment.
     * @param x
     *            the x-position of the colour in the pixel buffer.
     * @param y
     *            the y-position of the colour in the pixel buffer.
     * @param w
     *            the width of the pixel buffer.
     * @param h
     *            the height of the pixel buffer.
     * @return the augmented colour.
     */
    public int augmentColor(LightingMaskEquation eq, int color, int x, int y, int w, int h) {
        double d = Math.pow(w / 2 - x, 2) + Math.pow(h / 2 - y, 2);
        double mix = eq.getMix(d);
        return ColorUtils.mixColor(color, this.color, mix);
    }

    /**
     * This interface defines a function that is used to compute the
     * intensity/gradient of the lighting mask applied. The value returned by
     * the getMix() function is how much the current colour should be mixed with
     * the base colour.
     * 
     * @author Joe Ellis
     *
     */
    public interface LightingMaskEquation {

        /**
         * Defines how much this colour should be mixed with the base colour
         * given how far away the point is from the centre of the pixel buffer.
         * 
         * @param distance
         *            distance from the centre of the pixel buffer.
         * @return
         */
        public double getMix(double distance);

    }

}
