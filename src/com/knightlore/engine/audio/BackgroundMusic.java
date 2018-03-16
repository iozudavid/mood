package com.knightlore.engine.audio;

public enum BackgroundMusic {
    THEME_1(new SoundResource("res/bg_music/theme1.wav"));

    public final SoundResource soundRes;

    private BackgroundMusic(SoundResource bgMusic) {
        this.soundRes = bgMusic;
    }
}
