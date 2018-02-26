package com.knightlore.ai;

import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public final class TurretClient extends TurretShared {

	protected TurretClient(double size, DirectionalSprite dSprite, Vector2D position, Vector2D direction) {
		super(size, dSprite, position, direction);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void shoot() {
		System.out.println("Ratatatatata");
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
