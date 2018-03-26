package com.knightlore.game.entity.weapon;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.engine.audio.SoundResource;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.WeaponSprite;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.physics.RaycastHit;

/**
 * Class representing our Pistol weapon as held by players.
 *
 * @author Joe, Will
 */
public class Pistol extends Weapon {
    
    private static final SoundResource SHOOT_SFX = new SoundResource("res/sfx/pistol.wav");
    private static final long FIRE_DELAY = 10;
    private static final int BASE_DAMAGE = 40;
    private static final float SQR_RANGE = 40;
    private long nextFireTime;
    
    public Pistol() {
        super(WeaponSprite.SHOTGUN, (int)(GameEngine.UPDATES_PER_SECOND * 0.25D), SHOOT_SFX);
    }
    
    @Override
    public void fire(Entity shooter) {
        super.fire(shooter);
        if (nextFireTime > GameEngine.ticker.getTime()) {
            // can't shoot
            return;
        }
        
        nextFireTime = GameEngine.ticker.getTime() + FIRE_DELAY;
        
        if (GameSettings.isServer()) {
            RaycastHit hit = GameEngine.getSingleton().getWorld().raycast(shooter.getPosition(), shooter.getDirection(),
                    100, SQR_RANGE, shooter);
            
            if (hit.didHitEntity()) {
                hit.getEntity().takeDamage(damageInflicted(shooter, hit.getEntity()), shooter);
            }
        }
    }
    
    @Override
    public int damageInflicted(Entity shooter, Entity target) {
        Vector2D displacement = shooter.getPosition().subtract(target.getPosition());
        int dmg = (int)(BASE_DAMAGE - displacement.sqrMagnitude());
        if (dmg < 0) {
            dmg = 0;
        }
        return dmg;
        
    }
    
    @Override
    public WeaponType getWeaponType() {
        return WeaponType.PISTOL;
    }
    
    @Override
    public Graphic getGraphic() {
        return WeaponSprite.PISTOL;
    }
    
}
