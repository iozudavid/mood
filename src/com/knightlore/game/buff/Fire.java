package com.knightlore.game.buff;

import com.knightlore.game.Player;

public class Fire extends Buff{

    private static int FIRE_DAMAGE = 5;
    private static int MAX_ITERATIONS = 7;
    private int counter = 0;
    
    @Override
    public void onApply(Player p) {
        // TODO: Apply some fiery effect on the player's display
        p.takeDamage(FIRE_DAMAGE);
        counter++;
    }

    @Override
    public void periodicEffect(Player p) {
        // TODO: Potentially muck about with the ticker
        // and maybe only apply damage on certain ticks
        if(counter >= MAX_ITERATIONS) {
            // tell the player to remove this buff
            p.removeBuff(this);
            return;
        }
        p.takeDamage(FIRE_DAMAGE);
        counter++;
        
    }

    @Override
    public void onRemove(Player p) {
        // TODO: Remove fiery effect on the player's display
        
    }

    @Override
    public BuffType getType() {
        return BuffType.fire;
    }
    
}
