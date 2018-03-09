package com.knightlore.engine.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class SoundManager {
    // 0-1, volume as a fraction.
    private final float DEFAULT_VOLUME;

    public SoundManager(float defaultVolume) {
        // The volume to play a clip at if not specified.
        this.DEFAULT_VOLUME = defaultVolume;
    }

    public SoundManager() {
        // A default for the default volume #meta
        this(0.8f);
    }

    /**
     * Play a one-time sound effect.
     * 
     * @param e:
     *            The sound effect to play.
     * @param volume:
     *            The volume level to play at, as a float from 0 to 1.
     */
    public void play(SoundEffect e, float volume) {
        Clip clip = e.getNewClip();
        final FloatControl control = (FloatControl) clip
                .getControl(FloatControl.Type.MASTER_GAIN);
        float range = control.getMaximum() - control.getMinimum();
        float gain = range * volume + control.getMinimum();
        control.setValue(gain);
        clip.start();
    }

    /**
     * Play a one-time sound effect.
     * 
     * @param e:
     *            The sound effect to play.
     */
    public void play(SoundEffect e) {
        this.play(e, this.DEFAULT_VOLUME);
    }
}
