package com.knightlore.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.knightlore.game.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.Camera;
import com.knightlore.render.IRenderable;
import com.knightlore.render.Screen;
import com.knightlore.render.sprite.Texture;

public class World implements IRenderable {

	private Map map;

	private long ticker;
	private List<GameObject> entities;

	private Camera camera;

	public World(Map map) {
		this.map = map;
		entities = new ArrayList<GameObject>();
		camera = new Camera(4.5, 4.5, 1, 0, 0, Camera.FIELD_OF_VIEW, map);
	}

	@Override
	public void render(Screen screen, int x, int y) {

		Tile[][] mapArr = map.getMapArray();
		final int width = screen.getWidth();
		final int height = screen.getHeight();

		map.getEnvironment().renderEnvironment(screen);

		final int BLOCKINESS = 1; // how 'old school' you want to look.

		for (int xx = 0; xx < width; xx = xx += BLOCKINESS) {

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

				// If this is anything but an empty cell, we've hit a wall
				if (mapArr[mapX][mapY] != Tile.AIR)
					hit = true;
			}

			// Calculate distance to the point of impact
			if (!side)
				distanceToWall = Math.abs((mapX - camera.getxPos() + (1 - stepX) / 2) / rayX);
			else
				distanceToWall = Math.abs((mapY - camera.getyPos() + (1 - stepY) / 2) / rayY);
			// Now calculate the height of the wall based on the distance from
			// the camera
			int lineHeight;
			if (distanceToWall > 0)
				lineHeight = Math.abs((int) (height / distanceToWall));
			else
				lineHeight = height;
			// calculate lowest and highest pixel to fill in current stripe
			int drawStart = -lineHeight / 2 + height / 2;
			if (drawStart < 0)
				drawStart = 0;
			int drawEnd = lineHeight / 2 + height / 2;
			if (drawEnd >= height)
				drawEnd = height - 1;

			// add a texture
			double wallX;// Exact position of where wall was hit
			if (side) {// If its a y-axis wall
				wallX = (camera.getxPos() + ((mapY - camera.getyPos() + (1 - stepY) / 2) / rayY) * rayX);
			} else {// X-axis wall
				wallX = (camera.getyPos() + ((mapX - camera.getxPos() + (1 - stepX) / 2) / rayX) * rayY);
			}
			wallX -= Math.floor(wallX);

			// What pixel did we hit the texture on?
			int texX = (int) (wallX * (Texture.BRICK.getSize()));
			if (side && rayY < 0)
				texX = Texture.BRICK.getSize() - texX - 1;
			if (!side && rayX > 0)
				texX = Texture.BRICK.getSize() - texX - 1;

			// calculate y coordinate on texture
			for (int yy = drawStart; yy < drawEnd; yy++) {

				// TODO: only compensates for 16x16 textures here, maybe change?
				int texY = (((yy * 2 - height + lineHeight) << 4) / lineHeight) / 2;

				int color = Texture.BRICK.getPixels()[texX + (texY * Texture.BRICK.getSize())];

				screen.fillRect(darken(color, distanceToWall), xx, yy, BLOCKINESS, 1);
			}

		}

	}

	public void tick() {

		garbageCollect();

		camera.tick(ticker);

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
