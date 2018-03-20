package com.knightlore.render.animation;

import java.util.ArrayList;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;
import com.knightlore.render.graphic.sprite.DirectionalSprite;

public class PlayerMoveAnimation extends Animation<DirectionalSprite> {

    private final int animationFrames = 8;

    private double distanceTraveled = 0D;

    private final double TRIGGER_NEXT_FRAME_DISTANCE = 0.2;
    private double nextFrameDistance;

    public PlayerMoveAnimation(GraphicSheet sheet) {
        super();
        for (int i = 0; i < animationFrames; i++) {
            ArrayList<Graphic> angles = new ArrayList<Graphic>();
            for (int y = 0; y < 32; y++) {
                angles.add(sheet.graphicAt(i, y));
            }

            DirectionalSprite d = new DirectionalSprite(angles);
            this.frames.add(d);
        }
    }

    public void update(double displacement) {
        distanceTraveled += displacement;
        if (distanceTraveled > nextFrameDistance) {
            nextFrameDistance += TRIGGER_NEXT_FRAME_DISTANCE;
            nextFrame();
        }
    }

}
