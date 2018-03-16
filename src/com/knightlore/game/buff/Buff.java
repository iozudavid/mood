package com.knightlore.game.buff;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;

public abstract class Buff {
    
    protected Entity ent;
    protected boolean done = false;
    private int applyGap; // on multiples of the applyGap,
                          // the counter will trigger the
                          // periodic effect
    private int maxSteps;
    private int counter;
    
    protected Buff(Entity ent, double frequency, double length) {
        this.ent = ent;
        applyGap = calculateGap(frequency);
        maxSteps = calculateMaxSteps(length);
    }
    
    /** The affect the buff has on application
     * 
     * @param ent
     */
    public abstract void onApply();
    
    
    /** The continuous affect of the buff. loop() will
     * call this periodically as appropriate
     * @param ent
     * @return
     */
    protected abstract void periodicEffect();
    
    /** Called by the onTick() method in Entity.
     *  Manages all the mechanisms of each buff.
     */
    public void loop() {
        if(counter >= maxSteps) {
            done = true;
            return;
        }
        
        if(counter % applyGap == 0) {
            periodicEffect();
        }
        counter++;
    }
    
    /** Put a buff back to the beginning of its 
     *  execution while ensuring the frequency of
     *  the call of its periodic effect is not
     *  changed
     */
    public void reset() {
        if(counter >= applyGap) {
            counter = 1;
        }
    }
    
    /** Can manually set a buff to be done (ie. upon 
     *  the death of a player)
     * @param b
     */
    public void setDone(boolean b) {
        done = b;
    }
    
    /** Used by Entity to check whether a buff should be 
     *  removed from the buffList
     * @return
     */
    public boolean isDone() {
        return done;
    }
    
    /** Anything that must be done upon the removal
     *  of a buff
     * @param ent
     */
    public abstract void onRemove();
    
    private int calculateGap(double frequency) {
        double ticksPerSecond = Entity.getBuffTickRate() / GameEngine.UPDATES_PER_SECOND;
        if( (int) (frequency / ticksPerSecond) == 0) {
            return 1;
        }
        return (int) (frequency / ticksPerSecond);
    }
    
    private int calculateMaxSteps(double length) {
        double ticksPerSecond = Entity.getBuffTickRate() / GameEngine.UPDATES_PER_SECOND;
        if( (int) (length / ticksPerSecond) == 0) {
            return 1;
        }
        return (int) (length / ticksPerSecond);
    }
    
    public abstract BuffType getType();
    
    public abstract String toString();
    
}
