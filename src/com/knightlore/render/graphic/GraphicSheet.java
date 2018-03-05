package com.knightlore.render.graphic;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.filter.ColorFilter;

public class GraphicSheet {

    public final static GraphicSheet TEXTURES = new GraphicSheet("res/graphics/textures.png", 16);
    public final static GraphicSheet WEAPONS = new GraphicSheet("res/graphics/weapon_sprites.png", 128);

    public final static GraphicSheet GENERAL_SPRITES = new GraphicSheet("res/graphics/sprites.png", 64);

    public final static GraphicSheet PLAYER_SPRITES = new GraphicSheet("res/models/player_sprites.png", 128);
    public final static GraphicSheet RED_PLAYER_SPRITES = new GraphicSheet("res/models/player_sprites.png", 128,
            ColorFilter.RED);
    public final static GraphicSheet BLUE_PLAYER_SPRITES = new GraphicSheet("res/models/player_sprites.png", 128,
            ColorFilter.BLUE);

    public final static GraphicSheet SHOTGUN_SPRITES = new GraphicSheet("res/models/shotgun_sprites.png", 128);
    public final static GraphicSheet RED_SHOTGUN_SPRITES = new GraphicSheet("res/models/shotgun_sprites.png", 128,
            ColorFilter.RED);
    public final static GraphicSheet BLUE_SHOTGUN_SPRITES = new GraphicSheet("res/models/shotgun_sprites.png", 128,
            ColorFilter.BLUE);

    public final static GraphicSheet TURRET_SPRITES = new GraphicSheet("res/models/turret_sprites.png", 128);

    private final int cellSize;
    private BufferedImage sheet;
    private ColorFilter filter;

    public GraphicSheet(String path, int cellSize) {
        this(path, cellSize, null);
    }

    public GraphicSheet(String path, int cellSize, ColorFilter filter) {
        this.cellSize = cellSize;
        this.filter = filter;
        load(path);
    }

    private void load(String path) {
        try {
            sheet = ImageIO.read(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Graphic graphicAt(int x, int y) {
        return graphicAt(x, y, 1, 1);
    }

    public Graphic graphicAt(int x, int y, int xx, int yy) {
        BufferedImage subImg = sheet.getSubimage(x * cellSize, y * cellSize, xx * cellSize, yy * cellSize);
        Graphic graphic = new Graphic(subImg);
        if (filter != null) {
            filter.apply(graphic.getPixels(), PixelBuffer.CHROMA_KEY);
        }
        return graphic;
    }

}
