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

public class SoundResource {
    // The cached data of the audio file.
    private byte[] data;
    private AudioFormat encoding;
    

    // Path to a WAV file to read.
    public SoundResource(String path) {
        File f = new File(path);
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(f)) {
            this.encoding = stream.getFormat();
            // Store the audio data in the byte array.
            data = new byte[(int) (stream.getFrameLength() * stream.getFormat().getFrameSize())];
            stream.read(data, 0, data.length);
        } catch (IOException | UnsupportedAudioFileException e) {
            System.err.println("Error while reading sound file: ");
            e.printStackTrace();
        }
    }

    /**
     * Generates a new audio Clip from the stored audio data on each call. This
     * allows the same audio file to be played multiple times concurrently, if
     * necessary.
     * 
     * @throws LineUnavailableException if a system audio line could not be opened.
     */
    public Clip getNewClip() throws LineUnavailableException {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            clip.open(new AudioInputStream(new ByteArrayInputStream(data), encoding, data.length));
        } catch (IOException e) {
            System.err.println("Error while reconstituting wave file from bytes: ");
            e.printStackTrace();
        }
        return clip;
    }
}