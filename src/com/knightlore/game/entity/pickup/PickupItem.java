package com.knightlore.game.entity.pickup;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public abstract class PickupItem extends Entity {

	private static final double ROTATION_SPEED = 0.1D;
	private static final double PICKUP_SIZE = 0.25D;
	private static final double FLOAT_BOB_AMOUNT = 50.0;
	private static final double FLOAT_BOB_SPEED = 0.10;
	private static final int FLOOR_OFFSET = 100;

	protected DirectionalSprite sprite;

	public PickupItem(Vector2D position) {
		super(PICKUP_SIZE, position, Math.random() < 0.5 ? Vector2D.UP : Vector2D.DOWN);
	}

	@Override
	public void onUpdate() {
		// Make the item bob up and down.
		long t = GameEngine.ticker.getTime();
		zOffset = FLOOR_OFFSET + (int) (Math.sin(t * FLOAT_BOB_SPEED) * FLOAT_BOB_AMOUNT);

		double xprime = direction.getX() * Math.cos(ROTATION_SPEED) - direction.getY() * Math.sin(ROTATION_SPEED);
		double yprime = direction.getX() * Math.sin(ROTATION_SPEED) + direction.getY() * Math.cos(ROTATION_SPEED);
		direction = new Vector2D(xprime, yprime);
	}

}
