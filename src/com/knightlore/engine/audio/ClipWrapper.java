package com.knightlore.engine.audio;

import javax.sound.sampled.Clip;

import com.knightlore.utils.pruner.Prunable;

/**
 * A trivial wrapper class for an audio Clip which implements Prunable, allowing
 * us to close clips that are no longer in use. N.B. in this game, clips are
 * generally only used once.
 * 
 * @author Will
 *
 */
public class ClipWrapper implements Prunable {
    private final Clip clip;

    public ClipWrapper(Clip clip) {
        this.clip = clip;
    }

    @Override
    public boolean exists() {
        return this.clip.isActive();
    }

    @Override
    public void destroy() {
        this.clip.close();
    }
}
