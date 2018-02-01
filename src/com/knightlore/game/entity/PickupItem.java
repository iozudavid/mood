package com.knightlore.game.entity;

import com.knightlore.engine.GameEngine;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public abstract class PickupItem extends Mob {

	private static final double PICKUP_SIZE = 0.25D;
	private static final double FLOAT_BOB_AMOUNT = 50.0;
	private static final double FLOAT_BOB_SPEED = 0.10;
	private static final int FLOOR_OFFSET = 100;

	protected DirectionalSprite sprite;

	public PickupItem(DirectionalSprite sprite, Vector2D position) {
		super(PICKUP_SIZE, sprite, position, Math.random() < 0.5 ? Vector2D.UP : Vector2D.DOWN);
		this.sprite = sprite;
	}

	@Override
	public void onUpdate() {
		zOffset = FLOOR_OFFSET + (int) (Math.sin(GameEngine.ticker.getTime() * FLOAT_BOB_SPEED) * FLOAT_BOB_AMOUNT);
	}

}
