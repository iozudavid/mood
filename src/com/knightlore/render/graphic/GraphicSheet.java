package com.knightlore.render.graphic;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.filter.ColorFilter;

/**
 * A graphicsheet reads in an image file from the disk and divides it down into
 * fixed-sized segments. You can query a graphicsheet to get the graphic at a
 * specific position.
 * 
 * @author Joe Ellis
 *
 */
public class GraphicSheet {

    /**
     * This graphicsheet is to store textures which are applied to tiles.
     */
    public final static GraphicSheet TEXTURES = new GraphicSheet("res/graphics/textures.png", 16);

    public final static GraphicSheet RED_TEXTURES = new GraphicSheet("res/graphics/textures.png", 16, ColorFilter.RED);
    public final static GraphicSheet BLUE_TEXTURES = new GraphicSheet("res/graphics/textures.png", 16,
            ColorFilter.BLUE);
    /**
     * This graphicsheet stores weapon graphics for the player's point of view.
     */
    public final static GraphicSheet WEAPONS = new GraphicSheet("res/graphics/weapon_sprites.png", 128);

    /**
     * A graphicsheet for the player directional sprite. The PLAYER_SPRITES is
     * plain white. The coloured player sprites have a colour filter applied to
     * them.
     */
    public final static GraphicSheet ZOMBIE_SPRITES = new GraphicSheet("res/models/player_sprites.png", 128);

    /**
     * A graphicsheet for the shotgun directional sprite.
     */
    public final static GraphicSheet SHOTGUN_SPRITES = new GraphicSheet("res/models/shotgun_sprites.png", 128);

    /**
     * A graphicsheet for the shotgun directional sprite.
     */
    public final static GraphicSheet PISTOL_SPRITES = new GraphicSheet("res/models/pistol_static_sprites.png", 128);

    /**
     * A graphicsheet for the turret directional sprite.
     */
    public final static GraphicSheet TURRET_SPRITES = new GraphicSheet("res/models/turret_sprites.png", 128);

    /**
     * A graphicsheet for the spectator camera directional sprite.
     */
    public final static GraphicSheet CAMERA_SPRITES = new GraphicSheet("res/models/camera_sprites.png", 128);

    /**
     * A graphicsheet for the gravestone directional sprite that appears when a
     * player dies.
     */
    public final static GraphicSheet GRAVESTONE_SPRITES = new GraphicSheet("res/models/gravestone_sprites.png", 128);
    
    public final static GraphicSheet SPEED_SPRITES = new GraphicSheet("res/models/speedup_static_sprites.png",128);
    
    public final static GraphicSheet HEALTHKIT_SPRITES = new GraphicSheet("res/models/healthkit_static_sprites.png",
            128);

    private final int cellSize;
    private BufferedImage sheet;

    private Map<GraphicCacheItem, Graphic> cache;

    /**
     * A colour filter for the graphicsheet. This can be used to give the models
     * in the graphicsheet a different colour tint.
     */
    private ColorFilter filter;

    public GraphicSheet(String path, int cellSize) {
        this(path, cellSize, null);
    }

    public GraphicSheet(String path, int cellSize, ColorFilter filter) {
        this.cellSize = cellSize;
        this.filter = filter;
        this.cache = new HashMap<GraphicCacheItem, Graphic>();
        load(path);
    }

    /**
     * Loads the graphicsheet from the given path.
     * 
     * @param path
     *            the path to the graphicsheet image.
     */
    private void load(String path) {
        try {
            sheet = ImageIO.read(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the graphic at the given x-y position.
     * 
     * @param x
     *            the x-position of the graphic in the sheet.
     * @param y
     *            the y-position of the graphic in the sheet.
     * @return the graphic at the x-y position.
     */
    public Graphic graphicAt(int x, int y) {
        return graphicAt(x, y, 1, 1);
    }

    /**
     * Gets the graphic bounded by the rectangle starting from (x, y) and ending
     * at (xx, yy).
     * 
     * @param x
     *            the x-position of the top-left of the graphic.
     * @param y
     *            the y-position of the top-left of the graphic.
     * @param xx
     *            the x-position of the bottom-right of the graphic.
     * @param yy
     *            the y-position of the bottom-right of the graphic.
     * @return the graphic bounded by the rectangle starting from (x, y) and
     *         ending at (xx, yy).
     */
    public Graphic graphicAt(int x, int y, int xx, int yy) {
        GraphicCacheItem item = new GraphicCacheItem(x, y, xx, yy);
        if (cache.containsKey(item)) {
            return cache.get(item);
        }

        BufferedImage subImg = sheet.getSubimage(x * cellSize, y * cellSize, xx * cellSize, yy * cellSize);
        Graphic graphic = new Graphic(subImg);
        if (filter != null) {
            filter.apply(graphic.getPixels(), PixelBuffer.CHROMA_KEY);
        }

        // add to cache
        cache.put(item, graphic);

        return graphic;
    }

    class GraphicCacheItem {
        public int x, y, xx, yy;

        public GraphicCacheItem(int x, int y, int xx, int yy) {
            this.x = x;
            this.y = y;
            this.xx = xx;
            this.yy = yy;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (!(obj instanceof GraphicCacheItem))
                return false;

            GraphicCacheItem c = (GraphicCacheItem) obj;
            return x == c.x && y == c.y && xx == c.xx && yy == c.yy;
        }

        @Override
        public int hashCode() {
            return x * y * xx * yy;
        }

    }

}
