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

	public static final int RESOLUTION = 3;
	public static final int SCALE = 8;
	public static final int DRAW_SIZE = SCALE / RESOLUTION;

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
		pix.fillRect(0x000000, x, y, size + 1, size + 1);

		Vector2D dir = player.getDirection();
		double theta = -Math.atan2(dir.getX(), dir.getY());

		for (int yy = 0; yy < height; yy++) {
			for (int xx = 0; xx < width; xx++) {
				double drawX = xx * DRAW_SIZE, drawY = yy * DRAW_SIZE;
				drawX -= player.getPosition().getX() * SCALE;
				drawY -= player.getPosition().getY() * SCALE;

				drawX += size / 2;
				drawY += size / 2;
				drawY = size - drawY;

				drawX += x;
				drawY += y;

				drawX -= x + size / 2;
				drawY -= y + size / 2;

				double oldDrawX = drawX;
				drawX = drawX * Math.cos(theta) - drawY * Math.sin(theta);
				drawY = oldDrawX * Math.sin(theta) + drawY * Math.cos(theta);

				drawX += x + size / 2;
				drawY += y + size / 2;

				if (drawX < x || drawY < y || drawX >= x + size || drawY >= y + size)
					continue;

				pix.fillRect(pixelMap[xx + yy * width], (int) drawX, (int) drawY, DRAW_SIZE, DRAW_SIZE);
			}
		}

		final int PLAYER_COLOR = 0xFF00FF;
		pix.fillRect(PLAYER_COLOR, x + size / 2, y + size / 2, SCALE / 2, SCALE / 2);

		// Draw a border to prevent clipping whilst rendering.
		final int BORDER_COLOR = 0xFEFEFE;
		pix.fillRect(BORDER_COLOR, x, y, DRAW_SIZE, size);
		pix.fillRect(BORDER_COLOR, x, y + size, size + DRAW_SIZE, DRAW_SIZE);
		pix.fillRect(BORDER_COLOR, x, y, size, DRAW_SIZE);
		pix.fillRect(BORDER_COLOR, x + size, y, DRAW_SIZE, size);

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

	// Every second, we recreate our pixelmap representation of the map using
	// the actual map object.

	@Override
	public void onTick() {
		recreatePixelMap();
	}

	@Override
	public long interval() {
		return 60L;
	}

}
