package com.knightlore.game.buff;

import com.knightlore.game.entity.Entity;

public class Speed extends Buff {
    
    private static final double DEFAULT_MOVE_SPEED = 0.060;
    private static final double DEFAULT_STRAFE_SPEED = DEFAULT_MOVE_SPEED / 2;
    
    private static final double FAST_MOVE_SPEED = .120;
    private static final double FAST_STRAFE_SPEED = FAST_MOVE_SPEED / 2;
    
    private static final double SPEED_BUFF_FREQUENCY = 0.5;
    private static final double SPEED_BUFF_LENGTH = 10.0;
    
    public Speed(Entity ent) {
        super(ent, SPEED_BUFF_FREQUENCY, SPEED_BUFF_LENGTH);
    }
    
    @Override
    public void onApply() {
        // TODO: Maybe make some kind of sound
        ent.setMoveSpeed(FAST_MOVE_SPEED);
        ent.setRotateSpeed(FAST_STRAFE_SPEED);
    }
    
    @Override
    public void periodicEffect() {
    }
    
    @Override
    public void onRemove() {
        // TODO: Maybe make some kind of sound
        ent.setMoveSpeed(DEFAULT_MOVE_SPEED);
        ent.setStrafeSpeed(DEFAULT_STRAFE_SPEED);
    }
    
    @Override
    public BuffType getType() {
        return BuffType.SPEED;
    }
    
    @Override
    public String toString() {
        return "Speed";
    }
    
}
