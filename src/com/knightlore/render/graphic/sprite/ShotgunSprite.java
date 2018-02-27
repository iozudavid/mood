package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.GraphicSheet;

public class ShotgunSprite extends DirectionalSprite {

    protected ShotgunSprite(GraphicSheet shotgunSprites) {
        super();
        for (int i = 0; i < 32; i++) {
            addGraphic(shotgunSprites.graphicAt(0, i));
        }
    }

}
