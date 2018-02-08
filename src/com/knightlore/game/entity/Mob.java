package com.knightlore.game.entity;

import com.knightlore.engine.GameObject;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.minimap.IMinimapObject;
import com.knightlore.utils.Vector2D;

public abstract class Mob extends GameObject implements IMinimapObject {

	protected DirectionalSprite dSprite;
	protected double size;
	protected Vector2D direction;

	protected int zOffset;

	protected Mob(double size, DirectionalSprite dSprite, Vector2D position, Vector2D direction) {
		super(position);
		this.size = size;
		this.dSprite = dSprite;
		this.direction = direction;
		this.zOffset = 0;
	}
	
	public Graphic getGraphic(Vector2D playerPos) {
		return dSprite.getCurrentGraphic(position, direction, playerPos);
	}

	public double getSize() {
		return size;
	}

	public Vector2D getDirection() {
		return direction;
	}
	
	public int getzOffset() {
		return zOffset;
	}

}
