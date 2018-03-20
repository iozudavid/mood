package com.knightlore.engine.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

public class SoundManager implements LineListener {
    // 0-1, volume as a fraction.
    public final float defaultVolume;

    public SoundManager(float defaultVolume) {
        // The volume to play a clip at if not specified.
        this.defaultVolume = defaultVolume;
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
        if (!res.isPlaying())
            play(res, volume, 0);
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
        play(res, volume, 0);
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
     *            The number of times to repeat the clip after the first play.
     */
    private void play(SoundResource res, float volume, int numLoops) {
        Clip clip = res.getNewClip();
        // FIXME
        if (clip == null) {
            return;
        }

        final FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float range = control.getMaximum() - control.getMinimum();
        float gain = range * volume + control.getMinimum();
        control.setValue(gain);
        clip.loop(numLoops);
        // Ensure that the update() method in this class is called when the clip
        // finishes playing.
        clip.addLineListener(this);
    }

    // Called when any event occurs for the line of an audio clip.
    @Override
    public void update(LineEvent event) {
        // Lines are ephemeral in this game, so ensure they are closed as soon
        // as they finish playback, to avoid resource leaks.
        if (event.getType() == Type.STOP)
            event.getLine().close();
    }
}
