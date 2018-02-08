package com.knightlore.game.entity.pickup;

import com.knightlore.render.graphic.sprite.ShotgunSprite;
import com.knightlore.utils.Vector2D;

public class ShotgunPickup extends PickupItem {

	public ShotgunPickup(Vector2D position) {
		super(ShotgunSprite.SHOTGUN_DIRECTIONAL_SPRITE, position);
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public int getDrawSize() {
		return 5;
	}

	@Override
	public int getMinimapColor() {
		return 0xFF00FF;
	}

}
