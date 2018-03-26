package com.knightlore.render.animation;

import com.knightlore.engine.TickListener;

public class TimedAnimation<T> extends Animation<T> implements TickListener {

    private final long interval;

    public TimedAnimation(long interval) {
        super();
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
