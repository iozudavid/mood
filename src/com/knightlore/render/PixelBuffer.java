package com.knightlore.render;

import java.util.HashMap;

import com.knightlore.render.font.Font;
import com.knightlore.render.graphic.Graphic;

/**
 * A pixel buffer is a 2D array of pixels that can be drawn to the screen. The
 * game has a main pixel buffer, which all of the game content is rendered to.
 * Additionally, pixel buffers can be composited on top of one another.
 * <p>
 * Pixel buffers use an integer array to represent the pixels under the hood,
 * but offer numerous helper methods for drawing, etc.
 *
 * @author Joe Ellis
 */
public class PixelBuffer {
    
    /**
     * Pure green chroma key. Pixels rendered with this colour will be ignored.
     */
    public static final int CHROMA_KEY = 0xFF00FF00;
    
    private final int WIDTH, HEIGHT;
    private final int[] pixels;
    private final HashMap<TextWidthCache, Integer> twCache = new HashMap<>();
    
    public PixelBuffer(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        pixels = new int[WIDTH * HEIGHT];
    }
    
    /**
     * Fill the pixel buffer with a single colour.
     *
     * @param color the colour to fill the pixel buffer with.
     */
    public void flood(int color) {
        for (int yy = 0; yy < getHeight(); yy++) {
            for (int xx = 0; xx < getWidth(); xx++) {
                if (!inBounds(xx, yy)) {
                    continue;
                }
                pixels[xx + yy * WIDTH] = color;
            }
        }
        // fillRect(color, 0, 0, getWidth(), getHeight());
    }
    
    /**
     * Composites a new pixel buffer onto this one at the given x and y
     * coordinates.
     *
     * @param pix the pixel buffer for compositing.
     * @param x   the x-position of where to composite.
     * @param y   the y-position of where to composite.
     */
    public void composite(PixelBuffer pix, int x, int y) {
        for (int yy = 0; yy < pix.getHeight(); yy++) {
            for (int xx = 0; xx < pix.getWidth(); xx++) {
                int myX = x + xx, myY = y + yy;
                fillPixel(pix.pixelAt(xx, yy), myX, myY);
            }
        }
    }
    
    /**
     * Draws a graphic at the given x and y position, scaled as necessary.
     *
     * @param graphic the graphic (class: Graphic) to render.
     * @param x       the x-position of where to render.
     * @param y       the y-position of where to render.
     */
    public void drawGraphic(Graphic graphic, int x, int y, int width, int height) {
        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                int drawX = x + xx;
                int drawY = y + yy;
                
                int texX = (int)((xx / (double)width) * graphic.getWidth());
                int texY = (int)((yy / (double)height) * graphic.getHeight());
                int color = graphic.getPixels()[texY * graphic.getWidth() + texX];
                fillPixel(color, drawX, drawY);
            }
        }
    }
    
    /**
     * Fills a single pixel to a given colour.
     *
     * @param color the colour to fill the pixel.
     * @param x     the x-position of the pixel to fill.
     * @param y     the y-position of the pixel to fill.
     */
    public void fillPixel(int color, int x, int y) {
        fillSquare(color, x, y, 1);
    }
    
    /**
     * Fills a square of a given side length.
     *
     * @param color the colour to make the square.
     * @param x     the x-position of the top-left corner of the square.
     * @param y     the y-position of the top-left corner of the square.
     * @param size  the side length of the square.
     */
    public void fillSquare(int color, int x, int y, int size) {
        fillRect(color, x, y, size, size);
    }
    
    /**
     * Fills a rectangle of the right dimensions.
     *
     * @param color the colour to make the rectange.
     * @param x     the x-position of the top-left corner of the rectangle.
     * @param y     the y-position of the top-left corner of the rectangle.
     * @param w     the width of the rectangle.
     * @param h     the height of the rectangle.
     */
    public void fillRect(int color, int x, int y, int w, int h) {
        if (color == CHROMA_KEY) {
            return;
        }
        
        for (int yy = y; yy < y + h; yy++) {
            for (int xx = x; xx < x + w; xx++) {
                if (!inBounds(xx, yy)) {
                    continue;
                }
                pixels[xx + yy * WIDTH] = color;
            }
        }
    }
    
    public void drawRect(int color, int x, int y, int w, int h) {
        if (color == CHROMA_KEY) {
            return;
        }
        drawLine(color, x, y, x + w, y);
        drawLine(color, x, y + h, x + w, y + h);
        drawLine(color, x, y, x, y + h);
        drawLine(color, x + w, y, x + w, y + h);
    }
    
    /**
     * Fills an oval of the given colour, width and height.
     *
     * @param color  the colour to draw the oval.
     * @param x      the x-position of the square bounding the oval.
     * @param y      the y-position of the square bounding the oval.
     * @param w      the width of the circle.
     * @param h      the height of the circle.
     * @param points the number of points to draw the oval with.
     */
    public void fillOval(int color, int x, int y, int w, int h, int points) {
        for (double i = 0; i < Math.PI; i += Math.PI / points) {
            double xx = -Math.sin(i);
            double yy = Math.cos(i);
            
            int startX = (int)(w / 2 * (xx + 1));
            int startY = (int)(h / 2 * (yy + 1));
            int len = 2 * (w / 2 - startX);
            
            fillRect(color, x + startX, y + startY, len, 1);
        }
    }
    
    /**
     * Draws a line starting at (x1, y1) to (x2, y2) using Bresenham's line
     * drawing algorithm.
     *
     * @param color
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine(int color, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1, dy = y2 - y1;
        if (dx == 0) {
            fillRect(color, x1, y1, 1, y2 - y1);
        }
        
        double de = Math.abs(dy / dx);
        double e = 0.0;
        
        int y = y1;
        for (int x = x1; x < x2; x++) {
            fillPixel(color, x, y);
            e += de;
            while (e >= 0.5) {
                y += Math.signum(dy);
                e -= 1.0;
                fillPixel(color, x, y);
            }
        }
    }
    
    /**
     * Draws a string on the pixel buffer.
     *
     * @param font    the font to use.
     * @param str     the string to render.
     * @param x       the x-position of the top-left of the string.
     * @param y       the y-position of the top-left of the string.
     * @param scaling the scale of the string.
     * @param spacing the spacing between letters.
     */
    public void drawString(Font font, String str, int x, int y, double scaling, double spacing) {
        if (!inBounds(x, y)) {
            return;
        }
        
        for (char c : str.toCharArray()) {
            Graphic g = font.getGraphic(c);
            this.drawGraphic(g, x, y, (int)(g.getWidth() * scaling), (int)(g.getHeight() * scaling));
            x += g.getWidth() * scaling + spacing;
        }
    }
    
    /**
     * Computes the width of a string if it were to be rendered on the pixel
     * buffer.
     * <p>
     * This is cached.
     *
     * @param font    the font to use.
     * @param str     the relevant string.
     * @param scaling the scale of the render.
     * @param spacing the spacing between letters.
     * @return the width of the string on the pixel buffer.
     */
    public int stringWidth(Font font, String str, double scaling, double spacing) {
        TextWidthCache cached = new TextWidthCache(font, str, scaling, spacing);
        if (twCache.containsKey(cached)) {
            return twCache.get(cached);
        }
        
        int width = 0;
        for (char c : str.toCharArray()) {
            Graphic g = font.getGraphic(c);
            width += g.getWidth() * scaling + spacing;
        }
        
        int retval = (int)(width - spacing);
        twCache.put(cached, retval);
        return retval;
    }
    
    /**
     * Gets the colour of the pixel at a given x-y position.
     *
     * @param x the x-position.
     * @param y the y-position.
     * @return the colour at the x-y position -- 0 if it is out of bounds.
     */
    public int pixelAt(int x, int y) {
        if (!inBounds(x, y)) {
            return 0;
        }
        return pixels[x + y * WIDTH];
    }
    
    /**
     * Copies the contents of this pixel buffer to a given integer array.
     *
     * @param c the integer array that you want to copy the buffer into.
     */
    public void copy(int[] c) {
        System.arraycopy(pixels, 0, c, 0, WIDTH * HEIGHT);
    }
    
    /**
     * Internal method -- checks whether the given x-y position is within the
     * bounds of the pixel buffer.
     *
     * @param x the x-position to check.
     * @param y the y-position to check.
     * @return true if x-y is in bounds, false otherwise.
     */
    private boolean inBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }
    
    public int getWidth() {
        return WIDTH;
    }
    
    public int getHeight() {
        return HEIGHT;
    }
    
    private class TextWidthCache {
        final Font font;
        final String string;
        final double scaling;
        final double spacing;
        
        public TextWidthCache(Font font, String string, double scaling, double spacing) {
            this.font = font;
            this.string = string;
            this.scaling = scaling;
            this.spacing = spacing;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof TextWidthCache)) {
                return false;
            }
            
            TextWidthCache cache = (TextWidthCache)obj;
            return cache.font == font && cache.string.equals(string) && cache.scaling == scaling
                    && cache.spacing == spacing;
        }
        
        @Override
        public int hashCode() {
            return (int)(scaling * spacing * string.length());
        }
        
    }
    
}
