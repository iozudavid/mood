package com.knightlore.render.minimap;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.Player;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.Vector2D;

/**
 * The minimap class. This is a small UI element that shows a birds-eye view of
 * the map the player is currently in. This class stores a bitmap representation
 * of the map (in pixelMap), which is updated periodically to reflect any
 * changes in the world.
 * 
 * The scale variable dictates how zoomed in we should display the map. The
 * larger it is, the more zoomed.
 * 
 * This class uses its own pixelbuffer to render. This is so that it can easily
 * be composited anywhere on another pixelbuffer. It saves us having to deal
 * with rendering the minimap relative to a given position.
 * 
 * @author Joe Ellis
 *
 */
public class Minimap implements TickListener {

	/*
	 * NOTE: to render this, first call render(), then use getPixelBuffer() to
	 * get the pixel buffer. Composite this onto any other pixel buffer using
	 * the composite() method.
	 */

	public static final int SCALE = 10;

	private Player player;
	private Map map;

	private int width, height;
	private int[] pixelMap;

	private PixelBuffer display;

	public Minimap(Player player, Map map, int size) {
		this.player = player;
		this.map = map;
		this.width = map.getWidth() * SCALE;
		this.height = map.getHeight() * SCALE;

		this.pixelMap = new int[width * height];
		recreatePixelMap();

		this.display = new PixelBuffer(size, size);

		GameEngine.ticker.addTickListener(this);
	}

	/**
	 * Updates the pixelbuffer 'display' to be the current appearance of the
	 * minimap.
	 */
	public void render() {
	    //FIXME
	    if (player == null)
	        return;
		final int size = display.getWidth();
		display.flood(0x000000);

		Vector2D dir = player.getDirection();
		double theta = -Math.atan2(dir.getX(), dir.getY());

		for (int yy = 0; yy < height; yy++) {
			for (int xx = 0; xx < width; xx++) {
				double drawX = xx, drawY = yy;
				drawX -= player.getPosition().getX() * SCALE;
				drawY -= player.getPosition().getY() * SCALE;

				drawX += size / 2;
				drawY += size / 2;

				drawY = size - drawY; // flip the map in the y-direction.

				// Now, rotate the image according to the 2D rotation matrix.
				drawX -= size / 2;
				drawY -= size / 2;

				double oldDrawX = drawX;
				drawX = drawX * Math.cos(theta) - drawY * Math.sin(theta);
				drawY = oldDrawX * Math.sin(theta) + drawY * Math.cos(theta);

				drawX += size / 2;
				drawY += size / 2;

				/*
				 * Finally, draw the pixel at the correct position. We draw a
				 * rectangle of size 2 as a really basic form of interpolation
				 * (so we don't get 'holes' in the minimap).
				 */
				display.fillRect(pixelMap[xx + yy * width], (int) drawX, (int) drawY, 2, 2);
			}
		}

		// draw the player.
		final int PLAYER_COLOR = 0xFF00FF;
		display.fillRect(PLAYER_COLOR, size / 2, size / 2, SCALE / 2, SCALE / 2);
		
	}

	/**
	 * The map can change at runtime, and we need some way to reflect this in
	 * the minimap. This method is called using the GameEngine's main ticker
	 * periodically. It converts the map into an array of pixels (a bitmap) of
	 * the required scale/resolution. We then transform this bitmap according to
	 * the position and direction of the player in the render method.
	 */
	private void recreatePixelMap() {
		final int base = map.getEnvironment().getMinimapBaseColor();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Tile t = map.getTile(x / SCALE, y / SCALE);
				int tileColor = t.getMinimapColor();
				pixelMap[x + y * width] = tileColor != 0 ? tileColor : base;
			}
		}
	}

	public PixelBuffer getPixelBuffer() {
		return display;
	}

	// Every three seconds, we recreate our pixelmap representation of the map
	// using the actual map object.

	@Override
	public void onTick() {
		recreatePixelMap();
	}

	@Override
	public long interval() {
		final long UPDATE_DELAY = 3 * 60L;
		return UPDATE_DELAY;
	}

}
