package com.knightlore.game.buff;

import com.knightlore.game.entity.Entity;

public class Immune extends Buff {

    private static final double IMMUNE_FREQUENCY = 1;
    private static final double IMMUNE_LENGTH = 15;

    public Immune(Entity ent) {
        super(ent, IMMUNE_FREQUENCY, IMMUNE_LENGTH);
    }

    @Override
    public void onApply() {
        ent.setDamageTakenModifier(0);
    }

    @Override
    public void periodicEffect() {
        // TODO: Possibly another visual affect
        // or sound

    }

    @Override
    public void onRemove() {
        // TODO: Remove visual affect
        ent.setDamageTakenModifier(1);
    }

    @Override
    public BuffType getType() {
        return BuffType.IMMUNE;
    }

    @Override
    public String toString() {
        return "Immune";
    }

}
