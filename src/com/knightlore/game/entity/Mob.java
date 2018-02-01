package com.knightlore.game.entity;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameObject;
import com.knightlore.engine.TickListener;
import com.knightlore.render.animation.Animation;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.Sprite;
import com.knightlore.utils.Vector2D;

public abstract class Mob extends GameObject {

	protected Animation anim;
	protected double size;
	protected Vector2D direction;

	protected Mob(double size, Vector2D direction) {
		super(direction);
		this.size = size;
		this.direction = direction;
		anim = new Animation();
		anim.addFrame(Sprite.SAMPLE_SPRITE);
		anim.addFrame(Sprite.SAMPLE_SPRITE2);

		GameEngine.ticker.addTickListener(new TickListener() {

			@Override
			public void onTick() {
				anim.nextFrame();
			}

			@Override
			public long interval() {
				return 20;
			}
		});
	}

	public Graphic getSprite() {
		return anim.getGraphic();
	}

	public double getSize() {
		return size;
	}

	public Vector2D getDirection() {
		return direction;
	}

}
