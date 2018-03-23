package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.GraphicSheet;

public class SpeedSprite extends DirectionalSprite {

    public SpeedSprite(GraphicSheet speedSprites) {
        super();
        for (int i = 0; i < 32; i++) {
            addGraphic(speedSprites.graphicAt(0, i));
        }
    }
    
}
