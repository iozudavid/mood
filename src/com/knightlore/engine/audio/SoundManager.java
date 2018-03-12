package com.knightlore.engine.audio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

import com.knightlore.engine.GameObject;
import com.knightlore.utils.pruner.Pruner;

public class SoundManager extends GameObject {
    // 0-1, volume as a fraction.
    private final float DEFAULT_VOLUME;
    //
    private List<ClipWrapper> clips;
    
    public SoundManager(float defaultVolume) {
        // The volume to play a clip at if not specified.
        this.DEFAULT_VOLUME = defaultVolume;
        this.clips = Collections.synchronizedList(new ArrayList<>());
    }

    public SoundManager() {
        // A default for the default volume #meta
        this(0.8f);
    }

    /**
     * Play a one-time sound resource.
     * 
     * @param e:
     *            The sound resource to play.
     * @param volume:
     *            The volume level to play at, as a float from 0 to 1.
     */
    public synchronized void play(SoundResource e, float volume) {
        Clip clip;
        try {
            clip = e.getNewClip();
        } catch (LineUnavailableException e1) {
            // This can happen if, for example, we've reached the limit on
            // number of open audio lines, on Linux systems. Silently fail,
            // since audio effects aren't essential.
            return;
        }
        final FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float range = control.getMaximum() - control.getMinimum();
        float gain = range * volume + control.getMinimum();
        control.setValue(gain);
        clip.start();
        this.clips.add(new ClipWrapper(clip));
    }

    /**
     * Play a one-time sound resource.
     * 
     * @param e:
     *            The sound resource to play.
     */
    public void play(SoundResource e) {
        this.play(e, this.DEFAULT_VOLUME);
    }

    @Override
    public void onUpdate() {
        // Ensure all finished clips are cleared up.
        Pruner.prune(clips);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onCreate() {
    }
}
