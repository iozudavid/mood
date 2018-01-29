package com.knightlore.game.entity;

import com.knightlore.engine.GameObject;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.Sprite;
import com.knightlore.utils.Vector2D;

public abstract class Mob extends GameObject {

	protected Graphic sprite;
	protected double size;
	protected Vector2D direction;

	protected Mob(double size, Vector2D direction) {
		super(new Vector2D(6, 6));
		this.size = size;
		this.direction = direction;
		sprite = Sprite.SAMPLE_SPRITE;
	}

	public Graphic getSprite() {
		return sprite;
	}

	public double getSize() {
		return size;
	}

	public Vector2D getDirection() {
		return direction;
	}

}
