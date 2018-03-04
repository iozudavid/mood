package com.knightlore.game.entity.weapon;

import com.knightlore.engine.TickListener;
import com.knightlore.game.Player;
import com.knightlore.render.graphic.Graphic;

public abstract class Weapon implements TickListener {

    protected Graphic graphic;
    protected boolean automatic;
    protected int fireRate, timer;

    public Weapon(Graphic graphic, boolean automatic, int fireRate) {
        this.graphic = graphic;
        this.automatic = automatic;
        this.fireRate = fireRate;
    }
    
    public abstract int damageInflicted(Player shooter, Player target);
    
    public void fire(Player shooter) {
        timer = 0;
        // TODO: find the player that was hit somehow.
        Player shot;
    }
    
    public boolean canFire() {
        return timer >= fireRate;
    }

    public Graphic getGraphic() {
        return graphic;
    }
    
    @Override
    public void onTick() {
        timer++;
    }
    
    @Override
    public long interval() {
        return 1L;
    }

}
