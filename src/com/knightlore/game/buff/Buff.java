package com.knightlore.game.buff;

import com.knightlore.game.Player;

public abstract class Buff {
    
    // TODO: Collaborate with Will to make Buffs
    // work on entities
    
    public abstract void onApply(Player p);
    
    public abstract void periodicEffect(Player p);
    
    public abstract void onRemove(Player p);
    
    public abstract BuffType getType();
    
}
