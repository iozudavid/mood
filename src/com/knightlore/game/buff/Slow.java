package com.knightlore.game.buff;

import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;

public class Slow extends Buff {

    private static int MAX_ITERATIONS = 2;
    private int counter = 0;
    
    @Override
    public void onApply(Entity p) {
        // TODO: Maybe make some kind of sound
        p.setMoveSpeed(p.getMoveSpeed()/2);
        p.setRotateSpeed(p.getRotateSpeed()/2);
    }

    @Override
    public void periodicEffect(Entity p) {
        if(counter >= MAX_ITERATIONS) {
            p.removeBuff(this);
            done = true;
            return;
        }
        counter++;
    }

    @Override
    public void onRemove(Entity p) {
        // TODO: Maybe make some kind of sound
        p.setMoveSpeed(p.getMoveSpeed()*2);
        p.setRotateSpeed(p.getRotateSpeed()*2);
    }

    @Override
    public BuffType getType() {
        return BuffType.slow;
    }

}
