package com.knightlore.game.buff;

import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;

public abstract class Buff {
    
    // TODO: Collaborate with Will to make Buffs
    // work on entities
    
    protected boolean done = false;
    
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
    
    public abstract BuffType getType();
    
}
