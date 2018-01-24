package com.knightlore.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.knightlore.game.Player;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.Camera;
import com.knightlore.render.Renderable;
import com.knightlore.render.Screen;
import com.knightlore.render.sprite.Texture;

public class World implements Renderable {
	private final List<GameObject> entities;
	private long ticker;

	private final Map map;
	private Player player;

	public World(Map map) {
		this.map = map;
		entities = new ArrayList<>();

		Camera camera = new Camera(4.5, 4.5, 1, 0, 0, Camera.FIELD_OF_VIEW, map);
		player = new Player(camera);
	}

	@Override
	public void render(Screen screen, int x, int y) {
		map.getEnvironment().renderEnvironment(screen);
		drawPerspective(screen);
		drawCrosshair(screen);

	}

	private final int BLOCKINESS = 3; // how 'old school' you want to look.

	/*
	 * NOTE: THIS ONLY AFFECTS THE RENDERING SIZE OF A TILE. If you change this
	 * variable, tiles will be drawn differently but the player will still move
	 * at their usual speed over a single tile. You might want to compensate a
	 * change here with a change in player move speed.
	 */
	private final float TILE_SIZE = 1F;

	private void drawPerspective(Screen screen) {

		final int width = screen.getWidth(), height = screen.getHeight();
		Camera camera = player.getCamera();

		double opacity = 1D;

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

			boolean hit = false;
			boolean side = false; // wall facing x-direction vs y-direction?
			double last = opacity;

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
					if (opacity < 1 && tile.getOpacity() < 1)
						continue;
					hit = true;
					opacity = tile.getOpacity();
				}
			}

			distanceToWall = getImpactDistance(camera, rayX, rayY, mapX, mapY, side, stepX, stepY);
			int lineHeight = getDrawHeight(height, distanceToWall);

			// calculate lowest and highest pixel to fill in current strip
			int drawStart = -lineHeight / 2 + height / 2;
			if (drawStart < 0) {
				drawStart = 0;
			}

			int drawEnd = lineHeight / 2 + height / 2;
			if (drawEnd >= height) {
				drawEnd = height - 1;
			}

			double wallX = getWallHitPosition(camera, rayX, rayY, mapX, mapY, side, stepX, stepY);

			Texture texture = map.getTile(mapX, mapY).getTexture();
			if (texture == Texture.EMPTY) {
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

			// calculate y coordinate on texture
			for (int yy = drawStart; yy < drawEnd; yy++) {

				// TODO: only compensates for 16x16 textures here, maybe change?
				int texY = (((yy * 2 - height + lineHeight) << 4) / lineHeight) / 2;

				int color = texture.getPixels()[texX + (texY * texture.getSize())];

				if (last < 1) {
					color = screen.mixColor(color, screen.getPixels()[xx + yy * width], last);
				}
				screen.fillRect(darken(color, distanceToWall), xx, yy, BLOCKINESS, 1);
			}

			if (opacity < 1) {
				xx -= BLOCKINESS;
			}

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

	private void drawCrosshair(Screen screen) {
		final int CROSSHAIR_SIZE = 10;
		final int CROSSHAIR_WIDTH = 2;
		final int CROSSHAIR_COLOR = 0xFFFFFF;
		final int w = screen.getWidth() / 2, h = screen.getHeight() / 2;
		screen.fillRect(CROSSHAIR_COLOR, w - CROSSHAIR_SIZE, h - CROSSHAIR_WIDTH / 2, CROSSHAIR_SIZE * 2,
				CROSSHAIR_WIDTH);
		screen.fillRect(CROSSHAIR_COLOR, w - CROSSHAIR_WIDTH / 2, h - CROSSHAIR_SIZE, CROSSHAIR_WIDTH,
				CROSSHAIR_SIZE * 2);
	}

	public void tick() {
		garbageCollect();
		player.tick(ticker);

		ticker++;
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
		ListIterator<GameObject> itr = entities.listIterator();
		while (itr.hasNext()) {
			GameObject e = itr.next();
			if (!e.exists()) {
				itr.remove();
			}
		}
	}

	public List<GameObject> getEntities() {
		return entities;
	}

	public Map getMap() {
		return map;
	}

}
