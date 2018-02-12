package com.knightlore.game.entity.weapon;

import com.knightlore.render.graphic.GraphicSheet;

public class Shotgun extends Weapon {

	public Shotgun() {
		super(GraphicSheet.SHOTGUN_SPRITES.graphicAt(0, 15));
	}

}
