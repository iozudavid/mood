package com.knightlore.game.buff;

import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

public class Push extends Buff {

    private static double MOVE_DISTANCE = .15;
    private Vector2D direction;
    
    private static final int MAX_ITERATIONS = 3;
    private static final double PUSH_FREQUENCY = 1 / 16;
    private static final double PUSH_LENGTH = 0.5;
    private int counter = 0;
    
    public Push(Vector2D d) {
        super(PUSH_FREQUENCY, PUSH_LENGTH);
        direction = d;
    }
    
    @Override
    public void onApply(Entity ent) {
        ent.absoluteMove(direction, MOVE_DISTANCE);
    }

    @Override
    public void periodicEffect(Entity ent) {
        // TODO: PUSH PLAYER IN DIRECTION
        if(counter >= MAX_ITERATIONS) {
            done = true;
        }
        if(counter % 2 == 0) {
            ent.absoluteMove(direction, MOVE_DISTANCE);
        }
        counter++;
    }

    @Override
    public void reset(Entity ent) {
        counter = 1;        
    }
    
    @Override
    public void onRemove(Entity ent) {
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
