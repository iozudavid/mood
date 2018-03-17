package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.GraphicSheet;

public class CameraSprite extends DirectionalSprite {
    
    protected CameraSprite(GraphicSheet cameraSprites) {
        super();
        for (int i = 0; i < 32; i++) {
            addGraphic(cameraSprites.graphicAt(0, i));
        }
    }
    
}
