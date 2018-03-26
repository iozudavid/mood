package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;

/**
 * Class used to render images.
 *
 * @author David Iozu
 */
public class Image extends GUIObject {
    
    public Graphic graphic;
    public BufferedImage sheet;
    public boolean needBackground = false;
    
    public Image(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
    }
    
    public Image(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public Image(int x, int y, int width, int height, String path) {
        this(x, y, width, height, 0, path);
    }
    
    public Image(int x, int y, int width, int height, int depth, String path) {
        super(x, y, width, height, depth);
        try {
            this.sheet = ImageIO.read(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.graphic = new Graphic(sheet);
    }
    
    /**
     * Resized the image to the given parameters.
     *
     * @param img  - image to be resized
     * @param newW - width to be resized
     * @param newH -height to be resized
     * @return the resized image
     */
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        java.awt.Image tmp = img.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = dimg.createGraphics();
        g2d.setBackground(new Color(1f, 0f, 0f, 1f));
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        
        return dimg;
    }
    
    /**
     * Draw the given image.
     */
    @Override
    void Draw(PixelBuffer pix, int x, int y) {
        if (this.needBackground) {
            int color = GuiUtils.makeTransparent(new Color(0), 255).getRGB();
            pix.fillRect(color, rect.x, rect.y, rect.width, rect.height);
        }
        pix.drawGraphic(graphic, rect.x, rect.y, rect.width, rect.height);
    }
    
}
