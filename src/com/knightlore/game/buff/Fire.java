package com.knightlore.game.buff;

import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;

public class Fire extends Buff{

    private static int FIRE_DAMAGE = 2;
    private static final double FIRE_FREQUENCY = 0.5;
    private static final double FIRE_LENGTH = 8;
    
    public Fire() {
        super(FIRE_FREQUENCY, FIRE_LENGTH);
    }
    
    @Override
    public void onApply(Entity ent) {
        // TODO: Apply some fiery effect on the player's display
        ent.takeDamage(FIRE_DAMAGE);
        counter++;
    }

    @Override
    public void periodicEffect(Entity ent) {
        if(counter >= maxSteps) {
            // tell the player to remove this buff
            done =  true;
            return;
        }
        if(counter % applyGap == 0) {
            ent.takeDamage(FIRE_DAMAGE);
        }
        counter++;
        return;
    }

    /*
    @Override
    public void reset(Entity ent) {
        if(counter >= 2) {
            counter = 1;
        }
    }
    */
    
    @Override
    public void onRemove(Entity ent) {
        // TODO: Remove fiery effect on the player's display
        
    }

    @Override
    public BuffType getType() {
        return BuffType.fire;
    }
    
    @Override
    public String toString() {
        return "Fire";
    }
    
}
