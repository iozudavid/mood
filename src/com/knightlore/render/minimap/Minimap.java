package com.knightlore.render.minimap;

import java.util.Iterator;
import java.util.function.DoubleUnaryOperator;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.world.ClientWorld;
import com.knightlore.render.Camera;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.filter.LightingMask;
import com.knightlore.utils.Vector2D;

/**
 * The minimap class. This is a small UI element that shows a birds-eye view of
 * the map the player is currently in. This class stores a bitmap representation
 * of the map (in pixelMap), which is updated periodically to reflect any
 * changes in the world.
 * <p>
 * The scale variable dictates how zoomed in we should display the map. The
 * larger it is, the more zoomed.
 * <p>
 * This class uses its own pixelbuffer to render. This is so that it can easily
 * be composited anywhere on another pixelbuffer. It saves us having to deal
 * with rendering the minimap relative to a given position.
 *
 * @author Joe Ellis
 */
public class Minimap implements TickListener {

    /*
     * NOTE: to render this, first call render(), then use getPixelBuffer() to
     * get the pixel buffer. Composite this onto any other pixel buffer using
     * the composite() method.
     */

    /**
     * The resolution of the minimap. This is the size to draw a single pixel. A
     * larger number will give you better performance, but 'poorer quality'.
     */
    public static final int RESOLUTION = 5;
    /**
     * The scope of the minimap. This range forms a box around the player.
     * Pixels beyond this range are not transformed/rendered, so the lower the
     * number the better the performance.
     * <p>
     * NOTE: you WILL need to change this value if you modify SCALE.
     */
    public static final int SCOPE = 90;
    /**
     * How zoomed in the minimap should appear. The higher this value, the more
     * zoomed in.
     */
    public final double scale = 8;
    private final PixelBuffer display;
    private final LightingMask mask;
    private final ClientWorld world;
    private int width, height;
    private int[] pixelMap;
    private DoubleUnaryOperator eq; // for lighting mask
    private Camera camera;

    public Minimap(Camera camera, ClientWorld world, int size) {
        this.camera = camera;
        this.world = world;
        recreatePixelMap();

        this.mask = new LightingMask(0x000000);
        this.display = new PixelBuffer(size, size);

        GameEngine.ticker.addTickListener(this);
    }

    /**
     * Updates the pixelbuffer 'display' to be the current appearance of the
     * minimap.
     */
    public void render() {
        if (camera == null || !camera.isSubjectSet()) {
            return;
        }

        Vector2D dir = camera.getDirection();
        double theta = -Math.atan2(dir.getX(), dir.getY());
        drawMap(theta);
        drawMinimapObjects(theta);

        drawBorder();
    }

    private void drawMap(double theta) {
        Vector2D pos = camera.getPosition();
        display.flood(0x000000);

        // Find the positions to start rendering based on SCOPE.
        int startX = (int)Math.max(0, pos.getX() * scale - SCOPE),
                endX = (int)Math.min(width, pos.getX() * scale + SCOPE);
        int startY = (int)Math.max(0, pos.getY() * scale - SCOPE),
                endY = (int)Math.min(height, pos.getY() * scale + SCOPE);

        // make sure we always start on a multiple of resolution to avoid weird
        // stuttering.
        startX -= startX % RESOLUTION;
        startY -= startY % RESOLUTION;

        for (int yy = startY; yy < endY; yy += RESOLUTION) {
            for (int xx = startX; xx < endX; xx += RESOLUTION) {
                Vector2D drawPos = transform(xx, yy, theta);

                int color = pixelMap[xx + yy * width];
                color = mask.augmentColor(eq, color, (int)drawPos.getX(), (int)drawPos.getY(), display.getWidth(),
                        display.getHeight());

                /*
                 * Finally, draw the pixel at the correct position. We draw a
                 * rectangle of size 2 as a really basic form of interpolation
                 * (so we don't get 'holes' in the minimap).
                 */
                final int INTERPOLATION_CONSTANT = 7;
                display.fillSquare(color, (int)drawPos.getX(), (int)drawPos.getY(), INTERPOLATION_CONSTANT);
            }
        }
    }

    private void drawMinimapObjects(double theta) {
        Iterator<Entity> iter = world.getEntityIterator();
        while (iter.hasNext()) {
            IMinimapObject obj = iter.next();
            Vector2D pos = obj.getPosition();
            pos = transform((int)(pos.getX() * scale), (int)(pos.getY() * scale), theta);

            int color = obj.getMinimapColor();
            color = mask.augmentColor(eq, color, (int)pos.getX(), (int)pos.getY(), display.getWidth(),
                    display.getHeight());
            display.fillSquare(color, (int)pos.getX(), (int)pos.getY(), (int)(obj.getDrawSize() * scale));
        }
    }

    private Vector2D transform(int xx, int yy, double theta) {
        final int size = display.getWidth();

        double drawX = xx, drawY = yy;
        drawX -= camera.getxPos() * scale;
        drawY -= camera.getyPos() * scale;

        drawX += size / 2;
        drawY += size / 2;

        drawY = size - drawY; // flip the map in the y-direction.

        // Now, rotate the image relative to the player position
        // according to the 2D rotation matrix.
        drawX -= size / 2;
        drawY -= size / 2;

        double oldDrawX = drawX;
        drawX = drawX * Math.cos(theta) - drawY * Math.sin(theta);
        drawY = oldDrawX * Math.sin(theta) + drawY * Math.cos(theta);

        drawX += size / 2;
        drawY += size / 2;

        return new Vector2D(drawX, drawY);
    }

    /**
     * Draws a border around the minimap.
     */

    private void drawBorder() {
        final int BORDER_COLOR = 0xFFFFFF;
        final int BORDER_WIDTH = 2;

        int w = display.getWidth(), h = display.getHeight();
        display.fillRect(BORDER_COLOR, 0, 0, w, BORDER_WIDTH);
        display.fillRect(BORDER_COLOR, 0, 0, BORDER_WIDTH, h);
        display.fillRect(BORDER_COLOR, 0, h - BORDER_WIDTH, w, BORDER_WIDTH);
        display.fillRect(BORDER_COLOR, w - BORDER_WIDTH, 0, BORDER_WIDTH, h);
    }

    /**
     * The map can change at runtime, and we need some way to reflect this in
     * the minimap. This method is called using the GameEngine's main ticker
     * periodically. It converts the map into an array of pixels (a bitmap) of
     * the required scale/resolution. We then transform this bitmap according to
     * the position and direction of the player in the render method.
     */
    private void recreatePixelMap() {
        Map map = world.getMap();
        this.width = (int)(map.getWidth() * scale);
        this.height = (int)(map.getHeight() * scale);
        this.pixelMap = new int[width * height];

        final int base = world.getEnvironment().getMinimapBaseColor();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = map.getTile((int)(x / scale), (int)(y / scale));
                int tileColor = t.getMinimapColor();
                pixelMap[x + y * width] = tileColor != 0 ? tileColor : base;
            }
        }

        final double DARKNESS_COEFFICIENT = 50000D;
        eq = (double distance) -> (world.getEnvironment().getDarkness()) / DARKNESS_COEFFICIENT * distance;
    }

    public PixelBuffer getPixelBuffer() {
        return display;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void onTick() {
        recreatePixelMap();
    }

    @Override
    public long interval() {
        // 3 seconds, no matter what the value of UPDATES_PER_SECOND is.
        return (long)(3 * GameEngine.UPDATES_PER_SECOND);
    }

}
