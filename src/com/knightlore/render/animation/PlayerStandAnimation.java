package com.knightlore.render.animation;

import java.util.ArrayList;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;
import com.knightlore.render.graphic.sprite.DirectionalSprite;

public class PlayerStandAnimation extends TimedAnimation<DirectionalSprite> {
    
    private final int animationFrames = 8;

    public PlayerStandAnimation(GraphicSheet sheet, long interval) {
        super(interval);
        for (int i = 0; i < animationFrames; i++) {
            ArrayList<Graphic> angles = new ArrayList<Graphic>();
            for (int y = 0; y < 32; y++) {
                angles.add(sheet.graphicAt(i, y));
            }

            DirectionalSprite d = new DirectionalSprite(angles);
            this.frames.add(d);
        }
    
    }

}
