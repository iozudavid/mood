package com.knightlore.gui;

import java.awt.Color;
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
