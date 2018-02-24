package com.knightlore.render;

import com.knightlore.render.graphic.Graphic;

/**
 * A pixel buffer is a 2D array of pixels that can be drawn to the screen. The
 * game has a main pixel buffer, which all of the game content is rendered to.
 * Additionally, pixel buffers can be composited on top of one another.
 * 
 * @author Joe Ellis
 *
 */
public class PixelBuffer {

    /**
     * Pure green chroma key. Pixels rendered with this colour will be ignored.
     */
    public static final int CHROMA_KEY = -16711936;

    private final int WIDTH, HEIGHT;
    private int[] pixels;

    public PixelBuffer(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        pixels = new int[WIDTH * HEIGHT];
    }

    /**
     * Fill the pixel buffer with a single colour.
     * 
     * @param color
     *            the color to fill the pixel buffer with.
     */
    public void flood(int color) {
        fillRect(color, 0, 0, getWidth(), getHeight());
    }

    public void composite(PixelBuffer pix, int x, int y) {
        for (int yy = 0; yy < pix.getHeight(); yy++) {
            for (int xx = 0; xx < pix.getWidth(); xx++) {
                int myX = x + xx, myY = y + yy;
                fillPixel(pix.pixelAt(xx, yy), myX, myY);
            }
        }
    }

    public void drawGraphic(Graphic graphic, int x, int y) {
        drawGraphic(graphic, x, y, 1, 1);
    }

    public void drawGraphic(Graphic graphic, int x, int y, int scaleX, int scaleY) {
        for (int yy = 0; yy < graphic.getHeight() * scaleY; yy++) {
            for (int xx = 0; xx < graphic.getWidth() * scaleX; xx++) {
                int drawX = x + xx;
                int drawY = y + yy;
                if (drawX < 0 || drawX >= WIDTH || drawY < 0 || drawY >= HEIGHT)
                    continue;
                int color = graphic.getPixels()[xx / scaleX + (yy / scaleY) * graphic.getWidth()];
                fillPixel(color, drawX, drawY);
            }
        }
    }

    public void copy(int[] c) {
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            c[i] = pixels[i];
        }
    }

    public void fillPixel(int color, int x, int y) {
        if (color == CHROMA_KEY)
            return;
        fillRect(color, x, y, 1, 1);
    }

    public void fillSquare(int color, int x, int y, int size) {
        fillRect(color, x, y, size, size);
    }

    public void fillRect(int color, int x, int y, int w, int h) {
        for (int yy = y; yy < y + h; yy++) {
            for (int xx = x; xx < x + w; xx++) {
                if (color == CHROMA_KEY || xx < 0 || xx >= WIDTH || yy < 0 || yy >= HEIGHT)
                    continue;
                pixels[xx + yy * WIDTH] = color;
            }
        }
    }

    public void fillOval(int color, int x, int y, int w, int h) {
        for (double i = 0; i < Math.PI; i += 0.01) {
            double xx = -Math.sin(i);
            double yy = Math.cos(i);

            int startX = (int) (w / 2 * (xx + 1));
            int startY = (int) (h / 2 * (yy + 1));
            int len = 2 * (w / 2 - startX);
            System.out.println(len);

            fillRect(color, x + startX, y + startY, len, 1);
        }
    }

    public int pixelAt(int x, int y) {
        int i = x + y * WIDTH;
        if (i < 0 || i >= pixels.length)
            return 0;
        return pixels[x + y * WIDTH];
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

}
