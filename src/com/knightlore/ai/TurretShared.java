package com.knightlore.ai;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameObject;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public abstract class TurretShared extends Entity {

	protected int damage;
	protected int team;
	
	protected long nextCheckTime = 0;
	protected static final long TURRET_CHECK_DELAY = 20;
	
	protected Entity target = null;
	
	protected TurretShared(double size, DirectionalSprite dSprite, Vector2D position, Vector2D direction) {
		super(size, dSprite, position, direction);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate() {
		// 60 ticks per second
		long currentTime = GameEngine.ticker.getTime();
		aim();
		if( currentTime >= nextCheckTime) {
			shoot();
			nextCheckTime = currentTime + TURRET_CHECK_DELAY;
		}
	}

	protected void aim() {
		long currentTime = GameEngine.ticker.getTime();
		if(target == null) {
			this.direction = new Vector2D(Math.sin(currentTime / 360d) , Math.cos(currentTime / 360d));
			return;
		}
		this.direction = target.getPosition().subtract(this.getPosition());
		this.direction.normalise();
		
	}
	
	protected abstract void shoot();
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

	}

}
