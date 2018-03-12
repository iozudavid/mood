package com.knightlore.game.buff;

import com.knightlore.game.entity.Entity;

public class Slow extends Buff {
    
    private double originalMoveSpeed;
    private double originalRotateSpeed;
    private double originalStrafeSpeed;
    
    private static final double SLOW_MOVE_SPEED = .015;
    private static final double SLOW_ROTATE_SPEED = .005;
    private static final double SLOW_STRAFE_SPEED = .0125;
    
    private static final double SLOW_FREQUENCY = 0.5;
    private static final double SLOW_LENGTH = 1.0;
    private int counter = 0;
    
    public Slow() {
        super(SLOW_FREQUENCY, SLOW_LENGTH);
    }
    
    @Override
    public void onApply(Entity p) {
        // TODO: Maybe make some kind of sound
        
        originalMoveSpeed = p.getMoveSpeed();
        originalRotateSpeed = p.getRotateSpeed();
        originalStrafeSpeed = p.getStrafeSpeed();
        
        p.setMoveSpeed(SLOW_MOVE_SPEED);
        p.setRotateSpeed(SLOW_ROTATE_SPEED);
        p.setRotateSpeed(SLOW_STRAFE_SPEED);
    }

    @Override
    public void periodicEffect(Entity p) {
        if(counter >= maxSteps) {
            done = true;
            return;
        }
        counter++;
    }
    
    @Override
    public void reset(Entity ent) {
        done = false; //not sure of the necessity 
        counter = 0;
    }

    @Override
    public void onRemove(Entity p) {
        // TODO: Maybe make some kind of sound
        p.setMoveSpeed(originalMoveSpeed);
        p.setRotateSpeed(originalRotateSpeed);
        p.setStrafeSpeed(originalStrafeSpeed);
    }

    @Override
    public BuffType getType() {
        return BuffType.slow;
    }

    @Override
    public String toString() {
        return "Slow";
    }
    
}
