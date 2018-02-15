package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.GraphicSheet;

public class PlayerSprite extends DirectionalSprite {

    protected PlayerSprite() {
        super();
        for (int i = 0; i < 32; i++) {
            addGraphic(GraphicSheet.PLAYER_SPRITES.graphicAt(0, i));
        }
    }

}
