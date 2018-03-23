package com.knightlore.game.buff;

import com.knightlore.game.entity.Entity;

public class Fire extends Buff {

    private static int FIRE_DAMAGE = 6;
    private static final double FIRE_FREQUENCY = 1;
    private static final double FIRE_LENGTH = 8;

    public Fire(Entity ent) {
        super(ent, FIRE_FREQUENCY, FIRE_LENGTH);
    }

    @Override
    public void onApply() {
        // TODO: Apply some fiery effect on the player's display
        ent.takeDamage(FIRE_DAMAGE, null);
    }

    @Override
    public void periodicEffect() {
        ent.takeDamage(FIRE_DAMAGE, null);
    }

    @Override
    public void onRemove() {
        // TODO: Remove fiery effect on the player's display

    }

    @Override
    public BuffType getType() {
        return BuffType.FIRE;
    }

    @Override
    public String toString() {
        return "Fire";
    }

}
