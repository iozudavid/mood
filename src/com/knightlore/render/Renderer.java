package com.knightlore.render;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import com.knightlore.GameSettings;
import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Player;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.world.ClientWorld;
import com.knightlore.render.font.Font;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.hud.HealthCounter;
import com.knightlore.utils.Vector2D;

/**
 * Renders the game content.
 *
 * @author Joe Ellis
 */
public class Renderer {
    
    private final PixelBuffer pix;
    
    /**
     * Viewport into the world. Can be bound to any entity.
     */
    private final Camera camera;
    
    /**
     * The world to render.
     */
    private final ClientWorld world;
    
    public Renderer(int width, int height, Camera camera, ClientWorld world) {
        this.pix = new PixelBuffer(width, height);
        this.camera = camera;
        this.world = world;
    }
    
    public void render() {
        if (camera == null || !camera.isSubjectSet()) {
            return;
        }
        
        if (camera.getSubject().getDirection().equals(Vector2D.ZERO, 0.01)) {
            return;
        }
        
        // draw the perspective and the crosshairs
        int offset = camera.getMotionBobOffset();
        
        try {
            drawPerspective(pix, offset);
            
            camera.render(pix, 0, 0);
            drawCrosshair(pix);
        } catch (Exception e) {
            // might miss a frame, but beats crashing :)
        }
    }
    
    private void drawPerspective(PixelBuffer pix, int offset) {
        Map map = world.getMap();
        
        final int width = pix.getWidth(), height = pix.getHeight();
        double[] zbuffer = new double[width];
        
        /*
         * SEE: PerspectiveRenderItem.java for how this works. In short: we keep
         * adding new render items to a stack until we reach an opaque block.
         * The stack is then popped and rendered in turn.
         */
        Stack<PerspectiveRenderItem> renderStack = new Stack<>();
        
        for (int xx = 0; xx < width; xx += GameSettings.actualBlockiness) {
            // Calculate direction of the ray based on camera direction.
            double cameraX = 2 * xx / (double)(width) - 1;
            double rayX = camera.getxDir() + camera.getxPlane() * cameraX;
            double rayY = camera.getyDir() + camera.getyPlane() * cameraX;
            
            // Round the camera position to the nearest map cell.
            int mapX = (int)camera.getxPos();
            int mapY = (int)camera.getyPos();
            
            double sideDistX;
            double sideDistY;
            
            // Length of ray from one side to next in map
            double deltaDistX = Math.sqrt(1 + (rayY * rayY) / (rayX * rayX));
            double deltaDistY = Math.sqrt(1 + (rayX * rayX) / (rayY * rayY));
            
            double wallX = 0;
            double distanceToWall = 0;
            
            int drawStart, drawEnd = 0;
            
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
                    if (opacity >= 1) {
                        hit = true;
                    }
                    
                    distanceToWall = RaycasterUtils.getImpactDistance(camera, rayX, rayY, mapX, mapY, side, stepX,
                            stepY);
                    int lineHeight = RaycasterUtils.getDrawHeight(height, distanceToWall);
                    
                    // calculate lowest and highest pixel to fill in current
                    // strip
                    drawStart = -lineHeight / 2 + height / 2;
                    if (drawStart < 0) {
                        drawStart = 0;
                    }
                    
                    drawEnd = lineHeight / 2 + height / 2;
                    if (drawEnd >= height) {
                        drawEnd = height - 1;
                    }
                    
                    wallX = RaycasterUtils.getWallHitPosition(camera, rayX, rayY, mapX, mapY, side, stepX, stepY);
                    
                    Graphic texture = map.getTile(mapX, mapY).getWallTexture();
                    
                    // What pixel did we hit the texture on?
                    int texX = (int)(wallX * (texture.getSize()));
                    if (side && rayY < 0) {
                        texX = texture.getSize() - texX - 1;
                    }
                    
                    if (!side && rayX > 0) {
                        texX = texture.getSize() - texX - 1;
                    }
                    
                    PerspectiveRenderItem p = new PerspectiveRenderItem(opacity, drawStart, drawEnd, lineHeight,
                            texture, texX, distanceToWall, xx, side);
                    renderStack.push(p);
                    zbuffer[xx] = p.distanceToWall;
                    
                }
                
            }
            
            // We know that we've hit the final tile (and opaque one). So now,
            // we can do the floorcast.
            floorCast(pix, offset, xx, rayX, rayY, mapX, mapY, wallX, distanceToWall, drawEnd, side);
        }
        
        while (!renderStack.isEmpty()) {
            draw(pix, renderStack.pop(), offset);
        }
        
        drawSprites(pix, zbuffer, offset);
    }
    
    private void floorCast(PixelBuffer pix, int offset, int xx, double rayX, double rayY, int mapX, int mapY,
                           double wallX, double distanceToWall, int drawEnd, boolean side) {
        Map map = world.getMap();
        double floorXWall, floorYWall;
        
        if (!side && rayX > 0) {
            floorXWall = mapX;
            floorYWall = mapY + wallX;
        } else if (!side && rayX < 0) {
            floorXWall = mapX + 1.0;
            floorYWall = mapY + wallX;
        } else if (side && rayY > 0) {
            floorXWall = mapX + wallX;
            floorYWall = mapY;
        } else {
            floorXWall = mapX + wallX;
            floorYWall = mapY + 1.0;
        }
        
        double distWall, distPlayer, currentDist;
        
        distWall = distanceToWall;
        distPlayer = 0.0;
        
        int h = pix.getHeight();
        drawEnd = drawEnd < 0 ? h : drawEnd;
        
        Graphic floor;
        Graphic ceil = world.getEnvironment().getCeilingTexture();
        
        // stops us from getting weird rendering artifacts, since
        // we start drawing a pixel after drawEnd to avoid indexing errors.
        pix.fillRect(0x000000, xx, drawEnd + offset, GameSettings.actualBlockiness, 1);
        
        // draw from drawEnd to the bottom of the screen
        for (int y = drawEnd + 1; y < h; y++) {
            // TODO: maybe add a lookup table here for speed?
            currentDist = h / (2.0 * y - h);
            
            double weight = (currentDist - distPlayer) / (distWall - distPlayer);
            
            double currentFloorX = weight * floorXWall + (1 - weight) * camera.getxPos();
            double currentFloorY = weight * floorYWall + (1 - weight) * camera.getyPos();
            
            Tile tile = map.getTile((int)currentFloorX, (int)currentFloorY);
            if (tile == AirTile.getInstance()) {
                // if air, use the global floor texture.
                floor = world.getEnvironment().getFloorTexture();
            } else {
                // otherwise, use the tile floor texture.
                floor = tile.getFloorTexture();
            }
            
            int floorTexX, floorTexY;
            floorTexX = (int)((currentFloorX * floor.getWidth()) % floor.getWidth());
            floorTexY = (int)((currentFloorY * floor.getHeight()) % floor.getHeight());
            
            // floor
            int floorColor = floor.getPixels()[floor.getWidth() * floorTexY + floorTexX];
            floorColor = ColorUtils.darken(floorColor, world.getEnvironment().getDarkness(), currentDist);
            
            pix.fillRect(floorColor, xx, y + offset, GameSettings.actualBlockiness, 1);
            
            // ceiling
            int ceilColor = ceil.getPixels()[ceil.getWidth() * floorTexY + floorTexX];
            ceilColor = ColorUtils.darken(ceilColor, world.getEnvironment().getDarkness(), currentDist);
            pix.fillRect(ceilColor, xx, h - y + offset, GameSettings.actualBlockiness, 1);
        }
    }
    
    private void draw(PixelBuffer pix, PerspectiveRenderItem p, int offset) {
        if (p.opacity == 0) {
            return;
        }
        
        // calculate y coordinate on texture
        for (int yy = p.drawStart; yy < p.drawEnd; yy++) {
            
            int texY = (((yy * 2 - pix.getHeight() + p.lineHeight) << 4) / p.lineHeight) / 2;
            
            int color = p.texture.getPixels()[p.texX + (texY * p.texture.getSize())];
            
            int drawY = yy + offset;
            color = ColorUtils.mixColor(pix.pixelAt(p.xx, drawY), color, p.opacity);
            
            color = ColorUtils.darken(color, world.getEnvironment().getDarkness(), p.distanceToWall);
            if (p.side) {
                color = ColorUtils.quickDarken(color);
            }
            pix.fillRect(color, p.xx, drawY, GameSettings.actualBlockiness, 1);
        }
    }
    
    private synchronized void drawSprites(PixelBuffer pix, double[] zbuffer, int offset) {
        synchronized (world) {
            Entity[] entities = world.getEntityArray();
            Comparator<Entity> c = (o1, o2) -> {
                final double distance1 = camera.getPosition().sqrDistTo(o1.getPosition());
                final double distance2 = camera.getPosition().sqrDistTo(o2.getPosition());
                return Double.compare(distance2, distance1);
            };
            Arrays.sort(entities, c);
    
            synchronized (entities) {
                for (Entity m : entities) {
                    boolean isVisible = false;
                    
                    double spriteX = m.getPosition().getX() - camera.getxPos();
                    double spriteY = m.getPosition().getY() - camera.getyPos();
                    
                    double invDet = 1.0
                            / (camera.getxPlane() * camera.getyDir() - camera.getxDir() * camera.getyPlane());
                    
                    double transformX = invDet * (camera.getyDir() * spriteX - camera.getxDir() * spriteY);
                    double transformY = invDet * (-camera.getyPlane() * spriteX + camera.getxPlane() * spriteY);
                    
                    int zMoveScreen = (int)(m.getzOffset() / transformY);
                    
                    int spriteScreenX = (int)((pix.getWidth() / 2) * (1 + transformX / transformY));
                    int spriteHeight = Math.abs((int)(pix.getHeight() / transformY));
                    
                    // calculate lowest and highest pixel
                    int drawStartY = -spriteHeight / 2 + pix.getHeight() / 2 + zMoveScreen;
                    drawStartY = Math.max(0, drawStartY);
                    int drawEndY = spriteHeight / 2 + pix.getHeight() / 2 + zMoveScreen;
                    drawEndY = Math.min(drawEndY, pix.getHeight() - 1);
                    
                    // calculate width of the sprite
                    int spriteWidth = Math.abs((int)(pix.getHeight() / transformY));
                    int drawStartX = -spriteWidth / 2 + spriteScreenX;
                    if (drawStartX < 0) {
                        drawStartX = 0;
                    }
                    int drawEndX = spriteWidth / 2 + spriteScreenX;
                    if (drawEndX >= pix.getWidth()) {
                        drawEndX = pix.getWidth() - 1;
                    }
                    
                    // loop through every vertical stripe of the sprite on
                    // screen
                    for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
                        Graphic g = m.getGraphic(camera.getPosition());
                        
                        int texX = 256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * g.getWidth() / spriteWidth
                                / 256;
                        
                        // the conditions in the if are:
                        // 1) it's in front of camera plane so you don't see
                        // things
                        // behind you
                        // 2) it's on the screen (left)
                        // 3) it's on the screen (right)
                        // 4) ZBuffer, with perpendicular distance
                        if (transformY > 0 && stripe > 0 && stripe < pix.getWidth() && transformY < zbuffer[stripe]) {
                            isVisible = true;
                            for (int y = drawStartY; y < drawEndY; y++) {
                                // 16 and 8 are factors to avoid division and
                                // floats.
                                int d = 16 * (y - zMoveScreen) - 8 * (pix.getHeight() - spriteHeight - 1);
                                
                                int texY = ((d * g.getHeight()) / spriteHeight) / 16;
                                
                                int color = g.getPixels()[texX + g.getWidth() * texY];
                                
                                // dont draw transparent pixels.
                                if (color == PixelBuffer.CHROMA_KEY) {
                                    continue;
                                }
                                
                                color = ColorUtils.darken(color, world.getEnvironment().getDarkness(),
                                        camera.getPosition().distance(m.getPosition()));
                                
                                int drawY = y + offset;
                                
                                pix.fillRect(color, stripe, drawY, GameSettings.actualBlockiness, 1);
                            }
                        }
                    }
                    
                    if (isVisible) {
                        final double sc = (drawEndY - drawStartY) / 90D;
                        final double sp = (drawEndY - drawStartY) / 50D;
                        final int sw = pix.stringWidth(Font.DEFAULT_WHITE, m.getName(), sc, sp);
                        if (m.renderName()) {
                            pix.drawString(Font.DEFAULT_WHITE, m.getName(), spriteScreenX - sw / 2, drawStartY + offset,
                                    sc, sp);
                        }
                        
                        if (m instanceof Player) {
                            pix.fillRect(HealthCounter.BASE, drawStartX, drawStartY - 20, drawEndX - drawStartX,
                                    (int)sc * 3);
                            double r = ((Player)m).getCurrentHealth() / (double)Player.MAX_HEALTH;
                            pix.fillRect(HealthCounter.G1, drawStartX, drawStartY - 20,
                                    (int)(r * (drawEndX - drawStartX)), (int)sc * 3);
                        }
                    }
                }
            }
            
        }
        
    }
    
    private void drawCrosshair(PixelBuffer pix) {
        final int CROSSHAIR_SIZE = 6;
        final int CROSSHAIR_WIDTH = 2;
        final int CROSSHAIR_COLOR = 0xFFFFFF;
        final int w = pix.getWidth() / 2, h = pix.getHeight() / 2;
        pix.fillRect(CROSSHAIR_COLOR, w - CROSSHAIR_SIZE, h - CROSSHAIR_WIDTH / 2, CROSSHAIR_SIZE * 2, CROSSHAIR_WIDTH);
        pix.fillRect(CROSSHAIR_COLOR, w - CROSSHAIR_WIDTH / 2, h - CROSSHAIR_SIZE, CROSSHAIR_WIDTH, CROSSHAIR_SIZE * 2);
    }
    
    public PixelBuffer getPixelBuffer() {
        return pix;
    }
    
}
