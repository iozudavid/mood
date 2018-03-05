package com.knightlore.render.hud;

import com.knightlore.render.PixelBuffer;

public class HUD {

    private PixelBuffer display;

    private HealthCounter healthCounter;
    private WeaponSlot currentWeapon;

    public HUD(int width, int height) {
        display = new PixelBuffer(width, height);
        healthCounter = new HealthCounter();
        currentWeapon = new WeaponSlot();
    }

    public void render() {
        display.flood(0x1F1F1F);
        currentWeapon.render(display, 10, -25);
        healthCounter.render(display, 0, 0);
    }

    public PixelBuffer getPixelBuffer() {
        return display;
    }

}
