package com.knightlore.game.buff;

import com.knightlore.game.Player;

public class Slow extends Buff {

    private static int MAX_ITERATIONS = 2;
    private int counter = 0;
    
    @Override
    public void onApply(Player p) {
        // TODO: Maybe make some kind of sound
        p.setMoveSpeed(p.getMoveSpeed()/2);
        p.setRotateSpeed(p.getRotateSpeed()/2);
    }

    @Override
    public void periodicEffect(Player p) {
        if(counter >= MAX_ITERATIONS) {
            p.removeBuff(this);
        }else {
            counter++;
        }
    }

    @Override
    public void onRemove(Player p) {
        // TODO: Maybe make some kind of sound
        p.setMoveSpeed(p.getMoveSpeed()*2);
        p.setRotateSpeed(p.getRotateSpeed()*2);
    }

    @Override
    public BuffType getType() {
        return BuffType.slow;
    }

}
