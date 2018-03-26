package com.knightlore.game.entity.weapon;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.engine.audio.SoundManager;
import com.knightlore.engine.audio.SoundResource;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.Vector2D;

/**
 * An abstract weapon class containing shared methods and useful data common to
 * all weapons
 * 
 * @authors James, Joe, Will
 *
 */
public abstract class Weapon {
    // How far away an entity should be from us before we don't play the shoot
    // sound. Sound effects for shots fired closer than this will have their
    // volume scaled linearly.
    private static final float SHOOT_SFX_CUTOFF_DISTANCE = 10;
    private static final double WEAPON_BOB_SPEED = 2;
    private static final int INERTIA_COEFF_X = 125;
    private static final int INERTIA_COEFF_Y = 35;
    
    private final SoundResource shootSFX;
    private final int fireRate;
    private int timer;
    protected final Graphic graphic;
    
    private final int weaponBobX = GameSettings.motionBob ? 20 : 0;
    private final int weaponBobY = GameSettings.motionBob ? 30 : 0;
    
    
    Weapon(Graphic graphic, int fireRate, SoundResource shootSFX) {
        this.graphic = graphic;
        this.fireRate = fireRate;
        this.shootSFX = shootSFX;
    }
    
    /**
     * Helper method that each weapon must implement to calculate damage of it's shot.
     */
    public abstract int damageInflicted(Entity shooter, Entity target);
    
    /**
     * Resets the fire timer, and plays a sound effect on the client
     * 
     * @param shooter
     *            - the entity that fired
     */
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
        float distance = (float)ourPos.distance(theirPos);
        // This must be a decimal from 0 to 1.
        float scale = 1 - (distance / SHOOT_SFX_CUTOFF_DISTANCE);
        SoundManager soundManager = GameEngine.getSingleton().getSoundManager();
        // Scale against the default volume of sound effects.
        float volume = scale * soundManager.defaultVolume;
        if (volume > 0) {
            soundManager.playConcurrently(shootSFX, volume);
        }
    }
    
    /**
     * Draws this weapon on the player's HUD. And applies weapon bob.
     * 
     * @param pix
     *            - the PixelBuffer to draw into
     * @param x
     *            - the x coordinate of the pixel buffer
     * @param y
     *            - the y coordinate of the pixel buffer
     * @param inertiaX
     *            - how much to offset the weapon by horizontally
     * @param inertiaY
     *            - how much to offset the weapon by vertically
     * @param distanceTraveled
     *            - how far the holder has moved
     * @param muzzleFlash
     *            - do we draw a muzzle flash
     */
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
        
        int xOffset = (int) (Math.cos(distanceTraveled * WEAPON_BOB_SPEED) * weaponBobX);
        int yOffset = (int) (Math.abs(Math.sin(distanceTraveled * WEAPON_BOB_SPEED) * weaponBobY)) - 85;
        
        if (muzzleFlash) {
            pix.fillOval(0xFCC07F, xx + xOffset + inertiaX + width / 4, yy + yOffset + inertiaY + height / 4, width / 2,
                    height / 2, 500);
        }
        pix.drawGraphic(g, xx + xOffset + inertiaX, yy + yOffset + inertiaY, width, height);
    }
    
    /**
     * Increases the weapon timer
     */
    public void update() {
        timer++;
    }
    
    /**
     * @returns TRUE if the weapon can fire, that is if the elapsed time since
     *          the last shot is greater than the firing delay
     */
    public boolean canFire() {
        return timer > fireRate;
    }
    
    public Graphic getGraphic() {
        return graphic;
    }
    
    public int getInertiaCoeffX() {
        return INERTIA_COEFF_X;
    }
    
    public int getInertiaCoeffY() {
        return INERTIA_COEFF_Y;
    }
    
    /**
     * @returns The type of the current weapon.
     */
    public abstract WeaponType getWeaponType();
    
}
