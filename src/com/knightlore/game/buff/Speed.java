package com.knightlore.game.buff;

import com.knightlore.game.entity.Entity;

public class Speed extends Buff {
    
    protected Speed(Entity ent, double frequency, double length) {
        super(ent, frequency, length);
        // TODO Auto-generated constructor stub
    }

    private double originalMoveSpeed;
    private double originalRotateSpeed;
    private double originalStrafeSpeed;
    
    private static final double SLOW_MOVE_SPEED = .015;
    private static final double SLOW_ROTATE_SPEED = .005;
    private static final double SLOW_STRAFE_SPEED = .0125;
    
    private static final double SLOW_FREQUENCY = 0.5;
    private static final double SLOW_LENGTH = 1.0;
    
    public Speed(Entity ent) {
        super(ent, SLOW_FREQUENCY, SLOW_LENGTH);
    }
    
    @Override
    public void onApply() {
        // TODO: Maybe make some kind of sound
        
        originalMoveSpeed = ent.getMoveSpeed();
        originalRotateSpeed = ent.getRotateSpeed();
        originalStrafeSpeed = ent.getStrafeSpeed();
        
        ent.setMoveSpeed(SLOW_MOVE_SPEED);
        ent.setRotateSpeed(SLOW_ROTATE_SPEED);
        ent.setRotateSpeed(SLOW_STRAFE_SPEED);
    }

    @Override
    public void periodicEffect() {
    }

    @Override
    public void onRemove() {
        // TODO: Maybe make some kind of sound
        ent.setMoveSpeed(originalMoveSpeed);
        ent.setRotateSpeed(originalRotateSpeed);
        ent.setStrafeSpeed(originalStrafeSpeed);
    }

    @Override
    public BuffType getType() {
        return BuffType.SLOW;
    }

    @Override
    public String toString() {
        return "Slow";
    }
    
}
