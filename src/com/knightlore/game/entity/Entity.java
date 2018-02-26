package com.knightlore.game.entity;

import com.knightlore.engine.GameObject;
import com.knightlore.game.Team;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.minimap.IMinimapObject;
import com.knightlore.utils.Vector2D;

public abstract class Entity extends GameObject implements IMinimapObject {

	protected DirectionalSprite dSprite;
	protected double size;
	protected Vector2D direction;

	// cannot have invalid values
	// anyone can set a team and get a team
	public Team team;
	
	protected int zOffset;

	protected Entity(double size, DirectionalSprite dSprite, Vector2D position, Vector2D direction) {
		super(position);
		this.size = size;
		this.dSprite = dSprite;
		this.direction = direction;
		this.zOffset = 0;
		this.team = Team.none;
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
