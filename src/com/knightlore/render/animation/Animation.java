package com.knightlore.render.animation;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.engine.TickListener;
import com.knightlore.render.graphic.Graphic;

public class Animation implements TickListener {

    private List<Graphic> frames;
    private int currentFrame;
    
    private long interval;

    public Animation(long interval) {
        frames = new ArrayList<Graphic>();
        currentFrame = 0;
        this.interval = interval;
    }

    public void addFrame(Graphic g) {
        frames.add(g);
    }

    public Graphic getGraphic() {
        return frames.get(currentFrame);
    }

    public void nextFrame() {
        currentFrame = (currentFrame + 1) % frames.size();
    }

    public void prevFrame() {
        currentFrame = (currentFrame + 1) % frames.size();
    }

    public void setFrame(int frame) {
        currentFrame = frame;
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
