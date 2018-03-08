package com.knightlore.game.buff;

import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

public class Push extends Buff {

    private static double MOVE_DISTANCE = .040;
    private Vector2D direction;
    
    @SuppressWarnings("unused")
    private Push() {}
    
    public Push(Vector2D d) {
        direction = d;
    }
    
    @Override
    public void onApply(Entity ent) {
        // TODO: PUSH PLAYER IN DIRECTION
        if(ent.hasBuff(BuffType.push)) {
            ent.addBuff(this);
            ent.absoluteMove(direction, MOVE_DISTANCE);
        }
    }

    @Override
    public void periodicEffect(Entity ent) {
        // TODO: PUSH PLAYER IN DIRECTION
        done = true;
    }

    @Override
    public void onRemove(Entity ent) {
        ent.removeBuff(this);
    }

    @Override
    public BuffType getType() {
        // TODO Auto-generated method stub
        return BuffType.push;
    }

}
