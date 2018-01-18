package com.knightlore.engine;

import java.util.ArrayList;

import com.knightlore.render.IRenderable;

public abstract class GameObject {

	
	/**
	 * Position of the entity.
	 */
	protected Vector2 position;
	protected int x, y;

	/**
	 * Whether the entity currently exists. If this variable is set to false,
	 * entities will be 'garbage collected' by the game engine.
	 */
	protected boolean exists;

	public GameObject(){
		position = Vector2.ZERO;
	}
	
	public GameObject(Vector2 position) {
		this.position = position;
		exists = true;
	}
	
	public Vector2 getPosition(){
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
	
	void Destroy(){
		onDestroy();
	}
	
}
