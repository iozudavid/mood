package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.GraphicSheet;

public class GravestoneSprite extends DirectionalSprite {

    public GravestoneSprite(GraphicSheet gravestoneSprites) {
        super();
        for (int i = 0; i < 32; i++) {
            addGraphic(gravestoneSprites.graphicAt(0, i));
        }
    }

}
