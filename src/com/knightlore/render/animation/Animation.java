package com.knightlore.render.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * An animation is a collection of 'frame' objects in order. At any one time, an
 * animation object has a 'current frame' which can be cycled between all of the
 * available frames using the built-in methods.
 * 
 * @author Joe Ellis
 *
 * @param <T>
 *            the type of frame you want -- i.e. Graphic, DirectionalSprite.
 */
public class Animation<T> {

    protected final List<T> frames;
    protected int currentFrame;

    public Animation() {
        this.frames = new ArrayList<>();
        currentFrame = 0;
    }

    /**
     * Add a frame to this animation.
     * 
     * @param g
     *            the frame to add.
     */
    public void addFrame(T g) {
        frames.add(g);
    }

    /**
     * Get the current frame the animation is on.
     * 
     * @return the current frame.
     */
    public T getFrame() {
        return frames.get(currentFrame);
    }

    /**
     * Cycle one frame along.
     */
    public void nextFrame() {
        currentFrame = (currentFrame + 1) % frames.size();
    }

    /**
     * Cycle one frame backwards.
     */
    public void prevFrame() {
        currentFrame = (currentFrame + 1) % frames.size();
    }

    /**
     * Manually set the current frame.
     * 
     * @param frame
     *            the frame index.
     */
    public void setFrame(int frame) {
        currentFrame = frame;
    }
    
    protected void clearFrames() {
        currentFrame = 0;
        frames.clear();
    }

}
