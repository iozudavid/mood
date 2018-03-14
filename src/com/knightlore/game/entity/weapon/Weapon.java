package com.knightlore.game.entity.weapon;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.WeaponSprite;

public abstract class Weapon {

    protected Graphic graphic;
    protected boolean automatic;
    protected int fireRate, timer;

    public Weapon(Graphic graphic, boolean automatic, int fireRate) {
        this.graphic = graphic;
        this.automatic = automatic;
        this.fireRate = fireRate;
    }

    public abstract int damageInflicted(Entity shooter, Entity target);

    public void fire(Entity shooter) {
        this.timer = 0;
    }

    private int weaponBobX = 20, weaponBobY = 30;
    private int inertiaCoeffX = 125, inertiaCoeffY = 35;

    public void render(PixelBuffer pix, int x, int y, int inertiaX, int inertiaY, double distanceTraveled,
            boolean muzzleFlash) {
        // Used a linear equation to get the expression below.
        // With a screen height of 558, we want a scale of 5.
        // With a screen height of 800, we want a scale of 6.
        // The linear equation relating is therefore y = 1/242 * (h - 558),
        // hence below
        int SCALE = (int) (5 + 1 / 242D * (pix.getHeight() - 558));

        Graphic g = getGraphic();
        final int width = g.getWidth() * SCALE, height = g.getHeight() * SCALE;

        int xx = x + (pix.getWidth() - width) / 2;
        int yy = pix.getHeight() - height + 28 * SCALE;

        int xOffset = (int) (Math.cos(distanceTraveled) * weaponBobX);
        int yOffset = (int) (Math.abs(Math.sin(distanceTraveled) * weaponBobY));

        if (inertiaX < -120) {
            g = WeaponSprite.SHOTGUN_LEFT;
        } else if (inertiaX > 120) {
            g = WeaponSprite.SHOTGUN_RIGHT;
        }

        if (muzzleFlash) {
            pix.fillOval(0xFCC07F, xx + xOffset + inertiaX + width / 4, yy + yOffset + inertiaY + height / 4, width / 2,
                    height / 2, 500);
        }
        pix.drawGraphic(g, xx + xOffset + inertiaX, yy + yOffset + inertiaY, width, height);
    }
    
    public void update() {
        timer++;
    }

    public boolean canFire() {
        return timer > fireRate;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public int getInertiaCoeffX() {
        return inertiaCoeffX;
    }

    public int getInertiaCoeffY() {
        return inertiaCoeffY;
    }

}
