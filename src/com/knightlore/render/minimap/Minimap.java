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

	private Player player;
	private Map map;

	private int width, height;
	private int[] pixelMap;

	public Minimap(Player player, Map map) {
		this.player = player;
		this.map = map;
		this.width = map.getWidth() * RESOLUTION;
		this.height = map.getHeight() * RESOLUTION;

		this.pixelMap = new int[width * height];
		recreatePixelMap();

		GameEngine.ticker.addTickListener(this);
	}

	@Override
	public void render(PixelBuffer pix, int x, int y) {
		for (int yy = 0; yy < height; yy++) {
			for (int xx = 0; xx < width; xx++) {
				pix.fillPixel(pixelMap[xx + yy * width], xx + x, yy + y);
			}
		}
		
		Vector2D pos = player.getPosition();
		int xx = (int) pos.getX(), yy = (int) pos.getY();
		pix.fillRect(0x0000FF, xx * RESOLUTION, yy * RESOLUTION, RESOLUTION, RESOLUTION);
	}

	private void recreatePixelMap() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Tile t = map.getTile(x / RESOLUTION, y / RESOLUTION);
				pixelMap[x + y * width] = t.getMinimapColor();
			}
		}
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
