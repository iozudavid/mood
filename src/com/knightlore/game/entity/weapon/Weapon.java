package com.knightlore.game.entity.weapon;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.engine.audio.SoundManager;
import com.knightlore.engine.audio.SoundResource;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.WeaponSprite;
import com.knightlore.utils.Vector2D;

public abstract class Weapon {
    // How far away an entity should be from us before we don't play the shoot
    // sound. Sound effects for shots fired closer than this will have their
    // volume scaled linearly.
    private static final float SHOOT_SFX_CUTOFF_DISTANCE = 10;

    protected Graphic graphic;
    protected boolean automatic;
    protected int fireRate, timer;

    private SoundResource shootSFX;

    Weapon(Graphic graphic, boolean automatic, int fireRate, SoundResource shootSFX) {
        this.graphic = graphic;
        this.automatic = automatic;
        this.fireRate = fireRate;
        this.shootSFX = shootSFX;
    }

    public abstract int damageInflicted(Entity shooter, Entity target);

    public void fire(Entity shooter) {
        this.timer = 0;

        if (GameSettings.isClient()) {
            playShotSFX(shooter);
        }
    }

    private void playShotSFX(Entity shooter) {
        // Determine volume to play sound effect based on distance from us.
        Vector2D ourPos = GameEngine.getSingleton().getCamera().getPosition();
        Vector2D theirPos = shooter.getPosition();
        float distance = (float) ourPos.distance(theirPos);
        // This must be a decimal from 0 to 1.
        float scale = 1 - (distance / SHOOT_SFX_CUTOFF_DISTANCE);
        SoundManager soundManager = GameEngine.getSingleton().getSoundManager();
        // Scale against the default volume of sound effects.
        float volume = scale * soundManager.defaultVolume;
        if (volume > 0) {
            soundManager.playConcurrently(shootSFX, volume);
        }
    }

    private int weaponBobX = GameSettings.MOTION_BOB ? 20 : 0, weaponBobY = GameSettings.MOTION_BOB ? 30 : 0;
    private double weaponBobSpeed = 2;
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

        int xOffset = (int) (Math.cos(distanceTraveled * weaponBobSpeed) * weaponBobX);
        int yOffset = (int) (Math.abs(Math.sin(distanceTraveled * weaponBobSpeed) * weaponBobY));

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

    public abstract WeaponType getWeaponType();

}
