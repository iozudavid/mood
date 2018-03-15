package com.knightlore.engine.audio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.knightlore.engine.GameObject;
import com.knightlore.utils.pruner.Pruner;

public class SoundManager extends GameObject {
    // 0-1, volume as a fraction.
    public final float defaultVolume;
    // A list of ClipWrappers encapsulating Clips. Kept to ensure ephemeral
    // clips are disposed of when completed.
    public List<ClipWrapper> ephemeralClips;

    public SoundManager(float defaultVolume) {
        // The volume to play a clip at if not specified.
        this.defaultVolume = defaultVolume;
        this.ephemeralClips = Collections.synchronizedList(new ArrayList<>());
    }

    public SoundManager() {
        // A default for the default volume #meta
        this(0.8f);
    }

    /**
     * Plays the sound resource if it is not already playing.
     * 
     * @param res:
     *            The sound resource to play.
     * @param volume:
     *            The volume to play it at.
     */
    public void playIfNotAlreadyPlaying(SoundResource res, float volume) {
        play(res, volume, 1);
    }

    /**
     * Play a new instance of a resource, regardless of whether another instance
     * is already playing.
     * 
     * @param res:
     *            The sound resource to play.
     * @param volume:
     *            The volume to play it at.
     */
    public synchronized void playConcurrently(SoundResource res, float volume) {
        play(res, volume, 1);
    }

    /**
     * Continuously loop the given sound resource.
     * 
     * @param res:
     *            The sound resource to play.
     * @param volume:
     *            The volume to play it at.
     */
    public void loop(SoundResource res, float volume) {
        play(res, volume, Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Plays a sound resource through the given clip.
     * 
     * @param res:
     *            The sound resource to play.
     * @param volume:
     *            The volume to play the clip at.
     * @param numLoops:
     *            The number of times to play the clip.
     */
    private void play(SoundResource res, float volume, int numLoops) {
        if (res.isPlaying())
            // The clip is already playing: don't interrupt it.
            return;

        Clip clip = res.getNewClip();
        System.out.println("num controls: " + clip.getControls().length);
        if (clip.getControls().length == 0)
            return;
        final FloatControl control = (FloatControl) clip
                .getControl(FloatControl.Type.MASTER_GAIN);
        float range = control.getMaximum() - control.getMinimum();
        float gain = range * volume + control.getMinimum();
        control.setValue(gain);
        // The Clip library interprets 'numLoops' to mean the number of
        // additional times to repeat the resource, so a value of 0 means to
        // play once.
        //clip.loop(numLoops - 1);
        clip.start();
        this.ephemeralClips.add(new ClipWrapper(clip));
    }

    @Override
    public void onUpdate() {
        // Ensure all finished clips are cleared up.
        Pruner.prune(ephemeralClips);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onCreate() {
    }
}
