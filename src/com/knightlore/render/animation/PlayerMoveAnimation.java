package com.knightlore.render.animation;

import java.util.ArrayList;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;
import com.knightlore.render.graphic.sprite.DirectionalSprite;

public class PlayerMoveAnimation extends Animation<DirectionalSprite> {
    
    private final int animationFrames = 8;
    private final double TRIGGER_NEXT_FRAME_DISTANCE = 0.2;
    /**
     * If the player is detected to be moving, play the animation for 5 ticks
     * before defaulting to the standing animation if they aren't still moving.
     */
    private final long LIFESPAN = 5;
    private double distanceTraveled = 0D;
    private double nextFrameDistance;
    private long timeLeft = 0;
    
    public PlayerMoveAnimation(GraphicSheet sheet) {
        super();
        setFrames(sheet);
    }
    
    public void update(double displacement) {
        if (displacement == 0) {
            timeLeft--;
            return;
        }
        
        timeLeft = LIFESPAN;
        distanceTraveled += displacement;
        if (distanceTraveled > nextFrameDistance) {
            nextFrameDistance += TRIGGER_NEXT_FRAME_DISTANCE;
            nextFrame();
        }
    }
    
    public boolean expired() {
        return timeLeft <= 0;
    }
    
    public void setFrames(GraphicSheet sheet) {
        this.clearFrames();
        for (int i = 0; i < animationFrames; i++) {
            ArrayList<Graphic> angles = new ArrayList<>();
            for (int y = 0; y < 32; y++) {
                angles.add(sheet.graphicAt(i, y));
            }
            
            DirectionalSprite d = new DirectionalSprite(angles);
            this.frames.add(d);
        }
        
    }
    
}
