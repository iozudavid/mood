package com.knightlore.render.minimap;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.Player;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.Vector2D;

public class Minimap implements IRenderable, TickListener {

	public static final int RESOLUTION = 5;
	public static final int SCALE = 5;
	private int size;

	private Player player;
	private Map map;

	private int width, height;
	private int[] pixelMap;

	public Minimap(Player player, Map map, int size) {
		this.player = player;
		this.map = map;
		this.width = map.getWidth() * RESOLUTION;
		this.height = map.getHeight() * RESOLUTION;

		this.size = size;

		this.pixelMap = new int[width * height];
		recreatePixelMap();

		GameEngine.ticker.addTickListener(this);
	}

	@Override
	public void render(PixelBuffer pix, int x, int y) {
		pix.fillRect(0x000000, x, y, size, size);

		Vector2D direction = player.getDirection();
		double theta = Math.atan2(direction.getX(), direction.getY());
		theta = -theta;

		Vector2D pos = player.getPosition();
		double pX = (pos.getX() / map.getWidth()) * width, pY = (pos.getY() / map.getHeight()) * height;

		for (int yy = 0; yy < height; yy++) {
			for (int xx = 0; xx < width; xx++) {
				double tX = xx - pX, tY = yy - pY;
				double drawX = (tX * Math.cos(theta) - tY * Math.sin(theta));
				double drawY = (tX * Math.sin(theta) + tY * Math.cos(theta));

				drawX += x;
				drawY += y;
				if (drawX < x || drawX > x + size || drawY < y || drawY > y + size)
					continue;

				pix.fillRect(pixelMap[xx + yy * width], (int) (drawX), (int) (drawY), 2, 2);
			}
		}
		
	}

	private void recreatePixelMap() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Tile t = map.getTile(x / RESOLUTION, y / RESOLUTION);
				pixelMap[x + y * width] = t.getMinimapColor();
			}
		}
	}

	public int getSize() {
		return size;
	}

	@Override
	public void onTick() {
		recreatePixelMap();
	}

	@Override
	public long interval() {
		return 60L;
	}

}
