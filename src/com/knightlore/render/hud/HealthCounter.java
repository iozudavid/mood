package com.knightlore.render.hud;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.render.ColorUtils;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;

public class HealthCounter implements IRenderable, TickListener {
    
    private int anim = 0;
    
    public HealthCounter() {
        GameEngine.ticker.addTickListener(this);
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        final int HEIGHT = 10;
        final int G1 = 0xA10000;
        final int G2 = 0x630000;
        for (int i = 0; i < HEIGHT; i++) {
            pix.fillRect(ColorUtils.mixColor(G1, G2, i / (double) HEIGHT), 0, (i + anim) % HEIGHT, pix.getWidth(), 1);
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
