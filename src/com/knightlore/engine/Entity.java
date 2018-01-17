package com.knightlore.engine;

import com.knightlore.render.Renderable;

public abstract class Entity implements Renderable, Updateable {

	/**
	 * Position of the entity.
	 */
	protected int x, y;

	/**
	 * Whether the entity currently exists. If this variable is set to false,
	 * entities will be 'garbage collected' by the game engine.
	 */
	protected boolean exists;

	public Entity(int x, int y) {
		this.x = x;
		this.y = y;
		exists = true;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean exists() {
		return exists;
	}

}
