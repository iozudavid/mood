package com.knightlore.game.entity;

import com.knightlore.engine.GameObject;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.Sprite;
import com.knightlore.utils.Vector2D;

public class Enemy extends GameObject implements IRenderable {

	private Graphic sprite;

	public Enemy() {
		sprite = Sprite.SAMPLE_SPRITE;
		position = new Vector2D(5, 5);
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onUpdate() {
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public void render(PixelBuffer pix, int x, int y) {
		pix.drawGraphic(sprite, x, y);
	}

}
