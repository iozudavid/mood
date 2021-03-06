package com.knightlore.engine.audio;

import java.util.Random;

public enum Grunt {
    GRUNT1(new SoundResource("res/sfx/grunt1.wav")), GRUNT2(
            new SoundResource("res/sfx/grunt2.wav")), GRUNT3(
            new SoundResource("res/sfx/grunt3.wav"));
    
    // Caching.
    private static final Grunt[] VALUES = values();
    
    private final SoundResource res;
    
    Grunt(SoundResource res) {
        this.res = res;
    }
    
    public static SoundResource getRandomGrunt() {
        Random r = new Random();
        Grunt grunt = VALUES[r.nextInt(VALUES.length)];
        return grunt.res;
    }
    
    public SoundResource getRes() {
        return this.res;
    }
}
