package com.knightlore.game.entity;

import com.knightlore.render.graphic.sprite.ShotgunSprite;
import com.knightlore.utils.Vector2D;

public class ShotgunPickup extends PickupItem {

	private static final double ROTATION_SPEED = 0.1D;

	public ShotgunPickup(Vector2D position) {
		super(ShotgunSprite.SHOTGUN_DIRECTIONAL_SPRITE, position);
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		double xprime = direction.getX() * Math.cos(ROTATION_SPEED) - direction.getY() * Math.sin(ROTATION_SPEED);
		double yprime = direction.getX() * Math.sin(ROTATION_SPEED) + direction.getY() * Math.cos(ROTATION_SPEED);
		direction = new Vector2D(xprime, yprime);
	}

	@Override
	public void onDestroy() {
	}

}
