package com.knightlore.engine.audio;

import com.knightlore.engine.GameEngine;

public class FootstepHandler {
    
    private final double TRIGGER_FOOTSTEP_DISTANCE = 1D;
    
    private double nextFootstepDistance = TRIGGER_FOOTSTEP_DISTANCE;
    private final SoundResource footstepSFX = new SoundResource("res/sfx/footstep.wav");
    
    public void playFootstepIfNecessary(double distanceTravelled) {
        if(distanceTravelled > nextFootstepDistance) {
            nextFootstepDistance += TRIGGER_FOOTSTEP_DISTANCE;
            playFootstep();
        }
    }
    
    public void playFootstep() {
        SoundManager soundManager = GameEngine.getSingleton()
                .getSoundManager();
        soundManager.playIfNotAlreadyPlaying(footstepSFX,
                soundManager.defaultVolume);
    }

}
