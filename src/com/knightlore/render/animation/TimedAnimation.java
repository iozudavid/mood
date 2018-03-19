package com.knightlore.render.animation;

import java.util.ArrayList;

import com.knightlore.engine.TickListener;
import com.knightlore.render.graphic.Graphic;

public class TimedAnimation extends Animation<Graphic> implements TickListener {

    private long interval;

    public TimedAnimation(long interval) {
        frames = new ArrayList<Graphic>();
        currentFrame = 0;
        this.interval = interval;
    }

    @Override
    public long interval() {
        return interval;
    }
    
    @Override
    public void onTick() {
        nextFrame();
    }

}
