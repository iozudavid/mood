package com.knightlore.engine;

import com.knightlore.utils.Vector2D;

public abstract class GameObject {

	protected Vector2D position;
	/**
	 * Whether the entity currently exists. If this variable is set to false,
	 * entities will be 'garbage collected' by the game engine.
	 */
	protected boolean exists;

	public GameObject() {
		this(Vector2D.ZERO);
	}

	public GameObject(Vector2D position) {
		this.position = position;
		this.exists = true;
	}

	public Vector2D getPosition() {
		return position;
	}

	public boolean exists() {
		return exists;
	}

	// Called when the component is first added to the gameObject
	public abstract void onCreate();

	// Called every game-frame to update it
	public abstract void onUpdate();

	// Called when the attached gameObject is being removed from the game
	public abstract void onDestroy();

	void destroy() {
		onDestroy();
	}

}
