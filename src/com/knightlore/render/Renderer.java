package com.knightlore.render;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Zombie;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.gui.GUICanvas;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.minimap.Minimap;
import com.knightlore.utils.Vector2D;
import com.knightlore.world.GameWorld;

public class Renderer implements IRenderable {

    /**
     * Viewport into the world. Can be bound to any entity.
     */
    private Camera camera;

    /**
     * The world to render.
     */
    private GameWorld world;

    private Minimap minimap;

    private GUICanvas gui;

    private java.util.Map<NetworkObject, Vector2D> networkObjPos;
    // consider other players as mobs for now
    // in order to render them
    private java.util.Map<NetworkObject, Entity> networkObjMobs;

    public Renderer(Camera camera, GameWorld world) {
        this.camera = camera;
        this.world = world;
        this.minimap = null;

        this.networkObjPos = new HashMap<>();
        this.networkObjMobs = new HashMap<>();
        // FIXME: create this elsewhere.....
        new NetworkObjectManager();
    }

    private final int BLOCKINESS = 3; // how 'old school' you want to look.

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        // FIXME: HACK
        if (camera == null || minimap == null)
            return;

        // Draw the environment, as specified by the world.
        world.getEnvironment().renderEnvironment(pix);

        // draw the perspective and the crosshairs
        drawPerspective(pix);
        drawCrosshair(pix);

        if (gui != null) {
            gui.render(pix, x, y);
        }

        minimap.render();

        PixelBuffer minimapBuffer = minimap.getPixelBuffer();
        pix.composite(minimapBuffer, pix.getWidth() - minimapBuffer.getWidth() - 10, 5);
    }

    public void updateNetworkObjectPos(NetworkObject obj, Vector2D position, Vector2D direction) {
        if (camera == null)
            return;

        List<Entity> entities = world.getEntities();

        synchronized (entities) {
            if (position == null) {
                this.networkObjPos.remove(obj);
                entities.remove(this.networkObjMobs.get(obj));
                this.minimap.removeMinimapObject(this.networkObjMobs.get(obj));
                this.networkObjMobs.remove(obj);
            } else if (networkObjPos.containsKey(obj)) {
                this.minimap.removeMinimapObject(this.networkObjMobs.get(obj));
                this.networkObjPos.put(obj, position);
                entities.remove(this.networkObjMobs.get(obj));
                Zombie z = new Zombie(1D, position, direction);
                this.networkObjMobs.put(obj, z);
                entities.add(z);
                this.minimap.addMinimapObject(this.networkObjMobs.get(obj));
            } else {
                this.networkObjPos.put(obj, position);
                Zombie z = new Zombie(1D, position, direction);
                this.networkObjMobs.put(obj, z);
                entities.add(z);
                this.minimap.addMinimapObject(this.networkObjMobs.get(obj));
            }
        }
    }

    /*
     * NOTE: THIS ONLY AFFECTS THE RENDERING SIZE OF A TILE. If you change this
     * variable, tiles will be drawn differently but the player will still move
     * at their usual speed over a single tile. You might want to compensate a
     * change here with a change in player move speed.
     */
    private final float TILE_SIZE = 1F;

    private void drawPerspective(PixelBuffer pix) {

        // Can't render without a camera.
        if (camera == null)
            return;

        Map map = world.getMap();

        final int width = pix.getWidth(), height = pix.getHeight();
        double[] zbuffer = new double[width];

        /*
         * SEE: PerspectiveRenderItem.java for how this works. In short: we keep
         * adding new render items to a stack until we reach an opaque block.
         * The stack is then popped and rendered in turn.
         */
        Stack<PerspectiveRenderItem> renderStack = new Stack<PerspectiveRenderItem>();

        for (int xx = 0; xx < width; xx += BLOCKINESS) {
            // Calculate direction of the ray based on camera direction.
            double cameraX = 2 * xx / (double) (width) - 1;
            double rayX = camera.getxDir() + camera.getxPlane() * cameraX;
            double rayY = camera.getyDir() + camera.getyPlane() * cameraX;

            // Round the camera position to the nearest map cell.
            int mapX = (int) camera.getxPos();
            int mapY = (int) camera.getyPos();

            double sideDistX;
            double sideDistY;

            // Length of ray from one side to next in map
            double deltaDistX = Math.sqrt(1 + (rayY * rayY) / (rayX * rayX));
            double deltaDistY = Math.sqrt(1 + (rayX * rayX) / (rayY * rayY));

            double distanceToWall;

            // hit = true when the ray has hit a wall.
            // side = true or false depending on the side.
            boolean hit = false, side = false;

            // find x and y components of the ray vector.
            int stepX, stepY;
            if (rayX < 0) {
                stepX = -1;
                sideDistX = (camera.getxPos() - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - camera.getxPos()) * deltaDistX;
            }

            if (rayY < 0) {
                stepY = -1;
                sideDistY = (camera.getyPos() - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - camera.getyPos()) * deltaDistY;
            }

            // Loop to find where the ray hits a wall
            while (!hit) {

                // Next cell
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = false;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = true;
                }

                Tile tile = map.getTile(mapX, mapY);

                // If this is anything but an empty cell, we've hit a tile
                if (tile != AirTile.getInstance()) {

                    double opacity = tile.getOpacity();
                    if (opacity >= 1)
                        hit = true;

                    distanceToWall = RaycasterUtils.getImpactDistance(camera, rayX, rayY, mapX, mapY, side, stepX,
                            stepY, TILE_SIZE);
                    int lineHeight = RaycasterUtils.getDrawHeight(height, distanceToWall);

                    // calculate lowest and highest pixel to fill in current
                    // strip
                    int drawStart = -lineHeight / 2 + height / 2;
                    if (drawStart < 0) {
                        drawStart = 0;
                    }

                    int drawEnd = lineHeight / 2 + height / 2;
                    if (drawEnd >= height) {
                        drawEnd = height - 1;
                    }

                    double wallX = RaycasterUtils.getWallHitPosition(camera, rayX, rayY, mapX, mapY, side, stepX,
                            stepY);

                    Graphic texture = map.getTile(mapX, mapY).getTexture();

                    // What pixel did we hit the texture on?
                    int texX = (int) (wallX * (texture.getSize()));
                    if (side && rayY < 0) {
                        texX = texture.getSize() - texX - 1;
                    }

                    if (!side && rayX > 0) {
                        texX = texture.getSize() - texX - 1;
                    }

                    PerspectiveRenderItem p = new PerspectiveRenderItem(opacity, drawStart, drawEnd, lineHeight,
                            texture, texX, distanceToWall, xx);
                    renderStack.push(p);
                    zbuffer[xx] = p.distanceToWall;

                }

            }

            while (!renderStack.isEmpty()) {
                draw(pix, renderStack.pop());
            }

        }

        drawSprites(pix, zbuffer);

    }

    private void draw(PixelBuffer pix, PerspectiveRenderItem p) {
        // calculate y coordinate on texture
        for (int yy = p.drawStart; yy < p.drawEnd; yy++) {

            int texY = (((yy * 2 - pix.getHeight() + p.lineHeight) << 4) / p.lineHeight) / 2;

            int color = p.texture.getPixels()[p.texX + (texY * p.texture.getSize())];

            int drawY = yy + camera.getMotionOffset();
            color = ColorUtils.mixColor(pix.pixelAt(p.xx, drawY), color, p.opacity);

            color = ColorUtils.darken(color, world.getEnvironment().getDarkness(), p.distanceToWall);
            pix.fillRect(color, p.xx, drawY, BLOCKINESS, 1);
        }
    }

    private void drawSprites(PixelBuffer pix, double[] zbuffer) {
        List<Entity> entities = world.getEntities();
        synchronized (entities) {
            entities.sort(new Comparator<Entity>() {

                @Override
                public int compare(Entity o1, Entity o2) {
                    final double distance1 = camera.getPosition().distance(o1.getPosition());
                    final double distance2 = camera.getPosition().distance(o2.getPosition());
                    return Double.compare(distance2, distance1);
                }

            });

            for (Entity m : entities) {
                double spriteX = m.getPosition().getX() - camera.getxPos();
                double spriteY = m.getPosition().getY() - camera.getyPos();

                double invDet = 1.0 / (camera.getxPlane() * camera.getyDir() - camera.getxDir() * camera.getyPlane());

                double transformX = invDet * (camera.getyDir() * spriteX - camera.getxDir() * spriteY);
                double transformY = invDet * (-camera.getyPlane() * spriteX + camera.getxPlane() * spriteY);

                int zMoveScreen = (int) (m.getzOffset() / transformY);

                int spriteScreenX = (int) ((pix.getWidth() / 2) * (1 + transformX / transformY));
                int spriteHeight = Math.abs((int) (pix.getHeight() / transformY));

                // calculate lowest and highest pixel
                int drawStartY = -spriteHeight / 2 + pix.getHeight() / 2 + zMoveScreen;
                drawStartY = Math.max(0, drawStartY);
                int drawEndY = spriteHeight / 2 + pix.getHeight() / 2 + zMoveScreen;
                drawEndY = Math.min(drawEndY, pix.getHeight() - 1);

                // calculate width of the sprite
                int spriteWidth = Math.abs((int) (pix.getHeight() / transformY));
                int drawStartX = -spriteWidth / 2 + spriteScreenX;
                if (drawStartX < 0)
                    drawStartX = 0;
                int drawEndX = spriteWidth / 2 + spriteScreenX;
                if (drawEndX >= pix.getWidth())
                    drawEndX = pix.getWidth() - 1;

                // loop through every vertical stripe of the sprite on screen
                for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
                    Graphic g = m.getGraphic(camera.getPosition());

                    int texX = (int) (256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * g.getWidth() / spriteWidth)
                            / 256;

                    // the conditions in the if are:
                    // 1) it's in front of camera plane so you don't see things
                    // behind you
                    // 2) it's on the screen (left)
                    // 3) it's on the screen (right)
                    // 4) ZBuffer, with perpendicular distance
                    if (transformY > 0 && stripe > 0 && stripe < pix.getWidth() && transformY < zbuffer[stripe])
                        for (int y = drawStartY; y < drawEndY; y++) {
                            // 16 and 8 are factors to avoid division and
                            // floats.
                            int d = 16 * (y - zMoveScreen) - 8 * (pix.getHeight() - spriteHeight - 1);

                            int texY = ((d * g.getHeight()) / spriteHeight) / 16;

                            int color = g.getPixels()[texX + g.getWidth() * texY];

                            // dont draw transparent pixels.
                            if (color == PixelBuffer.CHROMA_KEY)
                                continue;

                            color = ColorUtils.darken(color, world.getEnvironment().getDarkness(),
                                    camera.getPosition().distance(m.getPosition()));

                            int drawY = y + camera.getMotionOffset();
                            pix.fillRect(color, stripe, drawY, BLOCKINESS, 1);
                        }
                }

            }
        }
    }

    private void drawCrosshair(PixelBuffer pix) {
        final int CROSSHAIR_SIZE = 10;
        final int CROSSHAIR_WIDTH = 2;
        final int CROSSHAIR_COLOR = 0xFFFFFF;
        final int w = pix.getWidth() / 2, h = pix.getHeight() / 2;
        pix.fillRect(CROSSHAIR_COLOR, w - CROSSHAIR_SIZE, h - CROSSHAIR_WIDTH / 2, CROSSHAIR_SIZE * 2, CROSSHAIR_WIDTH);
        pix.fillRect(CROSSHAIR_COLOR, w - CROSSHAIR_WIDTH / 2, h - CROSSHAIR_SIZE, CROSSHAIR_WIDTH, CROSSHAIR_SIZE * 2);
    }

    // FIXME
    public Map getMap() {
        return world.getMap();
    }

    public void setCamera(Camera cam) {
        // this.mobsToRender = new ArrayList<Entity>();
        this.camera = cam;
        // FIXME: put all this in the constructor when we get a camera on objet
        // creation.
        // ShotgunPickup p = new ShotgunPickup(cam.getPosition());
        // mobsToRender.add(p);
        this.minimap = new Minimap(camera, world, 128);
        // this.minimap.addMinimapObject(p);
        // propagate to minimap
        minimap.setCamera(cam);
    }
}