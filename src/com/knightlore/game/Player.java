package com.knightlore.game;

import com.knightlore.engine.IUpdateable;
import com.knightlore.render.Camera;
import com.knightlore.utils.Vector2D;

public class Player implements IUpdateable {

	private Camera camera;
	
	public Player(Camera camera) {
		this.camera = camera;
	}

	public Vector2D getPosition() {
		Vector2D pos = new Vector2D(camera.getxPos(), camera.getyPos());
		return pos;
	}

	public Vector2D getDirection() {
		Vector2D dir = new Vector2D(camera.getxDir(), camera.getyDir());
		return dir;
	}

	public Camera getCamera() {
		return camera;
	}

	@Override
	public void tick(long ticker) {
		camera.tick(ticker);
	}

}
