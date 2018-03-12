package com.knightlore.game.buff;

import com.knightlore.game.entity.Entity;

public class Immune extends Buff {

    private static double IMMUNE_FREQUENCY = 1;
    private static double IMMUNE_LENGTH = 3;
    
    public Immune() {
        super(IMMUNE_FREQUENCY, IMMUNE_LENGTH);
    }

    @Override
    public void onApply(Entity ent) {
        // TODO: Show visual affect
        ent.setImmune(true);
    }

    @Override
    public void periodicEffect(Entity ent) {
        // TODO: Possibly another visual affect
        // or sound
    }

    @Override
    public void onRemove(Entity ent) {
        // TODO: Remove visual affect
        ent.setImmune(false);
    }

    @Override
    public BuffType getType() {
        return BuffType.immune;
    }

    @Override
    public String toString() {
        return "Immune";
    }

}
