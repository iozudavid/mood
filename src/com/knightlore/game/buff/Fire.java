package com.knightlore.game.buff;

import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;

public class Fire extends Buff{

    private static int FIRE_DAMAGE = 5;
    private static int MAX_ITERATIONS = 7;
    private int counter = 0;
    
    @Override
    public void onApply(Entity ent) {
        // TODO: Apply some fiery effect on the player's display
        ent.takeDamage(FIRE_DAMAGE);
        counter++;
    }

    @Override
    public boolean periodicEffect(Entity ent) {
        // TODO: Potentially muck about with the ticker
        // and maybe only apply damage on certain ticks
        if(counter >= MAX_ITERATIONS) {
            // tell the player to remove this buff
            return false;
        }
        ent.takeDamage(FIRE_DAMAGE);
        counter++;
        return true;
        
    }

    @Override
    public void onRemove(Entity ent) {
        // TODO: Remove fiery effect on the player's display
        
    }

    @Override
    public BuffType getType() {
        return BuffType.fire;
    }
    
}
