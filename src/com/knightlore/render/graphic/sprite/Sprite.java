package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;

public class Sprite {

	public static final Graphic SAMPLE_SPRITE = GraphicSheet.GENERAL_SPRITES.graphicAt(0, 0, 1, 2);
	public static final Graphic SAMPLE_SPRITE2 = GraphicSheet.GENERAL_SPRITES.graphicAt(0, 0, 1, 1);

	private Sprite() {
	}

}
