package com.knightlore.render.animation;

import java.util.ArrayList;
import java.util.List;

public class Animation<T> {
    
    protected List<T> frames;
    protected int currentFrame;
    
    public Animation() {
        this.frames = new ArrayList<T>();
    }

    public void addFrame(T g) {
        frames.add(g);
    }

    public T getGraphic() {
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

}
