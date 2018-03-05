package com.knightlore.render.hud;

import com.knightlore.game.Player;
import com.knightlore.render.PixelBuffer;

public class HUD {

    private PixelBuffer display;

    private HealthCounter healthCounter;
    private Compass currentWeapon;

    public HUD(Player myPlayer, int width, int height) {
        display = new PixelBuffer(width, height);
        healthCounter = new HealthCounter();
        currentWeapon = new Compass();
        healthCounter.setPlayer(myPlayer);
        currentWeapon.setPlayer(myPlayer);
    }

    public void render() {
        display.flood(0x1F1F1F);
        currentWeapon.render(display, 10, -20);
        healthCounter.render(display, 0, 0);
    }

    public PixelBuffer getPixelBuffer() {
        return display;
    }

}
