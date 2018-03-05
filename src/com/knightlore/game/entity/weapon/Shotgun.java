package com.knightlore.game.entity.weapon;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.sprite.WeaponSprite;
import com.knightlore.utils.RaycastHit;
import com.knightlore.utils.RaycastHitType;
import com.knightlore.utils.Vector2D;

public class Shotgun extends Weapon {

    private final float sqrRange = 10;
    
    public Shotgun() {
        super(WeaponSprite.SHOTGUN, false, (int) (0.75 * GameEngine.UPDATES_PER_SECOND));
    }
    
    @Override
    public void fire(Player shooter) {
        //System.out.println("fired shotgun");
        RaycastHit hit = GameEngine.getSingleton().getWorld().raycast(shooter.getPosition(), shooter.getDirection(), 100,sqrRange,shooter);
        //System.out.println(hit.getHitType());
        //System.out.println(hit.getEntity());
        if(hit.didHitEntity()) {
            //System.out.println("hit entity");
            // take damage!
            hit.getEntity().takeDamage(damageInflicted(shooter,hit.getEntity()));
        }
    }

    @Override
    public int damageInflicted(Player shooter, Entity target) {
        final int BASE_DAMAGE = 100;
        Vector2D displacement = shooter.getPosition().subtract(target.getPosition());
        int dmg = (int)(BASE_DAMAGE - displacement.sqrMagnitude());
        if(dmg < 0) {
            dmg = 0;
        }
        return dmg;
        
    }

}
