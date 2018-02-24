package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.GraphicSheet;

public class ShotgunSprite extends DirectionalSprite {

    protected ShotgunSprite() {
        super();
        for (int i = 0; i < 32; i++) {
            addGraphic(GraphicSheet.SHOTGUN_SPRITES.graphicAt(0, i));
        }
    }

}
