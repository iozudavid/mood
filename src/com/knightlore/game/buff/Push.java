package com.knightlore.game.buff;

import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

public class Push extends Buff {

    private static double MOVE_DISTANCE = .15;
    private Vector2D direction;
    
    private static final double PUSH_FREQUENCY = 1 / 16;
    private static final double PUSH_LENGTH = 0.5;
    
    public Push(Entity ent, Vector2D d) {
        super(ent, PUSH_FREQUENCY, PUSH_LENGTH);
        direction = d;
    }
    
    @Override
    public void onApply() {
        ent.absoluteMove(direction, MOVE_DISTANCE);
    }

    @Override
    public void periodicEffect() {
        // TODO: PUSH PLAYER IN DIRECTION
        ent.absoluteMove(direction, MOVE_DISTANCE);
    }
    
    @Override
    public void onRemove() {
    }

    @Override
    public BuffType getType() {
        // TODO Auto-generated method stub
        return BuffType.push;
    }
    
    @Override
    public String toString() {
        return "Push";
    }

}
