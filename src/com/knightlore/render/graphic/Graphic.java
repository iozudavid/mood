package com.knightlore.render.graphic;

import java.awt.image.BufferedImage;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.filter.ColorFilter;

public class Graphic {

    public static final Graphic EMPTY = GraphicSheet.TEXTURES.graphicAt(3, 3);

    protected int width, height;
    protected int[] pixels;

    private BufferedImage img;

    public Graphic(int width, int height, int[] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public Graphic(BufferedImage img) {
        this(img, null);
    }

    public Graphic(BufferedImage img, ColorFilter filter) {
        this.width = img.getWidth();
        this.height = img.getHeight();
        this.pixels = new int[width * height];

        img.getRGB(0, 0, width, height, pixels, 0, width);

        if (filter != null) {
            filter.apply(pixels, PixelBuffer.CHROMA_KEY);
        }
    }

    public BufferedImage getImage() {
        return this.img;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return Math.max(width, height);
    }

}
