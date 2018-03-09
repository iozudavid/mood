package com.knightlore.engine.audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundEffect {
    byte[] data;
    AudioFormat encoding;

    public SoundEffect(String path) {
        File f = new File(path);
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(f)) {
            this.encoding = stream.getFormat();
            data = new byte[(int) (stream.getFrameLength()
                    * stream.getFormat().getFrameSize())];
            stream.read(data, 0, data.length);
        } catch (IOException | UnsupportedAudioFileException e) {
            System.err.println("Error while reading sound file: ");
            e.printStackTrace();
        }
    }

    public Clip getNewClip() {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            clip.open(new AudioInputStream(new ByteArrayInputStream(data),
                    encoding, data.length));
        } catch (LineUnavailableException | IOException e) {
            System.err.println(
                    "Error while reconstituting wave file from bytes: ");
            e.printStackTrace();
        }
        return clip;
    }
}