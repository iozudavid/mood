package com.knightlore.game.buff;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;

public abstract class Buff {
    
    protected boolean done = false;
    protected int applyGap;
    protected int maxSteps;
    protected int counter;
    
    protected Buff(double frequency, double length) {
        applyGap = calculateGap(frequency);
        maxSteps = calculateMaxSteps(length);
    }
    
    /** The affect the buff has on application
     * 
     * @param ent
     */
    public abstract void onApply(Entity ent);
    
    
    /** The continuous affect of the buff. Will modify
     *  remove if the buff should be removed on the next
     *  call.
     * @param ent
     * @return
     */
    public abstract void periodicEffect(Entity ent);
    
    public abstract void reset(Entity ent);
    
    public void setDone(boolean b) {
        done = b;
    }
    
    public boolean getDone() {
        return done;
    }
    
    /** Anything that must be done upon the removal
     *  of a buff
     * @param ent
     */
    public abstract void onRemove(Entity ent);
    
    public int calculateGap(double frequency) {
        double ticksPerSecond = Entity.getBuffTickRate() / GameEngine.UPDATES_PER_SECOND;
        return (int) (frequency / ticksPerSecond);
    }
    
    public int calculateMaxSteps(double length) {
        double ticksPerSecond = Entity.getBuffTickRate() / GameEngine.UPDATES_PER_SECOND;
        return (int) (length / ticksPerSecond);
    }
    
    public abstract BuffType getType();
    
    public abstract String toString();
    
}
