package com.knightlore.render.hud;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.Player;
import com.knightlore.render.ColorUtils;
import com.knightlore.render.PixelBuffer;

public class HealthCounter extends HUDElement implements TickListener {
    
    private int previous;

    public HealthCounter() {
        super();
        GameEngine.ticker.addTickListener(this);
        previous = player.getCurrentHealth();
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        assert(player != null);
        final int HEIGHT = 10;
        final int G1 = 0xA10000;
        final int G2 = 0x810000;

        pix.fillRect(0x450007, 0, 0, pix.getWidth(), HEIGHT);
        
        final double p = -0.1D;
        double delta = previous - player.getCurrentHealth();
        previous += delta * p;

        double r = player.getCurrentHealth() / (double) Player.MAX_HEALTH;
        for (int xx = 0; xx < r * pix.getWidth(); xx++) {
            int color = ColorUtils.mixColor(G1, G2, Math.sin(GameEngine.ticker.getTime() / 50D));
            
            int red = color & 0xFF0000;
            red += delta;
            color = (red << 16) + (color & 0x00FFFF);
            
            pix.fillRect(color, xx, y, 1, HEIGHT);
        }

    }

    @Override
    public void onTick() {
        anim++;
        previous = player.getCurrentHealth();
    }

    @Override
    public long interval() {
        return (long) (GameEngine.UPDATES_PER_SECOND / 4);
    }

}
