package com.knightlore.game.buff;

import com.knightlore.game.Player;
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
    public void onApply(Player p) {
        // TODO: PUSH PLAYER IN DIRECTION
        p.addBuff(this);
        p.absoluteMove(direction, MOVE_DISTANCE);
    }

    @Override
    public void periodicEffect(Player p) {
        // TODO: PUSH PLAYER IN DIRECTION

    }

    @Override
    public void onRemove(Player p) {
        p.removeBuff(this);
    }

    @Override
    public BuffType getType() {
        // TODO Auto-generated method stub
        return null;
    }

}
