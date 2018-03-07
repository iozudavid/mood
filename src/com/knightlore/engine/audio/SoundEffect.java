package com.knightlore.engine.audio;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.IOException;
import java.net.URL;

public enum SoundEffect {

    SHOOT("res/sound/shoot.wav");

    public static enum Volume {
        MUTE, LOW, MEDIUM, HIGH
    }

    private Volume volume = Volume.MEDIUM;
    private AudioClip clip;

    private SoundEffect(String filename) {
        try {
            URL url = new URL(filename);
            clip = Applet.newAudioClip(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (volume == Volume.MUTE)
            return;
        new Thread(() -> clip.play()).start();
    }

}
