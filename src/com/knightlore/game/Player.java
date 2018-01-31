package com.knightlore.game;

import com.knightlore.engine.GameObject;
import com.knightlore.render.Camera;
import com.knightlore.utils.Vector2D;

public class Player extends GameObject {

	private Camera camera;

	public Player(Camera camera) {
		super();
		this.camera = camera;
	}

	public Vector2D getPosition() {
		return camera.getPosition();
	}

	public Vector2D getDirection() {
		Vector2D dir = new Vector2D(camera.getxDir(), camera.getyDir());
		return dir;
	}

	public Camera getCamera() {
		return camera;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onUpdate() {
		camera.onUpdate();
	}

	@Override
	public void onDestroy() {
	}

}