package com.knightlore.game.entity.weapon;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.sprite.WeaponSprite;
import com.knightlore.utils.physics.RaycastHit;
import com.knightlore.utils.Vector2D;

public class Shotgun extends Weapon {
    
    private static final long FIRE_DELAY = 10;
    private final float sqrRange = 10;
    private long nextFireTime;


    public Shotgun() {
        super(WeaponSprite.SHOTGUN, false, (int) (GameEngine.UPDATES_PER_SECOND * 0.25D));
    }

    @Override
    public void fire(Entity shooter) {
        if (nextFireTime > GameEngine.ticker.getTime()) {
            // can't shoot
            return;
        }
        nextFireTime = GameEngine.ticker.getTime() + FIRE_DELAY;
        
        RaycastHit hit = GameEngine.getSingleton().getWorld().raycast(shooter.getPosition(), shooter.getDirection(),
                100, sqrRange, shooter);
        if (hit.didHitEntity()) {
            // take damage!
            hit.getEntity().takeDamage(damageInflicted(shooter, hit.getEntity()),shooter);
        }
    }
    
    @Override
    public int damageInflicted(Entity shooter, Entity target) {
        final int BASE_DAMAGE = 100;
        Vector2D displacement = shooter.getPosition().subtract(target.getPosition());
        int dmg = (int) (BASE_DAMAGE - displacement.sqrMagnitude());
        if (dmg < 0) {
            dmg = 0;
        }
        return dmg;

    }

    @Override
    public WeaponType getWeaponType() {
        return WeaponType.SHOTGUN;
    }
    
}
