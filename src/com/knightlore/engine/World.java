package com.knightlore.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.knightlore.game.Player;
import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Mob;
import com.knightlore.game.entity.Zombie;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.Camera;
import com.knightlore.render.ColorUtils;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PerspectiveRenderItem;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.Vector2D;

public class World implements IRenderable {

	private final List<Mob> mobs;

	private final Map map;
	private Player player;

	public World(Map map) {
		this.map = map;
		mobs = new ArrayList<>();

		Camera camera = new Camera(4.5, 4.5, 1, 0, 0, Camera.FIELD_OF_VIEW, map);
		player = new Player(camera);

		mobs.add(new Zombie(0.5D, new Vector2D(7, 7)));
		mobs.add(new Zombie(0.5D, new Vector2D(25, 25)));
	}

	@Override
	public void render(PixelBuffer pix, int x, int y) {
		map.getEnvironment().renderEnvironment(pix);
		drawPerspective(pix);
		drawCrosshair(pix);
	}

	private final int BLOCKINESS = 6; // how 'old school' you want to look.

	/*
	 * NOTE: THIS ONLY AFFECTS THE RENDERING SIZE OF A TILE. If you change this
	 * variable, tiles will be drawn differently but the player will still move
	 * at their usual speed over a single tile. You might want to compensate a
	 * change here with a change in player move speed.
	 */
	private final float TILE_SIZE = 1F;

	private void drawPerspective(PixelBuffer pix) {

		final int width = pix.getWidth(), height = pix.getHeight();
		Camera camera = player.getCamera();

		double[] zbuffer = new double[width];

		for (int xx = 0; xx < width; xx += BLOCKINESS) {
			Stack<PerspectiveRenderItem> renderStack = new Stack<PerspectiveRenderItem>();

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

			boolean hit = false;
			boolean side = false; // wall facing x-direction vs y-direction?

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

					distanceToWall = getImpactDistance(camera, rayX, rayY, mapX, mapY, side, stepX, stepY);
					int lineHeight = getDrawHeight(height, distanceToWall);

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

					double wallX = getWallHitPosition(camera, rayX, rayY, mapX, mapY, side, stepX, stepY);

					Graphic texture = map.getTile(mapX, mapY).getTexture();
					if (texture == Graphic.EMPTY) {
						continue;
					}

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

	private void drawSprites(PixelBuffer pix, double[] zbuffer) {
		Camera cam = player.getCamera();

		mobs.sort(new Comparator<Mob>() {

			@Override
			public int compare(Mob o1, Mob o2) {
				final double distance1 = cam.getPosition().distance(o1.position);
				final double distance2 = cam.getPosition().distance(o2.position);
				return Double.compare(distance2, distance1);
			}

		});

		for (Mob m : mobs) {
			double spriteX = m.getPosition().getX() - cam.getxPos();
			double spriteY = m.getPosition().getY() - cam.getyPos();

			double invDet = 1.0 / (cam.getxPlane() * cam.getyDir() - cam.getxDir() * cam.getyPlane());

			double transformX = invDet * (cam.getyDir() * spriteX - cam.getxDir() * spriteY);
			double transformY = invDet * (-cam.getyPlane() * spriteX + cam.getxPlane() * spriteY);

			int spriteScreenX = (int) ((pix.getWidth() / 2) * (1 + transformX / transformY));
			int spriteHeight = Math.abs((int) (pix.getHeight() / transformY));

			// calculate lowest and highest pixel to fill in current stripe
			int drawStartY = -spriteHeight / 2 + pix.getHeight() / 2;
			if (drawStartY < 0)
				drawStartY = 0;
			int drawEndY = spriteHeight / 2 + pix.getHeight() / 2;
			if (drawEndY >= pix.getHeight())
				drawEndY = pix.getHeight() - 1;

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
				int texX = (int) (256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * m.getSprite().getWidth()
						/ spriteWidth) / 256;

				// the conditions in the if are:
				// 1) it's in front of camera plane so you don't see things
				// behind you
				// 2) it's on the screen (left)
				// 3) it's on the screen (right)
				// 4) ZBuffer, with perpendicular distance
				if (transformY > 0 && stripe > 0 && stripe < pix.getWidth() && transformY < zbuffer[stripe])
					for (int y = drawStartY; y < drawEndY; y++) {
						// here, 256 and 128 are factors to avoid floats.
						int d = y * 256 - pix.getHeight() * 128 + spriteHeight * 128;
						int texY = ((d * m.getSprite().getHeight()) / spriteHeight) / 256;
						int color = m.getSprite().getPixels()[m.getSprite().getWidth() * texY + texX];
						
						if (color != -16747777) // TODO REMOVE
							continue; // texture
						color = darken(color, cam.getPosition().distance(m.getPosition()));

						int drawY = y + player.getCamera().getMotionOffset();
						pix.fillRect(color, stripe, drawY, BLOCKINESS, 1);
					}
			}
		}
	}

	private void draw(PixelBuffer pix, PerspectiveRenderItem p) {
		// calculate y coordinate on texture
		for (int yy = p.drawStart; yy < p.drawEnd; yy++) {

			int texY = (((yy * 2 - pix.getHeight() + p.lineHeight) << 4) / p.lineHeight) / 2;

			int color = p.texture.getPixels()[p.texX + (texY * p.texture.getSize())];

			int drawY = yy + player.getCamera().getMotionOffset();
			color = ColorUtils.mixColor(pix.pixelAt(p.xx, drawY), color, p.opacity);

			pix.fillRect(darken(color, p.distanceToWall), p.xx, drawY, BLOCKINESS, 1);
		}
	}

	private double getWallHitPosition(Camera camera, double rayX, double rayY, int mapX, int mapY, boolean side,
			int stepX, int stepY) {
		// add a texture
		double wallX;// Exact position of where wall was hit
		if (side) {// If its a y-axis wall
			wallX = (camera.getxPos() + ((mapY - camera.getyPos() + (1 - stepY) / 2) / rayY) * rayX);
		} else {// X-axis wall
			wallX = (camera.getyPos() + ((mapX - camera.getxPos() + (1 - stepX) / 2) / rayX) * rayY);
		}
		wallX -= Math.floor(wallX);
		return wallX;
	}

	private int getDrawHeight(final int screenHeight, double distanceToWall) {
		int lineHeight;
		if (distanceToWall > 0) {
			lineHeight = Math.abs((int) (screenHeight / distanceToWall));
		} else {
			lineHeight = screenHeight;
		}
		return lineHeight;
	}

	private double getImpactDistance(Camera camera, double rayX, double rayY, int mapX, int mapY, boolean side,
			int stepX, int stepY) {
		double distanceToWall;
		// Calculate distance to the point of impact
		if (!side) {
			distanceToWall = Math.abs((mapX - camera.getxPos() + (1 - stepX) / 2) / (rayX / TILE_SIZE));
		} else {
			distanceToWall = Math.abs((mapY - camera.getyPos() + (1 - stepY) / 2) / (rayY / TILE_SIZE));
		}
		return distanceToWall;
	}

	private void drawCrosshair(PixelBuffer pix) {
		final int CROSSHAIR_SIZE = 10;
		final int CROSSHAIR_WIDTH = 2;
		final int CROSSHAIR_COLOR = 0xFFFFFF;
		final int w = pix.getWidth() / 2, h = pix.getHeight() / 2;
		pix.fillRect(CROSSHAIR_COLOR, w - CROSSHAIR_SIZE, h - CROSSHAIR_WIDTH / 2, CROSSHAIR_SIZE * 2, CROSSHAIR_WIDTH);
		pix.fillRect(CROSSHAIR_COLOR, w - CROSSHAIR_WIDTH / 2, h - CROSSHAIR_SIZE, CROSSHAIR_WIDTH, CROSSHAIR_SIZE * 2);
	}

	public void tick() {
		garbageCollect();
		player.onUpdate();
	}

	private int darken(int color, double distance) {
		Color c = new Color(color);
		double fogFactor = distance * map.getEnvironment().getDarkness();
		int red = (int) (Math.max(0, c.getRed() - fogFactor));
		int green = (int) (Math.max(0, c.getGreen() - fogFactor));
		int blue = (int) (Math.max(0, c.getBlue() - fogFactor));
		return new Color(red, green, blue).getRGB();
	}

	/**
	 * Deletes any entities that don't exist any more.
	 */
	private void garbageCollect() {
		ListIterator<Mob> itr = mobs.listIterator();
		while (itr.hasNext()) {
			GameObject e = itr.next();
			if (!e.exists()) {
				itr.remove();
			}
		}
	}

	public List<Mob> getMobs() {
		return mobs;
	}

	public Map getMap() {
		return map;
	}

}
