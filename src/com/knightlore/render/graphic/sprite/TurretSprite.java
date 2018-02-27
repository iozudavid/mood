package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.GraphicSheet;

public class TurretSprite extends DirectionalSprite {
    protected TurretSprite() {
        super();
        for (int i = 0; i < 32; i++) {
            addGraphic(GraphicSheet.TURRET_SPRITES.graphicAt(0, i));
        }
    }
}
