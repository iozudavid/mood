package com.knightlore.game.buff;

import com.knightlore.GameSettings;
import com.knightlore.game.entity.Entity;

public class SpawnVision extends Buff {

    private static final double SPAWN_VISION_FREQUENCY = 1 / 32d;
    private static final double SPAWN_VISION_LENGTH = 4;

    public SpawnVision(Entity ent) {
        super(ent, SPAWN_VISION_FREQUENCY, SPAWN_VISION_LENGTH);
    }

    @Override
    public void onApply() {
        GameSettings.actualBlockiness = 100;
    }

    @Override
    protected void periodicEffect() {
        double delta;
        delta = GameSettings.desiredBlockiness - GameSettings.actualBlockiness;

        final double pp = 0.01;
        GameSettings.actualBlockiness += pp * delta;
    }

    @Override
    public void onRemove() {
        GameSettings.actualBlockiness = GameSettings.desiredBlockiness;
    }

    @Override
    public BuffType getType() {
        return BuffType.SPAWNVISION;
    }

    @Override
    public String toString() {
        return "SPAWNVISION";
    }
    
    @Override
    protected boolean finished() {
        return GameSettings.actualBlockiness == GameSettings.desiredBlockiness;
    }

}
