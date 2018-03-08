package com.knightlore.render.hud;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.render.ColorUtils;
import com.knightlore.render.PixelBuffer;

public class HealthCounter extends HUDElement implements TickListener {

    public HealthCounter() {
        super();
        GameEngine.ticker.addTickListener(this);
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        if (player == null)
            return;
        final int HEIGHT = 10;
        final int G1 = 0xA10000;
        final int G2 = 0x810000;

        pix.fillRect(0x450007, 0, 0, pix.getWidth(), HEIGHT);

        double r = player.getCurrentHealth() / (double) player.getMaxHealth();
        for (int xx = 0; xx < r * pix.getWidth(); xx++) {
            int color = ColorUtils.mixColor(G1, G2, Math.sin(GameEngine.ticker.getTime() / 50D));
            pix.fillRect(color, xx, y, 1, HEIGHT);
        }

    }

    @Override
    public void onTick() {
        anim++;
    }

    @Override
    public long interval() {
        return (long) (GameEngine.UPDATES_PER_SECOND / 4);
    }

}
