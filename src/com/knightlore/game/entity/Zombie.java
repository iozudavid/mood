package com.knightlore.game.entity;

import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Zombie extends Mob {

	public Zombie(double size, Vector2D position) {
		super(size, DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE, position, Vector2D.UP);
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
	public int getDrawSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinimapColor() {
		// TODO Auto-generated method stub
		return 0;
	}

}
