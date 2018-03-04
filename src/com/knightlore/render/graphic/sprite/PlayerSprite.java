package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.GraphicSheet;

public class PlayerSprite extends DirectionalSprite {

    protected PlayerSprite(GraphicSheet sheet) {
        super();
        for (int i = 0; i < 32; i++) {
            addGraphic(sheet.graphicAt(0, i));
        }
    }

}
