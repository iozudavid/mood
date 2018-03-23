package com.knightlore.render.hud;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.entity.Player;
import com.knightlore.render.ColorUtils;
import com.knightlore.render.PixelBuffer;

public class HealthCounter extends HUDElement implements TickListener {

    public static final int BASE = 0x450007;
    public static final int G1 = 0xA10000;
    public static final int G2 = 0x810000;

    private double displayHealth;
    private final double p = 0.2D;

    private double delta = 0D;

    public HealthCounter() {
        super();
        GameEngine.ticker.addTickListener(this);
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        assert (player != null);
        final double HEIGHT = 10;

        pix.fillRect(0x450007, 0, 0, pix.getWidth(), (int) HEIGHT);

        final int invulnColor = 0x0099CC;
        int color;
        if (player.getDamageTakenModifier() == 0) {
            color = invulnColor;
        } else {
            color = ColorUtils.mixColor(G1, G2, Math.sin(GameEngine.ticker.getTime() / 50D));
        }

        double r = displayHealth / (double) Player.MAX_HEALTH;
        for (int xx = 0; xx < r * pix.getWidth(); xx++) {
            pix.fillRect(color, xx, y, 1, (int) (Math.max(HEIGHT, HEIGHT * Math.abs(delta) * 0.1)));
        }

    }

    @Override
    public void onTick() {
        anim++;
        delta = player.getCurrentHealth() - displayHealth;
        displayHealth += delta * p;
    }

    @Override
    public long interval() {
        return 1L;
    }

}
