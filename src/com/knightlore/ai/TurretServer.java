package com.knightlore.ai;

import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public final class TurretServer extends TurretShared {

	public TurretServer(double size, DirectionalSprite dSprite, Vector2D position, Vector2D direction) {
		super(size, dSprite, position, direction);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void shoot() {
		if(target == null) {
			System.out.println("No target");
			return;
		}
		System.out.println("Shooting at position " + target.getPosition());

	}

	@Override
	public int getDrawSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinimapColor() {
		// TODO Auto-generated method stub
		return 0xFF00FFFF;
	}

}
