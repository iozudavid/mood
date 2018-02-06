package com.knightlore.render.minimap;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.Player;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.Vector2D;

public class Minimap implements TickListener {

	public static final int RESOLUTION = 8;
	public static final int SCALE = 8;
	public static final int DRAW_SIZE = SCALE / RESOLUTION;

	private Player player;
	private Map map;

	private int width, height;
	private int[] pixelMap;
	
	private PixelBuffer display;

	public Minimap(Player player, Map map, int size) {
		this.player = player;
		this.map = map;
		this.width = map.getWidth() * RESOLUTION;
		this.height = map.getHeight() * RESOLUTION;

		this.pixelMap = new int[width * height];
		recreatePixelMap();
		
		this.display = new PixelBuffer(size, size);

		GameEngine.ticker.addTickListener(this);
	}

	public void render() {
		final int size = display.getWidth();
		display.flood(0x000000);

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

				drawX -= size / 2;
				drawY -= size / 2;
				
				double oldDrawX = drawX;
				drawX = drawX * Math.cos(theta) - drawY * Math.sin(theta);
				drawY = oldDrawX * Math.sin(theta) + drawY * Math.cos(theta);

				drawX += size / 2;
				drawY += size / 2;

				display.fillRect(pixelMap[xx + yy * width], (int) drawX, (int) drawY, DRAW_SIZE + 1, DRAW_SIZE + 1);
			}
		}

		final int PLAYER_COLOR = 0xFF00FF;
		display.fillRect(PLAYER_COLOR, size / 2, size / 2, SCALE / 2, SCALE / 2);

		// Draw a border to prevent clipping whilst rendering.
		final int BORDER_COLOR = 0xFEFEFE;
		display.fillRect(BORDER_COLOR, 0, 0, DRAW_SIZE, size);
		display.fillRect(BORDER_COLOR, 0, 0 + size, size + DRAW_SIZE, DRAW_SIZE);
		display.fillRect(BORDER_COLOR, 0, 0, size, DRAW_SIZE);
		display.fillRect(BORDER_COLOR, size, 0, DRAW_SIZE, size);
	}

	private void recreatePixelMap() {
		final int base = map.getEnvironment().getMinimapBaseColor();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Tile t = map.getTile(x / RESOLUTION, y / RESOLUTION);
				int tileColor = t.getMinimapColor();
				pixelMap[x + y * width] = tileColor != 0 ? tileColor : base;
			}
		}
	}
	
	public PixelBuffer getPixelBuffer() {
		return display;
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
