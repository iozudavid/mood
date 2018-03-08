package com.knightlore.game.buff;

import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;

public abstract class Buff {
    
    // TODO: Collaborate with Will to make Buffs
    // work on entities
    
    public abstract void onApply(Entity ent);
    
    public abstract boolean periodicEffect(Entity ent);
    
    public abstract void onRemove(Entity ent);
    
    public abstract BuffType getType();
    
}
