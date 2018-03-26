package com.knightlore.game.buff;

import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

public class Push extends Buff {
    
    private static final double MOVE_DISTANCE = .06;
    private static final double PUSH_FREQUENCY = 1D / 32;
    private static final double PUSH_LENGTH = 0.5;
    private final Vector2D direction;
    
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
        ent.absoluteMove(direction, MOVE_DISTANCE);
    }
    
    @Override
    public void onRemove() {
    }
    
    @Override
    public BuffType getType() {
        // TODO Auto-generated method stub
        return BuffType.PUSH;
    }
    
    @Override
    public String toString() {
        return "Push";
    }
    
}
